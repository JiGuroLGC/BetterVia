package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fastscroll.FastScroll;
import fastscroll.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

final class CookieDomainPage {

    private static final int BTN_ID_SELECT_ALL = 1;
    private static final int BTN_ID_DELETE = 2;
    private static final int BTN_ID_EDIT = 3;
    private static final int ANIM_GUARD_MS = 240;
    private static final String EMPTY_FACE = "¯\\_(ツ)_/¯";

    private final Activity act;
    private final String domain;
    private final Runnable onDataChanged;
    private final List<CookieStore.CookieInfo> cookies = new ArrayList<CookieStore.CookieInfo>();
    private ViaSelectionTracker tracker;
    private ViaActionBar actionBar;
    private ViaActionBar.Button btnSelectAll;
    private ViaActionBar.Button btnDelete;
    private ViaActionBar.Button btnEdit;
    private View loadingContainer;
    private TextView emptyView;
    private FastScrollRecyclerView recyclerView;
    private CookieAdapter adapter;
    private long pageOpenElapsedMs;
    private boolean loadApplied;
    private boolean suppressEditModeRebind;

    static void show(Activity act, String domain, Runnable onDataChanged) {
        if (act == null || act.isFinishing() || act.isDestroyed()) {
            return;
        }
        new CookieDomainPage(act, domain, onDataChanged).showInternal();
    }

    private CookieDomainPage(Activity act, String domain, Runnable onDataChanged) {
        this.act = act;
        this.domain = domain;
        this.onDataChanged = onDataChanged;
    }

