package com.jiguro.bettervia;
import android.animation.*;
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
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;
import org.json.*;
import android.content.ClipboardManager;
public class Hook implements IXposedHookLoadPackage
{
	private static final String MODULE_VERSION_NAME = "1.4.0";
	private static final int MODULE_VERSION_CODE = 20260111;
	private static final String SUPPORTED_VIA_VERSION = "6.9.0";
	private static volatile boolean hasShownBlockedToast = false;
	private static Activity Context = null; 
	private static Object moduleButtonRef = null; 
	private static String currentPackageName = ""; 
	private static Activity currentActivity = null; 
	private static final String KEY_WHITELIST = "enable_whitelist_hook";
	private static final String KEY_B_HOOK = "enable_b_hook";
	private static final String KEY_COMPONENT_BLOCK = "component_block_settings";
	private static final String KEY_BLOCK_STARTUP_MESSAGE = "block_startup_message";
	private static final String KEY_BLOCK_GOOGLE_SERVICES = "block_google_services";
	private static final String KEY_DOWNLOAD_DIALOG_SHARE = "download_dialog_share";
	private static final String KEY_DISABLE_CUSTOM_TABS = "disable_custom_tabs";
	private static final String KEY_EYE_PROTECTION = "eye_protection_mode";
	private static final String KEY_EYE_TEMPERATURE = "eye_protection_temperature";
	private static final String KEY_EYE_TEXTURE = "eye_protection_texture";
	private static final String KEY_HOMEPAGE_BG = "homepage_background_image";
	private static final String KEY_HOMEPAGE_MASK_A = "homepage_mask_alpha";
	private static final String KEY_HOMEPAGE_MASK_C = "homepage_mask_color";
	private static final String KEY_HIDE_STATUS_BAR = "hide_status_bar";
	private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
	private static final String KEY_SCREENSHOT_PROTECTION = "screenshot_protection";
	private static final String KEY_NETWORK_SOURCE = "network_source";
	private static final String KEY_AUTO_UPDATE = "auto_update";
	private static final String KEY_HOMEPAGE_THEME = "homepage_theme_settings";
	private static final String KEY_CURRENT_THEME = "current_homepage_theme";
	private static final String KEY_SCRIPT_REPOSITORY = "script_repository_settings";
	private static final String KEY_AD_BLOCK_RULES = "ad_block_rules_settings";
	private static final String KEY_SEARCH_COMMANDS = "search_commands_settings";
	private static final String KEY_COOKIE_MANAGEMENT = "cookie_management_settings";
	private static final String KEY_USER_AGENT = "user_agent_settings";
	private static final String KEY_BACKGROUND_VIDEO = "background_video_audio";
	private static final String KEY_VERSION_CHECK = "version_check_settings";
	private static final String KEY_VERSION_CHECK_DISABLED = "version_check_disabled";
	private static boolean whitelistHookEnabled = true;
	private static boolean eyeProtectionEnabled = false;
	private static boolean blockGoogleServicesEnabled = false;
	private static boolean blockStartupMessageEnabled = false;
	private static boolean screenshotProtectionEnabled = false;
	private static boolean keepScreenOnEnabled = false;
	private static boolean hideStatusBarEnabled = false;
	private static boolean autoUpdateEnabled = true;
	private static boolean downloadDialogShareEnabled = false;
	private static boolean disableCustomTabsEnabled = false;
	private static int eyeTemperature = 50;
	private static int eyeTexture = 0;
	private static String homepageBgPath = "";
	private static int homepageMaskAlpha = 120;
	private static int homepageMaskColor = 0x80000000;
	private static boolean backgroundVideoEnabled = false;
	private static XC_MethodHook.Unhook whitelistHook = null;
	private static XC_MethodHook.Unhook bHook = null;
	private static XC_MethodHook.Unhook componentHook = null;
	private static XC_MethodHook.Unhook activityHook = null;
	private static XC_MethodHook.Unhook firebaseAnalyticsHook = null;
	private static XC_MethodHook.Unhook googleAnalyticsHook = null;
	private static XC_MethodHook.Unhook screenshotProtectionHook = null;
	private static XC_MethodHook.Unhook keepScreenOnHook = null;
	private static XC_MethodHook.Unhook hideStatusBarHook = null;
	private static XC_MethodHook.Unhook downloadDialogShareHook = null;
	private static XC_MethodHook.Unhook backgroundVideoHook = null;
	private static XC_MethodHook.Unhook customTabHook1 = null;
	private static XC_MethodHook.Unhook customTabHook2 = null;
	private static XC_MethodHook.Unhook[] customTabServiceHooks = null;
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
	private static final String NETWORK_SOURCE_GITEE = "gitee";
	private static final String NETWORK_SOURCE_GITHUB = "github";
	private static final String DEFAULT_NETWORK_SOURCE = NETWORK_SOURCE_GITEE;
	private static final String GITEE_THEMES_JSON_URL = "https:
	private static final String GITHUB_THEMES_JSON_URL = "https:
	private static final String GITHUB_UPDATE_URL = "https:
	private static final String GITEE_UPDATE_URL = "https:
	private static final String GITEE_SHISUI_JSON_URL = "https:
	private static final String GITHUB_SHISUI_JSON_URL = "https:
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
	private static class ThemeInfo
	{
		String id;
		Map<String, String> nameMap; 
		Map<String, String> authorMap; 
		String previewUrl;
		Map<String, String> htmlUrls; 
		Map<String, String> cssUrls; 
		ThemeInfo(String id, Map<String, String> nameMap, Map<String, String> authorMap, String previewUrl,
				  Map<String, String> htmlUrls, Map<String, String> cssUrls)
		{
			this.id = id;
			this.nameMap = nameMap;
			this.authorMap = authorMap;
			this.previewUrl = previewUrl;
			this.htmlUrls = htmlUrls;
			this.cssUrls = cssUrls;
		}
		String getName(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
		}
		String getAuthor(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return authorMap.getOrDefault(langCode, authorMap.get("zh-CN"));
		}
		static ThemeInfo fromJSON(JSONObject json) throws JSONException
		{
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext())
			{
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> authorMap = new HashMap<>();
			JSONObject authors = json.getJSONObject("authors");
			Iterator<String> authorKeys = authors.keys();
			while (authorKeys.hasNext())
			{
				String lang = authorKeys.next();
				authorMap.put(lang, authors.getString(lang));
			}
			String previewUrl = json.getString("previewUrl");
			Map<String, String> htmlUrls = new HashMap<>();
			JSONObject htmls = json.getJSONObject("htmlUrls");
			Iterator<String> htmlKeys = htmls.keys();
			while (htmlKeys.hasNext())
			{
				String pkg = htmlKeys.next();
				htmlUrls.put(pkg, htmls.getString(pkg));
			}
			Map<String, String> cssUrls = new HashMap<>();
			JSONObject csss = json.getJSONObject("cssUrls");
			Iterator<String> cssKeys = csss.keys();
			while (cssKeys.hasNext())
			{
				String pkg = cssKeys.next();
				cssUrls.put(pkg, csss.getString(pkg));
			}
			return new ThemeInfo(id, nameMap, authorMap, previewUrl, htmlUrls, cssUrls);
		}
		private String getLanguageCode(Context ctx)
		{
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved))
			{
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				}
				else
				{
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale))
				{
					return "zh-CN";
				}
				else if (Locale.TRADITIONAL_CHINESE.equals(locale))
				{
					return "zh-TW";
				}
				else if (Locale.ENGLISH.equals(locale))
				{
					return "en";
				}
				return "zh-CN"; 
			}
			return saved;
		}
	}
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam pkg) throws Throwable
	{
		if (pkg.packageName.equals("com.jiguro.bettervia"))
		{
			try
			{
				Class<?> clazz = pkg.classLoader.loadClass("com.jiguro.bettervia.ModuleStatus"); 
				java.lang.reflect.Field field = clazz.getDeclaredField("activated");
				field.setAccessible(true);
				field.setBoolean(null, true);
			}
			catch (Throwable ignored)
			{
			}
			return;
		}
		try
		{
			pkg.classLoader.loadClass("k.a.c0.i.k");
			currentPackageName = pkg.packageName;
			handleViaApp(pkg); 
		}
		catch (ClassNotFoundException e)
		{
		}
	}
	private void handleViaApp(final XC_LoadPackage.LoadPackageParam param)
	{
		XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam attachParam) throws Throwable
				{
					final Context ctx = (Context) attachParam.args[0];
					final ClassLoader cl = ctx.getClassLoader();
					XposedHelpers.findAndHookMethod(Toast.class, "makeText", Context.class, CharSequence.class, int.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								if (param.getResult() == null) return;
								CharSequence msg = (CharSequence) param.args[1];
								if (msg == null) return;
								String message = msg.toString();
								if (message.contains("token") && message.contains("not valid"))
								{
									StackTraceElement[] stack = Thread.currentThread().getStackTrace();
									for (StackTraceElement el : stack)
									{
										if (el.getClassName().contains("mark.via.BrowserApp"))
										{
											XposedBridge.log("[BetterVia] 已屏蔽 BrowserApp 的 BadTokenException Toast: " + message);
											param.setResult(null); 
											return;
										}
									}
								}
							}
						});
					if (Context == null)
					{
						XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
								@Override
								protected void afterHookedMethod(MethodHookParam param) throws Throwable
								{
									if (Context == null)
									{
										Context = (Activity) param.thisObject;
										checkViaVersion(ctx);
										if (!getPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, false))
										{
											jiguroMessage(getLocalizedString(ctx, "hook_success_message"));
										}
										XposedBridge.log("[BetterVia] 初加载成功，得到Via活动上下文");
									}
									currentActivity = (Activity) param.thisObject;
								}
							});
					}
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
					homepageBgPath = getPrefString(ctx, KEY_HOMEPAGE_BG, "");
					homepageMaskAlpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
					homepageMaskColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x80000000);
					if (!homepageBgPath.equals(""))
					{
						hookHomepageBgWithMask(ctx, cl, homepageBgPath, homepageMaskColor);
					}
					boolean blockGoogleServices = getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false);
					setGoogleServicesInterceptHook(ctx, cl, blockGoogleServices);
					downloadDialogShareEnabled = getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false);
					if (downloadDialogShareEnabled)
					{
						setDownloadDialogShareHook(ctx, cl, true);
					}
					disableCustomTabsEnabled = getPrefBoolean(ctx, KEY_DISABLE_CUSTOM_TABS, false);
					if (disableCustomTabsEnabled)
					{
						setCustomTabsHook(ctx, cl, true);
					}
					autoUpdateEnabled = getPrefBoolean(ctx, KEY_AUTO_UPDATE, true);
					if (autoUpdateEnabled)
					{
						checkUpdateOnStart(ctx);
					}
					if (whitelistHookEnabled)
					{
						XposedHelpers.findAndHookMethod("k.a.c0.i.k", cl, "u", "k.a.c0.i.a", new XC_MethodHook() {
								@Override
								protected void beforeHookedMethod(MethodHookParam param) throws Throwable
								{
									param.setResult(null);
									XposedBridge.log("[BetterVia] 已解除Via白名单限制");
								}
							});
					}
					keepScreenOnEnabled = getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false);
					setKeepScreenOn(ctx, cl, keepScreenOnEnabled);
					backgroundVideoEnabled = getPrefBoolean(ctx, KEY_BACKGROUND_VIDEO, false);
					setBackgroundVideoAudio(ctx, cl, backgroundVideoEnabled);
					XposedHelpers.findAndHookMethod("k.a.o0.f7", cl, "f3", new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								List<Object> orig = (List<Object>) param.getResult();
								if (orig == null)
									orig = new ArrayList<>();
								List<Object> nList = new ArrayList<>(orig);
								Class<?> yClass = XposedHelpers.findClass("e.h.g.g.y", cl);
								String txt = getLocalizedString(ctx, "module_settings");
								Object btn = XposedHelpers.newInstance(yClass, 1000, txt);
								moduleButtonRef = btn;
								nList.add(btn);
								param.setResult(nList);
								XposedBridge.log("[BetterVia] 已在Via设置列表中添加模块按钮");
							}
						});
					XposedHelpers.findAndHookMethod("e.h.g.g.a0$a", cl, "a", View.class, new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								Object clicked = XposedHelpers.getObjectField(param.thisObject, "d");
								if (clicked == null)
									return;
								int id = XposedHelpers.getIntField(clicked, "b");
								if (id == 1000)
								{
									XposedBridge.log("[BetterVia] 模块按钮被点击");
									showSettingsDialog(ctx);
								}
							}
						});
					XposedHelpers.findAndHookMethod(Activity.class, "onActivityResult", int.class, int.class, Intent.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								int req = (Integer) param.args[0];
								int res = (Integer) param.args[1];
								Intent data = (Intent) param.args[2];
								handleActivityResult(req, res, data, (Activity) param.thisObject);
							}
						});
					XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								Activity activity = (Activity) param.thisObject;
								screenOnActivities.remove(activity);
								statusBarHiddenActivities.remove(activity);
								Runnable rehideRunnable = statusBarRehideRunnables.get(activity);
								if (rehideRunnable != null)
								{
									View decorView = activity.getWindow().getDecorView();
									decorView.removeCallbacks(rehideRunnable);
									statusBarRehideRunnables.remove(activity);
								}
								if (currentActivity == activity)
								{
									currentActivity = null;
								}
							}
						});
					final String last = getSavedLanguage(ctx);
					if (!"auto".equals(last))
					{
						updateViaLocale(ctx, last);
					}
				}
			});
	}
	private void showSettingsDialog(final Context ctx)
	{
		Activity activityRef = currentActivity;
		if (activityRef == null)
		{
			activityRef = Context;
		}
		if (activityRef == null || !(activityRef instanceof Activity))
		{
			return;
		}
		final Activity act = activityRef;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
					{
						return;
					}
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					GradientDrawable bg = new GradientDrawable();
					bg.setColor(Color.WHITE);
					bg.setCornerRadius(dp(act, 24));
					root.setBackground(bg);
					TextView title = new TextView(act);
					title.setText("BetterVia");
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					title.setTextColor(0xFF6200EE); 
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					title.setPadding(0, 0, 0, dp(act, 16));
					root.addView(title);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "module_settings_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
					subtitle.setGravity(Gravity.CENTER);
					subtitle.setPadding(0, 0, 0, dp(act, 24));
					root.addView(subtitle);
					addSwitch(root, act, getLocalizedString(ctx, "whitelist_switch"),
						getLocalizedString(ctx, "whitelist_hint"), KEY_WHITELIST, true, new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_WHITELIST, true);
								setWhitelistHook(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "block_google_switch"),
						getLocalizedString(ctx, "block_google_hint"), KEY_BLOCK_GOOGLE_SERVICES, false, new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false);
								setGoogleServicesInterceptHook(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "screenshot_protection_switch"),
						getLocalizedString(ctx, "screenshot_protection_hint"), KEY_SCREENSHOT_PROTECTION, false,
						new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, false);
								setScreenshotProtection(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "block_startup_message_switch"),
						getLocalizedString(ctx, "block_startup_message_hint"), KEY_BLOCK_STARTUP_MESSAGE, false,
						new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, false);
								blockStartupMessageEnabled = on;
								putPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "keep_screen_on_switch"),
						getLocalizedString(ctx, "keep_screen_on_hint"), KEY_KEEP_SCREEN_ON, false, new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false);
								setKeepScreenOn(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "hide_status_bar_switch"),
						getLocalizedString(ctx, "hide_status_bar_hint"), KEY_HIDE_STATUS_BAR, false, new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false);
								setHideStatusBar(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "download_dialog_share_switch"),
						getLocalizedString(ctx, "download_dialog_share_hint"), KEY_DOWNLOAD_DIALOG_SHARE, false,
						new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false);
								setDownloadDialogShareHook(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "disable_custom_tabs_switch"),
						getLocalizedString(ctx, "disable_custom_tabs_hint"), KEY_DISABLE_CUSTOM_TABS, false,
						new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_DISABLE_CUSTOM_TABS, false);
								setCustomTabsHook(ctx, act.getClassLoader(), on);
							}
						});
					addSwitch(root, act, getLocalizedString(ctx, "eye_protection_switch"),
						getLocalizedString(ctx, "eye_protection_hint"), KEY_EYE_PROTECTION, false, new Runnable() {
							@Override
							public void run()
							{
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
					View div = new View(act);
					LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					dp(act, 1));
					divLp.setMargins(0, dp(act, 12), 0, dp(act, 12));
					div.setLayoutParams(divLp);
					div.setBackgroundColor(0xFFDDDDDD);
					root.addView(div);
					LinearLayout langRow = new LinearLayout(act);
					langRow.setOrientation(LinearLayout.HORIZONTAL);
					langRow.setGravity(Gravity.CENTER_VERTICAL);
					TextView langTitle = new TextView(act);
					langTitle.setText(getLocalizedString(ctx, "language_title"));
					langTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					langTitle.setTextColor(Color.BLACK);
					langRow.addView(langTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
					final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
					final String[] langItems = new String[]{getLocalizedString(ctx, "language_auto"), "简体中文", "繁體中文",
						"English"};
					String savedLang = getSavedLanguage(ctx);
					int langIdx = 0;
					for (int i = 0; i < langValues.length; i++)
						if (langValues[i].equals(savedLang))
						{
							langIdx = i;
							break;
						}
					final TextView langSelector = new TextView(act);
					langSelector.setText(langItems[langIdx]);
					langSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					langSelector.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					langSelector.setBackground(getRoundBg(act, 0xFFE0E0E0, 12));
					langSelector.setTextColor(0xFF000000);
					langRow.addView(langSelector);
					root.addView(langRow);
					TextView langHintTv = new TextView(act);
					langHintTv.setText(getLocalizedString(ctx, "language_hint"));
					langHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					langHintTv.setTextColor(0xFF666666);
					langHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
					root.addView(langHintTv);
					LinearLayout sourceRow = new LinearLayout(act);
					sourceRow.setOrientation(LinearLayout.HORIZONTAL);
					sourceRow.setGravity(Gravity.CENTER_VERTICAL);
					TextView sourceTitle = new TextView(act);
					sourceTitle.setText(getLocalizedString(ctx, "network_source_title"));
					sourceTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					sourceTitle.setTextColor(Color.BLACK);
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
					sourceSelector.setBackground(getRoundBg(act, 0xFFE0E0E0, 12));
					sourceSelector.setTextColor(0xFF000000);
					sourceRow.addView(sourceSelector);
					root.addView(sourceRow);
					TextView sourceHintTv = new TextView(act);
					sourceHintTv.setText(getLocalizedString(ctx, "network_source_hint"));
					sourceHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					sourceHintTv.setTextColor(0xFF666666);
					sourceHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
					root.addView(sourceHintTv);
					sourceSelector.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								showSourcePopup(ctx, sourceSelector, sourceValues, sourceItems, new SourceSelectedCallback() {
										@Override
										public void onSelected(int pos)
										{
											String selectedSource = sourceValues[pos];
											putPrefString(ctx, KEY_NETWORK_SOURCE, selectedSource);
											sourceSelector.setText(sourceItems[pos]);
											themesLoaded = false;
											loadedThemes.clear();
											Toast.makeText(ctx,
														   getLocalizedString(ctx, "network_source_changed") + " " + sourceItems[pos],
														   Toast.LENGTH_SHORT).show();
										}
									});
							}
						});
					LinearLayout aboutRow = new LinearLayout(act);
					aboutRow.setOrientation(LinearLayout.HORIZONTAL);
					aboutRow.setGravity(Gravity.CENTER_VERTICAL);
					TextView aboutTitle = new TextView(act);
					aboutTitle.setText(getLocalizedString(ctx, "about_title"));
					aboutTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					aboutTitle.setTextColor(Color.BLACK);
					aboutRow.addView(aboutTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
					TextView aboutBtn = new TextView(act);
					aboutBtn.setText(getLocalizedString(ctx, "about_view"));
					aboutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					aboutBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					aboutBtn.setBackground(getRoundBg(act, 0xFFE0E0E0, 8));
					aboutBtn.setTextColor(0xFF000000);
					aboutBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								showAboutDialog(ctx);
							}
						});
					aboutRow.addView(aboutBtn);
					root.addView(aboutRow);
					TextView aboutHintTv = new TextView(act);
					aboutHintTv.setText(getLocalizedString(ctx, "about_hint"));
					aboutHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					aboutHintTv.setTextColor(0xFF666666);
					aboutHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
					root.addView(aboutHintTv);
					LinearLayout shisuiRow = new LinearLayout(act);
					shisuiRow.setOrientation(LinearLayout.HORIZONTAL);
					shisuiRow.setGravity(Gravity.CENTER_VERTICAL);
					TextView shisuiTitle = new TextView(act);
					shisuiTitle.setText(getLocalizedString(ctx, "shisui_title"));
					shisuiTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					shisuiTitle.setTextColor(Color.BLACK);
					shisuiRow.addView(shisuiTitle,
									  new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
					TextView shisuiBtn = new TextView(act);
					shisuiBtn.setText(getLocalizedString(ctx, "shisui_view"));
					shisuiBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					shisuiBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					shisuiBtn.setBackground(getRoundBg(act, 0xFFE0E0E0, 8));
					shisuiBtn.setTextColor(0xFF000000);
					shisuiBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								showShisuiDialog(ctx);
							}
						});
					shisuiRow.addView(shisuiBtn);
					root.addView(shisuiRow);
					TextView shisuiHintTv = new TextView(act);
					shisuiHintTv.setText(getLocalizedString(ctx, "shisui_hint"));
					shisuiHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					shisuiHintTv.setTextColor(0xFF666666);
					shisuiHintTv.setPadding(0, dp(act, 4), 0, dp(act, 12));
					root.addView(shisuiHintTv);
					addSwitch(root, act, getLocalizedString(ctx, "auto_update_switch"),
						getLocalizedString(ctx, "auto_update_hint"), KEY_AUTO_UPDATE, true, new Runnable() {
							@Override
							public void run()
							{
								boolean on = getPrefBoolean(ctx, KEY_AUTO_UPDATE, true);
								autoUpdateEnabled = on;
								putPrefBoolean(ctx, KEY_AUTO_UPDATE, on);
							}
						});
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					GradientDrawable btnBg = new GradientDrawable();
					btnBg.setColor(0xFF6200EE);
					btnBg.setCornerRadius(dp(act, 12));
					ok.setBackground(btnBg);
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					final AlertDialog[] dialogRef = new AlertDialog[1];
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setView(scrollRoot);
					dialogRef[0] = builder.create();
					Window win = dialogRef[0].getWindow();
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						GradientDrawable round = new GradientDrawable();
						round.setColor(Color.WHITE);
						round.setCornerRadius(dp(act, 24));
						win.setBackgroundDrawable(round);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialogRef[0].dismiss();
							}
						});
					langSelector.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								showLangPopup(ctx, langSelector, langValues, langItems, new LangSelectedCallback() {
										@Override
										public void onSelected(int pos)
										{
											saveLanguageSetting(ctx, langValues[pos]);
											showLanguageChangeToast(ctx, pos);
											refreshModuleButtonText(ctx);
											langSelector.setText(langItems[pos]);
										}
									});
							}
						});
					dialogRef[0].show();
				}
			});
	}
	private void addComponentBlockItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "component_block_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "component_block_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showComponentBlockDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "component_block_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showComponentBlockDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					GradientDrawable bg = new GradientDrawable();
					bg.setColor(Color.WHITE);
					bg.setCornerRadius(dp(act, 24));
					root.setBackground(bg);
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "component_block_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					title.setTextColor(Color.BLACK);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 16);
					root.addView(title, titleLp);
					final Map<String, CheckBox> checkboxes = new HashMap<>();
					String[] componentNames = getComponentNames(ctx);
					for (int i = 0; i < COMPONENT_KEYS.length; i++)
					{
						CheckBox cb = new CheckBox(act);
						cb.setText(componentNames[i]);
						cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						cb.setTextColor(Color.BLACK);
						cb.setChecked(getPrefBoolean(ctx, COMPONENT_KEYS[i], false));
						LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					   ViewGroup.LayoutParams.WRAP_CONTENT);
						cbLp.bottomMargin = dp(act, 8);
						root.addView(cb, cbLp);
						checkboxes.put(COMPONENT_KEYS[i], cb);
					}
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					GradientDrawable btnBg = new GradientDrawable();
					btnBg.setColor(0xFF6200EE);
					btnBg.setCornerRadius(dp(act, 12));
					ok.setBackground(btnBg);
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
					Window win = dialog.getWindow();
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						GradientDrawable round = new GradientDrawable();
						round.setColor(Color.WHITE);
						round.setCornerRadius(dp(act, 24));
						win.setBackgroundDrawable(round);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								for (Map.Entry<String, CheckBox> entry : checkboxes.entrySet())
								{
									putPrefBoolean(ctx, entry.getKey(), entry.getValue().isChecked());
								}
								Toast.makeText(ctx, getLocalizedString(ctx, "component_block_saved"), Toast.LENGTH_SHORT)
									.show();
								dialog.dismiss();
							}
						});
					dialog.show();
				}
			});
	}
	private void setComponentBlockHook(final Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (componentHook == null)
			{
				componentHook = XposedHelpers.findAndHookMethod("java.util.ArrayList", null, "add", Object.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable
						{
							if (!isCalledFromK6A2())
								return;
							Object item = param.args[0];
							if (item == null)
								return;
							if (!"e.h.g.g.n".equals(item.getClass().getName()))
								return;
							int type = XposedHelpers.getIntField(item, "b"); 
							String[] componentNames = getComponentNames(ctx); 
							int index = mapTypeToIndex(type); 
							if (index < 0)
								return; 
							boolean block = getPrefBoolean(ctx, COMPONENT_KEYS[index], false);
							if (block)
							{
								XposedBridge.log("[BetterVia] 组件屏蔽：阻止类型 " + type + " → " + componentNames[index]);
								param.setResult(false);
							}
						}
					});
				XposedBridge.log("[BetterVia] 组件屏蔽逻辑已启用");
			}
		}
		else
		{
			if (componentHook != null)
			{
				componentHook.unhook();
				componentHook = null;
				XposedBridge.log("[BetterVia] 组件屏蔽逻辑已停用");
			}
		}
	}
	private int mapTypeToIndex(int type)
	{
		switch (type)
		{
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
	private boolean isCalledFromK6A2()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement el : stack)
		{
			if ("k.a.o0.f6".equals(el.getClassName()) && "X1".equals(el.getMethodName()))
			{
				return true;
			}
		}
		return false;
	}
	private String[] getComponentNames(Context ctx)
	{
		return new String[]{getLocalizedString(ctx, "component_update"), 
			getLocalizedString(ctx, "component_telegram"), 
			getLocalizedString(ctx, "component_qq"), 
			getLocalizedString(ctx, "component_email"), 
			getLocalizedString(ctx, "component_wechat"), 
			getLocalizedString(ctx, "component_donate"), 
			getLocalizedString(ctx, "component_assist"), 
			getLocalizedString(ctx, "component_agreement"), 
			getLocalizedString(ctx, "component_privacy"), 
			getLocalizedString(ctx, "component_opensource"), 
			getLocalizedString(ctx, "component_icp") 
		};
	}
	private interface LangSelectedCallback
	{
		void onSelected(int pos);
	}
	private void showLangPopup(final Context ctx, View anchor, final String[] values, String[] items,
							   final LangSelectedCallback callback)
	{
		final ListView list = new ListView(ctx);
		list.setDivider(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setSingleLine(true);
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
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
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					callback.onSelected(position);
					pop.dismiss();
				}
			});
		GradientDrawable bg = getRoundBg(ctx, Color.WHITE, 12);
		bg.setStroke(dp(ctx, 1), 0xFFE0E0E0); 
		list.setBackground(bg);
		list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4)); 
		pop.showAsDropDown(anchor, 0, dp(ctx, 4));
	}
	private interface SourceSelectedCallback
	{
		void onSelected(int pos);
	}
	private void showSourcePopup(final Context ctx, View anchor, final String[] values, String[] items,
								 final SourceSelectedCallback callback)
	{
		final ListView list = new ListView(ctx);
		list.setDivider(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setSingleLine(true);
				textView.setEllipsize(TextUtils.TruncateAt.END);
				textView.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
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
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					callback.onSelected(position);
					pop.dismiss();
				}
			});
		GradientDrawable bg = getRoundBg(ctx, Color.WHITE, 12);
		bg.setStroke(dp(ctx, 1), 0xFFE0E0E0);
		list.setBackground(bg);
		list.setPadding(0, dp(ctx, 4), 0, dp(ctx, 4));
		pop.showAsDropDown(anchor, 0, dp(ctx, 4));
	}
	private void addSwitch(LinearLayout parent, final Context ctx, String title, String hint, final String prefKey,
						   boolean defVal, final Runnable onChange)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(title);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		Switch sw = new Switch(ctx);
		sw.setChecked(getPrefBoolean(ctx, prefKey, defVal));
		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					putPrefBoolean(ctx, prefKey, isChecked);
					if (onChange != null)
						onChange.run();
				}
			});
		hor.addView(sw);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(hint);
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void setWhitelistHook(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (whitelistHook == null)
			{
				whitelistHook = XposedHelpers.findAndHookMethod("k.a.c0.i.k", cl, "u", "k.a.c0.i.a",
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable
						{
							param.setResult(null);
							XposedBridge.log("[BetterVia] 成功Hook白名单方法");
						}
					});
				XposedBridge.log("[BetterVia] 已解除Via白名单限制");
			}
		}
		else
		{
			if (whitelistHook != null)
			{
				whitelistHook.unhook();
				whitelistHook = null;
				XposedBridge.log("[BetterVia] Via白名单限制已恢复");
			}
		}
		whitelistHookEnabled = on;
		putPrefBoolean(ctx, KEY_WHITELIST, on);
	}
	private void setEyeProtectionMode(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (activityHook == null)
			{
				activityHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable
						{
							if (!eyeProtectionEnabled)
								return;
							final Activity activity = (Activity) param.thisObject;
							activity.runOnUiThread(new Runnable() {
									@Override
									public void run()
									{
										addEyeProtectionOverlay(activity, getPrefInt(activity, KEY_EYE_TEMPERATURE, 50),
																getPrefInt(activity, KEY_EYE_TEXTURE, 0));
									}
								});
						}
					});
				XposedBridge.log("[BetterVia] 护眼模式已启用");
			}
		}
		else
		{
			if (activityHook != null)
			{
				activityHook.unhook();
				activityHook = null;
				XposedBridge.log("[BetterVia] 护眼模式已停用");
			}
			removeAllEyeProtectionOverlays();
		}
		eyeProtectionEnabled = on;
		putPrefBoolean(ctx, KEY_EYE_PROTECTION, on);
	}
	private void addEyeProtectionOverlay(Activity activity, final int temperature, final int texture)
	{
		try
		{
			ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
			View existingOverlay = overlayViews.get(activity);
			if (existingOverlay != null)
			{
				rootView.removeView(existingOverlay);
			}
			View overlay = new View(activity) {
				@Override
				protected void onDraw(Canvas canvas)
				{
					super.onDraw(canvas);
					int color = calculateTemperatureColor(temperature);
					canvas.drawColor(color);
					if (texture > 0)
					{
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
			XposedBridge.log("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 添加护眼遮罩");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 添加护眼遮罩失败: " + e);
		}
	}
	private int calculateTemperatureColor(int temperature)
	{
		float ratio = temperature / 100.0f;
		int alpha = (int) (0x40 * ratio); 
		int r = (int) (255 * ratio); 
		int g = (int) (245 * ratio); 
		int b = (int) (200 * ratio); 
		return (alpha << 24) | (r << 16) | (g << 8) | b;
	}
	private void drawPaperTexture(Canvas canvas, int textureLevel)
	{
		Paint paint = new Paint();
		paint.setColor(0x20FFFFFF); 
		Random random = new Random(12345); 
		int density = textureLevel / 5; 
		for (int i = 0; i < density; i++)
		{
			float x = random.nextFloat() * canvas.getWidth();
			float y = random.nextFloat() * canvas.getHeight();
			float radius = random.nextFloat() * 2 + 1;
			canvas.drawCircle(x, y, radius, paint);
		}
		if (textureLevel > 50)
		{
			paint.setColor(0x10FFFFFF);
			for (int i = 0; i < textureLevel / 10; i++)
			{
				float x = random.nextFloat() * canvas.getWidth();
				float y = random.nextFloat() * canvas.getHeight();
				float radius = random.nextFloat() * 3 + 2;
				canvas.drawCircle(x, y, radius, paint);
			}
		}
	}
	private void updateEyeProtectionOverlay(Activity activity, int temperature, int texture)
	{
		View overlay = overlayViews.get(activity);
		if (overlay != null)
		{
			overlay.invalidate(); 
		}
		else if (eyeProtectionEnabled)
		{
			addEyeProtectionOverlay(activity, temperature, texture);
		}
	}
	private void updateAllEyeProtectionOverlays(int temperature, int texture)
	{
		for (Map.Entry<Activity, View> entry : overlayViews.entrySet())
		{
			Activity activity = entry.getKey();
			if (!activity.isFinishing() && !activity.isDestroyed())
			{
				updateEyeProtectionOverlay(activity, temperature, texture);
			}
		}
	}
	private void removeAllEyeProtectionOverlays()
	{
		for (Map.Entry<Activity, View> entry : overlayViews.entrySet())
		{
			Activity activity = entry.getKey();
			View overlay = entry.getValue();
			if (!activity.isFinishing() && !activity.isDestroyed())
			{
				ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
				rootView.removeView(overlay);
			}
		}
		overlayViews.clear();
		XposedBridge.log("[BetterVia] 已移除所有护眼遮罩");
	}
	private void addEyeProtectionConfig(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "eye_protection_config"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "eye_protection_config_btn"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showEyeProtectionConfigDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "eye_protection_config_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showEyeProtectionConfigDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final int savedTemperature = getPrefInt(ctx, KEY_EYE_TEMPERATURE, 50);
					final int savedTexture = getPrefInt(ctx, KEY_EYE_TEXTURE, 0);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					GradientDrawable bg = new GradientDrawable();
					bg.setColor(Color.WHITE);
					bg.setCornerRadius(dp(act, 24));
					root.setBackground(bg);
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "eye_protection_config_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(Color.BLACK);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "eye_protection_config_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					TextView previewTitle = new TextView(act);
					previewTitle.setText(getLocalizedString(ctx, "eye_protection_preview_title"));
					previewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					previewTitle.setTextColor(Color.BLACK);
					previewTitle.setTypeface(null, Typeface.BOLD);
					previewContainer.addView(previewTitle);
					FrameLayout previewContent = new FrameLayout(act);
					previewContent.setLayoutParams(
						new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 80)));
					previewContent.setBackgroundColor(Color.WHITE);
					previewContent.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
					GradientDrawable previewBg = new GradientDrawable();
					previewBg.setColor(Color.WHITE);
					previewBg.setStroke(dp(act, 1), 0xFFE0E0E0);
					previewBg.setCornerRadius(dp(act, 8));
					previewContent.setBackground(previewBg);
					TextView sampleText = new TextView(act);
					sampleText.setText(getLocalizedString(ctx, "eye_protection_sample_text"));
					sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					sampleText.setTextColor(Color.BLACK);
					FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					textLp.gravity = Gravity.CENTER;
					previewContent.addView(sampleText, textLp);
					final View previewOverlay = new View(act) {
						@Override
						protected void onDraw(Canvas canvas)
						{
							super.onDraw(canvas);
							if (tempSeekBarRef[0] != null)
							{
								int color = calculateTemperatureColor(tempSeekBarRef[0].getProgress());
								canvas.drawColor(color);
							}
							if (textureSeekBarRef[0] != null && textureSeekBarRef[0].getProgress() > 0)
							{
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
					tempTitle.setText(getLocalizedString(ctx, "eye_protection_temperature"));
					tempTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					tempTitle.setTextColor(Color.BLACK);
					tempTitle.setTypeface(null, Typeface.BOLD);
					tempContainer.addView(tempTitle);
					final SeekBar tempSeekBar = new SeekBar(act);
					tempSeekBar.setMax(100);
					tempSeekBar.setProgress(savedTemperature);
					tempSeekBarRef[0] = tempSeekBar; 
					tempContainer.addView(tempSeekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					 ViewGroup.LayoutParams.WRAP_CONTENT));
					LinearLayout tempLabels = new LinearLayout(act);
					tempLabels.setOrientation(LinearLayout.HORIZONTAL);
					TextView coldLabel = new TextView(act);
					coldLabel.setText(getLocalizedString(ctx, "eye_protection_cold"));
					coldLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					coldLabel.setTextColor(0xFF666666);
					LinearLayout.LayoutParams coldLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																					 1);
					tempLabels.addView(coldLabel, coldLp);
					TextView warmLabel = new TextView(act);
					warmLabel.setText(getLocalizedString(ctx, "eye_protection_warm"));
					warmLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					warmLabel.setTextColor(0xFF666666);
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
					textureTitle.setText(getLocalizedString(ctx, "eye_protection_texture"));
					textureTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					textureTitle.setTextColor(Color.BLACK);
					textureTitle.setTypeface(null, Typeface.BOLD);
					textureContainer.addView(textureTitle);
					final SeekBar textureSeekBar = new SeekBar(act);
					textureSeekBar.setMax(100);
					textureSeekBar.setProgress(savedTexture);
					textureSeekBarRef[0] = textureSeekBar; 
					textureContainer.addView(textureSeekBar, new LinearLayout.LayoutParams(
												 ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					LinearLayout textureLabels = new LinearLayout(act);
					textureLabels.setOrientation(LinearLayout.HORIZONTAL);
					TextView smoothLabel = new TextView(act);
					smoothLabel.setText(getLocalizedString(ctx, "eye_protection_smooth"));
					smoothLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					smoothLabel.setTextColor(0xFF666666);
					LinearLayout.LayoutParams smoothLp = new LinearLayout.LayoutParams(0,
																					   ViewGroup.LayoutParams.WRAP_CONTENT, 1);
					textureLabels.addView(smoothLabel, smoothLp);
					TextView roughLabel = new TextView(act);
					roughLabel.setText(getLocalizedString(ctx, "eye_protection_rough"));
					roughLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					roughLabel.setTextColor(0xFF666666);
					roughLabel.setGravity(Gravity.END);
					LinearLayout.LayoutParams roughLp = new LinearLayout.LayoutParams(0,
																					  ViewGroup.LayoutParams.WRAP_CONTENT, 1);
					textureLabels.addView(roughLabel, roughLp);
					textureContainer.addView(textureLabels, new LinearLayout.LayoutParams(
												 ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					root.addView(textureContainer);
					TextView previewHint = new TextView(act);
					previewHint.setText(getLocalizedString(ctx, "eye_protection_preview_hint"));
					previewHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					previewHint.setTextColor(0xFF888888);
					previewHint.setGravity(Gravity.CENTER);
					previewHint.setTypeface(null, Typeface.ITALIC);
					LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					 ViewGroup.LayoutParams.WRAP_CONTENT);
					hintLp.topMargin = dp(act, 8);
					hintLp.bottomMargin = dp(act, 16);
					root.addView(previewHint, hintLp);
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					GradientDrawable btnBg = new GradientDrawable();
					btnBg.setColor(0xFF6200EE);
					btnBg.setCornerRadius(dp(act, 12));
					ok.setBackground(btnBg);
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 8);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					final AlertDialog dialog = new AlertDialog.Builder(act).setView(scrollRoot).create();
					Window win = dialog.getWindow();
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						GradientDrawable round = new GradientDrawable();
						round.setColor(Color.WHITE);
						round.setCornerRadius(dp(act, 24));
						win.setBackgroundDrawable(round);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					SeekBar.OnSeekBarChangeListener previewListener = new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
						{
							if (fromUser && previewOverlayRef[0] != null)
							{
								previewOverlayRef[0].invalidate(); 
							}
						}
						@Override
						public void onStartTrackingTouch(SeekBar seekBar)
						{
						}
						@Override
						public void onStopTrackingTouch(SeekBar seekBar)
						{
						}
					};
					tempSeekBar.setOnSeekBarChangeListener(previewListener);
					textureSeekBar.setOnSeekBarChangeListener(previewListener);
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								int newTemperature = tempSeekBar.getProgress();
								int newTexture = textureSeekBar.getProgress();
								putPrefInt(ctx, KEY_EYE_TEMPERATURE, newTemperature);
								putPrefInt(ctx, KEY_EYE_TEXTURE, newTexture);
								updateAllEyeProtectionOverlays(newTemperature, newTexture);
								Toast.makeText(ctx, getLocalizedString(ctx, "eye_protection_config_saved"), Toast.LENGTH_SHORT)
									.show();
								dialog.dismiss();
							}
						});
					dialog.show();
				}
			});
	}
	private void drawPaperTexturePreview(Canvas canvas, int textureLevel, int width, int height)
	{
		Paint paint = new Paint();
		paint.setColor(0x20FFFFFF); 
		Random random = new Random(12345); 
		int density = textureLevel / 3; 
		int pointCount = (width * height) / 1000 * density / 10;
		for (int i = 0; i < pointCount; i++)
		{
			float x = random.nextFloat() * width;
			float y = random.nextFloat() * height;
			float radius = random.nextFloat() * 1.5f + 0.5f; 
			canvas.drawCircle(x, y, radius, paint);
		}
		if (textureLevel > 50)
		{
			paint.setColor(0x10FFFFFF);
			for (int i = 0; i < pointCount / 2; i++)
			{
				float x = random.nextFloat() * width;
				float y = random.nextFloat() * height;
				float radius = random.nextFloat() * 2 + 1;
				canvas.drawCircle(x, y, radius, paint);
			}
		}
	}
	private void setGoogleServicesInterceptHook(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			XposedBridge.log("[BetterVia] 已启用Google个人信息收集拦截");
			try
			{
				setFirebaseAnalyticsHook(ctx, cl, true);
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] Firebase Analytics拦截启用失败: " + e);
			}
			try
			{
				setAppMeasurementHook(ctx, cl, true);
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] AppMeasurement拦截启用失败: " + e);
			}
			XposedBridge.log("[BetterVia] Google个人信息收集拦截完成");
		}
		else
		{
			XposedBridge.log("[BetterVia] 已停用Google个人信息收集拦截");
			try
			{
				setFirebaseAnalyticsHook(ctx, cl, false);
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] Firebase Analytics拦截停用失败: " + e);
			}
			try
			{
				setAppMeasurementHook(ctx, cl, false);
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] AppMeasurement拦截停用失败: " + e);
			}
			XposedBridge.log("[BetterVia] Google个人信息收集拦截停用完成");
		}
		blockGoogleServicesEnabled = on;
		putPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, on);
	}
	private void setFirebaseAnalyticsHook(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (firebaseAnalyticsHook == null)
			{
				try
				{
					Class<?> firebaseAnalyticsClass = XposedHelpers
						.findClassIfExists("com.google.firebase.analytics.FirebaseAnalytics", cl);
					if (firebaseAnalyticsClass != null)
					{
						firebaseAnalyticsHook = XposedHelpers.findAndHookMethod(firebaseAnalyticsClass, "a",
							String.class, Bundle.class, new XC_MethodHook() {
								@Override
								protected void beforeHookedMethod(MethodHookParam param) throws Throwable
								{
									XposedBridge.log("[BetterVia] 拦截Firebase Analytics事件: " + param.args[0]);
								}
							});
						XposedBridge.log("[BetterVia] Firebase Analytics精确拦截已启用");
					}
				}
				catch (Exception e)
				{
					XposedBridge.log("[BetterVia] Firebase Analytics精确拦截设置失败: " + e);
				}
			}
		}
		else
		{
			if (firebaseAnalyticsHook != null)
			{
				firebaseAnalyticsHook.unhook();
				firebaseAnalyticsHook = null;
				XposedBridge.log("[BetterVia] Firebase Analytics拦截已停用");
			}
		}
	}
	private void setAppMeasurementHook(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (googleAnalyticsHook == null)
			{
				try
				{
					Class<?> appMeasurementClass = XposedHelpers
						.findClassIfExists("com.google.android.gms.measurement.AppMeasurement", cl);
					if (appMeasurementClass != null)
					{
						googleAnalyticsHook = XposedHelpers.findAndHookMethod(appMeasurementClass, "logEventInternal",
							String.class, String.class, Bundle.class, new XC_MethodHook() {
								@Override
								protected void beforeHookedMethod(MethodHookParam param) throws Throwable
								{
									XposedBridge.log("[BetterVia] 拦截AppMeasurement事件: " + param.args[0] + ", "
													 + param.args[1]);
								}
							});
						XposedBridge.log("[BetterVia] AppMeasurement精确拦截已启用");
					}
				}
				catch (Exception e)
				{
					XposedBridge.log("[BetterVia] AppMeasurement精确拦截设置失败: " + e);
				}
			}
		}
		else
		{
			if (googleAnalyticsHook != null)
			{
				googleAnalyticsHook.unhook();
				googleAnalyticsHook = null;
				XposedBridge.log("[BetterVia] AppMeasurement拦截已停用");
			}
		}
	}
	private void addSearchCommandsItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "search_commands_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "search_commands_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showSearchCommandsDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "search_commands_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showSearchCommandsDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(0, 0, 0, 0); 
					scrollRoot.setClipToPadding(false); 
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "search_commands_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(0xFF333333);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "search_commands_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					for (int i = 0; i < commands.length; i++)
					{
						final String[] command = commands[i];
						LinearLayout commandContainer = new LinearLayout(act);
						commandContainer.setOrientation(LinearLayout.HORIZONTAL);
						commandContainer.setGravity(Gravity.CENTER_VERTICAL);
						commandContainer.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
						GradientDrawable commandBg = new GradientDrawable();
						commandBg.setColor(0xFFF8F9FA);
						commandBg.setStroke(dp(act, 1), 0xFFE9ECEF);
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
						commandText.setTextColor(0xFF2C3E50);
						commandText.setTypeface(Typeface.MONOSPACE);
						commandText.setSingleLine(true);
						commandText.setEllipsize(TextUtils.TruncateAt.MIDDLE);
						commandText.setPadding(0, 0, dp(act, 8), 0);
						leftContent.addView(commandText);
						TextView descText = new TextView(act);
						descText.setText(getLocalizedString(ctx, command[1]));
						descText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
						descText.setTextColor(0xFF7F8C8D);
						descText.setPadding(0, dp(act, 4), 0, 0);
						leftContent.addView(descText);
						commandContainer.addView(leftContent);
						Button copyBtn = new Button(act);
						copyBtn.setText(getLocalizedString(ctx, "command_copy"));
						copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
						copyBtn.setTextColor(Color.WHITE);
						copyBtn.setPadding(dp(act, 10), dp(act, 4), dp(act, 10), dp(act, 4));
						copyBtn.setMinHeight(dp(act, 28));
						copyBtn.setMinWidth(dp(act, 52));
						GradientDrawable btnBg = new GradientDrawable();
						btnBg.setColor(0xFF3498DB);
						btnBg.setCornerRadius(dp(act, 6));
						copyBtn.setBackground(btnBg);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
						{
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
								public void onClick(View v)
								{
									copyToClipboard(act, commands[index][0]);
									Toast.makeText(act, getLocalizedString(ctx, "command_copied"), Toast.LENGTH_SHORT).show();
								}
							});
					}
					root.addView(commandsContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				  ViewGroup.LayoutParams.WRAP_CONTENT));
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					dialogContainer.addView(scrollRoot);
					dialog.setContentView(dialogContainer);
					Window window = dialog.getWindow();
					if (window != null)
					{
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
						window.setClipToOutline(true);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					dialog.show();
				}
			});
	}
	private void addHomepageThemeItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "homepage_theme_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "homepage_theme_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showHomepageThemeDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "homepage_theme_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showHomepageThemeDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(0, 0, 0, 0);
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "homepage_theme_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(0xFF333333);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "homepage_theme_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					errorIcon.setColorFilter(0xFF888888);
					errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
					iconLp.gravity = Gravity.CENTER;
					iconLp.bottomMargin = dp(act, 16);
					emptyStateContainer.addView(errorIcon, iconLp);
					final TextView emptyStateText = new TextView(act);
					emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					emptyStateText.setTextColor(0xFF888888);
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
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																				   1.0f);
					okLp.rightMargin = dp(act, 8);
					buttonContainer.addView(ok, okLp);
					Button edit = new Button(act);
					edit.setText(getLocalizedString(ctx, "homepage_theme_edit"));
					edit.setTextColor(Color.WHITE);
					edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					edit.setTypeface(null, Typeface.BOLD);
					edit.setPadding(0, dp(act, 14), 0, dp(act, 14));
					edit.setBackground(getRoundBg(act, 0xFF6200EE, 12));
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
					if (window != null)
					{
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
						window.setClipToOutline(true);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					edit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
								showThemeEditorDialog(ctx);
							}
						});
					showLoadingState(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
					if (!themesLoaded && !themesLoading)
					{
						loadThemesFromNetwork(ctx, new ThemesLoadCallback() {
								@Override
								public void onThemesLoaded(List<ThemeInfo> themes)
								{
									loadedThemes = themes;
									themesLoaded = true;
									themesLoading = false;
									if (act != null && !act.isFinishing())
									{
										act.runOnUiThread(new Runnable() {
												@Override
												public void run()
												{
													refreshThemesList(act, ctx, themesContainer, emptyStateContainer,
																	  emptyStateText);
												}
											});
									}
								}
								@Override
								public void onLoadFailed(final String error)
								{
									themesLoading = false;
									themesLoaded = true; 
									loadedThemes = new ArrayList<>(); 
									if (act != null && !act.isFinishing())
									{
										act.runOnUiThread(new Runnable() {
												@Override
												public void run()
												{
													showErrorState(act, ctx, themesContainer, emptyStateContainer, emptyStateText,
																   error);
												}
											});
									}
								}
							});
					}
					else
					{
						refreshThemesList(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
					}
					dialog.show();
				}
			});
	}
	private void showLoadingState(Activity act, Context ctx, LinearLayout themesContainer,
								  LinearLayout emptyStateContainer, TextView emptyStateText)
	{
		themesContainer.removeAllViews();
		themesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(getLocalizedString(ctx, "themes_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showErrorState(Activity act, Context ctx, LinearLayout themesContainer,
								LinearLayout emptyStateContainer, TextView emptyStateText, String error)
	{
		themesContainer.removeAllViews();
		themesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(
			getLocalizedString(ctx, "themes_load_failed") + "\n" + getLocalizedString(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.VISIBLE);
			}
			else if (child instanceof ProgressBar)
			{
				emptyStateContainer.removeView(child); 
			}
		}
		Toast.makeText(ctx, getLocalizedString(ctx, "themes_load_failed") + ": " + error, Toast.LENGTH_SHORT).show();
	}
	private LinearLayout createThemeCard(final Activity act, final Context ctx, final ThemeInfo theme)
	{
		LinearLayout themeCard = new LinearLayout(act);
		themeCard.setOrientation(LinearLayout.VERTICAL);
		themeCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(0xFFF8F9FA);
		cardBg.setStroke(dp(act, 1), 0xFFE9ECEF);
		cardBg.setCornerRadius(dp(act, 12));
		themeCard.setBackground(cardBg);
		FrameLayout imageContainer = new FrameLayout(act);
		imageContainer
			.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 150)));
		imageContainer.setBackground(getRoundBg(act, 0xFFE0E0E0, 8));
		final ProgressBar loadingSpinner = new ProgressBar(act);
		loadingSpinner.setIndeterminate(true);
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
				public void run()
				{
					try
					{
						URL url = new URL(theme.previewUrl);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setDoInput(true);
						connection.setConnectTimeout(10000);
						connection.setReadTimeout(10000);
						connection.setRequestProperty("User-Agent",
													  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							InputStream input = connection.getInputStream();
							final Bitmap bitmap = BitmapFactory.decodeStream(input);
							input.close();
							if (bitmap != null)
							{
								act.runOnUiThread(new Runnable() {
										@Override
										public void run()
										{
											previewImage.setImageBitmap(bitmap);
											previewImage.setVisibility(View.VISIBLE);
											loadingSpinner.setVisibility(View.GONE);
										}
									});
							}
						}
						connection.disconnect();
					}
					catch (Exception e)
					{
						act.runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									int iconSize = dp(act, 48); 
									Bitmap errorBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
									Canvas canvas = new Canvas(errorBitmap);
									Paint backgroundPaint = new Paint();
									backgroundPaint.setColor(0xFFE0E0E0);
									canvas.drawCircle(iconSize / 2, iconSize / 2, iconSize / 2, backgroundPaint);
									Drawable errorIcon = act.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
									errorIcon.setBounds(iconSize / 4, iconSize / 4, iconSize * 3 / 4, iconSize * 3 / 4);
									errorIcon.draw(canvas);
									previewImage.setImageBitmap(errorBitmap);
									previewImage.setScaleType(ImageView.ScaleType.CENTER); 
									previewImage.setColorFilter(0xFF888888);
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
		themeName.setTextColor(Color.BLACK);
		themeName.setTypeface(null, Typeface.BOLD);
		infoLayout.addView(themeName);
		TextView themeAuthor = new TextView(act);
		themeAuthor.setText(getLocalizedString(ctx, "homepage_theme_by") + " " + theme.getAuthor(ctx));
		themeAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		themeAuthor.setTextColor(0xFF666666);
		infoLayout.addView(themeAuthor);
		themeCard.addView(infoLayout);
		themeCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showThemeApplyDialog(ctx, theme);
				}
			});
		return themeCard;
	}
	private interface ThemesLoadCallback
	{
		void onThemesLoaded(List<ThemeInfo> themes);
		void onLoadFailed(String error);
	}
	private void loadThemesFromNetwork(final Context ctx, final ThemesLoadCallback callback)
	{
		themesLoading = true;
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
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
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							InputStream inputStream = connection.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
							StringBuilder response = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null)
							{
								response.append(line);
							}
							reader.close();
							JSONObject json = new JSONObject(response.toString());
							JSONArray themesArray = json.getJSONArray("themes");
							List<ThemeInfo> themes = new ArrayList<>();
							for (int i = 0; i < themesArray.length(); i++)
							{
								JSONObject themeJson = themesArray.getJSONObject(i);
								ThemeInfo theme = ThemeInfo.fromJSON(themeJson);
								themes.add(theme);
							}
							callback.onThemesLoaded(themes);
						}
						else
						{
							callback.onLoadFailed("HTTP " + connection.getResponseCode());
						}
						connection.disconnect();
					}
					catch (Exception e)
					{
						callback.onLoadFailed(e.getMessage());
					}
				}
			}).start();
	}
	private void refreshThemesList(final Activity act, final Context ctx, LinearLayout themesContainer,
								   LinearLayout emptyStateContainer, TextView emptyStateText)
	{
		themesContainer.removeAllViews();
		if (themesLoading)
		{
			showLoadingState(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
			return;
		}
		if (loadedThemes == null || loadedThemes.isEmpty())
		{
			showErrorState(act, ctx, themesContainer, emptyStateContainer, emptyStateText, "No themes available");
			return;
		}
		themesContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		for (final ThemeInfo theme : loadedThemes)
		{
			LinearLayout themeCard = createThemeCard(act, ctx, theme);
			LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																			 ViewGroup.LayoutParams.WRAP_CONTENT);
			cardLp.bottomMargin = dp(ctx, 12);
			themesContainer.addView(themeCard, cardLp);
		}
	}
	private void showThemeEditorDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
					dialog.setCancelable(true);
					LinearLayout rootLayout = new LinearLayout(act);
					rootLayout.setOrientation(LinearLayout.VERTICAL);
					rootLayout.setBackgroundColor(Color.WHITE);
					RelativeLayout titleBar = new RelativeLayout(act);
					titleBar.setBackgroundColor(0xFFF5F5F5);
					titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																		   ViewGroup.LayoutParams.WRAP_CONTENT));
					ImageButton backButton = new ImageButton(act);
					backButton.setImageResource(android.R.drawable.ic_menu_revert);
					backButton.setBackgroundResource(android.R.color.transparent);
					backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
					backButton.setColorFilter(0xFF000000); 
					RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
					backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
					titleBar.addView(backButton, backButtonLp);
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "homepage_theme_editor_title"));
					title.setTextColor(Color.BLACK);
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
					fileLabel.setText(getLocalizedString(ctx, "theme_editor_select_file"));
					fileLabel.setTextColor(0xFF333333);
					fileLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					fileLabel.setPadding(0, 0, 0, dp(act, 8));
					contentLayout.addView(fileLabel);
					LinearLayout fileButtonGroup = new LinearLayout(act);
					fileButtonGroup.setOrientation(LinearLayout.HORIZONTAL);
					fileButtonGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				  ViewGroup.LayoutParams.WRAP_CONTENT));
					final Button htmlButton = new Button(act);
					htmlButton.setText("homepage2.html"); 
					htmlButton.setTextColor(Color.WHITE);
					htmlButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					htmlButton.setBackground(getRoundBg(act, 0xFF6200EE, 6));
					htmlButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					LinearLayout.LayoutParams htmlLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																					 1.0f);
					htmlLp.rightMargin = dp(act, 8);
					fileButtonGroup.addView(htmlButton, htmlLp);
					final Button cssButton = new Button(act);
					cssButton.setText("homepage.css"); 
					cssButton.setTextColor(0xFF666666);
					cssButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					cssButton.setBackground(getRoundBg(act, 0xFFE0E0E0, 6));
					cssButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					LinearLayout.LayoutParams cssLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																					1.0f);
					fileButtonGroup.addView(cssButton, cssLp);
					contentLayout.addView(fileButtonGroup);
					TextView editorLabel = new TextView(act);
					editorLabel.setText(getLocalizedString(ctx, "theme_editor_edit_content"));
					editorLabel.setTextColor(0xFF333333);
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
					codeEditor.setTextColor(Color.BLACK);
					codeEditor.setBackground(getRoundBg(act, 0xFFF5F5F5, 8));
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
					cancelButton.setText(getLocalizedString(ctx, "dialog_cancel")); 
					cancelButton.setTextColor(0xFF666666);
					cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					cancelButton.setBackground(getRoundBg(act, 0xFFE0E0E0, 8));
					cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
					LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0,
																					   ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					cancelLp.rightMargin = dp(act, 8);
					buttonBar.addView(cancelButton, cancelLp);
					Button saveButton = new Button(act);
					saveButton.setText(getLocalizedString(ctx, "dialog_ok")); 
					saveButton.setTextColor(Color.WHITE);
					saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					saveButton.setBackground(getRoundBg(act, 0xFF6200EE, 8));
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
							public void onClick(View v)
							{
								saveCurrentEditorState(currentFile[0], codeEditor, editorScroll);
								currentFile[0] = "homepage2.html";
								htmlButton.setTextColor(Color.WHITE);
								htmlButton.setBackground(getRoundBg(act, 0xFF6200EE, 6));
								cssButton.setTextColor(0xFF666666);
								cssButton.setBackground(getRoundBg(act, 0xFFE0E0E0, 6));
								loadFileContent(act, "homepage2.html", codeEditor, editorScroll, true);
							}
						});
					cssButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								saveCurrentEditorState(currentFile[0], codeEditor, editorScroll);
								currentFile[0] = "homepage.css";
								cssButton.setTextColor(Color.WHITE);
								cssButton.setBackground(getRoundBg(act, 0xFF6200EE, 6));
								htmlButton.setTextColor(0xFF666666);
								htmlButton.setBackground(getRoundBg(act, 0xFFE0E0E0, 6));
								loadFileContent(act, "homepage.css", codeEditor, editorScroll, true);
							}
						});
					editorScroll.getViewTreeObserver()
						.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
							@Override
							public void onScrollChanged()
							{
								if (currentFile[0] != null && editorStateCache.containsKey(currentFile[0]))
								{
									EditorState oldState = editorStateCache.get(currentFile[0]);
									editorStateCache.put(currentFile[0],
														 new EditorState(oldState.content, editorScroll.getScrollY()));
								}
							}
						});
					dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog)
							{
								long currentTime = System.currentTimeMillis();
								Iterator<Map.Entry<String, EditorState>> it = editorStateCache.entrySet().iterator();
								while (it.hasNext())
								{
									Map.Entry<String, EditorState> entry = it.next();
									if (currentTime - entry.getValue().timestamp > 10 * 60 * 1000)
									{ 
										it.remove();
									}
								}
							}
						});
					backButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					cancelButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					saveButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								saveFileContent(act, currentFile[0], codeEditor.getText().toString());
								if (editorScroll != null)
								{
									int scrollY = editorScroll.getScrollY();
									editorStateCache.put(currentFile[0],
														 new EditorState(codeEditor.getText().toString(), scrollY));
								}
								Toast.makeText(act, getLocalizedString(ctx, "theme_editor_save_success"), Toast.LENGTH_SHORT)
									.show();
								dialog.dismiss();
							}
						});
					dialog.show();
				}
			});
	}
	private void showThemeApplyDialog(final Context ctx, final ThemeInfo theme)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle(getLocalizedString(ctx, "homepage_theme_apply_title"));
					builder.setMessage(
						getLocalizedString(ctx, "homepage_theme_apply_message") + " \"" + theme.getName(ctx) + "\"?");
					builder.setPositiveButton(getLocalizedString(ctx, "homepage_theme_apply"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								applyHomepageTheme(ctx, theme);
							}
						});
					builder.setNegativeButton(getLocalizedString(ctx, "dialog_cancel"), null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			});
	}
	private void applyHomepageTheme(final Context ctx, final ThemeInfo theme)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						String packageName = ctx.getPackageName();
						String filesDir = "/data/user/0/" + packageName + "/files/";
						boolean needReplacePackageName = false; 
						String htmlUrl = theme.htmlUrls.get(packageName);
						String cssUrl = theme.cssUrls.get(packageName);
						if (htmlUrl == null || cssUrl == null)
						{
							if (theme.htmlUrls.containsKey("mark.via") && theme.cssUrls.containsKey("mark.via"))
							{
								htmlUrl = theme.htmlUrls.get("mark.via");
								cssUrl = theme.cssUrls.get("mark.via");
								needReplacePackageName = true; 
							}
							else
							{
								htmlUrl = theme.htmlUrls.values().iterator().next();
								cssUrl = theme.cssUrls.values().iterator().next();
							}
						}
						else
						{
							if (!"mark.via".equals(packageName) && !"mark.via.gp".equals(packageName))
							{
								needReplacePackageName = true;
							}
						}
						final boolean htmlSuccess = downloadAndSaveFile(htmlUrl, filesDir + "homepage2.html");
						final boolean cssSuccess = downloadAndSaveFile(cssUrl, filesDir + "homepage.css");
						final boolean replaceSuccess = (htmlSuccess && cssSuccess && needReplacePackageName)
							? replacePackageNameInFiles(filesDir + "homepage2.html", filesDir + "homepage.css", "mark.via",
														packageName)
							: true;
						((Activity) Context).runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									if (htmlSuccess && cssSuccess && replaceSuccess)
									{
										putPrefString(ctx, KEY_CURRENT_THEME, theme.id);
										Toast.makeText(ctx, getLocalizedString(ctx, "homepage_theme_apply_success"),
													   Toast.LENGTH_LONG).show();
										restartVia(ctx);
									}
									else
									{
										Toast.makeText(ctx, getLocalizedString(ctx, "homepage_theme_apply_failed"),
													   Toast.LENGTH_LONG).show();
									}
								}
							});
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 应用主题失败: " + e);
						((Activity) Context).runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									Toast.makeText(ctx, getLocalizedString(ctx, "homepage_theme_apply_error"),
												   Toast.LENGTH_LONG).show();
								}
							});
					}
				}
			}).start();
	}
	private boolean downloadAndSaveFile(String urlString, String filePath)
	{
		HttpURLConnection connection = null;
		FileOutputStream outputStream = null;
		try
		{
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				InputStream inputStream = connection.getInputStream();
				File file = new File(filePath);
				outputStream = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				return true;
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 下载文件失败: " + e);
		}
		finally
		{
			try
			{
				if (outputStream != null)
					outputStream.close();
				if (connection != null)
					connection.disconnect();
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] 关闭流失败: " + e);
			}
		}
		return false;
	}
	private boolean replacePackageNameInFiles(String htmlFilePath, String cssFilePath, String oldPackageName,
											  String newPackageName)
	{
		boolean htmlSuccess = replacePackageNameInFile(htmlFilePath, oldPackageName, newPackageName);
		boolean cssSuccess = replacePackageNameInFile(cssFilePath, oldPackageName, newPackageName);
		if (htmlSuccess && cssSuccess)
		{
			XposedBridge.log("[BetterVia] 已将文件中的包名从 " + oldPackageName + " 替换为 " + newPackageName);
			return true;
		}
		else
		{
			XposedBridge.log("[BetterVia] 替换文件中的包名失败");
			return false;
		}
	}
	private boolean replacePackageNameInFile(String filePath, String oldPackageName, String newPackageName)
	{
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				XposedBridge.log("[BetterVia] 文件不存在: " + filePath);
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
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 替换文件内容失败: " + e);
			return false;
		}
		finally
		{
			try
			{
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
			}
			catch (Exception e)
			{
				XposedBridge.log("[BetterVia] 关闭流失败: " + e);
			}
		}
	}
	private void restartVia(Context ctx)
	{
		try
		{
			Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
			if (intent != null)
			{
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				android.os.Process.killProcess(android.os.Process.myPid());
				ctx.startActivity(intent);
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 重启Via失败: " + e);
		}
	}
	private void setScreenshotProtection(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (screenshotProtectionHook == null)
			{
				screenshotProtectionHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable
						{
							Activity activity = (Activity) param.thisObject;
							if (screenshotProtectionEnabled)
							{
								activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
															  WindowManager.LayoutParams.FLAG_SECURE);
								XposedBridge
									.log("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 启用截屏防护");
							}
						}
					});
				XposedBridge.log("[BetterVia] 截屏防护已启用");
			}
		}
		else
		{
			if (screenshotProtectionHook != null)
			{
				screenshotProtectionHook.unhook();
				screenshotProtectionHook = null;
				XposedBridge.log("[BetterVia] 截屏防护已停用");
				removeScreenshotProtection();
			}
		}
		screenshotProtectionEnabled = on;
		putPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, on);
	}
	private void removeScreenshotProtection()
	{
		for (final Activity activity : overlayViews.keySet())
		{
			if (!activity.isFinishing() && !activity.isDestroyed())
			{
				activity.runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
						}
					});
			}
		}
		XposedBridge.log("[BetterVia] 已移除所有Activity的截屏防护");
	}
	private void setKeepScreenOn(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (keepScreenOnHook == null)
			{
				keepScreenOnHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable
						{
							final Activity activity = (Activity) param.thisObject;
							if (keepScreenOnEnabled)
							{
								activity.runOnUiThread(new Runnable() {
										@Override
										public void run()
										{
											activity.getWindow()
												.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
											screenOnActivities.put(activity, true);
											XposedBridge.log("[BetterVia] 已为 " + activity.getClass().getSimpleName()
															 + " 启用屏幕常亮");
										}
									});
							}
						}
					});
				XposedBridge.log("[BetterVia] 屏幕常亮已启用");
			}
		}
		else
		{
			if (keepScreenOnHook != null)
			{
				keepScreenOnHook.unhook();
				keepScreenOnHook = null;
				XposedBridge.log("[BetterVia] 屏幕常亮已停用");
				removeKeepScreenOn();
			}
		}
		keepScreenOnEnabled = on;
		putPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, on);
	}
	private void setBackgroundVideoAudio(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (backgroundVideoHook == null)
			{
				try
				{
					Class<?> shellClass = findClassWithFallback("Shell", ctx, cl);
					if (shellClass == null)
					{
						XposedBridge.log("[BetterVia] 未找到Shell类，无法启用后台听视频功能");
						return;
					}
					backgroundVideoHook = XposedHelpers.findAndHookMethod(shellClass, "onPause",
						new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
							{
								XposedBridge.log("[BetterVia] 阻止了Shell.onPause调用，保持前台状态");
								return null; 
							}
						});
					XposedHelpers.findAndHookMethod(shellClass, "onWindowFocusChanged", boolean.class,
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable
							{
								param.args[0] = true;
								XposedBridge.log("[BetterVia] 强制设置窗口焦点为true");
							}
						});
					XposedHelpers.findAndHookMethod(shellClass, "isFinishing", new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
							{
								XposedBridge.log("[BetterVia] 强制返回isFinishing=false");
								return false;
							}
						});
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
					{
						XposedHelpers.findAndHookMethod(shellClass, "isDestroyed",
							new XC_MethodReplacement() {
								@Override
								protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
								{
									XposedBridge.log("[BetterVia] 强制返回isDestroyed=false");
									return false;
								}
							});
					}
					XposedBridge.log("[BetterVia] 后台听视频功能已启用");
				}
				catch (Throwable e)
				{
					XposedBridge.log("[BetterVia] 启用后台听视频功能失败: " + e.getMessage());
				}
			}
		}
		else
		{
			if (backgroundVideoHook != null)
			{
				try
				{
					backgroundVideoHook.unhook();
					backgroundVideoHook = null;
					XposedBridge.log("[BetterVia] 后台听视频功能已停用");
				}
				catch (Throwable e)
				{
					XposedBridge.log("[BetterVia] 停用后台听视频功能出错: " + e.getMessage());
				}
			}
		}
		backgroundVideoEnabled = on;
		putPrefBoolean(ctx, KEY_BACKGROUND_VIDEO, on);
	}
	private void injectVideoKeepAliveScript(final WebView webView)
	{
		if (!backgroundVideoEnabled)
			return;
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						String jsCode = "javascript:(function() {" + "    
							+ "    var originalHidden = document.hidden;"
							+ "    var originalVisibilityState = document.visibilityState;" + "    "
							+ "    
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
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
						{
							webView.evaluateJavascript(jsCode, null);
						}
						else
						{
							webView.loadUrl(jsCode);
						}
						XposedBridge.log("[BetterVia] 已注入视频保持播放脚本");
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 注入视频脚本失败: " + e.getMessage());
					}
				}
			}, 2000); 
	}
	private void removeKeepScreenOn()
	{
		for (final Activity activity : screenOnActivities.keySet())
		{
			if (!activity.isFinishing() && !activity.isDestroyed())
			{
				activity.runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						}
					});
			}
		}
		screenOnActivities.clear();
		XposedBridge.log("[BetterVia] 已移除所有Activity的屏幕常亮设置");
	}
	private void addScriptRepositoryItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "script_repository_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "script_repository_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showScriptRepositoryDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "script_repository_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private static class ScriptInfo
	{
		String id;
		Map<String, String> nameMap; 
		Map<String, String> descriptionMap; 
		Map<String, String> detailMap; 
		Map<String, String> downloadUrls; 
		String category;
		ScriptInfo(String id, Map<String, String> nameMap, Map<String, String> descriptionMap,
				   Map<String, String> detailMap, Map<String, String> downloadUrls, String category)
		{
			this.id = id;
			this.nameMap = nameMap;
			this.descriptionMap = descriptionMap;
			this.detailMap = detailMap;
			this.downloadUrls = downloadUrls;
			this.category = category;
		}
		String getName(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
		}
		String getDescription(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return descriptionMap.getOrDefault(langCode, descriptionMap.get("zh-CN"));
		}
		String getDetail(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return detailMap.getOrDefault(langCode, detailMap.get("zh-CN"));
		}
		private String getLanguageCode(Context ctx)
		{
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved))
			{
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				}
				else
				{
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale))
				{
					return "zh-CN";
				}
				else if (Locale.TRADITIONAL_CHINESE.equals(locale))
				{
					return "zh-TW";
				}
				else if (Locale.ENGLISH.equals(locale))
				{
					return "en";
				}
				return "zh-CN";
			}
			return saved;
		}
		static ScriptInfo fromJSON(JSONObject json) throws JSONException
		{
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext())
			{
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> descriptionMap = new HashMap<>();
			JSONObject descriptions = json.getJSONObject("descriptions");
			Iterator<String> descKeys = descriptions.keys();
			while (descKeys.hasNext())
			{
				String lang = descKeys.next();
				descriptionMap.put(lang, descriptions.getString(lang));
			}
			Map<String, String> detailMap = new HashMap<>();
			JSONObject details = json.getJSONObject("details");
			Iterator<String> detailKeys = details.keys();
			while (detailKeys.hasNext())
			{
				String lang = detailKeys.next();
				detailMap.put(lang, details.getString(lang));
			}
			Map<String, String> downloadUrls = new HashMap<>();
			JSONObject downloads = json.getJSONObject("downloadUrls");
			Iterator<String> downloadKeys = downloads.keys();
			while (downloadKeys.hasNext())
			{
				String channel = downloadKeys.next();
				downloadUrls.put(channel, downloads.getString(channel));
			}
			String category = json.getString("category");
			return new ScriptInfo(id, nameMap, descriptionMap, detailMap, downloadUrls, category);
		}
	}
	private void showScriptRepositoryDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(0, 0, 0, 0);
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "script_repository_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(0xFF333333);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "script_repository_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					searchEdit.setHint(getLocalizedString(ctx, "script_search_hint"));
					searchEdit.setTextColor(Color.BLACK);
					searchEdit.setHintTextColor(0xFF888888);
					searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					searchEdit.setBackground(getRoundBg(act, 0xFFF5F5F5, 8));
					searchEdit.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
					LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(0,
																					   ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					searchLp.rightMargin = dp(act, 8);
					searchRow.addView(searchEdit, searchLp);
					Button searchButton = new Button(act);
					searchButton.setText(getLocalizedString(ctx, "script_search_button"));
					searchButton.setTextColor(Color.WHITE);
					searchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					searchButton.setBackground(getRoundBg(act, 0xFF6200EE, 8));
					searchButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
					searchRow.addView(searchButton);
					searchContainer.addView(searchRow);
					final TextView scriptCountText = new TextView(act);
					scriptCountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					scriptCountText.setTextColor(0xFF888888);
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
					errorIcon.setColorFilter(0xFF888888);
					errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
					iconLp.gravity = Gravity.CENTER;
					iconLp.bottomMargin = dp(act, 16);
					emptyStateContainer.addView(errorIcon, iconLp);
					final TextView emptyStateText = new TextView(act);
					emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					emptyStateText.setTextColor(0xFF888888);
					emptyStateText.setGravity(Gravity.CENTER);
					emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
					emptyStateText.setText(getLocalizedString(ctx, "scripts_loading"));
					emptyStateContainer.addView(emptyStateText);
					root.addView(scriptsContainer);
					root.addView(emptyStateContainer);
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					dialogContainer.addView(scrollRoot);
					dialog.setContentView(dialogContainer);
					Window window = dialog.getWindow();
					if (window != null)
					{
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
						window.setClipToOutline(true);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					final List<ScriptInfo>[] allScripts = new List[]{new ArrayList<ScriptInfo>()};
					final Runnable updateScriptCount = new Runnable() {
						@Override
						public void run()
						{
							if (allScripts[0] != null && !allScripts[0].isEmpty())
							{
								String countText = String.format(getLocalizedString(ctx, "script_total_count"),
																 allScripts[0].size());
								scriptCountText.setText(countText);
							}
							else
							{
								scriptCountText.setText(getLocalizedString(ctx, "script_loading_count"));
							}
						}
					};
					searchButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								String query = searchEdit.getText().toString().trim().toLowerCase();
								filterScripts(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts[0],
											  query, true);
							}
						});
					searchEdit.addTextChangedListener(new TextWatcher() {
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after)
							{
							}
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count)
							{
								String query = s.toString().trim().toLowerCase();
								filterScripts(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts[0],
											  query, false);
								if (allScripts[0] != null && !allScripts[0].isEmpty())
								{
									List<ScriptInfo> filteredScripts = new ArrayList<>();
									for (ScriptInfo script : allScripts[0])
									{
										String name = script.getName(ctx).toLowerCase();
										String description = script.getDescription(ctx).toLowerCase();
										String category = script.category.toLowerCase();
										if (name.contains(query) || description.contains(query) || category.contains(query))
										{
											filteredScripts.add(script);
										}
									}
									String countText;
									if (query.isEmpty())
									{
										countText = String.format(getLocalizedString(ctx, "script_total_count"),
																  allScripts[0].size());
									}
									else
									{
										countText = String.format(getLocalizedString(ctx, "script_filtered_count"),
																  filteredScripts.size(), allScripts[0].size());
									}
									scriptCountText.setText(countText);
								}
							}
							@Override
							public void afterTextChanged(Editable s)
							{
							}
						});
					showScriptsLoadingState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText);
					loadScriptsFromNetwork(ctx, new ScriptsLoadCallback() {
							@Override
							public void onScriptsLoaded(final List<ScriptInfo> scripts)
							{
								allScripts[0] = scripts; 
								if (act != null && !act.isFinishing())
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
																   scripts);
												updateScriptCount.run();
											}
										});
								}
							}
							@Override
							public void onLoadFailed(final String error)
							{
								if (act != null && !act.isFinishing())
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer,
																	  emptyStateText, error);
												scriptCountText.setText(getLocalizedString(ctx, "script_load_failed_count"));
											}
										});
								}
							}
						});
					dialog.show();
				}
			});
	}
	private void loadScriptsFromNetwork(final Context ctx, final ScriptsLoadCallback callback)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
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
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							InputStream inputStream = connection.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
							StringBuilder response = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null)
							{
								response.append(line);
							}
							reader.close();
							JSONObject json = new JSONObject(response.toString());
							JSONArray scriptsArray = json.getJSONArray("scripts");
							List<ScriptInfo> scripts = new ArrayList<>();
							for (int i = 0; i < scriptsArray.length(); i++)
							{
								JSONObject scriptJson = scriptsArray.getJSONObject(i);
								ScriptInfo script = ScriptInfo.fromJSON(scriptJson);
								scripts.add(script);
							}
							callback.onScriptsLoaded(scripts);
						}
						else
						{
							callback.onLoadFailed("HTTP " + connection.getResponseCode());
						}
						connection.disconnect();
					}
					catch (Exception e)
					{
						callback.onLoadFailed(e.getMessage());
					}
				}
			}).start();
	}
	private void filterScripts(Activity act, Context ctx, LinearLayout scriptsContainer,
							   LinearLayout emptyStateContainer, TextView emptyStateText, List<ScriptInfo> allScripts, String query,
							   boolean showToast)
	{
		if (allScripts == null || allScripts.isEmpty())
		{
			showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
								  getLocalizedString(ctx, "no_scripts_available"));
			return;
		}
		if (query.isEmpty())
		{
			refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts);
			if (showToast)
			{
				Toast.makeText(act, String.format(getLocalizedString(ctx, "script_show_all"), allScripts.size()),
							   Toast.LENGTH_SHORT).show();
			}
			return;
		}
		List<ScriptInfo> filteredScripts = new ArrayList<>();
		for (ScriptInfo script : allScripts)
		{
			String name = script.getName(ctx).toLowerCase();
			String description = script.getDescription(ctx).toLowerCase();
			String category = script.category.toLowerCase();
			if (name.contains(query) || description.contains(query) || category.contains(query))
			{
				filteredScripts.add(script);
			}
		}
		if (filteredScripts.isEmpty())
		{
			scriptsContainer.removeAllViews();
			scriptsContainer.setVisibility(View.GONE);
			emptyStateContainer.setVisibility(View.VISIBLE);
			emptyStateText.setText(String.format(getLocalizedString(ctx, "script_search_no_results"), query));
			for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
			{
				View child = emptyStateContainer.getChildAt(i);
				if (child instanceof ImageView)
				{
					child.setVisibility(View.VISIBLE);
					((ImageView) child).setImageResource(android.R.drawable.ic_search_category_default);
				}
				else if (child instanceof ProgressBar)
				{
					emptyStateContainer.removeView(child);
				}
			}
			if (showToast)
			{
				Toast.makeText(act, getLocalizedString(ctx, "script_search_no_results_toast"), Toast.LENGTH_SHORT)
					.show();
			}
		}
		else
		{
			refreshScriptsList(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, filteredScripts);
			if (showToast)
			{
				Toast.makeText(act,
							   String.format(getLocalizedString(ctx, "script_search_results"), filteredScripts.size()),
							   Toast.LENGTH_SHORT).show();
			}
		}
	}
	private void showScriptsLoadingState(Activity act, Context ctx, LinearLayout scriptsContainer,
										 LinearLayout emptyStateContainer, TextView emptyStateText)
	{
		scriptsContainer.removeAllViews();
		scriptsContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(getLocalizedString(ctx, "scripts_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showScriptsErrorState(Activity act, Context ctx, LinearLayout scriptsContainer,
									   LinearLayout emptyStateContainer, TextView emptyStateText, String error)
	{
		scriptsContainer.removeAllViews();
		scriptsContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(
			getLocalizedString(ctx, "scripts_load_failed") + "\n" + getLocalizedString(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.VISIBLE);
			}
			else if (child instanceof ProgressBar)
			{
				emptyStateContainer.removeView(child); 
			}
		}
		Toast.makeText(ctx, getLocalizedString(ctx, "scripts_load_failed") + ": " + error, Toast.LENGTH_SHORT).show();
	}
	private void refreshScriptsList(final Activity act, final Context ctx, LinearLayout scriptsContainer,
									LinearLayout emptyStateContainer, TextView emptyStateText, List<ScriptInfo> scripts)
	{
		scriptsContainer.removeAllViews();
		if (scripts == null || scripts.isEmpty())
		{
			showScriptsErrorState(act, ctx, scriptsContainer, emptyStateContainer, emptyStateText,
								  "No scripts available");
			return;
		}
		scriptsContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		Map<String, List<ScriptInfo>> categorizedScripts = new HashMap<>();
		for (ScriptInfo script : scripts)
		{
			String category = script.category;
			if (!categorizedScripts.containsKey(category))
			{
				categorizedScripts.put(category, new ArrayList<ScriptInfo>());
			}
			categorizedScripts.get(category).add(script);
		}
		for (Map.Entry<String, List<ScriptInfo>> entry : categorizedScripts.entrySet())
		{
			String category = entry.getKey();
			List<ScriptInfo> categoryScripts = entry.getValue();
			TextView categoryTitle = new TextView(act);
			categoryTitle.setText(category);
			categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			categoryTitle.setTextColor(0xFF6200EE);
			categoryTitle.setTypeface(null, Typeface.BOLD);
			categoryTitle.setPadding(0, dp(act, 16), 0, dp(act, 8));
			scriptsContainer.addView(categoryTitle);
			for (final ScriptInfo script : categoryScripts)
			{
				LinearLayout scriptCard = createScriptCard(act, ctx, script);
				LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				 ViewGroup.LayoutParams.WRAP_CONTENT);
				cardLp.bottomMargin = dp(ctx, 12);
				scriptsContainer.addView(scriptCard, cardLp);
			}
		}
	}
	private LinearLayout createScriptCard(final Activity act, final Context ctx, final ScriptInfo script)
	{
		LinearLayout scriptCard = new LinearLayout(act);
		scriptCard.setOrientation(LinearLayout.VERTICAL);
		scriptCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(0xFFF8F9FA);
		cardBg.setStroke(dp(act, 1), 0xFFE9ECEF);
		cardBg.setCornerRadius(dp(act, 12));
		scriptCard.setBackground(cardBg);
		TextView scriptName = new TextView(act);
		scriptName.setText(script.getName(ctx));
		scriptName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		scriptName.setTextColor(Color.BLACK);
		scriptName.setTypeface(null, Typeface.BOLD);
		scriptCard.addView(scriptName);
		TextView scriptDescription = new TextView(act);
		scriptDescription.setText(script.getDescription(ctx));
		scriptDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		scriptDescription.setTextColor(0xFF666666);
		scriptDescription.setPadding(0, dp(act, 8), 0, 0);
		scriptCard.addView(scriptDescription);
		scriptCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showScriptDetailDialog(ctx, script);
				}
			});
		return scriptCard;
	}
	private void showScriptDetailDialog(final Context ctx, final ScriptInfo script)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					final AlertDialog[] detailDialogRef = new AlertDialog[1];
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle(script.getName(ctx));
					ScrollView scrollView = new ScrollView(act);
					LinearLayout layout = new LinearLayout(act);
					layout.setOrientation(LinearLayout.VERTICAL);
					layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					TextView detailText = new TextView(act);
					detailText.setText(script.getDetail(ctx));
					detailText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					detailText.setTextColor(Color.BLACK);
					detailText.setLineSpacing(dp(act, 4), 1.2f);
					layout.addView(detailText);
					LinearLayout buttonContainer = new LinearLayout(act);
					buttonContainer.setOrientation(LinearLayout.VERTICAL);
					buttonContainer.setPadding(0, dp(act, 16), 0, 0);
					for (final Map.Entry<String, String> entry : script.downloadUrls.entrySet())
					{
						Button downloadBtn = new Button(act);
						downloadBtn.setText(entry.getKey());
						downloadBtn.setTextColor(Color.WHITE);
						downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						downloadBtn.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
						downloadBtn.setBackground(getRoundBg(act, 0xFF6200EE, 8));
						LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																						ViewGroup.LayoutParams.WRAP_CONTENT);
						btnLp.bottomMargin = dp(act, 8);
						buttonContainer.addView(downloadBtn, btnLp);
						downloadBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
									try
									{
										if (detailDialogRef[0] != null)
										{
											detailDialogRef[0].dismiss();
										}
										new Handler().postDelayed(new Runnable() {
												@Override
												public void run()
												{
													try
													{
														Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue()));
														act.startActivity(intent);
														Toast.makeText(act, getLocalizedString(ctx, "script_opened_in_via"),
																	   Toast.LENGTH_LONG).show();
													}
													catch (Exception e)
													{
														Toast.makeText(act, getLocalizedString(ctx, "cannot_open_download_link"),
																	   Toast.LENGTH_SHORT).show();
													}
												}
											}, 100); 
									}
									catch (Exception e)
									{
										Toast.makeText(act, getLocalizedString(ctx, "cannot_open_download_link"),
													   Toast.LENGTH_SHORT).show();
									}
								}
							});
					}
					layout.addView(buttonContainer);
					scrollView.addView(layout);
					builder.setView(scrollView);
					builder.setNegativeButton(getLocalizedString(ctx, "dialog_cancel"), null);
					detailDialogRef[0] = builder.create();
					detailDialogRef[0].show();
				}
			});
	}
	private interface ScriptsLoadCallback
	{
		void onScriptsLoaded(List<ScriptInfo> scripts);
		void onLoadFailed(String error);
	}
	private void addAdBlockRulesItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "ad_block_rules_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "ad_block_rules_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showAdBlockRulesDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "ad_block_rules_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showAdBlockRulesDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(0, 0, 0, 0);
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "ad_block_rules_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(0xFF333333);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "ad_block_rules_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					errorIcon.setColorFilter(0xFF888888);
					errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
					iconLp.gravity = Gravity.CENTER;
					iconLp.bottomMargin = dp(act, 16);
					emptyStateContainer.addView(errorIcon, iconLp);
					final TextView emptyStateText = new TextView(act);
					emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					emptyStateText.setTextColor(0xFF888888);
					emptyStateText.setGravity(Gravity.CENTER);
					emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
					emptyStateContainer.addView(emptyStateText);
					root.addView(rulesContainer);
					root.addView(emptyStateContainer);
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					dialogContainer.addView(scrollRoot);
					dialog.setContentView(dialogContainer);
					Window window = dialog.getWindow();
					if (window != null)
					{
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
						window.setClipToOutline(true);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					showRulesLoadingState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText);
					loadRulesFromNetwork(ctx, new RulesLoadCallback() {
							@Override
							public void onRulesLoaded(final List<RuleInfo> rules)
							{
								if (act != null && !act.isFinishing())
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												refreshRulesList(act, ctx, rulesContainer, emptyStateContainer, emptyStateText,
																 rules);
											}
										});
								}
							}
							@Override
							public void onLoadFailed(final String error)
							{
								if (act != null && !act.isFinishing())
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												showRulesErrorState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText,
																	error);
											}
										});
								}
							}
						});
					dialog.show();
				}
			});
	}
	private static class RuleInfo
	{
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
				 String homepage)
		{
			this.id = id;
			this.nameMap = nameMap;
			this.descriptionMap = descriptionMap;
			this.detailMap = detailMap;
			this.downloadUrls = downloadUrls;
			this.category = category;
			this.author = author;
			this.homepage = homepage;
		}
		String getName(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return nameMap.getOrDefault(langCode, nameMap.get("zh-CN"));
		}
		String getDescription(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return descriptionMap.getOrDefault(langCode, descriptionMap.get("zh-CN"));
		}
		String getDetail(Context ctx)
		{
			String langCode = getLanguageCode(ctx);
			return detailMap.getOrDefault(langCode, detailMap.get("zh-CN"));
		}
		private String getLanguageCode(Context ctx)
		{
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved))
			{
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				}
				else
				{
					locale = ctx.getResources().getConfiguration().locale;
				}
				if (Locale.SIMPLIFIED_CHINESE.equals(locale))
				{
					return "zh-CN";
				}
				else if (Locale.TRADITIONAL_CHINESE.equals(locale))
				{
					return "zh-TW";
				}
				else if (Locale.ENGLISH.equals(locale))
				{
					return "en";
				}
				return "zh-CN";
			}
			return saved;
		}
		static RuleInfo fromJSON(JSONObject json) throws JSONException
		{
			String id = json.getString("id");
			Map<String, String> nameMap = new HashMap<>();
			JSONObject names = json.getJSONObject("names");
			Iterator<String> nameKeys = names.keys();
			while (nameKeys.hasNext())
			{
				String lang = nameKeys.next();
				nameMap.put(lang, names.getString(lang));
			}
			Map<String, String> descriptionMap = new HashMap<>();
			JSONObject descriptions = json.getJSONObject("descriptions");
			Iterator<String> descKeys = descriptions.keys();
			while (descKeys.hasNext())
			{
				String lang = descKeys.next();
				descriptionMap.put(lang, descriptions.getString(lang));
			}
			Map<String, String> detailMap = new HashMap<>();
			JSONObject details = json.getJSONObject("details");
			Iterator<String> detailKeys = details.keys();
			while (detailKeys.hasNext())
			{
				String lang = detailKeys.next();
				detailMap.put(lang, details.getString(lang));
			}
			Map<String, String> downloadUrls = new HashMap<>();
			JSONObject downloads = json.getJSONObject("downloadUrls");
			Iterator<String> downloadKeys = downloads.keys();
			while (downloadKeys.hasNext())
			{
				String channel = downloadKeys.next();
				downloadUrls.put(channel, downloads.getString(channel));
			}
			String category = json.getString("category");
			String author = json.optString("author", "");
			String homepage = json.optString("homepage", "");
			return new RuleInfo(id, nameMap, descriptionMap, detailMap, downloadUrls, category, author, homepage);
		}
	}
	private void loadRulesFromNetwork(final Context ctx, final RulesLoadCallback callback)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
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
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							InputStream inputStream = connection.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
							StringBuilder response = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null)
							{
								response.append(line);
							}
							reader.close();
							JSONObject json = new JSONObject(response.toString());
							JSONArray rulesArray = json.getJSONArray("rules");
							List<RuleInfo> rules = new ArrayList<>();
							for (int i = 0; i < rulesArray.length(); i++)
							{
								JSONObject ruleJson = rulesArray.getJSONObject(i);
								RuleInfo rule = RuleInfo.fromJSON(ruleJson);
								rules.add(rule);
							}
							callback.onRulesLoaded(rules);
						}
						else
						{
							callback.onLoadFailed("HTTP " + connection.getResponseCode());
						}
						connection.disconnect();
					}
					catch (Exception e)
					{
						callback.onLoadFailed(e.getMessage());
					}
				}
			}).start();
	}
	private void showRulesLoadingState(Activity act, Context ctx, LinearLayout rulesContainer,
									   LinearLayout emptyStateContainer, TextView emptyStateText)
	{
		rulesContainer.removeAllViews();
		rulesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(getLocalizedString(ctx, "rules_loading"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.GONE);
			}
		}
		ProgressBar progressBar = new ProgressBar(act);
		progressBar.setIndeterminate(true);
		LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
		progressLp.gravity = Gravity.CENTER;
		progressLp.bottomMargin = dp(act, 16);
		emptyStateContainer.addView(progressBar, 0, progressLp);
	}
	private void showRulesErrorState(Activity act, Context ctx, LinearLayout rulesContainer,
									 LinearLayout emptyStateContainer, TextView emptyStateText, String error)
	{
		rulesContainer.removeAllViews();
		rulesContainer.setVisibility(View.GONE);
		emptyStateContainer.setVisibility(View.VISIBLE);
		emptyStateText.setText(
			getLocalizedString(ctx, "rules_load_failed") + "\n" + getLocalizedString(ctx, "check_network"));
		for (int i = 0; i < emptyStateContainer.getChildCount(); i++)
		{
			View child = emptyStateContainer.getChildAt(i);
			if (child instanceof ImageView)
			{
				child.setVisibility(View.VISIBLE);
			}
			else if (child instanceof ProgressBar)
			{
				emptyStateContainer.removeView(child); 
			}
		}
		Toast.makeText(ctx, getLocalizedString(ctx, "rules_load_failed") + ": " + error, Toast.LENGTH_SHORT).show();
	}
	private void refreshRulesList(final Activity act, final Context ctx, LinearLayout rulesContainer,
								  LinearLayout emptyStateContainer, TextView emptyStateText, List<RuleInfo> rules)
	{
		rulesContainer.removeAllViews();
		if (rules == null || rules.isEmpty())
		{
			showRulesErrorState(act, ctx, rulesContainer, emptyStateContainer, emptyStateText, "No rules available");
			return;
		}
		rulesContainer.setVisibility(View.VISIBLE);
		emptyStateContainer.setVisibility(View.GONE);
		Map<String, List<RuleInfo>> categorizedRules = new HashMap<>();
		for (RuleInfo rule : rules)
		{
			String category = rule.category;
			if (!categorizedRules.containsKey(category))
			{
				categorizedRules.put(category, new ArrayList<RuleInfo>());
			}
			categorizedRules.get(category).add(rule);
		}
		for (Map.Entry<String, List<RuleInfo>> entry : categorizedRules.entrySet())
		{
			String category = entry.getKey();
			List<RuleInfo> categoryRules = entry.getValue();
			TextView categoryTitle = new TextView(act);
			categoryTitle.setText(getCategoryDisplayName(ctx, category));
			categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			categoryTitle.setTextColor(0xFF6200EE);
			categoryTitle.setTypeface(null, Typeface.BOLD);
			categoryTitle.setPadding(0, dp(act, 16), 0, dp(act, 8));
			rulesContainer.addView(categoryTitle);
			for (final RuleInfo rule : categoryRules)
			{
				LinearLayout ruleCard = createRuleCard(act, ctx, rule);
				LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				 ViewGroup.LayoutParams.WRAP_CONTENT);
				cardLp.bottomMargin = dp(ctx, 12);
				rulesContainer.addView(ruleCard, cardLp);
			}
		}
	}
	private String getCategoryDisplayName(Context ctx, String category)
	{
		if ("small".equals(category))
		{
			return getLocalizedString(ctx, "rules_category_small");
		}
		else if ("large".equals(category))
		{
			return getLocalizedString(ctx, "rules_category_large");
		}
		return category;
	}
	private LinearLayout createRuleCard(final Activity act, final Context ctx, final RuleInfo rule)
	{
		LinearLayout ruleCard = new LinearLayout(act);
		ruleCard.setOrientation(LinearLayout.VERTICAL);
		ruleCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(0xFFF8F9FA);
		cardBg.setStroke(dp(act, 1), 0xFFE9ECEF);
		cardBg.setCornerRadius(dp(act, 12));
		ruleCard.setBackground(cardBg);
		TextView ruleName = new TextView(act);
		ruleName.setText(rule.getName(ctx));
		ruleName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		ruleName.setTextColor(Color.BLACK);
		ruleName.setTypeface(null, Typeface.BOLD);
		ruleCard.addView(ruleName);
		TextView ruleDescription = new TextView(act);
		ruleDescription.setText(rule.getDescription(ctx));
		ruleDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		ruleDescription.setTextColor(0xFF666666);
		ruleDescription.setPadding(0, dp(act, 8), 0, 0);
		ruleCard.addView(ruleDescription);
		ruleCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showRuleDetailDialog(ctx, rule);
				}
			});
		return ruleCard;
	}
	private void showRuleDetailDialog(final Context ctx, final RuleInfo rule)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					final AlertDialog[] detailDialogRef = new AlertDialog[1];
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle(rule.getName(ctx));
					ScrollView scrollView = new ScrollView(act);
					LinearLayout layout = new LinearLayout(act);
					layout.setOrientation(LinearLayout.VERTICAL);
					layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					TextView detailText = new TextView(act);
					detailText.setText(rule.getDetail(ctx));
					detailText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					detailText.setTextColor(Color.BLACK);
					detailText.setLineSpacing(dp(act, 4), 1.2f);
					layout.addView(detailText);
					if (rule.author != null && !rule.author.isEmpty())
					{
						TextView authorText = new TextView(act);
						authorText.setText(getLocalizedString(ctx, "rule_author") + ": " + rule.author);
						authorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						authorText.setTextColor(0xFF666666);
						authorText.setPadding(0, dp(act, 8), 0, 0);
						layout.addView(authorText);
					}
					if (rule.homepage != null && !rule.homepage.isEmpty())
					{
						TextView homepageText = new TextView(act);
						homepageText.setText(getLocalizedString(ctx, "rule_homepage") + ": " + rule.homepage);
						homepageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						homepageText.setTextColor(0xFF6200EE);
						homepageText.setPadding(0, dp(act, 8), 0, 0);
						homepageText.setPaintFlags(homepageText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
						homepageText.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
									try
									{
										Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rule.homepage));
										act.startActivity(intent);
									}
									catch (Exception e)
									{
										Toast.makeText(act, getLocalizedString(ctx, "cannot_open_homepage"), Toast.LENGTH_SHORT)
											.show();
									}
								}
							});
						layout.addView(homepageText);
					}
					LinearLayout buttonContainer = new LinearLayout(act);
					buttonContainer.setOrientation(LinearLayout.VERTICAL);
					buttonContainer.setPadding(0, dp(act, 16), 0, 0);
					int channelIndex = 1;
					for (final Map.Entry<String, String> entry : rule.downloadUrls.entrySet())
					{
						Button downloadBtn = new Button(act);
						downloadBtn.setText(
							getLocalizedString(ctx, "rule_channel") + " " + channelIndex + " - " + entry.getKey());
						downloadBtn.setTextColor(Color.WHITE);
						downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						downloadBtn.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
						downloadBtn.setBackground(getRoundBg(act, 0xFF6200EE, 8));
						LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																						ViewGroup.LayoutParams.WRAP_CONTENT);
						btnLp.bottomMargin = dp(act, 8);
						buttonContainer.addView(downloadBtn, btnLp);
						downloadBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
									copyToClipboard(act, entry.getValue());
									Toast.makeText(act, getLocalizedString(ctx, "rule_link_copied"), Toast.LENGTH_SHORT).show();
								}
							});
						channelIndex++;
					}
					layout.addView(buttonContainer);
					scrollView.addView(layout);
					builder.setView(scrollView);
					builder.setNegativeButton(getLocalizedString(ctx, "dialog_cancel"), null);
					detailDialogRef[0] = builder.create();
					detailDialogRef[0].show();
				}
			});
	}
	private interface RulesLoadCallback
	{
		void onRulesLoaded(List<RuleInfo> rules);
		void onLoadFailed(String error);
	}
	private void setHideStatusBar(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (hideStatusBarHook == null)
			{
				hideStatusBarHook = XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable
						{
							final Activity activity = (Activity) param.thisObject;
							if (hideStatusBarEnabled)
							{
								activity.runOnUiThread(new Runnable() {
										@Override
										public void run()
										{
											setupStatusBarHiding(activity);
											statusBarHiddenActivities.put(activity, true);
											XposedBridge.log("[BetterVia] 已为 " + activity.getClass().getSimpleName()
															 + " 设置状态栏隐藏");
										}
									});
							}
						}
					});
				XposedBridge.log("[BetterVia] 隐藏状态栏已启用");
			}
		}
		else
		{
			if (hideStatusBarHook != null)
			{
				hideStatusBarHook.unhook();
				hideStatusBarHook = null;
				XposedBridge.log("[BetterVia] 隐藏状态栏已停用");
				restoreStatusBar();
			}
		}
		hideStatusBarEnabled = on;
		putPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, on);
	}
	private void setupStatusBarHiding(final Activity activity)
	{
		try
		{
			if (activity.isFinishing() || activity.isDestroyed())
				return;
			final View decorView = activity.getWindow().getDecorView();
			hideStatusBarImmediate(activity);
			decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
					@Override
					public void onSystemUiVisibilityChange(int visibility)
					{
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
						{
							decorView.postDelayed(new Runnable() {
									@Override
									public void run()
									{
										if (!activity.isFinishing() && !activity.isDestroyed() && hideStatusBarEnabled)
										{
											hideStatusBarImmediate(activity);
											setupStatusBarHiding(activity);
										}
									}
								}, 100);
						}
					}
				});
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 滑动更新状态栏失败: " + e);
		}
	}
	private void hideStatusBarImmediate(Activity activity)
	{
		try
		{
			if (activity.isFinishing() || activity.isDestroyed())
			{
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
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 立即隐藏状态栏失败: " + e);
		}
	}
	private void restoreStatusBar()
	{
		for (final Activity activity : statusBarHiddenActivities.keySet())
		{
			if (!activity.isFinishing() && !activity.isDestroyed())
			{
				activity.runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							try
							{
								View decorView = activity.getWindow().getDecorView();
								decorView.setOnSystemUiVisibilityChangeListener(null);
								Runnable rehideRunnable = statusBarRehideRunnables.get(activity);
								if (rehideRunnable != null)
								{
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
							}
							catch (Exception e)
							{
								XposedBridge.log("[BetterVia] 恢复状态栏失败: " + e);
							}
						}
					});
			}
		}
		statusBarHiddenActivities.clear();
		statusBarRehideRunnables.clear();
		XposedBridge.log("[BetterVia] 已恢复所有Activity的状态栏显示");
	}
	private void addCookieManagementItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "cookie_management_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "cookie_management_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showCookieManagementDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "cookie_management_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showCookieManagementDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
					dialog.setCancelable(true);
					LinearLayout rootLayout = new LinearLayout(act);
					rootLayout.setOrientation(LinearLayout.VERTICAL);
					rootLayout.setBackgroundColor(Color.WHITE);
					RelativeLayout titleBar = new RelativeLayout(act);
					titleBar.setBackgroundColor(0xFFF5F5F5);
					titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
					titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																		   ViewGroup.LayoutParams.WRAP_CONTENT));
					ImageButton backButton = new ImageButton(act);
					backButton.setImageResource(android.R.drawable.ic_menu_revert);
					backButton.setBackgroundResource(android.R.color.transparent);
					backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
					backButton.setColorFilter(0xFF000000);
					RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
					backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
					titleBar.addView(backButton, backButtonLp);
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "cookie_manager_dialog_title"));
					title.setTextColor(Color.BLACK);
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
					refreshButton.setColorFilter(0xFF000000);
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
					searchEdit.setHint(getLocalizedString(ctx, "cookie_manager_search_hint"));
					searchEdit.setTextColor(Color.BLACK);
					searchEdit.setHintTextColor(0xFF888888);
					searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					searchEdit.setBackground(getRoundBg(act, 0xFFF0F0F0, 8));
					searchEdit.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
					LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(0,
																					   ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					searchLp.rightMargin = dp(act, 8);
					searchBar.addView(searchEdit, searchLp);
					Button searchButton = new Button(act);
					searchButton.setText(getLocalizedString(ctx, "cookie_manager_search_btn"));
					searchButton.setTextColor(Color.WHITE);
					searchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					searchButton.setBackground(getRoundBg(act, 0xFF6200EE, 8));
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
					LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
					progressLp.gravity = Gravity.CENTER;
					progressLp.bottomMargin = dp(act, 16);
					loadingContainer.addView(progressBar, progressLp);
					TextView loadingText = new TextView(act);
					loadingText.setText(getLocalizedString(ctx, "cookie_manager_loading"));
					loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					loadingText.setTextColor(0xFF888888);
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
					final Button deleteButton = new Button(act);
					deleteButton.setText(getLocalizedString(ctx, "cookie_manager_delete_selected"));
					deleteButton.setTextColor(Color.WHITE);
					deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					deleteButton.setBackground(getRoundBg(act, 0xFFE53935, 8));
					deleteButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
					deleteButton.setEnabled(false);
					LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(0,
																					   ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					deleteLp.rightMargin = dp(act, 6);
					buttonBar.addView(deleteButton, deleteLp);
					final Button selectAllButton = new Button(act);
					selectAllButton.setText(getLocalizedString(ctx, "cookie_manager_select_all"));
					selectAllButton.setTextColor(0xFF6200EE);
					selectAllButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					selectAllButton.setBackground(getRoundBg(act, 0xFFF0F0F0, 8));
					selectAllButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
					LinearLayout.LayoutParams selectAllLp = new LinearLayout.LayoutParams(0,
																						  ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					selectAllLp.leftMargin = dp(act, 6);
					selectAllLp.rightMargin = dp(act, 6);
					buttonBar.addView(selectAllButton, selectAllLp);
					Button closeButton = new Button(act);
					closeButton.setText(getLocalizedString(ctx, "dialog_close"));
					closeButton.setTextColor(Color.WHITE);
					closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					closeButton.setBackground(getRoundBg(act, 0xFF6200EE, 8));
					closeButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
					LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(0,
																					  ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					closeLp.leftMargin = dp(act, 6);
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
					domainViewBtn.setText(getLocalizedString(ctx, "cookie_view_domain"));
					domainViewBtn.setTextColor(Color.WHITE);
					domainViewBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					domainViewBtn.setBackground(getRoundBg(act, 0xFF6200EE, 6));
					domainViewBtn.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
					LinearLayout.LayoutParams domainViewLp = new LinearLayout.LayoutParams(0,
																						   ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					domainViewLp.rightMargin = dp(act, 4);
					switchBar.addView(domainViewBtn, domainViewLp);
					final Button listViewBtn = new Button(act);
					listViewBtn.setText(getLocalizedString(ctx, "cookie_view_list"));
					listViewBtn.setTextColor(Color.BLACK);
					listViewBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					listViewBtn.setBackground(getRoundBg(act, 0xFFF0F0F0, 6));
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
					LinearLayout.LayoutParams switchProgressLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
					switchProgressLp.gravity = Gravity.CENTER;
					switchProgressLp.bottomMargin = dp(act, 12);
					switchLoadingContainer.addView(switchProgressBar, switchProgressLp);
					TextView switchLoadingText = new TextView(act);
					switchLoadingText.setText(getLocalizedString(ctx, "cookie_view_switching"));
					switchLoadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					switchLoadingText.setTextColor(0xFF666666);
					switchLoadingText.setGravity(Gravity.CENTER);
					switchLoadingText.setTypeface(null, Typeface.NORMAL);
					switchLoadingContainer.addView(switchLoadingText);
					listAndLoadingContainer.addView(switchLoadingFrame);
					new Thread(new Runnable() {
							@Override
							public void run()
							{
								final List<CookieItem> cookieItems = loadCookieData(ctx);
								final List<DomainItem> domainItems = loadDomainGroupedCookieData(ctx);
								currentCookieList[0] = cookieItems;
								currentDomainList[0] = domainItems;
								act.runOnUiThread(new Runnable() {
										@Override
										public void run()
										{
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
							public void onClick(View v)
							{
								if (!isDomainView[0])
								{
									domainViewBtn.setEnabled(false);
									listViewBtn.setEnabled(false);
									switchLoadingFrame.setVisibility(View.VISIBLE);
									scrollView.setVisibility(View.GONE);
									new Handler().postDelayed(new Runnable() {
											@Override
											public void run()
											{
												act.runOnUiThread(new Runnable() {
														@Override
														public void run()
														{
															isDomainView[0] = true;
															domainViewBtn.setTextColor(Color.WHITE);
															domainViewBtn.setBackground(getRoundBg(act, 0xFF6200EE, 6));
															listViewBtn.setTextColor(Color.BLACK);
															listViewBtn.setBackground(getRoundBg(act, 0xFFF0F0F0, 6));
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
							public void onClick(View v)
							{
								if (isDomainView[0])
								{
									domainViewBtn.setEnabled(false);
									listViewBtn.setEnabled(false);
									switchLoadingFrame.setVisibility(View.VISIBLE);
									scrollView.setVisibility(View.GONE);
									new Handler().postDelayed(new Runnable() {
											@Override
											public void run()
											{
												act.runOnUiThread(new Runnable() {
														@Override
														public void run()
														{
															isDomainView[0] = false;
															isAllSelected[0] = false; 
															selectAllButton.setText(getLocalizedString(ctx, "cookie_manager_select_all"));
															listViewBtn.setTextColor(Color.WHITE);
															listViewBtn.setBackground(getRoundBg(act, 0xFF6200EE, 6));
															domainViewBtn.setTextColor(Color.BLACK);
															domainViewBtn.setBackground(getRoundBg(act, 0xFFF0F0F0, 6));
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
							public void onClick(View v)
							{
								isAllSelected[0] = !isAllSelected[0];
								if (isAllSelected[0])
								{
									selectAllButton.setText(getLocalizedString(ctx, "cookie_manager_unselect_all"));
									Toast.makeText(act, getLocalizedString(ctx, "cookie_manager_selecting"), Toast.LENGTH_SHORT).show();
									if (isDomainView[0])
									{
										for (DomainItem item : currentDomainList[0])
										{
											item.selected = true;
										}
									}
									else
									{
										for (CookieItem item : currentCookieList[0])
										{
											item.selected = true;
										}
									}
									new Thread(new Runnable() {
											@Override
											public void run()
											{
												act.runOnUiThread(new Runnable() {
														@Override
														public void run()
														{
															for (int i = 0; i < listContainer.getChildCount(); i++)
															{
																View child = listContainer.getChildAt(i);
																if (child instanceof LinearLayout)
																{
																	LinearLayout itemLayout = (LinearLayout) child;
																	View firstChild = itemLayout.getChildAt(0);
																	if (firstChild instanceof LinearLayout)
																	{
																		CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild).getChildAt(0);
																		if (checkbox != null)
																		{
																			checkbox.setChecked(true);
																		}
																		if (itemLayout.getTag() instanceof CookieItem)
																		{
																			((CookieItem) itemLayout.getTag()).selected = true;
																		}
																		else if (itemLayout.getTag() instanceof DomainItem)
																		{
																			((DomainItem) itemLayout.getTag()).selected = true;
																		}
																	}
																}
															}
															deleteButton.setEnabled(true);
															deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
														}
													});
											}
										}).start();
								}
								else
								{
									selectAllButton.setText(getLocalizedString(ctx, "cookie_manager_select_all"));
									Toast.makeText(act, getLocalizedString(ctx, "cookie_manager_unselecting"), Toast.LENGTH_SHORT).show();
									if (isDomainView[0])
									{
										for (DomainItem item : currentDomainList[0])
										{
											item.selected = false;
										}
									}
									else
									{
										for (CookieItem item : currentCookieList[0])
										{
											item.selected = false;
										}
									}
									new Thread(new Runnable() {
											@Override
											public void run()
											{
												act.runOnUiThread(new Runnable() {
														@Override
														public void run()
														{
															for (int i = 0; i < listContainer.getChildCount(); i++)
															{
																View child = listContainer.getChildAt(i);
																if (child instanceof LinearLayout)
																{
																	LinearLayout itemLayout = (LinearLayout) child;
																	View firstChild = itemLayout.getChildAt(0);
																	if (firstChild instanceof LinearLayout)
																	{
																		CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild).getChildAt(0);
																		if (checkbox != null)
																		{
																			checkbox.setChecked(false);
																		}
																		if (itemLayout.getTag() instanceof CookieItem)
																		{
																			((CookieItem) itemLayout.getTag()).selected = false;
																		}
																		else if (itemLayout.getTag() instanceof DomainItem)
																		{
																			((DomainItem) itemLayout.getTag()).selected = false;
																		}
																	}
																}
															}
															deleteButton.setEnabled(false);
															deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
														}
													});
											}
										}).start();
								}
							}
						});
					backButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					refreshButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								loadingContainer.setVisibility(View.VISIBLE);
								switchBar.setVisibility(View.GONE);
								scrollView.setVisibility(View.GONE);
								new Thread(new Runnable() {
										@Override
										public void run()
										{
											final List<CookieItem> refreshedCookieData = loadCookieData(ctx);
											final List<DomainItem> refreshedDomainData = loadDomainGroupedCookieData(ctx);
											currentCookieList[0] = refreshedCookieData;
											currentDomainList[0] = refreshedDomainData;
											act.runOnUiThread(new Runnable() {
													@Override
													public void run()
													{
														loadingContainer.setVisibility(View.GONE);
														switchBar.setVisibility(View.VISIBLE);
														scrollView.setVisibility(View.VISIBLE);
														listContainer.removeAllViews(); 
														if (isDomainView[0])
														{
															populateDomainList(act, listContainer, refreshedDomainData, deleteButton,
																			   scrollView, ctx, refreshedDomainData);
														}
														else
														{
															populateCookieList(act, listContainer, refreshedCookieData, deleteButton,
																			   scrollView);
														}
														Toast.makeText(act, getLocalizedString(ctx, "cookie_management_refreshed"),
																	   Toast.LENGTH_SHORT).show();
													}
												});
										}
									}).start();
							}
						});
					searchButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								final String query = searchEdit.getText().toString().trim().toLowerCase();
								if (query.isEmpty())
								{
									if (isDomainView[0])
									{
										populateDomainList(act, listContainer, currentDomainList[0], deleteButton, scrollView,
														   ctx);
									}
									else
									{
										populateCookieList(act, listContainer, currentCookieList[0], deleteButton, scrollView);
									}
									return;
								}
								if (isDomainView[0])
								{
									List<DomainItem> filteredDomainList = new ArrayList<DomainItem>();
									for (DomainItem domainItem : currentDomainList[0])
									{
										boolean domainMatch = domainItem.domain.toLowerCase().contains(query);
										boolean cookieMatch = false;
										for (CookieItem cookie : domainItem.cookies)
										{
											if ((cookie.name != null && cookie.name.toLowerCase().contains(query))
												|| (cookie.value != null && cookie.value.toLowerCase().contains(query)))
											{
												cookieMatch = true;
												break;
											}
										}
										if (domainMatch || cookieMatch)
										{
											filteredDomainList.add(domainItem);
										}
									}
									populateDomainList(act, listContainer, filteredDomainList, deleteButton, scrollView, ctx,
													   filteredDomainList);
									String resultMsg = String.format(getLocalizedString(act, "cookie_domain_search_result"),
																	 filteredDomainList.size());
									Toast.makeText(act, resultMsg, Toast.LENGTH_SHORT).show();
								}
								else
								{
									List<CookieItem> filteredList = new ArrayList<CookieItem>();
									for (CookieItem item : currentCookieList[0])
									{
										if ((item.host_key != null && item.host_key.toLowerCase().contains(query))
											|| (item.name != null && item.name.toLowerCase().contains(query))
											|| (item.value != null && item.value.toLowerCase().contains(query)))
										{
											filteredList.add(item);
										}
									}
									populateCookieList(act, listContainer, filteredList, deleteButton, scrollView);
									String resultMsg = String.format(getLocalizedString(act, "cookie_search_result"),
																	 filteredList.size());
									Toast.makeText(act, resultMsg, Toast.LENGTH_SHORT).show();
								}
							}
						});
					deleteButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								showDeleteConfirmDialog(act, ctx, listContainer, deleteButton, scrollView, isDomainView[0]);
							}
						});
					closeButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					dialog.show();
				}
			});
	}
	private void showCookieDetailDialog(final Activity act, final CookieItem cookie)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle(getLocalizedString(act, "cookie_detail_dialog_title"));
		ScrollView scrollView = new ScrollView(act);
		LinearLayout layout = new LinearLayout(act);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
		TextView basicInfoTitle = new TextView(act);
		basicInfoTitle.setText(getLocalizedString(act, "cookie_detail_basic_info"));
		basicInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		basicInfoTitle.setTextColor(Color.BLACK);
		basicInfoTitle.setTypeface(null, Typeface.BOLD);
		basicInfoTitle.setPadding(0, 0, 0, dp(act, 12));
		layout.addView(basicInfoTitle);
		final EditText hostKeyEdit = addEditableField(layout, act, getLocalizedString(act, "cookie_field_host_key"),
													  cookie.host_key != null ? cookie.host_key : "");
		final EditText nameEdit = addEditableField(layout, act, getLocalizedString(act, "cookie_field_name"),
												   cookie.name != null ? cookie.name : "");
		final EditText valueEdit = addEditableField(layout, act, getLocalizedString(act, "cookie_field_value"),
													cookie.value != null ? cookie.value : "");
		final EditText pathEdit = addEditableField(layout, act, getLocalizedString(act, "cookie_field_path"),
												   cookie.path != null ? cookie.path : "");
		TextView timeInfoTitle = new TextView(act);
		timeInfoTitle.setText(getLocalizedString(act, "cookie_detail_time_info"));
		timeInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		timeInfoTitle.setTextColor(Color.BLACK);
		timeInfoTitle.setTypeface(null, Typeface.BOLD);
		timeInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(timeInfoTitle);
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_creation_time"),
						 cookie.creation_utc > 0
						 ? formatTimestamp(cookie.creation_utc)
						 : getLocalizedString(act, "cookie_field_unknown"));
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_last_access"),
						 cookie.last_access_utc > 0
						 ? formatTimestamp(cookie.last_access_utc)
						 : getLocalizedString(act, "cookie_field_unknown"));
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_expires"),
						 cookie.expires_utc > 0
						 ? formatTimestamp(cookie.expires_utc)
						 : getLocalizedString(act, "cookie_field_session"));
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_last_update"),
						 cookie.last_update_utc > 0
						 ? formatTimestamp(cookie.last_update_utc)
						 : getLocalizedString(act, "cookie_field_unknown"));
		TextView securityInfoTitle = new TextView(act);
		securityInfoTitle.setText(getLocalizedString(act, "cookie_detail_security_info"));
		securityInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		securityInfoTitle.setTextColor(Color.BLACK);
		securityInfoTitle.setTypeface(null, Typeface.BOLD);
		securityInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(securityInfoTitle);
		final CheckBox secureCheckbox = addCheckboxField(layout, act, getLocalizedString(act, "cookie_field_secure"),
														 cookie.is_secure);
		final CheckBox httpOnlyCheckbox = addCheckboxField(layout, act,
														   getLocalizedString(act, "cookie_field_httponly"), cookie.is_httponly);
		final CheckBox persistentCheckbox = addCheckboxField(layout, act,
															 getLocalizedString(act, "cookie_field_persistent"), cookie.is_persistent);
		final CheckBox hasExpiresCheckbox = addCheckboxField(layout, act,
															 getLocalizedString(act, "cookie_field_has_expires"), cookie.has_expires);
		TextView advancedInfoTitle = new TextView(act);
		advancedInfoTitle.setText(getLocalizedString(act, "cookie_detail_advanced_info"));
		advancedInfoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		advancedInfoTitle.setTextColor(Color.BLACK);
		advancedInfoTitle.setTypeface(null, Typeface.BOLD);
		advancedInfoTitle.setPadding(0, dp(act, 16), 0, dp(act, 12));
		layout.addView(advancedInfoTitle);
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_priority"),
						 String.valueOf(cookie.priority));
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_samesite"),
						 getSameSiteText(act, cookie.samesite)); 
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_source_port"),
						 cookie.source_port > 0
						 ? String.valueOf(cookie.source_port)
						 : getLocalizedString(act, "cookie_field_default"));
		addReadOnlyField(layout, act, getLocalizedString(act, "cookie_field_source_type"),
						 getSourceTypeText(act, cookie.source_type)); 
		scrollView.addView(layout);
		builder.setView(scrollView);
		builder.setPositiveButton(getLocalizedString(act, "dialog_ok"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
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
					Toast.makeText(act, getLocalizedString(act, "cookie_save_success"), Toast.LENGTH_SHORT).show();
				}
			});
		builder.setNeutralButton(getLocalizedString(act, "dialog_cancel"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
		builder.show();
	}
	private void removeDeletedCookieFromList(final Activity act, final LinearLayout listContainer,
											 final Button deleteButton, final ScrollView scrollView, final CookieItem deletedCookie)
	{
		for (int i = 0; i < listContainer.getChildCount(); i++)
		{
			View child = listContainer.getChildAt(i);
			if (child.getTag() instanceof CookieItem)
			{
				CookieItem item = (CookieItem) child.getTag();
				if (itemMatchesDeleted(item, deletedCookie))
				{
					listContainer.removeViewAt(i);
					updateDeleteButtonState(act, listContainer, deleteButton);
					XposedBridge.log("[BetterVia] 已从列表移除被删除的Cookie: " + item.name);
					break;
				}
			}
		}
		if (listContainer.getChildCount() == 0)
		{
			showEmptyCookieListState(act, listContainer);
		}
	}
	private boolean itemMatchesDeleted(CookieItem item, CookieItem deletedCookie)
	{
		return item.creation_utc == deletedCookie.creation_utc && safeEquals(item.host_key, deletedCookie.host_key)
			&& safeEquals(item.name, deletedCookie.name);
	}
	private boolean safeEquals(String str1, String str2)
	{
		if (str1 == null && str2 == null)
			return true;
		if (str1 == null || str2 == null)
			return false;
		return str1.equals(str2);
	}
	private void updateDeleteButtonState(Context ctx, LinearLayout listContainer, Button deleteButton)
	{ 
		int selectedCount = 0;
		for (int i = 0; i < listContainer.getChildCount(); i++)
		{
			View child = listContainer.getChildAt(i);
			if (child.getTag() instanceof CookieItem)
			{
				CookieItem item = (CookieItem) child.getTag();
				if (item.selected)
				{
					selectedCount++;
				}
			}
		}
		deleteButton.setEnabled(selectedCount > 0);
		deleteButton.setText(getLocalizedString(ctx, "cookie_manager_delete_selected")); 
	}
	private void showEmptyCookieListState(final Activity act, final LinearLayout listContainer)
	{
		listContainer.removeAllViews();
		TextView emptyText = new TextView(act);
		emptyText.setText(getLocalizedString(act, "cookie_manager_empty"));
		emptyText.setTextColor(0xFF888888);
		emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		emptyText.setGravity(Gravity.CENTER);
		emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
		listContainer.addView(emptyText);
	}
	private LinearLayout findCookieListContainer(View view)
	{
		if (view instanceof LinearLayout)
		{
			LinearLayout layout = (LinearLayout) view;
			for (int i = 0; i < layout.getChildCount(); i++)
			{
				View child = layout.getChildAt(i);
				if (child.getTag() instanceof CookieItem)
				{
					return layout; 
				}
			}
		}
		if (view instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				LinearLayout result = findCookieListContainer(viewGroup.getChildAt(i));
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
	private ScrollView findCookieListScrollView(View view)
	{
		if (view instanceof ScrollView)
		{
			ScrollView scrollView = (ScrollView) view;
			if (scrollView.getChildCount() > 0)
			{
				View child = scrollView.getChildAt(0);
				if (child instanceof LinearLayout)
				{
					LinearLayout container = (LinearLayout) child;
					if (container.getChildCount() > 0)
					{
						View firstChild = container.getChildAt(0);
						if (firstChild instanceof LinearLayout && firstChild.getTag() instanceof CookieItem)
						{
							return scrollView;
						}
					}
				}
			}
			return null;
		}
		if (view instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				ScrollView result = findCookieListScrollView(viewGroup.getChildAt(i));
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
	private Button findDeleteButton(Context ctx, View view)
	{ 
		if (view instanceof Button)
		{
			Button button = (Button) view;
			String buttonText = button.getText().toString();
			if (buttonText.startsWith(getLocalizedString(ctx, "cookie_manager_delete_selected")))
			{
				return button;
			}
			return null;
		}
		if (view instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				Button result = findDeleteButton(ctx, viewGroup.getChildAt(i)); 
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
	private String formatTimestamp(long timestamp)
	{
		try
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp * 1000));
		}
		catch (Exception e)
		{
			return "Time format error";
		}
	}
	private String getSameSiteText(Context ctx, int samesite)
	{
		switch (samesite)
		{
			case 0 :
				return getLocalizedString(ctx, "cookie_samesite_none");
			case 1 :
				return getLocalizedString(ctx, "cookie_samesite_lax");
			case 2 :
				return getLocalizedString(ctx, "cookie_samesite_strict");
			default :
				return String.format(getLocalizedString(ctx, "cookie_samesite_unknown"), samesite);
		}
	}
	private String getSourceTypeText(Context ctx, int sourceType)
	{
		switch (sourceType)
		{
			case 0 :
				return getLocalizedString(ctx, "cookie_source_type_none");
			case 1 :
				return getLocalizedString(ctx, "cookie_source_type_http");
			case 2 :
				return getLocalizedString(ctx, "cookie_source_type_https");
			case 3 :
				return getLocalizedString(ctx, "cookie_source_type_file");
			default :
				return String.format(getLocalizedString(ctx, "cookie_source_type_unknown"), sourceType);
		}
	}
	private EditText addEditableField(LinearLayout parent, Context ctx, String label, String value)
	{
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 12));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(Color.BLACK);
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
																			  ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		final EditText editText = new EditText(ctx);
		editText.setText(value != null ? value : "");
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		editText.setTextColor(Color.BLACK);
		editText.setBackground(getRoundBg(ctx, 0xFFF0F0F0, 4));
		editText.setPadding(dp(ctx, 8), dp(ctx, 6), dp(ctx, 8), dp(ctx, 6));
		editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(editText);
		parent.addView(rowLayout);
		return editText;
	}
	private void addReadOnlyField(LinearLayout parent, Context ctx, String label, String value)
	{
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 8));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(0xFF666666);
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
																			  ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		TextView valueView = new TextView(ctx);
		valueView.setText(value != null ? value : "N/A");
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(0xFF666666);
		valueView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(valueView);
		parent.addView(rowLayout);
	}
	private CheckBox addCheckboxField(LinearLayout parent, Context ctx, String label, boolean checked)
	{
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setPadding(0, 0, 0, dp(ctx, 12));
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		labelView.setTextColor(Color.BLACK);
		labelView.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dp(ctx, 120),
																			  ViewGroup.LayoutParams.WRAP_CONTENT);
		rowLayout.addView(labelView, labelParams);
		final CheckBox checkBox = new CheckBox(ctx);
		checkBox.setChecked(checked);
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		checkBox.setTextColor(Color.BLACK);
		checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		rowLayout.addView(checkBox);
		parent.addView(rowLayout);
		return checkBox;
	}
	private void updateCookieInDatabase(Context ctx, CookieItem cookie)
	{
		SQLiteDatabase db = null;
		try
		{
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
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 更新Cookie失败: " + e);
		}
		finally
		{
			if (db != null)
			{
				db.close();
			}
		}
	}
	private void populateCookieList(final Activity act, final LinearLayout container, List<CookieItem> cookieItems,
									final Button deleteButton, final ScrollView scrollView)
	{
		container.removeAllViews();
		if (cookieItems.isEmpty())
		{
			TextView emptyText = new TextView(act);
			emptyText.setText(getLocalizedString(act, "cookie_manager_empty"));
			emptyText.setTextColor(0xFF888888);
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < cookieItems.size(); i++)
		{
			final CookieItem item = cookieItems.get(i);
			item.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, 0xFFF8F9FA, 6));
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
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			String domain = item.host_key != null && !item.host_key.isEmpty()
				? item.host_key
				: getLocalizedString(act, "cookie_unknown_domain");
			domainText.setText(domain);
			domainText.setTextColor(Color.BLACK);
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
			String nameLabel = getLocalizedString(act, "cookie_field_name_label");
			String nameValue = item.name != null && !item.name.isEmpty()
				? item.name
				: getLocalizedString(act, "cookie_field_unknown");
			nameText.setText(nameLabel + nameValue);
			nameText.setTextColor(0xFF666666);
			nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			secondRow.addView(nameText);
			TextView valueText = new TextView(act);
			String valueLabel = getLocalizedString(act, "cookie_field_value_label");
			String valueRaw = item.value != null && !item.value.isEmpty()
				? item.value
				: getLocalizedString(act, "cookie_no_value");
			String valueDisplay = valueRaw.length() > 30 ? valueRaw.substring(0, 30) + "..." : valueRaw;
			valueText.setText(valueLabel + valueDisplay);
			valueText.setTextColor(0xFF666666);
			valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			valueText.setPadding(0, dp(act, 2), 0, 0);
			secondRow.addView(valueText);
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						showCookieDetailDialog(act, item);
					}
				});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v)
					{
						item.selected = !item.selected;
						selectCheckbox.setChecked(item.selected);
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof CookieItem)
							{
								if (((CookieItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
						return true;
					}
				});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						item.selected = selectCheckbox.isChecked();
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof CookieItem)
							{
								if (((CookieItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
					}
				});
		}
		scrollView.post(new Runnable() {
				@Override
				public void run()
				{
					scrollView.scrollTo(0, 0);
				}
			});
	}
	private void populateDomainList(final Activity act, final LinearLayout container, List<DomainItem> domainItems,
									final Button deleteButton, final ScrollView scrollView, final Context ctx)
	{
		container.removeAllViews();
		if (domainItems.isEmpty())
		{
			TextView emptyText = new TextView(act);
			emptyText.setText(getLocalizedString(act, "cookie_manager_empty"));
			emptyText.setTextColor(0xFF888888);
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < domainItems.size(); i++)
		{
			final DomainItem domainItem = domainItems.get(i);
			domainItem.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, 0xFFF8F9FA, 6));
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
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			domainText.setText(domainItem.domain);
			domainText.setTextColor(Color.BLACK);
			domainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			domainText.setTypeface(null, Typeface.BOLD);
			domainText.setEllipsize(TextUtils.TruncateAt.END);
			domainText.setSingleLine(true);
			domainText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
			firstRow.addView(domainText);
			TextView countText = new TextView(act);
			String countLabel = getLocalizedString(act, "cookie_domain_count_label");
			countText.setText(String.format(countLabel, domainItem.getCookieCount()));
			countText.setTextColor(0xFF666666);
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
			for (int j = 0; j < previewCount; j++)
			{
				CookieItem cookie = domainItem.cookies.get(j);
				TextView cookiePreview = new TextView(act);
				String cookieName = cookie.name != null && !cookie.name.isEmpty()
					? cookie.name
					: getLocalizedString(act, "cookie_field_unknown");
				String cookieValue = cookie.value != null && !cookie.value.isEmpty()
					? cookie.value
					: getLocalizedString(act, "cookie_no_value");
				String valueDisplay = cookieValue.length() > 20 ? cookieValue.substring(0, 20) + "..." : cookieValue;
				cookiePreview.setText("• " + cookieName + ": " + valueDisplay);
				cookiePreview.setTextColor(0xFF666666);
				cookiePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
				secondRow.addView(cookiePreview);
			}
			if (domainItem.cookies.size() > 3)
			{
				TextView moreText = new TextView(act);
				int moreCount = domainItem.cookies.size() - 3;
				String moreLabel = getLocalizedString(act, "cookie_domain_more_label");
				moreText.setText(String.format(moreLabel, moreCount));
				moreText.setTextColor(0xFF999999);
				moreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				moreText.setPadding(0, dp(act, 2), 0, 0);
				secondRow.addView(moreText);
			}
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						showDomainCookieList(act, domainItem, ctx);
					}
				});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v)
					{
						domainItem.selected = !domainItem.selected;
						selectCheckbox.setChecked(domainItem.selected);
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof DomainItem)
							{
								if (((DomainItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
						return true;
					}
				});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						domainItem.selected = selectCheckbox.isChecked();
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof DomainItem)
							{
								if (((DomainItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
					}
				});
		}
		scrollView.post(new Runnable() {
				@Override
				public void run()
				{
					scrollView.scrollTo(0, 0);
				}
			});
	}
	private void populateDomainList(final Activity act, final LinearLayout container, List<DomainItem> domainItems,
									final Button deleteButton, final ScrollView scrollView, final Context ctx,
									final List<DomainItem> masterDomainList)
	{
		container.removeAllViews();
		if (domainItems.isEmpty())
		{
			TextView emptyText = new TextView(act);
			emptyText.setText(getLocalizedString(act, "cookie_manager_empty"));
			emptyText.setTextColor(0xFF888888);
			emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			emptyText.setGravity(Gravity.CENTER);
			emptyText.setPadding(0, dp(act, 32), 0, dp(act, 32));
			container.addView(emptyText);
			deleteButton.setEnabled(false);
			return;
		}
		final int[] selectedCount = {0};
		for (int i = 0; i < domainItems.size(); i++)
		{
			final DomainItem domainItem = domainItems.get(i);
			domainItem.selected = false;
			LinearLayout itemLayout = new LinearLayout(act);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setBackground(getRoundBg(act, 0xFFF8F9FA, 6));
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
			firstRow.addView(selectCheckbox);
			TextView domainText = new TextView(act);
			domainText.setText(domainItem.domain);
			domainText.setTextColor(Color.BLACK);
			domainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			domainText.setTypeface(null, Typeface.BOLD);
			domainText.setEllipsize(TextUtils.TruncateAt.END);
			domainText.setSingleLine(true);
			domainText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
			firstRow.addView(domainText);
			TextView countText = new TextView(act);
			String countLabel = getLocalizedString(act, "cookie_domain_count_label");
			countText.setText(String.format(countLabel, domainItem.getCookieCount()));
			countText.setTextColor(0xFF666666);
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
			for (int j = 0; j < previewCount; j++)
			{
				CookieItem cookie = domainItem.cookies.get(j);
				TextView cookiePreview = new TextView(act);
				String cookieName = cookie.name != null && !cookie.name.isEmpty()
					? cookie.name
					: getLocalizedString(act, "cookie_field_unknown");
				String cookieValue = cookie.value != null && !cookie.value.isEmpty()
					? cookie.value
					: getLocalizedString(act, "cookie_no_value");
				String valueDisplay = cookieValue.length() > 20 ? cookieValue.substring(0, 20) + "..." : cookieValue;
				cookiePreview.setText("• " + cookieName + ": " + valueDisplay);
				cookiePreview.setTextColor(0xFF666666);
				cookiePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
				secondRow.addView(cookiePreview);
			}
			if (domainItem.cookies.size() > 3)
			{
				TextView moreText = new TextView(act);
				int moreCount = domainItem.cookies.size() - 3;
				String moreLabel = getLocalizedString(act, "cookie_domain_more_label");
				moreText.setText(String.format(moreLabel, moreCount));
				moreText.setTextColor(0xFF999999);
				moreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				moreText.setPadding(0, dp(act, 2), 0, 0);
				secondRow.addView(moreText);
			}
			itemLayout.addView(secondRow);
			itemLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						showDomainCookieList(act, domainItem, ctx, masterDomainList);
					}
				});
			itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v)
					{
						domainItem.selected = !domainItem.selected;
						selectCheckbox.setChecked(domainItem.selected);
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof DomainItem)
							{
								if (((DomainItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
						return true;
					}
				});
			selectCheckbox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						domainItem.selected = selectCheckbox.isChecked();
						int count = 0;
						for (int j = 0; j < container.getChildCount(); j++)
						{
							View child = container.getChildAt(j);
							if (child.getTag() instanceof DomainItem)
							{
								if (((DomainItem) child.getTag()).selected)
								{
									count++;
								}
							}
						}
						deleteButton.setEnabled(count > 0);
						deleteButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
					}
				});
		}
		scrollView.post(new Runnable() {
				@Override
				public void run()
				{
					scrollView.scrollTo(0, 0);
				}
			});
	}
	private void showDomainCookieList(final Activity act, final DomainItem domainItem, final Context ctx)
	{
		showDomainCookieList(act, domainItem, ctx, null);
	}
	private void showDomainCookieList(final Activity act, final DomainItem domainItem, final Context ctx,
									  final List<DomainItem> masterDomainList)
	{
		if (act.isFinishing() || act.isDestroyed())
			return;
		final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
		dialog.setCancelable(true);
		LinearLayout rootLayout = new LinearLayout(act);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setBackgroundColor(Color.WHITE);
		RelativeLayout titleBar = new RelativeLayout(act);
		titleBar.setBackgroundColor(0xFFF5F5F5);
		titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
		titleBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
															   ViewGroup.LayoutParams.WRAP_CONTENT));
		ImageButton backButton = new ImageButton(act);
		backButton.setImageResource(android.R.drawable.ic_menu_revert);
		backButton.setBackgroundResource(android.R.color.transparent);
		backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
		backButton.setColorFilter(0xFF000000);
		RelativeLayout.LayoutParams backButtonLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
		backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
		titleBar.addView(backButton, backButtonLp);
		TextView title = new TextView(act);
		title.setText(domainItem.domain);
		title.setTextColor(Color.BLACK);
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
		domainInfoLayout.setBackground(getRoundBg(act, 0xFFF0F0F0, 6));
		domainInfoLayout.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
		domainInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																	   ViewGroup.LayoutParams.WRAP_CONTENT));
		TextView domainInfoText = new TextView(act);
		String cookieCountLabel = getLocalizedString(act, "cookie_domain_total_count");
		domainInfoText.setText(String.format(cookieCountLabel, domainItem.getCookieCount()));
		domainInfoText.setTextColor(0xFF666666);
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
		final Button deleteDomainButton = new Button(act);
		deleteDomainButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
		deleteDomainButton.setTextColor(Color.WHITE);
		deleteDomainButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		deleteDomainButton.setBackground(getRoundBg(act, 0xFFE53935, 8));
		deleteDomainButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		deleteDomainButton.setEnabled(false);
		LinearLayout.LayoutParams deleteDomainLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																				 1.0f);
		deleteDomainLp.rightMargin = dp(act, 6);
		buttonBar.addView(deleteDomainButton, deleteDomainLp);
		final Button selectAllDomainButton = new Button(act);
		selectAllDomainButton.setText(getLocalizedString(act, "cookie_manager_select_all"));
		selectAllDomainButton.setTextColor(0xFF6200EE);
		selectAllDomainButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		selectAllDomainButton.setBackground(getRoundBg(act, 0xFFF0F0F0, 8));
		selectAllDomainButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		LinearLayout.LayoutParams selectAllDomainLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
																					1.0f);
		selectAllDomainLp.leftMargin = dp(act, 6);
		selectAllDomainLp.rightMargin = dp(act, 6);
		buttonBar.addView(selectAllDomainButton, selectAllDomainLp);
		Button closeButton = new Button(act);
		closeButton.setText(getLocalizedString(act, "dialog_close"));
		closeButton.setTextColor(Color.WHITE);
		closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		closeButton.setBackground(getRoundBg(act, 0xFF6200EE, 8));
		closeButton.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
		LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
		closeLp.leftMargin = dp(act, 6);
		buttonBar.addView(closeButton, closeLp);
		contentLayout.addView(buttonBar);
		rootLayout.addView(contentLayout);
		dialog.setContentView(rootLayout);
		final boolean[] isDomainAllSelected = {false};
		populateCookieList(act, listContainer, domainItem.cookies, deleteDomainButton, scrollView);
		backButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
				}
			});
		deleteDomainButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle(getLocalizedString(act, "cookie_delete_confirm_title"));
					builder.setMessage(getLocalizedString(act, "cookie_delete_confirm_msg"));
					builder.setPositiveButton(getLocalizedString(act, "cookie_manager_delete_btn"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, int which)
							{
								deleteSelectedCookies(act, listContainer, deleteDomainButton, scrollView,
									new Runnable() {
										@Override
										public void run()
										{
											List<CookieItem> refreshedCookies = new ArrayList<>();
											SQLiteDatabase db = null;
											Cursor cursor = null;
											try
											{
												String cookiePath = getCookieFilePath(act);
												db = SQLiteDatabase.openDatabase(cookiePath, null,
																				 SQLiteDatabase.OPEN_READONLY);
												String selection = "host_key = ?";
												String[] selectionArgs = {domainItem.domain};
												cursor = db.query(COOKIE_TABLE_NAME, null, selection, selectionArgs,
																  null, null, "name");
												if (cursor != null && cursor.moveToFirst())
												{
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
											}
											catch (Exception e)
											{
												XposedBridge.log("[BetterVia] 重新加载Cookie数据失败: " + e);
											}
											finally
											{
												if (cursor != null)
												{
													cursor.close();
												}
												if (db != null)
												{
													db.close();
												}
											}
											domainItem.cookies.clear();
											domainItem.cookies.addAll(refreshedCookies);
											if (masterDomainList != null)
											{
												for (DomainItem masterDomainItem : masterDomainList)
												{
													if (masterDomainItem.domain.equals(domainItem.domain))
													{
														masterDomainItem.cookies.clear();
														masterDomainItem.cookies.addAll(refreshedCookies);
														break;
													}
												}
											}
											populateCookieList(act, listContainer, domainItem.cookies,
															   deleteDomainButton, scrollView);
											if (domainItem.cookies.isEmpty())
											{
												dialog.dismiss();
												Toast.makeText(act,
															   String.format(
																   getLocalizedString(act,
																					  "cookie_domain_delete_success"),
																   domainItem.domain, 0),
															   Toast.LENGTH_SHORT).show();
											}
										}
									});
							}
						});
					builder.setNegativeButton(getLocalizedString(act, "dialog_cancel"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
							}
						});
					builder.show();
				}
			});
		selectAllDomainButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					isDomainAllSelected[0] = !isDomainAllSelected[0];
					if (isDomainAllSelected[0])
					{
						selectAllDomainButton.setText(getLocalizedString(act, "cookie_manager_unselect_all"));
						Toast.makeText(act, getLocalizedString(act, "cookie_manager_selecting"), Toast.LENGTH_SHORT).show();
						for (CookieItem item : domainItem.cookies)
						{
							item.selected = true;
						}
						new Thread(new Runnable() {
								@Override
								public void run()
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												for (int i = 0; i < listContainer.getChildCount(); i++)
												{
													View child = listContainer.getChildAt(i);
													if (child instanceof LinearLayout)
													{
														LinearLayout itemLayout = (LinearLayout) child;
														View firstChild = itemLayout.getChildAt(0);
														if (firstChild instanceof LinearLayout)
														{
															CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild).getChildAt(0);
															if (checkbox != null)
															{
																checkbox.setChecked(true);
															}
															if (itemLayout.getTag() instanceof CookieItem)
															{
																((CookieItem) itemLayout.getTag()).selected = true;
															}
														}
													}
												}
												deleteDomainButton.setEnabled(true);
												deleteDomainButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
											}
										});
								}
							}).start();
					}
					else
					{
						selectAllDomainButton.setText(getLocalizedString(act, "cookie_manager_select_all"));
						Toast.makeText(act, getLocalizedString(act, "cookie_manager_unselecting"), Toast.LENGTH_SHORT).show();
						for (CookieItem item : domainItem.cookies)
						{
							item.selected = false;
						}
						new Thread(new Runnable() {
								@Override
								public void run()
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												for (int i = 0; i < listContainer.getChildCount(); i++)
												{
													View child = listContainer.getChildAt(i);
													if (child instanceof LinearLayout)
													{
														LinearLayout itemLayout = (LinearLayout) child;
														View firstChild = itemLayout.getChildAt(0);
														if (firstChild instanceof LinearLayout)
														{
															CheckBox checkbox = (CheckBox) ((LinearLayout) firstChild).getChildAt(0);
															if (checkbox != null)
															{
																checkbox.setChecked(false);
															}
															if (itemLayout.getTag() instanceof CookieItem)
															{
																((CookieItem) itemLayout.getTag()).selected = false;
															}
														}
													}
												}
												deleteDomainButton.setEnabled(false);
												deleteDomainButton.setText(getLocalizedString(act, "cookie_manager_delete_selected"));
											}
										});
								}
							}).start();
					}
				}
			});
		closeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
				}
			});
		dialog.show();
	}
	private void showDeleteConfirmDialog(final Activity act, final Context ctx, final LinearLayout listContainer,
										 final Button deleteButton, final ScrollView scrollView, final boolean isDomainView)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle(getLocalizedString(ctx, "cookie_delete_confirm_title"));
		String confirmMsg;
		if (isDomainView)
		{
			confirmMsg = getLocalizedString(ctx, "cookie_domain_delete_selected_confirm_msg");
		}
		else
		{
			confirmMsg = getLocalizedString(ctx, "cookie_delete_confirm_msg");
		}
		builder.setMessage(confirmMsg);
		builder.setPositiveButton(getLocalizedString(ctx, "cookie_manager_delete_btn"),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (isDomainView)
					{
						deleteSelectedDomains(act, listContainer, deleteButton, scrollView);
					}
					else
					{
						deleteSelectedCookies(act, listContainer, deleteButton, scrollView);
					}
				}
			});
		builder.setNegativeButton(getLocalizedString(ctx, "dialog_cancel"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
		builder.show();
	}
	private void deleteSelectedCookies(final Activity act, final LinearLayout listContainer, final Button deleteButton,
									   final ScrollView scrollView)
	{
		deleteSelectedCookies(act, listContainer, deleteButton, scrollView, null);
	}
	private void deleteSelectedCookies(final Activity act, final LinearLayout listContainer, final Button deleteButton,
									   final ScrollView scrollView, final Runnable onCompleteCallback)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					SQLiteDatabase db = null;
					final List<CookieItem> deletedItems = new ArrayList<>();
					try
					{
						String cookiePath = getCookieFilePath(act);
						db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READWRITE);
						db.beginTransaction();
						try
						{
							for (int i = 0; i < listContainer.getChildCount(); i++)
							{
								View child = listContainer.getChildAt(i);
								if (child instanceof LinearLayout && child.getTag() instanceof CookieItem)
								{
									CookieItem item = (CookieItem) child.getTag();
									if (item.selected)
									{
										String whereClause = "creation_utc = ? AND host_key = ? AND name = ?";
										String[] whereArgs = {String.valueOf(item.creation_utc), item.host_key, item.name};
										db.delete(COOKIE_TABLE_NAME, whereClause, whereArgs);
										deletedItems.add(item);
									}
								}
							}
							db.setTransactionSuccessful();
						}
						finally
						{
							db.endTransaction();
						}
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 批量删除Cookie失败: " + e);
					}
					finally
					{
						if (db != null)
						{
							db.close();
						}
					}
					final int finalCount = deletedItems.size();
					act.runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								if (finalCount > 0)
								{
									for (CookieItem deletedItem : deletedItems)
									{
										removeDeletedCookieFromList(act, listContainer, deleteButton, scrollView, deletedItem);
									}
									Toast.makeText(act, getLocalizedString(act, "cookie_delete_success"), Toast.LENGTH_SHORT)
										.show();
								}
								else
								{
									Toast.makeText(act, getLocalizedString(act, "cookie_delete_no_selected"),
												   Toast.LENGTH_SHORT).show();
								}
								if (onCompleteCallback != null)
								{
									onCompleteCallback.run();
								}
							}
						});
				}
			}).start();
	}
	private void deleteSelectedDomains(final Activity act, final LinearLayout listContainer, final Button deleteButton,
									   final ScrollView scrollView)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					SQLiteDatabase db = null;
					final List<DomainItem> deletedDomains = new ArrayList<>();
					final List<CookieItem> deletedCookies = new ArrayList<>();
					try
					{
						String cookiePath = getCookieFilePath(act);
						db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READWRITE);
						db.beginTransaction();
						try
						{
							for (int i = 0; i < listContainer.getChildCount(); i++)
							{
								View child = listContainer.getChildAt(i);
								if (child instanceof LinearLayout && child.getTag() instanceof DomainItem)
								{
									DomainItem domainItem = (DomainItem) child.getTag();
									if (domainItem.selected)
									{
										for (CookieItem cookie : domainItem.cookies)
										{
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
						}
						finally
						{
							db.endTransaction();
						}
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 批量删除域名Cookie失败: " + e);
					}
					finally
					{
						if (db != null)
						{
							db.close();
						}
					}
					final int finalDomainCount = deletedDomains.size();
					final int finalCookieCount = deletedCookies.size();
					act.runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								if (finalDomainCount > 0)
								{
									for (int i = listContainer.getChildCount() - 1; i >= 0; i--)
									{
										View child = listContainer.getChildAt(i);
										if (child instanceof LinearLayout && child.getTag() instanceof DomainItem)
										{
											DomainItem domainItem = (DomainItem) child.getTag();
											if (domainItem.selected)
											{
												listContainer.removeViewAt(i);
											}
										}
									}
									updateDeleteButtonState(act, listContainer, deleteButton);
									String successMsg = String.format(
										getLocalizedString(act, "cookie_domain_delete_selected_success"), finalDomainCount,
										finalCookieCount);
									Toast.makeText(act, successMsg, Toast.LENGTH_SHORT).show();
									if (listContainer.getChildCount() == 0)
									{
										showEmptyCookieListState(act, listContainer);
									}
								}
								else
								{
									Toast.makeText(act, getLocalizedString(act, "cookie_delete_no_selected"),
												   Toast.LENGTH_SHORT).show();
								}
							}
						});
				}
			}).start();
	}
	private static Map<String, EditorState> editorStateCache = new HashMap<>();
	private static class EditorState
	{
		String content;
		int scrollY;
		long timestamp;
		EditorState(String content, int scrollY)
		{
			this.content = content;
			this.scrollY = scrollY;
			this.timestamp = System.currentTimeMillis();
		}
	}
	private void loadFileContent(final Context ctx, final String fileName, final EditText editor,
								 final ScrollView scrollView, final boolean fromCache)
	{
		if (fromCache && editorStateCache.containsKey(fileName))
		{
			final EditorState state = editorStateCache.get(fileName);
			editor.setText(state.content);
			editor.post(new Runnable() {
					@Override
					public void run()
					{
						if (scrollView != null)
						{
							scrollView.scrollTo(0, state.scrollY);
						}
					}
				});
			return;
		}
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						String filePath = "/data/user/0/" + ctx.getPackageName() + "/files/" + fileName;
						File file = new File(filePath);
						final StringBuilder content = new StringBuilder();
						if (file.exists())
						{
							BufferedReader reader = new BufferedReader(new FileReader(file));
							String line;
							while ((line = reader.readLine()) != null)
							{
								content.append(line).append("\n");
							}
							reader.close();
						}
						else
						{
							content.append("
						}
						((Activity) ctx).runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									editor.setText(content.toString());
									editorStateCache.put(fileName, new EditorState(content.toString(), 0));
								}
							});
					}
					catch (final Exception e)
					{
						((Activity) ctx).runOnUiThread(new Runnable() {
								@Override
								public void run()
								{
									editor.setText("加载失败: " + e.getMessage());
								}
							});
					}
				}
			}).start();
	}
	private void saveCurrentEditorState(String fileName, EditText editor, ScrollView scrollView)
	{
		if (editor != null && scrollView != null)
		{
			String content = editor.getText().toString();
			int scrollY = scrollView.getScrollY();
			editorStateCache.put(fileName, new EditorState(content, scrollY));
		}
	}
	private void saveFileContent(final Context ctx, final String fileName, final String content)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						String filePath = "/data/user/0/" + ctx.getPackageName() + "/files/" + fileName;
						File file = new File(filePath);
						FileWriter writer = new FileWriter(file);
						writer.write(content);
						writer.close();
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 保存文件失败: " + e);
					}
				}
			}).start();
	}
	private List<CookieItem> loadCookieData(Context ctx)
	{
		List<CookieItem> cookieItems = new ArrayList<CookieItem>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			String cookiePath = getCookieFilePath(ctx);
			File cookieFile = new File(cookiePath);
			if (!cookieFile.exists())
			{
				return cookieItems;
			}
			db = SQLiteDatabase.openDatabase(cookiePath, null, SQLiteDatabase.OPEN_READONLY);
			Cursor tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
											 new String[]{COOKIE_TABLE_NAME});
			if (!tableCursor.moveToFirst())
			{
				tableCursor.close();
				return cookieItems;
			}
			tableCursor.close();
			cursor = db.query(COOKIE_TABLE_NAME, null, null, null, null, null, "host_key, name");
			if (cursor != null && cursor.moveToFirst())
			{
				do {
					try
					{
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
					}
					catch (Exception e)
					{
					}
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 读取Cookie数据失败: " + e);
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
			if (db != null)
			{
				db.close();
			}
		}
		return cookieItems;
	}
	private List<DomainItem> loadDomainGroupedCookieData(Context ctx)
	{
		List<DomainItem> domainItems = new ArrayList<>();
		Map<String, DomainItem> domainMap = new HashMap<>();
		List<CookieItem> cookieItems = loadCookieData(ctx);
		for (CookieItem cookie : cookieItems)
		{
			String domain = cookie.host_key;
			if (domain == null || domain.isEmpty())
			{
				domain = getLocalizedString(ctx, "cookie_unknown_domain");
			}
			DomainItem domainItem = domainMap.get(domain);
			if (domainItem == null)
			{
				domainItem = new DomainItem(domain);
				domainMap.put(domain, domainItem);
				domainItems.add(domainItem);
			}
			domainItem.addCookie(cookie);
		}
		Collections.sort(domainItems, new Comparator<DomainItem>() {
				@Override
				public int compare(DomainItem d1, DomainItem d2)
				{
					return d1.domain.compareToIgnoreCase(d2.domain);
				}
			});
		return domainItems;
	}
	private String getStringSafe(Cursor cursor, String columnName)
	{
		try
		{
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return "";
			return cursor.getString(columnIndex);
		}
		catch (Exception e)
		{
			return "";
		}
	}
	private long getLongSafe(Cursor cursor, String columnName)
	{
		try
		{
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return 0;
			return cursor.getLong(columnIndex);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	private int getIntSafe(Cursor cursor, String columnName)
	{
		try
		{
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex == -1)
				return 0;
			return cursor.getInt(columnIndex);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	private static class CookieItem
	{
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
		CookieItem()
		{
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
	private static class DomainItem
	{
		String domain;
		List<CookieItem> cookies;
		boolean selected;
		DomainItem(String domain)
		{
			this.domain = domain;
			this.cookies = new ArrayList<>();
			this.selected = false;
		}
		void addCookie(CookieItem cookie)
		{
			cookies.add(cookie);
		}
		int getCookieCount()
		{
			return cookies.size();
		}
	}
	private String getCookieFilePath(Context ctx)
	{
		String packageName = ctx.getPackageName();
		return "/data/user/0/" + packageName + "/app_webview/Default/Cookies";
	}
	private void addImagePickerItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "homepage_bg_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "homepage_bg_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showHomepageBeautyDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "homepage_bg_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showHomepageBeautyDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					homepageBgPath = getPrefString(ctx, KEY_HOMEPAGE_BG, "");
					homepageMaskAlpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
					int savedRgb = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x000000);
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					GradientDrawable bg = new GradientDrawable();
					bg.setColor(Color.WHITE);
					bg.setCornerRadius(dp(act, 24));
					root.setBackground(bg);
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "homepage_bg_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(Color.BLACK);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "homepage_bg_dialog_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
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
					preBg.setStroke(dp(act, 1), 0xFFE0E0E0);
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
					pickBtn.setText(getLocalizedString(ctx, "homepage_bg_pick_btn"));
					pickBtn.setTextColor(Color.WHITE);
					pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					pickBtn.setTypeface(null, Typeface.BOLD);
					pickBtn.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					root.addView(pickBtn);
					TextView alphaTitle = new TextView(act);
					alphaTitle.setText(getLocalizedString(ctx, "homepage_bg_mask_alpha"));
					alphaTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					alphaTitle.setTextColor(Color.BLACK);
					alphaTitle.setTypeface(null, Typeface.BOLD);
					alphaTitle.setPadding(0, dp(act, 16), 0, 0);
					root.addView(alphaTitle);
					final SeekBar alphaSeek = new SeekBar(act);
					alphaSeek.setMax(255);
					alphaSeek.setProgress(homepageMaskAlpha);
					root.addView(alphaSeek);
					TextView colorTitle = new TextView(act);
					colorTitle.setText(getLocalizedString(ctx, "homepage_bg_mask_color_rgb"));
					colorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					colorTitle.setTextColor(Color.BLACK);
					colorTitle.setTypeface(null, Typeface.BOLD);
					colorTitle.setPadding(0, dp(act, 12), 0, 0);
					root.addView(colorTitle);
					LinearLayout rgbContainer = new LinearLayout(act);
					rgbContainer.setOrientation(LinearLayout.VERTICAL);
					final EditText rgbEdit = new EditText(act);
					rgbEdit.setHint("#RRGGBB");
					rgbEdit.setText(colorToRgbString(savedRgb)); 
					rgbEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					rgbEdit.setBackground(getRoundBg(act, 0xFFF5F5F5, 4));
					rgbEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
					LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					 ViewGroup.LayoutParams.WRAP_CONTENT);
					rgbContainer.addView(rgbEdit, editLp);
					TextView rgbHint = new TextView(act);
					rgbHint.setText(getLocalizedString(ctx, "homepage_bg_mask_color_hint"));
					rgbHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					rgbHint.setTextColor(0xFF888888);
					rgbHint.setPadding(dp(act, 4), dp(act, 4), 0, 0);
					rgbContainer.addView(rgbHint);
					root.addView(rgbContainer);
					SeekBar.OnSeekBarChangeListener alphaListener = new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
						{
							if (fromUser)
							{
								homepageMaskAlpha = progress;
								String rgbStr = rgbEdit.getText().toString();
								int rgbColor = parseRgbColor(rgbStr, 0); 
								refreshPreview(ctx, imageView, maskView, homepageMaskAlpha, rgbColor);
							}
						}
						@Override
						public void onStartTrackingTouch(SeekBar seekBar)
						{
						}
						@Override
						public void onStopTrackingTouch(SeekBar seekBar)
						{
						}
					};
					alphaSeek.setOnSeekBarChangeListener(alphaListener);
					rgbEdit.addTextChangedListener(new TextWatcher() {
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after)
							{
							}
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count)
							{
							}
							@Override
							public void afterTextChanged(Editable s)
							{
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
					cancel.setText(getLocalizedString(ctx, "dialog_cancel"));
					cancel.setTextColor(0xFF6200EE);
					cancel.setBackground(getRoundBg(act, 0xFFE0E0E0, 12));
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
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
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						GradientDrawable round = new GradientDrawable();
						round.setColor(Color.WHITE);
						round.setCornerRadius(dp(act, 24));
						win.setBackgroundDrawable(round);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					cancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								String rgbStr = rgbEdit.getText().toString();
								int rgbColor = parseRgbColor(rgbStr, 0);
								if (rgbStr.trim().length() > 0 && !isValidRgbColor(rgbStr))
								{
									Toast.makeText(ctx, getLocalizedString(ctx, "homepage_bg_mask_color_invalid"),
												   Toast.LENGTH_SHORT).show();
									return; 
								}
								putPrefInt(ctx, KEY_HOMEPAGE_MASK_A, homepageMaskAlpha);
								putPrefInt(ctx, KEY_HOMEPAGE_MASK_C, rgbColor); 
								Toast.makeText(ctx, getLocalizedString(ctx, "homepage_bg_saved"), Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							}
						});
					pickBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								Intent i = new Intent(Intent.ACTION_GET_CONTENT);
								i.setType("image
	private boolean isValidRgbColor(String rgbStr)
	{
		if (rgbStr == null || rgbStr.trim().isEmpty())
		{
			return true; 
		}
		String colorStr = rgbStr.trim();
		if (!colorStr.startsWith("#"))
		{
			colorStr = "#" + colorStr;
		}
		return colorStr.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
	}
	private void refreshPreview(Context ctx, ImageView iv, View mask, int alpha, int rgbColor)
	{
		if (homepageBgPath != null && new File(homepageBgPath).exists())
		{
			Bitmap bmp = BitmapFactory.decodeFile(homepageBgPath);
			if (bmp != null)
			{
				iv.setImageBitmap(bmp);
			}
			else
			{
				iv.setBackgroundColor(0xFFD0D0D0);
				iv.setImageBitmap(null);
			}
		}
		else
		{
			iv.setBackgroundColor(0xFFD0D0D0);
			iv.setImageBitmap(null);
		}
		int finalColor = (alpha << 24) | (rgbColor & 0x00FFFFFF);
		mask.setBackgroundColor(finalColor);
	}
	private void hookHomepageBgWithMask(final Context ctx, ClassLoader cl, final String imgPath, final int maskColor)
	{
		XposedHelpers.findAndHookMethod("k.a.c0.l.c", cl, "g", Context.class, List.class, boolean.class,
			new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					String uri = (String) param.getResult();
					File html = new File(Uri.parse(uri).getPath());
					if (!html.exists())
						return;
					StringBuilder htmlSb = new StringBuilder();
					BufferedReader br = null;
					try
					{
						br = new BufferedReader(new FileReader(html));
						String line;
						while ((line = br.readLine()) != null)
							htmlSb.append(line).append("\n");
					}
					finally
					{
						if (br != null)
							try
							{
								br.close();
							}
							catch (Exception ignored)
							{
							}
					}
					String originalHtml = htmlSb.toString();
					int alpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
					int rgbColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x000000);
					XposedBridge.log("[BetterVia] 读取设置透明度: " + alpha + ", 颜色值: " + Integer.toHexString(rgbColor));
					String cssColor = colorToCssString(alpha, rgbColor);
					XposedBridge.log("[BetterVia] 最终颜色值转换: " + cssColor);
					String backgroundStyle;
					if (imgPath != null && !imgPath.isEmpty() && new File(imgPath).exists())
					{
						String encodedPath = imgPath.replace("'", "\\'").replace("\\", "\\\\");
						backgroundStyle = "background:url('file:
							+ "') no-repeat center/cover fixed;";
					}
					else
					{
						backgroundStyle = "background-color:#F0F0F0;";
					}
					String maskStyle = "background:" + cssColor + ";";
					String newBodyContent = "<body style=\"" + backgroundStyle + "\">"
						+ "<div style='position:fixed;top:0;left:0;right:0;bottom:0;" + maskStyle
						+ "z-index:0;'></div>" + "<div style='position:relative;z-index:1;'>";
					String modifiedHtml = originalHtml.replace("<body>", newBodyContent).replace("</body>",
																								 "</div></body>");
					FileWriter fw = null;
					try
					{
						fw = new FileWriter(html);
						fw.write(modifiedHtml);
						XposedBridge.log("[BetterVia] 成功应用具有正确颜色格式的背景");
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 写入修改HTML时出错: " + e);
					}
					finally
					{
						if (fw != null)
							try
							{
								fw.close();
							}
							catch (Exception ignored)
							{
							}
					}
				}
			});
	}
	private void handleActivityResult(int requestCode, int resultCode, Intent data, final Activity activity)
	{
		if (resultCode != Activity.RESULT_OK || data == null)
			return;
		if (requestCode == 0x1002)
		{
			Uri uri = data.getData();
			if (saveUserImage(activity, uri))
			{
				homepageBgPath = getPrefString(activity, KEY_HOMEPAGE_BG, "");
				if (Context != null && Context instanceof Activity)
				{
					((Activity) Context).runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								Toast.makeText(activity, getLocalizedString(activity, "homepage_bg_set_ok"),
											   Toast.LENGTH_SHORT).show();
							}
						});
				}
			}
		}
	}
	private boolean saveUserImage(Activity act, Uri uri)
	{
		if (uri == null)
			return false;
		InputStream in = null;
		FileOutputStream out = null;
		try
		{
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
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 保存用户图片时出现错误: " + e);
			return false;
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (Exception ignored)
				{
				}
			if (out != null)
				try
				{
					out.close();
				}
				catch (Exception ignored)
				{
				}
		}
	}
	private String colorToCssString(int alpha, int rgbColor)
	{
		int r = (rgbColor >> 16) & 0xFF;
		int g = (rgbColor >> 8) & 0xFF;
		int b = rgbColor & 0xFF;
		float alphaFloat = alpha / 255.0f;
		return String.format(Locale.US, "rgba(%d, %d, %d, %.2f)", r, g, b, alphaFloat);
	}
	private int parseRgbColor(String rgbStr, int defaultAlpha)
	{
		if (rgbStr == null || rgbStr.trim().isEmpty())
		{
			return 0xFFFFFF; 
		}
		String colorStr = rgbStr.trim();
		if (!colorStr.startsWith("#"))
		{
			colorStr = "#" + colorStr;
		}
		try
		{
			if (colorStr.length() == 7)
			{ 
				return Color.parseColor(colorStr) & 0x00FFFFFF;
			}
		}
		catch (Exception e)
		{
		}
		return 0xFFFFFF; 
	}
	private String colorToRgbString(int color)
	{
		return String.format("#%06X", color & 0x00FFFFFF);
	}
	private void addUserAgentItem(LinearLayout parent, final Activity act, final Context ctx)
	{
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));
		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);
		TextView tv = new TextView(ctx);
		tv.setText(getLocalizedString(ctx, "user_agent_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.BLACK);
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView configBtn = new TextView(ctx);
		configBtn.setText(getLocalizedString(ctx, "user_agent_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, 0xFFE0E0E0, 8));
		configBtn.setTextColor(0xFF000000);
		configBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					showUserAgentDialog(ctx);
				}
			});
		hor.addView(configBtn);
		TextView hintTv = new TextView(ctx);
		hintTv.setText(getLocalizedString(ctx, "user_agent_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(0xFF666666);
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);
		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}
	private void showUserAgentDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(0, 0, 0, 0);
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "user_agent_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					title.setTextColor(0xFF333333);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "user_agent_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
					subtitle.setGravity(Gravity.CENTER);
					subtitle.setPadding(0, 0, 0, dp(act, 16));
					root.addView(subtitle);
					final LinearLayout uaContainer = new LinearLayout(act);
					uaContainer.setOrientation(LinearLayout.VERTICAL);
					uaContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																			  ViewGroup.LayoutParams.WRAP_CONTENT));
					root.addView(uaContainer);
					Button ok = new Button(act);
					ok.setText(getLocalizedString(ctx, "dialog_ok"));
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					ok.setPadding(0, dp(act, 14), 0, dp(act, 14));
					ok.setBackground(getRoundBg(act, 0xFF6200EE, 12));
					LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																				   ViewGroup.LayoutParams.WRAP_CONTENT);
					okLp.topMargin = dp(act, 16);
					root.addView(ok, okLp);
					scrollRoot.addView(root);
					dialogContainer.addView(scrollRoot);
					dialog.setContentView(dialogContainer);
					Window window = dialog.getWindow();
					if (window != null)
					{
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
						window.setClipToOutline(true);
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					populateUserAgentList(act, ctx, uaContainer);
					dialog.show();
				}
			});
	}
	private void populateUserAgentList(final Activity act, final Context ctx, LinearLayout container)
	{
		container.removeAllViews();
		List<UserAgentInfo> uaList = getPersonalizedUserAgents(act);
		for (final UserAgentInfo uaInfo : uaList)
		{
			LinearLayout uaItem = new LinearLayout(act);
			uaItem.setOrientation(LinearLayout.VERTICAL);
			uaItem.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
			GradientDrawable itemBg = new GradientDrawable();
			itemBg.setColor(0xFFF8F9FA);
			itemBg.setStroke(dp(act, 1), 0xFFE9ECEF);
			itemBg.setCornerRadius(dp(act, 12));
			uaItem.setBackground(itemBg);
			LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																			 ViewGroup.LayoutParams.WRAP_CONTENT);
			itemLp.bottomMargin = dp(act, 8);
			container.addView(uaItem, itemLp);
			TextView browserName = new TextView(act);
			browserName.setText(uaInfo.browserName);
			browserName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			browserName.setTextColor(Color.BLACK);
			browserName.setTypeface(null, Typeface.BOLD);
			uaItem.addView(browserName);
			final TextView uaText = new TextView(act);
			uaText.setText(uaInfo.userAgent);
			uaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			uaText.setTextColor(0xFF666666);
			uaText.setSingleLine(true);
			uaText.setEllipsize(TextUtils.TruncateAt.MIDDLE);
			uaText.setPadding(0, dp(act, 8), 0, dp(act, 8));
			uaItem.addView(uaText);
			Button copyBtn = new Button(act);
			copyBtn.setText(getLocalizedString(ctx, "user_agent_copy"));
			copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			copyBtn.setTextColor(Color.WHITE);
			copyBtn.setPadding(dp(act, 12), dp(act, 4), dp(act, 12), dp(act, 4));
			copyBtn.setMinHeight(dp(act, 28));
			GradientDrawable btnBg = new GradientDrawable();
			btnBg.setColor(0xFF3498DB);
			btnBg.setCornerRadius(dp(act, 6));
			copyBtn.setBackground(btnBg);
			LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																			ViewGroup.LayoutParams.WRAP_CONTENT);
			btnLp.gravity = Gravity.END;
			uaItem.addView(copyBtn, btnLp);
			copyBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						copyToClipboard(act, uaInfo.userAgent);
						Toast.makeText(act, getLocalizedString(ctx, "user_agent_copied"), Toast.LENGTH_SHORT).show();
					}
				});
			uaItem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						copyToClipboard(act, uaInfo.userAgent);
						Toast.makeText(act, getLocalizedString(ctx, "user_agent_copied"), Toast.LENGTH_SHORT).show();
					}
				});
		}
	}
	private static class UserAgentInfo
	{
		String browserName;
		String userAgent;
		UserAgentInfo(String browserName, String userAgent)
		{
			this.browserName = browserName;
			this.userAgent = userAgent;
		}
	}
	private List<UserAgentInfo> getPersonalizedUserAgents(Context ctx)
	{
		List<UserAgentInfo> uaList = new ArrayList<>();
		String deviceModel = Build.MODEL;
		String androidVersion = "Android " + Build.VERSION.RELEASE;
		String buildVersion = Build.DISPLAY;
		if (buildVersion == null || buildVersion.isEmpty())
		{
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
		for (String template : uaTemplates)
		{
			String[] parts = template.split(": ", 2);
			if (parts.length == 2)
			{
				String browserName = parts[0];
				String uaTemplate = parts[1];
				String personalizedUA = uaTemplate.replace("{android_version}", androidVersion)
					.replace("{device_model}", deviceModel).replace("{build_version}", buildVersion);
				uaList.add(new UserAgentInfo(browserName, personalizedUA));
			}
		}
		return uaList;
	}
	private void setDownloadDialogShareHook(Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (downloadDialogShareHook == null)
			{
				addShareButtonToDownloadDialog(ctx, cl);
				downloadDialogShareEnabled = true;
				XposedBridge.log("[BetterVia] 下载对话框分享按钮已启用");
			}
		}
		else
		{
			if (downloadDialogShareHook != null)
			{
				downloadDialogShareHook.unhook();
				downloadDialogShareHook = null;
				XposedBridge.log("[BetterVia] 下载对话框分享按钮已停用");
			}
			downloadDialogShareEnabled = false;
		}
		putPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, on);
	}
	private void addShareButtonToDownloadDialog(final Context ctx, ClassLoader cl)
	{
		try
		{
			downloadDialogShareHook = XposedHelpers.findAndHookMethod(Dialog.class, "show", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						try
						{
							final Dialog dialog = (Dialog) param.thisObject;
							if (!downloadDialogShareEnabled)
							{
								return;
							}
							if (isViaDownloadDialog(dialog))
							{
								new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
										@Override
										public void run()
										{
											try
											{
												addShareButtonToDialog(dialog, ctx);
											}
											catch (Exception e)
											{
												XposedBridge.log("[BetterVia] 添加分享按钮异常: " + e);
											}
										}
									}, 100);
							}
						}
						catch (Exception e)
						{
							XposedBridge.log("[BetterVia] Hook Dialog.show失败: " + e);
						}
					}
				});
			XposedHelpers.findAndHookMethod(AlertDialog.Builder.class, "create", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						try
						{
							final AlertDialog dialog = (AlertDialog) param.getResult();
							if (dialog == null)
								return;
							if (!downloadDialogShareEnabled)
							{
								return;
							}
							dialog.setOnShowListener(new DialogInterface.OnShowListener() {
									@Override
									public void onShow(DialogInterface dialogInterface)
									{
										try
										{
											if (isViaDownloadDialog(dialog))
											{
												addShareButtonToDialog(dialog, ctx);
											}
										}
										catch (Exception e)
										{
											XposedBridge.log("[BetterVia] AlertDialog显示监听异常: " + e);
										}
									}
								});
						}
						catch (Exception e)
						{
							XposedBridge.log("[BetterVia] Hook AlertDialog.create失败: " + e);
						}
					}
				});
			XposedBridge.log("[BetterVia] 下载对话框分享按钮Hook已启用");
		}
		catch (Throwable t)
		{
			XposedBridge.log("[BetterVia] Hook下载对话框失败: " + t);
		}
	}
	private boolean isViaDownloadDialog(Dialog dialog)
	{
		try
		{
			View copyLinkButton = dialog.findViewById(0x7f0900c7); 
			View cancelButton = dialog.findViewById(0x7f0900c6); 
			View okButton = dialog.findViewById(0x7f0900cb); 
			return copyLinkButton != null && cancelButton != null && okButton != null;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	private ViewGroup findButtonContainerRecursive(View view)
	{
		if (!(view instanceof ViewGroup))
			return null;
		ViewGroup group = (ViewGroup) view;
		int buttonCount = 0;
		for (int i = 0; i < group.getChildCount(); i++)
		{
			View child = group.getChildAt(i);
			if (child instanceof Button)
			{
				buttonCount++;
			}
		}
		if (buttonCount >= 2)
		{
			return group;
		}
		for (int i = 0; i < group.getChildCount(); i++)
		{
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup)
			{
				ViewGroup result = findButtonContainerRecursive(child);
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
	private void setupShareButtonClick(TextView shareButton, final Dialog dialog, final Context ctx)
	{
		shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					try
					{
						XposedBridge.log("[BetterVia] 分享按钮被点击");
						String[] downloadInfo = extractDownloadInfoFromDialog(dialog, ctx);
						String fileName = downloadInfo[0];
						String fileUrl = downloadInfo[1];
						String fileSize = downloadInfo[2];
						XposedBridge.log("[BetterVia] 提取到的下载信息 - 文件名: " + fileName + ", 大小: " + fileSize + ", URL: "
										 + (fileUrl.isEmpty() ? "空" : "已获取"));
						if (fileUrl.isEmpty())
						{
							Toast.makeText(ctx, "无法获取下载链接", Toast.LENGTH_SHORT).show();
							return;
						}
						String shareText = createShareText(fileName, fileSize, fileUrl, ctx);
						Intent shareIntent = new Intent(Intent.ACTION_SEND);
						shareIntent.setType("text/plain");
						shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
						Intent chooser = Intent.createChooser(shareIntent, "分享下载链接");
						chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						ctx.startActivity(chooser);
						Toast.makeText(ctx, "正在分享下载链接", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e)
					{
						XposedBridge.log("[BetterVia] 分享失败: " + e);
						Toast.makeText(ctx, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
	}
	private void insertShareButtonToContainer(ViewGroup container, TextView shareButton, Dialog dialog)
	{
		try
		{
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null)
			{
				XposedBridge.log("[BetterVia] 未找到复制链接按钮，无法确定插入位置");
				return;
			}
			View okButton = dialog.findViewById(0x7f0900cb);
			View cancelButton = dialog.findViewById(0x7f0900c6);
			int referenceMargin = dp(dialog.getContext(), 8); 
			if (okButton != null && cancelButton != null)
			{
				if (okButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
				{
					ViewGroup.MarginLayoutParams okParams = (ViewGroup.MarginLayoutParams) okButton.getLayoutParams();
					referenceMargin = okParams.rightMargin;
					XposedBridge.log("[BetterVia] 获取到确定按钮的右边距: " + referenceMargin + "px");
				}
			}
			int copyLinkIndex = -1;
			for (int i = 0; i < container.getChildCount(); i++)
			{
				if (container.getChildAt(i) == copyLinkButton)
				{
					copyLinkIndex = i;
					break;
				}
			}
			if (copyLinkIndex == -1)
			{
				XposedBridge.log("[BetterVia] 复制链接按钮不在容器中");
				return;
			}
			if (container instanceof RelativeLayout)
			{
				XposedBridge.log("[BetterVia] 检测到RelativeLayout容器，设置分享按钮在复制链接按钮右侧，间距: " + referenceMargin + "px");
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_TOP, copyLinkButton.getId());
				params.addRule(RelativeLayout.ALIGN_BOTTOM, copyLinkButton.getId());
				params.addRule(RelativeLayout.RIGHT_OF, copyLinkButton.getId());
				if (copyLinkButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
				{
					ViewGroup.MarginLayoutParams refParams = (ViewGroup.MarginLayoutParams) copyLinkButton
						.getLayoutParams();
					params.setMargins(referenceMargin, 
									  refParams.topMargin, refParams.rightMargin, refParams.bottomMargin);
					params.height = refParams.height;
				}
				else
				{
					params.setMargins(referenceMargin, 0, 0, 0);
				}
				shareButton.setLayoutParams(params);
			}
			else if (container instanceof LinearLayout)
			{
				LinearLayout.LayoutParams refParams = (LinearLayout.LayoutParams) copyLinkButton.getLayoutParams();
				LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(refParams.width, refParams.height,
																					refParams.weight);
				newParams.setMargins(refParams.leftMargin, refParams.topMargin, refParams.rightMargin,
									 refParams.bottomMargin);
				newParams.gravity = refParams.gravity;
				shareButton.setLayoutParams(newParams);
			}
			else
			{
				ViewGroup.LayoutParams refParams = copyLinkButton.getLayoutParams();
				ViewGroup.LayoutParams newParams = new ViewGroup.LayoutParams(refParams.width, refParams.height);
				shareButton.setLayoutParams(newParams);
			}
			container.addView(shareButton, copyLinkIndex + 1);
			XposedBridge.log("[BetterVia] 分享按钮已插入到位置: " + (copyLinkIndex + 1));
			container.requestLayout();
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 插入分享按钮失败: " + e);
			try
			{
				ViewGroup.LayoutParams simpleParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																				 ViewGroup.LayoutParams.WRAP_CONTENT);
				shareButton.setLayoutParams(simpleParams);
				container.addView(shareButton);
			}
			catch (Exception e2)
			{
				XposedBridge.log("[BetterVia] 备用方案也失败: " + e2);
			}
		}
	}
	private TextView createShareButton(Context ctx, Dialog dialog)
	{
		try
		{
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null)
			{
				XposedBridge.log("[BetterVia] 未找到复制链接按钮，无法获取样式");
				return null;
			}
			TextView shareButton = new TextView(ctx);
			shareButton.setId(0x7f09abcd);
			shareButton.setClickable(true);
			shareButton.setFocusable(true);
			shareButton.setText(getLocalizedString(ctx, "download_dialog_share"));
			shareButton.setTextSize(14);
			shareButton.setGravity(Gravity.CENTER);
			if (copyLinkButton instanceof TextView)
			{
				TextView refTextView = (TextView) copyLinkButton;
				shareButton.setTextColor(refTextView.getTextColors());
				shareButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, refTextView.getTextSize());
			}
			else
			{
				shareButton.setTextColor(0xFF6200EE);
			}
			Drawable refBackground = copyLinkButton.getBackground();
			if (refBackground != null)
			{
				try
				{
					Drawable backgroundCopy = refBackground.getConstantState().newDrawable().mutate();
					shareButton.setBackground(backgroundCopy);
					XposedBridge.log("[BetterVia] 已创建独立的背景Drawable");
				}
				catch (Exception e)
				{
					XposedBridge.log("[BetterVia] 创建独立Drawable失败，使用原始背景: " + e);
					shareButton.setBackground(refBackground);
				}
			}
			shareButton.setPadding(copyLinkButton.getPaddingLeft(), copyLinkButton.getPaddingTop(),
								   copyLinkButton.getPaddingRight(), copyLinkButton.getPaddingBottom());
			return shareButton;
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 创建分享按钮失败: " + e);
			return null;
		}
	}
	private void fixCopyLinkButtonBackground(Dialog dialog)
	{
		try
		{
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			if (copyLinkButton == null)
				return;
			Drawable background = copyLinkButton.getBackground();
			if (background != null)
			{
				Drawable independentBackground = background.getConstantState().newDrawable().mutate();
				copyLinkButton.setBackground(independentBackground);
				XposedBridge.log("[BetterVia] 已修复复制链接按钮的背景状态");
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 修复复制链接按钮背景失败: " + e);
		}
	}
	private void addShareButtonToDialog(final Dialog dialog, final Context ctx)
	{
		try
		{
			if (dialog.findViewById(0x7f09abcd) != null)
			{
				XposedBridge.log("[BetterVia] 分享按钮已存在，跳过添加");
				return;
			}
			fixCopyLinkButtonBackground(dialog);
			ViewGroup buttonContainer = findButtonContainer(dialog);
			if (buttonContainer == null)
			{
				XposedBridge.log("[BetterVia] 未找到按钮容器");
				return;
			}
			XposedBridge.log("[BetterVia] 找到按钮容器，类型: " + buttonContainer.getClass().getSimpleName() + ", 子视图数量: "
							 + buttonContainer.getChildCount());
			logButtonInfo(buttonContainer, "添加分享按钮前");
			TextView shareButton = createShareButton(ctx, dialog);
			if (shareButton == null)
			{
				XposedBridge.log("[BetterVia] 创建分享按钮失败");
				return;
			}
			insertShareButtonToContainer(buttonContainer, shareButton, dialog);
			setupShareButtonClick(shareButton, dialog, ctx);
			XposedBridge.log("[BetterVia] 成功添加TextView分享按钮到下载对话框");
			logButtonInfo(buttonContainer, "添加分享按钮后");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 添加分享按钮到对话框失败: " + e);
		}
	}
	private ViewGroup findButtonContainer(Dialog dialog)
	{
		try
		{
			View copyLinkButton = dialog.findViewById(0x7f0900c7);
			View cancelButton = dialog.findViewById(0x7f0900c6);
			View okButton = dialog.findViewById(0x7f0900cb);
			if (copyLinkButton != null)
			{
				ViewGroup parent = (ViewGroup) copyLinkButton.getParent();
				while (parent != null)
				{
					boolean hasCopyLink = parent.indexOfChild(copyLinkButton) >= 0;
					boolean hasCancel = parent.indexOfChild(cancelButton) >= 0;
					boolean hasOk = parent.indexOfChild(okButton) >= 0;
					if (hasCopyLink && hasCancel && hasOk)
					{
						XposedBridge.log("[BetterVia] 找到包含所有按钮的容器: " + parent.getClass().getSimpleName());
						return parent;
					}
					if (parent.getParent() instanceof ViewGroup)
					{
						parent = (ViewGroup) parent.getParent();
					}
					else
					{
						break;
					}
				}
			}
			View decorView = dialog.getWindow().getDecorView();
			return findHorizontalButtonContainer(decorView);
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 查找按钮容器失败: " + e);
			return null;
		}
	}
	private ViewGroup findHorizontalButtonContainer(View view)
	{
		if (!(view instanceof ViewGroup))
			return null;
		ViewGroup group = (ViewGroup) view;
		if (group instanceof LinearLayout)
		{
			LinearLayout layout = (LinearLayout) group;
			if (layout.getOrientation() == LinearLayout.HORIZONTAL)
			{
				int buttonCount = 0;
				for (int i = 0; i < layout.getChildCount(); i++)
				{
					View child = layout.getChildAt(i);
					if (child instanceof TextView && child.isClickable())
					{
						buttonCount++;
					}
				}
				if (buttonCount >= 2)
				{
					XposedBridge.log("[BetterVia] 找到水平按钮容器，按钮数量: " + buttonCount);
					return layout;
				}
			}
		}
		for (int i = 0; i < group.getChildCount(); i++)
		{
			View child = group.getChildAt(i);
			ViewGroup result = findHorizontalButtonContainer(child);
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}
	private void logButtonInfo(ViewGroup container, String stage)
	{
		try
		{
			XposedBridge.log("[BetterVia] " + stage + " - 容器类型: " + container.getClass().getSimpleName() + ", 子视图数量: "
							 + container.getChildCount());
			for (int i = 0; i < container.getChildCount(); i++)
			{
				View child = container.getChildAt(i);
				String info = "子视图 " + i + ": " + child.getClass().getSimpleName();
				if (child instanceof TextView)
				{
					TextView textView = (TextView) child;
					info += " 文本: \"" + textView.getText() + "\"";
					info += " ID: " + Integer.toHexString(child.getId());
					info += " 可点击: " + child.isClickable();
					ViewGroup.LayoutParams params = child.getLayoutParams();
					if (params instanceof LinearLayout.LayoutParams)
					{
						LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) params;
						info += " 权重: " + llParams.weight;
						info += " 宽度: " + llParams.width;
					}
				}
				info += " 可见: " + (child.getVisibility() == View.VISIBLE);
				XposedBridge.log("[BetterVia] " + info);
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 记录按钮信息失败: " + e);
		}
	}
	private String[] extractDownloadInfoFromDialog(Dialog dialog, Context ctx)
	{
		String[] info = {"未知文件", "", "未知大小"};
		try
		{
			View decorView = dialog.getWindow().getDecorView();
			List<TextView> textViews = findAllTextViews(decorView);
			for (TextView textView : textViews)
			{
				String text = textView.getText().toString().trim();
				if (TextUtils.isEmpty(text) || isButtonText(text))
				{
					continue;
				}
				if (text.contains(".") && text.length() > 3)
				{
					info[0] = text;
				}
				else if (text.contains("MB") || text.contains("KB") || text.contains("GB") || text.contains("字节")
						 || text.contains("B") || text.matches(".*\\d+.*"))
				{
					info[2] = text;
				}
			}
			info[1] = extractUrlFromDialog(dialog);
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 提取下载信息失败: " + e);
		}
		return info;
	}
	private List<TextView> findAllTextViews(View view)
	{
		List<TextView> textViews = new ArrayList<>();
		if (view instanceof TextView)
		{
			textViews.add((TextView) view);
		}
		else if (view instanceof ViewGroup)
		{
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++)
			{
				textViews.addAll(findAllTextViews(group.getChildAt(i)));
			}
		}
		return textViews;
	}
	private boolean isButtonText(String text)
	{
		String[] buttonTexts = {"确定", "取消", "复制链接", "OK", "Cancel", "Copy Link", "分享", "Share"};
		for (String buttonText : buttonTexts)
		{
			if (text.equals(buttonText))
			{
				return true;
			}
		}
		return false;
	}
	private String extractUrlFromDialog(Dialog dialog)
	{
		try
		{
			if (Context != null && Context instanceof Activity)
			{
				Activity activity = (Activity) Context;
				try
				{
					Object webView = XposedHelpers.getObjectField(activity, "u");
					if (webView instanceof WebView)
					{
						String url = ((WebView) webView).getUrl();
						if (url != null && !url.isEmpty())
						{
							XposedBridge.log("[BetterVia] 通过WebView获取到URL: " + url);
							return url;
						}
					}
				}
				catch (Exception e1)
				{
					try
					{
						Object webView = XposedHelpers.getObjectField(activity, "webView");
						if (webView instanceof WebView)
						{
							String url = ((WebView) webView).getUrl();
							if (url != null && !url.isEmpty())
							{
								XposedBridge.log("[BetterVia] 通过WebView获取到URL: " + url);
								return url;
							}
						}
					}
					catch (Exception e2)
					{
						XposedBridge.log("[BetterVia] 无法通过反射获取WebView: " + e2.getMessage());
					}
				}
				View currentFocus = activity.getCurrentFocus();
				if (currentFocus instanceof WebView)
				{
					String url = ((WebView) currentFocus).getUrl();
					if (url != null && !url.isEmpty())
					{
						XposedBridge.log("[BetterVia] 通过当前焦点WebView获取到URL: " + url);
						return url;
					}
				}
				ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
				WebView webView = findWebViewRecursive(decorView);
				if (webView != null)
				{
					String url = webView.getUrl();
					if (url != null && !url.isEmpty())
					{
						XposedBridge.log("[BetterVia] 通过遍历View获取到URL: " + url);
						return url;
					}
				}
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 提取URL失败: " + e.getMessage());
		}
		return "";
	}
	private WebView findWebViewRecursive(View view)
	{
		if (view instanceof WebView)
		{
			return (WebView) view;
		}
		else if (view instanceof ViewGroup)
		{
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++)
			{
				WebView webView = findWebViewRecursive(group.getChildAt(i));
				if (webView != null)
				{
					return webView;
				}
			}
		}
		return null;
	}
	private String createShareText(String fileName, String fileSize, String fileUrl, Context ctx)
	{
		if (!fileUrl.isEmpty())
		{
			return fileUrl;
		}
		else
		{
			return "";
		}
	}
	private void setCustomTabsHook(final Context ctx, ClassLoader cl, boolean on)
	{
		if (on)
		{
			if (customTabHook1 == null)
			{
				try
				{
					Class<?> customTabClass = findClassWithFallback("CustomTab", ctx, cl);
					Class<?> shellClass = findClassWithFallback("Shell", ctx, cl);
					if (customTabClass == null || shellClass == null)
					{
						XposedBridge.log("[BetterVia] 未找到CustomTab或Shell类，无法启用CustomTabs Hook");
						return;
					}
					final String customTabClassName = customTabClass.getName();
					final String shellClassName = shellClass.getName();
					customTabHook1 = XposedHelpers.findAndHookMethod(Activity.class, "startActivityForResult",
						Intent.class, int.class, Bundle.class, new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable
							{
								if (!disableCustomTabsEnabled)
									return;
								Intent intent = (Intent) param.args[0];
								if (intent == null)
									return;
								ComponentName component = intent.getComponent();
								if (component == null)
									return;
								if (component.getClassName().contains("CustomTab")
									|| customTabClassName.equals(component.getClassName()))
								{
									XposedBridge.log(
										"[BetterVia] 已拦截 CustomTab 的启动意图，重定向到 Shell...");
									String packageName = ctx.getPackageName();
									intent.setComponent(new ComponentName(packageName, shellClassName));
									if (intent.getData() == null && intent.getDataString() != null)
									{
										intent.setData(Uri.parse(intent.getDataString()));
									}
									intent.addFlags(0x10000000); 
									param.args[0] = intent;
								}
							}
						});
					customTabHook2 = XposedHelpers.findAndHookMethod(customTabClass, "onCreate", Bundle.class,
						new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(MethodHookParam param) throws Throwable
							{
								if (!disableCustomTabsEnabled)
									return;
								Activity activity = (Activity) param.thisObject;
								Intent intent = activity.getIntent();
								boolean isCustomTab = intent.getBooleanExtra("CUSTOM_TAB", true);
								Uri data = intent.getData();
								if (data != null && isCustomTab)
								{
									XposedBridge.log(
										"[BetterVia] CustomTab 通过 Fallback 打开，正在重定向...");
									Intent newIntent = new Intent(intent);
									String packageName = ctx.getPackageName();
									newIntent.setComponent(new ComponentName(packageName, shellClassName));
									newIntent.addFlags(0x10000000); 
									newIntent.setData(data);
									activity.startActivity(newIntent);
									activity.finish();
								}
							}
						});
					setCustomTabsServiceHooks(ctx, cl);
					XposedBridge.log("[BetterVia] CustomTabs Hook已启用");
				}
				catch (Throwable t)
				{
					XposedBridge.log("[BetterVia] 启用CustomTabs Hook失败: " + t);
				}
			}
		}
		else
		{
			if (customTabHook1 != null)
			{
				customTabHook1.unhook();
				customTabHook1 = null;
			}
			if (customTabHook2 != null)
			{
				customTabHook2.unhook();
				customTabHook2 = null;
			}
			if (customTabServiceHooks != null)
			{
				for (XC_MethodHook.Unhook hook : customTabServiceHooks)
				{
					if (hook != null)
					{
						hook.unhook();
					}
				}
				customTabServiceHooks = null;
			}
			XposedBridge.log("[BetterVia] CustomTabs Hook已停用");
		}
		disableCustomTabsEnabled = on;
		putPrefBoolean(ctx, KEY_DISABLE_CUSTOM_TABS, on);
	}
	private void setCustomTabsServiceHooks(Context ctx, ClassLoader cl)
	{
		try
		{
			String packageName = ctx.getPackageName();
			Class<?> customTabsConnectionServiceClass = null;
			String currentClassName = packageName + ".service.CustomTabsConnectionService";
			try
			{
				customTabsConnectionServiceClass = XposedHelpers.findClass(currentClassName, cl);
				XposedBridge.log("[BetterVia] 找到类: " + currentClassName);
			}
			catch (Throwable e)
			{
				XposedBridge.log("[BetterVia] 未找到类: " + currentClassName + "，尝试回退到mark.via");
			}
			if (customTabsConnectionServiceClass == null)
			{
				String fallbackClassName = "mark.via.service.CustomTabsConnectionService";
				try
				{
					customTabsConnectionServiceClass = XposedHelpers.findClass(fallbackClassName, cl);
					XposedBridge.log("[BetterVia] 使用回退类: " + fallbackClassName);
				}
				catch (Throwable e)
				{
					XposedBridge.log("[BetterVia] 未找到回退类: " + fallbackClassName);
					return;
				}
			}
			customTabServiceHooks = new XC_MethodHook.Unhook[4];
			customTabServiceHooks[0] = XposedHelpers.findAndHookMethod(customTabsConnectionServiceClass, "d",
																	   XC_MethodReplacement.returnConstant(false));
			customTabServiceHooks[1] = XposedHelpers.findAndHookMethod(customTabsConnectionServiceClass, "e",
																	   XC_MethodReplacement.returnConstant(false));
			customTabServiceHooks[2] = XposedHelpers.findAndHookMethod(customTabsConnectionServiceClass, "l",
																	   XC_MethodReplacement.returnConstant(false));
			customTabServiceHooks[3] = XposedHelpers.findAndHookMethod(customTabsConnectionServiceClass, "m",
																	   XC_MethodReplacement.returnConstant(false));
			XposedBridge.log("[BetterVia] CustomTabsConnectionService Hook已启用");
		}
		catch (Throwable t)
		{
			XposedBridge.log(
				"[BetterVia] 已跳过 CustomTabsConnectionService Hook（未找到类或方法已更改）：" + t);
		}
	}
	private static boolean getPrefBoolean(Context ctx, String key, boolean def)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (boolean) XposedHelpers.callMethod(sp, "getBoolean", key, def);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	private static void putPrefBoolean(Context ctx, String key, boolean value)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putBoolean", key, value);
			XposedHelpers.callMethod(ed, "apply");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 写入布尔值时失败: " + e);
		}
	}
	private void saveLanguageSetting(Context ctx, String lang)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putString", "preferred_language", lang);
			XposedHelpers.callMethod(ed, "apply");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 保存语言失败: " + e);
		}
	}
	private String getSavedLanguage(Context ctx)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "preferred_language", "auto");
		}
		catch (Exception e)
		{
			return "auto";
		}
	}
	private void putPrefString(Context ctx, String key, String value)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putString", key, value);
			XposedHelpers.callMethod(ed, "apply");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 写入字符串值时失败: " + e);
		}
	}
	private String getPrefString(Context ctx, String key, String def)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", key, def);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	private Class<?> findClassWithFallback(String simpleClassName, Context ctx, ClassLoader cl)
	{
		String packageName = ctx.getPackageName();
		String currentClassName = packageName + "." + simpleClassName;
		try
		{
			Class<?> clazz = XposedHelpers.findClass(currentClassName, cl);
			XposedBridge.log("[BetterVia] 找到类: " + currentClassName);
			return clazz;
		}
		catch (Throwable e)
		{
			XposedBridge.log("[BetterVia] 未找到类: " + currentClassName + "，尝试回退到mark.via");
		}
		String fallbackClassName = "mark.via." + simpleClassName;
		try
		{
			Class<?> clazz = XposedHelpers.findClass(fallbackClassName, cl);
			XposedBridge.log("[BetterVia] 使用回退类: " + fallbackClassName);
			return clazz;
		}
		catch (Throwable e)
		{
			XposedBridge.log("[BetterVia] 未找到回退类: " + fallbackClassName);
			return null;
		}
	}
	private static String getSavedLanguageStatic(Context ctx)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "preferred_language", "auto");
		}
		catch (Exception e)
		{
			return "auto";
		}
	}
	private void copyToClipboard(Context ctx, String text)
	{
		try
		{
			ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("Via Command", text);
			clipboard.setPrimaryClip(clip);
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 复制到剪贴板失败: " + e);
		}
	}
	private GradientDrawable getRoundBg(Context ctx, int color, int radiusDp)
	{
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(color);
		gd.setCornerRadius(dp(ctx, radiusDp));
		return gd;
	}
	private void updateViaLocale(Context ctx, String lang)
	{
		try
		{
			Locale newLoc;
			switch (lang)
			{
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
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
			{
				cfg.setLocale(newLoc);
			}
			else
			{
				cfg.locale = newLoc;
			}
			res.updateConfiguration(cfg, res.getDisplayMetrics());
			XposedBridge.log("[BetterVia] Via语言环境已切换: " + newLoc.toString());
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 切换Locale失败: " + e);
		}
	}
	private void refreshModuleButtonText(Context ctx)
	{
		if (moduleButtonRef == null)
			return;
		try
		{
			String newText = getLocalizedString(ctx, "module_settings");
			XposedHelpers.setObjectField(moduleButtonRef, "a", newText);
			XposedBridge.log("[BetterVia] 模块按钮文字已刷新: " + newText);
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 刷新按钮文字失败: " + e);
		}
	}
	private void showLanguageChangeToast(Context ctx, int which)
	{
		String key;
		switch (which)
		{
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
		Toast.makeText(ctx, getLocalizedString(ctx, key), Toast.LENGTH_SHORT).show();
	}
	private void jiguroMessage(String msg)
	{
		try
		{
			if (Context == null)
			{
				XposedBridge.log("[BetterVia] Context为null，无法显示Toast: " + msg);
				return;
			}
			Context appContext = Context.getApplicationContext();
			if (appContext == null)
			{
				XposedBridge.log("[BetterVia] Application Context为null，无法显示Toast: " + msg);
				return;
			}
			showToastSafely(appContext, msg);
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] Toast显示异常: " + e);
		}
	}
	private void showToastSafely(final Context context, final String msg)
	{
		try
		{
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
					@Override
					public void run()
					{
						try
						{
							Context appContext = context.getApplicationContext();
							Toast toast = Toast.makeText(appContext, msg, Toast.LENGTH_SHORT);
							toast.show();
						}
						catch (Exception innerException)
						{
							XposedBridge.log("[BetterVia] 主线程Toast异常: " + innerException);
							XposedBridge.log("[BetterVia] Toast消息: " + msg);
						}
					}
				});
		}
		catch (Exception outerException)
		{
			XposedBridge.log("[BetterVia] 安全显示Toast失败: " + outerException);
		}
	}
	private static int dp(Context ctx, int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
	}
	private int getPrefInt(Context ctx, String key, int def)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (int) XposedHelpers.callMethod(sp, "getInt", key, def);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	private void putPrefInt(Context ctx, String key, int value)
	{
		try
		{
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object ed = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(ed, "putInt", key, value);
			XposedHelpers.callMethod(ed, "apply");
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 写入Int值失败: " + e);
		}
	}
	private void checkUpdateOnStart(final Context ctx)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(3000);
						checkUpdate(ctx);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
	}
	private void checkUpdate(final Context ctx)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					try
					{
						String networkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
						String updateUrl = networkSource.equals(NETWORK_SOURCE_GITEE)
							? GITEE_UPDATE_URL
							: GITHUB_UPDATE_URL;
						URL url = new URL(updateUrl);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(8000);
						conn.setReadTimeout(8000);
						conn.setRequestMethod("GET");
						if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
						{
							return;
						}
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						StringBuilder sb = new StringBuilder();
						String line;
						while ((line = br.readLine()) != null)
						{
							sb.append(line);
						}
						br.close();
						conn.disconnect();
						String jsonResponse = sb.toString();
						JSONObject json = new JSONObject(jsonResponse);
						final String remoteVersion = json.getString("versionName");
						final String apkUrl = json.getString("apkUrl");
						final String updateLog;
						try
						{
							JSONObject updateLogJson = json.getJSONObject("updateLog");
							String currentLang = getCurrentLanguageCode(ctx);
							if (updateLogJson.has(currentLang))
							{
								updateLog = updateLogJson.getString(currentLang);
							}
							else
							{
								updateLog = updateLogJson.getString("en");
							}
						}
						catch (JSONException e)
						{
							updateLog = json.getString("updateLog");
						}
						String localVersion = MODULE_VERSION_NAME;
						if (!remoteVersion.equals(localVersion))
						{
							if (Context != null && Context instanceof Activity)
							{
								((Activity) Context).runOnUiThread(new Runnable() {
										@Override
										public void run()
										{
											showUpdateDialog(ctx, remoteVersion, updateLog, apkUrl);
										}
									});
							}
						}
					}
					catch (Exception e)
					{
					}
				}
			}).start();
	}
	private String getCurrentLanguageCode(Context ctx)
	{
		String saved = getSavedLanguage(ctx);
		if ("auto".equals(saved))
		{
			Locale locale;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			{
				locale = ctx.getResources().getConfiguration().getLocales().get(0);
			}
			else
			{
				locale = ctx.getResources().getConfiguration().locale;
			}
			if (Locale.SIMPLIFIED_CHINESE.equals(locale))
			{
				return "zh-CN";
			}
			else if (Locale.TRADITIONAL_CHINESE.equals(locale))
			{
				return "zh-TW";
			}
			return "en";
		}
		return saved;
	}
	private void showUpdateDialog(final Context ctx, final String version, final String updateLog,
								  final String apkUrl)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 24), dp(act, 24), dp(act, 24));
					GradientDrawable bg = new GradientDrawable();
					bg.setColor(Color.WHITE);
					bg.setCornerRadius(dp(act, 24));
					root.setBackground(bg);
					TextView title = new TextView(act);
					title.setText("BetterVia");
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
					title.setTextColor(0xFF6200EE); 
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.START); 
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView versionTitle = new TextView(act);
					versionTitle.setText(String.format(getLocalizedString(ctx, "new_version_found"), version));
					versionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					versionTitle.setTextColor(Color.BLACK);
					versionTitle.setTypeface(null, Typeface.BOLD);
					versionTitle.setGravity(Gravity.START);
					LinearLayout.LayoutParams versionLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																						ViewGroup.LayoutParams.WRAP_CONTENT);
					versionLp.bottomMargin = dp(act, 16);
					root.addView(versionTitle, versionLp);
					TextView logTitle = new TextView(act);
					logTitle.setText(getLocalizedString(ctx, "update_log_title"));
					logTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					logTitle.setTextColor(0xFF666666);
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
					logBg.setColor(0xFFF8F9FA); 
					logBg.setCornerRadius(dp(act, 12));
					logContainer.setBackground(logBg);
					LinearLayout.LayoutParams containerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					containerLp.bottomMargin = dp(act, 20);
					root.addView(logContainer, containerLp);
					TextView logContent = new TextView(act);
					logContent.setText(updateLog);
					logContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					logContent.setTextColor(0xFF444444);
					logContent.setLineSpacing(dp(act, 4), 1.2f); 
					logContent.setGravity(Gravity.START);
					logContainer.addView(logContent);
					LinearLayout buttonLayout = new LinearLayout(act);
					buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
					buttonLayout.setGravity(Gravity.CENTER);
					Button laterButton = new Button(act);
					laterButton.setText(getLocalizedString(ctx, "later"));
					laterButton.setTextColor(0xFF6200EE); 
					laterButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					laterButton.setTypeface(null, Typeface.BOLD);
					GradientDrawable laterBg = new GradientDrawable();
					laterBg.setColor(0xFFEEEEEE); 
					laterBg.setCornerRadius(dp(act, 12));
					laterButton.setBackground(laterBg);
					laterButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
					LinearLayout.LayoutParams laterLp = new LinearLayout.LayoutParams(0,
																					  ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
					laterLp.rightMargin = dp(act, 8);
					buttonLayout.addView(laterButton, laterLp);
					Button downloadButton = new Button(act);
					downloadButton.setText(getLocalizedString(ctx, "download_now"));
					downloadButton.setTextColor(Color.WHITE);
					downloadButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					downloadButton.setTypeface(null, Typeface.BOLD);
					GradientDrawable downloadBg = new GradientDrawable();
					downloadBg.setColor(0xFF6200EE); 
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
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						GradientDrawable round = new GradientDrawable();
						round.setColor(Color.WHITE);
						round.setCornerRadius(dp(act, 24));
						win.setBackgroundDrawable(round);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					laterButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					downloadButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								try
								{
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
									act.startActivity(intent);
									dialog.dismiss();
								}
								catch (Exception e)
								{
									Toast.makeText(act, getLocalizedString(ctx, "cannot_open_download_link"),
												   Toast.LENGTH_SHORT).show();
								}
							}
						});
					dialog.show();
				}
			});
	}
	private void showAboutDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					FrameLayout dialogContainer = new FrameLayout(act);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText("BetterVia");
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					title.setTextColor(0xFF6200EE);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					ObjectAnimator colorAnim = ObjectAnimator.ofInt(title, "textColor", 0xFF6200EE, 0xFFFF6B35, 0xFF4CD964,
																	0xFF5AC8FA, 0xFF6200EE);
					colorAnim.setDuration(2000);
					colorAnim.setEvaluator(new ArgbEvaluator());
					colorAnim.setRepeatCount(ObjectAnimator.INFINITE);
					colorAnim.start();
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "about_subtitle") + " 🎉");
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
					subtitle.setGravity(Gravity.CENTER);
					subtitle.setPadding(0, 0, 0, dp(act, 24));
					root.addView(subtitle);
					TextView moduleTitle = new TextView(act);
					moduleTitle.setText(getLocalizedString(ctx, "about_module_title"));
					moduleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					moduleTitle.setTextColor(Color.BLACK);
					moduleTitle.setTypeface(null, Typeface.BOLD);
					moduleTitle.setPadding(0, 0, 0, dp(act, 12));
					root.addView(moduleTitle);
					addAboutItem(root, act, getLocalizedString(ctx, "about_version"), MODULE_VERSION_NAME + " 🎊");
					addAboutItem(root, act, getLocalizedString(ctx, "about_author"), "JiGuro 🧧");
					addClickableAboutItem(root, act, getLocalizedString(ctx, "about_github"),
						"https:
							@Override
							public void onClick(View v)
							{
								openUrl(act, "https:
								Toast.makeText(act, getLocalizedString(ctx, "start_url_message"), Toast.LENGTH_SHORT)
									.show();
							}
						});
					addClickableAboutItem(root, act, getLocalizedString(ctx, "about_gitee"),
						"https:
							@Override
							public void onClick(View v)
							{
								openUrl(act, "https:
								Toast.makeText(act, getLocalizedString(ctx, "start_url_message"), Toast.LENGTH_SHORT)
									.show();
							}
						});
					addClickableAboutItem(root, act, getLocalizedString(ctx, "about_xposed"),
						getLocalizedString(ctx, "about_xposed_repo"), new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								openUrl(act, "https:
								Toast.makeText(act, getLocalizedString(ctx, "start_url_message"), Toast.LENGTH_SHORT)
									.show();
							}
						});
					TextView newYearTitle = new TextView(act);
					newYearTitle.setText(getLocalizedString(ctx, "new_year_title"));
					newYearTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					newYearTitle.setTextColor(0xFFFF6B35);
					newYearTitle.setTypeface(null, Typeface.BOLD);
					newYearTitle.setGravity(Gravity.CENTER);
					newYearTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
					ObjectAnimator scaleX = ObjectAnimator.ofFloat(newYearTitle, "scaleX", 0.8f, 1.2f, 1.0f);
					ObjectAnimator scaleY = ObjectAnimator.ofFloat(newYearTitle, "scaleY", 0.8f, 1.2f, 1.0f);
					AnimatorSet scaleAnim = new AnimatorSet();
					scaleAnim.playTogether(scaleX, scaleY);
					scaleAnim.setDuration(1500);
					scaleAnim.setInterpolator(new OvershootInterpolator());
					scaleAnim.start();
					root.addView(newYearTitle);
					TextView newYearWish = new TextView(act);
					newYearWish.setText(getLocalizedString(ctx, "new_year_wish_text"));
					newYearWish.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					newYearWish.setTextColor(0xFF666666);
					newYearWish.setGravity(Gravity.CENTER);
					newYearWish.setLineSpacing(dp(act, 4), 1.2f);
					newYearWish.setPadding(0, 0, 0, dp(act, 16));
					root.addView(newYearWish);
					TextView updateTitle = new TextView(act);
					updateTitle.setText(getLocalizedString(ctx, "about_update_title"));
					updateTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					updateTitle.setTextColor(Color.BLACK);
					updateTitle.setTypeface(null, Typeface.BOLD);
					updateTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
					root.addView(updateTitle);
					LinearLayout updateContainer = new LinearLayout(act);
					updateContainer.setOrientation(LinearLayout.VERTICAL);
					updateContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
					GradientDrawable updateBg = new GradientDrawable();
					updateBg.setColor(0xFFF8F9FA);
					updateBg.setCornerRadius(dp(act, 8));
					updateContainer.setBackground(updateBg);
					String[] updateLogs = {getLocalizedString(ctx, "about_update_log0"),
						getLocalizedString(ctx, "about_update_log1"), getLocalizedString(ctx, "about_update_log2"),
						getLocalizedString(ctx, "about_update_log3"), getLocalizedString(ctx, "about_update_log4"),
						getLocalizedString(ctx, "about_update_log5")};
					for (String log : updateLogs)
					{
						TextView logItem = new TextView(act);
						logItem.setText("🎯 " + log);
						logItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
						logItem.setTextColor(0xFF444444);
						logItem.setPadding(0, dp(act, 4), 0, dp(act, 4));
						updateContainer.addView(logItem);
					}
					root.addView(updateContainer);
					TextView thanksTitle = new TextView(act);
					thanksTitle.setText(getLocalizedString(ctx, "about_thanks_title"));
					thanksTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					thanksTitle.setTextColor(Color.BLACK);
					thanksTitle.setTypeface(null, Typeface.BOLD);
					thanksTitle.setPadding(0, dp(act, 24), 0, dp(act, 12));
					root.addView(thanksTitle);
					addAboutItem(root, act, "", getLocalizedString(ctx, "about_thanks_content"));
					addClickableAboutItem(root, act, "", "Coolapk @半烟半雨溪桥畔", new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								openUrl(act, "https:
								Toast.makeText(act, getLocalizedString(ctx, "start_url_message"), Toast.LENGTH_SHORT).show();
							}
						});
					addClickableAboutItem(root, act, "", "Blog @sgfox", new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								openUrl(act, "https:
								Toast.makeText(act, getLocalizedString(ctx, "start_url_message"), Toast.LENGTH_SHORT).show();
							}
						});
					addAboutItem(root, act, "", getLocalizedString(ctx, "about_thanks_others"));
					Button ok = new Button(act);
					ok.setText("🎊 " + getLocalizedString(ctx, "dialog_ok") + " 🎊");
					ok.setTextColor(Color.WHITE);
					ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					ok.setTypeface(null, Typeface.BOLD);
					GradientDrawable btnBg = new GradientDrawable();
					btnBg.setColor(0xFFFF6B35); 
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
					if (window != null)
					{
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
					}
					ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					dialog.show();
					new Handler().postDelayed(new Runnable() {
							@Override
							public void run()
							{
								Toast.makeText(act, getLocalizedString(ctx, "new_year_toast"), Toast.LENGTH_LONG).show();
							}
						}, 1000);
				}
			});
	}
	private void showShisuiDialog(final Context ctx)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					final Dialog dialog = new Dialog(act);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setCancelable(true);
					LinearLayout dialogContainer = new LinearLayout(act);
					dialogContainer.setOrientation(LinearLayout.VERTICAL);
					GradientDrawable containerBg = new GradientDrawable();
					containerBg.setColor(Color.WHITE);
					containerBg.setCornerRadius(dp(act, 24));
					dialogContainer.setBackground(containerBg);
					ScrollView scrollRoot = new ScrollView(act);
					scrollRoot.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
					final LinearLayout root = new LinearLayout(act);
					root.setOrientation(LinearLayout.VERTICAL);
					root.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 24));
					TextView title = new TextView(act);
					title.setText(getLocalizedString(ctx, "shisui_dialog_title"));
					title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					title.setTextColor(0xFF6200EE);
					title.setTypeface(null, Typeface.BOLD);
					title.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																					  ViewGroup.LayoutParams.WRAP_CONTENT);
					titleLp.bottomMargin = dp(act, 8);
					root.addView(title, titleLp);
					TextView subtitle = new TextView(act);
					subtitle.setText(getLocalizedString(ctx, "shisui_dialog_subtitle"));
					subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					subtitle.setTextColor(0xFF666666);
					subtitle.setGravity(Gravity.CENTER);
					subtitle.setPadding(0, 0, 0, dp(act, 24));
					root.addView(subtitle);
					final TextView loadingText = new TextView(act);
					loadingText.setText(getLocalizedString(ctx, "shisui_loading"));
					loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					loadingText.setTextColor(0xFF666666);
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
							public void run()
							{
								try
								{
									URL url = new URL(shisuiUrl);
									HttpURLConnection conn = (HttpURLConnection) url.openConnection();
									conn.setRequestMethod("GET");
									conn.setConnectTimeout(10000);
									conn.setReadTimeout(15000);
									conn.connect();
									if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
									{
										InputStream is = conn.getInputStream();
										BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
										StringBuilder sb = new StringBuilder();
										String line;
										while ((line = reader.readLine()) != null)
										{
											sb.append(line);
										}
										reader.close();
										is.close();
										conn.disconnect();
										final String jsonData = sb.toString();
										act.runOnUiThread(new Runnable() {
												@Override
												public void run()
												{
													try
													{
														root.removeView(loadingText);
														JSONArray jsonArray = new JSONArray(jsonData);
														String lastYear = "";
														for (int i = 0; i < jsonArray.length(); i++)
														{
															JSONObject item = jsonArray.getJSONObject(i);
															final String year = item.getString("year");
															final String version = item.getString("version");
															final String content = item.getString("content");
															if (!year.equals(lastYear))
															{
																TextView yearTitle = new TextView(act);
																yearTitle.setText(year);
																yearTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
																yearTitle.setTextColor(Color.BLACK);
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
															versionTitle.setTextColor(0xFF6200EE);
															versionTitle.setTypeface(null, Typeface.BOLD);
															versionTitle.setPadding(0, 0, dp(act, 8), dp(act, 4));
															versionContainer.addView(versionTitle, new LinearLayout.LayoutParams(0,
																																 ViewGroup.LayoutParams.WRAP_CONTENT, 1));
															TextView copyBtn = new TextView(act);
															copyBtn.setText(getLocalizedString(ctx, "shisui_copy"));
															copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
															copyBtn.setTextColor(Color.BLACK);
															copyBtn.setPadding(dp(act, 8), dp(act, 4), dp(act, 8), dp(act, 4));
															copyBtn.setBackground(getRoundBg(act, 0xFFE0E0E0, 12));
															copyBtn.setOnClickListener(new View.OnClickListener() {
																	@Override
																	public void onClick(View v)
																	{
																		StringBuilder copyContent = new StringBuilder();
																		copyContent.append(year).append(" - ").append(version)
																			.append("\n");
																		copyContent.append(content.replace("\\r\\n", "\n"));
																		ClipboardManager clipboard = (ClipboardManager) act
																			.getSystemService(Context.CLIPBOARD_SERVICE);
																		android.content.ClipData clip = android.content.ClipData
																			.newPlainText("Via Shisui", copyContent.toString());
																		clipboard.setPrimaryClip(clip);
																		Toast.makeText(act, getLocalizedString(ctx, "shisui_copied"),
																					   Toast.LENGTH_SHORT).show();
																	}
																});
															versionContainer.addView(copyBtn);
															contentContainer.addView(versionContainer);
															String[] lines = content.split("\\\\r\\\\n");
															for (String line : lines)
															{
																TextView contentText = new TextView(act);
																contentText.setText(line);
																contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
																contentText.setTextColor(0xFF333333);
																contentText.setPadding(dp(act, 8), dp(act, 4), dp(act, 8),
																					   dp(act, 4));
																contentContainer.addView(contentText);
															}
															if (i < jsonArray.length() - 1)
															{
																View divider = new View(act);
																divider.setLayoutParams(new LinearLayout.LayoutParams(
																							ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1)));
																divider.setBackgroundColor(0xFFDDDDDD);
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
														finalDivider.setBackgroundColor(0xFFDDDDDD);
														LinearLayout.LayoutParams finalDividerLp = new LinearLayout.LayoutParams(
															ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 1));
														finalDividerLp.setMargins(0, dp(act, 12), 0, dp(act, 8));
														finalDivider.setLayoutParams(finalDividerLp);
														contentContainer.addView(finalDivider);
														TextView continuedText = new TextView(act);
														continuedText.setText(getLocalizedString(ctx, "to_be_continued"));
														continuedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
														continuedText.setTextColor(0xFF666666);
														continuedText.setTypeface(null, Typeface.ITALIC);
														continuedText.setGravity(Gravity.CENTER);
														continuedText.setPadding(0, 0, 0, dp(act, 16));
														contentContainer.addView(continuedText);
														LinearLayout bottomContainer = new LinearLayout(act);
														bottomContainer.setOrientation(LinearLayout.HORIZONTAL);
														bottomContainer.setGravity(Gravity.CENTER);
														TextView bottomText = new TextView(act);
														bottomText.setText(getLocalizedString(ctx, "shisui_source_credit") + " ");
														bottomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
														bottomText.setTextColor(0xFF666666);
														bottomContainer.addView(bottomText);
														SpannableString ss = new SpannableString("sgfox");
														ClickableSpan clickableSpan = new ClickableSpan() {
															@Override
															public void onClick(View widget)
															{
																openUrl(act, "https:
																Toast.makeText(act, getLocalizedString(ctx, "url_opened"),
																			   Toast.LENGTH_SHORT).show();
															}
														};
														ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
														ss.setSpan(new ForegroundColorSpan(0xFF4285F4), 0, ss.length(),
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
													}
													catch (JSONException e)
													{
														loadingText.setText(getLocalizedString(ctx, "shisui_load_failed"));
													}
												}
											});
									}
									else
									{
										act.runOnUiThread(new Runnable() {
												@Override
												public void run()
												{
													loadingText.setText(getLocalizedString(ctx, "shisui_load_failed"));
												}
											});
									}
								}
								catch (Exception e)
								{
									act.runOnUiThread(new Runnable() {
											@Override
											public void run()
											{
												loadingText.setText(getLocalizedString(ctx, "shisui_load_failed"));
											}
										});
								}
							}
						}).start();
					scrollRoot.addView(root);
					dialogContainer.addView(scrollRoot);
					Window win = dialog.getWindow();
					if (win != null)
					{
						win.setBackgroundDrawableResource(android.R.color.transparent);
						win.setGravity(Gravity.CENTER);
						win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
					dialog.setContentView(dialogContainer);
					dialog.show();
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.9);
					dialog.getWindow().setAttributes(lp);
				}
			});
	}
	private void addAboutItem(LinearLayout parent, Activity act, String label, String value)
	{
		LinearLayout row = new LinearLayout(act);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setPadding(0, dp(act, 6), 0, dp(act, 6));
		if (!label.isEmpty())
		{
			TextView labelView = new TextView(act);
			labelView.setText(label);
			labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			labelView.setTextColor(0xFF666666);
			labelView.setTypeface(null, Typeface.BOLD);
			LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(dp(act, 80),
																			  ViewGroup.LayoutParams.WRAP_CONTENT);
			row.addView(labelView, labelLp);
		}
		TextView valueView = new TextView(act);
		valueView.setText(value);
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(0xFF333333);
		row.addView(valueView);
		parent.addView(row);
	}
	private void addClickableAboutItem(LinearLayout parent, Activity act, String label, String value,
									   View.OnClickListener listener)
	{
		LinearLayout row = new LinearLayout(act);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setPadding(0, dp(act, 6), 0, dp(act, 6));
		row.setOnClickListener(listener);
		if (!label.isEmpty())
		{
			TextView labelView = new TextView(act);
			labelView.setText(label);
			labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			labelView.setTextColor(0xFF666666);
			labelView.setTypeface(null, Typeface.BOLD);
			LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(dp(act, 80),
																			  ViewGroup.LayoutParams.WRAP_CONTENT);
			row.addView(labelView, labelLp);
		}
		TextView valueView = new TextView(act);
		valueView.setText(value);
		valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		valueView.setTextColor(0xFF6200EE);
		valueView.setPaintFlags(valueView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		row.addView(valueView);
		parent.addView(row);
	}
	private void openUrl(Context ctx, String url)
	{
		try
		{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			ctx.startActivity(intent);
		}
		catch (Exception e)
		{
			Toast.makeText(ctx, getLocalizedString(ctx, "cannot_open_url"), Toast.LENGTH_SHORT).show();
		}
	}
	private static void checkViaVersion(Context ctx)
	{
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			String currentVersion = pi.versionName;
			String prefKey = KEY_VERSION_CHECK_DISABLED + "_" + currentVersion;
			boolean versionCheckDisabled = getPrefBoolean(ctx, prefKey, false);
			if (!SUPPORTED_VIA_VERSION.equals(currentVersion) && !versionCheckDisabled)
			{
				showVersionErrorDialog(ctx, currentVersion);
			}
		}
		catch (Exception e)
		{
			XposedBridge.log("[BetterVia] 版本检测失败: " + e.getMessage());
		}
	}
	private static void showVersionErrorDialog(final Context ctx, final String currentVersion)
	{
		if (Context == null || !(Context instanceof Activity))
			return;
		final Activity act = (Activity) Context;
		act.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					if (act.isFinishing() || act.isDestroyed())
						return;
					Toast.makeText(ctx, getLocalizedString(ctx, "hook_success_message"), Toast.LENGTH_SHORT).show();
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle(getLocalizedString(ctx, "version_error_title"));
					String message = String.format(getLocalizedString(ctx, "version_error_message"), currentVersion,
												   SUPPORTED_VIA_VERSION);
					builder.setMessage(message);
					LinearLayout layout = new LinearLayout(act);
					layout.setOrientation(LinearLayout.VERTICAL);
					layout.setPadding(dp(act, 20), dp(act, 10), dp(act, 20), dp(act, 10));
					final CheckBox checkBox = new CheckBox(act);
					checkBox.setText(getLocalizedString(ctx, "version_error_dont_show_again"));
					checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					layout.addView(checkBox);
					builder.setView(layout);
					builder.setNegativeButton(getLocalizedString(ctx, "version_error_exit"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (checkBox.isChecked())
								{
									String prefKey = KEY_VERSION_CHECK_DISABLED + "_" + currentVersion;
									putPrefBoolean(ctx, prefKey, true);
								}
								System.exit(0);
							}
						});
					builder.setPositiveButton(getLocalizedString(ctx, "version_error_continue"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (checkBox.isChecked())
								{
									String prefKey = KEY_VERSION_CHECK_DISABLED + "_" + currentVersion;
									putPrefBoolean(ctx, prefKey, true);
								}
								dialog.dismiss();
							}
						});
					builder.setCancelable(false);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			});
	}
	private static String getLocalizedString(Context ctx, String key)
	{
		Locale loc = getUserLocale(ctx);
		String lang = loc.getLanguage();
		String country = loc.getCountry();
		if ("module_settings".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "模块" : "模組";
			}
			return "Module";
		}
		if ("dialog_ok".equals(key))
		{
			return "zh".equals(lang) ? "确定" : "OK";
		}
		if ("dialog_cancel".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "取消" : "取消";
			}
			return "Cancel";
		}
		if ("dialog_back".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "返回" : "返回";
			}
			return "Back";
		}
		if ("dialog_close".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "关闭" : "關閉";
			}
			return "Close";
		}
		if ("language_title".equals(key))
		{
			return "zh".equals(lang) ? "语言设置" : "Language";
		}
		if ("language_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "设置模块界面和功能提示的语言" : "設置模組界面和功能提示的語言";
			}
			return "Set the language for module interface and feature hints";
		}
		if ("language_auto".equals(key))
		{
			return "zh".equals(lang) ? "自动选择语言" : "Auto Select Language";
		}
		if ("language_zh_cn".equals(key))
		{
			return "zh".equals(lang) ? "简体中文" : "Simplified Chinese";
		}
		if ("language_zh_tw".equals(key))
		{
			return "zh".equals(lang) ? "繁體中文" : "Traditional Chinese";
		}
		if ("language_en".equals(key))
		{
			return "English";
		}
		if ("toast_language_auto".equals(key))
		{
			return "zh".equals(lang) ? "已设置为自动选择语言" : "Set to auto select language";
		}
		if ("toast_language_zh_cn".equals(key))
		{
			return "zh".equals(lang) ? "已设置为简体中文" : "Set to Simplified Chinese";
		}
		if ("toast_language_zh_tw".equals(key))
		{
			return "zh".equals(lang) ? "已設置為繁體中文" : "Set to Traditional Chinese";
		}
		if ("toast_language_en".equals(key))
		{
			return "Set to English";
		}
		if ("whitelist_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "解除白名单限制" : "解除白名單限制";
			}
			return "Bypass Whitelist";
		}
		if ("whitelist_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "解除某些网站的资源嗅探、广告拦截和脚本限制" : "解除某些網站的資源嗅探、廣告攔截和腳本限制";
			}
			return "Unblock resource sniffing, ad blocking and script restrictions for certain websites";
		}
		if ("hook_success_message".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "領域展開，りょういきてんかい !" : "領域展開，りょういきてんかい !";
			}
			return "Field Expansion，りょういきてんかい !";
		}
		if ("component_block_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "屏蔽组件" : "屏蔽組件";
			}
			return "Block Components";
		}
		if ("component_block_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Configure";
		}
		if ("component_block_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "点击配置要屏蔽的组件" : "點擊配置要屏蔽的組件";
			}
			return "Click to configure components to block";
		}
		if ("component_block_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "选择要屏蔽的组件" : "選擇要屏蔽的組件";
			}
			return "Select Components to Block";
		}
		if ("component_block_saved".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "设置已保存" : "設置已保存";
			}
			return "Settings saved";
		}
		if ("component_update".equals(key))
			return "检查更新";
		if ("component_telegram".equals(key))
			return "加入 Telegram 群组";
		if ("component_qq".equals(key))
			return "加入 QQ 群组";
		if ("component_email".equals(key))
			return "通过邮件联系我";
		if ("component_wechat".equals(key))
			return "微信公众号";
		if ("component_donate".equals(key))
			return "捐助我们";
		if ("component_assist".equals(key))
			return "协助翻译";
		if ("component_agreement".equals(key))
			return "使用协议";
		if ("component_privacy".equals(key))
			return "隐私政策";
		if ("component_opensource".equals(key))
			return "开源许可协议";
		if ("component_icp".equals(key))
			return "备案号";
		if ("eye_protection_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "护眼模式" : "護眼模式";
			}
			return "Eye Protection Mode";
		}
		if ("eye_protection_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "屏幕偏暖，减少蓝光对眼睛的伤害" : "屏幕偏暖，減少藍光對眼睛的傷害";
			}
			return "The screen is warmer to reduce the damage of blue light to the eyes";
		}
		if ("eye_protection_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "护眼调节" : "護眼調節";
			}
			return "Eye Protection Adjust";
		}
		if ("eye_protection_config_btn".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Configure";
		}
		if ("eye_protection_config_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "点击调节色温和纸质纹理" : "點擊調節色溫和紙質紋理";
			}
			return "Click to adjust temperature and paper texture";
		}
		if ("eye_protection_config_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "护眼调节" : "護眼調節";
			}
			return "Eye Protection Adjust";
		}
		if ("eye_protection_config_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "护眼模式设置" : "護眼模式設置";
			}
			return "Eye Protection Settings";
		}
		if ("eye_protection_temperature".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "色温调节" : "色溫調節";
			}
			return "Color Temperature";
		}
		if ("eye_protection_texture".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "纸质纹理" : "紙質紋理";
			}
			return "Paper Texture";
		}
		if ("eye_protection_cold".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "偏冷" : "偏冷";
			}
			return "Cool";
		}
		if ("eye_protection_warm".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "偏暖" : "偏暖";
			}
			return "Warm";
		}
		if ("eye_protection_smooth".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "光滑" : "光滑";
			}
			return "Smooth";
		}
		if ("eye_protection_rough".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "粗糙" : "粗糙";
			}
			return "Rough";
		}
		if ("eye_protection_preview_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "效果预览" : "效果預覽";
			}
			return "Preview";
		}
		if ("eye_protection_sample_text".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "片云天共远，永夜月同孤。" : "片雲天共遠，永夜月同孤。";
			}
			return "This is sample text";
		}
		if ("eye_protection_preview_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "开启护眼模式后可实时预览效果" : "開啟護眼模式後可實時預覽效果";
			}
			return "Real-time preview available when eye protection is enabled";
		}
		if ("eye_protection_config_saved".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "护眼设置已保存" : "護眼設置已保存";
			}
			return "Eye protection settings saved";
		}
		if ("block_google_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "超级隐身" : "超級隱身";
			}
			return "Super Stealth";
		}
		if ("block_google_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "阻止收集用户隐私数据，增强安全性" : "阻止收集用戶隱私數據，增強安全性";
			}
			return "Prevent the collection of user private data and enhance security";
		}
		if ("block_startup_message_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "屏蔽启动提示" : "屏蔽啟動提示";
			}
			return "Block Startup Message";
		}
		if ("block_startup_message_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "启动时不显示领域展开提示" : "啟動時不顯示領域展開提示";
			}
			return "Don't show the field expansion message on startup";
		}
		if ("screenshot_protection_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "截屏防护" : "截屏防護";
			}
			return "Screenshot Protection";
		}
		if ("screenshot_protection_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "禁止第三方应用截屏或录屏，保护隐私" : "禁止第三方應用截屏或錄屏，保護隱私";
			}
			return "Prevent third-party apps from taking screenshots or recording screen to protect privacy";
		}
		if ("keep_screen_on_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "屏幕常亮" : "屏幕常亮";
			}
			return "Keep Screen On";
		}
		if ("keep_screen_on_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "防止屏幕自动息屏，但会增加耗电" : "防止屏幕自動息屏，但會增加耗電";
			}
			return "Prevent screen from turning off automatically, but will increase battery consumption";
		}
		if ("background_video_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "后台听视频" : "後台聽影片";
			}
			return "Background Video Audio";
		}
		if ("background_video_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "在浏览器播放视频时置于后台，声音不会停止" : "在瀏覽器播放影片時置於後台，聲音不會停止";
			}
			return "Continue playing audio when video is in background";
		}
		if ("hide_status_bar_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "隐藏状态栏" : "隱藏狀態欄";
			}
			return "Hide Status Bar";
		}
		if ("hide_status_bar_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "隐藏屏幕上方的状态栏，下滑即可恢复" : "隱藏屏幕上方的狀態欄，下滑即可恢復";
			}
			return "Hide the status bar at the top of the screen and slide it down to restore it";
		}
		if ("search_commands_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "搜索指令" : "搜尋指令";
			}
			return "Search Commands";
		}
		if ("search_commands_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看" : "查看";
			}
			return "View";
		}
		if ("search_commands_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看所有Via搜索指令" : "查看所有Via搜尋指令";
			}
			return "View all Via search commands";
		}
		if ("search_commands_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "Via搜索指令大全" : "Via搜尋指令大全";
			}
			return "Via Search Commands";
		}
		if ("search_commands_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "以下指令可在Via浏览器地址栏中使用" : "以下指令可在Via瀏覽器地址欄中使用";
			}
			return "The following commands can be used in Via browser address bar";
		}
		if ("command_copy".equals(key))
		{
			return "zh".equals(lang) ? "复制" : "Copy";
		}
		if ("command_copied".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已复制到剪贴板" : "已複製到剪貼簿";
			}
			return "Copied to clipboard";
		}
		if ("command_bookmark".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开书签" : "開啟書籤籤";
			}
			return "Open bookmarks";
		}
		if ("command_search".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开搜索框" : "開啟搜尋框";
			}
			return "Open search box";
		}
		if ("command_unknown".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "功能未知" : "功能未知";
			}
			return "Unknown function";
		}
		if ("command_print".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打印当前网页" : "列印當前網頁";
			}
			return "Print current page";
		}
		if ("command_adblock".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "拦截广告" : "攔截廣告";
			}
			return "Block ads";
		}
		if ("command_log".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开日志" : "開啟日誌";
			}
			return "Open logs";
		}
		if ("command_home".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开主页" : "開啟主頁";
			}
			return "Open home page";
		}
		if ("command_skins".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开皮肤" : "開啟皮膚";
			}
			return "Open skins";
		}
		if ("command_about".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开关于" : "開啟關於";
			}
			return "Open about";
		}
		if ("command_search_page".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开搜索页面" : "開啟搜尋頁面";
			}
			return "Open search page";
		}
		if ("command_offline".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开离线窗口" : "開啟離線視窗";
			}
			return "Open offline window";
		}
		if ("command_history".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开历史" : "開啟歷史";
			}
			return "Open history";
		}
		if ("command_scanner".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "扫二维码" : "掃描QR碼";
			}
			return "Scan QR code";
		}
		if ("command_bookmarks_page".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开书签页面" : "開啟書籤籤頁面";
			}
			return "Open bookmarks page";
		}
		if ("command_downloader".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开下载管理" : "開啟下載管理";
			}
			return "Open download manager";
		}
		if ("command_readaloud".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "阅读控制器" : "閱讀控制器";
			}
			return "Reading controller";
		}
		if ("command_translator".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "翻译文本" : "翻譯文字";
			}
			return "Translate text";
		}
		if ("command_history_page".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开历史页面" : "開啟歷史頁面";
			}
			return "Open history page";
		}
		if ("command_folder".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "打开书签文件夹" : "開啟書籤籤資料夾";
			}
			return "Open bookmarks folder";
		}
		if ("homepage_theme_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主页主题" : "主頁主題";
			}
			return "Homepage Theme";
		}
		if ("homepage_theme_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Configure";
		}
		if ("homepage_theme_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "自定义Via浏览器主页外观" : "自定義Via瀏覽器主頁外觀";
			}
			return "Customize Via browser homepage appearance";
		}
		if ("homepage_theme_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主页主题" : "主頁主題";
			}
			return "Homepage Themes";
		}
		if ("homepage_theme_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "丰富的主题资源" : "豐富的主題資源";
			}
			return "Rich theme resources";
		}
		if ("homepage_theme_by".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "作者：" : "作者：";
			}
			return "By ";
		}
		if ("homepage_theme_developing".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在开发中..." : "正在開發中...";
			}
			return "Developing...";
		}
		if ("homepage_theme_apply_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "应用主题" : "應用主題";
			}
			return "Apply Theme";
		}
		if ("homepage_theme_apply_message".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "您是否要应用主题" : "您是否要應用主題";
			}
			return "Do you want to apply the theme";
		}
		if ("homepage_theme_apply".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "应用" : "應用";
			}
			return "Apply";
		}
		if ("homepage_theme_apply_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主题应用成功，重启Via后生效" : "主題應用成功，重啟Via後生效";
			}
			return "Theme applied successfully, changes effective after restart Via";
		}
		if ("homepage_theme_apply_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主题应用失败，请检查网络连接" : "主題應用失敗，請檢查網路連接";
			}
			return "Theme application failed, please check network connection";
		}
		if ("homepage_theme_apply_error".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "应用主题时发生错误" : "應用主題時發生錯誤";
			}
			return "Error occurred while applying theme";
		}
		if ("homepage_theme_edit".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "编辑" : "編輯";
			}
			return "Edit";
		}
		if ("homepage_theme_editor_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主题编辑器" : "主題編輯器";
			}
			return "Theme Editor";
		}
		if ("theme_editor_select_file".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "选择编辑的文件:" : "選擇編輯的文件:";
			}
			return "Select file to edit:";
		}
		if ("theme_editor_edit_content".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "编辑内容:" : "編輯內容:";
			}
			return "Edit content:";
		}
		if ("theme_editor_save_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "保存成功" : "保存成功";
			}
			return "Save successful";
		}
		if ("homepage_theme_save".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "保存" : "保存";
			}
			return "Save";
		}
		if ("homepage_theme_save_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "保存成功" : "保存成功";
			}
			return "Save successful";
		}
		if ("homepage_theme_save_error".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "保存失败" : "保存失敗";
			}
			return "Save failed";
		}
		if ("homepage_theme_file_not_found".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "文件不存在" : "文件不存在";
			}
			return "File not found";
		}
		if ("homepage_theme_load_error".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "加载错误" : "加載錯誤";
			}
			return "Load error";
		}
		if ("homepage_theme_edit_file".equals(key))
		{
			return "编辑文件:";
		}
		if ("homepage_theme_edit_content".equals(key))
		{
			return "编辑内容:";
		}
		if ("script_repository_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "脚本仓库" : "腳本倉庫";
			}
			return "Script Repository";
		}
		if ("script_repository_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Configure";
		}
		if ("script_repository_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "下载浏览器脚本插件" : "下載瀏覽器腳本插件";
			}
			return "Download browser script plugins";
		}
		if ("script_repository_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "脚本仓库" : "腳本倉庫";
			}
			return "Script Repository";
		}
		if ("script_repository_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "丰富的用户脚本资源，但不保证可用" : "豐富的用戶腳本資源，但不保證可用";
			}
			return "Rich user script resources, but availability is not guaranteed";
		}
		if ("scripts_loading".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在加载脚本..." : "正在加載腳本...";
			}
			return "Loading scripts...";
		}
		if ("scripts_load_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "脚本加载失败" : "腳本加載失敗";
			}
			return "Failed to load scripts";
		}
		if ("script_opened_in_via".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已打开脚本，现在您可以继续浏览，刷新网页即可批量安装" : "已打開腳本，現在您可以繼續瀏覽，刷新網頁即可批量安裝";
			}
			return "Script opened in Via, refresh the page to install";
		}
		if ("script_search_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "搜索脚本名称或描述..." : "搜尋腳本名稱或描述...";
			}
			return "Search script name or description...";
		}
		if ("script_search_button".equals(key))
		{
			return "zh".equals(lang) ? "搜索" : "Search";
		}
		if ("script_search_no_results".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未找到包含\"%s\"的脚本" : "未找到包含\"%s\"的腳本";
			}
			return "No scripts found containing \"%s\"";
		}
		if ("script_search_no_results_toast".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未找到相关脚本" : "未找到相關腳本";
			}
			return "No related scripts found";
		}
		if ("script_search_results".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "找到 %d 个脚本" : "找到 %d 個腳本";
			}
			return "Found %d scripts";
		}
		if ("script_show_all".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "显示全部 %d 个脚本" : "顯示全部 %d 個腳本";
			}
			return "Showing all %d scripts";
		}
		if ("no_scripts_available".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "没有可用的脚本" : "沒有可用的腳本";
			}
			return "No scripts available";
		}
		if ("script_total_count".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "共收录 %d 个脚本" : "共收錄 %d 個腳本";
			}
			return "Total %d scripts";
		}
		if ("script_filtered_count".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "筛选出 %d/%d 个脚本" : "篩選出 %d/%d 個腳本";
			}
			return "Filtered %d/%d scripts";
		}
		if ("script_loading_count".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在加载脚本..." : "正在加載腳本...";
			}
			return "Loading scripts...";
		}
		if ("script_load_failed_count".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "加载失败" : "加載失敗";
			}
			return "Load failed";
		}
		if ("ad_block_rules_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "广告走开" : "廣告走開";
			}
			return "Ad Block Rules";
		}
		if ("ad_block_rules_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Configure";
		}
		if ("ad_block_rules_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "点击配置广告拦截规则" : "點擊配置廣告攔截規則";
			}
			return "Click to configure ad blocking rules";
		}
		if ("ad_block_rules_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "广告拦截规则" : "廣告攔截規則";
			}
			return "Ad Block Rules";
		}
		if ("ad_block_rules_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "选择适合的广告拦截规则" : "選擇適合的廣告攔截規則";
			}
			return "Select suitable ad blocking rules";
		}
		if ("rules_loading".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "加载规则中..." : "加載規則中...";
			}
			return "Loading rules...";
		}
		if ("rules_load_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "规则加载失败" : "規則加載失敗";
			}
			return "Failed to load rules";
		}
		if ("rules_category_small".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "小型规则" : "小型規則";
			}
			return "Small Rules";
		}
		if ("rules_category_large".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "大型规则" : "大型規則";
			}
			return "Large Rules";
		}
		if ("rule_author".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "作者" : "作者";
			}
			return "Author";
		}
		if ("rule_homepage".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主页" : "主頁";
			}
			return "Homepage";
		}
		if ("rule_channel".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "渠道" : "渠道";
			}
			return "Channel";
		}
		if ("rule_link_copied".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "链接已复制到剪贴板" : "連結已複製到剪貼板";
			}
			return "Link copied to clipboard";
		}
		if ("cannot_open_homepage".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "无法打开主页链接" : "無法開啟主頁連結";
			}
			return "Cannot open homepage link";
		}
		if ("homepage_bg_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "资源界面美化" : "資源界面美化";
			}
			return "Resource UI Beauty";
		}
		if ("homepage_bg_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "配置" : "配置";
			}
			return "Pick";
		}
		if ("homepage_bg_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "为日志/资源页设置背景图" : "為日誌/資源頁設定背景圖";
			}
			return "Set background for log/resource page";
		}
		if ("homepage_bg_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "资源界面美化" : "資源界面美化";
			}
			return "Resource UI Beauty";
		}
		if ("homepage_bg_dialog_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "调整背景图与遮罩，让文字更清晰" : "調整背景圖與遮罩，讓文字更清晰";
			}
			return "Adjust background & mask to keep text clear";
		}
		if ("homepage_bg_pick_btn".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "选择图片" : "選擇圖片";
			}
			return "Select Image";
		}
		if ("homepage_bg_mask_alpha".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "遮罩透明度" : "遮罩透明度";
			}
			return "Mask Opacity";
		}
		if ("homepage_bg_mask_color".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "遮罩色相" : "遮罩色相";
			}
			return "Mask Hue";
		}
		if ("homepage_bg_mask_color_rgb".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "遮罩颜色（RGB）" : "遮罩顏色（RGB）";
			}
			return "Mask Color (RGB)";
		}
		if ("homepage_bg_mask_color_confirm".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "确认" : "確認";
			}
			return "Confirm";
		}
		if ("homepage_bg_mask_color_invalid".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "颜色格式无效，请使用#RRGGBB格式" : "顏色格式無效，請使用#RRGGBB格式";
			}
			return "Invalid color format, please use #RRGGBB";
		}
		if ("homepage_bg_mask_color_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "输入RGB颜色代码（如#FFFFFF表示白色）" : "輸入RGB顏色代碼（如#FFFFFF表示白色）";
			}
			return "Enter RGB color code (e.g. #FFFFFF for white)";
		}
		if ("homepage_bg_set_ok".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "背景图已设置" : "背景圖已設定";
			}
			return "Background image set";
		}
		if ("homepage_bg_set_fail".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "设置失败" : "設定失敗";
			}
			return "Set failed";
		}
		if ("homepage_bg_saved".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "设置已保存" : "設定已儲存";
			}
			return "Settings saved";
		}
		if ("cookie_management_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "Cookie管理" : "Cookie管理";
			}
			return "Cookie Management";
		}
		if ("cookie_management_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "管理" : "管理";
			}
			return "Manage";
		}
		if ("cookie_management_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看和管理浏览器Cookie" : "查看和管理瀏覽器Cookie";
			}
			return "View and manage browser cookies";
		}
		if ("cookie_manager_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "Cookie管理器" : "Cookie管理器";
			}
			return "Cookie Manager";
		}
		if ("cookie_manager_search_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "搜索域名或Cookie名称..." : "搜尋網域或Cookie名稱...";
			}
			return "Search domain or cookie name...";
		}
		if ("cookie_manager_search_btn".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "搜索" : "搜尋";
			}
			return "Search";
		}
		if ("cookie_manager_loading".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在加载Cookie数据..." : "正在加載Cookie數據...";
			}
			return "Loading cookie data...";
		}
		if ("cookie_manager_empty".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "没有找到Cookie数据" : "沒有找到Cookie數據";
			}
			return "No cookie data found";
		}
		if ("cookie_management_refreshed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "刷新成功" : "刷新成功";
			}
			return "Refresh successful";
		}
		if ("cookie_search_result".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "找到 %d 个结果" : "找到 %d 個結果";
			}
			return "Found %d results";
		}
		if ("cookie_manager_delete_selected".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "删除选中" : "刪除選中";
			}
			return "Delete Selected";
		}
		if ("cookie_manager_select_all".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "全选" : "全選";
			}
			return "Select All";
		}
		if ("cookie_manager_unselect_all".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "取消全选" : "取消全選";
			}
			return "Unselect All";
		}
		if ("cookie_manager_selecting".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在选择所有项，请稍候..." : "正在選擇所有項，請稍候...";
			}
			return "Selecting all items, please wait...";
		}
		if ("cookie_manager_unselecting".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在取消选择，请稍候..." : "正在取消選擇，請稍候...";
			}
			return "Unselecting, please wait...";
		}
		if ("cookie_detail_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "Cookie详情" : "Cookie詳情";
			}
			return "Cookie Details";
		}
		if ("cookie_field_host_key".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "域名:" : "網域:";
			}
			return "Host:";
		}
		if ("cookie_field_name".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "名称:" : "名稱:";
			}
			return "Name:";
		}
		if ("cookie_field_value".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "值:" : "值:";
			}
			return "Value:";
		}
		if ("cookie_field_path".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "路径:" : "路徑:";
			}
			return "Path:";
		}
		if ("cookie_field_creation_time".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "创建时间:" : "建立時間:";
			}
			return "Creation Time:";
		}
		if ("cookie_field_last_access".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "最后访问:" : "最後訪問:";
			}
			return "Last Access:";
		}
		if ("cookie_field_expires".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "过期时间:" : "過期時間:";
			}
			return "Expires:";
		}
		if ("cookie_field_last_update".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "最后更新:" : "最後更新:";
			}
			return "Last Update:";
		}
		if ("cookie_field_secure".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "安全连接(HTTPS):" : "安全連接(HTTPS):";
			}
			return "Secure (HTTPS):";
		}
		if ("cookie_field_httponly".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "HTTP Only:" : "HTTP Only:";
			}
			return "HTTP Only:";
		}
		if ("cookie_field_persistent".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "持久化:" : "持久化:";
			}
			return "Persistent:";
		}
		if ("cookie_field_has_expires".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "有过期时间:" : "有過期時間:";
			}
			return "Has Expires:";
		}
		if ("cookie_field_priority".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "优先级:" : "優先級:";
			}
			return "Priority:";
		}
		if ("cookie_field_samesite".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "SameSite:" : "SameSite:";
			}
			return "SameSite:";
		}
		if ("cookie_field_source_port".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "源端口:" : "來源端口:";
			}
			return "Source Port:";
		}
		if ("cookie_field_source_type".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "源类型:" : "來源類型:";
			}
			return "Source Type:";
		}
		if ("cookie_save_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "Cookie已保存" : "Cookie已保存";
			}
			return "Cookie saved successfully";
		}
		if ("cookie_delete_confirm_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "确认删除" : "確認刪除";
			}
			return "Confirm Delete";
		}
		if ("cookie_delete_confirm_msg".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "确定要删除选中的Cookie吗？此操作不可撤销。" : "確定要刪除選中的Cookie嗎？此操作不可撤銷。";
			}
			return "Are you sure you want to delete the selected cookies? This operation cannot be undone.";
		}
		if ("cookie_delete_error".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "删除Cookie时发生错误" : "刪除Cookie時發生錯誤";
			}
			return "Error occurred while deleting cookies";
		}
		if ("cookie_detail_basic_info".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "基本信息" : "基本資訊";
			}
			return "Basic Information";
		}
		if ("cookie_detail_time_info".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "时间信息" : "時間資訊";
			}
			return "Time Information";
		}
		if ("cookie_detail_security_info".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "安全信息" : "安全資訊";
			}
			return "Security Information";
		}
		if ("cookie_detail_advanced_info".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "高级信息" : "進階資訊";
			}
			return "Advanced Information";
		}
		if ("cookie_field_unknown".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未知" : "未知";
			}
			return "Unknown";
		}
		if ("cookie_field_session".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "会话Cookie" : "工作階段Cookie";
			}
			return "Session Cookie";
		}
		if ("cookie_field_default".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "默认" : "預設";
			}
			return "Default";
		}
		if ("cookie_samesite_none".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未设置" : "未設定";
			}
			return "Not Set";
		}
		if ("cookie_samesite_lax".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "宽松模式 (Lax)" : "寬鬆模式 (Lax)";
			}
			return "Lax Mode";
		}
		if ("cookie_samesite_strict".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "严格模式 (Strict)" : "嚴格模式 (Strict)";
			}
			return "Strict Mode";
		}
		if ("cookie_samesite_unknown".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未知 (%d)" : "未知 (%d)";
			}
			return "Unknown (%d)";
		}
		if ("cookie_source_type_none".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未设置" : "未設定";
			}
			return "Not Set";
		}
		if ("cookie_source_type_http".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "HTTP" : "HTTP";
			}
			return "HTTP";
		}
		if ("cookie_source_type_https".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "HTTPS" : "HTTPS";
			}
			return "HTTPS";
		}
		if ("cookie_source_type_file".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "文件" : "檔案";
			}
			return "File";
		}
		if ("cookie_source_type_unknown".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未知 (%d)" : "未知 (%d)";
			}
			return "Unknown (%d)";
		}
		if ("cookie_unknown_domain".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未知域名" : "未知網域";
			}
			return "Unknown Domain";
		}
		if ("cookie_field_name_label".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "名称: " : "名稱: ";
			}
			return "Name: ";
		}
		if ("cookie_field_value_label".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "值: " : "值: ";
			}
			return "Value: ";
		}
		if ("cookie_no_value".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "无值" : "無值";
			}
			return "No Value";
		}
		if ("cookie_manager_delete_btn".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "确定" : "確定";
			}
			return "Delete";
		}
		if ("cookie_view_domain".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "域名视图" : "網域視圖";
			}
			return "Domain View";
		}
		if ("cookie_view_list".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "列表视图" : "列表視圖";
			}
			return "List View";
		}
		if ("cookie_view_switching".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在切换视图..." : "正在切換視圖...";
			}
			return "Switching view...";
		}
		if ("cookie_domain_count_label".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "%d 个Cookie" : "%d 個Cookie";
			}
			return "%d cookies";
		}
		if ("cookie_domain_more_label".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "还有 %d 个..." : "還有 %d 個...";
			}
			return "%d more...";
		}
		if ("cookie_domain_delete_all".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "删除所有" : "刪除所有";
			}
			return "Delete All";
		}
		if ("cookie_delete_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已删除选中的Cookie" : "已刪除選中的Cookie";
			}
			return "Selected cookies deleted";
		}
		if ("cookie_delete_no_selected".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "没有选中要删除的Cookie" : "沒有選中要刪除的Cookie";
			}
			return "No cookies are selected for deletion";
		}
		if ("cookie_domain_total_count".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "该域名下共有 %d 个Cookie" : "該網域下共有 %d 個Cookie";
			}
			return "This domain has %d cookies";
		}
		if ("cookie_domain_delete_confirm_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "删除Cookie确认" : "刪除Cookie確認";
			}
			return "Delete Cookie Confirmation";
		}
		if ("cookie_domain_delete_confirm_msg".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country)
					? "确定要删除域名 %s 下的所有 %d 个Cookie吗？此操作不可撤销。"
					: "確定要刪除網域 %s 下的所有 %d 個Cookie嗎？此操作不可撤銷。";
			}
			return "Are you sure you want to delete all %d cookies from domain %s? This operation cannot be undone.";
		}
		if ("cookie_domain_delete_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已删除域名 %s 下的 %d 个Cookie" : "已刪除網域 %s 下的 %d 個Cookie";
			}
			return "Deleted %d cookies from domain %s";
		}
		if ("cookie_domain_delete_selected_confirm_msg".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "确定要删除选中域名下的所有Cookie吗？此操作不可撤销。" : "確定要刪除選中網域下的所有Cookie嗎？此操作不可撤銷。";
			}
			return "Are you sure you want to delete all cookies from the selected domains? This operation cannot be undone.";
		}
		if ("cookie_domain_delete_selected_success".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已删除 %d 个域名的共 %d 个Cookie" : "已刪除 %d 個網域的共 %d 個Cookie";
			}
			return "Deleted %d cookies from %d domains";
		}
		if ("cookie_domain_search_result".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "找到 %d 个域名" : "找到 %d 個網域";
			}
			return "Found %d domains";
		}
		if ("network_source_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "网络源" : "網路源";
			}
			return "Network Source";
		}
		if ("network_source_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "选择主题和脚本等资源的下载源" : "選擇主題和腳本等資源的下載源";
			}
			return "Select the download source for themes, scripts and other resources";
		}
		if ("network_source_changed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "网络源已切换至" : "網路源已切換至";
			}
			return "Network source changed to";
		}
		if ("auto_update_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "自动检查更新" : "自動檢查更新";
			}
			return "Auto Check Updates";
		}
		if ("auto_update_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "启动时自动检查模块更新" : "啟動時自動檢查模組更新";
			}
			return "Automatically check for module updates on startup";
		}
		if ("new_version_found".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "发现新版本 %s" : "發現新版本 %s";
			}
			return "New Version %s Found";
		}
		if ("download_now".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "立即下载" : "立即下載";
			}
			return "Download";
		}
		if ("later".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "以后再说" : "以後再說";
			}
			return "Later";
		}
		if ("cannot_open_download_link".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "无法打开下载链接" : "無法開啟下載連結";
			}
			return "Cannot open download link";
		}
		if ("update_log_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "更新内容" : "更新內容";
			}
			return "Update Log";
		}
		if ("module_settings_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "让Via变得更好" : "讓Via變得更好";
			}
			return "Make Via Better";
		}
		if ("themes_loading".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在加载主题..." : "正在加載主題...";
			}
			return "Loading themes...";
		}
		if ("themes_load_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "主题加载失败" : "主題加載失敗";
			}
			return "Failed to load themes";
		}
		if ("check_network".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "请检查网络连接后重试" : "請檢查網路連接後重試";
			}
			return "Please check your network connection and try again";
		}
		if ("user_agent_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "浏览器标识" : "瀏覽器標識";
			}
			return "User Agent";
		}
		if ("user_agent_config".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看" : "查看";
			}
			return "View";
		}
		if ("user_agent_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看和复制各种浏览器的User-Agent" : "查看和複製各種瀏覽器的User-Agent";
			}
			return "View and copy User-Agents for various browsers";
		}
		if ("user_agent_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "浏览器标识大全" : "瀏覽器標識大全";
			}
			return "User Agent Collection";
		}
		if ("user_agent_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已根据您的设备信息个性化调整" : "已根據您的設備信息個性化調整";
			}
			return "Personalized based on your device information";
		}
		if ("user_agent_copy".equals(key))
		{
			return "zh".equals(lang) ? "复制" : "Copy";
		}
		if ("user_agent_copied".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "UA已复制到剪贴板" : "UA已複製到剪貼簿";
			}
			return "UA copied to clipboard";
		}
		if ("development_toast".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在开发中，敬请期待" : "正在開發中，敬請期待";
			}
			return "Under development, stay tuned";
		}
		if ("just_trust_me_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "SSL证书绕过" : "SSL證書繞過";
			}
			return "SSL Certificate Bypass";
		}
		if ("just_trust_me_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "绕过SSL证书验证，用于调试和抓包" : "繞過SSL證書驗證，用於調試和抓包";
			}
			return "Bypass SSL certificate verification for debugging and packet capture";
		}
		if ("version_error_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "版本错误" : "版本錯誤";
			}
			return "Version Error";
		}
		if ("version_error_message".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country)
					? "检测到您的Via版本为%s，本模块版本适用于Via 6.9.0，建议您切换Via至模块支持版本。\n继续可能会导致模块失效和Via闪退，您确定要继续吗？"
					: "檢測到您的Via版本為%s，本模組版本適用於Via 6.9.0，建議您切換Via至模組支援版本。\n繼續可能會導致模組失效和Via閃退，您確定要繼續嗎？";
			}
			return "Detected your Via version is %s, this module version is suitable for Via 6.9.0, it is recommended to switch Via to the version supported by the module.\nContinuing may cause the module to fail and Via to crash, are you sure you want to continue?";
		}
		if ("version_error_dont_show_again".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "不再提示" : "不再提示";
			}
			return "Don't show again";
		}
		if ("version_error_exit".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "退出" : "退出";
			}
			return "Exit";
		}
		if ("version_error_continue".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "进入" : "進入";
			}
			return "Continue";
		}
		if ("about_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "关于" : "關於";
			}
			return "About";
		}
		if ("about_view".equals(key))
		{
			return "zh".equals(lang) ? "查看" : "View";
		}
		if ("about_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看模块信息和更新日志" : "查看模組信息和更新日誌";
			}
			return "View module information and update log";
		}
		if ("about_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "让Via变得更好" : "讓Via變得更好";
			}
			return "Make Via Better";
		}
		if ("about_module_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "模块" : "模組";
			}
			return "Module";
		}
		if ("about_version".equals(key))
		{
			return "zh".equals(lang) ? "版本" : "Version";
		}
		if ("about_author".equals(key))
		{
			return "zh".equals(lang) ? "作者" : "Author";
		}
		if ("about_github".equals(key))
		{
			return "GitHub";
		}
		if ("about_gitee".equals(key))
		{
			return "Gitee";
		}
		if ("about_xposed".equals(key))
		{
			return "Xposed Repo";
		}
		if ("about_xposed_repo".equals(key))
		{
			return "zh".equals(lang) ? "Xposed模块仓库" : "Xposed Module Repository";
		}
		if ("start_url_message".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "链接已打开" : "鏈接已打開";
			}
			return "Link is open";
		}
		if ("about_update_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "更新" : "更新";
			}
			return "Update";
		}
		if ("about_update_log0".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "新增了对多包名的支持" : "新增了對多包名的支持";
			}
			return "Added support for multiple package names";
		}
		if ("about_update_log1".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "进一步修复了在某些机型上导致崩溃的问题，并添加了错误代码支持" : "進一步修復了在某些機型上導致崩潰的問題，並添加了錯誤代碼支持";
			}
			return "Further fixes for crashes on some models and added error code support";
		}
		if ("about_update_log2".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "新增了禁用CustomTabs的功能" : "新增了禁用CustomTabs的功能";
			}
			return "Added ability to disable CustomTabs";
		}
		if ("about_update_log3".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "在Cookie管理器中新增全选按钮" : "在Cookie管理器中新增全選按鈕";
			}
			return "Added select all button in Cookie Manager";
		}
		if ("about_update_log4".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "简化了一些复杂的逻辑，增强用户体验" : "簡化了一些複雜的邏輯，增強用戶體驗";
			}
			return "Simplified some complex logic and enhanced user experience";
		}
		if ("about_update_log5".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "修复了包括点击模块按钮无反应等亿些Bug" : "修復了包括點擊模塊按鈕無反應等億些Bug";
			}
			return "Fixed bugs including unresponsiveness when clicking module buttons";
		}
		if ("about_thanks_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "鸣谢" : "鳴謝";
			}
			return "Thanks";
		}
		if ("about_thanks_content".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "AIDE+, AndroidIDE" : "AIDE+, AndroidIDE";
			}
			return "AIDE+, AndroidIDE";
		}
		if ("about_thanks_others".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "因人数过多，不一一列举，详见模块各版面详细作者" : "因人數過多，不一一列舉，詳見模塊各版面詳細作者";
			}
			return "Because there are too many people, we will not list them one by one. For details, please see the detailed authors in each section of the module";
		}
		if ("shisui_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "拾穗" : "拾穗";
			}
			return "Shisui";
		}
		if ("shisui_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "你喜爱的，从未缺席" : "你喜愛的，從未缺席";
			}
			return "What you love has never been absent";
		}
		if ("shisui_view".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "查看" : "查看";
			}
			return "View";
		}
		if ("shisui_dialog_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "拾穗" : "拾穗";
			}
			return "Shisui";
		}
		if ("shisui_dialog_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "整理Via拾穗中的内容" : "整理Via拾穗中的內容";
			}
			return "Organizing content from Via Shisui";
		}
		if ("shisui_loading".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "正在加载拾穗内容..." : "正在載入拾穗內容...";
			}
			return "Loading Shisui content...";
		}
		if ("shisui_load_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "加载失败，请检查网络连接" : "載入失敗，請檢查網路連接";
			}
			return "Failed to load, please check your network connection";
		}
		if ("shisui_copy".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "复制" : "複製";
			}
			return "Copy";
		}
		if ("shisui_copied".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已复制到剪贴板" : "已複製到剪貼板";
			}
			return "Copied to clipboard";
		}
		if ("shisui_source_credit".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "拾穗整理来自" : "拾穗整理來自";
			}
			return "Shisui compiled by";
		}
		if ("url_opened".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "链接已打开" : "連結已開啟";
			}
			return "Link opened";
		}
		if ("to_be_continued".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "未完待续..." : "未完待續...";
			}
			return "To be continued...";
		}
		if ("cannot_open_url".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "无法打开链接" : "無法開啟連結";
			}
			return "Cannot open link";
		}
		if ("download_dialog_share".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "分享" : "分享";
			}
			return "Share";
		}
		if ("share_started".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "已启动分享" : "已啟動分享";
			}
			return "Share started";
		}
		if ("share_failed".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "分享失败" : "分享失敗";
			}
			return "Share failed";
		}
		if ("download_dialog_share_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "下载分享" : "下載分享";
			}
			return "Download Share";
		}
		if ("download_dialog_share_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "在下载对话框中添加分享按钮，便于分享到其他应用" : "在下載對話框中添加分享按鈕，便於分享到其他应用";
			}
			return "Add a share button in the download dialog box to facilitate sharing to other applications";
		}
		if ("download_dialog_share_enabled".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "下载分享功能已启用" : "下載分享功能已啟用";
			}
			return "Download share feature enabled";
		}
		if ("download_dialog_share_disabled".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "下载分享功能已禁用" : "下載分享功能已禁用";
			}
			return "Download share feature disabled";
		}
		if ("disable_custom_tabs_switch".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "禁用CustomTabs" : "禁用CustomTabs";
			}
			return "Disable CustomTabs";
		}
		if ("disable_custom_tabs_hint".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "将CustomTab重定向到普通浏览器页面" : "將CustomTab重定向到普通瀏覽器頁面";
			}
			return "Redirect CustomTab to normal browser page";
		}
		if ("new_year_wish".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "2026新年快乐！感谢您使用BetterVia！" : "2026新年快樂！感謝您使用BetterVia！";
			}
			return "Happy New Year 2026! Thank you for using BetterVia!";
		}
		if ("new_year_message".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "愿新的一年里：代码无bug，功能更强大！" : "願新的一年裡：代碼無bug，功能更強大！";
			}
			return "Wish you in the new year: No bugs in code, more powerful features!";
		}
		if ("new_year_title".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "🎉 2026 新年快乐！ 🎉" : "🎉 2026 新年快樂！ 🎉";
			}
			return "🎉 Happy New Year 2026! 🎉";
		}
		if ("new_year_wish_text".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country)
					? "感谢您使用BetterVia！愿新的一年里：\n✨ 代码无bug，功能更强大 ✨\n🎁 更新更及时，体验更流畅 🎁"
					: "感謝您使用BetterVia！願新的一年裡：\n✨ 代碼無bug，功能更強大 ✨\n🎁 更新更及時，體驗更流暢 🎁";
			}
			return "Thanks for using BetterVia! May the new year bring:\n✨ Bug-free code and more powerful features ✨\n🎁 Timelier updates and smoother experience 🎁";
		}
		if ("new_year_toast".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "新年快乐，感谢支持" : "新年快樂，感謝支持";
			}
			return "Happy New Year and thanks for your support";
		}
		if ("new_year_fireworks_text".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "2026 新年快乐" : "2026 新年快樂";
			}
			return "Happy New Year 2026";
		}
		if ("new_year_fireworks_subtitle".equals(key))
		{
			if ("zh".equals(lang))
			{
				return "CN".equals(country) ? "BetterVia 感谢您的支持！" : "BetterVia 感謝您的支持！";
			}
			return "BetterVia thanks for your support!";
		}
		return "";
	}
	private static Locale getUserLocale(Context ctx)
	{
		try
		{
			String saved = getSavedLanguageStatic(ctx);
			if ("auto".equals(saved))
			{
				Locale locale;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					locale = ctx.getResources().getConfiguration().getLocales().get(0);
				}
				else
				{
					locale = ctx.getResources().getConfiguration().locale;
				}
				return locale;
			}
			else if ("zh-CN".equals(saved))
			{
				return Locale.SIMPLIFIED_CHINESE;
			}
			else if ("zh-TW".equals(saved))
			{
				return Locale.TRADITIONAL_CHINESE;
			}
			else if ("en".equals(saved))
			{
				return Locale.ENGLISH;
			}
			return Locale.getDefault();
		}
		catch (Exception e)
		{
			return Locale.getDefault();
		}
	}
}