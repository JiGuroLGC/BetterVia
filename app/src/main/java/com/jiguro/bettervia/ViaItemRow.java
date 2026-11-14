package com.jiguro.bettervia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViaItemRow extends LinearLayout {

    private static final int ROW_MIN_HEIGHT_DP = 54;
    private static final int ROW_PADDING_H_DP = 16;
    private static final int ICON_SIZE_DP = 22;
    private static final int ICON_MARGIN_END_DP = 16;
    private static final int TITLE_TEXT_SP = 14;
    private static final int SUBTITLE_TEXT_SP = 12;
    private static final int SUBTITLE_MARGIN_TOP_DP = 2;
    private static final int MOVE_HANDLE_WIDTH_DP = 48;
    private static final int MOVE_HANDLE_PADDING_DP = 15;
    private static final int MOVE_HANDLE_MARGIN_DP = 16;
    private static final int CHECKBOX_WIDTH_DP = 16;
    private static final int CHECKBOX_MARGIN_DP = 2;

    private final ImageView iconView;
    private final TextView titleView;
    private final TextView subtitleView;
    private final ImageView moveHandleView;
    private final CheckBox checkBox;

    public ViaItemRow(Context ctx) {
        super(ctx);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackground(createRowBackground());
        setPadding(ViaUIRes.dp(ctx, ROW_PADDING_H_DP), 0, ViaUIRes.dp(ctx, ROW_PADDING_H_DP), 0);
        setMinimumHeight(ViaUIRes.dp(ctx, ROW_MIN_HEIGHT_DP));
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        iconView = new ImageView(ctx);
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iconView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        LayoutParams iconLp =
                new LayoutParams(ViaUIRes.dp(ctx, ICON_SIZE_DP), ViaUIRes.dp(ctx, ICON_SIZE_DP));
        iconLp.rightMargin = ViaUIRes.dp(ctx, ICON_MARGIN_END_DP);
        iconView.setLayoutParams(iconLp);
        iconView.setVisibility(View.GONE);
        addView(iconView);

        LinearLayout textBox = new LinearLayout(ctx);
        textBox.setOrientation(VERTICAL);
        textBox.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        titleView = new TextView(ctx);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SP);
        titleView.setTextColor(
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextPrimary(), ThemeColors.getTextColor(ctx)));
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        textBox.addView(
                titleView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        subtitleView = new TextView(ctx);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SUBTITLE_TEXT_SP);
        subtitleView.setTextColor(
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextSecondary(), ThemeColors.getHintColor(ctx)));
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        LayoutParams subtitleLp =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        subtitleLp.topMargin = ViaUIRes.dp(ctx, SUBTITLE_MARGIN_TOP_DP);
        textBox.addView(subtitleView, subtitleLp);
        addView(textBox);

        moveHandleView = new ImageView(ctx);
        moveHandleView.setPadding(
                ViaUIRes.dp(ctx, MOVE_HANDLE_PADDING_DP),
                ViaUIRes.dp(ctx, MOVE_HANDLE_PADDING_DP),
                ViaUIRes.dp(ctx, MOVE_HANDLE_PADDING_DP),
                ViaUIRes.dp(ctx, MOVE_HANDLE_PADDING_DP));
        moveHandleView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        moveHandleView.setImageResource(R.drawable.ic_via_drag);
        try {
            moveHandleView.setColorFilter(
                    ViaUIRes.resolveAttrColor(
                            ctx, ViaUIRes.VIA_ATTR_TINT, ThemeColors.getHintColor(ctx)));
        } catch (Throwable t) {
        }
        LayoutParams handleLp =
                new LayoutParams(ViaUIRes.dp(ctx, MOVE_HANDLE_WIDTH_DP), LayoutParams.MATCH_PARENT);
        handleLp.leftMargin = ViaUIRes.dp(ctx, MOVE_HANDLE_MARGIN_DP);
        handleLp.rightMargin = ViaUIRes.dp(ctx, MOVE_HANDLE_MARGIN_DP);
        moveHandleView.setLayoutParams(handleLp);
        moveHandleView.setVisibility(View.GONE);
        addView(moveHandleView);

        checkBox = new CheckBox(ctx);
        checkBox.setButtonDrawable(createCheckboxDrawable(ctx));
        checkBox.setDuplicateParentStateEnabled(true);
        checkBox.setClickable(false);
        checkBox.setFocusable(false);
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams cbLp =
                new LayoutParams(ViaUIRes.dp(ctx, CHECKBOX_WIDTH_DP), LayoutParams.WRAP_CONTENT);
        cbLp.leftMargin = ViaUIRes.dp(ctx, CHECKBOX_MARGIN_DP);
        cbLp.rightMargin = ViaUIRes.dp(ctx, CHECKBOX_MARGIN_DP);
        checkBox.setLayoutParams(cbLp);
        checkBox.setVisibility(View.GONE);
        addView(checkBox);
    }

    public void setIcon(Drawable icon) {
        iconView.setImageDrawable(icon);
        iconView.setVisibility(icon != null ? View.VISIBLE : View.GONE);
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public CharSequence getTitle() {
        return titleView.getText();
    }

    public void setSubtitle(CharSequence subtitle) {
        if (subtitle == null || subtitle.length() == 0) {
            subtitleView.setVisibility(View.GONE);
        } else {
            subtitleView.setText(subtitle);
            subtitleView.setVisibility(View.VISIBLE);
        }
    }

    public CharSequence getSubtitle() {
        return subtitleView.getText();
    }

    public void setEditMode(boolean showCheckbox, boolean showMoveHandle) {
        checkBox.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);
        moveHandleView.setVisibility(showMoveHandle ? View.VISIBLE : View.GONE);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setOnMoveTouchListener(OnTouchListener l) {
        moveHandleView.setOnTouchListener(l);
    }

    private Drawable createRowBackground() {
        return SettingsUI.createRippleDrawable(getContext());
    }

    private static Drawable createCheckboxDrawable(Context ctx) {
        int size = ViaUIRes.dp(ctx, 16);
        GradientDrawable mask = new GradientDrawable();
        mask.setShape(GradientDrawable.OVAL);
        mask.setColor(0xFFFFFFFF);
        mask.setSize(size, size);
        return new RippleDrawable(
                android.content.res.ColorStateList.valueOf(ViaUIRes.VIA_PRESSED_COLOR),
                SettingsUI.createCircleToggleDrawable(ctx),
                mask);
    }
}
