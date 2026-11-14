package com.jiguro.bettervia;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public final class ViaSearchBox {

    private static final int BG_RADIUS_DP = 18;
    private static final int TEXT_SIZE_SP = 14;
    private static final int DRAWABLE_PADDING_DP = 12;
    private static final int PADDING_H_DP = 16;
    private static final int PADDING_V_DP = 10;
    private static final int CLEAR_ICON_DP = 18;

    private ViaSearchBox() {}

    public static EditText create(Context ctx, String hint) {
        final EditText edit = new EditText(ctx);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(ViaUIRes.dp(ctx, BG_RADIUS_DP));
        bg.setColor(ViaUIRes.VIA_SEARCH_BG_COLOR);
        edit.setBackground(bg);
        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        edit.setTextColor(
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextPrimary(), ThemeColors.getTextColor(ctx)));
        edit.setHintTextColor(
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextHint(), ThemeColors.getHintColor(ctx)));
        if (hint != null) {
            edit.setHint(hint);
        }
        edit.setCompoundDrawablePadding(ViaUIRes.dp(ctx, DRAWABLE_PADDING_DP));
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edit.setPadding(
                ViaUIRes.dp(ctx, PADDING_H_DP),
                ViaUIRes.dp(ctx, PADDING_V_DP),
                ViaUIRes.dp(ctx, PADDING_H_DP),
                ViaUIRes.dp(ctx, PADDING_V_DP));
        try {
            edit.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        } catch (Throwable t) {
        }
        installClearButton(ctx, edit);
        return edit;
    }

    private static void installClearButton(final Context ctx, final EditText edit) {
        try {
            final Drawable rawIcon = ctx.getResources().getDrawable(R.drawable.ic_via_close);
            if (rawIcon == null) {
                return;
            }
            rawIcon.setColorFilter(
                    ViaUIRes.resolveAttrColor(
                            ctx, ViaUIRes.VIA_ATTR_TINT, ThemeColors.getHintColor(ctx)),
                    PorterDuff.Mode.SRC_IN);
            final int iconSize = ViaUIRes.dp(ctx, CLEAR_ICON_DP);
            rawIcon.setBounds(0, 0, iconSize, iconSize);
            final Drawable icon = rawIcon;
            final boolean rtl =
                    ctx.getResources().getConfiguration().getLayoutDirection()
                            == View.LAYOUT_DIRECTION_RTL;

            edit.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(
                                CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(
                                CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                            boolean empty = s.length() <= 0;
                            Drawable[] cur = edit.getCompoundDrawables();
                            Drawable curSide = rtl ? cur[0] : cur[2];
                            if ((curSide != null) == !empty) {
                                return;
                            }
                            setSideDrawable(edit, empty ? null : icon, rtl);
                        }
                    });

            edit.setOnTouchListener(
                    new View.OnTouchListener() {
                        private boolean canClear = false;

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            if (action == MotionEvent.ACTION_DOWN) {
                                canClear = edit.getText().length() > 0;
                                if (canClear) {
                                    float x = event.getX();
                                    if (rtl) {
                                        if (x > edit.getCompoundPaddingLeft()) {
                                            canClear = false;
                                        }
                                    } else {
                                        if (x
                                                < edit.getMeasuredWidth()
                                                        - edit.getCompoundPaddingRight()) {
                                            canClear = false;
                                        }
                                    }
                                }
                            } else if (action == MotionEvent.ACTION_UP) {
                                if (canClear) {
                                    edit.setText("");
                                }
                            }
                            return canClear;
                        }
                    });
        } catch (Throwable t) {
        }
    }

    private static void setSideDrawable(EditText edit, Drawable icon, boolean rtl) {
        Drawable[] cur = edit.getCompoundDrawables();
        if (rtl) {
            edit.setCompoundDrawables(icon, cur[1], cur[2], cur[3]);
        } else {
            edit.setCompoundDrawables(cur[0], cur[1], icon, cur[3]);
        }
    }
}
