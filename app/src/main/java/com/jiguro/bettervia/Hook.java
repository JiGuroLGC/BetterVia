package com.jiguro.bettervia;

import android.animation.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.ClipboardManager;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
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

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;

import org.json.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.cert.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

public class Hook implements IXposedHookLoadPackage {

    static final String MODULE_VERSION_NAME = "2.3.0";
    static final int MODULE_VERSION_CODE = 20261114;
    static final String MODULE_BUILD_TIME = "2026/11/14 17:13";
    private static final String SUPPORTED_VIA_VERSION = "7.4.1";

    private static Activity Context = null;
    private static Object moduleButtonRef = null;
    private static final List<Object> moduleButtons = new ArrayList<Object>();
    static String currentPackageName = "";
    private static Activity currentActivity = null;

    static final String KEY_WHITELIST = "enable_whitelist_hook";
    static final String KEY_BLOCK_STARTUP_MESSAGE = "block_startup_message";
    static final String KEY_BLOCK_GOOGLE_SERVICES = "block_google_services";
    static final String KEY_DOWNLOAD_DIALOG_SHARE = "download_dialog_share";
    static final String KEY_SHOW_URL_SCHEME = "show_url_scheme";
    static final String KEY_LONG_PRESS_SPEED = "long_press_speed";
    static final String KEY_FREE_ZOOM = "free_zoom";
    static final String KEY_EYE_PROTECTION = "eye_protection_mode";
    static final String KEY_EYE_TEMPERATURE = "eye_protection_temperature";
    static final String KEY_EYE_TEXTURE = "eye_protection_texture";
    static final String KEY_HOMEPAGE_BG = "homepage_background_image";
    static final String KEY_HOMEPAGE_MASK_A = "homepage_mask_alpha";
    static final String KEY_HOMEPAGE_MASK_C = "homepage_mask_color";
    static final String KEY_BLOCK_MENU_BAR = "block_menu_bar_urls";
    static final String KEY_HIDE_STATUS_BAR = "hide_status_bar";
    static final String KEY_RESTORE_OLD_SEARCH_BOX = "restore_old_search_box";
    static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    static final String KEY_SCREENSHOT_PROTECTION = "screenshot_protection";
    static final String KEY_RANDOM_UA = "random_ua";
    static final String KEY_UA_PLATFORM_ANDROID = "ua_platform_android";
    static final String KEY_UA_PLATFORM_IOS = "ua_platform_ios";
    static final String KEY_UA_PLATFORM_WINDOWS = "ua_platform_windows";
    static final String KEY_UA_PLATFORM_MACOS = "ua_platform_macos";
    static final String KEY_UA_PLATFORM_LINUX = "ua_platform_linux";
    static final String KEY_UA_BROWSER_CHROME = "ua_browser_chrome";
    static final String KEY_UA_BROWSER_SAFARI = "ua_browser_safari";
    static final String KEY_UA_BROWSER_EDGE = "ua_browser_edge";
    static final String KEY_UA_BROWSER_FIREFOX = "ua_browser_firefox";
    static final String KEY_UA_ANDROID_VERSIONS = "ua_android_versions";
    static final String KEY_UA_ANDROID_DEVICES = "ua_android_devices";
    static final String KEY_UA_IOS_VERSIONS = "ua_ios_versions";
    static final String KEY_UA_WINDOWS_TOKENS = "ua_windows_tokens";
    static final String KEY_UA_MACOS_TOKENS = "ua_macos_tokens";
    static final String KEY_UA_LINUX_TOKENS = "ua_linux_tokens";
    static final String KEY_NETWORK_SOURCE = "network_source";
    static final String KEY_HAS_NETWORK_SOURCE = "has_network_source";
    static final String KEY_MODULE_THEME = "module_theme";
    static final String KEY_AUTO_UPDATE = "auto_update";
    static final String KEY_LAST_AUTO_UPDATE_DATE = "last_auto_update_date";
    static final String KEY_LAST_ANNOUNCEMENT_CHECK_DATE = "last_announcement_check_date";
    static final String KEY_CUSTOM_TOAST = "custom_toast";
    static final String KEY_DISABLE_PAGE_RIPPLE = "disable_page_ripple";
    static final String KEY_SETTINGS_SEARCH = "settings_search_enable";
    static final String KEY_CURRENT_THEME = "current_homepage_theme";
    static final String KEY_BACKGROUND_VIDEO = "background_video_audio";
    static final String KEY_VERSION_CHECK_DISABLED = "version_check_disabled";
    static final String KEY_SELECTED_VIA_VERSION = "selected_via_version";
    static final String KEY_HAS_LANGUAGE_SELECTION = "has_language_selection";
    static final String KEY_HAS_USER_AGREEMENT = "has_user_agreement";
    static final String KEY_DEVELOPER_MODE = "developer_mode";
    static final String KEY_BLOCK_SWIPE_BACK = "block_swipe_back";

    static final String KEY_LAST_VIA_VERSION_CODE = "last_via_version_code";
    static final String KEY_LAST_VIA_VERSION_NAME = "last_via_version_name";
    static final String KEY_LAST_VIA_UPDATE_TIME = "last_via_update_time";
    static final String KEY_NEED_CLEAR_NETWORK_MAPPINGS = "need_clear_network_mappings";
    static final String KEY_SKIP_CLEAR_NETWORK_MAPPINGS = "skip_clear_network_mappings";

    static final String KEY_USER_SANDBOX_ENABLE = "user_sandbox_enable";
    static final String KEY_USER_SANDBOX_HIDE_DOWNLOAD = "user_sandbox_hide_download";
    static final String KEY_USER_SANDBOX_HIDE_CACHE = "user_sandbox_hide_cache";

    static final String KEY_ONLINE_PREVIEW_ENABLE = "online_preview_enable";
    static final String KEY_ONLINE_PREVIEW_WORD = "online_preview_word";
    static final String KEY_ONLINE_PREVIEW_PPT = "online_preview_ppt";
    static final String KEY_ONLINE_PREVIEW_EXCEL = "online_preview_excel";
    static final String KEY_ONLINE_PREVIEW_PDF = "online_preview_pdf";
    static final String KEY_ONLINE_PREVIEW_SOURCE = "online_preview_source";

    private static boolean blockGoogleServicesEnabled = false;
    private static boolean blockStartupMessageEnabled = false;
    private static boolean screenshotProtectionEnabled = false;
    private static boolean randomUaEnabled = false;
    private static boolean keepScreenOnEnabled = false;
    private static boolean hideStatusBarEnabled = false;
    private static boolean autoUpdateEnabled = true;
    private static boolean downloadDialogShareEnabled = false;
    private static boolean showUrlSchemeEnabled = false;
    private static String homepageBgPath = "";
    private static int homepageMaskAlpha = 120;
    private static int homepageMaskColor = 0x80000000;
    private static boolean backgroundVideoEnabled = false;
    private static boolean developerModeEnabled = false;
    private static boolean blockSwipeBackEnabled = false;
    private static PrintWriter logWriter = null;
    private static String logFilePath = null;
    static int selectedViaVersionCode = 20261113;
    static String currentDetectedVersion = null;
    static long moduleStartElapsedMs = 0;
    static boolean hasShownStartupDialog = false;

    private static XC_MethodHook.Unhook firebaseAnalyticsHook = null;
    private static XC_MethodHook.Unhook googleAnalyticsHook = null;
    private static XC_MethodHook.Unhook downloadDialogShareHook = null;
    private static XC_MethodHook.Unhook downloadDialogCreateHook = null;
    private static final List<XC_MethodHook.Unhook> showUrlSchemeHooks =
            new ArrayList<XC_MethodHook.Unhook>();
    private static final List<XC_MethodHook.Unhook> backgroundVideoHooks =
            new ArrayList<XC_MethodHook.Unhook>();
    private static XC_MethodHook.Unhook swipeBackHook = null;
    private static XC_MethodHook.Unhook randomUaGetSettingsHook = null;
    private static XC_MethodHook.Unhook randomUaGetHook = null;
    private static XC_MethodHook.Unhook randomUaSetHook = null;
    private static String currentRandomUa = null;
    private static boolean uaAndroid = true,
            uaIos = true,
            uaWindows = false,
            uaMacos = false,
            uaLinux = false;
    private static boolean uaChrome = true, uaSafari = true, uaEdge = false, uaFirefox = false;
    private static String uaAndroidVersions = "9,10,11,12,13,14,15,16,17";
    private static String uaAndroidDevices =
            "SM-G9910,SM-S9080,SM-S9180,Pixel 7,Pixel 8,Pixel 9,"
                    + "M2012K11AC,23127PN0CC,"
                    + "PGT-AN00,ALN-AL80,BRA-AL00,"
                    + "CPH2581,PHN110,PJV110,PJG110,"
                    + "RMX3850,RMX3706,RMX3888,"
                    + "LE2120,NE2210,PHB110,"
                    + "XQ-DQ72,XQ-CT72,"
                    + "V2357A,V2405A,V2429A,"
                    + "24090RA29C,24094RAD4C,25010PN30C";
    private static String uaIosVersions = "15.0,15.1,16.0,16.1,16.2,17.0,17.1,17.2,18.0,18.1,18.2";
    private static String uaWindowsTokens = "Windows NT 10.0; Win64; x64";
    private static String uaMacosTokens = "Macintosh; Intel Mac OS X 10_15_7";
    private static String uaLinuxTokens = "X11; Linux x86_64";

    private MonetMomentManager monetManager;

    private BossGestureHelper bossGestureHelper;

    static final String KEY_PERFECT_EXIT = "perfect_exit";
    private XC_MethodHook.Unhook perfectExitHook;
    private boolean perfectExitEnabled = false;

    private static final String HUB_EYE_PROTECTION = "eye_protection";
    private static final String HUB_SCREENSHOT = "screenshot_protection";
    private static final String HUB_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String HUB_HIDE_STATUS_BAR = "hide_status_bar";

    private static final List<View> urlSchemeOverlays = new ArrayList<View>();
    private static final List<TextView> urlSchemeTextViews = new ArrayList<TextView>();
    private static final List<String> displayedUrlSchemes = new ArrayList<String>();
    private final List<Handler> urlSchemeDismissHandlers = new ArrayList<Handler>();
    private static final int URL_SCHEME_DISPLAY_DURATION = 8000;

    public static final String NETWORK_SOURCE_VERCEL = "vercel";
    public static final String NETWORK_SOURCE_GITHUB = "github";
    public static final String DEFAULT_NETWORK_SOURCE = NETWORK_SOURCE_VERCEL;

    public static boolean isValidNetworkSource(String source) {
        return NETWORK_SOURCE_VERCEL.equals(source) || NETWORK_SOURCE_GITHUB.equals(source);
    }

    private static final String MODULE_THEME_AUTO = ThemeColors.THEME_AUTO;
    private static final String MODULE_THEME_LIGHT = ThemeColors.THEME_LIGHT;
    private static final String MODULE_THEME_DARK = ThemeColors.THEME_DARK;
    private static final String MODULE_THEME_QINGXIA = ThemeColors.THEME_QINGXIA;
    private static final String DEFAULT_MODULE_THEME = ThemeColors.DEFAULT_THEME;

    private static final String VERCEL_THEMES_JSON_URL = "https://raw.196104.xyz/themes_new.json";
    private static final String GITHUB_THEMES_JSON_URL =
            "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/themes_new.json";
    private static final String GITHUB_UPDATE_URL =
            "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/update.json";
    private static final String VERCEL_UPDATE_URL = "https://raw.196104.xyz/update.json";
    private static final String VERCEL_SHISUI_JSON_URL = "https://raw.196104.xyz/shisui.json";
    private static final String GITHUB_SHISUI_JSON_URL =
            "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/shisui.json";

    public static final String VERCEL_MAPPING_JSON_URL = "https://raw.196104.xyz/mapping.json";
    public static final String GITHUB_MAPPING_JSON_URL =
            "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/mapping.json";

    private static List<ThemeInfo> loadedThemes = new ArrayList<>();
    private static boolean themesLoaded = false;
    private static boolean themesLoading = false;
    private static Map<Activity, Boolean> screenshotActivities = new WeakHashMap<>();
    private static Map<Activity, Boolean> screenOnActivities = new WeakHashMap<>();
    private static Map<Activity, Boolean> statusBarHiddenActivities = new WeakHashMap<>();
    private static Map<Activity, Runnable> statusBarRehideRunnables = new WeakHashMap<>();
    private static final int REHIDE_DELAY = 3000;
    static final long RESTART_VIA_DELAY_MS = 1500;
    private static final String DEFAULT_THEME_ID = "default";
    private static final String COOKIE_TABLE_NAME = "cookies";

    public static class ThemeInfo {

        String id;
        Map<String, String> nameMap;
        Map<String, String> authorMap;
        String previewUrl;
        Map<String, String> htmlUrls;
        Map<String, String> cssUrls;

        ThemeInfo(
                String id,
                Map<String, String> nameMap,
                Map<String, String> authorMap,
                String previewUrl,
                Map<String, String> htmlUrls,
                Map<String, String> cssUrls) {
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
        if (moduleStartElapsedMs == 0L) {
            moduleStartElapsedMs = SystemClock.elapsedRealtime();
        }
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
            handleViaApp();
        } catch (ClassNotFoundException e) {
        }
    }

