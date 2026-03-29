package com.jiguro.bettervia;
import android.animation.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.os.Process;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.media.*;
import com.android.apksig.ApkSigner;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.json.*;
import android.content.ClipboardManager;
public class Hook implements IXposedHookLoadPackage {
	private static final String MODULE_VERSION_NAME = "1.8.0";
	private static final int MODULE_VERSION_CODE = 20260401;
	private static final String SUPPORTED_VIA_VERSION = "7.0.0";
	private static volatile boolean hasShownBlockedToast = false;
	private static Activity Context = null;
	private static Object moduleButtonRef = null;
	private static String currentPackageName = "";
	private static Activity currentActivity = null;
	private static final String KEY_WHITELIST = "enable_whitelist_hook";
	private static final String KEY_BLOCK_STARTUP_MESSAGE = "block_startup_message";
	private static final String KEY_BLOCK_GOOGLE_SERVICES = "block_google_services";
	private static final String KEY_DOWNLOAD_DIALOG_SHARE = "download_dialog_share";
	private static final String KEY_SHOW_URL_SCHEME = "show_url_scheme";
	private static final String KEY_EYE_PROTECTION = "eye_protection_mode";
	private static final String KEY_EYE_TEMPERATURE = "eye_protection_temperature";
	private static final String KEY_EYE_TEXTURE = "eye_protection_texture";
	private static final String KEY_HOMEPAGE_BG = "homepage_background_image";
	private static final String KEY_HOMEPAGE_MASK_A = "homepage_mask_alpha";
	private static final String KEY_HOMEPAGE_MASK_C = "homepage_mask_color";
	private static final String KEY_BLOCK_MENU_BAR = "block_menu_bar_urls";
	private static final String KEY_HIDE_STATUS_BAR = "hide_status_bar";
	private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
	private static final String KEY_SCREENSHOT_PROTECTION = "screenshot_protection";
	private static final String KEY_NETWORK_SOURCE = "network_source";
	private static final String KEY_HAS_NETWORK_SOURCE = "has_network_source";
	private static final String KEY_MODULE_THEME = "module_theme";
	private static final String KEY_AUTO_UPDATE = "auto_update";
	private static final String KEY_CUSTOM_TOAST = "custom_toast";
	private static final String KEY_CURRENT_THEME = "current_homepage_theme";
	private static final String KEY_BACKGROUND_VIDEO = "background_video_audio";
	private static final String KEY_VERSION_CHECK_DISABLED = "version_check_disabled";
	private static final String KEY_SELECTED_VIA_VERSION = "selected_via_version";
	private static final String KEY_HAS_LANGUAGE_SELECTION = "has_language_selection";
	private static final String KEY_HAS_USER_AGREEMENT = "has_user_agreement";
	private static final String KEY_DEVELOPER_MODE = "developer_mode";
	private static final String KEY_BLOCK_SWIPE_BACK = "block_swipe_back";
	private static final String KEY_MONET_BASE = "monet_base_version"; 
	private static final String KEY_MONET_PKG = "monet_package_name";
	private static final String KEY_MONET_VER_NAME = "monet_version_name";
	private static final String KEY_MONET_VER_CODE = "monet_version_code";
	private static final String KEY_MONET_USE_ICON = "monet_use_icon";
	private static final String KEY_MONET_MAKE_LITE = "monet_make_lite";
	private static final String KEY_MONET_SIGN_SCHEME = "monet_sign_scheme"; 
	private static final String KEY_MONET_OUTPUT_LOCATION = "monet_output_location"; 
	private static final String KEY_LAST_VIA_VERSION_CODE = "last_via_version_code"; 
	private static final String KEY_LAST_VIA_VERSION_NAME = "last_via_version_name"; 
	private static final String KEY_LAST_VIA_UPDATE_TIME = "last_via_update_time"; 
	private static final String KEY_PRIVACY_LOCK_ENABLE = "privacy_lock_enable";
	private static final String KEY_PRIVACY_LOCK_APPLY_STARTUP = "privacy_lock_apply_startup";
	private static final String KEY_PRIVACY_LOCK_APPLY_HISTORY = "privacy_lock_apply_history";
	private static final String KEY_PRIVACY_LOCK_APPLY_BOOKMARKS = "privacy_lock_apply_bookmarks";
	private static final String KEY_PRIVACY_LOCK_APPLY_OFFLINE = "privacy_lock_apply_offline";
	private static final String KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE = "privacy_lock_apply_comprehensive";
	private static final String KEY_PRIVACY_LOCK_PASSWORD_TYPE = "privacy_lock_password_type"; 
	private static final String KEY_PRIVACY_LOCK_PASSWORD_SET = "privacy_lock_password_set"; 
	private static boolean whitelistHookEnabled = true;
	private static boolean eyeProtectionEnabled = false;
	private static boolean blockGoogleServicesEnabled = false;
	private static boolean blockStartupMessageEnabled = false;
	private static boolean screenshotProtectionEnabled = false;
	private static boolean keepScreenOnEnabled = false;
	private static boolean hideStatusBarEnabled = false;
	private static boolean autoUpdateEnabled = true;
	private static boolean downloadDialogShareEnabled = false;
	private static boolean showUrlSchemeEnabled = false;
	private static int eyeTemperature = 50;
	private static int eyeTexture = 0;
	private static String homepageBgPath = "";
	private static int homepageMaskAlpha = 120;
	private static PasswordManager patternPasswordManager = null;
	private static int homepageMaskColor = 0x80000000;
	private static boolean backgroundVideoEnabled = false;
	private static boolean developerModeEnabled = false;
	private static boolean blockSwipeBackEnabled = false;
	private static PrintWriter logWriter = null;
	private static String logFilePath = null;
	private static final int MONET_BUFFER_SIZE = 8192;
	private static volatile boolean monetProcessing = false;
	private static volatile boolean monetCancelled = false;
	private static boolean isPrivacyLockSwitchChanging = false; 
	private static boolean isApplyStartupCbChanging = false; 
	private static boolean isApplyHistoryCbChanging = false; 
	private static boolean isApplyBookmarksCbChanging = false; 
	private static boolean isApplyOfflineCbChanging = false; 
	private static boolean isApplyComprehensiveCbChanging = false; 
	private static int selectedViaVersionCode = 20260211; 
	private static String currentDetectedVersion = null; 
	private static boolean hasShownStartupDialog = false; 
	private static XC_MethodHook.Unhook whitelistHook = null;
	private static XC_MethodHook.Unhook componentHook = null;
	private static XC_MethodHook.Unhook activityHook = null;
	private static XC_MethodHook.Unhook firebaseAnalyticsHook = null;
	private static XC_MethodHook.Unhook googleAnalyticsHook = null;
	private static XC_MethodHook.Unhook screenshotProtectionHook = null;
	private static XC_MethodHook.Unhook keepScreenOnHook = null;
	private static XC_MethodHook.Unhook hideStatusBarHook = null;
	private static XC_MethodHook.Unhook downloadDialogShareHook = null;
	private static XC_MethodHook.Unhook showUrlSchemeHook = null;
	private static XC_MethodHook.Unhook backgroundVideoHook = null;
	private static XC_MethodHook.Unhook swipeBackHook = null;
	private static final String[] COMPONENT_KEYS = {"block_update", 
			"block_telegram", 
			"block_qq", 
			"block_email", 
			"block_wechat", 
			"block_donate", 
			"block_assist", 
			"block_agreement", 
			"block_privacy", 
			"block_opensource", 
			"block_icp" 
	};
	private static final List<View> urlSchemeOverlays = new ArrayList<View>();
	private static final List<TextView> urlSchemeTextViews = new ArrayList<TextView>();
	private static final List<String> displayedUrlSchemes = new ArrayList<String>();
	private static final List<Handler> urlSchemeDismissHandlers = new ArrayList<Handler>();
	private static final int URL_SCHEME_DISPLAY_DURATION = 8000; 
	public static final String NETWORK_SOURCE_GITEE = "gitee";
	public static final String NETWORK_SOURCE_GITHUB = "github";
	public static final String DEFAULT_NETWORK_SOURCE = NETWORK_SOURCE_GITEE;
	private static final String MODULE_THEME_AUTO = "auto";
	private static final String MODULE_THEME_LIGHT = "light";
	private static final String MODULE_THEME_DARK = "dark";
	private static final String DEFAULT_MODULE_THEME = MODULE_THEME_AUTO;
	private static final int LIGHT_BG_COLOR = Color.WHITE;
	private static final int LIGHT_TITLE_COLOR = 0xFF6200EE;
	private static final int LIGHT_TEXT_COLOR = Color.BLACK;
	private static final int LIGHT_HINT_COLOR = 0xFF666666;
	private static final int LIGHT_DIVIDER_COLOR = 0xFFDDDDDD;
	private static final int LIGHT_BTN_BG_COLOR = 0xFFE0E0E0;
	private static final int LIGHT_BTN_TEXT_COLOR = 0xFF000000;
	private static final int LIGHT_OK_BTN_BG_COLOR = 0xFF6200EE;
	private static final int LIGHT_OK_BTN_TEXT_COLOR = Color.WHITE;
	private static final int LIGHT_SWITCH_ON_COLOR = 0xFF6200EE;
	private static final int LIGHT_SWITCH_OFF_COLOR = 0xFFBDBDBD;
	private static final int LIGHT_ITEM_BG_COLOR = 0xFFF8F9FA;
	private static final int LIGHT_EDIT_BG_COLOR = 0xFFF5F5F5;
	private static final int DARK_BG_COLOR = 0xFF18181A;
	private static final int DARK_TITLE_COLOR = 0xFF87CEEB;
	private static final int DARK_TEXT_COLOR = 0xFFE0E0E0;
	private static final int DARK_HINT_COLOR = 0xFFAAAAAA;
	private static final int DARK_DIVIDER_COLOR = 0xFF333333;
	private static final int DARK_BTN_BG_COLOR = 0xFF2D2D2D;
	private static final int DARK_BTN_TEXT_COLOR = 0xFFE0E0E0;
	private static final int DARK_OK_BTN_BG_COLOR = 0xFF87CEEB;
	private static final int DARK_OK_BTN_TEXT_COLOR = Color.BLACK;
	private static final int DARK_SWITCH_ON_COLOR = 0xFF87CEEB;
	private static final int DARK_SWITCH_OFF_COLOR = 0xFF424242;
	private static final int DARK_ITEM_BG_COLOR = 0xFF252525;
	private static final int DARK_EDIT_BG_COLOR = 0xFF2D2D2D;
	private static final String GITEE_THEMES_JSON_URL = "https:
	private static final String GITHUB_THEMES_JSON_URL = "https:
	private static final String GITHUB_UPDATE_URL = "https:
	private static final String GITEE_UPDATE_URL = "https:
	private static final String GITEE_SHISUI_JSON_URL = "https:
	private static final String GITHUB_SHISUI_JSON_URL = "https:
	private static final String MONET_JSON_URL_GITEE = "https:
	private static final String MONET_JSON_URL_GITHUB = "https:
	private static final String NEWEST_JSON_URL_GITEE = "https:
	private static final String NEWEST_JSON_URL_GITHUB = "https:
	public static final String GITEE_MAPPING_JSON_URL = "https:
	public static final String GITHUB_MAPPING_JSON_URL = "https:
	private static final String MONET_TEMP_DIR = "/storage/emulated/0/Android/data/mark.via/files/BetterVia/temp/";
	private static String generatedApkPath = null;
	private static List<ThemeInfo> loadedThemes = new ArrayList<>();
	private static boolean themesLoaded = false;
	private static boolean themesLoading = false;
	private static Map<Activity, View> overlayViews = new WeakHashMap<>();
	private static Map<Activity, Boolean> screenOnActivities = new WeakHashMap<>();
	private static Map<Activity, Boolean> statusBarHiddenActivities = new WeakHashMap<>();
	private static Map<Activity, Runnable> statusBarRehideRunnables = new WeakHashMap<>();
	private static final int REHIDE_DELAY = 3000;
	private static final String DEFAULT_THEME_ID = "default";
	private static final String COOKIE_TABLE_NAME = "cookies";
	public static class ThemeInfo {
		String id;
		Map<String, String> nameMap;
		Map<String, String> authorMap;
		String previewUrl;
		Map<String, String> htmlUrls;
		Map<String, String> cssUrls;
		ThemeInfo(String id, Map<String, String> nameMap, Map<String, String> authorMap, String previewUrl,
				Map<String, String> htmlUrls, Map<String, String> cssUrls) {
			this.id = id;
			this.nameMap = nameMap;
			this.authorMap = authorMap;
			this.previewUrl = previewUrl;
			this.htmlUrls = htmlUrls;
			this.cssUrls = cssUrls;
		}
		String getName(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
			}
			return langCode;
		}
		String getAuthor(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return authorMap.getOrDefault(langCode, authorMap.get("zh-CN"));
			}
			return langCode;
		}
		static ThemeInfo fromJSON(JSONObject json) throws JSONException {
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext()) {
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> authorMap = new HashMap<>();
			JSONObject authors = json.getJSONObject("authors");
			Iterator<String> authorKeys = authors.keys();
			while (authorKeys.hasNext()) {
				String lang = authorKeys.next();
				authorMap.put(lang, authors.getString(lang));
			}
			String previewUrl = json.getString("previewUrl");
			Map<String, String> htmlUrls = new HashMap<>();
			JSONObject htmls = json.getJSONObject("htmlUrls");
			Iterator<String> htmlKeys = htmls.keys();
			while (htmlKeys.hasNext()) {
				String pkg = htmlKeys.next();
				htmlUrls.put(pkg, htmls.getString(pkg));
			}
			Map<String, String> cssUrls = new HashMap<>();
			JSONObject csss = json.getJSONObject("cssUrls");
			Iterator<String> cssKeys = csss.keys();
			while (cssKeys.hasNext()) {
				String pkg = cssKeys.next();
				cssUrls.put(pkg, csss.getString(pkg));
			}
			return new ThemeInfo(id, nameMap, authorMap, previewUrl, htmlUrls, cssUrls);
		}
		private String getLanguageCode(Context ctx) {
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved)) {
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				} else {
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
					return "zh-CN";
				} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
					return "zh-TW";
				} else if (Locale.ENGLISH.equals(locale)) {
					return "en";
				}
				return "zh-CN";
			}
			return saved;
		}
	}
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam pkg) throws Throwable {
		if (pkg.packageName.equals("com.jiguro.bettervia")) {
			try {
				Class<?> clazz = pkg.classLoader.loadClass("com.jiguro.bettervia.ModuleStatus");
				Field field = clazz.getDeclaredField("activated");
				field.setAccessible(true);
				field.setBoolean(null, true);
			} catch (Throwable ignored) {
			}
			return;
		}
		try {
			pkg.classLoader.loadClass("com.tuyafeng.support.crash.UnknowException");
			currentPackageName = pkg.packageName;
			handleViaApp(pkg);
		} catch (ClassNotFoundException e) {
		}
	}
	private void handleViaApp(final XC_LoadPackage.LoadPackageParam param) {
		XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam attachParam) throws Throwable {
				final Context ctx = (Context) attachParam.args[0];
				final ClassLoader cl = ctx.getClassLoader();
				if (shouldBlockHook(ctx, param.packageName)) {
					showBlockedToast(ctx);
					return;
				}
				ViaVersionDetector.restoreSupportedVersions(ctx);
				ViaClassMapping.restoreVersionMappings(ctx);
				bvLog("[BetterVia] 已恢复持久化的版本和映射数据");
				ViaVersionDetector.VersionInfo viaVersion = ViaVersionDetector.detectViaVersion(ctx);
				if (viaVersion != null) {
					bvLog("[BetterVia] 检测到Via版本: " + viaVersion.versionName + " (code: " + viaVersion.versionCode
							+ ")");
				} else {
					bvLog("[BetterVia] 无法检测Via版本，使用默认类名映射");
				}
				int savedVersionCode = getPrefInt(ctx, KEY_SELECTED_VIA_VERSION, 20260211);
				selectedViaVersionCode = savedVersionCode;
				ViaClassMapping.setUserSelectedVersionCode(savedVersionCode);
				int userSelectedVersionCode = selectedViaVersionCode;
				Map<ViaClassMapping.ClassMethodKey, ViaClassMapping.ClassMethodMapping> classMethodMappings = ViaClassMapping
						.getClassMethodMappingsByVersionCode(userSelectedVersionCode);
				bvLog("[BetterVia] 类和方法映射表已加载（使用版本: " + ViaVersionDetector.getVersionName(userSelectedVersionCode)
						+ "）");
				XposedHelpers.findAndHookMethod(Toast.class, "makeText", Context.class, CharSequence.class, int.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								if (param.getResult() == null)
									return;
								CharSequence msg = (CharSequence) param.args[1];
								if (msg == null)
									return;
								String message = msg.toString();
								if (message.contains("token") && message.contains("not valid")) {
									StackTraceElement[] stack = Thread.currentThread().getStackTrace();
									for (StackTraceElement el : stack) {
										if (el.getClassName().contains("mark.via.BrowserApp")) {
											bvLog("[BetterVia] 已屏蔽 BrowserApp 的 BadTokenException Toast: " + message);
											param.setResult(null);
											return;
										}
									}
								}
							}
						});
				if (Context == null) {
					XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							final Activity activity = (Activity) param.thisObject;
							final boolean privacyLockEnabled = getPrivacyLockBoolean(activity, KEY_PRIVACY_LOCK_ENABLE,
									false);
							final boolean applyStartup = getPrivacyLockBoolean(activity, KEY_PRIVACY_LOCK_APPLY_STARTUP,
									false);
							final boolean startupImageEnabled = StartupExecutionHelper.getStartupImageEnable(activity);
							if (!privacyLockEnabled || !applyStartup) {
								if (startupImageEnabled) {
									new Handler(Looper.getMainLooper()).post(new Runnable() {
										@Override
										public void run() {
											bvLog("[BetterVia] 在onCreate之前显示启动图");
											StartupExecutionHelper.showStartupImageEarly(activity, new Runnable() {
												@Override
												public void run() {
													bvLog("[BetterVia] 早期启动图显示完成");
												}
											});
										}
									});
								}
							} else {
								if (startupImageEnabled) {
									bvLog("[BetterVia] 隐私锁已启用，启动图将在隐私锁覆盖层上显示");
								} else {
									bvLog("[BetterVia] 隐私锁已启用，启动图未启用");
								}
							}
						}
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							if (Context == null) {
								Context = (Activity) param.thisObject;
								checkBasicSettings(ctx);
								if (!getPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, false)) {
									jiguroMessage(
											LocalizedStringProvider.getInstance().get(ctx, "hook_success_message"));
								}
								bvLog("[BetterVia] 初加载成功，得到Via活动上下文");
								final boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE,
										false);
								final boolean applyStartup = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP,
										false);
								final boolean startupImageEnabled = StartupExecutionHelper.getStartupImageEnable(ctx);
								if (!privacyLockEnabled || !applyStartup) {
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											if (!Context.isFinishing() && !Context.isDestroyed()) {
												bvLog("[BetterVia] 无隐私锁模式，执行音乐和提示");
												StartupExecutionHelper.executeStartupWithoutImage(Context,
														new Runnable() {
															@Override
															public void run() {
																bvLog("[BetterVia] 无隐私锁模式，启动执行流程完成");
															}
														});
											}
										}
									}, 200); 
								} else {
									if (startupImageEnabled) {
										bvLog("[BetterVia] 隐私锁已启用，启动图已在beforeHookedMethod中显示，等待密码验证后执行音乐和提示");
									} else {
										bvLog("[BetterVia] 隐私锁已启用，启动图未启用，等待密码验证后执行音乐和提示");
									}
								}
							}
							currentActivity = (Activity) param.thisObject;
							try {
								developerModeEnabled = getPrefBoolean((Context) param.thisObject, KEY_DEVELOPER_MODE,
										false);
								if (developerModeEnabled) {
									initLogFile();
									bvLog("[BetterVia] 初加载成功，得到Via活动上下文");
									bvLog("[BetterVia] 恢复开发者模式：日志写入初始化完成");
								}
							} catch (Throwable t) {
								XposedBridge.log("[BetterVia] 恢复开发者模式失败: " + t);
							}
						}
					});
				}
				boolean hasLanguageSelection = getPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, false);
				boolean hasNetworkSource = getPrefBoolean(ctx, KEY_HAS_NETWORK_SOURCE, false);
				boolean hasUserAgreement = getPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
				boolean hasVersionSelection = getPrefBoolean(ctx, "has_version_selection", false);
				if (hasLanguageSelection && hasNetworkSource && hasUserAgreement && hasVersionSelection) {
					eyeProtectionEnabled = getPrefBoolean(ctx, KEY_EYE_PROTECTION, false);
					eyeTemperature = getPrefInt(ctx, KEY_EYE_TEMPERATURE, 50);
					eyeTexture = getPrefInt(ctx, KEY_EYE_TEXTURE, 0);
					setEyeProtectionMode(ctx, cl, eyeProtectionEnabled);
					whitelistHookEnabled = getPrefBoolean(ctx, KEY_WHITELIST, true);
					setWhitelistHook(ctx, cl, whitelistHookEnabled);
					setComponentBlockHook(ctx, cl, true);
					screenshotProtectionEnabled = getPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, false);
					setScreenshotProtection(ctx, cl, screenshotProtectionEnabled);
					hideStatusBarEnabled = getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false);
					setHideStatusBar(ctx, cl, hideStatusBarEnabled);
					blockSwipeBackEnabled = getPrefBoolean(ctx, KEY_BLOCK_SWIPE_BACK, false);
					setBlockSwipeBack(ctx, cl, blockSwipeBackEnabled);
					homepageBgPath = getPrefString(ctx, KEY_HOMEPAGE_BG, "");
					homepageMaskAlpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
					homepageMaskColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x80000000);
					if (!homepageBgPath.equals("")) {
						hookHomepageBgWithMask(ctx, cl, homepageBgPath, homepageMaskColor);
					}
					boolean blockGoogleServices = getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false);
					setGoogleServicesInterceptHook(ctx, cl, blockGoogleServices);
					downloadDialogShareEnabled = getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false);
					if (downloadDialogShareEnabled) {
						setDownloadDialogShareHook(ctx, cl, true);
					}
					showUrlSchemeEnabled = getPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, false);
					if (showUrlSchemeEnabled) {
						setUrlSchemeHook(ctx, cl, true);
					}
					autoUpdateEnabled = getPrefBoolean(ctx, KEY_AUTO_UPDATE, true);
					if (autoUpdateEnabled) {
						checkUpdateOnStart(ctx);
					}
					if (whitelistHookEnabled) {
						String viaCheckClass = ViaClassMapping
								.getClassName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS, ctx);
						String viaCheckMethod = ViaClassMapping
								.getMethodName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS, ctx);
						String viaCheckParamClass = ViaClassMapping
								.getParameterClassName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS, ctx);
						XposedHelpers.findAndHookMethod(viaCheckClass, cl, viaCheckMethod, viaCheckParamClass,
								new XC_MethodHook() {
									@Override
									protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
										param.setResult(null);
										bvLog("[BetterVia] 已解除Via白名单限制");
									}
								});
					}
					keepScreenOnEnabled = getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false);
					setKeepScreenOn(ctx, cl, keepScreenOnEnabled);
					backgroundVideoEnabled = getPrefBoolean(ctx, KEY_BACKGROUND_VIDEO, false);
					setBackgroundVideoAudio(ctx, cl, backgroundVideoEnabled);
				} else {
					bvLog("[BetterVia] 用户未完成初始设置（语言选择/用户协议/版本选择），暂不执行Hook功能（隐私锁除外）");
				}
				setPrivacyLockStartupHook(ctx, cl);
				setPrivacyLockPageAccessHook(ctx, cl);
				if (hasLanguageSelection && hasNetworkSource && hasUserAgreement && hasVersionSelection) {
					String privacyLockWhitelistClass = ViaClassMapping
							.getClassName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_WHITELIST, ctx);
					String privacyLockWhitelistMethod = ViaClassMapping
							.getMethodName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_WHITELIST, ctx);
					XposedHelpers.findAndHookMethod(privacyLockWhitelistClass, cl, privacyLockWhitelistMethod,
							new XC_MethodHook() {
								@Override
								protected void afterHookedMethod(MethodHookParam param) throws Throwable {
									List<Object> orig = (List<Object>) param.getResult();
									if (orig == null)
										orig = new ArrayList<>();
									List<Object> nList = new ArrayList<>(orig);
									String settingsItemClass = ViaClassMapping
											.getClassName(ViaClassMapping.ClassMethodKey.SETTINGS_ITEM_CLASS, ctx);
									Class<?> yClass = XposedHelpers.findClass(settingsItemClass, cl);
									String txt = LocalizedStringProvider.getInstance().get(ctx, "module_settings");
									Object btn = XposedHelpers.newInstance(yClass, 1000, txt);
									moduleButtonRef = btn;
									nList.add(btn);
									param.setResult(nList);
									bvLog("[BetterVia] 已在Via设置列表中添加模块按钮");
								}
							});
					String privacyLockOverlayClass = ViaClassMapping
							.getClassName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_OVERLAY, ctx);
					String privacyLockOverlayMethod = ViaClassMapping
							.getMethodName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_OVERLAY, ctx);
					XposedHelpers.findAndHookMethod(privacyLockOverlayClass, cl, privacyLockOverlayMethod, View.class,
							new XC_MethodHook() {
								@Override
								protected void afterHookedMethod(MethodHookParam param) throws Throwable {
									Object clicked = XposedHelpers.getObjectField(param.thisObject, "d");
									if (clicked == null)
										return;
									int id = XposedHelpers.getIntField(clicked, "b");
									if (id == 1000) {
										bvLog("[BetterVia] 模块按钮被点击");
										showSettingsDialog(ctx);
									}
								}
							});
				} else {
					bvLog("[BetterVia] 用户未选择版本，暂不添加模块按钮");
				}
				XposedHelpers.findAndHookMethod(Activity.class, "onActivityResult", int.class, int.class, Intent.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								int req = (Integer) param.args[0];
								int res = (Integer) param.args[1];
								Intent data = (Intent) param.args[2];
								handleActivityResult(req, res, data, (Activity) param.thisObject);
							}
						});
				XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						Activity activity = (Activity) param.thisObject;
						screenOnActivities.remove(activity);
						statusBarHiddenActivities.remove(activity);
						Runnable rehideRunnable = statusBarRehideRunnables.get(activity);
						if (rehideRunnable != null) {
							View decorView = activity.getWindow().getDecorView();
							decorView.removeCallbacks(rehideRunnable);
							statusBarRehideRunnables.remove(activity);
						}
						if (currentActivity == activity) {
							currentActivity = null;
						}
						if (privacyLockOverlayView != null) {
							ViewParent parent = privacyLockOverlayView.getParent();
							if (parent instanceof ViewGroup) {
								ViewGroup parentGroup = (ViewGroup) parent;
								if (parentGroup == activity.getWindow().getDecorView()) {
									bvLog("[BetterVia] 清理隐私锁覆盖层");
									parentGroup.removeView(privacyLockOverlayView);
									privacyLockOverlayView = null;
								}
							}
						}
						String activityName = activity.getClass().getName();
						if (activityName.equals("mark.via.Shell")
								|| activityName.startsWith(currentPackageName + ".Shell")) {
							bvLog("[BetterVia] Shell Activity销毁，重置隐私锁验证状态");
							privacyLockVerified = false;
							privacyLockPageUsed = false; 
						}
					}
				});
				if (hasLanguageSelection && hasNetworkSource && hasUserAgreement && hasVersionSelection) {
					setBlockMenuBarHook(ctx, cl, true);
				} else {
					bvLog("[BetterVia] 用户未完成初始设置，暂不启用屏蔽菜单栏功能");
				}
				final String last = getSavedLanguage(ctx);
				if (!"auto".equals(last)) {
					updateViaLocale(ctx, last);
				}
			}
		});
	}
	private void showSettingsDialog(final Context ctx) {
		Activity activityRef = currentActivity;
		if (activityRef == null) {
			activityRef = Context;
		}
		if (activityRef == null || !(activityRef instanceof Activity)) {
			return;
		}
		final Activity act = activityRef;
		final AlertDialog[] dialogRef = new AlertDialog[1];
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed()) {
					return;
				}
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				title.setPadding(0, 0, 0, dp(act, 16));
				root.addView(title);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "module_settings_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(getHintColor(ctx));
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "whitelist_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "whitelist_hint"), KEY_WHITELIST, true,
						new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_WHITELIST, true);
								setWhitelistHook(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "block_google_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "block_google_hint"), KEY_BLOCK_GOOGLE_SERVICES,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false);
								setGoogleServicesInterceptHook(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "screenshot_protection_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "screenshot_protection_hint"),
						KEY_SCREENSHOT_PROTECTION, false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, false);
								setScreenshotProtection(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "block_startup_message_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "block_startup_message_hint"),
						KEY_BLOCK_STARTUP_MESSAGE, false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, false);
								blockStartupMessageEnabled = on;
								putPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "keep_screen_on_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "keep_screen_on_hint"), KEY_KEEP_SCREEN_ON,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false);
								setKeepScreenOn(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "hide_status_bar_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "hide_status_bar_hint"), KEY_HIDE_STATUS_BAR,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false);
								setHideStatusBar(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "download_dialog_share_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "download_dialog_share_hint"),
						KEY_DOWNLOAD_DIALOG_SHARE, false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false);
								setDownloadDialogShareHook(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "show_url_scheme_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "show_url_scheme_hint"), KEY_SHOW_URL_SCHEME,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, false);
								setUrlSchemeHook(ctx, act.getClassLoader(), on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "eye_protection_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "eye_protection_hint"), KEY_EYE_PROTECTION,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_EYE_PROTECTION, false);
								setEyeProtectionMode(ctx, act.getClassLoader(), on);
							}
						});
				addEyeProtectionConfig(root, act, ctx);
				addComponentBlockItem(root, act, ctx);
				addHomepageThemeItem(root, act, ctx);
				addScriptRepositoryItem(root, act, ctx);
				addAdBlockRulesItem(root, act, ctx);
				addSearchCommandsItem(root, act, ctx);
				addUserAgentItem(root, act, ctx);
				addCookieManagementItem(root, act, ctx);
				addImagePickerItem(root, act, ctx);
				addBlockMenuBarItem(root, act, ctx);
				addMonetMomentItem(root, act, ctx);
				addPrivacyLockItem(root, act, ctx);
				addStartupExecutionItem(root, act, ctx);
				View div = new View(act);
				LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						dp(act, 1));
				divLp.setMargins(0, dp(act, 12), 0, dp(act, 12));
				div.setLayoutParams(divLp);
				div.setBackgroundColor(getDividerColor(ctx));
				root.addView(div);
				LinearLayout versionRow = new LinearLayout(act);
				versionRow.setOrientation(LinearLayout.HORIZONTAL);
				versionRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView versionTitle = new TextView(act);
				versionTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_title"));
				versionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				versionTitle.setTextColor(getTextColor(ctx));
				versionRow.addView(versionTitle,
						new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				final ArrayList<Integer> supportedVersionCodesList = new ArrayList<Integer>();
				final ArrayList<String> supportedVersionNamesList = new ArrayList<String>();
				int[] initialVersionCodes = ViaVersionDetector.getSupportedVersionCodes();
				String[] initialVersionNames = ViaVersionDetector.getSupportedVersionNames();
				for (int i = 0; i < initialVersionCodes.length; i++) {
					supportedVersionCodesList.add(initialVersionCodes[i]);
					supportedVersionNamesList.add(initialVersionNames[i]);
				}
				int savedVersionCode = getPrefInt(ctx, KEY_SELECTED_VIA_VERSION, 20260211);
				int versionIdx = 0;
				for (int i = 0; i < supportedVersionCodesList.size(); i++)
					if (supportedVersionCodesList.get(i) == savedVersionCode) {
						versionIdx = i;
						break;
					}
				final TextView versionSelector = new TextView(act);
				versionSelector.setText(supportedVersionNamesList.get(versionIdx));
				versionSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				versionSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				versionSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				versionSelector.setTextColor(getBtnTextColor(ctx));
				versionRow.addView(versionSelector);
				root.addView(versionRow);
				TextView versionHintTv = new TextView(act);
				applyClickAnim(versionHintTv);
				versionHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_hint"));
				versionHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				versionHintTv.setTextColor(getHintColor(ctx));
				versionHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(versionHintTv);
				versionSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ListView list = new ListView(ctx);
						list.setDivider(null);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
								android.R.layout.simple_list_item_1, supportedVersionNamesList) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								TextView textView = (TextView) view.findViewById(android.R.id.text1);
								textView.setSingleLine(true);
								textView.setEllipsize(TextUtils.TruncateAt.END);
								textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
								textView.setTextColor(getTextColor(ctx));
								return view;
							}
						};
						list.setAdapter(adapter);
						int popupWidth = Math.max(versionSelector.getWidth(), dp(ctx, 200));
						final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
						pop.setOutsideTouchable(true);
						pop.setFocusable(true);
						pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								int selectedVersionCode = supportedVersionCodesList.get(position);
								putPrefInt(ctx, KEY_SELECTED_VIA_VERSION, selectedVersionCode);
								selectedViaVersionCode = selectedVersionCode;
								ViaClassMapping.setUserSelectedVersionCode(selectedVersionCode);
								versionSelector.setText(supportedVersionNamesList.get(position));
								ViaClassMapping.clearCache();
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "startup_restart_hint"));
								bvLog("[BetterVia] 用户在设置中选择的Via版本: " + supportedVersionNamesList.get(position) + " (code: "
										+ selectedVersionCode + ")");
								new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
									@Override
									public void run() {
										System.exit(0);
									}
								}, 1500);
								pop.dismiss();
							}
						});
						GradientDrawable popBg = getRoundBg(ctx, getBgColor(ctx), 12);
						popBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
						list.setBackground(popBg);
						list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
						pop.showAsDropDown(versionSelector, 0, dp(ctx, 4));
					}
				});
				LinearLayout langRow = new LinearLayout(act);
				langRow.setOrientation(LinearLayout.HORIZONTAL);
				langRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView langTitle = new TextView(act);
				langTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "language_title"));
				langTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				langTitle.setTextColor(getTextColor(ctx));
				langRow.addView(langTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
				final String[] langItems = new String[]{LocalizedStringProvider.getInstance().get(ctx, "language_auto"),
						"简体中文", "繁體中文", "English"};
				String savedLang = getSavedLanguage(ctx);
				int langIdx = 0;
				for (int i = 0; i < langValues.length; i++)
					if (langValues[i].equals(savedLang)) {
						langIdx = i;
						break;
					}
				final TextView langSelector = new TextView(act);
				langSelector.setText(langItems[langIdx]);
				langSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				langSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				langSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				langSelector.setTextColor(getBtnTextColor(ctx));
				langRow.addView(langSelector);
				root.addView(langRow);
				TextView langHintTv = new TextView(act);
				applyClickAnim(langHintTv);
				langHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "language_hint"));
				langHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				langHintTv.setTextColor(getHintColor(ctx));
				langHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(langHintTv);
				LinearLayout sourceRow = new LinearLayout(act);
				sourceRow.setOrientation(LinearLayout.HORIZONTAL);
				sourceRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView sourceTitle = new TextView(act);
				sourceTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "network_source_title"));
				sourceTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				sourceTitle.setTextColor(getTextColor(ctx));
				sourceRow.addView(sourceTitle,
						new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				final String[] sourceValues = {NETWORK_SOURCE_GITEE, NETWORK_SOURCE_GITHUB};
				final String[] sourceItems = new String[]{"Gitee", "GitHub"};
				String savedSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
				int sourceIdx = savedSource.equals(NETWORK_SOURCE_GITEE) ? 0 : 1;
				final TextView sourceSelector = new TextView(act);
				sourceSelector.setText(sourceItems[sourceIdx]);
				sourceSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				sourceSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				sourceSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				sourceSelector.setTextColor(getBtnTextColor(ctx));
				sourceRow.addView(sourceSelector);
				root.addView(sourceRow);
				TextView sourceHintTv = new TextView(act);
				applyClickAnim(sourceHintTv);
				sourceHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "network_source_hint"));
				sourceHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				sourceHintTv.setTextColor(getHintColor(ctx));
				sourceHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(sourceHintTv);
				sourceSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showSourcePopup(ctx, sourceSelector, sourceValues, sourceItems, new SourceSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								String selectedSource = sourceValues[pos];
								putPrefString(ctx, KEY_NETWORK_SOURCE, selectedSource);
								sourceSelector.setText(sourceItems[pos]);
								themesLoaded = false;
								loadedThemes.clear();
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "network_source_changed") + " "
												+ sourceItems[pos]);
							}
						});
					}
				});
				LinearLayout mappingSyncRow = new LinearLayout(act);
				mappingSyncRow.setOrientation(LinearLayout.HORIZONTAL);
				mappingSyncRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView mappingSyncTitle = new TextView(act);
				mappingSyncTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_config_title"));
				mappingSyncTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				mappingSyncTitle.setTextColor(getTextColor(ctx));
				mappingSyncRow.addView(mappingSyncTitle,
						new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				final TextView mappingSyncButton = new TextView(act);
				applyClickAnim(mappingSyncButton);
				mappingSyncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_button"));
				mappingSyncButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				mappingSyncButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				mappingSyncButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				mappingSyncButton.setTextColor(getBtnTextColor(ctx));
				mappingSyncRow.addView(mappingSyncButton);
				root.addView(mappingSyncRow);
				TextView mappingSyncHintTv = new TextView(act);
				mappingSyncHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_config_hint"));
				mappingSyncHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				mappingSyncHintTv.setTextColor(getHintColor(ctx));
				mappingSyncHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(mappingSyncHintTv);
				mappingSyncButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mappingSyncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_loading"));
						mappingSyncButton.setEnabled(false);
						MappingSyncHelper.syncMappingFromNetwork(ctx, new MappingSyncHelper.SyncCallback() {
							@Override
							public void onSuccess(int addedVersions) {
								mappingSyncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_button"));
								mappingSyncButton.setEnabled(true);
								jiguroMessageWithContext(ctx, String.format(
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_success"),
										addedVersions));
								supportedVersionCodesList.clear();
								supportedVersionNamesList.clear();
								int[] newSupportedVersionCodes = ViaVersionDetector.getSupportedVersionCodes();
								String[] newSupportedVersionNames = ViaVersionDetector.getSupportedVersionNames();
								for (int i = 0; i < newSupportedVersionCodes.length; i++) {
									supportedVersionCodesList.add(newSupportedVersionCodes[i]);
									supportedVersionNamesList.add(newSupportedVersionNames[i]);
								}
							}
							@Override
							public void onFailed(String errorMessage) {
								mappingSyncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_button"));
								mappingSyncButton.setEnabled(true);
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_failed"));
							}
							@Override
							public void onNoUpdate() {
								mappingSyncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_button"));
								mappingSyncButton.setEnabled(true);
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_no_update"));
							}
						});
					}
				});
				LinearLayout themeRow = new LinearLayout(act);
				themeRow.setOrientation(LinearLayout.HORIZONTAL);
				themeRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView themeTitle = new TextView(act);
				themeTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "module_theme_title"));
				themeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				themeTitle.setTextColor(getTextColor(ctx));
				themeRow.addView(themeTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				final String[] themeValues = {MODULE_THEME_AUTO, MODULE_THEME_LIGHT, MODULE_THEME_DARK};
				final String[] themeItems = new String[]{
						LocalizedStringProvider.getInstance().get(ctx, "module_theme_auto"),
						LocalizedStringProvider.getInstance().get(ctx, "module_theme_light"),
						LocalizedStringProvider.getInstance().get(ctx, "module_theme_dark")};
				String savedTheme = getModuleTheme(ctx);
				int themeIdx = 0;
				for (int i = 0; i < themeValues.length; i++) {
					if (themeValues[i].equals(savedTheme)) {
						themeIdx = i;
						break;
					}
				}
				final TextView themeSelector = new TextView(act);
				themeSelector.setText(themeItems[themeIdx]);
				themeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				themeSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				themeSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				themeSelector.setTextColor(getBtnTextColor(ctx));
				themeRow.addView(themeSelector);
				root.addView(themeRow);
				TextView themeHintTv = new TextView(act);
				applyClickAnim(themeHintTv);
				themeHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "module_theme_hint"));
				themeHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				themeHintTv.setTextColor(getHintColor(ctx));
				themeHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(themeHintTv);
				themeSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showSourcePopup(ctx, themeSelector, themeValues, themeItems, new SourceSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								String selectedTheme = themeValues[pos];
								putPrefString(ctx, KEY_MODULE_THEME, selectedTheme);
								themeSelector.setText(themeItems[pos]);
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "module_theme_changed") + " "
												+ themeItems[pos]);
								if (dialogRef[0] != null && dialogRef[0].isShowing()) {
									dialogRef[0].dismiss();
									showSettingsDialog(ctx);
								}
							}
						});
					}
				});
				LinearLayout aboutRow = new LinearLayout(act);
				aboutRow.setOrientation(LinearLayout.HORIZONTAL);
				aboutRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView aboutTitle = new TextView(act);
				aboutTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_title"));
				aboutTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				aboutTitle.setTextColor(getTextColor(ctx));
				aboutRow.addView(aboutTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				TextView aboutBtn = new TextView(act);
				applyClickAnim(aboutBtn);
				aboutBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "about_view"));
				aboutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				aboutBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				aboutBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				aboutBtn.setTextColor(getBtnTextColor(ctx));
				aboutBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showAboutDialog(ctx);
					}
				});
				aboutRow.addView(aboutBtn);
				root.addView(aboutRow);
				TextView aboutHintTv = new TextView(act);
				aboutHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "about_hint"));
				aboutHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				aboutHintTv.setTextColor(getHintColor(ctx));
				aboutHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(aboutHintTv);
				LinearLayout storageRow = new LinearLayout(act);
				storageRow.setOrientation(LinearLayout.HORIZONTAL);
				storageRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView storageTitle = new TextView(act);
				storageTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "storage_item_title"));
				storageTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				storageTitle.setTextColor(getTextColor(ctx));
				storageRow.addView(storageTitle,
						new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				TextView storageBtn = new TextView(act);
				applyClickAnim(storageBtn);
				storageBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "storage_manage"));
				storageBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				storageBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				storageBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				storageBtn.setTextColor(getBtnTextColor(ctx));
				storageBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showStorageManagerDialog(act); 
					}
				});
				storageRow.addView(storageBtn);
				root.addView(storageRow);
				TextView storageHintTv = new TextView(act);
				storageHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "storage_hint"));
				storageHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				storageHintTv.setTextColor(getHintColor(ctx));
				storageHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(storageHintTv);
				LinearLayout shisuiRow = new LinearLayout(act);
				shisuiRow.setOrientation(LinearLayout.HORIZONTAL);
				shisuiRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView shisuiTitle = new TextView(act);
				shisuiTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_title"));
				shisuiTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				shisuiTitle.setTextColor(getTextColor(ctx));
				shisuiRow.addView(shisuiTitle,
						new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
				TextView shisuiBtn = new TextView(act);
				applyClickAnim(shisuiBtn);
				shisuiBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_view"));
				shisuiBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				shisuiBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				shisuiBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				shisuiBtn.setTextColor(getBtnTextColor(ctx));
				shisuiBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showShisuiDialog(ctx);
					}
				});
				shisuiRow.addView(shisuiBtn);
				root.addView(shisuiRow);
				TextView shisuiHintTv = new TextView(act);
				shisuiHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_hint"));
				shisuiHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				shisuiHintTv.setTextColor(getHintColor(ctx));
				shisuiHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
				root.addView(shisuiHintTv);
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "auto_update_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "auto_update_hint"), KEY_AUTO_UPDATE, true,
						new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_AUTO_UPDATE, true);
								autoUpdateEnabled = on;
								putPrefBoolean(ctx, KEY_AUTO_UPDATE, on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "custom_toast_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "custom_toast_hint"), KEY_CUSTOM_TOAST, true,
						new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_CUSTOM_TOAST, true);
								putPrefBoolean(ctx, KEY_CUSTOM_TOAST, on);
							}
						});
				addSwitch(root, act, LocalizedStringProvider.getInstance().get(ctx, "developer_mode_switch"),
						LocalizedStringProvider.getInstance().get(ctx, "developer_mode_hint"), KEY_DEVELOPER_MODE,
						false, new Runnable() {
							@Override
							public void run() {
								boolean on = getPrefBoolean(ctx, KEY_DEVELOPER_MODE, false);
								developerModeEnabled = on;
								putPrefBoolean(ctx, KEY_DEVELOPER_MODE, on);
								if (on) {
									initLogFile();
								} else {
									closeLogFile();
								}
							}
						});
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(getOkBtnTextColor(ctx));
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				GradientDrawable btnBg = new GradientDrawable();
				btnBg.setColor(getOkBtnBgColor(ctx));
				btnBg.setCornerRadius(dp(act, 12));
				ok.setBackground(btnBg);
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setView(scrollRoot);
				dialogRef[0] = builder.create();
				Window win = dialogRef[0].getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogRef[0].dismiss();
					}
				});
				langSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showLangPopup(ctx, langSelector, langValues, langItems, new LangSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								saveLanguageSetting(ctx, langValues[pos]);
								showLanguageChangeToast(ctx, pos);
								refreshModuleButtonText(ctx);
								langSelector.setText(langItems[pos]);
							}
						});
					}
				});
				dialogRef[0].show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void addComponentBlockItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "component_block_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "component_block_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showComponentBlockDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "component_block_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showComponentBlockDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(bgColor);
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "component_block_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 16);
				root.addView(title, titleLp);
				final Map<String, CheckBox> checkboxes = new HashMap<>();
				String[] componentNames = getComponentNames(ctx);
				for (int i = 0; i < COMPONENT_KEYS.length; i++) {
					CheckBox cb = new CheckBox(act);
					cb.setText(componentNames[i]);
					cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					cb.setTextColor(textColor);
					cb.setChecked(getPrefBoolean(ctx, COMPONENT_KEYS[i], false));
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
								new int[]{-android.R.attr.state_checked}};
						int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
						ColorStateList colorStateList = new ColorStateList(states, colors);
						cb.setButtonTintList(colorStateList);
					}
					LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					cbLp.bottomMargin = dp(act, 8);
					root.addView(cb, cbLp);
					checkboxes.put(COMPONENT_KEYS[i], cb);
				}
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				GradientDrawable btnBg = new GradientDrawable();
				btnBg.setColor(okBtnBgColor);
				btnBg.setCornerRadius(dp(act, 12));
				ok.setBackground(btnBg);
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor);
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (Map.Entry<String, CheckBox> entry : checkboxes.entrySet()) {
							putPrefBoolean(ctx, entry.getKey(), entry.getValue().isChecked());
						}
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "component_block_saved"));
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void setComponentBlockHook(final Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (componentHook == null) {
				final String componentBlockClassName = ViaClassMapping
						.getClassName(ViaClassMapping.ClassMethodKey.COMPONENT_BLOCK_CLASS, ctx);
				componentHook = XposedHelpers.findAndHookMethod("java.util.ArrayList", null, "add", Object.class,
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
								if (!isCalledFromSpecificMethod(ctx))
									return;
								Object item = param.args[0];
								if (item == null)
									return;
								if (!componentBlockClassName.equals(item.getClass().getName()))
									return;
								int type = XposedHelpers.getIntField(item, "b");
								String[] componentNames = getComponentNames(ctx);
								int index = mapTypeToIndex(type);
								if (index < 0)
									return;
								boolean block = getPrefBoolean(ctx, COMPONENT_KEYS[index], false);
								if (block) {
									bvLog("[BetterVia] 组件屏蔽：阻止类型 " + type + " → " + componentNames[index]);
									param.setResult(false);
								}
							}
						});
				bvLog("[BetterVia] 组件屏蔽逻辑已启用");
			}
		} else {
			if (componentHook != null) {
				componentHook.unhook();
				componentHook = null;
				bvLog("[BetterVia] 组件屏蔽逻辑已停用");
			}
		}
	}
	private int mapTypeToIndex(int type) {
		switch (type) {
			case 12 :
				return 0;
			case 5 :
				return 1;
			case 6 :
				return 2;
			case 13 :
				return 3;
			case 14 :
				return 4;
			case 7 : 
				return 5;
			case 4 : 
				return 6;
			case 2 :
				return 7;
			case 3 :
				return 8;
			case 1 :
				return 9;
			case 16 :
				return 10;
			default :
				return -1;
		}
	}
	private boolean isCalledFromSpecificMethod(Context ctx) {
		String checkClassName = ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.COMPONENT_CHECK_METHOD,
				ctx);
		String checkMethodName = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.COMPONENT_CHECK_METHOD,
				ctx);
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement el : stack) {
			if (checkClassName.equals(el.getClassName()) && checkMethodName.equals(el.getMethodName())) {
				return true;
			}
		}
		return false;
	}
	private String[] getComponentNames(Context ctx) {
		return new String[]{LocalizedStringProvider.getInstance().get(ctx, "component_update"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_telegram"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_qq"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_email"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_wechat"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_donate"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_assist"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_agreement"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_privacy"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_opensource"), 
				LocalizedStringProvider.getInstance().get(ctx, "component_icp") 
		};
	}
	private interface LangSelectedCallback {
		void onSelected(int pos);
	}
	private void showLangPopup(final Context ctx, View anchor, final String[] values, String[] items,
			final LangSelectedCallback callback) {
		final ListView list = new ListView(ctx);
		list.setDivider(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setSingleLine(true);
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
				textView.setTextColor(getTextColor(ctx));
				return view;
			}
		};
		list.setAdapter(adapter);
		DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int maxWidth = (int) (screenWidth * 0.8);
		int anchorWidth = anchor.getWidth();
		int popupWidth = Math.max(anchorWidth, dp(ctx, 200));
		popupWidth = Math.min(popupWidth, maxWidth);
		final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				callback.onSelected(position);
				pop.dismiss();
			}
		});
		GradientDrawable bg = getRoundBg(ctx, getBgColor(ctx), 12);
		bg.setStroke(dp(ctx, 1), getDividerColor(ctx));
		list.setBackground(bg);
		list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
		pop.showAsDropDown(anchor, 0, dp(ctx, 4));
	}
	private interface SourceSelectedCallback {
		void onSelected(int pos);
	}
	private void showSourcePopup(final Context ctx, View anchor, final String[] values, String[] items,
			final SourceSelectedCallback callback) {
		final ListView list = new ListView(ctx);
		list.setDivider(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setSingleLine(true);
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
				textView.setTextColor(getTextColor(ctx));
				return view;
			}
		};
		list.setAdapter(adapter);
		int popupWidth = Math.max(anchor.getWidth(), dp(ctx, 200));
		final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				callback.onSelected(position);
				pop.dismiss();
			}
		});
		GradientDrawable bg = getRoundBg(ctx, getBgColor(ctx), 12);
		bg.setStroke(dp(ctx, 1), getDividerColor(ctx));
		list.setBackground(bg);
		list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
		pop.showAsDropDown(anchor, 0, dp(ctx, 4));
	}
	private void addSwitch(LinearLayout parent, final Context ctx, String title, String hint, final String prefKey,
			boolean defVal, final Runnable onChange) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(title);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		Switch sw = new Switch(ctx);
		sw.setChecked(getPrefBoolean(ctx, prefKey, defVal));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			sw.getThumbDrawable().setTintList(new ColorStateList(
					new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
					new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)}));
			sw.getTrackDrawable().setTintList(new ColorStateList(
					new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
					new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)}));
		}
		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				putPrefBoolean(ctx, prefKey, isChecked);
				if (onChange != null)
					onChange.run();
			}
		});
		hor.addView(sw);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(hint);
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void setWhitelistHook(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (whitelistHook == null) {
				String viaCheckClass = ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS,
						ctx);
				String viaCheckMethod = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS,
						ctx);
				String viaCheckParamClass = ViaClassMapping
						.getParameterClassName(ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS, ctx);
				whitelistHook = XposedHelpers.findAndHookMethod(viaCheckClass, cl, viaCheckMethod, viaCheckParamClass,
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
								param.setResult(null);
								bvLog("[BetterVia] 成功Hook白名单方法");
							}
						});
				bvLog("[BetterVia] 已解除Via白名单限制");
			}
		} else {
			if (whitelistHook != null) {
				whitelistHook.unhook();
				whitelistHook = null;
				bvLog("[BetterVia] Via白名单限制已恢复");
			}
		}
		whitelistHookEnabled = on;
		putPrefBoolean(ctx, KEY_WHITELIST, on);
	}
	private void setEyeProtectionMode(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (activityHook == null) {
				activityHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								if (!eyeProtectionEnabled)
									return;
								final Activity activity = (Activity) param.thisObject;
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										addEyeProtectionOverlay(activity, getPrefInt(activity, KEY_EYE_TEMPERATURE, 50),
												getPrefInt(activity, KEY_EYE_TEXTURE, 0));
									}
								});
							}
						});
				bvLog("[BetterVia] 护眼模式已启用");
			}
		} else {
			if (activityHook != null) {
				activityHook.unhook();
				activityHook = null;
				bvLog("[BetterVia] 护眼模式已停用");
			}
			removeAllEyeProtectionOverlays();
		}
		eyeProtectionEnabled = on;
		putPrefBoolean(ctx, KEY_EYE_PROTECTION, on);
	}
	private void addEyeProtectionOverlay(Activity activity, final int temperature, final int texture) {
		try {
			ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
			View existingOverlay = overlayViews.get(activity);
			if (existingOverlay != null) {
				rootView.removeView(existingOverlay);
			}
			View overlay = new View(activity) {
				@Override
				protected void onDraw(Canvas canvas) {
					super.onDraw(canvas);
					int color = calculateTemperatureColor(temperature);
					canvas.drawColor(color);
					if (texture > 0) {
						drawPaperTexture(canvas, texture);
					}
				}
			};
			overlay.setTag("eye_protection_overlay");
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			overlay.setClickable(false);
			overlay.setFocusable(false);
			overlay.setFocusableInTouchMode(false);
			rootView.addView(overlay, params);
			overlayViews.put(activity, overlay);
			bvLog("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 添加护眼遮罩");
		} catch (Exception e) {
			bvLog("[BetterVia] 添加护眼遮罩失败: " + e);
		}
	}
	private int calculateTemperatureColor(int temperature) {
		float ratio = temperature / 100.0f;
		int alpha = (int) (0x40 * ratio);
		int r = (int) (255 * ratio);
		int g = (int) (245 * ratio);
		int b = (int) (200 * ratio);
		return (alpha << 24) | (r << 16) | (g << 8) | b;
	}
	private void drawPaperTexture(Canvas canvas, int textureLevel) {
		Paint paint = new Paint();
		paint.setColor(0x20FFFFFF);
		Random random = new Random(12345);
		int density = textureLevel / 5;
		for (int i = 0; i < density; i++) {
			float x = random.nextFloat() * canvas.getWidth();
			float y = random.nextFloat() * canvas.getHeight();
			float radius = random.nextFloat() * 2 + 1;
			canvas.drawCircle(x, y, radius, paint);
		}
		if (textureLevel > 50) {
			paint.setColor(0x10FFFFFF);
			for (int i = 0; i < textureLevel / 10; i++) {
				float x = random.nextFloat() * canvas.getWidth();
				float y = random.nextFloat() * canvas.getHeight();
				float radius = random.nextFloat() * 3 + 2;
				canvas.drawCircle(x, y, radius, paint);
			}
		}
	}
	private void updateEyeProtectionOverlay(Activity activity, int temperature, int texture) {
		View overlay = overlayViews.get(activity);
		if (overlay != null) {
			overlay.invalidate();
		} else if (eyeProtectionEnabled) {
			addEyeProtectionOverlay(activity, temperature, texture);
		}
	}
	private void updateAllEyeProtectionOverlays(int temperature, int texture) {
		for (Map.Entry<Activity, View> entry : overlayViews.entrySet()) {
			Activity activity = entry.getKey();
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				updateEyeProtectionOverlay(activity, temperature, texture);
			}
		}
	}
	private void removeAllEyeProtectionOverlays() {
		for (Map.Entry<Activity, View> entry : overlayViews.entrySet()) {
			Activity activity = entry.getKey();
			View overlay = entry.getValue();
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
				rootView.removeView(overlay);
			}
		}
		overlayViews.clear();
		bvLog("[BetterVia] 已移除所有护眼遮罩");
	}
	private void addEyeProtectionConfig(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config_btn"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showEyeProtectionConfigDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showEyeProtectionConfigDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int savedTemperature = getPrefInt(ctx, KEY_EYE_TEMPERATURE, 50);
				final int savedTexture = getPrefInt(ctx, KEY_EYE_TEXTURE, 0);
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(bgColor);
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				subtitleLp.bottomMargin = dp(act, 16);
				root.addView(subtitle, subtitleLp);
				final SeekBar[] tempSeekBarRef = new SeekBar[1];
				final SeekBar[] textureSeekBarRef = new SeekBar[1];
				final View[] previewOverlayRef = new View[1];
				LinearLayout previewContainer = new LinearLayout(act);
				previewContainer.setOrientation(LinearLayout.VERTICAL);
				previewContainer.setPadding(0, 0, 0, dp(act, 16));
				previewContainer.setBackgroundColor(bgColor);
				TextView previewTitle = new TextView(act);
				previewTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_preview_title"));
				previewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				previewTitle.setTextColor(textColor);
				previewTitle.setTypeface(null, Typeface.BOLD);
				previewContainer.addView(previewTitle);
				FrameLayout previewContent = new FrameLayout(act);
				previewContent.setLayoutParams(
						new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 80)));
				previewContent.setBackgroundColor(Color.WHITE);
				previewContent.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				GradientDrawable previewBg = new GradientDrawable();
				previewBg.setColor(getBgColor(ctx));
				previewBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
				previewBg.setCornerRadius(dp(act, 8));
				previewContent.setBackground(previewBg);
				TextView sampleText = new TextView(act);
				sampleText.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_sample_text"));
				sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				sampleText.setTextColor(getTextColor(ctx));
				FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				textLp.gravity = Gravity.CENTER;
				previewContent.addView(sampleText, textLp);
				final View previewOverlay = new View(act) {
					@Override
					protected void onDraw(Canvas canvas) {
						super.onDraw(canvas);
						if (tempSeekBarRef[0] != null) {
							int color = calculateTemperatureColor(tempSeekBarRef[0].getProgress());
							canvas.drawColor(color);
						}
						if (textureSeekBarRef[0] != null && textureSeekBarRef[0].getProgress() > 0) {
							drawPaperTexturePreview(canvas, textureSeekBarRef[0].getProgress(), getWidth(),
									getHeight());
						}
					}
				};
				previewOverlay.setClickable(false);
				previewOverlay.setFocusable(false);
				FrameLayout.LayoutParams overlayLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				previewContent.addView(previewOverlay, overlayLp);
				previewOverlayRef[0] = previewOverlay;
				previewContainer.addView(previewContent);
				root.addView(previewContainer);
				LinearLayout tempContainer = new LinearLayout(act);
				tempContainer.setOrientation(LinearLayout.VERTICAL);
				tempContainer.setPadding(0, 0, 0, dp(act, 16));
				TextView tempTitle = new TextView(act);
				tempTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_temperature"));
				tempTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				tempTitle.setTextColor(textColor);
				tempTitle.setTypeface(null, Typeface.BOLD);
				tempContainer.addView(tempTitle);
				final SeekBar tempSeekBar = new SeekBar(act);
				tempSeekBar.setMax(100);
				tempSeekBar.setProgress(savedTemperature);
				tempSeekBarRef[0] = tempSeekBar; 
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					tempSeekBar.setProgressTintList(ColorStateList.valueOf(okBtnBgColor));
					tempSeekBar.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
				}
				tempContainer.addView(tempSeekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				LinearLayout tempLabels = new LinearLayout(act);
				tempLabels.setOrientation(LinearLayout.HORIZONTAL);
				TextView coldLabel = new TextView(act);
				coldLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_cold"));
				coldLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				coldLabel.setTextColor(hintColor);
				LinearLayout.LayoutParams coldLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				tempLabels.addView(coldLabel, coldLp);
				TextView warmLabel = new TextView(act);
				warmLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_warm"));
				warmLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				warmLabel.setTextColor(hintColor);
				warmLabel.setGravity(Gravity.END);
				LinearLayout.LayoutParams warmLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				tempLabels.addView(warmLabel, warmLp);
				tempContainer.addView(tempLabels, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				root.addView(tempContainer);
				LinearLayout textureContainer = new LinearLayout(act);
				textureContainer.setOrientation(LinearLayout.VERTICAL);
				textureContainer.setPadding(0, 0, 0, dp(act, 16));
				TextView textureTitle = new TextView(act);
				textureTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_texture"));
				textureTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				textureTitle.setTextColor(textColor);
				textureTitle.setTypeface(null, Typeface.BOLD);
				textureContainer.addView(textureTitle);
				final SeekBar textureSeekBar = new SeekBar(act);
				textureSeekBar.setMax(100);
				textureSeekBar.setProgress(savedTexture);
				textureSeekBarRef[0] = textureSeekBar; 
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					textureSeekBar.setProgressTintList(ColorStateList.valueOf(okBtnBgColor));
					textureSeekBar.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
				}
				textureContainer.addView(textureSeekBar, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				LinearLayout textureLabels = new LinearLayout(act);
				textureLabels.setOrientation(LinearLayout.HORIZONTAL);
				TextView smoothLabel = new TextView(act);
				smoothLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_smooth"));
				smoothLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				smoothLabel.setTextColor(hintColor);
				LinearLayout.LayoutParams smoothLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1);
				textureLabels.addView(smoothLabel, smoothLp);
				TextView roughLabel = new TextView(act);
				roughLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_rough"));
				roughLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				roughLabel.setTextColor(hintColor);
				roughLabel.setGravity(Gravity.END);
				LinearLayout.LayoutParams roughLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1);
				textureLabels.addView(roughLabel, roughLp);
				textureContainer.addView(textureLabels, new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				root.addView(textureContainer);
				TextView previewHint = new TextView(act);
				previewHint.setText(LocalizedStringProvider.getInstance().get(ctx, "eye_protection_preview_hint"));
				previewHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				previewHint.setTextColor(hintColor);
				previewHint.setGravity(Gravity.CENTER);
				previewHint.setTypeface(null, Typeface.ITALIC);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.topMargin = dp(act, 8);
				hintLp.bottomMargin = dp(act, 16);
				root.addView(previewHint, hintLp);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				GradientDrawable btnBg = new GradientDrawable();
				btnBg.setColor(okBtnBgColor);
				btnBg.setCornerRadius(dp(act, 12));
				ok.setBackground(btnBg);
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 8);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor);
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				SeekBar.OnSeekBarChangeListener previewListener = new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (fromUser && previewOverlayRef[0] != null) {
							previewOverlayRef[0].invalidate();
						}
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				};
				tempSeekBar.setOnSeekBarChangeListener(previewListener);
				textureSeekBar.setOnSeekBarChangeListener(previewListener);
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int newTemperature = tempSeekBar.getProgress();
						int newTexture = textureSeekBar.getProgress();
						putPrefInt(ctx, KEY_EYE_TEMPERATURE, newTemperature);
						putPrefInt(ctx, KEY_EYE_TEXTURE, newTexture);
						updateAllEyeProtectionOverlays(newTemperature, newTexture);
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "eye_protection_config_saved"));
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void drawPaperTexturePreview(Canvas canvas, int textureLevel, int width, int height) {
		Paint paint = new Paint();
		paint.setColor(0x20FFFFFF);
		Random random = new Random(12345);
		int density = textureLevel / 3;
		int pointCount = (((width * height) / 1000) * density) / 10;
		for (int i = 0; i < pointCount; i++) {
			float x = random.nextFloat() * width;
			float y = random.nextFloat() * height;
			float radius = random.nextFloat() * 1.5f + 0.5f;
			canvas.drawCircle(x, y, radius, paint);
		}
		if (textureLevel > 50) {
			paint.setColor(0x10FFFFFF);
			for (int i = 0; i < pointCount / 2; i++) {
				float x = random.nextFloat() * width;
				float y = random.nextFloat() * height;
				float radius = random.nextFloat() * 2 + 1;
				canvas.drawCircle(x, y, radius, paint);
			}
		}
	}
	private void setGoogleServicesInterceptHook(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			bvLog("[BetterVia] 已启用Google个人信息收集拦截");
			try {
				setFirebaseAnalyticsHook(ctx, cl, true);
			} catch (Exception e) {
				bvLog("[BetterVia] Firebase Analytics拦截启用失败: " + e);
			}
			try {
				setAppMeasurementHook(ctx, cl, true);
			} catch (Exception e) {
				bvLog("[BetterVia] AppMeasurement拦截启用失败: " + e);
			}
			bvLog("[BetterVia] Google个人信息收集拦截完成");
		} else {
			bvLog("[BetterVia] 已停用Google个人信息收集拦截");
			try {
				setFirebaseAnalyticsHook(ctx, cl, false);
			} catch (Exception e) {
				bvLog("[BetterVia] Firebase Analytics拦截停用失败: " + e);
			}
			try {
				setAppMeasurementHook(ctx, cl, false);
			} catch (Exception e) {
				bvLog("[BetterVia] AppMeasurement拦截停用失败: " + e);
			}
			bvLog("[BetterVia] Google个人信息收集拦截停用完成");
		}
		blockGoogleServicesEnabled = on;
		putPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, on);
	}
	private void setFirebaseAnalyticsHook(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (firebaseAnalyticsHook == null) {
				try {
					String firebaseAnalyticsClass = ViaClassMapping
							.getClassName(ViaClassMapping.ClassMethodKey.FIREBASE_ANALYTICS, ctx);
					String firebaseAnalyticsMethod = ViaClassMapping
							.getMethodName(ViaClassMapping.ClassMethodKey.FIREBASE_ANALYTICS, ctx);
					Class<?> analyticsClass = XposedHelpers.findClassIfExists(firebaseAnalyticsClass, cl);
					if (analyticsClass != null) {
						firebaseAnalyticsHook = XposedHelpers.findAndHookMethod(analyticsClass, firebaseAnalyticsMethod,
								String.class, Bundle.class, new XC_MethodHook() {
									@Override
									protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
										bvLog("[BetterVia] 拦截Firebase Analytics事件: " + param.args[0]);
									}
								});
						bvLog("[BetterVia] Firebase Analytics精确拦截已启用");
					}
				} catch (Exception e) {
					bvLog("[BetterVia] Firebase Analytics精确拦截设置失败: " + e);
				}
			}
		} else {
			if (firebaseAnalyticsHook != null) {
				firebaseAnalyticsHook.unhook();
				firebaseAnalyticsHook = null;
				bvLog("[BetterVia] Firebase Analytics拦截已停用");
			}
		}
	}
	private void setAppMeasurementHook(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (googleAnalyticsHook == null) {
				try {
					Class<?> appMeasurementClass = XposedHelpers
							.findClassIfExists("com.google.android.gms.measurement.AppMeasurement", cl);
					if (appMeasurementClass != null) {
						googleAnalyticsHook = XposedHelpers.findAndHookMethod(appMeasurementClass, "logEventInternal",
								String.class, String.class, Bundle.class, new XC_MethodHook() {
									@Override
									protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
										bvLog("[BetterVia] 拦截AppMeasurement事件: " + param.args[0] + ", "
												+ param.args[1]);
									}
								});
						bvLog("[BetterVia] AppMeasurement精确拦截已启用");
					}
				} catch (Exception e) {
					bvLog("[BetterVia] AppMeasurement精确拦截设置失败: " + e);
				}
			}
		} else {
			if (googleAnalyticsHook != null) {
				googleAnalyticsHook.unhook();
				googleAnalyticsHook = null;
				bvLog("[BetterVia] AppMeasurement拦截已停用");
			}
		}
	}
	private void addSearchCommandsItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "search_commands_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "search_commands_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchCommandsDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "search_commands_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showSearchCommandsDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int itemBgColor = getItemBgColor(ctx);
				final int dividerColor = getDividerColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(0, 0, 0, 0);
				scrollRoot.setClipToPadding(false);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "search_commands_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "search_commands_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				final String[][] commands = {{"javascript:via.cmd(257);", "command_bookmark"},
						{"javascript:via.cmd(514);", "command_search"}, {"javascript:via.cmd(515);", "command_unknown"},
						{"javascript:via.cmd(516);", "command_print"}, {"javascript:via.cmd(517);", "command_adblock"},
						{"v:
						{"v:
						{"v:
						{"v:
						{"v:
						{"v:
						{"history:
				LinearLayout commandsContainer = new LinearLayout(act);
				commandsContainer.setOrientation(LinearLayout.VERTICAL);
				for (int i = 0; i < commands.length; i++) {
					final String[] command = commands[i];
					LinearLayout commandContainer = new LinearLayout(act);
					commandContainer.setOrientation(LinearLayout.HORIZONTAL);
					commandContainer.setGravity(Gravity.CENTER_VERTICAL);
					commandContainer.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					GradientDrawable commandBg = new GradientDrawable();
					commandBg.setColor(itemBgColor);
					commandBg.setStroke(dp(act, 1), dividerColor);
					commandBg.setCornerRadius(dp(act, 12));
					commandContainer.setBackground(commandBg);
					LinearLayout leftContent = new LinearLayout(act);
					leftContent.setOrientation(LinearLayout.VERTICAL);
					LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0,
							ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					leftContent.setLayoutParams(leftParams);
					TextView commandText = new TextView(act);
					commandText.setText(command[0]);
					commandText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					commandText.setTextColor(textColor);
					commandText.setTypeface(Typeface.MONOSPACE);
					commandText.setSingleLine(true);
					commandText.setEllipsize(TextUtils.TruncateAt.MIDDLE);
					commandText.setPadding(0, 0, dp(act, 8), 0);
					leftContent.addView(commandText);
					TextView descText = new TextView(act);
					descText.setText(LocalizedStringProvider.getInstance().get(ctx, command[1]));
					descText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					descText.setTextColor(hintColor);
					descText.setPadding(0, dp(act, 4), 0, 0);
					leftContent.addView(descText);
					commandContainer.addView(leftContent);
					Button copyBtn = new Button(act);
					applyClickAnim(copyBtn);
					copyBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "command_copy"));
					copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
					copyBtn.setTextColor(okBtnTextColor);
					copyBtn.setPadding(dp(act, 10), dp(act, 4), dp(act, 10), dp(act, 4));
					copyBtn.setMinHeight(dp(act, 28));
					copyBtn.setMinWidth(dp(act, 52));
					GradientDrawable btnBg = new GradientDrawable();
					btnBg.setColor(okBtnBgColor);
					btnBg.setCornerRadius(dp(act, 6));
					copyBtn.setBackground(btnBg);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						copyBtn.setStateListAnimator(null);
					}
					LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					btnLp.gravity = Gravity.CENTER_VERTICAL;
					commandContainer.addView(copyBtn, btnLp);
					LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					itemLp.bottomMargin = dp(act, 8);
					commandsContainer.addView(commandContainer, itemLp);
					final int index = i;
					copyBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							copyToClipboard(act, commands[index][0]);
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(ctx, "command_copied"));
						}
					});
				}
				root.addView(commandsContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					int height = (int) (metrics.heightPixels * 0.8);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
						window.setClipToOutline(true);
					}
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void addHomepageThemeItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showHomepageThemeDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showHomepageThemeDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(0, 0, 0, 0);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				final LinearLayout themesContainer = new LinearLayout(act);
				themesContainer.setOrientation(LinearLayout.VERTICAL);
				themesContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				final LinearLayout emptyStateContainer = new LinearLayout(act);
				emptyStateContainer.setOrientation(LinearLayout.VERTICAL);
				emptyStateContainer.setGravity(Gravity.CENTER);
				emptyStateContainer.setPadding(0, dp(act, 48), 0, dp(act, 48));
				emptyStateContainer.setVisibility(View.GONE);
				final ImageView errorIcon = new ImageView(act);
				errorIcon.setImageResource(android.R.drawable.ic_menu_report_image);
				errorIcon.setColorFilter(hintColor);
				errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
				iconLp.gravity = Gravity.CENTER;
				iconLp.bottomMargin = dp(act, 16);
				emptyStateContainer.addView(errorIcon, iconLp);
				final TextView emptyStateText = new TextView(act);
				emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				emptyStateText.setTextColor(hintColor);
				emptyStateText.setGravity(Gravity.CENTER);
				emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
				emptyStateContainer.addView(emptyStateText);
				root.addView(themesContainer);
				root.addView(emptyStateContainer);
				LinearLayout buttonContainer = new LinearLayout(act);
				buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
				buttonContainer.setGravity(Gravity.CENTER);
				buttonContainer.setPadding(0, dp(act, 16), 0, dp(act, 8));
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1.0f);
				okLp.rightMargin = dp(act, 8);
				buttonContainer.addView(ok, okLp);
				Button edit = new Button(act);
				applyClickAnim(edit);
				edit.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_edit"));
				edit.setTextColor(okBtnTextColor);
				edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				edit.setTypeface(null, Typeface.BOLD);
				edit.setPadding(0, dp(act, 14), 0, dp(act, 14));
				edit.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1.0f);
				editLp.leftMargin = dp(act, 8);
				buttonContainer.addView(edit, editLp);
				root.addView(buttonContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
						window.setClipToOutline(true);
					}
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				edit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						showThemeEditorDialog(ctx);
					}
				});
				showLoadingState(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
				if (!themesLoaded && !themesLoading) {
					loadThemesFromNetwork(ctx, new ThemesLoadCallback() {
						@Override
						public void onThemesLoaded(List<ThemeInfo> themes) {
							loadedThemes = themes;
							themesLoaded = true;
							themesLoading = false;
							if (act != null && !act.isFinishing()) {
								act.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										refreshThemesList(act, ctx, themesContainer, emptyStateContainer,
												emptyStateText);
									}
								});
							}
						}
						@Override
						public void onLoadFailed(final String error) {
							themesLoading = false;
							themesLoaded = true;
							loadedThemes = new ArrayList<>();
							if (act != null && !act.isFinishing()) {
								act.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										showErrorState(act, ctx, themesContainer, emptyStateContainer, emptyStateText,
												error);
									}
								});
							}
						}
					});
				} else {
					refreshThemesList(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
				}
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void showLoadingState(Activity act, Context ctx, LinearLayout themesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText) {
		themesContainer.removeAllViews();
		themesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "themes_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
		}
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showErrorState(Activity act, Context ctx, LinearLayout themesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, String error) {
		themesContainer.removeAllViews();
		themesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "themes_load_failed") + "\n"
				+ LocalizedStringProvider.getInstance().get(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.VISIBLE);
			} else if (child instanceof ProgressBar) {
				emptyStateContainer.removeView(child);
			}
		}
		jiguroMessageWithContext(ctx,
				LocalizedStringProvider.getInstance().get(ctx, "themes_load_failed") + ": " + error);
	}
	private LinearLayout createThemeCard(final Activity act, final Context ctx, final ThemeInfo theme) {
		LinearLayout themeCard = new LinearLayout(act);
		themeCard.setOrientation(LinearLayout.VERTICAL);
		themeCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(getItemBgColor(ctx)); 
		cardBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
		cardBg.setCornerRadius(dp(act, 12));
		themeCard.setBackground(cardBg);
		FrameLayout imageContainer = new FrameLayout(act);
		imageContainer
				.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 150)));
		imageContainer.setBackground(getRoundBg(act, getEditBgColor(ctx), 8)); 
		final ProgressBar loadingSpinner = new ProgressBar(act);
		loadingSpinner.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			loadingSpinner.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
		}
		FrameLayout.LayoutParams spinnerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		spinnerParams.gravity = Gravity.CENTER;
		imageContainer.addView(loadingSpinner, spinnerParams);
		final ImageView previewImage = new ImageView(act);
		previewImage.setLayoutParams(
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		previewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		previewImage.setVisibility(View.GONE);
		imageContainer.addView(previewImage);
		themeCard.addView(imageContainer);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(theme.previewUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.setConnectTimeout(10000);
					connection.setReadTimeout(10000);
					connection.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream input = connection.getInputStream();
						final Bitmap bitmap = BitmapFactory.decodeStream(input);
						input.close();
						if (bitmap != null) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									previewImage.setImageBitmap(bitmap);
									previewImage.setVisibility(View.VISIBLE);
									loadingSpinner.setVisibility(View.GONE);
								}
							});
						}
					}
					connection.disconnect();
				} catch (Exception e) {
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							int iconSize = dp(act, 48);
							Bitmap errorBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
							Canvas canvas = new Canvas(errorBitmap);
							Paint backgroundPaint = new Paint();
							backgroundPaint.setColor(getEditBgColor(ctx)); 
							canvas.drawCircle(iconSize / 2, iconSize / 2, iconSize / 2, backgroundPaint);
							Drawable errorIcon = act.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
							errorIcon.setBounds(iconSize / 4, iconSize / 4, (iconSize * 3) / 4, (iconSize * 3) / 4);
							errorIcon.draw(canvas);
							previewImage.setImageBitmap(errorBitmap);
							previewImage.setScaleType(ImageView.ScaleType.CENTER);
							previewImage.setColorFilter(getHintColor(ctx)); 
							previewImage.setVisibility(View.VISIBLE);
							loadingSpinner.setVisibility(View.GONE);
						}
					});
				}
			}
		}).start();
		LinearLayout infoLayout = new LinearLayout(act);
		infoLayout.setOrientation(LinearLayout.VERTICAL);
		infoLayout.setPadding(0, dp(act, 8), 0, 0);
		TextView themeName = new TextView(act);
		themeName.setText(theme.getName(ctx));
		themeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		themeName.setTextColor(getTextColor(ctx)); 
		themeName.setTypeface(null, Typeface.BOLD);
		infoLayout.addView(themeName);
		TextView themeAuthor = new TextView(act);
		themeAuthor.setText(
				LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_by") + " " + theme.getAuthor(ctx));
		themeAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		themeAuthor.setTextColor(getHintColor(ctx)); 
		infoLayout.addView(themeAuthor);
		themeCard.addView(infoLayout);
		themeCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showThemeApplyDialog(ctx, theme);
			}
		});
		return themeCard;
	}
	private interface ThemesLoadCallback {
		void onThemesLoaded(List<ThemeInfo> themes);
		void onLoadFailed(String error);
	}
	private void loadThemesFromNetwork(final Context ctx, final ThemesLoadCallback callback) {
		themesLoading = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String jsonUrl = networkSource.equals(NETWORK_SOURCE_GITEE)
							? GITEE_THEMES_JSON_URL
							: GITHUB_THEMES_JSON_URL;
					URL url = new URL(jsonUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(15000);
					connection.setReadTimeout(15000);
					connection.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						reader.close();
						JSONObject json = new JSONObject(response.toString());
						JSONArray themesArray = json.getJSONArray("themes");
						List<ThemeInfo> themes = new ArrayList<>();
						for (int i = 0; i < themesArray.length(); i++) {
							JSONObject themeJson = themesArray.getJSONObject(i);
							ThemeInfo theme = ThemeInfo.fromJSON(themeJson);
							themes.add(theme);
						}
						callback.onThemesLoaded(themes);
					} else {
						callback.onLoadFailed("HTTP " + connection.getResponseCode());
					}
					connection.disconnect();
				} catch (Exception e) {
					callback.onLoadFailed(e.getMessage());
				}
			}
		}).start();
	}
	private void refreshThemesList(final Activity act, final Context ctx, LinearLayout themesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText) {
		themesContainer.removeAllViews();
		if (themesLoading) {
			showLoadingState(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
			return;
		}
		if (loadedThemes == null || loadedThemes.isEmpty()) {
			showErrorState(act, ctx, themesContainer, emptyStateContainer, emptyStateText, "No themes available");
			return;
		}
		themesContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		for (final ThemeInfo theme : loadedThemes) {
			LinearLayout themeCard = createThemeCard(act, ctx, theme);
			LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			cardLp.bottomMargin = dp(ctx, 12);
			themesContainer.addView(themeCard, cardLp);
		}
	}
	private void showThemeEditorDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
				dialog.setCancelable(true);
				LinearLayout rootLayout = new LinearLayout(act);
				rootLayout.setOrientation(LinearLayout.VERTICAL);
				rootLayout.setBackgroundColor(getBgColor(ctx));
				RelativeLayout titleBar = new RelativeLayout(act);
				titleBar.setBackgroundColor(getItemBgColor(ctx));
				titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
				titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				ImageButton backButton = new ImageButton(act);
				backButton.setImageResource(android.R.drawable.ic_menu_revert);
				backButton.setBackgroundResource(android.R.color.transparent);
				backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				backButton.setColorFilter(getTextColor(ctx));
				RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
				backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(backButton, backButtonLp);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_editor_title"));
				title.setTextColor(getTextColor(ctx));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				title.setTypeface(null, Typeface.BOLD);
				RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.addRule(RelativeLayout.CENTER_IN_PARENT);
				titleBar.addView(title, titleLp);
				rootLayout.addView(titleBar);
				LinearLayout contentLayout = new LinearLayout(act);
				contentLayout.setOrientation(LinearLayout.VERTICAL);
				contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				contentLayout.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				TextView fileLabel = new TextView(act);
				fileLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "theme_editor_select_file"));
				fileLabel.setTextColor(getTextColor(ctx));
				fileLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				fileLabel.setPadding(0, 0, 0, dp(act, 8));
				contentLayout.addView(fileLabel);
				LinearLayout fileButtonGroup = new LinearLayout(act);
				fileButtonGroup.setOrientation(LinearLayout.HORIZONTAL);
				fileButtonGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				final Button htmlButton = new Button(act);
				applyClickAnim(htmlButton);
				htmlButton.setText("homepage2.html");
				htmlButton.setTextColor(getOkBtnTextColor(ctx));
				htmlButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				htmlButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
				htmlButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				LinearLayout.LayoutParams htmlLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1.0f);
				htmlLp.rightMargin = dp(act, 8);
				fileButtonGroup.addView(htmlButton, htmlLp);
				final Button cssButton = new Button(act);
				applyClickAnim(cssButton);
				cssButton.setText("homepage.css");
				cssButton.setTextColor(getHintColor(ctx));
				cssButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				cssButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
				cssButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				LinearLayout.LayoutParams cssLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1.0f);
				fileButtonGroup.addView(cssButton, cssLp);
				contentLayout.addView(fileButtonGroup);
				TextView editorLabel = new TextView(act);
				editorLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "theme_editor_edit_content"));
				editorLabel.setTextColor(getTextColor(ctx));
				editorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				editorLabel.setPadding(0, dp(act, 16), 0, dp(act, 8));
				contentLayout.addView(editorLabel);
				final ScrollView editorScroll = new ScrollView(act);
				editorScroll
						.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
				final EditText codeEditor = new EditText(act);
				codeEditor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				codeEditor.setTypeface(Typeface.MONOSPACE);
				codeEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				codeEditor.setTextColor(getTextColor(ctx));
				codeEditor.setBackground(getRoundBg(act, getEditBgColor(ctx), 8));
				codeEditor.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				codeEditor.setSingleLine(false);
				codeEditor.setGravity(Gravity.TOP);
				codeEditor.setMinLines(20);
				editorScroll.addView(codeEditor);
				contentLayout.addView(editorScroll);
				LinearLayout buttonBar = new LinearLayout(act);
				buttonBar.setOrientation(LinearLayout.HORIZONTAL);
				buttonBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				buttonBar.setPadding(0, dp(act, 16), 0, 0);
				Button cancelButton = new Button(act);
				applyClickAnim(cancelButton);
				cancelButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelButton.setTextColor(getBtnTextColor(ctx));
				cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cancelButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelLp.rightMargin = dp(act, 8);
				buttonBar.addView(cancelButton, cancelLp);
				Button saveButton = new Button(act);
				applyClickAnim(saveButton);
				saveButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				saveButton.setTextColor(getOkBtnTextColor(ctx));
				saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				saveButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));
				saveButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1.0f);
				buttonBar.addView(saveButton, saveLp);
				contentLayout.addView(buttonBar);
				rootLayout.addView(contentLayout);
				dialog.setContentView(rootLayout);
				final String[] currentFile = {"homepage2.html"};
				loadFileContent(act, "homepage2.html", codeEditor, editorScroll, true);
				htmlButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveCurrentEditorState(currentFile[0], codeEditor, editorScroll);
						currentFile[0] = "homepage2.html";
						htmlButton.setTextColor(getOkBtnTextColor(ctx));
						htmlButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
						cssButton.setTextColor(getHintColor(ctx));
						cssButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
						loadFileContent(act, "homepage2.html", codeEditor, editorScroll, true);
					}
				});
				cssButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveCurrentEditorState(currentFile[0], codeEditor, editorScroll);
						currentFile[0] = "homepage.css";
						cssButton.setTextColor(getOkBtnTextColor(ctx));
						cssButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
						htmlButton.setTextColor(getHintColor(ctx));
						htmlButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
						loadFileContent(act, "homepage.css", codeEditor, editorScroll, true);
					}
				});
				editorScroll.getViewTreeObserver()
						.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
							@Override
							public void onScrollChanged() {
								if (currentFile[0] != null && editorStateCache.containsKey(currentFile[0])) {
									EditorState oldState = editorStateCache.get(currentFile[0]);
									editorStateCache.put(currentFile[0],
											new EditorState(oldState.content, editorScroll.getScrollY()));
								}
							}
						});
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						long currentTime = System.currentTimeMillis();
						Iterator<Map.Entry<String, EditorState>> it = editorStateCache.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, EditorState> entry = it.next();
							if (currentTime - entry.getValue().timestamp > 10 * 60 * 1000) {
								it.remove();
							}
						}
					}
				});
				backButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				saveButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveFileContent(act, currentFile[0], codeEditor.getText().toString());
						if (editorScroll != null) {
							int scrollY = editorScroll.getScrollY();
							editorStateCache.put(currentFile[0],
									new EditorState(codeEditor.getText().toString(), scrollY));
						}
						jiguroMessageWithContext(act,
								LocalizedStringProvider.getInstance().get(ctx, "theme_editor_save_success"));
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(rootLayout, act);
			}
		});
	}
	private void showThemeApplyDialog(final Context ctx, final ThemeInfo theme) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_apply_title"));
				builder.setMessage(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_apply_message")
						+ " \"" + theme.getName(ctx) + "\"?");
				builder.setPositiveButton(LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_apply"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								applyHomepageTheme(ctx, theme);
							}
						});
				builder.setNegativeButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"), null);
				AlertDialog dialog = builder.create();
				applyAlertDialogTheme(act, ctx, dialog);
				dialog.show();
			}
		});
	}
	private void applyHomepageTheme(final Context ctx, final ThemeInfo theme) {
		ThemeApplier applier = new ThemeApplier(ctx, theme);
		applier.applyTheme(new ThemeApplier.ThemeApplyCallback() {
			@Override
			public void onSuccess() {
				jiguroMessageWithContext(ctx,
						LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_apply_success"));
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						restartVia(ctx);
					}
				}, 500);
			}
			@Override
			public void onError(String message) {
				jiguroMessageWithContext(ctx, message);
			}
		});
	}
	private boolean downloadAndSaveFile(String urlString, String filePath) {
		HttpURLConnection connection = null;
		FileOutputStream outputStream = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				File file = new File(filePath);
				outputStream = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				return true;
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 下载文件失败: " + e);
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				bvLog("[BetterVia] 关闭流失败: " + e);
			}
		}
		return false;
	}
	private boolean replacePackageNameInFiles(String htmlFilePath, String cssFilePath, String oldPackageName,
			String newPackageName) {
		boolean htmlSuccess = replacePackageNameInFile(htmlFilePath, oldPackageName, newPackageName);
		boolean cssSuccess = replacePackageNameInFile(cssFilePath, oldPackageName, newPackageName);
		if (htmlSuccess && cssSuccess) {
			bvLog("[BetterVia] 已将文件中的包名从 " + oldPackageName + " 替换为 " + newPackageName);
			return true;
		} else {
			bvLog("[BetterVia] 替换文件中的包名失败");
			return false;
		}
	}
	private boolean replacePackageNameInFile(String filePath, String oldPackageName, String newPackageName) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				bvLog("[BetterVia] 文件不存在: " + filePath);
				return false;
			}
			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputStream.read(buffer);
			inputStream.close();
			String content = new String(buffer, "UTF-8");
			String newContent = content.replace(oldPackageName, newPackageName);
			outputStream = new FileOutputStream(file);
			outputStream.write(newContent.getBytes("UTF-8"));
			outputStream.flush();
			return true;
		} catch (Exception e) {
			bvLog("[BetterVia] 替换文件内容失败: " + e);
			return false;
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				bvLog("[BetterVia] 关闭流失败: " + e);
			}
		}
	}
	private void restartVia(Context ctx) {
		try {
			Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				Process.killProcess(Process.myPid());
				ctx.startActivity(intent);
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 重启Via失败: " + e);
		}
	}
	private void setScreenshotProtection(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (screenshotProtectionHook == null) {
				screenshotProtectionHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								Activity activity = (Activity) param.thisObject;
								if (screenshotProtectionEnabled) {
									activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
											WindowManager.LayoutParams.FLAG_SECURE);
									XposedBridge
											.log("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 启用截屏防护");
								}
							}
						});
				bvLog("[BetterVia] 截屏防护已启用");
			}
		} else {
			if (screenshotProtectionHook != null) {
				screenshotProtectionHook.unhook();
				screenshotProtectionHook = null;
				bvLog("[BetterVia] 截屏防护已停用");
				removeScreenshotProtection();
			}
		}
		screenshotProtectionEnabled = on;
		putPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, on);
	}
	private void removeScreenshotProtection() {
		for (final Activity activity : overlayViews.keySet()) {
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
					}
				});
			}
		}
		bvLog("[BetterVia] 已移除所有Activity的截屏防护");
	}
	private void setKeepScreenOn(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (keepScreenOnHook == null) {
				keepScreenOnHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								final Activity activity = (Activity) param.thisObject;
								if (keepScreenOnEnabled) {
									activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											activity.getWindow()
													.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
											screenOnActivities.put(activity, true);
											bvLog("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 启用屏幕常亮");
										}
									});
								}
							}
						});
				bvLog("[BetterVia] 屏幕常亮已启用");
			}
		} else {
			if (keepScreenOnHook != null) {
				keepScreenOnHook.unhook();
				keepScreenOnHook = null;
				bvLog("[BetterVia] 屏幕常亮已停用");
				removeKeepScreenOn();
			}
		}
		keepScreenOnEnabled = on;
		putPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, on);
	}
	private void setBackgroundVideoAudio(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (backgroundVideoHook == null) {
				try {
					Class<?> shellClass = findClassWithFallback("Shell", ctx, cl);
					if (shellClass == null) {
						bvLog("[BetterVia] 未找到Shell类，无法启用后台听视频功能");
						return;
					}
					backgroundVideoHook = XposedHelpers.findAndHookMethod(shellClass, "onPause",
							new XC_MethodReplacement() {
								@Override
								protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
									bvLog("[BetterVia] 阻止了Shell.onPause调用，保持前台状态");
									return null;
								}
							});
					XposedHelpers.findAndHookMethod(shellClass, "onWindowFocusChanged", boolean.class,
							new XC_MethodHook() {
								@Override
								protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
									param.args[0] = true;
									bvLog("[BetterVia] 强制设置窗口焦点为true");
								}
							});
					XposedHelpers.findAndHookMethod(shellClass, "isFinishing", new XC_MethodReplacement() {
						@Override
						protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
							bvLog("[BetterVia] 强制返回isFinishing=false");
							return false;
						}
					});
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
						XposedHelpers.findAndHookMethod(shellClass, "isDestroyed", new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
								bvLog("[BetterVia] 强制返回isDestroyed=false");
								return false;
							}
						});
					}
					bvLog("[BetterVia] 后台听视频功能已启用");
				} catch (Throwable e) {
					bvLog("[BetterVia] 启用后台听视频功能失败: " + e.getMessage());
				}
			}
		} else {
			if (backgroundVideoHook != null) {
				try {
					backgroundVideoHook.unhook();
					backgroundVideoHook = null;
					bvLog("[BetterVia] 后台听视频功能已停用");
				} catch (Throwable e) {
					bvLog("[BetterVia] 停用后台听视频功能出错: " + e.getMessage());
				}
			}
		}
		backgroundVideoEnabled = on;
		putPrefBoolean(ctx, KEY_BACKGROUND_VIDEO, on);
	}
	private void injectVideoKeepAliveScript(final WebView webView) {
		if (!backgroundVideoEnabled)
			return;
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					String jsCode = "javascript:(function() {" + " 
							+ "    var originalHidden = document.hidden;"
							+ "    var originalVisibilityState = document.visibilityState;" + "    " + " 
							+ "    Object.defineProperty(document, 'hidden', {"
							+ "        get: function() { return false; }" + 
							"    });" + "    " + "    Object.defineProperty(document, 'visibilityState', {"
							+ "        get: function() { return 'visible'; }" + 
							"    });" + "    " + " 
							+ "    var originalAddEventListener = document.addEventListener;"
							+ "    document.addEventListener = function(type, listener, options) {"
							+ "        if (type === 'visibilitychange') {" + " 
							+ "            console.log('Blocked visibilitychange listener');" + "            return;"
							+ "        }" + "        originalAddEventListener.call(this, type, listener, options);"
							+ "    };" + "    " + " 
							+ "    var videos = document.getElementsByTagName('video');"
							+ "    for (var i = 0; i < videos.length; i++) {" + "        var video = videos[i];"
							+ "        if (video.paused) {" + "            video.play().catch(function(e) {});"
							+ "        }" + "        " + " 
							+ "        video.addEventListener('pause', function(e) {"
							+ "            if (!document.hidden) { 
							+ "                this.play().catch(function(e) {});" + "            }" + "        });"
							+ "    }" + "    " + "    console.log('Video keep-alive script injected');" + "})();";
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						webView.evaluateJavascript(jsCode, null);
					} else {
						webView.loadUrl(jsCode);
					}
					bvLog("[BetterVia] 已注入视频保持播放脚本");
				} catch (Exception e) {
					bvLog("[BetterVia] 注入视频脚本失败: " + e.getMessage());
				}
			}
		}, 2000);
	}
	private void removeKeepScreenOn() {
		for (final Activity activity : screenOnActivities.keySet()) {
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					}
				});
			}
		}
		screenOnActivities.clear();
		bvLog("[BetterVia] 已移除所有Activity的屏幕常亮设置");
	}
	private void addScriptRepositoryItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "script_repository_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "script_repository_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showScriptRepositoryDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "script_repository_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private static class ScriptInfo {
		String id;
		Map<String, String> nameMap;
		Map<String, String> descriptionMap;
		Map<String, String> detailMap;
		Map<String, String> downloadUrls;
		String category;
		ScriptInfo(String id, Map<String, String> nameMap, Map<String, String> descriptionMap,
				Map<String, String> detailMap, Map<String, String> downloadUrls, String category) {
			this.id = id;
			this.nameMap = nameMap;
			this.descriptionMap = descriptionMap;
			this.detailMap = detailMap;
			this.downloadUrls = downloadUrls;
			this.category = category;
		}
		String getName(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
			}
			return langCode;
		}
		String getDescription(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return descriptionMap.getOrDefault(langCode, descriptionMap.get("zh-CN"));
			}
			return langCode;
		}
		String getDetail(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return detailMap.getOrDefault(langCode, detailMap.get("zh-CN"));
			}
			return langCode;
		}
		private String getLanguageCode(Context ctx) {
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved)) {
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				} else {
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
					return "zh-CN";
				} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
					return "zh-TW";
				} else if (Locale.ENGLISH.equals(locale)) {
					return "en";
				}
				return "zh-CN";
			}
			return saved;
		}
		static ScriptInfo fromJSON(JSONObject json) throws JSONException {
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext()) {
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> descriptionMap = new HashMap<>();
			JSONObject descriptions = json.getJSONObject("descriptions");
			Iterator<String> descKeys = descriptions.keys();
			while (descKeys.hasNext()) {
				String lang = descKeys.next();
				descriptionMap.put(lang, descriptions.getString(lang));
			}
			Map<String, String> detailMap = new HashMap<>();
			JSONObject details = json.getJSONObject("details");
			Iterator<String> detailKeys = details.keys();
			while (detailKeys.hasNext()) {
				String lang = detailKeys.next();
				detailMap.put(lang, details.getString(lang));
			}
			Map<String, String> downloadUrls = new HashMap<>();
			JSONObject downloads = json.getJSONObject("downloadUrls");
			Iterator<String> downloadKeys = downloads.keys();
			while (downloadKeys.hasNext()) {
				String channel = downloadKeys.next();
				downloadUrls.put(channel, downloads.getString(channel));
			}
			String category = json.getString("category");
			return new ScriptInfo(id, nameMap, descriptionMap, detailMap, downloadUrls, category);
		}
	}
	private void showScriptRepositoryDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int editBgColor = getEditBgColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(0, 0, 0, 0);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "script_repository_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "script_repository_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 16));
				root.addView(subtitle);
				LinearLayout searchContainer = new LinearLayout(act);
				searchContainer.setOrientation(LinearLayout.VERTICAL);
				searchContainer.setPadding(0, 0, 0, dp(act, 16));
				LinearLayout searchRow = new LinearLayout(act);
				searchRow.setOrientation(LinearLayout.HORIZONTAL);
				searchRow.setGravity(Gravity.CENTER_VERTICAL);
				final EditText searchEdit = new EditText(act);
				searchEdit.setHint(LocalizedStringProvider.getInstance().get(ctx, "script_search_hint"));
				searchEdit.setTextColor(textColor);
				searchEdit.setHintTextColor(hintColor);
				searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				searchEdit.setBackground(getRoundBg(act, editBgColor, 8));
				searchEdit.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				searchLp.rightMargin = dp(act, 8);
				searchRow.addView(searchEdit, searchLp);
				Button searchButton = new Button(act);
				applyClickAnim(searchButton);
				searchButton.setText(LocalizedStringProvider.getInstance().get(ctx, "script_search_button"));
				searchButton.setTextColor(okBtnTextColor);
				searchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				searchButton.setBackground(getRoundBg(act, okBtnBgColor, 8));
				searchButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				searchRow.addView(searchButton);
				searchContainer.addView(searchRow);
				final TextView scriptCountText = new TextView(act);
				scriptCountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				scriptCountText.setTextColor(hintColor);
				scriptCountText.setPadding(dp(act, 4), dp(act, 4), 0, 0);
				searchContainer.addView(scriptCountText);
				root.addView(searchContainer);
				final LinearLayout scriptsContainer = new LinearLayout(act);
				scriptsContainer.setOrientation(LinearLayout.VERTICAL);
				scriptsContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				final LinearLayout emptyStateContainer = new LinearLayout(act);
				emptyStateContainer.setOrientation(LinearLayout.VERTICAL);
				emptyStateContainer.setGravity(Gravity.CENTER);
				emptyStateContainer.setPadding(0, dp(act, 48), 0, dp(act, 48));
				emptyStateContainer.setVisibility(View.GONE);
				final ImageView errorIcon = new ImageView(act);
				errorIcon.setImageResource(android.R.drawable.ic_menu_report_image);
				errorIcon.setColorFilter(hintColor);
				errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
				iconLp.gravity = Gravity.CENTER;
				iconLp.bottomMargin = dp(act, 16);
				emptyStateContainer.addView(errorIcon, iconLp);
				final TextView emptyStateText = new TextView(act);
				emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				emptyStateText.setTextColor(hintColor);
				emptyStateText.setGravity(Gravity.CENTER);
				emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
				emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "scripts_loading"));
				emptyStateContainer.addView(emptyStateText);
				root.addView(scriptsContainer);
				root.addView(emptyStateContainer);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
						window.setClipToOutline(true);
					}
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				final List<ScriptInfo>[] allScripts = new List[]{new ArrayList<ScriptInfo>()};
				final Runnable updateScriptCount = new Runnable() {
					@Override
					public void run() {
						if (allScripts[0] != null && !allScripts[0].isEmpty()) {
							String countText = String.format(
									LocalizedStringProvider.getInstance().get(ctx, "script_total_count"),
									allScripts[0].size());
							scriptCountText.setText(countText);
						} else {
							scriptCountText
									.setText(LocalizedStringProvider.getInstance().get(ctx, "script_loading_count"));
						}
					}
				};
				searchButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = searchEdit.getText().toString().trim().toLowerCase();
						filterScripts(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts[0],
								query, true);
					}
				});
				searchEdit.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						String query = s.toString().trim().toLowerCase();
						filterScripts(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts[0],
								query, false);
						if (allScripts[0] != null && !allScripts[0].isEmpty()) {
							List<ScriptInfo> filteredScripts = new ArrayList<>();
							for (ScriptInfo script : allScripts[0]) {
								String name = script.getName(ctx).toLowerCase();
								String description = script.getDescription(ctx).toLowerCase();
								String category = script.category.toLowerCase();
								if (name.contains(query) || description.contains(query) || category.contains(query)) {
									filteredScripts.add(script);
								}
							}
							String countText;
							if (query.isEmpty()) {
								countText = String.format(
										LocalizedStringProvider.getInstance().get(ctx, "script_total_count"),
										allScripts[0].size());
							} else {
								countText = String.format(
										LocalizedStringProvider.getInstance().get(ctx, "script_filtered_count"),
										filteredScripts.size(), allScripts[0].size());
							}
							scriptCountText.setText(countText);
						}
					}
					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				showScriptsLoadingState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText);
				loadScriptsFromNetwork(ctx, new ScriptsLoadCallback() {
					@Override
					public void onScriptsLoaded(final List<ScriptInfo> scripts) {
						allScripts[0] = scripts;
						if (act != null && !act.isFinishing()) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
											scripts);
									updateScriptCount.run();
								}
							});
						}
					}
					@Override
					public void onLoadFailed(final String error) {
						if (act != null && !act.isFinishing()) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer,
											emptyStateText, error);
									scriptCountText.setText(
											LocalizedStringProvider.getInstance().get(ctx, "script_load_failed_count"));
								}
							});
						}
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void loadScriptsFromNetwork(final Context ctx, final ScriptsLoadCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String scriptsUrl = networkSource.equals(NETWORK_SOURCE_GITEE)
							? "https:
							: "https:
					URL url = new URL(scriptsUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(15000);
					connection.setReadTimeout(15000);
					connection.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						reader.close();
						JSONObject json = new JSONObject(response.toString());
						JSONArray scriptsArray = json.getJSONArray("scripts");
						List<ScriptInfo> scripts = new ArrayList<>();
						for (int i = 0; i < scriptsArray.length(); i++) {
							JSONObject scriptJson = scriptsArray.getJSONObject(i);
							ScriptInfo script = ScriptInfo.fromJSON(scriptJson);
							scripts.add(script);
						}
						callback.onScriptsLoaded(scripts);
					} else {
						callback.onLoadFailed("HTTP " + connection.getResponseCode());
					}
					connection.disconnect();
				} catch (Exception e) {
					callback.onLoadFailed(e.getMessage());
				}
			}
		}).start();
	}
	private void filterScripts(Activity act, Context ctx, LinearLayout scriptsContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, List<ScriptInfo> allScripts, String query,
			boolean showToast) {
		if (allScripts == null || allScripts.isEmpty()) {
			showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
					LocalizedStringProvider.getInstance().get(ctx, "no_scripts_available"));
			return;
		}
		if (query.isEmpty()) {
			refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts);
			if (showToast) {
				jiguroMessageWithContext(act, String
						.format(LocalizedStringProvider.getInstance().get(ctx, "script_show_all"), allScripts.size()));
			}
			return;
		}
		List<ScriptInfo> filteredScripts = new ArrayList<>();
		for (ScriptInfo script : allScripts) {
			String name = script.getName(ctx).toLowerCase();
			String description = script.getDescription(ctx).toLowerCase();
			String category = script.category.toLowerCase();
			if (name.contains(query) || description.contains(query) || category.contains(query)) {
				filteredScripts.add(script);
			}
		}
		if (filteredScripts.isEmpty()) {
			scriptsContainer.removeAllViews();
			scriptsContainer.setVisibility(View.GONE);
			emptyStateContainer.setVisibility(View.VISIBLE);
			emptyStateText.setText(
					String.format(LocalizedStringProvider.getInstance().get(ctx, "script_search_no_results"), query));
			for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
				View child = emptyStateContainer.getChildAt(i);
				if (child instanceof ImageView) {
					child.setVisibility(View.VISIBLE);
					((ImageView) child).setImageResource(android.R.drawable.ic_search_category_default);
				} else if (child instanceof ProgressBar) {
					emptyStateContainer.removeView(child);
				}
			}
			if (showToast) {
				jiguroMessageWithContext(act,
						LocalizedStringProvider.getInstance().get(ctx, "script_search_no_results_toast"));
			}
		} else {
			refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, filteredScripts);
			if (showToast) {
				jiguroMessageWithContext(act,
						String.format(LocalizedStringProvider.getInstance().get(ctx, "script_search_results"),
								filteredScripts.size()));
			}
		}
	}
	private void showScriptsLoadingState(Activity act, Context ctx, LinearLayout scriptsContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText) {
		scriptsContainer.removeAllViews();
		scriptsContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "scripts_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
		}
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showScriptsErrorState(Activity act, Context ctx, LinearLayout scriptsContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, String error) {
		scriptsContainer.removeAllViews();
		scriptsContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "scripts_load_failed") + "\n"
				+ LocalizedStringProvider.getInstance().get(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.VISIBLE);
			} else if (child instanceof ProgressBar) {
				emptyStateContainer.removeView(child);
			}
		}
		jiguroMessageWithContext(ctx,
				LocalizedStringProvider.getInstance().get(ctx, "scripts_load_failed") + ": " + error);
	}
	private void refreshScriptsList(final Activity act, final Context ctx, LinearLayout scriptsContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, List<ScriptInfo> scripts) {
		scriptsContainer.removeAllViews();
		if (scripts == null || scripts.isEmpty()) {
			showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
					"No scripts available");
			return;
		}
		scriptsContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		Map<String, List<ScriptInfo>> categorizedScripts = new HashMap<>();
		for (ScriptInfo script : scripts) {
			String category = script.category;
			if (!categorizedScripts.containsKey(category)) {
				categorizedScripts.put(category, new ArrayList<ScriptInfo>());
			}
			categorizedScripts.get(category).add(script);
		}
		for (Map.Entry<String, List<ScriptInfo>> entry : categorizedScripts.entrySet()) {
			String category = entry.getKey();
			List<ScriptInfo> categoryScripts = entry.getValue();
			TextView categoryTitle = new TextView(act);
			categoryTitle.setText(category);
			categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			categoryTitle.setTextColor(getOkBtnBgColor(ctx));
			categoryTitle.setTypeface(null, Typeface.BOLD);
			categoryTitle.setPadding(0, dp(act, 16), 0, dp(act, 8));
			scriptsContainer.addView(categoryTitle);
			for (final ScriptInfo script : categoryScripts) {
				LinearLayout scriptCard = createScriptCard(act, ctx, script);
				LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				cardLp.bottomMargin = dp(ctx, 12);
				scriptsContainer.addView(scriptCard, cardLp);
			}
		}
	}
	private LinearLayout createScriptCard(final Activity act, final Context ctx, final ScriptInfo script) {
		LinearLayout scriptCard = new LinearLayout(act);
		scriptCard.setOrientation(LinearLayout.VERTICAL);
		scriptCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(getItemBgColor(ctx)); 
		cardBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
		cardBg.setCornerRadius(dp(act, 12));
		scriptCard.setBackground(cardBg);
		TextView scriptName = new TextView(act);
		scriptName.setText(script.getName(ctx));
		scriptName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		scriptName.setTextColor(getTextColor(ctx)); 
		scriptName.setTypeface(null, Typeface.BOLD);
		scriptCard.addView(scriptName);
		TextView scriptDescription = new TextView(act);
		scriptDescription.setText(script.getDescription(ctx));
		scriptDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		scriptDescription.setTextColor(getHintColor(ctx)); 
		scriptDescription.setPadding(0, dp(act, 8), 0, 0);
		scriptCard.addView(scriptDescription);
		scriptCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showScriptDetailDialog(ctx, script);
			}
		});
		return scriptCard;
	}
	private void showScriptDetailDialog(final Context ctx, final ScriptInfo script) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog[] detailDialogRef = new AlertDialog[1];
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle(script.getName(ctx));
				ScrollView scrollView = new ScrollView(act);
				scrollView.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				scrollView.setBackground(new ColorDrawable(Color.TRANSPARENT));
				LinearLayout layout = new LinearLayout(act);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				layout.setBackground(bg);
				TextView detailText = new TextView(act);
				detailText.setText(script.getDetail(ctx));
				detailText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				detailText.setTextColor(getTextColor(ctx)); 
				detailText.setLineSpacing(dp(act, 4), 1.2f);
				layout.addView(detailText);
				LinearLayout buttonContainer = new LinearLayout(act);
				buttonContainer.setOrientation(LinearLayout.VERTICAL);
				buttonContainer.setPadding(0, dp(act, 16), 0, 0);
				for (final Map.Entry<String, String> entry : script.downloadUrls.entrySet()) {
					Button downloadBtn = new Button(act);
					applyClickAnim(downloadBtn);
					downloadBtn.setText(entry.getKey());
					downloadBtn.setTextColor(getOkBtnTextColor(ctx)); 
					downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					downloadBtn.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					downloadBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8)); 
					LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					btnLp.bottomMargin = dp(act, 8);
					buttonContainer.addView(downloadBtn, btnLp);
					downloadBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								if (detailDialogRef[0] != null) {
									detailDialogRef[0].dismiss();
								}
								new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
									@Override
									public void run() {
										try {
											jiguroMessageWithContext(act, LocalizedStringProvider.getInstance().get(ctx,
													"script_opened_in_via"));
											new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
												@Override
												public void run() {
													try {
														Intent intent = new Intent(Intent.ACTION_VIEW,
																Uri.parse(entry.getValue()));
														act.startActivity(intent);
													} catch (Exception e) {
														jiguroMessageWithContext(act, LocalizedStringProvider
																.getInstance().get(ctx, "cannot_open_download_link"));
													}
												}
											}, 300);
										} catch (Exception e) {
											jiguroMessageWithContext(act, LocalizedStringProvider.getInstance().get(ctx,
													"cannot_open_download_link"));
										}
									}
								}, 100);
							} catch (Exception e) {
								jiguroMessageWithContext(act,
										LocalizedStringProvider.getInstance().get(ctx, "cannot_open_download_link"));
							}
						}
					});
				}
				layout.addView(buttonContainer);
				scrollView.addView(layout);
				builder.setView(scrollView);
				builder.setNegativeButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"), null);
				detailDialogRef[0] = builder.create();
				detailDialogRef[0].show();
				applyAlertDialogTheme(act, ctx, detailDialogRef[0]);
				animateDialogEntrance(layout, act);
			}
		});
	}
	private interface ScriptsLoadCallback {
		void onScriptsLoaded(List<ScriptInfo> scripts);
		void onLoadFailed(String error);
	}
	private void addAdBlockRulesItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "ad_block_rules_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "ad_block_rules_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAdBlockRulesDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "ad_block_rules_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showAdBlockRulesDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(0, 0, 0, 0);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "ad_block_rules_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "ad_block_rules_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				final LinearLayout rulesContainer = new LinearLayout(act);
				rulesContainer.setOrientation(LinearLayout.VERTICAL);
				rulesContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				final LinearLayout emptyStateContainer = new LinearLayout(act);
				emptyStateContainer.setOrientation(LinearLayout.VERTICAL);
				emptyStateContainer.setGravity(Gravity.CENTER);
				emptyStateContainer.setPadding(0, dp(act, 48), 0, dp(act, 48));
				emptyStateContainer.setVisibility(View.GONE);
				final ImageView errorIcon = new ImageView(act);
				errorIcon.setImageResource(android.R.drawable.ic_menu_report_image);
				errorIcon.setColorFilter(hintColor);
				errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
				iconLp.gravity = Gravity.CENTER;
				iconLp.bottomMargin = dp(act, 16);
				emptyStateContainer.addView(errorIcon, iconLp);
				final TextView emptyStateText = new TextView(act);
				emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				emptyStateText.setTextColor(hintColor);
				emptyStateText.setGravity(Gravity.CENTER);
				emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
				emptyStateContainer.addView(emptyStateText);
				root.addView(rulesContainer);
				root.addView(emptyStateContainer);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
						window.setClipToOutline(true);
					}
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				showRulesLoadingState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText);
				loadRulesFromNetwork(ctx, new RulesLoadCallback() {
					@Override
					public void onRulesLoaded(final List<RuleInfo> rules) {
						if (act != null && !act.isFinishing()) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									refreshRulesList(act, ctx, rulesContainer, emptyStateContainer, emptyStateText,
											rules);
								}
							});
						}
					}
					@Override
					public void onLoadFailed(final String error) {
						if (act != null && !act.isFinishing()) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showRulesErrorState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText,
											error);
								}
							});
						}
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private static class RuleInfo {
		String id;
		Map<String, String> nameMap;
		Map<String, String> descriptionMap;
		Map<String, String> detailMap;
		Map<String, String> downloadUrls;
		String category;
		String author;
		String homepage;
		RuleInfo(String id, Map<String, String> nameMap, Map<String, String> descriptionMap,
				Map<String, String> detailMap, Map<String, String> downloadUrls, String category, String author,
				String homepage) {
			this.id = id;
			this.nameMap = nameMap;
			this.descriptionMap = descriptionMap;
			this.detailMap = detailMap;
			this.downloadUrls = downloadUrls;
			this.category = category;
			this.author = author;
			this.homepage = homepage;
		}
		String getName(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
			}
			return langCode;
		}
		String getDescription(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return descriptionMap.getOrDefault(langCode, descriptionMap.get("zh-CN"));
			}
			return langCode;
		}
		String getDetail(Context ctx) {
			String langCode = getLanguageCode(ctx);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return detailMap.getOrDefault(langCode, detailMap.get("zh-CN"));
			}
			return langCode;
		}
		private String getLanguageCode(Context ctx) {
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved)) {
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				} else {
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
					return "zh-CN";
				} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
					return "zh-TW";
				} else if (Locale.ENGLISH.equals(locale)) {
					return "en";
				}
				return "zh-CN";
			}
			return saved;
		}
		static RuleInfo fromJSON(JSONObject json) throws JSONException {
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext()) {
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> descriptionMap = new HashMap<>();
			JSONObject descriptions = json.getJSONObject("descriptions");
			Iterator<String> descKeys = descriptions.keys();
			while (descKeys.hasNext()) {
				String lang = descKeys.next();
				descriptionMap.put(lang, descriptions.getString(lang));
			}
			Map<String, String> detailMap = new HashMap<>();
			JSONObject details = json.getJSONObject("details");
			Iterator<String> detailKeys = details.keys();
			while (detailKeys.hasNext()) {
				String lang = detailKeys.next();
				detailMap.put(lang, details.getString(lang));
			}
			Map<String, String> downloadUrls = new HashMap<>();
			JSONObject downloads = json.getJSONObject("downloadUrls");
			Iterator<String> downloadKeys = downloads.keys();
			while (downloadKeys.hasNext()) {
				String channel = downloadKeys.next();
				downloadUrls.put(channel, downloads.getString(channel));
			}
			String category = json.getString("category");
			String author = json.optString("author", "");
			String homepage = json.optString("homepage", "");
			return new RuleInfo(id, nameMap, descriptionMap, detailMap, downloadUrls, category, author, homepage);
		}
	}
	private void loadRulesFromNetwork(final Context ctx, final RulesLoadCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String rulesUrl = networkSource.equals(NETWORK_SOURCE_GITEE)
							? "https:
							: "https:
					URL url = new URL(rulesUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(15000);
					connection.setReadTimeout(15000);
					connection.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						reader.close();
						JSONObject json = new JSONObject(response.toString());
						JSONArray rulesArray = json.getJSONArray("rules");
						List<RuleInfo> rules = new ArrayList<>();
						for (int i = 0; i < rulesArray.length(); i++) {
							JSONObject ruleJson = rulesArray.getJSONObject(i);
							RuleInfo rule = RuleInfo.fromJSON(ruleJson);
							rules.add(rule);
						}
						callback.onRulesLoaded(rules);
					} else {
						callback.onLoadFailed("HTTP " + connection.getResponseCode());
					}
					connection.disconnect();
				} catch (Exception e) {
					callback.onLoadFailed(e.getMessage());
				}
			}
		}).start();
	}
	private void showRulesLoadingState(Activity act, Context ctx, LinearLayout rulesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText) {
		rulesContainer.removeAllViews();
		rulesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "rules_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
		}
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showRulesErrorState(Activity act, Context ctx, LinearLayout rulesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, String error) {
		rulesContainer.removeAllViews();
		rulesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(LocalizedStringProvider.getInstance().get(ctx, "rules_load_failed") + "\n"
				+ LocalizedStringProvider.getInstance().get(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.VISIBLE);
			} else if (child instanceof ProgressBar) {
				emptyStateContainer.removeView(child);
			}
		}
		jiguroMessageWithContext(ctx,
				LocalizedStringProvider.getInstance().get(ctx, "rules_load_failed") + ": " + error);
	}
	private void refreshRulesList(final Activity act, final Context ctx, LinearLayout rulesContainer,
			LinearLayout emptyStateContainer, TextView emptyStateText, List<RuleInfo> rules) {
		rulesContainer.removeAllViews();
		if (rules == null || rules.isEmpty()) {
			showRulesErrorState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText, "No rules available");
			return;
		}
		rulesContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		Map<String, List<RuleInfo>> categorizedRules = new HashMap<>();
		for (RuleInfo rule : rules) {
			String category = rule.category;
			if (!categorizedRules.containsKey(category)) {
				categorizedRules.put(category, new ArrayList<RuleInfo>());
			}
			categorizedRules.get(category).add(rule);
		}
		for (Map.Entry<String, List<RuleInfo>> entry : categorizedRules.entrySet()) {
			String category = entry.getKey();
			List<RuleInfo> categoryRules = entry.getValue();
			TextView categoryTitle = new TextView(act);
			categoryTitle.setText(getCategoryDisplayName(ctx, category));
			categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			categoryTitle.setTextColor(getOkBtnBgColor(ctx));
			categoryTitle.setTypeface(null, Typeface.BOLD);
			categoryTitle.setPadding(0, dp(act, 16), 0, dp(act, 8));
			rulesContainer.addView(categoryTitle);
			for (final RuleInfo rule : categoryRules) {
				LinearLayout ruleCard = createRuleCard(act, ctx, rule);
				LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				cardLp.bottomMargin = dp(ctx, 12);
				rulesContainer.addView(ruleCard, cardLp);
			}
		}
	}
	private String getCategoryDisplayName(Context ctx, String category) {
		if ("small".equals(category)) {
			return LocalizedStringProvider.getInstance().get(ctx, "rules_category_small");
		} else if ("large".equals(category)) {
			return LocalizedStringProvider.getInstance().get(ctx, "rules_category_large");
		}
		return category;
	}
	private LinearLayout createRuleCard(final Activity act, final Context ctx, final RuleInfo rule) {
		LinearLayout ruleCard = new LinearLayout(act);
		ruleCard.setOrientation(LinearLayout.VERTICAL);
		ruleCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(getItemBgColor(ctx)); 
		cardBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
		cardBg.setCornerRadius(dp(act, 12));
		ruleCard.setBackground(cardBg);
		TextView ruleName = new TextView(act);
		ruleName.setText(rule.getName(ctx));
		ruleName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		ruleName.setTextColor(getTextColor(ctx)); 
		ruleName.setTypeface(null, Typeface.BOLD);
		ruleCard.addView(ruleName);
		TextView ruleDescription = new TextView(act);
		ruleDescription.setText(rule.getDescription(ctx));
		ruleDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		ruleDescription.setTextColor(getHintColor(ctx)); 
		ruleDescription.setPadding(0, dp(act, 8), 0, 0);
		ruleCard.addView(ruleDescription);
		ruleCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRuleDetailDialog(ctx, rule);
			}
		});
		return ruleCard;
	}
	private void showRuleDetailDialog(final Context ctx, final RuleInfo rule) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog[] detailDialogRef = new AlertDialog[1];
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle(rule.getName(ctx));
				ScrollView scrollView = new ScrollView(act);
				scrollView.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				scrollView.setBackground(new ColorDrawable(Color.TRANSPARENT));
				LinearLayout layout = new LinearLayout(act);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				layout.setBackground(bg);
				TextView detailText = new TextView(act);
				detailText.setText(rule.getDetail(ctx));
				detailText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				detailText.setTextColor(getTextColor(ctx)); 
				detailText.setLineSpacing(dp(act, 4), 1.2f);
				layout.addView(detailText);
				if (rule.author != null && !rule.author.isEmpty()) {
					TextView authorText = new TextView(act);
					authorText.setText(
							LocalizedStringProvider.getInstance().get(ctx, "rule_author") + ": " + rule.author);
					authorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					authorText.setTextColor(getHintColor(ctx)); 
					authorText.setPadding(0, dp(act, 8), 0, 0);
					layout.addView(authorText);
				}
				if (rule.homepage != null && !rule.homepage.isEmpty()) {
					TextView homepageText = new TextView(act);
					homepageText.setText(
							LocalizedStringProvider.getInstance().get(ctx, "rule_homepage") + ": " + rule.homepage);
					homepageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					homepageText.setTextColor(getTitleColor(ctx)); 
					homepageText.setPadding(0, dp(act, 8), 0, 0);
					homepageText.setPaintFlags(homepageText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
					homepageText.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rule.homepage));
								act.startActivity(intent);
							} catch (Exception e) {
								jiguroMessageWithContext(act,
										LocalizedStringProvider.getInstance().get(ctx, "cannot_open_homepage"));
							}
						}
					});
					layout.addView(homepageText);
				}
				LinearLayout buttonContainer = new LinearLayout(act);
				buttonContainer.setOrientation(LinearLayout.VERTICAL);
				buttonContainer.setPadding(0, dp(act, 16), 0, 0);
				int channelIndex = 1;
				for (final Map.Entry<String, String> entry : rule.downloadUrls.entrySet()) {
					Button downloadBtn = new Button(act);
					applyClickAnim(downloadBtn);
					downloadBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "rule_channel") + " "
							+ channelIndex + " - " + entry.getKey());
					downloadBtn.setTextColor(getOkBtnTextColor(ctx)); 
					downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					downloadBtn.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					downloadBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8)); 
					LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					btnLp.bottomMargin = dp(act, 8);
					buttonContainer.addView(downloadBtn, btnLp);
					downloadBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							copyToClipboard(act, entry.getValue());
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(ctx, "rule_link_copied"));
						}
					});
					channelIndex++;
				}
				layout.addView(buttonContainer);
				scrollView.addView(layout);
				builder.setView(scrollView);
				builder.setNegativeButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"), null);
				detailDialogRef[0] = builder.create();
				detailDialogRef[0].show();
				applyAlertDialogTheme(act, ctx, detailDialogRef[0]);
				animateDialogEntrance(layout, act);
			}
		});
	}
	private interface RulesLoadCallback {
		void onRulesLoaded(List<RuleInfo> rules);
		void onLoadFailed(String error);
	}
	private void setHideStatusBar(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (hideStatusBarHook == null) {
				hideStatusBarHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								final Activity activity = (Activity) param.thisObject;
								if (hideStatusBarEnabled) {
									activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											setupStatusBarHiding(activity);
											statusBarHiddenActivities.put(activity, true);
											bvLog("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 设置状态栏隐藏");
										}
									});
								}
							}
						});
				bvLog("[BetterVia] 隐藏状态栏已启用");
			}
		} else {
			if (hideStatusBarHook != null) {
				hideStatusBarHook.unhook();
				hideStatusBarHook = null;
				bvLog("[BetterVia] 隐藏状态栏已停用");
				restoreStatusBar();
			}
		}
		hideStatusBarEnabled = on;
		putPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, on);
	}
	private void setupStatusBarHiding(final Activity activity) {
		try {
			if (activity.isFinishing() || activity.isDestroyed())
				return;
			final View decorView = activity.getWindow().getDecorView();
			hideStatusBarImmediate(activity);
			decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
						decorView.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (!activity.isFinishing() && !activity.isDestroyed() && hideStatusBarEnabled) {
									hideStatusBarImmediate(activity);
									setupStatusBarHiding(activity);
								}
							}
						}, 100);
					}
				}
			});
		} catch (Exception e) {
			bvLog("[BetterVia] 滑动更新状态栏失败: " + e);
		}
	}
	private void hideStatusBarImmediate(Activity activity) {
		try {
			if (activity.isFinishing() || activity.isDestroyed()) {
				return;
			}
			View decorView = activity.getWindow().getDecorView();
			int flags = decorView.getSystemUiVisibility();
			flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(flags);
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} catch (Exception e) {
			bvLog("[BetterVia] 立即隐藏状态栏失败: " + e);
		}
	}
	private void restoreStatusBar() {
		for (final Activity activity : statusBarHiddenActivities.keySet()) {
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							View decorView = activity.getWindow().getDecorView();
							decorView.setOnSystemUiVisibilityChangeListener(null);
							Runnable rehideRunnable = statusBarRehideRunnables.get(activity);
							if (rehideRunnable != null) {
								decorView.removeCallbacks(rehideRunnable);
								statusBarRehideRunnables.remove(activity);
							}
							int flags = decorView.getSystemUiVisibility();
							flags &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
							flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
							flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
							flags &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
							decorView.setSystemUiVisibility(flags);
							activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						} catch (Exception e) {
							bvLog("[BetterVia] 恢复状态栏失败: " + e);
						}
					}
				});
			}
		}
		statusBarHiddenActivities.clear();
		statusBarRehideRunnables.clear();
		bvLog("[BetterVia] 已恢复所有Activity的状态栏显示");
	}
	private void setBlockSwipeBack(Context ctx, ClassLoader cl, boolean on) {
		try {
			if (on) {
				if (swipeBackHook == null) {
					bvLog("[BetterVia] 开始设置屏蔽右滑返回功能...");
					try {
						Class<?> viewPagerClass = cl.loadClass("androidx.viewpager.widget.ViewPager");
						swipeBackHook = XposedHelpers.findAndHookMethod(viewPagerClass, "canScrollHorizontally",
								int.class, new XC_MethodHook() {
									@Override
									protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
										if (!blockSwipeBackEnabled) {
											return;
										}
										int direction = (Integer) param.args[0];
										if (direction < 0) {
											bvLog("[BetterVia] ViewPager.canScrollHorizontally: 阻止向右滚动");
											param.setResult(false);
										}
									}
								});
						bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (androidx.viewpager.widget.ViewPager)");
					} catch (ClassNotFoundException e) {
						bvLog("[BetterVia] ✗ 未找到 androidx.viewpager.widget.ViewPager");
						try {
							Class<?> viewPagerClass = cl.loadClass("android.support.v4.view.ViewPager");
							swipeBackHook = XposedHelpers.findAndHookMethod(viewPagerClass, "canScrollHorizontally",
									int.class, new XC_MethodHook() {
										@Override
										protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
											if (!blockSwipeBackEnabled) {
												return;
											}
											int direction = (Integer) param.args[0];
											if (direction < 0) {
												bvLog("[BetterVia] ViewPager.canScrollHorizontally: 阻止向右滚动");
												param.setResult(false);
											}
										}
									});
							bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (android.support.v4.view.ViewPager)");
						} catch (ClassNotFoundException e2) {
							bvLog("[BetterVia] ✗ 未找到 android.support.v4.view.ViewPager");
						}
					}
					if (swipeBackHook == null) {
						try {
							swipeBackHook = XposedHelpers.findAndHookMethod(ViewGroup.class,
									"requestDisallowInterceptTouchEvent", boolean.class, new XC_MethodHook() {
										@Override
										protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
											if (!blockSwipeBackEnabled) {
												return;
											}
											param.args[0] = false;
											bvLog("[BetterVia] requestDisallowInterceptTouchEvent: 强制不拦截");
										}
									});
							bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (ViewGroup.requestDisallowInterceptTouchEvent)");
						} catch (Throwable e) {
							bvLog("[BetterVia] ✗ Hook ViewGroup.requestDisallowInterceptTouchEvent 失败: " + e);
						}
					}
					if (swipeBackHook == null) {
						try {
							swipeBackHook = XposedHelpers.findAndHookMethod(ViewGroup.class, "onInterceptTouchEvent",
									MotionEvent.class, new XC_MethodHook() {
										@Override
										protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
											if (!blockSwipeBackEnabled) {
												return;
											}
											MotionEvent event = (MotionEvent) param.args[0];
											if (event.getAction() == MotionEvent.ACTION_DOWN) {
												float x = event.getX();
												if (x < dp(((View) param.thisObject).getContext(), 50)) {
													bvLog("[BetterVia] onInterceptTouchEvent: 拦截左边缘触摸");
													param.setResult(true);
												}
											}
										}
									});
							bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (ViewGroup.onInterceptTouchEvent)");
						} catch (Throwable e) {
							bvLog("[BetterVia] ✗ Hook ViewGroup.onInterceptTouchEvent 失败: " + e);
						}
					}
					if (swipeBackHook == null) {
						try {
							swipeBackHook = XposedHelpers.findAndHookMethod(Activity.class, "onBackPressed",
									new XC_MethodHook() {
										@Override
										protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
											if (!blockSwipeBackEnabled) {
												return;
											}
											bvLog("[BetterVia] onBackPressed: 阻止返回");
											param.setResult(null);
										}
									});
							bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (Activity.onBackPressed)");
						} catch (Throwable e) {
							bvLog("[BetterVia] ✗ Hook Activity.onBackPressed 失败: " + e);
						}
					}
					if (swipeBackHook == null) {
						bvLog("[BetterVia] ✗ 所有策略都失败，无法屏蔽右滑返回");
					}
				}
			} else {
				if (swipeBackHook != null) {
					swipeBackHook.unhook();
					swipeBackHook = null;
					bvLog("[BetterVia] 屏蔽右滑返回已停用");
				}
			}
			blockSwipeBackEnabled = on;
			putPrefBoolean(ctx, KEY_BLOCK_SWIPE_BACK, on);
		} catch (Throwable t) {
			bvLog("[BetterVia] 设置屏蔽右滑返回失败: " + t);
		}
	}
	private static XC_MethodHook.Unhook privacyLockStartupHook = null;
	private static volatile boolean privacyLockVerified = false;
	private static volatile boolean privacyLockPageUsed = false; 
	private static volatile View privacyLockOverlayView = null;
	private static volatile boolean privacyLockHookRegistered = false;
	private void setPrivacyLockStartupHook(final Context ctx, final ClassLoader cl) {
		try {
			final boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
			final boolean applyStartup = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, false);
			if (!privacyLockEnabled || !applyStartup) {
				bvLog("[BetterVia] 隐私锁启动拦截未启用");
				if (privacyLockStartupHook != null) {
					privacyLockStartupHook.unhook();
					privacyLockStartupHook = null;
					privacyLockHookRegistered = false;
				}
				return;
			}
			SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
			boolean passwordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
			if (!passwordSet) {
				bvLog("[BetterVia] 隐私锁未设置密码，跳过启动拦截");
				return;
			}
			privacyLockVerified = false;
			privacyLockOverlayView = null;
			bvLog("[BetterVia] 开始设置隐私锁启动拦截（全屏覆盖层方案）...");
			if (privacyLockStartupHook != null && privacyLockHookRegistered) {
				privacyLockStartupHook.unhook();
				privacyLockStartupHook = null;
				bvLog("[BetterVia] 移除旧的隐私锁启动拦截Hook");
			}
			privacyLockStartupHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							final Activity activity = (Activity) param.thisObject;
							String activityName = activity.getClass().getName();
							boolean isTargetActivity = false;
							if (activityName.equals("mark.via.Shell")) {
								isTargetActivity = true;
							} else if (activityName.contains(".Shell")) {
								if (activityName.startsWith(currentPackageName + ".Shell")) {
									isTargetActivity = true;
								}
							}
							if (!isTargetActivity) {
								return;
							}
							bvLog("[BetterVia] 检测到 " + activityName + " Activity启动，重置验证状态");
							privacyLockVerified = false;
							privacyLockOverlayView = null;
							if (privacyLockVerified) {
								return;
							}
							bvLog("[BetterVia] 执行启动拦截");
							new Handler().post(new Runnable() {
								@Override
								public void run() {
									if (activity.isFinishing() || activity.isDestroyed()) {
										return;
									}
									activity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (activity.isFinishing() || activity.isDestroyed()) {
												return;
											}
											if (!privacyLockVerified) {
												bvLog("[BetterVia] 准备显示隐私锁覆盖层");
												showPrivacyLockStartupOverlay(activity, activity); 
											}
										}
									});
								}
							});
						}
					});
			privacyLockHookRegistered = true;
			bvLog("[BetterVia] ✓ 隐私锁启动拦截已启用（全屏覆盖层方案）");
		} catch (Throwable t) {
			bvLog("[BetterVia] 设置隐私锁启动拦截失败: " + t);
		}
	}
	private void showPrivacyLockStartupOverlay(final Activity activity, final Context ctx) {
		try {
			if (privacyLockOverlayView != null) {
				return;
			}
			final boolean startupImageEnabled = StartupExecutionHelper.getStartupImageEnable(ctx);
			bvLog("[BetterVia] 创建隐私锁启动覆盖层，启动图启用: " + startupImageEnabled);
			int overlayColor = getBgColor(activity); 
			final FrameLayout overlay = new FrameLayout(activity);
			overlay.setBackgroundColor(overlayColor); 
			overlay.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			overlay.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			try {
				activity.getWindow().addContentView(overlay, new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				privacyLockOverlayView = overlay;
				bvLog("[BetterVia] 隐私锁启动覆盖层已添加到DecorView");
			} catch (Exception e) {
				bvLog("[BetterVia] 添加覆盖层失败: " + e);
				privacyLockVerified = true;
				return;
			}
			activity.getWindow().getDecorView().setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
						bvLog("[BetterVia] 用户按返回键，关闭Via应用");
						activity.finish();
						System.exit(0);
						return true;
					}
					return false;
				}
			});
			SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
			int passwordType = -1;
			String patternPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
			if (patternPassword != null && !patternPassword.isEmpty()) {
				passwordType = PasswordManager.PASSWORD_TYPE_PATTERN;
			}
			String pinPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
			if (pinPassword != null && !pinPassword.isEmpty()) {
				passwordType = PasswordManager.PASSWORD_TYPE_PIN;
			}
			if (passwordType == -1) {
				bvLog("[BetterVia] 未找到密码，移除覆盖层");
				privacyLockVerified = true;
				removePrivacyLockOverlay(activity);
				return;
			}
			bvLog("[BetterVia] 准备显示验证对话框，密码类型: "
					+ (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN ? "图案" : "数字"));
			if (startupImageEnabled) {
				bvLog("[BetterVia] 启动图已启用，在隐私锁覆盖层上显示启动图");
				final int finalPasswordType = passwordType;
				showStartupImageOnOverlay(activity, overlay, new Runnable() {
					@Override
					public void run() {
						bvLog("[BetterVia] 启动图显示结束，显示密码验证对话框");
						showPasswordVerifyDialog(activity, ctx, finalPasswordType);
					}
				});
			} else {
				bvLog("[BetterVia] 启动图未启用，先执行音乐和文字，然后显示密码验证对话框");
				final int finalPasswordType = passwordType;
				StartupExecutionHelper.executeStartupWithoutImage(activity, new Runnable() {
					@Override
					public void run() {
						bvLog("[BetterVia] 启动音乐和文字执行完成，显示密码验证对话框");
						showPasswordVerifyDialog(activity, ctx, finalPasswordType);
					}
				});
			}
			bvLog("[BetterVia] 隐私锁启动覆盖层已显示");
		} catch (Throwable t) {
			bvLog("[BetterVia] 显示隐私锁启动覆盖层失败: " + t);
			privacyLockVerified = true;
			removePrivacyLockOverlay(activity);
		}
	}
	private void showStartupImageOnOverlay(final Activity activity, final FrameLayout overlay,
			final Runnable onComplete) {
		try {
			final String imagePath = StartupExecutionHelper.getStartupImagePath(activity);
			if (imagePath == null || imagePath.isEmpty()) {
				bvLog("[BetterVia] 启动图路径为空，直接执行完成回调");
				if (onComplete != null)
					onComplete.run();
				return;
			}
			bvLog("[BetterVia] 在覆盖层上显示启动图: " + imagePath);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (activity.isFinishing() || activity.isDestroyed())
						return;
					final ImageView imageView = new ImageView(activity);
					imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
					imageView.setBackgroundColor(Color.BLACK); 
					try {
						File imageFile = new File(imagePath);
						if (!imageFile.exists()) {
							bvLog("[BetterVia] 启动图文件不存在: " + imagePath);
							if (onComplete != null)
								onComplete.run();
							return;
						}
						Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
							bvLog("[BetterVia] 启动图加载成功: " + imagePath);
						} else {
							bvLog("[BetterVia] 启动图解码失败: " + imagePath);
							if (onComplete != null)
								onComplete.run();
							return;
						}
					} catch (Exception e) {
						bvLog("[BetterVia] 加载启动图失败: " + e);
						if (onComplete != null)
							onComplete.run();
						return;
					}
					FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					imageView.setLayoutParams(imgParams);
					overlay.addView(imageView, imgParams);
					AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
					fadeIn.setDuration(300);
					imageView.startAnimation(fadeIn);
					bvLog("[BetterVia] 启动图已添加到覆盖层，同时执行启动音乐和文字");
					StartupExecutionHelper.executeStartupWithoutImage(activity, new Runnable() {
						@Override
						public void run() {
							bvLog("[BetterVia] 启动音乐和文字执行完成");
						}
					});
					int duration = StartupExecutionHelper.getStartupImageDuration(activity) * 1000;
					imageView.postDelayed(new Runnable() {
						@Override
						public void run() {
							AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
							fadeOut.setDuration(300);
							imageView.startAnimation(fadeOut);
							imageView.postDelayed(new Runnable() {
								@Override
								public void run() {
									overlay.removeView(imageView);
									bvLog("[BetterVia] 启动图已从覆盖层移除");
									if (onComplete != null)
										onComplete.run();
								}
							}, 300);
						}
					}, duration);
				}
			});
		} catch (Throwable t) {
			bvLog("[BetterVia] 显示启动图失败: " + t);
			if (onComplete != null)
				onComplete.run();
		}
	}
	private void showPasswordVerifyDialog(final Activity activity, final Context ctx, final int passwordType) {
		try {
			bvLog("[BetterVia] 开始创建PasswordManager，使用activity context");
			final PasswordManager passwordManager = new PasswordManager(activity);
			passwordManager.setVerifyMode(true); 
			passwordManager.setListener(new PasswordManager.PasswordListener() {
				@Override
				public void onPasswordSet() {
				}
				@Override
				public void onPasswordReset() {
				}
				@Override
				public void onVerifySuccess() {
					bvLog("[BetterVia] 密码验证成功");
					privacyLockVerified = true;
					removePrivacyLockOverlay(activity);
					bvLog("[BetterVia] 密码验证成功，启动流程已完成");
				}
				@Override
				public void onCancelled() {
					bvLog("[BetterVia] 用户取消验证，关闭Via应用");
					activity.finish();
					System.exit(0);
				}
			});
			if (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
				bvLog("[BetterVia] 调用showVerifyPasswordDialog");
				passwordManager.showVerifyPasswordDialog(
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_startup_verify_hint"));
			} else {
				bvLog("[BetterVia] 调用showVerifyPinPasswordDialog");
				passwordManager.showVerifyPinPasswordDialog(
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_startup_verify_hint"));
			}
			bvLog("[BetterVia] 验证对话框显示方法已调用");
		} catch (Throwable t) {
			bvLog("[BetterVia] 显示验证对话框失败: " + t);
			t.printStackTrace();
			privacyLockVerified = true;
			removePrivacyLockOverlay(activity);
		}
	}
	private void removePrivacyLockOverlay(final Activity activity) {
		try {
			if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (privacyLockOverlayView != null) {
							ViewParent parent = privacyLockOverlayView.getParent();
							if (parent instanceof ViewGroup) {
								((ViewGroup) parent).removeView(privacyLockOverlayView);
							}
							privacyLockOverlayView = null;
							bvLog("[BetterVia] 隐私锁启动覆盖层已移除");
						}
					}
				});
			}
		} catch (Throwable t) {
			bvLog("[BetterVia] 移除隐私锁启动覆盖层失败: " + t);
		}
	}
	private boolean verifyPatternPassword(Context ctx, List<Integer> inputPattern, String storedHash) {
		try {
			String patternStr = patternListToString(inputPattern);
			SecurePasswordHelper helper = new SecurePasswordHelper(ctx);
			return helper.verify(patternStr, storedHash);
		} catch (Exception e) {
			return false;
		}
	}
	private boolean verifyPinPassword(Context ctx, String inputPassword, String storedHash) {
		try {
			SecurePasswordHelper helper = new SecurePasswordHelper(ctx);
			return helper.verify(inputPassword, storedHash);
		} catch (Exception e) {
			return false;
		}
	}
	private void setPrivacyLockPageAccessHook(final Context ctx, final ClassLoader cl) {
		try {
			final boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
			final boolean applyHistory = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, false);
			final boolean applyBookmarks = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, false);
			final boolean applyOffline = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, false);
			final boolean applyComprehensive = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, false);
			if (!privacyLockEnabled || (!applyHistory && !applyBookmarks && !applyOffline && !applyComprehensive)) {
				bvLog("[BetterVia] 隐私锁页面访问拦截未启用");
				return;
			}
			SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
			boolean passwordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
			if (!passwordSet) {
				bvLog("[BetterVia] 隐私锁未设置密码，跳过页面访问拦截");
				return;
			}
			bvLog("[BetterVia] 开始设置隐私锁页面访问拦截...");
			try {
				String privacyLockVerify1Class = ViaClassMapping
						.getClassName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_1, ctx);
				String privacyLockVerify1Method = ViaClassMapping
						.getMethodName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_1, ctx);
				XposedHelpers.findAndHookMethod(privacyLockVerify1Class, cl, privacyLockVerify1Method, int.class,
						String.class, new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable {
								String result = (String) param.getResult();
								if (privacyLockPageUsed) {
									return;
								}
								if (result == null || result.isEmpty()) {
									return;
								}
								if (applyHistory && result.contains("history.html")) {
									bvLog("[BetterVia] [B方法] 检测到访问历史记录页面(history.html)，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
								if (applyBookmarks && result.contains("bookmarks.html")) {
									bvLog("[BetterVia] [B方法] 检测到访问书签页面(bookmarks.html)，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
							}
						});
				bvLog("[BetterVia] ✓ B方法Hook已注册");
			} catch (Throwable t) {
				bvLog("[BetterVia] Hook B方法失败: " + t);
			}
			try {
				String privacyLockVerify2Class = ViaClassMapping
						.getClassName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_2, ctx);
				String privacyLockVerify2Method = ViaClassMapping
						.getMethodName(ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_2, ctx);
				XposedHelpers.findAndHookMethod(privacyLockVerify2Class, cl, privacyLockVerify2Method, int.class,
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
								int pageType = (Integer) param.args[0];
								if (privacyLockPageUsed) {
									return;
								}
								if (applyComprehensive && (pageType == 2 || pageType == 3 || pageType == 13)) {
									bvLog("[BetterVia] [f方法] 检测到访问综合界面(参数=" + pageType + ")，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
								if (applyHistory && pageType == 3) {
									bvLog("[BetterVia] [f方法] 检测到访问历史记录页面(参数=3)，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
								if (applyBookmarks && pageType == 2) {
									bvLog("[BetterVia] [f方法] 检测到访问书签页面(参数=2)，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
								if (applyOffline && pageType == 13) {
									bvLog("[BetterVia] [f方法] 检测到访问离线页面(参数=13)，执行拦截");
									showPrivacyLockPageVerifyOverlay(ctx);
									return;
								}
							}
						});
				bvLog("[BetterVia] ✓ f方法Hook已注册");
			} catch (Throwable t) {
				bvLog("[BetterVia] Hook f方法失败: " + t);
			}
			bvLog("[BetterVia] ✓ 隐私锁页面访问拦截已启用");
		} catch (Throwable t) {
			bvLog("[BetterVia] 设置隐私锁页面访问拦截失败: " + t);
		}
	}
	private void showPrivacyLockPageVerifyOverlay(final Context ctx) {
		final Activity activity = getActivityFrom(ctx);
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}
		bvLog("[BetterVia] 准备显示页面访问密码验证");
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (activity.isFinishing() || activity.isDestroyed()) {
					return;
				}
				if (!privacyLockPageUsed) {
					showPrivacyLockPageVerifyOverlayInternal(activity, ctx);
				}
			}
		});
	}
	private void showPrivacyLockPageVerifyOverlayInternal(final Activity activity, final Context ctx) {
		try {
			if (privacyLockOverlayView != null) {
				return;
			}
			bvLog("[BetterVia] 创建页面访问验证覆盖层");
			int overlayColor = getBgColor(activity);
			final FrameLayout overlay = new FrameLayout(activity);
			overlay.setBackgroundColor(overlayColor);
			overlay.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			overlay.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			try {
				activity.getWindow().addContentView(overlay, new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				privacyLockOverlayView = overlay;
				bvLog("[BetterVia] 页面访问验证覆盖层已添加到DecorView");
			} catch (Exception e) {
				bvLog("[BetterVia] 添加覆盖层失败: " + e);
				privacyLockPageUsed = true;
				return;
			}
			SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
			int passwordType = -1;
			String patternPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
			if (patternPassword != null && !patternPassword.isEmpty()) {
				passwordType = PasswordManager.PASSWORD_TYPE_PATTERN;
			}
			String pinPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
			if (pinPassword != null && !pinPassword.isEmpty()) {
				passwordType = PasswordManager.PASSWORD_TYPE_PIN;
			}
			if (passwordType == -1) {
				bvLog("[BetterVia] 未找到密码，移除覆盖层");
				privacyLockPageUsed = true;
				removePrivacyLockOverlay(activity);
				return;
			}
			bvLog("[BetterVia] 准备显示验证对话框，密码类型: "
					+ (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN ? "图案" : "数字"));
			final int finalPasswordType = passwordType;
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					if (activity.isFinishing() || activity.isDestroyed()) {
						bvLog("[BetterVia] Activity已销毁，取消显示验证对话框");
						return;
					}
					bvLog("[BetterVia] 开始创建PasswordManager，使用activity context");
					try {
						final PasswordManager passwordManager = new PasswordManager(activity);
						passwordManager.setVerifyMode(true); 
						passwordManager.setListener(new PasswordManager.PasswordListener() {
							@Override
							public void onPasswordSet() {
							}
							@Override
							public void onPasswordReset() {
							}
							@Override
							public void onVerifySuccess() {
								bvLog("[BetterVia] 页面访问密码验证成功");
								privacyLockPageUsed = false;
								removePrivacyLockOverlay(activity);
							}
							@Override
							public void onCancelled() {
								bvLog("[BetterVia] 用户取消验证，返回上页");
								privacyLockPageUsed = false;
								removePrivacyLockOverlay(activity);
								activity.onBackPressed();
							}
						});
						if (finalPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
							bvLog("[BetterVia] 调用showVerifyPasswordDialog");
							passwordManager.showVerifyPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_startup_verify_hint"));
						} else {
							bvLog("[BetterVia] 调用showVerifyPinPasswordDialog");
							passwordManager.showVerifyPinPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_startup_verify_hint"));
						}
						bvLog("[BetterVia] 验证对话框显示方法已调用");
					} catch (Throwable t) {
						bvLog("[BetterVia] 显示验证对话框失败: " + t);
						t.printStackTrace();
						privacyLockPageUsed = true;
						removePrivacyLockOverlay(activity);
					}
				}
			}, 300); 
			bvLog("[BetterVia] 页面访问验证覆盖层已显示，等待验证对话框");
		} catch (Throwable t) {
			bvLog("[BetterVia] 显示页面访问验证覆盖层失败: " + t);
			privacyLockPageUsed = true;
			removePrivacyLockOverlay(activity);
		}
	}
	private String patternListToString(List<Integer> pattern) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pattern.size(); i++) {
			sb.append(pattern.get(i));
			if (i < pattern.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	private void addCookieManagementItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_management_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_management_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCookieManagementDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_management_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showCookieManagementDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
				dialog.setCancelable(true);
				LinearLayout rootLayout = new LinearLayout(act);
				rootLayout.setOrientation(LinearLayout.VERTICAL);
				rootLayout.setBackgroundColor(getBgColor(ctx));
				RelativeLayout titleBar = new RelativeLayout(act);
				titleBar.setBackgroundColor(getItemBgColor(ctx));
				titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
				titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				ImageButton backButton = new ImageButton(act);
				backButton.setImageResource(android.R.drawable.ic_menu_revert);
				backButton.setBackgroundResource(android.R.color.transparent);
				backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				backButton.setColorFilter(getTextColor(ctx));
				RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
				backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(backButton, backButtonLp);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_dialog_title"));
				title.setTextColor(getTextColor(ctx));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				title.setTypeface(null, Typeface.BOLD);
				RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.addRule(RelativeLayout.CENTER_IN_PARENT);
				titleBar.addView(title, titleLp);
				ImageButton refreshButton = new ImageButton(act);
				refreshButton.setImageResource(android.R.drawable.ic_menu_rotate);
				refreshButton.setBackgroundResource(android.R.color.transparent);
				refreshButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				refreshButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				refreshButton.setColorFilter(getTextColor(ctx));
				RelativeLayout.LayoutParams refreshButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
				refreshButtonLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				refreshButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(refreshButton, refreshButtonLp);
				rootLayout.addView(titleBar);
				LinearLayout contentLayout = new LinearLayout(act);
				contentLayout.setOrientation(LinearLayout.VERTICAL);
				contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				contentLayout.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				LinearLayout searchBar = new LinearLayout(act);
				searchBar.setOrientation(LinearLayout.HORIZONTAL);
				searchBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				searchBar.setPadding(0, 0, 0, dp(act, 12));
				final EditText searchEdit = new EditText(act);
				searchEdit.setHint(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_search_hint"));
				searchEdit.setTextColor(getTextColor(ctx));
				searchEdit.setHintTextColor(getHintColor(ctx));
				searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				searchEdit.setBackground(getRoundBg(act, getEditBgColor(ctx), 8));
				searchEdit.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				searchLp.rightMargin = dp(act, 8);
				searchBar.addView(searchEdit, searchLp);
				Button searchButton = new Button(act);
				applyClickAnim(searchButton);
				searchButton.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_search_btn"));
				searchButton.setTextColor(getOkBtnTextColor(ctx));
				searchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				searchButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));
				searchButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				searchBar.addView(searchButton);
				contentLayout.addView(searchBar);
				FrameLayout listAndLoadingContainer = new FrameLayout(act);
				listAndLoadingContainer
						.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
				final LinearLayout loadingContainer = new LinearLayout(act);
				loadingContainer.setOrientation(LinearLayout.VERTICAL);
				loadingContainer.setGravity(Gravity.CENTER);
				loadingContainer.setPadding(0, dp(act, 48), 0, dp(act, 48));
				loadingContainer.setVisibility(View.VISIBLE);
				ProgressBar progressBar = new ProgressBar(act);
				progressBar.setIndeterminate(true);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
				}
				LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
				progressLp.gravity = Gravity.CENTER;
				progressLp.bottomMargin = dp(act, 16);
				loadingContainer.addView(progressBar, progressLp);
				TextView loadingText = new TextView(act);
				loadingText.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_loading"));
				loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				loadingText.setTextColor(getHintColor(ctx));
				loadingText.setGravity(Gravity.CENTER);
				loadingContainer.addView(loadingText);
				contentLayout.addView(loadingContainer);
				final ScrollView scrollView = new ScrollView(act);
				scrollView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				scrollView.setVisibility(View.GONE);
				final LinearLayout listContainer = new LinearLayout(act);
				listContainer.setOrientation(LinearLayout.VERTICAL);
				listContainer.setPadding(0, 0, 0, 0);
				scrollView.addView(listContainer);
				listAndLoadingContainer.addView(scrollView);
				contentLayout.addView(listAndLoadingContainer);
				LinearLayout buttonBar = new LinearLayout(act);
				buttonBar.setOrientation(LinearLayout.HORIZONTAL);
				buttonBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				buttonBar.setPadding(0, dp(act, 12), 0, 0);
				buttonBar.setGravity(Gravity.CENTER_VERTICAL); 
				final Button deleteButton = new Button(act);
				applyClickAnim(deleteButton);
				deleteButton.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_delete_selected"));
				deleteButton.setTextColor(Color.WHITE);
				deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				deleteButton.setBackground(getRoundBg(act, 0xFFE53935, 8));
				deleteButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				deleteButton.setEnabled(false);
				LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				deleteLp.rightMargin = dp(act, 6);
				deleteLp.gravity = Gravity.CENTER_VERTICAL; 
				buttonBar.addView(deleteButton, deleteLp);
				final Button selectAllButton = new Button(act);
				applyClickAnim(selectAllButton);
				selectAllButton.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_select_all"));
				selectAllButton.setTextColor(getTextColor(ctx));
				selectAllButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				selectAllButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
				selectAllButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				LinearLayout.LayoutParams selectAllLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				selectAllLp.leftMargin = dp(act, 6);
				selectAllLp.rightMargin = dp(act, 6);
				selectAllLp.gravity = Gravity.CENTER_VERTICAL; 
				buttonBar.addView(selectAllButton, selectAllLp);
				Button closeButton = new Button(act);
				applyClickAnim(closeButton);
				closeButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_close"));
				closeButton.setTextColor(getOkBtnTextColor(ctx));
				closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				closeButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));
				closeButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				closeLp.leftMargin = dp(act, 6);
				closeLp.gravity = Gravity.CENTER_VERTICAL; 
				buttonBar.addView(closeButton, closeLp);
				contentLayout.addView(buttonBar);
				rootLayout.addView(contentLayout);
				dialog.setContentView(rootLayout);
				final List<CookieItem>[] currentCookieList = new List[]{new ArrayList<CookieItem>()};
				final List<DomainItem>[] currentDomainList = new List[]{new ArrayList<DomainItem>()};
				final boolean[] isDomainView = {true};
				final boolean[] isAllSelected = {false};
				final LinearLayout switchBar = new LinearLayout(act);
				switchBar.setOrientation(LinearLayout.HORIZONTAL);
				switchBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				switchBar.setPadding(0, 0, 0, dp(act, 12));
				switchBar.setVisibility(View.GONE);
				final Button domainViewBtn = new Button(act);
				applyClickAnim(domainViewBtn);
				domainViewBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_view_domain"));
				domainViewBtn.setTextColor(getOkBtnTextColor(ctx));
				domainViewBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				domainViewBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
				domainViewBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				LinearLayout.LayoutParams domainViewLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				domainViewLp.rightMargin = dp(act, 4);
				switchBar.addView(domainViewBtn, domainViewLp);
				final Button listViewBtn = new Button(act);
				applyClickAnim(listViewBtn);
				listViewBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_view_list"));
				listViewBtn.setTextColor(getTextColor(ctx));
				listViewBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				listViewBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
				listViewBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				LinearLayout.LayoutParams listViewLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				listViewLp.leftMargin = dp(act, 4);
				switchBar.addView(listViewBtn, listViewLp);
				contentLayout.addView(switchBar, contentLayout.getChildCount() - 2);
				final FrameLayout switchLoadingFrame = new FrameLayout(act);
				FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				switchLoadingFrame.setLayoutParams(frameLp);
				switchLoadingFrame.setVisibility(View.GONE);
				final LinearLayout switchLoadingContainer = new LinearLayout(act);
				switchLoadingContainer.setOrientation(LinearLayout.VERTICAL);
				switchLoadingContainer.setGravity(Gravity.CENTER);
				switchLoadingContainer.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				FrameLayout.LayoutParams containerLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
				switchLoadingFrame.addView(switchLoadingContainer, containerLp);
				ProgressBar switchProgressBar = new ProgressBar(act);
				switchProgressBar.setIndeterminate(true);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					switchProgressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
				}
				LinearLayout.LayoutParams switchProgressLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
				switchProgressLp.gravity = Gravity.CENTER;
				switchProgressLp.bottomMargin = dp(act, 12);
				switchLoadingContainer.addView(switchProgressBar, switchProgressLp);
				TextView switchLoadingText = new TextView(act);
				switchLoadingText.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_view_switching"));
				switchLoadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				switchLoadingText.setTextColor(getHintColor(ctx));
				switchLoadingText.setGravity(Gravity.CENTER);
				switchLoadingText.setTypeface(null, Typeface.NORMAL);
				switchLoadingContainer.addView(switchLoadingText);
				listAndLoadingContainer.addView(switchLoadingFrame);
				new Thread(new Runnable() {
					@Override
					public void run() {
						final List<CookieItem> cookieItems = loadCookieData(ctx);
						final List<DomainItem> domainItems = loadDomainGroupedCookieData(ctx);
						currentCookieList[0] = cookieItems;
						currentDomainList[0] = domainItems;
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loadingContainer.setVisibility(View.GONE);
								switchBar.setVisibility(View.VISIBLE);
								scrollView.setVisibility(View.VISIBLE);
								populateDomainList(act, listContainer, domainItems, deleteButton, scrollView, ctx,
										domainItems);
							}
						});
					}
				}).start();
				domainViewBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDomainView[0]) {
							domainViewBtn.setEnabled(false);
							listViewBtn.setEnabled(false);
							switchLoadingFrame.setVisibility(View.VISIBLE);
							scrollView.setVisibility(View.GONE);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									act.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											isDomainView[0] = true;
											domainViewBtn.setTextColor(getOkBtnTextColor(ctx));
											domainViewBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
											listViewBtn.setTextColor(getTextColor(ctx));
											listViewBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
											switchLoadingFrame.setVisibility(View.GONE);
											scrollView.setVisibility(View.VISIBLE);
											listContainer.removeAllViews();
											populateDomainList(act, listContainer, currentDomainList[0], deleteButton,
													scrollView, ctx, currentDomainList[0]);
											domainViewBtn.setEnabled(true);
											listViewBtn.setEnabled(true);
										}
									});
								}
							}, 300);
						}
					}
				});
				listViewBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isDomainView[0]) {
							domainViewBtn.setEnabled(false);
							listViewBtn.setEnabled(false);
							switchLoadingFrame.setVisibility(View.VISIBLE);
							scrollView.setVisibility(View.GONE);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									act.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											isDomainView[0] = false;
											isAllSelected[0] = false;
											selectAllButton.setText(LocalizedStringProvider.getInstance().get(ctx,
													"cookie_manager_select_all"));
											listViewBtn.setTextColor(getOkBtnTextColor(ctx));
											listViewBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
											domainViewBtn.setTextColor(getTextColor(ctx));
											domainViewBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
											switchLoadingFrame.setVisibility(View.GONE);
											scrollView.setVisibility(View.VISIBLE);
											listContainer.removeAllViews();
											populateCookieList(act, listContainer, currentCookieList[0], deleteButton,
													scrollView);
											domainViewBtn.setEnabled(true);
											listViewBtn.setEnabled(true);
										}
									});
								}
							}, 300);
						}
					}
				});
				selectAllButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						isAllSelected[0] = !isAllSelected[0];
						if (isAllSelected[0]) {
							selectAllButton.setText(
									LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_unselect_all"));
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_selecting"));
							if (isDomainView[0]) {
								for (DomainItem item : currentDomainList[0]) {
									item.selected = true;
								}
							} else {
								for (CookieItem item : currentCookieList[0]) {
									item.selected = true;
								}
							}
							new Thread(new Runnable() {
								@Override
								public void run() {
									act.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											for (int i = 0; i < listContainer.getChildCount(); i++) {
												View child = listContainer.getChildAt(i);
												if (child instanceof LinearLayout) {
													LinearLayout itemLayout = (LinearLayout) child;
													View firstChild = itemLayout.getChildAt(0);
													if (firstChild instanceof LinearLayout) {
														CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild)
																.getChildAt(0);
														if (checkbox != null) {
															checkbox.setChecked(true);
														}
														if (itemLayout.getTag() instanceof CookieItem) {
															((CookieItem) itemLayout.getTag()).selected = true;
														} else if (itemLayout.getTag() instanceof DomainItem) {
															((DomainItem) itemLayout.getTag()).selected = true;
														}
													}
												}
											}
											deleteButton.setEnabled(true);
											deleteButton.setText(LocalizedStringProvider.getInstance().get(act,
													"cookie_manager_delete_selected"));
										}
									});
								}
							}).start();
						} else {
							selectAllButton.setText(
									LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_select_all"));
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_unselecting"));
							if (isDomainView[0]) {
								for (DomainItem item : currentDomainList[0]) {
									item.selected = false;
								}
							} else {
								for (CookieItem item : currentCookieList[0]) {
									item.selected = false;
								}
							}
							new Thread(new Runnable() {
								@Override
								public void run() {
									act.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											for (int i = 0; i < listContainer.getChildCount(); i++) {
												View child = listContainer.getChildAt(i);
												if (child instanceof LinearLayout) {
													LinearLayout itemLayout = (LinearLayout) child;
													View firstChild = itemLayout.getChildAt(0);
													if (firstChild instanceof LinearLayout) {
														CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild)
																.getChildAt(0);
														if (checkbox != null) {
															checkbox.setChecked(false);
														}
														if (itemLayout.getTag() instanceof CookieItem) {
															((CookieItem) itemLayout.getTag()).selected = false;
														} else if (itemLayout.getTag() instanceof DomainItem) {
															((DomainItem) itemLayout.getTag()).selected = false;
														}
													}
												}
											}
											deleteButton.setEnabled(false);
											deleteButton.setText(LocalizedStringProvider.getInstance().get(act,
													"cookie_manager_delete_selected"));
										}
									});
								}
							}).start();
						}
					}
				});
				backButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				refreshButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loadingContainer.setVisibility(View.VISIBLE);
						switchBar.setVisibility(View.GONE);
						scrollView.setVisibility(View.GONE);
						new Thread(new Runnable() {
							@Override
							public void run() {
								final List<CookieItem> refreshedCookieData = loadCookieData(ctx);
								final List<DomainItem> refreshedDomainData = loadDomainGroupedCookieData(ctx);
								currentCookieList[0] = refreshedCookieData;
								currentDomainList[0] = refreshedDomainData;
								act.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										loadingContainer.setVisibility(View.GONE);
										switchBar.setVisibility(View.VISIBLE);
										scrollView.setVisibility(View.VISIBLE);
										listContainer.removeAllViews();
										if (isDomainView[0]) {
											populateDomainList(act, listContainer, refreshedDomainData, deleteButton,
													scrollView, ctx, refreshedDomainData);
										} else {
											populateCookieList(act, listContainer, refreshedCookieData, deleteButton,
													scrollView);
										}
										jiguroMessageWithContext(act, LocalizedStringProvider.getInstance().get(ctx,
												"cookie_management_refreshed"));
									}
								});
							}
						}).start();
					}
				});
				searchButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final String query = searchEdit.getText().toString().trim().toLowerCase();
						if (query.isEmpty()) {
							if (isDomainView[0]) {
								populateDomainList(act, listContainer, currentDomainList[0], deleteButton, scrollView,
										ctx);
							} else {
								populateCookieList(act, listContainer, currentCookieList[0], deleteButton, scrollView);
							}
							return;
						}
						if (isDomainView[0]) {
							List<DomainItem> filteredDomainList = new ArrayList<DomainItem>();
							for (DomainItem domainItem : currentDomainList[0]) {
								boolean domainMatch = domainItem.domain.toLowerCase().contains(query);
								boolean cookieMatch = false;
								for (CookieItem cookie : domainItem.cookies) {
									if ((cookie.name != null && cookie.name.toLowerCase().contains(query))
											|| (cookie.value != null && cookie.value.toLowerCase().contains(query))) {
										cookieMatch = true;
										break;
									}
								}
								if (domainMatch || cookieMatch) {
									filteredDomainList.add(domainItem);
								}
							}
							populateDomainList(act, listContainer, filteredDomainList, deleteButton, scrollView, ctx,
									filteredDomainList);
							String resultMsg = String.format(
									LocalizedStringProvider.getInstance().get(act, "cookie_domain_search_result"),
									filteredDomainList.size());
							jiguroMessageWithContext(act, resultMsg);
						} else {
							List<CookieItem> filteredList = new ArrayList<CookieItem>();
							for (CookieItem item : currentCookieList[0]) {
								if ((item.host_key != null && item.host_key.toLowerCase().contains(query))
										|| (item.name != null && item.name.toLowerCase().contains(query))
										|| (item.value != null && item.value.toLowerCase().contains(query))) {
									filteredList.add(item);
								}
							}
							populateCookieList(act, listContainer, filteredList, deleteButton, scrollView);
							String resultMsg = String.format(
									LocalizedStringProvider.getInstance().get(act, "cookie_search_result"),
									filteredList.size());
							jiguroMessageWithContext(act, resultMsg);
						}
					}
				});
				deleteButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDeleteConfirmDialog(act, ctx, listContainer, deleteButton, scrollView, isDomainView[0]);
					}
				});
				closeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
	}
	private void showCookieDetailDialog(final Activity act, final CookieItem cookie) {
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle(LocalizedStringProvider.getInstance().get(act, "cookie_detail_dialog_title"));
		ScrollView scrollView = new ScrollView(act);
		LinearLayout layout = new LinearLayout(act);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
		TextView basicInfoTitle = new TextView(act);
		basicInfoTitle.setText(LocalizedStringProvider.getInstance().get(act, "cookie_detail_basic_info"));
		basicInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		basicInfoTitle.setTextColor(getTextColor(act));
		basicInfoTitle.setTypeface(null, Typeface.BOLD);
		basicInfoTitle.setPadding(0, 0, 0, dp(act, 12));
		layout.addView(basicInfoTitle);
		final EditText hostKeyEdit = addEditableField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_host_key"),
				cookie.host_key != null ? cookie.host_key : "");
		final EditText nameEdit = addEditableField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_name"),
				cookie.name != null ? cookie.name : "");
		final EditText valueEdit = addEditableField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_value"),
				cookie.value != null ? cookie.value : "");
		final EditText pathEdit = addEditableField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_path"),
				cookie.path != null ? cookie.path : "");
		TextView timeInfoTitle = new TextView(act);
		timeInfoTitle.setText(LocalizedStringProvider.getInstance().get(act, "cookie_detail_time_info"));
		timeInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		timeInfoTitle.setTextColor(getTextColor(act));
		timeInfoTitle.setTypeface(null, Typeface.BOLD);
		timeInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(timeInfoTitle);
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_creation_time"),
				cookie.creation_utc > 0
						? formatTimestamp(cookie.creation_utc)
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown"));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_last_access"),
				cookie.last_access_utc > 0
						? formatTimestamp(cookie.last_access_utc)
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown"));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_expires"),
				cookie.expires_utc > 0
						? formatTimestamp(cookie.expires_utc)
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_session"));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_last_update"),
				cookie.last_update_utc > 0
						? formatTimestamp(cookie.last_update_utc)
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown"));
		TextView securityInfoTitle = new TextView(act);
		securityInfoTitle.setText(LocalizedStringProvider.getInstance().get(act, "cookie_detail_security_info"));
		securityInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		securityInfoTitle.setTextColor(getTextColor(act));
		securityInfoTitle.setTypeface(null, Typeface.BOLD);
		securityInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(securityInfoTitle);
		final CheckBox secureCheckbox = addCheckboxField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_secure"), cookie.is_secure);
		final CheckBox httpOnlyCheckbox = addCheckboxField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_httponly"), cookie.is_httponly);
		final CheckBox persistentCheckbox = addCheckboxField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_persistent"), cookie.is_persistent);
		final CheckBox hasExpiresCheckbox = addCheckboxField(layout, act,
				LocalizedStringProvider.getInstance().get(act, "cookie_field_has_expires"), cookie.has_expires);
		TextView advancedInfoTitle = new TextView(act);
		advancedInfoTitle.setText(LocalizedStringProvider.getInstance().get(act, "cookie_detail_advanced_info"));
		advancedInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		advancedInfoTitle.setTextColor(getTextColor(act));
		advancedInfoTitle.setTypeface(null, Typeface.BOLD);
		advancedInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(advancedInfoTitle);
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_priority"),
				String.valueOf(cookie.priority));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_samesite"),
				getSameSiteText(act, cookie.samesite));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_source_port"),
				cookie.source_port > 0
						? String.valueOf(cookie.source_port)
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_default"));
		addReadOnlyField(layout, act, LocalizedStringProvider.getInstance().get(act, "cookie_field_source_type"),
				getSourceTypeText(act, cookie.source_type));
		scrollView.addView(layout);
		builder.setView(scrollView);
		builder.setPositiveButton(LocalizedStringProvider.getInstance().get(act, "dialog_ok"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cookie.host_key = hostKeyEdit.getText().toString();
						cookie.name = nameEdit.getText().toString();
						cookie.value = valueEdit.getText().toString();
						cookie.path = pathEdit.getText().toString();
						cookie.is_secure = secureCheckbox.isChecked();
						cookie.is_httponly = httpOnlyCheckbox.isChecked();
						cookie.is_persistent = persistentCheckbox.isChecked();
						cookie.has_expires = hasExpiresCheckbox.isChecked();
						cookie.last_update_utc = System.currentTimeMillis() / 1000;
						updateCookieInDatabase(act, cookie);
						jiguroMessageWithContext(act,
								LocalizedStringProvider.getInstance().get(act, "cookie_save_success"));
					}
				});
		builder.setNeutralButton(LocalizedStringProvider.getInstance().get(act, "dialog_cancel"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		applyAlertDialogTheme(act, act, dialog);
		dialog.show();
	}
	private void removeDeletedCookieFromList(final Activity act, final LinearLayout listContainer,
			final Button deleteButton, final ScrollView scrollView, final CookieItem deletedCookie) {
		for (int i = 0; i < listContainer.getChildCount(); i++) {
			View child = listContainer.getChildAt(i);
			if (child.getTag() instanceof CookieItem) {
				CookieItem item = (CookieItem) child.getTag();
				if (itemMatchesDeleted(item, deletedCookie)) {
					listContainer.removeViewAt(i);
					updateDeleteButtonState(act, listContainer, deleteButton);
					bvLog("[BetterVia] 已从列表移除被删除的Cookie: " + item.name);
					break;
				}
			}
		}
		if (listContainer.getChildCount() == 0) {
			showEmptyCookieListState(act, listContainer);
		}
	}
	private boolean itemMatchesDeleted(CookieItem item, CookieItem deletedCookie) {
		return (item.creation_utc == deletedCookie.creation_utc && safeEquals(item.host_key, deletedCookie.host_key)
				&& safeEquals(item.name, deletedCookie.name));
	}
	private boolean safeEquals(String str1, String str2) {
		if (str1 == null && str2 == null)
			return true;
		if (str1 == null || str2 == null)
			return false;
		return str1.equals(str2);
	}
	private void updateDeleteButtonState(Context ctx, LinearLayout listContainer, Button deleteButton) {
		int selectedCount = 0;
		for (int i = 0; i < listContainer.getChildCount(); i++) {
			View child = listContainer.getChildAt(i);
			if (child.getTag() instanceof CookieItem) {
				CookieItem item = (CookieItem) child.getTag();
				if (item.selected) {
					selectedCount++;
				}
			}
		}
		deleteButton.setEnabled(selectedCount > 0);
		deleteButton.setText(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_delete_selected"));
	}
	private void showEmptyCookieListState(final Activity act, final LinearLayout listContainer) {
		listContainer.removeAllViews();
		TextView emptyText = new TextView(act);
		emptyText.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_empty"));
		emptyText.setTextColor(getHintColor(act));
		emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		emptyText.setGravity(Gravity.CENTER);
		emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
		listContainer.addView(emptyText);
	}
	private LinearLayout findCookieListContainer(View view) {
		if (view instanceof LinearLayout) {
			LinearLayout layout = (LinearLayout) view;
			for (int i = 0; i < layout.getChildCount(); i++) {
				View child = layout.getChildAt(i);
				if (child.getTag() instanceof CookieItem) {
					return layout;
				}
			}
		}
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				LinearLayout result = findCookieListContainer(viewGroup.getChildAt(i));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	private ScrollView findCookieListScrollView(View view) {
		if (view instanceof ScrollView) {
			ScrollView scrollView = (ScrollView) view;
			if (scrollView.getChildCount() > 0) {
				View child = scrollView.getChildAt(0);
				if (child instanceof LinearLayout) {
					LinearLayout container = (LinearLayout) child;
					if (container.getChildCount() > 0) {
						View firstChild = container.getChildAt(0);
						if (firstChild instanceof LinearLayout && firstChild.getTag() instanceof CookieItem) {
							return scrollView;
						}
					}
				}
			}
			return null;
		}
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				ScrollView result = findCookieListScrollView(viewGroup.getChildAt(i));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	private Button findDeleteButton(Context ctx, View view) {
		if (view instanceof Button) {
			Button button = (Button) view;
			String buttonText = button.getText().toString();
			if (buttonText
					.startsWith(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_delete_selected"))) {
				return button;
			}
			return null;
		}
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				Button result = findDeleteButton(ctx, viewGroup.getChildAt(i));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	private String formatTimestamp(long timestamp) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp * 1000));
		} catch (Exception e) {
			return "Time format error";
		}
	}
	private String getSameSiteText(Context ctx, int samesite) {
		switch (samesite) {
			case 0 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_samesite_none");
			case 1 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_samesite_lax");
			case 2 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_samesite_strict");
			default :
				return String.format(LocalizedStringProvider.getInstance().get(ctx, "cookie_samesite_unknown"),
						samesite);
		}
	}
	private String getSourceTypeText(Context ctx, int sourceType) {
		switch (sourceType) {
			case 0 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_source_type_none");
			case 1 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_source_type_http");
			case 2 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_source_type_https");
			case 3 :
				return LocalizedStringProvider.getInstance().get(ctx, "cookie_source_type_file");
			default :
				return String.format(LocalizedStringProvider.getInstance().get(ctx, "cookie_source_type_unknown"),
						sourceType);
		}
	}
	private EditText addEditableField(LinearLayout parent, Context ctx, String label, String value) {
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 12));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(getTextColor(ctx));
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		final EditText editText = new EditText(ctx);
		editText.setText(value != null ? value : "");
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		editText.setTextColor(getTextColor(ctx));
		editText.setBackground(getRoundBg(ctx, getEditBgColor(ctx), 4));
		editText.setPadding(dp(ctx, 8), dp(ctx, 6), dp(ctx, 8), dp(ctx, 6));
		editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(editText);
		parent.addView(rowLayout);
		return editText;
	}
	private void addReadOnlyField(LinearLayout parent, Context ctx, String label, String value) {
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 8));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(getHintColor(ctx));
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		TextView valueView = new TextView(ctx);
		valueView.setText(value != null ? value : "N/A");
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(getHintColor(ctx));
		valueView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(valueView);
		parent.addView(rowLayout);
	}
	private CheckBox addCheckboxField(LinearLayout parent, Context ctx, String label, boolean checked) {
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 12));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(getTextColor(ctx));
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		final CheckBox checkBox = new CheckBox(ctx);
		checkBox.setChecked(checked);
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		checkBox.setTextColor(getTextColor(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			checkBox.setButtonTintList(colorStateList);
		}
		checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(checkBox);
		parent.addView(rowLayout);
		return checkBox;
	}
	private void updateCookieInDatabase(Context ctx, CookieItem cookie) {
		SQLiteDatabase db = null;
		try {
			String cookiePath = getCookieFilePath(ctx);
			db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READWRITE);
			ContentValues values = new ContentValues();
			values.put("host_key", cookie.host_key);
			values.put("name", cookie.name);
			values.put("value", cookie.value);
			values.put("path", cookie.path);
			values.put("is_secure", cookie.is_secure ? 1 : 0);
			values.put("is_httponly", cookie.is_httponly ? 1 : 0);
			values.put("is_persistent", cookie.is_persistent ? 1 : 0);
			values.put("last_update_utc", cookie.last_update_utc);
			String whereClause = "creation_utc = ? AND host_key = ? AND name = ?";
			String[] whereArgs = {String.valueOf(cookie.creation_utc), cookie.host_key, cookie.name};
			db.update(COOKIE_TABLE_NAME, values, whereClause, whereArgs);
		} catch (Exception e) {
			bvLog("[BetterVia] 更新Cookie失败: " + e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	private void populateCookieList(final Activity act, final LinearLayout container, List<CookieItem> cookieItems,
			final Button deleteButton, final ScrollView scrollView) {
		container.removeAllViews();
		if (cookieItems.isEmpty()) {
			TextView emptyText = new TextView(act);
			emptyText.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_empty"));
			emptyText.setTextColor(getHintColor(act));
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < cookieItems.size(); i++) {
			final CookieItem item = cookieItems.get(i);
			item.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, getItemBgColor(act), 6));
			itemLayout.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
			LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			itemLp.bottomMargin = dp(act, 8);
			container.addView(itemLayout, itemLp);
			itemLayout.setTag(item);
			LinearLayout firstRow = new LinearLayout(act);
			firstRow.setOrientation(LinearLayout.HORIZONTAL);
			firstRow.setGravity(Gravity.CENTER_VERTICAL);
			final CheckBox selectCheckbox = new CheckBox(act);
			selectCheckbox.setChecked(item.selected);
			selectCheckbox.setScaleX(0.8f);
			selectCheckbox.setScaleY(0.8f);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
						new int[]{-android.R.attr.state_checked}};
				int[] colors = new int[]{getSwitchOnColor(act), getSwitchOffColor(act)};
				ColorStateList colorStateList = new ColorStateList(states, colors);
				selectCheckbox.setButtonTintList(colorStateList);
			}
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			String domain = item.host_key != null && !item.host_key.isEmpty()
					? item.host_key
					: LocalizedStringProvider.getInstance().get(act, "cookie_unknown_domain");
			domainText.setText(domain);
			domainText.setTextColor(getTextColor(act));
			domainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			domainText.setTypeface(null, Typeface.BOLD);
			domainText.setEllipsize(TextUtils.TruncateAt.END);
			domainText.setSingleLine(true);
			domainText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
			firstRow.addView(domainText);
			itemLayout.addView(firstRow);
			LinearLayout secondRow = new LinearLayout(act);
			secondRow.setOrientation(LinearLayout.VERTICAL);
			secondRow.setPadding(dp(act, 24), dp(act, 4), 0, 0);
			TextView nameText = new TextView(act);
			String nameLabel = LocalizedStringProvider.getInstance().get(act, "cookie_field_name_label");
			String nameValue = item.name != null && !item.name.isEmpty()
					? item.name
					: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown");
			nameText.setText(nameLabel + nameValue);
			nameText.setTextColor(getHintColor(act));
			nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			secondRow.addView(nameText);
			TextView valueText = new TextView(act);
			String valueLabel = LocalizedStringProvider.getInstance().get(act, "cookie_field_value_label");
			String valueRaw = item.value != null && !item.value.isEmpty()
					? item.value
					: LocalizedStringProvider.getInstance().get(act, "cookie_no_value");
			String valueDisplay = valueRaw.length() > 30 ? valueRaw.substring(0, 30) + "..." : valueRaw;
			valueText.setText(valueLabel + valueDisplay);
			valueText.setTextColor(getHintColor(act));
			valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			valueText.setPadding(0, dp(act, 2), 0, 0);
			secondRow.addView(valueText);
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showCookieDetailDialog(act, item);
				}
			});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					item.selected = !item.selected;
					selectCheckbox.setChecked(item.selected);
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof CookieItem) {
							if (((CookieItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
					return true;
				}
			});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					item.selected = selectCheckbox.isChecked();
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof CookieItem) {
							if (((CookieItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
				}
			});
		}
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, 0);
			}
		});
	}
	private void populateDomainList(final Activity act, final LinearLayout container, List<DomainItem> domainItems,
			final Button deleteButton, final ScrollView scrollView, final Context ctx) {
		container.removeAllViews();
		if (domainItems.isEmpty()) {
			TextView emptyText = new TextView(act);
			emptyText.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_empty"));
			emptyText.setTextColor(getHintColor(act));
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < domainItems.size(); i++) {
			final DomainItem domainItem = domainItems.get(i);
			domainItem.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, getItemBgColor(act), 6));
			itemLayout.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
			LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			itemLp.bottomMargin = dp(act, 8);
			container.addView(itemLayout, itemLp);
			itemLayout.setTag(domainItem);
			LinearLayout firstRow = new LinearLayout(act);
			firstRow.setOrientation(LinearLayout.HORIZONTAL);
			firstRow.setGravity(Gravity.CENTER_VERTICAL);
			final CheckBox selectCheckbox = new CheckBox(act);
			selectCheckbox.setChecked(domainItem.selected);
			selectCheckbox.setScaleX(0.8f);
			selectCheckbox.setScaleY(0.8f);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
						new int[]{-android.R.attr.state_checked}};
				int[] colors = new int[]{getSwitchOnColor(act), getSwitchOffColor(act)};
				ColorStateList colorStateList = new ColorStateList(states, colors);
				selectCheckbox.setButtonTintList(colorStateList);
			}
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			domainText.setText(domainItem.domain);
			domainText.setTextColor(getTextColor(act));
			domainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			domainText.setTypeface(null, Typeface.BOLD);
			domainText.setEllipsize(TextUtils.TruncateAt.END);
			domainText.setSingleLine(true);
			domainText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
			firstRow.addView(domainText);
			TextView countText = new TextView(act);
			String countLabel = LocalizedStringProvider.getInstance().get(act, "cookie_domain_count_label");
			countText.setText(String.format(countLabel, domainItem.getCookieCount()));
			countText.setTextColor(getHintColor(act));
			countText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			countLp.leftMargin = dp(act, 8);
			firstRow.addView(countText, countLp);
			itemLayout.addView(firstRow);
			LinearLayout secondRow = new LinearLayout(act);
			secondRow.setOrientation(LinearLayout.VERTICAL);
			secondRow.setPadding(dp(act, 24), dp(act, 4), 0, 0);
			int previewCount = Math.min(3, domainItem.cookies.size());
			for (int j = 0; j < previewCount; j++) {
				CookieItem cookie = domainItem.cookies.get(j);
				TextView cookiePreview = new TextView(act);
				String cookieName = cookie.name != null && !cookie.name.isEmpty()
						? cookie.name
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown");
				String cookieValue = cookie.value != null && !cookie.value.isEmpty()
						? cookie.value
						: LocalizedStringProvider.getInstance().get(act, "cookie_no_value");
				String valueDisplay = cookieValue.length() > 20 ? cookieValue.substring(0, 20) + "..." : cookieValue;
				cookiePreview.setText("• " + cookieName + ": " + valueDisplay);
				cookiePreview.setTextColor(getHintColor(act));
				cookiePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
				secondRow.addView(cookiePreview);
			}
			if (domainItem.cookies.size() > 3) {
				TextView moreText = new TextView(act);
				int moreCount = domainItem.cookies.size() - 3;
				String moreLabel = LocalizedStringProvider.getInstance().get(act, "cookie_domain_more_label");
				moreText.setText(String.format(moreLabel, moreCount));
				moreText.setTextColor(getHintColor(act));
				moreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				moreText.setPadding(0, dp(act, 2), 0, 0);
				secondRow.addView(moreText);
			}
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDomainCookieList(act, domainItem, ctx);
				}
			});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					domainItem.selected = !domainItem.selected;
					selectCheckbox.setChecked(domainItem.selected);
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof DomainItem) {
							if (((DomainItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
					return true;
				}
			});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					domainItem.selected = selectCheckbox.isChecked();
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof DomainItem) {
							if (((DomainItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
				}
			});
		}
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, 0);
			}
		});
	}
	private void populateDomainList(final Activity act, final LinearLayout container, List<DomainItem> domainItems,
			final Button deleteButton, final ScrollView scrollView, final Context ctx,
			final List<DomainItem> masterDomainList) {
		container.removeAllViews();
		if (domainItems.isEmpty()) {
			TextView emptyText = new TextView(act);
			emptyText.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_empty"));
			emptyText.setTextColor(getHintColor(act));
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < domainItems.size(); i++) {
			final DomainItem domainItem = domainItems.get(i);
			domainItem.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, getItemBgColor(act), 6));
			itemLayout.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
			LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			itemLp.bottomMargin = dp(act, 8);
			container.addView(itemLayout, itemLp);
			itemLayout.setTag(domainItem);
			LinearLayout firstRow = new LinearLayout(act);
			firstRow.setOrientation(LinearLayout.HORIZONTAL);
			firstRow.setGravity(Gravity.CENTER_VERTICAL);
			final CheckBox selectCheckbox = new CheckBox(act);
			selectCheckbox.setChecked(domainItem.selected);
			selectCheckbox.setScaleX(0.8f);
			selectCheckbox.setScaleY(0.8f);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
						new int[]{-android.R.attr.state_checked}};
				int[] colors = new int[]{getSwitchOnColor(act), getSwitchOffColor(act)};
				ColorStateList colorStateList = new ColorStateList(states, colors);
				selectCheckbox.setButtonTintList(colorStateList);
			}
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			domainText.setText(domainItem.domain);
			domainText.setTextColor(getTextColor(act));
			domainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			domainText.setTypeface(null, Typeface.BOLD);
			domainText.setEllipsize(TextUtils.TruncateAt.END);
			domainText.setSingleLine(true);
			domainText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
			firstRow.addView(domainText);
			TextView countText = new TextView(act);
			String countLabel = LocalizedStringProvider.getInstance().get(act, "cookie_domain_count_label");
			countText.setText(String.format(countLabel, domainItem.getCookieCount()));
			countText.setTextColor(getHintColor(act));
			countText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			countLp.leftMargin = dp(act, 8);
			firstRow.addView(countText, countLp);
			itemLayout.addView(firstRow);
			LinearLayout secondRow = new LinearLayout(act);
			secondRow.setOrientation(LinearLayout.VERTICAL);
			secondRow.setPadding(dp(act, 24), dp(act, 4), 0, 0);
			int previewCount = Math.min(3, domainItem.cookies.size());
			for (int j = 0; j < previewCount; j++) {
				CookieItem cookie = domainItem.cookies.get(j);
				TextView cookiePreview = new TextView(act);
				String cookieName = cookie.name != null && !cookie.name.isEmpty()
						? cookie.name
						: LocalizedStringProvider.getInstance().get(act, "cookie_field_unknown");
				String cookieValue = cookie.value != null && !cookie.value.isEmpty()
						? cookie.value
						: LocalizedStringProvider.getInstance().get(act, "cookie_no_value");
				String valueDisplay = cookieValue.length() > 20 ? cookieValue.substring(0, 20) + "..." : cookieValue;
				cookiePreview.setText("• " + cookieName + ": " + valueDisplay);
				cookiePreview.setTextColor(getHintColor(act));
				cookiePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
				secondRow.addView(cookiePreview);
			}
			if (domainItem.cookies.size() > 3) {
				TextView moreText = new TextView(act);
				int moreCount = domainItem.cookies.size() - 3;
				String moreLabel = LocalizedStringProvider.getInstance().get(act, "cookie_domain_more_label");
				moreText.setText(String.format(moreLabel, moreCount));
				moreText.setTextColor(getHintColor(act));
				moreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				moreText.setPadding(0, dp(act, 2), 0, 0);
				secondRow.addView(moreText);
			}
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDomainCookieList(act, domainItem, ctx, masterDomainList);
				}
			});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					domainItem.selected = !domainItem.selected;
					selectCheckbox.setChecked(domainItem.selected);
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof DomainItem) {
							if (((DomainItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
					return true;
				}
			});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					domainItem.selected = selectCheckbox.isChecked();
					int count = 0;
					for (int j = 0; j < container.getChildCount(); j++) {
						View child = container.getChildAt(j);
						if (child.getTag() instanceof DomainItem) {
							if (((DomainItem) child.getTag()).selected) {
								count++;
							}
						}
					}
					deleteButton.setEnabled(count > 0);
					deleteButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
				}
			});
		}
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, 0);
			}
		});
	}
	private void showDomainCookieList(final Activity act, final DomainItem domainItem, final Context ctx) {
		showDomainCookieList(act, domainItem, ctx, null);
	}
	private void showDomainCookieList(final Activity act, final DomainItem domainItem, final Context ctx,
			final List<DomainItem> masterDomainList) {
		if (act.isFinishing() || act.isDestroyed())
			return;
		final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
		dialog.setCancelable(true);
		LinearLayout rootLayout = new LinearLayout(act);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setBackgroundColor(getBgColor(act));
		RelativeLayout titleBar = new RelativeLayout(act);
		titleBar.setBackgroundColor(getItemBgColor(act));
		titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
		titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		ImageButton backButton = new ImageButton(act);
		backButton.setImageResource(android.R.drawable.ic_menu_revert);
		backButton.setBackgroundResource(android.R.color.transparent);
		backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		backButton.setColorFilter(getTextColor(act));
		RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
		backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
		titleBar.addView(backButton, backButtonLp);
		TextView title = new TextView(act);
		title.setText(domainItem.domain);
		title.setTextColor(getTextColor(act));
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		title.setTypeface(null, Typeface.BOLD);
		title.setEllipsize(TextUtils.TruncateAt.END);
		title.setSingleLine(true);
		RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLp.addRule(RelativeLayout.CENTER_IN_PARENT);
		titleLp.leftMargin = dp(act, 60);
		titleLp.rightMargin = dp(act, 60);
		titleBar.addView(title, titleLp);
		rootLayout.addView(titleBar);
		LinearLayout contentLayout = new LinearLayout(act);
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		contentLayout.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		LinearLayout domainInfoLayout = new LinearLayout(act);
		domainInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
		domainInfoLayout.setGravity(Gravity.CENTER_VERTICAL);
		domainInfoLayout.setBackground(getRoundBg(act, getEditBgColor(act), 6));
		domainInfoLayout.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
		domainInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		TextView domainInfoText = new TextView(act);
		String cookieCountLabel = LocalizedStringProvider.getInstance().get(act, "cookie_domain_total_count");
		domainInfoText.setText(String.format(cookieCountLabel, domainItem.getCookieCount()));
		domainInfoText.setTextColor(getHintColor(act));
		domainInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		domainInfoLayout.addView(domainInfoText);
		contentLayout.addView(domainInfoLayout);
		final ScrollView scrollView = new ScrollView(act);
		scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
		final LinearLayout listContainer = new LinearLayout(act);
		listContainer.setOrientation(LinearLayout.VERTICAL);
		listContainer.setPadding(0, dp(act, 12), 0, 0);
		scrollView.addView(listContainer);
		contentLayout.addView(scrollView);
		LinearLayout buttonBar = new LinearLayout(act);
		buttonBar.setOrientation(LinearLayout.HORIZONTAL);
		buttonBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		buttonBar.setPadding(0, dp(act, 12), 0, 0);
		buttonBar.setGravity(Gravity.CENTER_VERTICAL); 
		final Button deleteDomainButton = new Button(act);
		applyClickAnim(deleteDomainButton);
		deleteDomainButton.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_selected"));
		deleteDomainButton.setTextColor(Color.WHITE);
		deleteDomainButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		deleteDomainButton.setBackground(getRoundBg(act, 0xFFE53935, 8));
		deleteDomainButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		deleteDomainButton.setEnabled(false);
		LinearLayout.LayoutParams deleteDomainLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
				1.0f);
		deleteDomainLp.rightMargin = dp(act, 6);
		deleteDomainLp.gravity = Gravity.CENTER_VERTICAL; 
		buttonBar.addView(deleteDomainButton, deleteDomainLp);
		final Button selectAllDomainButton = new Button(act);
		applyClickAnim(selectAllDomainButton);
		selectAllDomainButton.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_select_all"));
		selectAllDomainButton.setTextColor(getTextColor(act));
		selectAllDomainButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		selectAllDomainButton.setBackground(getRoundBg(act, getBtnBgColor(act), 8));
		selectAllDomainButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		LinearLayout.LayoutParams selectAllDomainLp = new LinearLayout.LayoutParams(0,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
		selectAllDomainLp.leftMargin = dp(act, 6);
		selectAllDomainLp.rightMargin = dp(act, 6);
		selectAllDomainLp.gravity = Gravity.CENTER_VERTICAL; 
		buttonBar.addView(selectAllDomainButton, selectAllDomainLp);
		Button closeButton = new Button(act);
		applyClickAnim(closeButton);
		closeButton.setText(LocalizedStringProvider.getInstance().get(act, "dialog_close"));
		closeButton.setTextColor(getOkBtnTextColor(act));
		closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		closeButton.setBackground(getRoundBg(act, getOkBtnBgColor(act), 8));
		closeButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
		closeLp.leftMargin = dp(act, 6);
		closeLp.gravity = Gravity.CENTER_VERTICAL; 
		buttonBar.addView(closeButton, closeLp);
		contentLayout.addView(buttonBar);
		rootLayout.addView(contentLayout);
		dialog.setContentView(rootLayout);
		final boolean[] isDomainAllSelected = {false};
		populateCookieList(act, listContainer, domainItem.cookies, deleteDomainButton, scrollView);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		deleteDomainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle(LocalizedStringProvider.getInstance().get(act, "cookie_delete_confirm_title"));
				builder.setMessage(LocalizedStringProvider.getInstance().get(act, "cookie_delete_confirm_msg"));
				builder.setPositiveButton(LocalizedStringProvider.getInstance().get(act, "cookie_manager_delete_btn"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, int which) {
								deleteSelectedCookies(act, listContainer, deleteDomainButton, scrollView,
										new Runnable() {
											@Override
											public void run() {
												List<CookieItem> refreshedCookies = new ArrayList<>();
												SQLiteDatabase db = null;
												Cursor cursor = null;
												try {
													String cookiePath = getCookieFilePath(act);
													db = SQLiteDatabase.openDatabase(cookiePath, null,
															SQLiteDatabase.OPEN_READONLY);
													String selection = "host_key = ?";
													String[] selectionArgs = {domainItem.domain};
													cursor = db.query(COOKIE_TABLE_NAME, null, selection, selectionArgs,
															null, null, "name");
													if (cursor != null && cursor.moveToFirst()) {
														do {
															CookieItem item = new CookieItem();
															item.creation_utc = getLongSafe(cursor, "creation_utc");
															item.host_key = getStringSafe(cursor, "host_key");
															item.name = getStringSafe(cursor, "name");
															item.value = getStringSafe(cursor, "value");
															item.path = getStringSafe(cursor, "path");
															item.expires_utc = getLongSafe(cursor, "expires_utc");
															item.is_secure = getIntSafe(cursor, "is_secure") == 1;
															item.is_httponly = getIntSafe(cursor, "is_httponly") == 1;
															item.last_access_utc = getLongSafe(cursor,
																	"last_access_utc");
															item.is_persistent = getIntSafe(cursor,
																	"is_persistent") == 1;
															item.selected = false;
															refreshedCookies.add(item);
														} while (cursor.moveToNext());
													}
												} catch (Exception e) {
													bvLog("[BetterVia] 重新加载Cookie数据失败: " + e);
												} finally {
													if (cursor != null) {
														cursor.close();
													}
													if (db != null) {
														db.close();
													}
												}
												domainItem.cookies.clear();
												domainItem.cookies.addAll(refreshedCookies);
												if (masterDomainList != null) {
													for (DomainItem masterDomainItem : masterDomainList) {
														if (masterDomainItem.domain.equals(domainItem.domain)) {
															masterDomainItem.cookies.clear();
															masterDomainItem.cookies.addAll(refreshedCookies);
															break;
														}
													}
												}
												populateCookieList(act, listContainer, domainItem.cookies,
														deleteDomainButton, scrollView);
												if (domainItem.cookies.isEmpty()) {
													dialog.dismiss();
													String resultMsg = String.format(LocalizedStringProvider
															.getInstance().get(act, "cookie_domain_delete_success"),
															domainItem.domain);
													jiguroMessageWithContext(act, resultMsg);
												}
											}
										});
							}
						});
				builder.setNegativeButton(LocalizedStringProvider.getInstance().get(act, "dialog_cancel"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				AlertDialog confirmDialog = builder.create();
				applyAlertDialogTheme(act, act, confirmDialog);
				confirmDialog.show();
			}
		});
		selectAllDomainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isDomainAllSelected[0] = !isDomainAllSelected[0];
				if (isDomainAllSelected[0]) {
					selectAllDomainButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_unselect_all"));
					jiguroMessageWithContext(act,
							LocalizedStringProvider.getInstance().get(act, "cookie_manager_selecting"));
					for (CookieItem item : domainItem.cookies) {
						item.selected = true;
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									for (int i = 0; i < listContainer.getChildCount(); i++) {
										View child = listContainer.getChildAt(i);
										if (child instanceof LinearLayout) {
											LinearLayout itemLayout = (LinearLayout) child;
											View firstChild = itemLayout.getChildAt(0);
											if (firstChild instanceof LinearLayout) {
												CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild)
														.getChildAt(0);
												if (checkbox != null) {
													checkbox.setChecked(true);
												}
												if (itemLayout.getTag() instanceof CookieItem) {
													((CookieItem) itemLayout.getTag()).selected = true;
												}
											}
										}
									}
									deleteDomainButton.setEnabled(true);
									deleteDomainButton.setText(LocalizedStringProvider.getInstance().get(act,
											"cookie_manager_delete_selected"));
								}
							});
						}
					}).start();
				} else {
					selectAllDomainButton
							.setText(LocalizedStringProvider.getInstance().get(act, "cookie_manager_select_all"));
					jiguroMessageWithContext(act,
							LocalizedStringProvider.getInstance().get(act, "cookie_manager_unselecting"));
					for (CookieItem item : domainItem.cookies) {
						item.selected = false;
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									for (int i = 0; i < listContainer.getChildCount(); i++) {
										View child = listContainer.getChildAt(i);
										if (child instanceof LinearLayout) {
											LinearLayout itemLayout = (LinearLayout) child;
											View firstChild = itemLayout.getChildAt(0);
											if (firstChild instanceof LinearLayout) {
												CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild)
														.getChildAt(0);
												if (checkbox != null) {
													checkbox.setChecked(false);
												}
												if (itemLayout.getTag() instanceof CookieItem) {
													((CookieItem) itemLayout.getTag()).selected = false;
												}
											}
										}
									}
									deleteDomainButton.setEnabled(false);
									deleteDomainButton.setText(LocalizedStringProvider.getInstance().get(act,
											"cookie_manager_delete_selected"));
								}
							});
						}
					}).start();
				}
			}
		});
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void showDeleteConfirmDialog(final Activity act, final Context ctx, final LinearLayout listContainer,
			final Button deleteButton, final ScrollView scrollView, final boolean isDomainView) {
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle(LocalizedStringProvider.getInstance().get(ctx, "cookie_delete_confirm_title"));
		String confirmMsg;
		if (isDomainView) {
			confirmMsg = LocalizedStringProvider.getInstance().get(ctx, "cookie_domain_delete_selected_confirm_msg");
		} else {
			confirmMsg = LocalizedStringProvider.getInstance().get(ctx, "cookie_delete_confirm_msg");
		}
		builder.setMessage(confirmMsg);
		builder.setPositiveButton(LocalizedStringProvider.getInstance().get(ctx, "cookie_manager_delete_btn"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isDomainView) {
							deleteSelectedDomains(act, listContainer, deleteButton, scrollView);
						} else {
							deleteSelectedCookies(act, listContainer, deleteButton, scrollView);
						}
					}
				});
		builder.setNegativeButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		applyAlertDialogTheme(act, ctx, dialog);
		dialog.show();
	}
	private void deleteSelectedCookies(final Activity act, final LinearLayout listContainer, final Button deleteButton,
			final ScrollView scrollView) {
		deleteSelectedCookies(act, listContainer, deleteButton, scrollView, null);
	}
	private void deleteSelectedCookies(final Activity act, final LinearLayout listContainer, final Button deleteButton,
			final ScrollView scrollView, final Runnable onCompleteCallback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = null;
				final List<CookieItem> deletedItems = new ArrayList<>();
				try {
					String cookiePath = getCookieFilePath(act);
					db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READWRITE);
					db.beginTransaction();
					try {
						for (int i = 0; i < listContainer.getChildCount(); i++) {
							View child = listContainer.getChildAt(i);
							if (child instanceof LinearLayout && child.getTag() instanceof CookieItem) {
								CookieItem item = (CookieItem) child.getTag();
								if (item.selected) {
									String whereClause = "creation_utc = ? AND host_key = ? AND name = ?";
									String[] whereArgs = {String.valueOf(item.creation_utc), item.host_key, item.name};
									db.delete(COOKIE_TABLE_NAME, whereClause, whereArgs);
									deletedItems.add(item);
								}
							}
						}
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				} catch (Exception e) {
					bvLog("[BetterVia] 批量删除Cookie失败: " + e);
				} finally {
					if (db != null) {
						db.close();
					}
				}
				final int finalCount = deletedItems.size();
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (finalCount > 0) {
							for (CookieItem deletedItem : deletedItems) {
								removeDeletedCookieFromList(act, listContainer, deleteButton, scrollView, deletedItem);
							}
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(act, "cookie_delete_success"));
						} else {
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(act, "cookie_delete_no_selected"));
						}
						if (onCompleteCallback != null) {
							onCompleteCallback.run();
						}
					}
				});
			}
		}).start();
	}
	private void deleteSelectedDomains(final Activity act, final LinearLayout listContainer, final Button deleteButton,
			final ScrollView scrollView) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = null;
				final List<DomainItem> deletedDomains = new ArrayList<>();
				final List<CookieItem> deletedCookies = new ArrayList<>();
				try {
					String cookiePath = getCookieFilePath(act);
					db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READWRITE);
					db.beginTransaction();
					try {
						for (int i = 0; i < listContainer.getChildCount(); i++) {
							View child = listContainer.getChildAt(i);
							if (child instanceof LinearLayout && child.getTag() instanceof DomainItem) {
								DomainItem domainItem = (DomainItem) child.getTag();
								if (domainItem.selected) {
									for (CookieItem cookie : domainItem.cookies) {
										String whereClause = "creation_utc = ? AND host_key = ? AND name = ?";
										String[] whereArgs = {String.valueOf(cookie.creation_utc), cookie.host_key,
												cookie.name};
										db.delete(COOKIE_TABLE_NAME, whereClause, whereArgs);
										deletedCookies.add(cookie);
									}
									deletedDomains.add(domainItem);
								}
							}
						}
						db.setTransactionSuccessful();
					} finally {
						db.endTransaction();
					}
				} catch (Exception e) {
					bvLog("[BetterVia] 批量删除域名Cookie失败: " + e);
				} finally {
					if (db != null) {
						db.close();
					}
				}
				final int finalDomainCount = deletedDomains.size();
				final int finalCookieCount = deletedCookies.size();
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (finalDomainCount > 0) {
							for (int i = listContainer.getChildCount() - 1; i >= 0; i--) {
								View child = listContainer.getChildAt(i);
								if (child instanceof LinearLayout && child.getTag() instanceof DomainItem) {
									DomainItem domainItem = (DomainItem) child.getTag();
									if (domainItem.selected) {
										listContainer.removeViewAt(i);
									}
								}
							}
							updateDeleteButtonState(act, listContainer, deleteButton);
							String successMsg = String.format(
									LocalizedStringProvider.getInstance().get(act,
											"cookie_domain_delete_selected_success"),
									finalDomainCount, finalCookieCount);
							jiguroMessageWithContext(act, successMsg);
							if (listContainer.getChildCount() == 0) {
								showEmptyCookieListState(act, listContainer);
							}
						} else {
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(act, "cookie_delete_no_selected"));
						}
					}
				});
			}
		}).start();
	}
	private static Map<String, EditorState> editorStateCache = new HashMap<>();
	private static class EditorState {
		String content;
		int scrollY;
		long timestamp;
		EditorState(String content, int scrollY) {
			this.content = content;
			this.scrollY = scrollY;
			this.timestamp = System.currentTimeMillis();
		}
	}
	private void loadFileContent(final Context ctx, final String fileName, final EditText editor,
			final ScrollView scrollView, final boolean fromCache) {
		if (fromCache && editorStateCache.containsKey(fileName)) {
			final EditorState state = editorStateCache.get(fileName);
			editor.setText(state.content);
			editor.post(new Runnable() {
				@Override
				public void run() {
					if (scrollView != null) {
						scrollView.scrollTo(0, state.scrollY);
					}
				}
			});
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String filePath = "/data/user/0/" + ctx.getPackageName() + "/files/" + fileName;
					File file = new File(filePath);
					final StringBuilder content = new StringBuilder();
					if (file.exists()) {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String line;
						while ((line = reader.readLine()) != null) {
							content.append(line).append("\n");
						}
						reader.close();
					} else {
						content.append("
					}
					((Activity) ctx).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							editor.setText(content.toString());
							editorStateCache.put(fileName, new EditorState(content.toString(), 0));
						}
					});
				} catch (final Exception e) {
					((Activity) ctx).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							editor.setText("加载失败: " + e.getMessage());
						}
					});
				}
			}
		}).start();
	}
	private void saveCurrentEditorState(String fileName, EditText editor, ScrollView scrollView) {
		if (editor != null && scrollView != null) {
			String content = editor.getText().toString();
			int scrollY = scrollView.getScrollY();
			editorStateCache.put(fileName, new EditorState(content, scrollY));
		}
	}
	private void saveFileContent(final Context ctx, final String fileName, final String content) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String filePath = "/data/user/0/" + ctx.getPackageName() + "/files/" + fileName;
					File file = new File(filePath);
					FileWriter writer = new FileWriter(file);
					writer.write(content);
					writer.close();
				} catch (Exception e) {
					bvLog("[BetterVia] 保存文件失败: " + e);
				}
			}
		}).start();
	}
	private List<CookieItem> loadCookieData(Context ctx) {
		List<CookieItem> cookieItems = new ArrayList<CookieItem>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			String cookiePath = getCookieFilePath(ctx);
			File cookieFile = new File(cookiePath);
			if (!cookieFile.exists()) {
				return cookieItems;
			}
			db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READONLY);
			Cursor tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
					new String[]{COOKIE_TABLE_NAME});
			if (!tableCursor.moveToFirst()) {
				tableCursor.close();
				return cookieItems;
			}
			tableCursor.close();
			cursor = db.query(COOKIE_TABLE_NAME, null, null, null, null, null, "host_key, name");
			if (cursor != null && cursor.moveToFirst()) {
				do {
					try {
						CookieItem item = new CookieItem();
						item.creation_utc = getLongSafe(cursor, "creation_utc");
						item.host_key = getStringSafe(cursor, "host_key");
						item.name = getStringSafe(cursor, "name");
						item.value = getStringSafe(cursor, "value");
						item.path = getStringSafe(cursor, "path");
						item.expires_utc = getLongSafe(cursor, "expires_utc");
						item.is_secure = getIntSafe(cursor, "is_secure") == 1;
						item.is_httponly = getIntSafe(cursor, "is_httponly") == 1;
						item.last_access_utc = getLongSafe(cursor, "last_access_utc");
						item.is_persistent = getIntSafe(cursor, "is_persistent") == 1;
						cookieItems.add(item);
					} catch (Exception e) {
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 读取Cookie数据失败: " + e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return cookieItems;
	}
	private List<DomainItem> loadDomainGroupedCookieData(Context ctx) {
		List<DomainItem> domainItems = new ArrayList<>();
		Map<String, DomainItem> domainMap = new HashMap<>();
		List<CookieItem> cookieItems = loadCookieData(ctx);
		for (CookieItem cookie : cookieItems) {
			String domain = cookie.host_key;
			if (domain == null || domain.isEmpty()) {
				domain = LocalizedStringProvider.getInstance().get(ctx, "cookie_unknown_domain");
			}
			DomainItem domainItem = domainMap.get(domain);
			if (domainItem == null) {
				domainItem = new DomainItem(domain);
				domainMap.put(domain, domainItem);
				domainItems.add(domainItem);
			}
			domainItem.addCookie(cookie);
		}
		Collections.sort(domainItems, new Comparator<DomainItem>() {
			@Override
			public int compare(DomainItem d1, DomainItem d2) {
				return d1.domain.compareToIgnoreCase(d2.domain);
			}
		});
		return domainItems;
	}
	private String getStringSafe(Cursor cursor, String columnName) {
		try {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return "";
			return cursor.getString(columnIndex);
		} catch (Exception e) {
			return "";
		}
	}
	private long getLongSafe(Cursor cursor, String columnName) {
		try {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return 0;
			return cursor.getLong(columnIndex);
		} catch (Exception e) {
			return 0;
		}
	}
	private int getIntSafe(Cursor cursor, String columnName) {
		try {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return 0;
			return cursor.getInt(columnIndex);
		} catch (Exception e) {
			return 0;
		}
	}
	private static class CookieItem {
		long creation_utc;
		String host_key;
		String name;
		String value;
		String path;
		long expires_utc;
		boolean is_secure;
		boolean is_httponly;
		long last_access_utc;
		boolean is_persistent;
		String top_frame_site_key;
		String encrypted_value;
		boolean has_expires;
		int priority;
		int samesite;
		int source_scheme;
		int source_port;
		long last_update_utc;
		int source_type;
		boolean has_cross_site_ancestor;
		boolean selected;
		CookieItem() {
			this.selected = false;
			this.creation_utc = 0;
			this.expires_utc = 0;
			this.last_access_utc = 0;
			this.last_update_utc = 0;
			this.is_secure = false;
			this.is_httponly = false;
			this.is_persistent = false;
			this.has_expires = false;
			this.priority = 0;
			this.samesite = 0;
			this.source_scheme = 0;
			this.source_port = 0;
			this.source_type = 0;
			this.has_cross_site_ancestor = false;
			this.host_key = "";
			this.name = "";
			this.value = "";
			this.path = "";
			this.top_frame_site_key = "";
			this.encrypted_value = "";
		}
	}
	private static class DomainItem {
		String domain;
		List<CookieItem> cookies;
		boolean selected;
		DomainItem(String domain) {
			this.domain = domain;
			this.cookies = new ArrayList<>();
			this.selected = false;
		}
		void addCookie(CookieItem cookie) {
			cookies.add(cookie);
		}
		int getCookieCount() {
			return cookies.size();
		}
	}
	private String getCookieFilePath(Context ctx) {
		String packageName = ctx.getPackageName();
		return "/data/user/0/" + packageName + "/app_webview/Default/Cookies";
	}
	private void addImagePickerItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showHomepageBeautyDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showHomepageBeautyDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int editBgColor = getEditBgColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final int cancelBtnBgColor = getBtnBgColor(ctx);
				final int cancelBtnTextColor = getBtnTextColor(ctx);
				homepageBgPath = getPrefString(ctx, KEY_HOMEPAGE_BG, "");
				homepageMaskAlpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
				int savedRgb = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x000000);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(bgColor);
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_dialog_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 16));
				root.addView(subtitle);
				final FrameLayout previewContainer = new FrameLayout(act);
				LinearLayout.LayoutParams preLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						dp(act, 150));
				preLp.bottomMargin = dp(act, 16);
				previewContainer.setLayoutParams(preLp);
				GradientDrawable preBg = new GradientDrawable();
				preBg.setColor(0xFFF5F5F5);
				preBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
				preBg.setCornerRadius(dp(act, 12));
				previewContainer.setBackground(preBg);
				final ImageView imageView = new ImageView(act);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				final View maskView = new View(act);
				maskView.setClickable(false);
				previewContainer.addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				previewContainer.addView(maskView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				root.addView(previewContainer);
				refreshPreview(ctx, imageView, maskView, homepageMaskAlpha, savedRgb);
				Button pickBtn = new Button(act);
				applyClickAnim(pickBtn);
				pickBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_pick_btn"));
				pickBtn.setTextColor(okBtnTextColor);
				pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				pickBtn.setTypeface(null, Typeface.BOLD);
				pickBtn.setBackground(getRoundBg(act, okBtnBgColor, 12));
				root.addView(pickBtn);
				TextView alphaTitle = new TextView(act);
				alphaTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_mask_alpha"));
				alphaTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				alphaTitle.setTextColor(textColor);
				alphaTitle.setTypeface(null, Typeface.BOLD);
				alphaTitle.setPadding(0, dp(act, 16), 0, 0);
				root.addView(alphaTitle);
				final SeekBar alphaSeek = new SeekBar(act);
				alphaSeek.setMax(255);
				alphaSeek.setProgress(homepageMaskAlpha);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					alphaSeek.setProgressTintList(ColorStateList.valueOf(okBtnBgColor));
					alphaSeek.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
				}
				root.addView(alphaSeek);
				TextView colorTitle = new TextView(act);
				colorTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_mask_color_rgb"));
				colorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				colorTitle.setTextColor(textColor);
				colorTitle.setTypeface(null, Typeface.BOLD);
				colorTitle.setPadding(0, dp(act, 12), 0, 0);
				root.addView(colorTitle);
				LinearLayout rgbContainer = new LinearLayout(act);
				rgbContainer.setOrientation(LinearLayout.VERTICAL);
				final EditText rgbEdit = new EditText(act);
				rgbEdit.setHint("#RRGGBB");
				rgbEdit.setText(colorToRgbString(savedRgb));
				rgbEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				rgbEdit.setTextColor(textColor);
				rgbEdit.setHintTextColor(hintColor);
				rgbEdit.setBackground(getRoundBg(act, editBgColor, 4));
				rgbEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				rgbContainer.addView(rgbEdit, editLp);
				TextView rgbHint = new TextView(act);
				rgbHint.setText(LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_mask_color_hint"));
				rgbHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				rgbHint.setTextColor(hintColor);
				rgbHint.setPadding(dp(act, 4), dp(act, 4), 0, 0);
				rgbContainer.addView(rgbHint);
				root.addView(rgbContainer);
				SeekBar.OnSeekBarChangeListener alphaListener = new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (fromUser) {
							homepageMaskAlpha = progress;
							String rgbStr = rgbEdit.getText().toString();
							int rgbColor = parseRgbColor(rgbStr, 0);
							refreshPreview(ctx, imageView, maskView, homepageMaskAlpha, rgbColor);
						}
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				};
				alphaSeek.setOnSeekBarChangeListener(alphaListener);
				rgbEdit.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						String rgbStr = s.toString();
						int rgbColor = parseRgbColor(rgbStr, 0);
						refreshPreview(ctx, imageView, maskView, homepageMaskAlpha, rgbColor);
					}
				});
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				btnRow.setPadding(0, dp(act, 24), 0, 0);
				Button cancel = new Button(act);
				applyClickAnim(cancel);
				cancel.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancel.setTextColor(cancelBtnTextColor);
				cancel.setBackground(getRoundBg(act, cancelBtnBgColor, 12));
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				btnLp.rightMargin = dp(act, 8);
				btnRow.addView(cancel, btnLp);
				btnLp.leftMargin = dp(act, 8);
				btnRow.addView(ok, btnLp);
				root.addView(btnRow);
				scrollRoot.addView(root);
				dialog.setContentView(scrollRoot);
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor); 
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String rgbStr = rgbEdit.getText().toString();
						int rgbColor = parseRgbColor(rgbStr, 0);
						if (rgbStr.trim().length() > 0 && !isValidRgbColor(rgbStr)) {
							jiguroMessageWithContext(ctx,
									LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_mask_color_invalid"));
							return;
						}
						putPrefInt(ctx, KEY_HOMEPAGE_MASK_A, homepageMaskAlpha);
						putPrefInt(ctx, KEY_HOMEPAGE_MASK_C, rgbColor);
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "homepage_bg_saved"));
						dialog.dismiss();
					}
				});
				pickBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_GET_CONTENT);
						i.setType("image
	private boolean isValidRgbColor(String rgbStr) {
		if (rgbStr == null || rgbStr.trim().isEmpty()) {
			return true;
		}
		String colorStr = rgbStr.trim();
		if (!colorStr.startsWith("#")) {
			colorStr = "#" + colorStr;
		}
		return colorStr.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
	}
	private void refreshPreview(Context ctx, ImageView iv, View mask, int alpha, int rgbColor) {
		if (homepageBgPath != null && new File(homepageBgPath).exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(homepageBgPath);
			if (bmp != null) {
				iv.setImageBitmap(bmp);
			} else {
				iv.setBackgroundColor(0xFFD0D0D0);
				iv.setImageBitmap(null);
			}
		} else {
			iv.setBackgroundColor(0xFFD0D0D0);
			iv.setImageBitmap(null);
		}
		int finalColor = (alpha << 24) | (rgbColor & 0x00FFFFFF);
		mask.setBackgroundColor(finalColor);
	}
	private void hookHomepageBgWithMask(final Context ctx, ClassLoader cl, final String imgPath, final int maskColor) {
		String scriptRepo1Class = ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_1, ctx);
		String scriptRepo1Method = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_1, ctx);
		XposedHelpers.findAndHookMethod(scriptRepo1Class, cl, scriptRepo1Method, Context.class, List.class,
				boolean.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						String uri = (String) param.getResult();
						File html = new File(Uri.parse(uri).getPath());
						if (!html.exists())
							return;
						StringBuilder htmlSb = new StringBuilder();
						BufferedReader br = null;
						try {
							br = new BufferedReader(new FileReader(html));
							String line;
							while ((line = br.readLine()) != null)
								htmlSb.append(line).append("\n");
						} finally {
							if (br != null)
								try {
									br.close();
								} catch (Exception ignored) {
								}
						}
						String originalHtml = htmlSb.toString();
						int alpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
						int rgbColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x000000);
						bvLog("[BetterVia] 读取设置透明度: " + alpha + ", 颜色值: " + Integer.toHexString(rgbColor));
						String cssColor = colorToCssString(alpha, rgbColor);
						bvLog("[BetterVia] 最终颜色值转换: " + cssColor);
						String backgroundStyle;
						if (imgPath != null && !imgPath.isEmpty() && new File(imgPath).exists()) {
							String encodedPath = imgPath.replace("'", "\\'").replace("\\", "\\\\");
							backgroundStyle = "background:url('file:
									+ "') no-repeat center/cover fixed;";
						} else {
							backgroundStyle = "background-color:#F0F0F0;";
						}
						String maskStyle = "background:" + cssColor + ";";
						String newBodyContent = "<body style=\"" + backgroundStyle + "\">"
								+ "<div style='position:fixed;top:0;left:0;right:0;bottom:0;" + maskStyle
								+ "z-index:0;'></div>" + "<div style='position:relative;z-index:1;'>";
						String modifiedHtml = originalHtml.replace("<body>", newBodyContent).replace("</body>",
								"</div></body>");
						FileWriter fw = null;
						try {
							fw = new FileWriter(html);
							fw.write(modifiedHtml);
							bvLog("[BetterVia] 成功应用具有正确颜色格式的背景");
						} catch (Exception e) {
							bvLog("[BetterVia] 写入修改HTML时出错: " + e);
						} finally {
							if (fw != null)
								try {
									fw.close();
								} catch (Exception ignored) {
								}
						}
					}
				});
	}
	private void handleActivityResult(int requestCode, int resultCode, Intent data, final Activity activity) {
		if (resultCode != Activity.RESULT_OK || data == null)
			return;
		if (requestCode == 0x1002) {
			Uri uri = data.getData();
			if (saveUserImage(activity, uri)) {
				homepageBgPath = getPrefString(activity, KEY_HOMEPAGE_BG, "");
				if (Context != null && Context instanceof Activity) {
					((Activity) Context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							jiguroMessageWithContext(activity,
									LocalizedStringProvider.getInstance().get(activity, "homepage_bg_set_ok"));
						}
					});
				}
			}
		} else if (requestCode == 0x2001) {
			final Uri uri = data.getData();
			if (generatedApkPath != null && uri != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							File sourceFile = new File(generatedApkPath);
							InputStream in = null;
							OutputStream out = null;
							try {
								in = new FileInputStream(sourceFile);
								out = activity.getContentResolver().openOutputStream(uri);
								byte[] buffer = new byte[8192];
								int len;
								while ((len = in.read(buffer)) != -1) {
									out.write(buffer, 0, len);
								}
								out.flush();
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										jiguroMessageWithContext(activity, LocalizedStringProvider.getInstance()
												.get(activity, "homepage_bg_saved"));
									}
								});
							} finally {
								if (in != null) {
									try {
										in.close();
									} catch (Exception ignored) {
									}
								}
								if (out != null) {
									try {
										out.close();
									} catch (Exception ignored) {
									}
								}
							}
						} catch (final Exception e) {
							bvLog("[BetterVia] 保存APK文件失败: " + e);
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									jiguroMessageWithContext(activity, "保存失败: " + e.getMessage());
								}
							});
						}
					}
				}).start();
			}
		} else if (requestCode == 0x3001) {
			Uri uri = data.getData();
			if (StartupExecutionHelper.saveStartupImage(activity, uri)) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						jiguroMessageWithContext(activity,
								LocalizedStringProvider.getInstance().get(activity, "startup_image_set_ok"));
					}
				});
			}
		} else if (requestCode == 0x3002) {
			Uri uri = data.getData();
			if (StartupExecutionHelper.saveStartupMusic(activity, uri)) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						jiguroMessageWithContext(activity,
								LocalizedStringProvider.getInstance().get(activity, "startup_music_set_ok"));
					}
				});
			}
		}
	}
	private boolean saveUserImage(Activity act, Uri uri) {
		if (uri == null)
			return false;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			File outFile = new File(act.getFilesDir(), "homepage_bg.jpg");
			in = act.getContentResolver().openInputStream(uri);
			out = new FileOutputStream(outFile);
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			homepageBgPath = outFile.getAbsolutePath();
			putPrefString(act, KEY_HOMEPAGE_BG, homepageBgPath);
			return true;
		} catch (Exception e) {
			bvLog("[BetterVia] 保存用户图片时出现错误: " + e);
			return false;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception ignored) {
				}
			if (out != null)
				try {
					out.close();
				} catch (Exception ignored) {
				}
		}
	}
	private String colorToCssString(int alpha, int rgbColor) {
		int r = (rgbColor >> 16) & 0xFF;
		int g = (rgbColor >> 8) & 0xFF;
		int b = rgbColor & 0xFF;
		float alphaFloat = alpha / 255.0f;
		return String.format(Locale.US, "rgba(%d, %d, %d, %.2f)", r, g, b, alphaFloat);
	}
	private int parseRgbColor(String rgbStr, int defaultAlpha) {
		if (rgbStr == null || rgbStr.trim().isEmpty()) {
			return 0xFFFFFF;
		}
		String colorStr = rgbStr.trim();
		if (!colorStr.startsWith("#")) {
			colorStr = "#" + colorStr;
		}
		try {
			if (colorStr.length() == 7) {
				return Color.parseColor(colorStr) & 0x00FFFFFF;
			}
		} catch (Exception e) {
		}
		return 0xFFFFFF;
	}
	private int parseBorderColor(String rgbStr) {
		if (rgbStr == null || rgbStr.trim().isEmpty()) {
			return 0xFF000000;
		}
		String colorStr = rgbStr.trim();
		if (!colorStr.startsWith("#")) {
			colorStr = "#" + colorStr;
		}
		try {
			if (colorStr.length() == 7) {
				int color = Color.parseColor(colorStr);
				return color | 0xFF000000;  
			}
		} catch (Exception e) {
		}
		return 0xFF000000;
	}
	private String colorToRgbString(int color) {
		return String.format("#%06X", color & 0x00FFFFFF);
	}
	private void addBlockMenuBarItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showBlockMenuBarDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showBlockMenuBarDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int editBgColor = getEditBgColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(bgColor);
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 16);
				root.addView(title, titleLp);
				TextView hint = new TextView(act);
				hint.setText(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_dialog_hint"));
				hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hint.setTextColor(textColor);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 12);
				root.addView(hint, hintLp);
				final EditText urlEdit = new EditText(act);
				urlEdit.setHint(LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_input_hint"));
				urlEdit.setText(getPrefString(ctx, KEY_BLOCK_MENU_BAR, ""));
				urlEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				urlEdit.setTextColor(textColor);
				urlEdit.setHintTextColor(hintColor); 
				urlEdit.setBackground(getRoundBg(act, editBgColor, 4));
				urlEdit.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				urlEdit.setMinLines(3);
				urlEdit.setGravity(Gravity.TOP | Gravity.START);
				LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				editLp.bottomMargin = dp(act, 16);
				root.addView(urlEdit, editLp);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				GradientDrawable btnBg = new GradientDrawable();
				btnBg.setColor(okBtnBgColor);
				btnBg.setCornerRadius(dp(act, 12));
				ok.setBackground(btnBg);
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor);
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String urls = urlEdit.getText().toString().trim();
						putPrefString(ctx, KEY_BLOCK_MENU_BAR, urls);
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "block_menu_bar_saved"));
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private XC_MethodHook.Unhook blockMenuBarHook = null;
	private void setBlockMenuBarHook(final Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (blockMenuBarHook == null) {
				try {
					String scriptRepo2Class = ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_2,
							ctx);
					String scriptRepo2Method = ViaClassMapping
							.getMethodName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_2, ctx);
					XposedHelpers.findAndHookMethod(scriptRepo2Class, cl, scriptRepo2Method, int.class, int.class,
							String.class, String.class, String.class, new XC_MethodHook() {
								@Override
								protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
									String currentUrl = getCurrentUrl();
									if (currentUrl == null || currentUrl.isEmpty()) {
										return;
									}
									String blockUrls = getPrefString(ctx, KEY_BLOCK_MENU_BAR, "");
									if (blockUrls == null || blockUrls.trim().isEmpty()) {
										return;
									}
									String[] urlList = blockUrls.split(",");
									boolean shouldBlock = false;
									for (String url : urlList) {
										String trimmedUrl = url.trim();
										if (!trimmedUrl.isEmpty() && currentUrl.contains(trimmedUrl)) {
											shouldBlock = true;
											bvLog("[BetterVia] 屏蔽菜单栏: 当前URL " + currentUrl + " 匹配 " + trimmedUrl);
											break;
										}
									}
									if (shouldBlock) {
										param.setResult(null);
										bvLog("[BetterVia] 已阻止菜单栏显示");
									}
								}
							});
					bvLog("[BetterVia] 屏蔽菜单栏Hook已启用");
				} catch (Throwable e) {
					bvLog("[BetterVia] 屏蔽菜单栏Hook失败: " + e.getMessage());
				}
			}
		} else {
			if (blockMenuBarHook != null) {
				blockMenuBarHook.unhook();
				blockMenuBarHook = null;
				bvLog("[BetterVia] 屏蔽菜单栏Hook已停用");
			}
		}
	}
	private String getCurrentUrl() {
		try {
			if (Context != null && Context instanceof Activity) {
				Activity activity = (Activity) Context;
				try {
					Object webView = XposedHelpers.getObjectField(activity, "u");
					if (webView instanceof WebView) {
						String url = ((WebView) webView).getUrl();
						if (url != null && !url.isEmpty()) {
							return url;
						}
					}
				} catch (Exception e1) {
					try {
						Object webView = XposedHelpers.getObjectField(activity, "webView");
						if (webView instanceof WebView) {
							String url = ((WebView) webView).getUrl();
							if (url != null && !url.isEmpty()) {
								return url;
							}
						}
					} catch (Exception e2) {
					}
				}
				View currentFocus = activity.getCurrentFocus();
				if (currentFocus instanceof WebView) {
					String url = ((WebView) currentFocus).getUrl();
					if (url != null && !url.isEmpty()) {
						return url;
					}
				}
				ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
				WebView webView = findWebViewRecursive(decorView);
				if (webView != null) {
					String url = webView.getUrl();
					if (url != null && !url.isEmpty()) {
						return url;
					}
				}
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 获取当前URL失败: " + e.getMessage());
		}
		return "";
	}
	private void addUserAgentItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showUserAgentDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showUserAgentDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(0, 0, 0, 0);
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 16));
				root.addView(subtitle);
				final LinearLayout uaContainer = new LinearLayout(act);
				uaContainer.setOrientation(LinearLayout.VERTICAL);
				uaContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				root.addView(uaContainer);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
						window.setClipToOutline(true);
					}
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				populateUserAgentList(act, ctx, uaContainer);
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void populateUserAgentList(final Activity act, final Context ctx, LinearLayout container) {
		container.removeAllViews();
		final int itemBgColor = getItemBgColor(ctx);
		final int textColor = getTextColor(ctx);
		final int hintColor = getHintColor(ctx);
		final int dividerColor = getDividerColor(ctx);
		final int okBtnBgColor = getOkBtnBgColor(ctx);
		final int okBtnTextColor = getOkBtnTextColor(ctx);
		List<UserAgentInfo> uaList = getPersonalizedUserAgents(act);
		for (final UserAgentInfo uaInfo : uaList) {
			LinearLayout uaItem = new LinearLayout(act);
			uaItem.setOrientation(LinearLayout.VERTICAL);
			uaItem.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
			GradientDrawable itemBg = new GradientDrawable();
			itemBg.setColor(itemBgColor);
			itemBg.setStroke(dp(act, 1), dividerColor);
			itemBg.setCornerRadius(dp(act, 12));
			uaItem.setBackground(itemBg);
			LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			itemLp.bottomMargin = dp(act, 8);
			container.addView(uaItem, itemLp);
			TextView browserName = new TextView(act);
			browserName.setText(uaInfo.browserName);
			browserName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			browserName.setTextColor(textColor);
			browserName.setTypeface(null, Typeface.BOLD);
			uaItem.addView(browserName);
			final TextView uaText = new TextView(act);
			uaText.setText(uaInfo.userAgent);
			uaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			uaText.setTextColor(hintColor);
			uaText.setSingleLine(true);
			uaText.setEllipsize(TextUtils.TruncateAt.MIDDLE);
			uaText.setPadding(0, dp(act, 8), 0, dp(act, 8));
			uaItem.addView(uaText);
			Button copyBtn = new Button(act);
			applyClickAnim(copyBtn);
			copyBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agent_copy"));
			copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			copyBtn.setTextColor(okBtnTextColor);
			copyBtn.setPadding(dp(act, 12), dp(act, 4), dp(act, 12), dp(act, 4));
			copyBtn.setMinHeight(dp(act, 28));
			GradientDrawable btnBg = new GradientDrawable();
			btnBg.setColor(okBtnBgColor);
			btnBg.setCornerRadius(dp(act, 6));
			copyBtn.setBackground(btnBg);
			LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			btnLp.gravity = Gravity.END;
			uaItem.addView(copyBtn, btnLp);
			copyBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					copyToClipboard(act, uaInfo.userAgent);
					jiguroMessageWithContext(act, LocalizedStringProvider.getInstance().get(ctx, "user_agent_copied"));
				}
			});
			uaItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					copyToClipboard(act, uaInfo.userAgent);
					jiguroMessageWithContext(act, LocalizedStringProvider.getInstance().get(ctx, "user_agent_copied"));
				}
			});
		}
	}
	private static class UserAgentInfo {
		String browserName;
		String userAgent;
		UserAgentInfo(String browserName, String userAgent) {
			this.browserName = browserName;
			this.userAgent = userAgent;
		}
	}
	private List<UserAgentInfo> getPersonalizedUserAgents(Context ctx) {
		List<UserAgentInfo> uaList = new ArrayList<>();
		String deviceModel = Build.MODEL;
		String androidVersion = "Android " + Build.VERSION.RELEASE;
		String buildVersion = Build.DISPLAY;
		if (buildVersion == null || buildVersion.isEmpty()) {
			buildVersion = "PKQ1.181007.001";
		}
		String[] uaTemplates = {
				"百度: Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36 T7/13.59 SP-engine/2.98.0 baiduboxapp/13.59.0.10 (Baidu; P1 12) NABar/1.0",
				"小米浏览器: Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model} Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.119 Mobile Safari/537.36 XiaoMi/MiuiBrowser/19.2.820324",
				"华为浏览器: Mozilla/5.0 (Linux; {android_version}; HarmonyOS; {device_model}; HMSCore 5.3.0.312) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93 HuaweiBrowser/11.1.1.310 Mobile Safari/537.36",
				"UC浏览器: Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model} Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/100.0.4896.58 UCBrowser/17.5.0.1381 Mobile Safari/537.36",
				"Edge浏览器: Mozilla/5.0 (Linux; {android_version}; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36 EdgA/134.0.0.0",
				"QQ浏览器: Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model} Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/109.0.5414.86 MQQBrowser/16.1 Mobile Safari/537.36 COVC/046915",
				"夸克浏览器: Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model} Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/100.0.4896.58 Quark/7.9.6.781 Mobile Safari/537.36",
				"360浏览器: Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/62.0.3202.97 Mobile Safari/537.36",
				"简单搜索: Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 danSearchCraft Chrome/76.0.3809.89 Mobile Safari/537.36",
				"Chrome: Mozilla/5.0 (Linux; {android_version}; {device_model}) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36",
				"微信: Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.62 XWEB/2893 MMWEBSDK/20210601 Mobile Safari/537.36 MMWEBID/9453 MicroMessenger/8.0.9.1940(0x28000951) Process/toolsmp WeChat/arm64 Weixin NetType/4G Language/zh_CN ABI/arm64",
				"iPhone: Mozilla/5.0 (iPhone; CPU iPhone OS 18_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Mobile/15E148 Safari/604.1",
				"淘宝浏览器: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11"};
		for (String template : uaTemplates) {
			String[] parts = template.split(": ", 2);
			if (parts.length == 2) {
				String browserName = parts[0];
				String uaTemplate = parts[1];
				String personalizedUA = uaTemplate.replace("{android_version}", androidVersion)
						.replace("{device_model}", deviceModel).replace("{build_version}", buildVersion);
				uaList.add(new UserAgentInfo(browserName, personalizedUA));
			}
		}
		return uaList;
	}
	private void setDownloadDialogShareHook(Context ctx, ClassLoader cl, boolean on) {
		if (on) {
			if (downloadDialogShareHook == null) {
				addShareButtonToDownloadDialog(ctx, cl);
				downloadDialogShareEnabled = true;
				bvLog("[BetterVia] 下载对话框分享按钮已启用");
			}
		} else {
			if (downloadDialogShareHook != null) {
				downloadDialogShareHook.unhook();
				downloadDialogShareHook = null;
				bvLog("[BetterVia] 下载对话框分享按钮已停用");
			}
			downloadDialogShareEnabled = false;
		}
		putPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, on);
	}
	private void addShareButtonToDownloadDialog(final Context ctx, ClassLoader cl) {
		try {
			downloadDialogShareHook = XposedHelpers.findAndHookMethod(Dialog.class, "show", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					try {
						final Dialog dialog = (Dialog) param.thisObject;
						if (!downloadDialogShareEnabled) {
							return;
						}
						if (isViaDownloadDialog(dialog)) {
							new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										addShareButtonToDialog(dialog, ctx);
									} catch (Exception e) {
										bvLog("[BetterVia] 添加分享按钮异常: " + e);
									}
								}
							}, 100);
						}
					} catch (Exception e) {
						bvLog("[BetterVia] Hook Dialog.show失败: " + e);
					}
				}
			});
			XposedHelpers.findAndHookMethod(AlertDialog.Builder.class, "create", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					try {
						final AlertDialog dialog = (AlertDialog) param.getResult();
						if (dialog == null)
							return;
						if (!downloadDialogShareEnabled) {
							return;
						}
						dialog.setOnShowListener(new DialogInterface.OnShowListener() {
							@Override
							public void onShow(DialogInterface dialogInterface) {
								try {
									if (isViaDownloadDialog(dialog)) {
										addShareButtonToDialog(dialog, ctx);
									}
								} catch (Exception e) {
									bvLog("[BetterVia] AlertDialog显示监听异常: " + e);
								}
							}
						});
					} catch (Exception e) {
						bvLog("[BetterVia] Hook AlertDialog.create失败: " + e);
					}
				}
			});
			bvLog("[BetterVia] 下载对话框分享按钮Hook已启用");
		} catch (Throwable t) {
			bvLog("[BetterVia] Hook下载对话框失败: " + t);
		}
	}
	private boolean isViaDownloadDialog(Dialog dialog) {
		try {
			@SuppressLint("ResourceType")
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			View cancelButton = dialog.findViewById(0x7f0900c6);
			View okButton = dialog.findViewById(0x7f0900cb);
			return copyLinkButton != null && cancelButton != null && okButton != null;
		} catch (Exception e) {
			return false;
		}
	}
	private ViewGroup findButtonContainerRecursive(View view) {
		if (!(view instanceof ViewGroup))
			return null;
		ViewGroup group = (ViewGroup) view;
		int buttonCount = 0;
		for (int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			if (child instanceof Button) {
				buttonCount++;
			}
		}
		if (buttonCount >= 2) {
			return group;
		}
		for (int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				ViewGroup result = findButtonContainerRecursive(child);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	private void setupShareButtonClick(TextView shareButton, final Dialog dialog, final Context ctx) {
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					bvLog("[BetterVia] 分享按钮被点击");
					String[] downloadInfo = extractDownloadInfoFromDialog(dialog, ctx);
					String fileName = downloadInfo[0];
					String fileUrl = downloadInfo[1];
					String fileSize = downloadInfo[2];
					bvLog("[BetterVia] 提取到的下载信息 - 文件名: " + fileName + ", 大小: " + fileSize + ", URL: "
							+ (fileUrl.isEmpty() ? "空" : "已获取"));
					if (fileUrl.isEmpty()) {
						jiguroMessageWithContext(ctx, "无法获取下载链接");
						return;
					}
					String shareText = createShareText(fileName, fileSize, fileUrl, ctx);
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
					Intent chooser = Intent.createChooser(shareIntent, "分享下载链接");
					chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(chooser);
					jiguroMessageWithContext(ctx, "正在分享下载链接");
				} catch (Exception e) {
					bvLog("[BetterVia] 分享失败: " + e);
					jiguroMessageWithContext(ctx, "分享失败");
				}
			}
		});
	}
	private void insertShareButtonToContainer(ViewGroup container, TextView shareButton, Dialog dialog) {
		try {
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null) {
				bvLog("[BetterVia] 未找到复制链接按钮，无法确定插入位置");
				return;
			}
			View okButton = dialog.findViewById(0x7f0900cb);
			View cancelButton = dialog.findViewById(0x7f0900c6);
			int referenceMargin = dp(dialog.getContext(), 8);
			if (okButton != null && cancelButton != null) {
				if (okButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams okParams = (ViewGroup.MarginLayoutParams) okButton.getLayoutParams();
					referenceMargin = okParams.rightMargin;
					bvLog("[BetterVia] 获取到确定按钮的右边距: " + referenceMargin + "px");
				}
			}
			int copyLinkIndex = -1;
			for (int i = 0; i < container.getChildCount(); i++) {
				if (container.getChildAt(i) == copyLinkButton) {
					copyLinkIndex = i;
					break;
				}
			}
			if (copyLinkIndex == -1) {
				bvLog("[BetterVia] 复制链接按钮不在容器中");
				return;
			}
			if (container instanceof RelativeLayout) {
				bvLog("[BetterVia] 检测到RelativeLayout容器，设置分享按钮在复制链接按钮右侧，间距: " + referenceMargin + "px");
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_TOP, copyLinkButton.getId());
				params.addRule(RelativeLayout.ALIGN_BOTTOM, copyLinkButton.getId());
				params.addRule(RelativeLayout.RIGHT_OF, copyLinkButton.getId());
				if (copyLinkButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams refParams = (ViewGroup.MarginLayoutParams) copyLinkButton
							.getLayoutParams();
					params.setMargins(referenceMargin, 
							refParams.topMargin, refParams.rightMargin, refParams.bottomMargin);
					params.height = refParams.height;
				} else {
					params.setMargins(referenceMargin, 0, 0, 0);
				}
				shareButton.setLayoutParams(params);
			} else if (container instanceof LinearLayout) {
				LinearLayout.LayoutParams refParams = (LinearLayout.LayoutParams) copyLinkButton.getLayoutParams();
				LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(refParams.width, refParams.height,
						refParams.weight);
				newParams.setMargins(refParams.leftMargin, refParams.topMargin, refParams.rightMargin,
						refParams.bottomMargin);
				newParams.gravity = refParams.gravity;
				shareButton.setLayoutParams(newParams);
			} else {
				ViewGroup.LayoutParams refParams = copyLinkButton.getLayoutParams();
				ViewGroup.LayoutParams newParams = new ViewGroup.LayoutParams(refParams.width, refParams.height);
				shareButton.setLayoutParams(newParams);
			}
			container.addView(shareButton, copyLinkIndex + 1);
			bvLog("[BetterVia] 分享按钮已插入到位置: " + (copyLinkIndex + 1));
			container.requestLayout();
		} catch (Exception e) {
			bvLog("[BetterVia] 插入分享按钮失败: " + e);
			try {
				ViewGroup.LayoutParams simpleParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				shareButton.setLayoutParams(simpleParams);
				container.addView(shareButton);
			} catch (Exception e2) {
				bvLog("[BetterVia] 备用方案也失败: " + e2);
			}
		}
	}
	private TextView createShareButton(Context ctx, Dialog dialog) {
		try {
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null) {
				bvLog("[BetterVia] 未找到复制链接按钮，无法获取样式");
				return null;
			}
			TextView shareButton = new TextView(ctx);
			shareButton.setId(0x7f09abcd);
			shareButton.setClickable(true);
			shareButton.setFocusable(true);
			shareButton.setText(LocalizedStringProvider.getInstance().get(ctx, "download_dialog_share"));
			shareButton.setTextSize(14);
			shareButton.setGravity(Gravity.CENTER);
			if (copyLinkButton instanceof TextView) {
				TextView refTextView = (TextView) copyLinkButton;
				shareButton.setTextColor(refTextView.getTextColors());
				shareButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, refTextView.getTextSize());
			} else {
				shareButton.setTextColor(0xFF6200EE);
			}
			Drawable refBackground = copyLinkButton.getBackground();
			if (refBackground != null) {
				try {
					Drawable backgroundCopy = refBackground.getConstantState().newDrawable().mutate();
					shareButton.setBackground(backgroundCopy);
					bvLog("[BetterVia] 已创建独立的背景Drawable");
				} catch (Exception e) {
					bvLog("[BetterVia] 创建独立Drawable失败，使用原始背景: " + e);
					shareButton.setBackground(refBackground);
				}
			}
			shareButton.setPadding(copyLinkButton.getPaddingLeft(), copyLinkButton.getPaddingTop(),
					copyLinkButton.getPaddingRight(), copyLinkButton.getPaddingBottom());
			return shareButton;
		} catch (Exception e) {
			bvLog("[BetterVia] 创建分享按钮失败: " + e);
			return null;
		}
	}
	private void fixCopyLinkButtonBackground(Dialog dialog) {
		try {
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null)
				return;
			Drawable background = copyLinkButton.getBackground();
			if (background != null) {
				Drawable independentBackground = background.getConstantState().newDrawable().mutate();
				copyLinkButton.setBackground(independentBackground);
				bvLog("[BetterVia] 已修复复制链接按钮的背景状态");
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 修复复制链接按钮背景失败: " + e);
		}
	}
	private void addShareButtonToDialog(final Dialog dialog, final Context ctx) {
		try {
			if (dialog.findViewById(0x7f09abcd) != null) {
				bvLog("[BetterVia] 分享按钮已存在，跳过添加");
				return;
			}
			fixCopyLinkButtonBackground(dialog);
			ViewGroup buttonContainer = findButtonContainer(dialog);
			if (buttonContainer == null) {
				bvLog("[BetterVia] 未找到按钮容器");
				return;
			}
			bvLog("[BetterVia] 找到按钮容器，类型: " + buttonContainer.getClass().getSimpleName() + ", 子视图数量: "
					+ buttonContainer.getChildCount());
			logButtonInfo(buttonContainer, "添加分享按钮前");
			TextView shareButton = createShareButton(ctx, dialog);
			if (shareButton == null) {
				bvLog("[BetterVia] 创建分享按钮失败");
				return;
			}
			insertShareButtonToContainer(buttonContainer, shareButton, dialog);
			setupShareButtonClick(shareButton, dialog, ctx);
			bvLog("[BetterVia] 成功添加TextView分享按钮到下载对话框");
			logButtonInfo(buttonContainer, "添加分享按钮后");
		} catch (Exception e) {
			bvLog("[BetterVia] 添加分享按钮到对话框失败: " + e);
		}
	}
	private ViewGroup findButtonContainer(Dialog dialog) {
		try {
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			View cancelButton = dialog.findViewById(0x7f0900c6);
			View okButton = dialog.findViewById(0x7f0900cb);
			if (copyLinkButton != null) {
				ViewGroup parent = (ViewGroup) copyLinkButton.getParent();
				while (parent != null) {
					boolean hasCopyLink = parent.indexOfChild(copyLinkButton) >= 0;
					boolean hasCancel = parent.indexOfChild(cancelButton) >= 0;
					boolean hasOk = parent.indexOfChild(okButton) >= 0;
					if (hasCopyLink && hasCancel && hasOk) {
						bvLog("[BetterVia] 找到包含所有按钮的容器: " + parent.getClass().getSimpleName());
						return parent;
					}
					if (parent.getParent() instanceof ViewGroup) {
						parent = (ViewGroup) parent.getParent();
					} else {
						break;
					}
				}
			}
			View decorView = dialog.getWindow().getDecorView();
			return findHorizontalButtonContainer(decorView);
		} catch (Exception e) {
			bvLog("[BetterVia] 查找按钮容器失败: " + e);
			return null;
		}
	}
	private ViewGroup findHorizontalButtonContainer(View view) {
		if (!(view instanceof ViewGroup))
			return null;
		ViewGroup group = (ViewGroup) view;
		if (group instanceof LinearLayout) {
			LinearLayout layout = (LinearLayout) group;
			if (layout.getOrientation() == LinearLayout.HORIZONTAL) {
				int buttonCount = 0;
				for (int i = 0; i < layout.getChildCount(); i++) {
					View child = layout.getChildAt(i);
					if (child instanceof TextView && child.isClickable()) {
						buttonCount++;
					}
				}
				if (buttonCount >= 2) {
					bvLog("[BetterVia] 找到水平按钮容器，按钮数量: " + buttonCount);
					return layout;
				}
			}
		}
		for (int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			ViewGroup result = findHorizontalButtonContainer(child);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	private void logButtonInfo(ViewGroup container, String stage) {
		try {
			bvLog("[BetterVia] " + stage + " - 容器类型: " + container.getClass().getSimpleName() + ", 子视图数量: "
					+ container.getChildCount());
			for (int i = 0; i < container.getChildCount(); i++) {
				View child = container.getChildAt(i);
				String info = "子视图 " + i + ": " + child.getClass().getSimpleName();
				if (child instanceof TextView) {
					TextView textView = (TextView) child;
					info += " 文本: \"" + textView.getText() + "\"";
					info += " ID: " + Integer.toHexString(child.getId());
					info += " 可点击: " + child.isClickable();
					ViewGroup.LayoutParams params = child.getLayoutParams();
					if (params instanceof LinearLayout.LayoutParams) {
						LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) params;
						info += " 权重: " + llParams.weight;
						info += " 宽度: " + llParams.width;
					}
				}
				info += " 可见: " + (child.getVisibility() == View.VISIBLE);
				bvLog("[BetterVia] " + info);
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 记录按钮信息失败: " + e);
		}
	}
	private String[] extractDownloadInfoFromDialog(Dialog dialog, Context ctx) {
		String[] info = {"未知文件", "", "未知大小"};
		try {
			View decorView = dialog.getWindow().getDecorView();
			List<TextView> textViews = findAllTextViews(decorView);
			for (TextView textView : textViews) {
				String text = textView.getText().toString().trim();
				if (TextUtils.isEmpty(text) || isButtonText(text)) {
					continue;
				}
				if (text.contains(".") && text.length() > 3) {
					info[0] = text;
				} else if (text.contains("MB") || text.contains("KB") || text.contains("GB") || text.contains("字节")
						|| text.contains("B") || text.matches(".*\\d+.*")) {
					info[2] = text;
				}
			}
			info[1] = extractUrlFromDialog(dialog);
		} catch (Exception e) {
			bvLog("[BetterVia] 提取下载信息失败: " + e);
		}
		return info;
	}
	private List<TextView> findAllTextViews(View view) {
		List<TextView> textViews = new ArrayList<>();
		if (view instanceof TextView) {
			textViews.add((TextView) view);
		} else if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				textViews.addAll(findAllTextViews(group.getChildAt(i)));
			}
		}
		return textViews;
	}
	private boolean isButtonText(String text) {
		String[] buttonTexts = {"确定", "取消", "复制链接", "OK", "Cancel", "Copy Link", "分享", "Share"};
		for (String buttonText : buttonTexts) {
			if (text.equals(buttonText)) {
				return true;
			}
		}
		return false;
	}
	private String extractUrlFromDialog(Dialog dialog) {
		try {
			if (Context != null && Context instanceof Activity) {
				Activity activity = (Activity) Context;
				try {
					Intent intent = activity.getIntent();
					if (intent != null) {
						Uri data = intent.getData();
						if (data != null) {
							String scheme = data.getScheme();
							if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
								bvLog("[BetterVia] 通过Intent获取到URL: " + data.toString());
								return data.toString();
							}
						}
						String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
						if (extraText != null && (extraText.startsWith("http") || extraText.startsWith("ftp"))) {
							bvLog("[BetterVia] 通过Intent Extra获取到URL: " + extraText);
							return extraText;
						}
					}
				} catch (Throwable t) {
					bvLog("[BetterVia] 尝试解析Intent失败: " + t.getMessage());
				}
				try {
					Object webView = XposedHelpers.getObjectField(activity, "u");
					if (webView instanceof WebView) {
						String url = ((WebView) webView).getUrl();
						if (url != null && !url.isEmpty()) {
							bvLog("[BetterVia] 通过WebView(u)获取到URL: " + url);
							return url;
						}
					}
				} catch (Throwable e1) {
					bvLog("[BetterVia] 字段 'u' 不存在或无法访问 (非Shell Activity): " + e1.getMessage());
					try {
						Object webView = XposedHelpers.getObjectField(activity, "webView");
						if (webView instanceof WebView) {
							String url = ((WebView) webView).getUrl();
							if (url != null && !url.isEmpty()) {
								bvLog("[BetterVia] 通过WebView(webView)获取到URL: " + url);
								return url;
							}
						}
					} catch (Throwable e2) {
					}
				}
				try {
					View currentFocus = activity.getCurrentFocus();
					if (currentFocus instanceof WebView) {
						String url = ((WebView) currentFocus).getUrl();
						if (url != null && !url.isEmpty()) {
							bvLog("[BetterVia] 通过当前焦点WebView获取到URL: " + url);
							return url;
						}
					}
					ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
					WebView webView = findWebViewRecursive(decorView);
					if (webView != null) {
						String url = webView.getUrl();
						if (url != null && !url.isEmpty()) {
							bvLog("[BetterVia] 通过遍历View获取到URL: " + url);
							return url;
						}
					}
				} catch (Throwable t) {
					bvLog("[BetterVia] UI查找WebView失败: " + t.getMessage());
				}
			}
		} catch (Throwable e) {
			bvLog("[BetterVia] 提取URL发生严重错误: " + e.getMessage());
		}
		return "";
	}
	private WebView findWebViewRecursive(View view) {
		if (view instanceof WebView) {
			return (WebView) view;
		} else if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				WebView webView = findWebViewRecursive(group.getChildAt(i));
				if (webView != null) {
					return webView;
				}
			}
		}
		return null;
	}
	private String createShareText(String fileName, String fileSize, String fileUrl, Context ctx) {
		if (!fileUrl.isEmpty()) {
			return fileUrl;
		} else {
			return "";
		}
	}
	private void addMonetMomentItem(LinearLayout parent, final Activity act, final Context ctx) {
		bvLog("[BetterVia] addMonetMomentItem 方法被调用");
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setClickable(true);
		configBtn.setFocusable(true);
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					bvLog("[BetterVia] 莫奈时刻配置按钮被点击");
					showMonetMomentDialog(ctx);
				} catch (Exception e) {
					bvLog("[BetterVia] 莫奈时刻配置按钮点击时发生异常: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
		LinearLayout.LayoutParams configBtnLp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		hor.addView(configBtn, configBtnLp);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void addPrivacyLockItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPrivacyLockDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void addStartupExecutionItem(LinearLayout parent, final Activity act, final Context ctx) {
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		configBtn.setTextColor(getBtnTextColor(ctx));
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showStartupExecutionDialog(ctx);
			}
		});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showMonetMomentDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			return;
		}
		final EditText[] versionInfoEdits = new EditText[2];
		showMonetMomentDialogInternal(ctx, act, null, versionInfoEdits);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ViaVersionInfo latestVersionInfo = null;
				try {
					latestVersionInfo = fetchLatestViaVersion(ctx);
				} catch (Exception e) {
					bvLog("[BetterVia] 获取最新版本信息时发生异常: " + e.getMessage());
					e.printStackTrace();
				}
				final ViaVersionInfo finalVersionInfo = latestVersionInfo;
				bvLog("[BetterVia] 版本信息获取完成，版本名: " + (finalVersionInfo != null ? finalVersionInfo.versionName : "null"));
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (act.isFinishing() || act.isDestroyed()) {
							return;
						}
						if (finalVersionInfo != null && finalVersionInfo.versionName != null
								&& versionInfoEdits[0] != null) {
							versionInfoEdits[0].setText(finalVersionInfo.versionName);
						}
						if (finalVersionInfo != null && finalVersionInfo.versionCode != null
								&& versionInfoEdits[1] != null) {
							versionInfoEdits[1].setText(finalVersionInfo.versionCode);
						}
					}
				});
			}
		}).start();
	}
	private void showMonetMomentDialogInternal(final Context ctx, final Activity act,
			final ViaVersionInfo latestVersionInfo, final EditText[] versionInfoEdits) {
		if (act.isFinishing() || act.isDestroyed()) {
				return;
		}
						final int bgColor = getBgColor(ctx);
						final int titleColor = getTitleColor(ctx);
						final int textColor = getTextColor(ctx);
						final int hintColor = getHintColor(ctx);
						final int btnBgColor = getBtnBgColor(ctx);
						final int btnTextColor = getBtnTextColor(ctx);
						final int okBtnBgColor = getOkBtnBgColor(ctx);
						final int okBtnTextColor = getOkBtnTextColor(ctx);
						final int itemBgColor = getItemBgColor(ctx);
						final int editBgColor = getEditBgColor(ctx);
						final Dialog dialog = new Dialog(act);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER); 
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 6); 
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_hint"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				subtitle.setTextColor(hintColor); 
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 36));
				root.addView(subtitle);
				if (Build.VERSION.SDK_INT < 31) { 
					LinearLayout warningCard = new LinearLayout(act);
					warningCard.setOrientation(LinearLayout.HORIZONTAL);
					warningCard.setGravity(Gravity.CENTER_VERTICAL);
					warningCard.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					GradientDrawable cardBg = new GradientDrawable();
					cardBg.setColor(0xFFFF5252); 
					cardBg.setCornerRadius(dp(act, 12));
					warningCard.setBackground(cardBg);
					ImageView icon = new ImageView(act);
					icon.setImageResource(android.R.drawable.stat_sys_warning);
					icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
					LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 20), dp(act, 20));
					iconLp.rightMargin = dp(act, 10);
					warningCard.addView(icon, iconLp);
					TextView warningText = new TextView(act);
					warningText.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_version_warning"));
					warningText.setTextColor(Color.WHITE);
					warningText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					warningText.setTypeface(null, Typeface.BOLD);
					warningCard.addView(warningText,
							new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
					LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					cardLp.setMargins(0, 0, 0, dp(act, 16)); 
					root.addView(warningCard, cardLp);
				}
				addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_principle_title"),
						true);
				addMonetInfoCard(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_principle_content"));
				addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_notes_title"),
						false);
				addMonetInfoCard(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_notes_content"));
				addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_config_section"),
						false);
				LinearLayout configContainer = new LinearLayout(act);
				configContainer.setOrientation(LinearLayout.VERTICAL);
				configContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12)); 
				GradientDrawable configBg = new GradientDrawable();
				configBg.setColor(itemBgColor); 
				configBg.setCornerRadius(dp(act, 8)); 
				configBg.setStroke(dp(act, 1), getDividerColor(ctx));
				configContainer.setBackground(configBg);
				TextView baseVerLabel = new TextView(act);
				baseVerLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_base_ver"));
				baseVerLabel.setTextColor(hintColor); 
				baseVerLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				baseVerLabel.setTypeface(null, Typeface.BOLD);
				baseVerLabel.setPadding(0, 0, 0, dp(act, 4)); 
				configContainer.addView(baseVerLabel);
				final TextView baseVerSelector = new TextView(act);
				final int savedBaseIdx = getPrefInt(ctx, KEY_MONET_BASE, 0);
				final String[] baseItems = {LocalizedStringProvider.getInstance().get(ctx, "monet_base_cn"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_base_global")};
				baseVerSelector.setText(baseItems[savedBaseIdx]);
				baseVerSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				baseVerSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				GradientDrawable selectorBg = new GradientDrawable();
				selectorBg.setColor(editBgColor);
				selectorBg.setCornerRadius(dp(act, 8));
				selectorBg.setStroke(dp(act, 1), getDividerColor(ctx));
				baseVerSelector.setBackground(selectorBg);
				baseVerSelector.setTextColor(textColor);
				LinearLayout.LayoutParams selectorLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				selectorLp.bottomMargin = dp(act, 12); 
				configContainer.addView(baseVerSelector, selectorLp);
				final CheckBox useIconCb = new CheckBox(act);
				useIconCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_use_icon"));
				useIconCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				useIconCb.setTextColor(textColor);
				useIconCb.setChecked(getPrefBoolean(ctx, KEY_MONET_USE_ICON, false));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					useIconCb.setButtonTintList(colorStateList);
				}
				LinearLayout.LayoutParams useIconCbLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				useIconCbLp.bottomMargin = dp(act, 8);
				configContainer.addView(useIconCb, useIconCbLp);
				final CheckBox makeLiteCb = new CheckBox(act);
				makeLiteCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_make_lite"));
				makeLiteCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				makeLiteCb.setTextColor(textColor);
				makeLiteCb.setChecked(getPrefBoolean(ctx, KEY_MONET_MAKE_LITE, false));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					makeLiteCb.setButtonTintList(colorStateList);
				}
				LinearLayout.LayoutParams makeLiteCbLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				makeLiteCbLp.bottomMargin = dp(act, 12);
				configContainer.addView(makeLiteCb, makeLiteCbLp);
				final EditText pkgEdit = addMonetInput(act, configContainer,
						LocalizedStringProvider.getInstance().get(ctx, "monet_pkg_name"),
						getPrefString(ctx, KEY_MONET_PKG, savedBaseIdx == 0 ? "mark.via" : "mark.via.gp"));
				pkgEdit.setEnabled(false); 
				pkgEdit.setTextColor(hintColor); 
				final String monetVerName = (latestVersionInfo != null && latestVersionInfo.versionName != null)
						? latestVersionInfo.versionName
						: "7.0.0";
				final String monetVerCode = (latestVersionInfo != null && latestVersionInfo.versionCode != null)
						? latestVersionInfo.versionCode
						: "20260211";
				bvLog("[BetterVia] 莫奈时刻使用版本信息 - 版本名: " + monetVerName + ", 版本号: " + monetVerCode);
				final EditText verNameEdit = addMonetInput(act, configContainer,
						LocalizedStringProvider.getInstance().get(ctx, "monet_ver_name"), monetVerName);
				verNameEdit.setEnabled(false);
				verNameEdit.setTextColor(hintColor);
				if (versionInfoEdits != null && versionInfoEdits.length > 0) {
					versionInfoEdits[0] = verNameEdit;
				}
				final EditText verCodeEdit = addMonetInput(act, configContainer,
						LocalizedStringProvider.getInstance().get(ctx, "monet_ver_code"), monetVerCode);
				verCodeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
				verCodeEdit.setEnabled(false);
				verCodeEdit.setTextColor(hintColor);
				if (versionInfoEdits != null && versionInfoEdits.length > 1) {
					versionInfoEdits[1] = verCodeEdit;
				}
				TextView signSchemeLabel = new TextView(act);
				signSchemeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_sign_scheme"));
				signSchemeLabel.setTextColor(hintColor);
				signSchemeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				signSchemeLabel.setTypeface(null, Typeface.BOLD);
				signSchemeLabel.setPadding(0, 0, 0, dp(act, 4));
				configContainer.addView(signSchemeLabel);
				final TextView signSchemeSelector = new TextView(act);
				final int savedSignSchemeIdx = getPrefInt(ctx, KEY_MONET_SIGN_SCHEME, 0);
				final String[] signSchemeItems = {LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v2v3"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v2"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v3"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v2v3"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v2"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v3")};
				signSchemeSelector.setText(signSchemeItems[savedSignSchemeIdx]);
				signSchemeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				signSchemeSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				GradientDrawable signSchemeBg = new GradientDrawable();
				signSchemeBg.setColor(editBgColor);
				signSchemeBg.setCornerRadius(dp(act, 8));
				signSchemeBg.setStroke(dp(act, 1), getDividerColor(ctx));
				signSchemeSelector.setBackground(signSchemeBg);
				signSchemeSelector.setTextColor(textColor);
				LinearLayout.LayoutParams signSchemeLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				signSchemeLp.bottomMargin = dp(act, 12);
				configContainer.addView(signSchemeSelector, signSchemeLp);
				root.addView(configContainer);
				baseVerSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMonetBasePopup(ctx, baseVerSelector, baseItems, new SourceSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								baseVerSelector.setText(baseItems[pos]);
								if (pos == 0) {
									pkgEdit.setText("mark.via");
								} else {
									pkgEdit.setText("mark.via.gp");
								}
								putPrefInt(ctx, KEY_MONET_BASE, pos);
							}
						});
					}
				});
				signSchemeSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMonetBasePopup(ctx, signSchemeSelector, signSchemeItems, new SourceSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								signSchemeSelector.setText(signSchemeItems[pos]);
								putPrefInt(ctx, KEY_MONET_SIGN_SCHEME, pos);
							}
						});
					}
				});
				TextView outputLocationLabel = new TextView(act);
				outputLocationLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_output_location"));
				outputLocationLabel.setTextColor(hintColor);
				outputLocationLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				outputLocationLabel.setTypeface(null, Typeface.BOLD);
				outputLocationLabel.setPadding(0, 0, 0, dp(act, 4));
				configContainer.addView(outputLocationLabel);
				final TextView outputLocationSelector = new TextView(act);
				final int savedOutputLocationIdx = getPrefInt(ctx, KEY_MONET_OUTPUT_LOCATION, 0);
				final String[] outputLocationItems = {
						LocalizedStringProvider.getInstance().get(ctx, "monet_output_external"),
						LocalizedStringProvider.getInstance().get(ctx, "monet_output_internal")};
				outputLocationSelector.setText(outputLocationItems[savedOutputLocationIdx]);
				outputLocationSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				outputLocationSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				GradientDrawable outputLocationBg = new GradientDrawable();
				outputLocationBg.setColor(editBgColor);
				outputLocationBg.setCornerRadius(dp(act, 8));
				outputLocationBg.setStroke(dp(act, 1), getDividerColor(ctx));
				outputLocationSelector.setBackground(outputLocationBg);
				outputLocationSelector.setTextColor(textColor);
				LinearLayout.LayoutParams outputLocationLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				configContainer.addView(outputLocationSelector, outputLocationLp);
				final TextView outputLocationDesc = new TextView(act);
				outputLocationDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
				outputLocationDesc.setTextColor(hintColor);
				outputLocationDesc.setPadding(0, 0, 0, dp(act, 8));
				configContainer.addView(outputLocationDesc);
				final Runnable updateOutputDesc = new Runnable() {
					@Override
					public void run() {
						int idx = getPrefInt(ctx, KEY_MONET_OUTPUT_LOCATION, 0);
						String desc = idx == 0
								? LocalizedStringProvider.getInstance().get(ctx, "monet_output_external_desc")
								: LocalizedStringProvider.getInstance().get(ctx, "monet_output_internal_desc");
						outputLocationDesc.setText(desc);
					}
				};
				updateOutputDesc.run();
				outputLocationSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMonetBasePopup(ctx, outputLocationSelector, outputLocationItems,
								new SourceSelectedCallback() {
									@Override
									public void onSelected(int pos) {
										outputLocationSelector.setText(outputLocationItems[pos]);
										putPrefInt(ctx, KEY_MONET_OUTPUT_LOCATION, pos);
										updateOutputDesc.run();
									}
								});
					}
				});
				addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_agreement_title"),
						false);
				TextView agreementText = new TextView(act);
				agreementText.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_agreement_content"));
				agreementText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13); 
				agreementText.setTextColor(hintColor);
				agreementText.setLineSpacing(dp(act, 3), 1.1f);
				root.addView(agreementText);
				final CheckBox agreeCb = new CheckBox(act);
				agreeCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_i_agree"));
				agreeCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				agreeCb.setTextColor(textColor);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					agreeCb.setButtonTintList(colorStateList);
				}
				LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				cbLp.topMargin = dp(act, 8);
				root.addView(agreeCb, cbLp);
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRowLp.topMargin = dp(act, 16);
				root.addView(btnRow, btnRowLp);
				Button cancelBtn = new Button(act);
				applyClickAnim(cancelBtn);
				cancelBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelBtn.setTextColor(btnTextColor);
				cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				cancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				cancelBtn.setBackground(cancelBg);
				cancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				final Button continueBtn = new Button(act);
				applyClickAnim(continueBtn);
				continueBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_continue"));
				continueBtn.setTextColor(okBtnTextColor);
				continueBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				continueBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable continueBg = new GradientDrawable();
				continueBg.setColor(0xFF9E9E9E);
				continueBg.setCornerRadius(dp(act, 12));
				continueBtn.setBackground(continueBg);
				continueBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				continueBtn.setEnabled(false);
				LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				btnLp.rightMargin = dp(act, 8);
				btnRow.addView(cancelBtn, btnLp);
				btnLp.leftMargin = dp(act, 8);
				btnRow.addView(continueBtn, btnLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					if (metrics.heightPixels < dp(act, 600)) {
						layoutParams.height = (int) (metrics.heightPixels * 0.85);
					}
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				agreeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						continueBtn.setEnabled(isChecked);
						GradientDrawable bg = new GradientDrawable();
						bg.setCornerRadius(dp(act, 12));
						if (isChecked) {
							bg.setColor(okBtnBgColor); 
						} else {
							bg.setColor(0xFF9E9E9E); 
						}
						continueBtn.setBackground(bg);
					}
				});
				cancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				continueBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						putPrefString(ctx, KEY_MONET_PKG, pkgEdit.getText().toString());
						putPrefString(ctx, KEY_MONET_VER_NAME, verNameEdit.getText().toString());
						putPrefString(ctx, KEY_MONET_VER_CODE, verCodeEdit.getText().toString());
						putPrefBoolean(ctx, KEY_MONET_USE_ICON, useIconCb.isChecked());
						putPrefBoolean(ctx, KEY_MONET_MAKE_LITE, makeLiteCb.isChecked());
						int signSchemeIdx = 0;
						for (int i = 0; i < signSchemeItems.length; i++) {
							if (signSchemeSelector.getText().toString().equals(signSchemeItems[i])) {
								signSchemeIdx = i;
								break;
							}
						}
						putPrefInt(ctx, KEY_MONET_SIGN_SCHEME, signSchemeIdx);
						dialog.dismiss();
						startMonetMomentProcess(ctx);
					}
				});
				dialog.show();
		animateDialogEntrance(root, act);
	}
	private void addMonetSectionTitle(LinearLayout parent, Context ctx, String text, boolean isFirst) {
		TextView title = new TextView(ctx);
		title.setText(text);
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		title.setTextColor(getTextColor(ctx)); 
		title.setTypeface(null, Typeface.BOLD);
		title.setPadding(0, isFirst ? 0 : dp(ctx, 24), 0, dp(ctx, 12));
		parent.addView(title);
	}
	private void addMonetInfoCard(LinearLayout parent, Context ctx, String text) {
		LinearLayout card = new LinearLayout(ctx);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(ctx, 12), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12));
		GradientDrawable bg = new GradientDrawable();
		bg.setColor(getItemBgColor(ctx)); 
		bg.setCornerRadius(dp(ctx, 8)); 
		if (!MODULE_THEME_DARK.equals(getActualTheme(ctx))) {
			bg.setStroke(dp(ctx, 1), getDividerColor(ctx)); 
		}
		card.setBackground(bg);
		TextView content = new TextView(ctx);
		content.setText(text);
		content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		content.setTextColor(getTextColor(ctx)); 
		content.setLineSpacing(dp(ctx, 4), 1.2f); 
		card.addView(content);
		parent.addView(card);
	}
	private EditText addMonetInput(Context ctx, LinearLayout parent, String label, String value) {
		TextView labelTv = new TextView(ctx);
		labelTv.setText(label);
		labelTv.setTextColor(getHintColor(ctx)); 
		labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		labelTv.setTypeface(null, Typeface.BOLD);
		labelTv.setPadding(0, 0, 0, dp(ctx, 4));
		parent.addView(labelTv);
		EditText edit = new EditText(ctx);
		edit.setText(value);
		edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		edit.setTextColor(getTextColor(ctx)); 
		GradientDrawable editBg = new GradientDrawable();
		editBg.setColor(getEditBgColor(ctx));
		editBg.setCornerRadius(dp(ctx, 8));
		editBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
		edit.setBackground(editBg);
		edit.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.bottomMargin = dp(ctx, 12); 
		parent.addView(edit, lp);
		return edit;
	}
	private void showMonetBasePopup(final Context ctx, View anchor, String[] items,
			final SourceSelectedCallback callback) {
		final int bgColor = getBgColor(ctx);
		final int textColor = getTextColor(ctx);
		final int dividerColor = getDividerColor(ctx);
		final ListView list = new ListView(ctx);
		list.setDivider(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setSingleLine(true);
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				textView.setTextColor(textColor); 
				return view;
			}
		};
		list.setAdapter(adapter);
		int popupWidth = Math.max(anchor.getWidth(), dp(ctx, 150));
		final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				callback.onSelected(position);
				pop.dismiss();
			}
		});
		GradientDrawable bg = getRoundBg(ctx, bgColor, 12);
		bg.setStroke(dp(ctx, 1), dividerColor); 
		list.setBackground(bg);
		list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
		pop.showAsDropDown(anchor, 0, dp(ctx, 4));
	}
	private void showStartupExecutionDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int titleColor = getTitleColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int btnBgColor = getBtnBgColor(ctx);
				final int btnTextColor = getBtnTextColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final int itemBgColor = getItemBgColor(ctx);
				final int editBgColor = getEditBgColor(ctx);
				final int dividerColor = getDividerColor(ctx);
				final int switchOnColor = getSwitchOnColor(ctx);
				final int switchOffColor = getSwitchOffColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 6);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_dialog_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				LinearLayout enableContainer = new LinearLayout(act);
				enableContainer.setOrientation(LinearLayout.VERTICAL);
				enableContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable enableBg = new GradientDrawable();
				enableBg.setColor(itemBgColor);
				enableBg.setCornerRadius(dp(act, 12));
				enableBg.setStroke(dp(act, 1), dividerColor);
				enableContainer.setBackground(enableBg);
				final Switch executionSwitch = new Switch(act);
				executionSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_enable"));
				executionSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				executionSwitch.setTextColor(textColor);
				executionSwitch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
				executionSwitch.setChecked(StartupExecutionHelper.getStartupExecutionEnable(ctx));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{switchOnColor, switchOffColor};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						executionSwitch.setThumbTintList(colorStateList);
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						executionSwitch.setTrackTintList(colorStateList);
					}
				}
				LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				switchLp.bottomMargin = dp(act, 8);
				enableContainer.addView(executionSwitch, switchLp);
				TextView switchHint = new TextView(act);
				switchHint.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_execution_enable_hint"));
				switchHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				switchHint.setTextColor(hintColor);
				switchHint.setGravity(Gravity.START);
				enableContainer.addView(switchHint);
				executionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						StartupExecutionHelper.setStartupExecutionEnable(ctx, isChecked);
					}
				});
				LinearLayout.LayoutParams enableLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				enableLp.bottomMargin = dp(act, 16);
				root.addView(enableContainer, enableLp);
				LinearLayout imageContainer = createStartupImageCard(act, ctx, bgColor, textColor, hintColor,
						itemBgColor, editBgColor, dividerColor, switchOnColor, switchOffColor, okBtnBgColor,
						okBtnTextColor);
				LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				imageLp.bottomMargin = dp(act, 16);
				root.addView(imageContainer, imageLp);
				LinearLayout musicContainer = createStartupMusicCard(act, ctx, bgColor, textColor, hintColor,
						itemBgColor, editBgColor, dividerColor, switchOnColor, switchOffColor, okBtnBgColor,
						okBtnTextColor);
				LinearLayout.LayoutParams musicLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				musicLp.bottomMargin = dp(act, 16);
				root.addView(musicContainer, musicLp);
				LinearLayout hintContainer = createStartupHintCard(act, ctx, bgColor, textColor, hintColor, itemBgColor,
						editBgColor, dividerColor, switchOnColor, switchOffColor);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 16);
				root.addView(hintContainer, hintLp);
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				btnRow.setPadding(0, dp(act, 24), 0, 0);
				Button cancel = new Button(act);
				applyClickAnim(cancel);
				cancel.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancel.setTextColor(btnTextColor);
				cancel.setBackground(getRoundBg(act, btnBgColor, 12));
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(okBtnTextColor);
				ok.setBackground(getRoundBg(act, okBtnBgColor, 12));
				LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				btnLp.rightMargin = dp(act, 8);
				btnRow.addView(cancel, btnLp);
				btnLp.leftMargin = dp(act, 8);
				btnRow.addView(ok, btnLp);
				root.addView(btnRow);
				scrollRoot.addView(root);
				dialog.setContentView(scrollRoot);
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor);
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private LinearLayout createStartupImageCard(final Activity act, final Context ctx, int bgColor, int textColor,
			int hintColor, int itemBgColor, int editBgColor, int dividerColor, int switchOnColor, int switchOffColor,
			final int okBtnBgColor, final int okBtnTextColor) {
		LinearLayout card = new LinearLayout(act);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(itemBgColor);
		cardBg.setCornerRadius(dp(act, 12));
		cardBg.setStroke(dp(act, 1), dividerColor);
		card.setBackground(cardBg);
		TextView cardTitle = new TextView(act);
		cardTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_title"));
		cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		cardTitle.setTypeface(null, Typeface.BOLD);
		cardTitle.setTextColor(textColor);
		LinearLayout.LayoutParams cardTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cardTitleLp.bottomMargin = dp(act, 8);
		card.addView(cardTitle, cardTitleLp);
		final Switch imageSwitch = new Switch(act);
		imageSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_hint"));
		imageSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		imageSwitch.setTextColor(textColor);
		imageSwitch.setChecked(StartupExecutionHelper.getStartupImageEnable(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{switchOnColor, switchOffColor};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				imageSwitch.setThumbTintList(colorStateList);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				imageSwitch.setTrackTintList(colorStateList);
			}
		}
		LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		switchLp.bottomMargin = dp(act, 12);
		card.addView(imageSwitch, switchLp);
		final LinearLayout previewContainer = new LinearLayout(act);
		previewContainer.setOrientation(LinearLayout.HORIZONTAL);
		previewContainer.setGravity(Gravity.CENTER);
		previewContainer.setPadding(0, 0, 0, dp(act, 8));
		previewContainer.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		ImageView previewImg = new ImageView(act);
		previewImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
		previewImg.setLayoutParams(new LinearLayout.LayoutParams(dp(act, 80), dp(act, 80)));
		previewContainer.addView(previewImg);
		String imagePath = StartupExecutionHelper.getStartupImagePath(ctx);
		if (imagePath != null && !imagePath.isEmpty()) {
			try {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				if (bitmap != null) {
					previewImg.setImageBitmap(bitmap);
				}
			} catch (Exception e) {
			}
		} else {
			previewImg.setBackgroundColor(editBgColor);
		}
		card.addView(previewContainer);
		final TextView durationTitle = new TextView(act);
		durationTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_duration"));
		durationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		durationTitle.setTextColor(textColor);
		durationTitle.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams durationTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		durationTitleLp.topMargin = dp(act, 8);
		durationTitleLp.bottomMargin = dp(act, 4);
		card.addView(durationTitle, durationTitleLp);
		final SeekBar durationSeek = new SeekBar(act);
		durationSeek.setMax(10);
		durationSeek.setProgress(StartupExecutionHelper.getStartupImageDuration(ctx));
		durationSeek.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			durationSeek.setProgressTintList(ColorStateList.valueOf(okBtnBgColor));
			durationSeek.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
		}
		card.addView(durationSeek);
		final TextView durationHint = new TextView(act);
		durationHint
				.setText(String.format(LocalizedStringProvider.getInstance().get(ctx, "startup_image_duration_hint"),
						StartupExecutionHelper.getStartupImageDuration(ctx)));
		durationHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		durationHint.setTextColor(hintColor);
		durationHint.setGravity(Gravity.CENTER);
		durationHint.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams durationHintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		durationHintLp.topMargin = dp(act, 4);
		durationHintLp.bottomMargin = dp(act, 12);
		card.addView(durationHint, durationHintLp);
		final TextView borderColorTitle = new TextView(act);
		borderColorTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_border_color"));
		borderColorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		borderColorTitle.setTextColor(textColor);
		borderColorTitle.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams borderColorTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		borderColorTitleLp.topMargin = dp(act, 8);
		borderColorTitleLp.bottomMargin = dp(act, 4);
		card.addView(borderColorTitle, borderColorTitleLp);
		final EditText borderColorEdit = new EditText(act);
		borderColorEdit.setHint("#RRGGBB");
		borderColorEdit.setText(colorToRgbString(StartupExecutionHelper.getStartupImageBorderColor(ctx)));
		borderColorEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		borderColorEdit.setTextColor(textColor);
		borderColorEdit.setHintTextColor(hintColor);
		borderColorEdit.setBackground(getRoundBg(act, editBgColor, 4));
		borderColorEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		borderColorEdit.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams borderColorEditLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		borderColorEditLp.bottomMargin = dp(act, 4);
		card.addView(borderColorEdit, borderColorEditLp);
		final TextView borderColorHint = new TextView(act);
		borderColorHint.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_border_color_hint"));
		borderColorHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		borderColorHint.setTextColor(hintColor);
		borderColorHint.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams borderColorHintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		borderColorHintLp.topMargin = dp(act, 4);
		borderColorHintLp.bottomMargin = dp(act, 8);
		card.addView(borderColorHint, borderColorHintLp);
		final LinearLayout forceStretchContainer = new LinearLayout(act);
		forceStretchContainer.setOrientation(LinearLayout.HORIZONTAL);
		forceStretchContainer.setGravity(Gravity.CENTER_VERTICAL);
		forceStretchContainer.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams forceStretchContainerLp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		forceStretchContainerLp.topMargin = dp(act, 4);
		forceStretchContainerLp.bottomMargin = dp(act, 12);
		card.addView(forceStretchContainer, forceStretchContainerLp);
		final Switch forceStretchSwitch = new Switch(act);
		forceStretchSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_force_stretch"));
		forceStretchSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		forceStretchSwitch.setTextColor(textColor);
		forceStretchSwitch.setChecked(StartupExecutionHelper.getStartupImageForceStretch(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{switchOnColor, switchOffColor};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				forceStretchSwitch.setThumbTintList(colorStateList);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				forceStretchSwitch.setTrackTintList(colorStateList);
			}
		}
		forceStretchContainer.addView(forceStretchSwitch);
		TextView forceStretchHint = new TextView(act);
		forceStretchHint.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_force_stretch_hint"));
		forceStretchHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		forceStretchHint.setTextColor(hintColor);
		forceStretchHint.setPadding(dp(act, 8), 0, 0, 0);
		forceStretchContainer.addView(forceStretchHint);
		final Button pickBtn = new Button(act);
		applyClickAnim(pickBtn);
		pickBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_pick_btn"));
		pickBtn.setTextColor(okBtnTextColor);
		pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		pickBtn.setBackground(getRoundBg(act, okBtnBgColor, 8));
		pickBtn.setVisibility(StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
		card.addView(pickBtn);
		imageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				StartupExecutionHelper.setStartupImageEnable(ctx, isChecked);
				int visibility = isChecked ? View.VISIBLE : View.GONE;
				previewContainer.setVisibility(visibility);
				durationTitle.setVisibility(visibility);
				durationSeek.setVisibility(visibility);
				durationHint.setVisibility(visibility);
				borderColorTitle.setVisibility(visibility);
				borderColorEdit.setVisibility(visibility);
				borderColorHint.setVisibility(visibility);
				forceStretchContainer.setVisibility(visibility);
				pickBtn.setVisibility(visibility);
			}
		});
		durationSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					if (progress < 1)
						progress = 1;
					StartupExecutionHelper.setStartupImageDuration(ctx, progress);
					durationHint.setText(String.format(
							LocalizedStringProvider.getInstance().get(ctx, "startup_image_duration_hint"), progress));
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		pickBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("image
	private LinearLayout createStartupMusicCard(final Activity act, final Context ctx, int bgColor, int textColor,
			int hintColor, int itemBgColor, int editBgColor, int dividerColor, int switchOnColor, int switchOffColor,
			final int okBtnBgColor, final int okBtnTextColor) {
		LinearLayout card = new LinearLayout(act);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(itemBgColor);
		cardBg.setCornerRadius(dp(act, 12));
		cardBg.setStroke(dp(act, 1), dividerColor);
		card.setBackground(cardBg);
		TextView cardTitle = new TextView(act);
		cardTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_title"));
		cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		cardTitle.setTypeface(null, Typeface.BOLD);
		cardTitle.setTextColor(textColor);
		LinearLayout.LayoutParams cardTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cardTitleLp.bottomMargin = dp(act, 8);
		card.addView(cardTitle, cardTitleLp);
		final Switch musicSwitch = new Switch(act);
		musicSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_hint"));
		musicSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		musicSwitch.setTextColor(textColor);
		musicSwitch.setChecked(StartupExecutionHelper.getStartupMusicEnable(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{switchOnColor, switchOffColor};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				musicSwitch.setThumbTintList(colorStateList);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				musicSwitch.setTrackTintList(colorStateList);
			}
		}
		LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		switchLp.bottomMargin = dp(act, 12);
		card.addView(musicSwitch, switchLp);
		final LinearLayout previewContainer = new LinearLayout(act);
		previewContainer.setOrientation(LinearLayout.HORIZONTAL);
		previewContainer.setGravity(Gravity.CENTER);
		previewContainer.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
		previewContainer.setBackgroundColor(editBgColor);
		previewContainer.setVisibility(StartupExecutionHelper.getStartupMusicEnable(ctx) ? View.VISIBLE : View.GONE);
		TextView previewText = new TextView(act);
		previewText.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_preview"));
		previewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		previewText.setTextColor(hintColor);
		previewContainer.addView(previewText);
		LinearLayout.LayoutParams previewLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		previewLp.bottomMargin = dp(act, 12);
		card.addView(previewContainer, previewLp);
		final Button pickBtn = new Button(act);
		applyClickAnim(pickBtn);
		pickBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_pick_btn"));
		pickBtn.setTextColor(okBtnTextColor);
		pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		pickBtn.setBackground(getRoundBg(act, okBtnBgColor, 8));
		pickBtn.setVisibility(StartupExecutionHelper.getStartupMusicEnable(ctx) ? View.VISIBLE : View.GONE);
		card.addView(pickBtn);
		musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				StartupExecutionHelper.setStartupMusicEnable(ctx, isChecked);
				int visibility = isChecked ? View.VISIBLE : View.GONE;
				previewContainer.setVisibility(visibility);
				pickBtn.setVisibility(visibility);
			}
		});
		previewContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String musicPath = StartupExecutionHelper.getStartupMusicPath(ctx);
				if (musicPath != null && !musicPath.isEmpty()) {
					try {
						final MediaPlayer previewPlayer = new MediaPlayer();
						previewPlayer.setDataSource(musicPath);
						previewPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						previewPlayer.prepare();
						previewPlayer.start();
						previewPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								previewPlayer.release();
							}
						});
					} catch (Exception e) {
						jiguroMessageWithContext(ctx, "播放失败");
					}
				}
			}
		});
		pickBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("audio
	private LinearLayout createStartupHintCard(final Activity act, final Context ctx, int bgColor, int textColor,
			int hintColor, int itemBgColor, int editBgColor, int dividerColor, int switchOnColor, int switchOffColor) {
		LinearLayout card = new LinearLayout(act);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(itemBgColor);
		cardBg.setCornerRadius(dp(act, 12));
		cardBg.setStroke(dp(act, 1), dividerColor);
		card.setBackground(cardBg);
		TextView cardTitle = new TextView(act);
		cardTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_title"));
		cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		cardTitle.setTypeface(null, Typeface.BOLD);
		cardTitle.setTextColor(textColor);
		LinearLayout.LayoutParams cardTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cardTitleLp.bottomMargin = dp(act, 8);
		card.addView(cardTitle, cardTitleLp);
		final Switch hintSwitch = new Switch(act);
		hintSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hint"));
		hintSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		hintSwitch.setTextColor(textColor);
		hintSwitch.setChecked(StartupExecutionHelper.getStartupHintEnable(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{switchOnColor, switchOffColor};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				hintSwitch.setThumbTintList(colorStateList);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				hintSwitch.setTrackTintList(colorStateList);
			}
		}
		LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		switchLp.bottomMargin = dp(act, 12);
		card.addView(hintSwitch, switchLp);
		final TextView typeLabel = new TextView(act);
		typeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type"));
		typeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		typeLabel.setTextColor(textColor);
		typeLabel.setVisibility(StartupExecutionHelper.getStartupHintEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams typeLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		typeLabelLp.bottomMargin = dp(act, 4);
		card.addView(typeLabel, typeLabelLp);
		final TextView typeSelector = new TextView(act);
		final int savedType = StartupExecutionHelper.getStartupHintType(ctx);
		final String[] typeItems = {LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type_custom"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type_hitokoto")};
		typeSelector.setText(typeItems[savedType]);
		typeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		typeSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
		GradientDrawable selectorBg = new GradientDrawable();
		selectorBg.setColor(editBgColor);
		selectorBg.setCornerRadius(dp(act, 8));
		selectorBg.setStroke(dp(act, 1), dividerColor);
		typeSelector.setBackground(selectorBg);
		typeSelector.setTextColor(textColor);
		typeSelector.setVisibility(StartupExecutionHelper.getStartupHintEnable(ctx) ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams typeLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		typeLp.bottomMargin = dp(act, 12);
		card.addView(typeSelector, typeLp);
		final LinearLayout customTextContainer = new LinearLayout(act);
		customTextContainer.setOrientation(LinearLayout.VERTICAL);
		customTextContainer.setVisibility(savedType == 0 ? View.VISIBLE : View.GONE);
		TextView customTextLabel = new TextView(act);
		customTextLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_custom_text"));
		customTextLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		customTextLabel.setTextColor(textColor);
		LinearLayout.LayoutParams customTextLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		customTextLabelLp.bottomMargin = dp(act, 4);
		customTextContainer.addView(customTextLabel, customTextLabelLp);
		EditText customTextEdit = new EditText(act);
		customTextEdit.setHint(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_custom_text_hint"));
		customTextEdit.setText(StartupExecutionHelper.getStartupHintCustomText(ctx));
		customTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		customTextEdit.setTextColor(textColor);
		customTextEdit.setHintTextColor(hintColor);
		customTextEdit.setBackground(getRoundBg(act, editBgColor, 4));
		customTextEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		LinearLayout.LayoutParams customEditLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		customEditLp.bottomMargin = dp(act, 12);
		customTextContainer.addView(customTextEdit, customEditLp);
		card.addView(customTextContainer);
		final LinearLayout hitokotoContainer = new LinearLayout(act);
		hitokotoContainer.setOrientation(LinearLayout.VERTICAL);
		hitokotoContainer.setVisibility(savedType == 1 ? View.VISIBLE : View.GONE);
		TextView apiLabel = new TextView(act);
		apiLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_api"));
		apiLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		apiLabel.setTextColor(textColor);
		LinearLayout.LayoutParams apiLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		apiLabelLp.bottomMargin = dp(act, 4);
		hitokotoContainer.addView(apiLabel, apiLabelLp);
		EditText apiEdit = new EditText(act);
		apiEdit.setHint(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_api_hint"));
		apiEdit.setText(StartupExecutionHelper.getStartupHintHitokotoApi(ctx));
		apiEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		apiEdit.setTextColor(textColor);
		apiEdit.setHintTextColor(hintColor);
		apiEdit.setBackground(getRoundBg(act, editBgColor, 4));
		apiEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		LinearLayout.LayoutParams apiEditLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		apiEditLp.bottomMargin = dp(act, 12);
		hitokotoContainer.addView(apiEdit, apiEditLp);
		TextView hitokotoTypeLabel = new TextView(act);
		hitokotoTypeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type"));
		hitokotoTypeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		hitokotoTypeLabel.setTextColor(textColor);
		LinearLayout.LayoutParams hitokotoTypeLabelLp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		hitokotoTypeLabelLp.bottomMargin = dp(act, 4);
		hitokotoContainer.addView(hitokotoTypeLabel, hitokotoTypeLabelLp);
		TextView hitokotoTypeHint = new TextView(act);
		hitokotoTypeHint.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_select_hint"));
		hitokotoTypeHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		hitokotoTypeHint.setTextColor(hintColor);
		LinearLayout.LayoutParams hitokotoTypeHintLp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		hitokotoTypeHintLp.bottomMargin = dp(act, 8);
		hitokotoContainer.addView(hitokotoTypeHint, hitokotoTypeHintLp);
		LinearLayout checkboxContainer = new LinearLayout(act);
		checkboxContainer.setOrientation(LinearLayout.VERTICAL);
		checkboxContainer.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		GradientDrawable checkboxBg = new GradientDrawable();
		checkboxBg.setColor(editBgColor);
		checkboxBg.setCornerRadius(dp(act, 8));
		checkboxContainer.setBackground(checkboxBg);
		hitokotoContainer.addView(checkboxContainer);
		final String[] hitokotoTypeItems = {
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_anime"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_comic"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_game"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_literature"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_original"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_internet"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_other"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_movie"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_poetry"),
				LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_philosophy")};
		final String[] hitokotoTypeValues = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "k"};
		String savedTypes = StartupExecutionHelper.getStartupHintHitokotoTypes(ctx);
		final boolean[] typeChecked = new boolean[hitokotoTypeValues.length];
		if (savedTypes != null && !savedTypes.isEmpty()) {
			String[] savedTypeArray = savedTypes.split(",");
			for (String typeStr : savedTypeArray) {
				for (int i = 0; i < hitokotoTypeValues.length; i++) {
					if (hitokotoTypeValues[i].equals(typeStr.trim())) {
						typeChecked[i] = true;
						break;
					}
				}
			}
		} else {
			typeChecked[0] = true;
		}
		for (int i = 0; i < hitokotoTypeValues.length; i++) {
			final int index = i;
			CheckBox cb = new CheckBox(act);
			cb.setText(hitokotoTypeItems[i]);
			cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			cb.setTextColor(textColor);
			cb.setChecked(typeChecked[i]);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
						new int[]{-android.R.attr.state_checked}};
				int[] colors = new int[]{switchOnColor, switchOffColor};
				ColorStateList colorStateList = new ColorStateList(states, colors);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					cb.setButtonTintList(colorStateList);
				}
			}
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					typeChecked[index] = isChecked;
					StringBuilder selectedTypes = new StringBuilder();
					for (int j = 0; j < hitokotoTypeValues.length; j++) {
						if (typeChecked[j]) {
							if (selectedTypes.length() > 0) {
								selectedTypes.append(",");
							}
							selectedTypes.append(hitokotoTypeValues[j]);
						}
					}
					StartupExecutionHelper.setStartupHintHitokotoTypes(ctx, selectedTypes.toString());
				}
			});
			checkboxContainer.addView(cb);
		}
		TextView lengthTitle = new TextView(act);
		lengthTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_length_title"));
		lengthTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		lengthTitle.setTextColor(textColor);
		lengthTitle.setTypeface(null, Typeface.BOLD);
		lengthTitle.setPadding(0, dp(act, 12), 0, 0);
		hitokotoContainer.addView(lengthTitle);
		TextView minLengthLabel = new TextView(act);
		minLengthLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_min_length"));
		minLengthLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		minLengthLabel.setTextColor(textColor);
		LinearLayout.LayoutParams minLengthLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		minLengthLabelLp.bottomMargin = dp(act, 4);
		hitokotoContainer.addView(minLengthLabel, minLengthLabelLp);
		final TextView minLengthValue = new TextView(act);
		minLengthValue.setText(String.valueOf(StartupExecutionHelper.getStartupHintMinLength(ctx)));
		minLengthValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		minLengthValue.setTextColor(textColor);
		minLengthValue.setPadding(dp(act, 8), dp(act, 4), dp(act, 8), dp(act, 4));
		GradientDrawable minLengthBg = new GradientDrawable();
		minLengthBg.setColor(editBgColor);
		minLengthBg.setCornerRadius(dp(act, 6));
		minLengthValue.setBackground(minLengthBg);
		hitokotoContainer.addView(minLengthValue);
		SeekBar minLengthSeek = new SeekBar(act);
		minLengthSeek.setMax(50);
		minLengthSeek.setProgress(StartupExecutionHelper.getStartupHintMinLength(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			minLengthSeek.setProgressTintList(ColorStateList.valueOf(switchOnColor));
			minLengthSeek.setThumbTintList(ColorStateList.valueOf(switchOnColor));
		}
		hitokotoContainer.addView(minLengthSeek);
		LinearLayout.LayoutParams minLengthSeekLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		minLengthSeekLp.bottomMargin = dp(act, 12);
		minLengthSeek.setLayoutParams(minLengthSeekLp);
		TextView maxLengthLabel = new TextView(act);
		maxLengthLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_max_length"));
		maxLengthLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		maxLengthLabel.setTextColor(textColor);
		LinearLayout.LayoutParams maxLengthLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		maxLengthLabelLp.bottomMargin = dp(act, 4);
		hitokotoContainer.addView(maxLengthLabel, maxLengthLabelLp);
		final TextView maxLengthValue = new TextView(act);
		maxLengthValue.setText(String.valueOf(StartupExecutionHelper.getStartupHintMaxLength(ctx)));
		maxLengthValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		maxLengthValue.setTextColor(textColor);
		maxLengthValue.setPadding(dp(act, 8), dp(act, 4), dp(act, 8), dp(act, 4));
		GradientDrawable maxLengthBg = new GradientDrawable();
		maxLengthBg.setColor(editBgColor);
		maxLengthBg.setCornerRadius(dp(act, 6));
		maxLengthValue.setBackground(maxLengthBg);
		hitokotoContainer.addView(maxLengthValue);
		SeekBar maxLengthSeek = new SeekBar(act);
		maxLengthSeek.setMax(100);
		maxLengthSeek.setProgress(StartupExecutionHelper.getStartupHintMaxLength(ctx));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			maxLengthSeek.setProgressTintList(ColorStateList.valueOf(switchOnColor));
			maxLengthSeek.setThumbTintList(ColorStateList.valueOf(switchOnColor));
		}
		hitokotoContainer.addView(maxLengthSeek);
		minLengthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					minLengthValue.setText(String.valueOf(progress));
					StartupExecutionHelper.setStartupHintMinLength(ctx, progress);
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		maxLengthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					maxLengthValue.setText(String.valueOf(progress));
					StartupExecutionHelper.setStartupHintMaxLength(ctx, progress);
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		card.addView(hitokotoContainer);
		hintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				StartupExecutionHelper.setStartupHintEnable(ctx, isChecked);
				int visibility = isChecked ? View.VISIBLE : View.GONE;
				typeLabel.setVisibility(visibility);
				typeSelector.setVisibility(visibility);
				customTextContainer.setVisibility(savedType == 0 ? visibility : View.GONE);
				hitokotoContainer.setVisibility(savedType == 1 ? visibility : View.GONE);
			}
		});
		typeSelector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMonetBasePopup(ctx, typeSelector, typeItems, new SourceSelectedCallback() {
					@Override
					public void onSelected(int pos) {
						typeSelector.setText(typeItems[pos]);
						StartupExecutionHelper.setStartupHintType(ctx, pos);
						if (pos == 0) {
							customTextContainer.setVisibility(View.VISIBLE);
							hitokotoContainer.setVisibility(View.GONE);
						} else {
							customTextContainer.setVisibility(View.GONE);
							hitokotoContainer.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		});
		customTextEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				StartupExecutionHelper.setStartupHintCustomText(ctx, s.toString());
			}
		});
		apiEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				StartupExecutionHelper.setStartupHintHitokotoApi(ctx, s.toString());
			}
		});
		return card;
	}
	private void showPrivacyLockEnableWarningDialog(final Activity act, final Context ctx,
			final Switch privacyLockSwitch) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final int btnBgColor = getBtnBgColor(ctx);
				final int btnTextColor = getBtnTextColor(ctx);
				final AlertDialog.Builder builder = new AlertDialog.Builder(act);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 20), dp(act, 20), dp(act, 20), dp(act, 20)); 
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(bgColor);
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				ImageView warningIcon = new ImageView(act);
				warningIcon.setImageResource(android.R.drawable.ic_dialog_alert);
				warningIcon.setColorFilter(0xFFFF6B6B);
				warningIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 80), dp(act, 80)); 
				iconLp.gravity = Gravity.CENTER;
				iconLp.bottomMargin = dp(act, 16);
				root.addView(warningIcon, iconLp);
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable_warning_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				title.setTextColor(textColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 12);
				root.addView(title, titleLp);
				TextView message = new TextView(act);
				message.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable_warning_message"));
				message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				message.setTextColor(textColor);
				message.setGravity(Gravity.START);
				LinearLayout.LayoutParams messageLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				messageLp.bottomMargin = dp(act, 8);
				root.addView(message, messageLp);
				TextView hint = new TextView(act);
				hint.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable_warning_hint"));
				hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				hint.setTextColor(hintColor);
				hint.setGravity(Gravity.START);
				root.addView(hint);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(bgColor);
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
						LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								isPrivacyLockSwitchChanging = true;
								privacyLockSwitch.setChecked(false);
								isPrivacyLockSwitchChanging = false;
								dialog.dismiss();
							}
						});
				dialog.setButton(AlertDialog.BUTTON_POSITIVE,
						LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, true);
								dialog.dismiss();
							}
						});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void showPrivacyLockDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(ctx);
				final int titleColor = getTitleColor(ctx);
				final int textColor = getTextColor(ctx);
				final int hintColor = getHintColor(ctx);
				final int btnBgColor = getBtnBgColor(ctx);
				final int btnTextColor = getBtnTextColor(ctx);
				final int okBtnBgColor = getOkBtnBgColor(ctx);
				final int okBtnTextColor = getOkBtnTextColor(ctx);
				final int itemBgColor = getItemBgColor(ctx);
				final int editBgColor = getEditBgColor(ctx);
				final int dividerColor = getDividerColor(ctx);
				final int switchOnColor = getSwitchOnColor(ctx);
				final int switchOffColor = getSwitchOffColor(ctx);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 6);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_dialog_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 36));
				root.addView(subtitle);
				TextView enableTitle = new TextView(act);
				enableTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable_section"));
				enableTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				enableTitle.setTypeface(null, Typeface.BOLD);
				enableTitle.setTextColor(textColor);
				enableTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams enableTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				enableTitleLp.bottomMargin = dp(act, 12);
				root.addView(enableTitle, enableTitleLp);
				LinearLayout enableContainer = new LinearLayout(act);
				enableContainer.setOrientation(LinearLayout.VERTICAL);
				enableContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				GradientDrawable enableBg = new GradientDrawable();
				enableBg.setColor(itemBgColor);
				enableBg.setCornerRadius(dp(act, 8));
				enableBg.setStroke(dp(act, 1), dividerColor);
				enableContainer.setBackground(enableBg);
				final Switch privacyLockSwitch = new Switch(act);
				privacyLockSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable"));
				privacyLockSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				privacyLockSwitch.setTextColor(textColor);
				privacyLockSwitch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL); 
				privacyLockSwitch.setChecked(getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{switchOnColor, switchOffColor};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						privacyLockSwitch.setThumbTintList(colorStateList);
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						privacyLockSwitch.setTrackTintList(colorStateList);
					}
				}
				LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				switchLp.bottomMargin = dp(act, 8);
				enableContainer.addView(privacyLockSwitch, switchLp);
				TextView switchHint = new TextView(act);
				switchHint.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_enable_hint"));
				switchHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				switchHint.setTextColor(hintColor);
				switchHint.setGravity(Gravity.START); 
				enableContainer.addView(switchHint);
				LinearLayout.LayoutParams enableLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				enableLp.bottomMargin = dp(act, 16);
				root.addView(enableContainer, enableLp);
				TextView notesTitle = new TextView(act);
				notesTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_notes_title"));
				notesTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				notesTitle.setTypeface(null, Typeface.BOLD);
				notesTitle.setTextColor(textColor);
				notesTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams notesTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				notesTitleLp.topMargin = dp(act, 8);
				notesTitleLp.bottomMargin = dp(act, 12);
				root.addView(notesTitle, notesTitleLp);
				LinearLayout notesContainer = new LinearLayout(act);
				notesContainer.setOrientation(LinearLayout.VERTICAL);
				notesContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				GradientDrawable notesBg = new GradientDrawable();
				notesBg.setColor(itemBgColor);
				notesBg.setCornerRadius(dp(act, 8));
				notesBg.setStroke(dp(act, 1), dividerColor);
				notesContainer.setBackground(notesBg);
				TextView notesContent = new TextView(act);
				notesContent.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_notes_content"));
				notesContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				notesContent.setTextColor(textColor);
				notesContent.setLineSpacing(dp(act, 4), 1.2f);
				notesContainer.addView(notesContent);
				LinearLayout.LayoutParams notesLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				notesLp.bottomMargin = dp(act, 16);
				root.addView(notesContainer, notesLp);
				TextView configTitle = new TextView(act);
				configTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_config_section"));
				configTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				configTitle.setTypeface(null, Typeface.BOLD);
				configTitle.setTextColor(textColor);
				configTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams configTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				configTitleLp.bottomMargin = dp(act, 12);
				root.addView(configTitle, configTitleLp);
				LinearLayout applyContainer = new LinearLayout(act);
				applyContainer.setOrientation(LinearLayout.VERTICAL);
				applyContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable applyBg = new GradientDrawable();
				applyBg.setColor(itemBgColor);
				applyBg.setCornerRadius(dp(act, 12));
				applyBg.setStroke(dp(act, 1), dividerColor);
				applyContainer.setBackground(applyBg);
				LinearLayout applyStartupContainer = createPrivacyCheckbox(act, ctx,
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_startup"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_startup_hint"),
						KEY_PRIVACY_LOCK_APPLY_STARTUP, textColor, hintColor, switchOnColor, switchOffColor);
				final CheckBox applyStartupCb = (CheckBox) applyStartupContainer.getChildAt(0);
				LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				cbLp.bottomMargin = dp(act, 12);
				applyContainer.addView(applyStartupContainer, cbLp);
				LinearLayout applyHistoryContainer = createPrivacyCheckbox(act, ctx,
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_history"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_history_hint"),
						KEY_PRIVACY_LOCK_APPLY_HISTORY, textColor, hintColor, switchOnColor, switchOffColor);
				final CheckBox applyHistoryCb = (CheckBox) applyHistoryContainer.getChildAt(0);
				applyContainer.addView(applyHistoryContainer, cbLp);
				LinearLayout applyBookmarksContainer = createPrivacyCheckbox(act, ctx,
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_bookmarks"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_bookmarks_hint"),
						KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, textColor, hintColor, switchOnColor, switchOffColor);
				final CheckBox applyBookmarksCb = (CheckBox) applyBookmarksContainer.getChildAt(0);
				applyContainer.addView(applyBookmarksContainer, cbLp);
				LinearLayout applyOfflineContainer = createPrivacyCheckbox(act, ctx,
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_offline"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_offline_hint"),
						KEY_PRIVACY_LOCK_APPLY_OFFLINE, textColor, hintColor, switchOnColor, switchOffColor);
				final CheckBox applyOfflineCb = (CheckBox) applyOfflineContainer.getChildAt(0);
				applyContainer.addView(applyOfflineContainer, cbLp);
				FrameLayout applyComprehensiveWrapper = new FrameLayout(act);
				LinearLayout applyComprehensiveContainer = createPrivacyCheckbox(act, ctx,
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_comprehensive"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_apply_comprehensive_hint"),
						KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, textColor, hintColor, switchOnColor, switchOffColor);
				final CheckBox applyComprehensiveCb = (CheckBox) applyComprehensiveContainer.getChildAt(0);
				applyComprehensiveWrapper.addView(applyComprehensiveContainer);
				final View comprehensiveLockOverlay = new View(act);
				GradientDrawable lockOverlayBg = new GradientDrawable();
				lockOverlayBg.setColor(0x20000000); 
				lockOverlayBg.setCornerRadius(dp(act, 8)); 
				comprehensiveLockOverlay.setBackground(lockOverlayBg);
				FrameLayout.LayoutParams overlayLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				comprehensiveLockOverlay.setLayoutParams(overlayLp);
				comprehensiveLockOverlay.setVisibility(View.GONE); 
				applyComprehensiveWrapper.addView(comprehensiveLockOverlay);
				applyContainer.addView(applyComprehensiveWrapper, cbLp);
				LinearLayout.LayoutParams applyLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				applyLp.bottomMargin = dp(act, 16);
				root.addView(applyContainer, applyLp);
				final Runnable updateComprehensiveState = new Runnable() {
					@Override
					public void run() {
						boolean anyMiddleThree = applyHistoryCb.isChecked() || applyBookmarksCb.isChecked()
								|| applyOfflineCb.isChecked();
						if (anyMiddleThree) {
							isApplyComprehensiveCbChanging = true;
							applyComprehensiveCb.setChecked(true);
							isApplyComprehensiveCbChanging = false;
							applyComprehensiveCb.setEnabled(false);
							comprehensiveLockOverlay.setVisibility(View.VISIBLE); 
						} else {
							applyComprehensiveCb.setEnabled(true);
							comprehensiveLockOverlay.setVisibility(View.GONE); 
						}
					}
				};
				final class VerifyPasswordExecutor {
					private Runnable actionAfterVerify;
					private Runnable cancelAction;
					public void setAction(Runnable action, Runnable cancel) {
						this.actionAfterVerify = action;
						this.cancelAction = cancel;
					}
					public void execute() {
						if (patternPasswordManager == null) {
							patternPasswordManager = new PasswordManager(act);
						}
						patternPasswordManager.setVerifyMode(true);
						patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
							@Override
							public void onPasswordSet() {
							}
							@Override
							public void onPasswordReset() {
								bvLog("[BetterVia] 修改配置验证成功");
								if (actionAfterVerify != null) {
									actionAfterVerify.run();
								}
							}
							@Override
							public void onVerifySuccess() {
							}
							@Override
							public void onCancelled() {
								bvLog("[BetterVia] 修改配置验证取消");
								if (cancelAction != null) {
									cancelAction.run();
								}
							}
						});
						int currentPasswordType = patternPasswordManager.detectCurrentPasswordType();
						if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
							patternPasswordManager.showVerifyPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "pattern_lock_verify_for_config"));
						} else if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PIN) {
							patternPasswordManager.showVerifyPinPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "pin_lock_verify_for_config"));
						}
					}
				};
				final VerifyPasswordExecutor verifyPasswordAndExecute = new VerifyPasswordExecutor();
				applyStartupCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isApplyStartupCbChanging) {
							return;
						}
						boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
						if (!privacyLockEnabled) {
							putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, isChecked);
							return;
						}
						final boolean newState = isChecked;
						isApplyStartupCbChanging = true;
						buttonView.setChecked(!isChecked);
						isApplyStartupCbChanging = false;
						verifyPasswordAndExecute.setAction(new Runnable() {
							@Override
							public void run() {
								isApplyStartupCbChanging = true;
								applyStartupCb.setChecked(newState);
								isApplyStartupCbChanging = false;
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, newState);
							}
						}, null); 
						verifyPasswordAndExecute.execute();
					}
				});
				applyHistoryCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isApplyHistoryCbChanging) {
							return;
						}
						boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
						if (!privacyLockEnabled) {
							putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, isChecked);
							updateComprehensiveState.run();
							return;
						}
						final boolean newState = isChecked;
						isApplyHistoryCbChanging = true;
						buttonView.setChecked(!isChecked);
						isApplyHistoryCbChanging = false;
						verifyPasswordAndExecute.setAction(new Runnable() {
							@Override
							public void run() {
								isApplyHistoryCbChanging = true;
								applyHistoryCb.setChecked(newState);
								isApplyHistoryCbChanging = false;
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, newState);
								updateComprehensiveState.run();
							}
						}, null); 
						verifyPasswordAndExecute.execute();
					}
				});
				applyBookmarksCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isApplyBookmarksCbChanging) {
							return;
						}
						boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
						if (!privacyLockEnabled) {
							putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, isChecked);
							updateComprehensiveState.run();
							return;
						}
						final boolean newState = isChecked;
						isApplyBookmarksCbChanging = true;
						buttonView.setChecked(!isChecked);
						isApplyBookmarksCbChanging = false;
						verifyPasswordAndExecute.setAction(new Runnable() {
							@Override
							public void run() {
								isApplyBookmarksCbChanging = true;
								applyBookmarksCb.setChecked(newState);
								isApplyBookmarksCbChanging = false;
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, newState);
								updateComprehensiveState.run();
							}
						}, null); 
						verifyPasswordAndExecute.execute();
					}
				});
				applyOfflineCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isApplyOfflineCbChanging) {
							return;
						}
						boolean privacyLockEnabled = getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
						if (!privacyLockEnabled) {
							putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, isChecked);
							updateComprehensiveState.run();
							return;
						}
						final boolean newState = isChecked;
						isApplyOfflineCbChanging = true;
						buttonView.setChecked(!isChecked);
						isApplyOfflineCbChanging = false;
						verifyPasswordAndExecute.setAction(new Runnable() {
							@Override
							public void run() {
								isApplyOfflineCbChanging = true;
								applyOfflineCb.setChecked(newState);
								isApplyOfflineCbChanging = false;
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, newState);
								updateComprehensiveState.run();
							}
						}, null); 
						verifyPasswordAndExecute.execute();
					}
				});
				applyComprehensiveCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isApplyComprehensiveCbChanging) {
							return;
						}
						if (!applyComprehensiveCb.isEnabled()) {
							return;
						}
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, isChecked);
					}
				});
				updateComprehensiveState.run();
				TextView passwordTypeLabel = new TextView(act);
				passwordTypeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_password_type"));
				passwordTypeLabel.setTextColor(hintColor);
				passwordTypeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				passwordTypeLabel.setTypeface(null, Typeface.BOLD);
				passwordTypeLabel.setPadding(0, 0, 0, dp(act, 4));
				root.addView(passwordTypeLabel);
				final TextView passwordTypeSelector = new TextView(act);
				final int savedPasswordType = getPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE, 0);
				final String[] passwordTypeItems = {
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_password_type_pattern"),
						LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_password_type_numeric")};
				passwordTypeSelector.setText(passwordTypeItems[savedPasswordType]);
				passwordTypeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				passwordTypeSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
				GradientDrawable selectorBg = new GradientDrawable();
				selectorBg.setColor(editBgColor);
				selectorBg.setCornerRadius(dp(act, 8));
				selectorBg.setStroke(dp(act, 1), dividerColor);
				passwordTypeSelector.setBackground(selectorBg);
				passwordTypeSelector.setTextColor(textColor);
				LinearLayout.LayoutParams passwordTypeLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				passwordTypeLp.bottomMargin = dp(act, 16);
				root.addView(passwordTypeSelector, passwordTypeLp);
				passwordTypeSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMonetBasePopup(ctx, passwordTypeSelector, passwordTypeItems, new SourceSelectedCallback() {
							@Override
							public void onSelected(int pos) {
								passwordTypeSelector.setText(passwordTypeItems[pos]);
								putPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE, pos);
							}
						});
					}
				});
				final Button setPasswordBtn = new Button(act);
				applyClickAnim(setPasswordBtn);
				SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
				boolean passwordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
				setPasswordBtn.setText(passwordSet
						? LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_reset_password")
						: LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_set_password"));
				setPasswordBtn.setTextColor(okBtnTextColor);
				setPasswordBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				setPasswordBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable setPasswordBtnBg = new GradientDrawable();
				setPasswordBtnBg.setColor(okBtnBgColor);
				setPasswordBtnBg.setCornerRadius(dp(act, 12));
				setPasswordBtn.setBackground(setPasswordBtnBg);
				setPasswordBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams setPasswordBtnLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				setPasswordBtnLp.bottomMargin = dp(act, 8);
				root.addView(setPasswordBtn, setPasswordBtnLp);
				final Button clearPasswordBtn = new Button(act);
				applyClickAnim(clearPasswordBtn);
				clearPasswordBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_clear_password"));
				clearPasswordBtn.setTextColor(0xFFFF0000); 
				clearPasswordBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				clearPasswordBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable clearPasswordBtnBg = new GradientDrawable();
				clearPasswordBtnBg.setColor(btnBgColor);
				clearPasswordBtnBg.setCornerRadius(dp(act, 12));
				clearPasswordBtn.setBackground(clearPasswordBtnBg);
				clearPasswordBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams clearPasswordBtnLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				clearPasswordBtnLp.bottomMargin = dp(act, 16);
				root.addView(clearPasswordBtn, clearPasswordBtnLp);
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRowLp.topMargin = dp(act, 24);
				root.addView(btnRow, btnRowLp);
				Button cancelBtn = new Button(act);
				applyClickAnim(cancelBtn);
				cancelBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelBtn.setTextColor(btnTextColor);
				cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				cancelBtn.setBackground(cancelBg);
				cancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				Button saveBtn = new Button(act);
				applyClickAnim(saveBtn);
				saveBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				saveBtn.setTextColor(okBtnTextColor);
				saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				saveBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable saveBg = new GradientDrawable();
				saveBg.setColor(okBtnBgColor);
				saveBg.setCornerRadius(dp(act, 12));
				saveBtn.setBackground(saveBg);
				saveBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
						1);
				btnLp.rightMargin = dp(act, 8);
				btnRow.addView(cancelBtn, btnLp);
				btnLp.leftMargin = dp(act, 8);
				btnRow.addView(saveBtn, btnLp);
				privacyLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isPrivacyLockSwitchChanging) {
							return;
						}
						if (isChecked) {
							SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
							boolean passwordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET,
									false);
							if (!passwordSet) {
								jiguroMessage(LocalizedStringProvider.getInstance().get(ctx,
										"privacy_lock_not_set_password"));
								isPrivacyLockSwitchChanging = true;
								privacyLockSwitch.setChecked(false);
								isPrivacyLockSwitchChanging = false;
								return;
							}
							showPrivacyLockEnableWarningDialog(act, ctx, privacyLockSwitch);
						} else {
							if (patternPasswordManager == null) {
								patternPasswordManager = new PasswordManager(act);
							}
							patternPasswordManager.setVerifyMode(true);
							patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
								@Override
								public void onPasswordSet() {
								}
								@Override
								public void onPasswordReset() {
									bvLog("[BetterVia] 关闭隐私锁验证成功");
									putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
									jiguroMessage(
											LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_disabled"));
								}
								@Override
								public void onVerifySuccess() {
								}
								@Override
								public void onCancelled() {
									bvLog("[BetterVia] 关闭隐私锁验证取消");
									isPrivacyLockSwitchChanging = true;
									privacyLockSwitch.setChecked(true);
									isPrivacyLockSwitchChanging = false;
								}
							});
							int currentPasswordType = patternPasswordManager.detectCurrentPasswordType();
							if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
								patternPasswordManager.showVerifyPasswordDialog(LocalizedStringProvider.getInstance()
										.get(ctx, "pattern_lock_verify_for_disable"));
							} else if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PIN) {
								patternPasswordManager.showVerifyPinPasswordDialog(
										LocalizedStringProvider.getInstance().get(ctx, "pin_lock_verify_for_disable"));
							}
						}
					}
				});
				cancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				saveBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, privacyLockSwitch.isChecked());
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, applyStartupCb.isChecked());
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, applyHistoryCb.isChecked());
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, applyBookmarksCb.isChecked());
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, applyOfflineCb.isChecked());
						putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE,
								applyComprehensiveCb.isChecked());
						dialog.dismiss();
						jiguroMessage(LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_saved"));
					}
				});
				setPasswordBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
						boolean passwordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET,
								false);
						if (!passwordSet) {
							if (patternPasswordManager == null) {
								patternPasswordManager = new PasswordManager(act);
							}
							final int userSelectedPasswordType = getPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE,
									0);
							patternPasswordManager.setPasswordType(userSelectedPasswordType);
							patternPasswordManager.setTargetPasswordType(userSelectedPasswordType);
							patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
								@Override
								public void onPasswordSet() {
									if (userSelectedPasswordType == 0) {
										jiguroMessage(LocalizedStringProvider.getInstance().get(ctx,
												"pattern_lock_set_success"));
									} else {
										jiguroMessage(
												LocalizedStringProvider.getInstance().get(ctx, "pin_lock_set_success"));
									}
									setPasswordBtn.setText(LocalizedStringProvider.getInstance().get(ctx,
											"privacy_lock_reset_password"));
								}
								@Override
								public void onPasswordReset() {
								}
								@Override
								public void onVerifySuccess() {
								}
								@Override
								public void onCancelled() {
								}
							});
							if (userSelectedPasswordType == 0) {
								patternPasswordManager.showSetPasswordDialog();
							} else {
								patternPasswordManager.showSetPinPasswordDialog();
							}
						} else {
							if (patternPasswordManager == null) {
								patternPasswordManager = new PasswordManager(act);
							}
							patternPasswordManager.setClearingPassword(true);
							patternPasswordManager.setVerifyMode(true);
							final int userSelectedPasswordType = getPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE,
									0);
							patternPasswordManager.setPasswordType(userSelectedPasswordType);
							patternPasswordManager.setTargetPasswordType(userSelectedPasswordType);
							patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
								@Override
								public void onPasswordSet() {
								}
								@Override
								public void onPasswordReset() {
									bvLog("[BetterVia] 重置密码验证成功");
									if (patternPasswordManager == null) {
										patternPasswordManager = new PasswordManager(act);
									}
									patternPasswordManager.setClearingPassword(false);
									patternPasswordManager.setPasswordType(userSelectedPasswordType);
									patternPasswordManager.setTargetPasswordType(userSelectedPasswordType);
									patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
										@Override
										public void onPasswordSet() {
											if (userSelectedPasswordType == 0) {
												jiguroMessage(LocalizedStringProvider.getInstance().get(ctx,
														"pattern_lock_set_success"));
											} else {
												jiguroMessage(LocalizedStringProvider.getInstance().get(ctx,
														"pin_lock_set_success"));
											}
											setPasswordBtn.setText(LocalizedStringProvider.getInstance().get(ctx,
													"privacy_lock_reset_password"));
										}
										@Override
										public void onPasswordReset() {
										}
										@Override
										public void onVerifySuccess() {
										}
										@Override
										public void onCancelled() {
										}
									});
									if (userSelectedPasswordType == 0) {
										patternPasswordManager.showSetPasswordDialog();
									} else {
										patternPasswordManager.showSetPinPasswordDialog();
									}
								}
								@Override
								public void onVerifySuccess() {
								}
								@Override
								public void onCancelled() {
									bvLog("[BetterVia] 重置密码验证取消");
								}
							});
							int currentPasswordType = patternPasswordManager.detectCurrentPasswordType();
							if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
								patternPasswordManager.showVerifyPasswordDialog(
										LocalizedStringProvider.getInstance().get(ctx, "pattern_lock_verify_hint"));
							} else if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PIN) {
								patternPasswordManager.showVerifyPinPasswordDialog(
										LocalizedStringProvider.getInstance().get(ctx, "pin_lock_verify_hint"));
							}
						}
					}
				});
				clearPasswordBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SecurePasswordStorage secureStorageCheck = new SecurePasswordStorage(ctx);
						boolean passwordSet = secureStorageCheck
								.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
						if (!passwordSet) {
							jiguroMessage(
									LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_not_set_password"));
							return;
						}
						if (patternPasswordManager == null) {
							patternPasswordManager = new PasswordManager(act);
						}
						patternPasswordManager.setClearingPassword(true);
						patternPasswordManager.setListener(new PasswordManager.PasswordListener() {
							@Override
							public void onPasswordSet() {
							}
							@Override
							public void onPasswordReset() {
								bvLog("[BetterVia] 清除密码验证成功");
								if (patternPasswordManager == null) {
									patternPasswordManager = new PasswordManager(act);
								}
								patternPasswordManager.clearPasswordData();
								jiguroMessage(LocalizedStringProvider.getInstance().get(ctx,
										"privacy_lock_password_cleared"));
								setPasswordBtn.setText(
										LocalizedStringProvider.getInstance().get(ctx, "privacy_lock_set_password"));
								isPrivacyLockSwitchChanging = true;
								privacyLockSwitch.setChecked(false);
								isPrivacyLockSwitchChanging = false;
								putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
								dialog.dismiss();
							}
							@Override
							public void onVerifySuccess() {
							}
							@Override
							public void onCancelled() {
								bvLog("[BetterVia] 清除密码验证取消");
							}
						});
						int currentPasswordType = patternPasswordManager.detectCurrentPasswordType();
						if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
							patternPasswordManager.showVerifyPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "pattern_lock_verify_for_clear"));
						} else if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PIN) {
							patternPasswordManager.showVerifyPinPasswordDialog(
									LocalizedStringProvider.getInstance().get(ctx, "pin_lock_verify_for_clear"));
						}
					}
				});
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					if (metrics.heightPixels < dp(act, 600)) {
						layoutParams.height = (int) (metrics.heightPixels * 0.85);
					}
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private LinearLayout createPrivacyCheckbox(Activity act, Context ctx, String title, String hint, String prefKey,
			int textColor, int hintColor, int switchOnColor, int switchOffColor) {
		LinearLayout container = new LinearLayout(act);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setGravity(Gravity.CENTER_VERTICAL);
		final CheckBox cb = new CheckBox(act);
		cb.setText("");
		cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		cb.setTextColor(textColor);
		cb.setChecked(getPrivacyLockBoolean(ctx, prefKey, false));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{switchOnColor, switchOffColor};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			cb.setButtonTintList(colorStateList);
		}
		LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cbLp.gravity = Gravity.CENTER_VERTICAL;
		cbLp.rightMargin = dp(act, 8);
		container.addView(cb, cbLp);
		LinearLayout textContainer = new LinearLayout(act);
		textContainer.setOrientation(LinearLayout.VERTICAL);
		TextView titleTv = new TextView(act);
		titleTv.setText(title);
		titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		titleTv.setTextColor(textColor);
		textContainer.addView(titleTv);
		TextView hintTv = new TextView(act);
		hintTv.setText(hint);
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		hintTv.setTextColor(hintColor);
		textContainer.addView(hintTv);
		LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		container.addView(textContainer, textLp);
		cb.setTag(prefKey);
		return container;
	}
	private static boolean getPrefBoolean(Context ctx, String key, boolean def) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (boolean) XposedHelpers.callMethod(sp, "getBoolean", key, def);
		} catch (Exception e) {
			return def;
		}
	}
	private static void putPrefBoolean(Context ctx, String key, boolean value) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putBoolean", key, value);
			XposedHelpers.callMethod(ed, "apply");
		} catch (Exception e) {
			bvLog("[BetterVia] 写入布尔值时失败: " + e);
		}
	}
	private static boolean getPrivacyLockBoolean(Context ctx, String key, boolean def) {
		try {
			SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
			return storage.getSecureBoolean(key, def);
		} catch (Exception e) {
			bvLog("[BetterVia] 读取隐私锁配置失败: " + e);
			return def;
		}
	}
	private static void putPrivacyLockBoolean(Context ctx, String key, boolean value) {
		try {
			SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
			storage.putSecureBoolean(key, value);
		} catch (Exception e) {
			bvLog("[BetterVia] 写入隐私锁配置失败: " + e);
		}
	}
	private static int getPrivacyLockInt(Context ctx, String key, int def) {
		try {
			SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
			return storage.getSecureInt(key, def);
		} catch (Exception e) {
			bvLog("[BetterVia] 读取隐私锁配置失败: " + e);
			return def;
		}
	}
	private static void putPrivacyLockInt(Context ctx, String key, int value) {
		try {
			SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
			storage.putSecureInt(key, value);
		} catch (Exception e) {
			bvLog("[BetterVia] 写入隐私锁配置失败: " + e);
		}
	}
	private void saveLanguageSetting(Context ctx, String lang) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putString", "preferred_language", lang);
			XposedHelpers.callMethod(ed, "apply");
		} catch (Exception e) {
			bvLog("[BetterVia] 保存语言失败: " + e);
		}
	}
	private String getSavedLanguage(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "preferred_language", "auto");
		} catch (Exception e) {
			return "auto";
		}
	}
	private String getModuleTheme(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", KEY_MODULE_THEME, DEFAULT_MODULE_THEME);
		} catch (Exception e) {
			return DEFAULT_MODULE_THEME;
		}
	}
	private String getActualTheme(Context ctx) {
		String theme = getModuleTheme(ctx);
		if (MODULE_THEME_AUTO.equals(theme)) {
			int nightMode = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					nightMode = ctx.getResources().getConfiguration().isNightModeActive() ? 1 : 0;
				}
			} else {
				try {
					Object uiModeManager = ctx.getSystemService(android.content.Context.UI_MODE_SERVICE);
					nightMode = (Integer) XposedHelpers.callMethod(uiModeManager, "getNightMode");
				} catch (Exception e) {
					nightMode = 0;
				}
			}
			return nightMode == 1 ? MODULE_THEME_DARK : MODULE_THEME_LIGHT;
		}
		return theme;
	}
	private int getBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BG_COLOR : LIGHT_BG_COLOR;
	}
	private int getTitleColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_TITLE_COLOR : LIGHT_TITLE_COLOR;
	}
	private int getTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_TEXT_COLOR : LIGHT_TEXT_COLOR;
	}
	private int getHintColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_HINT_COLOR : LIGHT_HINT_COLOR;
	}
	private int getDividerColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_DIVIDER_COLOR : LIGHT_DIVIDER_COLOR;
	}
	private int getBtnBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BTN_BG_COLOR : LIGHT_BTN_BG_COLOR;
	}
	private int getBtnTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BTN_TEXT_COLOR : LIGHT_BTN_TEXT_COLOR;
	}
	private int getOkBtnBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_OK_BTN_BG_COLOR : LIGHT_OK_BTN_BG_COLOR;
	}
	private int getOkBtnTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_OK_BTN_TEXT_COLOR : LIGHT_OK_BTN_TEXT_COLOR;
	}
	private int getSwitchOnColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_SWITCH_ON_COLOR : LIGHT_SWITCH_ON_COLOR;
	}
	private int getSwitchOffColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_SWITCH_OFF_COLOR : LIGHT_SWITCH_OFF_COLOR;
	}
	private int getItemBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_ITEM_BG_COLOR : LIGHT_ITEM_BG_COLOR;
	}
	private int getEditBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_EDIT_BG_COLOR : LIGHT_EDIT_BG_COLOR;
	}
	private Activity getActivityFrom(Context ctx) {
		try {
			if (ctx instanceof Activity) {
				return (Activity) ctx;
			}
			if (currentActivity != null) {
				return currentActivity;
			}
			if (Context != null && Context instanceof Activity) {
				return (Activity) Context;
			}
		} catch (Throwable ignored) {
		}
		return null;
	}
	private void putPrefString(Context ctx, String key, String value) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putString", key, value);
			XposedHelpers.callMethod(ed, "apply");
		} catch (Exception e) {
			bvLog("[BetterVia] 写入字符串值时失败: " + e);
		}
	}
	private String getPrefString(Context ctx, String key, String def) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", key, def);
		} catch (Exception e) {
			return def;
		}
	}
	private Class<?> findClassWithFallback(String simpleClassName, Context ctx, ClassLoader cl) {
		String packageName = ctx.getPackageName();
		String currentClassName = packageName + "." + simpleClassName;
		try {
			Class<?> clazz = XposedHelpers.findClass(currentClassName, cl);
			bvLog("[BetterVia] 找到类: " + currentClassName);
			return clazz;
		} catch (Throwable e) {
			bvLog("[BetterVia] 未找到类: " + currentClassName + "，尝试回退到mark.via");
		}
		String fallbackClassName = "mark.via." + simpleClassName;
		try {
			Class<?> clazz = XposedHelpers.findClass(fallbackClassName, cl);
			bvLog("[BetterVia] 使用回退类: " + fallbackClassName);
			return clazz;
		} catch (Throwable e) {
			bvLog("[BetterVia] 未找到回退类: " + fallbackClassName);
			return null;
		}
	}
	public static String getSavedLanguageStatic(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "preferred_language", "auto");
		} catch (Exception e) {
			return "auto";
		}
	}
	private void copyToClipboard(Context ctx, String text) {
		try {
			ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("Via Command", text);
			clipboard.setPrimaryClip(clip);
		} catch (Exception e) {
			bvLog("[BetterVia] 复制到剪贴板失败: " + e);
		}
	}
	private GradientDrawable getRoundBg(Context ctx, int color, int radiusDp) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(color);
		gd.setCornerRadius(dp(ctx, radiusDp));
		return gd;
	}
	private void updateViaLocale(Context ctx, String lang) {
		try {
			Locale newLoc;
			switch (lang) {
				case "zh-CN" :
					newLoc = Locale.SIMPLIFIED_CHINESE;
					break;
				case "zh-TW" :
					newLoc = Locale.TRADITIONAL_CHINESE;
					break;
				case "en" :
					newLoc = Locale.ENGLISH;
					break;
				default :
					newLoc = Locale.getDefault();
					break;
			}
			Resources res = ctx.getResources();
			Configuration cfg = res.getConfiguration();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				cfg.setLocale(newLoc);
			} else {
				cfg.locale = newLoc;
			}
			res.updateConfiguration(cfg, res.getDisplayMetrics());
			bvLog("[BetterVia] Via语言环境已切换: " + newLoc.toString());
		} catch (Exception e) {
			bvLog("[BetterVia] 切换Locale失败: " + e);
		}
	}
	private void refreshModuleButtonText(Context ctx) {
		if (moduleButtonRef == null)
			return;
		try {
			String newText = LocalizedStringProvider.getInstance().get(ctx, "module_settings");
			XposedHelpers.setObjectField(moduleButtonRef, "a", newText);
			bvLog("[BetterVia] 模块按钮文字已刷新: " + newText);
		} catch (Exception e) {
			bvLog("[BetterVia] 刷新按钮文字失败: " + e);
		}
	}
	private void showLanguageChangeToast(Context ctx, int which) {
		String key;
		switch (which) {
			case 0 :
				key = "toast_language_auto";
				break;
			case 1 :
				key = "toast_language_zh_cn";
				break;
			case 2 :
				key = "toast_language_zh_tw";
				break;
			case 3 :
				key = "toast_language_en";
				break;
			default :
				return;
		}
		jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, key));
	}
	private Toast createCustomToast(final Context context, final String msg, final int duration) {
		if (context == null || msg == null) {
			return null;
		}
		try {
			final Context appContext = context.getApplicationContext();
			if (appContext == null) {
				return null;
			}
			LinearLayout container = new LinearLayout(appContext);
			container.setOrientation(LinearLayout.HORIZONTAL);
			container.setGravity(Gravity.CENTER);
			GradientDrawable bg = new GradientDrawable();
			bg.setColor(0xCC1E1E1E); 
			bg.setCornerRadius(dp(appContext, 22)); 
			container.setBackgroundDrawable(bg);
			int padding = dp(appContext, 18);
			int verticalPadding = dp(appContext, 14);
			container.setPadding(padding, verticalPadding, padding, verticalPadding);
			TextView textView = new TextView(appContext);
			textView.setText(msg);
			textView.setTextColor(Color.WHITE); 
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			textView.setGravity(Gravity.CENTER);
			textView.setMaxWidth(dp(appContext, 280)); 
			container.addView(textView);
			Toast toast = new Toast(appContext);
			toast.setView(container);
			toast.setDuration(duration);
			toast.setGravity(Gravity.BOTTOM, 0, dp(appContext, 122));
			return toast;
		} catch (Exception e) {
			bvLog("[BetterVia] 创建自定义Toast失败: " + e);
			return null;
		}
	}
	private void jiguroMessage(String msg) {
		if (Context == null) {
			bvLog("[BetterVia] Context为null，无法显示Toast: " + msg);
			return;
		}
		Context appContext = Context.getApplicationContext();
		if (appContext == null) {
			bvLog("[BetterVia] Application Context为null，无法显示Toast: " + msg);
			return;
		}
		boolean useCustomToast = getPrefBoolean(Context, KEY_CUSTOM_TOAST, true);
		showToastSafely(appContext, msg, useCustomToast);
	}
	private void jiguroMessageWithContext(Context ctx, String msg) {
		if (ctx == null) {
			bvLog("[BetterVia] Context为null，无法显示Toast: " + msg);
			return;
		}
		try {
			boolean useCustomToast = getPrefBoolean(ctx, KEY_CUSTOM_TOAST, true);
			showToastSafely(ctx, msg, useCustomToast);
		} catch (Exception e) {
			bvLog("[BetterVia] Toast显示异常: " + e);
		}
	}
	private void showToastSafely(final Context context, final String msg, final boolean useCustomToast) {
		if (context == null) {
			bvLog("[BetterVia] Context为null，无法显示Toast: " + msg);
			return;
		}
		if (msg == null || msg.isEmpty()) {
			return;
		}
		final Runnable toastTask = new Runnable() {
			@Override
			public void run() {
				try {
					Context appContext = context.getApplicationContext();
					if (appContext == null) {
						bvLog("[BetterVia] Application Context为null: " + msg);
						return;
					}
					int duration = msg.length() > 20 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
					boolean shown = false;
					if (useCustomToast) {
						try {
							Toast customToast = createCustomToast(appContext, msg, duration);
							if (customToast != null) {
								customToast.show();
								shown = true;
							}
						} catch (Exception customEx) {
							bvLog("[BetterVia] 自定义Toast显示失败，尝试降级: " + customEx.getMessage());
						}
					}
					if (!shown) {
						Toast.makeText(appContext, msg, duration).show();
					}
				} catch (Exception innerException) {
					bvLog("[BetterVia] 主线程Toast异常: " + innerException);
					bvLog("[BetterVia] Toast消息: " + msg);
				}
			}
		};
		if (Looper.myLooper() == Looper.getMainLooper()) {
			toastTask.run();
		} else {
			try {
				Handler mainHandler = new Handler(Looper.getMainLooper());
				mainHandler.post(toastTask);
			} catch (Exception outerException) {
				bvLog("[BetterVia] 安全显示Toast失败: " + outerException);
			}
		}
	}
	private static int dp(Context ctx, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
	}
	private void applyAlertDialogTheme(Activity act, Context ctx, AlertDialog dialog) {
		if (dialog == null || act == null)
			return;
		Window win = dialog.getWindow();
		if (win != null) {
			int bgColor = getBgColor(ctx);
			GradientDrawable bg = new GradientDrawable();
			bg.setColor(bgColor);
			bg.setCornerRadius(dp(act, 24));
			win.setBackgroundDrawable(bg);
			View decorView = win.getDecorView();
			if (decorView != null) {
				int titleId = decorView.getResources().getIdentifier("alertTitle", "id", "android");
				TextView titleView = (TextView) decorView.findViewById(titleId);
				if (titleView != null) {
					titleView.setTextColor(getTitleColor(ctx));
				}
				int messageId = decorView.getResources().getIdentifier("message", "id", "android");
				TextView messageView = (TextView) decorView.findViewById(messageId);
				if (messageView != null) {
					messageView.setTextColor(getTextColor(ctx));
				}
				int buttonPanelId = decorView.getResources().getIdentifier("buttonPanel", "id", "android");
				ViewGroup buttonPanel = (ViewGroup) decorView.findViewById(buttonPanelId);
				if (buttonPanel != null) {
					for (int i = 0; i < buttonPanel.getChildCount(); i++) {
						View child = buttonPanel.getChildAt(i);
						if (child instanceof Button) {
							Button btn = (Button) child;
							btn.setTextColor(getTitleColor(ctx));
							btn.setBackground(null);
						}
					}
				}
			}
		}
	}
	private int getPrefInt(Context ctx, String key, int def) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (int) XposedHelpers.callMethod(sp, "getInt", key, def);
		} catch (Exception e) {
			return def;
		}
	}
	public static String getNetworkSource(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
		} catch (Exception e) {
			return DEFAULT_NETWORK_SOURCE;
		}
	}
	private void putPrefInt(Context ctx, String key, int value) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putInt", key, value);
			XposedHelpers.callMethod(ed, "apply");
		} catch (Exception e) {
			bvLog("[BetterVia] 写入Int值失败: " + e);
		}
	}
	private long getPrefLong(Context ctx, String key, long def) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (long) XposedHelpers.callMethod(sp, "getLong", key, def);
		} catch (Exception e) {
			return def;
		}
	}
	private void putPrefLong(Context ctx, String key, long value) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putLong", key, value);
			XposedHelpers.callMethod(ed, "apply");
		} catch (Exception e) {
			bvLog("[BetterVia] 写入Long值失败: " + e);
		}
	}
	private boolean shouldBlockHook(Context ctx, String packageName) {
		File privateConfig = new File(ctx.getExternalFilesDir(null), ".bettervia/config.json");
		if (privateConfig.exists() && hasWhiteConfigurationConfig(privateConfig)) {
			return true;
		}
		File publicConfig = new File("/storage/emulated/0/.bettervia/config.json");
		if (publicConfig.exists() && hasWhiteConfigurationConfig(publicConfig)) {
			return true;
		}
		return false;
	}
	private boolean hasWhiteConfigurationConfig(File configFile) {
		try {
			FileInputStream fis = new FileInputStream(configFile);
			byte[] buffer = new byte[(int) configFile.length()];
			fis.read(buffer);
			fis.close();
			String content = new String(buffer, "UTF-8");
			JSONObject config = new JSONObject(content);
			return config.has("whiteConfiguration");
		} catch (Exception e) {
			return false;
		}
	}
	private void showBlockedToast(final Context ctx) {
		if (hasShownBlockedToast)
			return;
		hasShownBlockedToast = true;
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast toast = new Toast(ctx);
				TextView textView = new TextView(ctx);
				textView.setPadding(32, 24, 32, 24);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(14);
				textView.setBackgroundColor(0xCC000000);
				textView.setText("检测到模块非法，Hook 功能被禁用。\n请安装正版模块，并在授予其 Root 或文件读写权限后进入模块主页→打开右上角菜单→选择安全修复，以恢复被禁用功能。");
				toast.setView(textView);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}
	private void animateDialogEntrance(final ViewGroup root, final Activity act) {
		if (root == null || act == null)
			return;
		final int childCount = root.getChildCount();
		final int baseDelay = 60; 
		int delay = 0;
		if (childCount > 0) {
			final View maybeTitle = root.getChildAt(0);
			maybeTitle.setAlpha(0f);
			maybeTitle.setTranslationY(dp(act, 6));
			maybeTitle.setScaleX(0.98f);
			maybeTitle.setScaleY(0.98f);
			maybeTitle.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setStartDelay(delay).setDuration(320)
					.setInterpolator(new OvershootInterpolator(1.0f)).start();
			delay += baseDelay;
		}
		for (int i = 1; i < childCount; i++) {
			final View v = root.getChildAt(i);
			if (v == null)
				continue;
			v.setAlpha(0f);
			v.setTranslationY(dp(act, 10));
			v.animate().alpha(1f).translationY(0f).setStartDelay(delay).setDuration(220)
					.setInterpolator(new DecelerateInterpolator()).start();
			delay += baseDelay;
		}
	}
	private void applyClickAnim(final View v) {
		if (v == null)
			return;
		v.animate().cancel();
		v.setClickable(true);
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						view.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
								.setInterpolator(new DecelerateInterpolator()).start();
						break;
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_CANCEL :
						view.animate().scaleX(1f).scaleY(1f).setDuration(120)
								.setInterpolator(new OvershootInterpolator(1.1f)).start();
						break;
				}
				return false; 
			}
		});
	}
	private void checkUpdateOnStart(final Context ctx) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					checkUpdate(ctx);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private void checkUpdate(final Context ctx) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String updateUrl = networkSource.equals(NETWORK_SOURCE_GITEE)
							? GITEE_UPDATE_URL
							: GITHUB_UPDATE_URL;
					URL url = new URL(updateUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
						return;
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					br.close();
					conn.disconnect();
					String jsonResponse = sb.toString();
					JSONObject json = new JSONObject(jsonResponse);
					final String remoteVersion = json.getString("versionName");
					final String apkUrl = json.getString("apkUrl");
					String updateLog = "";
					try {
						JSONObject updateLogJson = json.getJSONObject("updateLog");
						String currentLang = getCurrentLanguageCode(ctx);
						if (updateLogJson.has(currentLang)) {
							updateLog = updateLogJson.getString(currentLang);
						} else {
							updateLog = updateLogJson.getString("en");
						}
					} catch (JSONException e) {
						updateLog = json.getString("updateLog");
					}
					String localVersion = MODULE_VERSION_NAME;
					if (!remoteVersion.equals(localVersion)) {
						if (Context != null && Context instanceof Activity) {
							final String finalUpdateLog = updateLog;
							((Activity) Context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showUpdateDialog(ctx, remoteVersion, finalUpdateLog, apkUrl);
								}
							});
						}
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
	private String getCurrentLanguageCode(Context ctx) {
		String saved = getSavedLanguage(ctx);
		if ("auto".equals(saved)) {
			Locale locale;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				locale = ctx.getResources().getConfiguration().getLocales().get(0);
			} else {
				locale = ctx.getResources().getConfiguration().locale;
			}
			if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
				return "zh-CN";
			} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
				return "zh-TW";
			}
			return "en";
		}
		return saved;
	}
	private void showUpdateDialog(final Context ctx, final String version, final String updateLog,
			final String apkUrl) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.START);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView versionTitle = new TextView(act);
				versionTitle.setText(
						String.format(LocalizedStringProvider.getInstance().get(ctx, "new_version_found"), version));
				versionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				versionTitle.setTextColor(getTextColor(ctx));
				versionTitle.setTypeface(null, Typeface.BOLD);
				versionTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams versionLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				versionLp.bottomMargin = dp(act, 16);
				root.addView(versionTitle, versionLp);
				TextView logTitle = new TextView(act);
				logTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "update_log_title"));
				logTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				logTitle.setTextColor(getHintColor(ctx));
				logTitle.setTypeface(null, Typeface.BOLD);
				logTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams logTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				logTitleLp.bottomMargin = dp(act, 8);
				root.addView(logTitle, logTitleLp);
				LinearLayout logContainer = new LinearLayout(act);
				logContainer.setOrientation(LinearLayout.VERTICAL);
				logContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable logBg = new GradientDrawable();
				logBg.setColor(getItemBgColor(ctx));
				logBg.setCornerRadius(dp(act, 12));
				logContainer.setBackground(logBg);
				LinearLayout.LayoutParams containerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				containerLp.bottomMargin = dp(act, 20);
				root.addView(logContainer, containerLp);
				TextView logContent = new TextView(act);
				logContent.setText(updateLog);
				logContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				logContent.setTextColor(getHintColor(ctx));
				logContent.setLineSpacing(dp(act, 4), 1.2f);
				logContent.setGravity(Gravity.START);
				logContainer.addView(logContent);
				LinearLayout buttonLayout = new LinearLayout(act);
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER);
				Button laterButton = new Button(act);
				applyClickAnim(laterButton);
				laterButton.setText(LocalizedStringProvider.getInstance().get(ctx, "later"));
				laterButton.setTextColor(getTitleColor(ctx));
				laterButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				laterButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable laterBg = new GradientDrawable();
				laterBg.setColor(getBtnBgColor(ctx));
				laterBg.setCornerRadius(dp(act, 12));
				laterButton.setBackground(laterBg);
				laterButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams laterLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				laterLp.rightMargin = dp(act, 8);
				buttonLayout.addView(laterButton, laterLp);
				Button downloadButton = new Button(act);
				applyClickAnim(downloadButton);
				downloadButton.setText(LocalizedStringProvider.getInstance().get(ctx, "download_now"));
				downloadButton.setTextColor(getOkBtnTextColor(ctx));
				downloadButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				downloadButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable downloadBg = new GradientDrawable();
				downloadBg.setColor(getOkBtnBgColor(ctx));
				downloadBg.setCornerRadius(dp(act, 12));
				downloadButton.setBackground(downloadBg);
				downloadButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams downloadLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				downloadLp.leftMargin = dp(act, 8);
				buttonLayout.addView(downloadButton, downloadLp);
				root.addView(buttonLayout);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).setCancelable(false)
						.create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				laterButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				downloadButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
							act.startActivity(intent);
							dialog.dismiss();
						} catch (Exception e) {
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(ctx, "cannot_open_download_link"));
						}
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void showAboutDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(getBgColor(ctx)); 
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				int baseTitleColor = getTitleColor(ctx);
				title.setTextColor(baseTitleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				ObjectAnimator colorAnim = ObjectAnimator.ofInt(title, "textColor", baseTitleColor, 0xFFFF6B35,
						0xFF4CD964, 0xFF5AC8FA, baseTitleColor);
				colorAnim.setDuration(2000);
				colorAnim.setEvaluator(new ArgbEvaluator());
				colorAnim.setRepeatCount(ObjectAnimator.INFINITE);
				colorAnim.start();
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(getHintColor(ctx)); 
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				TextView moduleTitle = new TextView(act);
				moduleTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_module_title"));
				moduleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				moduleTitle.setTextColor(getTextColor(ctx)); 
				moduleTitle.setTypeface(null, Typeface.BOLD);
				moduleTitle.setPadding(0, 0, 0, dp(act, 12));
				root.addView(moduleTitle);
				addAboutItem(root, act, LocalizedStringProvider.getInstance().get(ctx, "about_version"),
						MODULE_VERSION_NAME);
				addAboutItem(root, act, LocalizedStringProvider.getInstance().get(ctx, "about_author"), "JiGuro");
				addClickableAboutItem(root, act, LocalizedStringProvider.getInstance().get(ctx, "about_github"),
						"https:
							@Override
							public void onClick(View v) {
								openUrl(act, "https:
								jiguroMessageWithContext(act,
										LocalizedStringProvider.getInstance().get(ctx, "start_url_message"));
							}
						});
				addClickableAboutItem(root, act, LocalizedStringProvider.getInstance().get(ctx, "about_gitee"),
						"https:
							@Override
							public void onClick(View v) {
								openUrl(act, "https:
								jiguroMessageWithContext(act,
										LocalizedStringProvider.getInstance().get(ctx, "start_url_message"));
							}
						});
				addClickableAboutItem(root, act, LocalizedStringProvider.getInstance().get(ctx, "about_xposed"),
						LocalizedStringProvider.getInstance().get(ctx, "about_xposed_repo"),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								openUrl(act, "https:
								jiguroMessageWithContext(act,
										LocalizedStringProvider.getInstance().get(ctx, "start_url_message"));
							}
						});
				TextView updateTitle = new TextView(act);
				updateTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_update_title"));
				updateTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				updateTitle.setTextColor(getTextColor(ctx)); 
				updateTitle.setTypeface(null, Typeface.BOLD);
				updateTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
				root.addView(updateTitle);
				LinearLayout updateContainer = new LinearLayout(act);
				updateContainer.setOrientation(LinearLayout.VERTICAL);
				updateContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
				GradientDrawable updateBg = new GradientDrawable();
				updateBg.setColor(getItemBgColor(ctx)); 
				updateBg.setCornerRadius(dp(act, 8));
				updateBg.setStroke(dp(act, 1), getDividerColor(ctx)); 
				updateContainer.setBackground(updateBg);
				String[] updateLogs = {LocalizedStringProvider.getInstance().get(ctx, "about_update_log0"),
						LocalizedStringProvider.getInstance().get(ctx, "about_update_log1"),
						LocalizedStringProvider.getInstance().get(ctx, "about_update_log2"),
						LocalizedStringProvider.getInstance().get(ctx, "about_update_log3"),
						LocalizedStringProvider.getInstance().get(ctx, "about_update_log4"),
						LocalizedStringProvider.getInstance().get(ctx, "about_update_log5")};
				for (String log : updateLogs) {
					TextView logItem = new TextView(act);
					logItem.setText("• " + log);
					logItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					logItem.setTextColor(getTextColor(ctx)); 
					logItem.setPadding(0, dp(act, 4), 0, dp(act, 4));
					updateContainer.addView(logItem);
				}
				root.addView(updateContainer);
				TextView thanksTitle = new TextView(act);
				thanksTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_thanks_title"));
				thanksTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				thanksTitle.setTextColor(getTextColor(ctx)); 
				thanksTitle.setTypeface(null, Typeface.BOLD);
				thanksTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
				root.addView(thanksTitle);
				addAboutItem(root, act, "", LocalizedStringProvider.getInstance().get(ctx, "about_thanks_content"));
				addAboutItem(root, act, "", LocalizedStringProvider.getInstance().get(ctx, "about_licence"));
				addAboutItem(root, act, "", LocalizedStringProvider.getInstance().get(ctx, "about_licence_apksig"));
				addClickableAboutItem(root, act, "", "Coolapk @半烟半雨溪桥畔", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openUrl(act, "https:
						jiguroMessageWithContext(act,
								LocalizedStringProvider.getInstance().get(ctx, "start_url_message"));
					}
				});
				addClickableAboutItem(root, act, "", "Blog @sgfox", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openUrl(act, "https:
						jiguroMessageWithContext(act,
								LocalizedStringProvider.getInstance().get(ctx, "start_url_message"));
					}
				});
				addAboutItem(root, act, "", LocalizedStringProvider.getInstance().get(ctx, "about_thanks_others"));
				TextView agreementTitle = new TextView(act);
				agreementTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "about_agreement_title"));
				agreementTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				agreementTitle.setTextColor(getTextColor(ctx)); 
				agreementTitle.setTypeface(null, Typeface.BOLD);
				agreementTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
				root.addView(agreementTitle);
				addClickableAboutItem(root, act, "", "《用户协议》 (简体中文)", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loadUserAgreementUrlWithLanguage(ctx, "zh_CN");
					}
				});
				addClickableAboutItem(root, act, "", "《用戶協議》 (繁體中文)", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loadUserAgreementUrlWithLanguage(ctx, "zh_TW");
					}
				});
				addClickableAboutItem(root, act, "", "《User Agreement》 (English)", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loadUserAgreementUrlWithLanguage(ctx, "en");
					}
				});
				TextView withdrawAgreementText = new TextView(act);
				withdrawAgreementText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				withdrawAgreementText.setTextColor(getTextColor(ctx)); 
				withdrawAgreementText.setPadding(0, dp(act, 12), 0, dp(act, 8));
				withdrawAgreementText.setMovementMethod(LinkMovementMethod.getInstance()); 
				String fullText = LocalizedStringProvider.getInstance().get(ctx, "about_withdraw_agreement");
				bvLog("[BetterVia] fullText: " + fullText);
				String highlightText;
				String savedLang = getSavedLanguage(ctx);
				bvLog("[BetterVia] savedLang: " + savedLang);
				if ("auto".equals(savedLang)) {
					Locale locale;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						locale = ctx.getResources().getConfiguration().getLocales().get(0);
					} else {
						locale = ctx.getResources().getConfiguration().locale;
					}
					if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
						highlightText = "撤回用户协议同意";
					} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
						highlightText = "撤回用戶協議同意";
					} else {
						highlightText = "Withdraw Agreement";
					}
				} else if ("zh-CN".equals(savedLang)) {
					highlightText = "撤回用户协议同意";
				} else if ("zh-TW".equals(savedLang)) {
					highlightText = "撤回用戶協議同意";
				} else {
					highlightText = "Withdraw Agreement";
				}
				bvLog("[BetterVia] highlightText: " + highlightText);
				int startIndex = fullText.indexOf(highlightText);
				bvLog("[BetterVia] startIndex: " + startIndex);
				if (startIndex >= 0) {
					SpannableString spannableString = new SpannableString(fullText);
					final int highlightColor = getTitleColor(act); 
					spannableString.setSpan(new ClickableSpan() {
						@Override
						public void onClick(View widget) {
							bvLog("[BetterVia] 点击了撤回用户协议同意");
							showWithdrawAgreementDialog(ctx);
						}
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(highlightColor); 
							ds.setUnderlineText(false); 
						}
					}, startIndex, startIndex + highlightText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					withdrawAgreementText.setText(spannableString);
				} else {
					bvLog("[BetterVia] 没有找到highlightText，使用普通文本");
					withdrawAgreementText.setText(fullText);
				}
				root.addView(withdrawAgreementText);
				Button ok = new Button(act);
				applyClickAnim(ok);
				ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				ok.setTextColor(getOkBtnTextColor(ctx)); 
				ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				ok.setTypeface(null, Typeface.BOLD);
				GradientDrawable btnBg = new GradientDrawable();
				btnBg.setColor(getOkBtnBgColor(ctx)); 
				btnBg.setCornerRadius(dp(act, 12));
				ok.setBackground(btnBg);
				ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = dp(act, 16);
				root.addView(ok, okLp);
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawableResource(android.R.color.transparent);
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.9);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				animateDialogEntrance(root, act);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						jiguroMessageWithContext(act,
								LocalizedStringProvider.getInstance().get(ctx, "thank_user_toast"));
					}
				}, 1000);
			}
		});
	}
	private void showWithdrawAgreementDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle(LocalizedStringProvider.getInstance().get(ctx, "withdraw_agreement_dialog_title"));
				builder.setMessage(LocalizedStringProvider.getInstance().get(ctx, "withdraw_agreement_dialog_message"));
				builder.setPositiveButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								putPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
								bvLog("[BetterVia] 用户已撤回用户协议同意，等待SharedPreferences写入完成");
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										bvLog("[BetterVia] SharedPreferences写入完成，退出Via");
										act.finish();
										System.exit(0);
									}
								}, 500); 
							}
						});
				builder.setNegativeButton(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"), null);
				AlertDialog dialog = builder.create();
				applyAlertDialogTheme(act, ctx, dialog);
				dialog.show();
			}
		});
	}
	private void showShisuiDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				LinearLayout dialogContainer = new LinearLayout(act);
				dialogContainer.setOrientation(LinearLayout.VERTICAL);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(getBgColor(ctx)); 
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(getTitleColor(ctx)); 
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_dialog_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				subtitle.setTextColor(getHintColor(ctx)); 
				subtitle.setGravity(Gravity.CENTER);
				subtitle.setPadding(0, 0, 0, dp(act, 24));
				root.addView(subtitle);
				final TextView loadingText = new TextView(act);
				loadingText.setText(LocalizedStringProvider.getInstance().get(ctx, "shisui_loading"));
				loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				loadingText.setTextColor(getHintColor(ctx)); 
				loadingText.setGravity(Gravity.CENTER);
				loadingText.setPadding(0, dp(act, 24), 0, dp(act, 24));
				root.addView(loadingText);
				final LinearLayout contentContainer = new LinearLayout(act);
				contentContainer.setOrientation(LinearLayout.VERTICAL);
				root.addView(contentContainer);
				String savedSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
				final String shisuiUrl = savedSource.equals(NETWORK_SOURCE_GITEE)
						? GITEE_SHISUI_JSON_URL
						: GITHUB_SHISUI_JSON_URL;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							URL url = new URL(shisuiUrl);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setRequestMethod("GET");
							conn.setConnectTimeout(10000);
							conn.setReadTimeout(15000);
							conn.connect();
							if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
								InputStream is = conn.getInputStream();
								BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
								StringBuilder sb = new StringBuilder();
								String line;
								while ((line = reader.readLine()) != null) {
									sb.append(line);
								}
								reader.close();
								is.close();
								conn.disconnect();
								final String jsonData = sb.toString();
								act.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											root.removeView(loadingText);
											JSONArray jsonArray = new JSONArray(jsonData);
											String lastYear = "";
											for (int i = 0; i < jsonArray.length(); i++) {
												JSONObject item = jsonArray.getJSONObject(i);
												final String year = item.getString("year");
												final String version = item.getString("version");
												final String content = item.getString("content");
												if (!year.equals(lastYear)) {
													TextView yearTitle = new TextView(act);
													yearTitle.setText(year);
													yearTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
													yearTitle.setTextColor(getTextColor(ctx)); 
													yearTitle.setTypeface(null, Typeface.BOLD);
													yearTitle.setPadding(0, dp(act, 16), 0, dp(act, 8));
													contentContainer.addView(yearTitle);
													lastYear = year;
												}
												LinearLayout versionContainer = new LinearLayout(act);
												versionContainer.setOrientation(LinearLayout.HORIZONTAL);
												versionContainer.setGravity(Gravity.CENTER_VERTICAL);
												TextView versionTitle = new TextView(act);
												versionTitle.setText(version);
												versionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
												versionTitle.setTextColor(getTitleColor(ctx)); 
												versionTitle.setTypeface(null, Typeface.BOLD);
												versionTitle.setPadding(0, 0, dp(act, 8), dp(act, 4));
												versionContainer.addView(versionTitle, new LinearLayout.LayoutParams(0,
														ViewGroup.LayoutParams.WRAP_CONTENT, 1));
												TextView copyBtn = new TextView(act);
												applyClickAnim(copyBtn);
												copyBtn.setText(
														LocalizedStringProvider.getInstance().get(ctx, "shisui_copy"));
												copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
												copyBtn.setTextColor(getBtnTextColor(ctx)); 
												copyBtn.setPadding(dp(act, 8), dp(act, 4), dp(act, 8), dp(act, 4));
												copyBtn.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12)); 
												copyBtn.setOnClickListener(new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														StringBuilder copyContent = new StringBuilder();
														copyContent.append(version).append("\n");
														copyContent.append(content.replace("\\r\\n", "\n"));
														ClipboardManager clipboard = (ClipboardManager) act
																.getSystemService(Context.CLIPBOARD_SERVICE);
														ClipData clip = ClipData.newPlainText("Via Shisui",
																copyContent.toString());
														clipboard.setPrimaryClip(clip);
														jiguroMessageWithContext(act, LocalizedStringProvider
																.getInstance().get(ctx, "shisui_copied"));
													}
												});
												versionContainer.addView(copyBtn);
												contentContainer.addView(versionContainer);
												String[] lines = content.split("\\\\r\\\\n");
												for (String line : lines) {
													TextView contentText = new TextView(act);
													contentText.setText(line);
													contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
													contentText.setTextColor(getTextColor(ctx)); 
													contentText.setPadding(dp(act, 8), dp(act, 4), dp(act, 8),
															dp(act, 4));
													contentContainer.addView(contentText);
												}
												if (i < jsonArray.length() - 1) {
													View divider = new View(act);
													divider.setLayoutParams(new LinearLayout.LayoutParams(
															ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1)));
													divider.setBackgroundColor(getDividerColor(ctx)); 
													LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(
															ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1));
													dividerLp.setMargins(0, dp(act, 12), 0, dp(act, 12));
													divider.setLayoutParams(dividerLp);
													contentContainer.addView(divider);
												}
											}
											View finalDivider = new View(act);
											finalDivider.setLayoutParams(new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1)));
											finalDivider.setBackgroundColor(getDividerColor(ctx)); 
											LinearLayout.LayoutParams finalDividerLp = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1));
											finalDividerLp.setMargins(0, dp(act, 12), 0, dp(act, 8));
											finalDivider.setLayoutParams(finalDividerLp);
											contentContainer.addView(finalDivider);
											TextView continuedText = new TextView(act);
											continuedText.setText(
													LocalizedStringProvider.getInstance().get(ctx, "to_be_continued"));
											continuedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
											continuedText.setTextColor(getHintColor(ctx)); 
											continuedText.setTypeface(null, Typeface.ITALIC);
											continuedText.setGravity(Gravity.CENTER);
											continuedText.setPadding(0, 0, 0, dp(act, 16));
											contentContainer.addView(continuedText);
											LinearLayout bottomContainer = new LinearLayout(act);
											bottomContainer.setOrientation(LinearLayout.HORIZONTAL);
											bottomContainer.setGravity(Gravity.CENTER);
											TextView bottomText = new TextView(act);
											bottomText.setText(LocalizedStringProvider.getInstance().get(ctx,
													"shisui_source_credit") + " ");
											bottomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
											bottomText.setTextColor(getHintColor(ctx)); 
											bottomContainer.addView(bottomText);
											SpannableString ss = new SpannableString("sgfox");
											ClickableSpan clickableSpan = new ClickableSpan() {
												@Override
												public void onClick(View widget) {
													openUrl(act, "https:
													jiguroMessageWithContext(act, LocalizedStringProvider.getInstance()
															.get(ctx, "url_opened"));
												}
											};
											ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
											ss.setSpan(new ForegroundColorSpan(getTitleColor(ctx)), 0, ss.length(), 
													Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
											TextView sgfoxText = new TextView(act);
											sgfoxText.setText(ss);
											sgfoxText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
											sgfoxText.setMovementMethod(LinkMovementMethod.getInstance());
											bottomContainer.addView(sgfoxText);
											LinearLayout.LayoutParams bottomLp = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													ViewGroup.LayoutParams.WRAP_CONTENT);
											bottomLp.gravity = Gravity.CENTER;
											bottomLp.topMargin = dp(act, 8);
											contentContainer.addView(bottomContainer, bottomLp);
										} catch (JSONException e) {
											loadingText.setText(LocalizedStringProvider.getInstance().get(ctx,
													"shisui_load_failed"));
										}
									}
								});
							} else {
								act.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										loadingText.setText(
												LocalizedStringProvider.getInstance().get(ctx, "shisui_load_failed"));
									}
								});
							}
						} catch (Exception e) {
							act.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									loadingText.setText(
											LocalizedStringProvider.getInstance().get(ctx, "shisui_load_failed"));
								}
							});
						}
					}
				}).start();
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				dialog.setContentView(dialogContainer);
				dialog.show();
				animateDialogEntrance(root, act);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.9);
				dialog.getWindow().setAttributes(lp);
			}
		});
	}
	private void addAboutItem(LinearLayout parent, Activity act, String label, String value) {
		LinearLayout row = new LinearLayout(act);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setPadding(0, dp(act, 6), 0, dp(act, 6));
		if (!label.isEmpty()) {
			TextView labelView = new TextView(act);
			labelView.setText(label);
			labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			labelView.setTextColor(getHintColor(act)); 
			labelView.setTypeface(null, Typeface.BOLD);
			LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(dp(act, 80),
					ViewGroup.LayoutParams.WRAP_CONTENT);
			row.addView(labelView, labelLp);
		}
		TextView valueView = new TextView(act);
		valueView.setText(value);
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(getTextColor(act)); 
		row.addView(valueView);
		parent.addView(row);
	}
	private void addClickableAboutItem(LinearLayout parent, Activity act, String label, String value,
			View.OnClickListener listener) {
		LinearLayout row = new LinearLayout(act);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setPadding(0, dp(act, 6), 0, dp(act, 6));
		row.setOnClickListener(listener);
		if (!label.isEmpty()) {
			TextView labelView = new TextView(act);
			labelView.setText(label);
			labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			labelView.setTextColor(getHintColor(act)); 
			labelView.setTypeface(null, Typeface.BOLD);
			LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(dp(act, 80),
					ViewGroup.LayoutParams.WRAP_CONTENT);
			row.addView(labelView, labelLp);
		}
		TextView valueView = new TextView(act);
		valueView.setText(value);
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(getTitleColor(act));
		valueView.setPaintFlags(valueView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		row.addView(valueView);
		parent.addView(row);
	}
	private void openUrl(Context ctx, String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			ctx.startActivity(intent);
		} catch (Exception e) {
			jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "cannot_open_url"));
		}
	}
	private void checkViaVersion(final Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			String currentVersionName = pi.versionName;
			int currentVersionCode = pi.versionCode;
			currentDetectedVersion = currentVersionName;
			long currentUpdateTime = new java.io.File(pi.applicationInfo.sourceDir).lastModified();
			int lastVersionCode = getPrefInt(ctx, KEY_LAST_VIA_VERSION_CODE, -1);
			String lastVersionName = getPrefString(ctx, KEY_LAST_VIA_VERSION_NAME, "");
			long lastUpdateTime = getPrefLong(ctx, KEY_LAST_VIA_UPDATE_TIME, 0);
			boolean hasViaUpdate = false;
			if (lastVersionCode != -1) {
				if (currentVersionCode != lastVersionCode) {
					hasViaUpdate = true;
					bvLog("[BetterVia] 检测到Via版本代码变化: " + lastVersionCode + " -> " + currentVersionCode);
				} else if (currentUpdateTime != lastUpdateTime) {
					hasViaUpdate = true;
					bvLog("[BetterVia] 检测到Via AP修改时间变化");
				}
			}
			boolean hasVersionSelection = getPrefBoolean(ctx, "has_version_selection", false);
			int savedVersionCode = getPrefInt(ctx, KEY_SELECTED_VIA_VERSION, 20260211);
			selectedViaVersionCode = savedVersionCode;
			ViaClassMapping.setUserSelectedVersionCode(savedVersionCode);
			if (!hasVersionSelection) {
				showVersionSelectionDialog(ctx, currentVersionName, savedVersionCode, false, currentVersionCode,
						currentUpdateTime);
			} else if (hasViaUpdate) {
				bvLog("[BetterVia] Via已更新，重新弹出版本选择对话框");
				showVersionSelectionDialog(ctx, currentVersionName, savedVersionCode, true, currentVersionCode,
						currentUpdateTime);
			} else {
				bvLog("[BetterVia] 使用已保存的Via版本: " + ViaVersionDetector.getVersionName(savedVersionCode) + " (code: "
						+ savedVersionCode + ")");
			}
			if (hasVersionSelection && !hasViaUpdate && hasShownStartupDialog) {
				bvLog("[BetterVia] 启动流程已完成，需要重启Via以使设置生效");
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							jiguroMessageWithContext(ctx,
									LocalizedStringProvider.getInstance().get(ctx, "startup_restart_hint"));
							new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
								@Override
								public void run() {
									System.exit(0);
								}
							}, 1500);
						} catch (Exception e) {
							bvLog("[BetterVia] 重启逻辑执行失败: " + e.getMessage());
						}
					}
				}, 500);
			}
		} catch (Exception e) {
			bvLog("[BetterVia] 版本检测失败: " + e.getMessage());
		}
	}
	private void showBasicSettingsDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.START);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView settingsTitle = new TextView(act);
				settingsTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "basic_settings_dialog_title"));
				settingsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				settingsTitle.setTextColor(getTextColor(ctx));
				settingsTitle.setTypeface(null, Typeface.BOLD);
				settingsTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams settingsLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				settingsLp.bottomMargin = dp(act, 16);
				root.addView(settingsTitle, settingsLp);
				TextView hintLabel = new TextView(act);
				hintLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "basic_settings_subtitle"));
				hintLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintLabel.setTextColor(getHintColor(ctx));
				hintLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 20);
				root.addView(hintLabel, hintLp);
				LinearLayout langSectionContainer = new LinearLayout(act);
				langSectionContainer.setOrientation(LinearLayout.VERTICAL);
				langSectionContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable langSectionBg = new GradientDrawable();
				langSectionBg.setColor(getItemBgColor(ctx));
				langSectionBg.setCornerRadius(dp(act, 12));
				langSectionContainer.setBackground(langSectionBg);
				LinearLayout.LayoutParams langSectionLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				langSectionLp.bottomMargin = dp(act, 12);
				root.addView(langSectionContainer, langSectionLp);
				TextView langSectionTitle = new TextView(act);
				langSectionTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "basic_settings_language_section"));
				langSectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				langSectionTitle.setTextColor(getTextColor(ctx));
				langSectionTitle.setTypeface(null, Typeface.BOLD);
				langSectionTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams langSectionTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				langSectionTitleLp.bottomMargin = dp(act, 12);
				langSectionContainer.addView(langSectionTitle, langSectionTitleLp);
				LinearLayout langSelectorRow = new LinearLayout(act);
				langSelectorRow.setOrientation(LinearLayout.HORIZONTAL);
				langSelectorRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView langSelectorLabel = new TextView(act);
				langSelectorLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "language_selection_select"));
				langSelectorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				langSelectorLabel.setTextColor(getTextColor(ctx));
				langSelectorLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams langSelectorLabelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				langSelectorRow.addView(langSelectorLabel, langSelectorLabelLp);
				final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
				final String[] langItems = new String[]{LocalizedStringProvider.getInstance().get(ctx, "language_auto"),
						"简体中文", "繁體中文", "English"};
				final TextView langSelector = new TextView(act);
				langSelector.setText(langItems[0]);
				langSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				langSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				langSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				langSelector.setTextColor(getBtnTextColor(ctx));
				LinearLayout.LayoutParams langSelectorLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				langSelectorRow.addView(langSelector, langSelectorLp);
				langSectionContainer.addView(langSelectorRow);
				LinearLayout networkSectionContainer = new LinearLayout(act);
				networkSectionContainer.setOrientation(LinearLayout.VERTICAL);
				networkSectionContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable networkSectionBg = new GradientDrawable();
				networkSectionBg.setColor(getItemBgColor(ctx));
				networkSectionBg.setCornerRadius(dp(act, 12));
				networkSectionContainer.setBackground(networkSectionBg);
				LinearLayout.LayoutParams networkSectionLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				networkSectionLp.bottomMargin = dp(act, 20);
				root.addView(networkSectionContainer, networkSectionLp);
				TextView networkSectionTitle = new TextView(act);
				networkSectionTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "basic_settings_network_section"));
				networkSectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				networkSectionTitle.setTextColor(getTextColor(ctx));
				networkSectionTitle.setTypeface(null, Typeface.BOLD);
				networkSectionTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams networkSectionTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				networkSectionTitleLp.bottomMargin = dp(act, 12);
				networkSectionContainer.addView(networkSectionTitle, networkSectionTitleLp);
				TextView networkHint = new TextView(act);
				networkHint.setText(LocalizedStringProvider.getInstance().get(ctx, "basic_settings_network_hint"));
				networkHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				networkHint.setTextColor(getHintColor(ctx));
				networkHint.setGravity(Gravity.START);
				LinearLayout.LayoutParams networkHintLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				networkHintLp.bottomMargin = dp(act, 12);
				networkSectionContainer.addView(networkHint, networkHintLp);
				LinearLayout networkSelectorRow = new LinearLayout(act);
				networkSelectorRow.setOrientation(LinearLayout.HORIZONTAL);
				networkSelectorRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView networkSelectorLabel = new TextView(act);
				networkSelectorLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "network_source_title"));
				networkSelectorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				networkSelectorLabel.setTextColor(getTextColor(ctx));
				networkSelectorLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams networkSelectorLabelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				networkSelectorRow.addView(networkSelectorLabel, networkSelectorLabelLp);
				final String[] sourceValues = {NETWORK_SOURCE_GITEE, NETWORK_SOURCE_GITHUB};
				final String[] sourceItems = new String[]{"Gitee", "GitHub"};
				final TextView networkSelector = new TextView(act);
				networkSelector.setText(sourceItems[0]);
				networkSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				networkSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				networkSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				networkSelector.setTextColor(getBtnTextColor(ctx));
				LinearLayout.LayoutParams networkSelectorLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				networkSelectorRow.addView(networkSelector, networkSelectorLp);
				networkSectionContainer.addView(networkSelectorRow);
				LinearLayout buttonLayout = new LinearLayout(act);
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER);
				Button cancelButton = new Button(act);
				applyClickAnim(cancelButton);
				cancelButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelButton.setTextColor(getTitleColor(ctx));
				cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				cancelButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(getBtnBgColor(ctx));
				cancelBg.setCornerRadius(dp(act, 12));
				cancelButton.setBackground(cancelBg);
				cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelLp.rightMargin = dp(act, 8);
				buttonLayout.addView(cancelButton, cancelLp);
				Button confirmButton = new Button(act);
				applyClickAnim(confirmButton);
				confirmButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				confirmButton.setTextColor(getOkBtnTextColor(ctx));
				confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				confirmButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable confirmBg = new GradientDrawable();
				confirmBg.setColor(getOkBtnBgColor(ctx));
				confirmBg.setCornerRadius(dp(act, 12));
				confirmButton.setBackground(confirmBg);
				confirmButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams confirmLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				confirmLp.leftMargin = dp(act, 8);
				buttonLayout.addView(confirmButton, confirmLp);
				root.addView(buttonLayout);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).setCancelable(false)
						.create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				langSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ListView list = new ListView(ctx);
						list.setDivider(null);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
								android.R.layout.simple_list_item_1, langItems) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								TextView textView = (TextView) view.findViewById(android.R.id.text1);
								textView.setSingleLine(true);
								textView.setEllipsize(TextUtils.TruncateAt.END);
								textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
								textView.setTextColor(getTextColor(ctx));
								return view;
							}
						};
						list.setAdapter(adapter);
						int popupWidth = Math.max(langSelector.getWidth(), dp(ctx, 200));
						final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
						pop.setOutsideTouchable(true);
						pop.setFocusable(true);
						pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								langSelector.setText(langItems[position]);
								pop.dismiss();
							}
						});
						GradientDrawable popBg = getRoundBg(ctx, getBgColor(ctx), 12);
						popBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
						list.setBackground(popBg);
						list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
						pop.showAsDropDown(langSelector, 0, dp(ctx, 4));
					}
				});
				networkSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ListView list = new ListView(ctx);
						list.setDivider(null);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
								android.R.layout.simple_list_item_1, sourceItems) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								TextView textView = (TextView) view.findViewById(android.R.id.text1);
								textView.setSingleLine(true);
								textView.setEllipsize(TextUtils.TruncateAt.END);
								textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
								textView.setTextColor(getTextColor(ctx));
								return view;
							}
						};
						list.setAdapter(adapter);
						int popupWidth = Math.max(networkSelector.getWidth(), dp(ctx, 200));
						final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
						pop.setOutsideTouchable(true);
						pop.setFocusable(true);
						pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								networkSelector.setText(sourceItems[position]);
								pop.dismiss();
							}
						});
						GradientDrawable popBg = getRoundBg(ctx, getBgColor(ctx), 12);
						popBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
						list.setBackground(popBg);
						list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
						pop.showAsDropDown(networkSelector, 0, dp(ctx, 4));
					}
				});
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveLanguageSetting(ctx, "auto");
						putPrefString(ctx, KEY_NETWORK_SOURCE, NETWORK_SOURCE_GITEE);
						putPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, true);
						putPrefBoolean(ctx, KEY_HAS_NETWORK_SOURCE, true);
						dialog.dismiss();
						checkUserAgreement(ctx);
					}
				});
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int selectedLangIdx = 0;
						for (int i = 0; i < langItems.length; i++) {
							if (langItems[i].equals(langSelector.getText())) {
								selectedLangIdx = i;
								break;
							}
						}
						String selectedLang = langValues[selectedLangIdx];
						saveLanguageSetting(ctx, selectedLang);
						int selectedSourceIdx = 0;
						for (int i = 0; i < sourceItems.length; i++) {
							if (sourceItems[i].equals(networkSelector.getText())) {
								selectedSourceIdx = i;
								break;
							}
						}
						String selectedSource = sourceValues[selectedSourceIdx];
						putPrefString(ctx, KEY_NETWORK_SOURCE, selectedSource);
						putPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, true);
						putPrefBoolean(ctx, KEY_HAS_NETWORK_SOURCE, true);
						dialog.dismiss();
						checkUserAgreement(ctx);
					}
				});
				hasShownStartupDialog = true;
				dialog.show();
			}
		});
	}
	private void showLanguageSelectionDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.START);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView langTitle = new TextView(act);
				langTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "language_selection_dialog_title"));
				langTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				langTitle.setTextColor(getTextColor(ctx));
				langTitle.setTypeface(null, Typeface.BOLD);
				langTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams langLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				langLp.bottomMargin = dp(act, 16);
				root.addView(langTitle, langLp);
				TextView hintLabel = new TextView(act);
				hintLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "language_selection_subtitle"));
				hintLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintLabel.setTextColor(getHintColor(ctx));
				hintLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 20);
				root.addView(hintLabel, hintLp);
				LinearLayout selectorContainer = new LinearLayout(act);
				selectorContainer.setOrientation(LinearLayout.VERTICAL);
				selectorContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable selectorBg = new GradientDrawable();
				selectorBg.setColor(getItemBgColor(ctx));
				selectorBg.setCornerRadius(dp(act, 12));
				selectorContainer.setBackground(selectorBg);
				LinearLayout.LayoutParams containerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				containerLp.bottomMargin = dp(act, 20);
				root.addView(selectorContainer, containerLp);
				LinearLayout selectorRow = new LinearLayout(act);
				selectorRow.setOrientation(LinearLayout.HORIZONTAL);
				selectorRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView selectorLabel = new TextView(act);
				selectorLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "language_selection_select"));
				selectorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				selectorLabel.setTextColor(getTextColor(ctx));
				selectorLabel.setTypeface(null, Typeface.BOLD);
				selectorLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams selectorLabelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				selectorRow.addView(selectorLabel, selectorLabelLp);
				final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
				final String[] langItems = new String[]{LocalizedStringProvider.getInstance().get(ctx, "language_auto"),
						"简体中文", "繁體中文", "English"};
				final TextView langSelector = new TextView(act);
				langSelector.setText(langItems[0]);
				langSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				langSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				langSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				langSelector.setTextColor(getBtnTextColor(ctx));
				LinearLayout.LayoutParams selectorLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				selectorRow.addView(langSelector, selectorLp);
				selectorContainer.addView(selectorRow);
				LinearLayout buttonLayout = new LinearLayout(act);
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER);
				Button cancelButton = new Button(act);
				applyClickAnim(cancelButton);
				cancelButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelButton.setTextColor(getTitleColor(ctx));
				cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				cancelButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(getBtnBgColor(ctx));
				cancelBg.setCornerRadius(dp(act, 12));
				cancelButton.setBackground(cancelBg);
				cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelLp.rightMargin = dp(act, 8);
				buttonLayout.addView(cancelButton, cancelLp);
				Button confirmButton = new Button(act);
				applyClickAnim(confirmButton);
				confirmButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				confirmButton.setTextColor(getOkBtnTextColor(ctx));
				confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				confirmButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable confirmBg = new GradientDrawable();
				confirmBg.setColor(getOkBtnBgColor(ctx));
				confirmBg.setCornerRadius(dp(act, 12));
				confirmButton.setBackground(confirmBg);
				confirmButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams confirmLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				confirmLp.leftMargin = dp(act, 8);
				buttonLayout.addView(confirmButton, confirmLp);
				root.addView(buttonLayout);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).setCancelable(false)
						.create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				langSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ListView list = new ListView(ctx);
						list.setDivider(null);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
								android.R.layout.simple_list_item_1, langItems) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								TextView textView = (TextView) view.findViewById(android.R.id.text1);
								textView.setSingleLine(true);
								textView.setEllipsize(TextUtils.TruncateAt.END);
								textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
								textView.setTextColor(getTextColor(ctx));
								return view;
							}
						};
						list.setAdapter(adapter);
						int popupWidth = Math.max(langSelector.getWidth(), dp(ctx, 200));
						final PopupWindow pop = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
						pop.setOutsideTouchable(true);
						pop.setFocusable(true);
						pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								langSelector.setText(langItems[position]);
								pop.dismiss();
							}
						});
						GradientDrawable popBg = getRoundBg(ctx, getBgColor(ctx), 12);
						popBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
						list.setBackground(popBg);
						list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
						pop.showAsDropDown(langSelector, 0, dp(ctx, 4));
					}
				});
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveLanguageSetting(ctx, "auto");
						putPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, true);
						dialog.dismiss();
						checkUserAgreement(ctx);
					}
				});
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int selectedIdx = 0;
						for (int i = 0; i < langItems.length; i++) {
							if (langItems[i].equals(langSelector.getText())) {
								selectedIdx = i;
								break;
							}
						}
						String selectedLang = langValues[selectedIdx];
						saveLanguageSetting(ctx, selectedLang);
						putPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, true);
						dialog.dismiss();
						checkUserAgreement(ctx);
					}
				});
				hasShownStartupDialog = true;
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void showUserAgreementDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.START);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView agreementTitle = new TextView(act);
				agreementTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agreement_dialog_title"));
				agreementTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				agreementTitle.setTextColor(getTextColor(ctx));
				agreementTitle.setTypeface(null, Typeface.BOLD);
				agreementTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams agreementTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				agreementTitleLp.bottomMargin = dp(act, 16);
				root.addView(agreementTitle, agreementTitleLp);
				TextView contentText = new TextView(act);
				String content = LocalizedStringProvider.getInstance().get(ctx, "user_agreement_content");
				String linkText = LocalizedStringProvider.getInstance().get(ctx, "user_agreement_link_text");
				SpannableString spannableString = new SpannableString(content);
				int linkStart = content.indexOf(linkText);
				if (linkStart >= 0) {
					int linkEnd = linkStart + linkText.length();
					final int linkColor = getTitleColor(act); 
					ClickableSpan clickableSpan = new ClickableSpan() {
						@Override
						public void onClick(View widget) {
							loadUserAgreementUrl(ctx);
						}
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(linkColor); 
							ds.setUnderlineText(true);
						}
					};
					spannableString.setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				contentText.setText(spannableString);
				contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				contentText.setTextColor(getTextColor(ctx));
				contentText.setGravity(Gravity.START);
				contentText.setMovementMethod(LinkMovementMethod.getInstance());
				LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				contentLp.bottomMargin = dp(act, 24);
				root.addView(contentText, contentLp);
				LinearLayout buttonLayout = new LinearLayout(act);
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER);
				Button rejectButton = new Button(act);
				applyClickAnim(rejectButton);
				rejectButton.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agreement_reject"));
				rejectButton.setTextColor(getTitleColor(ctx));
				rejectButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				rejectButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable rejectBg = new GradientDrawable();
				rejectBg.setColor(getBtnBgColor(ctx));
				rejectBg.setCornerRadius(dp(act, 12));
				rejectButton.setBackground(rejectBg);
				rejectButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams rejectLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				rejectLp.rightMargin = dp(act, 8);
				buttonLayout.addView(rejectButton, rejectLp);
				Button agreeButton = new Button(act);
				applyClickAnim(agreeButton);
				agreeButton.setText(LocalizedStringProvider.getInstance().get(ctx, "user_agreement_agree"));
				agreeButton.setTextColor(getOkBtnTextColor(ctx));
				agreeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				agreeButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable agreeBg = new GradientDrawable();
				agreeBg.setColor(getOkBtnBgColor(ctx));
				agreeBg.setCornerRadius(dp(act, 12));
				agreeButton.setBackground(agreeBg);
				agreeButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams agreeLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				agreeLp.leftMargin = dp(act, 8);
				buttonLayout.addView(agreeButton, agreeLp);
				root.addView(buttonLayout);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).setCancelable(false)
						.create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				rejectButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "user_agreement_reject_toast"));
						dialog.dismiss();
					}
				});
				agreeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						putPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, true);
						dialog.dismiss();
						checkViaVersion(ctx);
					}
				});
				hasShownStartupDialog = true;
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private void loadUserAgreementUrl(final Context ctx) {
		jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "user_agreement_loading"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String jsonUrl = NETWORK_SOURCE_GITEE.equals(networkSource)
							? "https:
							: "https:
					HttpURLConnection connection = null;
					InputStream inputStream = null;
					try {
						URL url = new URL(jsonUrl);
						connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(10000);
						connection.setReadTimeout(10000);
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							inputStream = connection.getInputStream();
							StringBuilder jsonBuilder = new StringBuilder();
							byte[] buffer = new byte[4096];
							int bytesRead;
							while ((bytesRead = inputStream.read(buffer)) != -1) {
								jsonBuilder.append(new String(buffer, 0, bytesRead, "UTF-8"));
							}
							String jsonContent = jsonBuilder.toString();
							JSONObject jsonObject = new JSONObject(jsonContent);
							JSONObject userAgreementUrls = jsonObject.getJSONObject("user_agreement_urls");
							String userLang = getSavedLanguage(ctx);
							if ("auto".equals(userLang)) {
								Locale locale;
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
									locale = ctx.getResources().getConfiguration().getLocales().get(0);
								} else {
									locale = ctx.getResources().getConfiguration().locale;
								}
								if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
									userLang = "zh_CN";
								} else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
									userLang = "zh_TW";
								} else {
									userLang = "en";
								}
							} else {
								if ("zh-CN".equals(userLang)) {
									userLang = "zh_CN";
								} else if ("zh-TW".equals(userLang)) {
									userLang = "zh_TW";
								} else {
									userLang = "en";
								}
							}
							String agreementUrl = null;
							if (userAgreementUrls.has(userLang)) {
								JSONObject langObj = userAgreementUrls.getJSONObject(userLang);
								agreementUrl = langObj.getString("url");
							}
							if (agreementUrl != null && !agreementUrl.isEmpty()) {
								final String finalUrl = agreementUrl;
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										ctx.startActivity(intent);
									}
								});
							} else {
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx,
												"user_agreement_load_failed"));
									}
								});
							}
						} else {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx,
											"user_agreement_load_failed"));
								}
							});
						}
					} finally {
						if (inputStream != null)
							inputStream.close();
						if (connection != null)
							connection.disconnect();
					}
				} catch (Exception e) {
					bvLog("[BetterVia] 加载用户协议URL失败: " + e);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							jiguroMessageWithContext(ctx,
									LocalizedStringProvider.getInstance().get(ctx, "user_agreement_load_failed"));
						}
					});
				}
			}
		}).start();
	}
	private void loadUserAgreementUrlWithLanguage(final Context ctx, final String language) {
		jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "user_agreement_loading"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
					String jsonUrl = NETWORK_SOURCE_GITEE.equals(networkSource)
							? "https:
							: "https:
					HttpURLConnection connection = null;
					InputStream inputStream = null;
					try {
						URL url = new URL(jsonUrl);
						connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(10000);
						connection.setReadTimeout(10000);
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							inputStream = connection.getInputStream();
							StringBuilder jsonBuilder = new StringBuilder();
							byte[] buffer = new byte[4096];
							int bytesRead;
							while ((bytesRead = inputStream.read(buffer)) != -1) {
								jsonBuilder.append(new String(buffer, 0, bytesRead, "UTF-8"));
							}
							String jsonContent = jsonBuilder.toString();
							JSONObject jsonObject = new JSONObject(jsonContent);
							JSONObject userAgreementUrls = jsonObject.getJSONObject("user_agreement_urls");
							String userLang = language;
							String agreementUrl = null;
							if (userAgreementUrls.has(userLang)) {
								JSONObject langObj = userAgreementUrls.getJSONObject(userLang);
								agreementUrl = langObj.getString("url");
							}
							if (agreementUrl != null && !agreementUrl.isEmpty()) {
								final String finalUrl = agreementUrl;
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										ctx.startActivity(intent);
									}
								});
							} else {
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx,
												"user_agreement_load_failed"));
									}
								});
							}
						} else {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx,
											"user_agreement_load_failed"));
								}
							});
						}
					} finally {
						if (inputStream != null)
							inputStream.close();
						if (connection != null)
							connection.disconnect();
					}
				} catch (Exception e) {
					bvLog("[BetterVia] 加载用户协议URL失败: " + e);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							jiguroMessageWithContext(ctx,
									LocalizedStringProvider.getInstance().get(ctx, "user_agreement_load_failed"));
						}
					});
				}
			}
		}).start();
	}
	private void checkUserAgreement(final Context ctx) {
		boolean hasUserAgreement = getPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
		if (!hasUserAgreement) {
			showUserAgreementDialog(ctx);
		} else {
			checkViaVersion(ctx);
		}
	}
	private void checkBasicSettings(final Context ctx) {
		boolean hasLanguageSelection = getPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, false);
		boolean hasNetworkSource = getPrefBoolean(ctx, KEY_HAS_NETWORK_SOURCE, false);
		if (!hasLanguageSelection || !hasNetworkSource) {
			showBasicSettingsDialog(ctx);
		} else {
			checkUserAgreement(ctx);
		}
	}
	private void showVersionSelectionDialog(final Context ctx, final String currentVersion, final int savedVersionCode,
			final boolean isViaUpdated, final int currentVersionCode, final long currentUpdateTime) {
		final Activity act = getActivityFrom(ctx);
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
				GradientDrawable bg = new GradientDrawable();
				bg.setColor(getBgColor(ctx));
				bg.setCornerRadius(dp(act, 24));
				root.setBackground(bg);
				TextView title = new TextView(act);
				title.setText("BetterVia");
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				title.setTextColor(getTitleColor(ctx));
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.START);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = dp(act, 8);
				root.addView(title, titleLp);
				TextView versionTitle = new TextView(act);
				versionTitle.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_dialog_title"));
				versionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				versionTitle.setTextColor(getTextColor(ctx));
				versionTitle.setTypeface(null, Typeface.BOLD);
				versionTitle.setGravity(Gravity.START);
				LinearLayout.LayoutParams versionLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				versionLp.bottomMargin = dp(act, 16);
				root.addView(versionTitle, versionLp);
				TextView currentVersionLabel = new TextView(act);
				currentVersionLabel.setText(String.format(
						LocalizedStringProvider.getInstance().get(ctx, "version_selector_current"), currentVersion));
				currentVersionLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				currentVersionLabel.setTextColor(getHintColor(ctx));
				currentVersionLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams currentLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				currentLp.bottomMargin = dp(act, 12);
				root.addView(currentVersionLabel, currentLp);
				TextView hintLabel = new TextView(act);
				hintLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_subtitle"));
				hintLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintLabel.setTextColor(getHintColor(ctx));
				hintLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 8);
				root.addView(hintLabel, hintLp);
				if (isViaUpdated) {
					LinearLayout updateHintContainer = new LinearLayout(act);
					updateHintContainer.setOrientation(LinearLayout.HORIZONTAL);
					updateHintContainer.setGravity(Gravity.CENTER_VERTICAL);
					updateHintContainer.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
					GradientDrawable updateHintBg = new GradientDrawable();
					updateHintBg.setColor(0xFFFFE0B0); 
					updateHintBg.setCornerRadius(dp(act, 8));
					updateHintContainer.setBackground(updateHintBg);
					LinearLayout.LayoutParams updateHintLp = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					updateHintLp.bottomMargin = dp(act, 16);
					root.addView(updateHintContainer, updateHintLp);
					TextView updateIcon = new TextView(act);
					updateIcon.setText("⚠");
					updateIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					updateIcon.setTextColor(0xFFFF9800);
					updateIcon.setPadding(0, 0, dp(act, 8), 0);
					updateHintContainer.addView(updateIcon);
					TextView updateHint = new TextView(act);
					updateHint.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_update_hint"));
					updateHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					updateHint.setTextColor(0xFF795548);
					updateHintContainer.addView(updateHint);
				}
				boolean isCurrentVersionSupported = ViaVersionDetector.isVersionSupported(currentVersionCode);
				if (!isCurrentVersionSupported) {
					LinearLayout unsupportedHintContainer = new LinearLayout(act);
					unsupportedHintContainer.setOrientation(LinearLayout.HORIZONTAL);
					unsupportedHintContainer.setGravity(Gravity.CENTER_VERTICAL);
					unsupportedHintContainer.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
					GradientDrawable unsupportedHintBg = new GradientDrawable();
					unsupportedHintBg.setColor(0xFFFFE0B0); 
					unsupportedHintBg.setCornerRadius(dp(act, 8));
					unsupportedHintContainer.setBackground(unsupportedHintBg);
					LinearLayout.LayoutParams unsupportedHintLp = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					unsupportedHintLp.bottomMargin = dp(act, 16);
					root.addView(unsupportedHintContainer, unsupportedHintLp);
					TextView unsupportedIcon = new TextView(act);
					unsupportedIcon.setText(
							LocalizedStringProvider.getInstance().get(ctx, "version_unsupported_warning_icon"));
					unsupportedIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					unsupportedIcon.setTextColor(0xFFFF9800);
					unsupportedIcon.setPadding(0, 0, dp(act, 8), 0);
					unsupportedHintContainer.addView(unsupportedIcon);
					TextView unsupportedHint = new TextView(act);
					unsupportedHint.setText(
							LocalizedStringProvider.getInstance().get(ctx, "version_unsupported_warning_message"));
					unsupportedHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					unsupportedHint.setTextColor(0xFF795548);
					unsupportedHintContainer.addView(unsupportedHint);
				}
				LinearLayout selectorContainer = new LinearLayout(act);
				selectorContainer.setOrientation(LinearLayout.VERTICAL);
				selectorContainer.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
				GradientDrawable selectorBg = new GradientDrawable();
				selectorBg.setColor(getItemBgColor(ctx));
				selectorBg.setCornerRadius(dp(act, 12));
				selectorContainer.setBackground(selectorBg);
				LinearLayout.LayoutParams containerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				containerLp.bottomMargin = dp(act, 20);
				root.addView(selectorContainer, containerLp);
				LinearLayout selectorRow = new LinearLayout(act);
				selectorRow.setOrientation(LinearLayout.HORIZONTAL);
				selectorRow.setGravity(Gravity.CENTER_VERTICAL);
				TextView selectorLabel = new TextView(act);
				selectorLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "version_selector_select"));
				selectorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				selectorLabel.setTextColor(getTextColor(ctx));
				selectorLabel.setTypeface(null, Typeface.BOLD);
				selectorLabel.setGravity(Gravity.START);
				LinearLayout.LayoutParams selectorLabelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				selectorRow.addView(selectorLabel, selectorLabelLp);
				final ArrayList<Integer> supportedVersionCodesList = new ArrayList<Integer>();
				final ArrayList<String> supportedVersionNamesList = new ArrayList<String>();
				int[] initialVersionCodes = ViaVersionDetector.getSupportedVersionCodes();
				String[] initialVersionNames = ViaVersionDetector.getSupportedVersionNames();
				for (int i = 0; i < initialVersionCodes.length; i++) {
					supportedVersionCodesList.add(initialVersionCodes[i]);
					supportedVersionNamesList.add(initialVersionNames[i]);
				}
				int defaultIndex = 0;
				int recommendedVersionCode = ViaVersionDetector.getRecommendedVersionByName(currentVersion);
				for (int i = 0; i < supportedVersionCodesList.size(); i++) {
					if (supportedVersionCodesList.get(i) == recommendedVersionCode) {
						defaultIndex = i;
						break;
					}
				}
				final TextView versionSelector = new TextView(act);
				versionSelector.setText(supportedVersionNamesList.get(defaultIndex));
				versionSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				versionSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
				versionSelector.setBackground(getRoundBg(act, getBtnBgColor(ctx), 12));
				versionSelector.setTextColor(getBtnTextColor(ctx));
				LinearLayout.LayoutParams selectorLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				selectorRow.addView(versionSelector, selectorLp);
				selectorContainer.addView(selectorRow);
				final ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(ctx,
						android.R.layout.simple_list_item_1, supportedVersionNamesList) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						TextView textView = (TextView) view.findViewById(android.R.id.text1);
						textView.setSingleLine(true);
						textView.setEllipsize(TextUtils.TruncateAt.END);
						textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
						textView.setTextColor(getTextColor(ctx));
						return view;
					}
				};
				final PopupWindow[] versionPopup = new PopupWindow[1];
				final Button syncButton = new Button(act);
				applyClickAnim(syncButton);
				syncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_title"));
				syncButton.setTextColor(getTitleColor(ctx));
				syncButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				syncButton.setTypeface(null, Typeface.NORMAL);
				GradientDrawable syncBg = new GradientDrawable();
				syncBg.setColor(getBtnBgColor(ctx));
				syncBg.setCornerRadius(dp(act, 8));
				syncButton.setBackground(syncBg);
				syncButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
				LinearLayout.LayoutParams syncButtonLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				syncButtonLp.gravity = Gravity.CENTER;
				syncButtonLp.bottomMargin = dp(act, 16);
				root.addView(syncButton, syncButtonLp);
				syncButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						syncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_loading"));
						syncButton.setEnabled(false);
						MappingSyncHelper.syncMappingFromNetwork(ctx, new MappingSyncHelper.SyncCallback() {
							@Override
							public void onSuccess(int addedVersions) {
								syncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_title"));
								syncButton.setEnabled(true);
								jiguroMessageWithContext(ctx, String.format(
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_success"),
										addedVersions));
								supportedVersionCodesList.clear();
								supportedVersionNamesList.clear();
								int[] newSupportedVersionCodes = ViaVersionDetector.getSupportedVersionCodes();
								String[] newSupportedVersionNames = ViaVersionDetector.getSupportedVersionNames();
								for (int i = 0; i < newSupportedVersionCodes.length; i++) {
									supportedVersionCodesList.add(newSupportedVersionCodes[i]);
									supportedVersionNamesList.add(newSupportedVersionNames[i]);
								}
								versionAdapter.notifyDataSetChanged();
								if (versionPopup[0] != null && versionPopup[0].isShowing()) {
									versionPopup[0].dismiss();
								}
								boolean isCurrentVersionSupported = ViaVersionDetector
										.isVersionSupported(currentVersionCode);
								if (isCurrentVersionSupported) {
									new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
										@Override
										public void run() {
											System.exit(0);
										}
									}, 2000);
								}
							}
							@Override
							public void onFailed(String errorMessage) {
								syncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_title"));
								syncButton.setEnabled(true);
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_failed"));
							}
							@Override
							public void onNoUpdate() {
								syncButton.setText(LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_title"));
								syncButton.setEnabled(true);
								jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "mapping_sync_no_update"));
							}
						});
					}
				});
				LinearLayout buttonLayout = new LinearLayout(act);
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER);
				Button cancelButton = new Button(act);
				applyClickAnim(cancelButton);
				cancelButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
				cancelButton.setTextColor(getTitleColor(ctx));
				cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				cancelButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(getBtnBgColor(ctx));
				cancelBg.setCornerRadius(dp(act, 12));
				cancelButton.setBackground(cancelBg);
				cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelLp.rightMargin = dp(act, 8);
				buttonLayout.addView(cancelButton, cancelLp);
				Button confirmButton = new Button(act);
				applyClickAnim(confirmButton);
				confirmButton.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				confirmButton.setTextColor(getOkBtnTextColor(ctx));
				confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				confirmButton.setTypeface(null, Typeface.BOLD);
				GradientDrawable confirmBg = new GradientDrawable();
				confirmBg.setColor(getOkBtnBgColor(ctx));
				confirmBg.setCornerRadius(dp(act, 12));
				confirmButton.setBackground(confirmBg);
				confirmButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
				LinearLayout.LayoutParams confirmLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				confirmLp.leftMargin = dp(act, 8);
				buttonLayout.addView(confirmButton, confirmLp);
				root.addView(buttonLayout);
				scrollRoot.addView(root);
				final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).setCancelable(false)
						.create();
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					GradientDrawable round = new GradientDrawable();
					round.setColor(getBgColor(ctx));
					round.setCornerRadius(dp(act, 24));
					win.setBackgroundDrawable(round);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				versionSelector.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ListView list = new ListView(ctx);
						list.setDivider(null);
						list.setAdapter(versionAdapter);
						int popupWidth = Math.max(versionSelector.getWidth(), dp(ctx, 200));
						versionPopup[0] = new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
						versionPopup[0].setOutsideTouchable(true);
						versionPopup[0].setFocusable(true);
						versionPopup[0].setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								versionSelector.setText(supportedVersionNamesList.get(position));
								versionPopup[0].dismiss();
							}
						});
						GradientDrawable popBg = getRoundBg(ctx, getBgColor(ctx), 12);
						popBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
						list.setBackground(popBg);
						list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
						versionPopup[0].showAsDropDown(versionSelector, 0, dp(ctx, 4));
					}
				});
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "version_selector_cancel_hint"));
						dialog.dismiss();
					}
				});
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String selectedVersionName = versionSelector.getText().toString();
						int selectedVersionCode = -1;
						for (int i = 0; i < supportedVersionNamesList.size(); i++) {
							if (supportedVersionNamesList.get(i).equals(selectedVersionName)) {
								selectedVersionCode = supportedVersionCodesList.get(i);
								break;
							}
						}
						if (selectedVersionCode != -1) {
							putPrefInt(ctx, KEY_SELECTED_VIA_VERSION, selectedVersionCode);
							selectedViaVersionCode = selectedVersionCode;
							ViaClassMapping.setUserSelectedVersionCode(selectedVersionCode);
							putPrefBoolean(ctx, "has_version_selection", true);
							putPrefInt(ctx, KEY_LAST_VIA_VERSION_CODE, currentVersionCode);
							putPrefString(ctx, KEY_LAST_VIA_VERSION_NAME, currentVersion);
							putPrefLong(ctx, KEY_LAST_VIA_UPDATE_TIME, currentUpdateTime);
							bvLog("[BetterVia] 用户选择的Via版本: " + selectedVersionName + " (code: " + selectedVersionCode
									+ ")");
							bvLog("[BetterVia] 已保存当前Via版本信息: " + currentVersion + " (code: " + currentVersionCode
									+ ")");
							ViaClassMapping.clearCache();
							dialog.dismiss();
							jiguroMessageWithContext(ctx,
									LocalizedStringProvider.getInstance().get(ctx, "startup_restart_hint"));
							new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
								@Override
								public void run() {
									jiguroMessageWithContext(ctx,
											LocalizedStringProvider.getInstance().get(ctx, "startup_restart_hint"));
								}
							}, 500);
							new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
								@Override
								public void run() {
									System.exit(0);
								}
							}, 2000);
						}
					}
				});
				hasShownStartupDialog = true;
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private static void bvLog(String message) {
		XposedBridge.log(message);
		if (developerModeEnabled && Context != null) {
			try {
				if (logWriter == null) {
					initLogFile();
				}
				if (logWriter != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
					String timestamp = sdf.format(new Date());
					logWriter.println("[" + timestamp + "] " + message);
					logWriter.flush();
				}
			} catch (Exception e) {
			}
		}
	}
	private static void initLogFile() {
		try {
			if (Context == null) {
				XposedBridge.log("[BetterVia] initLogFile: Context 为 null，延迟初始化");
				return;
			}
			Context appCtx = Context.getApplicationContext();
			if (appCtx == null)
				appCtx = Context;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
			String timestamp = sdf.format(new Date());
			File baseDir = appCtx.getExternalFilesDir(null);
			if (baseDir == null) {
				XposedBridge.log("[BetterVia] initLogFile: getExternalFilesDir 返回 null");
				return;
			}
			File logDir = new File(baseDir, "BetterVia");
			if (!logDir.exists()) {
				boolean ok = logDir.mkdirs();
				if (!ok) {
					XposedBridge.log("[BetterVia] initLogFile: 无法创建目录 " + logDir.getAbsolutePath());
				}
			}
			logFilePath = new File(logDir, "BetterVia_" + timestamp + ".log").getAbsolutePath();
			logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), true);
			XposedBridge.log("[BetterVia] initLogFile: 日志文件已创建 -> " + logFilePath);
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] initLogFile 异常: " + Log.getStackTraceString(e));
		}
	}
	private static void closeLogFile() {
		if (logWriter != null) {
			try {
				logWriter.flush();
				logWriter.close();
				XposedBridge.log("[BetterVia] closeLogFile: 日志文件已关闭");
			} catch (Exception e) {
				XposedBridge.log("[BetterVia] closeLogFile 异常: " + Log.getStackTraceString(e));
			}
			logWriter = null;
		}
		logFilePath = null;
		developerModeEnabled = false;
	}
	private static class MonetFileInfo {
		String id;
		String name;
		List<String> sources;
		String type;
		String targetPath;
		List<String> compatibleWith;
		public MonetFileInfo(String id, String name, List<String> sources, String type, String targetPath,
				List<String> compatibleWith) {
			this.id = id;
			this.name = name;
			this.sources = sources;
			this.type = type;
			this.targetPath = targetPath;
			this.compatibleWith = compatibleWith;
		}
	}
	private class MonetProgressDialog {
		private Dialog dialog;
		private TextView statusText;
		private TextView errorText;
		private ProgressBar progressBar;
		private Button cancelButton;
		private Activity activity;
		private Context context;
		public MonetProgressDialog(Activity act, Context ctx) {
			this.activity = act;
			this.context = ctx;
			createDialog();
		}
		private void createDialog() {
			dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			LinearLayout root = new LinearLayout(activity);
			root.setOrientation(LinearLayout.VERTICAL);
			root.setPadding(dp(activity, 24), dp(activity, 24), dp(activity, 24), dp(activity, 24));
			GradientDrawable bg = new GradientDrawable();
			bg.setColor(getBgColor(context));
			bg.setCornerRadius(dp(activity, 24));
			root.setBackground(bg);
			TextView title = new TextView(activity);
			title.setText(LocalizedStringProvider.getInstance().get(context, "monet_progress_title"));
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			title.setTextColor(getTitleColor(context));
			title.setTypeface(null, Typeface.BOLD);
			title.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			titleLp.bottomMargin = dp(activity, 16);
			root.addView(title, titleLp);
			progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
			progressBar.setMax(100);
			progressBar.setProgress(0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				progressBar.setProgressTintList(ColorStateList.valueOf(getOkBtnBgColor(context)));
			}
			LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			progressLp.bottomMargin = dp(activity, 12);
			root.addView(progressBar, progressLp);
			statusText = new TextView(activity);
			statusText.setText("");
			statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			statusText.setTextColor(getHintColor(context));
			statusText.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			statusLp.bottomMargin = dp(activity, 8);
			root.addView(statusText, statusLp);
			errorText = new TextView(activity);
			errorText.setText("");
			errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			errorText.setTextColor(0xFFFF5252);
			errorText.setGravity(Gravity.CENTER);
			errorText.setVisibility(View.GONE);
			LinearLayout.LayoutParams errorLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			errorLp.bottomMargin = dp(activity, 16);
			root.addView(errorText, errorLp);
			cancelButton = new Button(activity);
			cancelButton.setText(LocalizedStringProvider.getInstance().get(context, "monet_progress_cancel"));
			cancelButton.setTextColor(getBtnTextColor(context));
			cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			cancelButton.setTypeface(null, Typeface.BOLD);
			GradientDrawable btnBg = new GradientDrawable();
			btnBg.setColor(getBtnBgColor(context));
			btnBg.setCornerRadius(dp(activity, 12));
			cancelButton.setBackground(btnBg);
			LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			root.addView(cancelButton, btnLp);
			dialog.setContentView(root);
			Window win = dialog.getWindow();
			if (win != null) {
				win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				DisplayMetrics metrics = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int width = (int) (metrics.widthPixels * 0.85);
				WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
				layoutParams.copyFrom(win.getAttributes());
				layoutParams.width = width;
				layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
				layoutParams.gravity = Gravity.CENTER;
				win.setAttributes(layoutParams);
			}
		}
		public void show() {
			dialog.show();
		}
		public void dismiss() {
			dialog.dismiss();
		}
		public void updateProgress(final String message, final int progress) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (dialog != null && dialog.isShowing()) {
						statusText.setText(message);
						progressBar.setProgress(progress);
						errorText.setVisibility(View.GONE);
					}
				}
			});
		}
		public void showError(final String errorMessage) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (dialog != null && dialog.isShowing()) {
						errorText.setText(errorMessage);
						errorText.setVisibility(View.VISIBLE);
					}
				}
			});
		}
		public void setCancelButtonListener(final View.OnClickListener listener) {
			cancelButton.setOnClickListener(listener);
		}
		public boolean isShowing() {
			return dialog != null && dialog.isShowing();
		}
	}
	private void startMonetMomentProcess(final Context ctx) {
		if (monetProcessing) {
			jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "monet_error_processing"));
			return;
		}
		monetProcessing = true;
		monetCancelled = false;
		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			monetProcessing = false;
			return;
		}
		final MonetProgressDialog progressDialog = new MonetProgressDialog(act, ctx);
		progressDialog.setCancelButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				monetCancelled = true;
				monetProcessing = false;
				progressDialog.dismiss();
				jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "monet_error_cancelled"));
			}
		});
		progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					processMonetMoment(ctx, progressDialog);
					monetProcessing = false;
				} catch (final Exception e) {
					bvLog("[BetterVia] 莫奈时刻处理异常: " + Log.getStackTraceString(e));
					monetProcessing = false;
					monetCancelled = false;
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.showError(
									String.format(LocalizedStringProvider.getInstance().get(ctx, "monet_error_unknown"),
											e.getMessage()));
							jiguroMessageWithContext(ctx,
									String.format(LocalizedStringProvider.getInstance().get(ctx, "monet_error_unknown"),
											e.getMessage()));
						}
					});
				}
			}
		}).start();
	}
	private void processMonetMoment(final Context ctx, final MonetProgressDialog progressDialog) throws Exception {
		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			throw new Exception("Activity is null");
		}
		int outputLocationIdx = getPrefInt(ctx, KEY_MONET_OUTPUT_LOCATION, 0);
		String packageName = ctx.getPackageName();
		File tempDir;
		if (outputLocationIdx == 0) {
			tempDir = new File("/storage/emulated/0/Android/data/" + packageName + "/files/BetterVia/temp/");
		} else {
			tempDir = new File("/data/user/0/" + packageName + "/files/BetterVia/temp/");
		}
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		int baseVersionIdx = getPrefInt(ctx, KEY_MONET_BASE, 0);
		boolean useIcon = getPrefBoolean(ctx, KEY_MONET_USE_ICON, false);
		boolean makeLite = getPrefBoolean(ctx, KEY_MONET_MAKE_LITE, false);
		int signScheme = getPrefInt(ctx, KEY_MONET_SIGN_SCHEME, 0);
		try {
			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_cleaning"), 0);
			cleanMonetTempDir(tempDir);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_loading_config"),
					5);
			JSONObject configJson = loadMonetConfigJson(ctx);
			if (monetCancelled)
				return;
			progressDialog
					.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_apk"), 10);
			MonetFileInfo selectedApk = selectApkFile(configJson, baseVersionIdx, useIcon, makeLite);
			if (selectedApk == null) {
				progressDialog.showError(LocalizedStringProvider.getInstance().get(ctx, "monet_error_select_apk"));
				throw new Exception(LocalizedStringProvider.getInstance().get(ctx, "monet_error_select_apk"));
			}
			String apkSource = selectSource(ctx, selectedApk.sources);
			File apkFile = new File(tempDir, "downloaded.apk");
			downloadFile(apkSource, apkFile, new DownloadProgressCallback() {
				@Override
				public void onProgress(long downloaded, long total) {
					String downloadedMB = String.format("%.2f", downloaded / 1024.0 / 1024.0);
					String totalMB = String.format("%.2f", total / 1024.0 / 1024.0);
					progressDialog
							.updateProgress(
									String.format(LocalizedStringProvider.getInstance().get(ctx,
											"monet_status_downloading_apk"), downloadedMB, totalMB),
									10 + (int) (downloaded * 25 / total));
				}
			});
			if (monetCancelled)
				return;
			progressDialog.updateProgress(
					LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_sign_zip"), 40);
			MonetFileInfo keyFile = getKeyFile(configJson);
			if (keyFile == null) {
				progressDialog.showError("Key file not found in config");
				throw new Exception("Key file not found in config");
			}
			String keySource = selectSource(ctx, keyFile.sources);
			File keyZipFile = new File(tempDir, "test.zip");
			downloadFile(keySource, keyZipFile, new DownloadProgressCallback() {
				@Override
				public void onProgress(long downloaded, long total) {
					String downloadedMB = String.format("%.2f", downloaded / 1024.0 / 1024.0);
					String totalMB = String.format("%.2f", total / 1024.0 / 1024.0);
					progressDialog.updateProgress(
							String.format(
									LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_sign_zip")
											+ " (%s/%s MB)",
									downloadedMB, totalMB),
							40 + (int) (downloaded * 10 / (total > 0 ? total : 1)));
				}
			});
			if (monetCancelled)
				return;
			progressDialog
					.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_extracting_sign"), 50);
			File keyDir = new File(tempDir, "keys");
			if (keyDir.exists()) {
				deleteDirectory(keyDir);
			}
			keyDir.mkdirs();
			extractZip(keyZipFile, keyDir);
			File pemFile = new File(keyDir, "test.x509.pem");
			File pk8File = new File(keyDir, "test.pk8");
			if (!pemFile.exists() || !pk8File.exists()) {
				progressDialog.showError("Signing files not found in zip file");
				throw new Exception("Signing files not found in zip file");
			}
			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_renaming_apk"),
					55);
			File baseApk = new File(tempDir, "base.apk");
			if (!apkFile.renameTo(baseApk)) {
				progressDialog.showError(LocalizedStringProvider.getInstance().get(ctx, "monet_error_rename"));
				throw new Exception(LocalizedStringProvider.getInstance().get(ctx, "monet_error_rename"));
			}
			if (monetCancelled)
				return;
			final File signedApk = new File(tempDir, "Moneted.apk");
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_signing"), 60);
			signApkWithApksigner(ctx, baseApk, signedApk, pemFile, pk8File, signScheme);
			generatedApkPath = signedApk.getAbsolutePath();
			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_cleaning_temp"),
					70);
			deleteDirectory(keyDir);
			if (keyZipFile.exists()) {
				keyZipFile.delete();
			}
			if (baseApk.exists()) {
				baseApk.delete();
			}
			if (monetCancelled)
				return;
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					jiguroMessageWithContext(ctx,
							LocalizedStringProvider.getInstance().get(ctx, "monet_status_completed"));
					Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("application/vnd.android.package-archive");
					intent.putExtra(Intent.EXTRA_TITLE, "Via_Moneted.apk");
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					}
					try {
						act.startActivityForResult(intent, 0x2001);
					} catch (Exception e) {
						bvLog("[BetterVia] 打开文件选择器失败: " + e.getMessage());
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "monet_error_open_apk"));
					}
				}
			});
		} catch (Exception e) {
			progressDialog.showError(e.getMessage());
			throw e;
		}
	}
	private void cleanMonetTempDir(File tempDir) throws Exception {
		if (tempDir.exists()) {
			deleteDirectory(tempDir);
		}
		tempDir.mkdirs();
	}
	private void deleteDirectory(File dir) throws Exception {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteDirectory(file);
				}
			}
		}
		dir.delete();
	}
	private static class ViaVersionInfo {
		String versionName;
		String versionCode;
		ViaVersionInfo(String versionName, String versionCode) {
			this.versionName = versionName;
			this.versionCode = versionCode;
		}
	}
	private ViaVersionInfo fetchLatestViaVersion(Context ctx) {
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			String url = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE).equals(NETWORK_SOURCE_GITEE)
					? NEWEST_JSON_URL_GITEE
					: NEWEST_JSON_URL_GITHUB;
			bvLog("[BetterVia] 开始获取最新Via版本信息: " + url);
			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			bvLog("[BetterVia] HTTP响应码: " + responseCode);
			if (responseCode != HttpURLConnection.HTTP_OK) {
				bvLog("[BetterVia] 获取最新版本信息失败，HTTP响应码: " + responseCode);
				return null;
			}
			is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String jsonString = sb.toString();
			bvLog("[BetterVia] 获取到的JSON内容: " + jsonString);
			if (jsonString == null || jsonString.trim().isEmpty()) {
				bvLog("[BetterVia] 获取最新版本信息失败，文件内容为空");
				return null;
			}
			JSONObject json = new JSONObject(jsonString);
			String versionName = json.optString("versionName", "");
			String versionCode = json.optString("versionCode", "");
			bvLog("[BetterVia] 解析版本信息 - 版本名: " + versionName + ", 版本号: " + versionCode);
			if (versionName.isEmpty() || versionCode.isEmpty()) {
				bvLog("[BetterVia] 获取最新版本信息失败，版本信息不完整");
				return null;
			}
			bvLog("[BetterVia] 获取最新版本信息成功: " + versionName + " (" + versionCode + ")");
			return new ViaVersionInfo(versionName, versionCode);
		} catch (Exception e) {
			bvLog("[BetterVia] 获取最新版本信息异常: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ignored) {
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	private JSONObject loadMonetConfigJson(Context ctx) throws Exception {
		String url = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE).equals(NETWORK_SOURCE_GITEE)
				? MONET_JSON_URL_GITEE
				: MONET_JSON_URL_GITHUB;
		bvLog("[BetterVia] 加载莫奈配置: " + url);
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("HTTP response code: " + responseCode);
			}
			is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String jsonString = sb.toString();
			return new JSONObject(jsonString);
		} finally {
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	private String selectSource(Context ctx, List<String> sources) {
		String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
		if (networkSource.equals(NETWORK_SOURCE_GITEE)) {
			for (String source : sources) {
				if (source.contains("gitee.com")) {
					return source;
				}
			}
		} else {
			for (String source : sources) {
				if (source.contains("github.com")) {
					return source;
				}
			}
		}
		return sources.get(0);
	}
	private MonetFileInfo selectApkFile(JSONObject configJson, int baseVersionIdx, boolean useIcon, boolean makeLite)
			throws Exception {
		try {
			JSONArray apkFiles = configJson.getJSONArray("apk_files");
			String basePrefix = baseVersionIdx == 0 ? "cn" : "gp";
			for (int i = 0; i < apkFiles.length(); i++) {
				JSONObject apkObj = apkFiles.getJSONObject(i);
				String id = apkObj.getString("id");
				if (!id.startsWith(basePrefix)) {
					continue;
				}
				boolean isNoIcon = id.contains("noicon");
				if (useIcon && isNoIcon) {
					continue;
				}
				if (!useIcon && !isNoIcon) {
					continue;
				}
				boolean isOptimize = id.contains("optimize");
				if (makeLite && !isOptimize) {
					continue;
				}
				if (!makeLite && isOptimize) {
					continue;
				}
				String name = apkObj.getString("name");
				JSONArray sourcesArray = apkObj.getJSONArray("sources");
				List<String> sources = new ArrayList<String>();
				for (int j = 0; j < sourcesArray.length(); j++) {
					sources.add(sourcesArray.getString(j));
				}
				return new MonetFileInfo(id, name, sources, "apk", "", null);
			}
		} catch (JSONException e) {
			throw new Exception("Failed to parse APK files: " + e.getMessage());
		}
		return null;
	}
	private MonetFileInfo getKeyFile(JSONObject configJson) throws Exception {
		try {
			JSONArray keyFiles = configJson.getJSONArray("key_files");
			for (int i = 0; i < keyFiles.length(); i++) {
				JSONObject keyObj = keyFiles.getJSONObject(i);
				String id = keyObj.getString("id");
				String name = keyObj.getString("name");
				JSONArray sourcesArray = keyObj.getJSONArray("sources");
				List<String> sources = new ArrayList<String>();
				for (int j = 0; j < sourcesArray.length(); j++) {
					sources.add(sourcesArray.getString(j));
				}
				return new MonetFileInfo(id, name, sources, "zip", "", null);
			}
		} catch (JSONException e) {
			throw new Exception("Failed to parse key files: " + e.getMessage());
		}
		return null;
	}
	private void signApkWithApksigner(Context ctx, File inputApk, File outputApk, File pemFile, File pk8File,
			int signScheme) throws Exception {
		bvLog("[BetterVia] 使用 apksig 库进行签名");
		boolean signV1 = (signScheme == 0 || signScheme == 1 || signScheme == 2 || signScheme == 3);
		boolean signV2 = (signScheme == 0 || signScheme == 1 || signScheme == 4 || signScheme == 5);
		boolean signV3 = (signScheme == 0 || signScheme == 2 || signScheme == 4 || signScheme == 6);
		bvLog("[BetterVia] 签名方案: V1=" + signV1 + ", V2=" + signV2 + ", V3=" + signV3);
		try {
			PrivateKey privateKey = readPrivateKey(pk8File);
			List<X509Certificate> certs = readCertificates(pemFile);
			ApkSigner.SignerConfig signerConfig = new ApkSigner.SignerConfig.Builder("CERT", privateKey, certs).build();
			List<ApkSigner.SignerConfig> signerConfigs = new ArrayList<>();
			signerConfigs.add(signerConfig);
			ApkSigner.Builder builder = new ApkSigner.Builder(signerConfigs).setInputApk(inputApk)
					.setOutputApk(outputApk).setV1SigningEnabled(signV1) 
					.setV2SigningEnabled(signV2).setV3SigningEnabled(signV3) 
					.setOtherSignersSignaturesPreserved(false); 
			builder.build().sign();
			bvLog("[BetterVia] APK 签名完成");
		} catch (Exception e) {
			bvLog("[BetterVia] 签名过程中出错: " + e.getMessage());
			throw e;
		}
	}
	private byte[] readFileToBytes(File file) throws IOException {
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		FileInputStream fis = new FileInputStream(file);
		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
		} finally {
			fis.close();
		}
		return bytes;
	}
	private PrivateKey readPrivateKey(File pk8File) throws Exception {
		byte[] keyBytes = readFileToBytes(pk8File);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	private List<X509Certificate> readCertificates(File pemFile) throws Exception {
		List<X509Certificate> certs = new ArrayList<>();
		FileInputStream fis = new FileInputStream(pemFile);
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> collection = cf.generateCertificates(fis);
			for (Certificate cert : collection) {
				if (cert instanceof X509Certificate) {
					certs.add((X509Certificate) cert);
				}
			}
		} finally {
			fis.close();
		}
		return certs;
	}
	private X509Certificate readCertificate(File pemFile) throws Exception {
		byte[] certBytes = readFileToBytes(pemFile);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
		return cert;
	}
	private interface DownloadProgressCallback {
		void onProgress(long downloaded, long total);
	}
	private void downloadFile(String url, File dest, DownloadProgressCallback callback) throws Exception {
		bvLog("[BetterVia] 下载文件: " + url + " -> " + dest.getAbsolutePath());
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("HTTP response code: " + responseCode);
			}
			long total = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				total = conn.getContentLengthLong();
			}
			is = conn.getInputStream();
			fos = new FileOutputStream(dest);
			byte[] buffer = new byte[MONET_BUFFER_SIZE];
			int len;
			long downloaded = 0;
			while ((len = is.read(buffer)) != -1) {
				if (monetCancelled) {
					throw new Exception("Download cancelled");
				}
				fos.write(buffer, 0, len);
				downloaded += len;
				if (callback != null) {
					callback.onProgress(downloaded, total);
				}
			}
			bvLog("[BetterVia] 文件下载完成: " + dest.getAbsolutePath());
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	private void extractZip(File apkFile, File destDir) throws Exception {
		bvLog("[BetterVia] 解压Zip: " + apkFile.getAbsolutePath() + " -> " + destDir.getAbsolutePath());
		ZipFile zipFile = new ZipFile(apkFile);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File file = new File(destDir, entry.getName());
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[MONET_BUFFER_SIZE];
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		}
		zipFile.close();
		bvLog("[BetterVia] Zip解压完成");
	}
	private void collectFiles(File dir, List<String> files, File rootDir) {
		File[] list = dir.listFiles();
		if (list != null) {
			for (File file : list) {
				if (file.isDirectory()) {
					collectFiles(file, files, rootDir);
				} else {
					String relativePath = file.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1);
					files.add(relativePath.replace("\\", "/"));
				}
			}
		}
	}
	private void showStorageManagerDialog(final Activity act) {
		if (act == null)
			return;
		final String packageName = act.getPackageName(); 
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				LinearLayout container = new LinearLayout(act);
				container.setOrientation(LinearLayout.VERTICAL);
				int padding = dp(act, 20);
				container.setPadding(padding, padding, padding, padding);
				final File internalDir = new File("/data/user/0/" + packageName + "/files/BetterVia/");
				final File externalDir = new File(
						"/storage/emulated/0/Android/data/" + packageName + "/files/BetterVia/");
				long totalSize = getFolderSize(internalDir) + getFolderSize(externalDir);
				String sizeStr = formatSize(totalSize);
				final TextView cacheTitle = createTitle(act,
						LocalizedStringProvider.getInstance().get(act, "storage_cache_title")); 
				final TextView cacheSize = createSubtitle(act, sizeStr); 
				Button clearCacheBtn = createButton(act,
						LocalizedStringProvider.getInstance().get(act, "storage_clear")); 
				clearCacheBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteFolder(internalDir);
						deleteFolder(externalDir);
						cacheSize.setText(formatSize(0));
					}
				});
				container.addView(cacheTitle);
				container.addView(cacheSize);
				container.addView(clearCacheBtn);
				final TextView dataTitle = createTitle(act,
						LocalizedStringProvider.getInstance().get(act, "storage_clear_data_title")); 
				final Button clearDataBtn = createButton(act,
						LocalizedStringProvider.getInstance().get(act, "storage_clear"));
				clearDataBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog confirmDialog = new AlertDialog.Builder(act)
								.setTitle(LocalizedStringProvider.getInstance().get(act, "storage_confirm_title"))
								.setMessage(LocalizedStringProvider.getInstance().get(act, "storage_confirm_message"))
								.setNegativeButton(LocalizedStringProvider.getInstance().get(act, "dialog_cancel"),
										null)
								.setPositiveButton(
										LocalizedStringProvider.getInstance().get(act, "storage_confirm_delete"),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												performDeepClean(act, packageName, clearDataBtn);
											}
										})
								.create();
						applyAlertDialogTheme(act, act, confirmDialog);
						confirmDialog.show();
					}
				});
				container.addView(space(act, 16)); 
				container.addView(dataTitle);
				container.addView(clearDataBtn);
				AlertDialog dialog = new AlertDialog.Builder(act)
						.setTitle(LocalizedStringProvider.getInstance().get(act, "storage_title")).setView(container)
						.setPositiveButton(LocalizedStringProvider.getInstance().get(act, "dialog_close"), null)
						.create();
				applyAlertDialogTheme(act, act, dialog);
				dialog.show();
				animateDialogEntrance(container, act);
			}
		});
	}
	private void performDeepClean(final Activity act, final String packageName, final Button triggerBtn) {
		triggerBtn.setEnabled(false);
		triggerBtn.setText(LocalizedStringProvider.getInstance().get(act, "storage_cleaning"));
		final ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(act)));
		}
		final AlertDialog loadingDialog = new AlertDialog.Builder(act)
				.setTitle(LocalizedStringProvider.getInstance().get(act, "storage_cleaning_title")).setView(progressBar)
				.setCancelable(false).create();
		applyAlertDialogTheme(act, act, loadingDialog);
		loadingDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean success = true;
				try {
					File internalDir = new File("/data/user/0/" + packageName + "/files/BetterVia/");
					File externalDir = new File(
							"/storage/emulated/0/Android/data/" + packageName + "/files/BetterVia/");
					File prefFile = new File("/data/user/0/" + packageName + "/shared_prefs/BetterVia.xml");
					safeDelete(internalDir);
					safeDelete(externalDir);
					if (prefFile.exists()) {
						prefFile.delete();
					}
				} catch (Throwable t) {
					success = false;
				}
				final boolean finalSuccess = success;
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loadingDialog.dismiss();
						if (finalSuccess) {
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(act, "storage_clean_success"));
							act.getWindow().getDecorView().postDelayed(new Runnable() {
								@Override
								public void run() {
									Process.killProcess(Process.myPid());
									System.exit(0);
								}
							}, 800);
						} else {
							triggerBtn.setEnabled(true);
							triggerBtn
									.setText(LocalizedStringProvider.getInstance().get(act, "storage_confirm_delete"));
							jiguroMessageWithContext(act,
									LocalizedStringProvider.getInstance().get(act, "storage_clean_failed"));
						}
					}
				});
			}
		}).start();
	}
	private static long getFolderSize(File dir) {
		if (dir == null || !dir.exists())
			return 0;
		long size = 0;
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					size += getFolderSize(f);
				} else {
					size += f.length();
				}
			}
		}
		return size;
	}
	private static void deleteFolder(File dir) {
		if (dir == null || !dir.exists())
			return;
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				}
				f.delete();
			}
		}
	}
	private static String formatSize(long size) {
		if (size <= 0)
			return "0 B";
		final String[] units = new String[]{"B", "KB", "MB", "GB"};
		int digit = (int) (Math.log10(size) / Math.log10(1024));
		return String.format(Locale.getDefault(), "%.2f %s", size / Math.pow(1024, digit), units[digit]);
	}
	private TextView createTitle(Context ctx, String text) {
		TextView tv = new TextView(ctx);
		tv.setText(text);
		tv.setTextSize(16);
		tv.setTypeface(Typeface.DEFAULT_BOLD);
		tv.setTextColor(getTextColor(ctx));
		return tv;
	}
	private TextView createSubtitle(Context ctx, String text) {
		TextView tv = new TextView(ctx);
		tv.setText(text);
		tv.setTextSize(13);
		tv.setTextColor(getHintColor(ctx));
		tv.setAlpha(0.7f);
		return tv;
	}
	private Button createButton(Context ctx, String text) {
		Button btn = new Button(ctx);
		btn.setText(text);
		btn.setTextColor(getBtnTextColor(ctx));
		btn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
		return btn;
	}
	private static View space(Context ctx, int dp) {
		View v = new View(ctx);
		v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(ctx, dp)));
		return v;
	}
	private static void safeDelete(File dir) {
		if (dir == null || !dir.exists())
			return;
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (File f : files) {
			try {
				if (f.isDirectory()) {
					safeDelete(f);
				}
				f.delete();
			} catch (Throwable ignored) {
			}
		}
	}
	private void setUrlSchemeHook(final Context ctx, final ClassLoader cl, final boolean enable) {
		try {
			if (enable) {
				bvLog("[BetterVia] 启用URL Scheme显示功能");
				showUrlSchemeEnabled = true;
				installUrlSchemeHook(ctx, cl);
			} else {
				bvLog("[BetterVia] 禁用URL Scheme显示功能");
				showUrlSchemeEnabled = false;
				if (showUrlSchemeHook != null) {
					showUrlSchemeHook.unhook();
					showUrlSchemeHook = null;
				}
			}
		} catch (Throwable t) {
			bvLog("[BetterVia] 设置URL Scheme Hook失败: " + t);
		}
	}
	private void installUrlSchemeHook(final Context ctx, final ClassLoader cl) {
		try {
			XposedHelpers.findAndHookMethod(Activity.class, "startActivity", Intent.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!showUrlSchemeEnabled) {
						return;
					}
					Intent intent = (Intent) param.args[0];
					if (intent != null && intent.getData() != null) {
						String scheme = intent.getScheme();
						String dataString = intent.getDataString();
						if (scheme != null && !scheme.equals("http") && !scheme.equals("https")
								&& !scheme.equals("file")) {
							bvLog("[BetterVia] 检测到URL Scheme: " + dataString);
							showUrlSchemeToast(ctx, dataString);
						}
					}
				}
			});
			XposedHelpers.findAndHookMethod(Activity.class, "startActivityForResult", Intent.class, int.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							if (!showUrlSchemeEnabled) {
								return;
							}
							Intent intent = (Intent) param.args[0];
							if (intent != null && intent.getData() != null) {
								String scheme = intent.getScheme();
								String dataString = intent.getDataString();
								if (scheme != null && !scheme.equals("http") && !scheme.equals("https")
										&& !scheme.equals("file")) {
									bvLog("[BetterVia] 检测到URL Scheme: " + dataString);
									showUrlSchemeToast(ctx, dataString);
								}
							}
						}
					});
			XposedHelpers.findAndHookMethod(Context.class, "startActivity", Intent.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!showUrlSchemeEnabled) {
						return;
					}
					Intent intent = (Intent) param.args[0];
					if (intent != null && intent.getData() != null) {
						String scheme = intent.getScheme();
						String dataString = intent.getDataString();
						if (scheme != null && !scheme.equals("http") && !scheme.equals("https")
								&& !scheme.equals("file")) {
							bvLog("[BetterVia] 检测到URL Scheme: " + dataString);
							showUrlSchemeToast(ctx, dataString);
						}
					}
				}
			});
			bvLog("[BetterVia] URL Scheme Hook安装成功");
		} catch (Throwable t) {
			bvLog("[BetterVia] 安装URL Scheme Hook失败: " + t);
		}
	}
	private void showUrlSchemeToast(final Context ctx, final String urlScheme) {
		if (ctx == null || urlScheme == null) {
			return;
		}
		try {
			synchronized (displayedUrlSchemes) {
				if (displayedUrlSchemes.contains(urlScheme)) {
					return;
				}
				displayedUrlSchemes.add(urlScheme);
			}
			Activity activity = currentActivity;
			if (activity == null) {
				activity = Context;
			}
			if (activity == null) {
				return;
			}
			final Activity finalActivity = activity;
			finalActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (finalActivity.isFinishing() || finalActivity.isDestroyed()) {
						return;
					}
					try {
						final FrameLayout overlay = new FrameLayout(finalActivity);
						overlay.setBackgroundColor(0xCC000000); 
						TextView titleView = new TextView(finalActivity);
						titleView.setText(LocalizedStringProvider.getInstance().get(ctx, "url_scheme_detected"));
						titleView.setTextColor(Color.WHITE); 
						titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						titleView.setTypeface(null, Typeface.BOLD);
						titleView.setPadding(dp(finalActivity, 16), dp(finalActivity, 12), dp(finalActivity, 16),
								dp(finalActivity, 8));
						final TextView textView = new TextView(finalActivity);
						textView.setText(urlScheme);
						textView.setTextColor(Color.WHITE);
						textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
						textView.setPadding(dp(finalActivity, 16), dp(finalActivity, 8), dp(finalActivity, 16),
								dp(finalActivity, 8));
						textView.setMaxWidth(dp(finalActivity, 320)); 
						textView.setEllipsize(TextUtils.TruncateAt.END);
						textView.setSingleLine(true);
						final TextView hintView = new TextView(finalActivity);
						hintView.setText(LocalizedStringProvider.getInstance().get(ctx, "url_scheme_copied"));
						hintView.setTextColor(Color.GREEN);
						hintView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
						hintView.setPadding(dp(finalActivity, 16), dp(finalActivity, 8), dp(finalActivity, 16),
								dp(finalActivity, 12));
						hintView.setVisibility(View.GONE); 
						GradientDrawable bg = new GradientDrawable();
						bg.setColor(0xCC000000);
						bg.setCornerRadius(dp(finalActivity, 12)); 
						overlay.setBackgroundDrawable(bg);
						LinearLayout layout = new LinearLayout(finalActivity);
						layout.setOrientation(LinearLayout.VERTICAL);
						layout.addView(titleView);
						layout.addView(textView);
						layout.addView(hintView);
						overlay.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									ClipboardManager clipboard = (ClipboardManager) finalActivity
											.getSystemService(Context.CLIPBOARD_SERVICE);
									android.content.ClipData clip = android.content.ClipData.newPlainText("URL Scheme",
											urlScheme);
									clipboard.setPrimaryClip(clip);
									hintView.setVisibility(View.VISIBLE);
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											hintView.setVisibility(View.GONE);
										}
									}, 2000);
									bvLog("[BetterVia] 已复制URL Scheme: " + urlScheme);
								} catch (Exception e) {
									bvLog("[BetterVia] 复制URL Scheme失败: " + e);
								}
							}
						});
						overlay.addView(layout);
						int overlayIndex;
						synchronized (urlSchemeOverlays) {
							overlayIndex = urlSchemeOverlays.size();
							urlSchemeOverlays.add(overlay);
							urlSchemeTextViews.add(textView);
						}
						int marginTop = dp(finalActivity, 16) + overlayIndex * (dp(finalActivity, 110));
						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(finalActivity, 320),
								ViewGroup.LayoutParams.WRAP_CONTENT);
						params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
						params.setMargins(dp(finalActivity, 16), marginTop, dp(finalActivity, 16), 0);
						ViewGroup decorView = (ViewGroup) finalActivity.getWindow().getDecorView();
						decorView.addView(overlay, params);
						TranslateAnimation slideInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
								-1.0f, 
								Animation.RELATIVE_TO_PARENT, 0.0f, 
								Animation.RELATIVE_TO_PARENT, 0.0f, 
								Animation.RELATIVE_TO_PARENT, 0.0f); 
						slideInAnimation.setDuration(300); 
						slideInAnimation.setInterpolator(new DecelerateInterpolator()); 
						overlay.startAnimation(slideInAnimation);
						final Handler dismissHandler = new Handler(Looper.getMainLooper());
						synchronized (urlSchemeDismissHandlers) {
							urlSchemeDismissHandlers.add(dismissHandler);
						}
						dismissHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								TranslateAnimation slideOutAnimation = new TranslateAnimation(
										Animation.RELATIVE_TO_PARENT, 0.0f, 
										Animation.RELATIVE_TO_PARENT, 1.0f, 
										Animation.RELATIVE_TO_PARENT, 0.0f, 
										Animation.RELATIVE_TO_PARENT, 0.0f); 
								slideOutAnimation.setDuration(300); 
								slideOutAnimation.setInterpolator(new AccelerateInterpolator()); 
								slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(Animation animation) {
									}
									@Override
									public void onAnimationEnd(Animation animation) {
										ViewGroup parent = (ViewGroup) overlay.getParent();
										if (parent != null) {
											parent.removeView(overlay);
										}
										synchronized (urlSchemeOverlays) {
											urlSchemeOverlays.remove(overlay);
											urlSchemeTextViews.remove(textView);
										}
										synchronized (urlSchemeDismissHandlers) {
											urlSchemeDismissHandlers.remove(dismissHandler);
										}
										synchronized (displayedUrlSchemes) {
											displayedUrlSchemes.remove(urlScheme);
										}
										bvLog("[BetterVia] URL Scheme浮窗已自动消失: " + urlScheme);
									}
									@Override
									public void onAnimationRepeat(Animation animation) {
									}
								});
								overlay.startAnimation(slideOutAnimation);
							}
						}, URL_SCHEME_DISPLAY_DURATION);
						bvLog("[BetterVia] 已显示URL Scheme浮窗（第" + (overlayIndex + 1) + "个）: " + urlScheme);
					} catch (Exception e) {
						bvLog("[BetterVia] 显示URL Scheme浮窗失败: " + e);
					}
				}
			});
		} catch (Exception e) {
			bvLog("[BetterVia] 显示URL Scheme失败: " + e);
		}
	}
}