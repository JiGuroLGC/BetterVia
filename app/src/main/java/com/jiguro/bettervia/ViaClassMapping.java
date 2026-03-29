package com.jiguro.bettervia;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
public class ViaClassMapping {
	private static final String TAG = "ViaClassMapping";
	public static class ClassMethodMapping {
		public final String className;
		public final String methodName;
		public final String parameterClassName; 
		public ClassMethodMapping(String className, String methodName) {
			this(className, methodName, null);
		}
		public ClassMethodMapping(String className, String methodName, String parameterClassName) {
			this.className = className;
			this.methodName = methodName;
			this.parameterClassName = parameterClassName;
		}
		@Override
		public String toString() {
			return "ClassMethodMapping{" + "className='" + className + '\'' + ", methodName='" + methodName + '\''
					+ ", parameterClassName='" + parameterClassName + '\'' + '}';
		}
	}
	public enum ClassMethodKey {
		VIA_CHECK_CLASS("k.a.c0.i.k", "u", "k.a.c0.i.a"),
		PRIVACY_LOCK_WHITELIST("k.a.o0.g7", "f3"),
		PRIVACY_LOCK_OVERLAY("e.h.g.g.a0$a", "a"),
		SETTINGS_ITEM_CLASS("e.h.g.g.y", ""),
		COMPONENT_BLOCK_CLASS("e.h.g.g.n", ""),
		COMPONENT_CHECK_METHOD("k.a.o0.g6", "X1"),
		FIREBASE_ANALYTICS("com.google.firebase.analytics.FirebaseAnalytics", "a", ""),
		PRIVACY_LOCK_VERIFY_1("k.a.y.bb", "B"), PRIVACY_LOCK_VERIFY_2("k.a.z.b0.f", "f"),
		SCRIPT_REPO_1("k.a.c0.l.c", "g"), SCRIPT_REPO_2("k.a.y.ya", "Sb");
		private final String className;
		private final String methodName;
		private final String parameterClassName;
		ClassMethodKey(String className, String methodName) {
			this(className, methodName, null);
		}
		ClassMethodKey(String className, String methodName, String parameterClassName) {
			this.className = className;
			this.methodName = methodName;
			this.parameterClassName = parameterClassName;
		}
		public String getClassName() {
			return className;
		}
		public String getMethodName() {
			return methodName;
		}
		public String getParameterClassName() {
			return parameterClassName;
		}
	}
	private static final Map<Integer, Map<ClassMethodKey, ClassMethodMapping>> VERSION_MAPPINGS = new HashMap<Integer, Map<ClassMethodKey, ClassMethodMapping>>();
	private static final Map<ClassMethodKey, ClassMethodMapping> DEFAULT_MAPPINGS = new HashMap<ClassMethodKey, ClassMethodMapping>();
	static {
		Map<ClassMethodKey, ClassMethodMapping> defaultMap = new HashMap<ClassMethodKey, ClassMethodMapping>();
		for (ClassMethodKey key : ClassMethodKey.values()) {
			defaultMap.put(key,
					new ClassMethodMapping(key.getClassName(), key.getMethodName(), key.getParameterClassName()));
		}
		DEFAULT_MAPPINGS.putAll(defaultMap);
		Map<ClassMethodKey, ClassMethodMapping> v700Map = new HashMap<ClassMethodKey, ClassMethodMapping>();
		v700Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("k.a.c0.i.k", "u", "k.a.c0.i.a"));
		v700Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("k.a.o0.g7", "f3"));
		v700Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("e.h.g.g.a0$a", "a"));
		v700Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("e.h.g.g.y", ""));
		v700Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("e.h.g.g.n", ""));
		v700Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("k.a.o0.g6", "X1"));
		v700Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v700Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("k.a.y.bb", "B"));
		v700Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("k.a.z.b0.f", "f"));
		v700Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("k.a.c0.l.c", "g"));
		v700Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("k.a.y.ya", "Sb"));
		VERSION_MAPPINGS.put(20260211, v700Map);
		Map<ClassMethodKey, ClassMethodMapping> v690Map = new HashMap<ClassMethodKey, ClassMethodMapping>();
		v690Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("k.a.c0.i.k", "u", "k.a.c0.i.a"));
		v690Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("k.a.o0.f7", "f3"));
		v690Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("e.h.g.g.a0$a", "a"));
		v690Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("e.h.g.g.y", ""));
		v690Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("e.h.g.g.n", ""));
		v690Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("k.a.o0.f6", "X1"));
		v690Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v690Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("k.a.y.bb", "B"));
		v690Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("k.a.z.b0.f", "f"));
		v690Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("k.a.c0.l.c", "g"));
		v690Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("k.a.y.ya", "Rb"));
		VERSION_MAPPINGS.put(20251223, v690Map);
		Map<ClassMethodKey, ClassMethodMapping> v680Map = new HashMap<ClassMethodKey, ClassMethodMapping>();
		v680Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("k.a.a0.i.k", "u", "k.a.a0.i.a"));
		v680Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("k.a.m0.f7", "f3"));
		v680Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("e.h.g.g.a0$a", "a"));
		v680Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("e.h.g.g.y", ""));
		v680Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("e.h.g.g.n", ""));
		v680Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("k.a.m0.f6", "X1"));
		v680Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v680Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("k.a.w.eb", "B"));
		v680Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("k.a.x.b0.f", "f"));
		v680Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("k.a.a0.l.c", "g"));
		v680Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("k.a.w.bb", "Zb"));
		VERSION_MAPPINGS.put(20251024, v680Map);
		Map<ClassMethodKey, ClassMethodMapping> v671Map = new HashMap<ClassMethodKey, ClassMethodMapping>();
		v671Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("i.a.a0.i.k", "u", "i.a.a0.i.a"));
		v671Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("i.a.m0.k7", "m3"));
		v671Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("d.h.g.g.a0$a", "a"));
		v671Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("d.h.g.g.y", ""));
		v671Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("d.h.g.g.n", ""));
		v671Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("i.a.m0.k6", "a2"));
		v671Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v671Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("i.a.w.kb", "B"));
		v671Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("i.a.x.b0.f", "f"));
		v671Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("i.a.a0.l.c", "g"));
		v671Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("i.a.w.hb", "fc"));
		VERSION_MAPPINGS.put(20250907, v671Map);
		Map<ClassMethodKey, ClassMethodMapping> v660Map = new HashMap<ClassMethodKey, ClassMethodMapping>();
		v660Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("i.a.a0.i.k", "u", "i.a.a0.i.a"));
		v660Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("i.a.m0.m7", "l3"));
		v660Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("d.h.g.g.a0$a", "a"));
		v660Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("d.h.g.g.y", ""));
		v660Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("d.h.g.g.n", ""));
		v660Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("i.a.m0.m6", "Z1"));
		v660Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v660Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("i.a.w.za", "B"));
		v660Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("i.a.x.b0.f", "f"));
		v660Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("i.a.a0.l.c", "g"));
		v660Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("i.a.w.wa", "Tb"));
		VERSION_MAPPINGS.put(20250713, v660Map);
	}
	private static Map<ClassMethodKey, ClassMethodMapping> cachedMappings = null;
	private static Integer cachedVersionCode = null;
	private static Integer userSelectedVersionCode = null;
	public static void setUserSelectedVersionCode(int versionCode) {
		userSelectedVersionCode = versionCode;
		clearCache();
		Log.i(TAG, "设置用户选择的版本: " + versionCode);
	}
	public static Integer getUserSelectedVersionCode() {
		return userSelectedVersionCode;
	}
	public static Map<ClassMethodKey, ClassMethodMapping> getClassMethodMappings(int versionCode) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = VERSION_MAPPINGS.get(versionCode);
		if (mappings != null) {
			return mappings;
		}
		int recommendedVersion = ViaVersionDetector.getRecommendedVersion(versionCode);
		mappings = VERSION_MAPPINGS.get(recommendedVersion);
		if (mappings != null) {
			Log.i(TAG, "使用推荐版本的映射: 目标版本 " + versionCode + " -> 推荐版本 " + recommendedVersion);
			return mappings;
		}
		Log.w(TAG, "未找到版本 " + versionCode + " 的映射，使用默认映射");
		return DEFAULT_MAPPINGS;
	}
	public static Map<ClassMethodKey, ClassMethodMapping> getAutoClassMethodMappings(Context context) {
		try {
			ViaVersionDetector.VersionInfo versionInfo = ViaVersionDetector.detectViaVersion(context);
			if (versionInfo != null) {
				return getClassMethodMappings(versionInfo.versionCode);
			}
		} catch (Exception e) {
			Log.e(TAG, "自动检测版本失败", e);
		}
		return DEFAULT_MAPPINGS;
	}
	public static Map<ClassMethodKey, ClassMethodMapping> getCachedClassMethodMappings(Context context) {
		try {
			int targetVersionCode;
			if (userSelectedVersionCode != null) {
				targetVersionCode = userSelectedVersionCode;
				Log.i(TAG, "使用用户选择的版本: " + targetVersionCode);
			} else {
				ViaVersionDetector.VersionInfo versionInfo = ViaVersionDetector.detectViaVersion(context);
				if (versionInfo != null) {
					targetVersionCode = versionInfo.versionCode;
					Log.i(TAG, "使用自动检测的版本: " + targetVersionCode);
				} else {
					Log.w(TAG, "无法检测Via版本，使用默认映射");
					return DEFAULT_MAPPINGS;
				}
			}
			if (cachedVersionCode != null && cachedVersionCode.equals(targetVersionCode)) {
				if (cachedMappings != null) {
					return cachedMappings;
				}
			}
			Map<ClassMethodKey, ClassMethodMapping> mappings = getClassMethodMappings(targetVersionCode);
			cachedVersionCode = targetVersionCode;
			cachedMappings = mappings;
			Log.i(TAG, "获取类和方法映射成功: 版本 " + targetVersionCode);
			return mappings;
		} catch (Exception e) {
			Log.e(TAG, "获取缓存映射失败", e);
		}
		return DEFAULT_MAPPINGS;
	}
	public static String getClassName(ClassMethodKey key, Context context) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = getCachedClassMethodMappings(context);
		ClassMethodMapping mapping = mappings.get(key);
		return mapping != null ? mapping.className : key.getClassName();
	}
	public static String getMethodName(ClassMethodKey key, Context context) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = getCachedClassMethodMappings(context);
		ClassMethodMapping mapping = mappings.get(key);
		return mapping != null ? mapping.methodName : key.getMethodName();
	}
	public static String getParameterClassName(ClassMethodKey key, Context context) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = getCachedClassMethodMappings(context);
		ClassMethodMapping mapping = mappings.get(key);
		return mapping != null ? mapping.parameterClassName : key.getParameterClassName();
	}
	public static ClassMethodMapping getMapping(ClassMethodKey key, Context context) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = getCachedClassMethodMappings(context);
		ClassMethodMapping mapping = mappings.get(key);
		return mapping != null
				? mapping
				: new ClassMethodMapping(key.getClassName(), key.getMethodName(), key.getParameterClassName());
	}
	public static void clearCache() {
		cachedMappings = null;
		cachedVersionCode = null;
	}
	public static void addMapping(int versionCode, ClassMethodKey key, ClassMethodMapping mapping) {
		Map<ClassMethodKey, ClassMethodMapping> versionMap = VERSION_MAPPINGS.get(versionCode);
		if (versionMap == null) {
			versionMap = new HashMap<ClassMethodKey, ClassMethodMapping>();
			VERSION_MAPPINGS.put(versionCode, versionMap);
		}
		versionMap.put(key, mapping);
		clearCache();
	}
	public static boolean isClassExists(String className, ClassLoader classLoader) {
		try {
			Class<?> clazz = classLoader.loadClass(className);
			return clazz != null;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (Exception e) {
			Log.e(TAG, "检查类是否存在时出错: " + className, e);
			return false;
		}
	}
	public static int autoFixMappings(Context context, ClassLoader classLoader) {
		Map<ClassMethodKey, ClassMethodMapping> currentMappings = getCachedClassMethodMappings(context);
		int fixedCount = 0;
		for (Map.Entry<ClassMethodKey, ClassMethodMapping> entry : currentMappings.entrySet()) {
			ClassMethodKey key = entry.getKey();
			ClassMethodMapping mapping = entry.getValue();
			if (!isClassExists(mapping.className, classLoader)) {
				Log.w(TAG, "类不存在: " + key + " -> " + mapping.className);
			}
			if (mapping.parameterClassName != null && !isClassExists(mapping.parameterClassName, classLoader)) {
				Log.w(TAG, "参数类不存在: " + key + " -> " + mapping.parameterClassName);
			}
		}
		return fixedCount;
	}
	public static Map<ClassMethodKey, ClassMethodMapping> getClassMethodMappingsByVersionCode(int versionCode) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = VERSION_MAPPINGS.get(versionCode);
		if (mappings != null) {
			return mappings;
		}
		Log.w(TAG, "未找到版本 " + versionCode + " 的映射，使用默认映射");
		return DEFAULT_MAPPINGS;
	}
	private static final int[] DEFAULT_VERSION_CODES = {20260211, 20251223, 20251024, 20250907, 20250713};
	public static void saveVersionMappings(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			JSONObject versionsJson = new JSONObject();
			for (Map.Entry<Integer, Map<ClassMethodKey, ClassMethodMapping>> versionEntry : VERSION_MAPPINGS.entrySet()) {
				int versionCode = versionEntry.getKey();
				boolean isDefault = false;
				for (int defaultCode : DEFAULT_VERSION_CODES) {
					if (defaultCode == versionCode) {
						isDefault = true;
						break;
					}
				}
				if (isDefault) {
					continue;
				}
				JSONObject mappingsJson = new JSONObject();
				Map<ClassMethodKey, ClassMethodMapping> mappings = versionEntry.getValue();
				for (Map.Entry<ClassMethodKey, ClassMethodMapping> mappingEntry : mappings.entrySet()) {
					ClassMethodKey key = mappingEntry.getKey();
					ClassMethodMapping mapping = mappingEntry.getValue();
					JSONObject mappingJson = new JSONObject();
					mappingJson.put("className", mapping.className);
					mappingJson.put("methodName", mapping.methodName);
					if (mapping.parameterClassName != null) {
						mappingJson.put("parameterClassName", mapping.parameterClassName);
					}
					mappingsJson.put(key.name(), mappingJson);
				}
				versionsJson.put(String.valueOf(versionCode), mappingsJson);
			}
			editor.putString("version_mappings", versionsJson.toString());
			editor.apply();
			Log.i(TAG, "已保存版本映射到SharedPreferences");
		} catch (Exception e) {
			Log.e(TAG, "保存版本映射失败", e);
		}
	}
	public static void restoreVersionMappings(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			String mappingsStr = sp.getString("version_mappings", null);
			if (mappingsStr == null || mappingsStr.trim().isEmpty()) {
				Log.i(TAG, "未找到持久化的版本映射");
				return;
			}
			JSONObject versionsJson = new JSONObject(mappingsStr);
			for (java.util.Iterator<String> keys = versionsJson.keys(); keys.hasNext(); ) {
				String versionCodeStr = keys.next();
				try {
					int versionCode = Integer.parseInt(versionCodeStr);
					JSONObject mappingsJson = versionsJson.getJSONObject(versionCodeStr);
					for (java.util.Iterator<String> mappingKeys = mappingsJson.keys(); mappingKeys.hasNext(); ) {
						String keyName = mappingKeys.next();
						JSONObject mappingJson = mappingsJson.getJSONObject(keyName);
						try {
							ClassMethodKey key = ClassMethodKey.valueOf(keyName);
							String className = mappingJson.getString("className");
							String methodName = mappingJson.getString("methodName");
							String parameterClassName = null;
							if (mappingJson.has("parameterClassName")) {
								parameterClassName = mappingJson.getString("parameterClassName");
								if (parameterClassName != null && parameterClassName.isEmpty()) {
									parameterClassName = null;
								}
							}
							ClassMethodMapping mapping;
							if (parameterClassName != null) {
								mapping = new ClassMethodMapping(className, methodName, parameterClassName);
							} else {
								mapping = new ClassMethodMapping(className, methodName);
							}
							Map<ClassMethodKey, ClassMethodMapping> versionMap = VERSION_MAPPINGS.get(versionCode);
							if (versionMap == null) {
								versionMap = new HashMap<ClassMethodKey, ClassMethodMapping>();
								VERSION_MAPPINGS.put(versionCode, versionMap);
							}
							versionMap.put(key, mapping);
						} catch (IllegalArgumentException e) {
							Log.w(TAG, "未知的映射键: " + keyName);
						}
					}
					Log.i(TAG, "已恢复版本 " + versionCode + " 的映射");
				} catch (NumberFormatException e) {
					Log.w(TAG, "解析版本代码失败: " + versionCodeStr);
				}
			}
			clearCache();
			Log.i(TAG, "已从SharedPreferences恢复版本映射");
		} catch (Exception e) {
			Log.e(TAG, "恢复版本映射失败", e);
		}
	}
	public static void addMapping(int versionCode, ClassMethodKey key, ClassMethodMapping mapping, Context context) {
		addMapping(versionCode, key, mapping);
		if (context != null) {
			saveVersionMappings(context);
		}
	}
}