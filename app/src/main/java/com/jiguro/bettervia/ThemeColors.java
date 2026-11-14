package com.jiguro.bettervia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;

public final class ThemeColors {

    private ThemeColors() {}

    public static final String THEME_AUTO = "auto";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_QINGXIA = "qingxia";
    public static final String DEFAULT_THEME = THEME_AUTO;
    public static final String KEY_THEME = "module_theme";
    public static final String SP_NAME = "BetterVia";

    public static final int LIGHT_BG_COLOR = Color.WHITE;
    public static final int LIGHT_TITLE_COLOR = 0xFF6200EE;
    public static final int LIGHT_TEXT_COLOR = Color.BLACK;
    public static final int LIGHT_HINT_COLOR = 0xFF666666;
    public static final int LIGHT_DIVIDER_COLOR = 0xFFDDDDDD;
    public static final int LIGHT_BTN_BG_COLOR = 0xFFE0E0E0;
    public static final int LIGHT_BTN_TEXT_COLOR = 0xFF000000;
    public static final int LIGHT_OK_BTN_BG_COLOR = 0xFF6200EE;
    public static final int LIGHT_OK_BTN_TEXT_COLOR = Color.WHITE;
    public static final int LIGHT_SWITCH_ON_COLOR = 0xFF6200EE;
    public static final int LIGHT_SWITCH_OFF_COLOR = 0xFFBDBDBD;
    public static final int LIGHT_ITEM_BG_COLOR = 0xFFF8F9FA;
    public static final int LIGHT_EDIT_BG_COLOR = 0xFFF5F5F5;

    public static final int DARK_BG_COLOR = 0xFF000000;
    public static final int DARK_TITLE_COLOR = 0xBEFFFFFF;
    public static final int DARK_TEXT_COLOR = 0xBEFFFFFF;
    public static final int DARK_HINT_COLOR = 0x99FFFFFF;
    public static final int DARK_DIVIDER_COLOR = 0x30808080;
    public static final int DARK_BTN_BG_COLOR = 0xFF2D2D2D;
    public static final int DARK_BTN_TEXT_COLOR = 0xBEFFFFFF;
    public static final int DARK_OK_BTN_BG_COLOR = 0xFF617AC1;
    public static final int DARK_OK_BTN_TEXT_COLOR = Color.WHITE;
    public static final int DARK_SWITCH_ON_COLOR = 0xFF617AC1;
    public static final int DARK_SWITCH_OFF_COLOR = 0x30808080;
    public static final int DARK_ITEM_BG_COLOR = 0xFF1C1B1D;
    public static final int DARK_EDIT_BG_COLOR = 0xFF1C1B1D;

    public static final int QINGXIA_BG_COLOR = 0xFFF5F5F0;
    public static final int QINGXIA_TITLE_COLOR = 0xFF333333;
    public static final int QINGXIA_TEXT_COLOR = 0xFF212121;
    public static final int QINGXIA_HINT_COLOR = 0xFF757575;
    public static final int QINGXIA_DIVIDER_COLOR = 0xFFE0E0E0;
    public static final int QINGXIA_BTN_BG_COLOR = 0xFFD6D2CC;
    public static final int QINGXIA_BTN_TEXT_COLOR = 0xFF333333;
    public static final int QINGXIA_OK_BTN_BG_COLOR = 0xFF86C3E3;
    public static final int QINGXIA_OK_BTN_TEXT_COLOR = 0xFF333333;
    public static final int QINGXIA_SWITCH_ON_COLOR = 0xFF87ACC7;
    public static final int QINGXIA_SWITCH_OFF_COLOR = 0xFFD6D2CC;
    public static final int QINGXIA_ITEM_BG_COLOR = 0xFFFAFAF5;
    public static final int QINGXIA_EDIT_BG_COLOR = 0xFFFAFAF5;

    public static final int RIPPLE_COLOR = 0x30808080;
    public static final int SWITCH_STROKE_COLOR = 0x30808080;
    public static final int SWITCH_FILL_COLOR = 0xFF6F8DE1;
    public static final int DIALOG_BUTTON_TEXT_COLOR = 0xFF6F8DE1;
    public static final int DIALOG_BUTTON_PRESSED_COLOR = 0x30808080;
    public static final int SECTION_HEADER_TEXT_COLOR = 0xFF6F8DE1;

    public static final int ABOUT_ACCENT_LIGHT = 0xFF8A4AC8;
    public static final int ABOUT_ACCENT_DARK = 0xFFCE93D8;
    public static final int ABOUT_TITLE_BASE_LIGHT = 0xFF3A2A6B;
    public static final int ABOUT_TITLE_BASE_DARK = 0xFFB39DDB;
    public static final int ABOUT_SLOGAN_LIGHT = 0xFF4A3A6B;
    public static final int ABOUT_SLOGAN_DARK = 0xFFCBB8E8;
    public static final int ABOUT_BADGE_LIGHT = 0xFF5A4A8B;
    public static final int ABOUT_BADGE_DARK = 0xFFB39DDB;
    public static final int ABOUT_DESIGNED_BY_LIGHT = 0x994A3A6B;
    public static final int ABOUT_DESIGNED_BY_DARK = 0x99B39DDB;
    public static final int ABOUT_CARD_BG_LIGHT = 0xFFF7F8FE;
    public static final int ABOUT_CARD_BG_DARK = 0xFF1A1B22;
    public static final int ABOUT_GLASS_TOP_LIGHT = 0xF2FFFFFF;
    public static final int ABOUT_GLASS_TOP_DARK = 0xD8FFFFFF;
    public static final int ABOUT_GLASS_SHINE_LIGHT = 0x8CFFFFFF;
    public static final int ABOUT_GLASS_SHINE_DARK = 0x5CFFFFFF;
    public static final int ABOUT_GLASS_BODY_LIGHT = 0x42FFFFFF;
    public static final int ABOUT_GLASS_BODY_DARK = 0x24FFFFFF;
    public static final int ABOUT_GLASS_BOTTOM_LIGHT = 0x2EFFFFFF;
    public static final int ABOUT_GLASS_BOTTOM_DARK = 0x16FFFFFF;
    public static final int ABOUT_GLOW_LIGHT = 0x3D8A4AC8;
    public static final int ABOUT_GLOW_DARK = 0x1DCE93D8;

