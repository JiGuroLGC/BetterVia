package com.jiguro.bettervia;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LongPressMenuHelper {

    public static final String KEY_ENABLE = "long_press_menu_down";

    private static final float GAP_DP = 8f;
    private static final float EDGE_DP = 4f;

    private static boolean sInitialized = false;

    private static final ThreadLocal<Integer> sAnchorDepth = new ThreadLocal<Integer>();

    private static final Map<Class<?>, Field> sDialogFieldCache = new HashMap<Class<?>, Field>();

    private LongPressMenuHelper() {}

    public static void init(final Context ctx, final ClassLoader cl) {
        if (sInitialized) {
            return;
        }
        sInitialized = true;
        installHook(ctx, cl);
    }

    private static void installHook(final Context ctx, final ClassLoader cl) {
        final String className =
                ViaClassMapping.getClassName(
                        ViaClassMapping.ClassMethodKey.LONG_PRESS_MENU_SHOW_AT, ctx);
        final String methodName =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.LONG_PRESS_MENU_SHOW_AT, ctx);
        if (className == null
                || className.isEmpty()
                || methodName == null
                || methodName.isEmpty()) {
            Hook.bvLog("[BetterVia] 长按菜单：类名/方法名未就绪，跳过 Hook");
            return;
        }
        try {
            XposedHelpers.findAndHookMethod(
                    className,
                    cl,
                    methodName,
                    int.class,
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                if (isAnchoredCall()) {
                                    return;
                                }
                                if (!Hook.getPrefBoolean(ctx, KEY_ENABLE, false)) {
                                    return;
                                }
                                Integer fingerY = (Integer) param.args[1];
                                if (fingerY == null) {
                                    return;
                                }
                                applyAtPoint(ctx, param.thisObject, fingerY);
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 长按菜单定位失败: " + t);
                            }
                        }
                    });
            Hook.bvLog("[BetterVia] 长按菜单 Hook 已挂载: " + className + "." + methodName + "(int,int)");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 长按菜单 Hook 挂载失败: " + t);
            return;
        }
        flagAnchorCalls(
                ctx, cl, ViaClassMapping.ClassMethodKey.LONG_PRESS_MENU_ANCHOR_VIEW, View.class);
        flagAnchorCalls(
                ctx,
                cl,
                ViaClassMapping.ClassMethodKey.LONG_PRESS_MENU_ANCHOR_VIEW_XY,
                View.class,
                int.class,
                int.class);
    }

    private static void flagAnchorCalls(
            final Context ctx,
            final ClassLoader cl,
            final ViaClassMapping.ClassMethodKey key,
            Class<?>... params) {
        String className = ViaClassMapping.getClassName(key, ctx);
        String methodName = ViaClassMapping.getMethodName(key, ctx);
        if (className == null || methodName == null || methodName.isEmpty()) {
            return;
        }
        XC_MethodHook flag =
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        pushAnchor();
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        popAnchor();
                    }
                };
        Object[] args = new Object[params.length + 1];
        System.arraycopy(params, 0, args, 0, params.length);
        args[params.length] = flag;
        try {
            XposedHelpers.findAndHookMethod(className, cl, methodName, args);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 长按菜单锚定标记挂载失败(" + methodName + "): " + t);
        }
    }

    private static void applyAtPoint(Context ctx, Object builder, int fingerY) {
        Dialog dialog = findDialog(builder);
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        View decor = window.getDecorView();
        if (decor == null) {
            return;
        }
        int menuHeight = measuredHeight(decor);
        if (menuHeight <= 0) {
            return;
        }

        int[] frame = resolveFrame(ctx, decor, builder);
        int targetTop =
                clampTop(
                        fingerY + dp(ctx, GAP_DP),
                        menuHeight,
                        frame[0],
                        frame[1],
                        dp(ctx, EDGE_DP));

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = horizontalGravity(lp.gravity) | Gravity.TOP;
        lp.y = targetTop - frame[0];
        window.setAttributes(lp);

        Hook.bvLog(
                "[BetterVia] 长按菜单: fingerY="
                        + fingerY
                        + " 菜单高="
                        + menuHeight
                        + " 框架=["
                        + frame[0]
                        + ","
                        + frame[1]
                        + "] 目标顶="
                        + targetTop);
    }

    private static int clampTop(
            int desiredTop, int menuHeight, int frameTop, int frameBottom, int margin) {
        int min = frameTop + margin;
        int max = frameBottom - menuHeight - margin;
        if (max < min) {
            max = min;
        }
        if (desiredTop < min) {
            return min;
        }
        if (desiredTop > max) {
            return max;
        }
        return desiredTop;
    }

    private static int[] resolveFrame(Context ctx, View decor, Object builder) {
        int screenHeight = ctx.getResources().getDisplayMetrics().heightPixels;
        int top = 0;
        int bottom = screenHeight;
        boolean resolved = false;

        if (Build.VERSION.SDK_INT >= 23) {
            WindowInsets insets = decor.getRootWindowInsets();
            if (insets != null) {
                int insetTop;
                int insetBottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    insetTop = insets.getInsets(WindowInsets.Type.statusBars()).top;
                    insetBottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                } else {
                    insetTop = insets.getSystemWindowInsetTop();
                    insetBottom = insets.getSystemWindowInsetBottom();
                }
                top = insetTop;
                bottom = screenHeight - insetBottom;
                resolved = bottom - top > 0;
            }
        }

        if (!resolved && builder != null) {
            try {
                int contentTop =
                        XposedHelpers.getIntField(
                                builder,
                                ViaClassMapping.getMethodName(
                                        ViaClassMapping.ClassMethodKey
                                                .LONG_PRESS_MENU_FIELD_CONTENT_TOP,
                                        ctx));
                int contentHeight =
                        XposedHelpers.getIntField(
                                builder,
                                ViaClassMapping.getMethodName(
                                        ViaClassMapping.ClassMethodKey
                                                .LONG_PRESS_MENU_FIELD_CONTENT_HEIGHT,
                                        ctx));
                if (contentHeight > 0) {
                    top = contentTop;
                    bottom = contentTop + contentHeight;
                    resolved = true;
                }
            } catch (Throwable ignored) {
            }
        }

        if (!resolved) {
            top = 0;
            bottom = screenHeight;
        }
        return new int[] {top, bottom};
    }

    private static int measuredHeight(View decor) {
        int height = decor.getMeasuredHeight();
        return height > 0 ? height : decor.getHeight();
    }

    private static int horizontalGravity(int gravity) {
        int horizontal = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        return horizontal == 0 ? Gravity.LEFT : horizontal;
    }

    private static int dp(Context ctx, float value) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }

    private static Dialog findDialog(Object builder) {
        if (builder == null) {
            return null;
        }
        Class<?> cls = builder.getClass();
        Field cached;
        synchronized (sDialogFieldCache) {
            cached = sDialogFieldCache.get(cls);
        }
        if (cached != null) {
            try {
                return (Dialog) cached.get(builder);
            } catch (Throwable ignored) {
            }
        }
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (!Dialog.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object value = field.get(builder);
                    if (value instanceof Dialog) {
                        synchronized (sDialogFieldCache) {
                            sDialogFieldCache.put(cls, field);
                        }
                        return (Dialog) value;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return null;
    }

    private static void pushAnchor() {
        Integer depth = sAnchorDepth.get();
        sAnchorDepth.set(depth == null ? 1 : depth + 1);
    }

    private static void popAnchor() {
        Integer depth = sAnchorDepth.get();
        if (depth == null || depth <= 1) {
            sAnchorDepth.remove();
        } else {
            sAnchorDepth.set(depth - 1);
        }
    }

    private static boolean isAnchoredCall() {
        Integer depth = sAnchorDepth.get();
        return depth != null && depth > 0;
    }
}
