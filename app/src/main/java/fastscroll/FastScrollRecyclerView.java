package fastscroll;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class FastScrollRecyclerView extends RecyclerView implements FastScrollView {

    private FastScroll fastScroll;

    public FastScrollRecyclerView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        fastScroll = new FastScrollBuilder(this).setRtl(true).build();
        addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dx != 0 || dy != 0) {
                            fastScroll.awakenScrollBars();
                        }
                    }
                });
    }

    @Override
    public int getVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int getVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int getVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public void onTouchEventForward(MotionEvent ev) {
        super.onTouchEvent(ev);
    }

    @Override
    public View getFastScrollableView() {
        return this;
    }

    @Override
    public FastScroll getFastScrollDelegate() {
        return fastScroll;
    }

    @Override
    public boolean awakenScrollBars() {
        return fastScroll.awakenScrollBars();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        fastScroll.drawFastScroll(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fastScroll.onAttached();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return fastScroll.handleInterceptTouch(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return fastScroll.handleTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (fastScroll != null) {
            fastScroll.onVisibilityChanged(changedView, visibility);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        fastScroll.onWindowVisibilityChanged(visibility);
    }
}
