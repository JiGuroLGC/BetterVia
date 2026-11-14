package com.jiguro.bettervia;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ComponentBlockHelper {

    public static final String[] KEYS = {
        "block_update",
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

    private static final Object LOCK = new Object();

    private static final List<XC_MethodHook.Unhook> sHooks = new ArrayList<XC_MethodHook.Unhook>();

    private static boolean sInstalled = false;

    private static final ThreadLocal<int[]> TL_DEPTH =
            new ThreadLocal<int[]>() {
                @Override
                protected int[] initialValue() {
                    return new int[1];
                }
            };

    private ComponentBlockHelper() {}

    public static void init(Context ctx, ClassLoader cl) {
        refresh(ctx, cl);
    }

    public static void refresh(Context ctx, ClassLoader cl) {
        if (computeAnyBlocked(ctx)) {
            install(ctx, cl);
        } else {
            uninstall();
        }
    }

    private static boolean computeAnyBlocked(Context ctx) {
        for (String key : KEYS) {
            if (Hook.getPrefBoolean(ctx, key, false)) return true;
        }
        return false;
    }

    private static void install(final Context ctx, final ClassLoader cl) {
        synchronized (LOCK) {
            if (sInstalled) return;
            final String blockClassName =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.COMPONENT_BLOCK_CLASS, ctx);
            final String checkClassName =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.COMPONENT_CHECK_METHOD, ctx);
            final String checkMethodName =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.COMPONENT_CHECK_METHOD, ctx);
            if (blockClassName == null
                    || blockClassName.isEmpty()
                    || checkClassName == null
                    || checkClassName.isEmpty()
                    || checkMethodName == null
                    || checkMethodName.isEmpty()) {
                Hook.bvLog("[BetterVia] 组件屏蔽：缺少类名映射，跳过");
                return;
            }

            boolean checkHooked = false;
            boolean returnsList = false;
            try {
                Class<?> holder = Class.forName(checkClassName, false, cl);
                for (Method m : holder.getDeclaredMethods()) {
                    if (m.getName().equals(checkMethodName)
                            && List.class.isAssignableFrom(m.getReturnType())) {
                        returnsList = true;
                        break;
                    }
                }
                sHooks.addAll(
                        XposedBridge.hookAllMethods(
                                holder,
                                checkMethodName,
                                new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) {
                                        TL_DEPTH.get()[0]++;
                                    }

                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) {
                                        int[] depth = TL_DEPTH.get();
                                        if (depth[0] > 0) depth[0]--;
                                        filterComponents(param.getResult(), blockClassName, ctx);
                                        if (param.args != null) {
                                            for (Object arg : param.args) {
                                                filterComponents(arg, blockClassName, ctx);
                                            }
                                        }
                                    }
                                }));
                checkHooked = !sHooks.isEmpty();
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] 组件屏蔽：组件列表方法不可用，降级为旧实现: " + t);
            }

            if (!checkHooked) {
                installLegacyHook(ctx, blockClassName, checkClassName, checkMethodName);
            }

            if (checkHooked && !returnsList) {
                try {
                    sHooks.add(
                            XposedHelpers.findAndHookMethod(
                                    "java.util.ArrayList",
                                    null,
                                    "add",
                                    Object.class,
                                    new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param) {
                                            if (TL_DEPTH.get()[0] <= 0) return;
                                            if (!shouldBlock(param.args[0], blockClassName, ctx))
                                                return;
                                            param.setResult(Boolean.FALSE);
                                        }
                                    }));
                } catch (Throwable t) {
                    Hook.bvLog("[BetterVia] 组件屏蔽：ArrayList.add 拦截安装失败: " + t);
                }
            }

            sInstalled = !sHooks.isEmpty();
            Hook.bvLog("[BetterVia] 组件屏蔽已启用（" + sHooks.size() + " 处 hook）");
        }
    }

    private static void installLegacyHook(
            final Context ctx,
            final String blockClassName,
            final String checkClassName,
            final String checkMethodName) {
        try {
            sHooks.add(
                    XposedHelpers.findAndHookMethod(
                            "java.util.ArrayList",
                            null,
                            "add",
                            Object.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) {
                                    if (!isCalledFrom(checkClassName, checkMethodName)) return;
                                    if (!shouldBlock(param.args[0], blockClassName, ctx)) return;
                                    param.setResult(Boolean.FALSE);
                                }
                            }));
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 组件屏蔽：旧实现安装失败: " + t);
        }
    }

    private static boolean isCalledFrom(String checkClassName, String checkMethodName) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stack) {
            if (checkClassName.equals(el.getClassName())
                    && checkMethodName.equals(el.getMethodName())) return true;
        }
        return false;
    }

    private static void uninstall() {
        synchronized (LOCK) {
            if (!sInstalled && sHooks.isEmpty()) return;
            for (XC_MethodHook.Unhook h : sHooks) {
                try {
                    h.unhook();
                } catch (Throwable ignored) {
                }
            }
            sHooks.clear();
            sInstalled = false;
            Hook.bvLog("[BetterVia] 组件屏蔽已停用");
        }
    }

    private static void filterComponents(Object target, String blockClassName, Context ctx) {
        if (!(target instanceof List)) return;
        List<?> list = (List<?>) target;
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (shouldBlock(list.get(i), blockClassName, ctx)) {
                    list.remove(i);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static boolean shouldBlock(Object item, String blockClassName, Context ctx) {
        if (item == null) return false;
        if (!blockClassName.equals(item.getClass().getName())) return false;
        try {
            int index = mapTypeToIndex(XposedHelpers.getIntField(item, "b"));
            if (index < 0) return false;
            if (!Hook.getPrefBoolean(ctx, KEYS[index], false)) return false;
            Hook.bvLog("[BetterVia] 组件屏蔽：隐藏 " + KEYS[index]);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static int mapTypeToIndex(int type) {
        switch (type) {
            case 12:
                return 0;
            case 5:
                return 1;
            case 6:
                return 2;
            case 13:
                return 3;
            case 14:
                return 4;
            case 7:
                return 5;
            case 4:
                return 6;
            case 2:
                return 7;
            case 3:
                return 8;
            case 1:
                return 9;
            case 16:
                return 10;
            default:
                return -1;
        }
    }
}
