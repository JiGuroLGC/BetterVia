package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

final class StartupCheckChain {

    private static final String KEY_HAS_RISK_NOTICE = "has_runtime_risk_notice";

    private static final long RISK_CHECK_TIMEOUT_MS = 3000L;

    private static boolean sRiskNoticeShown = false;

    private final Hook host;

    private StartupCheckChain(Hook host) {
        this.host = host;
    }

    static void start(Hook host, Context ctx) {
        if (host == null || ctx == null) return;
        new StartupCheckChain(host).checkBasicSettings(ctx);
    }

    private void checkViaVersion(final Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            String currentVersionName = pi.versionName;
            int currentVersionCode = pi.versionCode;
            Hook.currentDetectedVersion = currentVersionName;

            long currentUpdateTime = new java.io.File(pi.applicationInfo.sourceDir).lastModified();

            int lastVersionCode = Hook.getPrefInt(ctx, Hook.KEY_LAST_VIA_VERSION_CODE, -1);
            String lastVersionName = Hook.getPrefString(ctx, Hook.KEY_LAST_VIA_VERSION_NAME, "");
            long lastUpdateTime = Hook.getPrefLong(ctx, Hook.KEY_LAST_VIA_UPDATE_TIME, 0);

            boolean hasViaUpdate = false;
            if (lastVersionCode != -1) {
                if (currentVersionCode != lastVersionCode) {
                    hasViaUpdate = true;
                    Hook.bvLog(
                            "[BetterVia] 检测到Via版本代码变化: "
                                    + lastVersionCode
                                    + " -> "
                                    + currentVersionCode);
                } else if (currentUpdateTime != lastUpdateTime) {
                    hasViaUpdate = true;
                    Hook.bvLog("[BetterVia] 检测到Via AP修改时间变化");
                }
            }
            Hook.sViaUpdateDetected = hasViaUpdate;

            boolean hasVersionSelection = Hook.getPrefBoolean(ctx, "has_version_selection", false);

            int savedVersionCode = Hook.getPrefInt(ctx, Hook.KEY_SELECTED_VIA_VERSION, 20260706);
            Hook.selectedViaVersionCode = savedVersionCode;

            ViaClassMapping.setUserSelectedVersionCode(savedVersionCode);

            if (!hasVersionSelection) {
                host.showVersionSelectionDialog(
                        ctx,
                        currentVersionName,
                        savedVersionCode,
                        false,
                        currentVersionCode,
                        currentUpdateTime);
            } else if (hasViaUpdate) {
                Hook.bvLog("[BetterVia] Via已更新，重新弹出版本选择对话框");
                host.showVersionSelectionDialog(
                        ctx,
                        currentVersionName,
                        savedVersionCode,
                        true,
                        currentVersionCode,
                        currentUpdateTime);
            } else {
                Hook.bvLog(
                        "[BetterVia] 使用已保存的Via版本: "
                                + ViaVersionDetector.getVersionName(savedVersionCode)
                                + " (code: "
                                + savedVersionCode
                                + ")");
            }

            if (hasVersionSelection && !hasViaUpdate && Hook.hasShownStartupDialog) {
                Hook.bvLog("[BetterVia] 启动流程已完成，需要重启Via以使设置生效");
                new Handler(Looper.getMainLooper())
                        .postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Hook.jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(ctx, "startup_restart_hint"));
                                            new Handler(Looper.getMainLooper())
                                                    .postDelayed(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    System.exit(0);
                                                                }
                                                            },
                                                            Hook.RESTART_VIA_DELAY_MS);
                                        } catch (Exception e) {
                                            Hook.bvLog("[BetterVia] 重启逻辑执行失败: " + e.getMessage());
                                        }
                                    }
                                },
                                500);
            }
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 版本检测失败: " + e.getMessage());
        }
    }

    private void showBasicSettingsDialog(final Context ctx) {
        final Activity act = Hook.getActivityFrom(ctx);
        if (act == null) return;

        final String[] langValues = {"auto", "zh-CN", "zh-TW", "en"};
        final String[] langKeys = {
            "language_auto", "language_zh_cn", "language_zh_tw", "language_en"
        };
        String savedLang = Hook.getSavedLanguage(ctx);
        int langIdx = 0;
        for (int i = 0; i < langValues.length; i++)
            if (langValues[i].equals(savedLang)) {
                langIdx = i;
                break;
            }

        final String[] sourceValues = {Hook.NETWORK_SOURCE_VERCEL, Hook.NETWORK_SOURCE_GITHUB};
        final String[] sourceKeys = {"network_source_vercel", "network_source_github"};
        String savedSource =
                Hook.getPrefString(ctx, Hook.KEY_NETWORK_SOURCE, Hook.DEFAULT_NETWORK_SOURCE);
        int sourceIdx = savedSource.equals(Hook.NETWORK_SOURCE_VERCEL) ? 0 : 1;

        SettingsUI.DialogSection[] sections =
                new SettingsUI.DialogSection[] {
                    new SettingsUI.DialogSection(
                            "basic_settings_language_section", langKeys, langIdx),
                    new SettingsUI.DialogSection(
                            "basic_settings_network_section", sourceKeys, sourceIdx)
                };

        SettingsUI.showSectionedSelectDialog(
                act,
                "basic_settings_dialog_title",
                sections,
                "dialog_ok",
                "dialog_cancel",
                new SettingsUI.OnSectionedSelectListener() {
                    @Override
                    public void onConfirm(int[] selectedIndices) {
                        Hook.saveLanguageSetting(ctx, langValues[selectedIndices[0]]);
                        Hook.putPrefString(
                                ctx, Hook.KEY_NETWORK_SOURCE, sourceValues[selectedIndices[1]]);
                        Hook.putPrefBoolean(ctx, Hook.KEY_HAS_LANGUAGE_SELECTION, true);
                        Hook.putPrefBoolean(ctx, Hook.KEY_HAS_NETWORK_SOURCE, true);
                        checkUserAgreement(ctx);
                    }

                    @Override
                    public void onCancel() {
                        Hook.saveLanguageSetting(ctx, "auto");
                        Hook.putPrefString(
                                ctx, Hook.KEY_NETWORK_SOURCE, Hook.NETWORK_SOURCE_VERCEL);
                        Hook.putPrefBoolean(ctx, Hook.KEY_HAS_LANGUAGE_SELECTION, true);
                        Hook.putPrefBoolean(ctx, Hook.KEY_HAS_NETWORK_SOURCE, true);
                        checkUserAgreement(ctx);
                    }
                });

        Hook.hasShownStartupDialog = true;
    }

    private void showUserAgreementDialog(final Context ctx) {
        final Activity act = Hook.getActivityFrom(ctx);
        if (act == null) return;

        String content = LocalizedStringProvider.getInstance().get(ctx, "user_agreement_content");
        String linkText =
                LocalizedStringProvider.getInstance().get(ctx, "user_agreement_link_text");

        SpannableString spannable = new SpannableString(content);
        int linkStart = content.indexOf(linkText);
        if (linkStart >= 0) {
            int linkEnd = linkStart + linkText.length();
            final int linkColor = host.getTitleColor(act);
            ClickableSpan clickableSpan =
                    new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            loadUserAgreementUrl(ctx);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(linkColor);
                            ds.setUnderlineText(true);
                        }
                    };
            spannable.setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        SettingsUI.showMessageDialog(
                act,
                "user_agreement_dialog_title",
                spannable,
                "user_agreement_checkbox",
                false,
                "user_agreement_agree",
                "user_agreement_reject",
                new Runnable() {
                    @Override
                    public void run() {
                        Hook.putPrefBoolean(ctx, Hook.KEY_HAS_USER_AGREEMENT, true);
                        maybeShowRiskNotice(
                                ctx,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        checkViaVersion(ctx);
                                    }
                                });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Hook.jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, "user_agreement_reject_toast"));
                    }
                },
                null,
                true,
                false);

        Hook.hasShownStartupDialog = true;
    }

    private void loadUserAgreementUrl(final Context ctx) {
        Hook.jiguroMessageWithContext(
                ctx, LocalizedStringProvider.getInstance().get(ctx, "user_agreement_loading"));

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            Hook.getPrefString(
                                                    ctx,
                                                    Hook.KEY_NETWORK_SOURCE,
                                                    Hook.DEFAULT_NETWORK_SOURCE);
                                    String jsonUrl =
                                            Hook.NETWORK_SOURCE_VERCEL.equals(networkSource)
                                                    ? "https://raw.196104.xyz/useragreement.json"
                                                    : "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/useragreement.json";

                                    HttpURLConnection connection = null;
                                    InputStream inputStream = null;
                                    try {
                                        URL url = new URL(jsonUrl);
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setConnectTimeout(10000);
                                        connection.setReadTimeout(10000);

                                        if (connection.getResponseCode()
                                                == HttpURLConnection.HTTP_OK) {
                                            inputStream = connection.getInputStream();
                                            StringBuilder jsonBuilder = new StringBuilder();
                                            byte[] buffer = new byte[4096];
                                            int bytesRead;
                                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                jsonBuilder.append(
                                                        new String(buffer, 0, bytesRead, "UTF-8"));
                                            }

                                            String jsonContent = jsonBuilder.toString();
                                            JSONObject jsonObject = new JSONObject(jsonContent);
                                            JSONObject userAgreementUrls =
                                                    jsonObject.getJSONObject("user_agreement_urls");

                                            String userLang = Hook.getSavedLanguage(ctx);
                                            if ("auto".equals(userLang)) {
                                                Locale locale;
                                                if (Build.VERSION.SDK_INT
                                                        >= Build.VERSION_CODES.N) {
                                                    locale =
                                                            ctx.getResources()
                                                                    .getConfiguration()
                                                                    .getLocales()
                                                                    .get(0);
                                                } else {
                                                    locale =
                                                            ctx.getResources()
                                                                    .getConfiguration()
                                                                    .locale;
                                                }

                                                if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
                                                    userLang = "zh_CN";
                                                } else if (Locale.TRADITIONAL_CHINESE.equals(
                                                        locale)) {
                                                    userLang = "zh_TW";
                                                } else {
                                                    userLang = "en";
                                                }
                                            } else {
                                                if ("zh-CN".equals(userLang)) {
                                                    userLang = "zh_CN";
                                                } else if ("zh-TW".equals(userLang)) {
                                                    userLang = "zh_TW";
                                                } else {
                                                    userLang = "en";
                                                }
                                            }

                                            String agreementUrl = null;
                                            if (userAgreementUrls.has(userLang)) {
                                                JSONObject langObj =
                                                        userAgreementUrls.getJSONObject(userLang);
                                                agreementUrl = langObj.getString("url");
                                            }

                                            if (agreementUrl != null && !agreementUrl.isEmpty()) {
                                                final String finalUrl = agreementUrl;
                                                new Handler(Looper.getMainLooper())
                                                        .post(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Intent intent =
                                                                                new Intent(
                                                                                        Intent
                                                                                                .ACTION_VIEW,
                                                                                        Uri.parse(
                                                                                                finalUrl));
                                                                        intent.addFlags(
                                                                                Intent
                                                                                        .FLAG_ACTIVITY_NEW_TASK);
                                                                        ctx.startActivity(intent);
                                                                    }
                                                                });
                                            } else {
                                                new Handler(Looper.getMainLooper())
                                                        .post(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Hook
                                                                                .jiguroMessageWithContext(
                                                                                        ctx,
                                                                                        LocalizedStringProvider
                                                                                                .getInstance()
                                                                                                .get(
                                                                                                        ctx,
                                                                                                        "user_agreement_load_failed"));
                                                                    }
                                                                });
                                            }
                                        } else {
                                            new Handler(Looper.getMainLooper())
                                                    .post(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Hook.jiguroMessageWithContext(
                                                                            ctx,
                                                                            LocalizedStringProvider
                                                                                    .getInstance()
                                                                                    .get(
                                                                                            ctx,
                                                                                            "user_agreement_load_failed"));
                                                                }
                                                            });
                                        }
                                    } finally {
                                        if (inputStream != null) inputStream.close();
                                        if (connection != null) connection.disconnect();
                                    }
                                } catch (Exception e) {
                                    Hook.bvLog("[BetterVia] 加载用户协议URL失败: " + e);
                                    new Handler(Looper.getMainLooper())
                                            .post(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Hook.jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "user_agreement_load_failed"));
                                                        }
                                                    });
                                }
                            }
                        })
                .start();
    }

    private void loadUserAgreementUrlWithLanguage(final Context ctx, final String language) {
        Hook.jiguroMessageWithContext(
                ctx, LocalizedStringProvider.getInstance().get(ctx, "user_agreement_loading"));

        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String networkSource =
                                            Hook.getPrefString(
                                                    ctx,
                                                    Hook.KEY_NETWORK_SOURCE,
                                                    Hook.DEFAULT_NETWORK_SOURCE);
                                    String jsonUrl =
                                            Hook.NETWORK_SOURCE_VERCEL.equals(networkSource)
                                                    ? "https://raw.196104.xyz/useragreement.json"
                                                    : "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/useragreement.json";

                                    HttpURLConnection connection = null;
                                    InputStream inputStream = null;
                                    try {
                                        URL url = new URL(jsonUrl);
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setConnectTimeout(10000);
                                        connection.setReadTimeout(10000);

                                        if (connection.getResponseCode()
                                                == HttpURLConnection.HTTP_OK) {
                                            inputStream = connection.getInputStream();
                                            StringBuilder jsonBuilder = new StringBuilder();
                                            byte[] buffer = new byte[4096];
                                            int bytesRead;
                                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                jsonBuilder.append(
                                                        new String(buffer, 0, bytesRead, "UTF-8"));
                                            }

                                            String jsonContent = jsonBuilder.toString();
                                            JSONObject jsonObject = new JSONObject(jsonContent);
                                            JSONObject userAgreementUrls =
                                                    jsonObject.getJSONObject("user_agreement_urls");

                                            String userLang = language;

                                            String agreementUrl = null;
                                            if (userAgreementUrls.has(userLang)) {
                                                JSONObject langObj =
                                                        userAgreementUrls.getJSONObject(userLang);
                                                agreementUrl = langObj.getString("url");
                                            }

                                            if (agreementUrl != null && !agreementUrl.isEmpty()) {
                                                final String finalUrl = agreementUrl;
                                                new Handler(Looper.getMainLooper())
                                                        .post(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Intent intent =
                                                                                new Intent(
                                                                                        Intent
                                                                                                .ACTION_VIEW,
                                                                                        Uri.parse(
                                                                                                finalUrl));
                                                                        intent.addFlags(
                                                                                Intent
                                                                                        .FLAG_ACTIVITY_NEW_TASK);
                                                                        ctx.startActivity(intent);
                                                                    }
                                                                });
                                            } else {
                                                new Handler(Looper.getMainLooper())
                                                        .post(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Hook
                                                                                .jiguroMessageWithContext(
                                                                                        ctx,
                                                                                        LocalizedStringProvider
                                                                                                .getInstance()
                                                                                                .get(
                                                                                                        ctx,
                                                                                                        "user_agreement_load_failed"));
                                                                    }
                                                                });
                                            }
                                        } else {
                                            new Handler(Looper.getMainLooper())
                                                    .post(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Hook.jiguroMessageWithContext(
                                                                            ctx,
                                                                            LocalizedStringProvider
                                                                                    .getInstance()
                                                                                    .get(
                                                                                            ctx,
                                                                                            "user_agreement_load_failed"));
                                                                }
                                                            });
                                        }
                                    } finally {
                                        if (inputStream != null) inputStream.close();
                                        if (connection != null) connection.disconnect();
                                    }
                                } catch (Exception e) {
                                    Hook.bvLog("[BetterVia] 加载用户协议URL失败: " + e);
                                    new Handler(Looper.getMainLooper())
                                            .post(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Hook.jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "user_agreement_load_failed"));
                                                        }
                                                    });
                                }
                            }
                        })
                .start();
    }

    private void checkUserAgreement(final Context ctx) {
        boolean hasUserAgreement = Hook.getPrefBoolean(ctx, Hook.KEY_HAS_USER_AGREEMENT, false);
        if (!hasUserAgreement) {
            showUserAgreementDialog(ctx);
        } else {
            maybeShowRiskNotice(
                    ctx,
                    new Runnable() {
                        @Override
                        public void run() {
                            checkViaVersion(ctx);
                        }
                    });
        }
    }

    private void checkBasicSettings(final Context ctx) {
        boolean hasLanguageSelection =
                Hook.getPrefBoolean(ctx, Hook.KEY_HAS_LANGUAGE_SELECTION, false);
        String savedNetworkSource = Hook.getPrefString(ctx, Hook.KEY_NETWORK_SOURCE, "");
        boolean hasValidNetworkSource = Hook.isValidNetworkSource(savedNetworkSource);

        if (!hasLanguageSelection || !hasValidNetworkSource) {
            showBasicSettingsDialog(ctx);
        } else {
            checkUserAgreement(ctx);
        }
    }

    private void maybeShowRiskNotice(final Context ctx, final Runnable onContinue) {
        if (onContinue == null) return;
        if (ctx == null || Hook.getPrefBoolean(ctx, KEY_HAS_RISK_NOTICE, false)) {
            onContinue.run();
            return;
        }
        final boolean[] finished = new boolean[] {false};
        final Handler main = new Handler(Looper.getMainLooper());
        final Runnable fallback =
                new Runnable() {
                    @Override
                    public void run() {
                        if (finished[0]) return;
                        finished[0] = true;
                        Hook.bvLog("[BetterVia] 风险检测超时，跳过声明检查继续启动");
                        onContinue.run();
                    }
                };
        main.postDelayed(fallback, RISK_CHECK_TIMEOUT_MS);

        HostPatchDetector.detectAsync(
                ctx,
                new HostPatchDetector.Callback() {
                    @Override
                    public void onResult(boolean lspatch, String[] riskApks) {
                        if (finished[0]) return;
                        finished[0] = true;
                        main.removeCallbacks(fallback);

                        boolean risky = lspatch || (riskApks != null && riskApks.length > 0);
                        if (!risky || sRiskNoticeShown) {
                            onContinue.run();
                            return;
                        }
                        final Activity act = Hook.getActivityFrom(ctx);
                        if (act == null) {
                            onContinue.run();
                            return;
                        }
                        sRiskNoticeShown = true;
                        showRiskNotice(ctx, act, lspatch, onContinue);
                    }
                });
    }

    private void showRiskNotice(
            final Context ctx, final Activity act, boolean lspatch, final Runnable onContinue) {
        String contentKey =
                lspatch ? "risk_notice_content_builtin" : "risk_notice_content_modified";
        String content = LocalizedStringProvider.getInstance().get(act, contentKey);
        Hook.hasShownStartupDialog = true;

        SettingsUI.showMessageDialog(
                act,
                "risk_notice_title",
                content,
                "risk_notice_checkbox",
                false,
                "user_agreement_agree",
                "user_agreement_reject",
                new Runnable() {
                    @Override
                    public void run() {
                        Hook.putPrefBoolean(ctx, KEY_HAS_RISK_NOTICE, true);
                        onContinue.run();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Hook.jiguroMessageWithContext(
                                ctx,
                                LocalizedStringProvider.getInstance()
                                        .get(act, "user_agreement_reject_toast"));
                    }
                },
                null,
                true,
                false);
    }
}
