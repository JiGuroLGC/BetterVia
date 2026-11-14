package com.jiguro.bettervia;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public final class ViaUIRes {

    private ViaUIRes() {}

    public static final int VIA_ATTR_TEXT_PRIMARY = 0x7f040027;
    public static final int VIA_ATTR_TEXT_SECONDARY = 0x7f040028;
    public static final int VIA_ATTR_TEXT_HINT = 0x7f040026;

    public static final int VIA_ATTR_TEXT_PRIMARY_741 = 0x7f040028;
    public static final int VIA_ATTR_TEXT_SECONDARY_741 = 0x7f040029;
    public static final int VIA_ATTR_TEXT_HINT_741 = 0x7f040027;

    public static int attrTextPrimary() {
        return Hook.isViaVersionAtLeast(20261113)
                ? VIA_ATTR_TEXT_PRIMARY_741
                : VIA_ATTR_TEXT_PRIMARY;
    }

    public static int attrTextSecondary() {
        return Hook.isViaVersionAtLeast(20261113)
                ? VIA_ATTR_TEXT_SECONDARY_741
                : VIA_ATTR_TEXT_SECONDARY;
    }

    public static int attrTextHint() {
        return Hook.isViaVersionAtLeast(20261113) ? VIA_ATTR_TEXT_HINT_741 : VIA_ATTR_TEXT_HINT;
    }

    public static final int VIA_ATTR_TEXT_DEFAULT = 0x7f040021;
    public static final int VIA_ATTR_TINT = 0x7f040023;
    public static final int VIA_ATTR_ACCENT = 0x7f040022;

    public static final int VIA_PRESSED_COLOR = 0x30808080;
    public static final int VIA_ACCENT_TEXT_COLOR = 0xFFD64146;
    public static final int VIA_SEARCH_BG_COLOR = 0x20808080;
    public static final int VIA_CORNER_RADIUS_DP = 18;

    public static void animateListRefresh(android.view.View target) {
        if (target == null || target.getVisibility() != android.view.View.VISIBLE) {
            return;
        }
        target.animate().cancel();
        target.setAlpha(0.1f);
        target.setTranslationY(dp(target.getContext(), 14));
        target.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    public static int dp(Context ctx, int dp) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public static int resolveAttrColor(Context ctx, int attrId, int fallbackColor) {
        try {
            TypedValue tv = new TypedValue();
            if (!ctx.getTheme().resolveAttribute(attrId, tv, true)) {
                return fallbackColor;
            }
            if (tv.resourceId != 0) {
                Resources res = ctx.getResources();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    return res.getColor(tv.resourceId, ctx.getTheme());
                }
                return res.getColor(tv.resourceId);
            }
            if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return tv.data;
            }
        } catch (Throwable t) {
        }
        return fallbackColor;
    }
}