    private void handleViaApp() {
        XposedHelpers.findAndHookMethod(
                Application.class,
                "attach",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam attachParam) throws Throwable {
                        final Context ctx = (Context) attachParam.args[0];
                        final ClassLoader cl = ctx.getClassLoader();

                        if (monetManager == null) {
                            monetManager = new MonetMomentManager(Hook.this);
                        }

                        if (bossGestureHelper == null) {
                            bossGestureHelper = new BossGestureHelper(Hook.this);
                        }
                        if (getPrefBoolean(ctx, BossGestureHelper.KEY_BOSS_GESTURE, false)) {
                            bossGestureHelper.startMonitoring(ctx);
                        }

                        if (getPrefBoolean(ctx, KEY_PERFECT_EXIT, false)) {
                            setPerfectExitHook(ctx, cl, true);
                        }

                        new Handler(Looper.getMainLooper())
                                .postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    boolean hasVersionSelection =
                                                            getPrefBoolean(
                                                                    ctx,
                                                                    "has_version_selection",
                                                                    false);
                                                    if (!hasVersionSelection || sViaUpdateDetected)
                                                        return;

                                                    String testClass =
                                                            ViaClassMapping.getClassName(
                                                                    ViaClassMapping.ClassMethodKey
                                                                            .PRIVACY_LOCK_WHITELIST,
                                                                    ctx);
                                                    if (testClass != null && !testClass.isEmpty()) {
                                                        cl.loadClass(testClass);
                                                        bvLog(
                                                                "[BetterVia] 版本校验通过，类名: "
                                                                        + testClass);
                                                    } else {
                                                        bvLog("[BetterVia] 无法获取类名，显示版本错误提示");
                                                        showVersionErrorDialog(ctx);
                                                    }
                                                } catch (ClassNotFoundException e) {
                                                    bvLog(
                                                            "[BetterVia] 版本校验失败，类不存在: "
                                                                    + e.getMessage());
                                                    showVersionErrorDialog(ctx);
                                                } catch (Throwable t) {
                                                    bvLog("[BetterVia] 版本校验异常: " + t);
                                                }
                                            }
                                        },
                                        3000);

                        boolean needClearNetworkMappings =
                                getPrefBoolean(ctx, KEY_NEED_CLEAR_NETWORK_MAPPINGS, false);
                        boolean skipClearNetworkMappings =
                                getPrefBoolean(ctx, KEY_SKIP_CLEAR_NETWORK_MAPPINGS, false);

                        if (needClearNetworkMappings && !skipClearNetworkMappings) {
                            bvLog("[BetterVia] 检测到Via更新，清理旧的网络映射配置");
                            ViaClassMapping.clearNetworkMappings(ctx);

                            putPrefBoolean(ctx, KEY_NEED_CLEAR_NETWORK_MAPPINGS, false);
                            bvLog("[BetterVia] 已清理网络映射，重置清理标记");
                        } else if (skipClearNetworkMappings) {
                            bvLog("[BetterVia] 检测到同步后重启，跳过清理以保留新映射");

                            putPrefBoolean(ctx, KEY_SKIP_CLEAR_NETWORK_MAPPINGS, false);
                            bvLog("[BetterVia] 已重置跳过清理标记");
                        }

                        ViaVersionDetector.restoreSupportedVersions(ctx);
                        ViaClassMapping.restoreVersionMappings(ctx);
                        bvLog("[BetterVia] 已恢复持久化的版本和映射数据");

                        ViaVersionDetector.VersionInfo viaVersion =
                                ViaVersionDetector.detectViaVersion(ctx);
                        if (viaVersion != null) {
                            bvLog(
                                    "[BetterVia] 检测到Via版本: "
                                            + viaVersion.versionName
                                            + " (code: "
                                            + viaVersion.versionCode
                                            + ")");
                        } else {
                            bvLog("[BetterVia] 无法检测Via版本，使用默认类名映射");
                        }

                        int savedVersionCode = getPrefInt(ctx, KEY_SELECTED_VIA_VERSION, 20261113);
                        selectedViaVersionCode = savedVersionCode;

                        ViaClassMapping.setUserSelectedVersionCode(savedVersionCode);

                        int userSelectedVersionCode = selectedViaVersionCode;
                        Map<ViaClassMapping.ClassMethodKey, ViaClassMapping.ClassMethodMapping>
                                classMethodMappings =
                                        ViaClassMapping.getClassMethodMappingsByVersionCode(
                                                userSelectedVersionCode);
                        bvLog(
                                "[BetterVia] 类和方法映射表已加载（使用版本: "
                                        + ViaVersionDetector.getVersionName(userSelectedVersionCode)
                                        + "）");

                        setupPredictiveBackOrderKeeper(ctx);

                        XposedHelpers.findAndHookMethod(
                                Toast.class,
                                "makeText",
                                Context.class,
                                CharSequence.class,
                                int.class,
                                new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param)
                                            throws Throwable {
                                        if (param.getResult() == null) return;

                                        CharSequence msg = (CharSequence) param.args[1];
                                        if (msg == null) return;
                                        String message = msg.toString();
                                        if (message.contains("token")
                                                && message.contains("not valid")) {
                                            StackTraceElement[] stack =
                                                    Thread.currentThread().getStackTrace();
                                            for (StackTraceElement el : stack) {
                                                if (el.getClassName()
                                                        .contains("mark.via.BrowserApp")) {
                                                    bvLog(
                                                            "[BetterVia] 已屏蔽 BrowserApp 的"
                                                                + " BadTokenException Toast: "
                                                                    + message);
                                                    param.setResult(null);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                });

                        if (Context == null) {
                            XposedHelpers.findAndHookMethod(
                                    Activity.class,
                                    "onCreate",
                                    Bundle.class,
                                    new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            final Activity activity = (Activity) param.thisObject;
                                            final boolean startupImageEnabled =
                                                    StartupExecutionHelper.getStartupImageEnable(
                                                            activity);
                                            if (!startupImageEnabled) return;

                                            final boolean privacyLockEnabled =
                                                    PrivacyLockManager.isEnabled(activity);
                                            final boolean applyStartup =
                                                    PrivacyLockManager.appliesToStartup(activity);

                                            if (privacyLockEnabled && applyStartup) {
                                                bvLogVerbose("[BetterVia] 隐私锁已启用，启动图将在隐私锁覆盖层上显示");
                                                return;
                                            }
                                            new Handler(Looper.getMainLooper())
                                                    .post(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    bvLogVerbose(
                                                                            "[BetterVia]"
                                                                                + " 在onCreate之前显示启动图");
                                                                    StartupExecutionHelper
                                                                            .showStartupImageEarly(
                                                                                    activity,
                                                                                    new Runnable() {
                                                                                        @Override
                                                                                        public void
                                                                                                run() {
                                                                                            bvLogVerbose(
                                                                                                    "[BetterVia]"
                                                                                                        + " 早期启动图显示完成");
                                                                                        }
                                                                                    });
                                                                }
                                                            });
                                        }

                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            if (Context == null) {
                                                Context = (Activity) param.thisObject;
                                                StartupCheckChain.start(Hook.this, ctx);
                                                if (!getPrefBoolean(
                                                        ctx, KEY_BLOCK_STARTUP_MESSAGE, false)) {
                                                    jiguroMessage(
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(
                                                                            ctx,
                                                                            "hook_success_message"));
                                                }
                                                bvLog("[BetterVia] 初加载成功，得到Via活动上下文");

                                                final boolean privacyLockEnabled =
                                                        PrivacyLockManager.isEnabled(ctx);
                                                final boolean applyStartup =
                                                        PrivacyLockManager.appliesToStartup(ctx);
                                                final boolean startupImageEnabled =
                                                        StartupExecutionHelper
                                                                .getStartupImageEnable(ctx);

                                                if (!privacyLockEnabled || !applyStartup) {
                                                    new Handler()
                                                            .postDelayed(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (!Context
                                                                                            .isFinishing()
                                                                                    && !Context
                                                                                            .isDestroyed()) {
                                                                                bvLog(
                                                                                        "[BetterVia]"
                                                                                            + " 无隐私锁模式，执行音乐和提示");
                                                                                StartupExecutionHelper
                                                                                        .executeStartupWithoutImage(
                                                                                                Context,
                                                                                                new Runnable() {
                                                                                                    @Override
                                                                                                    public
                                                                                                    void
                                                                                                            run() {
                                                                                                        bvLog(
                                                                                                                "[BetterVia]"
                                                                                                                    + " 无隐私锁模式，启动执行流程完成");
                                                                                                        showAnnouncementIfReady(
                                                                                                                ctx);
                                                                                                    }
                                                                                                });
                                                                            }
                                                                        }
                                                                    },
                                                                    200);
                                                } else {
                                                    if (startupImageEnabled) {
                                                        bvLog(
                                                                "[BetterVia]"
                                                                    + " 隐私锁已启用，启动图已在beforeHookedMethod中显示，等待密码验证后执行音乐和提示");
                                                    } else {
                                                        bvLog(
                                                                "[BetterVia]"
                                                                    + " 隐私锁已启用，启动图未启用，等待密码验证后执行音乐和提示");
                                                    }
                                                }
                                            }
                                            currentActivity = (Activity) param.thisObject;
                                            try {
                                                developerModeEnabled =
                                                        getPrefBoolean(
                                                                (Context) param.thisObject,
                                                                KEY_DEVELOPER_MODE,
                                                                false);
                                                if (developerModeEnabled) {
                                                    initLogFile();
                                                    bvLog("[BetterVia] 初加载成功，得到Via活动上下文");
                                                    bvLogVerbose("[BetterVia] 恢复开发者模式：日志写入初始化完成");
                                                }
                                            } catch (Throwable t) {
                                                XposedBridge.log("[BetterVia] 恢复开发者模式失败: " + t);
                                            }
                                        }
                                    });
                            XposedHelpers.findAndHookMethod(
                                    Activity.class,
                                    "onResume",
                                    new XC_MethodHook() {
                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            currentActivity = (Activity) param.thisObject;
                                        }
                                    });
                        }

                        boolean hasLanguageSelection =
                                getPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, false);
                        String savedNetworkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, "");
                        boolean hasValidNetworkSource = isValidNetworkSource(savedNetworkSource);
                        boolean hasUserAgreement =
                                getPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
                        boolean hasVersionSelection =
                                getPrefBoolean(ctx, "has_version_selection", false);
                        final boolean setupComplete =
                                hasLanguageSelection
                                        && hasValidNetworkSource
                                        && hasUserAgreement
                                        && hasVersionSelection;

                        installFeatureHooks(ctx, cl, setupComplete);

                        PrivacyLockManager.install(ctx, cl);

                        installSettingsEntry(ctx, cl, setupComplete);

                        installActivityLifecycleHooks();

                        if (setupComplete) {
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

    private void installFeatureHooks(
            final Context ctx, final ClassLoader cl, boolean setupComplete) {
        if (setupComplete) {
            EyeProtectionManager.init(ctx);
            WhitelistHelper.init(ctx, cl);
            ComponentBlockHelper.init(ctx, cl);
            screenshotProtectionEnabled = getPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, false);
            setScreenshotProtection(ctx, cl, screenshotProtectionEnabled);
            randomUaEnabled = getPrefBoolean(ctx, KEY_RANDOM_UA, false);
            setRandomUa(ctx, cl, randomUaEnabled);
            uaAndroid = getPrefBoolean(ctx, KEY_UA_PLATFORM_ANDROID, true);
            uaIos = getPrefBoolean(ctx, KEY_UA_PLATFORM_IOS, true);
            uaWindows = getPrefBoolean(ctx, KEY_UA_PLATFORM_WINDOWS, false);
            uaMacos = getPrefBoolean(ctx, KEY_UA_PLATFORM_MACOS, false);
            uaLinux = getPrefBoolean(ctx, KEY_UA_PLATFORM_LINUX, false);
            uaChrome = getPrefBoolean(ctx, KEY_UA_BROWSER_CHROME, true);
            uaSafari = getPrefBoolean(ctx, KEY_UA_BROWSER_SAFARI, true);
            uaEdge = getPrefBoolean(ctx, KEY_UA_BROWSER_EDGE, false);
            uaFirefox = getPrefBoolean(ctx, KEY_UA_BROWSER_FIREFOX, false);
            uaAndroidVersions = getPrefString(ctx, KEY_UA_ANDROID_VERSIONS, uaAndroidVersions);
            uaAndroidDevices = getPrefString(ctx, KEY_UA_ANDROID_DEVICES, uaAndroidDevices);
            uaIosVersions = getPrefString(ctx, KEY_UA_IOS_VERSIONS, uaIosVersions);
            uaWindowsTokens = getPrefString(ctx, KEY_UA_WINDOWS_TOKENS, uaWindowsTokens);
            uaMacosTokens = getPrefString(ctx, KEY_UA_MACOS_TOKENS, uaMacosTokens);
            uaLinuxTokens = getPrefString(ctx, KEY_UA_LINUX_TOKENS, uaLinuxTokens);
            hideStatusBarEnabled = getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false);
            setHideStatusBar(ctx, cl, hideStatusBarEnabled);
            blockSwipeBackEnabled = getPrefBoolean(ctx, KEY_BLOCK_SWIPE_BACK, false);
            setBlockSwipeBack(ctx, cl, blockSwipeBackEnabled);

            homepageBgPath = getPrefString(ctx, KEY_HOMEPAGE_BG, "");
            homepageMaskAlpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
            homepageMaskColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x80000000);
            hookHomepageInjection(ctx, cl, homepageBgPath, homepageMaskColor);
            hookSearchBoxRestore(ctx, cl);

            boolean blockGoogleServices = getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false);
            setGoogleServicesInterceptHook(ctx, cl, blockGoogleServices);
            SettingsSearchHelper.init(ctx, cl, this);
            SettingsSearchHelper.setEnabled(
                    getPrefBoolean(ctx, KEY_SETTINGS_SEARCH, isViaVersionAtLeast(20261113)));
            downloadDialogShareEnabled = getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false);
            if (downloadDialogShareEnabled) {
                setDownloadDialogShareHook(ctx, cl, true);
            }
            showUrlSchemeEnabled = getPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, false);
            if (showUrlSchemeEnabled) {
                setUrlSchemeHook(ctx, cl, true);
            }
            OnlinePreviewHandler.init(ctx, cl);
            UrlCorrectionHelper.init(ctx, cl, Hook.this);
            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    LongPressMenuHelper.init(ctx, cl);
                                    ListSearchEnhance.init(ctx, cl);
                                    AutoRefreshHelper.init(ctx, cl);
                                    bvLog("[BetterVia] 页面级功能已就绪");
                                }
                            },
                            500);
            VideoPlayerHook.initVideoPlayerHook(ctx, cl);
            TimeProvider.init(ctx);
            autoUpdateEnabled = getPrefBoolean(ctx, KEY_AUTO_UPDATE, true);
            if (autoUpdateEnabled) {
                checkUpdateOnStart(ctx);
            }

            keepScreenOnEnabled = getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false);
            setKeepScreenOn(ctx, cl, keepScreenOnEnabled);
            backgroundVideoEnabled = getPrefBoolean(ctx, KEY_BACKGROUND_VIDEO, false);
            setBackgroundVideoAudio(ctx, cl, backgroundVideoEnabled);
        } else {
            bvLog("[BetterVia] 用户未完成初始设置（语言选择/用户协议/版本选择），暂不执行Hook功能（隐私锁除外）");
        }
    }

    private void installSettingsEntry(
            final Context ctx, final ClassLoader cl, boolean setupComplete) {
        if (setupComplete) {
            String privacyLockWhitelistClass =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_WHITELIST, ctx);
            String privacyLockWhitelistMethod =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_WHITELIST, ctx);
            String privacyLockOverlayClass =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_OVERLAY, ctx);
            String privacyLockOverlayMethod =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_OVERLAY, ctx);

            try {
                XposedHelpers.findAndHookMethod(
                        privacyLockWhitelistClass,
                        cl,
                        privacyLockWhitelistMethod,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                List<Object> orig = (List<Object>) param.getResult();
                                if (orig == null) orig = new ArrayList<>();
                                List<Object> nList = new ArrayList<>(orig);

                                String settingsItemClass =
                                        ViaClassMapping.getClassName(
                                                ViaClassMapping.ClassMethodKey.SETTINGS_ITEM_CLASS,
                                                ctx);
                                Class<?> yClass = XposedHelpers.findClass(settingsItemClass, cl);
                                String txt =
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "module_settings");
                                Object btn = XposedHelpers.newInstance(yClass, 1000, txt);
                                moduleButtonRef = btn;
                                moduleButtons.add(btn);
                                while (moduleButtons.size() > 8) {
                                    moduleButtons.remove(0);
                                }
                                nList.add(btn);

                                param.setResult(nList);
                                bvLog("[BetterVia] 已在Via设置列表中添加模块按钮");
                            }
                        });
                XposedHelpers.findAndHookMethod(
                        privacyLockOverlayClass,
                        cl,
                        privacyLockOverlayMethod,
                        View.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                Object clicked =
                                        XposedHelpers.getObjectField(param.thisObject, "d");
                                if (clicked == null) return;
                                if (isModuleButton(clicked)) {
                                    bvLog("[BetterVia] 模块按钮被点击");
                                    if (getPrefBoolean(
                                            ctx,
                                            KEY_DISABLE_PAGE_RIPPLE,
                                            isViaVersionAtLeast(20260823))) {
                                        Object rowArg = param.args[0];
                                        if (rowArg instanceof View) {
                                            Drawable bg = ((View) rowArg).getBackground();
                                            if (bg != null) {
                                                bg.jumpToCurrentState();
                                            }
                                        }
                                    }
                                    showSettingsPage(ctx);
                                }
                            }
                        });
                bvLog("[BetterVia] 模块按钮Hook已设置");
            } catch (Throwable t) {
                bvLog("[BetterVia] Hook模块按钮失败: " + t);
            }

            setupSettingsPageBackKeyHook(cl);
        } else {
            bvLog("[BetterVia] 用户未完成初始设置，暂不添加模块按钮");
        }
    }

    private void installActivityLifecycleHooks() {
        XposedHelpers.findAndHookMethod(
                Activity.class,
                "onActivityResult",
                int.class,
                int.class,
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        int req = (Integer) param.args[0];
                        int res = (Integer) param.args[1];
                        Intent data = (Intent) param.args[2];
                        handleActivityResult(req, res, data, (Activity) param.thisObject);
                    }
                });
        XposedHelpers.findAndHookMethod(
                Activity.class,
                "onDestroy",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        screenOnActivities.remove(activity);
                        statusBarHiddenActivities.remove(activity);
                        EyeProtectionManager.onActivityDestroyed(activity);
                        screenshotActivities.remove(activity);
                        Runnable rehideRunnable = statusBarRehideRunnables.get(activity);
                        if (rehideRunnable != null) {
                            View decorView = activity.getWindow().getDecorView();
                            decorView.removeCallbacks(rehideRunnable);
                            statusBarRehideRunnables.remove(activity);
                        }
                        if (currentActivity == activity) {
                            currentActivity = null;
                        }
                        PrivacyLockManager.onActivityDestroyed(activity);
                        if (SettingsUI.isPageActive()) {
                            bvLog("[BetterVia] Activity销毁，清理模块设置页");
                            SettingsUI.dismissAllPages();
                        }
                    }
                });
    }

    private void setupPredictiveBackOrderKeeper(final Context appCtx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        try {
            Class<?> winDispatcher = null;
            try {
                winDispatcher = Class.forName("android.window.WindowOnBackInvokedDispatcher");
            } catch (ClassNotFoundException e1) {
                winDispatcher = Class.forName("android.view.WindowOnBackInvokedDispatcher");
            }
            XposedBridge.hookAllMethods(
                    winDispatcher,
                    "registerOnBackInvokedCallback",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (param.args == null || param.args.length < 2) return;
                            if (!isViaBackCallback(param.args[1])) return;
                            SettingsUI.resyncPredictiveBackCallback();
                        }
                    });
            XposedBridge.hookAllMethods(
                    winDispatcher,
                    "unregisterOnBackInvokedCallback",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (param.args == null || param.args.length < 1) return;
                            if (!isViaBackCallback(param.args[0])) return;
                            SettingsUI.resyncPredictiveBackCallback();
                        }
                    });
            bvLog("[BetterVia] 预测性返回顺序维护已设置");
        } catch (Throwable t) {
            bvLog("[BetterVia] 设置预测性返回顺序维护失败: " + t);
        }
    }

    private static boolean isViaBackCallback(Object cb) {
        if (cb == null) return false;
        Class<?> c = cb.getClass();
        if (java.lang.reflect.Proxy.isProxyClass(c)) return false;
        String name = c.getName();
        if (name.startsWith("com.jiguro.bettervia")) return false;
        return true;
    }

    private void setupSettingsPageBackKeyHook(final ClassLoader cl) {
        try {
            XC_MethodHook hook =
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!SettingsUI.isPageActive()) return;
                            Object arg = param.args.length > 0 ? param.args[0] : null;
                            if (!(arg instanceof KeyEvent)) return;
                            KeyEvent event = (KeyEvent) arg;
                            if (event.getAction() == KeyEvent.ACTION_DOWN
                                    && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                                Object thisObj = param.thisObject;
                                if (thisObj instanceof Activity) {
                                    bvLog("[BetterVia] 返回键拦截：关闭模块设置页");
                                    SettingsUI.dismissCurrentPage((Activity) thisObj);
                                    param.setResult(Boolean.TRUE);
                                }
                            }
                        }
                    };
            boolean dispatchHooked = false;
            try {
                XposedHelpers.findAndHookMethod(
                        "mark.via.Shell", cl, "dispatchKeyEvent", KeyEvent.class, hook);
                bvLog("[BetterVia] 模块设置页返回键拦截已设置（Shell.dispatchKeyEvent）");
                dispatchHooked = true;
            } catch (Throwable t) {
            }
            if (!dispatchHooked) {
                try {
                    XposedHelpers.findAndHookMethod(
                            "mark.via.CustomTab", cl, "dispatchKeyEvent", KeyEvent.class, hook);
                    bvLog("[BetterVia] 模块设置页返回键拦截已设置（CustomTab.dispatchKeyEvent）");
                    dispatchHooked = true;
                } catch (Throwable t) {
                }
            }
            if (!dispatchHooked) {
                try {
                    XposedHelpers.findAndHookMethod(
                            Activity.class, "dispatchKeyEvent", KeyEvent.class, hook);
                    bvLog("[BetterVia] 模块设置页返回键拦截已设置（Activity.dispatchKeyEvent）");
                    dispatchHooked = true;
                } catch (Throwable t) {
                    bvLog("[BetterVia] 设置模块设置页返回键拦截失败: " + t);
                }
            }
            try {
                XposedHelpers.findAndHookMethod(
                        Activity.class,
                        "onBackPressed",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                if (!SettingsUI.isPageActive()) return;
                                if (!SettingsUI.isViaPredictiveBackOptedOut(
                                        (Context) param.thisObject)) return;
                                Object thisObj = param.thisObject;
                                if (thisObj instanceof Activity) {
                                    bvLog("[BetterVia] onBackPressed 拦截：关闭模块设置页（预测性返回已禁用兜底）");
                                    SettingsUI.dismissCurrentPage((Activity) thisObj);
                                    param.setResult(null);
                                }
                            }
                        });
                bvLog("[BetterVia] 模块设置页 onBackPressed 兜底拦截已设置");
            } catch (Throwable t) {
                bvLog("[BetterVia] 设置模块设置页 onBackPressed 兜底拦截失败: " + t);
            }
        } catch (Throwable t) {
            bvLog("[BetterVia] 设置模块设置页返回键拦截失败: " + t);
        }
    }

    private static boolean isModuleButton(Object clicked) {
        if (clicked == null) return false;
        if (clicked == moduleButtonRef) return true;
        for (Object btn : moduleButtons) {
            if (clicked == btn) return true;
            try {
                if (clicked.equals(btn)) return true;
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private void applySettingsSearch(Context ctx, SettingsList list, boolean enabled) {
        putPrefBoolean(ctx, KEY_SETTINGS_SEARCH, enabled);
        SettingsSearchHelper.setEnabled(enabled);
        if (list != null) {
            list.setSwitchChecked("settings_search_switch", enabled);
        }
    }

    void openSettingsPageForSearch(final Context ctx, int pageId, String titleKey) {
        if (titleKey != null && pageId >= 1 && pageId <= 8) {
            SettingsList.requestHighlight(titleKey);
        }
        switch (pageId) {
            case 1:
                showBasicPage(ctx);
                break;
            case 2:
                showAppearancePage(ctx);
                break;
            case 3:
                showPrivacyPage(ctx);
                break;
            case 4:
                showPlaybackPage(ctx);
                break;
            case 5:
                showRepositoryPage(ctx);
                break;
            case 6:
                showOtherPage(ctx);
                break;
            case 7:
                showModulePage(ctx);
                break;
            case 8:
                AboutPage.show(this, ctx);
                break;
            case 9:
                InfoPage.show(this, ctx);
                break;
            default:
                break;
        }
    }

    void showSettingsPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            bvLog("[BetterVia] showSettingsPage: 找不到有效的 Activity");
            return;
        }
        SettingsUI.showPage(
                act,
                "module_settings",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addItem(
                                "category_basic",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showBasicPage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_appearance",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showAppearancePage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_privacy",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showPrivacyPage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_playback",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showPlaybackPage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_repository",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showRepositoryPage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_other",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showOtherPage(ctx);
                                    }
                                });
                        list.addItem(
                                "category_module",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showModulePage(ctx);
                                    }
                                });
                        list.addItem(
                                "info_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        InfoPage.show(Hook.this, ctx);
                                    }
                                });
                        list.addItem(
                                "about_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        AboutPage.show(Hook.this, ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showBasicPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_basic",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addItem(
                                "whitelist_switch",
                                "whitelist_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        WhitelistHelper.showPage(act, ctx);
                                    }
                                });
                        list.addSwitchItem(
                                "keep_screen_on_switch",
                                null,
                                getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, isChecked);
                                        setKeepScreenOn(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, false));
                                    }
                                });
                        list.addSwitchItem(
                                "download_dialog_share_switch",
                                "download_dialog_share_hint",
                                getPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, isChecked);
                                        setDownloadDialogShareHook(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(
                                                        ctx, KEY_DOWNLOAD_DIALOG_SHARE, false));
                                    }
                                });
                        list.addSwitchItem(
                                "show_url_scheme_switch",
                                "show_url_scheme_hint",
                                getPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, isChecked);
                                        setUrlSchemeHook(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(ctx, KEY_SHOW_URL_SCHEME, false));
                                    }
                                });
                        list.addSwitchItem(
                                "hide_status_bar_switch",
                                null,
                                getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, isChecked);
                                        setHideStatusBar(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, false));
                                    }
                                });
                        list.addSwitchItem(
                                "perfect_exit_switch",
                                "perfect_exit_hint",
                                getPrefBoolean(ctx, KEY_PERFECT_EXIT, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_PERFECT_EXIT, isChecked);
                                        setPerfectExitHook(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(ctx, KEY_PERFECT_EXIT, false));
                                    }
                                });
                        list.addSwitchItem(
                                "auto_refresh_switch",
                                "auto_refresh_switch_hint",
                                getPrefBoolean(ctx, AutoRefreshHelper.KEY_ENABLE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(
                                                ctx, AutoRefreshHelper.KEY_ENABLE, isChecked);
                                    }
                                });

                        final boolean[] suppressSwitch = {false};
                        final boolean[] committed = {false};
                        list.addSwitchItem(
                                "restore_old_search_box_switch",
                                "restore_old_search_box_hint",
                                getPrefBoolean(ctx, KEY_RESTORE_OLD_SEARCH_BOX, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            final CompoundButton buttonView, boolean isChecked) {
                                        if (suppressSwitch[0] || committed[0]) return;
                                        if (isChecked && !isViaVersionAtLeast(20260823)) {
                                            SettingsUI.showMessageDialog(
                                                    act,
                                                    "restore_old_search_box_warn_title",
                                                    "restore_old_search_box_warn_msg",
                                                    "dialog_ok",
                                                    "dialog_cancel",
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            committed[0] = true;
                                                            buttonView.setEnabled(false);
                                                            commitSearchBoxRestore(ctx, true);
                                                        }
                                                    },
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            suppressSwitch[0] = true;
                                                            buttonView.setChecked(false);
                                                            suppressSwitch[0] = false;
                                                        }
                                                    });
                                        } else {
                                            committed[0] = true;
                                            buttonView.setEnabled(false);
                                            commitSearchBoxRestore(ctx, isChecked);
                                        }
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showModulePage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_module",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        list.addItem(
                                "version_selector_title",
                                "version_selector_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showViaVersionPage(ctx, false);
                                    }
                                });
                        list.addItemKeepRipple(
                                "language_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showLanguageSelectDialog(act, ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "network_source_title",
                                "network_source_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showNetworkSourceSelectDialog(act, ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "module_theme_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showModuleThemeSelectDialog(act, ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "storage_item_title",
                                "storage_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showStorageManagerDialog(act);
                                    }
                                });

                        list.addSwitchItem(
                                "auto_update_switch",
                                null,
                                getPrefBoolean(ctx, KEY_AUTO_UPDATE, true),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        autoUpdateEnabled = isChecked;
                                        putPrefBoolean(ctx, KEY_AUTO_UPDATE, isChecked);
                                    }
                                });
                        list.addSwitchItem(
                                "block_startup_message_switch",
                                "block_startup_message_hint",
                                getPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        blockStartupMessageEnabled = isChecked;
                                        putPrefBoolean(ctx, KEY_BLOCK_STARTUP_MESSAGE, isChecked);
                                    }
                                });
                        list.addSwitchItem(
                                "custom_toast_switch",
                                "custom_toast_hint",
                                getPrefBoolean(ctx, KEY_CUSTOM_TOAST, true),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_CUSTOM_TOAST, isChecked);
                                    }
                                });
                        final boolean defaultSettingsSearch = isViaVersionAtLeast(20261113);
                        final boolean[] searchSuppress = {false};
                        list.addSwitchItem(
                                "settings_search_switch",
                                "settings_search_hint",
                                getPrefBoolean(ctx, KEY_SETTINGS_SEARCH, defaultSettingsSearch),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            final CompoundButton buttonView, boolean isChecked) {
                                        if (searchSuppress[0]) {
                                            return;
                                        }
                                        if (isChecked && !isViaVersionAtLeast(20261113)) {
                                            SettingsUI.showMessageDialog(
                                                    act,
                                                    "settings_search_warn_title",
                                                    "settings_search_warn_msg",
                                                    "dialog_ok",
                                                    "dialog_cancel",
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            applySettingsSearch(ctx, list, true);
                                                        }
                                                    },
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            searchSuppress[0] = true;
                                                            buttonView.setChecked(false);
                                                            searchSuppress[0] = false;
                                                        }
                                                    });
                                            return;
                                        }
                                        applySettingsSearch(ctx, list, isChecked);
                                    }
                                });
                        final boolean defaultDisablePageRipple = isViaVersionAtLeast(20260823);
                        list.addSwitchItem(
                                "disable_page_ripple_switch",
                                "disable_page_ripple_hint",
                                getPrefBoolean(
                                        ctx, KEY_DISABLE_PAGE_RIPPLE, defaultDisablePageRipple),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_DISABLE_PAGE_RIPPLE, isChecked);
                                        list.setSwitchChecked(
                                                "disable_page_ripple_switch", isChecked);
                                        list.setDisablePageRipple(isChecked);
                                    }
                                });
                        list.addSwitchItem(
                                "developer_mode_switch",
                                "developer_mode_hint",
                                getPrefBoolean(ctx, KEY_DEVELOPER_MODE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        developerModeEnabled = isChecked;
                                        putPrefBoolean(ctx, KEY_DEVELOPER_MODE, isChecked);
                                        if (isChecked) {
                                            initLogFile();
                                        } else {
                                            closeLogFile();
                                        }
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showViaVersionPage(final Context ctx, final boolean recommendOnly) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        ViaVersionDetector.VersionInfo info = ViaVersionDetector.detectViaVersion(ctx);
        final int detectedVersionCode = (info != null) ? info.versionCode : -1;
        final int defaultVersionCode =
                findExactSupportedVersionCode(
                        ViaVersionDetector.getVersionName(detectedVersionCode));
        final ViewGroup[] contentRef = new ViewGroup[1];
        SettingsUI.showPage(
                act,
                "version_selector_title",
                "mapping_sync_config_title",
                new Runnable() {
                    @Override
                    public void run() {
                        performMappingSync(
                                ctx,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contentRef[0] != null) {
                                            buildViaVersionList(
                                                    ctx,
                                                    contentRef[0],
                                                    act,
                                                    defaultVersionCode,
                                                    detectedVersionCode,
                                                    recommendOnly);
                                        }
                                    }
                                });
                    }
                },
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        contentRef[0] = content;
                        buildViaVersionList(
                                ctx,
                                content,
                                act,
                                defaultVersionCode,
                                detectedVersionCode,
                                recommendOnly);
                    }
                },
                recommendOnly);
    }

    private static int findExactSupportedVersionCode(String versionName) {
        if (versionName == null || versionName.isEmpty()) {
            return -1;
        }
        String[] names = ViaVersionDetector.getSupportedVersionNames();
        int[] codes = ViaVersionDetector.getSupportedVersionCodes();
        for (int i = 0; i < names.length && i < codes.length; i++) {
            if (versionName.equals(names[i])) {
                return codes[i];
            }
        }
        return -1;
    }

    private void buildViaVersionList(
            final Context ctx,
            final ViewGroup content,
            final Activity act,
            final int defaultVersionCode,
            final int detectedVersionCode,
            final boolean recommendOnly) {
        content.removeAllViews();
        SettingsList list = new SettingsList(act);

        final int[] versionCodes = ViaVersionDetector.getSupportedVersionCodes();
        final String[] versionNames = ViaVersionDetector.getSupportedVersionNames();
        final int count = Math.min(versionCodes.length, versionNames.length);

        for (int i = 0; i < count; i++) {
            final int code = versionCodes[i];
            final String name = versionNames[i];
            final boolean checked = !recommendOnly && (code == defaultVersionCode);
            final boolean recommend = recommendOnly && (code == defaultVersionCode);
            list.addRadioItem(
                    name,
                    checked,
                    recommend,
                    new Runnable() {
                        @Override
                        public void run() {
                            showViaVersionConfirmDialog(
                                    ctx,
                                    act,
                                    content,
                                    code,
                                    name,
                                    defaultVersionCode,
                                    detectedVersionCode,
                                    recommendOnly);
                        }
                    });
        }

        content.addView(
                list,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void showViaVersionConfirmDialog(
            final Context ctx,
            final Activity act,
            final ViewGroup content,
            final int code,
            final String name,
            final int defaultVersionCode,
            final int detectedVersionCode,
            final boolean recommendOnly) {
        String msg =
                String.format(
                        LocalizedStringProvider.getInstance().get(ctx, "version_confirm_message"),
                        name);
        if (code != detectedVersionCode) {
            msg +=
                    "\n\n"
                            + LocalizedStringProvider.getInstance()
                                    .get(ctx, "version_confirm_warning");
        }
        SettingsUI.showMessageDialog(
                act,
                "version_confirm_title",
                (CharSequence) msg,
                null,
                false,
                "dialog_ok",
                "cancel",
                new Runnable() {
                    @Override
                    public void run() {
                        applyViaVersionSelection(ctx, code, name);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        buildViaVersionList(
                                ctx,
                                content,
                                act,
                                defaultVersionCode,
                                detectedVersionCode,
                                recommendOnly);
                    }
                },
                null,
                false);
    }

    private void applyViaVersionSelection(final Context ctx, final int code, final String name) {
        putPrefInt(ctx, KEY_SELECTED_VIA_VERSION, code);
        selectedViaVersionCode = code;
        ViaClassMapping.setUserSelectedVersionCode(code);
        ViaClassMapping.clearCache();
        putPrefBoolean(ctx, "has_version_selection", true);
        try {
            android.content.pm.PackageManager pm = ctx.getPackageManager();
            android.content.pm.PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            ViaVersionDetector.VersionInfo vi = ViaVersionDetector.detectViaVersion(ctx);
            if (vi != null) {
                putPrefInt(ctx, KEY_LAST_VIA_VERSION_CODE, vi.versionCode);
                putPrefString(ctx, KEY_LAST_VIA_VERSION_NAME, vi.versionName);
            }
            putPrefLong(
                    ctx,
                    KEY_LAST_VIA_UPDATE_TIME,
                    new java.io.File(pi.applicationInfo.sourceDir).lastModified());
        } catch (Throwable t) {
            bvLog("[BetterVia] 保存Via版本信息失败: " + t);
        }
        jiguroMessageWithContext(
                ctx, LocalizedStringProvider.getInstance().get(ctx, "startup_restart_hint"));
        bvLog("[BetterVia] 用户在设置中选择的Via版本: " + name + " (code: " + code + ")");

        new Handler(Looper.getMainLooper())
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        },
                        RESTART_VIA_DELAY_MS);
    }

    private void showLanguageSelectDialog(final Activity act, final Context ctx) {
        final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
        final String[] langKeys = {
            "language_auto", "language_zh_cn", "language_zh_tw", "language_en"
        };
        String savedLang = getSavedLanguage(ctx);
        int langIdx = 0;
        for (int i = 0; i < langValues.length; i++)
            if (langValues[i].equals(savedLang)) {
                langIdx = i;
                break;
            }
        SettingsUI.showSelectDialog(
                act,
                "language_title",
                langKeys,
                langIdx,
                new SettingsUI.OnSelectListener() {
                    @Override
                    public void onSelect(int index) {
                        saveLanguageSetting(ctx, langValues[index]);
                        showLanguageChangeToast(ctx, index);
                        refreshModuleButtonText(ctx);
                    }
                });
    }

    private void showNetworkSourceSelectDialog(final Activity act, final Context ctx) {
        final String[] sourceValues = {NETWORK_SOURCE_VERCEL, NETWORK_SOURCE_GITHUB};
        final String[] sourceKeys = {"network_source_vercel", "network_source_github"};
        String savedSource = getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
        int sourceIdx = savedSource.equals(NETWORK_SOURCE_VERCEL) ? 0 : 1;
        SettingsUI.showSelectDialog(
                act,
                "network_source_title",
                sourceKeys,
                sourceIdx,
                new SettingsUI.OnSelectListener() {
                    @Override
                    public void onSelect(int index) {
                        putPrefString(ctx, KEY_NETWORK_SOURCE, sourceValues[index]);
                        themesLoaded = false;
                        loadedThemes.clear();
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                                .get(ctx, "network_source_changed")
                                        + " "
                                        + LocalizedStringProvider.getInstance()
                                                .get(ctx, sourceKeys[index]));
                    }
                });
    }

    private void showModuleThemeSelectDialog(final Activity act, final Context ctx) {
        final String[] themeValues = {
            MODULE_THEME_AUTO, MODULE_THEME_LIGHT, MODULE_THEME_DARK,
        };
        final String[] themeKeys = {
            "module_theme_auto", "module_theme_light", "module_theme_dark",
        };
        String savedTheme = getModuleTheme(ctx);
        int themeIdx = 0;
        for (int i = 0; i < themeValues.length; i++) {
            if (themeValues[i].equals(savedTheme)) {
                themeIdx = i;
                break;
            }
        }
        SettingsUI.showSelectDialog(
                act,
                "module_theme_title",
                themeKeys,
                themeIdx,
                new SettingsUI.OnSelectListener() {
                    @Override
                    public void onSelect(int index) {
                        putPrefString(ctx, KEY_MODULE_THEME, themeValues[index]);
                        SettingsUI.notifyThemeChanged(ctx);
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                                .get(ctx, "module_theme_changed")
                                        + " "
                                        + LocalizedStringProvider.getInstance()
                                                .get(ctx, themeKeys[index]));
                    }
                });
    }

    private void performMappingSync(final Context ctx) {
        performMappingSync(ctx, null);
    }

    private void performMappingSync(final Context ctx, final Runnable onSuccess) {
        MappingSyncHelper.syncMappingFromNetwork(
                ctx,
                new MappingSyncHelper.SyncCallback() {
                    @Override
                    public void onSuccess(int addedVersions) {
                        jiguroMessageWithContext(
                                ctx,
                                String.format(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "mapping_sync_success"),
                                        addedVersions));
                        if (onSuccess != null) {
                            new Handler(Looper.getMainLooper()).post(onSuccess);
                        }
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "mapping_sync_failed"));
                    }

                    @Override
                    public void onNoUpdate() {
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "mapping_sync_no_update"));
                    }
                });
    }

    private void showPrivacyPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_privacy",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addSwitchItem(
                                "block_google_switch",
                                "block_google_hint",
                                getPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_BLOCK_GOOGLE_SERVICES, isChecked);
                                        setGoogleServicesInterceptHook(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(
                                                        ctx, KEY_BLOCK_GOOGLE_SERVICES, false));
                                    }
                                });
                        list.addSwitchItem(
                                "screenshot_protection_switch",
                                "screenshot_protection_hint",
                                getPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, isChecked);
                                        setScreenshotProtection(
                                                ctx,
                                                act.getClassLoader(),
                                                getPrefBoolean(
                                                        ctx, KEY_SCREENSHOT_PROTECTION, false));
                                    }
                                });
                        list.addItemKeepRipple(
                                "boss_gesture_title",
                                "boss_gesture_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (bossGestureHelper != null) {
                                            bossGestureHelper.showDialog(ctx);
                                        }
                                    }
                                });
                        list.addItemKeepRipple(
                                "random_ua_title",
                                "random_ua_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showRandomUaDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "privacy_lock_title",
                                "privacy_lock_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        PrivacyLockManager.showPrivacyLockDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "user_sandbox_title",
                                "user_sandbox_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showUserSandboxDialog(ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showPlaybackPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_playback",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addSwitchItem(
                                "long_press_speed_switch",
                                "long_press_speed_hint",
                                getPrefBoolean(ctx, KEY_LONG_PRESS_SPEED, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_LONG_PRESS_SPEED, isChecked);
                                        VideoPlayerHook.initVideoPlayerHook(
                                                ctx, act.getClassLoader());
                                    }
                                });
                        list.addSwitchItem(
                                "free_zoom_switch",
                                "free_zoom_hint",
                                getPrefBoolean(ctx, KEY_FREE_ZOOM, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(ctx, KEY_FREE_ZOOM, isChecked);
                                        VideoPlayerHook.initVideoPlayerHook(
                                                ctx, act.getClassLoader());
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showRepositoryPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_repository",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addItemKeepRipple(
                                "script_repository_title",
                                "script_repository_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showScriptRepositoryDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "ad_block_rules_title",
                                "ad_block_rules_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showAdBlockRulesDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "search_commands_title",
                                "search_commands_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showSearchCommandsDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "user_agent_title",
                                "user_agent_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showUserAgentDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "shisui_title",
                                "shisui_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showShisuiDialog(ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showOtherPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_other",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addItem(
                                "cookie_management_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        CookieManagerPage.show(act);
                                    }
                                });
                        list.addItemKeepRipple(
                                "startup_execution_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showStartupExecutionDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "online_preview_title",
                                "online_preview_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showOnlinePreviewDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "url_correction_title",
                                "url_correction_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        UrlCorrectionHelper.showDialog(ctx, Hook.this);
                                    }
                                });
                        list.addItem(
                                "long_press_menu_page_title",
                                "long_press_menu_page_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showLongPressMenuPage(ctx);
                                    }
                                });
                        list.addItem(
                                "list_search_page_title",
                                "list_search_page_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showListSearchPage(ctx);
                                    }
                                });
                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showLongPressMenuPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "long_press_menu_page_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addItemKeepRipple(
                                "block_menu_bar_title",
                                "block_menu_bar_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showBlockMenuBarDialog(ctx);
                                    }
                                });
                        list.addSwitchItem(
                                "long_press_menu_title",
                                "long_press_menu_hint",
                                getPrefBoolean(ctx, LongPressMenuHelper.KEY_ENABLE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(
                                                ctx, LongPressMenuHelper.KEY_ENABLE, isChecked);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showListSearchPage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "list_search_page_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addCheckItem(
                                "list_search_rule_page",
                                "list_search_rule_page_hint",
                                getPrefBoolean(ctx, ListSearchEnhance.KEY_ENABLE_RULE, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(
                                                ctx, ListSearchEnhance.KEY_ENABLE_RULE, isChecked);
                                        ListSearchEnhance.reinjectEnabledPages(ctx);
                                    }
                                });
                        list.addCheckItem(
                                "list_search_script_page",
                                "list_search_script_page_hint",
                                getPrefBoolean(ctx, ListSearchEnhance.KEY_ENABLE_SCRIPT, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        putPrefBoolean(
                                                ctx,
                                                ListSearchEnhance.KEY_ENABLE_SCRIPT,
                                                isChecked);
                                        ListSearchEnhance.reinjectEnabledPages(ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showAppearancePage(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "category_appearance",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);

                        list.addSwitchItem(
                                "eye_protection_switch",
                                "eye_protection_hint",
                                getPrefBoolean(ctx, KEY_EYE_PROTECTION, false),
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        EyeProtectionManager.setEnabled(act, isChecked);
                                    }
                                });
                        list.addItemKeepRipple(
                                "eye_protection_config",
                                "eye_protection_config_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showEyeProtectionConfigDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "monet_title",
                                "monet_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (monetManager != null) {
                                            monetManager.showDialog(ctx);
                                        }
                                    }
                                });
                        list.addItemKeepRipple(
                                "component_block_title",
                                "component_block_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showComponentBlockSelectDialog(act, ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "homepage_theme_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showHomepageThemeDialog(ctx);
                                    }
                                });
                        list.addItemKeepRipple(
                                "homepage_bg_title",
                                "homepage_bg_hint",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showHomepageBeautyDialog(ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showComponentBlockSelectDialog(final Activity act, final Context ctx) {
        final String[] componentNameKeys = {
            "component_update",
            "component_telegram",
            "component_qq",
            "component_email",
            "component_wechat",
            "component_donate",
            "component_assist",
            "component_agreement",
            "component_privacy",
            "component_opensource",
            "component_icp"
        };
        boolean[] checked = new boolean[ComponentBlockHelper.KEYS.length];
        for (int i = 0; i < ComponentBlockHelper.KEYS.length; i++) {
            checked[i] = getPrefBoolean(ctx, ComponentBlockHelper.KEYS[i], false);
        }
        SettingsUI.showMultiSelectDialog(
                act,
                "component_block_dialog_title",
                componentNameKeys,
                checked,
                "dialog_ok",
                "dialog_cancel",
                null,
                new SettingsUI.OnMultiSelectListener() {
                    @Override
                    public void onResult(int which, boolean[] result) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            for (int i = 0; i < ComponentBlockHelper.KEYS.length; i++) {
                                putPrefBoolean(ctx, ComponentBlockHelper.KEYS[i], result[i]);
                            }
                            ComponentBlockHelper.refresh(ctx, act.getClassLoader());
                            jiguroMessageWithContext(
                                    ctx,
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "component_block_saved"));
                        }
                    }
                });
    }

    interface SourceSelectedCallback {
        void onSelected(int pos);
    }

    private void showEyeProtectionConfigDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_config_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        title.setTextColor(textColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);

                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_config_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        subtitle.setTextColor(hintColor);
                        subtitle.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams subtitleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        previewTitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_preview_title"));
                        previewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        previewTitle.setTextColor(textColor);
                        previewTitle.setTypeface(null, Typeface.BOLD);
                        previewContainer.addView(previewTitle);

                        FrameLayout previewContent = new FrameLayout(act);
                        previewContent.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 80)));
                        previewContent.setBackgroundColor(Color.WHITE);
                        previewContent.setPadding(
                                dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));

                        GradientDrawable previewBg = new GradientDrawable();
                        previewBg.setColor(getBgColor(ctx));
                        previewBg.setStroke(dp(act, 1), getDividerColor(ctx));
                        previewBg.setCornerRadius(dp(act, 8));
                        previewContent.setBackground(previewBg);

                        TextView sampleText = new TextView(act);
                        sampleText.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_sample_text"));
                        sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        sampleText.setTextColor(getTextColor(ctx));

                        FrameLayout.LayoutParams textLp =
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        textLp.gravity = Gravity.CENTER;
                        previewContent.addView(sampleText, textLp);

                        final View previewOverlay =
                                new View(act) {
                                    @Override
                                    protected void onDraw(Canvas canvas) {
                                        super.onDraw(canvas);
                                        if (tempSeekBarRef[0] != null) {
                                            canvas.drawColor(
                                                    EyeProtectionManager.calculateTemperatureColor(
                                                            tempSeekBarRef[0].getProgress()));
                                        }
                                        if (textureSeekBarRef[0] != null
                                                && textureSeekBarRef[0].getProgress() > 0) {
                                            EyeProtectionManager.drawTexturePreview(
                                                    canvas,
                                                    textureSeekBarRef[0].getProgress(),
                                                    getWidth(),
                                                    getHeight());
                                        }
                                    }
                                };
                        previewOverlay.setClickable(false);
                        previewOverlay.setFocusable(false);

                        FrameLayout.LayoutParams overlayLp =
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
                        previewContent.addView(previewOverlay, overlayLp);
                        previewOverlayRef[0] = previewOverlay;

                        previewContainer.addView(previewContent);
                        root.addView(previewContainer);

                        LinearLayout tempContainer = new LinearLayout(act);
                        tempContainer.setOrientation(LinearLayout.VERTICAL);
                        tempContainer.setPadding(0, 0, 0, dp(act, 16));

                        TextView tempTitle = new TextView(act);
                        tempTitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_temperature"));
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

                        tempContainer.addView(
                                tempSeekBar,
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        LinearLayout tempLabels = new LinearLayout(act);
                        tempLabels.setOrientation(LinearLayout.HORIZONTAL);

                        TextView coldLabel = new TextView(act);
                        coldLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_cold"));
                        coldLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        coldLabel.setTextColor(hintColor);

                        LinearLayout.LayoutParams coldLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                        tempLabels.addView(coldLabel, coldLp);

                        TextView warmLabel = new TextView(act);
                        warmLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_warm"));
                        warmLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        warmLabel.setTextColor(hintColor);
                        warmLabel.setGravity(Gravity.END);

                        LinearLayout.LayoutParams warmLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                        tempLabels.addView(warmLabel, warmLp);

                        tempContainer.addView(
                                tempLabels,
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        root.addView(tempContainer);

                        LinearLayout textureContainer = new LinearLayout(act);
                        textureContainer.setOrientation(LinearLayout.VERTICAL);
                        textureContainer.setPadding(0, 0, 0, dp(act, 16));

                        TextView textureTitle = new TextView(act);
                        textureTitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_texture"));
                        textureTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        textureTitle.setTextColor(textColor);
                        textureTitle.setTypeface(null, Typeface.BOLD);
                        textureContainer.addView(textureTitle);

                        final SeekBar textureSeekBar = new SeekBar(act);
                        textureSeekBar.setMax(100);
                        textureSeekBar.setProgress(savedTexture);
                        textureSeekBarRef[0] = textureSeekBar;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textureSeekBar.setProgressTintList(
                                    ColorStateList.valueOf(okBtnBgColor));
                            textureSeekBar.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
                        }
                        textureContainer.addView(
                                textureSeekBar,
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        LinearLayout textureLabels = new LinearLayout(act);
                        textureLabels.setOrientation(LinearLayout.HORIZONTAL);

                        TextView smoothLabel = new TextView(act);
                        smoothLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_smooth"));
                        smoothLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        smoothLabel.setTextColor(hintColor);

                        LinearLayout.LayoutParams smoothLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                        textureLabels.addView(smoothLabel, smoothLp);

                        TextView roughLabel = new TextView(act);
                        roughLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_rough"));
                        roughLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        roughLabel.setTextColor(hintColor);
                        roughLabel.setGravity(Gravity.END);

                        LinearLayout.LayoutParams roughLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                        textureLabels.addView(roughLabel, roughLp);

                        textureContainer.addView(
                                textureLabels,
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        root.addView(textureContainer);

                        TextView previewHint = new TextView(act);
                        previewHint.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "eye_protection_preview_hint"));
                        previewHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        previewHint.setTextColor(hintColor);
                        previewHint.setGravity(Gravity.CENTER);
                        previewHint.setTypeface(null, Typeface.ITALIC);

                        LinearLayout.LayoutParams hintLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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

                        LinearLayout.LayoutParams okLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        okLp.topMargin = dp(act, 8);
                        root.addView(ok, okLp);

                        scrollRoot.addView(root);

                        final AlertDialog dialog =
                                new AlertDialog.Builder(act).setView(scrollRoot).create();

                        Window win = dialog.getWindow();
                        if (win != null) {
                            win.setBackgroundDrawableResource(android.R.color.transparent);

                            GradientDrawable round = new GradientDrawable();
                            round.setColor(bgColor);
                            round.setCornerRadius(dp(act, 24));
                            win.setBackgroundDrawable(round);

                            win.setGravity(Gravity.CENTER);
                            DisplayMetrics dialogMetrics = new DisplayMetrics();
                            act.getWindowManager().getDefaultDisplay().getMetrics(dialogMetrics);
                            int dialogWidth = (int) (dialogMetrics.widthPixels * 0.9);
                            WindowManager.LayoutParams dialogLp = new WindowManager.LayoutParams();
                            dialogLp.copyFrom(win.getAttributes());
                            dialogLp.width = dialogWidth;
                            dialogLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            dialogLp.gravity = Gravity.CENTER;
                            win.setAttributes(dialogLp);
                        }

                        SeekBar.OnSeekBarChangeListener previewListener =
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(
                                            SeekBar seekBar, int progress, boolean fromUser) {
                                        if (fromUser && previewOverlayRef[0] != null) {
                                            previewOverlayRef[0].invalidate();
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {}

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                };

                        tempSeekBar.setOnSeekBarChangeListener(previewListener);
                        textureSeekBar.setOnSeekBarChangeListener(previewListener);

                        ok.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int newTemperature = tempSeekBar.getProgress();
                                        int newTexture = textureSeekBar.getProgress();

                                        EyeProtectionManager.applyConfig(
                                                ctx, newTemperature, newTexture);

                                        jiguroMessageWithContext(
                                                ctx,
                                                LocalizedStringProvider.getInstance()
                                                        .get(ctx, "eye_protection_config_saved"));
                                        dialog.dismiss();
                                    }
                                });

                        dialog.show();
                        animateDialogEntrance(root, act);
                    }
                });
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
                    String firebaseAnalyticsClass =
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.FIREBASE_ANALYTICS, ctx);
                    String firebaseAnalyticsMethod =
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.FIREBASE_ANALYTICS, ctx);

                    Class<?> analyticsClass =
                            XposedHelpers.findClassIfExists(firebaseAnalyticsClass, cl);
                    if (analyticsClass != null) {
                        firebaseAnalyticsHook =
                                XposedHelpers.findAndHookMethod(
                                        analyticsClass,
                                        firebaseAnalyticsMethod,
                                        String.class,
                                        Bundle.class,
                                        new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param)
                                                    throws Throwable {
                                                bvLog(
                                                        "[BetterVia] 拦截Firebase Analytics事件: "
                                                                + param.args[0]);
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
                    Class<?> appMeasurementClass =
                            XposedHelpers.findClassIfExists(
                                    "com.google.android.gms.measurement.AppMeasurement", cl);
                    if (appMeasurementClass != null) {
                        googleAnalyticsHook =
                                XposedHelpers.findAndHookMethod(
                                        appMeasurementClass,
                                        "logEventInternal",
                                        String.class,
                                        String.class,
                                        Bundle.class,
                                        new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param)
                                                    throws Throwable {
                                                bvLog(
                                                        "[BetterVia] 拦截AppMeasurement事件: "
                                                                + param.args[0]
                                                                + ", "
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

    private void showSearchCommandsDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "search_commands_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);
                        final String[][] commands = {
                            {"javascript:via.cmd(257);", "command_bookmark"},
                            {"javascript:via.cmd(514);", "command_search"},
                            {"javascript:via.cmd(515);", "command_unknown"},
                            {"javascript:via.cmd(516);", "command_print"},
                            {"javascript:via.cmd(517);", "command_adblock"},
                            {"v://log", "command_log"},
                            {"v://home", "command_home"},
                            {"v://skins", "command_skins"},
                            {"v://about", "command_about"},
                            {"v://search", "command_search_page"},
                            {"v://offline", "command_offline"},
                            {"v://history", "command_history"},
                            {"v://scanner", "command_scanner"},
                            {"v://bookmarks", "command_bookmarks_page"},
                            {"v://downloader", "command_downloader"},
                            {"v://readaloud", "command_readaloud"},
                            {"v://translator/translate?text=", "command_translator"},
                            {"history://", "command_history_page"},
                            {"folder://", "command_folder"}
                        };
                        for (int i = 0; i < commands.length; i++) {
                            final String command = commands[i][0];
                            list.addItemKeepRipple(
                                    commands[i][1],
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            copyToClipboard(act, command);
                                            jiguroMessageWithContext(
                                                    act,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(act, "command_copied"));
                                        }
                                    });
                            list.updateItemText(list.getItemCount() - 1, command, true);
                        }
                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private void showHomepageThemeDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        title.setTextColor(textColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);
                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        subtitle.setTextColor(hintColor);
                        subtitle.setGravity(Gravity.CENTER);
                        subtitle.setPadding(0, 0, 0, dp(act, 24));
                        root.addView(subtitle);
                        final LinearLayout themesContainer = new LinearLayout(act);
                        themesContainer.setOrientation(LinearLayout.VERTICAL);
                        themesContainer.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                        LinearLayout.LayoutParams iconLp =
                                new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
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

                        LinearLayout.LayoutParams okLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        okLp.rightMargin = dp(act, 8);
                        buttonContainer.addView(ok, okLp);
                        Button edit = new Button(act);
                        applyClickAnim(edit);
                        edit.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_edit"));
                        edit.setTextColor(okBtnTextColor);
                        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        edit.setTypeface(null, Typeface.BOLD);
                        edit.setPadding(0, dp(act, 14), 0, dp(act, 14));
                        edit.setBackground(getRoundBg(act, okBtnBgColor, 12));

                        LinearLayout.LayoutParams editLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        editLp.leftMargin = dp(act, 8);
                        buttonContainer.addView(edit, editLp);

                        root.addView(
                                buttonContainer,
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                            WindowManager.LayoutParams layoutParams =
                                    new WindowManager.LayoutParams();
                            layoutParams.copyFrom(window.getAttributes());
                            layoutParams.width = width;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity = Gravity.CENTER;
                            window.setAttributes(layoutParams);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                window.setClipToOutline(true);
                            }
                        }
                        ok.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                        edit.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        showThemeEditorDialog(ctx);
                                    }
                                });
                        showLoadingState(
                                act, ctx, themesContainer, emptyStateContainer, emptyStateText);
                        if (!themesLoaded && !themesLoading) {
                            loadThemesFromNetwork(
                                    ctx,
                                    new ThemesLoadCallback() {
                                        @Override
                                        public void onThemesLoaded(List<ThemeInfo> themes) {
                                            loadedThemes = themes;
                                            themesLoaded = true;
                                            themesLoading = false;
                                            if (act != null && !act.isFinishing()) {
                                                act.runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                refreshThemesList(
                                                                        act,
                                                                        ctx,
                                                                        themesContainer,
                                                                        emptyStateContainer,
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
                                                act.runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                showErrorState(
                                                                        act,
                                                                        ctx,
                                                                        themesContainer,
                                                                        emptyStateContainer,
                                                                        emptyStateText,
                                                                        error);
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
                            refreshThemesList(
                                    act, ctx, themesContainer, emptyStateContainer, emptyStateText);
                        }

                        dialog.show();
                        animateDialogEntrance(root, act);
                    }
                });
    }

    private void showLoadingState(
            Activity act,
            Context ctx,
            LinearLayout themesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText) {
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
        LinearLayout.LayoutParams progressLp =
                new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
        progressLp.gravity = Gravity.CENTER;
        progressLp.bottomMargin = dp(act, 16);
        emptyStateContainer.addView(progressBar, 0, progressLp);
    }

    private void showErrorState(
            Activity act,
            Context ctx,
            LinearLayout themesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            String error) {
        themesContainer.removeAllViews();
        themesContainer.setVisibility(View.GONE);

        emptyStateContainer.setVisibility(View.VISIBLE);
        emptyStateText.setText(
                LocalizedStringProvider.getInstance().get(ctx, "themes_load_failed")
                        + "\n"
                        + LocalizedStringProvider.getInstance().get(ctx, "check_network"));
        for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
            View child = emptyStateContainer.getChildAt(i);
            if (child instanceof ImageView) {
                child.setVisibility(View.VISIBLE);
            } else if (child instanceof ProgressBar) {
                emptyStateContainer.removeView(child);
            }
        }

        jiguroMessageWithContext(
                ctx,
                LocalizedStringProvider.getInstance().get(ctx, "themes_load_failed")
                        + ": "
                        + error);
    }

    private LinearLayout createThemeCard(
            final Activity act, final Context ctx, final ThemeInfo theme) {
        LinearLayout themeCard = new LinearLayout(act);
        themeCard.setOrientation(LinearLayout.VERTICAL);
        themeCard.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(getItemBgColor(ctx));
        cardBg.setStroke(dp(act, 1), getDividerColor(ctx));
        cardBg.setCornerRadius(dp(act, 12));
        themeCard.setBackground(cardBg);
        FrameLayout imageContainer = new FrameLayout(act);
        imageContainer.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 150)));
        imageContainer.setBackground(getRoundBg(act, getEditBgColor(ctx), 8));
        final ProgressBar loadingSpinner = new ProgressBar(act);
        loadingSpinner.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingSpinner.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
        }
        FrameLayout.LayoutParams spinnerParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams.gravity = Gravity.CENTER;
        imageContainer.addView(loadingSpinner, spinnerParams);
        final ImageView previewImage = new ImageView(act);
        previewImage.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        previewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewImage.setVisibility(View.GONE);
        imageContainer.addView(previewImage);

        themeCard.addView(imageContainer);
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(theme.previewUrl);
                                    HttpURLConnection connection =
                                            (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.setConnectTimeout(10000);
                                    connection.setReadTimeout(10000);
                                    connection.setRequestProperty(
                                            "User-Agent",
                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                                                + " AppleWebKit/537.36");

                                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        InputStream input = connection.getInputStream();
                                        final Bitmap bitmap = BitmapFactory.decodeStream(input);
                                        input.close();

                                        if (bitmap != null) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            previewImage.setImageBitmap(bitmap);
                                                            previewImage.setVisibility(
                                                                    View.VISIBLE);
                                                            loadingSpinner.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                    }
                                    connection.disconnect();
                                } catch (Exception e) {
                                    act.runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    int iconSize = dp(act, 48);
                                                    Bitmap errorBitmap =
                                                            Bitmap.createBitmap(
                                                                    iconSize,
                                                                    iconSize,
                                                                    Bitmap.Config.ARGB_8888);
                                                    Canvas canvas = new Canvas(errorBitmap);
                                                    Paint backgroundPaint = new Paint();
                                                    backgroundPaint.setColor(getEditBgColor(ctx));
                                                    canvas.drawCircle(
                                                            iconSize / 2,
                                                            iconSize / 2,
                                                            iconSize / 2,
                                                            backgroundPaint);
                                                    Drawable errorIcon =
                                                            act.getResources()
                                                                    .getDrawable(
                                                                            android.R.drawable
                                                                                    .ic_menu_gallery);
                                                    errorIcon.setBounds(
                                                            iconSize / 4,
                                                            iconSize / 4,
                                                            (iconSize * 3) / 4,
                                                            (iconSize * 3) / 4);
                                                    errorIcon.draw(canvas);

                                                    previewImage.setImageBitmap(errorBitmap);
                                                    previewImage.setScaleType(
                                                            ImageView.ScaleType.CENTER);
                                                    previewImage.setColorFilter(getHintColor(ctx));
                                                    previewImage.setVisibility(View.VISIBLE);
                                                    loadingSpinner.setVisibility(View.GONE);
                                                }
                                            });
                                }
                            }
                        })
                .start();
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
                LocalizedStringProvider.getInstance().get(ctx, "homepage_theme_by")
                        + " "
                        + theme.getAuthor(ctx));
        themeAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        themeAuthor.setTextColor(getHintColor(ctx));
        infoLayout.addView(themeAuthor);

        themeCard.addView(infoLayout);
        themeCard.setOnClickListener(
                new View.OnClickListener() {
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

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            getPrefString(
                                                    ctx,
                                                    KEY_NETWORK_SOURCE,
                                                    DEFAULT_NETWORK_SOURCE);
                                    String jsonUrl =
                                            networkSource.equals(NETWORK_SOURCE_VERCEL)
                                                    ? VERCEL_THEMES_JSON_URL
                                                    : GITHUB_THEMES_JSON_URL;

                                    URL url = new URL(jsonUrl);
                                    HttpURLConnection connection =
                                            (HttpURLConnection) url.openConnection();
                                    connection.setConnectTimeout(15000);
                                    connection.setReadTimeout(15000);
                                    connection.setRequestProperty(
                                            "User-Agent",
                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                                                + " AppleWebKit/537.36");

                                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = connection.getInputStream();
                                        BufferedReader reader =
                                                new BufferedReader(
                                                        new InputStreamReader(
                                                                inputStream, "UTF-8"));
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
                                        callback.onLoadFailed(
                                                "HTTP " + connection.getResponseCode());
                                    }

                                    connection.disconnect();
                                } catch (Exception e) {
                                    callback.onLoadFailed(e.getMessage());
                                }
                            }
                        })
                .start();
    }

    private void refreshThemesList(
            final Activity act,
            final Context ctx,
            LinearLayout themesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText) {
        themesContainer.removeAllViews();

        if (themesLoading) {
            showLoadingState(act, ctx, themesContainer, emptyStateContainer, emptyStateText);
            return;
        }

        if (loadedThemes == null || loadedThemes.isEmpty()) {
            showErrorState(
                    act,
                    ctx,
                    themesContainer,
                    emptyStateContainer,
                    emptyStateText,
                    "No themes available");
            return;
        }

        themesContainer.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);

        for (final ThemeInfo theme : loadedThemes) {
            LinearLayout themeCard = createThemeCard(act, ctx, theme);
            LinearLayout.LayoutParams cardLp =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            cardLp.bottomMargin = dp(ctx, 12);
            themesContainer.addView(themeCard, cardLp);
        }
    }

    private void showThemeEditorDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;
                        final Dialog dialog =
                                new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
                        dialog.setCancelable(true);
                        LinearLayout rootLayout = new LinearLayout(act);
                        rootLayout.setOrientation(LinearLayout.VERTICAL);
                        rootLayout.setBackgroundColor(getBgColor(ctx));
                        RelativeLayout titleBar = new RelativeLayout(act);
                        titleBar.setBackgroundColor(getItemBgColor(ctx));
                        titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
                        titleBar.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        ImageButton backButton = new ImageButton(act);
                        backButton.setImageResource(android.R.drawable.ic_menu_revert);
                        backButton.setBackgroundResource(android.R.color.transparent);
                        backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
                        backButton.setColorFilter(getTextColor(ctx));
                        RelativeLayout.LayoutParams backButtonLp =
                                new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
                        backButtonLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        backButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
                        titleBar.addView(backButton, backButtonLp);
                        TextView title = new TextView(act);
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_editor_title"));
                        title.setTextColor(getTextColor(ctx));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        title.setTypeface(null, Typeface.BOLD);
                        RelativeLayout.LayoutParams titleLp =
                                new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.addRule(RelativeLayout.CENTER_IN_PARENT);
                        titleBar.addView(title, titleLp);
                        rootLayout.addView(titleBar);
                        LinearLayout contentLayout = new LinearLayout(act);
                        contentLayout.setOrientation(LinearLayout.VERTICAL);
                        contentLayout.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                        contentLayout.setPadding(
                                dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
                        TextView fileLabel = new TextView(act);
                        fileLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "theme_editor_select_file"));
                        fileLabel.setTextColor(getTextColor(ctx));
                        fileLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        fileLabel.setPadding(0, 0, 0, dp(act, 8));
                        contentLayout.addView(fileLabel);
                        LinearLayout fileButtonGroup = new LinearLayout(act);
                        fileButtonGroup.setOrientation(LinearLayout.HORIZONTAL);
                        fileButtonGroup.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        final Button htmlButton = new Button(act);
                        applyClickAnim(htmlButton);
                        htmlButton.setText("homepage2.html");
                        htmlButton.setTextColor(getOkBtnTextColor(ctx));
                        htmlButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        htmlButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 6));
                        htmlButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
                        LinearLayout.LayoutParams htmlLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        htmlLp.rightMargin = dp(act, 8);
                        fileButtonGroup.addView(htmlButton, htmlLp);
                        final Button cssButton = new Button(act);
                        applyClickAnim(cssButton);
                        cssButton.setText("homepage.css");
                        cssButton.setTextColor(getHintColor(ctx));
                        cssButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cssButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 6));
                        cssButton.setPadding(dp(act, 12), dp(act, 6), dp(act, 12), dp(act, 6));
                        LinearLayout.LayoutParams cssLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        fileButtonGroup.addView(cssButton, cssLp);
                        contentLayout.addView(fileButtonGroup);
                        TextView editorLabel = new TextView(act);
                        editorLabel.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "theme_editor_edit_content"));
                        editorLabel.setTextColor(getTextColor(ctx));
                        editorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        editorLabel.setPadding(0, dp(act, 16), 0, dp(act, 8));
                        contentLayout.addView(editorLabel);
                        final ScrollView editorScroll = new ScrollView(act);
                        editorScroll.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
                        final EditText codeEditor = new EditText(act);
                        codeEditor.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                        buttonBar.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        buttonBar.setPadding(0, dp(act, 16), 0, 0);
                        Button cancelButton = new Button(act);
                        applyClickAnim(cancelButton);
                        cancelButton.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
                        cancelButton.setTextColor(getBtnTextColor(ctx));
                        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        cancelButton.setBackground(getRoundBg(act, getBtnBgColor(ctx), 8));
                        cancelButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
                        LinearLayout.LayoutParams cancelLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        cancelLp.rightMargin = dp(act, 8);
                        buttonBar.addView(cancelButton, cancelLp);
                        Button saveButton = new Button(act);
                        applyClickAnim(saveButton);
                        saveButton.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
                        saveButton.setTextColor(getOkBtnTextColor(ctx));
                        saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        saveButton.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));
                        saveButton.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
                        LinearLayout.LayoutParams saveLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        buttonBar.addView(saveButton, saveLp);
                        contentLayout.addView(buttonBar);
                        rootLayout.addView(contentLayout);
                        dialog.setContentView(rootLayout);
                        final String[] currentFile = {"homepage2.html"};
                        loadFileContent(act, "homepage2.html", codeEditor, editorScroll, true);
                        htmlButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveCurrentEditorState(
                                                currentFile[0], codeEditor, editorScroll);

                                        currentFile[0] = "homepage2.html";
                                        htmlButton.setTextColor(getOkBtnTextColor(ctx));
                                        htmlButton.setBackground(
                                                getRoundBg(act, getOkBtnBgColor(ctx), 6));
                                        cssButton.setTextColor(getHintColor(ctx));
                                        cssButton.setBackground(
                                                getRoundBg(act, getBtnBgColor(ctx), 6));
                                        loadFileContent(
                                                act,
                                                "homepage2.html",
                                                codeEditor,
                                                editorScroll,
                                                true);
                                    }
                                });
                        cssButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveCurrentEditorState(
                                                currentFile[0], codeEditor, editorScroll);

                                        currentFile[0] = "homepage.css";
                                        cssButton.setTextColor(getOkBtnTextColor(ctx));
                                        cssButton.setBackground(
                                                getRoundBg(act, getOkBtnBgColor(ctx), 6));
                                        htmlButton.setTextColor(getHintColor(ctx));
                                        htmlButton.setBackground(
                                                getRoundBg(act, getBtnBgColor(ctx), 6));
                                        loadFileContent(
                                                act,
                                                "homepage.css",
                                                codeEditor,
                                                editorScroll,
                                                true);
                                    }
                                });

                        editorScroll
                                .getViewTreeObserver()
                                .addOnScrollChangedListener(
                                        new ViewTreeObserver.OnScrollChangedListener() {
                                            @Override
                                            public void onScrollChanged() {
                                                if (currentFile[0] != null
                                                        && editorStateCache.containsKey(
                                                                currentFile[0])) {
                                                    EditorState oldState =
                                                            editorStateCache.get(currentFile[0]);
                                                    editorStateCache.put(
                                                            currentFile[0],
                                                            new EditorState(
                                                                    oldState.content,
                                                                    editorScroll.getScrollY()));
                                                }
                                            }
                                        });

                        dialog.setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        long currentTime = System.currentTimeMillis();
                                        Iterator<Map.Entry<String, EditorState>> it =
                                                editorStateCache.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, EditorState> entry = it.next();
                                            if (currentTime - entry.getValue().timestamp
                                                    > 10 * 60 * 1000) {
                                                it.remove();
                                            }
                                        }
                                    }
                                });
                        backButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                        cancelButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                        saveButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveFileContent(
                                                act,
                                                currentFile[0],
                                                codeEditor.getText().toString());
                                        if (editorScroll != null) {
                                            int scrollY = editorScroll.getScrollY();
                                            editorStateCache.put(
                                                    currentFile[0],
                                                    new EditorState(
                                                            codeEditor.getText().toString(),
                                                            scrollY));
                                        }

                                        jiguroMessageWithContext(
                                                act,
                                                LocalizedStringProvider.getInstance()
                                                        .get(ctx, "theme_editor_save_success"));
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
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setTitle(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_apply_title"));
                        builder.setMessage(
                                LocalizedStringProvider.getInstance()
                                                .get(ctx, "homepage_theme_apply_message")
                                        + " \""
                                        + theme.getName(ctx)
                                        + "\"?");

                        builder.setPositiveButton(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_apply"),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        applyHomepageTheme(ctx, theme);
                                    }
                                });

                        builder.setNegativeButton(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"),
                                null);

                        AlertDialog dialog = builder.create();
                        applyAlertDialogTheme(act, ctx, dialog);
                        dialog.show();
                    }
                });
    }

    private void applyHomepageTheme(final Context ctx, final ThemeInfo theme) {
        ThemeApplier applier = new ThemeApplier(ctx, theme);
        applier.applyTheme(
                new ThemeApplier.ThemeApplyCallback() {
                    @Override
                    public void onSuccess() {
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_theme_apply_success"));
                        new Handler(Looper.getMainLooper())
                                .postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                restartVia(ctx);
                                            }
                                        },
                                        RESTART_VIA_DELAY_MS);
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
                if (outputStream != null) outputStream.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                bvLog("[BetterVia] 关闭流失败: " + e);
            }
        }
        return false;
    }

    private boolean replacePackageNameInFiles(
            String htmlFilePath, String cssFilePath, String oldPackageName, String newPackageName) {
        boolean htmlSuccess =
                replacePackageNameInFile(htmlFilePath, oldPackageName, newPackageName);
        boolean cssSuccess = replacePackageNameInFile(cssFilePath, oldPackageName, newPackageName);

        if (htmlSuccess && cssSuccess) {
            bvLog("[BetterVia] 已将文件中的包名从 " + oldPackageName + " 替换为 " + newPackageName);
            return true;
        } else {
            bvLog("[BetterVia] 替换文件中的包名失败");
            return false;
        }
    }

    private boolean replacePackageNameInFile(
            String filePath, String oldPackageName, String newPackageName) {
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
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
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
            ActivityLifecycleHub.add(
                    HUB_SCREENSHOT,
                    new ActivityLifecycleHub.Listener() {
                        @Override
                        public void onActivityCreated(
                                Activity activity, Bundle savedInstanceState) {
                            if (!screenshotProtectionEnabled) return;
                            activity.getWindow()
                                    .setFlags(
                                            WindowManager.LayoutParams.FLAG_SECURE,
                                            WindowManager.LayoutParams.FLAG_SECURE);
                            screenshotActivities.put(activity, true);
                            bvLogVerbose(
                                    "[BetterVia] 已为 "
                                            + activity.getClass().getSimpleName()
                                            + " 启用截屏防护");
                        }
                    },
                    true);
            bvLog("[BetterVia] 截屏防护已启用");
        } else {
            boolean wasEnabled = ActivityLifecycleHub.isEnabled(HUB_SCREENSHOT);
            ActivityLifecycleHub.setEnabled(HUB_SCREENSHOT, false);
            if (wasEnabled) {
                removeScreenshotProtection();
                screenshotActivities.clear();
            }
            bvLog("[BetterVia] 截屏防护已停用");
        }
        screenshotProtectionEnabled = on;
        putPrefBoolean(ctx, KEY_SCREENSHOT_PROTECTION, on);
    }

    private void removeScreenshotProtection() {
        for (final Activity activity : screenshotActivities.keySet()) {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                activity.getWindow()
                                        .clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                            }
                        });
            }
        }
        bvLog("[BetterVia] 已移除所有Activity的截屏防护");
    }

    private void setRandomUa(Context ctx, ClassLoader cl, boolean on) {
        if (on) {
            currentRandomUa = null;
            if (randomUaGetSettingsHook == null) {
                try {
                    randomUaGetSettingsHook =
                            XposedHelpers.findAndHookMethod(
                                    WebView.class,
                                    "getSettings",
                                    new XC_MethodHook() {
                                        private boolean uaMethodsHooked = false;

                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            if (!randomUaEnabled || uaMethodsHooked) {
                                                return;
                                            }
                                            Object settings = param.getResult();
                                            if (settings == null) {
                                                return;
                                            }
                                            try {
                                                Method getUa =
                                                        settings.getClass()
                                                                .getMethod("getUserAgentString");
                                                randomUaGetHook =
                                                        XposedBridge.hookMethod(
                                                                getUa,
                                                                new XC_MethodHook() {
                                                                    @Override
                                                                    protected void
                                                                            beforeHookedMethod(
                                                                                    MethodHookParam
                                                                                            p)
                                                                                    throws
                                                                                            Throwable {
                                                                        if (!randomUaEnabled) {
                                                                            return;
                                                                        }
                                                                        if (currentRandomUa
                                                                                == null) {
                                                                            currentRandomUa =
                                                                                    generateRandomUserAgent();
                                                                            XposedBridge.log(
                                                                                    "[BetterVia]"
                                                                                        + " 已生成随机标识:"
                                                                                        + " "
                                                                                            + currentRandomUa);
                                                                        }
                                                                        p.setResult(
                                                                                currentRandomUa);
                                                                    }
                                                                });

                                                Method setUa =
                                                        settings.getClass()
                                                                .getMethod(
                                                                        "setUserAgentString",
                                                                        String.class);
                                                randomUaSetHook =
                                                        XposedBridge.hookMethod(
                                                                setUa,
                                                                new XC_MethodHook() {
                                                                    @Override
                                                                    protected void
                                                                            beforeHookedMethod(
                                                                                    MethodHookParam
                                                                                            p)
                                                                                    throws
                                                                                            Throwable {
                                                                        if (!randomUaEnabled) {
                                                                            return;
                                                                        }
                                                                        if (currentRandomUa
                                                                                == null) {
                                                                            currentRandomUa =
                                                                                    generateRandomUserAgent();
                                                                        }
                                                                        p.args[0] = currentRandomUa;
                                                                    }
                                                                });

                                                uaMethodsHooked = true;
                                                XposedBridge.log(
                                                        "[BetterVia] 随机标识已启用，已Hook "
                                                                + settings.getClass().getName()
                                                                + ".getUserAgentString/setUserAgentString");
                                            } catch (Exception e) {
                                                XposedBridge.log(
                                                        "[BetterVia] 动态Hook WebSettings失败: "
                                                                + e.getMessage());
                                            }
                                        }
                                    });
                } catch (Exception e) {
                    XposedBridge.log("[BetterVia] 随机标识getSettings Hook失败: " + e.getMessage());
                }
            }
        } else {
            if (randomUaGetHook != null) {
                randomUaGetHook.unhook();
                randomUaGetHook = null;
            }
            if (randomUaSetHook != null) {
                randomUaSetHook.unhook();
                randomUaSetHook = null;
            }
            if (randomUaGetSettingsHook != null) {
                randomUaGetSettingsHook.unhook();
                randomUaGetSettingsHook = null;
            }
            currentRandomUa = null;
            XposedBridge.log("[BetterVia] 随机标识已停用");
        }
        randomUaEnabled = on;
        putPrefBoolean(ctx, KEY_RANDOM_UA, on);
    }

    private static String generateRandomUserAgent() {
        java.util.Random rnd = new java.util.Random();

        java.util.List<String> platforms = new java.util.ArrayList<String>();
        if (uaAndroid) platforms.add("android");
        if (uaIos) platforms.add("ios");
        if (uaWindows) platforms.add("windows");
        if (uaMacos) platforms.add("macos");
        if (uaLinux) platforms.add("linux");
        if (platforms.isEmpty()) platforms.add("android");

        java.util.List<String> browsers = new java.util.ArrayList<String>();
        if (uaChrome) browsers.add("chrome");
        if (uaSafari) browsers.add("safari");
        if (uaEdge) browsers.add("edge");
        if (uaFirefox) browsers.add("firefox");
        if (browsers.isEmpty()) browsers.add("chrome");

        String platform = platforms.get(rnd.nextInt(platforms.size()));
        java.util.List<String> validBrowsers = new java.util.ArrayList<String>();
        for (String b : browsers) {
            if ("ios".equals(platform)) {
                if ("chrome".equals(b)
                        || "safari".equals(b)
                        || "edge".equals(b)
                        || "firefox".equals(b)) validBrowsers.add(b);
            } else if ("android".equals(platform)) {
                if ("chrome".equals(b) || "firefox".equals(b) || "edge".equals(b))
                    validBrowsers.add(b);
            } else {
                validBrowsers.add(b);
            }
        }
        if (validBrowsers.isEmpty()) validBrowsers.add("chrome");
        String browser = validBrowsers.get(rnd.nextInt(validBrowsers.size()));

        return buildUA(platform, browser, rnd);
    }

    private static String buildUA(String platform, String browser, java.util.Random rnd) {
        if ("android".equals(platform)) {
            return buildAndroidUA(browser, rnd);
        } else if ("ios".equals(platform)) {
            return buildIOSUA(browser, rnd);
        } else if ("windows".equals(platform)) {
            return buildDesktopUA(uaWindowsTokens, browser, rnd);
        } else if ("macos".equals(platform)) {
            return buildDesktopUA(uaMacosTokens, browser, rnd);
        } else {
            return buildDesktopUA(uaLinuxTokens, browser, rnd);
        }
    }

    private static String buildAndroidUA(String browser, java.util.Random rnd) {
        String[] androidVersions = splitTrim(uaAndroidVersions);
        String[] devices = splitTrim(uaAndroidDevices);
        if (androidVersions.length == 0) androidVersions = new String[] {"14"};
        if (devices.length == 0) devices = new String[] {"Pixel 8"};

        String androidVer = androidVersions[rnd.nextInt(androidVersions.length)];
        String device = devices[rnd.nextInt(devices.length)];

        if ("chrome".equals(browser) || "edge".equals(browser)) {
            int major = 100 + rnd.nextInt(32);
            int build = 4000 + rnd.nextInt(3000);
            int patch = 100 + rnd.nextInt(200);
            String ua =
                    "Mozilla/5.0 (Linux; Android "
                            + androidVer
                            + "; "
                            + device
                            + ") "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"
                            + major
                            + ".0."
                            + build
                            + "."
                            + patch
                            + " "
                            + "Mobile Safari/537.36";
            if ("edge".equals(browser)) ua += " Edg/" + major + ".0." + build + "." + patch;
            return ua;
        } else {
            int major = 100 + rnd.nextInt(32);
            return "Mozilla/5.0 (Android "
                    + androidVer
                    + "; "
                    + device
                    + "; rv:"
                    + major
                    + ".0) "
                    + "Gecko/20100101 Firefox/"
                    + major
                    + ".0";
        }
    }

    private static String buildIOSUA(String browser, java.util.Random rnd) {
        String[] iosVersions = splitTrim(uaIosVersions);
        if (iosVersions.length == 0) iosVersions = new String[] {"17.0"};
        String iosVerDotted = iosVersions[rnd.nextInt(iosVersions.length)];
        String iosVer = iosVerDotted.replace('.', '_');
        String device = rnd.nextBoolean() ? "iPhone" : "iPad";

        if ("safari".equals(browser)) {
            return "Mozilla/5.0 ("
                    + device
                    + "; CPU "
                    + device
                    + " OS "
                    + iosVer
                    + " like Mac OS X) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) "
                    + "Version/"
                    + iosVerDotted
                    + " Mobile/15E148 Safari/604.1";
        } else if ("chrome".equals(browser)) {
            int major = 100 + rnd.nextInt(32);
            return "Mozilla/5.0 ("
                    + device
                    + "; CPU "
                    + device
                    + " OS "
                    + iosVer
                    + " like Mac OS X) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) "
                    + "CriOS/"
                    + major
                    + ".0."
                    + (4000 + rnd.nextInt(3000))
                    + "."
                    + (100 + rnd.nextInt(200))
                    + " Mobile/15E148 Safari/604.1";
        } else if ("edge".equals(browser)) {
            int major = 100 + rnd.nextInt(32);
            return "Mozilla/5.0 ("
                    + device
                    + "; CPU "
                    + device
                    + " OS "
                    + iosVer
                    + " like Mac OS X) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) "
                    + "EdgiOS/"
                    + major
                    + ".0."
                    + (4000 + rnd.nextInt(3000))
                    + "."
                    + (100 + rnd.nextInt(200))
                    + " Mobile/15E148 Safari/604.1";
        } else {
            int major = 100 + rnd.nextInt(32);
            return "Mozilla/5.0 ("
                    + device
                    + "; CPU "
                    + device
                    + " OS "
                    + iosVer
                    + " like Mac OS X) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) "
                    + "FxiOS/"
                    + major
                    + ".0 Mobile/15E148 Safari/605.1.15";
        }
    }

    private static String buildDesktopUA(String osTokensCsv, String browser, java.util.Random rnd) {
        String[] tokens = splitTrim(osTokensCsv);
        if (tokens.length == 0) tokens = new String[] {"Windows NT 10.0; Win64; x64"};
        String osToken = tokens[rnd.nextInt(tokens.length)];
        if ("firefox".equals(browser)) {
            int major = 100 + rnd.nextInt(32);
            return "Mozilla/5.0 ("
                    + osToken
                    + "; rv:"
                    + major
                    + ".0) "
                    + "Gecko/20100101 Firefox/"
                    + major
                    + ".0";
        } else {
            int major = 100 + rnd.nextInt(32);
            int build = 4000 + rnd.nextInt(3000);
            int patch = 100 + rnd.nextInt(200);
            String ua =
                    "Mozilla/5.0 ("
                            + osToken
                            + ") "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/"
                            + major
                            + ".0."
                            + build
                            + "."
                            + patch
                            + " "
                            + "Safari/537.36";
            if ("edge".equals(browser)) ua += " Edg/" + major + ".0." + build + "." + patch;
            return ua;
        }
    }

    private static String[] splitTrim(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new String[0];
        String[] parts = csv.split(",");
        java.util.List<String> result = new java.util.ArrayList<String>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result.toArray(new String[result.size()]);
    }

    private void setKeepScreenOn(Context ctx, ClassLoader cl, boolean on) {
        if (on) {
            ActivityLifecycleHub.add(
                    HUB_KEEP_SCREEN_ON,
                    new ActivityLifecycleHub.Listener() {
                        @Override
                        public void onActivityCreated(
                                final Activity activity, Bundle savedInstanceState) {
                            if (!keepScreenOnEnabled) return;
                            activity.runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.getWindow()
                                                    .addFlags(
                                                            WindowManager.LayoutParams
                                                                    .FLAG_KEEP_SCREEN_ON);
                                            screenOnActivities.put(activity, true);
                                            bvLogVerbose(
                                                    "[BetterVia] 已为 "
                                                            + activity.getClass().getSimpleName()
                                                            + " 启用屏幕常亮");
                                        }
                                    });
                        }
                    },
                    true);
            bvLog("[BetterVia] 屏幕常亮已启用");
        } else {
            boolean wasEnabled = ActivityLifecycleHub.isEnabled(HUB_KEEP_SCREEN_ON);
            ActivityLifecycleHub.setEnabled(HUB_KEEP_SCREEN_ON, false);
            if (wasEnabled) {
                removeKeepScreenOn();
            }
            bvLog("[BetterVia] 屏幕常亮已停用");
        }
        keepScreenOnEnabled = on;
        putPrefBoolean(ctx, KEY_KEEP_SCREEN_ON, on);
    }

    private void setBackgroundVideoAudio(Context ctx, ClassLoader cl, boolean on) {
        if (on) {
            if (backgroundVideoHooks.isEmpty()) {
                try {
                    Class<?> shellClass = findClassWithFallback("Shell", ctx, cl);
                    if (shellClass == null) {
                        bvLog("[BetterVia] 未找到Shell类，无法启用后台听视频功能");
                        return;
                    }
                    backgroundVideoHooks.add(
                            XposedHelpers.findAndHookMethod(
                                    shellClass,
                                    "onPause",
                                    new XC_MethodReplacement() {
                                        @Override
                                        protected Object replaceHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            bvLogVerbose("[BetterVia] 阻止了Shell.onPause调用，保持前台状态");
                                            return null;
                                        }
                                    }));
                    backgroundVideoHooks.add(
                            XposedHelpers.findAndHookMethod(
                                    shellClass,
                                    "onWindowFocusChanged",
                                    boolean.class,
                                    new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            param.args[0] = true;
                                            bvLogVerbose("[BetterVia] 强制设置窗口焦点为true");
                                        }
                                    }));
                    backgroundVideoHooks.add(
                            XposedHelpers.findAndHookMethod(
                                    shellClass,
                                    "isFinishing",
                                    new XC_MethodReplacement() {
                                        @Override
                                        protected Object replaceHookedMethod(MethodHookParam param)
                                                throws Throwable {
                                            bvLogVerbose("[BetterVia] 强制返回isFinishing=false");
                                            return false;
                                        }
                                    }));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        backgroundVideoHooks.add(
                                XposedHelpers.findAndHookMethod(
                                        shellClass,
                                        "isDestroyed",
                                        new XC_MethodReplacement() {
                                            @Override
                                            protected Object replaceHookedMethod(
                                                    MethodHookParam param) throws Throwable {
                                                bvLogVerbose("[BetterVia] 强制返回isDestroyed=false");
                                                return false;
                                            }
                                        }));
                    }

                    bvLog("[BetterVia] 后台听视频功能已启用");
                } catch (Throwable e) {
                    bvLog("[BetterVia] 启用后台听视频功能失败: " + e.getMessage());
                }
            }
        } else {
            if (!backgroundVideoHooks.isEmpty()) {
                try {
                    for (XC_MethodHook.Unhook h : backgroundVideoHooks) {
                        h.unhook();
                    }
                    backgroundVideoHooks.clear();
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
        if (!backgroundVideoEnabled) return;

        new Handler(Looper.getMainLooper())
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String jsCode =
                                            "javascript:(function() { // 阻止页面可见性变化导致的视频暂停    var"
                                                + " originalHidden = document.hidden;    var"
                                                + " originalVisibilityState ="
                                                + " document.visibilityState;     // 重写页面可见性属性   "
                                                + " Object.defineProperty(document, 'hidden', {    "
                                                + "    get: function() { return false; }    });    "
                                                + "    Object.defineProperty(document,"
                                                + " 'visibilityState', {        get: function() {"
                                                + " return 'visible'; }    });     //"
                                                + " 重写addEventListener，过滤visibilitychange事件    var"
                                                + " originalAddEventListener ="
                                                + " document.addEventListener;   "
                                                + " document.addEventListener = function(type,"
                                                + " listener, options) {        if (type ==="
                                                + " 'visibilitychange') { // 忽略visibilitychange事件监听"
                                                + "            console.log('Blocked"
                                                + " visibilitychange listener');            return;"
                                                + "        }       "
                                                + " originalAddEventListener.call(this, type,"
                                                + " listener, options);    };     // 保持现有视频播放   "
                                                + " var videos ="
                                                + " document.getElementsByTagName('video');    for"
                                                + " (var i = 0; i < videos.length; i++) {       "
                                                + " var video = videos[i];        if (video.paused)"
                                                + " {            video.play().catch(function(e)"
                                                + " {});        }         // 监听暂停事件，自动重新播放       "
                                                + " video.addEventListener('pause', function(e) {  "
                                                + "          if (!document.hidden) { //"
                                                + " 由于重写了hidden，这里总是true               "
                                                + " this.play().catch(function(e) {});            }"
                                                + "        });    }        console.log('Video"
                                                + " keep-alive script injected');})();";

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
                        },
                        2000);
    }

    private void removeKeepScreenOn() {
        for (final Activity activity : screenOnActivities.keySet()) {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                activity.getWindow()
                                        .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            }
                        });
            }
        }
        screenOnActivities.clear();
        bvLog("[BetterVia] 已移除所有Activity的屏幕常亮设置");
    }

    private static class ScriptInfo {

        String id;
        Map<String, String> nameMap;
        Map<String, String> descriptionMap;
        Map<String, String> detailMap;
        Map<String, String> downloadUrls;
        String category;

        ScriptInfo(
                String id,
                Map<String, String> nameMap,
                Map<String, String> descriptionMap,
                Map<String, String> detailMap,
                Map<String, String> downloadUrls,
                String category) {
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
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "script_repository_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        title.setTextColor(textColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);
                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "script_repository_subtitle"));
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
                        searchEdit.setHint(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "script_search_hint"));
                        searchEdit.setTextColor(textColor);
                        searchEdit.setHintTextColor(hintColor);
                        searchEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        searchEdit.setBackground(getRoundBg(act, editBgColor, 8));
                        searchEdit.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
                        LinearLayout.LayoutParams searchLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        searchLp.rightMargin = dp(act, 8);
                        searchRow.addView(searchEdit, searchLp);

                        Button searchButton = new Button(act);
                        applyClickAnim(searchButton);
                        searchButton.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "script_search_button"));
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
                        scriptsContainer.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                        LinearLayout.LayoutParams iconLp =
                                new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
                        iconLp.gravity = Gravity.CENTER;
                        iconLp.bottomMargin = dp(act, 16);
                        emptyStateContainer.addView(errorIcon, iconLp);
                        final TextView emptyStateText = new TextView(act);
                        emptyStateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        emptyStateText.setTextColor(hintColor);
                        emptyStateText.setGravity(Gravity.CENTER);
                        emptyStateText.setPadding(dp(act, 32), 0, dp(act, 32), 0);
                        emptyStateText.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "scripts_loading"));
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

                        LinearLayout.LayoutParams okLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                            WindowManager.LayoutParams layoutParams =
                                    new WindowManager.LayoutParams();
                            layoutParams.copyFrom(window.getAttributes());
                            layoutParams.width = width;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity = Gravity.CENTER;
                            window.setAttributes(layoutParams);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                window.setClipToOutline(true);
                            }
                        }

                        ok.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                        final List<ScriptInfo>[] allScripts =
                                new List[] {new ArrayList<ScriptInfo>()};
                        final Runnable updateScriptCount =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (allScripts[0] != null && !allScripts[0].isEmpty()) {
                                            String countText =
                                                    String.format(
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(ctx, "script_total_count"),
                                                            allScripts[0].size());
                                            scriptCountText.setText(countText);
                                        } else {
                                            scriptCountText.setText(
                                                    LocalizedStringProvider.getInstance()
                                                            .get(ctx, "script_loading_count"));
                                        }
                                    }
                                };
                        searchButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String query =
                                                searchEdit
                                                        .getText()
                                                        .toString()
                                                        .trim()
                                                        .toLowerCase();
                                        filterScripts(
                                                act,
                                                ctx,
                                                scriptsContainer,
                                                emptyStateContainer,
                                                emptyStateText,
                                                allScripts[0],
                                                query,
                                                true);
                                    }
                                });
                        searchEdit.addTextChangedListener(
                                new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(
                                            CharSequence s, int start, int count, int after) {}

                                    @Override
                                    public void onTextChanged(
                                            CharSequence s, int start, int before, int count) {
                                        String query = s.toString().trim().toLowerCase();
                                        filterScripts(
                                                act,
                                                ctx,
                                                scriptsContainer,
                                                emptyStateContainer,
                                                emptyStateText,
                                                allScripts[0],
                                                query,
                                                false);
                                        if (allScripts[0] != null && !allScripts[0].isEmpty()) {
                                            List<ScriptInfo> filteredScripts = new ArrayList<>();
                                            for (ScriptInfo script : allScripts[0]) {
                                                String name = script.getName(ctx).toLowerCase();
                                                String description =
                                                        script.getDescription(ctx).toLowerCase();
                                                String category = script.category.toLowerCase();

                                                if (name.contains(query)
                                                        || description.contains(query)
                                                        || category.contains(query)) {
                                                    filteredScripts.add(script);
                                                }
                                            }
                                            String countText;
                                            if (query.isEmpty()) {
                                                countText =
                                                        String.format(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "script_total_count"),
                                                                allScripts[0].size());
                                            } else {
                                                countText =
                                                        String.format(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "script_filtered_count"),
                                                                filteredScripts.size(),
                                                                allScripts[0].size());
                                            }
                                            scriptCountText.setText(countText);
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {}
                                });
                        showScriptsLoadingState(
                                act, ctx, scriptsContainer, emptyStateContainer, emptyStateText);
                        loadScriptsFromNetwork(
                                ctx,
                                new ScriptsLoadCallback() {
                                    @Override
                                    public void onScriptsLoaded(final List<ScriptInfo> scripts) {
                                        allScripts[0] = scripts;
                                        if (act != null && !act.isFinishing()) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            refreshScriptsList(
                                                                    act,
                                                                    ctx,
                                                                    scriptsContainer,
                                                                    emptyStateContainer,
                                                                    emptyStateText,
                                                                    scripts);
                                                            updateScriptCount.run();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onLoadFailed(final String error) {
                                        if (act != null && !act.isFinishing()) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showScriptsErrorState(
                                                                    act,
                                                                    ctx,
                                                                    scriptsContainer,
                                                                    emptyStateContainer,
                                                                    emptyStateText,
                                                                    error);
                                                            scriptCountText.setText(
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "script_load_failed_count"));
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
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            getPrefString(
                                                    ctx,
                                                    KEY_NETWORK_SOURCE,
                                                    DEFAULT_NETWORK_SOURCE);
                                    String scriptsUrl;

                                    if (networkSource.equals(NETWORK_SOURCE_VERCEL)) {
                                        String configUrl =
                                                "https://raw.196104.xyz/scripts_config.json";
                                        URL configUrlObj = new URL(configUrl);
                                        HttpURLConnection configConn =
                                                (HttpURLConnection) configUrlObj.openConnection();
                                        configConn.setConnectTimeout(15000);
                                        configConn.setReadTimeout(15000);
                                        configConn.setRequestProperty(
                                                "User-Agent",
                                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                                                    + " AppleWebKit/537.36");

                                        if (configConn.getResponseCode()
                                                == HttpURLConnection.HTTP_OK) {
                                            InputStream configIs = configConn.getInputStream();
                                            BufferedReader configReader =
                                                    new BufferedReader(
                                                            new InputStreamReader(
                                                                    configIs, "UTF-8"));
                                            StringBuilder configResponse = new StringBuilder();
                                            String configLine;
                                            while ((configLine = configReader.readLine()) != null) {
                                                configResponse.append(configLine);
                                            }
                                            configReader.close();

                                            JSONObject configJson =
                                                    new JSONObject(configResponse.toString());
                                            scriptsUrl =
                                                    configJson.optString(
                                                            "vercel_scripts_url",
                                                            "https://cdn.jsdelivr.net/gh/JiGuroLGC/CDN/scripts.json");
                                        } else {
                                            callback.onLoadFailed(
                                                    "Config HTTP " + configConn.getResponseCode());
                                            configConn.disconnect();
                                            return;
                                        }
                                        configConn.disconnect();
                                    } else {
                                        scriptsUrl =
                                                "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/scripts.json";
                                    }

                                    URL url = new URL(scriptsUrl);
                                    HttpURLConnection connection =
                                            (HttpURLConnection) url.openConnection();
                                    connection.setConnectTimeout(15000);
                                    connection.setReadTimeout(15000);
                                    connection.setRequestProperty(
                                            "User-Agent",
                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                                                + " AppleWebKit/537.36");

                                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = connection.getInputStream();
                                        BufferedReader reader =
                                                new BufferedReader(
                                                        new InputStreamReader(
                                                                inputStream, "UTF-8"));
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
                                        callback.onLoadFailed(
                                                "HTTP " + connection.getResponseCode());
                                    }

                                    connection.disconnect();
                                } catch (Exception e) {
                                    callback.onLoadFailed(e.getMessage());
                                }
                            }
                        })
                .start();
    }

    private void filterScripts(
            Activity act,
            Context ctx,
            LinearLayout scriptsContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            List<ScriptInfo> allScripts,
            String query,
            boolean showToast) {
        if (allScripts == null || allScripts.isEmpty()) {
            showScriptsErrorState(
                    act,
                    ctx,
                    scriptsContainer,
                    emptyStateContainer,
                    emptyStateText,
                    LocalizedStringProvider.getInstance().get(ctx, "no_scripts_available"));
            return;
        }

        if (query.isEmpty()) {
            refreshScriptsList(
                    act, ctx, scriptsContainer, emptyStateContainer, emptyStateText, allScripts);
            if (showToast) {
                jiguroMessageWithContext(
                        act,
                        String.format(
                                LocalizedStringProvider.getInstance().get(ctx, "script_show_all"),
                                allScripts.size()));
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
                    String.format(
                            LocalizedStringProvider.getInstance()
                                    .get(ctx, "script_search_no_results"),
                            query));
            for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
                View child = emptyStateContainer.getChildAt(i);
                if (child instanceof ImageView) {
                    child.setVisibility(View.VISIBLE);
                    ((ImageView) child)
                            .setImageResource(android.R.drawable.ic_search_category_default);
                } else if (child instanceof ProgressBar) {
                    emptyStateContainer.removeView(child);
                }
            }

            if (showToast) {
                jiguroMessageWithContext(
                        act,
                        LocalizedStringProvider.getInstance()
                                .get(ctx, "script_search_no_results_toast"));
            }
        } else {
            refreshScriptsList(
                    act,
                    ctx,
                    scriptsContainer,
                    emptyStateContainer,
                    emptyStateText,
                    filteredScripts);
            if (showToast) {
                jiguroMessageWithContext(
                        act,
                        String.format(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "script_search_results"),
                                filteredScripts.size()));
            }
        }
    }

    private void showScriptsLoadingState(
            Activity act,
            Context ctx,
            LinearLayout scriptsContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText) {
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
        LinearLayout.LayoutParams progressLp =
                new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
        progressLp.gravity = Gravity.CENTER;
        progressLp.bottomMargin = dp(act, 16);
        emptyStateContainer.addView(progressBar, 0, progressLp);
    }

    private void showScriptsErrorState(
            Activity act,
            Context ctx,
            LinearLayout scriptsContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            String error) {
        scriptsContainer.removeAllViews();
        scriptsContainer.setVisibility(View.GONE);

        emptyStateContainer.setVisibility(View.VISIBLE);
        emptyStateText.setText(
                LocalizedStringProvider.getInstance().get(ctx, "scripts_load_failed")
                        + "\n"
                        + LocalizedStringProvider.getInstance().get(ctx, "check_network"));
        for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
            View child = emptyStateContainer.getChildAt(i);
            if (child instanceof ImageView) {
                child.setVisibility(View.VISIBLE);
            } else if (child instanceof ProgressBar) {
                emptyStateContainer.removeView(child);
            }
        }

        jiguroMessageWithContext(
                ctx,
                LocalizedStringProvider.getInstance().get(ctx, "scripts_load_failed")
                        + ": "
                        + error);
    }

    private void refreshScriptsList(
            final Activity act,
            final Context ctx,
            LinearLayout scriptsContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            List<ScriptInfo> scripts) {
        scriptsContainer.removeAllViews();

        if (scripts == null || scripts.isEmpty()) {
            showScriptsErrorState(
                    act,
                    ctx,
                    scriptsContainer,
                    emptyStateContainer,
                    emptyStateText,
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
                LinearLayout.LayoutParams cardLp =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                cardLp.bottomMargin = dp(ctx, 12);
                scriptsContainer.addView(scriptCard, cardLp);
            }
        }
    }

    private LinearLayout createScriptCard(
            final Activity act, final Context ctx, final ScriptInfo script) {
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
        scriptCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showScriptDetailDialog(ctx, script);
                    }
                });

        return scriptCard;
    }

    private void showScriptDetailDialog(final Context ctx, final ScriptInfo script) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
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
                        for (final Map.Entry<String, String> entry :
                                script.downloadUrls.entrySet()) {
                            Button downloadBtn = new Button(act);
                            applyClickAnim(downloadBtn);
                            downloadBtn.setText(entry.getKey());
                            downloadBtn.setTextColor(getOkBtnTextColor(ctx));
                            downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            downloadBtn.setPadding(
                                    dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
                            downloadBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));

                            LinearLayout.LayoutParams btnLp =
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            btnLp.bottomMargin = dp(act, 8);
                            buttonContainer.addView(downloadBtn, btnLp);

                            downloadBtn.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                if (detailDialogRef[0] != null) {
                                                    detailDialogRef[0].dismiss();
                                                }
                                                new Handler(Looper.getMainLooper())
                                                        .postDelayed(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            jiguroMessageWithContext(
                                                                                    act,
                                                                                    LocalizedStringProvider
                                                                                            .getInstance()
                                                                                            .get(
                                                                                                    ctx,
                                                                                                    "script_opened_in_via"));
                                                                            new Handler(
                                                                                            Looper
                                                                                                    .getMainLooper())
                                                                                    .postDelayed(
                                                                                            new Runnable() {
                                                                                                @Override
                                                                                                public
                                                                                                void
                                                                                                        run() {
                                                                                                    try {
                                                                                                        Intent
                                                                                                                intent =
                                                                                                                        new Intent(
                                                                                                                                Intent
                                                                                                                                        .ACTION_VIEW,
                                                                                                                                Uri
                                                                                                                                        .parse(
                                                                                                                                                entry
                                                                                                                                                        .getValue()));
                                                                                                        act
                                                                                                                .startActivity(
                                                                                                                        intent);
                                                                                                    } catch (
                                                                                                            Exception
                                                                                                                    e) {
                                                                                                        jiguroMessageWithContext(
                                                                                                                act,
                                                                                                                LocalizedStringProvider
                                                                                                                        .getInstance()
                                                                                                                        .get(
                                                                                                                                ctx,
                                                                                                                                "cannot_open_download_link"));
                                                                                                    }
                                                                                                }
                                                                                            },
                                                                                            300);
                                                                        } catch (Exception e) {
                                                                            jiguroMessageWithContext(
                                                                                    act,
                                                                                    LocalizedStringProvider
                                                                                            .getInstance()
                                                                                            .get(
                                                                                                    ctx,
                                                                                                    "cannot_open_download_link"));
                                                                        }
                                                                    }
                                                                },
                                                                100);
                                            } catch (Exception e) {
                                                jiguroMessageWithContext(
                                                        act,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "cannot_open_download_link"));
                                            }
                                        }
                                    });
                        }

                        layout.addView(buttonContainer);
                        scrollView.addView(layout);
                        builder.setView(scrollView);

                        builder.setNegativeButton(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"),
                                null);
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

    private void showAdBlockRulesDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "ad_block_rules_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        title.setTextColor(textColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);
                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "ad_block_rules_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        subtitle.setTextColor(hintColor);
                        subtitle.setGravity(Gravity.CENTER);
                        subtitle.setPadding(0, 0, 0, dp(act, 24));
                        root.addView(subtitle);
                        final LinearLayout rulesContainer = new LinearLayout(act);
                        rulesContainer.setOrientation(LinearLayout.VERTICAL);
                        rulesContainer.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                        LinearLayout.LayoutParams iconLp =
                                new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
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

                        LinearLayout.LayoutParams okLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
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
                            WindowManager.LayoutParams layoutParams =
                                    new WindowManager.LayoutParams();
                            layoutParams.copyFrom(window.getAttributes());
                            layoutParams.width = width;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity = Gravity.CENTER;
                            window.setAttributes(layoutParams);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                window.setClipToOutline(true);
                            }
                        }

                        ok.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                        showRulesLoadingState(
                                act, ctx, rulesContainer, emptyStateContainer, emptyStateText);
                        loadRulesFromNetwork(
                                ctx,
                                new RulesLoadCallback() {
                                    @Override
                                    public void onRulesLoaded(final List<RuleInfo> rules) {
                                        if (act != null && !act.isFinishing()) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            refreshRulesList(
                                                                    act,
                                                                    ctx,
                                                                    rulesContainer,
                                                                    emptyStateContainer,
                                                                    emptyStateText,
                                                                    rules);
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onLoadFailed(final String error) {
                                        if (act != null && !act.isFinishing()) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showRulesErrorState(
                                                                    act,
                                                                    ctx,
                                                                    rulesContainer,
                                                                    emptyStateContainer,
                                                                    emptyStateText,
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

        RuleInfo(
                String id,
                Map<String, String> nameMap,
                Map<String, String> descriptionMap,
                Map<String, String> detailMap,
                Map<String, String> downloadUrls,
                String category,
                String author,
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

            return new RuleInfo(
                    id,
                    nameMap,
                    descriptionMap,
                    detailMap,
                    downloadUrls,
                    category,
                    author,
                    homepage);
        }
    }

    private void loadRulesFromNetwork(final Context ctx, final RulesLoadCallback callback) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            getPrefString(
                                                    ctx,
                                                    KEY_NETWORK_SOURCE,
                                                    DEFAULT_NETWORK_SOURCE);
                                    String rulesUrl =
                                            networkSource.equals(NETWORK_SOURCE_VERCEL)
                                                    ? "https://raw.196104.xyz/adblock.json"
                                                    : "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/adblock.json";

                                    URL url = new URL(rulesUrl);
                                    HttpURLConnection connection =
                                            (HttpURLConnection) url.openConnection();
                                    connection.setConnectTimeout(15000);
                                    connection.setReadTimeout(15000);
                                    connection.setRequestProperty(
                                            "User-Agent",
                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                                                + " AppleWebKit/537.36");

                                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = connection.getInputStream();
                                        BufferedReader reader =
                                                new BufferedReader(
                                                        new InputStreamReader(
                                                                inputStream, "UTF-8"));
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
                                        callback.onLoadFailed(
                                                "HTTP " + connection.getResponseCode());
                                    }

                                    connection.disconnect();
                                } catch (Exception e) {
                                    callback.onLoadFailed(e.getMessage());
                                }
                            }
                        })
                .start();
    }

    private void showRulesLoadingState(
            Activity act,
            Context ctx,
            LinearLayout rulesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText) {
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
        LinearLayout.LayoutParams progressLp =
                new LinearLayout.LayoutParams(dp(act, 48), dp(act, 48));
        progressLp.gravity = Gravity.CENTER;
        progressLp.bottomMargin = dp(act, 16);
        emptyStateContainer.addView(progressBar, 0, progressLp);
    }

    private void showRulesErrorState(
            Activity act,
            Context ctx,
            LinearLayout rulesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            String error) {
        rulesContainer.removeAllViews();
        rulesContainer.setVisibility(View.GONE);

        emptyStateContainer.setVisibility(View.VISIBLE);
        emptyStateText.setText(
                LocalizedStringProvider.getInstance().get(ctx, "rules_load_failed")
                        + "\n"
                        + LocalizedStringProvider.getInstance().get(ctx, "check_network"));
        for (int i = 0; i < emptyStateContainer.getChildCount(); i++) {
            View child = emptyStateContainer.getChildAt(i);
            if (child instanceof ImageView) {
                child.setVisibility(View.VISIBLE);
            } else if (child instanceof ProgressBar) {
                emptyStateContainer.removeView(child);
            }
        }

        jiguroMessageWithContext(
                ctx,
                LocalizedStringProvider.getInstance().get(ctx, "rules_load_failed") + ": " + error);
    }

    private void refreshRulesList(
            final Activity act,
            final Context ctx,
            LinearLayout rulesContainer,
            LinearLayout emptyStateContainer,
            TextView emptyStateText,
            List<RuleInfo> rules) {
        rulesContainer.removeAllViews();

        if (rules == null || rules.isEmpty()) {
            showRulesErrorState(
                    act,
                    ctx,
                    rulesContainer,
                    emptyStateContainer,
                    emptyStateText,
                    "No rules available");
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
                LinearLayout.LayoutParams cardLp =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
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

    private LinearLayout createRuleCard(
            final Activity act, final Context ctx, final RuleInfo rule) {
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
        ruleCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRuleDetailDialog(ctx, rule);
                    }
                });

        return ruleCard;
    }

    private void showRuleDetailDialog(final Context ctx, final RuleInfo rule) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
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
                                    LocalizedStringProvider.getInstance().get(ctx, "rule_author")
                                            + ": "
                                            + rule.author);
                            authorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            authorText.setTextColor(getHintColor(ctx));
                            authorText.setPadding(0, dp(act, 8), 0, 0);
                            layout.addView(authorText);
                        }
                        if (rule.homepage != null && !rule.homepage.isEmpty()) {
                            TextView homepageText = new TextView(act);
                            homepageText.setText(
                                    LocalizedStringProvider.getInstance().get(ctx, "rule_homepage")
                                            + ": "
                                            + rule.homepage);
                            homepageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            homepageText.setTextColor(getTitleColor(ctx));
                            homepageText.setPadding(0, dp(act, 8), 0, 0);
                            homepageText.setPaintFlags(
                                    homepageText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            homepageText.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent =
                                                        new Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(rule.homepage));
                                                act.startActivity(intent);
                                            } catch (Exception e) {
                                                jiguroMessageWithContext(
                                                        act,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(ctx, "cannot_open_homepage"));
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
                            downloadBtn.setText(
                                    LocalizedStringProvider.getInstance().get(ctx, "rule_channel")
                                            + " "
                                            + channelIndex
                                            + " - "
                                            + entry.getKey());
                            downloadBtn.setTextColor(getOkBtnTextColor(ctx));
                            downloadBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            downloadBtn.setPadding(
                                    dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
                            downloadBtn.setBackground(getRoundBg(act, getOkBtnBgColor(ctx), 8));

                            LinearLayout.LayoutParams btnLp =
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            btnLp.bottomMargin = dp(act, 8);
                            buttonContainer.addView(downloadBtn, btnLp);

                            downloadBtn.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            copyToClipboard(act, entry.getValue());
                                            jiguroMessageWithContext(
                                                    act,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(ctx, "rule_link_copied"));
                                        }
                                    });

                            channelIndex++;
                        }

                        layout.addView(buttonContainer);
                        scrollView.addView(layout);
                        builder.setView(scrollView);

                        builder.setNegativeButton(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"),
                                null);
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
            ActivityLifecycleHub.add(
                    HUB_HIDE_STATUS_BAR,
                    new ActivityLifecycleHub.Listener() {
                        @Override
                        public void onActivityCreated(
                                final Activity activity, Bundle savedInstanceState) {
                            if (!hideStatusBarEnabled) return;
                            activity.runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            setupStatusBarHiding(activity);
                                            statusBarHiddenActivities.put(activity, true);
                                            bvLogVerbose(
                                                    "[BetterVia] 已为 "
                                                            + activity.getClass().getSimpleName()
                                                            + " 设置状态栏隐藏");
                                        }
                                    });
                        }
                    },
                    true);
            bvLog("[BetterVia] 隐藏状态栏已启用");
        } else {
            boolean wasEnabled = ActivityLifecycleHub.isEnabled(HUB_HIDE_STATUS_BAR);
            ActivityLifecycleHub.setEnabled(HUB_HIDE_STATUS_BAR, false);
            if (wasEnabled) {
                restoreStatusBar();
            }
            bvLog("[BetterVia] 隐藏状态栏已停用");
        }
        hideStatusBarEnabled = on;
        putPrefBoolean(ctx, KEY_HIDE_STATUS_BAR, on);
    }

    private void setPerfectExitHook(Context ctx, ClassLoader cl, boolean on) {
        if (on) {
            if (perfectExitHook == null) {
                perfectExitHook =
                        XposedHelpers.findAndHookMethod(
                                Activity.class,
                                "finish",
                                new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param)
                                            throws Throwable {
                                        if (!perfectExitEnabled) return;
                                        Activity act = (Activity) param.thisObject;
                                        if (!"mark.via.Shell".equals(act.getClass().getName()))
                                            return;
                                        bvLog("[BetterVia] 完美退出: 拦截 Shell.finish()");
                                        param.setResult(null);
                                        killViaProcess(act);
                                    }
                                });
            }
        } else {
            if (perfectExitHook != null) {
                perfectExitHook.unhook();
                perfectExitHook = null;
            }
        }
        perfectExitEnabled = on;
        putPrefBoolean(ctx, KEY_PERFECT_EXIT, on);
    }

    private void killViaProcess(Activity act) {
        if (act == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                android.app.ActivityManager am =
                        (android.app.ActivityManager) act.getSystemService("activity");
                if (am != null) {
                    java.util.List<android.app.ActivityManager.AppTask> tasks = am.getAppTasks();
                    if (tasks != null) {
                        for (android.app.ActivityManager.AppTask task : tasks) {
                            try {
                                task.finishAndRemoveTask();
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setupStatusBarHiding(final Activity activity) {
        try {
            if (activity.isFinishing() || activity.isDestroyed()) return;

            final View decorView = activity.getWindow().getDecorView();
            hideStatusBarImmediate(activity);
            decorView.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                Runnable oldRunnable = statusBarRehideRunnables.get(activity);
                                if (oldRunnable != null) {
                                    decorView.removeCallbacks(oldRunnable);
                                }
                                Runnable rehideRunnable =
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                statusBarRehideRunnables.remove(activity);
                                                if (!activity.isFinishing()
                                                        && !activity.isDestroyed()
                                                        && hideStatusBarEnabled) {
                                                    hideStatusBarImmediate(activity);
                                                    bvLog("[BetterVia] 重新隐藏状态栏");
                                                }
                                            }
                                        };
                                statusBarRehideRunnables.put(activity, rehideRunnable);
                                decorView.postDelayed(rehideRunnable, REHIDE_DELAY);
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
            activity.getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            bvLog("[BetterVia] 立即隐藏状态栏失败: " + e);
        }
    }

    private void restoreStatusBar() {
        for (final Activity activity : statusBarHiddenActivities.keySet()) {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    View decorView = activity.getWindow().getDecorView();
                                    decorView.setOnSystemUiVisibilityChangeListener(null);
                                    Runnable rehideRunnable =
                                            statusBarRehideRunnables.get(activity);
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
                                    activity.getWindow()
                                            .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                        Class<?> viewPagerClass =
                                cl.loadClass("androidx.viewpager.widget.ViewPager");
                        swipeBackHook =
                                XposedHelpers.findAndHookMethod(
                                        viewPagerClass,
                                        "canScrollHorizontally",
                                        int.class,
                                        new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param)
                                                    throws Throwable {
                                                if (!blockSwipeBackEnabled) {
                                                    return;
                                                }
                                                int direction = (Integer) param.args[0];
                                                if (direction < 0) {
                                                    bvLogVerbose(
                                                            "[BetterVia]"
                                                                + " ViewPager.canScrollHorizontally:"
                                                                + " 阻止向右滚动");
                                                    param.setResult(false);
                                                }
                                            }
                                        });
                        bvLog("[BetterVia] ✓ 屏蔽右滑返回已启用 (androidx.viewpager.widget.ViewPager)");
                    } catch (ClassNotFoundException e) {
                        bvLog("[BetterVia] ✗ 未找到 androidx.viewpager.widget.ViewPager");

                        try {
                            Class<?> viewPagerClass =
                                    cl.loadClass("android.support.v4.view.ViewPager");
                            swipeBackHook =
                                    XposedHelpers.findAndHookMethod(
                                            viewPagerClass,
                                            "canScrollHorizontally",
                                            int.class,
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(
                                                        MethodHookParam param) throws Throwable {
                                                    if (!blockSwipeBackEnabled) {
                                                        return;
                                                    }
                                                    int direction = (Integer) param.args[0];
                                                    if (direction < 0) {
                                                        bvLogVerbose(
                                                                "[BetterVia]"
                                                                    + " ViewPager.canScrollHorizontally:"
                                                                    + " 阻止向右滚动");
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
                            swipeBackHook =
                                    XposedHelpers.findAndHookMethod(
                                            ViewGroup.class,
                                            "requestDisallowInterceptTouchEvent",
                                            boolean.class,
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(
                                                        MethodHookParam param) throws Throwable {
                                                    if (!blockSwipeBackEnabled) {
                                                        return;
                                                    }
                                                    param.args[0] = false;
                                                    bvLogVerbose(
                                                            "[BetterVia]"
                                                                + " requestDisallowInterceptTouchEvent:"
                                                                + " 强制不拦截");
                                                }
                                            });
                            bvLog(
                                    "[BetterVia] ✓ 屏蔽右滑返回已启用"
                                        + " (ViewGroup.requestDisallowInterceptTouchEvent)");
                        } catch (Throwable e) {
                            bvLog(
                                    "[BetterVia] ✗ Hook"
                                        + " ViewGroup.requestDisallowInterceptTouchEvent 失败: "
                                            + e);
                        }
                    }

                    if (swipeBackHook == null) {
                        try {
                            swipeBackHook =
                                    XposedHelpers.findAndHookMethod(
                                            ViewGroup.class,
                                            "onInterceptTouchEvent",
                                            MotionEvent.class,
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(
                                                        MethodHookParam param) throws Throwable {
                                                    if (!blockSwipeBackEnabled) {
                                                        return;
                                                    }
                                                    MotionEvent event = (MotionEvent) param.args[0];
                                                    if (event.getAction()
                                                            == MotionEvent.ACTION_DOWN) {
                                                        float x = event.getX();
                                                        if (x
                                                                < dp(
                                                                        ((View) param.thisObject)
                                                                                .getContext(),
                                                                        50)) {
                                                            bvLogVerbose(
                                                                    "[BetterVia]"
                                                                        + " onInterceptTouchEvent:"
                                                                        + " 拦截左边缘触摸");
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
                            swipeBackHook =
                                    XposedHelpers.findAndHookMethod(
                                            Activity.class,
                                            "onBackPressed",
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(
                                                        MethodHookParam param) throws Throwable {
                                                    if (!blockSwipeBackEnabled) {
                                                        return;
                                                    }
                                                    bvLogVerbose("[BetterVia] onBackPressed: 阻止返回");
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

    private void loadFileContent(
            final Context ctx,
            final String fileName,
            final EditText editor,
            final ScrollView scrollView,
            final boolean fromCache) {
        if (fromCache && editorStateCache.containsKey(fileName)) {
            final EditorState state = editorStateCache.get(fileName);
            editor.setText(state.content);
            editor.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (scrollView != null) {
                                scrollView.scrollTo(0, state.scrollY);
                            }
                        }
                    });
            return;
        }
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String filePath =
                                            "/data/user/0/"
                                                    + ctx.getPackageName()
                                                    + "/files/"
                                                    + fileName;
                                    File file = new File(filePath);
                                    final StringBuilder content = new StringBuilder();

                                    if (file.exists()) {
                                        BufferedReader reader =
                                                new BufferedReader(new FileReader(file));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            content.append(line).append("\n");
                                        }
                                        reader.close();
                                    } else {
                                        content.append("// 文件不存在，将创建新文件\n");
                                    }

                                    ((Activity) ctx)
                                            .runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            editor.setText(content.toString());
                                                            editorStateCache.put(
                                                                    fileName,
                                                                    new EditorState(
                                                                            content.toString(), 0));
                                                        }
                                                    });
                                } catch (final Exception e) {
                                    ((Activity) ctx)
                                            .runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            editor.setText(
                                                                    LocalizedStringProvider
                                                                                    .getInstance()
                                                                                    .get(
                                                                                            ctx,
                                                                                            "error_load_failed")
                                                                            + ": "
                                                                            + e.getMessage());
                                                        }
                                                    });
                                }
                            }
                        })
                .start();
    }

    private void saveCurrentEditorState(String fileName, EditText editor, ScrollView scrollView) {
        if (editor != null && scrollView != null) {
            String content = editor.getText().toString();
            int scrollY = scrollView.getScrollY();
            editorStateCache.put(fileName, new EditorState(content, scrollY));
        }
    }

    private void saveFileContent(final Context ctx, final String fileName, final String content) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String filePath =
                                            "/data/user/0/"
                                                    + ctx.getPackageName()
                                                    + "/files/"
                                                    + fileName;
                                    File file = new File(filePath);

                                    FileWriter writer = new FileWriter(file);
                                    writer.write(content);
                                    writer.close();
                                } catch (Exception e) {
                                    bvLog("[BetterVia] 保存文件失败: " + e);
                                }
                            }
                        })
                .start();
    }

    private void showHomepageBeautyDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        title.setTextColor(textColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);

                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_dialog_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        subtitle.setTextColor(hintColor);
                        subtitle.setGravity(Gravity.CENTER);
                        subtitle.setPadding(0, 0, 0, dp(act, 16));
                        root.addView(subtitle);

                        final FrameLayout previewContainer = new FrameLayout(act);
                        LinearLayout.LayoutParams preLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 150));
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
                        previewContainer.addView(
                                imageView,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                        previewContainer.addView(
                                maskView,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));

                        root.addView(previewContainer);

                        refreshPreview(ctx, imageView, maskView, homepageMaskAlpha, savedRgb);

                        Button pickBtn = new Button(act);
                        applyClickAnim(pickBtn);
                        pickBtn.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_pick_btn"));
                        pickBtn.setTextColor(okBtnTextColor);
                        pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        pickBtn.setTypeface(null, Typeface.BOLD);
                        pickBtn.setBackground(getRoundBg(act, okBtnBgColor, 12));
                        root.addView(pickBtn);

                        TextView alphaTitle = new TextView(act);
                        alphaTitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_mask_alpha"));
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
                        colorTitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_mask_color_rgb"));
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
                        LinearLayout.LayoutParams editLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        rgbContainer.addView(rgbEdit, editLp);
                        TextView rgbHint = new TextView(act);
                        rgbHint.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "homepage_bg_mask_color_hint"));
                        rgbHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        rgbHint.setTextColor(hintColor);
                        rgbHint.setPadding(dp(act, 4), dp(act, 4), 0, 0);
                        rgbContainer.addView(rgbHint);

                        root.addView(rgbContainer);

                        SeekBar.OnSeekBarChangeListener alphaListener =
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(
                                            SeekBar seekBar, int progress, boolean fromUser) {
                                        if (fromUser) {
                                            homepageMaskAlpha = progress;
                                            String rgbStr = rgbEdit.getText().toString();
                                            int rgbColor = parseRgbColor(rgbStr, 0);
                                            refreshPreview(
                                                    ctx,
                                                    imageView,
                                                    maskView,
                                                    homepageMaskAlpha,
                                                    rgbColor);
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {}

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                };
                        alphaSeek.setOnSeekBarChangeListener(alphaListener);
                        rgbEdit.addTextChangedListener(
                                new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(
                                            CharSequence s, int start, int count, int after) {}

                                    @Override
                                    public void onTextChanged(
                                            CharSequence s, int start, int before, int count) {}

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        String rgbStr = s.toString();
                                        int rgbColor = parseRgbColor(rgbStr, 0);
                                        refreshPreview(
                                                ctx,
                                                imageView,
                                                maskView,
                                                homepageMaskAlpha,
                                                rgbColor);
                                    }
                                });

                        LinearLayout btnRow = new LinearLayout(act);
                        btnRow.setOrientation(LinearLayout.HORIZONTAL);
                        btnRow.setGravity(Gravity.CENTER);
                        btnRow.setPadding(0, dp(act, 24), 0, 0);

                        Button cancel = new Button(act);
                        applyClickAnim(cancel);
                        cancel.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
                        cancel.setTextColor(cancelBtnTextColor);
                        cancel.setBackground(getRoundBg(act, cancelBtnBgColor, 12));
                        Button ok = new Button(act);
                        applyClickAnim(ok);
                        ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
                        ok.setTextColor(okBtnTextColor);
                        ok.setBackground(getRoundBg(act, okBtnBgColor, 12));

                        LinearLayout.LayoutParams btnLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
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
                            DisplayMetrics metrics = new DisplayMetrics();
                            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int width = (int) (metrics.widthPixels * 0.9);
                            WindowManager.LayoutParams layoutParams =
                                    new WindowManager.LayoutParams();
                            layoutParams.copyFrom(win.getAttributes());
                            layoutParams.width = width;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity = Gravity.CENTER;
                            win.setAttributes(layoutParams);
                        }

                        cancel.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                        ok.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String rgbStr = rgbEdit.getText().toString();
                                        int rgbColor = parseRgbColor(rgbStr, 0);
                                        if (rgbStr.trim().length() > 0
                                                && !isValidRgbColor(rgbStr)) {
                                            jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "homepage_bg_mask_color_invalid"));
                                            return;
                                        }
                                        putPrefInt(ctx, KEY_HOMEPAGE_MASK_A, homepageMaskAlpha);
                                        putPrefInt(ctx, KEY_HOMEPAGE_MASK_C, rgbColor);

                                        jiguroMessageWithContext(
                                                ctx,
                                                LocalizedStringProvider.getInstance()
                                                        .get(ctx, "homepage_bg_saved"));
                                        dialog.dismiss();
                                    }
                                });

                        pickBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                        i.setType("image/*");
                                        i.addCategory(Intent.CATEGORY_OPENABLE);
                                        act.startActivityForResult(
                                                Intent.createChooser(
                                                        i,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "homepage_bg_pick_title")),
                                                0x1002);
                                    }
                                });

                        dialog.show();
                        animateDialogEntrance(root, act);
                    }
                });
    }

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

    private void hookHomepageInjection(
            final Context ctx, ClassLoader cl, final String imgPath, final int maskColor) {
        String scriptRepo1Class =
                ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_1, ctx);
        String scriptRepo1Method =
                ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.SCRIPT_REPO_1, ctx);

        XposedHelpers.findAndHookMethod(
                scriptRepo1Class,
                cl,
                scriptRepo1Method,
                Context.class,
                List.class,
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String uri = (String) param.getResult();
                        File html = new File(Uri.parse(uri).getPath());
                        if (!html.exists()) return;

                        StringBuilder htmlSb = new StringBuilder();
                        BufferedReader br = null;
                        try {
                            br = new BufferedReader(new FileReader(html));
                            String line;
                            while ((line = br.readLine()) != null) htmlSb.append(line).append("\n");
                        } finally {
                            if (br != null)
                                try {
                                    br.close();
                                } catch (Exception ignored) {
                                }
                        }

                        String originalHtml = htmlSb.toString();
                        String modifiedHtml = originalHtml;

                        if (imgPath != null && !imgPath.isEmpty() && new File(imgPath).exists()) {
                            int alpha = getPrefInt(ctx, KEY_HOMEPAGE_MASK_A, 120);
                            int rgbColor = getPrefInt(ctx, KEY_HOMEPAGE_MASK_C, 0x000000);
                            bvLog(
                                    "[BetterVia] 读取设置透明度: "
                                            + alpha
                                            + ", 颜色值: "
                                            + Integer.toHexString(rgbColor));
                            String cssColor = colorToCssString(alpha, rgbColor);

                            bvLog("[BetterVia] 最终颜色值转换: " + cssColor);
                            String encodedPath = imgPath.replace("'", "\\'").replace("\\", "\\\\");
                            String backgroundStyle =
                                    "background:url('file:///"
                                            + encodedPath
                                            + "') no-repeat center/cover fixed;";
                            String maskStyle = "background:" + cssColor + ";";

                            String newBodyContent =
                                    "<body style=\""
                                            + backgroundStyle
                                            + "\"><div"
                                            + " style='position:fixed;top:0;left:0;right:0;bottom:0;"
                                            + maskStyle
                                            + "z-index:0;'></div>"
                                            + "<div style='position:relative;z-index:1;'>";

                            modifiedHtml =
                                    originalHtml
                                            .replace("<body>", newBodyContent)
                                            .replace("</body>", "</div></body>");
                        }

                        if (!modifiedHtml.equals(originalHtml)) {
                            FileWriter fw = null;
                            try {
                                fw = new FileWriter(html);
                                fw.write(modifiedHtml);
                                bvLog("[BetterVia] 成功应用主页注入");
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
                    }
                });
    }

    private void hookSearchBoxRestore(final Context ctx, ClassLoader cl) {
        String writerClass =
                ViaClassMapping.getClassName(
                        ViaClassMapping.ClassMethodKey.HOMEPAGE_CSS_WRITER_A, ctx);
        String writerMethod =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.HOMEPAGE_CSS_WRITER_A, ctx);
        if (writerClass == null
                || writerMethod == null
                || writerClass.isEmpty()
                || writerMethod.isEmpty()) return;
        try {
            XposedHelpers.findAndHookMethod(
                    writerClass,
                    cl,
                    writerMethod,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (!getPrefBoolean(ctx, KEY_RESTORE_OLD_SEARCH_BOX, false)) return;
                            Object arg = param.args[0];
                            if (!(arg instanceof String)) return;
                            String css = (String) arg;
                            if (css.contains("#search_bar_trigger")) {
                                int idx = css.lastIndexOf('}');
                                if (idx >= 0) {
                                    param.args[0] =
                                            css.substring(0, idx)
                                                    + "display:none!important;"
                                                    + css.substring(idx);
                                }
                            }
                        }
                    });
        } catch (Throwable t) {
            bvLog("[BetterVia] 主页CSS写入器Hook安装失败（可能版本不支持）: " + t);
        }
    }

    private void applySearchBoxRestoreToCss(final Context ctx, boolean enable) {
        try {
            String filesDir = "/data/user/0/" + ctx.getPackageName() + "/files/";
            File cssFile = new File(filesDir + "homepage.css");
            if (!cssFile.exists()) {
                bvLog("[BetterVia] homepage.css 不存在，跳过搜索框 CSS 落盘（运行时 Hook 仍生效）");
                return;
            }
            String content = readFileText(cssFile);
            if (content == null) return;

            final String INJECT = "display:none!important;";
            StringBuilder sb = new StringBuilder(content);
            int searchFrom = 0;
            while (true) {
                int sel = sb.indexOf("#search_bar_trigger", searchFrom);
                if (sel < 0) break;
                int open = sb.indexOf("{", sel);
                if (open < 0) break;
                int close = sb.indexOf("}", open);
                if (close < 0) break;

                if (enable) {
                    int existing = sb.indexOf(INJECT, open + 1);
                    if (existing < 0 || existing >= close) {
                        sb.insert(close, INJECT);
                    }
                } else {
                    int at;
                    while ((at = sb.indexOf(INJECT, open + 1)) >= 0 && at < close) {
                        sb.delete(at, at + INJECT.length());
                        close = sb.indexOf("}", open);
                    }
                }
                searchFrom = close + 1;
            }
            content = sb.toString();

            writeFileText(cssFile, content);
            bvLog("[BetterVia] 已" + (enable ? "写入" : "移除") + "搜索框恢复 CSS（homepage.css）");
        } catch (Throwable t) {
            bvLog("[BetterVia] 搜索框恢复 CSS 落盘失败: " + t);
        }
    }

    private String readFileText(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = fis.read(buf)) != -1) bos.write(buf, 0, n);
            return new String(bos.toByteArray(), "UTF-8");
        } catch (Throwable t) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private boolean writeFileText(File file, String text) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(text.getBytes("UTF-8"));
            fos.flush();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private void commitSearchBoxRestore(final Context ctx, boolean enable) {
        putPrefBoolean(ctx, KEY_RESTORE_OLD_SEARCH_BOX, enable);
        applySearchBoxRestoreToCss(ctx, enable);
        jiguroMessageWithContext(
                ctx,
                LocalizedStringProvider.getInstance().get(ctx, "restore_old_search_box_applied"));
        new Handler(Looper.getMainLooper())
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                restartVia(ctx);
                            }
                        },
                        RESTART_VIA_DELAY_MS);
    }

    private void handleActivityResult(
            int requestCode, int resultCode, Intent data, final Activity activity) {
        if (resultCode != Activity.RESULT_OK || data == null) return;

        if (requestCode == 0x1002) {
            Uri uri = data.getData();
            if (saveUserImage(activity, uri)) {
                homepageBgPath = getPrefString(activity, KEY_HOMEPAGE_BG, "");
                if (Context != null && Context instanceof Activity) {
                    ((Activity) Context)
                            .runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            jiguroMessageWithContext(
                                                    activity,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(activity, "homepage_bg_set_ok"));
                                        }
                                    });
                }
            }
        } else if (requestCode == 0x2001) {
            monetManager.handleSaveApkResult(activity, data);
        } else if (requestCode == 0x3001) {
            Uri uri = data.getData();
            if (StartupExecutionHelper.saveStartupImage(activity, uri)) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                jiguroMessageWithContext(
                                        activity,
                                        LocalizedStringProvider.getInstance()
                                                .get(activity, "startup_image_set_ok"));
                            }
                        });
            }
        } else if (requestCode == 0x3002) {
            Uri uri = data.getData();
            if (StartupExecutionHelper.saveStartupMusic(activity, uri)) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                jiguroMessageWithContext(
                                        activity,
                                        LocalizedStringProvider.getInstance()
                                                .get(activity, "startup_music_set_ok"));
                            }
                        });
            }
        }
    }

    private boolean saveUserImage(Activity act, Uri uri) {
        if (uri == null) return false;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            File outFile = new File(act.getFilesDir(), "homepage_bg.jpg");
            in = act.getContentResolver().openInputStream(uri);
            out = new FileOutputStream(outFile);
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
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

    private void showBlockMenuBarDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        final String current = getPrefString(ctx, KEY_BLOCK_MENU_BAR, "");
        SettingsUI.showInputDialog(
                act,
                "block_menu_bar_dialog_title",
                "block_menu_bar_dialog_hint",
                "block_menu_bar_input_hint",
                current,
                3,
                "dialog_ok",
                null,
                new SettingsUI.OnInputListener() {
                    @Override
                    public void onConfirm(String input) {
                        String urls = input.trim();
                        putPrefString(ctx, KEY_BLOCK_MENU_BAR, urls);
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "block_menu_bar_saved"));
                    }

                    @Override
                    public void onCancel() {}
                },
                true);
    }

    private XC_MethodHook.Unhook blockMenuBarHook = null;
    private static volatile String sBlockMenuRaw = null;
    private static volatile String[] sBlockMenuList = null;

    private void setBlockMenuBarHook(final Context ctx, ClassLoader cl, boolean on) {
        if (on) {
            if (blockMenuBarHook == null) {
                try {
                    String scriptRepo2Class =
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.SCRIPT_REPO_2, ctx);
                    String scriptRepo2Method =
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.SCRIPT_REPO_2, ctx);

                    XposedHelpers.findAndHookMethod(
                            scriptRepo2Class,
                            cl,
                            scriptRepo2Method,
                            int.class,
                            int.class,
                            String.class,
                            String.class,
                            String.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    String currentUrl = getCurrentUrl();
                                    if (currentUrl == null || currentUrl.isEmpty()) {
                                        return;
                                    }
                                    String[] urlList = getBlockMenuUrls(ctx);
                                    if (urlList.length == 0) {
                                        return;
                                    }
                                    boolean shouldBlock = false;
                                    for (String url : urlList) {
                                        String trimmedUrl = url.trim();
                                        if (!trimmedUrl.isEmpty()
                                                && currentUrl.contains(trimmedUrl)) {
                                            shouldBlock = true;
                                            bvLogVerbose(
                                                    "[BetterVia] 屏蔽菜单栏: 当前URL "
                                                            + currentUrl
                                                            + " 匹配 "
                                                            + trimmedUrl);
                                            break;
                                        }
                                    }
                                    if (shouldBlock) {
                                        param.setResult(null);
                                        bvLogVerbose("[BetterVia] 已阻止菜单栏显示");
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

    private static String[] getBlockMenuUrls(Context ctx) {
        String raw = getPrefString(ctx, KEY_BLOCK_MENU_BAR, "");
        if (raw == null) raw = "";
        String[] cached = sBlockMenuList;
        if (cached != null && raw.equals(sBlockMenuRaw)) return cached;
        String[] parsed = raw.trim().isEmpty() ? new String[0] : raw.split(",");
        sBlockMenuRaw = raw;
        sBlockMenuList = parsed;
        return parsed;
    }

    static boolean isViaVersionAtLeast(int versionCode) {
        Context ctx = currentActivity != null ? currentActivity : Context;
        return ViaClassMapping.getActiveMappingVersionCode(ctx) >= versionCode;
    }

    static Activity peekCurrentActivity() {
        Activity act = currentActivity;
        if (act != null && !act.isFinishing() && !act.isDestroyed()) {
            return act;
        }
        return null;
    }

    private String getCurrentUrl() {
        try {
            Activity activity = currentActivity != null ? currentActivity : Context;
            if (activity == null) {
                return "";
            }
            String field =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.SHELL_WEBVIEW_FIELD, activity);
            if (field != null && !field.isEmpty()) {
                try {
                    Object webView = XposedHelpers.getObjectField(activity, field);
                    if (webView instanceof WebView) {
                        String url = ((WebView) webView).getUrl();
                        if (url != null && !url.isEmpty()) {
                            return url;
                        }
                    }
                } catch (Throwable ignored) {
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
        } catch (Exception e) {
            bvLogVerbose("[BetterVia] 获取当前URL失败: " + e.getMessage());
        }
        return "";
    }

    private void showUserAgentDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "user_agent_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        SettingsList list = new SettingsList(act);
                        List<UserAgentInfo> uaList = getPersonalizedUserAgents(act);
                        for (final UserAgentInfo uaInfo : uaList) {
                            final String userAgent = uaInfo.userAgent;
                            list.addItemKeepRipple(
                                    uaInfo.browserNameKey,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            copyToClipboard(act, userAgent);
                                            jiguroMessageWithContext(
                                                    act,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(act, "user_agent_copied"));
                                        }
                                    });
                            list.updateItemText(list.getItemCount() - 1, userAgent, true);
                        }
                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private static class UserAgentInfo {

        String browserNameKey;
        String userAgent;

        UserAgentInfo(String browserNameKey, String userAgent) {
            this.browserNameKey = browserNameKey;
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
        String[][] uaTemplates = {
            {
                "ua_name_baidu",
                "Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98"
                    + " Mobile Safari/537.36 T7/13.59 SP-engine/2.98.0 baiduboxapp/13.59.0.10"
                    + " (Baidu; P1 12) NABar/1.0"
            },
            {
                "ua_name_xiaomi",
                "Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model}"
                    + " Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/122.0.6261.119 Mobile Safari/537.36 XiaoMi/MiuiBrowser/19.2.820324"
            },
            {
                "ua_name_huawei",
                "Mozilla/5.0 (Linux; {android_version}; HarmonyOS; {device_model}; HMSCore"
                    + " 5.3.0.312) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93"
                    + " HuaweiBrowser/11.1.1.310 Mobile Safari/537.36"
            },
            {
                "ua_name_uc",
                "Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model}"
                    + " Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0"
                    + " Chrome/100.0.4896.58 UCBrowser/17.5.0.1381 Mobile Safari/537.36"
            },
            {
                "ua_name_edge",
                "Mozilla/5.0 (Linux; {android_version}; K) AppleWebKit/537.36 (KHTML, like Gecko)"
                        + " Chrome/134.0.0.0 Mobile Safari/537.36 EdgA/134.0.0.0"
            },
            {
                "ua_name_qq",
                "Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model}"
                    + " Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0"
                    + " Chrome/109.0.5414.86 MQQBrowser/16.1 Mobile Safari/537.36 COVC/046915"
            },
            {
                "ua_name_quark",
                "Mozilla/5.0 (Linux; U; {android_version}; zh_CN; {device_model}"
                    + " Build/{build_version}) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0"
                    + " Chrome/100.0.4896.58 Quark/7.9.6.781 Mobile Safari/537.36"
            },
            {
                "ua_name_360",
                "Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/62.0.3202.97"
                    + " Mobile Safari/537.36"
            },
            {
                "ua_name_simple",
                "Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv)"
                        + " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 danSearchCraft"
                        + " Chrome/76.0.3809.89 Mobile Safari/537.36"
            },
            {
                "ua_name_chrome",
                "Mozilla/5.0 (Linux; {android_version}; {device_model}) AppleWebKit/537.36 (KHTML,"
                    + " like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36"
            },
            {
                "ua_name_wechat",
                "Mozilla/5.0 (Linux; {android_version}; {device_model} Build/{build_version}; wv)"
                    + " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.62"
                    + " XWEB/2893 MMWEBSDK/20210601 Mobile Safari/537.36 MMWEBID/9453"
                    + " MicroMessenger/8.0.9.1940(0x28000951) Process/toolsmp WeChat/arm64 Weixin"
                    + " NetType/4G Language/zh_CN ABI/arm64"
            },
            {
                "ua_name_iphone",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 18_4_1 like Mac OS X) AppleWebKit/605.1.15"
                    + " (KHTML, like Gecko) Version/18.4 Mobile/15E148 Safari/604.1"
            },
            {
                "ua_name_taobao",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko)"
                        + " Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11"
            }
        };

        for (String[] template : uaTemplates) {
            String personalizedUA =
                    template[1]
                            .replace("{android_version}", androidVersion)
                            .replace("{device_model}", deviceModel)
                            .replace("{build_version}", buildVersion);

            uaList.add(new UserAgentInfo(template[0], personalizedUA));
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
            }
            if (downloadDialogCreateHook != null) {
                downloadDialogCreateHook.unhook();
                downloadDialogCreateHook = null;
                bvLog("[BetterVia] 下载对话框分享按钮已停用");
            }
            downloadDialogShareEnabled = false;
        }
        putPrefBoolean(ctx, KEY_DOWNLOAD_DIALOG_SHARE, on);
    }

    private void addShareButtonToDownloadDialog(final Context ctx, ClassLoader cl) {
        try {
            downloadDialogShareHook =
                    XposedHelpers.findAndHookMethod(
                            Dialog.class,
                            "show",
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    try {
                                        final Dialog dialog = (Dialog) param.thisObject;
                                        if (!downloadDialogShareEnabled) {
                                            return;
                                        }
                                        if (isViaDownloadDialog(dialog)) {
                                            new Handler(Looper.getMainLooper())
                                                    .postDelayed(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        addShareButtonToDialog(
                                                                                dialog, ctx);
                                                                    } catch (Exception e) {
                                                                        bvLog(
                                                                                "[BetterVia]"
                                                                                    + " 添加分享按钮异常: "
                                                                                        + e);
                                                                    }
                                                                }
                                                            },
                                                            100);
                                        }
                                    } catch (Exception e) {
                                        bvLog("[BetterVia] Hook Dialog.show失败: " + e);
                                    }
                                }
                            });
            downloadDialogCreateHook =
                    XposedHelpers.findAndHookMethod(
                            AlertDialog.Builder.class,
                            "create",
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    try {
                                        final AlertDialog dialog = (AlertDialog) param.getResult();
                                        if (dialog == null) return;
                                        if (!downloadDialogShareEnabled) {
                                            return;
                                        }
                                        dialog.setOnShowListener(
                                                new DialogInterface.OnShowListener() {
                                                    @Override
                                                    public void onShow(
                                                            DialogInterface dialogInterface) {
                                                        try {
                                                            if (isViaDownloadDialog(dialog)) {
                                                                addShareButtonToDialog(dialog, ctx);
                                                            }
                                                        } catch (Exception e) {
                                                            bvLog(
                                                                    "[BetterVia] AlertDialog显示监听异常:"
                                                                        + " "
                                                                            + e);
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
            Context ctx = dialog.getContext();
            int copyLinkId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, ctx);
            int cancelId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, ctx);
            int okId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, ctx);

            @SuppressLint("ResourceType")
            View copyLinkButton = dialog.findViewById(copyLinkId);
            View cancelButton = dialog.findViewById(cancelId);
            View okButton = dialog.findViewById(okId);

            return copyLinkButton != null && cancelButton != null && okButton != null;
        } catch (Exception e) {
            return false;
        }
    }

    private ViewGroup findButtonContainerRecursive(View view) {
        if (!(view instanceof ViewGroup)) return null;

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

    private void setupShareButtonClick(
            TextView shareButton, final Dialog dialog, final Context ctx) {
        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            bvLog("[BetterVia] 分享按钮被点击");
                            String[] downloadInfo = extractDownloadInfoFromDialog(dialog, ctx);
                            String fileName = downloadInfo[0];
                            String fileUrl = downloadInfo[1];
                            String fileSize = downloadInfo[2];

                            bvLog(
                                    "[BetterVia] 提取到的下载信息 - 文件名: "
                                            + fileName
                                            + ", 大小: "
                                            + fileSize
                                            + ", URL: "
                                            + (fileUrl.isEmpty() ? "空" : "已获取"));
                            if (fileUrl.isEmpty()) {
                                jiguroMessageWithContext(
                                        ctx,
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "share_no_download_link"));
                                return;
                            }
                            String shareText = createShareText(fileName, fileSize, fileUrl, ctx);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                            Intent chooser =
                                    Intent.createChooser(
                                            shareIntent,
                                            LocalizedStringProvider.getInstance()
                                                    .get(ctx, "share_chooser_title"));
                            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            ctx.startActivity(chooser);
                            jiguroMessageWithContext(
                                    ctx,
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "share_downloading"));
                        } catch (Exception e) {
                            bvLog("[BetterVia] 分享失败: " + e);
                            jiguroMessageWithContext(
                                    ctx,
                                    LocalizedStringProvider.getInstance().get(ctx, "share_failed"));
                        }
                    }
                });
    }

    private void insertShareButtonToContainer(
            ViewGroup container, TextView shareButton, Dialog dialog) {
        try {
            Context ctx = dialog.getContext();
            int copyLinkId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, ctx);
            int cancelId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, ctx);
            int okId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, ctx);

            View copyLinkButton = dialog.findViewById(copyLinkId);
            if (copyLinkButton == null) {
                bvLog("[BetterVia] 未找到复制链接按钮，无法确定插入位置");
                return;
            }
            View okButton = dialog.findViewById(okId);
            View cancelButton = dialog.findViewById(cancelId);
            int referenceMargin = dp(dialog.getContext(), 8);
            if (okButton != null && cancelButton != null) {
                if (okButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams okParams =
                            (ViewGroup.MarginLayoutParams) okButton.getLayoutParams();
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
                bvLog(
                        "[BetterVia] 检测到RelativeLayout容器，设置分享按钮在复制链接按钮右侧，间距: "
                                + referenceMargin
                                + "px");
                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_TOP, copyLinkButton.getId());
                params.addRule(RelativeLayout.ALIGN_BOTTOM, copyLinkButton.getId());
                params.addRule(RelativeLayout.RIGHT_OF, copyLinkButton.getId());
                if (copyLinkButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams refParams =
                            (ViewGroup.MarginLayoutParams) copyLinkButton.getLayoutParams();
                    params.setMargins(
                            referenceMargin,
                            refParams.topMargin,
                            refParams.rightMargin,
                            refParams.bottomMargin);
                    params.height = refParams.height;
                } else {
                    params.setMargins(referenceMargin, 0, 0, 0);
                }

                shareButton.setLayoutParams(params);
            } else if (container instanceof LinearLayout) {
                LinearLayout.LayoutParams refParams =
                        (LinearLayout.LayoutParams) copyLinkButton.getLayoutParams();
                LinearLayout.LayoutParams newParams =
                        new LinearLayout.LayoutParams(
                                refParams.width, refParams.height, refParams.weight);
                newParams.setMargins(
                        refParams.leftMargin,
                        refParams.topMargin,
                        refParams.rightMargin,
                        refParams.bottomMargin);
                newParams.gravity = refParams.gravity;
                shareButton.setLayoutParams(newParams);
            } else {
                ViewGroup.LayoutParams refParams = copyLinkButton.getLayoutParams();
                ViewGroup.LayoutParams newParams =
                        new ViewGroup.LayoutParams(refParams.width, refParams.height);
                shareButton.setLayoutParams(newParams);
            }
            container.addView(shareButton, copyLinkIndex + 1);
            bvLog("[BetterVia] 分享按钮已插入到位置: " + (copyLinkIndex + 1));
            container.requestLayout();
        } catch (Exception e) {
            bvLog("[BetterVia] 插入分享按钮失败: " + e);
            try {
                ViewGroup.LayoutParams simpleParams =
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
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
            int copyLinkId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, ctx);
            int shareButtonId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, ctx);

            View copyLinkButton = dialog.findViewById(copyLinkId);
            if (copyLinkButton == null) {
                bvLog("[BetterVia] 未找到复制链接按钮，无法获取样式");
                return null;
            }
            TextView shareButton = new TextView(ctx);
            shareButton.setId(shareButtonId);
            shareButton.setClickable(true);
            shareButton.setFocusable(true);
            shareButton.setText(
                    LocalizedStringProvider.getInstance().get(ctx, "download_dialog_share"));
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
                    Drawable backgroundCopy =
                            refBackground.getConstantState().newDrawable().mutate();
                    shareButton.setBackground(backgroundCopy);
                    bvLog("[BetterVia] 已创建独立的背景Drawable");
                } catch (Exception e) {
                    bvLog("[BetterVia] 创建独立Drawable失败，使用原始背景: " + e);
                    shareButton.setBackground(refBackground);
                }
            }
            shareButton.setPadding(
                    copyLinkButton.getPaddingLeft(),
                    copyLinkButton.getPaddingTop(),
                    copyLinkButton.getPaddingRight(),
                    copyLinkButton.getPaddingBottom());

            return shareButton;
        } catch (Exception e) {
            bvLog("[BetterVia] 创建分享按钮失败: " + e);
            return null;
        }
    }

    private void fixCopyLinkButtonBackground(Dialog dialog) {
        try {
            Context ctx = dialog.getContext();
            int copyLinkId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, ctx);

            View copyLinkButton = dialog.findViewById(copyLinkId);
            if (copyLinkButton == null) return;
            Drawable background = copyLinkButton.getBackground();
            if (background != null) {
                Drawable independentBackground =
                        background.getConstantState().newDrawable().mutate();
                copyLinkButton.setBackground(independentBackground);
                bvLog("[BetterVia] 已修复复制链接按钮的背景状态");
            }
        } catch (Exception e) {
            bvLog("[BetterVia] 修复复制链接按钮背景失败: " + e);
        }
    }

    private void addShareButtonToDialog(final Dialog dialog, final Context ctx) {
        try {
            int shareButtonId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_SHARE_BUTTON, ctx);

            if (dialog.findViewById(shareButtonId) != null) {
                bvLog("[BetterVia] 分享按钮已存在，跳过添加");
                return;
            }
            fixCopyLinkButtonBackground(dialog);
            ViewGroup buttonContainer = findButtonContainer(dialog);
            if (buttonContainer == null) {
                bvLog("[BetterVia] 未找到按钮容器");
                return;
            }

            bvLog(
                    "[BetterVia] 找到按钮容器，类型: "
                            + buttonContainer.getClass().getSimpleName()
                            + ", 子视图数量: "
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
            Context ctx = dialog.getContext();
            int copyLinkId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_COPY_LINK_BUTTON, ctx);
            int cancelId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_CANCEL_BUTTON, ctx);
            int okId =
                    ViaClassMapping.getResourceId(
                            ViaClassMapping.ResourceKey.DOWNLOAD_DIALOG_OK_BUTTON, ctx);

            View copyLinkButton = dialog.findViewById(copyLinkId);
            View cancelButton = dialog.findViewById(cancelId);
            View okButton = dialog.findViewById(okId);

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
        if (!(view instanceof ViewGroup)) return null;

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
            bvLog(
                    "[BetterVia] "
                            + stage
                            + " - 容器类型: "
                            + container.getClass().getSimpleName()
                            + ", 子视图数量: "
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
                } else if (text.contains("MB")
                        || text.contains("KB")
                        || text.contains("GB")
                        || text.contains("字节")
                        || text.contains("B")
                        || text.matches(".*\\d+.*")) {
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
                            if ("http".equalsIgnoreCase(scheme)
                                    || "https".equalsIgnoreCase(scheme)) {
                                bvLog("[BetterVia] 通过Intent获取到URL: " + data.toString());
                                return data.toString();
                            }
                        }
                        String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
                        if (extraText != null
                                && (extraText.startsWith("http") || extraText.startsWith("ftp"))) {
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

    private void showUserSandboxDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final boolean[] currentEnable =
                new boolean[] {getPrefBoolean(ctx, KEY_USER_SANDBOX_ENABLE, false)};
        final boolean[] scopeSel =
                new boolean[] {
                    getPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_DOWNLOAD, false),
                    getPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_CACHE, false)
                };

        final String[] SCOPE_KEYS =
                new String[] {"user_sandbox_hide_download", "user_sandbox_hide_cache"};

        final Runnable persistScope =
                new Runnable() {
                    @Override
                    public void run() {
                        putPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_DOWNLOAD, scopeSel[0]);
                        putPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_CACHE, scopeSel[1]);
                    }
                };

        SettingsUI.showPage(
                act,
                "user_sandbox_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final int[] scopeRowRef = new int[1];
                        final int[] enableRowRef = new int[1];

                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        currentEnable[0] = isChecked;
                                        list.updateItemText(
                                                scopeRowRef[0],
                                                buildUaSelectionSummary(ctx, SCOPE_KEYS, scopeSel),
                                                !isChecked);
                                        if (isChecked) {
                                            if (!(scopeSel[0] || scopeSel[1])) {
                                                jiguroMessageWithContext(
                                                        ctx,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "user_sandbox_select_one"));
                                                list.updateSwitch(enableRowRef[0], false);
                                                currentEnable[0] = false;
                                                list.updateItemText(
                                                        scopeRowRef[0],
                                                        buildUaSelectionSummary(
                                                                ctx, SCOPE_KEYS, scopeSel),
                                                        true);
                                                return;
                                            }
                                        }
                                        putPrefBoolean(
                                                ctx, KEY_USER_SANDBOX_ENABLE, currentEnable[0]);
                                        persistScope.run();
                                        performSandboxOperation(ctx, currentEnable[0], false);
                                    }
                                };
                        enableRowRef[0] = list.getItemCount();
                        list.addSwitchItem(
                                "user_sandbox_enable",
                                "user_sandbox_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "user_sandbox_notes_title",
                                                "user_sandbox_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("user_sandbox_notes_title", notesRunnable);

                        list.addSectionHeader("user_sandbox_advanced");

                        scopeRowRef[0] = list.getItemCount();
                        final Runnable scopeRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "user_sandbox_incognito_scope",
                                                SCOPE_KEYS,
                                                scopeSel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        System.arraycopy(
                                                                checked,
                                                                0,
                                                                scopeSel,
                                                                0,
                                                                scopeSel.length);
                                                        list.updateItemText(
                                                                scopeRowRef[0],
                                                                buildUaSelectionSummary(
                                                                        ctx, SCOPE_KEYS, scopeSel),
                                                                !currentEnable[0]);
                                                        persistScope.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("user_sandbox_incognito_scope", scopeRunnable);
                        list.updateItemText(
                                scopeRowRef[0],
                                buildUaSelectionSummary(ctx, SCOPE_KEYS, scopeSel),
                                !currentEnable[0]);

                        final Runnable forceRestoreRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        putPrefBoolean(ctx, KEY_USER_SANDBOX_ENABLE, false);
                                        SettingsUI.dismissCurrentPage(act);
                                        performSandboxOperation(ctx, false, true);
                                    }
                                };
                        list.addItemKeepRipple(
                                "user_sandbox_force_restore",
                                "user_sandbox_force_restore_hint",
                                forceRestoreRunnable);

                        content.addView(list);
                    }
                });
    }

    private void showOnlinePreviewDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final boolean[] currentEnable =
                new boolean[] {getPrefBoolean(ctx, KEY_ONLINE_PREVIEW_ENABLE, false)};
        final boolean[] formatSel =
                new boolean[] {
                    getPrefBoolean(ctx, KEY_ONLINE_PREVIEW_WORD, true),
                    getPrefBoolean(ctx, KEY_ONLINE_PREVIEW_PPT, true),
                    getPrefBoolean(ctx, KEY_ONLINE_PREVIEW_EXCEL, true),
                    getPrefBoolean(ctx, KEY_ONLINE_PREVIEW_PDF, true)
                };
        final int[] source = new int[] {getPrefInt(ctx, KEY_ONLINE_PREVIEW_SOURCE, 0)};

        final String[] SOURCE_KEYS =
                new String[] {
                    "online_preview_source_kkfileview", "online_preview_source_basemetas"
                };
        final String[] FORMAT_KEYS =
                new String[] {
                    "online_preview_word",
                    "online_preview_ppt",
                    "online_preview_excel",
                    "online_preview_pdf"
                };

        final Runnable persist =
                new Runnable() {
                    @Override
                    public void run() {
                        putPrefBoolean(ctx, KEY_ONLINE_PREVIEW_ENABLE, currentEnable[0]);
                        putPrefBoolean(ctx, KEY_ONLINE_PREVIEW_WORD, formatSel[0]);
                        putPrefBoolean(ctx, KEY_ONLINE_PREVIEW_PPT, formatSel[1]);
                        putPrefBoolean(ctx, KEY_ONLINE_PREVIEW_EXCEL, formatSel[2]);
                        putPrefBoolean(ctx, KEY_ONLINE_PREVIEW_PDF, formatSel[3]);
                        putPrefInt(ctx, KEY_ONLINE_PREVIEW_SOURCE, source[0]);
                    }
                };

        SettingsUI.showPage(
                act,
                "online_preview_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final int[] enableRowRef = new int[1];

                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            showOnlinePreviewEnableWarningDialog(
                                                    act,
                                                    ctx,
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            currentEnable[0] = true;
                                                            persist.run();
                                                        }
                                                    },
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            list.updateSwitch(
                                                                    enableRowRef[0], false);
                                                        }
                                                    });
                                        } else {
                                            currentEnable[0] = false;
                                            persist.run();
                                        }
                                    }
                                };
                        enableRowRef[0] = list.getItemCount();
                        list.addSwitchItem(
                                "online_preview_enable",
                                "online_preview_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "online_preview_notes_title",
                                                "online_preview_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("online_preview_notes_title", notesRunnable);

                        list.addSectionHeader("online_preview_advanced");

                        final int sourceRow = list.getItemCount();
                        final Runnable sourceRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showSelectDialog(
                                                act,
                                                "online_preview_source",
                                                SOURCE_KEYS,
                                                source[0],
                                                new SettingsUI.OnSelectListener() {
                                                    @Override
                                                    public void onSelect(int index) {
                                                        source[0] = index;
                                                        list.updateItemText(
                                                                sourceRow,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                SOURCE_KEYS[index]),
                                                                true);
                                                        persist.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("online_preview_source", sourceRunnable);
                        list.updateItemText(
                                sourceRow,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, SOURCE_KEYS[source[0]]),
                                true);

                        final int formatRow = list.getItemCount();
                        final Runnable formatRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "online_preview_format",
                                                FORMAT_KEYS,
                                                formatSel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        System.arraycopy(
                                                                checked,
                                                                0,
                                                                formatSel,
                                                                0,
                                                                formatSel.length);
                                                        list.updateItemText(
                                                                formatRow,
                                                                buildUaSelectionSummary(
                                                                        ctx,
                                                                        FORMAT_KEYS,
                                                                        formatSel),
                                                                true);
                                                        persist.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("online_preview_format", formatRunnable);
                        list.updateItemText(
                                formatRow,
                                buildUaSelectionSummary(ctx, FORMAT_KEYS, formatSel),
                                true);

                        content.addView(list);
                    }
                });
    }

    private void showOnlinePreviewEnableWarningDialog(
            final Activity act,
            final Context ctx,
            final Runnable onSave,
            final Runnable onCancelRevert) {
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        SettingsUI.showMessageDialog(
                                act,
                                "online_preview_enable_warning_title",
                                "online_preview_enable_warning_message",
                                "online_preview_enable_warning_checkbox",
                                false,
                                "dialog_ok",
                                "dialog_cancel",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onSave != null) {
                                            onSave.run();
                                        }
                                    }
                                },
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onCancelRevert != null) {
                                            onCancelRevert.run();
                                        }
                                    }
                                },
                                null,
                                true);
                    }
                });
    }

    private void showRandomUaDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final boolean[] currentEnable = new boolean[] {getPrefBoolean(ctx, KEY_RANDOM_UA, false)};
        final boolean[] platformSel =
                new boolean[] {
                    getPrefBoolean(ctx, KEY_UA_PLATFORM_ANDROID, true),
                    getPrefBoolean(ctx, KEY_UA_PLATFORM_IOS, true),
                    getPrefBoolean(ctx, KEY_UA_PLATFORM_WINDOWS, false),
                    getPrefBoolean(ctx, KEY_UA_PLATFORM_MACOS, false),
                    getPrefBoolean(ctx, KEY_UA_PLATFORM_LINUX, false)
                };
        final boolean[] browserSel =
                new boolean[] {
                    getPrefBoolean(ctx, KEY_UA_BROWSER_CHROME, true),
                    getPrefBoolean(ctx, KEY_UA_BROWSER_SAFARI, true),
                    getPrefBoolean(ctx, KEY_UA_BROWSER_EDGE, false),
                    getPrefBoolean(ctx, KEY_UA_BROWSER_FIREFOX, false)
                };
        final String[] customVals =
                new String[] {
                    getPrefString(ctx, KEY_UA_ANDROID_VERSIONS, uaAndroidVersions),
                    getPrefString(ctx, KEY_UA_ANDROID_DEVICES, uaAndroidDevices),
                    getPrefString(ctx, KEY_UA_IOS_VERSIONS, uaIosVersions),
                    getPrefString(ctx, KEY_UA_WINDOWS_TOKENS, uaWindowsTokens),
                    getPrefString(ctx, KEY_UA_MACOS_TOKENS, uaMacosTokens),
                    getPrefString(ctx, KEY_UA_LINUX_TOKENS, uaLinuxTokens)
                };

        final String[] PLATFORM_KEYS =
                new String[] {
                    "random_ua_platform_android",
                    "random_ua_platform_ios",
                    "random_ua_platform_windows",
                    "random_ua_platform_macos",
                    "random_ua_platform_linux"
                };
        final String[] BROWSER_KEYS =
                new String[] {
                    "random_ua_browser_chrome",
                    "random_ua_browser_safari",
                    "random_ua_browser_edge",
                    "random_ua_browser_firefox"
                };
        final String[] CUSTOM_KEYS =
                new String[] {
                    "random_ua_android_versions",
                    "random_ua_android_devices",
                    "random_ua_ios_versions",
                    "random_ua_windows_versions",
                    "random_ua_macos_versions",
                    "random_ua_linux_versions"
                };

        final Runnable persist =
                new Runnable() {
                    @Override
                    public void run() {
                        boolean enable = currentEnable[0];
                        putPrefBoolean(ctx, KEY_RANDOM_UA, enable);
                        putPrefBoolean(ctx, KEY_UA_PLATFORM_ANDROID, platformSel[0]);
                        putPrefBoolean(ctx, KEY_UA_PLATFORM_IOS, platformSel[1]);
                        putPrefBoolean(ctx, KEY_UA_PLATFORM_WINDOWS, platformSel[2]);
                        putPrefBoolean(ctx, KEY_UA_PLATFORM_MACOS, platformSel[3]);
                        putPrefBoolean(ctx, KEY_UA_PLATFORM_LINUX, platformSel[4]);
                        putPrefBoolean(ctx, KEY_UA_BROWSER_CHROME, browserSel[0]);
                        putPrefBoolean(ctx, KEY_UA_BROWSER_SAFARI, browserSel[1]);
                        putPrefBoolean(ctx, KEY_UA_BROWSER_EDGE, browserSel[2]);
                        putPrefBoolean(ctx, KEY_UA_BROWSER_FIREFOX, browserSel[3]);
                        uaAndroid = platformSel[0];
                        uaIos = platformSel[1];
                        uaWindows = platformSel[2];
                        uaMacos = platformSel[3];
                        uaLinux = platformSel[4];
                        uaChrome = browserSel[0];
                        uaSafari = browserSel[1];
                        uaEdge = browserSel[2];
                        uaFirefox = browserSel[3];
                        uaAndroidVersions = customVals[0].trim();
                        uaAndroidDevices = customVals[1].trim();
                        uaIosVersions = customVals[2].trim();
                        uaWindowsTokens = customVals[3].trim();
                        uaMacosTokens = customVals[4].trim();
                        uaLinuxTokens = customVals[5].trim();
                        putPrefString(ctx, KEY_UA_ANDROID_VERSIONS, uaAndroidVersions);
                        putPrefString(ctx, KEY_UA_ANDROID_DEVICES, uaAndroidDevices);
                        putPrefString(ctx, KEY_UA_IOS_VERSIONS, uaIosVersions);
                        putPrefString(ctx, KEY_UA_WINDOWS_TOKENS, uaWindowsTokens);
                        putPrefString(ctx, KEY_UA_MACOS_TOKENS, uaMacosTokens);
                        putPrefString(ctx, KEY_UA_LINUX_TOKENS, uaLinuxTokens);

                        setRandomUa(ctx, act.getClassLoader(), enable);
                    }
                };

        SettingsUI.showPage(
                act,
                "random_ua_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final int[] enableRowRef = new int[1];
                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        currentEnable[0] = isChecked;
                                        if (isChecked) {
                                            boolean anyPlatform =
                                                    platformSel[0]
                                                            || platformSel[1]
                                                            || platformSel[2]
                                                            || platformSel[3]
                                                            || platformSel[4];
                                            boolean anyBrowser =
                                                    browserSel[0]
                                                            || browserSel[1]
                                                            || browserSel[2]
                                                            || browserSel[3];
                                            if (!anyPlatform) {
                                                jiguroMessageWithContext(
                                                        ctx,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "random_ua_select_platform"));
                                                list.updateSwitch(enableRowRef[0], false);
                                                currentEnable[0] = false;
                                                return;
                                            }
                                            if (!anyBrowser) {
                                                jiguroMessageWithContext(
                                                        ctx,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "random_ua_select_browser"));
                                                list.updateSwitch(enableRowRef[0], false);
                                                currentEnable[0] = false;
                                                return;
                                            }
                                        }
                                        persist.run();
                                    }
                                };
                        enableRowRef[0] = list.getItemCount();
                        list.addSwitchItem(
                                "random_ua_enable",
                                "random_ua_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "random_ua_notes_title",
                                                "random_ua_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("random_ua_notes_title", notesRunnable);

                        list.addSectionHeader("random_ua_range");

                        final int platformRow = list.getItemCount();
                        final Runnable platformRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "random_ua_platform_label",
                                                PLATFORM_KEYS,
                                                platformSel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        boolean anyPlat =
                                                                checked[0]
                                                                        || checked[1]
                                                                        || checked[2]
                                                                        || checked[3]
                                                                        || checked[4];
                                                        boolean anyBr =
                                                                browserSel[0]
                                                                        || browserSel[1]
                                                                        || browserSel[2]
                                                                        || browserSel[3];
                                                        if (currentEnable[0] && !anyPlat) {
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "random_ua_select_platform"));
                                                            return;
                                                        }
                                                        if (currentEnable[0] && !anyBr) {
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "random_ua_select_browser"));
                                                            return;
                                                        }
                                                        System.arraycopy(
                                                                checked,
                                                                0,
                                                                platformSel,
                                                                0,
                                                                platformSel.length);
                                                        list.updateItemText(
                                                                platformRow,
                                                                buildUaSelectionSummary(
                                                                        ctx,
                                                                        PLATFORM_KEYS,
                                                                        platformSel),
                                                                true);
                                                        persist.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("random_ua_platform_label", platformRunnable);
                        list.updateItemText(
                                platformRow,
                                buildUaSelectionSummary(ctx, PLATFORM_KEYS, platformSel),
                                true);

                        final int browserRow = list.getItemCount();
                        final Runnable browserRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "random_ua_browser_label",
                                                BROWSER_KEYS,
                                                browserSel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        boolean anyPlat =
                                                                platformSel[0]
                                                                        || platformSel[1]
                                                                        || platformSel[2]
                                                                        || platformSel[3]
                                                                        || platformSel[4];
                                                        boolean anyBr =
                                                                checked[0]
                                                                        || checked[1]
                                                                        || checked[2]
                                                                        || checked[3];
                                                        if (currentEnable[0] && !anyPlat) {
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "random_ua_select_platform"));
                                                            return;
                                                        }
                                                        if (currentEnable[0] && !anyBr) {
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "random_ua_select_browser"));
                                                            return;
                                                        }
                                                        System.arraycopy(
                                                                checked,
                                                                0,
                                                                browserSel,
                                                                0,
                                                                browserSel.length);
                                                        list.updateItemText(
                                                                browserRow,
                                                                buildUaSelectionSummary(
                                                                        ctx,
                                                                        BROWSER_KEYS,
                                                                        browserSel),
                                                                true);
                                                        persist.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("random_ua_browser_label", browserRunnable);
                        list.updateItemText(
                                browserRow,
                                buildUaSelectionSummary(ctx, BROWSER_KEYS, browserSel),
                                true);

                        list.addSectionHeader("random_ua_advanced");

                        final int[] customRows = new int[CUSTOM_KEYS.length];
                        for (int i = 0; i < CUSTOM_KEYS.length; i++) {
                            final int idx = i;
                            customRows[idx] = list.getItemCount();
                            final Runnable customRunnable =
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            SettingsUI.showInputDialog(
                                                    act,
                                                    CUSTOM_KEYS[idx],
                                                    (String) null,
                                                    (String) null,
                                                    customVals[idx],
                                                    4,
                                                    "dialog_ok",
                                                    "dialog_cancel",
                                                    new SettingsUI.OnInputListener() {
                                                        @Override
                                                        public void onConfirm(String input) {
                                                            customVals[idx] = input.trim();
                                                            list.updateItemText(
                                                                    customRows[idx],
                                                                    customVals[idx],
                                                                    true);
                                                            persist.run();
                                                        }

                                                        @Override
                                                        public void onCancel() {}
                                                    });
                                        }
                                    };
                            list.addItemKeepRipple(CUSTOM_KEYS[idx], customRunnable);
                            list.updateItemText(customRows[idx], customVals[idx], true);
                        }

                        content.addView(list);
                    }
                });
    }

    static CharSequence buildUaSelectionSummary(Context ctx, String[] keys, boolean[] sel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            if (sel[i]) {
                if (sb.length() > 0) {
                    sb.append("、");
                }
                sb.append(LocalizedStringProvider.getInstance().get(ctx, keys[i]));
            }
        }
        if (sb.length() == 0) {
            return LocalizedStringProvider.getInstance().get(ctx, "random_ua_none_selected");
        }
        return sb.toString();
    }

    private void performSandboxOperation(
            final Context ctx, final boolean enable, final boolean forceRestore) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final String packageName = currentPackageName;
        if (packageName == null || packageName.isEmpty()) {
            jiguroMessageWithContext(
                    ctx, LocalizedStringProvider.getInstance().get(ctx, "user_sandbox_no_package"));
            return;
        }

        final boolean hideDownload = getPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_DOWNLOAD, false);
        final boolean hideCache = getPrefBoolean(ctx, KEY_USER_SANDBOX_HIDE_CACHE, false);

        final Dialog progressDialog = new Dialog(act);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);

        FrameLayout dialogContainer = new FrameLayout(act);
        GradientDrawable containerBg = new GradientDrawable();
        containerBg.setColor(getBgColor(ctx));
        containerBg.setCornerRadius(dp(act, 24));
        dialogContainer.setBackground(containerBg);

        LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(act, 24), dp(act, 32), dp(act, 24), dp(act, 24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        final TextView title = new TextView(act);
        title.setText(LocalizedStringProvider.getInstance().get(ctx, "user_sandbox_backup_title"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setTextColor(getTitleColor(ctx));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        LinearLayout progressContainer = new LinearLayout(act);
        progressContainer.setOrientation(LinearLayout.VERTICAL);
        progressContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        final ProgressBar progressBar =
                new ProgressBar(act, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getOkBtnBgColor(ctx)));
        }
        LinearLayout.LayoutParams progressLp =
                new LinearLayout.LayoutParams(dp(act, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
        progressContainer.addView(progressBar, progressLp);

        final TextView statusText = new TextView(act);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        statusText.setTextColor(getHintColor(ctx));
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(act, 8), 0, 0);
        progressContainer.addView(statusText);

        LinearLayout.LayoutParams progressContainerLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressContainerLp.topMargin = dp(act, 16);
        root.addView(progressContainer, progressContainerLp);

        dialogContainer.addView(root);
        progressDialog.setContentView(dialogContainer);

        Window win = progressDialog.getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = (int) (metrics.widthPixels * 0.85);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(win.getAttributes());
            layoutParams.width = width;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            win.setAttributes(layoutParams);
        }

        final boolean[] operationSuccess = {false};
        final boolean[] operationFailed = {false};
        final String[] errorMessage = {""};

        final Runnable updateProgress =
                new Runnable() {
                    @Override
                    public void run() {
                        if (enable) {
                            title.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "user_sandbox_backup_title"));
                            statusText.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "user_sandbox_backup_status"));
                        } else {
                            title.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "user_sandbox_restore_title"));
                            statusText.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "user_sandbox_restore_status"));
                        }
                    }
                };

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        updateProgress.run();
                        progressDialog.show();
                    }
                });

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String dataDir = "/data/user/0/" + packageName;
                                    String sandboxDir = dataDir + "/files/BetterVia/Sandbox";
                                    String backupZipPath = sandboxDir + "/sandbox_recovery.zip";

                                    File sandboxDirFile = new File(sandboxDir);
                                    if (!sandboxDirFile.exists()) {
                                        sandboxDirFile.mkdirs();
                                    }

                                    File backupZipFile = new File(backupZipPath);

                                    if (enable) {
                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(10);
                                                        statusText.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_backup_status"));
                                                    }
                                                });

                                        ArrayList<File> filesToBackup = new ArrayList<File>();
                                        ArrayList<String> folderNames = new ArrayList<String>();
                                        if (hideDownload) {
                                            File downloadDir =
                                                    new File(dataDir + "/databases/downloader");
                                            if (downloadDir.exists()) {
                                                filesToBackup.add(downloadDir);
                                                folderNames.add("downloader");
                                            }
                                        }
                                        if (hideCache) {
                                            File cacheDir = new File(dataDir + "/app_webview");
                                            if (cacheDir.exists()) {
                                                filesToBackup.add(cacheDir);
                                                folderNames.add("app_webview");
                                            }
                                        }

                                        if (filesToBackup.isEmpty()) {
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                            return;
                                        }

                                        if (backupZipFile.exists()) {
                                            backupZipFile.delete();
                                        }

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(30);
                                                    }
                                                });

                                        ZipOutputStream zos =
                                                new ZipOutputStream(
                                                        new FileOutputStream(backupZipFile));
                                        byte[] buffer = new byte[8192];

                                        int totalFiles = filesToBackup.size();
                                        int currentFile = 0;

                                        for (int i = 0; i < filesToBackup.size(); i++) {
                                            File sourceFile = filesToBackup.get(i);
                                            if (sourceFile.exists()) {
                                                String baseName = folderNames.get(i);
                                                addFilesToZip(zos, sourceFile, baseName, buffer);
                                            }
                                            currentFile++;
                                            final int progress =
                                                    30 + (currentFile * 30 / totalFiles);
                                            final int finalProgress = progress;
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBar.setProgress(finalProgress);
                                                        }
                                                    });
                                        }

                                        zos.flush();
                                        zos.close();

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(70);
                                                        statusText.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_delete_status"));
                                                    }
                                                });

                                        for (File sourceFile : filesToBackup) {
                                            if (sourceFile.exists()) {
                                                deleteRecursive(sourceFile);
                                            }
                                        }

                                        operationSuccess[0] = true;

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(100);
                                                        statusText.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_complete"));
                                                    }
                                                });

                                        Thread.sleep(500);

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        jiguroMessageWithContext(
                                                                ctx,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_complete"));
                                                        exitViaAndApply(ctx);
                                                    }
                                                });

                                    } else {
                                        if (!backupZipFile.exists()) {
                                            final String errMsg =
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "user_sandbox_error_no_backup");
                                            operationFailed[0] = true;
                                            errorMessage[0] = errMsg;

                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            jiguroMessageWithContext(ctx, errMsg);
                                                        }
                                                    });

                                            putPrefBoolean(ctx, KEY_USER_SANDBOX_ENABLE, false);
                                            return;
                                        }

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(20);
                                                        statusText.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_restore_status"));
                                                    }
                                                });

                                        File extractDir = new File(dataDir);
                                        ZipFile zipFile = new ZipFile(backupZipFile);
                                        Enumeration<? extends ZipEntry> entriesEnum =
                                                zipFile.entries();
                                        ArrayList<ZipEntry> entryList = new ArrayList<ZipEntry>();
                                        while (entriesEnum.hasMoreElements()) {
                                            entryList.add(entriesEnum.nextElement());
                                        }

                                        if (entryList.isEmpty()) {
                                            zipFile.close();
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "user_sandbox_empty_backup"));
                                                        }
                                                    });
                                            return;
                                        }

                                        boolean hasDownloader = false;
                                        boolean hasAppWebview = false;
                                        for (ZipEntry entry : entryList) {
                                            String name = entry.getName();
                                            if (name.equals("downloader")
                                                    || name.startsWith("downloader/")) {
                                                hasDownloader = true;
                                            }
                                            if (name.equals("app_webview")
                                                    || name.startsWith("app_webview/")) {
                                                hasAppWebview = true;
                                            }
                                        }

                                        if (!hasDownloader && !hasAppWebview) {
                                            zipFile.close();
                                            act.runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "user_sandbox_invalid_format"));
                                                        }
                                                    });
                                            return;
                                        }

                                        if (hasDownloader) {
                                            File oldDownloader =
                                                    new File(dataDir + "/databases/downloader");
                                            if (oldDownloader.exists()) {
                                                oldDownloader.delete();
                                            }
                                        }
                                        if (hasAppWebview) {
                                            File oldCache = new File(dataDir + "/app_webview");
                                            if (oldCache.exists()) {
                                                deleteRecursive(oldCache);
                                            }
                                        }

                                        int totalEntries = entryList.size();
                                        int processedEntries = 0;

                                        for (ZipEntry entry : entryList) {
                                            String entryName = entry.getName();

                                            String restorePath = null;
                                            boolean isDownloaderFile =
                                                    entryName.equals("downloader");
                                            boolean isDownloaderDir =
                                                    entryName.startsWith("downloader/");
                                            boolean isAppWebviewFile =
                                                    entryName.equals("app_webview");
                                            boolean isAppWebviewDir =
                                                    entryName.startsWith("app_webview/");

                                            if (isDownloaderFile || isDownloaderDir) {
                                                restorePath = "databases/" + entryName;
                                            } else if (isAppWebviewFile || isAppWebviewDir) {
                                                restorePath = entryName;
                                            } else {
                                                restorePath = entryName;
                                            }

                                            File destFile = new File(extractDir, restorePath);

                                            if (entry.isDirectory()) {
                                                destFile.mkdirs();
                                            } else {
                                                File parent = destFile.getParentFile();
                                                if (parent != null && !parent.exists()) {
                                                    parent.mkdirs();
                                                }

                                                InputStream is = zipFile.getInputStream(entry);
                                                FileOutputStream fos =
                                                        new FileOutputStream(destFile);
                                                byte[] buffer = new byte[8192];
                                                int len;
                                                while ((len = is.read(buffer)) > 0) {
                                                    fos.write(buffer, 0, len);
                                                }
                                                fos.close();
                                                is.close();
                                            }

                                            processedEntries++;
                                            final int progress =
                                                    20 + (processedEntries * 70 / totalEntries);
                                            if (progress <= 90) {
                                                final int finalProgress = progress;
                                                act.runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressBar.setProgress(
                                                                        finalProgress);
                                                            }
                                                        });
                                            }
                                        }

                                        zipFile.close();

                                        operationSuccess[0] = true;

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressBar.setProgress(100);
                                                        statusText.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_complete"));
                                                    }
                                                });

                                        Thread.sleep(500);

                                        act.runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        jiguroMessageWithContext(
                                                                ctx,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_complete"));
                                                        exitViaAndApply(ctx);
                                                    }
                                                });
                                    }

                                } catch (final Exception e) {
                                    bvLog("[BetterVia] 用户沙箱操作异常: " + Log.getStackTraceString(e));
                                    operationFailed[0] = true;
                                    errorMessage[0] = e.getMessage();

                                    act.runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    if (enable) {
                                                        jiguroMessageWithContext(
                                                                ctx,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_error_backup_failed"));
                                                    } else {
                                                        jiguroMessageWithContext(
                                                                ctx,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "user_sandbox_error_restore_failed"));
                                                    }
                                                }
                                            });
                                }
                            }
                        })
                .start();
    }

    private void addFilesToZip(ZipOutputStream zos, File sourceFile, String baseName, byte[] buffer)
            throws IOException {
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        ZipEntry dirEntry = new ZipEntry(baseName + "/" + file.getName() + "/");
                        zos.putNextEntry(dirEntry);
                        zos.closeEntry();
                        addFilesToZip(zos, file, baseName + "/" + file.getName(), buffer);
                    } else {
                        ZipEntry entry = new ZipEntry(baseName + "/" + file.getName());
                        zos.putNextEntry(entry);
                        FileInputStream fis = new FileInputStream(file);
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        fis.close();
                        zos.closeEntry();
                    }
                }
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(baseName);
            zos.putNextEntry(zipEntry);
            FileInputStream fis = new FileInputStream(sourceFile);
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            fis.close();
            zos.closeEntry();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    private void exitViaAndApply(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        jiguroMessageWithContext(
                ctx, LocalizedStringProvider.getInstance().get(ctx, "user_sandbox_exiting_via"));

        new Handler()
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    act.finishAffinity();
                                    System.exit(0);
                                } catch (Exception e) {
                                    try {
                                        act.finish();
                                    } catch (Exception e2) {
                                        bvLog("[BetterVia] 退出Via失败: " + e2);
                                    }
                                }
                            }
                        },
                        500);
    }

    void showMonetBasePopup(
            final Context ctx, View anchor, String[] items, final SourceSelectedCallback callback) {
        final int bgColor = getBgColor(ctx);
        final int textColor = getTextColor(ctx);
        final int dividerColor = getDividerColor(ctx);

        final ListView list = new ListView(ctx);
        list.setDivider(null);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, items) {
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
        final PopupWindow pop =
                new PopupWindow(list, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
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
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        SettingsUI.applyViaScrollStyle(scrollRoot);

                        final LinearLayout root = new LinearLayout(act);
                        root.setOrientation(LinearLayout.VERTICAL);
                        root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));

                        TextView title = new TextView(act);
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "startup_execution_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                        title.setTextColor(titleColor);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 6);
                        root.addView(title, titleLp);

                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "startup_execution_dialog_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        subtitle.setTextColor(hintColor);
                        subtitle.setGravity(Gravity.CENTER);
                        subtitle.setPadding(0, 0, 0, dp(act, 24));
                        root.addView(subtitle);

                        LinearLayout enableContainer = new LinearLayout(act);
                        enableContainer.setOrientation(LinearLayout.VERTICAL);
                        enableContainer.setPadding(
                                dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
                        GradientDrawable enableBg = new GradientDrawable();
                        enableBg.setColor(itemBgColor);
                        enableBg.setCornerRadius(dp(act, 12));
                        enableBg.setStroke(dp(act, 1), dividerColor);
                        enableContainer.setBackground(enableBg);

                        final Switch executionSwitch = new Switch(act);
                        executionSwitch.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "startup_execution_enable"));
                        executionSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        executionSwitch.setTextColor(textColor);
                        executionSwitch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                        executionSwitch.setChecked(
                                StartupExecutionHelper.getStartupExecutionEnable(ctx));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            int[][] states =
                                    new int[][] {
                                        new int[] {android.R.attr.state_checked},
                                        new int[] {-android.R.attr.state_checked}
                                    };
                            int[] colors = new int[] {switchOnColor, switchOffColor};
                            ColorStateList colorStateList = new ColorStateList(states, colors);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                executionSwitch.setThumbTintList(colorStateList);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                executionSwitch.setTrackTintList(colorStateList);
                            }
                        }
                        LinearLayout.LayoutParams switchLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        switchLp.bottomMargin = dp(act, 8);
                        enableContainer.addView(executionSwitch, switchLp);

                        TextView switchHint = new TextView(act);
                        switchHint.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "startup_execution_enable_hint"));
                        switchHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        switchHint.setTextColor(hintColor);
                        switchHint.setGravity(Gravity.START);
                        enableContainer.addView(switchHint);

                        executionSwitch.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        StartupExecutionHelper.setStartupExecutionEnable(
                                                ctx, isChecked);
                                    }
                                });

                        LinearLayout.LayoutParams enableLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        enableLp.bottomMargin = dp(act, 16);
                        root.addView(enableContainer, enableLp);

                        LinearLayout imageContainer =
                                createStartupImageCard(
                                        act,
                                        ctx,
                                        bgColor,
                                        textColor,
                                        hintColor,
                                        itemBgColor,
                                        editBgColor,
                                        dividerColor,
                                        switchOnColor,
                                        switchOffColor,
                                        okBtnBgColor,
                                        okBtnTextColor);
                        LinearLayout.LayoutParams imageLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageLp.bottomMargin = dp(act, 16);
                        root.addView(imageContainer, imageLp);

                        LinearLayout musicContainer =
                                createStartupMusicCard(
                                        act,
                                        ctx,
                                        bgColor,
                                        textColor,
                                        hintColor,
                                        itemBgColor,
                                        editBgColor,
                                        dividerColor,
                                        switchOnColor,
                                        switchOffColor,
                                        okBtnBgColor,
                                        okBtnTextColor);
                        LinearLayout.LayoutParams musicLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        musicLp.bottomMargin = dp(act, 16);
                        root.addView(musicContainer, musicLp);

                        LinearLayout hintContainer =
                                createStartupHintCard(
                                        act,
                                        ctx,
                                        bgColor,
                                        textColor,
                                        hintColor,
                                        itemBgColor,
                                        editBgColor,
                                        dividerColor,
                                        switchOnColor,
                                        switchOffColor);
                        LinearLayout.LayoutParams hintLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        hintLp.bottomMargin = dp(act, 16);
                        root.addView(hintContainer, hintLp);

                        LinearLayout btnRow = new LinearLayout(act);
                        btnRow.setOrientation(LinearLayout.HORIZONTAL);
                        btnRow.setGravity(Gravity.CENTER);
                        btnRow.setPadding(0, dp(act, 24), 0, 0);

                        Button cancel = new Button(act);
                        applyClickAnim(cancel);
                        cancel.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
                        cancel.setTextColor(btnTextColor);
                        cancel.setBackground(getRoundBg(act, btnBgColor, 12));
                        Button ok = new Button(act);
                        applyClickAnim(ok);
                        ok.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
                        ok.setTextColor(okBtnTextColor);
                        ok.setBackground(getRoundBg(act, okBtnBgColor, 12));

                        LinearLayout.LayoutParams btnLp =
                                new LinearLayout.LayoutParams(
                                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
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
                            DisplayMetrics metrics = new DisplayMetrics();
                            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int width = (int) (metrics.widthPixels * 0.9);
                            WindowManager.LayoutParams layoutParams =
                                    new WindowManager.LayoutParams();
                            layoutParams.copyFrom(win.getAttributes());
                            layoutParams.width = width;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity = Gravity.CENTER;
                            win.setAttributes(layoutParams);
                        }

                        cancel.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                        ok.setOnClickListener(
                                new View.OnClickListener() {
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

    private LinearLayout createStartupImageCard(
            final Activity act,
            final Context ctx,
            int bgColor,
            int textColor,
            int hintColor,
            int itemBgColor,
            int editBgColor,
            int dividerColor,
            int switchOnColor,
            int switchOffColor,
            final int okBtnBgColor,
            final int okBtnTextColor) {
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
        LinearLayout.LayoutParams cardTitleLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardTitleLp.bottomMargin = dp(act, 8);
        card.addView(cardTitle, cardTitleLp);

        final Switch imageSwitch = new Switch(act);
        imageSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_image_hint"));
        imageSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        imageSwitch.setTextColor(textColor);
        imageSwitch.setChecked(StartupExecutionHelper.getStartupImageEnable(ctx));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states =
                    new int[][] {
                        new int[] {android.R.attr.state_checked},
                        new int[] {-android.R.attr.state_checked}
                    };
            int[] colors = new int[] {switchOnColor, switchOffColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageSwitch.setThumbTintList(colorStateList);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageSwitch.setTrackTintList(colorStateList);
            }
        }
        LinearLayout.LayoutParams switchLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchLp.bottomMargin = dp(act, 12);
        card.addView(imageSwitch, switchLp);

        final LinearLayout previewContainer = new LinearLayout(act);
        previewContainer.setOrientation(LinearLayout.HORIZONTAL);
        previewContainer.setGravity(Gravity.CENTER);
        previewContainer.setPadding(0, 0, 0, dp(act, 8));
        previewContainer.setBackground(
                getRoundBgWithStroke(act, editBgColor, getDividerColor(ctx), 8, 1));
        previewContainer.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);

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
        durationTitle.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_image_duration"));
        durationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        durationTitle.setTextColor(textColor);
        durationTitle.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams durationTitleLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        durationTitleLp.topMargin = dp(act, 8);
        durationTitleLp.bottomMargin = dp(act, 4);
        card.addView(durationTitle, durationTitleLp);

        final SeekBar durationSeek = new SeekBar(act);
        durationSeek.setMax(10);
        durationSeek.setProgress(StartupExecutionHelper.getStartupImageDuration(ctx));
        durationSeek.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            durationSeek.setProgressTintList(ColorStateList.valueOf(okBtnBgColor));
            durationSeek.setThumbTintList(ColorStateList.valueOf(okBtnBgColor));
        }
        card.addView(durationSeek);

        final TextView durationHint = new TextView(act);
        durationHint.setText(
                String.format(
                        LocalizedStringProvider.getInstance()
                                .get(ctx, "startup_image_duration_hint"),
                        StartupExecutionHelper.getStartupImageDuration(ctx)));
        durationHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        durationHint.setTextColor(hintColor);
        durationHint.setGravity(Gravity.CENTER);
        durationHint.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams durationHintLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        durationHintLp.topMargin = dp(act, 4);
        durationHintLp.bottomMargin = dp(act, 12);
        card.addView(durationHint, durationHintLp);

        final TextView borderColorTitle = new TextView(act);
        borderColorTitle.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_image_border_color"));
        borderColorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        borderColorTitle.setTextColor(textColor);
        borderColorTitle.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams borderColorTitleLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        borderColorTitleLp.topMargin = dp(act, 8);
        borderColorTitleLp.bottomMargin = dp(act, 4);
        card.addView(borderColorTitle, borderColorTitleLp);

        final EditText borderColorEdit = new EditText(act);
        borderColorEdit.setHint("#RRGGBB");
        borderColorEdit.setText(
                colorToRgbString(StartupExecutionHelper.getStartupImageBorderColor(ctx)));
        borderColorEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        borderColorEdit.setTextColor(textColor);
        borderColorEdit.setHintTextColor(hintColor);
        borderColorEdit.setBackground(
                getRoundBgWithStroke(act, editBgColor, getDividerColor(ctx), 4, 1));
        borderColorEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
        borderColorEdit.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams borderColorEditLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        borderColorEditLp.bottomMargin = dp(act, 4);
        card.addView(borderColorEdit, borderColorEditLp);

        final TextView borderColorHint = new TextView(act);
        borderColorHint.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_image_border_color_hint"));
        borderColorHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        borderColorHint.setTextColor(hintColor);
        borderColorHint.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams borderColorHintLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        borderColorHintLp.topMargin = dp(act, 4);
        borderColorHintLp.bottomMargin = dp(act, 8);
        card.addView(borderColorHint, borderColorHintLp);

        final LinearLayout forceStretchContainer = new LinearLayout(act);
        forceStretchContainer.setOrientation(LinearLayout.HORIZONTAL);
        forceStretchContainer.setGravity(Gravity.CENTER_VERTICAL);
        forceStretchContainer.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams forceStretchContainerLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        forceStretchContainerLp.topMargin = dp(act, 4);
        forceStretchContainerLp.bottomMargin = dp(act, 12);
        card.addView(forceStretchContainer, forceStretchContainerLp);

        final Switch forceStretchSwitch = new Switch(act);
        forceStretchSwitch.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_image_force_stretch"));
        forceStretchSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        forceStretchSwitch.setTextColor(textColor);
        forceStretchSwitch.setChecked(StartupExecutionHelper.getStartupImageForceStretch(ctx));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states =
                    new int[][] {
                        new int[] {android.R.attr.state_checked},
                        new int[] {-android.R.attr.state_checked}
                    };
            int[] colors = new int[] {switchOnColor, switchOffColor};
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
        forceStretchHint.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_image_force_stretch_hint"));
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
        pickBtn.setVisibility(
                StartupExecutionHelper.getStartupImageEnable(ctx) ? View.VISIBLE : View.GONE);
        card.addView(pickBtn);

        imageSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
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

        durationSeek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            if (progress < 1) progress = 1;
                            StartupExecutionHelper.setStartupImageDuration(ctx, progress);
                            durationHint.setText(
                                    String.format(
                                            LocalizedStringProvider.getInstance()
                                                    .get(ctx, "startup_image_duration_hint"),
                                            progress));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        pickBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.setType("image/*");
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        act.startActivityForResult(
                                Intent.createChooser(
                                        i,
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "startup_image_pick_title")),
                                0x3001);
                    }
                });

        borderColorEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        String rgbStr = s.toString();
                        int rgbColor = parseBorderColor(rgbStr);
                        StartupExecutionHelper.setStartupImageBorderColor(ctx, rgbColor);
                    }
                });

        forceStretchSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        StartupExecutionHelper.setStartupImageForceStretch(ctx, isChecked);
                    }
                });

        return card;
    }

    private LinearLayout createStartupMusicCard(
            final Activity act,
            final Context ctx,
            int bgColor,
            int textColor,
            int hintColor,
            int itemBgColor,
            int editBgColor,
            int dividerColor,
            int switchOnColor,
            int switchOffColor,
            final int okBtnBgColor,
            final int okBtnTextColor) {
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
        LinearLayout.LayoutParams cardTitleLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardTitleLp.bottomMargin = dp(act, 8);
        card.addView(cardTitle, cardTitleLp);

        final Switch musicSwitch = new Switch(act);
        musicSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_hint"));
        musicSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        musicSwitch.setTextColor(textColor);
        musicSwitch.setChecked(StartupExecutionHelper.getStartupMusicEnable(ctx));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states =
                    new int[][] {
                        new int[] {android.R.attr.state_checked},
                        new int[] {-android.R.attr.state_checked}
                    };
            int[] colors = new int[] {switchOnColor, switchOffColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                musicSwitch.setThumbTintList(colorStateList);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                musicSwitch.setTrackTintList(colorStateList);
            }
        }
        LinearLayout.LayoutParams switchLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchLp.bottomMargin = dp(act, 12);
        card.addView(musicSwitch, switchLp);

        final LinearLayout previewContainer = new LinearLayout(act);
        previewContainer.setOrientation(LinearLayout.HORIZONTAL);
        previewContainer.setGravity(Gravity.CENTER);
        previewContainer.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
        previewContainer.setBackground(
                getRoundBgWithStroke(act, editBgColor, getDividerColor(ctx), 8, 1));
        previewContainer.setVisibility(
                StartupExecutionHelper.getStartupMusicEnable(ctx) ? View.VISIBLE : View.GONE);

        TextView previewText = new TextView(act);
        previewText.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_music_preview"));
        previewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        previewText.setTextColor(hintColor);
        previewContainer.addView(previewText);

        LinearLayout.LayoutParams previewLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        previewLp.bottomMargin = dp(act, 12);
        card.addView(previewContainer, previewLp);

        final Button pickBtn = new Button(act);
        applyClickAnim(pickBtn);
        pickBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_music_pick_btn"));
        pickBtn.setTextColor(okBtnTextColor);
        pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        pickBtn.setBackground(getRoundBg(act, okBtnBgColor, 8));
        pickBtn.setVisibility(
                StartupExecutionHelper.getStartupMusicEnable(ctx) ? View.VISIBLE : View.GONE);
        card.addView(pickBtn);

        musicSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        StartupExecutionHelper.setStartupMusicEnable(ctx, isChecked);
                        int visibility = isChecked ? View.VISIBLE : View.GONE;
                        previewContainer.setVisibility(visibility);
                        pickBtn.setVisibility(visibility);
                    }
                });

        previewContainer.setOnClickListener(
                new View.OnClickListener() {
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
                                previewPlayer.setOnCompletionListener(
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                previewPlayer.release();
                                            }
                                        });
                            } catch (Exception e) {
                                jiguroMessageWithContext(
                                        ctx,
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "play_failed"));
                            }
                        }
                    }
                });

        pickBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.setType("audio/*");
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        act.startActivityForResult(
                                Intent.createChooser(
                                        i,
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "startup_music_pick_title")),
                                0x3002);
                    }
                });

        return card;
    }

    private LinearLayout createStartupHintCard(
            final Activity act,
            final Context ctx,
            int bgColor,
            int textColor,
            int hintColor,
            int itemBgColor,
            int editBgColor,
            int dividerColor,
            int switchOnColor,
            int switchOffColor) {
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
        LinearLayout.LayoutParams cardTitleLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardTitleLp.bottomMargin = dp(act, 8);
        card.addView(cardTitle, cardTitleLp);

        final Switch hintSwitch = new Switch(act);
        hintSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hint"));
        hintSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        hintSwitch.setTextColor(textColor);
        hintSwitch.setChecked(StartupExecutionHelper.getStartupHintEnable(ctx));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states =
                    new int[][] {
                        new int[] {android.R.attr.state_checked},
                        new int[] {-android.R.attr.state_checked}
                    };
            int[] colors = new int[] {switchOnColor, switchOffColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hintSwitch.setThumbTintList(colorStateList);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hintSwitch.setTrackTintList(colorStateList);
            }
        }
        LinearLayout.LayoutParams switchLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchLp.bottomMargin = dp(act, 12);
        card.addView(hintSwitch, switchLp);

        final TextView typeLabel = new TextView(act);
        typeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type"));
        typeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        typeLabel.setTextColor(textColor);
        typeLabel.setVisibility(
                StartupExecutionHelper.getStartupHintEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams typeLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        typeLabelLp.bottomMargin = dp(act, 4);
        card.addView(typeLabel, typeLabelLp);

        final TextView typeSelector = new TextView(act);
        final int savedType = StartupExecutionHelper.getStartupHintType(ctx);
        final String[] typeItems = {
            LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type_custom"),
            LocalizedStringProvider.getInstance().get(ctx, "startup_hint_type_hitokoto")
        };
        typeSelector.setText(typeItems[savedType]);
        typeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        typeSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
        GradientDrawable selectorBg = new GradientDrawable();
        selectorBg.setColor(editBgColor);
        selectorBg.setCornerRadius(dp(act, 8));
        selectorBg.setStroke(dp(act, 1), dividerColor);
        typeSelector.setBackground(selectorBg);
        typeSelector.setTextColor(textColor);
        typeSelector.setVisibility(
                StartupExecutionHelper.getStartupHintEnable(ctx) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams typeLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        typeLp.bottomMargin = dp(act, 12);
        card.addView(typeSelector, typeLp);

        final LinearLayout customTextContainer = new LinearLayout(act);
        customTextContainer.setOrientation(LinearLayout.VERTICAL);
        customTextContainer.setVisibility(savedType == 0 ? View.VISIBLE : View.GONE);

        TextView customTextLabel = new TextView(act);
        customTextLabel.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_custom_text"));
        customTextLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        customTextLabel.setTextColor(textColor);
        LinearLayout.LayoutParams customTextLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customTextLabelLp.bottomMargin = dp(act, 4);
        customTextContainer.addView(customTextLabel, customTextLabelLp);

        EditText customTextEdit = new EditText(act);
        customTextEdit.setHint(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_custom_text_hint"));
        customTextEdit.setText(StartupExecutionHelper.getStartupHintCustomText(ctx));
        customTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        customTextEdit.setTextColor(textColor);
        customTextEdit.setHintTextColor(hintColor);
        customTextEdit.setBackground(
                getRoundBgWithStroke(act, editBgColor, getDividerColor(ctx), 4, 1));
        customTextEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
        LinearLayout.LayoutParams customEditLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customEditLp.bottomMargin = dp(act, 12);
        customTextContainer.addView(customTextEdit, customEditLp);

        card.addView(customTextContainer);

        final LinearLayout hitokotoContainer = new LinearLayout(act);
        hitokotoContainer.setOrientation(LinearLayout.VERTICAL);
        hitokotoContainer.setVisibility(savedType == 1 ? View.VISIBLE : View.GONE);

        TextView apiLabel = new TextView(act);
        apiLabel.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_api"));
        apiLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        apiLabel.setTextColor(textColor);
        LinearLayout.LayoutParams apiLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        apiLabelLp.bottomMargin = dp(act, 4);
        hitokotoContainer.addView(apiLabel, apiLabelLp);

        EditText apiEdit = new EditText(act);
        apiEdit.setHint(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_api_hint"));
        apiEdit.setText(StartupExecutionHelper.getStartupHintHitokotoApi(ctx));
        apiEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        apiEdit.setTextColor(textColor);
        apiEdit.setHintTextColor(hintColor);
        apiEdit.setBackground(getRoundBgWithStroke(act, editBgColor, getDividerColor(ctx), 4, 1));
        apiEdit.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
        LinearLayout.LayoutParams apiEditLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        apiEditLp.bottomMargin = dp(act, 12);
        hitokotoContainer.addView(apiEdit, apiEditLp);

        TextView hitokotoTypeLabel = new TextView(act);
        hitokotoTypeLabel.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type"));
        hitokotoTypeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        hitokotoTypeLabel.setTextColor(textColor);
        LinearLayout.LayoutParams hitokotoTypeLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hitokotoTypeLabelLp.bottomMargin = dp(act, 4);
        hitokotoContainer.addView(hitokotoTypeLabel, hitokotoTypeLabelLp);

        TextView hitokotoTypeHint = new TextView(act);
        hitokotoTypeHint.setText(
                LocalizedStringProvider.getInstance()
                        .get(ctx, "startup_hint_hitokoto_select_hint"));
        hitokotoTypeHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        hitokotoTypeHint.setTextColor(hintColor);
        LinearLayout.LayoutParams hitokotoTypeHintLp =
                new LinearLayout.LayoutParams(
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
            LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_type_philosophy")
        };
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
                int[][] states =
                        new int[][] {
                            new int[] {android.R.attr.state_checked},
                            new int[] {-android.R.attr.state_checked}
                        };
                int[] colors = new int[] {switchOnColor, switchOffColor};
                ColorStateList colorStateList = new ColorStateList(states, colors);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cb.setButtonTintList(colorStateList);
                }
            }
            cb.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
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
                            StartupExecutionHelper.setStartupHintHitokotoTypes(
                                    ctx, selectedTypes.toString());
                        }
                    });
            checkboxContainer.addView(cb);
        }

        TextView lengthTitle = new TextView(act);
        lengthTitle.setText(
                LocalizedStringProvider.getInstance()
                        .get(ctx, "startup_hint_hitokoto_length_title"));
        lengthTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        lengthTitle.setTextColor(textColor);
        lengthTitle.setTypeface(null, Typeface.BOLD);
        lengthTitle.setPadding(0, dp(act, 12), 0, 0);
        hitokotoContainer.addView(lengthTitle);

        TextView minLengthLabel = new TextView(act);
        minLengthLabel.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_min_length"));
        minLengthLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        minLengthLabel.setTextColor(textColor);
        LinearLayout.LayoutParams minLengthLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

        LinearLayout.LayoutParams minLengthSeekLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        minLengthSeekLp.bottomMargin = dp(act, 12);
        minLengthSeek.setLayoutParams(minLengthSeekLp);

        TextView maxLengthLabel = new TextView(act);
        maxLengthLabel.setText(
                LocalizedStringProvider.getInstance().get(ctx, "startup_hint_hitokoto_max_length"));
        maxLengthLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        maxLengthLabel.setTextColor(textColor);
        LinearLayout.LayoutParams maxLengthLabelLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

        minLengthSeek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            minLengthValue.setText(String.valueOf(progress));
                            StartupExecutionHelper.setStartupHintMinLength(ctx, progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        maxLengthSeek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            maxLengthValue.setText(String.valueOf(progress));
                            StartupExecutionHelper.setStartupHintMaxLength(ctx, progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        card.addView(hitokotoContainer);

        hintSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
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

        typeSelector.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMonetBasePopup(
                                ctx,
                                typeSelector,
                                typeItems,
                                new SourceSelectedCallback() {
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

        customTextEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        StartupExecutionHelper.setStartupHintCustomText(ctx, s.toString());
                    }
                });

        apiEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        StartupExecutionHelper.setStartupHintHitokotoApi(ctx, s.toString());
                    }
                });

        return card;
    }

    private static volatile SharedPreferences sBetterViaPrefs;

    static SharedPreferences prefs(Context ctx) {
        SharedPreferences sp = sBetterViaPrefs;
        if (sp != null) return sp;
        synchronized (Hook.class) {
            if (sBetterViaPrefs == null) {
                Context app = ctx.getApplicationContext();
                if (app == null) app = ctx;
                sBetterViaPrefs = app.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
            }
            return sBetterViaPrefs;
        }
    }

    public static boolean getPrefBoolean(Context ctx, String key, boolean def) {
        try {
            return prefs(ctx).getBoolean(key, def);
        } catch (Throwable e) {
            return def;
        }
    }

    static void putPrefBoolean(Context ctx, String key, boolean value) {
        try {
            prefs(ctx).edit().putBoolean(key, value).apply();
        } catch (Throwable e) {
            bvLog("[BetterVia] 写入布尔值时失败: " + e);
        }
    }

    static void saveLanguageSetting(Context ctx, String lang) {
        try {
            prefs(ctx).edit().putString("preferred_language", lang).apply();
        } catch (Throwable e) {
            bvLog("[BetterVia] 保存语言失败: " + e);
        }
    }

    static String getSavedLanguage(Context ctx) {
        try {
            return prefs(ctx).getString("preferred_language", "auto");
        } catch (Throwable e) {
            return "auto";
        }
    }

    private String getModuleTheme(Context ctx) {
        try {
            return prefs(ctx).getString(KEY_MODULE_THEME, DEFAULT_MODULE_THEME);
        } catch (Throwable e) {
            return DEFAULT_MODULE_THEME;
        }
    }

    String getActualTheme(Context ctx) {
        return ThemeColors.getActualTheme(ctx);
    }

    int getBgColor(Context ctx) {
        return ThemeColors.getBgColor(ctx);
    }

    int getTitleColor(Context ctx) {
        return ThemeColors.getTitleColor(ctx);
    }

    int getTextColor(Context ctx) {
        return ThemeColors.getTextColor(ctx);
    }

    int getHintColor(Context ctx) {
        return ThemeColors.getHintColor(ctx);
    }

    int getDividerColor(Context ctx) {
        return ThemeColors.getDividerColor(ctx);
    }

    int getBtnBgColor(Context ctx) {
        return ThemeColors.getBtnBgColor(ctx);
    }

    int getBtnTextColor(Context ctx) {
        return ThemeColors.getBtnTextColor(ctx);
    }

    int getOkBtnBgColor(Context ctx) {
        return ThemeColors.getOkBtnBgColor(ctx);
    }

    int getOkBtnTextColor(Context ctx) {
        return ThemeColors.getOkBtnTextColor(ctx);
    }

    int getSwitchOnColor(Context ctx) {
        return ThemeColors.getSwitchOnColor(ctx);
    }

    int getSwitchOffColor(Context ctx) {
        return ThemeColors.getSwitchOffColor(ctx);
    }

    int getItemBgColor(Context ctx) {
        return ThemeColors.getItemBgColor(ctx);
    }

    int getEditBgColor(Context ctx) {
        return ThemeColors.getEditBgColor(ctx);
    }

    static Activity getActivityFrom(Context ctx) {
        try {
            if (ctx instanceof Activity) {
                return (Activity) ctx;
            }
            if (currentActivity != null) {
                if (!currentActivity.isFinishing() && !currentActivity.isDestroyed()) {
                    return currentActivity;
                }
                bvLog("[BetterVia] getActivityFrom: currentActivity 已销毁，尝试其他方式");
            }
            if (Context != null && Context instanceof Activity) {
                Activity ctxActivity = (Activity) Context;
                if (!ctxActivity.isFinishing() && !ctxActivity.isDestroyed()) {
                    return ctxActivity;
                }
                bvLog("[BetterVia] getActivityFrom: 全局Context已销毁，尝试反射获取Activity");
            }
            try {
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Method currentActivityThreadMethod =
                        activityThreadClass.getDeclaredMethod("currentActivityThread");
                currentActivityThreadMethod.setAccessible(true);
                Object activityThread = currentActivityThreadMethod.invoke(null);
                Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
                activitiesField.setAccessible(true);
                Object activities = activitiesField.get(activityThread);
                if (activities instanceof android.util.ArrayMap) {
                    android.util.ArrayMap<?, ?> map = (android.util.ArrayMap<?, ?>) activities;
                    for (int i = map.size() - 1; i >= 0; i--) {
                        Object record = map.valueAt(i);
                        Field pausedField = record.getClass().getDeclaredField("paused");
                        pausedField.setAccessible(true);
                        boolean paused = pausedField.getBoolean(record);
                        if (!paused) {
                            Field activityField = record.getClass().getDeclaredField("activity");
                            activityField.setAccessible(true);
                            Activity resumedActivity = (Activity) activityField.get(record);
                            if (resumedActivity != null
                                    && !resumedActivity.isFinishing()
                                    && !resumedActivity.isDestroyed()) {
                                bvLog(
                                        "[BetterVia] getActivityFrom: 通过ActivityThread找到resumed"
                                            + " Activity");
                                return resumedActivity;
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                bvLog("[BetterVia] getActivityFrom: ActivityThread反射失败: " + t.getMessage());
            }
        } catch (Throwable ignored) {
        }
        bvLog("[BetterVia] getActivityFrom: 所有方式均未找到有效的Activity");
        return null;
    }

    static void putPrefString(Context ctx, String key, String value) {
        try {
            prefs(ctx).edit().putString(key, value).apply();
        } catch (Throwable e) {
            bvLog("[BetterVia] 写入字符串值时失败: " + e);
        }
    }

    static String getPrefString(Context ctx, String key, String def) {
        try {
            return prefs(ctx).getString(key, def);
        } catch (Throwable e) {
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
            return prefs(ctx).getString("preferred_language", "auto");
        } catch (Throwable e) {
            return "auto";
        }
    }

    private void copyToClipboard(Context ctx, String text) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Via Command", text);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            bvLog("[BetterVia] 复制到剪贴板失败: " + e);
        }
    }

    GradientDrawable getRoundBg(Context ctx, int color, int radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(ctx, radiusDp));
        return gd;
    }

    GradientDrawable getRoundBgWithStroke(
            Context ctx, int fillColor, int strokeColor, int radiusDp, int strokeDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(ctx, radiusDp));
        gd.setStroke(dp(ctx, strokeDp), strokeColor);
        return gd;
    }

    private void updateViaLocale(Context ctx, String lang) {
        try {
            Locale newLoc;
            switch (lang) {
                case "zh-CN":
                    newLoc = Locale.SIMPLIFIED_CHINESE;
                    break;
                case "zh-TW":
                    newLoc = Locale.TRADITIONAL_CHINESE;
                    break;
                case "en":
                    newLoc = Locale.ENGLISH;
                    break;
                default:
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
        if (moduleButtonRef == null) return;
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
            case 0:
                key = "toast_language_auto";
                break;
            case 1:
                key = "toast_language_zh_cn";
                break;
            case 2:
                key = "toast_language_zh_tw";
                break;
            case 3:
                key = "toast_language_en";
                break;
            default:
                return;
        }
        jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, key));
    }

    private static Toast createCustomToast(
            final Context context, final String msg, final int duration) {
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

    public static void jiguroMessageWithContext(Context ctx, String msg) {
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

    private static void showToastSafely(
            final Context context, final String msg, final boolean useCustomToast) {
        if (context == null) {
            bvLog("[BetterVia] Context为null，无法显示Toast: " + msg);
            return;
        }
        if (msg == null || msg.isEmpty()) {
            return;
        }

        final Runnable toastTask =
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Context appContext = context.getApplicationContext();
                            if (appContext == null) {
                                bvLog("[BetterVia] Application Context为null: " + msg);
                                return;
                            }

                            int duration =
                                    msg.length() > 20 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
                            boolean shown = false;

                            if (useCustomToast) {
                                try {
                                    Toast customToast =
                                            createCustomToast(appContext, msg, duration);
                                    if (customToast != null) {
                                        customToast.show();
                                        shown = true;
                                    }
                                } catch (Exception customEx) {
                                    bvLog(
                                            "[BetterVia] 自定义Toast显示失败，尝试降级: "
                                                    + customEx.getMessage());
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

    static int dp(Context ctx, int dp) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    private void applyAlertDialogTheme(Activity act, Context ctx, AlertDialog dialog) {
        if (dialog == null || act == null) return;
        Window win = dialog.getWindow();
        if (win != null) {
            int bgColor = getBgColor(ctx);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(bgColor);
            bg.setCornerRadius(dp(act, 24));
            win.setBackgroundDrawable(bg);

            DisplayMetrics dialogMetrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dialogMetrics);
            int dialogWidth = (int) (dialogMetrics.widthPixels * 0.9);
            WindowManager.LayoutParams dialogLp = new WindowManager.LayoutParams();
            dialogLp.copyFrom(win.getAttributes());
            dialogLp.width = dialogWidth;
            dialogLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogLp.gravity = Gravity.CENTER;
            win.setAttributes(dialogLp);

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

                int buttonPanelId =
                        decorView.getResources().getIdentifier("buttonPanel", "id", "android");
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

    static int getPrefInt(Context ctx, String key, int def) {
        try {
            return prefs(ctx).getInt(key, def);
        } catch (Throwable e) {
            return def;
        }
    }

    public static String getNetworkSource(Context ctx) {
        try {
            String source = prefs(ctx).getString(KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
            if (!isValidNetworkSource(source)) {
                return DEFAULT_NETWORK_SOURCE;
            }
            return source;
        } catch (Throwable e) {
            return DEFAULT_NETWORK_SOURCE;
        }
    }

    static void putPrefInt(Context ctx, String key, int value) {
        try {
            prefs(ctx).edit().putInt(key, value).apply();
        } catch (Throwable e) {
            bvLog("[BetterVia] 写入Int值失败: " + e);
        }
    }

    static long getPrefLong(Context ctx, String key, long def) {
        try {
            return prefs(ctx).getLong(key, def);
        } catch (Throwable e) {
            return def;
        }
    }

    static void putPrefLong(Context ctx, String key, long value) {
        try {
            prefs(ctx).edit().putLong(key, value).apply();
        } catch (Throwable e) {
            bvLog("[BetterVia] 写入Long值失败: " + e);
        }
    }

    void animateDialogEntrance(final ViewGroup root, final Activity act) {
        if (root == null || act == null) return;

        final int childCount = root.getChildCount();
        final int baseDelay = 60;
        int delay = 0;

        if (childCount > 0) {
            final View maybeTitle = root.getChildAt(0);
            maybeTitle.setAlpha(0f);
            maybeTitle.setTranslationY(dp(act, 6));
            maybeTitle.setScaleX(0.98f);
            maybeTitle.setScaleY(0.98f);
            maybeTitle
                    .animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(delay)
                    .setDuration(320)
                    .setInterpolator(new OvershootInterpolator(1.0f))
                    .start();
            delay += baseDelay;
        }

        for (int i = 1; i < childCount; i++) {
            final View v = root.getChildAt(i);
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(dp(act, 10));
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(delay)
                    .setDuration(220)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            delay += baseDelay;
        }
    }

    void applyClickAnim(final View v) {
        if (v == null) return;

        v.animate().cancel();

        v.setClickable(true);
        v.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                view.animate()
                                        .scaleX(0.96f)
                                        .scaleY(0.96f)
                                        .setDuration(80)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .start();
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                view.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(120)
                                        .setInterpolator(new OvershootInterpolator(1.1f))
                                        .start();
                                break;
                        }
                        return false;
                    }
                });
    }

    private void checkUpdateOnStart(final Context ctx) {
        if (isAutoCheckDoneToday(ctx)) {
            return;
        }
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    checkUpdate(ctx, true);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .start();
    }

    private boolean isAutoCheckDoneToday(final Context ctx) {
        try {
            String today =
                    new SimpleDateFormat("yyyyMMdd", Locale.US)
                            .format(new Date(TimeProvider.now()));
            String lastCheckDate = getPrefString(ctx, KEY_LAST_AUTO_UPDATE_DATE, "");
            return today.equals(lastCheckDate);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAutoUpdateDueToday(final Context ctx) {
        try {
            String today =
                    new SimpleDateFormat("yyyyMMdd", Locale.US)
                            .format(new Date(TimeProvider.now()));
            String lastCheckDate = getPrefString(ctx, KEY_LAST_AUTO_UPDATE_DATE, "");
            if (today.equals(lastCheckDate)) {
                return false;
            }
            putPrefString(ctx, KEY_LAST_AUTO_UPDATE_DATE, today);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    void checkUpdate(final Context ctx, final boolean silent) {
        if (silent && (!autoUpdateEnabled || !isAutoUpdateDueToday(ctx))) {
            return;
        }
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            getPrefString(
                                                    ctx,
                                                    KEY_NETWORK_SOURCE,
                                                    DEFAULT_NETWORK_SOURCE);
                                    String updateUrl =
                                            networkSource.equals(NETWORK_SOURCE_VERCEL)
                                                    ? VERCEL_UPDATE_URL
                                                    : GITHUB_UPDATE_URL;

                                    URL url = new URL(updateUrl);
                                    HttpURLConnection conn =
                                            (HttpURLConnection) url.openConnection();
                                    conn.setConnectTimeout(8000);
                                    conn.setReadTimeout(8000);
                                    conn.setRequestMethod("GET");

                                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                        if (!silent) {
                                            jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(ctx, "check_update_failed"));
                                        }
                                        return;
                                    }

                                    BufferedReader br =
                                            new BufferedReader(
                                                    new InputStreamReader(conn.getInputStream()));
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
                                            ((Activity) Context)
                                                    .runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    showUpdateDialog(
                                                                            ctx,
                                                                            remoteVersion,
                                                                            finalUpdateLog,
                                                                            apkUrl);
                                                                }
                                                            });
                                        }
                                    } else if (!silent) {
                                        jiguroMessageWithContext(
                                                ctx,
                                                LocalizedStringProvider.getInstance()
                                                        .get(ctx, "check_update_no_update"));
                                    }
                                } catch (Exception e) {
                                    if (!silent) {
                                        jiguroMessageWithContext(
                                                ctx,
                                                LocalizedStringProvider.getInstance()
                                                        .get(ctx, "check_update_failed"));
                                    }
                                }
                            }
                        })
                .start();
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

    private void showUpdateDialog(
            final Context ctx, final String version, final String updateLog, final String apkUrl) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        final CharSequence log = (updateLog == null) ? "" : updateLog;
        SettingsUI.showUpdateDialog(
                act,
                version,
                log,
                new SettingsUI.UpdateDialogAction() {
                    @Override
                    public void onAction(Dialog dialog) {
                        if (dialog != null) dialog.dismiss();
                    }
                },
                new SettingsUI.UpdateDialogAction() {
                    @Override
                    public void onAction(final Dialog dialog) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
                            act.startActivity(intent);
                            if (dialog != null) dialog.dismiss();
                        } catch (Exception e) {
                            jiguroMessageWithContext(
                                    act,
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "cannot_open_download_link"));
                        }
                    }
                });
    }

    void showWithdrawAgreementDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        SettingsUI.showMessageDialog(
                                act,
                                "withdraw_agreement_dialog_title",
                                "withdraw_agreement_dialog_message",
                                "dialog_ok",
                                "dialog_cancel",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        putPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
                                        bvLog("[BetterVia] 用户已撤回用户协议同意，等待SharedPreferences写入完成");
                                        new Handler()
                                                .postDelayed(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                bvLog(
                                                                        "[BetterVia]"
                                                                            + " SharedPreferences写入完成，退出Via");
                                                                act.finish();
                                                                System.exit(0);
                                                            }
                                                        },
                                                        500);
                                    }
                                },
                                null);
                    }
                });
    }

    private void showShisuiDialog(final Context ctx) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

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
                        title.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "shisui_dialog_title"));
                        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                        title.setTextColor(getTitleColor(ctx));
                        title.setTypeface(null, Typeface.BOLD);
                        title.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams titleLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        titleLp.bottomMargin = dp(act, 8);
                        root.addView(title, titleLp);

                        TextView subtitle = new TextView(act);
                        subtitle.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "shisui_dialog_subtitle"));
                        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        subtitle.setTextColor(getHintColor(ctx));
                        subtitle.setGravity(Gravity.CENTER);
                        subtitle.setPadding(0, 0, 0, dp(act, 24));
                        root.addView(subtitle);

                        final TextView loadingText = new TextView(act);
                        loadingText.setText(
                                LocalizedStringProvider.getInstance().get(ctx, "shisui_loading"));
                        loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        loadingText.setTextColor(getHintColor(ctx));
                        loadingText.setGravity(Gravity.CENTER);
                        loadingText.setPadding(0, dp(act, 24), 0, dp(act, 24));
                        root.addView(loadingText);

                        final LinearLayout contentContainer = new LinearLayout(act);
                        contentContainer.setOrientation(LinearLayout.VERTICAL);
                        root.addView(contentContainer);

                        String savedSource =
                                getPrefString(ctx, KEY_NETWORK_SOURCE, DEFAULT_NETWORK_SOURCE);
                        final String shisuiUrl =
                                savedSource.equals(NETWORK_SOURCE_VERCEL)
                                        ? VERCEL_SHISUI_JSON_URL
                                        : GITHUB_SHISUI_JSON_URL;

                        new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    URL url = new URL(shisuiUrl);
                                                    HttpURLConnection conn =
                                                            (HttpURLConnection)
                                                                    url.openConnection();
                                                    conn.setRequestMethod("GET");
                                                    conn.setConnectTimeout(10000);
                                                    conn.setReadTimeout(15000);
                                                    conn.connect();

                                                    if (conn.getResponseCode()
                                                            == HttpURLConnection.HTTP_OK) {
                                                        InputStream is = conn.getInputStream();
                                                        BufferedReader reader =
                                                                new BufferedReader(
                                                                        new InputStreamReader(
                                                                                is, "UTF-8"));
                                                        StringBuilder sb = new StringBuilder();
                                                        String line;
                                                        while ((line = reader.readLine()) != null) {
                                                            sb.append(line);
                                                        }
                                                        reader.close();
                                                        is.close();
                                                        conn.disconnect();

                                                        final String jsonData = sb.toString();

                                                        act.runOnUiThread(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            root.removeView(
                                                                                    loadingText);

                                                                            JSONArray jsonArray =
                                                                                    new JSONArray(
                                                                                            jsonData);
                                                                            String lastYear = "";

                                                                            for (int i = 0;
                                                                                    i
                                                                                            < jsonArray
                                                                                                    .length();
                                                                                    i++) {
                                                                                JSONObject item =
                                                                                        jsonArray
                                                                                                .getJSONObject(
                                                                                                        i);
                                                                                final String year =
                                                                                        item
                                                                                                .getString(
                                                                                                        "year");
                                                                                final String
                                                                                        version =
                                                                                                item
                                                                                                        .getString(
                                                                                                                "version");
                                                                                final String
                                                                                        content =
                                                                                                item
                                                                                                        .getString(
                                                                                                                "content");

                                                                                if (!year.equals(
                                                                                        lastYear)) {
                                                                                    TextView
                                                                                            yearTitle =
                                                                                                    new TextView(
                                                                                                            act);
                                                                                    yearTitle
                                                                                            .setText(
                                                                                                    year);
                                                                                    yearTitle
                                                                                            .setTextSize(
                                                                                                    TypedValue
                                                                                                            .COMPLEX_UNIT_SP,
                                                                                                    18);
                                                                                    yearTitle
                                                                                            .setTextColor(
                                                                                                    getTextColor(
                                                                                                            ctx));
                                                                                    yearTitle
                                                                                            .setTypeface(
                                                                                                    null,
                                                                                                    Typeface
                                                                                                            .BOLD);
                                                                                    yearTitle
                                                                                            .setPadding(
                                                                                                    0,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            16),
                                                                                                    0,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            8));
                                                                                    contentContainer
                                                                                            .addView(
                                                                                                    yearTitle);
                                                                                    lastYear = year;
                                                                                }

                                                                                LinearLayout
                                                                                        versionContainer =
                                                                                                new LinearLayout(
                                                                                                        act);
                                                                                versionContainer
                                                                                        .setOrientation(
                                                                                                LinearLayout
                                                                                                        .HORIZONTAL);
                                                                                versionContainer
                                                                                        .setGravity(
                                                                                                Gravity
                                                                                                        .CENTER_VERTICAL);

                                                                                TextView
                                                                                        versionTitle =
                                                                                                new TextView(
                                                                                                        act);
                                                                                versionTitle
                                                                                        .setText(
                                                                                                version);
                                                                                versionTitle
                                                                                        .setTextSize(
                                                                                                TypedValue
                                                                                                        .COMPLEX_UNIT_SP,
                                                                                                14);
                                                                                versionTitle
                                                                                        .setTextColor(
                                                                                                getTitleColor(
                                                                                                        ctx));
                                                                                versionTitle
                                                                                        .setTypeface(
                                                                                                null,
                                                                                                Typeface
                                                                                                        .BOLD);
                                                                                versionTitle
                                                                                        .setPadding(
                                                                                                0,
                                                                                                0,
                                                                                                dp(
                                                                                                        act,
                                                                                                        8),
                                                                                                dp(
                                                                                                        act,
                                                                                                        4));
                                                                                versionContainer
                                                                                        .addView(
                                                                                                versionTitle,
                                                                                                new LinearLayout
                                                                                                        .LayoutParams(
                                                                                                        0,
                                                                                                        ViewGroup
                                                                                                                .LayoutParams
                                                                                                                .WRAP_CONTENT,
                                                                                                        1));

                                                                                TextView copyBtn =
                                                                                        new TextView(
                                                                                                act);
                                                                                applyClickAnim(
                                                                                        copyBtn);
                                                                                copyBtn.setText(
                                                                                        LocalizedStringProvider
                                                                                                .getInstance()
                                                                                                .get(
                                                                                                        ctx,
                                                                                                        "shisui_copy"));
                                                                                copyBtn.setTextSize(
                                                                                        TypedValue
                                                                                                .COMPLEX_UNIT_SP,
                                                                                        12);
                                                                                copyBtn
                                                                                        .setTextColor(
                                                                                                getBtnTextColor(
                                                                                                        ctx));
                                                                                copyBtn.setPadding(
                                                                                        dp(act, 8),
                                                                                        dp(act, 4),
                                                                                        dp(act, 8),
                                                                                        dp(act, 4));
                                                                                copyBtn
                                                                                        .setBackground(
                                                                                                getRoundBg(
                                                                                                        act,
                                                                                                        getBtnBgColor(
                                                                                                                ctx),
                                                                                                        12));
                                                                                copyBtn
                                                                                        .setOnClickListener(
                                                                                                new View
                                                                                                        .OnClickListener() {
                                                                                                    @Override
                                                                                                    public
                                                                                                    void
                                                                                                            onClick(
                                                                                                                    View
                                                                                                                            v) {
                                                                                                        StringBuilder
                                                                                                                copyContent =
                                                                                                                        new StringBuilder();
                                                                                                        copyContent
                                                                                                                .append(
                                                                                                                        version)
                                                                                                                .append(
                                                                                                                        "\n");
                                                                                                        copyContent
                                                                                                                .append(
                                                                                                                        content
                                                                                                                                .replace(
                                                                                                                                        "\\r"
                                                                                                                                            + "\\n",
                                                                                                                                        "\n"));

                                                                                                        ClipboardManager
                                                                                                                clipboard =
                                                                                                                        (ClipboardManager)
                                                                                                                                act
                                                                                                                                        .getSystemService(
                                                                                                                                                Context
                                                                                                                                                        .CLIPBOARD_SERVICE);
                                                                                                        ClipData
                                                                                                                clip =
                                                                                                                        ClipData
                                                                                                                                .newPlainText(
                                                                                                                                        "Via Shisui",
                                                                                                                                        copyContent
                                                                                                                                                .toString());
                                                                                                        clipboard
                                                                                                                .setPrimaryClip(
                                                                                                                        clip);

                                                                                                        jiguroMessageWithContext(
                                                                                                                act,
                                                                                                                LocalizedStringProvider
                                                                                                                        .getInstance()
                                                                                                                        .get(
                                                                                                                                ctx,
                                                                                                                                "shisui_copied"));
                                                                                                    }
                                                                                                });
                                                                                versionContainer
                                                                                        .addView(
                                                                                                copyBtn);

                                                                                contentContainer
                                                                                        .addView(
                                                                                                versionContainer);

                                                                                String[] lines =
                                                                                        content
                                                                                                .split(
                                                                                                        "\\\\r"
                                                                                                            + "\\\\n");
                                                                                for (String line :
                                                                                        lines) {
                                                                                    TextView
                                                                                            contentText =
                                                                                                    new TextView(
                                                                                                            act);
                                                                                    contentText
                                                                                            .setText(
                                                                                                    line);
                                                                                    contentText
                                                                                            .setTextSize(
                                                                                                    TypedValue
                                                                                                            .COMPLEX_UNIT_SP,
                                                                                                    14);
                                                                                    contentText
                                                                                            .setTextColor(
                                                                                                    getTextColor(
                                                                                                            ctx));
                                                                                    contentText
                                                                                            .setPadding(
                                                                                                    dp(
                                                                                                            act,
                                                                                                            8),
                                                                                                    dp(
                                                                                                            act,
                                                                                                            4),
                                                                                                    dp(
                                                                                                            act,
                                                                                                            8),
                                                                                                    dp(
                                                                                                            act,
                                                                                                            4));
                                                                                    contentContainer
                                                                                            .addView(
                                                                                                    contentText);
                                                                                }

                                                                                if (i
                                                                                        < jsonArray
                                                                                                        .length()
                                                                                                - 1) {
                                                                                    View divider =
                                                                                            new View(
                                                                                                    act);
                                                                                    divider
                                                                                            .setLayoutParams(
                                                                                                    new LinearLayout
                                                                                                            .LayoutParams(
                                                                                                            ViewGroup
                                                                                                                    .LayoutParams
                                                                                                                    .MATCH_PARENT,
                                                                                                            dp(
                                                                                                                    act,
                                                                                                                    1)));
                                                                                    divider
                                                                                            .setBackgroundColor(
                                                                                                    getDividerColor(
                                                                                                            ctx));
                                                                                    LinearLayout
                                                                                                    .LayoutParams
                                                                                            dividerLp =
                                                                                                    new LinearLayout
                                                                                                            .LayoutParams(
                                                                                                            ViewGroup
                                                                                                                    .LayoutParams
                                                                                                                    .MATCH_PARENT,
                                                                                                            dp(
                                                                                                                    act,
                                                                                                                    1));
                                                                                    dividerLp
                                                                                            .setMargins(
                                                                                                    0,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            12),
                                                                                                    0,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            12));
                                                                                    divider
                                                                                            .setLayoutParams(
                                                                                                    dividerLp);
                                                                                    contentContainer
                                                                                            .addView(
                                                                                                    divider);
                                                                                }
                                                                            }

                                                                            View finalDivider =
                                                                                    new View(act);
                                                                            finalDivider
                                                                                    .setLayoutParams(
                                                                                            new LinearLayout
                                                                                                    .LayoutParams(
                                                                                                    ViewGroup
                                                                                                            .LayoutParams
                                                                                                            .MATCH_PARENT,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            1)));
                                                                            finalDivider
                                                                                    .setBackgroundColor(
                                                                                            getDividerColor(
                                                                                                    ctx));
                                                                            LinearLayout
                                                                                            .LayoutParams
                                                                                    finalDividerLp =
                                                                                            new LinearLayout
                                                                                                    .LayoutParams(
                                                                                                    ViewGroup
                                                                                                            .LayoutParams
                                                                                                            .MATCH_PARENT,
                                                                                                    dp(
                                                                                                            act,
                                                                                                            1));
                                                                            finalDividerLp
                                                                                    .setMargins(
                                                                                            0,
                                                                                            dp(
                                                                                                    act,
                                                                                                    12),
                                                                                            0,
                                                                                            dp(
                                                                                                    act,
                                                                                                    8));
                                                                            finalDivider
                                                                                    .setLayoutParams(
                                                                                            finalDividerLp);
                                                                            contentContainer
                                                                                    .addView(
                                                                                            finalDivider);

                                                                            TextView continuedText =
                                                                                    new TextView(
                                                                                            act);
                                                                            continuedText.setText(
                                                                                    LocalizedStringProvider
                                                                                            .getInstance()
                                                                                            .get(
                                                                                                    ctx,
                                                                                                    "to_be_continued"));
                                                                            continuedText
                                                                                    .setTextSize(
                                                                                            TypedValue
                                                                                                    .COMPLEX_UNIT_SP,
                                                                                            14);
                                                                            continuedText
                                                                                    .setTextColor(
                                                                                            getHintColor(
                                                                                                    ctx));
                                                                            continuedText
                                                                                    .setTypeface(
                                                                                            null,
                                                                                            Typeface
                                                                                                    .ITALIC);
                                                                            continuedText
                                                                                    .setGravity(
                                                                                            Gravity
                                                                                                    .CENTER);
                                                                            continuedText
                                                                                    .setPadding(
                                                                                            0,
                                                                                            0,
                                                                                            0,
                                                                                            dp(
                                                                                                    act,
                                                                                                    16));
                                                                            contentContainer
                                                                                    .addView(
                                                                                            continuedText);

                                                                            LinearLayout
                                                                                    bottomContainer =
                                                                                            new LinearLayout(
                                                                                                    act);
                                                                            bottomContainer
                                                                                    .setOrientation(
                                                                                            LinearLayout
                                                                                                    .HORIZONTAL);
                                                                            bottomContainer
                                                                                    .setGravity(
                                                                                            Gravity
                                                                                                    .CENTER);

                                                                            TextView bottomText =
                                                                                    new TextView(
                                                                                            act);
                                                                            bottomText.setText(
                                                                                    LocalizedStringProvider
                                                                                                    .getInstance()
                                                                                                    .get(
                                                                                                            ctx,
                                                                                                            "shisui_source_credit")
                                                                                            + " ");
                                                                            bottomText.setTextSize(
                                                                                    TypedValue
                                                                                            .COMPLEX_UNIT_SP,
                                                                                    12);
                                                                            bottomText.setTextColor(
                                                                                    getHintColor(
                                                                                            ctx));
                                                                            bottomContainer.addView(
                                                                                    bottomText);
                                                                            SpannableString ss =
                                                                                    new SpannableString(
                                                                                            "sgfox");
                                                                            ClickableSpan
                                                                                    clickableSpan =
                                                                                            new ClickableSpan() {
                                                                                                @Override
                                                                                                public
                                                                                                void
                                                                                                        onClick(
                                                                                                                View
                                                                                                                        widget) {
                                                                                                    openUrl(
                                                                                                            act,
                                                                                                            "https://www.sgfox.cc/archives/via-shisui.html");
                                                                                                    jiguroMessageWithContext(
                                                                                                            act,
                                                                                                            LocalizedStringProvider
                                                                                                                    .getInstance()
                                                                                                                    .get(
                                                                                                                            ctx,
                                                                                                                            "url_opened"));
                                                                                                }
                                                                                            };

                                                                            ss.setSpan(
                                                                                    clickableSpan,
                                                                                    0,
                                                                                    ss.length(),
                                                                                    Spanned
                                                                                            .SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                            ss.setSpan(
                                                                                    new ForegroundColorSpan(
                                                                                            getTitleColor(
                                                                                                    ctx)),
                                                                                    0,
                                                                                    ss.length(),
                                                                                    Spanned
                                                                                            .SPAN_EXCLUSIVE_EXCLUSIVE);

                                                                            TextView sgfoxText =
                                                                                    new TextView(
                                                                                            act);
                                                                            sgfoxText.setText(ss);
                                                                            sgfoxText.setTextSize(
                                                                                    TypedValue
                                                                                            .COMPLEX_UNIT_SP,
                                                                                    12);
                                                                            sgfoxText
                                                                                    .setMovementMethod(
                                                                                            LinkMovementMethod
                                                                                                    .getInstance());
                                                                            bottomContainer.addView(
                                                                                    sgfoxText);

                                                                            LinearLayout
                                                                                            .LayoutParams
                                                                                    bottomLp =
                                                                                            new LinearLayout
                                                                                                    .LayoutParams(
                                                                                                    ViewGroup
                                                                                                            .LayoutParams
                                                                                                            .WRAP_CONTENT,
                                                                                                    ViewGroup
                                                                                                            .LayoutParams
                                                                                                            .WRAP_CONTENT);
                                                                            bottomLp.gravity =
                                                                                    Gravity.CENTER;
                                                                            bottomLp.topMargin =
                                                                                    dp(act, 8);
                                                                            contentContainer
                                                                                    .addView(
                                                                                            bottomContainer,
                                                                                            bottomLp);

                                                                        } catch (JSONException e) {
                                                                            loadingText.setText(
                                                                                    LocalizedStringProvider
                                                                                            .getInstance()
                                                                                            .get(
                                                                                                    ctx,
                                                                                                    "shisui_load_failed"));
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        act.runOnUiThread(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        loadingText.setText(
                                                                                LocalizedStringProvider
                                                                                        .getInstance()
                                                                                        .get(
                                                                                                ctx,
                                                                                                "shisui_load_failed"));
                                                                    }
                                                                });
                                                    }
                                                } catch (Exception e) {
                                                    act.runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    loadingText.setText(
                                                                            LocalizedStringProvider
                                                                                    .getInstance()
                                                                                    .get(
                                                                                            ctx,
                                                                                            "shisui_load_failed"));
                                                                }
                                                            });
                                                }
                                            }
                                        })
                                .start();

                        scrollRoot.addView(root);
                        dialogContainer.addView(scrollRoot);

                        Window win = dialog.getWindow();
                        if (win != null) {
                            win.setBackgroundDrawableResource(android.R.color.transparent);
                            win.setGravity(Gravity.CENTER);
                            DisplayMetrics dialogMetrics = new DisplayMetrics();
                            act.getWindowManager().getDefaultDisplay().getMetrics(dialogMetrics);
                            int dialogWidth = (int) (dialogMetrics.widthPixels * 0.9);
                            WindowManager.LayoutParams dialogLp = new WindowManager.LayoutParams();
                            dialogLp.copyFrom(win.getAttributes());
                            dialogLp.width = dialogWidth;
                            dialogLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            dialogLp.gravity = Gravity.CENTER;
                            win.setAttributes(dialogLp);
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

    private void openUrl(Context ctx, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ctx.startActivity(intent);
        } catch (Exception e) {
            jiguroMessageWithContext(
                    ctx, LocalizedStringProvider.getInstance().get(ctx, "cannot_open_url"));
        }
    }

    void openUrlAndClose(final Activity act, String url) {
        SettingsUI.dismissAllPages();
        openUrl(act, url);
        if (act != null) {
            try {
                act.onBackPressed();
            } catch (Exception ignored) {
            }
        }
    }

    void showVersionSelectionDialog(
            final Context ctx,
            final String currentVersion,
            final int savedVersionCode,
            final boolean isViaUpdated,
            final int currentVersionCode,
            final long currentUpdateTime) {
        final Activity act = getActivityFrom(ctx);
        if (act == null) return;

        if (isViaUpdated) {
            putPrefBoolean(ctx, KEY_NEED_CLEAR_NETWORK_MAPPINGS, true);
            putPrefBoolean(ctx, KEY_SKIP_CLEAR_NETWORK_MAPPINGS, false);
            bvLog("[BetterVia] Via已更新，设置需要清理网络映射的标记");
        }

        final String detectedName =
                (currentVersion != null && !currentVersion.isEmpty())
                        ? currentVersion
                        : ViaVersionDetector.getVersionName(currentVersionCode);

        String body =
                String.format(
                        LocalizedStringProvider.getInstance().get(ctx, "version_dialog_body"),
                        detectedName);
        if (isViaUpdated) {
            body +=
                    "\n\n"
                            + LocalizedStringProvider.getInstance()
                                    .get(ctx, "version_selector_update_hint");
        }

        SettingsUI.showMessageDialog(
                act,
                "version_dialog_title",
                (CharSequence) body,
                null,
                false,
                "version_dialog_choose",
                "cancel",
                new Runnable() {
                    @Override
                    public void run() {
                        showViaVersionPage(ctx, true);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "version_selector_cancel_hint"));
                    }
                },
                null,
                false,
                false);

        hasShownStartupDialog = true;
    }

    public static void bvLogVerbose(String message) {
        if (developerModeEnabled) bvLog(message);
    }

    public static void bvLog(String message) {
        XposedBridge.log(message);
        if (developerModeEnabled && Context != null) {
            try {
                if (logWriter == null) {
                    initLogFile();
                }
                if (logWriter != null) {
                    SimpleDateFormat sdf =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    String timestamp = sdf.format(new Date());
                    logWriter.println("[" + timestamp + "] " + message);
                    logWriter.flush();
                }
            } catch (Exception e) {
            }
        }
    }

    private static void initLogFile() {
        if (logWriter != null) return;
        try {
            if (Context == null) {
                XposedBridge.log("[BetterVia] initLogFile: Context 为 null，延迟初始化");
                return;
            }
            Context appCtx = Context.getApplicationContext();
            if (appCtx == null) appCtx = Context;
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
            logWriter =
                    new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), true);
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

    byte[] readFileToBytes(File file) throws IOException {
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        FileInputStream fis = new FileInputStream(file);
        try {
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            fis.close();
        }
        return bytes;
    }

    private X509Certificate readCertificate(File pemFile) throws Exception {
        byte[] certBytes = readFileToBytes(pemFile);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert =
                (X509Certificate)
                        certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        return cert;
    }

    private void collectFiles(File dir, List<String> files, File rootDir) {
        File[] list = dir.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    collectFiles(file, files, rootDir);
                } else {
                    String relativePath =
                            file.getAbsolutePath()
                                    .substring(rootDir.getAbsolutePath().length() + 1);
                    files.add(relativePath.replace("\\", "/"));
                }
            }
        }
    }

    private void showStorageManagerDialog(final Activity act) {
        if (act == null) return;

        final String packageName = act.getPackageName();

        final File internalDir = new File("/data/user/0/" + packageName + "/files/BetterVia/");
        final File externalDir =
                new File("/storage/emulated/0/Android/data/" + packageName + "/files/BetterVia/");

        final String sizeStr = formatSize(getFolderSize(internalDir) + getFolderSize(externalDir));

        SettingsUI.showStorageManagerDialog(
                act,
                sizeStr,
                new Runnable() {
                    @Override
                    public void run() {
                        deleteFolder(internalDir);
                        deleteFolder(externalDir);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        performDeepClean(act, packageName, null);
                    }
                });
    }

    private void performDeepClean(
            final Activity act, final String packageName, final Button triggerBtn) {

        if (triggerBtn != null) {
            triggerBtn.setEnabled(false);
            triggerBtn.setText(LocalizedStringProvider.getInstance().get(act, "storage_cleaning"));
        }

        final ProgressBar progressBar = new ProgressBar(act);
        progressBar.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getOkBtnBgColor(act)));
        }

        final AlertDialog loadingDialog =
                new AlertDialog.Builder(act)
                        .setTitle(
                                LocalizedStringProvider.getInstance()
                                        .get(act, "storage_cleaning_title"))
                        .setView(progressBar)
                        .setCancelable(false)
                        .create();

        applyAlertDialogTheme(act, act, loadingDialog);
        loadingDialog.show();

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                boolean success = true;

                                try {

                                    File internalDir =
                                            new File(
                                                    "/data/user/0/"
                                                            + packageName
                                                            + "/files/BetterVia/");
                                    File externalDir =
                                            new File(
                                                    "/storage/emulated/0/Android/data/"
                                                            + packageName
                                                            + "/files/BetterVia/");
                                    File prefFile =
                                            new File(
                                                    "/data/user/0/"
                                                            + packageName
                                                            + "/shared_prefs/BetterVia.xml");

                                    safeDelete(internalDir);
                                    safeDelete(externalDir);

                                    if (prefFile.exists()) {
                                        prefFile.delete();
                                    }

                                } catch (Throwable t) {
                                    success = false;
                                }

                                final boolean finalSuccess = success;

                                act.runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {

                                                loadingDialog.dismiss();

                                                if (finalSuccess) {

                                                    jiguroMessageWithContext(
                                                            act,
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(
                                                                            act,
                                                                            "storage_clean_success"));

                                                    act.getWindow()
                                                            .getDecorView()
                                                            .postDelayed(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Process.killProcess(
                                                                                    Process
                                                                                            .myPid());
                                                                            System.exit(0);
                                                                        }
                                                                    },
                                                                    800);

                                                } else {

                                                    if (triggerBtn != null) {
                                                        triggerBtn.setEnabled(true);
                                                        triggerBtn.setText(
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                act,
                                                                                "storage_confirm_delete"));
                                                    }

                                                    jiguroMessageWithContext(
                                                            act,
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(
                                                                            act,
                                                                            "storage_clean_failed"));
                                                }
                                            }
                                        });
                            }
                        })
                .start();
    }

    private static long getFolderSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
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
        if (dir == null || !dir.exists()) return;

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
        if (size <= 0) return "0 B";
        final String[] units = new String[] {"B", "KB", "MB", "GB"};
        int digit = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(
                Locale.getDefault(), "%.2f %s", size / Math.pow(1024, digit), units[digit]);
    }

    private static void safeDelete(File dir) {
        if (dir == null || !dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

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
                if (showUrlSchemeEnabled && !showUrlSchemeHooks.isEmpty()) return;
                bvLog("[BetterVia] 启用URL Scheme显示功能");
                showUrlSchemeEnabled = true;
                installUrlSchemeHook(ctx, cl);
            } else {
                bvLog("[BetterVia] 禁用URL Scheme显示功能");
                showUrlSchemeEnabled = false;
                unhookUrlSchemeHooks();
            }
        } catch (Throwable t) {
            bvLog("[BetterVia] 设置URL Scheme Hook失败: " + t);
        }
    }

    private void unhookUrlSchemeHooks() {
        for (XC_MethodHook.Unhook h : showUrlSchemeHooks) {
            try {
                h.unhook();
            } catch (Throwable ignored) {
            }
        }
        showUrlSchemeHooks.clear();
    }

    private void installUrlSchemeHook(final Context ctx, final ClassLoader cl) {
        unhookUrlSchemeHooks();
        try {
            showUrlSchemeHooks.add(
                    XposedHelpers.findAndHookMethod(
                            Activity.class,
                            "startActivity",
                            Intent.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    if (!showUrlSchemeEnabled) {
                                        return;
                                    }

                                    Intent intent = (Intent) param.args[0];
                                    if (intent != null && intent.getData() != null) {
                                        String scheme = intent.getScheme();
                                        String dataString = intent.getDataString();

                                        if (scheme != null
                                                && !scheme.equals("http")
                                                && !scheme.equals("https")
                                                && !scheme.equals("file")) {
                                            bvLogVerbose(
                                                    "[BetterVia] 检测到URL Scheme: " + dataString);
                                            showUrlSchemeToast(ctx, dataString);
                                        }
                                    }
                                }
                            }));

            showUrlSchemeHooks.add(
                    XposedHelpers.findAndHookMethod(
                            Activity.class,
                            "startActivityForResult",
                            Intent.class,
                            int.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    if (!showUrlSchemeEnabled) {
                                        return;
                                    }

                                    Intent intent = (Intent) param.args[0];
                                    if (intent != null && intent.getData() != null) {
                                        String scheme = intent.getScheme();
                                        String dataString = intent.getDataString();

                                        if (scheme != null
                                                && !scheme.equals("http")
                                                && !scheme.equals("https")
                                                && !scheme.equals("file")) {
                                            bvLogVerbose(
                                                    "[BetterVia] 检测到URL Scheme: " + dataString);
                                            showUrlSchemeToast(ctx, dataString);
                                        }
                                    }
                                }
                            }));

            showUrlSchemeHooks.add(
                    XposedHelpers.findAndHookMethod(
                            Context.class,
                            "startActivity",
                            Intent.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    if (!showUrlSchemeEnabled) {
                                        return;
                                    }

                                    Intent intent = (Intent) param.args[0];
                                    if (intent != null && intent.getData() != null) {
                                        String scheme = intent.getScheme();
                                        String dataString = intent.getDataString();

                                        if (scheme != null
                                                && !scheme.equals("http")
                                                && !scheme.equals("https")
                                                && !scheme.equals("file")) {
                                            bvLogVerbose(
                                                    "[BetterVia] 检测到URL Scheme: " + dataString);
                                            showUrlSchemeToast(ctx, dataString);
                                        }
                                    }
                                }
                            }));

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
            finalActivity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (finalActivity.isFinishing() || finalActivity.isDestroyed()) {
                                return;
                            }

                            try {
                                final FrameLayout overlay = new FrameLayout(finalActivity);
                                overlay.setBackgroundColor(0xCC000000);

                                TextView titleView = new TextView(finalActivity);
                                titleView.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "url_scheme_detected"));
                                titleView.setTextColor(Color.WHITE);
                                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                titleView.setTypeface(null, Typeface.BOLD);
                                titleView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 12),
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8));

                                final TextView textView = new TextView(finalActivity);
                                textView.setText(urlScheme);
                                textView.setTextColor(Color.WHITE);
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                textView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8),
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8));
                                textView.setMaxWidth(dp(finalActivity, 320));
                                textView.setEllipsize(TextUtils.TruncateAt.END);
                                textView.setSingleLine(true);

                                final TextView hintView = new TextView(finalActivity);
                                hintView.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "url_scheme_copied"));
                                hintView.setTextColor(Color.GREEN);
                                hintView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                                hintView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8),
                                        dp(finalActivity, 16),
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

                                overlay.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    ClipboardManager clipboard =
                                                            (ClipboardManager)
                                                                    finalActivity.getSystemService(
                                                                            Context
                                                                                    .CLIPBOARD_SERVICE);
                                                    android.content.ClipData clip =
                                                            android.content.ClipData.newPlainText(
                                                                    "URL Scheme", urlScheme);
                                                    clipboard.setPrimaryClip(clip);

                                                    hintView.setVisibility(View.VISIBLE);

                                                    new Handler()
                                                            .postDelayed(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            hintView.setVisibility(
                                                                                    View.GONE);
                                                                        }
                                                                    },
                                                                    2000);

                                                    bvLog(
                                                            "[BetterVia] 已复制URL Scheme: "
                                                                    + urlScheme);
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

                                int marginTop =
                                        dp(finalActivity, 16)
                                                + overlayIndex * (dp(finalActivity, 110));

                                FrameLayout.LayoutParams params =
                                        new FrameLayout.LayoutParams(
                                                dp(finalActivity, 320),
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                                params.setMargins(
                                        dp(finalActivity, 16), marginTop, dp(finalActivity, 16), 0);

                                ViewGroup decorView =
                                        (ViewGroup) finalActivity.getWindow().getDecorView();
                                decorView.addView(overlay, params);

                                TranslateAnimation slideInAnimation =
                                        new TranslateAnimation(
                                                Animation.RELATIVE_TO_PARENT,
                                                -1.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f);
                                slideInAnimation.setDuration(300);
                                slideInAnimation.setInterpolator(new DecelerateInterpolator());
                                overlay.startAnimation(slideInAnimation);

                                final Handler dismissHandler = new Handler(Looper.getMainLooper());
                                synchronized (urlSchemeDismissHandlers) {
                                    urlSchemeDismissHandlers.add(dismissHandler);
                                }

                                dismissHandler.postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                TranslateAnimation slideOutAnimation =
                                                        new TranslateAnimation(
                                                                Animation.RELATIVE_TO_PARENT, 0.0f,
                                                                Animation.RELATIVE_TO_PARENT, 1.0f,
                                                                Animation.RELATIVE_TO_PARENT, 0.0f,
                                                                Animation.RELATIVE_TO_PARENT, 0.0f);
                                                slideOutAnimation.setDuration(300);
                                                slideOutAnimation.setInterpolator(
                                                        new AccelerateInterpolator());
                                                slideOutAnimation.setAnimationListener(
                                                        new Animation.AnimationListener() {
                                                            @Override
                                                            public void onAnimationStart(
                                                                    Animation animation) {}

                                                            @Override
                                                            public void onAnimationEnd(
                                                                    Animation animation) {
                                                                ViewGroup parent =
                                                                        (ViewGroup)
                                                                                overlay.getParent();
                                                                if (parent != null) {
                                                                    parent.removeView(overlay);
                                                                }

                                                                synchronized (urlSchemeOverlays) {
                                                                    urlSchemeOverlays.remove(
                                                                            overlay);
                                                                    urlSchemeTextViews.remove(
                                                                            textView);
                                                                }
                                                                synchronized (
                                                                        urlSchemeDismissHandlers) {
                                                                    urlSchemeDismissHandlers.remove(
                                                                            dismissHandler);
                                                                }
                                                                synchronized (displayedUrlSchemes) {
                                                                    displayedUrlSchemes.remove(
                                                                            urlScheme);
                                                                }

                                                                bvLog(
                                                                        "[BetterVia] URL"
                                                                            + " Scheme浮窗已自动消失: "
                                                                                + urlScheme);
                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(
                                                                    Animation animation) {}
                                                        });
                                                overlay.startAnimation(slideOutAnimation);
                                            }
                                        },
                                        URL_SCHEME_DISPLAY_DURATION);

                                bvLog(
                                        "[BetterVia] 已显示URL Scheme浮窗（第"
                                                + (overlayIndex + 1)
                                                + "个）: "
                                                + urlScheme);
                            } catch (Exception e) {
                                bvLog("[BetterVia] 显示URL Scheme浮窗失败: " + e);
                            }
                        }
                    });
        } catch (Exception e) {
            bvLog("[BetterVia] 显示URL Scheme失败: " + e);
        }
    }

    static boolean sViaUpdateDetected = false;

    private void showVersionErrorDialog(final Context ctx) {
        if (ctx == null) {
            return;
        }

        try {
            Activity activity = currentActivity;
            if (activity == null) {
                activity = Context;
            }

            if (activity == null) {
                return;
            }

            final Activity finalActivity = activity;
            finalActivity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (finalActivity.isFinishing() || finalActivity.isDestroyed()) {
                                return;
                            }

                            try {
                                final FrameLayout overlay = new FrameLayout(finalActivity);
                                overlay.setBackgroundColor(0xCC000000);

                                TextView titleView = new TextView(finalActivity);
                                titleView.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "version_error_title"));
                                titleView.setTextColor(Color.WHITE);
                                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                titleView.setTypeface(null, Typeface.BOLD);
                                titleView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 12),
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8));

                                TextView hintView = new TextView(finalActivity);
                                hintView.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "version_error_hint"));
                                hintView.setTextColor(Color.WHITE);
                                hintView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                hintView.setMaxWidth(dp(finalActivity, 320));
                                hintView.setEllipsize(TextUtils.TruncateAt.END);
                                hintView.setSingleLine(true);
                                hintView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8),
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8));

                                final TextView cleaningView = new TextView(finalActivity);
                                cleaningView.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(ctx, "version_error_cleaning"));
                                cleaningView.setTextColor(Color.GREEN);
                                cleaningView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                                cleaningView.setPadding(
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 8),
                                        dp(finalActivity, 16),
                                        dp(finalActivity, 12));
                                cleaningView.setVisibility(View.GONE);

                                GradientDrawable bg = new GradientDrawable();
                                bg.setColor(0xCC000000);
                                bg.setCornerRadius(dp(finalActivity, 12));
                                overlay.setBackgroundDrawable(bg);

                                LinearLayout layout = new LinearLayout(finalActivity);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.addView(titleView);
                                layout.addView(hintView);
                                layout.addView(cleaningView);

                                overlay.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                cleaningView.setVisibility(View.VISIBLE);

                                                overlay.setClickable(false);

                                                performVersionErrorClean(finalActivity);
                                            }
                                        });

                                overlay.addView(layout);

                                int marginTop = dp(finalActivity, 16);

                                FrameLayout.LayoutParams params =
                                        new FrameLayout.LayoutParams(
                                                dp(finalActivity, 320),
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                                params.setMargins(
                                        dp(finalActivity, 16), marginTop, dp(finalActivity, 16), 0);

                                ViewGroup decorView =
                                        (ViewGroup) finalActivity.getWindow().getDecorView();
                                decorView.addView(overlay, params);

                                TranslateAnimation slideInAnimation =
                                        new TranslateAnimation(
                                                Animation.RELATIVE_TO_PARENT,
                                                -1.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f,
                                                Animation.RELATIVE_TO_PARENT,
                                                0.0f);
                                slideInAnimation.setDuration(300);
                                slideInAnimation.setInterpolator(new DecelerateInterpolator());
                                overlay.startAnimation(slideInAnimation);

                                final Handler dismissHandler = new Handler(Looper.getMainLooper());

                                dismissHandler.postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                TranslateAnimation slideOutAnimation =
                                                        new TranslateAnimation(
                                                                Animation.RELATIVE_TO_PARENT,
                                                                0.0f,
                                                                Animation.RELATIVE_TO_PARENT,
                                                                1.0f,
                                                                Animation.RELATIVE_TO_PARENT,
                                                                0.0f,
                                                                Animation.RELATIVE_TO_PARENT,
                                                                0.0f);
                                                slideOutAnimation.setDuration(300);
                                                slideOutAnimation.setInterpolator(
                                                        new AccelerateInterpolator());
                                                slideOutAnimation.setAnimationListener(
                                                        new Animation.AnimationListener() {
                                                            @Override
                                                            public void onAnimationStart(
                                                                    Animation animation) {}

                                                            @Override
                                                            public void onAnimationEnd(
                                                                    Animation animation) {
                                                                ViewGroup parent =
                                                                        (ViewGroup)
                                                                                overlay.getParent();
                                                                if (parent != null) {
                                                                    parent.removeView(overlay);
                                                                }
                                                                bvLog("[BetterVia] 版本错误提示框已自动消失");
                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(
                                                                    Animation animation) {}
                                                        });
                                                overlay.startAnimation(slideOutAnimation);
                                            }
                                        },
                                        URL_SCHEME_DISPLAY_DURATION);

                                bvLog("[BetterVia] 已显示版本错误提示框");
                            } catch (Exception e) {
                                bvLog("[BetterVia] 显示版本错误提示框失败: " + e);
                            }
                        }
                    });
        } catch (Exception e) {
            bvLog("[BetterVia] 显示版本错误提示框失败: " + e);
        }
    }

    private void performVersionErrorClean(final Activity act) {
        final String packageName = act.getPackageName();

        final ProgressBar progressBar = new ProgressBar(act);
        progressBar.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(0xFFFFFFFF));
        }

        final AlertDialog loadingDialog =
                new AlertDialog.Builder(act)
                        .setTitle(
                                LocalizedStringProvider.getInstance()
                                        .get(act, "version_error_cleaning"))
                        .setView(progressBar)
                        .setCancelable(false)
                        .create();

        Window window = loadingDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new GradientDrawable());
            window.setBackgroundDrawableResource(android.R.color.transparent);
            GradientDrawable dialogBg = new GradientDrawable();
            dialogBg.setColor(0xCC000000);
            dialogBg.setCornerRadius(dp(act, 12));
            window.setBackgroundDrawable(dialogBg);
        }

        loadingDialog.show();

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                boolean success = true;

                                try {
                                    File internalDir =
                                            new File(
                                                    "/data/user/0/"
                                                            + packageName
                                                            + "/files/BetterVia/");
                                    File externalDir =
                                            new File(
                                                    "/storage/emulated/0/Android/data/"
                                                            + packageName
                                                            + "/files/BetterVia/");
                                    File prefFile =
                                            new File(
                                                    "/data/user/0/"
                                                            + packageName
                                                            + "/shared_prefs/BetterVia.xml");

                                    safeDelete(internalDir);
                                    safeDelete(externalDir);

                                    if (prefFile.exists()) {
                                        prefFile.delete();
                                    }

                                } catch (Throwable t) {
                                    success = false;
                                    bvLog("[BetterVia] 清除数据失败: " + t);
                                }

                                final boolean finalSuccess = success;

                                act.runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingDialog.dismiss();

                                                if (finalSuccess) {
                                                    jiguroMessageWithContext(
                                                            act,
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(
                                                                            act,
                                                                            "storage_clean_success"));

                                                    act.getWindow()
                                                            .getDecorView()
                                                            .postDelayed(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Process.killProcess(
                                                                                    Process
                                                                                            .myPid());
                                                                            System.exit(0);
                                                                        }
                                                                    },
                                                                    800);
                                                } else {
                                                    jiguroMessageWithContext(
                                                            act,
                                                            LocalizedStringProvider.getInstance()
                                                                    .get(
                                                                            act,
                                                                            "storage_clean_failed"));
                                                }
                                            }
                                        });
                            }
                        })
                .start();
    }

    static void showAnnouncementIfReady(final Context ctx) {
        boolean hasLanguageSelection = getPrefBoolean(ctx, KEY_HAS_LANGUAGE_SELECTION, false);
        String savedNetworkSource = getPrefString(ctx, KEY_NETWORK_SOURCE, "");
        boolean hasValidNetworkSource = isValidNetworkSource(savedNetworkSource);
        boolean hasUserAgreement = getPrefBoolean(ctx, KEY_HAS_USER_AGREEMENT, false);
        boolean hasVersionSelection = getPrefBoolean(ctx, "has_version_selection", false);

        if (hasLanguageSelection
                && hasValidNetworkSource
                && hasUserAgreement
                && hasVersionSelection) {
            if (!isAnnouncementCheckDueToday(ctx)) {
                return;
            }
            Activity activity = Context != null ? Context : currentActivity;
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                AnnouncementManager announcementManager = new AnnouncementManager(activity);
                announcementManager.checkAndShowAnnouncement();
            }
        } else {
            bvLog("[BetterVia] 用户未完成初始设置，暂不显示公告");
        }
    }

    static boolean isAnnouncementCheckDueToday(final Context ctx) {
        try {
            String today =
                    new SimpleDateFormat("yyyyMMdd", Locale.US)
                            .format(new Date(TimeProvider.now()));
            String lastCheckDate = getPrefString(ctx, KEY_LAST_ANNOUNCEMENT_CHECK_DATE, "");
            if (today.equals(lastCheckDate)) {
                return false;
            }
            putPrefString(ctx, KEY_LAST_ANNOUNCEMENT_CHECK_DATE, today);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public static int getBgColorStatic(Context ctx) {
        return ThemeColors.getBgColor(ctx);
    }

    public static int getTitleColorStatic(Context ctx) {
        return ThemeColors.getTitleColor(ctx);
    }

    public static int getTextColorStatic(Context ctx) {
        return ThemeColors.getTextColor(ctx);
    }

    public static int getHintColorStatic(Context ctx) {
        return ThemeColors.getHintColor(ctx);
    }

    public static int getDividerColorStatic(Context ctx) {
        return ThemeColors.getDividerColor(ctx);
    }

    public static int getBtnBgColorStatic(Context ctx) {
        return ThemeColors.getBtnBgColor(ctx);
    }

    public static int getBtnTextColorStatic(Context ctx) {
        return ThemeColors.getBtnTextColor(ctx);
    }

    public static int getItemBgColorStatic(Context ctx) {
        return ThemeColors.getItemBgColor(ctx);
    }

    public static int getOkBtnBgColorStatic(Context ctx) {
        return ThemeColors.getOkBtnBgColor(ctx);
    }

    public static int getOkBtnTextColorStatic(Context ctx) {
        return ThemeColors.getOkBtnTextColor(ctx);
    }

    public static boolean isDarkTheme(Context ctx) {
        return ThemeColors.isDark(ctx);
    }

    public static String getCurrentLanguageCodeStatic(Context ctx) {
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
            }
            return "en";
        }
        return saved;
    }

    public static void showJiguroToast(Context ctx, String msg) {
        if (ctx == null || msg == null) return;
        try {
            boolean useCustom = getPrefBoolean(ctx, KEY_CUSTOM_TOAST, true);
            final Context appCtx = ctx.getApplicationContext();
            if (appCtx == null) return;

            int duration = msg.length() > 20 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;

            if (useCustom) {
                try {
                    LinearLayout container = new LinearLayout(appCtx);
                    container.setOrientation(LinearLayout.HORIZONTAL);
                    container.setGravity(Gravity.CENTER);
                    GradientDrawable bg = new GradientDrawable();
                    bg.setColor(0xCC1E1E1E);
                    float density = appCtx.getResources().getDisplayMetrics().density;
                    bg.setCornerRadius(22 * density + 0.5f);
                    container.setBackgroundDrawable(bg);
                    int pad = (int) (18 * density + 0.5f);
                    int vPad = (int) (14 * density + 0.5f);
                    container.setPadding(pad, vPad, pad, vPad);
                    TextView textView = new TextView(appCtx);
                    textView.setText(msg);
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    textView.setGravity(Gravity.CENTER);
                    textView.setMaxWidth((int) (280 * density + 0.5f));
                    container.addView(textView);
                    Toast toast = new Toast(appCtx);
                    toast.setView(container);
                    toast.setDuration(duration);
                    toast.setGravity(Gravity.BOTTOM, 0, (int) (122 * density + 0.5f));
                    toast.show();
                    return;
                } catch (Exception ignored) {
                }
            }
            Toast.makeText(appCtx, msg, duration).show();
        } catch (Exception e) {
            try {
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            } catch (Exception ignored) {
            }
        }
    }
}