    private void showInternal() {
        SettingsUI.showPageWithTitle(
                act,
                domain,
                null,
                null,
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, Activity act) {
                        buildContent(content);
                    }
                },
                new SettingsUI.PageBackInterceptor() {
                    @Override
                    public boolean onBackPressed() {
                        if (tracker != null && tracker.isEditMode()) {
                            tracker.setEditMode(false);
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void buildContent(ViewGroup content) {
        final Context ctx = act;
        pageOpenElapsedMs = SystemClock.elapsedRealtime();

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout listFrame = new FrameLayout(ctx);
        listFrame.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        loadingContainer = buildLoadingView(ctx);
        loadingContainer.setVisibility(View.INVISIBLE);
        listFrame.addView(loadingContainer);
        recyclerView = new FastScrollRecyclerView(ctx);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setVisibility(View.GONE);
        listFrame.addView(
                recyclerView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView = buildEmptyView(ctx);
        listFrame.addView(
                emptyView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(listFrame);

        actionBar = new ViaActionBar(ctx);
        btnSelectAll =
                new ViaActionBar.Button(BTN_ID_SELECT_ALL, str(ctx, "via_action_select_all"));
        btnSelectAll.setDisabled(true);
        btnDelete = new ViaActionBar.Button(BTN_ID_DELETE, str(ctx, "via_action_delete"));
        btnDelete.setDanger(true);
        btnDelete.setHidden(true);
        btnDelete.setDisabled(true);
        btnEdit = new ViaActionBar.Button(BTN_ID_EDIT, str(ctx, "via_action_edit"));
        actionBar.addLeftButton(btnSelectAll);
        actionBar.addLeftButton(btnDelete);
        actionBar.addRightButton(btnEdit);
        root.addView(actionBar);

        content.addView(root);

        adapter = new CookieAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        FastScroll fastScroll = recyclerView.getFastScrollDelegate();
        fastScroll.setEnabled(true);
        fastScroll.setThumbSize(24, 64);
        boolean rtl = SettingsList.isRtl(ctx);
        fastScroll.setRtl(rtl);
        fastScroll.setTouchSlop(SettingsList.dp(ctx, 4));
        fastScroll.setThumbDrawable(SettingsList.createThumbDrawable(ctx, rtl, 24, 0xFF808080));

        tracker = new ViaSelectionTracker();
        tracker.setListener(
                new ViaSelectionTracker.Listener() {
                    @Override
                    public void onSelectionChanged(int count) {
                        boolean hasSelection = count > 0;
                        boolean allSelected = hasSelection && count == cookies.size();
                        btnSelectAll.setText(
                                str(
                                        ctx,
                                        allSelected
                                                ? "via_action_unselect_all"
                                                : "via_action_select_all"));
                        btnDelete.setHidden(!tracker.isEditMode());
                        btnDelete.setDisabled(!hasSelection);
                        btnDelete.setText(
                                hasSelection
                                        ? String.format(str(ctx, "via_action_delete_count"), count)
                                        : str(ctx, "via_action_delete"));
                        actionBar.updateButtons(btnSelectAll, btnDelete);
                    }

                    @Override
                    public void onEditModeChanged(boolean editMode) {
                        btnEdit.setText(str(ctx, editMode ? "via_action_done" : "via_action_edit"));
                        btnSelectAll.setDisabled(!editMode);
                        if (editMode) {
                            btnSelectAll.setText(str(ctx, "via_action_select_all"));
                            btnDelete.setHidden(false);
                            btnDelete.setDisabled(true);
                            btnDelete.setText(str(ctx, "via_action_delete"));
                        } else {
                            btnSelectAll.setText(str(ctx, "via_action_select_all"));
                            btnDelete.setHidden(true);
                            btnDelete.setDisabled(true);
                            btnDelete.setText(str(ctx, "via_action_delete"));
                        }
                        actionBar.updateButtons(btnEdit, btnSelectAll, btnDelete);
                        if (suppressEditModeRebind) {
                            applyEditModeToVisibleRows();
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        actionBar.setOnActionClickListener(
                new ViaActionBar.OnActionClickListener() {
                    @Override
                    public void onActionClick(View view, ViaActionBar.Button button) {
                        int id = button.getId();
                        if (id == BTN_ID_EDIT) {
                            tracker.setEditMode(!tracker.isEditMode());
                        } else if (id == BTN_ID_SELECT_ALL) {
                            if (cookies.isEmpty()) {
                                return;
                            }
                            boolean allSelected = tracker.getSelectedCount() == cookies.size();
                            if (allSelected) {
                                tracker.deselectRange(0, cookies.size() - 1);
                            } else {
                                tracker.selectRange(0, cookies.size() - 1);
                            }
                            adapter.notifyItemRangeChanged(0, cookies.size());
                        } else if (id == BTN_ID_DELETE) {
                            confirmDeleteSelected();
                        }
                    }
                });

        reload(false);
    }

    private View buildLoadingView(Context ctx) {
        LinearLayout loading = new LinearLayout(ctx);
        loading.setOrientation(LinearLayout.VERTICAL);
        loading.setGravity(Gravity.CENTER);
        loading.setPadding(0, ViaUIRes.dp(ctx, 48), 0, ViaUIRes.dp(ctx, 48));
        android.widget.ProgressBar progressBar = new android.widget.ProgressBar(ctx);
        progressBar.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(
                    android.content.res.ColorStateList.valueOf(ThemeColors.getOkBtnBgColor(ctx)));
        }
        LinearLayout.LayoutParams pbLp =
                new LinearLayout.LayoutParams(ViaUIRes.dp(ctx, 48), ViaUIRes.dp(ctx, 48));
        pbLp.gravity = Gravity.CENTER;
        pbLp.bottomMargin = ViaUIRes.dp(ctx, 16);
        loading.addView(progressBar, pbLp);
        TextView text = new TextView(ctx);
        text.setText(str(ctx, "cookie_manager_loading"));
        text.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        text.setTextColor(ThemeColors.getHintColor(ctx));
        text.setGravity(Gravity.CENTER);
        loading.addView(text);
        return loading;
    }

    private TextView buildEmptyView(Context ctx) {
        TextView view = new TextView(ctx);
        view.setText(EMPTY_FACE);
        view.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 22);
        view.setTextColor(
                ViaUIRes.resolveAttrColor(
                        ctx, ViaUIRes.attrTextSecondary(), ThemeColors.getHintColor(ctx)));
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, 0, 0, ViaUIRes.dp(ctx, 20));
        view.setVisibility(View.GONE);
        view.setContentDescription(str(ctx, "cookie_manager_empty"));
        return view;
    }

    private static class RowHolder extends RecyclerView.ViewHolder {
        final ViaItemRow row;

        RowHolder(ViaItemRow row) {
            super(row);
            this.row = row;
        }
    }

    private class CookieAdapter extends RecyclerView.Adapter<RowHolder> {

        @Override
        public int getItemCount() {
            return cookies.size();
        }

        @Override
        public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RowHolder(new ViaItemRow(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(RowHolder holder, int position) {
            final RowHolder rowHolder = holder;
            final CookieStore.CookieInfo cookie = cookies.get(position);
            final ViaItemRow row = rowHolder.row;
            row.setTitle(cookie.name);
            row.setSubtitle(cookie.value);
            row.setEditMode(tracker.isEditMode(), false);
            row.setChecked(tracker.isSelected(position));
            row.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tracker.isEditMode()) {
                                int pos = rowHolder.getBindingAdapterPosition();
                                if (pos == RecyclerView.NO_POSITION) {
                                    return;
                                }
                                row.setChecked(tracker.toggle(pos));
                                return;
                            }
                            CookieDetailPage.show(
                                    act,
                                    cookie,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            reload(true);
                                            if (onDataChanged != null) {
                                                onDataChanged.run();
                                            }
                                        }
                                    });
                        }
                    });
            row.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int pos = rowHolder.getBindingAdapterPosition();
                            if (pos == RecyclerView.NO_POSITION) {
                                return true;
                            }
                            if (!tracker.isEditMode()) {
                                suppressEditModeRebind = true;
                                tracker.setEditMode(true);
                                suppressEditModeRebind = false;
                            }
                            if (!tracker.isSelected(pos)) {
                                tracker.setSelected(pos, true);
                                row.setChecked(true);
                            }
                            return true;
                        }
                    });
        }
    }

    private void reload(final boolean silent) {
        loadApplied = false;
        long sinceOpen = SystemClock.elapsedRealtime() - pageOpenElapsedMs;
        boolean inAnim = sinceOpen < ANIM_GUARD_MS;
        if (!silent) {
            if (!inAnim) {
                showLoading();
            } else {
                new Handler()
                        .postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!loadApplied) {
                                            showLoading();
                                        }
                                    }
                                },
                                ANIM_GUARD_MS - sinceOpen + 50);
            }
        }
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                final List<CookieStore.CookieInfo> loaded =
                                        CookieStore.loadCookies(act, domain);
                                act.runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                if (act.isFinishing() || act.isDestroyed()) {
                                                    return;
                                                }
                                                long elapsed =
                                                        SystemClock.elapsedRealtime()
                                                                - pageOpenElapsedMs;
                                                if (elapsed < ANIM_GUARD_MS) {
                                                    new Handler()
                                                            .postDelayed(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            applyCookies(loaded);
                                                                        }
                                                                    },
                                                                    ANIM_GUARD_MS - elapsed);
                                                } else {
                                                    applyCookies(loaded);
                                                }
                                            }
                                        });
                            }
                        })
                .start();
    }

    private void showLoading() {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void applyEditModeToVisibleRows() {
        if (recyclerView == null) {
            return;
        }
        boolean editMode = tracker.isEditMode();
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            if (child instanceof ViaItemRow) {
                ((ViaItemRow) child).setEditMode(editMode, false);
            }
        }
    }

    private void updateEmptyState() {
        boolean empty = cookies.isEmpty();
        boolean showEmpty =
                empty && (emptyView == null || emptyView.getVisibility() != View.VISIBLE);
        if (emptyView != null) {
            emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
            if (showEmpty) {
                emptyView.animate().cancel();
                emptyView.setAlpha(0f);
                emptyView.setTranslationY(0f);
                emptyView
                        .animate()
                        .alpha(1f)
                        .setDuration(160)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
            }
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }

    private void applyCookies(List<CookieStore.CookieInfo> loaded) {
        if (act.isFinishing() || act.isDestroyed()) {
            return;
        }
        cookies.clear();
        cookies.addAll(loaded);
        tracker.clearSelection();
        adapter.notifyDataSetChanged();
        if (loadingContainer != null) {
            loadingContainer.setVisibility(View.GONE);
        }
        updateEmptyState();
        loadApplied = true;
    }

    private void confirmDeleteSelected() {
        if (tracker.getSelectedCount() == 0) {
            return;
        }
        final List<CookieStore.CookieInfo> selected = new ArrayList<CookieStore.CookieInfo>();
        int[] indices = tracker.getSelectedIndices(false);
        for (int pos : indices) {
            if (pos >= 0 && pos < cookies.size()) {
                selected.add(cookies.get(pos));
            }
        }
        if (selected.isEmpty()) {
            return;
        }
        SettingsUI.showMessageDialog(
                act,
                "cookie_delete_confirm_title",
                "cookie_delete_confirm_msg",
                "cookie_manager_delete_btn",
                "dialog_cancel",
                new Runnable() {
                    @Override
                    public void run() {
                        doDelete(selected);
                    }
                },
                null);
    }

    private void doDelete(final List<CookieStore.CookieInfo> selected) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                final int deleted = CookieStore.deleteCookies(act, selected);
                                final List<CookieStore.CookieInfo> refreshed =
                                        CookieStore.loadCookies(act, domain);
                                act.runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                if (act.isFinishing() || act.isDestroyed()) {
                                                    return;
                                                }
                                                cookies.clear();
                                                cookies.addAll(refreshed);
                                                tracker.clearSelection();
                                                adapter.notifyDataSetChanged();
                                                updateEmptyState();
                                                if (deleted > 0) {
                                                    Hook.jiguroMessageWithContext(
                                                            act, str(act, "cookie_delete_success"));
                                                } else {
                                                    Hook.jiguroMessageWithContext(
                                                            act,
                                                            str(act, "cookie_delete_no_selected"));
                                                }
                                                if (onDataChanged != null) {
                                                    onDataChanged.run();
                                                }
                                            }
                                        });
                            }
                        })
                .start();
    }

    private static String str(Context ctx, String key) {
        return LocalizedStringProvider.getInstance().get(ctx, key);
    }
}
