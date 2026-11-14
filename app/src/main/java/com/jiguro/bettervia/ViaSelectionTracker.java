package com.jiguro.bettervia;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ViaSelectionTracker {

    public interface Listener {
        void onSelectionChanged(int selectedCount);

        void onEditModeChanged(boolean editMode);
    }

    private final Set<Integer> selected = new HashSet<Integer>();
    private boolean editMode;
    private Listener listener;

    public void setListener(Listener l) {
        this.listener = l;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        if (this.editMode == editMode) {
            return;
        }
        this.editMode = editMode;
        selected.clear();
        if (listener != null) {
            listener.onEditModeChanged(editMode);
        }
    }

    public int getSelectedCount() {
        return selected.size();
    }

    public boolean isSelected(int position) {
        return selected.contains(Integer.valueOf(position));
    }

    public int[] getSelectedIndices(boolean ascending) {
        int[] result = new int[selected.size()];
        int i = 0;
        for (Integer pos : selected) {
            result[i++] = pos.intValue();
        }
        Arrays.sort(result);
        if (!ascending) {
            for (int a = 0, b = result.length - 1; a < b; a++, b--) {
                int tmp = result[a];
                result[a] = result[b];
                result[b] = tmp;
            }
        }
        return result;
    }

    public boolean toggle(int position) {
        boolean nowSelected;
        if (selected.remove(Integer.valueOf(position))) {
            nowSelected = false;
        } else {
            selected.add(Integer.valueOf(position));
            nowSelected = true;
        }
        notifySelectionChanged();
        return nowSelected;
    }

    public void setSelected(int position, boolean selected) {
        boolean changed;
        if (selected) {
            changed = this.selected.add(Integer.valueOf(position));
        } else {
            changed = this.selected.remove(Integer.valueOf(position));
        }
        if (changed) {
            notifySelectionChanged();
        }
    }

    public void selectRange(int from, int to) {
        for (int i = from; i <= to; i++) {
            selected.add(Integer.valueOf(i));
        }
        notifySelectionChanged();
    }

    public void deselectRange(int from, int to) {
        for (int i = from; i <= to; i++) {
            selected.remove(Integer.valueOf(i));
        }
        notifySelectionChanged();
    }

    public void selectPositions(int[] positions) {
        for (int pos : positions) {
            selected.add(Integer.valueOf(pos));
        }
        notifySelectionChanged();
    }

    public void deselectPositions(int[] positions) {
        for (int pos : positions) {
            selected.remove(Integer.valueOf(pos));
        }
        notifySelectionChanged();
    }

    public void clearSelection() {
        if (selected.isEmpty()) {
            return;
        }
        selected.clear();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (listener != null) {
            listener.onSelectionChanged(selected.size());
        }
    }
}
