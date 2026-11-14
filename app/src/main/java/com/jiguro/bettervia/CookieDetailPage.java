package com.jiguro.bettervia;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

final class CookieDetailPage {

    private static final int FIELD_HOST = 0;
    private static final int FIELD_NAME = 1;
    private static final int FIELD_VALUE = 2;
    private static final int FIELD_PATH = 3;
    private static final int SWITCH_SECURE = 0;
    private static final int SWITCH_HTTP_ONLY = 1;
    private static final int SWITCH_PERSISTENT = 2;
    private static final int SWITCH_HAS_EXPIRES = 3;
    private static final String CLIPBOARD_LABEL = "Cookie";

    private final Activity act;
    private final CookieStore.CookieInfo cookie;
    private final Runnable onChanged;
    private String originalHostKey;
    private String originalName;
    private SettingsList list;
    private int idxHost;
    private int idxName;
    private int idxValue;
    private int idxPath;

    static void show(Activity act, CookieStore.CookieInfo cookie, Runnable onChanged) {
        if (act == null || act.isFinishing() || act.isDestroyed() || cookie == null) {
            return;
        }
        new CookieDetailPage(act, cookie, onChanged).showInternal();
    }

    private CookieDetailPage(Activity act, CookieStore.CookieInfo cookie, Runnable onChanged) {
        this.act = act;
        this.cookie = cookie;
        this.onChanged = onChanged;
    }

