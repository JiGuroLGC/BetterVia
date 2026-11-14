package com.jiguro.bettervia;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.text.InputType;
import android.util.*;
import android.util.Base64;
import android.view.*;
import android.view.animation.*;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public final class SettingsUI {

    private static final int PAGE_SLIDE_DISTANCE_DP = 156;
    private static final int PAGE_PARALLAX_DP = -66;
    private static final int PAGE_TRANSITION_DURATION_MS = 220;
    private static final float TRANSITION_MAX_STEP_PER_FRAME = 0.12f;
    private static final String BACK_ICON_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAFgAAABYCAMAAABGS8AGAAAAPFBMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADQLyYwAAAAE3RSTlMAIN+AEO8wf8+fYF+gv3BQb0CvpkvT+wAAAOhJREFUeNrt1dkNAyEQBNEGBnbZw9fkn6stmQisKUsr0QG8D0CF5ubmwveo3g1wF//shFx3g1w3yO3B7j7cbJCbYt1GuZVx03BrtJuH2xjXKXeLdY1y1+EWxY5yO+1CQTsu5q6UazPsTIAZV5kJuxYm7EpM2KXt694kJJVdYk4ihcN3pBCSbtBJqIy2UzB3FNDlZVHP7SkJSdBJ3Z7v4bBVKEIqUDZlQOjhr0nNKXnjZYOehq+cLEruF5KPIS8XkldKNk7ODuU58XKj5BovVyr8bYb/r+E/RclGySZG7gJWcn2Z5ubmftwbMRIkSXSuPPMAAAAASUVORK5CYII=";
    private static final int RIPPLE_COLOR = ThemeColors.RIPPLE_COLOR;
    private static final int SWITCH_STROKE_COLOR = ThemeColors.SWITCH_STROKE_COLOR;
    private static final int SWITCH_FILL_COLOR = ThemeColors.SWITCH_FILL_COLOR;
    private static final int DIALOG_BUTTON_TEXT_COLOR = ThemeColors.DIALOG_BUTTON_TEXT_COLOR;
    private static final int DIALOG_BUTTON_PRESSED_COLOR = ThemeColors.DIALOG_BUTTON_PRESSED_COLOR;
    private static final int SECTION_HEADER_TEXT_COLOR = ThemeColors.SECTION_HEADER_TEXT_COLOR;
    private static final List<View> pageStack = new ArrayList<View>();
    private static long sLastPageOpenAt = 0L;
    private static boolean savedWasFullscreen = false;
    private static int savedSoftInputMode = 0;
    private static boolean savedSoftInputModeValid = false;

    private static void applyImeNoAdjust(Window window) {
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            savedSoftInputMode = lp.softInputMode;
            savedSoftInputModeValid = true;
            int mode =
                    (lp.softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST)
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            window.setSoftInputMode(mode);
        } catch (Throwable t) {
            savedSoftInputModeValid = false;
        }
    }

    private static void restoreImeMode(Window window) {
        if (!savedSoftInputModeValid || window == null) {
            return;
        }
        savedSoftInputModeValid = false;
        try {
            window.setSoftInputMode(savedSoftInputMode);
        } catch (Throwable ignored) {
        }
    }

    private static String lastAppliedActualTheme = null;
    private static boolean themeReceiverRegistered = false;
    private static Object predictiveBackCallback;
    private static Activity predictiveBackActivity;
    private static boolean predictiveBackAnimated;
    private static Activity sHostActivity;
    private static final long FULLSCREEN_CORRECTION_WINDOW_MS = 3000;

    private SettingsUI() {}

    public interface PageContentBuilder {
        void build(ViewGroup content, Activity act);
    }

    public interface PageBackInterceptor {
        boolean onBackPressed();
    }

    private static final Map<View, PageBackInterceptor> pageBackInterceptors =
            new WeakHashMap<View, PageBackInterceptor>();

    public interface OnSelectListener {
        void onSelect(int index);
    }

    public interface OnMultiSelectListener {
        void onResult(int which, boolean[] checked);
    }

    public static class DialogSection {
        public final String titleKey;
        public final String[] optionKeys;
        public final int selectedIndex;

        public DialogSection(String titleKey, String[] optionKeys, int selectedIndex) {
            this.titleKey = titleKey;
            this.optionKeys = optionKeys;
            this.selectedIndex = selectedIndex;
        }
    }

    public interface OnSectionedSelectListener {
        void onConfirm(int[] selectedIndices);

        void onCancel();
    }

    public interface OnInputListener {
        void onConfirm(String input);

        void onCancel();
    }

    public static void showPage(
            final Activity act, final String titleKey, final PageContentBuilder builder) {
        showPage(act, titleKey, null, null, builder);
    }

    public static void showPage(
            final Activity act,
            final String titleKey,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder) {
        showPage(act, titleKey, rightButtonTextKey, rightButtonAction, builder, false);
    }

    public static void showPage(
            final Activity act,
            final String titleKey,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder,
            final boolean exitCancelHint) {
        showPageImpl(
                act,
                titleKey,
                null,
                rightButtonTextKey,
                rightButtonAction,
                builder,
                exitCancelHint,
                null);
    }

    public static void showPage(
            final Activity act,
            final String titleKey,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder,
            final PageBackInterceptor backInterceptor) {
        showPageImpl(
                act,
                titleKey,
                null,
                rightButtonTextKey,
                rightButtonAction,
                builder,
                false,
                backInterceptor);
    }

    public static void showPageWithTitle(
            final Activity act,
            final CharSequence titleText,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder) {
        showPageImpl(
                act,
                String.valueOf(titleText),
                titleText,
                rightButtonTextKey,
                rightButtonAction,
                builder,
                false,
                null);
    }

    public static void showPageWithTitle(
            final Activity act,
            final CharSequence titleText,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder,
            final PageBackInterceptor backInterceptor) {
        showPageImpl(
                act,
                String.valueOf(titleText),
                titleText,
                rightButtonTextKey,
                rightButtonAction,
                builder,
                false,
                backInterceptor);
    }

    private static void showPageImpl(
            final Activity act,
            final String titleKey,
            final CharSequence titleText,
            final String rightButtonTextKey,
            final Runnable rightButtonAction,
            final PageContentBuilder builder,
            final boolean exitCancelHint,
            final PageBackInterceptor backInterceptor) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;
                        long now = SystemClock.elapsedRealtime();
                        if (now - sLastPageOpenAt < 250) return;
                        sLastPageOpenAt = now;

                        final Window window = act.getWindow();
                        if (window == null) return;

                        final boolean isBasePage = pageStack.isEmpty();
                        if (isBasePage) {
                            savedWasFullscreen = ViaSystemUi.isFullscreenByViaAppFlag(act);
                            applyImeNoAdjust(window);
                        }
                        final boolean[] fsMode = {savedWasFullscreen};
                        final long[] openTime = {SystemClock.elapsedRealtime()};
                        final boolean[] hasLostFocus = {false};

                        final SwipeBackLayout rootLayout = new SwipeBackLayout(act);
                        rootLayout.setOrientation(LinearLayout.VERTICAL);
                        rootLayout.setBackgroundColor(Hook.getBgColorStatic(act));
                        rootLayout.setExitCancelHintEnabled(exitCancelHint);
                        rootLayout.setTag(titleKey);
                        if (backInterceptor != null) {
                            pageBackInterceptors.put(rootLayout, backInterceptor);
                        }

                        applyFullscreenState(act, window, fsMode[0]);

                        rootLayout.setOnApplyWindowInsetsListener(
                                new View.OnApplyWindowInsetsListener() {
                                    @Override
                                    public android.view.WindowInsets onApplyWindowInsets(
                                            View v, android.view.WindowInsets insets) {
                                        try {
                                            if (!fsMode[0]) {
                                                v.setPadding(
                                                        0,
                                                        insets.getSystemWindowInsetTop(),
                                                        0,
                                                        insets.getSystemWindowInsetBottom());
                                            } else {
                                                v.setPadding(0, 0, 0, 0);
                                            }
                                        } catch (Throwable t) {
                                        }
                                        return insets;
                                    }
                                });

                        CharSequence pageTitle =
                                titleText != null
                                        ? titleText
                                        : LocalizedStringProvider.getInstance().get(act, titleKey);
                        rootLayout.addView(
                                buildTitleBar(
                                        act,
                                        pageTitle,
                                        rootLayout,
                                        rightButtonTextKey,
                                        rightButtonAction));

                        FrameLayout content = new FrameLayout(act);
                        content.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                        if (builder != null) {
                            try {
                                builder.build(content, act);
                            } catch (Throwable t) {
                                Hook.bvLog("[BetterVia] 设置页构建失败: " + t);
                                try {
                                    StackTraceElement[] st = t.getStackTrace();
                                    if (st != null && st.length > 0) {
                                        Hook.bvLog("[BetterVia]     at " + st[0].toString());
                                    }
                                } catch (Throwable ignored) {
                                }
                            }
                            applyViaScrollStyleRecursive(content);
                        }
                        rootLayout.addView(content);

                        lastAppliedActualTheme = ThemeColors.getActualTheme(act);
                        ensureThemeReceiver(act);

                        try {
                            window.addContentView(
                                    rootLayout,
                                    new FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT));
                        } catch (Throwable t) {
                            Hook.bvLog("[BetterVia] 注入页面到宿主窗口失败: " + t);
                            return;
                        }
                        animateSlideInRight(rootLayout, act);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            final Runnable reapplyFullscreenState =
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            applyFullscreenState(act, window, fsMode[0]);
                                        }
                                    };
                            rootLayout
                                    .getViewTreeObserver()
                                    .addOnWindowFocusChangeListener(
                                            new ViewTreeObserver.OnWindowFocusChangeListener() {
                                                @Override
                                                public void onWindowFocusChanged(boolean hasFocus) {
                                                    if (!hasFocus) {
                                                        hasLostFocus[0] = true;
                                                        return;
                                                    }
                                                    if (!hasLostFocus[0]) return;
                                                    if (pageStack.isEmpty()
                                                            || pageStack.get(pageStack.size() - 1)
                                                                    != rootLayout) return;
                                                    reapplyFullscreenState.run();
                                                    new Handler(Looper.getMainLooper())
                                                            .postDelayed(
                                                                    reapplyFullscreenState, 300);
                                                    syncPredictiveBackCallback(act, true);
                                                }
                                            });
                        }

                        if (isBasePage) {
                            final Runnable recheckFullscreen =
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final boolean corrected =
                                                        ViaSystemUi.isFullscreenByViaAppFlag(act);
                                                if (corrected == fsMode[0]) return;
                                                final boolean covered =
                                                        pageStack.size() > 1
                                                                && pageStack.get(0) == rootLayout;
                                                final boolean inWindow =
                                                        (SystemClock.elapsedRealtime()
                                                                        - openTime[0])
                                                                < FULLSCREEN_CORRECTION_WINDOW_MS;
                                                final boolean canCorrect =
                                                        !hasLostFocus[0] && inWindow && !covered;
                                                if (canCorrect) {
                                                    fsMode[0] = corrected;
                                                    savedWasFullscreen = corrected;
                                                    applyFullscreenState(act, window, corrected);
                                                } else {
                                                    applyFullscreenState(act, window, fsMode[0]);
                                                }
                                            } catch (Throwable ignored) {
                                            }
                                        }
                                    };
                            rootLayout
                                    .getViewTreeObserver()
                                    .addOnGlobalLayoutListener(
                                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                                @Override
                                                public void onGlobalLayout() {
                                                    recheckFullscreen.run();
                                                }
                                            });
                            new Handler(Looper.getMainLooper()).postDelayed(recheckFullscreen, 400);
                        }
                    }
                });
    }

    private static void applyFullscreenState(
            final Activity act, final Window window, final boolean fullscreen) {
        try {
            if (ViaSystemUi.isFullscreenByViaAppFlag(act) == fullscreen) {
                if (!fullscreen) {
                    applyViaSystemBarColor(act, window);
                }
                return;
            }
            Hook.bvLog("[BetterVia] applyFullscreenState 切换系统栏：fullscreen=" + fullscreen);
            if (fullscreen) {
                ViaSystemUi.applyFullscreenModule(window, true);
            } else {
                ViaSystemUi.applyFullscreenModule(window, false);
                applyViaSystemBarColor(act, window);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void applyViaSystemBarColor(final Activity act, final Window window) {
        try {
            final boolean dark = Hook.isDarkTheme(act);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int statusColor = dark ? 0x33000000 : 0x00000000;
                window.setStatusBarColor(statusColor);
                int navColor = dark ? 0x33000000 : 0x00000000;
                if (!dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    navColor = blendColor(0x33000000, Color.BLACK, 0.2f);
                }
                window.setNavigationBarColor(navColor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        window.setNavigationBarDividerColor(navColor);
                    } catch (Throwable ignored) {
                    }
                }
            }
            ViaSystemUi.applyIconAppearance(window, !dark);
        } catch (Throwable t) {
        }
    }

    private static int blendColor(int color, int blendTo, float ratio) {
        try {
            int a = Color.alpha(color);
            int r = (int) (Color.red(color) * (1f - ratio));
            int g = (int) (Color.green(color) * (1f - ratio));
            int b = (int) (Color.blue(color) * (1f - ratio));
            r += (int) (Color.red(blendTo) * ratio);
            g += (int) (Color.green(blendTo) * ratio);
            b += (int) (Color.blue(blendTo) * ratio);
            return Color.argb(a, r, g, b);
        } catch (Throwable t) {
            return color;
        }
    }

    static Drawable createCircleToggleDrawable(Context ctx) {
        int strokePx = dp(ctx, 2);
        int sizePx = dp(ctx, 16);
        GradientDrawable off = new GradientDrawable();
        off.setShape(GradientDrawable.OVAL);
        off.setStroke(strokePx, SWITCH_STROKE_COLOR);
        off.setColor(Color.TRANSPARENT);
        off.setSize(sizePx, sizePx);
        int fillColor =
                Hook.isDarkTheme(ctx)
                        ? ThemeColors.DARK_SWITCH_ON_COLOR
                        : ThemeColors.SWITCH_FILL_COLOR;
        GradientDrawable on = new GradientDrawable();
        on.setShape(GradientDrawable.OVAL);
        on.setColor(fillColor);
        on.setSize(sizePx, sizePx);

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[] {android.R.attr.state_checked}, on);
        sld.addState(new int[] {-android.R.attr.state_checked}, off);
        return sld;
    }

    private static LinearLayout createSelectDialogRoot(Activity act, String titleKey) {
        LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(Hook.getBgColorStatic(act));
        if (Hook.isDarkTheme(act)) {
            bg.setColor(ThemeColors.DARK_ITEM_BG_COLOR);
        }
        bg.setStroke(dp(act, 1), ThemeColors.SWITCH_STROKE_COLOR);
        bg.setCornerRadius(dp(act, 18));
        root.setBackgroundDrawable(bg);

        TextView title = new TextView(act);
        title.setText(LocalizedStringProvider.getInstance().get(act, titleKey));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Hook.getTextColorStatic(act));
        title.setGravity(Gravity.START);
        title.setMaxLines(2);
        title.setPadding(dp(act, 16), dp(act, 20), dp(act, 16), dp(act, 14));
        root.addView(title);
        return root;
    }

    public static void applyViaScrollStyle(ScrollView scroll) {
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
    }

    private static void applyViaScrollStyleRecursive(View view) {
        if (view instanceof ScrollView) {
            applyViaScrollStyle((ScrollView) view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyViaScrollStyleRecursive(vg.getChildAt(i));
            }
        }
    }

    private static Dialog createSelectDialog(Activity act, View content) {
        final Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.4f);
        }
        dialog.setContentView(content);
        if (dialog.getWindow() != null) {
            int width = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.8f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }

    public static void showSelectDialog(
            final Activity act,
            final String titleKey,
            final String[] optionKeys,
            final int selectedIndex,
            final OnSelectListener listener) {
        showSelectDialog(
                act, titleKey, optionKeys, selectedIndex, null, null, false, null, listener);
    }

    public static void showSelectDialog(
            final Activity act,
            final String titleKey,
            final String[] optionKeys,
            final int selectedIndex,
            final String sectionTitleKey,
            final String checkboxTitleKey,
            final boolean checkboxChecked,
            final CompoundButton.OnCheckedChangeListener onCheckChanged,
            final OnSelectListener listener) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);

                        ScrollView scroll = new ScrollView(act);
                        applyViaScrollStyle(scroll);
                        final LinearLayout options = new LinearLayout(act);
                        options.setOrientation(LinearLayout.VERTICAL);
                        scroll.addView(options);
                        root.addView(scroll);

                        final Dialog[] dialogRef = new Dialog[1];
                        for (int i = 0; i < optionKeys.length; i++) {
                            final int idx = i;
                            addSelectOption(
                                    options,
                                    act,
                                    optionKeys[i],
                                    i == selectedIndex,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null) listener.onSelect(idx);
                                        }
                                    });
                        }

                        if (checkboxTitleKey != null) {
                            if (sectionTitleKey != null) {
                                TextView sectionHeader = new TextView(act);
                                sectionHeader.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(act, sectionTitleKey));
                                sectionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                sectionHeader.setTextColor(SECTION_HEADER_TEXT_COLOR);
                                sectionHeader.setPadding(
                                        dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 4));
                                root.addView(sectionHeader);
                            }
                            LinearLayout checkRow = new LinearLayout(act);
                            checkRow.setOrientation(LinearLayout.HORIZONTAL);
                            checkRow.setGravity(Gravity.CENTER_VERTICAL);
                            checkRow.setPadding(dp(act, 16), dp(act, 10), dp(act, 16), dp(act, 10));
                            checkRow.setBackground(createRippleDrawable(act));
                            TextView checkLabel = new TextView(act);
                            checkLabel.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(act, checkboxTitleKey));
                            checkLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            checkLabel.setTextColor(Hook.getTextColorStatic(act));
                            checkRow.addView(
                                    checkLabel,
                                    new LinearLayout.LayoutParams(
                                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            final CheckBox checkBox = new CheckBox(act);
                            checkBox.setButtonDrawable(createCircleToggleDrawable(act));
                            checkBox.setClickable(false);
                            checkBox.setFocusable(false);
                            checkBox.setChecked(checkboxChecked);
                            checkRow.addView(
                                    checkBox,
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkRow.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkBox.setChecked(!checkBox.isChecked());
                                            if (onCheckChanged != null) {
                                                onCheckChanged.onCheckedChanged(
                                                        checkBox, checkBox.isChecked());
                                            }
                                        }
                                    });
                            root.addView(checkRow);
                        }

                        View bottomSpace = new View(act);
                        bottomSpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bottomSpace);

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.show();
                    }
                });
    }

    public static void showSectionedSelectDialog(
            final Activity act,
            final String titleKey,
            final DialogSection[] sections,
            final String positiveTextKey,
            final String negativeTextKey,
            final OnSectionedSelectListener listener) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);

                        ScrollView scroll = new ScrollView(act);
                        applyViaScrollStyle(scroll);
                        final LinearLayout options = new LinearLayout(act);
                        options.setOrientation(LinearLayout.VERTICAL);
                        scroll.addView(options);
                        root.addView(scroll);

                        final CheckBox[][] boxes = new CheckBox[sections.length][];

                        for (int s = 0; s < sections.length; s++) {
                            final int groupIdx = s;
                            final DialogSection section = sections[s];

                            if (section.titleKey != null) {
                                TextView header = new TextView(act);
                                header.setText(
                                        LocalizedStringProvider.getInstance()
                                                .get(act, section.titleKey));
                                header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                header.setTextColor(SECTION_HEADER_TEXT_COLOR);
                                header.setPadding(
                                        dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 4));
                                options.addView(
                                        header,
                                        new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                            }

                            boxes[s] = new CheckBox[section.optionKeys.length];
                            for (int i = 0; i < section.optionKeys.length; i++) {
                                final int optIdx = i;
                                boxes[s][i] =
                                        addSelectOption(
                                                options,
                                                act,
                                                section.optionKeys[i],
                                                i == section.selectedIndex,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        for (int j = 0;
                                                                j < boxes[groupIdx].length;
                                                                j++) {
                                                            boxes[groupIdx][j].setChecked(
                                                                    j == optIdx);
                                                        }
                                                    }
                                                });
                            }
                        }

                        View gapSpace = new View(act);
                        gapSpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(gapSpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);

                        final Dialog[] dialogRef = new Dialog[1];

                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        if (negativeTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    negativeTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null) listener.onCancel();
                                        }
                                    });
                        }
                        if (positiveTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    positiveTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int[] selected = new int[sections.length];
                                            for (int g = 0; g < sections.length; g++) {
                                                int sel = 0;
                                                for (int j = 0; j < boxes[g].length; j++) {
                                                    if (boxes[g][j].isChecked()) {
                                                        sel = j;
                                                        break;
                                                    }
                                                }
                                                selected[g] = sel;
                                            }
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null) listener.onConfirm(selected);
                                        }
                                    });
                        }

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });
    }

    public static void showMultiSelectDialog(
            final Activity act,
            final String titleKey,
            final String[] optionKeys,
            final boolean[] checked,
            final String positiveTextKey,
            final String negativeTextKey,
            final String neutralTextKey,
            final OnMultiSelectListener listener) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);

                        ScrollView scroll = new ScrollView(act);
                        applyViaScrollStyle(scroll);
                        final LinearLayout options = new LinearLayout(act);
                        options.setOrientation(LinearLayout.VERTICAL);
                        scroll.addView(options);
                        root.addView(scroll);

                        final CheckBox[] boxes = new CheckBox[optionKeys.length];
                        for (int i = 0; i < optionKeys.length; i++) {
                            boolean init = checked != null && i < checked.length && checked[i];
                            boxes[i] = addSelectOption(options, act, optionKeys[i], init, null);
                        }

                        View gapSpace = new View(act);
                        gapSpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(gapSpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);

                        final Dialog[] dialogRef = new Dialog[1];

                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        if (negativeTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    negativeTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissAndNotify(
                                                    dialogRef[0],
                                                    listener,
                                                    DialogInterface.BUTTON_NEGATIVE,
                                                    boxes);
                                        }
                                    });
                        }
                        if (positiveTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    positiveTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissAndNotify(
                                                    dialogRef[0],
                                                    listener,
                                                    DialogInterface.BUTTON_POSITIVE,
                                                    boxes);
                                        }
                                    });
                        }
                        if (neutralTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    neutralTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissAndNotify(
                                                    dialogRef[0],
                                                    listener,
                                                    DialogInterface.BUTTON_NEUTRAL,
                                                    boxes);
                                        }
                                    });
                        }

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.show();
                    }
                });
    }

    public static void showMessageDialog(
            final Activity act, final String titleKey, final String messageKey) {
        showMessageDialog(
                act, titleKey, messageKey, null, false, "dialog_ok", null, null, null, null, false);
    }

    public static void showMessageDialog(
            final Activity act,
            final String titleKey,
            final String messageKey,
            final String positiveTextKey,
            final String negativeTextKey,
            final Runnable onPositive,
            final Runnable onNegative) {
        showMessageDialog(
                act,
                titleKey,
                messageKey,
                null,
                false,
                positiveTextKey,
                negativeTextKey,
                onPositive,
                onNegative,
                null,
                false);
    }

    public static void showMessageDialog(
            final Activity act,
            final String titleKey,
            final String messageKey,
            final String checkboxTextKey,
            final boolean checkboxChecked,
            final String positiveTextKey,
            final String negativeTextKey,
            final Runnable onPositive,
            final Runnable onNegative,
            final CompoundButton.OnCheckedChangeListener onCheckedChange,
            final boolean requireCheckboxChecked) {
        CharSequence message =
                (messageKey == null)
                        ? null
                        : LocalizedStringProvider.getInstance().get(act, messageKey);
        showMessageDialogInternal(
                act,
                titleKey,
                message,
                checkboxTextKey,
                checkboxChecked,
                positiveTextKey,
                negativeTextKey,
                onPositive,
                onNegative,
                onCheckedChange,
                requireCheckboxChecked,
                true);
    }

    private static void showMessageDialogInternal(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String checkboxTextKey,
            final boolean checkboxChecked,
            final String positiveTextKey,
            final String negativeTextKey,
            final Runnable onPositive,
            final Runnable onNegative,
            final CompoundButton.OnCheckedChangeListener onCheckedChange,
            final boolean requireCheckboxChecked,
            final boolean cancelableOnTouchOutside) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);
                        final CheckBox[] cbRef = new CheckBox[1];
                        final LinearLayout[] checkRowRef = new LinearLayout[1];

                        if (message != null) {
                            TextView msg = new TextView(act);
                            msg.setText(message);
                            msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            msg.setTextColor(Hook.getTextColorStatic(act));
                            msg.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
                            msg.setLineSpacing(dp(act, 4), 1.0f);
                            msg.setMovementMethod(
                                    android.text.method.LinkMovementMethod.getInstance());
                            MaxHeightScrollView msgScroll =
                                    new MaxHeightScrollView(
                                            act,
                                            (int)
                                                    (act.getResources()
                                                                    .getDisplayMetrics()
                                                                    .heightPixels
                                                            * 0.55f));
                            msgScroll.addView(msg);
                            root.addView(msgScroll);
                        }

                        if (checkboxTextKey != null) {
                            final LinearLayout checkRow = new LinearLayout(act);
                            checkRow.setOrientation(LinearLayout.HORIZONTAL);
                            checkRow.setGravity(Gravity.CENTER_VERTICAL);
                            checkRow.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 4));

                            final CheckBox cb = new CheckBox(act);
                            cb.setButtonDrawable(createCircleToggleDrawable(act));
                            cb.setChecked(checkboxChecked);
                            cb.setClickable(false);
                            cb.setFocusable(false);
                            checkRow.addView(cb);
                            cbRef[0] = cb;
                            checkRowRef[0] = checkRow;

                            TextView cbText = new TextView(act);
                            cbText.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(act, checkboxTextKey));
                            cbText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            cbText.setTextColor(Hook.getHintColorStatic(act));
                            LinearLayout.LayoutParams cbTextLp =
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            cbTextLp.leftMargin = dp(act, 8);
                            checkRow.addView(cbText, cbTextLp);

                            checkRow.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cb.setChecked(!cb.isChecked());
                                        }
                                    });
                            cb.setOnCheckedChangeListener(onCheckedChange);

                            root.addView(checkRow);
                        }

                        View bodySpace = new View(act);
                        bodySpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bodySpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);

                        final Dialog[] dialogRef = new Dialog[1];

                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        if (negativeTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    negativeTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (onNegative != null) onNegative.run();
                                        }
                                    });
                        }
                        if (positiveTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    positiveTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (requireCheckboxChecked
                                                    && cbRef[0] != null
                                                    && !cbRef[0].isChecked()) {
                                                shakeView(checkRowRef[0]);
                                                return;
                                            }
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (onPositive != null) onPositive.run();
                                        }
                                    });
                        }

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
                        dialog.setOnCancelListener(
                                new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface d) {
                                        if (onNegative != null) onNegative.run();
                                    }
                                });
                        dialog.show();
                    }
                });
    }

    public static void showMessageDialog(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String checkboxTextKey,
            final boolean checkboxChecked,
            final String positiveTextKey,
            final String negativeTextKey,
            final Runnable onPositive,
            final Runnable onNegative,
            final CompoundButton.OnCheckedChangeListener onCheckedChange,
            final boolean requireCheckboxChecked) {
        showMessageDialogInternal(
                act,
                titleKey,
                message,
                checkboxTextKey,
                checkboxChecked,
                positiveTextKey,
                negativeTextKey,
                onPositive,
                onNegative,
                onCheckedChange,
                requireCheckboxChecked,
                true);
    }

    public static void showMessageDialog(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String checkboxTextKey,
            final boolean checkboxChecked,
            final String positiveTextKey,
            final String negativeTextKey,
            final Runnable onPositive,
            final Runnable onNegative,
            final CompoundButton.OnCheckedChangeListener onCheckedChange,
            final boolean requireCheckboxChecked,
            final boolean cancelableOnTouchOutside) {
        showMessageDialogInternal(
                act,
                titleKey,
                message,
                checkboxTextKey,
                checkboxChecked,
                positiveTextKey,
                negativeTextKey,
                onPositive,
                onNegative,
                onCheckedChange,
                requireCheckboxChecked,
                cancelableOnTouchOutside);
    }

    public static void showInputDialog(
            final Activity act,
            final String titleKey,
            final String messageKey,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final OnInputListener listener) {
        CharSequence message =
                (messageKey == null)
                        ? null
                        : LocalizedStringProvider.getInstance().get(act, messageKey);
        showInputDialogInternal(
                act,
                titleKey,
                message,
                hintKey,
                defaultText,
                lines,
                positiveTextKey,
                negativeTextKey,
                null,
                null,
                listener,
                false);
    }

    public static void showInputDialog(
            final Activity act,
            final String titleKey,
            final String messageKey,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final String neutralTextKey,
            final Runnable neutralAction,
            final OnInputListener listener) {
        CharSequence message =
                (messageKey == null)
                        ? null
                        : LocalizedStringProvider.getInstance().get(act, messageKey);
        showInputDialogInternal(
                act,
                titleKey,
                message,
                hintKey,
                defaultText,
                lines,
                positiveTextKey,
                negativeTextKey,
                neutralTextKey,
                neutralAction,
                listener,
                false);
    }

    public static void showInputDialog(
            final Activity act,
            final String titleKey,
            final String messageKey,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final OnInputListener listener,
            final boolean cancelableOnTouchOutside) {
        CharSequence message =
                (messageKey == null)
                        ? null
                        : LocalizedStringProvider.getInstance().get(act, messageKey);
        showInputDialogInternal(
                act,
                titleKey,
                message,
                hintKey,
                defaultText,
                lines,
                positiveTextKey,
                negativeTextKey,
                null,
                null,
                listener,
                cancelableOnTouchOutside);
    }

    private static void showInputDialogInternal(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final String neutralTextKey,
            final Runnable neutralAction,
            final OnInputListener listener,
            final boolean cancelableOnTouchOutside) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);

                        if (message != null) {
                            TextView msg = new TextView(act);
                            msg.setText(message);
                            msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            msg.setTextColor(Hook.getTextColorStatic(act));
                            msg.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
                            msg.setLineSpacing(dp(act, 4), 1.0f);
                            msg.setMovementMethod(
                                    android.text.method.LinkMovementMethod.getInstance());
                            root.addView(msg);
                        }

                        final EditText input = buildViaInputBox(act, hintKey, defaultText, lines);
                        root.addView(input, createInputBoxLayoutParams(act));

                        View bodySpace = new View(act);
                        bodySpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bodySpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);

                        final Dialog[] dialogRef = new Dialog[1];

                        if (neutralTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    neutralTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            input.setText("");
                                            if (neutralAction != null) {
                                                neutralAction.run();
                                            }
                                        }
                                    });
                        }

                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        if (negativeTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    negativeTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null) listener.onCancel();
                                        }
                                    });
                        }
                        if (positiveTextKey != null) {
                            addDialogButton(
                                    bar,
                                    act,
                                    positiveTextKey,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null)
                                                listener.onConfirm(input.getText().toString());
                                        }
                                    });
                        }

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.setCancelable(cancelableOnTouchOutside);
                        dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
                        if (cancelableOnTouchOutside) {
                            dialog.setOnCancelListener(
                                    new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface d) {
                                            if (listener != null) listener.onCancel();
                                        }
                                    });
                        }
                        dialog.setOnShowListener(
                                new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface d) {
                                        input.requestFocus();
                                        try {
                                            InputMethodManager imm =
                                                    (InputMethodManager)
                                                            act.getSystemService(
                                                                    Context.INPUT_METHOD_SERVICE);
                                            if (imm != null)
                                                imm.showSoftInput(
                                                        input, InputMethodManager.SHOW_IMPLICIT);
                                        } catch (Throwable ignored) {
                                        }
                                    }
                                });
                        dialog.show();
                    }
                });
    }

    public static void showInputDialog(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final OnInputListener listener) {
        showInputDialogInternal(
                act,
                titleKey,
                message,
                hintKey,
                defaultText,
                lines,
                positiveTextKey,
                negativeTextKey,
                null,
                null,
                listener,
                false);
    }

    public static void showInputDialog(
            final Activity act,
            final String titleKey,
            final CharSequence message,
            final String hintKey,
            final String defaultText,
            final int lines,
            final String positiveTextKey,
            final String negativeTextKey,
            final OnInputListener listener,
            final boolean cancelableOnTouchOutside) {
        showInputDialogInternal(
                act,
                titleKey,
                message,
                hintKey,
                defaultText,
                lines,
                positiveTextKey,
                negativeTextKey,
                null,
                null,
                listener,
                cancelableOnTouchOutside);
    }

    private static EditText buildViaInputBox(
            Context ctx, String hintKey, String defaultText, int lines) {
        final EditText et = new EditText(ctx);
        if (defaultText != null) et.setText(defaultText);
        if (hintKey != null) {
            CharSequence hint = LocalizedStringProvider.getInstance().get(ctx, hintKey);
            if (hint != null) et.setHint(hint);
        }
        final int maxLines = lines > 0 ? lines : 1;
        et.setMaxLines(maxLines);
        et.setMinLines(1);
        et.setEllipsize(TextUtils.TruncateAt.END);
        if (maxLines <= 1) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            et.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            et.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }
        et.setTextColor(Hook.getTextColorStatic(ctx));
        et.setHintTextColor(Hook.getHintColorStatic(ctx));
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        et.setGravity(Gravity.TOP);
        et.setSelectAllOnFocus(maxLines <= 3);
        et.setBackgroundDrawable(createInputBoxBackground(ctx));
        et.setPadding(dp(ctx, 12), dp(ctx, 12), dp(ctx, 12), dp(ctx, 10));
        return et;
    }

    private static Drawable createInputBoxBackground(Context ctx) {
        final int insetH = dp(ctx, 12);
        final int lineH = dp(ctx, 1);
        return new Drawable() {
            private final Paint mPaint = new Paint();

            {
                mPaint.setColor(0x30808080);
                mPaint.setStyle(Paint.Style.FILL);
            }

            @Override
            public void draw(Canvas canvas) {
                final Rect b = getBounds();
                final float top = b.bottom - lineH;
                canvas.drawRect(b.left + insetH, top, b.right - insetH, b.bottom, mPaint);
            }

            @Override
            public void setAlpha(int alpha) {
                mPaint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
                mPaint.setColorFilter(cf);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };
    }

    private static LinearLayout.LayoutParams createInputBoxLayoutParams(Context ctx) {
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dp(ctx, 8);
        lp.rightMargin = dp(ctx, 8);
        return lp;
    }

    public interface UpdateDialogAction {
        void onAction(Dialog dialog);
    }

    public static void showUpdateDialog(
            final Activity act,
            final String version,
            final CharSequence updateLog,
            final UpdateDialogAction onLater,
            final UpdateDialogAction onDownload) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root =
                                createSelectDialogRoot(act, "update_dialog_title");

                        TextView versionLine = new TextView(act);
                        versionLine.setText(
                                String.format(
                                        LocalizedStringProvider.getInstance()
                                                .get(act, "new_version_found"),
                                        version));
                        versionLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        versionLine.setTextColor(Hook.getTextColorStatic(act));
                        versionLine.setPadding(dp(act, 16), dp(act, 4), dp(act, 16), dp(act, 12));
                        root.addView(versionLine);

                        TextView sectionTitle = new TextView(act);
                        sectionTitle.setText(
                                LocalizedStringProvider.getInstance().get(act, "update_log_title"));
                        sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        sectionTitle.setTextColor(SECTION_HEADER_TEXT_COLOR);
                        sectionTitle.setTypeface(null, Typeface.BOLD);
                        sectionTitle.setPadding(dp(act, 16), dp(act, 4), dp(act, 16), dp(act, 6));
                        root.addView(sectionTitle);

                        final ScrollView box = new ScrollView(act);
                        applyViaScrollStyle(box);
                        GradientDrawable boxBg = new GradientDrawable();
                        boxBg.setColor(Hook.getItemBgColorStatic(act));
                        boxBg.setCornerRadius(dp(act, 12));
                        box.setBackgroundDrawable(boxBg);
                        box.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
                        LinearLayout.LayoutParams boxLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        boxLp.setMargins(dp(act, 16), 0, dp(act, 16), dp(act, 8));
                        TextView logTv = new TextView(act);
                        logTv.setText(updateLog);
                        logTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        logTv.setTextColor(Hook.getTextColorStatic(act));
                        logTv.setLineSpacing(dp(act, 4), 1.2f);
                        logTv.setGravity(Gravity.START);
                        box.addView(logTv);
                        root.addView(box, boxLp);
                        final int maxBoxH = dp(act, 220);
                        box.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (box.getHeight() > maxBoxH) {
                                            box.getLayoutParams().height = maxBoxH;
                                            box.requestLayout();
                                        }
                                    }
                                });

                        View bodySpace = new View(act);
                        bodySpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bodySpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);
                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        final Dialog[] dialogRef = new Dialog[1];
                        addDialogButton(
                                bar,
                                act,
                                "later",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (onLater != null) onLater.onAction(dialogRef[0]);
                                    }
                                });
                        addDialogButton(
                                bar,
                                act,
                                "download_now",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (onDownload != null) onDownload.onAction(dialogRef[0]);
                                    }
                                });

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                });
    }

    public static void showButtonSelectDialog(
            final Activity act,
            final String titleKey,
            final String[] optionKeys,
            final int selectedIndex,
            final OnSelectListener listener) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, titleKey);

                        ScrollView scroll = new ScrollView(act);
                        applyViaScrollStyle(scroll);
                        final LinearLayout options = new LinearLayout(act);
                        options.setOrientation(LinearLayout.VERTICAL);
                        scroll.addView(options);
                        root.addView(scroll);

                        View bottomSpace = new View(act);
                        bottomSpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bottomSpace);

                        final Dialog[] dialogRef = new Dialog[1];

                        for (int i = 0; i < optionKeys.length; i++) {
                            final int idx = i;
                            final TextView option = new TextView(act);
                            option.setText(
                                    LocalizedStringProvider.getInstance().get(act, optionKeys[i]));
                            option.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            option.setTextColor(Hook.getTextColorStatic(act));
                            option.setSingleLine(true);
                            option.setEllipsize(TextUtils.TruncateAt.END);
                            option.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
                            if (i == selectedIndex) {
                                option.setBackgroundColor(RIPPLE_COLOR);
                            }
                            option.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogRef[0] != null) dialogRef[0].dismiss();
                                            if (listener != null) listener.onSelect(idx);
                                        }
                                    });
                            options.addView(
                                    option,
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                        }

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.show();
                    }
                });
    }

    public static void showStorageManagerDialog(
            final Activity act,
            final String cacheSizeStr,
            final Runnable onClearCache,
            final Runnable onClearData) {
        if (act == null) return;
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        final LinearLayout root = createSelectDialogRoot(act, "storage_title");

                        ScrollView scroll = new ScrollView(act);
                        applyViaScrollStyle(scroll);
                        final LinearLayout body = new LinearLayout(act);
                        body.setOrientation(LinearLayout.VERTICAL);
                        scroll.addView(body);
                        root.addView(scroll);

                        LinearLayout cacheHeaderRow = new LinearLayout(act);
                        cacheHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
                        cacheHeaderRow.setGravity(Gravity.CENTER_VERTICAL);
                        cacheHeaderRow.setPadding(
                                dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 4));
                        TextView cacheHeader = new TextView(act);
                        cacheHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        cacheHeader.setTextColor(SECTION_HEADER_TEXT_COLOR);
                        cacheHeader.setText(
                                LocalizedStringProvider.getInstance()
                                        .get(act, "storage_cache_title"));
                        cacheHeaderRow.addView(cacheHeader);
                        final TextView sizeText = new TextView(act);
                        sizeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        sizeText.setTextColor(Hook.getHintColorStatic(act));
                        sizeText.setText(cacheSizeStr);
                        LinearLayout.LayoutParams sizeLp =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        sizeLp.leftMargin = dp(act, 8);
                        sizeText.setLayoutParams(sizeLp);
                        cacheHeaderRow.addView(sizeText);
                        body.addView(cacheHeaderRow);

                        TextView clearCacheBtn = createDialogListItem(act, "storage_clear");
                        clearCacheBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (onClearCache != null) onClearCache.run();
                                        sizeText.setText("0 B");
                                    }
                                });
                        body.addView(clearCacheBtn);

                        body.addView(createDialogSectionHeader(act, "storage_clear_data_title"));

                        TextView clearDataBtn = createDialogListItem(act, "storage_clear");
                        clearDataBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showMessageDialog(
                                                act,
                                                "storage_confirm_title",
                                                (CharSequence)
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        act,
                                                                        "storage_confirm_message"),
                                                null,
                                                false,
                                                "storage_confirm_delete",
                                                "dialog_cancel",
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (onClearData != null) onClearData.run();
                                                    }
                                                },
                                                null,
                                                null,
                                                false);
                                    }
                                });
                        body.addView(clearDataBtn);

                        View bodySpace = new View(act);
                        bodySpace.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 12)));
                        root.addView(bodySpace);

                        LinearLayout bar = new LinearLayout(act);
                        bar.setOrientation(LinearLayout.HORIZONTAL);
                        bar.setGravity(Gravity.END);
                        root.addView(bar);
                        final Dialog[] dialogRef = new Dialog[1];
                        View spacer = new View(act);
                        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
                        bar.addView(spacer);
                        addDialogButton(
                                bar,
                                act,
                                "dialog_close",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialogRef[0] != null) dialogRef[0].dismiss();
                                    }
                                });

                        final Dialog dialog = createSelectDialog(act, root);
                        dialogRef[0] = dialog;
                        dialog.show();
                    }
                });
    }

    private static TextView createDialogListItem(Activity act, String textKey) {
        TextView item = new TextView(act);
        item.setText(LocalizedStringProvider.getInstance().get(act, textKey));
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        item.setTextColor(Hook.getTextColorStatic(act));
        item.setSingleLine(true);
        item.setEllipsize(TextUtils.TruncateAt.END);
        item.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
        item.setBackgroundDrawable(createRippleDrawable(act));
        return item;
    }

    private static TextView createDialogSectionHeader(Activity act, String textKey) {
        TextView header = new TextView(act);
        header.setText(LocalizedStringProvider.getInstance().get(act, textKey));
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        header.setTextColor(SECTION_HEADER_TEXT_COLOR);
        header.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 4));
        return header;
    }

    private static void dismissAndNotify(
            Dialog dialog, OnMultiSelectListener listener, int which, CheckBox[] boxes) {
        if (dialog != null) dialog.dismiss();
        if (listener != null) listener.onResult(which, collectChecked(boxes));
    }

    private static CheckBox addSelectOption(
            LinearLayout parent,
            final Activity act,
            String optionKey,
            boolean checked,
            final View.OnClickListener clickListener) {
        FrameLayout wrapper = new FrameLayout(act);
        wrapper.setPadding(dp(act, 16), 0, dp(act, 16), 0);
        wrapper.setBackground(createRippleDrawable(act));

        final CheckBox option = new CheckBox(act);
        option.setButtonDrawable(createCircleToggleDrawable(act));
        option.setText(LocalizedStringProvider.getInstance().get(act, optionKey));
        option.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        option.setTextColor(Hook.getTextColorStatic(act));
        option.setSingleLine(true);
        option.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        option.setPadding(dp(act, 12), dp(act, 12), 0, dp(act, 12));
        option.setCompoundDrawablePadding(dp(act, 24));
        option.setChecked(checked);
        option.setClickable(false);
        option.setFocusable(false);
        wrapper.addView(
                option,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        wrapper.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onClick(option);
                        } else {
                            option.setChecked(!option.isChecked());
                        }
                    }
                });
        parent.addView(
                wrapper,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return option;
    }

    private static void addDialogButton(
            LinearLayout bar, Activity act, String textKey, View.OnClickListener clickListener) {
        TextView btn = new TextView(act);
        btn.setText(LocalizedStringProvider.getInstance().get(act, textKey));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btn.setTextColor(DIALOG_BUTTON_TEXT_COLOR);
        btn.setGravity(Gravity.CENTER);
        btn.setSingleLine(true);
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
        btn.setMinWidth(dp(act, 56));
        GradientDrawable pressed = new GradientDrawable();
        pressed.setShape(GradientDrawable.RECTANGLE);
        pressed.setColor(DIALOG_BUTTON_PRESSED_COLOR);
        pressed.setCornerRadius(dp(act, 18));
        StateListDrawable sld = new StateListDrawable();
        sld.addState(
                new int[] {android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        btn.setBackgroundDrawable(sld);
        btn.setOnClickListener(clickListener);
        bar.addView(btn);
    }

    private static void addTopBarButton(
            LinearLayout bar, Activity act, String textKey, View.OnClickListener clickListener) {
        TextView btn = new TextView(act);
        btn.setText(LocalizedStringProvider.getInstance().get(act, textKey));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btn.setTextColor(Hook.getTextColorStatic(act));
        btn.setGravity(Gravity.CENTER);
        btn.setSingleLine(true);
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setPadding(dp(act, 16), dp(act, 16), dp(act, 16), dp(act, 16));
        btn.setMinWidth(dp(act, 56));
        GradientDrawable pressed = new GradientDrawable();
        pressed.setShape(GradientDrawable.RECTANGLE);
        pressed.setColor(DIALOG_BUTTON_PRESSED_COLOR);
        pressed.setCornerRadius(dp(act, 18));
        StateListDrawable sld = new StateListDrawable();
        sld.addState(
                new int[] {android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        btn.setBackgroundDrawable(sld);
        btn.setOnClickListener(clickListener);
        bar.addView(btn);
    }

    private static boolean[] collectChecked(CheckBox[] boxes) {
        boolean[] result = new boolean[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            result[i] = boxes[i].isChecked();
        }
        return result;
    }

    private static LinearLayout buildTitleBar(
            final Activity act,
            CharSequence titleText,
            final View rootLayout,
            String rightButtonTextKey,
            final Runnable rightButtonAction) {
        LinearLayout wrapper = new LinearLayout(act);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setBackgroundColor(Hook.getBgColorStatic(act));
        wrapper.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout titleBar = new LinearLayout(act);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 54)));

        ImageButton backButton = new ImageButton(act);
        Drawable backIcon = ViaIconLoader.loadBackIcon(act);
        if (backIcon != null) {
            backButton.setImageDrawable(backIcon);
        } else {
            Bitmap fallbackIcon = decodeBackIcon();
            if (fallbackIcon != null) {
                backButton.setImageBitmap(fallbackIcon);
            } else {
                backButton.setImageResource(android.R.drawable.ic_menu_revert);
            }
        }
        GradientDrawable backMask = new GradientDrawable();
        backMask.setShape(GradientDrawable.RECTANGLE);
        backMask.setCornerRadius(dp(act, 18));
        backMask.setColor(Color.BLACK);
        RippleDrawable backRipple =
                new RippleDrawable(
                        ColorStateList.valueOf(DIALOG_BUTTON_PRESSED_COLOR), null, backMask);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                RippleDrawable.class
                        .getMethod("setExitFadeDuration", int.class)
                        .invoke(backRipple, Integer.valueOf(300));
            }
        } catch (Throwable ignored) {
        }
        backButton.setBackground(backRipple);
        backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        backButton.setPadding(dp(act, 13), 0, dp(act, 13), 0);
        backButton.setColorFilter(Hook.getTextColorStatic(act));
        titleBar.addView(
                backButton,
                new LinearLayout.LayoutParams(dp(act, 48), ViewGroup.LayoutParams.MATCH_PARENT));
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissWithSlideOut(rootLayout, act);
                    }
                });

        TextView title = new TextView(act);
        title.setText(titleText);
        title.setTextColor(Hook.getTextColorStatic(act));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTypeface(null, Typeface.BOLD);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        titleBar.addView(
                title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        if (rightButtonTextKey != null && rightButtonAction != null) {
            addTopBarButton(
                    titleBar,
                    act,
                    rightButtonTextKey,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rightButtonAction.run();
                        }
                    });
        }

        wrapper.addView(titleBar);

        View divider = new View(act);
        divider.setBackgroundColor(Hook.getDividerColorStatic(act));
        int dividerPx =
                Math.max(
                        1,
                        (int)
                                TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_PX,
                                        2,
                                        act.getResources().getDisplayMetrics()));
        wrapper.addView(
                divider,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerPx));

        return wrapper;
    }

    private static Bitmap decodeBackIcon() {
        try {
            byte[] data = Base64.decode(BACK_ICON_BASE64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                bitmap.setDensity(DisplayMetrics.DENSITY_XXXHIGH);
            }
            return bitmap;
        } catch (Throwable t) {
            return null;
        }
    }

    static RippleDrawable createRippleDrawable(Context ctx) {
        RippleDrawable ripple =
                new RippleDrawable(
                        ColorStateList.valueOf(RIPPLE_COLOR), null, new ColorDrawable(Color.WHITE));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                RippleDrawable.class
                        .getMethod("setExitFadeDuration", int.class)
                        .invoke(ripple, Integer.valueOf(300));
            }
        } catch (Throwable ignored) {
        }
        return ripple;
    }

    private static void animateSlideInRight(final View view, Activity act) {
        if (view == null || act == null) return;
        final int slideIn = dp(act, PAGE_SLIDE_DISTANCE_DP);
        final int parallax = dp(act, PAGE_PARALLAX_DP);
        if (!pageStack.isEmpty()) {
            final View below = pageStack.get(pageStack.size() - 1);
            if (below instanceof SwipeBackLayout) {
                ((SwipeBackLayout) below)
                        .startTransition(
                                parallax,
                                -1f,
                                new AccelerateInterpolator(),
                                PAGE_TRANSITION_DURATION_MS,
                                null);
            } else {
                final float belowStart = below.getTranslationX();
                runFrameTransition(
                        below,
                        PAGE_TRANSITION_DURATION_MS,
                        new AccelerateInterpolator(),
                        new FrameTransitionListener() {
                            @Override
                            public void onTransition(float eased, float raw, boolean finished) {
                                below.setTranslationX(belowStart + (parallax - belowStart) * eased);
                            }
                        });
            }
        }
        if (view instanceof SwipeBackLayout) {
            ((SwipeBackLayout) view).startEnterTransition(slideIn);
        } else {
            view.setTranslationX(slideIn);
            view.setAlpha(0f);
            runFrameTransition(
                    view,
                    PAGE_TRANSITION_DURATION_MS,
                    new AccelerateDecelerateInterpolator(),
                    new FrameTransitionListener() {
                        @Override
                        public void onTransition(float eased, float raw, boolean finished) {
                            view.setTranslationX(slideIn * (1f - eased));
                            view.setAlpha(raw);
                        }
                    });
        }
        pageStack.add(view);
        syncPredictiveBackCallback(act, true);
    }

    private interface FrameTransitionListener {
        void onTransition(float eased, float raw, boolean finished);
    }

    private static final class MaxHeightScrollView extends ScrollView {

        private final int maxHeight;

        MaxHeightScrollView(Context context, int maxHeight) {
            super(context);
            this.maxHeight = maxHeight;
            setVerticalScrollBarEnabled(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (getMeasuredHeight() > maxHeight)
                setMeasuredDimension(getMeasuredWidth(), maxHeight);
        }
    }

    private static final class FrameTransition {
        Choreographer.FrameCallback callback;

        void cancel() {
            if (callback != null) {
                Choreographer.getInstance().removeFrameCallback(callback);
                callback = null;
            }
        }
    }

    private static FrameTransition runFrameTransition(
            final View view,
            final long durationMs,
            final Interpolator interpolator,
            final FrameTransitionListener listener) {
        final FrameTransition handle = new FrameTransition();
        if (view == null) {
            return handle;
        }
        final Choreographer choreographer = Choreographer.getInstance();
        final long durationNanos = Math.max(1L, durationMs) * 1000000L;
        final long[] startNanos = new long[1];
        final float[] lastStep = new float[1];
        Choreographer.FrameCallback callback =
                new Choreographer.FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        if (startNanos[0] == 0L) {
                            startNanos[0] = frameTimeNanos;
                        }
                        float raw = (frameTimeNanos - startNanos[0]) / (float) durationNanos;
                        if (raw > 1f) {
                            raw = 1f;
                        }
                        if (raw - lastStep[0] > TRANSITION_MAX_STEP_PER_FRAME) {
                            raw = lastStep[0] + TRANSITION_MAX_STEP_PER_FRAME;
                        }
                        lastStep[0] = raw;
                        boolean finished = raw >= 1f;
                        if (listener != null) {
                            listener.onTransition(
                                    interpolator.getInterpolation(raw), raw, finished);
                        }
                        if (!finished) {
                            choreographer.postFrameCallback(this);
                        } else {
                            handle.callback = null;
                        }
                    }
                };
        handle.callback = callback;
        choreographer.postFrameCallback(callback);
        return handle;
    }

    private static void dismissWithSlideOut(final View rootLayout, final Activity act) {
        if (rootLayout == null || act == null) {
            return;
        }
        if (isVersionSelectorPage(rootLayout) && isExitCancelHintEnabled(rootLayout)) {
            showVersionSelectorCancelHint(act);
        }
        final boolean isLastPage = pageStack.size() <= 1;
        final boolean restoreFullscreen = isLastPage && savedWasFullscreen;
        final int slideOut = dp(act, PAGE_SLIDE_DISTANCE_DP);
        if (!pageStack.isEmpty()) {
            pageStack.remove(pageStack.size() - 1);
        }
        syncPredictiveBackCallback(act);
        final Runnable onExitEnd =
                new Runnable() {
                    @Override
                    public void run() {
                        removePageFromWindow(rootLayout, act);
                        if (isLastPage && act.getWindow() != null) {
                            if (restoreFullscreen) {
                                ViaSystemUi.applyFullscreenModule(
                                        act.getWindow(), savedWasFullscreen);
                            }
                            restoreImeMode(act.getWindow());
                        }
                    }
                };
        if (rootLayout instanceof SwipeBackLayout) {
            ((SwipeBackLayout) rootLayout)
                    .startTransition(
                            slideOut,
                            0f,
                            new AccelerateInterpolator(),
                            PAGE_TRANSITION_DURATION_MS,
                            onExitEnd);
        } else {
            rootLayout
                    .animate()
                    .translationX(slideOut)
                    .setDuration(PAGE_TRANSITION_DURATION_MS)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(
                            new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    onExitEnd.run();
                                }
                            })
                    .start();
            ObjectAnimator.ofFloat(rootLayout, "alpha", 1f, 0f)
                    .setDuration(PAGE_TRANSITION_DURATION_MS)
                    .start();
        }
        if (!pageStack.isEmpty()) {
            View below = pageStack.get(pageStack.size() - 1);
            if (below instanceof SwipeBackLayout) {
                ((SwipeBackLayout) below)
                        .startTransition(
                                0f,
                                -1f,
                                new AccelerateDecelerateInterpolator(),
                                PAGE_TRANSITION_DURATION_MS,
                                null);
            } else {
                below.animate()
                        .translationX(0f)
                        .setDuration(PAGE_TRANSITION_DURATION_MS)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }
        }
    }

    private static void removePageFromWindow(View rootLayout, Activity act) {
        try {
            ViewParent parent = rootLayout.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(rootLayout);
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 移除页面失败: " + t);
        }
    }

    private static boolean isVersionSelectorPage(View page) {
        if (page == null) return false;
        Object tag = page.getTag();
        return "version_selector_title".equals(tag);
    }

    private static boolean isExitCancelHintEnabled(View page) {
        return page instanceof SwipeBackLayout && ((SwipeBackLayout) page).exitCancelHintEnabled;
    }

    private static void showVersionSelectorCancelHint(Activity act) {
        if (act == null) return;
        try {
            Hook.jiguroMessageWithContext(
                    act,
                    LocalizedStringProvider.getInstance().get(act, "version_selector_cancel_hint"));
        } catch (Throwable ignored) {
        }
    }

    public static boolean isPageActive() {
        return !pageStack.isEmpty();
    }

    public static void dismissCurrentPage(Activity act) {
        if (pageStack.isEmpty()) return;
        final View top = pageStack.get(pageStack.size() - 1);
        if (top == null) return;
        if (consumeBackByPage(top)) {
            return;
        }
        if (act != null) {
            dismissWithSlideOut(top, act);
        }
    }

    private static boolean consumeBackByPage(View page) {
        if (page == null) {
            return false;
        }
        PageBackInterceptor interceptor = pageBackInterceptors.get(page);
        return interceptor != null && interceptor.onBackPressed();
    }

    public static void dismissAllPages() {
        final List<View> stackCopy = new ArrayList<View>(pageStack);
        pageStack.clear();
        pageBackInterceptors.clear();
        unregisterPredictiveBackCallback();
        sHostActivity = null;
        Activity act = null;
        for (int i = stackCopy.size() - 1; i >= 0; i--) {
            View v = stackCopy.get(i);
            if (v == null) continue;
            Context c = v.getContext();
            if (c instanceof Activity) {
                act = (Activity) c;
            }
            removePageFromWindow(v, act);
        }
        if (act != null && act.getWindow() != null) {
            try {
                ViaSystemUi.applyFullscreenModule(act.getWindow(), savedWasFullscreen);
            } catch (Throwable t) {
            }
            restoreImeMode(act.getWindow());
        }
    }

    public static void notifyThemeChanged(Context ctx) {
        applyThemeToAllOpenPages(ctx);
    }

    private static void applyThemeToAllOpenPages(Context ctx) {
        applyThemeToAllOpenPages(ctx, false);
    }

    private static void applyThemeToAllOpenPages(Context ctx, boolean force) {
        try {
            String actual = ThemeColors.getActualTheme(ctx);
            if (!force && actual.equals(lastAppliedActualTheme)) return;
            lastAppliedActualTheme = actual;
            for (View v : new ArrayList<View>(pageStack)) {
                if (v instanceof SwipeBackLayout) {
                    reapplyThemeToPage((SwipeBackLayout) v);
                }
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 重应用主题失败: " + t);
        }
    }

    private static void reapplyThemeToPage(SwipeBackLayout page) {
        if (page == null) return;
        Activity act = page.act;
        if (act == null) return;
        page.setBackgroundColor(Hook.getBgColorStatic(act));
        if (page.getChildCount() >= 2) {
            View titleWrapper = page.getChildAt(0);
            if (titleWrapper instanceof ViewGroup) {
                titleWrapper.setBackgroundColor(Hook.getBgColorStatic(act));
                ViewGroup titleBar = (ViewGroup) ((ViewGroup) titleWrapper).getChildAt(0);
                if (titleBar.getChildCount() >= 1
                        && titleBar.getChildAt(0) instanceof ImageButton) {
                    ((ImageButton) titleBar.getChildAt(0))
                            .setColorFilter(Hook.getTextColorStatic(act));
                }
                for (int i = 1; i < titleBar.getChildCount(); i++) {
                    View c = titleBar.getChildAt(i);
                    if (c instanceof TextView) {
                        ((TextView) c).setTextColor(Hook.getTextColorStatic(act));
                    }
                }
                View divider = ((ViewGroup) titleWrapper).getChildAt(1);
                if (divider instanceof View) {
                    divider.setBackgroundColor(Hook.getDividerColorStatic(act));
                }
            }
            reapplyThemeToContent(page.getChildAt(1), act);
        }
        try {
            applyViaSystemBarColor(act, act.getWindow());
        } catch (Throwable ignored) {
        }
    }

    private static void reapplyThemeToContent(View content, Activity act) {
        if (content instanceof SettingsList) {
            ((SettingsList) content).reapplyTheme();
            return;
        }
        if (content instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) content;
            for (int i = 0; i < vg.getChildCount(); i++) {
                reapplyThemeToContent(vg.getChildAt(i), act);
            }
        }
    }

    private static void ensureThemeReceiver(Context ctx) {
        if (themeReceiverRegistered) return;
        try {
            Context app = ctx.getApplicationContext();
            BroadcastReceiver r =
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(final Context c, Intent intent) {
                            try {
                                if (!ThemeColors.THEME_AUTO.equals(ThemeColors.getTheme(c))) return;
                                new Handler(Looper.getMainLooper())
                                        .postDelayed(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        applyThemeToAllOpenPages(c, true);
                                                    }
                                                },
                                                300);
                            } catch (Throwable ignored) {
                            }
                        }
                    };
            app.registerReceiver(r, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
            themeReceiverRegistered = true;
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 注册主题监听失败: " + t);
        }
    }

    private static final int VIA_APPFLAG_DISABLE_PREDICTIVE_BACK = 0x80000;
    private static final String VIA_SETTINGS_PREFS = "settings";
    private static final String VIA_KEY_APPFLAG = "appflag";
    private static boolean sViaPredictiveBackEnabled = false;

    static boolean isViaPredictiveBackEnabled(Context ctx) {
        if (ctx == null) {
            return sViaPredictiveBackEnabled;
        }
        if (!ViaClassMapping.hasPredictiveBackSwitch(ctx)) {
            sViaPredictiveBackEnabled = false;
            return false;
        }
        try {
            int flag =
                    ctx.getApplicationContext()
                            .getSharedPreferences(VIA_SETTINGS_PREFS, Context.MODE_PRIVATE)
                            .getInt(VIA_KEY_APPFLAG, 0);
            sViaPredictiveBackEnabled = (flag & VIA_APPFLAG_DISABLE_PREDICTIVE_BACK) == 0;
        } catch (Throwable t) {
        }
        return sViaPredictiveBackEnabled;
    }

    static boolean isViaPredictiveBackOptedOut(Context ctx) {
        return !isViaPredictiveBackEnabled(ctx);
    }

    static void resyncPredictiveBackCallback() {
        if (sHostActivity == null || !isPageActive()) return;
        final Activity act = sHostActivity;
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                syncPredictiveBackCallback(act, true);
                            }
                        });
    }

    private static void syncPredictiveBackCallback(Activity act) {
        syncPredictiveBackCallback(act, false);
    }

    private static void syncPredictiveBackCallback(Activity act, boolean force) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        try {
            if (act != null) sHostActivity = act;
            if (pageStack.isEmpty() || act == null) {
                unregisterPredictiveBackCallback();
                return;
            }
            final boolean animated = isViaPredictiveBackEnabled(act);
            if (!force
                    && predictiveBackCallback != null
                    && predictiveBackActivity == act
                    && predictiveBackAnimated == animated) return;
            unregisterPredictiveBackCallback();
            predictiveBackActivity = act;
            predictiveBackAnimated = animated;
            final OnBackInvokedCallback cb = createPredictiveBackCallback(act, animated);
            predictiveBackCallback = cb;
            act.getOnBackInvokedDispatcher()
                    .registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, cb);
            Hook.bvLog("[BetterVia] 已注册预测性返回回调（animated=" + animated + "）");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 注册预测性返回回调失败: " + t);
            predictiveBackCallback = null;
            predictiveBackActivity = null;
        }
    }

    private static void unregisterPredictiveBackCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            predictiveBackCallback = null;
            predictiveBackActivity = null;
            predictiveBackAnimated = false;
            return;
        }
        try {
            if (predictiveBackCallback != null && predictiveBackActivity != null) {
                predictiveBackActivity
                        .getOnBackInvokedDispatcher()
                        .unregisterOnBackInvokedCallback(
                                (OnBackInvokedCallback) predictiveBackCallback);
            }
        } catch (Throwable ignored) {
        }
        predictiveBackCallback = null;
        predictiveBackActivity = null;
        predictiveBackAnimated = false;
    }

    private static OnBackInvokedCallback createPredictiveBackCallback(
            final Activity act, final boolean animated) {
        if (animated && Build.VERSION.SDK_INT >= 34) {
            try {
                final Class<?> animCb = Class.forName("android.window.OnBackAnimationCallback");
                return (OnBackInvokedCallback)
                        Proxy.newProxyInstance(
                                SettingsUI.class.getClassLoader(),
                                new Class<?>[] {animCb},
                                new InvocationHandler() {
                                    private SwipeBackLayout topPage;
                                    private boolean backConsumed;
                                    private Method getProgressMethod;

                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args)
                                            throws Throwable {
                                        String name = method.getName();
                                        if ("onBackStarted".equals(name)) {
                                            topPage = topPredictivePage();
                                            backConsumed = false;
                                            if (topPage != null && consumeBackByPage(topPage)) {
                                                backConsumed = true;
                                                topPage = null;
                                                return null;
                                            }
                                            if (topPage != null) topPage.beginPredictiveDrag();
                                        } else if ("onBackProgressed".equals(name)
                                                && args != null
                                                && args.length > 0) {
                                            if (topPage != null)
                                                topPage.predictiveDragTo(readBackProgress(args[0]));
                                        } else if ("onBackCancelled".equals(name)) {
                                            if (topPage != null) topPage.predictiveCancel();
                                            topPage = null;
                                            backConsumed = false;
                                        } else if ("onBackInvoked".equals(name)) {
                                            if (backConsumed) {
                                                backConsumed = false;
                                                topPage = null;
                                                return null;
                                            }
                                            if (topPage != null) topPage.predictiveCommit();
                                            else dismissCurrentPage(act);
                                            topPage = null;
                                        } else if ("toString".equals(name)) {
                                            return "BetterViaPredictiveBackCallback";
                                        } else if ("hashCode".equals(name)) {
                                            return Integer.valueOf(System.identityHashCode(proxy));
                                        } else if ("equals".equals(name)) {
                                            return Boolean.valueOf(
                                                    proxy
                                                            == (args != null && args.length > 0
                                                                    ? args[0]
                                                                    : null));
                                        }
                                        return null;
                                    }

                                    private float readBackProgress(Object backEvent) {
                                        try {
                                            if (getProgressMethod == null)
                                                getProgressMethod =
                                                        backEvent
                                                                .getClass()
                                                                .getMethod("getProgress");
                                            return ((Float) getProgressMethod.invoke(backEvent))
                                                    .floatValue();
                                        } catch (Throwable t) {
                                            return 1f;
                                        }
                                    }
                                });
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] 创建预测性返回动画回调失败，回退普通回调: " + t);
            }
        }
        return new OnBackInvokedCallback() {
            @Override
            public void onBackInvoked() {
                dismissCurrentPage(act);
            }
        };
    }

    private static SwipeBackLayout topPredictivePage() {
        if (pageStack.isEmpty()) return null;
        View top = pageStack.get(pageStack.size() - 1);
        return top instanceof SwipeBackLayout ? (SwipeBackLayout) top : null;
    }

    private static void shakeView(View view) {
        if (view == null) return;
        float dx = dp(view.getContext(), 24);
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationX", 0f, dx, 0f);
        anim.setRepeatCount(2);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setDuration(280);
        anim.setInterpolator(new PathInterpolator(0.2f, 0.2f, 0.8f, 0.8f));
        anim.start();
    }

    static int dp(Context ctx, int dp) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    private static final class SwipeBackLayout extends LinearLayout {
        private final Activity act;
        private final int touchSlop;
        private float startRawX;
        private float startRawY;
        private boolean dragging;
        private View belowView;
        private FrameTransition transition;
        private boolean entering;
        private float belowBaseX;
        private VelocityTracker velocityTracker;
        private ScrimView scrim;
        private boolean exitCancelHintEnabled = false;

        private void setExitCancelHintEnabled(boolean enabled) {
            this.exitCancelHintEnabled = enabled;
        }

        private static final int SCRIM_ALPHA = 0x40;
        private static final float DISMISS_FRACTION = 0.46f;
        private static final float DISMISS_FLING_FRACTION = 0.46f * 0.4f;
        private static final float FLING_VELOCITY = 300f;

        private static final Interpolator QUINTIC_EASE_OUT =
                new Interpolator() {
                    @Override
                    public float getInterpolation(float t) {
                        float x = t - 1f;
                        return x * x * x * x * x + 1f;
                    }
                };

        private final int maxFling;

        private SwipeBackLayout(Activity act) {
            super(act);
            this.act = act;
            this.touchSlop = ViewConfiguration.get(act).getScaledTouchSlop();
            this.maxFling = ViewConfiguration.get(act).getScaledMaximumFlingVelocity();
            setOrientation(LinearLayout.VERTICAL);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRawX = ev.getRawX();
                    startRawY = ev.getRawY();
                    dragging = false;
                    belowView = null;
                    scrim = null;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (entering) {
                        return false;
                    }
                    if (!dragging) {
                        float dx = ev.getRawX() - startRawX;
                        float dy = ev.getRawY() - startRawY;
                        if (dx > touchSlop && dx > Math.abs(dy)) {
                            dragging = true;
                            beginDrag();
                            return true;
                        }
                    }
                    return false;
                default:
                    return false;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (!dragging) return false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    {
                        float dx = ev.getRawX() - startRawX;
                        if (dx < 0f) dx = 0f;
                        setTranslationX(dx);
                        float w = getWidth() > 0 ? getWidth() : 1;
                        float progress = Math.min(1f, dx / w);
                        if (belowView != null) {
                            belowView.setTranslationX(belowBaseX * (1f - progress));
                        }
                        updateScrim(dx, w, progress);
                        if (velocityTracker != null) velocityTracker.addMovement(ev);
                        return true;
                    }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    {
                        float dx = ev.getRawX() - startRawX;
                        if (dx < 0f) dx = 0f;
                        float w = getWidth() > 0 ? getWidth() : 1;
                        float progress = Math.min(1f, dx / w);
                        boolean dismiss = false;
                        float vx = 0f, vy = 0f;
                        if (velocityTracker != null) {
                            velocityTracker.addMovement(ev);
                            velocityTracker.computeCurrentVelocity(1000);
                            vx = velocityTracker.getXVelocity();
                            vy = velocityTracker.getYVelocity();
                            velocityTracker.recycle();
                            velocityTracker = null;
                        }
                        if (progress > DISMISS_FRACTION) {
                            dismiss = true;
                        } else if (vx > FLING_VELOCITY
                                && Math.abs(vy) <= Math.abs(vx)
                                && progress > DISMISS_FLING_FRACTION) {
                            dismiss = true;
                        }
                        dragging = false;
                        if (dismiss) {
                            swipeDismiss(vx);
                        } else {
                            int dur = computeSettleDuration(getTranslationX(), vx);
                            startTransition(0f, -1f, QUINTIC_EASE_OUT, dur, null);
                            if (belowView != null) {
                                if (belowView instanceof SwipeBackLayout) {
                                    ((SwipeBackLayout) belowView)
                                            .startTransition(
                                                    belowBaseX, -1f, QUINTIC_EASE_OUT, dur, null);
                                } else {
                                    belowView
                                            .animate()
                                            .translationX(belowBaseX)
                                            .setDuration(dur)
                                            .setInterpolator(QUINTIC_EASE_OUT)
                                            .start();
                                }
                            }
                            animateScrimTo(0f, dur);
                        }
                        return true;
                    }
                default:
                    return false;
            }
        }

        void startEnterTransition(final int slideIn) {
            cancelTransition();
            entering = true;
            setTranslationX(slideIn);
            setAlpha(0f);
            transition =
                    runFrameTransition(
                            this,
                            PAGE_TRANSITION_DURATION_MS,
                            new AccelerateDecelerateInterpolator(),
                            new FrameTransitionListener() {
                                @Override
                                public void onTransition(float eased, float raw, boolean finished) {
                                    setTranslationX(slideIn * (1f - eased));
                                    setAlpha(raw);
                                    if (finished) {
                                        entering = false;
                                    }
                                }
                            });
        }

        void startTransition(
                final float targetX,
                final float targetAlpha,
                final Interpolator interpolator,
                final long durationMs,
                final Runnable onEnd) {
            cancelTransition();
            final float startX = getTranslationX();
            final float startAlpha = getAlpha();
            transition =
                    runFrameTransition(
                            this,
                            durationMs,
                            interpolator,
                            new FrameTransitionListener() {
                                @Override
                                public void onTransition(float eased, float raw, boolean finished) {
                                    setTranslationX(startX + (targetX - startX) * eased);
                                    if (targetAlpha >= 0f) {
                                        setAlpha(startAlpha + (targetAlpha - startAlpha) * raw);
                                    }
                                    if (finished && onEnd != null) {
                                        onEnd.run();
                                    }
                                }
                            });
        }

        void cancelTransition() {
            if (transition != null) {
                transition.cancel();
                transition = null;
            }
            entering = false;
        }

        private void beginDrag() {
            cancelTransition();
            if (pageStack.size() > 1) {
                belowView = pageStack.get(pageStack.size() - 2);
                if (belowView != null) {
                    if (belowView instanceof SwipeBackLayout) {
                        ((SwipeBackLayout) belowView).cancelTransition();
                    } else {
                        belowView.animate().cancel();
                    }
                    belowBaseX = belowView.getTranslationX();
                } else {
                    belowBaseX = dp(act, PAGE_PARALLAX_DP);
                }
            } else {
                belowView = null;
            }
            velocityTracker = VelocityTracker.obtain();
            ensureScrim();
        }

        private void swipeDismiss(float vx) {
            cancelTransition();
            final View cur = this;
            if (isVersionSelectorPage(cur) && isExitCancelHintEnabled(cur)) {
                showVersionSelectorCancelHint(act);
            }
            final int off = getWidth() + 10;
            if (!pageStack.isEmpty()) {
                pageStack.remove(cur);
            }
            syncPredictiveBackCallback(act);
            final boolean isLastPage = pageStack.isEmpty();
            final boolean restore = isLastPage && savedWasFullscreen;
            final int dur = computeSettleDuration(off, vx);
            if (!pageStack.isEmpty()) {
                final View below = pageStack.get(pageStack.size() - 1);
                if (below instanceof SwipeBackLayout) {
                    ((SwipeBackLayout) below).startTransition(0f, -1f, QUINTIC_EASE_OUT, dur, null);
                } else {
                    below.animate()
                            .translationX(0f)
                            .setDuration(dur)
                            .setInterpolator(QUINTIC_EASE_OUT)
                            .start();
                }
            }
            final Runnable onSwipeEnd =
                    new Runnable() {
                        @Override
                        public void run() {
                            removePageFromWindow(cur, act);
                            if (isLastPage && act.getWindow() != null) {
                                if (restore) {
                                    ViaSystemUi.applyFullscreenModule(
                                            act.getWindow(), savedWasFullscreen);
                                }
                                restoreImeMode(act.getWindow());
                            }
                        }
                    };
            if (cur instanceof SwipeBackLayout) {
                ((SwipeBackLayout) cur)
                        .startTransition(off, -1f, QUINTIC_EASE_OUT, dur, onSwipeEnd);
            } else {
                cur.animate()
                        .translationX(off)
                        .setDuration(dur)
                        .setInterpolator(QUINTIC_EASE_OUT)
                        .setListener(
                                new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        onSwipeEnd.run();
                                    }
                                })
                        .start();
            }
            animateScrimTo(off, dur);
        }

        void beginPredictiveDrag() {
            cancelTransition();
            if (pageStack.size() > 1) {
                belowView = pageStack.get(pageStack.size() - 2);
                if (belowView != null) {
                    if (belowView instanceof SwipeBackLayout) {
                        ((SwipeBackLayout) belowView).cancelTransition();
                    } else {
                        belowView.animate().cancel();
                    }
                    belowBaseX = belowView.getTranslationX();
                } else {
                    belowBaseX = dp(act, PAGE_PARALLAX_DP);
                }
            } else {
                belowView = null;
            }
            ensureScrim();
        }

        void predictiveDragTo(float progress) {
            if (progress < 0f) progress = 0f;
            else if (progress > 1f) progress = 1f;
            float w = getWidth() > 0 ? getWidth() : 1;
            float dx = progress * w;
            setTranslationX(dx);
            if (belowView != null) {
                belowView.setTranslationX(belowBaseX * (1f - progress));
            }
            updateScrim(dx, w, progress);
        }

        void predictiveCommit() {
            if (pageStack.isEmpty() || pageStack.get(pageStack.size() - 1) != this) {
                predictiveCancel();
                return;
            }
            swipeDismiss(0f);
            belowView = null;
        }

        void predictiveCancel() {
            int dur = computeSettleDuration(getTranslationX(), 0f);
            startTransition(0f, -1f, QUINTIC_EASE_OUT, dur, null);
            if (belowView != null) {
                if (belowView instanceof SwipeBackLayout) {
                    ((SwipeBackLayout) belowView)
                            .startTransition(belowBaseX, -1f, QUINTIC_EASE_OUT, dur, null);
                } else {
                    belowView
                            .animate()
                            .translationX(belowBaseX)
                            .setDuration(dur)
                            .setInterpolator(QUINTIC_EASE_OUT)
                            .start();
                }
            }
            animateScrimTo(0f, dur);
            belowView = null;
        }

        private int computeSettleDuration(float delta, float vx) {
            int w = getWidth() > 0 ? getWidth() : 1;
            float distance =
                    w / 2f
                            + w
                                    / 2f
                                    * (float)
                                            Math.sin(
                                                    (Math.min(1f, Math.abs(delta) / w) - 0.5f)
                                                            * 0.3f
                                                            * (float) Math.PI);
            int dur;
            if (vx != 0f) {
                float v = Math.max(-maxFling, Math.min(maxFling, vx));
                dur = (int) (4f * Math.round(1000f * distance / Math.max(1f, Math.abs(v))));
            } else {
                int minFling = ViewConfiguration.get(act).getScaledMinimumFlingVelocity();
                dur = (int) Math.round((Math.abs(delta) / Math.max(1, minFling) + 1f) * 256f);
            }
            return Math.min(600, Math.max(0, dur));
        }

        private void ensureScrim() {
            if (scrim != null) return;
            try {
                View content = act.findViewById(android.R.id.content);
                if (!(content instanceof ViewGroup)) return;
                FrameLayout.LayoutParams lp =
                        new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                scrim = new ScrimView(act);
                ((ViewGroup) content).addView(scrim, lp);
            } catch (Throwable t) {
                scrim = null;
            }
        }

        private void updateScrim(float dx, float w, float progress) {
            if (scrim == null) return;
            int a = (int) (SCRIM_ALPHA * (1f - progress));
            scrim.setCoverage(dx, a);
        }

        private void animateScrimTo(float targetLeft, int duration) {
            if (scrim == null) return;
            final ScrimView sv = scrim;
            scrim = null;
            final float startDx = sv.coverDx;
            final int w = sv.getWidth() > 0 ? sv.getWidth() : 1;
            ValueAnimator va = ValueAnimator.ofFloat(startDx, targetLeft);
            va.setDuration(duration);
            va.setInterpolator(QUINTIC_EASE_OUT);
            va.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float dx = (Float) animation.getAnimatedValue();
                            int a = (int) (SCRIM_ALPHA * (1f - dx / w));
                            if (a < 0) a = 0;
                            else if (a > SCRIM_ALPHA) a = SCRIM_ALPHA;
                            sv.setCoverage(dx, a);
                        }
                    });
            va.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeFromParent(sv);
                        }
                    });
            va.start();
        }

        private static final class ScrimView extends View {
            private float coverDx;
            private int coverAlpha;

            public ScrimView(Context c) {
                super(c);
                setClickable(false);
                setFocusable(false);
                setWillNotDraw(false);
            }

            void setCoverage(float dx, int alpha) {
                coverDx = dx;
                coverAlpha = alpha;
                invalidate();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                if (coverAlpha <= 0 || coverDx <= 0f) return;
                int w = (int) Math.min(getWidth(), coverDx);
                if (w <= 0) return;
                canvas.save();
                canvas.clipRect(0, 0, w, getHeight());
                canvas.drawARGB(coverAlpha, 0, 0, 0);
                canvas.restore();
            }
        }

        private static void removeFromParent(View v) {
            try {
                ViewParent p = v.getParent();
                if (p instanceof ViewGroup) {
                    ((ViewGroup) p).removeView(v);
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
