package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.lang.ref.WeakReference;

public class AutoRefreshHelper {

    public static final String KEY_ENABLE = "auto_refresh_enable";
    public static final String KEY_INTERVAL = "auto_refresh_interval";
    public static final String KEY_JITTER = "auto_refresh_jitter";

    private static final java.util.Random RANDOM = new java.util.Random();
    private static final float JITTER_RATIO = 0.2f;

    private static final int DEFAULT_SECONDS = 30;
    private static final int MIN_SECONDS = 1;
    private static final int MAX_SECONDS = 86400;

    private static final int[] PRESET_SECONDS = {5, 10, 30, 60, 120, 300, 600};
    private static final String[] PRESET_KEYS = {
        "auto_refresh_5s",
        "auto_refresh_10s",
        "auto_refresh_30s",
        "auto_refresh_1m",
        "auto_refresh_2m",
        "auto_refresh_5m",
        "auto_refresh_10m"
    };

    private static boolean sInitialized = false;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static final Rect TEMP_RECT = new Rect();
    private static WeakReference<View> sReloadButton = new WeakReference<View>(null);
    private static WeakReference<Object> sUrlBar = new WeakReference<Object>(null);
    private static int sIntervalSeconds = 0;
    private static String sLoadingField = "p";
    private static String sScanField = "q";
    private static String sUrlBarClassName = null;
    private static String sReloadFieldName = "k";

    private static final Runnable TICK =
            new Runnable() {
                @Override
                public void run() {
                    if (sIntervalSeconds <= 0) {
                        return;
                    }
                    final View button = sReloadButton.get();
                    if (!isUsable(button)) {
                        stop(false, null);
                        return;
                    }
                    if (!Hook.getPrefBoolean(button.getContext(), KEY_ENABLE, false)) {
                        stop(false, null);
                        return;
                    }
                    final Object urlBar = sUrlBar.get();
                    if (isScanMode(urlBar)) {
                        Hook.bvLog("[BetterVia] 定时刷新：当前为扫码按钮状态，停止刷新");
                        stop(false, null);
                        return;
                    }
                    if (isLoading(urlBar)) {
                        HANDLER.postDelayed(
                                this, nextDelayMillis(button.getContext(), sIntervalSeconds));
                        return;
                    }
                    try {
                        button.performClick();
                    } catch (Throwable t) {
                        Hook.bvLog("[BetterVia] 定时刷新执行失败: " + t);
                        stop(false, null);
                        return;
                    }
                    HANDLER.postDelayed(
                            this, nextDelayMillis(button.getContext(), sIntervalSeconds));
                }
            };

    private static long nextDelayMillis(Context ctx, int seconds) {
        try {
            if (ctx != null && Hook.getPrefBoolean(ctx, KEY_JITTER, false)) {
                int delta = Math.max(1, Math.round(seconds * JITTER_RATIO));
                int offset = RANDOM.nextInt(delta * 2 + 1) - delta;
                int actual = Math.max(1, seconds + offset);
                Hook.bvLogVerbose("[BetterVia] 定时刷新抖动: " + seconds + "s -> " + actual + "s");
                return actual * 1000L;
            }
        } catch (Throwable ignored) {
        }
        return seconds * 1000L;
    }

    private AutoRefreshHelper() {}

