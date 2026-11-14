package com.jiguro.bettervia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViaActionBar extends LinearLayout {

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_EQUAL = 1;

    private static final int DIVIDER_COLOR = 0x30808080;
    private static final int DIVIDER_HEIGHT_PX = 2;
    private static final int SPACER_HEIGHT_DP = 48;
    private static final int BUTTON_PADDING_H_DP = 16;
    private static final int BUTTON_MIN_HEIGHT_DP = 48;

    private final Paint dividerPaint;
    private final Space spacer;
    private final List<Button> leftButtons = new ArrayList<Button>();
    private final List<Button> rightButtons = new ArrayList<Button>();
    private int style = STYLE_NORMAL;
    private OnActionClickListener actionClickListener;

    public static class Button {
        private final int id;
        private int order;
        private String text;
        private boolean selected;
        private boolean accent;
        private boolean danger;
        private boolean disabled;
        private boolean hidden;

        public Button(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isAccent() {
            return accent;
        }

        public void setAccent(boolean accent) {
            this.accent = accent;
        }

        public boolean isDanger() {
            return danger;
        }

        public void setDanger(boolean danger) {
            this.danger = danger;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }
    }

    public interface OnActionClickListener {
        void onActionClick(View view, Button button);
    }

    public ViaActionBar(Context ctx) {
        this(ctx, null);
    }

    public ViaActionBar(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        spacer = new Space(ctx);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutParams spacerLp = new LayoutParams(0, ViaUIRes.dp(getContext(), SPACER_HEIGHT_DP));
        if (style == STYLE_NORMAL) {
            spacerLp.weight = 1f;
        }
        spacer.setLayoutParams(spacerLp);
        addView(spacer);
        setWillNotDraw(false);
    }

    public void addLeftButton(Button button) {
        addButton(button, leftButtons, false);
    }

    public void addRightButton(Button button) {
        addButton(button, rightButtons, true);
    }

    public void addLeftButtons(Button... buttons) {
        for (Button b : buttons) {
            addLeftButton(b);
        }
    }

    public void addRightButtons(Button... buttons) {
        for (Button b : buttons) {
            addRightButton(b);
        }
    }

    private void addButton(Button button, List<Button> group, boolean rightSide) {
        if (button == null) {
            return;
        }
        TextView tv = new TextView(getContext());
        styleButton(tv, button);
        int anchorId = -1;
        for (Button exist : group) {
            if (button.getOrder() < exist.getOrder()) {
                anchorId = exist.getId();
                break;
            }
        }
        int index;
        if (anchorId != -1) {
            index = indexOfChild(findViewById(anchorId));
        } else if (rightSide) {
            index = indexOfChild(spacer) + 1;
        } else {
            index = indexOfChild(spacer);
        }
        addView(tv, index);
        group.add(button);
    }

    public void updateButton(Button button) {
        if (button == null) {
            return;
        }
        View v = findViewById(button.getId());
        if (v instanceof TextView) {
            applyButtonState((TextView) v, button);
        }
    }

    public void updateButtons(Button... buttons) {
        for (Button b : buttons) {
            updateButton(b);
        }
    }

    private void applyButtonState(TextView tv, Button button) {
        tv.setText(button.getText());
        tv.setEnabled(!button.isDisabled());
        tv.setSelected(button.isSelected());
        tv.setVisibility(button.isHidden() ? View.GONE : View.VISIBLE);
    }

    public void setStyle(int style) {
        if (this.style == style) {
            return;
        }
        boolean equal = (style == STYLE_EQUAL);
        this.style = style;
        LayoutParams spacerLp = (LayoutParams) spacer.getLayoutParams();
        spacerLp.weight = equal ? 0f : 1f;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child == spacer) {
                continue;
            }
            if (equal) {
                lp.width = 0;
                lp.weight = 1f;
            } else {
                lp.width = LayoutParams.WRAP_CONTENT;
                lp.weight = 0f;
            }
            child.setLayoutParams(lp);
        }
    }

    public int getStyle() {
        return style;
    }

    public void setOnActionClickListener(OnActionClickListener l) {
        this.actionClickListener = l;
    }

    private void styleButton(TextView tv, Button button) {
        boolean equal = (style == STYLE_EQUAL);
        LayoutParams lp =
                new LayoutParams(equal ? 0 : LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (equal) {
            lp.weight = 1f;
        }
        tv.setLayoutParams(lp);
        tv.setId(button.getId());
        tv.setBackground(createButtonBackground());
        tv.setText(button.getText());
        tv.setEnabled(!button.isDisabled());
        tv.setSelected(button.isSelected());
        tv.setTextColor(createTextColorStateList(button));
        tv.setVisibility(button.isHidden() ? View.GONE : View.VISIBLE);
        tv.setPadding(
                ViaUIRes.dp(getContext(), BUTTON_PADDING_H_DP),
                0,
                ViaUIRes.dp(getContext(), BUTTON_PADDING_H_DP),
                0);
        tv.setMinHeight(ViaUIRes.dp(getContext(), BUTTON_MIN_HEIGHT_DP));
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setOnClickListener(new ButtonClickListener(button));
    }

    private Drawable createButtonBackground() {
        try {
            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.RECTANGLE);
            mask.setCornerRadius(ViaUIRes.dp(getContext(), ViaUIRes.VIA_CORNER_RADIUS_DP));
            mask.setColor(0xFFFFFFFF);
            return new RippleDrawable(
                    android.content.res.ColorStateList.valueOf(ViaUIRes.VIA_PRESSED_COLOR),
                    null,
                    mask);
        } catch (Throwable t) {
            GradientDrawable pressed = new GradientDrawable();
            pressed.setShape(GradientDrawable.RECTANGLE);
            pressed.setCornerRadius(ViaUIRes.dp(getContext(), ViaUIRes.VIA_CORNER_RADIUS_DP));
            pressed.setColor(ViaUIRes.VIA_PRESSED_COLOR);
            StateListDrawable sld = new StateListDrawable();
            sld.addState(
                    new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed},
                    pressed);
            return sld;
        }
    }

    private android.content.res.ColorStateList createTextColorStateList(Button button) {
        Context ctx = getContext();
        int primary =
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextPrimary(), ThemeColors.getTextColor(ctx));
        int normal;
        int pressed;
        if (button.isDanger()) {
            normal = ViaUIRes.VIA_ACCENT_TEXT_COLOR;
            pressed = ViaUIRes.VIA_ACCENT_TEXT_COLOR;
        } else {
            normal = primary;
            pressed =
                    button.isAccent()
                            ? ViaUIRes.VIA_ACCENT_TEXT_COLOR
                            : ViaUIRes.resolveAttrColor(
                                    ctx,
                                    ViaUIRes.VIA_ATTR_TEXT_DEFAULT,
                                    ThemeColors.getTextColor(ctx));
        }
        int disabled = (primary & 0x00FFFFFF) | 0x80000000;
        int[][] states =
                new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed},
                    new int[] {}
                };
        int[] colors = new int[] {disabled, pressed, normal};
        return new android.content.res.ColorStateList(states, colors);
    }

    private final class ButtonClickListener implements OnClickListener {
        private final Button button;

        ButtonClickListener(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            if (actionClickListener != null) {
                actionClickListener.onActionClick(v, button);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dividerPaint.setColor(DIVIDER_COLOR);
        dividerPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0f, 0f, getMeasuredWidth(), DIVIDER_HEIGHT_PX, dividerPaint);
    }
}
