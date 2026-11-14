package fastscroll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;

public final class FastScrollBuilder {

    private final float density;
    private final FastScrollView fastScrollView;
    private int thumbWidthPx;
    private int thumbLengthPx;
    private boolean enabled = true;
    private Drawable thumbDrawable;
    private int thumbColor = FastScroll.DEFAULT_THUMB_COLOR;
    private int thumbPressedColor = FastScroll.DEFAULT_PRESSED_COLOR;
    private float touchSlop = 0f;
    private boolean rtl = false;

    public FastScrollBuilder(FastScrollView fastScrollView) {
        this.fastScrollView = fastScrollView;
        Context context = fastScrollView.getFastScrollableView().getContext();
        this.density = context.getResources().getDisplayMetrics().density;
        this.thumbWidthPx = b(FastScroll.DEFAULT_THUMB_WIDTH_DP);
        this.thumbLengthPx = b(FastScroll.DEFAULT_THUMB_LENGTH_DP);
    }

    private int b(float dp) {
        return Math.round(dp * density);
    }

    public FastScrollBuilder setRtl(boolean rtl) {
        this.rtl = rtl;
        return this;
    }

    public FastScrollBuilder setTouchSlop(float slopDp) {
        this.touchSlop = slopDp;
        return this;
    }

    public FastScroll build() {
        if (thumbDrawable == null) {
            thumbDrawable = c();
        }
        return new FastScroll(
                fastScrollView,
                thumbWidthPx,
                thumbLengthPx,
                thumbDrawable,
                enabled,
                touchSlop,
                rtl);
    }

    private Drawable c() {
        StateListDrawable stateList = new StateListDrawable();

        GradientDrawable pressed = new GradientDrawable();
        pressed.setColor(thumbPressedColor);
        float cornerRadius = thumbWidthPx / 2.0f;
        pressed.setCornerRadius(cornerRadius);
        int inset = b(FastScroll.DEFAULT_INSET_DP);
        int leftInset = thumbWidthPx - 2 * inset;
        InsetDrawable pressedInset = new InsetDrawable(pressed, leftInset, inset, inset, inset);
        stateList.addState(FastScroll.STATE_PRESSED, pressedInset);

        GradientDrawable normal = new GradientDrawable();
        normal.setColor(thumbColor);
        normal.setCornerRadius(cornerRadius);
        InsetDrawable normalInset = new InsetDrawable(normal, leftInset, inset, inset, inset);
        stateList.addState(FastScroll.STATE_DEFAULT, normalInset);

        return stateList;
    }
}
