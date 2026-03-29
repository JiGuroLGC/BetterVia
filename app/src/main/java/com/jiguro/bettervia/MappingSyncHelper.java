package com.jiguro.bettervia;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import de.robv.android.xposed.XposedBridge;
public class MappingSyncHelper {
	private static final String TAG = "MappingSyncHelper";
	public interface SyncCallback {
		void onSuccess(int addedVersions);
		void onFailed(String errorMessage);
		void onNoUpdate();
	}
	public static void syncMappingFromNetwork(final Context context, final SyncCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = Hook.getNetworkSource(context);
					String jsonUrl = networkSource.equals(Hook.NETWORK_SOURCE_GITEE)
							? Hook.GITEE_MAPPING_JSON_URL
							: Hook.GITHUB_MAPPING_JSON_URL;
					XposedBridge.log("[BetterVia] 开始同步Mapping配置: " + jsonUrl);
					String jsonContent = downloadJson(jsonUrl);
					if (jsonContent == null || jsonContent.trim().isEmpty()) {
						XposedBridge.log("[BetterVia] Mapping配置文件为空");
						notifyNoUpdate(callback);
						return;
					}
					JSONObject jsonObject = new JSONObject(jsonContent);
					JSONArray versionsArray = jsonObject.optJSONArray("versions");
					if (versionsArray == null || versionsArray.length() == 0) {
						XposedBridge.log("[BetterVia] Mapping配置中没有版本信息");
						notifyNoUpdate(callback);
						return;
					}
					int addedCount = 0;
					for (int i = 0; i < versionsArray.length(); i++) {
						JSONObject versionObj = versionsArray.getJSONObject(i);
						int versionCode = versionObj.getInt("versionCode");
						String versionName = versionObj.getString("versionName");
						JSONObject mappingsObj = versionObj.getJSONObject("mappings");
						if (ViaVersionDetector.isVersionSupported(versionCode)) {
							XposedBridge.log("[BetterVia] 版本 " + versionName + " (code: " + versionCode + ") 已存在，跳过");
							continue;
						}
						ViaVersionDetector.addSupportedVersion(versionCode, versionName, context);
						addVersionMapping(versionCode, mappingsObj, context);
						addedCount++;
						XposedBridge.log("[BetterVia] 已添加新版本: " + versionName + " (code: " + versionCode + ")");
					}
					saveSyncTime(context);
					if (addedCount > 0) {
						notifySuccess(callback, addedCount);
					} else {
						notifyNoUpdate(callback);
					}
				} catch (Exception e) {
					XposedBridge.log("[BetterVia] 同步Mapping配置失败: " + e.getMessage());
					e.printStackTrace();
					notifyFailed(callback, e.getMessage());
				}
			}
		}).start();
	}
	private static String downloadJson(String urlString) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				XposedBridge.log("[BetterVia] 下载失败，响应码: " + responseCode);
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 下载JSON失败: " + e.getMessage());
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				XposedBridge.log("[BetterVia] 关闭连接失败: " + e.getMessage());
			}
		}
	}
	private static void addVersionMapping(int versionCode, JSONObject mappingsObj, Context context) {
		try {
			for (ViaClassMapping.ClassMethodKey key : ViaClassMapping.ClassMethodKey.values()) {
				String keyName = key.name();
				if (mappingsObj.has(keyName)) {
					JSONObject mappingObj = mappingsObj.getJSONObject(keyName);
					String className = mappingObj.getString("className");
					String methodName = mappingObj.optString("methodName", "");
					String parameterClassName = mappingObj.optString("parameterClassName", null);
					if (parameterClassName != null && parameterClassName.isEmpty()) {
						parameterClassName = null;
					}
					ViaClassMapping.ClassMethodMapping mapping;
					if (parameterClassName != null) {
						mapping = new ViaClassMapping.ClassMethodMapping(className, methodName, parameterClassName);
					} else {
						mapping = new ViaClassMapping.ClassMethodMapping(className, methodName);
					}
					ViaClassMapping.addMapping(versionCode, key, mapping, context);
				}
			}
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 添加版本Mapping失败: " + e.getMessage());
			e.printStackTrace();
		}
	}
	private static void saveSyncTime(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putLong("mapping_sync_time", System.currentTimeMillis());
			editor.apply();
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 保存同步时间失败: " + e.getMessage());
		}
	}
	public static long getLastSyncTime(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			return sp.getLong("mapping_sync_time", 0);
		} catch (Exception e) {
			return 0;
		}
	}
	private static void notifySuccess(final SyncCallback callback, final int addedVersions) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onSuccess(addedVersions);
				}
			}
		});
	}
	private static void notifyFailed(final SyncCallback callback, final String errorMessage) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onFailed(errorMessage);
				}
			}
		});
	}
	private static void notifyNoUpdate(final SyncCallback callback) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onNoUpdate();
				}
			}
		});
	}
}