package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

public class ListSearchEnhance {

    public static final String KEY_ENABLE_RULE = "list_search_rule_page";
    public static final String KEY_ENABLE_SCRIPT = "list_search_script_page";

    private static final String SEARCH_ICON_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAJwAAACcCAMAAAC9ZjJ/AAAAS1BMVEUAAAAjIyMjIyMjIyMmJiYiIiIjIyMiIiIfHx8jIyMiIiIiIiIjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMiIiIiIiIjIyMjIyMjIyMJnYphAAAAGHRSTlMA+fAUDeSpHQXYZjfLs5BxUEV8nIRbJ79GgM/bAAACsUlEQVR42u3Z266bMBAF0PENGwPmTvj/L60Yp0l1kjROH4ZU2us1SGyNxxMDBAAAAAAAAAAAAAAAAAAA8B/aYupa51zbpXWjL2Kapd7/5ObGBvoCwa8Xtf+kuridH89EjvZItb2hc22V3l/Rl4bOFG/R1KVvPBH5cerULV5Pp7GTysFctVq6M0NVq50ths5hkr7uzcH+3CXN4nLtZk+nWFTel6OhR2Gs8s+zpRP0+eYvF870mi9IJC/mZZsCvRL6vLQ9SWtqzrbS3wyae7IhWWbOo6Jo1FSeJIXBcb/Zd9dxY+oYSJDtjopcNnrH84WdJ0FjLkjJlVzilQRV3Eq2pMY87i4kZ+MRN5YVWR+l20jMcmSrA5UI3HULiWmP+61UZuVZF0iIP5pcGyoTeNZtJGTUH/V4l+sshCdrolKTaNPxWSmWF/oIN5MQ/l8dqFTDfxIkhEdwQ6X8cXlLLyDct4RL6vOeq6iE/G6Nors1qo/ulkQfJEb9URO1+QgjxPBTlS3eDwdDUj46BUW5zXo/BdWGSthW+NnVqPIbDjqfr+TMvFS+pHA8sRMJ2rgcfSg8XumRBNnEXdfQO9vRcaoyJCm/KulM0YNhPQSSFCbFJaG/42y7+Cs6c9nfd/q0X2eOtNHtXDtPr/ik9oNbLUkb6pxupOeaWe2Z6+XTrTqvWjL0yPc1Z2NuImlhdftB1fFnZWy8RtPtaa+tB3crTWNsCBzZmu32nU5Hc228zpO0sVO3T13VEodhiNN8FIvpbiCyOZ2qNpJmJo7ylJt8/s6jOV0nny40Se/P6NTY371Z550zkDzPpbl7nH+jy3kHOoGJVVs7zf9oWru6iv7ZTNwvnk5hmrWfUkpTv45PIjS8c9qv+vp/t82KX3F/Jz91iycAAAAAAAAAAAAAAAAAAAD4Z78ATQFF/VUFfGEAAAAASUVORK5CYII=";

    private static final WeakHashMap<Object, String> sKeywords = new WeakHashMap<Object, String>();

    private static final WeakHashMap<Object, Object> sToolbars = new WeakHashMap<Object, Object>();

    private static final WeakHashMap<Object, Boolean> sInjected =
            new WeakHashMap<Object, Boolean>();

    private static final List<PageSpec> sSpecs = new ArrayList<PageSpec>();
    private static Class<?> sToolbarCls;
    private static Class<?> sBtnCls;

    private static final class PageSpec {
        final String prefKey;
        final String fragCls;
        final String toolbarCbMethod;
        final String buildMethod;
        final String applyMethod;
        final String itemType;
        final String[] nameChain;

        PageSpec(
                String prefKey,
                String fragCls,
                String toolbarCbMethod,
                String buildMethod,
                String applyMethod,
                String itemType,
                String[] nameChain) {
            this.prefKey = prefKey;
            this.fragCls = fragCls;
            this.toolbarCbMethod = toolbarCbMethod;
            this.buildMethod = buildMethod;
            this.applyMethod = applyMethod;
            this.itemType = itemType;
            this.nameChain = nameChain;
        }
    }

