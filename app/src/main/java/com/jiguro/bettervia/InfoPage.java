package com.jiguro.bettervia;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public final class InfoPage {

    private static final String CLIPBOARD_LABEL = "BetterVia Info";

    private InfoPage() {}

    public static void show(final Hook host, final Context ctx) {
        final Activity act = host.getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "info_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        list.addItemKeepRipple(
                                "info_status_title",
                                "info_status_working",
                                new Runnable() {
                                    @Override
                                    public void run() {}
                                });

                        list.addSectionHeader("info_section_build");
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_build_version",
                                Hook.MODULE_VERSION_NAME + " (" + Hook.MODULE_VERSION_CODE + ")");
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_build_api",
                                "universal (Xposed API 82-102 verified)");
                        addCopyItem(
                                list, act, ctx, host, "info_build_time", Hook.MODULE_BUILD_TIME);
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_build_gradle",
                                "Android Gradle Plugin 7.0.2");
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_build_scope",
                                "Android 5 (API 21) - Android 16 (API 36)");
                        addCopyItem(
                                list, act, ctx, host, "info_build_effective", "Via CN/GP 6.6.0+");

                        list.addSectionHeader("info_section_runtime");
                        long elapsed = SystemClock.elapsedRealtime() - Hook.moduleStartElapsedMs;
                        if (elapsed < 0L) elapsed = 0L;
                        long totalSec = elapsed / 1000L;
                        long hh = totalSec / 3600L;
                        long mm = (totalSec % 3600L) / 60L;
                        long ss = totalSec % 60L;
                        String statusValue =
                                String.format(
                                        LocalizedStringProvider.getInstance()
                                                .get(act, "info_runtime_status_uptime"),
                                        Long.valueOf(hh),
                                        Long.valueOf(mm),
                                        Long.valueOf(ss));
                        addCopyItem(list, act, ctx, host, "info_runtime_status", statusValue);
                        ViaVersionDetector.VersionInfo vi =
                                ViaVersionDetector.detectViaVersion(act);
                        String viaVer;
                        String pkgName;
                        if (vi != null) {
                            viaVer = vi.versionName + " (" + vi.versionCode + ")";
                            pkgName = vi.packageName;
                        } else {
                            viaVer = "-";
                            pkgName = "-";
                        }
                        addCopyItem(list, act, ctx, host, "info_runtime_via", viaVer);
                        addCopyItem(list, act, ctx, host, "info_runtime_package", pkgName);
                        Integer selCode = ViaClassMapping.getUserSelectedVersionCode();
                        String mappingCodeStr =
                                selCode != null && selCode.intValue() > 0
                                        ? (ViaVersionDetector.getVersionName(selCode.intValue())
                                                + " ("
                                                + selCode.intValue()
                                                + ")")
                                        : "-";
                        addCopyItem(list, act, ctx, host, "info_runtime_mapping", mappingCodeStr);
                        final String[] modeHolder =
                                new String[] {
                                    LocalizedStringProvider.getInstance()
                                            .get(act, "info_mode_detecting")
                                };
                        final int modeIndex =
                                addCopyItem(list, act, ctx, host, "info_runtime_mode", modeHolder);
                        HostPatchDetector.detectAsync(
                                ctx,
                                new HostPatchDetector.Callback() {
                                    @Override
                                    public void onResult(boolean lspatch, String[] riskApks) {
                                        modeHolder[0] =
                                                LocalizedStringProvider.getInstance()
                                                        .get(
                                                                act,
                                                                lspatch
                                                                        ? "info_card_mode_portable"
                                                                        : "info_card_mode_inject");
                                        list.updateItemText(modeIndex, modeHolder[0], true);
                                    }
                                });

                        list.addSectionHeader("info_section_device");
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_device_model",
                                Build.MANUFACTURER + " " + Build.MODEL);
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_device_os",
                                "Android "
                                        + Build.VERSION.RELEASE
                                        + " (API "
                                        + Build.VERSION.SDK_INT
                                        + ")");
                        String arch =
                                (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0)
                                        ? Build.SUPPORTED_ABIS[0]
                                        : (Build.CPU_ABI != null ? Build.CPU_ABI : "-");
                        addCopyItem(list, act, ctx, host, "info_device_arch", arch);
                        String kernel = System.getProperty("os.version");
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_device_kernel",
                                kernel != null && kernel.length() > 0 ? kernel : "-");
                        DisplayMetrics dm = act.getResources().getDisplayMetrics();
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_device_screen",
                                dm.widthPixels
                                        + " x "
                                        + dm.heightPixels
                                        + ", "
                                        + dm.densityDpi
                                        + " dpi");
                        String patch = Build.VERSION.SECURITY_PATCH;
                        addCopyItem(
                                list,
                                act,
                                ctx,
                                host,
                                "info_device_security_patch",
                                patch != null && patch.length() > 0 ? patch : "-");

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private static void addCopyItem(
            final SettingsList list,
            final Activity act,
            final Context ctx,
            final Hook host,
            final String titleKey,
            final String value) {
        if (value == null) return;
        addCopyItem(list, act, ctx, host, titleKey, new String[] {value});
    }

    private static int addCopyItem(
            final SettingsList list,
            final Activity act,
            final Context ctx,
            final Hook host,
            final String titleKey,
            final String[] valueHolder) {
        list.addItemKeepRipple(
                titleKey,
                new Runnable() {
                    @Override
                    public void run() {
                        copyValue(ctx, act, valueHolder[0]);
                    }
                });
        int idx = list.getItemCount() - 1;
        list.updateItemText(idx, valueHolder[0], true);
        return idx;
    }

    private static void copyValue(final Context ctx, final Activity act, final String value) {
        try {
            ClipboardManager cb =
                    (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cb != null) {
                cb.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_LABEL, value));
            }
        } catch (Throwable ignored) {
        }
        String toast = LocalizedStringProvider.getInstance().get(act, "info_copied_toast");
        Hook.jiguroMessageWithContext(ctx, toast);
    }
}
