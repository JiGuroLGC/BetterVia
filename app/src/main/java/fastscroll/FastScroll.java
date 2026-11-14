package fastscroll;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;

public final class FastScroll {

    private static final long SCROLL_FADE_OUT_DELAY = 0x5dcL;
    private static final long SCROLL_FADE_OUT_DELAY_WITH_TOUCH = 0x2eeL;
    static final int[] STATE_PRESSED = {android.R.attr.state_pressed};
    static final int[] STATE_DEFAULT = new int[0];
    static final int DEFAULT_THUMB_WIDTH_DP = 0x14;
    static final int DEFAULT_THUMB_LENGTH_DP = 0x20;
    static final int DEFAULT_INSET_DP = 4;
    static final int DEFAULT_THUMB_COLOR = 0xFF808080;
    static final int DEFAULT_PRESSED_COLOR = 0xFF03A9F4;

    private final View view;
    private final float density;
    private float touchSlop;
    private final Rect rect;
    private Drawable drawable;
    private final FastScrollView fastScrollView;
    private int thumbLengthPx;
    private final FadeAnimator animator;
    private boolean enabled;
    private boolean dragging;
    private boolean dragged;
    private boolean rtl;
    private float lastTouchY;

    FastScroll(
            FastScrollView fastScrollView,
            int thumbWidthPx,
            int thumbLengthPx,
            Drawable thumb,
            boolean enabled,
            float touchSlop,
            boolean rtl) {
        this.dragging = false;
        this.dragged = false;
        this.rtl = rtl;
        this.view = fastScrollView.getFastScrollableView();
        this.view.setVerticalScrollBarEnabled(false);
        Context context = this.view.getContext();
        this.touchSlop = touchSlop;
        this.density = context.getResources().getDisplayMetrics().density;
        this.thumbLengthPx = dp2px(DEFAULT_THUMB_LENGTH_DP);
        this.rect = new Rect(0, 0, thumbWidthPx, thumbLengthPx);
        this.drawable = thumb;
        this.fastScrollView = fastScrollView;
        this.animator = new FadeAnimator(ViewConfiguration.get(context), this.view);
        this.enabled = enabled;
    }

    public boolean awakenScrollBars() {
        return d(SCROLL_FADE_OUT_DELAY);
    }

    public boolean handleInterceptTouch(MotionEvent ev) {
        return l(ev);
    }

    public boolean handleTouchEvent(MotionEvent ev) {
        return n(ev);
    }

    public void drawFastScroll(Canvas canvas) {
        e(canvas);
    }

    public void onAttached() {
        h();
    }

    public void onDetached() {
        i();
    }

    public void onVisibilityChanged(View changedView, int visibility) {
        o(changedView, visibility);
    }

    public void onWindowVisibilityChanged(int visibility) {
        p(visibility);
    }

    public void setEnabled(boolean enabled) {
        t(enabled);
    }

    public void setThumbSize(int widthDp, int lengthDp) {
        u(widthDp, lengthDp);
    }

    public void setTouchSlop(int slopDp) {
        v(slopDp);
    }

    public void setRtl(boolean rtl) {
        r(rtl);
    }

    public void setThumbDrawable(Drawable thumb) {
        s(thumb);
    }

    private boolean d(long duration) {
        view.postInvalidateOnAnimation();
        if (!dragging) {
            if (animator.state == 0) {
                duration = Math.max(SCROLL_FADE_OUT_DELAY_WITH_TOUCH, duration);
            }
            long now = AnimationUtils.currentAnimationTimeMillis();
            long endTime = now + duration;
            animator.endTime = endTime;
            animator.state = 1;
            view.removeCallbacks(animator);
            view.postDelayed(animator, endTime - AnimationUtils.currentAnimationTimeMillis());
        }
        return false;
    }

    private void e(Canvas canvas) {
        j(canvas);
    }

    private int f(float dp) {
        return Math.round(density * dp);
    }

    private int dp2px(float dp) {
        return f(dp);
    }

    private boolean g() {
        return false;
    }

    private void h() {
        g();
    }

    private void i() {}

    private void j(Canvas canvas) {
        boolean needsInvalidate;
        if (dragging) {
            drawable.setAlpha(0xff);
            needsInvalidate = false;
        } else {
            int state = animator.state;
            if (state == 0) {
                return;
            }
            if (state == 2) {
                if (animator.values == null) {
                    animator.values = new float[1];
                }
                float[] values = animator.values;
                Interpolator.Result result = animator.interpolator.timeToValues(values);
                if (result == Interpolator.Result.FREEZE_END) {
                    animator.state = 0;
                } else {
                    drawable.setAlpha(Math.round(values[0]));
                }
                needsInvalidate = true;
            } else {
                drawable.setAlpha(0xff);
                needsInvalidate = false;
            }
        }

        if (w(0)) {
            int scrollY = view.getScrollY();
            int scrollX = view.getScrollX();
            drawable.setBounds(
                    rect.left + scrollX,
                    rect.top + scrollY,
                    rect.right + scrollX,
                    rect.bottom + scrollY);
            drawable.draw(canvas);
        }
        if (needsInvalidate) {
            view.invalidate();
        }
    }

