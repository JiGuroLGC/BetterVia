package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fastscroll.FastScroll;
import fastscroll.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class CookieManagerPage {

    private static final int BTN_ID_SELECT_ALL = 1;
    private static final int BTN_ID_DELETE = 2;
    private static final int BTN_ID_EDIT = 3;
    private static final String EMPTY_FACE = "¯\\_(ツ)_/¯";
    private static final int ANIM_GUARD_MS = 240;

    private final Activity act;
    private final List<CookieStore.DomainInfo> fullList = new ArrayList<CookieStore.DomainInfo>();
    private final List<CookieStore.DomainInfo> displayed = new ArrayList<CookieStore.DomainInfo>();
    private ViaSelectionTracker tracker;
    private ViaActionBar actionBar;
    private ViaActionBar.Button btnSelectAll;
    private ViaActionBar.Button btnDelete;
    private ViaActionBar.Button btnEdit;
    private EditText searchEdit;
    private Handler searchHandler;
    private View loadingContainer;
    private FastScrollRecyclerView recyclerView;
    private TextView emptyView;
    private DomainAdapter adapter;
    private long pageOpenElapsedMs;
    private boolean loadApplied;
    private boolean suppressEditModeRebind;

    public static void show(Activity act) {
        if (act == null || act.isFinishing() || act.isDestroyed()) {
            return;
        }
        new CookieManagerPage(act).showInternal();
    }

    private CookieManagerPage(Activity act) {
        this.act = act;
    }

    private void showInternal() {
        SettingsUI.showPage(
                act,
                "cookie_management_title",
                "cookie_manager_refresh",
                new Runnable() {
                    @Override
                    public void run() {
                        reload(false, true);
                    }
                },
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

        searchEdit = ViaSearchBox.create(ctx, str(ctx, "cookie_domain_search_hint"));
        LinearLayout.LayoutParams searchLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchLp.setMarginStart(ViaUIRes.dp(ctx, 14));
        searchLp.setMarginEnd(ViaUIRes.dp(ctx, 14));
        searchLp.topMargin = ViaUIRes.dp(ctx, 10);
        searchLp.bottomMargin = ViaUIRes.dp(ctx, 10);
        searchEdit.setLayoutParams(searchLp);
        root.addView(searchEdit);

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

        adapter = new DomainAdapter();
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
                        boolean allSelected = hasSelection && count == displayed.size();
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
                            if (displayed.isEmpty()) {
                                return;
                            }
                            boolean allSelected = tracker.getSelectedCount() == displayed.size();
                            if (allSelected) {
                                tracker.deselectRange(0, displayed.size() - 1);
                            } else {
                                tracker.selectRange(0, displayed.size() - 1);
                            }
                            adapter.notifyItemRangeChanged(0, displayed.size());
                        } else if (id == BTN_ID_DELETE) {
                            confirmDeleteSelectedDomains();
                        }
                    }
                });

        searchHandler = new Handler();
        final Runnable filterAction =
                new Runnable() {
                    @Override
                    public void run() {
                        int oldSize = displayed.size();
                        String[] oldDomains = new String[oldSize];
                        for (int i = 0; i < oldSize; i++) {
                            oldDomains[i] = displayed.get(i).domain;
                        }
                        applyFilter(searchEdit.getText().toString());
                        boolean changed = displayed.size() != oldSize;
                        if (!changed) {
                            for (int i = 0; i < oldSize; i++) {
                                if (!oldDomains[i].equals(displayed.get(i).domain)) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                        if (changed) {
                            animateListRefresh();
                        }
                    }
                };
        searchEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        searchHandler.removeCallbacks(filterAction);
                        searchHandler.postDelayed(filterAction, 100);
                    }
                });

        reload(false, false);
    }

    private View buildLoadingView(Context ctx) {
        LinearLayout loading = new LinearLayout(ctx);
        loading.setOrientation(LinearLayout.VERTICAL);
        loading.setGravity(Gravity.CENTER);
        loading.setPadding(0, ViaUIRes.dp(ctx, 48), 0, ViaUIRes.dp(ctx, 48));
        ProgressBar progressBar = new ProgressBar(ctx);
        progressBar.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(
                    ColorStateList.valueOf(ThemeColors.getOkBtnBgColor(ctx)));
        }
        LinearLayout.LayoutParams pbLp =
                new LinearLayout.LayoutParams(ViaUIRes.dp(ctx, 48), ViaUIRes.dp(ctx, 48));
        pbLp.gravity = Gravity.CENTER;
        pbLp.bottomMargin = ViaUIRes.dp(ctx, 16);
        loading.addView(progressBar, pbLp);
        TextView text = new TextView(ctx);
        text.setText(str(ctx, "cookie_manager_loading"));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        text.setTextColor(ThemeColors.getHintColor(ctx));
        text.setGravity(Gravity.CENTER);
        loading.addView(text);
        return loading;
    }

    private TextView buildEmptyView(Context ctx) {
        TextView view = new TextView(ctx);
        view.setText(EMPTY_FACE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
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

    private class DomainAdapter extends RecyclerView.Adapter<RowHolder> {

        @Override
        public int getItemCount() {
            return displayed.size();
        }

        @Override
        public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RowHolder(new ViaItemRow(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(RowHolder holder, int position) {
            final RowHolder rowHolder = holder;
            final CookieStore.DomainInfo entry = displayed.get(position);
            final ViaItemRow row = rowHolder.row;
            row.setTitle(entry.domain);
            row.setSubtitle(
                    String.format(str(act, "cookie_domain_count_label"), entry.cookieCount));
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
                            CookieDomainPage.show(
                                    act,
                                    entry.domain,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            reload(true, false);
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

    private void reload(final boolean silent, final boolean toast) {
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
                                final List<CookieStore.DomainInfo> domains =
                                        CookieStore.loadDomains(act);
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
                                                                            applyDomains(
                                                                                    domains, toast);
                                                                        }
                                                                    },
                                                                    ANIM_GUARD_MS - elapsed);
                                                } else {
                                                    applyDomains(domains, toast);
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
        boolean empty = displayed.isEmpty();
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

    private void applyDomains(List<CookieStore.DomainInfo> domains, boolean toast) {
        if (act.isFinishing() || act.isDestroyed()) {
            return;
        }
        fullList.clear();
        fullList.addAll(domains);
        applyFilter(searchEdit != null ? searchEdit.getText().toString() : "");
        tracker.clearSelection();
        adapter.notifyDataSetChanged();
        if (loadingContainer != null) {
            loadingContainer.setVisibility(View.GONE);
        }
        updateEmptyState();
        loadApplied = true;
        if (toast) {
            Hook.jiguroMessageWithContext(act, str(act, "cookie_management_refreshed"));
        }
    }

    private void applyFilter(String query) {
        displayed.clear();
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            displayed.addAll(fullList);
        } else {
            for (CookieStore.DomainInfo entry : fullList) {
                if (entry.domain.toLowerCase().contains(q)) {
                    displayed.add(entry);
                }
            }
        }
    }

    private void confirmDeleteSelectedDomains() {
        if (tracker.getSelectedCount() == 0) {
            return;
        }
        final List<String> domains = new ArrayList<String>();
        int[] selected = tracker.getSelectedIndices(false);
        for (int pos : selected) {
            if (pos >= 0 && pos < displayed.size()) {
                domains.add(displayed.get(pos).domain);
            }
        }
        if (domains.isEmpty()) {
            return;
        }
        SettingsUI.showMessageDialog(
                act,
                "cookie_delete_confirm_title",
                "cookie_domain_delete_selected_confirm_msg",
                "cookie_manager_delete_btn",
                "dialog_cancel",
                new Runnable() {
                    @Override
                    public void run() {
                        doDeleteDomains(domains);
                    }
                },
                null);
    }

    private void doDeleteDomains(final List<String> domains) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                final int deletedCookies = CookieStore.deleteDomains(act, domains);
                                final List<CookieStore.DomainInfo> refreshed =
                                        CookieStore.loadDomains(act);
                                act.runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                if (act.isFinishing() || act.isDestroyed()) {
                                                    return;
                                                }
                                                fullList.clear();
                                                fullList.addAll(refreshed);
                                                applyFilter(
                                                        searchEdit != null
                                                                ? searchEdit.getText().toString()
                                                                : "");
                                                tracker.clearSelection();
                                                adapter.notifyDataSetChanged();
                                                Hook.jiguroMessageWithContext(
                                                        act,
                                                        String.format(
                                                                str(
                                                                        act,
                                                                        "cookie_domain_delete_selected_success"),
                                                                domains.size(),
                                                                deletedCookies));
                                            }
                                        });
                            }
                        })
                .start();
    }

    private void animateListRefresh() {
        ViaUIRes.animateListRefresh(recyclerView);
    }

    private static String str(Context ctx, String key) {
        return LocalizedStringProvider.getInstance().get(ctx, key);
    }
}
