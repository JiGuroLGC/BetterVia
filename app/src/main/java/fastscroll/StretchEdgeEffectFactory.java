package fastscroll;

import android.widget.EdgeEffect;

import androidx.recyclerview.widget.RecyclerView;

public final class StretchEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {

    private final StretchSharedState sharedState = new StretchSharedState();

    @Override
    public EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
        return new StretchEdgeEffect(view.getContext(), direction, view, sharedState);
    }
}
