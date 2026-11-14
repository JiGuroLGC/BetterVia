package fastscroll;

import android.view.MotionEvent;
import android.view.View;

public interface FastScrollView {

    View getFastScrollableView();

    int getVerticalScrollRange();

    int getVerticalScrollExtent();

    int getVerticalScrollOffset();

    void onTouchEventForward(MotionEvent ev);

    FastScroll getFastScrollDelegate();
}
