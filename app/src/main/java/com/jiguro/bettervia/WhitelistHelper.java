package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WhitelistHelper {

    private WhitelistHelper() {}

    private static final class Entry {
        final String prefKey;
        final ViaClassMapping.ClassMethodKey key;
        final boolean voidResult;

        Entry(String prefKey, ViaClassMapping.ClassMethodKey key, boolean voidResult) {
            this.prefKey = prefKey;
            this.key = key;
            this.voidResult = voidResult;
        }
    }

    public static final String KEY_CONFIG = Hook.KEY_WHITELIST;
    public static final String KEY_CORE = "wl_bypass_core";
    public static final String KEY_SNIFF = "wl_bypass_sniff";
    public static final String KEY_DOWNLOAD = "wl_bypass_download";
    public static final String KEY_SCRIPT = "wl_bypass_script";
    public static final String KEY_AD = "wl_bypass_ad";

    private static final Entry[] ENTRIES = {
        new Entry(KEY_CONFIG, ViaClassMapping.ClassMethodKey.VIA_CHECK_CLASS, true),
        new Entry(KEY_CORE, ViaClassMapping.ClassMethodKey.WHITELIST_CORE, false),
        new Entry(KEY_SNIFF, ViaClassMapping.ClassMethodKey.WHITELIST_SNIFF, false),
        new Entry(KEY_DOWNLOAD, ViaClassMapping.ClassMethodKey.WHITELIST_DOWNLOAD, false),
        new Entry(KEY_SCRIPT, ViaClassMapping.ClassMethodKey.WHITELIST_SCRIPT, false),
        new Entry(KEY_AD, ViaClassMapping.ClassMethodKey.WHITELIST_AD, false)
    };

    private static final Map<String, List<XC_MethodHook.Unhook>> sHooks =
            new HashMap<String, List<XC_MethodHook.Unhook>>();

    public static void init(Context ctx, ClassLoader cl) {
        for (Entry e : ENTRIES) {
            apply(ctx, cl, e, Hook.getPrefBoolean(ctx, e.prefKey, true));
        }
    }

    public static void setEnabled(Context ctx, ClassLoader cl, String prefKey, boolean on) {
        Hook.putPrefBoolean(ctx, prefKey, on);
        for (Entry e : ENTRIES) {
            if (e.prefKey.equals(prefKey)) {
                apply(ctx, cl, e, on);
                return;
            }
        }
    }

    private static void apply(Context ctx, ClassLoader cl, Entry e, boolean on) {
        synchronized (sHooks) {
            if (!on) {
                List<XC_MethodHook.Unhook> handles = sHooks.remove(e.prefKey);
                if (handles != null) {
                    for (XC_MethodHook.Unhook h : handles) {
                        try {
                            h.unhook();
                        } catch (Throwable ignored) {
                        }
                    }
                    Hook.bvLog("[BetterVia] 白名单：已恢复 " + e.key.name());
                }
                return;
            }
            if (sHooks.containsKey(e.prefKey)) {
                return;
            }
            try {
                String clsName = ViaClassMapping.getClassName(e.key, ctx);
                String methodName = ViaClassMapping.getMethodName(e.key, ctx);
                if (clsName == null
                        || clsName.isEmpty()
                        || methodName == null
                        || methodName.isEmpty()) {
                    return;
                }
                Class<?> cls = Class.forName(clsName, false, cl);
                final Object result = e.voidResult ? null : Boolean.FALSE;
                XC_MethodHook hook =
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                param.setResult(result);
                            }
                        };
                List<XC_MethodHook.Unhook> handles = new ArrayList<XC_MethodHook.Unhook>();
                for (Method m : cls.getDeclaredMethods()) {
                    if (!m.getName().equals(methodName)) {
                        continue;
                    }
                    Class<?> ret = m.getReturnType();
                    if (e.voidResult
                            ? ret != void.class
                            : (ret != boolean.class && ret != Boolean.class)) {
                        continue;
                    }
                    try {
                        handles.add(XposedBridge.hookMethod(m, hook));
                    } catch (Throwable ignored) {
                    }
                }
                if (handles.isEmpty()) {
                    Hook.bvLog("[BetterVia] 白名单：未找到方法 " + clsName + "." + methodName);
                    return;
                }
                sHooks.put(e.prefKey, handles);
                Hook.bvLog(
                        "[BetterVia] 白名单：已屏蔽 "
                                + clsName
                                + "."
                                + methodName
                                + "（"
                                + handles.size()
                                + " 个重载）");
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] 白名单：安装失败 " + e.key.name() + " -> " + t);
            }
        }
    }

    public static void showPage(final Activity act, final Context ctx) {
        final ClassLoader cl = act.getClassLoader();
        SettingsUI.showPage(
                act,
                "whitelist_page_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, Activity act) {
                        SettingsList list = new SettingsList(act);
                        addSwitch(
                                list,
                                act,
                                cl,
                                "whitelist_config",
                                "whitelist_config_hint",
                                KEY_CONFIG);
                        addSwitch(list, act, cl, "whitelist_core", "whitelist_core_hint", KEY_CORE);
                        addSwitch(
                                list,
                                act,
                                cl,
                                "whitelist_sniff",
                                "whitelist_sniff_hint",
                                KEY_SNIFF);
                        addSwitch(
                                list,
                                act,
                                cl,
                                "whitelist_download",
                                "whitelist_download_hint",
                                KEY_DOWNLOAD);
                        addSwitch(
                                list,
                                act,
                                cl,
                                "whitelist_script",
                                "whitelist_script_hint",
                                KEY_SCRIPT);
                        addSwitch(list, act, cl, "whitelist_ad", "whitelist_ad_hint", KEY_AD);
                        content.addView(list);
                    }
                });
    }

    private static void addSwitch(
            SettingsList list,
            Activity act,
            final ClassLoader cl,
            String titleKey,
            String hintKey,
            final String prefKey) {
        list.addSwitchItem(
                titleKey,
                hintKey,
                Hook.getPrefBoolean(act, prefKey, true),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        setEnabled(buttonView.getContext(), cl, prefKey, isChecked);
                    }
                });
    }
}