    public static void init(final Context ctx, final ClassLoader cl) {
        if (sInitialized) {
            return;
        }
        sInitialized = true;

        final String className =
                ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.ADDRESS_BAR_CLASS, ctx);
        final String setupMethod =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.ADDRESS_BAR_CLASS, ctx);
        final String reloadField =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.ADDRESS_BAR_FIELD_RELOAD, ctx);
        sLoadingField =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.ADDRESS_BAR_FIELD_LOADING, ctx);
        sScanField =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.ADDRESS_BAR_FIELD_SCAN_BUTTON, ctx);
        if (className == null
                || className.isEmpty()
                || setupMethod == null
                || setupMethod.isEmpty()) {
            Hook.bvLog("[BetterVia] 定时刷新：地址栏类名/方法名未就绪，跳过 Hook");
            return;
        }
        sUrlBarClassName = className;
        sReloadFieldName = reloadField;
        try {
            XposedHelpers.findAndHookMethod(
                    className,
                    cl,
                    setupMethod,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                Object reloadButton =
                                        XposedHelpers.getObjectField(param.thisObject, reloadField);
                                if (reloadButton instanceof View) {
                                    installLongClick(param.thisObject, (View) reloadButton);
                                }
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 定时刷新：获取刷新图标失败: " + t);
                            }
                        }
                    });
            Hook.bvLog("[BetterVia] 定时刷新 Hook 已挂载: " + className + "." + setupMethod);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 定时刷新 Hook 挂载失败: " + t);
        }

        try {
            attachInActivity(Hook.peekCurrentActivity());
            ActivityLifecycleHub.add(
                    "auto_refresh_attach",
                    new ActivityLifecycleHub.Listener() {
                        @Override
                        public void onActivityCreated(
                                final Activity activity, Bundle savedInstanceState) {
                            HANDLER.postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            attachInActivity(activity);
                                        }
                                    },
                                    400);
                        }
                    },
                    true);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 定时刷新：补挂监听注册失败: " + t);
        }
    }

    private static void attachInActivity(Activity activity) {
        if (activity == null || sUrlBarClassName == null) {
            return;
        }
        try {
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            if (activity.getWindow() == null) {
                return;
            }
            View root = activity.getWindow().getDecorView();
            if (!(root instanceof ViewGroup)) {
                return;
            }
            View urlBar = findAddressBar((ViewGroup) root, 0);
            if (urlBar == null) {
                return;
            }
            Object reload = XposedHelpers.getObjectField(urlBar, sReloadFieldName);
            if (reload instanceof View) {
                installLongClick(urlBar, (View) reload);
                Hook.bvLogVerbose("[BetterVia] 定时刷新：已补挂长按监听");
            }
        } catch (Throwable t) {
            Hook.bvLogVerbose("[BetterVia] 定时刷新：补挂失败: " + t);
        }
    }

    private static View findAddressBar(ViewGroup group, int depth) {
        if (depth > 32) {
            return null;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child == null) {
                continue;
            }
            if (sUrlBarClassName.equals(child.getClass().getName())) {
                return child;
            }
            if (child instanceof ViewGroup) {
                View found = findAddressBar((ViewGroup) child, depth + 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void installLongClick(final Object urlBar, final View reloadButton) {
        reloadButton.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            if (!Hook.getPrefBoolean(v.getContext(), KEY_ENABLE, false)) {
                                return true;
                            }
                            if (isScanMode(urlBar)) {
                                return true;
                            }
                            showIntervalDialog(urlBar, v);
                        } catch (Throwable t) {
                            Hook.bvLog("[BetterVia] 定时刷新长按处理失败: " + t);
                        }
                        return true;
                    }
                });
    }

    private static void showIntervalDialog(final Object urlBar, final View reloadButton) {
        final Activity act = findActivity(reloadButton.getContext());
        if (act == null) {
            return;
        }
        final boolean running = sIntervalSeconds > 0;
        final int currentSeconds =
                sIntervalSeconds > 0
                        ? sIntervalSeconds
                        : Hook.getPrefInt(act, KEY_INTERVAL, DEFAULT_SECONDS);

        final int presetCount = PRESET_SECONDS.length;
        String[] keys = new String[presetCount + 2];
        keys[0] = "auto_refresh_off";
        for (int i = 0; i < presetCount; i++) {
            keys[i + 1] = PRESET_KEYS[i];
        }
        keys[presetCount + 1] = "auto_refresh_custom";

        int selectedIndex = 0;
        if (running) {
            selectedIndex = -1;
            for (int i = 0; i < presetCount; i++) {
                if (PRESET_SECONDS[i] == currentSeconds) {
                    selectedIndex = i + 1;
                    break;
                }
            }
        }

        SettingsUI.showSelectDialog(
                act,
                "auto_refresh_title",
                keys,
                selectedIndex,
                "auto_refresh_advanced",
                "auto_refresh_jitter_switch",
                Hook.getPrefBoolean(act, KEY_JITTER, false),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Hook.putPrefBoolean(act, KEY_JITTER, isChecked);
                    }
                },
                new SettingsUI.OnSelectListener() {
                    @Override
                    public void onSelect(int index) {
                        if (index == 0) {
                            stop(running, act);
                            return;
                        }
                        if (index - 1 < presetCount) {
                            start(urlBar, reloadButton, PRESET_SECONDS[index - 1]);
                            return;
                        }
                        showCustomIntervalDialog(act, urlBar, reloadButton, currentSeconds);
                    }
                });
    }

    private static void showCustomIntervalDialog(
            final Activity act, final Object urlBar, final View reloadButton, int currentSeconds) {
        SettingsUI.showInputDialog(
                act,
                "auto_refresh_custom_title",
                "auto_refresh_custom_message",
                "auto_refresh_custom_hint",
                String.valueOf(currentSeconds),
                1,
                "dialog_ok",
                "dialog_cancel",
                new SettingsUI.OnInputListener() {
                    @Override
                    public void onConfirm(String input) {
                        int seconds = parseSeconds(input);
                        if (seconds < MIN_SECONDS || seconds > MAX_SECONDS) {
                            Hook.jiguroMessageWithContext(
                                    act,
                                    LocalizedStringProvider.getInstance()
                                            .get(act, "auto_refresh_invalid"));
                            return;
                        }
                        start(urlBar, reloadButton, seconds);
                    }

                    @Override
                    public void onCancel() {}
                });
    }

    private static void start(Object urlBar, View reloadButton, int seconds) {
        Context ctx = reloadButton.getContext();
        sUrlBar = new WeakReference<Object>(urlBar);
        sReloadButton = new WeakReference<View>(reloadButton);
        sIntervalSeconds = seconds;
        HANDLER.removeCallbacks(TICK);
        HANDLER.postDelayed(TICK, nextDelayMillis(ctx, seconds));
        Hook.putPrefInt(ctx, KEY_INTERVAL, seconds);
        Hook.jiguroMessageWithContext(
                ctx,
                String.format(
                        LocalizedStringProvider.getInstance().get(ctx, "auto_refresh_started"),
                        seconds));
        Hook.bvLog("[BetterVia] 定时刷新已开启: " + seconds + " 秒");
    }

    private static void stop(boolean notify, Context ctx) {
        boolean wasRunning = sIntervalSeconds > 0;
        HANDLER.removeCallbacks(TICK);
        sIntervalSeconds = 0;
        sReloadButton.clear();
        sUrlBar.clear();
        if (wasRunning) {
            Hook.bvLog("[BetterVia] 定时刷新已停止");
        }
        if (notify && ctx != null) {
            Hook.jiguroMessageWithContext(
                    ctx, LocalizedStringProvider.getInstance().get(ctx, "auto_refresh_stopped"));
        }
    }

    private static boolean isUsable(View view) {
        if (view == null || !view.isAttachedToWindow() || !view.isShown()) {
            return false;
        }
        if (view.getWindowVisibility() != View.VISIBLE) {
            return false;
        }
        return view.getGlobalVisibleRect(TEMP_RECT);
    }

    private static boolean isScanMode(Object urlBar) {
        if (urlBar == null) {
            return false;
        }
        try {
            return XposedHelpers.getBooleanField(urlBar, sScanField);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean isLoading(Object urlBar) {
        if (urlBar == null) {
            return false;
        }
        try {
            return XposedHelpers.getBooleanField(urlBar, sLoadingField);
        } catch (Throwable t) {
            return false;
        }
    }

    private static int parseSeconds(String input) {
        if (input == null) {
            return -1;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Activity findActivity(Context ctx) {
        Context context = ctx;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
