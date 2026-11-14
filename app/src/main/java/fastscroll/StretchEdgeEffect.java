package fastscroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EdgeEffect;

public final class StretchEdgeEffect extends EdgeEffect {

    private static final float MAX_STRETCH = 1.05f;
    private static final float SCALE_MIN = 0.5f;
    private static final float SCALE_MAX = 1.8f;
    private static final float PULL_SLOP = 0.05f;
    private static final float PULL_SENSITIVITY = 0.15f;

    private static final double SPRING_STIFFNESS = 200.0;
    private static final double SPRING_DAMPING_RATIO = 0.75;
    private static final int SPRING_DURATION_MS = 450;

    private final int direction;
    private final View view;
    private final StretchSharedState sharedState;
    private boolean pivotSet;

    public StretchEdgeEffect(
            Context context, int direction, View view, StretchSharedState sharedState) {
        super(context);
        this.direction = direction;
        this.view = view;
        this.sharedState = sharedState;
        this.pivotSet = false;
    }

    private boolean isHorizontal() {
        return direction == 0 || direction == 2;
    }

    private float getScale() {
        return isHorizontal() ? view.getScaleX() : view.getScaleY();
    }

    private int pivotEdge() {
        if (isHorizontal()) {
            return view.getPivotX() == 0f ? 0 : 2;
        }
        return view.getPivotY() == 0f ? 1 : 3;
    }

    private void setupPivot() {
        if (isHorizontal()) {
            view.setPivotX(direction == 0 ? 0f : view.getWidth());
        } else {
            view.setPivotY(direction == 1 ? 0f : view.getHeight());
        }
    }

    private void setScale(float scale) {
        float s = Math.max(SCALE_MIN, Math.min(SCALE_MAX, scale));
        if (isHorizontal()) {
            view.setScaleX(s);
        } else {
            view.setScaleY(s);
        }
    }

    @Override
    public void onPull(float deltaDistance) {
        super.onPull(deltaDistance);
        pull(deltaDistance);
    }

    @Override
    public void onPull(float deltaDistance, float displacement) {
        super.onPull(deltaDistance, displacement);
        pull(deltaDistance);
    }

    private void pull(float deltaDistance) {
        boolean takingOver = !pivotSet || sharedState.hasRunningSpring();
        if (takingOver) {
            sharedState.cancel();
            setScale(1.0f);
            pivotSet = true;
            setupPivot();
        }

        float cur = getScale();
        float base;
        if (pivotEdge() != direction) {
            base = 1.0f - cur;
        } else {
            base = MAX_STRETCH - cur;
        }
        float delta = base / PULL_SLOP * deltaDistance * PULL_SENSITIVITY;
        if (delta != 0f) {
            setScale(cur + delta);
        }
    }

    @Override
    public void onRelease() {
        super.onRelease();
        pivotSet = false;
        float cur = getScale();
        if (cur != 1.0f) {
            startSpring(cur, 1.0f);
        }
    }

    @Override
    public void onAbsorb(int velocity) {
        super.onAbsorb(velocity);
        setupPivot();
        float cur = getScale();
        if (cur > 1.0f) {
            startSpring(cur, 1.0f);
        } else {
            float amount = Math.min(0.05f, (velocity / 20000.0f) * 0.25f);
            setScale(1.0f + amount);
            startSpring(1.0f + amount, 1.0f);
        }
    }

    @Override
    public boolean draw(Canvas canvas) {
        return false;
    }

    @Override
    public boolean isFinished() {
        return !sharedState.hasRunningSpring();
    }

    private void startSpring(final float from, final float to) {
        sharedState.cancel();
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(SPRING_DURATION_MS);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float elapsedMs = animation.getAnimatedFraction() * SPRING_DURATION_MS;
                        setScale(springValue(elapsedMs / 1000.0f, from, to));
                        view.postInvalidateOnAnimation();
                    }
                });
        anim.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setScale(1.0f);
                        sharedState.springAnim = null;
                    }
                });
        sharedState.springAnim = anim;
        anim.start();
    }

    private float springValue(double t, double start, double target) {
        double omega0 = Math.sqrt(SPRING_STIFFNESS);
        double zeta = SPRING_DAMPING_RATIO;
        double omegaD = omega0 * Math.sqrt(1.0 - zeta * zeta);
        double x0 = start - target;
        double a = x0;
        double b = (zeta * omega0 * x0) / omegaD;
        double y =
                Math.exp(-zeta * omega0 * t) * (a * Math.cos(omegaD * t) + b * Math.sin(omegaD * t))
                        + target;
        return (float) y;
    }
}
