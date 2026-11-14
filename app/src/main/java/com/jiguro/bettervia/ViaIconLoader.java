package com.jiguro.bettervia;

import android.content.Context;
import android.graphics.drawable.Drawable;

import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public final class ViaIconLoader {

    private ViaIconLoader() {}

    public static Drawable loadSearchIcon(Context ctx) {
        return load(
                ctx,
                ViaClassMapping.ClassMethodKey.SEARCH_ICON_DRAWABLE,
                ViaClassMapping.ClassMethodKey.SEARCH_ICON_NAME,
                ViaClassMapping.ResourceKey.SEARCH_ICON_DRAWABLE_ID,
                ViaClassMapping.ResourceKey.SEARCH_ICON_NAME_ID);
    }

    public static Drawable loadBackIcon(Context ctx) {
        return load(
                ctx,
                ViaClassMapping.ClassMethodKey.BACK_ICON_DRAWABLE,
                ViaClassMapping.ClassMethodKey.BACK_ICON_NAME,
                ViaClassMapping.ResourceKey.BACK_ICON_DRAWABLE_ID,
                ViaClassMapping.ResourceKey.BACK_ICON_NAME_ID);
    }

    public static Drawable load(
            Context ctx,
            ViaClassMapping.ClassMethodKey drawableKey,
            ViaClassMapping.ClassMethodKey nameKey,
            ViaClassMapping.ResourceKey drawableIdKey,
            ViaClassMapping.ResourceKey nameIdKey) {
        if (ctx == null) {
            return null;
        }
        try {
            ClassLoader cl = ctx.getClass().getClassLoader();
            int drawableId = resolveId(ctx, cl, drawableKey, drawableIdKey);
            if (drawableId == 0) {
                Hook.bvLog("[BetterVia] 宿主图标：未能解析资源 ID " + drawableKey.name());
                return null;
            }
            int nameId = resolveId(ctx, cl, nameKey, nameIdKey);
            String loaderClsName =
                    ViaClassMapping.getClassName(ViaClassMapping.ClassMethodKey.ICON_LOADER, ctx);
            String loaderMethod =
                    ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.ICON_LOADER, ctx);
            Class<?> loaderCls = XposedHelpers.findClass(loaderClsName, cl);
            Method m =
                    XposedHelpers.findMethodExact(
                            loaderCls, loaderMethod, Context.class, int.class, int.class);
            m.setAccessible(true);
            Object loaded = m.invoke(null, ctx, drawableId, nameId);
            if (loaded instanceof Drawable) {
                return (Drawable) loaded;
            }
            Hook.bvLog("[BetterVia] 宿主图标：加载器返回非 Drawable（" + drawableKey.name() + "）");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 宿主图标：加载失败 " + drawableKey.name() + " -> " + t);
        }
        return null;
    }

    private static int resolveId(
            Context ctx,
            ClassLoader cl,
            ViaClassMapping.ClassMethodKey fieldKey,
            ViaClassMapping.ResourceKey idKey) {
        int id =
                readRFieldInt(
                        cl,
                        ViaClassMapping.getClassName(fieldKey, ctx),
                        ViaClassMapping.getMethodName(fieldKey, ctx));
        if (id != 0) {
            return id;
        }
        return ViaClassMapping.getResourceId(idKey, ctx);
    }

    private static int readRFieldInt(ClassLoader cl, String clsName, String fieldName) {
        try {
            if (clsName == null || clsName.isEmpty() || fieldName == null || fieldName.isEmpty()) {
                return 0;
            }
            Class<?> c = Class.forName(clsName, false, cl);
            return XposedHelpers.getStaticIntField(c, fieldName);
        } catch (Throwable t) {
            return 0;
        }
    }
}