    public static void init(final Context ctx, final ClassLoader cl) {
        try {
            Class<?> toolbarCls =
                    XposedHelpers.findClass(
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_TOOLBAR, ctx),
                            cl);
            Class<?> btnCls =
                    XposedHelpers.findClass(
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_TOOLBAR_BTN, ctx),
                            cl);

            PageSpec rule =
                    new PageSpec(
                            KEY_ENABLE_RULE,
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_FRAGMENT, ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_FRAGMENT, ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_BUILD, ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_APPLY, ctx),
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_ITEM, ctx),
                            splitChain(
                                    ViaClassMapping.getMethodName(
                                            ViaClassMapping.ClassMethodKey.LIST_SEARCH_RULE_ITEM,
                                            ctx)));
            PageSpec script =
                    new PageSpec(
                            KEY_ENABLE_SCRIPT,
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_FRAGMENT,
                                    ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_FRAGMENT,
                                    ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_BUILD, ctx),
                            ViaClassMapping.getMethodName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_APPLY, ctx),
                            ViaClassMapping.getClassName(
                                    ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_ITEM, ctx),
                            splitChain(
                                    ViaClassMapping.getMethodName(
                                            ViaClassMapping.ClassMethodKey.LIST_SEARCH_SCRIPT_ITEM,
                                            ctx)));

            int n = 0;
            sToolbarCls = toolbarCls;
            sBtnCls = btnCls;
            sSpecs.add(rule);
            sSpecs.add(script);
            n += hookPage(ctx, cl, toolbarCls, btnCls, rule);
            n += hookPage(ctx, cl, toolbarCls, btnCls, script);
            Hook.bvLog("[BetterVia] 列表搜索 Hook 已挂载: " + n + " 个钩子");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 列表搜索 Hook 初始化跳过: " + t);
        }
    }

    private static String[] splitChain(String chain) {
        if (chain == null || chain.isEmpty()) {
            return new String[0];
        }
        return chain.split(",");
    }

    private static int hookPage(
            final Context ctx,
            final ClassLoader cl,
            final Class<?> toolbarCls,
            final Class<?> btnCls,
            final PageSpec spec) {
        int count = 0;
        try {
            Class<?> fragCls = XposedHelpers.findClass(spec.fragCls, cl);
            Method toolbarCb =
                    XposedHelpers.findMethodExact(fragCls, spec.toolbarCbMethod, toolbarCls);
            XposedBridge.hookMethod(
                    toolbarCb,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                sKeywords.remove(param.thisObject);
                                if (param.args == null
                                        || param.args.length < 1
                                        || param.args[0] == null) {
                                    Hook.bvLog("[BetterVia] 列表搜索：toolbar 参数为空 " + spec.fragCls);
                                    return;
                                }
                                sToolbars.put(param.thisObject, param.args[0]);
                                if (!Hook.getPrefBoolean(ctx, spec.prefKey, false)) {
                                    Hook.bvLog("[BetterVia] 列表搜索未启用（开关关闭）: " + spec.fragCls);
                                    return;
                                }
                                injectButton(
                                        ctx,
                                        param.thisObject,
                                        param.args[0],
                                        spec,
                                        toolbarCls,
                                        btnCls);
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 列表搜索按钮注入失败: " + t);
                            }
                        }
                    });
            count++;
        } catch (Throwable t) {
            return count;
        }
        try {
            Class<?> fragCls = XposedHelpers.findClass(spec.fragCls, cl);
            Method build = XposedHelpers.findMethodExact(fragCls, spec.buildMethod);
            XposedBridge.hookMethod(
                    build,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                Object result = param.getResult();
                                if (!(result instanceof List)) {
                                    return;
                                }
                                String kw = sKeywords.get(param.thisObject);
                                if (kw == null || kw.isEmpty()) {
                                    return;
                                }
                                param.setResult(filterList((List<Object>) result, kw, spec));
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 列表搜索过滤失败: " + t);
                            }
                        }
                    });
            count++;
        } catch (Throwable t) {
        }
        return count;
    }

    private static void injectButton(
            final Context ctx,
            final Object frag,
            final Object toolbar,
            final PageSpec spec,
            Class<?> toolbarCls,
            Class<?> btnCls) {
        if (sInjected.containsKey(toolbar)) {
            return;
        }
        Context hostCtx = (Context) XposedHelpers.callMethod(toolbar, "getContext");
        if (hostCtx == null) {
            return;
        }
        Drawable icon = ViaIconLoader.loadSearchIcon(hostCtx);
        boolean needTint = false;
        if (icon == null) {
            icon = decodeSearchIcon(hostCtx);
            needTint = true;
        }
        if (icon == null) {
            try {
                icon = hostCtx.getResources().getDrawable(android.R.drawable.ic_menu_search);
            } catch (Throwable t) {
                icon = null;
            }
        }
        if (icon == null) {
            Hook.bvLog("[BetterVia] 列表搜索：图标加载失败，跳过注入 " + spec.fragCls);
            return;
        }
        if (needTint) {
            try {
                icon.setColorFilter(
                        ViaUIRes.resolveAttrColor(
                                hostCtx, ViaUIRes.VIA_ATTR_TINT, ThemeColors.getHintColor(hostCtx)),
                        PorterDuff.Mode.SRC_IN);
            } catch (Throwable ignored) {
            }
        }
        String label =
                LocalizedStringProvider.getInstance().get(hostCtx, "list_search_button_label");
        String btnFactory =
                ViaClassMapping.getMethodName(
                        ViaClassMapping.ClassMethodKey.LIST_SEARCH_TOOLBAR_BTN, ctx);
        Object btn = XposedHelpers.callStaticMethod(btnCls, btnFactory, icon, label);
        if (btn == null) {
            return;
        }
        final View.OnClickListener listener =
                (View.OnClickListener)
                        Proxy.newProxyInstance(
                                View.OnClickListener.class.getClassLoader(),
                                new Class<?>[] {View.OnClickListener.class},
                                new InvocationHandler() {
                                    @Override
                                    public Object invoke(
                                            Object proxy, Method method, Object[] args) {
                                        String name = method.getName();
                                        if ("onClick".equals(name)) {
                                            showFilterDialog(hostCtx, frag, spec);
                                            return null;
                                        }
                                        if ("hashCode".equals(name)) {
                                            return System.identityHashCode(proxy);
                                        }
                                        if ("equals".equals(name)) {
                                            return args != null
                                                    && args.length == 1
                                                    && proxy == args[0];
                                        }
                                        if ("toString".equals(name)) {
                                            return "BetterViaListSearchListener";
                                        }
                                        return null;
                                    }
                                });
        try {
            String addMethod =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.LIST_SEARCH_TOOLBAR, ctx);
            Method addBtn =
                    XposedHelpers.findMethodExact(
                            toolbarCls, addMethod, btnCls, View.OnClickListener.class);
            addBtn.setAccessible(true);
            addBtn.invoke(toolbar, btn, listener);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 列表搜索按钮插入失败: " + t);
            return;
        }
        sInjected.put(toolbar, Boolean.TRUE);
        Hook.bvLog("[BetterVia] 列表搜索按钮已注入: " + spec.fragCls);
    }

    public static void reinjectEnabledPages(final Context ctx) {
        try {
            if (sToolbarCls == null || sBtnCls == null || sSpecs.isEmpty()) {
                return;
            }
            for (final PageSpec spec : sSpecs) {
                if (!Hook.getPrefBoolean(ctx, spec.prefKey, false)) {
                    continue;
                }
                synchronized (sToolbars) {
                    for (java.util.Map.Entry<Object, Object> entry : sToolbars.entrySet()) {
                        Object frag = entry.getKey();
                        Object toolbar = entry.getValue();
                        if (frag == null
                                || toolbar == null
                                || !spec.fragCls.equals(frag.getClass().getName())) {
                            continue;
                        }
                        try {
                            injectButton(ctx, frag, toolbar, spec, sToolbarCls, sBtnCls);
                        } catch (Throwable t) {
                            Hook.bvLog("[BetterVia] 列表搜索补注入失败: " + t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 列表搜索补注入异常: " + t);
        }
    }

    private static Drawable decodeSearchIcon(Context ctx) {
        try {
            byte[] data = Base64.decode(SEARCH_ICON_BASE64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                return new BitmapDrawable(ctx.getResources(), bitmap);
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 列表搜索图标解码失败: " + t);
        }
        return null;
    }

    private static void showFilterDialog(Context ctx, final Object frag, final PageSpec spec) {
        final Activity act = findActivity(ctx);
        if (act == null) {
            Hook.bvLog(
                    "[BetterVia] 列表搜索：取不到 Activity，无法弹窗（ctx="
                            + (ctx == null ? "null" : ctx.getClass().getName())
                            + "）");
            return;
        }
        final Runnable show =
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing()) {
                            return;
                        }
                        final String current = sKeywords.get(frag);
                        final Runnable clearAction =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        applyKeyword(frag, "", spec);
                                    }
                                };
                        SettingsUI.showInputDialog(
                                act,
                                "list_search_filter_title",
                                "list_search_filter_message",
                                "list_search_filter_hint",
                                current == null ? "" : current,
                                1,
                                "dialog_ok",
                                "dialog_cancel",
                                "list_search_clear",
                                clearAction,
                                new SettingsUI.OnInputListener() {
                                    @Override
                                    public void onConfirm(String input) {
                                        applyKeyword(frag, input == null ? "" : input.trim(), spec);
                                    }

                                    @Override
                                    public void onCancel() {}
                                });
                    }
                };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show.run();
        } else {
            new Handler(Looper.getMainLooper()).post(show);
        }
    }

    private static void applyKeyword(Object frag, String kw, PageSpec spec) {
        if (kw.isEmpty()) {
            sKeywords.remove(frag);
        } else {
            sKeywords.put(frag, kw);
        }
        try {
            Class<?> cls = frag.getClass();
            Method build = findMethod(cls, spec.buildMethod);
            if (build == null) {
                return;
            }
            Object list = build.invoke(frag);
            Method apply = findMethod(cls, spec.applyMethod, List.class);
            if (apply == null) {
                return;
            }
            apply.invoke(frag, list);
            animateListRefresh(frag);
            Hook.bvLog("[BetterVia] 列表搜索已应用: " + spec.fragCls + " 关键词=" + kw);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 列表搜索刷新失败: " + t);
        }
    }

    private static List<Object> filterList(List<Object> src, String kw, PageSpec spec) {
        List<Object> out = new ArrayList<Object>(src.size());
        String lower = kw.toLowerCase(Locale.ROOT);
        Method[] chain = null;
        for (Object item : src) {
            if (item == null) {
                continue;
            }
            if (spec.itemType.equals(item.getClass().getName())) {
                if (chain == null) {
                    chain = resolveNameChain(item.getClass(), spec.nameChain);
                }
                String name = readName(item, chain);
                if (name == null || !name.toLowerCase(Locale.ROOT).contains(lower)) {
                    continue;
                }
            }
            out.add(item);
        }
        return out;
    }

    private static Method[] resolveNameChain(Class<?> itemCls, String[] names) {
        Method[] methods = new Method[names.length];
        Class<?> cur = itemCls;
        for (int i = 0; i < names.length; i++) {
            Method m = findMethod(cur, names[i]);
            if (m == null) {
                return new Method[0];
            }
            methods[i] = m;
            cur = m.getReturnType();
        }
        return methods;
    }

    private static String readName(Object item, Method[] chain) {
        if (chain == null || chain.length == 0) {
            return null;
        }
        Object cur = item;
        for (Method m : chain) {
            try {
                cur = m.invoke(cur);
            } catch (Throwable t) {
                return null;
            }
            if (cur == null) {
                return null;
            }
        }
        return cur.toString();
    }

    private static void animateListRefresh(Object frag) {
        try {
            Object view = XposedHelpers.callMethod(frag, "getView");
            if (!(view instanceof View)) {
                return;
            }
            View target = findRecyclerView((View) view);
            if (target == null) {
                return;
            }
            ViaUIRes.animateListRefresh(target);
        } catch (Throwable ignored) {
        }
    }

    private static View findRecyclerView(View root) {
        if (root == null) {
            return null;
        }
        if (root.getClass().getName().endsWith("RecyclerView")) {
            return root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View found = findRecyclerView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> cls, String name, Class<?>... types) {
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(name, types);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    private static Activity findActivity(Context ctx) {
        Context context = ctx;
        while (context != null) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        }
        return null;
    }
}
