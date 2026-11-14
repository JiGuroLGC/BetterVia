package com.jiguro.bettervia;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

import java.lang.reflect.*;

public final class ViaSystemUi {

    private static int fullscreenTagId = -1;

    private static Method viaApplyFullscreenMethod;

    private ViaSystemUi() {}

    public static void applyFullscreen(Window window, boolean fullscreen) {
        if (window == null) return;
        try {
            if (reflectViaApplyFullscreen(window, fullscreen)) {
                return;
            }
        } catch (Throwable t) {
        }
        try {
            applyFullscreenFallback(window, fullscreen);
        } catch (Throwable t) {
        }
    }

    public static void applyFullscreenModule(Window window, boolean fullscreen) {
        if (window == null) return;
        try {
            applyFullscreenFallback(window, fullscreen);
        } catch (Throwable t) {
        }
    }

    private static boolean reflectViaApplyFullscreen(Window window, boolean fullscreen)
            throws Throwable {
        Class<?> viaClass = findViaFullscreenClass(window);
        if (viaClass == null) return false;
        String methodName =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.WINDOW_FULLSCREEN_HELPER,
                        window.getContext());
        if (viaApplyFullscreenMethod == null
                || !viaApplyFullscreenMethod.getName().equals(methodName)) {
            viaApplyFullscreenMethod =
                    viaClass.getDeclaredMethod(methodName, Window.class, boolean.class);
            viaApplyFullscreenMethod.setAccessible(true);
        }
        viaApplyFullscreenMethod.invoke(null, window, Boolean.valueOf(fullscreen));
        return true;
    }

    private static void applyFullscreenFallback(Window window, boolean fullscreen) {
        View decor = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.getDecorView().setTag(getFullscreenTagId(), Boolean.valueOf(fullscreen));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT < 35) {
                window.setDecorFitsSystemWindows(false);
            }
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                if (fullscreen) {
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    controller.hide(WindowInsets.Type.systemBars());
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
                    }
                    controller.show(WindowInsets.Type.systemBars());
                }
            }
        } else {
            WindowManager.LayoutParams lp = window.getAttributes();
            if (fullscreen) {
                lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            } else {
                lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            }
            window.setAttributes(lp);
            int vis;
            if (!fullscreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vis = 0x1006;
            } else {
                vis = 0x1606;
            }
            if (fullscreen) {
                vis |= decor.getSystemUiVisibility();
            } else {
                vis = decor.getSystemUiVisibility() & ~vis;
            }
            decor.setSystemUiVisibility(vis);
        }
    }

    public static boolean isFullscreenByViaAppFlag(Context ctx) {
        return isFullscreenByWindow(ctx);
    }

    private static boolean isFullscreenByWindow(Context ctx) {
        try {
            if (!(ctx instanceof Activity)) {
                return false;
            }
            Window window = ((Activity) ctx).getWindow();
            if (window == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                View decor = window.getDecorView();
                if (decor != null) {
                    android.view.WindowInsets insets = decor.getRootWindowInsets();
                    if (insets != null) {
                        return !insets.isVisible(WindowInsets.Type.statusBars());
                    }
                    int vis = decor.getSystemUiVisibility();
                    if ((vis & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) return true;
                    if ((vis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) return true;
                }
                return false;
            } else {
                WindowManager.LayoutParams lp = window.getAttributes();
                if ((lp.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) return true;
                int vis = window.getDecorView().getSystemUiVisibility();
                if ((vis & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) return true;
                if ((vis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) return true;
                return false;
            }
        } catch (Throwable t) {
            return false;
        }
    }

    public static void applyIconAppearance(Window window, boolean lightIcons) {
        if (window == null || window.getDecorView() == null) return;
        try {
            View decor = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    int mask =
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                    | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                    controller.setSystemBarsAppearance(lightIcons ? mask : 0, mask);
                    return;
                }
            }
            int vis = decor.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vis =
                        lightIcons
                                ? (vis | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                                : (vis & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vis =
                        lightIcons
                                ? (vis | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                                : (vis & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            decor.setSystemUiVisibility(vis);
        } catch (Throwable t) {
        }
    }

    public static void updateSoftInputFlag(Activity act, boolean fullscreen) {
        if (act == null) return;
        try {
            ClassLoader cl = act.getClassLoader();
            if (cl == null) return;
            ViaClassMapping.ClassMethodKey[] keys = {
                ViaClassMapping.ClassMethodKey.SHELL_SOFT_INPUT_FLAG,
                ViaClassMapping.ClassMethodKey.CUSTOMTAB_SOFT_INPUT_FLAG
            };
            for (ViaClassMapping.ClassMethodKey key : keys) {
                try {
                    String hostClass = ViaClassMapping.getClassName(key, act);
                    String hostMethod = ViaClassMapping.getMethodName(key, act);
                    Class<?> c = Class.forName(hostClass, false, cl);
                    if (c == null || !c.isInstance(act)) continue;
                    Method m = c.getDeclaredMethod(hostMethod, boolean.class);
                    m.setAccessible(true);
                    m.invoke(act, Boolean.valueOf(fullscreen));
                    break;
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable t) {
        }
    }

    private static Class<?> findViaFullscreenClass(Window window) {
        try {
            Context ctx = window.getContext();
            if (ctx == null) return null;
            ClassLoader cl = ctx.getClassLoader();
            if (cl == null) return null;
            String className =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.WINDOW_FULLSCREEN_HELPER, ctx);
            return Class.forName(className, false, cl);
        } catch (Throwable t) {
            return null;
        }
    }

    private static int getFullscreenTagId() {
        if (fullscreenTagId < 0) {
            fullscreenTagId = View.generateViewId();
        }
        return fullscreenTagId;
    }
}
