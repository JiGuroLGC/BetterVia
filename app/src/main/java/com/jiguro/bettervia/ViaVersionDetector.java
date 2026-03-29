package com.jiguro.bettervia;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
public class ViaVersionDetector {
	private static final String TAG = "ViaVersionDetector";
	public static class VersionInfo {
		public int versionCode;
		public String versionName;
		public String packageName;
		public VersionInfo(int versionCode, String versionName, String packageName) {
			this.versionCode = versionCode;
			this.versionName = versionName;
			this.packageName = packageName;
		}
		@Override
		public String toString() {
			return "VersionInfo{" + "versionCode=" + versionCode + ", versionName='" + versionName + '\''
					+ ", packageName='" + packageName + '\'' + '}';
		}
	}
	public static VersionInfo detectViaVersion(XC_LoadPackage.LoadPackageParam lpparam) {
		try {
			Context context = (Context) XposedHelpers.callStaticMethod(
					XposedHelpers.findClass("android.app.ActivityThread", null), "currentApplication");
			if (context == null) {
				Log.e(TAG, "无法获取Application Context");
				return null;
			}
			return detectViaVersion(context, lpparam.packageName);
		} catch (Exception e) {
			Log.e(TAG, "检测Via版本失败", e);
			return null;
		}
	}
	public static VersionInfo detectViaVersion(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			VersionInfo versionInfo = new VersionInfo(packageInfo.versionCode, packageInfo.versionName,
					packageInfo.packageName);
			Log.i(TAG, "检测到Via版本: " + versionInfo);
			return versionInfo;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "未找到应用: " + packageName, e);
			return null;
		} catch (Exception e) {
			Log.e(TAG, "检测Via版本失败", e);
			return null;
		}
	}
	public static VersionInfo detectViaVersion(Context context) {
		if (context == null) {
			Log.e(TAG, "Context为null");
			return null;
		}
		return detectViaVersion(context, context.getPackageName());
	}
	private static final List<Integer> SUPPORTED_VERSION_CODES_LIST = new ArrayList<Integer>(Arrays.asList(20260211, 20251223, 20251024, 20250907, 20250713));
	private static final List<String> SUPPORTED_VERSION_NAMES_LIST = new ArrayList<String>(Arrays.asList("7.0.0", "6.9.0", "6.8.0", "6.7.1", "6.6.0"));
	public static int[] getSupportedVersionCodes() {
		int[] result = new int[SUPPORTED_VERSION_CODES_LIST.size()];
		for (int i = 0; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
			result[i] = SUPPORTED_VERSION_CODES_LIST.get(i);
		}
		return result;
	}
	public static String[] getSupportedVersionNames() {
		return SUPPORTED_VERSION_NAMES_LIST.toArray(new String[0]);
	}
	public static boolean addSupportedVersion(int versionCode, String versionName) {
		if (isVersionSupported(versionCode)) {
			Log.w(TAG, "版本 " + versionCode + " 已存在，无需添加");
			return false;
		}
		boolean inserted = false;
		for (int i = 0; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
			if (versionCode > SUPPORTED_VERSION_CODES_LIST.get(i)) {
				SUPPORTED_VERSION_CODES_LIST.add(i, versionCode);
				SUPPORTED_VERSION_NAMES_LIST.add(i, versionName);
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			SUPPORTED_VERSION_CODES_LIST.add(versionCode);
			SUPPORTED_VERSION_NAMES_LIST.add(versionName);
		}
		Log.i(TAG, "已添加新版本支持: " + versionName + " (code: " + versionCode + ")");
		return true;
	}
	public static void resetSupportedVersions() {
		SUPPORTED_VERSION_CODES_LIST.clear();
		SUPPORTED_VERSION_NAMES_LIST.clear();
		SUPPORTED_VERSION_CODES_LIST.addAll(Arrays.asList(20260211, 20251223, 20251024, 20250907, 20250713));
		SUPPORTED_VERSION_NAMES_LIST.addAll(Arrays.asList("7.0.0", "6.9.0", "6.8.0", "6.7.1", "6.6.0"));
		Log.i(TAG, "已重置版本支持列表到默认状态");
	}
	public static String getVersionName(int versionCode) {
		for (int i = 0; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
			if (SUPPORTED_VERSION_CODES_LIST.get(i) == versionCode) {
				return SUPPORTED_VERSION_NAMES_LIST.get(i);
			}
		}
		return "Unknown";
	}
	public static boolean isVersionSupported(int versionCode) {
		for (int i = 0; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
			if (SUPPORTED_VERSION_CODES_LIST.get(i) == versionCode) {
				return true;
			}
		}
		return false;
	}
	public static int getRecommendedVersion(int currentVersionCode) {
		if (isVersionSupported(currentVersionCode)) {
			return currentVersionCode;
		}
		int recommendedVersion = SUPPORTED_VERSION_CODES_LIST.get(0); 
		int minDistance = Math.abs(currentVersionCode - SUPPORTED_VERSION_CODES_LIST.get(0));
		for (int i = 1; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
			int distance = Math.abs(currentVersionCode - SUPPORTED_VERSION_CODES_LIST.get(i));
			if (distance < minDistance) {
				minDistance = distance;
				recommendedVersion = SUPPORTED_VERSION_CODES_LIST.get(i);
			}
		}
		Log.i(TAG, "当前版本 " + currentVersionCode + " 不在支持列表中，推荐版本: " + recommendedVersion);
		return recommendedVersion;
	}
	public static int getRecommendedVersionByName(String currentVersionName) {
		if (currentVersionName == null || currentVersionName.isEmpty()) {
			Log.w(TAG, "版本名称为空，使用默认推荐版本");
			return SUPPORTED_VERSION_CODES_LIST.get(0);
		}
		for (int i = 0; i < SUPPORTED_VERSION_NAMES_LIST.size(); i++) {
			if (SUPPORTED_VERSION_NAMES_LIST.get(i).equals(currentVersionName)) {
				return SUPPORTED_VERSION_CODES_LIST.get(i);
			}
		}
		Log.w(TAG, "版本名称 " + currentVersionName + " 不在支持列表中，使用最新支持版本");
		return SUPPORTED_VERSION_CODES_LIST.get(0);
	}
	private static final int[] DEFAULT_VERSION_CODES = {20260211, 20251223, 20251024, 20250907, 20250713};
	private static final String[] DEFAULT_VERSION_NAMES = {"7.0.0", "6.9.0", "6.8.0", "6.7.1", "6.6.0"};
	public static void saveSupportedVersions(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			StringBuilder codesBuilder = new StringBuilder();
			for (int i = 0; i < SUPPORTED_VERSION_CODES_LIST.size(); i++) {
				if (i > 0) {
					codesBuilder.append(",");
				}
				codesBuilder.append(SUPPORTED_VERSION_CODES_LIST.get(i));
			}
			editor.putString("supported_version_codes", codesBuilder.toString());
			StringBuilder namesBuilder = new StringBuilder();
			for (int i = 0; i < SUPPORTED_VERSION_NAMES_LIST.size(); i++) {
				if (i > 0) {
					namesBuilder.append(",");
				}
				namesBuilder.append(SUPPORTED_VERSION_NAMES_LIST.get(i));
			}
			editor.putString("supported_version_names", namesBuilder.toString());
			editor.apply();
			Log.i(TAG, "已保存版本列表到SharedPreferences");
		} catch (Exception e) {
			Log.e(TAG, "保存版本列表失败", e);
		}
	}
	public static void restoreSupportedVersions(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			String codesStr = sp.getString("supported_version_codes", null);
			String namesStr = sp.getString("supported_version_names", null);
			if (codesStr == null || namesStr == null) {
				Log.i(TAG, "未找到持久化的版本列表，使用默认列表");
				return;
			}
			SUPPORTED_VERSION_CODES_LIST.clear();
			SUPPORTED_VERSION_NAMES_LIST.clear();
			String[] codesArray = codesStr.split(",");
			for (String codeStr : codesArray) {
				try {
					int code = Integer.parseInt(codeStr.trim());
					SUPPORTED_VERSION_CODES_LIST.add(code);
				} catch (NumberFormatException e) {
					Log.w(TAG, "解析版本代码失败: " + codeStr);
				}
			}
			String[] namesArray = namesStr.split(",");
			for (String name : namesArray) {
				SUPPORTED_VERSION_NAMES_LIST.add(name.trim());
			}
			while (SUPPORTED_VERSION_NAMES_LIST.size() < SUPPORTED_VERSION_CODES_LIST.size()) {
				SUPPORTED_VERSION_NAMES_LIST.add("Unknown");
			}
			while (SUPPORTED_VERSION_CODES_LIST.size() < SUPPORTED_VERSION_NAMES_LIST.size()) {
				SUPPORTED_VERSION_CODES_LIST.add(0);
			}
			Log.i(TAG, "已从SharedPreferences恢复版本列表，共 " + SUPPORTED_VERSION_CODES_LIST.size() + " 个版本");
		} catch (Exception e) {
			Log.e(TAG, "恢复版本列表失败", e);
		}
	}
	public static boolean addSupportedVersion(int versionCode, String versionName, Context context) {
		boolean result = addSupportedVersion(versionCode, versionName);
		if (result && context != null) {
			saveSupportedVersions(context);
		}
		return result;
	}
}