    private void showInternal() {
        originalHostKey = cookie.hostKey;
        originalName = cookie.name;
        SettingsUI.showPage(
                act,
                "cookie_detail_dialog_title",
                "via_action_delete",
                new Runnable() {
                    @Override
                    public void run() {
                        confirmDelete();
                    }
                },
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, Activity act) {
                        buildContent(content);
                    }
                });
    }

    private void buildContent(ViewGroup content) {
        list = new SettingsList(act);

        list.addSectionHeader("cookie_detail_basic_info");
        idxHost = addEditableRow("cookie_field_host_key", cookie.hostKey, FIELD_HOST, 1);
        idxName = addEditableRow("cookie_field_name", cookie.name, FIELD_NAME, 1);
        idxValue = addEditableRow("cookie_field_value", cookie.value, FIELD_VALUE, 4);
        idxPath = addEditableRow("cookie_field_path", cookie.path, FIELD_PATH, 1);

        list.addSectionHeader("cookie_detail_time_info");
        addCopyRow(
                "cookie_field_creation_time",
                cookie.creationUtc > 0
                        ? CookieStore.formatTime(act, cookie.creationUtc)
                        : str("cookie_field_unknown"));
        addCopyRow(
                "cookie_field_last_access",
                cookie.lastAccessUtc > 0
                        ? CookieStore.formatTime(act, cookie.lastAccessUtc)
                        : str("cookie_field_unknown"));
        addCopyRow(
                "cookie_field_expires",
                cookie.expiresUtc > 0
                        ? CookieStore.formatTime(act, cookie.expiresUtc)
                        : str("cookie_field_session"));
        addCopyRow(
                "cookie_field_last_update",
                cookie.lastUpdateUtc > 0
                        ? CookieStore.formatTime(act, cookie.lastUpdateUtc)
                        : str("cookie_field_unknown"));

        list.addSectionHeader("cookie_detail_security_info");
        addSwitchRow("cookie_field_secure", cookie.secure, SWITCH_SECURE);
        addSwitchRow("cookie_field_httponly", cookie.httpOnly, SWITCH_HTTP_ONLY);
        addSwitchRow("cookie_field_persistent", cookie.persistent, SWITCH_PERSISTENT);
        addSwitchRow("cookie_field_has_expires", cookie.hasExpires, SWITCH_HAS_EXPIRES);

        list.addSectionHeader("cookie_detail_advanced_info");
        addCopyRow("cookie_field_priority", String.valueOf(cookie.priority));
        addCopyRow("cookie_field_samesite", CookieStore.sameSiteText(act, cookie.samesite));
        addCopyRow(
                "cookie_field_source_port",
                cookie.sourcePort > 0
                        ? String.valueOf(cookie.sourcePort)
                        : str("cookie_field_default"));
        addCopyRow("cookie_field_source_type", CookieStore.sourceTypeText(act, cookie.sourceType));

        content.addView(
                list,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private int addEditableRow(String titleKey, String value, final int field, final int lines) {
        int index = list.getItemCount();
        list.addItemKeepRipple(
                titleKey,
                new Runnable() {
                    @Override
                    public void run() {
                        showEditDialog(field, lines);
                    }
                });
        list.updateItemText(index, displayValue(value), true);
        return index;
    }

    private int addCopyRow(String titleKey, final String value) {
        int index = list.getItemCount();
        list.addItemKeepRipple(
                titleKey,
                new Runnable() {
                    @Override
                    public void run() {
                        copyToClipboard(value);
                    }
                });
        list.updateItemText(index, displayValue(value), true);
        return index;
    }

    private int addSwitchRow(String titleKey, boolean checked, final int which) {
        final int index = list.getItemCount();
        list.addSwitchItem(
                titleKey,
                null,
                checked,
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        saveSwitch(which, isChecked, index);
                    }
                });
        return index;
    }

    private void showEditDialog(final int field, final int lines) {
        String titleKey;
        String current;
        switch (field) {
            case FIELD_HOST:
                titleKey = "cookie_field_host_key";
                current = cookie.hostKey;
                break;
            case FIELD_NAME:
                titleKey = "cookie_field_name";
                current = cookie.name;
                break;
            case FIELD_VALUE:
                titleKey = "cookie_field_value";
                current = cookie.value;
                break;
            default:
                titleKey = "cookie_field_path";
                current = cookie.path;
                break;
        }
        SettingsUI.showInputDialog(
                act,
                titleKey,
                (String) null,
                (String) null,
                current,
                lines,
                "dialog_ok",
                "dialog_cancel",
                new SettingsUI.OnInputListener() {
                    @Override
                    public void onConfirm(String input) {
                        applyEdit(field, input);
                    }

                    @Override
                    public void onCancel() {}
                });
    }

    private void applyEdit(int field, String input) {
        String oldHost = cookie.hostKey;
        String oldName = cookie.name;
        String oldValue = cookie.value;
        String oldPath = cookie.path;
        switch (field) {
            case FIELD_HOST:
                cookie.hostKey = input == null ? "" : input;
                break;
            case FIELD_NAME:
                cookie.name = input == null ? "" : input;
                break;
            case FIELD_VALUE:
                cookie.value = input == null ? "" : input;
                break;
            default:
                cookie.path = input == null ? "" : input;
                break;
        }
        boolean ok = CookieStore.updateCookie(act, cookie, originalHostKey, originalName, oldPath);
        if (!ok) {
            cookie.hostKey = oldHost;
            cookie.name = oldName;
            cookie.value = oldValue;
            cookie.path = oldPath;
            Hook.jiguroMessageWithContext(act, str("cookie_save_failed"));
            return;
        }
        originalHostKey = cookie.hostKey;
        originalName = cookie.name;
        list.updateItemText(fieldRowIndex(field), displayValue(fieldValue(field)), true);
        if (onChanged != null) {
            onChanged.run();
        }
    }

    private void saveSwitch(int which, boolean value, int rowIndex) {
        boolean old;
        switch (which) {
            case SWITCH_SECURE:
                old = cookie.secure;
                cookie.secure = value;
                break;
            case SWITCH_HTTP_ONLY:
                old = cookie.httpOnly;
                cookie.httpOnly = value;
                break;
            case SWITCH_PERSISTENT:
                old = cookie.persistent;
                cookie.persistent = value;
                break;
            default:
                old = cookie.hasExpires;
                cookie.hasExpires = value;
                break;
        }
        if (!CookieStore.updateCookie(act, cookie, originalHostKey, originalName, cookie.path)) {
            switch (which) {
                case SWITCH_SECURE:
                    cookie.secure = old;
                    break;
                case SWITCH_HTTP_ONLY:
                    cookie.httpOnly = old;
                    break;
                case SWITCH_PERSISTENT:
                    cookie.persistent = old;
                    break;
                default:
                    cookie.hasExpires = old;
                    break;
            }
            list.updateSwitch(rowIndex, old);
            Hook.jiguroMessageWithContext(act, str("cookie_save_failed"));
        }
    }

    private void confirmDelete() {
        SettingsUI.showMessageDialog(
                act,
                "cookie_delete_confirm_title",
                "cookie_delete_confirm_msg",
                "cookie_manager_delete_btn",
                "dialog_cancel",
                new Runnable() {
                    @Override
                    public void run() {
                        if (CookieStore.deleteCookie(act, cookie)) {
                            SettingsUI.dismissCurrentPage(act);
                            if (onChanged != null) {
                                onChanged.run();
                            }
                            Hook.jiguroMessageWithContext(act, str("cookie_delete_success"));
                        } else {
                            Hook.jiguroMessageWithContext(act, str("cookie_delete_error"));
                        }
                    }
                },
                null);
    }

    private void copyToClipboard(CharSequence text) {
        try {
            ClipboardManager cb =
                    (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cb != null) {
                cb.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_LABEL, text));
                Hook.jiguroMessageWithContext(act, str("cookie_value_copied"));
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 复制Cookie信息失败: " + t);
        }
    }

    private static CharSequence displayValue(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return value;
    }

    private int fieldRowIndex(int field) {
        switch (field) {
            case FIELD_HOST:
                return idxHost;
            case FIELD_NAME:
                return idxName;
            case FIELD_VALUE:
                return idxValue;
            default:
                return idxPath;
        }
    }

    private String fieldValue(int field) {
        switch (field) {
            case FIELD_HOST:
                return cookie.hostKey;
            case FIELD_NAME:
                return cookie.name;
            case FIELD_VALUE:
                return cookie.value;
            default:
                return cookie.path;
        }
    }

    private String str(String key) {
        return LocalizedStringProvider.getInstance().get(act, key);
    }
}