    private boolean l(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            return n(ev);
        }
        return false;
    }

    private boolean n(MotionEvent ev) {
        int action = ev.getActionMasked();
        float y = ev.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            if (animator.state == 0) {
                dragging = false;
                return false;
            }
            if (dragging) {
                return finishTouch();
            }
            dragged = false;
            w(0);
            float x = ev.getX();
            if (y >= rect.top && y <= rect.bottom && x >= rect.left && x <= rect.right) {
                dragging = true;
                lastTouchY = y;
                fastScrollView.onTouchEventForward(ev);
                MotionEvent cancel = MotionEvent.obtain(ev);
                cancel.setAction(MotionEvent.ACTION_CANCEL);
                fastScrollView.onTouchEventForward(cancel);
                cancel.recycle();
                q(true);
                x(0, true);
                view.removeCallbacks(animator);
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (dragging) {
                int delta = Math.round(y - lastTouchY);
                if (delta == 0) {
                    return finishTouch();
                }
                if (dragged) {
                    w(delta);
                    lastTouchY = y;
                } else if (Math.abs(delta) > touchSlop) {
                    float fDelta = delta;
                    if (delta > 0) {
                        fDelta -= touchSlop;
                    } else {
                        fDelta += touchSlop;
                    }
                    w((int) fDelta);
                    dragged = true;
                    lastTouchY = y;
                }
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (dragging) {
                q(false);
                dragging = false;
                awakenScrollBars();
                if (action == MotionEvent.ACTION_UP && !dragged) {
                    MotionEvent down = MotionEvent.obtain(ev);
                    down.setAction(MotionEvent.ACTION_DOWN);
                    fastScrollView.onTouchEventForward(down);
                    fastScrollView.onTouchEventForward(ev);
                    down.recycle();
                }
            }
        }

        return finishTouch();
    }

    private boolean finishTouch() {
        if (dragging) {
            view.invalidate();
            ViewParent parent = view.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            return true;
        }
        return false;
    }

    private void o(View changedView, int visibility) {
        if (visibility == View.VISIBLE && view.isAttachedToWindow()) {
            g();
        }
    }

    private void p(int visibility) {
        if (visibility == View.VISIBLE) {
            g();
        }
    }

    private void q(boolean pressed) {
        drawable.setState(pressed ? STATE_PRESSED : STATE_DEFAULT);
        view.invalidate();
    }

    private void r(boolean rtl) {
        if (this.rtl == rtl) {
            return;
        }
        this.rtl = rtl;
        w(0);
    }

    private void s(Drawable thumb) {
        if (thumb != null) {
            this.drawable = thumb;
            w(0);
            return;
        }
        throw new IllegalArgumentException("drawable == null");
    }

    private void t(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        w(0);
    }

    private void u(int widthDp, int lengthDp) {
        rect.left = rect.right - f(widthDp);
        thumbLengthPx = f(lengthDp);
        w(0);
    }

    private void v(int slopDp) {
        touchSlop = Math.max(0, slopDp);
    }

    private boolean w(int deltaY) {
        return x(deltaY, false);
    }

    private boolean x(int deltaY, boolean updatePosition) {
        int range = fastScrollView.getVerticalScrollRange();
        if (range <= 0) {
            return false;
        }
        int offset = fastScrollView.getVerticalScrollOffset();
        int extent = fastScrollView.getVerticalScrollExtent();
        int scrollable = range - extent;
        int viewHeight = view.getHeight();
        if (scrollable <= viewHeight) {
            return false;
        }

        float ratio = offset * 1.0f / scrollable;
        if (ratio < 0f) {
            ratio = 0f;
        } else if (ratio > 1f) {
            ratio = 1f;
        }
        float extentRatio = extent * 1.0f / range;

        int rectWidth = rect.width();
        if (rtl) {
            rect.left = 0;
            rect.right = rectWidth;
        } else {
            rect.right = view.getWidth();
            rect.left = rect.right - rectWidth;
        }

        int length;
        if (enabled) {
            length = Math.max(thumbLengthPx, Math.round(Math.min(extentRatio, 0.25f) * viewHeight));
        } else {
            length = thumbLengthPx;
        }
        rect.bottom = rect.top + length;

        int maxTop = viewHeight - length;
        if (maxTop < 0) {
            maxTop = 0;
        }
        int thumbTop = Math.round(ratio * maxTop);
        rect.offsetTo(rect.left, thumbTop);

        if (deltaY != 0 && maxTop > 0) {
            int newTop = thumbTop + deltaY;
            if (newTop > maxTop) {
                newTop = maxTop;
            } else if (newTop < 0) {
                newTop = 0;
            }
            float scrollRatio = newTop * 1.0f / maxTop;
            int targetScroll = Math.round(scrollable * scrollRatio) - offset;
            if (view instanceof AbsListView) {
                ((AbsListView) view).smoothScrollBy(targetScroll, 0);
            } else {
                view.scrollBy(0, targetScroll);
            }
        }
        return true;
    }

    private static final class FadeAnimator implements Runnable {

        private static final float[] FADE_IN_ALPHA = {255.0f};
        private static final float[] FADE_OUT_ALPHA = {0.0f};

        private final int scrollDefaultDelay;
        private final int scrollBarFadeDuration;
        private float[] values;
        private final View view;
        private final Interpolator interpolator;
        private long endTime;
        private int state;

        FadeAnimator(ViewConfiguration viewConfiguration, View view) {
            interpolator = new Interpolator(1, 2);
            state = 0;
            scrollDefaultDelay = viewConfiguration.getScrollDefaultDelay();
            scrollBarFadeDuration = viewConfiguration.getScrollBarFadeDuration();
            this.view = view;
        }

        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            if (now >= endTime) {
                int nowInt = (int) now;
                interpolator.setKeyFrame(0, nowInt, FADE_IN_ALPHA);
                interpolator.setKeyFrame(1, nowInt + scrollBarFadeDuration, FADE_OUT_ALPHA);
                state = 2;
                view.invalidate();
            }
        }
    }
}
