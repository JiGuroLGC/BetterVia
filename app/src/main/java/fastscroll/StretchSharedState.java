package fastscroll;

import android.animation.ValueAnimator;

final class StretchSharedState {

    ValueAnimator springAnim;

    boolean hasRunningSpring() {
        return springAnim != null && springAnim.isRunning();
    }

    void cancel() {
        if (springAnim != null) {
            springAnim.cancel();
            springAnim = null;
        }
    }
}
