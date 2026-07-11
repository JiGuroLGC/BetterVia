package com.jiguro.bettervia;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
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

	public static class ResourceMapping {
		public final int resourceId;

		public ResourceMapping(int resourceId) {
			this.resourceId = resourceId;
		}

		@Override
		public String toString() {
			return "ResourceMapping{" + "resourceId=0x" + Integer.toHexString(resourceId) + '}';
		}
	}

	public enum ClassMethodKey {
	
		VIA_CHECK_CLASS("t9.k", "u", "t9.a"),

		PRIVACY_LOCK_WHITELIST("kb.a6", "h3"), PRIVACY_LOCK_OVERLAY("b6.a0$a", "a"), PRIVACY_LOCK_VERIFY_1("e8.ua",
				"G0"), PRIVACY_LOCK_VERIFY_2("v8.f", "f"),

		SETTINGS_ITEM_CLASS("b6.y", ""), COMPONENT_BLOCK_CLASS("b6.m", ""), COMPONENT_CHECK_METHOD("kb.d", "X1"),

		FIREBASE_ANALYTICS("com.google.firebase.analytics.FirebaseAnalytics", "a", ""),

		SCRIPT_REPO_1("w9.c", "h"), SCRIPT_REPO_2("e8.v6", "o9"),

		VIDEO_PLAYER_CLASS("com.tuyafeng.support.widget.v", ""), VIDEO_PLAYER_FIELD_SPEED(
				"com.tuyafeng.support.widget.v", "z", ""), VIDEO_PLAYER_FIELD_CALLBACK("com.tuyafeng.support.widget.v",
						"w", ""), VIDEO_PLAYER_FIELD_SPEED_TEXT("com.tuyafeng.support.widget.v", "o",
								""), VIDEO_PLAYER_FIELD_START_X("com.tuyafeng.support.widget.v", "P",
										""), VIDEO_PLAYER_FIELD_START_Y("com.tuyafeng.support.widget.v", "Q",
												""), VIDEO_PLAYER_CALLBACK_SET_SPEED("com.tuyafeng.support.widget.v$b",
														"c", "");

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

	public enum ResourceKey {
	
		DOWNLOAD_DIALOG_COPY_LINK_BUTTON(0x7f0900c8),
		DOWNLOAD_DIALOG_CANCEL_BUTTON(0x7f0900c7),
		DOWNLOAD_DIALOG_OK_BUTTON(0x7f0900cc),
		DOWNLOAD_DIALOG_SHARE_BUTTON(0x7f09abcd);

		private final int defaultResourceId;

		ResourceKey(int defaultResourceId) {
			this.defaultResourceId = defaultResourceId;
		}

		public int getDefaultResourceId() {
			return defaultResourceId;
		}
	}

	private static final Map<Integer, Map<ClassMethodKey, ClassMethodMapping>> VERSION_MAPPINGS = new HashMap<>();

	private static final Map<ClassMethodKey, ClassMethodMapping> DEFAULT_MAPPINGS = new HashMap<>();

	private static final Map<Integer, Map<ResourceKey, ResourceMapping>> RESOURCE_ID_MAPPINGS = new HashMap<>();

	private static final Map<ResourceKey, ResourceMapping> DEFAULT_RESOURCE_MAPPINGS = new HashMap<>();

	private static final int[] DEFAULT_VERSION_CODES = {20260706, 20260410, 20260211, 20251223, 20251024, 20250907,
			20250713};

	static {
	
		for (ClassMethodKey key : ClassMethodKey.values()) {
			DEFAULT_MAPPINGS.put(key,
					new ClassMethodMapping(key.getClassName(), key.getMethodName(), key.getParameterClassName()));
		}

		for (ResourceKey key : ResourceKey.values()) {
			DEFAULT_RESOURCE_MAPPINGS.put(key, new ResourceMapping(key.getDefaultResourceId()));
		}

		Map<ClassMethodKey, ClassMethodMapping> v720Map = new HashMap<>();
		v720Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("t9.k", "u", "t9.a"));
		v720Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("kb.a6", "h3"));
		v720Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("b6.a0$a", "a"));
		v720Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("b6.y", ""));
		v720Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("b6.m", ""));
		v720Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("kb.d", "X1"));
		v720Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v720Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("e8.ua", "G0"));
		v720Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("v8.f", "f"));
		v720Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("w9.c", "h"));
		v720Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("e8.v6", "o9"));
	
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("com.tuyafeng.support.widget.v", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "z", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "w", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "o", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "P", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "Q", ""));
		v720Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED,
				new ClassMethodMapping("com.tuyafeng.support.widget.v$b", "c", ""));
		VERSION_MAPPINGS.put(20260706, v720Map);

		Map<ResourceKey, ResourceMapping> v720ResourceMap = new HashMap<>();
		v720ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c8));
		v720ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c7));
		v720ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cc));
		v720ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20260706, v720ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v710Map = new HashMap<>();
		v710Map.put(ClassMethodKey.VIA_CHECK_CLASS, new ClassMethodMapping("t9.k", "u", "t9.a"));
		v710Map.put(ClassMethodKey.PRIVACY_LOCK_WHITELIST, new ClassMethodMapping("kb.a6", "h3"));
		v710Map.put(ClassMethodKey.PRIVACY_LOCK_OVERLAY, new ClassMethodMapping("b6.a0$a", "a"));
		v710Map.put(ClassMethodKey.SETTINGS_ITEM_CLASS, new ClassMethodMapping("b6.y", ""));
		v710Map.put(ClassMethodKey.COMPONENT_BLOCK_CLASS, new ClassMethodMapping("b6.m", ""));
		v710Map.put(ClassMethodKey.COMPONENT_CHECK_METHOD, new ClassMethodMapping("kb.d", "X1"));
		v710Map.put(ClassMethodKey.FIREBASE_ANALYTICS,
				new ClassMethodMapping("com.google.firebase.analytics.FirebaseAnalytics", "a", ""));
		v710Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_1, new ClassMethodMapping("e8.ra", "H0"));
		v710Map.put(ClassMethodKey.PRIVACY_LOCK_VERIFY_2, new ClassMethodMapping("v8.f", "f"));
		v710Map.put(ClassMethodKey.SCRIPT_REPO_1, new ClassMethodMapping("w9.c", "h"));
		v710Map.put(ClassMethodKey.SCRIPT_REPO_2, new ClassMethodMapping("e8.r6", "i9"));
	
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("com.tuyafeng.support.widget.v", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "z", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "w", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "o", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "P", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y,
				new ClassMethodMapping("com.tuyafeng.support.widget.v", "Q", ""));
		v710Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED,
				new ClassMethodMapping("com.tuyafeng.support.widget.v$b", "c", ""));
		VERSION_MAPPINGS.put(20260410, v710Map);

		Map<ResourceKey, ResourceMapping> v710ResourceMap = new HashMap<>();
		v710ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c8));
		v710ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c7));
		v710ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cc));
		v710ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20260410, v710ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v700Map = new HashMap<>();
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
	
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("e.h.g.l.q", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED, new ClassMethodMapping("e.h.g.l.q", "D", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, new ClassMethodMapping("e.h.g.l.q", "A", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, new ClassMethodMapping("e.h.g.l.q", "s", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X, new ClassMethodMapping("e.h.g.l.q", "R", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y, new ClassMethodMapping("e.h.g.l.q", "S", ""));
		v700Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, new ClassMethodMapping("e.h.g.l.q.b", "c", ""));
		VERSION_MAPPINGS.put(20260211, v700Map);

		Map<ResourceKey, ResourceMapping> v700ResourceMap = new HashMap<>();
		v700ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c7));
		v700ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c6));
		v700ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cb));
		v700ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20260211, v700ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v690Map = new HashMap<>();
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
	
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("e.h.g.l.q", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED, new ClassMethodMapping("e.h.g.l.q", "D", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, new ClassMethodMapping("e.h.g.l.q", "A", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, new ClassMethodMapping("e.h.g.l.q", "s", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X, new ClassMethodMapping("e.h.g.l.q", "R", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y, new ClassMethodMapping("e.h.g.l.q", "S", ""));
		v690Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, new ClassMethodMapping("e.h.g.l.q.b", "c", ""));
		VERSION_MAPPINGS.put(20251223, v690Map);

		Map<ResourceKey, ResourceMapping> v690ResourceMap = new HashMap<>();
		v690ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c7));
		v690ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c6));
		v690ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cb));
		v690ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20251223, v690ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v680Map = new HashMap<>();
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
	
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("e.h.g.l.q", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED, new ClassMethodMapping("e.h.g.l.q", "D", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, new ClassMethodMapping("e.h.g.l.q", "A", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, new ClassMethodMapping("e.h.g.l.q", "s", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X, new ClassMethodMapping("e.h.g.l.q", "R", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y, new ClassMethodMapping("e.h.g.l.q", "S", ""));
		v680Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, new ClassMethodMapping("e.h.g.l.q.b", "c", ""));
		VERSION_MAPPINGS.put(20251024, v680Map);

		Map<ResourceKey, ResourceMapping> v680ResourceMap = new HashMap<>();
		v680ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c7));
		v680ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c6));
		v680ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cb));
		v680ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20251024, v680ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v671Map = new HashMap<>();
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
	
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("d.h.g.l.r", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED, new ClassMethodMapping("d.h.g.l.r", "A", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, new ClassMethodMapping("d.h.g.l.r", "x", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, new ClassMethodMapping("d.h.g.l.r", "p", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X, new ClassMethodMapping("d.h.g.l.r", "O", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y, new ClassMethodMapping("d.h.g.l.r", "P", ""));
		v671Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, new ClassMethodMapping("d.h.g.l.r.b", "c", ""));
		VERSION_MAPPINGS.put(20250907, v671Map);

		Map<ResourceKey, ResourceMapping> v671ResourceMap = new HashMap<>();
		v671ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c7));
		v671ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c6));
		v671ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cb));
		v671ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20250907, v671ResourceMap);

		Map<ClassMethodKey, ClassMethodMapping> v660Map = new HashMap<>();
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
	
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_CLASS, new ClassMethodMapping("d.h.g.l.r", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED, new ClassMethodMapping("d.h.g.l.r", "A", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, new ClassMethodMapping("d.h.g.l.r", "x", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, new ClassMethodMapping("d.h.g.l.r", "p", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_X, new ClassMethodMapping("d.h.g.l.r", "O", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y, new ClassMethodMapping("d.h.g.l.r", "P", ""));
		v660Map.put(ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, new ClassMethodMapping("d.h.g.l.r.b", "c", ""));
		VERSION_MAPPINGS.put(20250713, v660Map);

		Map<ResourceKey, ResourceMapping> v660ResourceMap = new HashMap<>();
		v660ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, new ResourceMapping(0x7f0900c7));
		v660ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, new ResourceMapping(0x7f0900c6));
		v660ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, new ResourceMapping(0x7f0900cb));
		v660ResourceMap.put(ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, new ResourceMapping(0x7f09abcd));
		RESOURCE_ID_MAPPINGS.put(20250713, v660ResourceMap);
	}

	private static Map<ClassMethodKey, ClassMethodMapping> cachedMappings = null;
	private static Integer cachedVersionCode = null;

	private static Map<ResourceKey, ResourceMapping> cachedResourceMappings = null;

	private static Integer userSelectedVersionCode = null;

	public static void setUserSelectedVersionCode(int versionCode) {
		userSelectedVersionCode = versionCode;
		clearCache();
		Log.i(TAG, "设置用户选择的版本: " + versionCode);
	}

	public static Integer getUserSelectedVersionCode() {
		return userSelectedVersionCode;
	}

	public static void clearCache() {
		cachedMappings = null;
		cachedVersionCode = null;
		cachedResourceMappings = null;
	}

	public static Map<ClassMethodKey, ClassMethodMapping> getClassMethodMappingsByVersionCode(int versionCode) {
		Map<ClassMethodKey, ClassMethodMapping> mappings = VERSION_MAPPINGS.get(versionCode);
		if (mappings != null) {
			return mappings;
		}

		Log.w(TAG, "未找到版本 " + versionCode + " 的映射，使用默认映射");
		return DEFAULT_MAPPINGS;
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

	private static Map<ResourceKey, ResourceMapping> getResourceIdMappingsByVersionCode(int versionCode) {
		Map<ResourceKey, ResourceMapping> mappings = RESOURCE_ID_MAPPINGS.get(versionCode);
		if (mappings != null) {
			return mappings;
		}

		Log.w(TAG, "未找到版本 " + versionCode + " 的资源ID映射，使用默认映射");
		return DEFAULT_RESOURCE_MAPPINGS;
	}

	private static Map<ResourceKey, ResourceMapping> getCachedResourceIdMappings(Context context) {
		try {
		
			int targetVersionCode;
			if (userSelectedVersionCode != null) {
				targetVersionCode = userSelectedVersionCode;
			} else {
				ViaVersionDetector.VersionInfo versionInfo = ViaVersionDetector.detectViaVersion(context);
				if (versionInfo != null) {
					targetVersionCode = versionInfo.versionCode;
				} else {
					Log.w(TAG, "无法检测Via版本，使用默认资源ID映射");
					return DEFAULT_RESOURCE_MAPPINGS;
				}
			}

			if (cachedVersionCode != null && cachedVersionCode.equals(targetVersionCode)) {
				if (cachedResourceMappings != null) {
					return cachedResourceMappings;
				}
			}

			Map<ResourceKey, ResourceMapping> mappings = getResourceIdMappingsByVersionCode(targetVersionCode);
			cachedVersionCode = targetVersionCode;
			cachedResourceMappings = mappings;

			Log.i(TAG, "获取资源ID映射成功: 版本 " + targetVersionCode);
			return mappings;
		} catch (Exception e) {
			Log.e(TAG, "获取缓存资源ID映射失败", e);
		}

		return DEFAULT_RESOURCE_MAPPINGS;
	}

	public static int getResourceId(ResourceKey key, Context context) {
		Map<ResourceKey, ResourceMapping> mappings = getCachedResourceIdMappings(context);
		ResourceMapping mapping = mappings.get(key);
		return mapping != null ? mapping.resourceId : key.getDefaultResourceId();
	}

	public static Map<ResourceKey, ResourceMapping> getResourceIdMappings(int versionCode) {
		Map<ResourceKey, ResourceMapping> mappings = RESOURCE_ID_MAPPINGS.get(versionCode);
		if (mappings != null) {
			return mappings;
		}

		int recommendedVersion = ViaVersionDetector.getRecommendedVersion(versionCode);
		mappings = RESOURCE_ID_MAPPINGS.get(recommendedVersion);
		if (mappings != null) {
			Log.i(TAG, "使用推荐版本的资源ID映射: 目标版本 " + versionCode + " -> 推荐版本 " + recommendedVersion);
			return mappings;
		}

		Log.w(TAG, "未找到版本 " + versionCode + " 的资源ID映射，使用默认映射");
		return DEFAULT_RESOURCE_MAPPINGS;
	}

	public static void addMapping(int versionCode, ClassMethodKey key, ClassMethodMapping mapping) {
		Map<ClassMethodKey, ClassMethodMapping> versionMap = VERSION_MAPPINGS.get(versionCode);
		if (versionMap == null) {
			versionMap = new HashMap<>();
			VERSION_MAPPINGS.put(versionCode, versionMap);
		}
		versionMap.put(key, mapping);
		clearCache();
	}

	public static void addMapping(int versionCode, ClassMethodKey key, ClassMethodMapping mapping, Context context) {
		addMapping(versionCode, key, mapping);
		if (context != null) {
			saveVersionMappings(context);
		}
	}

	public static void addResourceMapping(int versionCode, ResourceKey key, ResourceMapping mapping) {
		Map<ResourceKey, ResourceMapping> versionMap = RESOURCE_ID_MAPPINGS.get(versionCode);
		if (versionMap == null) {
			versionMap = new HashMap<>();
			RESOURCE_ID_MAPPINGS.put(versionCode, versionMap);
		}
		versionMap.put(key, mapping);
		clearCache();
	}

	public static void addResourceMapping(int versionCode, ResourceKey key, ResourceMapping mapping, Context context) {
		addResourceMapping(versionCode, key, mapping);
		if (context != null) {
			saveVersionMappings(context);
		}
	}

	public static void saveVersionMappings(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			JSONObject versionsJson = new JSONObject();

			for (Map.Entry<Integer, Map<ClassMethodKey, ClassMethodMapping>> versionEntry : VERSION_MAPPINGS
					.entrySet()) {
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

			JSONObject resourceVersionsJson = new JSONObject();

			for (Map.Entry<Integer, Map<ResourceKey, ResourceMapping>> versionEntry : RESOURCE_ID_MAPPINGS.entrySet()) {
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

				JSONObject resourceMappingsJson = new JSONObject();
				Map<ResourceKey, ResourceMapping> resourceMappings = versionEntry.getValue();

				for (Map.Entry<ResourceKey, ResourceMapping> resourceMappingEntry : resourceMappings.entrySet()) {
					ResourceKey key = resourceMappingEntry.getKey();
					ResourceMapping resourceMapping = resourceMappingEntry.getValue();

					JSONObject resourceMappingJson = new JSONObject();
					resourceMappingJson.put("resourceId", resourceMapping.resourceId);

					resourceMappingsJson.put(key.name(), resourceMappingJson);
				}

				resourceVersionsJson.put(String.valueOf(versionCode), resourceMappingsJson);
			}

			editor.putString("resource_id_mappings", resourceVersionsJson.toString());
			editor.apply();
			Log.i(TAG, "已保存版本映射和资源ID映射到SharedPreferences");

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

			for (java.util.Iterator<String> keys = versionsJson.keys(); keys.hasNext();) {
				String versionCodeStr = keys.next();
				try {
					int versionCode = Integer.parseInt(versionCodeStr);
					JSONObject mappingsJson = versionsJson.getJSONObject(versionCodeStr);

					for (java.util.Iterator<String> mappingKeys = mappingsJson.keys(); mappingKeys.hasNext();) {
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
								versionMap = new HashMap<>();
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

			String resourceMappingsStr = sp.getString("resource_id_mappings", null);
			if (resourceMappingsStr != null && !resourceMappingsStr.trim().isEmpty()) {
				JSONObject resourceVersionsJson = new JSONObject(resourceMappingsStr);

				for (java.util.Iterator<String> keys = resourceVersionsJson.keys(); keys.hasNext();) {
					String versionCodeStr = keys.next();
					try {
						int versionCode = Integer.parseInt(versionCodeStr);
						JSONObject resourceMappingsJson = resourceVersionsJson.getJSONObject(versionCodeStr);

						for (java.util.Iterator<String> resourceMappingKeys = resourceMappingsJson
								.keys(); resourceMappingKeys.hasNext();) {
							String keyName = resourceMappingKeys.next();
							JSONObject resourceMappingJson = resourceMappingsJson.getJSONObject(keyName);

							try {
								ResourceKey key = ResourceKey.valueOf(keyName);
								int resourceId = resourceMappingJson.getInt("resourceId");

								ResourceMapping resourceMapping = new ResourceMapping(resourceId);

								Map<ResourceKey, ResourceMapping> resourceVersionMap = RESOURCE_ID_MAPPINGS
										.get(versionCode);
								if (resourceVersionMap == null) {
									resourceVersionMap = new HashMap<>();
									RESOURCE_ID_MAPPINGS.put(versionCode, resourceVersionMap);
								}
								resourceVersionMap.put(key, resourceMapping);

							} catch (IllegalArgumentException e) {
								Log.w(TAG, "未知的资源ID映射键: " + keyName);
							}
						}

						Log.i(TAG, "已恢复版本 " + versionCode + " 的资源ID映射");

					} catch (NumberFormatException e) {
						Log.w(TAG, "解析资源ID版本代码失败: " + versionCodeStr);
					}
				}

				Log.i(TAG, "已从SharedPreferences恢复资源ID映射");
			}

		} catch (Exception e) {
			Log.e(TAG, "恢复版本映射失败", e);
		}
	}

	public static void clearNetworkMappings(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			Iterator<Map.Entry<Integer, Map<ClassMethodKey, ClassMethodMapping>>> iterator = VERSION_MAPPINGS.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, Map<ClassMethodKey, ClassMethodMapping>> entry = iterator.next();
				int versionCode = entry.getKey();

				boolean isDefault = false;
				for (int defaultCode : DEFAULT_VERSION_CODES) {
					if (defaultCode == versionCode) {
						isDefault = true;
						break;
					}
				}

				if (!isDefault) {
					iterator.remove();
					Log.i(TAG, "已清理网络映射: 版本 " + versionCode);
				}
			}

			Iterator<Map.Entry<Integer, Map<ResourceKey, ResourceMapping>>> resourceIterator = RESOURCE_ID_MAPPINGS
					.entrySet().iterator();
			while (resourceIterator.hasNext()) {
				Map.Entry<Integer, Map<ResourceKey, ResourceMapping>> entry = resourceIterator.next();
				int versionCode = entry.getKey();

				boolean isDefault = false;
				for (int defaultCode : DEFAULT_VERSION_CODES) {
					if (defaultCode == versionCode) {
						isDefault = true;
						break;
					}
				}

				if (!isDefault) {
					resourceIterator.remove();
					Log.i(TAG, "已清理网络资源ID映射: 版本 " + versionCode);
				}
			}

			editor.remove("version_mappings");
			editor.remove("resource_id_mappings");
			editor.remove("mapping_sync_time");
			editor.apply();

			clearCache();

			ViaVersionDetector.clearNetworkData(context);

			Log.i(TAG, "已清理所有从网络同步的映射配置和相关XML数据");

		} catch (Exception e) {
			Log.e(TAG, "清理网络映射失败", e);
		}
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
}