    public static final int SUCCESS_COLOR = 0xFF4CAF50;
    public static final int ERROR_COLOR = 0xFFE53935;
    public static final int STATUS_ACTIVE_COLOR = 0xFF4CAF50;
    public static final int STATUS_INACTIVE_COLOR = 0xFFF44336;
    public static final int WARNING_COLOR = 0xFFFF9800;
    public static final int SCRIM_COLOR = 0xCC000000;

    private static volatile SharedPreferences sPrefs;
    private static volatile String sCachedTheme;
    private static volatile boolean sListenerRegistered;

    private static final SharedPreferences.OnSharedPreferenceChangeListener THEME_LISTENER =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                    if (KEY_THEME.equals(key)) sCachedTheme = null;
                }
            };

    private static SharedPreferences prefs(Context ctx) {
        SharedPreferences sp = sPrefs;
        if (sp != null) return sp;
        synchronized (ThemeColors.class) {
            if (sPrefs == null) {
                Context app = ctx.getApplicationContext();
                if (app == null) app = ctx;
                sPrefs = app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            }
            return sPrefs;
        }
    }

    public static String getTheme(Context ctx) {
        String cached = sCachedTheme;
        if (cached != null) return cached;
        try {
            SharedPreferences sp = prefs(ctx);
            if (!sListenerRegistered) {
                sListenerRegistered = true;
                sp.registerOnSharedPreferenceChangeListener(THEME_LISTENER);
            }
            String theme = sp.getString(KEY_THEME, DEFAULT_THEME);
            if (theme == null) theme = DEFAULT_THEME;
            sCachedTheme = theme;
            return theme;
        } catch (Exception e) {
            return DEFAULT_THEME;
        }
    }

    public static String getActualTheme(Context ctx) {
        String theme = getTheme(ctx);
        if (THEME_AUTO.equals(theme)) {
            int nightMode =
                    ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            return nightMode == Configuration.UI_MODE_NIGHT_YES ? THEME_DARK : THEME_LIGHT;
        }
        return theme;
    }

    public static boolean isDark(Context ctx) {
        String theme = getActualTheme(ctx);
        return THEME_DARK.equals(theme);
    }

    public static int getBgColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_BG_COLOR;
        return THEME_DARK.equals(theme) ? DARK_BG_COLOR : LIGHT_BG_COLOR;
    }

    public static int getTitleColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_TITLE_COLOR;
        return THEME_DARK.equals(theme) ? DARK_TITLE_COLOR : LIGHT_TITLE_COLOR;
    }

    public static int getTextColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_TEXT_COLOR;
        return THEME_DARK.equals(theme) ? DARK_TEXT_COLOR : LIGHT_TEXT_COLOR;
    }

    public static int getHintColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_HINT_COLOR;
        return THEME_DARK.equals(theme) ? DARK_HINT_COLOR : LIGHT_HINT_COLOR;
    }

    public static int getDividerColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_DIVIDER_COLOR;
        return THEME_DARK.equals(theme) ? DARK_DIVIDER_COLOR : LIGHT_DIVIDER_COLOR;
    }

    public static int getBtnBgColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_BTN_BG_COLOR;
        return THEME_DARK.equals(theme) ? DARK_BTN_BG_COLOR : LIGHT_BTN_BG_COLOR;
    }

    public static int getBtnTextColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_BTN_TEXT_COLOR;
        return THEME_DARK.equals(theme) ? DARK_BTN_TEXT_COLOR : LIGHT_BTN_TEXT_COLOR;
    }

    public static int getOkBtnBgColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_OK_BTN_BG_COLOR;
        return THEME_DARK.equals(theme) ? DARK_OK_BTN_BG_COLOR : LIGHT_OK_BTN_BG_COLOR;
    }

    public static int getOkBtnTextColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_OK_BTN_TEXT_COLOR;
        return THEME_DARK.equals(theme) ? DARK_OK_BTN_TEXT_COLOR : LIGHT_OK_BTN_TEXT_COLOR;
    }

    public static int getSwitchOnColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_SWITCH_ON_COLOR;
        return THEME_DARK.equals(theme) ? DARK_SWITCH_ON_COLOR : LIGHT_SWITCH_ON_COLOR;
    }

    public static int getSwitchOffColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_SWITCH_OFF_COLOR;
        return THEME_DARK.equals(theme) ? DARK_SWITCH_OFF_COLOR : LIGHT_SWITCH_OFF_COLOR;
    }

    public static int getItemBgColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_ITEM_BG_COLOR;
        return THEME_DARK.equals(theme) ? DARK_ITEM_BG_COLOR : LIGHT_ITEM_BG_COLOR;
    }

    public static int getEditBgColor(Context ctx) {
        String theme = getActualTheme(ctx);
        if (THEME_QINGXIA.equals(theme)) return QINGXIA_EDIT_BG_COLOR;
        return THEME_DARK.equals(theme) ? DARK_EDIT_BG_COLOR : LIGHT_EDIT_BG_COLOR;
    }
}
