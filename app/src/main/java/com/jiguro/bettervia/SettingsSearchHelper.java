package com.jiguro.bettervia;

import android.content.Context;
import android.os.Bundle;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SettingsSearchHelper {

    private static final int TARGET_ID_BASE = 1000;

    private static final String[] KEYS_BASIC = {
        "whitelist_switch",
        "keep_screen_on_switch",
        "download_dialog_share_switch",
        "show_url_scheme_switch",
        "hide_status_bar_switch",
        "perfect_exit_switch",
        "auto_refresh_switch",
        "restore_old_search_box_switch"
    };
    private static final String[] KEYS_APPEARANCE = {
        "eye_protection_switch",
        "eye_protection_config",
        "monet_title",
        "component_block_title",
        "homepage_theme_title",
        "homepage_bg_title"
    };
    private static final String[] KEYS_PRIVACY = {
        "block_google_switch",
        "screenshot_protection_switch",
        "boss_gesture_title",
        "random_ua_title",
        "privacy_lock_title",
        "user_sandbox_title"
    };
    private static final String[] KEYS_PLAYBACK = {"long_press_speed_switch", "free_zoom_switch"};
    private static final String[] KEYS_REPOSITORY = {
        "script_repository_title",
        "ad_block_rules_title",
        "search_commands_title",
        "user_agent_title",
        "shisui_title"
    };
    private static final String[] KEYS_OTHER = {
        "cookie_management_title",
        "startup_execution_title",
        "online_preview_title",
        "url_correction_title",
        "long_press_menu_page_title",
        "list_search_page_title"
    };
    private static final String[] KEYS_MODULE = {
        "version_selector_title",
        "language_title",
        "network_source_title",
        "module_theme_title",
        "storage_item_title",
        "auto_update_switch",
        "block_startup_message_switch",
        "custom_toast_switch",
        "disable_page_ripple_switch",
        "developer_mode_switch"
    };
    private static final String[] KEYS_ABOUT = {
        "about_check_update",
        "about_github_repo",
        "about_xposed_repo",
        "about_author_blog",
        "about_email",
        "about_telegram",
        "about_download",
        "about_changelog",
        "about_faq",
        "about_user_agreement",
        "about_credits",
        "about_thanks_title",
        "about_withdraw"
    };

    private static final String[] PAGE_TITLE_KEYS = {
        "category_basic",
        "category_appearance",
        "category_privacy",
        "category_playback",
        "category_repository",
        "category_other",
        "category_module",
        "about_title"
    };
    private static final String[][] PAGE_KEYS = {
        KEYS_BASIC,
        KEYS_APPEARANCE,
        KEYS_PRIVACY,
        KEYS_PLAYBACK,
        KEYS_REPOSITORY,
        KEYS_OTHER,
        KEYS_MODULE,
        KEYS_ABOUT
    };

    private static final Map<Object, Target> sEntryTargets =
            Collections.synchronizedMap(new IdentityHashMap<Object, Target>());

    private static final class Target {
        final int pageId;
        final String titleKey;

        Target(int pageId, String titleKey) {
            this.pageId = pageId;
            this.titleKey = titleKey;
        }
    }

    private static boolean sInstalled = false;
    private static volatile boolean sEnabled = false;
    private static Hook sHook;

    private SettingsSearchHelper() {}

    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
    }

    public static void init(final Context ctx, final ClassLoader cl, final Hook hook) {
        if (sInstalled) {
            return;
        }
        if (!Hook.isViaVersionAtLeast(20261113)) {
            Hook.bvLog("[BetterVia] 设置项搜索：宿主版本无搜索框，跳过注入");
            return;
        }
        sInstalled = true;
        sHook = hook;

        final String indexClass =
                ViaClassMapping.getClassName(
                        ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_INDEX, ctx);
        final String indexMethod =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_INDEX, ctx);
        final String clickClass =
                ViaClassMapping.getClassName(
                        ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_RESULT_CLICK, ctx);
        final String clickMethod =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_RESULT_CLICK, ctx);
        final String resultsField =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_RESULTS_FIELD, ctx);

        if (isEmpty(indexClass)
                || isEmpty(indexMethod)
                || isEmpty(clickClass)
                || isEmpty(clickMethod)) {
            Hook.bvLog("[BetterVia] 设置项搜索：符号未就绪，跳过注入");
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    indexClass,
                    cl,
                    indexMethod,
                    Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if (!sEnabled) {
                                return;
                            }
                            try {
                                Object result = param.getResult();
                                if (!(result instanceof List)) {
                                    return;
                                }
                                @SuppressWarnings("unchecked")
                                List<Object> list = (List<Object>) result;
                                sEntryTargets.clear();
                                injectEntries(ctx, cl, clickClass, list);
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 设置项搜索注入失败: " + t);
                            }
                        }
                    });
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] Hook 设置搜索索引失败: " + t);
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    clickClass,
                    cl,
                    clickMethod,
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                if (sEntryTargets.isEmpty()
                                        || resultsField == null
                                        || !(param.args[0] instanceof Integer)) {
                                    return;
                                }
                                int pos = ((Integer) param.args[0]).intValue();
                                Object listObj =
                                        XposedHelpers.getObjectField(
                                                param.thisObject, resultsField);
                                if (!(listObj instanceof List)) {
                                    return;
                                }
                                List<?> results = (List<?>) listObj;
                                if (pos < 0 || pos >= results.size()) {
                                    return;
                                }
                                Target target = sEntryTargets.get(results.get(pos));
                                if (target == null) {
                                    return;
                                }
                                param.setResult(null);
                                if (sHook != null) {
                                    sHook.openSettingsPageForSearch(
                                            ctx, target.pageId, target.titleKey);
                                }
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 设置项搜索点击处理失败: " + t);
                            }
                        }
                    });
            Hook.bvLog("[BetterVia] 设置项搜索注入已挂载");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] Hook 设置搜索结果点击失败: " + t);
        }
    }

    private static void injectEntries(
            Context ctx, ClassLoader cl, String fallbackClass, List<Object> out) throws Throwable {
        String root = LocalizedStringProvider.getInstance().get(ctx, "module_settings");
        if (isEmpty(root)) {
            root = "BetterVia";
        }
        int id = TARGET_ID_BASE;
        for (int p = 0; p < PAGE_KEYS.length; p++) {
            String pageTitle = LocalizedStringProvider.getInstance().get(ctx, PAGE_TITLE_KEYS[p]);
            for (int k = 0; k < PAGE_KEYS[p].length; k++) {
                String title = LocalizedStringProvider.getInstance().get(ctx, PAGE_KEYS[p][k]);
                if (isEmpty(title)) {
                    continue;
                }
                Object entry =
                        createEntry(
                                ctx,
                                cl,
                                fallbackClass,
                                title,
                                new String[] {root, pageTitle},
                                new String[] {pageTitle},
                                id++);
                if (entry != null) {
                    out.add(entry);
                    sEntryTargets.put(entry, new Target(p + 1, PAGE_KEYS[p][k]));
                }
            }
        }
        String infoTitle = LocalizedStringProvider.getInstance().get(ctx, "info_title");
        if (!isEmpty(infoTitle)) {
            Object entry =
                    createEntry(
                            ctx,
                            cl,
                            fallbackClass,
                            infoTitle,
                            new String[] {root},
                            new String[] {infoTitle},
                            id++);
            if (entry != null) {
                out.add(entry);
                sEntryTargets.put(entry, new Target(9, null));
            }
        }
    }

    private static Object createEntry(
            Context ctx,
            ClassLoader cl,
            String fallbackClass,
            String title,
            String[] breadcrumb,
            String[] keywords,
            int id) {
        try {
            String itemClassName =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.SETTINGS_SEARCH_ITEM_CLASS, ctx);
            if (isEmpty(itemClassName)) {
                return null;
            }
            Class<?> itemClass = XposedHelpers.findClass(itemClassName, cl);
            Class<?> target = XposedHelpers.findClass(fallbackClass, cl);
            Constructor<?> ctor =
                    XposedHelpers.findConstructorExact(
                            itemClass,
                            String.class,
                            String.class,
                            String[].class,
                            Class.class,
                            Bundle.class,
                            int.class,
                            String[].class);
            return ctor.newInstance(
                    title, title, breadcrumb, target, null, Integer.valueOf(id), keywords);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 构造搜索条目失败: " + t);
            return null;
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
