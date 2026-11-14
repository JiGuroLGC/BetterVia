package com.jiguro.bettervia;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import de.robv.android.xposed.*;

import java.io.*;
import java.util.*;

public final class PrivacyLockManager {

    private static final String KEY_PRIVACY_LOCK_ENABLE = "privacy_lock_enable";
    private static final String KEY_PRIVACY_LOCK_APPLY_STARTUP = "privacy_lock_apply_startup";
    private static final String KEY_PRIVACY_LOCK_APPLY_HISTORY = "privacy_lock_apply_history";
    private static final String KEY_PRIVACY_LOCK_APPLY_BOOKMARKS = "privacy_lock_apply_bookmarks";
    private static final String KEY_PRIVACY_LOCK_APPLY_OFFLINE = "privacy_lock_apply_offline";
    private static final String KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE =
            "privacy_lock_apply_comprehensive";
    private static final String KEY_PRIVACY_LOCK_PASSWORD_TYPE = "privacy_lock_password_type";
    private static final String KEY_PRIVACY_LOCK_PASSWORD_SET = "privacy_lock_password_set";

    private static XC_MethodHook.Unhook privacyLockStartupHook = null;
    private static volatile boolean privacyLockVerified = false;
    private static volatile boolean privacyLockPageUsed = false;
    private static volatile View privacyLockOverlayView = null;
    private static volatile boolean privacyLockHookRegistered = false;

    private static final List<XC_MethodHook.Unhook> privacyLockPageHooks =
            new ArrayList<XC_MethodHook.Unhook>();

    private static volatile boolean startupInterceptActive = false;
    private static volatile boolean pageAccessInterceptActive = false;

    private static PasswordManager patternPasswordManager = null;

    static void setPrivacyLockStartupHook(final Context ctx, final ClassLoader cl) {
        try {
            startupInterceptActive = false;
            final boolean privacyLockEnabled =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
            final boolean applyStartup =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, false);

            if (!privacyLockEnabled || !applyStartup) {
                Hook.bvLog("[BetterVia] 隐私锁启动拦截未启用");
                if (privacyLockStartupHook != null) {
                    privacyLockStartupHook.unhook();
                    privacyLockStartupHook = null;
                    privacyLockHookRegistered = false;
                }
                return;
            }

            SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
            boolean passwordSet =
                    secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
            if (!passwordSet) {
                Hook.bvLog("[BetterVia] 隐私锁未设置密码，跳过启动拦截");
                return;
            }

            privacyLockVerified = false;
            privacyLockOverlayView = null;

            Hook.bvLog("[BetterVia] 开始设置隐私锁启动拦截（全屏覆盖层方案）...");

            if (privacyLockStartupHook != null && privacyLockHookRegistered) {
                privacyLockStartupHook.unhook();
                privacyLockStartupHook = null;
                Hook.bvLog("[BetterVia] 移除旧的隐私锁启动拦截Hook");
            }

            privacyLockStartupHook =
                    XposedHelpers.findAndHookMethod(
                            Activity.class,
                            "onCreate",
                            Bundle.class,
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    if (!startupInterceptActive) return;
                                    final Activity activity = (Activity) param.thisObject;

                                    String activityName = activity.getClass().getName();

                                    boolean isTargetActivity = false;
                                    if (activityName.equals("mark.via.Shell")) {
                                        isTargetActivity = true;
                                    } else if (activityName.contains(".Shell")) {
                                        if (activityName.startsWith(
                                                Hook.currentPackageName + ".Shell")) {
                                            isTargetActivity = true;
                                        }
                                    }

                                    if (!isTargetActivity) {
                                        return;
                                    }

                                    Hook.bvLogVerbose(
                                            "[BetterVia] 检测到 "
                                                    + activityName
                                                    + " Activity启动，重置验证状态");
                                    privacyLockVerified = false;
                                    privacyLockOverlayView = null;

                                    if (privacyLockVerified) {
                                        return;
                                    }

                                    Hook.bvLog("[BetterVia] 执行启动拦截");

                                    new Handler()
                                            .post(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (activity.isFinishing()
                                                                    || activity.isDestroyed()) {
                                                                return;
                                                            }

                                                            activity.runOnUiThread(
                                                                    new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (activity
                                                                                            .isFinishing()
                                                                                    || activity
                                                                                            .isDestroyed()) {
                                                                                return;
                                                                            }

                                                                            if (!privacyLockVerified) {
                                                                                Hook.bvLog(
                                                                                        "[BetterVia]"
                                                                                            + " 准备显示隐私锁覆盖层");
                                                                                showPrivacyLockStartupOverlay(
                                                                                        activity,
                                                                                        activity);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                }
                            });

            privacyLockHookRegistered = true;
            startupInterceptActive = true;
            Hook.bvLog("[BetterVia] ✓ 隐私锁启动拦截已启用（全屏覆盖层方案）");

        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 设置隐私锁启动拦截失败: " + t);
        }
    }

    static void showPrivacyLockStartupOverlay(final Activity activity, final Context ctx) {
        try {
            if (privacyLockOverlayView != null) {
                return;
            }

            final boolean startupImageEnabled = StartupExecutionHelper.getStartupImageEnable(ctx);

            Hook.bvLog("[BetterVia] 创建隐私锁启动覆盖层，启动图启用: " + startupImageEnabled);

            int overlayColor = Hook.getBgColorStatic(activity);

            final FrameLayout overlay = new FrameLayout(activity);
            overlay.setBackgroundColor(overlayColor);
            overlay.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            overlay.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });

            try {
                activity.getWindow()
                        .addContentView(
                                overlay,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                privacyLockOverlayView = overlay;
                Hook.bvLog("[BetterVia] 隐私锁启动覆盖层已添加到DecorView");
            } catch (Exception e) {
                Hook.bvLog("[BetterVia] 添加覆盖层失败: " + e);
                privacyLockVerified = true;
                return;
            }

            activity.getWindow()
                    .getDecorView()
                    .setOnKeyListener(
                            new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK
                                            && event.getAction() == KeyEvent.ACTION_UP) {
                                        Hook.bvLog("[BetterVia] 用户按返回键，关闭Via应用");
                                        activity.finish();
                                        System.exit(0);
                                        return true;
                                    }
                                    return false;
                                }
                            });

            SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
            int passwordType = -1;

            String patternPassword =
                    secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
            if (patternPassword != null && !patternPassword.isEmpty()) {
                passwordType = PasswordManager.PASSWORD_TYPE_PATTERN;
            }

            String pinPassword =
                    secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
            if (pinPassword != null && !pinPassword.isEmpty()) {
                passwordType = PasswordManager.PASSWORD_TYPE_PIN;
            }

            if (passwordType == -1) {
                Hook.bvLog("[BetterVia] 未找到密码，移除覆盖层");
                privacyLockVerified = true;
                removePrivacyLockOverlay(activity);
                return;
            }

            Hook.bvLog(
                    "[BetterVia] 准备显示验证对话框，密码类型: "
                            + (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN
                                    ? "图案"
                                    : "数字"));

            if (startupImageEnabled) {
                Hook.bvLog("[BetterVia] 启动图已启用，在隐私锁覆盖层上显示启动图");
                final int finalPasswordType = passwordType;
                showStartupImageOnOverlay(
                        activity,
                        overlay,
                        new Runnable() {
                            @Override
                            public void run() {
                                Hook.bvLog("[BetterVia] 启动图显示结束，显示密码验证对话框");
                                showPasswordVerifyDialog(activity, ctx, finalPasswordType);
                            }
                        });
            } else {
                Hook.bvLog("[BetterVia] 启动图未启用，先执行音乐和文字，然后显示密码验证对话框");
                final int finalPasswordType = passwordType;
                StartupExecutionHelper.executeStartupWithoutImage(
                        activity,
                        new Runnable() {
                            @Override
                            public void run() {
                                Hook.bvLog("[BetterVia] 启动音乐和文字执行完成，显示密码验证对话框");
                                showPasswordVerifyDialog(activity, ctx, finalPasswordType);
                            }
                        });
            }

            Hook.bvLog("[BetterVia] 隐私锁启动覆盖层已显示");

        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 显示隐私锁启动覆盖层失败: " + t);
            privacyLockVerified = true;
            removePrivacyLockOverlay(activity);
        }
    }

    static void showStartupImageOnOverlay(
            final Activity activity, final FrameLayout overlay, final Runnable onComplete) {
        try {
            final String imagePath = StartupExecutionHelper.getStartupImagePath(activity);
            if (imagePath == null || imagePath.isEmpty()) {
                Hook.bvLog("[BetterVia] 启动图路径为空，直接执行完成回调");
                if (onComplete != null) onComplete.run();
                return;
            }

            Hook.bvLog("[BetterVia] 在覆盖层上显示启动图: " + imagePath);

            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (activity.isFinishing() || activity.isDestroyed()) return;

                            final ImageView imageView = new ImageView(activity);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            imageView.setBackgroundColor(Color.BLACK);
                            try {
                                File imageFile = new File(imagePath);
                                if (!imageFile.exists()) {
                                    Hook.bvLog("[BetterVia] 启动图文件不存在: " + imagePath);
                                    if (onComplete != null) onComplete.run();
                                    return;
                                }

                                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                                if (bitmap != null) {
                                    imageView.setImageBitmap(bitmap);
                                    Hook.bvLog("[BetterVia] 启动图加载成功: " + imagePath);
                                } else {
                                    Hook.bvLog("[BetterVia] 启动图解码失败: " + imagePath);
                                    if (onComplete != null) onComplete.run();
                                    return;
                                }
                            } catch (Exception e) {
                                Hook.bvLog("[BetterVia] 加载启动图失败: " + e);
                                if (onComplete != null) onComplete.run();
                                return;
                            }

                            FrameLayout.LayoutParams imgParams =
                                    new FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT);
                            imageView.setLayoutParams(imgParams);

                            overlay.addView(imageView, imgParams);

                            AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setDuration(300);
                            imageView.startAnimation(fadeIn);

                            Hook.bvLog("[BetterVia] 启动图已添加到覆盖层，同时执行启动音乐和文字");

                            StartupExecutionHelper.executeStartupWithoutImage(
                                    activity,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Hook.bvLog("[BetterVia] 启动音乐和文字执行完成");
                                        }
                                    });

                            int duration =
                                    StartupExecutionHelper.getStartupImageDuration(activity) * 1000;
                            imageView.postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                            fadeOut.setDuration(300);
                                            imageView.startAnimation(fadeOut);

                                            imageView.postDelayed(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            overlay.removeView(imageView);
                                                            Hook.bvLog("[BetterVia] 启动图已从覆盖层移除");
                                                            if (onComplete != null)
                                                                onComplete.run();
                                                        }
                                                    },
                                                    300);
                                        }
                                    },
                                    duration);
                        }
                    });
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 显示启动图失败: " + t);
            if (onComplete != null) onComplete.run();
        }
    }

    static void showPasswordVerifyDialog(
            final Activity activity, final Context ctx, final int passwordType) {
        try {
            Hook.bvLog("[BetterVia] 开始创建PasswordManager，使用activity context");

            final PasswordManager passwordManager = new PasswordManager(activity);
            passwordManager.setVerifyMode(true);

            passwordManager.setListener(
                    new PasswordManager.PasswordListener() {
                        @Override
                        public void onPasswordSet() {}

                        @Override
                        public void onPasswordReset() {}

                        @Override
                        public void onVerifySuccess() {
                            Hook.bvLog("[BetterVia] 密码验证成功");

                            privacyLockVerified = true;
                            removePrivacyLockOverlay(activity);

                            Hook.bvLog("[BetterVia] 密码验证成功，启动流程已完成");
                            Hook.showAnnouncementIfReady(ctx);
                        }

                        @Override
                        public void onCancelled() {
                            Hook.bvLog("[BetterVia] 用户取消验证，关闭Via应用");
                            activity.finish();
                            System.exit(0);
                        }
                    });

            if (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
                Hook.bvLog("[BetterVia] 调用showVerifyPasswordDialog");
                passwordManager.showVerifyPasswordDialog(
                        LocalizedStringProvider.getInstance()
                                .get(ctx, "privacy_lock_startup_verify_hint"));
            } else {
                Hook.bvLog("[BetterVia] 调用showVerifyPinPasswordDialog");
                passwordManager.showVerifyPinPasswordDialog(
                        LocalizedStringProvider.getInstance()
                                .get(ctx, "privacy_lock_startup_verify_hint"));
            }

            Hook.bvLog("[BetterVia] 验证对话框显示方法已调用");

        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 显示验证对话框失败: " + t);
            t.printStackTrace();
            privacyLockVerified = true;
            removePrivacyLockOverlay(activity);
        }
    }

    static void removePrivacyLockOverlay(final Activity activity) {
        try {
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                activity.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (privacyLockOverlayView != null) {
                                    ViewParent parent = privacyLockOverlayView.getParent();
                                    if (parent instanceof ViewGroup) {
                                        ((ViewGroup) parent).removeView(privacyLockOverlayView);
                                    }
                                    privacyLockOverlayView = null;
                                    Hook.bvLog("[BetterVia] 隐私锁启动覆盖层已移除");
                                }
                            }
                        });
            }
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 移除隐私锁启动覆盖层失败: " + t);
        }
    }

    static boolean verifyPatternPassword(
            Context ctx, List<Integer> inputPattern, String storedHash) {
        try {
            String patternStr = patternListToString(inputPattern);
            SecurePasswordHelper helper = new SecurePasswordHelper(ctx);
            return helper.verify(patternStr, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean verifyPinPassword(Context ctx, String inputPassword, String storedHash) {
        try {
            SecurePasswordHelper helper = new SecurePasswordHelper(ctx);
            return helper.verify(inputPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    static void setPrivacyLockPageAccessHook(final Context ctx, final ClassLoader cl) {
        unhookPageAccessHooks();
        try {
            final boolean privacyLockEnabled =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
            final boolean applyHistory =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, false);
            final boolean applyBookmarks =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, false);
            final boolean applyOffline =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, false);
            final boolean applyComprehensive =
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, false);

            if (!privacyLockEnabled
                    || (!applyHistory && !applyBookmarks && !applyOffline && !applyComprehensive)) {
                Hook.bvLog("[BetterVia] 隐私锁页面访问拦截未启用");
                return;
            }

            SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
            boolean passwordSet =
                    secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
            if (!passwordSet) {
                Hook.bvLog("[BetterVia] 隐私锁未设置密码，跳过页面访问拦截");
                return;
            }

            Hook.bvLog("[BetterVia] 开始设置隐私锁页面访问拦截...");

            try {
                String privacyLockVerify1Class =
                        ViaClassMapping.getClassName(
                                ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_1, ctx);
                String privacyLockVerify1Method =
                        ViaClassMapping.getMethodName(
                                ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_1, ctx);

                privacyLockPageHooks.add(
                        XposedHelpers.findAndHookMethod(
                                privacyLockVerify1Class,
                                cl,
                                privacyLockVerify1Method,
                                int.class,
                                String.class,
                                new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param)
                                            throws Throwable {
                                        if (!pageAccessInterceptActive) return;
                                        String result = (String) param.getResult();

                                        if (privacyLockPageUsed) {
                                            return;
                                        }

                                        if (result == null || result.isEmpty()) {
                                            return;
                                        }

                                        if (applyHistory && result.contains("history.html")) {
                                            Hook.bvLog(
                                                    "[BetterVia] [B方法]"
                                                        + " 检测到访问历史记录页面(history.html)，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }

                                        if (applyBookmarks && result.contains("bookmarks.html")) {
                                            Hook.bvLog(
                                                    "[BetterVia] [B方法]"
                                                        + " 检测到访问书签页面(bookmarks.html)，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }
                                    }
                                }));
                Hook.bvLog("[BetterVia] ✓ B方法Hook已注册");
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] Hook B方法失败: " + t);
            }

            try {
                String privacyLockVerify2Class =
                        ViaClassMapping.getClassName(
                                ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_2, ctx);
                String privacyLockVerify2Method =
                        ViaClassMapping.getMethodName(
                                ViaClassMapping.ClassMethodKey.PRIVACY_LOCK_VERIFY_2, ctx);

                privacyLockPageHooks.add(
                        XposedHelpers.findAndHookMethod(
                                privacyLockVerify2Class,
                                cl,
                                privacyLockVerify2Method,
                                int.class,
                                new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param)
                                            throws Throwable {
                                        if (!pageAccessInterceptActive) return;
                                        int pageType = (Integer) param.args[0];

                                        if (privacyLockPageUsed) {
                                            return;
                                        }

                                        if (applyComprehensive
                                                && (pageType == 2
                                                        || pageType == 3
                                                        || pageType == 13)) {
                                            Hook.bvLog(
                                                    "[BetterVia] [f方法] 检测到访问综合界面(参数="
                                                            + pageType
                                                            + ")，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }

                                        if (applyHistory && pageType == 3) {
                                            Hook.bvLog("[BetterVia] [f方法] 检测到访问历史记录页面(参数=3)，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }

                                        if (applyBookmarks && pageType == 2) {
                                            Hook.bvLog("[BetterVia] [f方法] 检测到访问书签页面(参数=2)，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }

                                        if (applyOffline && pageType == 13) {
                                            Hook.bvLog("[BetterVia] [f方法] 检测到访问离线页面(参数=13)，执行拦截");
                                            showPrivacyLockPageVerifyOverlay(ctx);
                                            return;
                                        }
                                    }
                                }));
                Hook.bvLog("[BetterVia] ✓ f方法Hook已注册");
            } catch (Throwable t) {
                Hook.bvLog("[BetterVia] Hook f方法失败: " + t);
            }

            pageAccessInterceptActive = !privacyLockPageHooks.isEmpty();
            Hook.bvLog("[BetterVia] ✓ 隐私锁页面访问拦截已启用");

        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 设置隐私锁页面访问拦截失败: " + t);
        }
    }

    private static void unhookPageAccessHooks() {
        pageAccessInterceptActive = false;
        for (XC_MethodHook.Unhook h : privacyLockPageHooks) {
            try {
                h.unhook();
            } catch (Throwable ignored) {
            }
        }
        privacyLockPageHooks.clear();
    }

    static void showPrivacyLockPageVerifyOverlay(final Context ctx) {
        final Activity activity = Hook.getActivityFrom(ctx);
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        Hook.bvLog("[BetterVia] 准备显示页面访问密码验证");

        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (activity.isFinishing() || activity.isDestroyed()) {
                                    return;
                                }

                                if (!privacyLockPageUsed) {
                                    showPrivacyLockPageVerifyOverlayInternal(activity, ctx);
                                }
                            }
                        });
    }

    static void showPrivacyLockPageVerifyOverlayInternal(
            final Activity activity, final Context ctx) {
        try {
            if (privacyLockOverlayView != null) {
                return;
            }

            Hook.bvLog("[BetterVia] 创建页面访问验证覆盖层");

            int overlayColor = Hook.getBgColorStatic(activity);

            final FrameLayout overlay = new FrameLayout(activity);
            overlay.setBackgroundColor(overlayColor);
            overlay.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            overlay.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });

            try {
                activity.getWindow()
                        .addContentView(
                                overlay,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                privacyLockOverlayView = overlay;
                Hook.bvLog("[BetterVia] 页面访问验证覆盖层已添加到DecorView");
            } catch (Exception e) {
                Hook.bvLog("[BetterVia] 添加覆盖层失败: " + e);
                privacyLockPageUsed = true;
                return;
            }

            SecurePasswordStorage secureStorage = new SecurePasswordStorage(ctx);
            int passwordType = -1;

            String patternPassword =
                    secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
            if (patternPassword != null && !patternPassword.isEmpty()) {
                passwordType = PasswordManager.PASSWORD_TYPE_PATTERN;
            }

            String pinPassword =
                    secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
            if (pinPassword != null && !pinPassword.isEmpty()) {
                passwordType = PasswordManager.PASSWORD_TYPE_PIN;
            }

            if (passwordType == -1) {
                Hook.bvLog("[BetterVia] 未找到密码，移除覆盖层");
                privacyLockPageUsed = true;
                removePrivacyLockOverlay(activity);
                return;
            }

            Hook.bvLog(
                    "[BetterVia] 准备显示验证对话框，密码类型: "
                            + (passwordType == PasswordManager.PASSWORD_TYPE_PATTERN
                                    ? "图案"
                                    : "数字"));

            final int finalPasswordType = passwordType;
            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (activity.isFinishing() || activity.isDestroyed()) {
                                        Hook.bvLog("[BetterVia] Activity已销毁，取消显示验证对话框");
                                        return;
                                    }

                                    Hook.bvLog(
                                            "[BetterVia] 开始创建PasswordManager，使用activity context");

                                    try {
                                        final PasswordManager passwordManager =
                                                new PasswordManager(activity);
                                        passwordManager.setVerifyMode(true);

                                        passwordManager.setListener(
                                                new PasswordManager.PasswordListener() {
                                                    @Override
                                                    public void onPasswordSet() {}

                                                    @Override
                                                    public void onPasswordReset() {}

                                                    @Override
                                                    public void onVerifySuccess() {
                                                        Hook.bvLog("[BetterVia] 页面访问密码验证成功");
                                                        privacyLockPageUsed = false;
                                                        removePrivacyLockOverlay(activity);
                                                    }

                                                    @Override
                                                    public void onCancelled() {
                                                        Hook.bvLog("[BetterVia] 用户取消验证，返回上页");
                                                        privacyLockPageUsed = false;
                                                        removePrivacyLockOverlay(activity);
                                                        activity.onBackPressed();
                                                    }
                                                });

                                        if (finalPasswordType
                                                == PasswordManager.PASSWORD_TYPE_PATTERN) {
                                            Hook.bvLog("[BetterVia] 调用showVerifyPasswordDialog");
                                            passwordManager.showVerifyPasswordDialog(
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "privacy_lock_startup_verify_hint"));
                                        } else {
                                            Hook.bvLog("[BetterVia] 调用showVerifyPinPasswordDialog");
                                            passwordManager.showVerifyPinPasswordDialog(
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "privacy_lock_startup_verify_hint"));
                                        }

                                        Hook.bvLog("[BetterVia] 验证对话框显示方法已调用");

                                    } catch (Throwable t) {
                                        Hook.bvLog("[BetterVia] 显示验证对话框失败: " + t);
                                        t.printStackTrace();
                                        privacyLockPageUsed = true;
                                        removePrivacyLockOverlay(activity);
                                    }
                                }
                            },
                            300);

            Hook.bvLog("[BetterVia] 页面访问验证覆盖层已显示，等待验证对话框");

        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 显示页面访问验证覆盖层失败: " + t);
            privacyLockPageUsed = true;
            removePrivacyLockOverlay(activity);
        }
    }

    static String patternListToString(List<Integer> pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.size(); i++) {
            sb.append(pattern.get(i));
            if (i < pattern.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    static void showPrivacyLockEnableWarningDialog(
            final Activity act,
            final Context ctx,
            final Runnable onSave,
            final Runnable onCancelRevert) {
        act.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (act.isFinishing() || act.isDestroyed()) return;

                        SettingsUI.showMessageDialog(
                                act,
                                "privacy_lock_enable_warning_title",
                                "privacy_lock_enable_warning_message",
                                "privacy_lock_enable_warning_checkbox",
                                false,
                                "dialog_ok",
                                "dialog_cancel",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onSave != null) {
                                            onSave.run();
                                        }
                                    }
                                },
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onCancelRevert != null) {
                                            onCancelRevert.run();
                                        }
                                    }
                                },
                                null,
                                true);
                    }
                });
    }

    static void showPrivacyLockDialog(final Context ctx) {
        final Activity act = Hook.getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final boolean[] currentEnable =
                new boolean[] {getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false)};
        final boolean[] applySel =
                new boolean[] {
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, false),
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, false),
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, false),
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, false),
                    getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, false)
                };
        final int[] passwordType =
                new int[] {getPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE, 0)};
        final boolean[] passwordSet =
                new boolean[] {
                    new SecurePasswordStorage(ctx)
                            .getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false)
                };

        final String[] APPLY_KEYS =
                new String[] {
                    "privacy_lock_apply_startup",
                    "privacy_lock_apply_history",
                    "privacy_lock_apply_bookmarks",
                    "privacy_lock_apply_offline",
                    "privacy_lock_apply_comprehensive"
                };
        final String[] PASSWORD_TYPE_KEYS =
                new String[] {
                    "privacy_lock_password_type_pattern", "privacy_lock_password_type_numeric"
                };

        final class VerifyPasswordExecutor {
            private Runnable actionAfterVerify;
            private Runnable cancelAction;

            public void setAction(Runnable action, Runnable cancel) {
                this.actionAfterVerify = action;
                this.cancelAction = cancel;
            }

            public void execute() {
                if (!currentEnable[0]) {
                    if (actionAfterVerify != null) {
                        actionAfterVerify.run();
                    }
                    return;
                }

                if (patternPasswordManager == null) {
                    patternPasswordManager = new PasswordManager(act);
                }
                patternPasswordManager.setVerifyMode(true);
                patternPasswordManager.setListener(
                        new PasswordManager.PasswordListener() {
                            @Override
                            public void onPasswordSet() {}

                            @Override
                            public void onPasswordReset() {
                                Hook.bvLog("[BetterVia] 修改配置验证成功");
                                if (actionAfterVerify != null) {
                                    actionAfterVerify.run();
                                }
                            }

                            @Override
                            public void onVerifySuccess() {}

                            @Override
                            public void onCancelled() {
                                Hook.bvLog("[BetterVia] 修改配置验证取消");
                                if (cancelAction != null) {
                                    cancelAction.run();
                                }
                            }
                        });

                int currentPasswordType = patternPasswordManager.detectCurrentPasswordType();
                if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PATTERN) {
                    patternPasswordManager.showVerifyPasswordDialog(
                            LocalizedStringProvider.getInstance()
                                    .get(ctx, "pattern_lock_verify_for_config"));
                } else if (currentPasswordType == PasswordManager.PASSWORD_TYPE_PIN) {
                    patternPasswordManager.showVerifyPinPasswordDialog(
                            LocalizedStringProvider.getInstance()
                                    .get(ctx, "pin_lock_verify_for_config"));
                }
            }
        }
        final VerifyPasswordExecutor verifyPasswordExecutor = new VerifyPasswordExecutor();

        final Runnable persistAll =
                new Runnable() {
                    @Override
                    public void run() {
                        if (currentEnable[0] && !passwordSet[0]) {
                            Hook.jiguroMessageWithContext(
                                    ctx,
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "privacy_lock_not_set_password"));
                            return;
                        }

                        putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, currentEnable[0]);
                        putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, applySel[0]);
                        putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_HISTORY, applySel[1]);
                        putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_BOOKMARKS, applySel[2]);
                        putPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_OFFLINE, applySel[3]);
                        putPrivacyLockBoolean(
                                ctx, KEY_PRIVACY_LOCK_APPLY_COMPREHENSIVE, applySel[4]);
                        putPrivacyLockInt(ctx, KEY_PRIVACY_LOCK_PASSWORD_TYPE, passwordType[0]);
                        refresh(ctx, ctx.getClassLoader());
                    }
                };

        SettingsUI.showPage(
                act,
                "privacy_lock_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final int enableRow = list.getItemCount();
                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            boolean pwSet =
                                                    new SecurePasswordStorage(ctx)
                                                            .getSecureBoolean(
                                                                    SecurePasswordStorage
                                                                            .KEY_PASSWORD_SET,
                                                                    false);
                                            if (!pwSet) {
                                                Hook.jiguroMessageWithContext(
                                                        ctx,
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "privacy_lock_not_set_password"));
                                                list.updateSwitch(enableRow, false);
                                                return;
                                            }
                                            showPrivacyLockEnableWarningDialog(
                                                    act,
                                                    ctx,
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            currentEnable[0] = true;
                                                            persistAll.run();
                                                        }
                                                    },
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            list.updateSwitch(enableRow, false);
                                                        }
                                                    });
                                        } else {
                                            if (patternPasswordManager == null) {
                                                patternPasswordManager = new PasswordManager(act);
                                            }
                                            patternPasswordManager.setVerifyMode(true);
                                            patternPasswordManager.setListener(
                                                    new PasswordManager.PasswordListener() {
                                                        @Override
                                                        public void onPasswordSet() {}

                                                        @Override
                                                        public void onPasswordReset() {
                                                            Hook.bvLog("[BetterVia] 关闭隐私锁验证成功");
                                                            currentEnable[0] = false;
                                                            putPrivacyLockBoolean(
                                                                    ctx,
                                                                    KEY_PRIVACY_LOCK_ENABLE,
                                                                    false);
                                                            refresh(ctx, ctx.getClassLoader());
                                                            Hook.jiguroMessageWithContext(
                                                                    ctx,
                                                                    LocalizedStringProvider
                                                                            .getInstance()
                                                                            .get(
                                                                                    ctx,
                                                                                    "privacy_lock_disabled"));
                                                        }

                                                        @Override
                                                        public void onVerifySuccess() {}

                                                        @Override
                                                        public void onCancelled() {
                                                            Hook.bvLog("[BetterVia] 关闭隐私锁验证取消");
                                                            list.updateSwitch(enableRow, true);
                                                        }
                                                    });
                                            int currentPasswordType =
                                                    patternPasswordManager
                                                            .detectCurrentPasswordType();
                                            if (currentPasswordType
                                                    == PasswordManager.PASSWORD_TYPE_PATTERN) {
                                                patternPasswordManager.showVerifyPasswordDialog(
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "pattern_lock_verify_for_disable"));
                                            } else if (currentPasswordType
                                                    == PasswordManager.PASSWORD_TYPE_PIN) {
                                                patternPasswordManager.showVerifyPinPasswordDialog(
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "pin_lock_verify_for_disable"));
                                            }
                                        }
                                    }
                                };
                        list.addSwitchItem(
                                "privacy_lock_enable",
                                "privacy_lock_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "privacy_lock_notes_title",
                                                "privacy_lock_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("privacy_lock_notes_title", notesRunnable);

                        list.addSectionHeader("privacy_lock_advanced");

                        final int applyRow = list.getItemCount();
                        final Runnable applyRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "privacy_lock_apply_scope",
                                                APPLY_KEYS,
                                                applySel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        final boolean[] newSel = checked;
                                                        verifyPasswordExecutor.setAction(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        System.arraycopy(
                                                                                newSel,
                                                                                0,
                                                                                applySel,
                                                                                0,
                                                                                applySel.length);
                                                                        if (applySel[1]
                                                                                || applySel[2]
                                                                                || applySel[3]) {
                                                                            applySel[4] = true;
                                                                        }
                                                                        list.updateItemText(
                                                                                applyRow,
                                                                                Hook
                                                                                        .buildUaSelectionSummary(
                                                                                                ctx,
                                                                                                APPLY_KEYS,
                                                                                                applySel),
                                                                                true);
                                                                        persistAll.run();
                                                                    }
                                                                },
                                                                null);
                                                        verifyPasswordExecutor.execute();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("privacy_lock_apply_scope", applyRunnable);
                        list.updateItemText(
                                applyRow,
                                Hook.buildUaSelectionSummary(ctx, APPLY_KEYS, applySel),
                                true);

                        final int typeRow = list.getItemCount();
                        final Runnable typeRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showSelectDialog(
                                                act,
                                                "privacy_lock_password_type",
                                                PASSWORD_TYPE_KEYS,
                                                passwordType[0],
                                                new SettingsUI.OnSelectListener() {
                                                    @Override
                                                    public void onSelect(int index) {
                                                        passwordType[0] = index;
                                                        list.updateItemText(
                                                                typeRow,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                PASSWORD_TYPE_KEYS[
                                                                                        index]),
                                                                true);
                                                        putPrivacyLockInt(
                                                                ctx,
                                                                KEY_PRIVACY_LOCK_PASSWORD_TYPE,
                                                                passwordType[0]);
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("privacy_lock_password_type", typeRunnable);
                        list.updateItemText(
                                typeRow,
                                LocalizedStringProvider.getInstance()
                                        .get(ctx, PASSWORD_TYPE_KEYS[passwordType[0]]),
                                true);

                        final int resetRow = list.getItemCount();
                        final Runnable resetRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        final int userSelectedPasswordType = passwordType[0];
                                        if (!passwordSet[0]) {
                                            if (patternPasswordManager == null) {
                                                patternPasswordManager = new PasswordManager(act);
                                            }
                                            patternPasswordManager.setPasswordType(
                                                    userSelectedPasswordType);
                                            patternPasswordManager.setTargetPasswordType(
                                                    userSelectedPasswordType);
                                            patternPasswordManager.setListener(
                                                    new PasswordManager.PasswordListener() {
                                                        @Override
                                                        public void onPasswordSet() {
                                                            if (userSelectedPasswordType == 0) {
                                                                Hook.jiguroMessageWithContext(
                                                                        ctx,
                                                                        LocalizedStringProvider
                                                                                .getInstance()
                                                                                .get(
                                                                                        ctx,
                                                                                        "pattern_lock_set_success"));
                                                            } else {
                                                                Hook.jiguroMessageWithContext(
                                                                        ctx,
                                                                        LocalizedStringProvider
                                                                                .getInstance()
                                                                                .get(
                                                                                        ctx,
                                                                                        "pin_lock_set_success"));
                                                            }
                                                            passwordSet[0] = true;
                                                            list.updateItemText(
                                                                    resetRow,
                                                                    buildPasswordStateText(
                                                                            ctx, true),
                                                                    true);
                                                        }

                                                        @Override
                                                        public void onPasswordReset() {}

                                                        @Override
                                                        public void onVerifySuccess() {}

                                                        @Override
                                                        public void onCancelled() {}
                                                    });
                                            if (userSelectedPasswordType == 0) {
                                                patternPasswordManager.showSetPasswordDialog();
                                            } else {
                                                patternPasswordManager.showSetPinPasswordDialog();
                                            }
                                        } else {
                                            if (patternPasswordManager == null) {
                                                patternPasswordManager = new PasswordManager(act);
                                            }
                                            patternPasswordManager.setClearingPassword(true);
                                            patternPasswordManager.setVerifyMode(true);
                                            patternPasswordManager.setPasswordType(
                                                    userSelectedPasswordType);
                                            patternPasswordManager.setTargetPasswordType(
                                                    userSelectedPasswordType);
                                            patternPasswordManager.setListener(
                                                    new PasswordManager.PasswordListener() {
                                                        @Override
                                                        public void onPasswordSet() {}

                                                        @Override
                                                        public void onPasswordReset() {
                                                            Hook.bvLog("[BetterVia] 重置密码验证成功");
                                                            if (patternPasswordManager == null) {
                                                                patternPasswordManager =
                                                                        new PasswordManager(act);
                                                            }
                                                            patternPasswordManager
                                                                    .setClearingPassword(false);
                                                            patternPasswordManager.setPasswordType(
                                                                    userSelectedPasswordType);
                                                            patternPasswordManager
                                                                    .setTargetPasswordType(
                                                                            userSelectedPasswordType);
                                                            patternPasswordManager.setListener(
                                                                    new PasswordManager
                                                                            .PasswordListener() {
                                                                        @Override
                                                                        public void
                                                                                onPasswordSet() {
                                                                            if (userSelectedPasswordType
                                                                                    == 0) {
                                                                                Hook
                                                                                        .jiguroMessageWithContext(
                                                                                                ctx,
                                                                                                LocalizedStringProvider
                                                                                                        .getInstance()
                                                                                                        .get(
                                                                                                                ctx,
                                                                                                                "pattern_lock_set_success"));
                                                                            } else {
                                                                                Hook
                                                                                        .jiguroMessageWithContext(
                                                                                                ctx,
                                                                                                LocalizedStringProvider
                                                                                                        .getInstance()
                                                                                                        .get(
                                                                                                                ctx,
                                                                                                                "pin_lock_set_success"));
                                                                            }
                                                                            passwordSet[0] = true;
                                                                            list.updateItemText(
                                                                                    resetRow,
                                                                                    buildPasswordStateText(
                                                                                            ctx,
                                                                                            true),
                                                                                    true);
                                                                        }

                                                                        @Override
                                                                        public void
                                                                                onPasswordReset() {}

                                                                        @Override
                                                                        public void
                                                                                onVerifySuccess() {}

                                                                        @Override
                                                                        public void onCancelled() {}
                                                                    });
                                                            if (userSelectedPasswordType == 0) {
                                                                patternPasswordManager
                                                                        .showSetPasswordDialog();
                                                            } else {
                                                                patternPasswordManager
                                                                        .showSetPinPasswordDialog();
                                                            }
                                                        }

                                                        @Override
                                                        public void onVerifySuccess() {}

                                                        @Override
                                                        public void onCancelled() {
                                                            Hook.bvLog("[BetterVia] 重置密码验证取消");
                                                        }
                                                    });
                                            int currentPasswordType =
                                                    patternPasswordManager
                                                            .detectCurrentPasswordType();
                                            if (currentPasswordType
                                                    == PasswordManager.PASSWORD_TYPE_PATTERN) {
                                                patternPasswordManager.showVerifyPasswordDialog(
                                                        LocalizedStringProvider.getInstance()
                                                                .get(
                                                                        ctx,
                                                                        "pattern_lock_verify_hint"));
                                            } else if (currentPasswordType
                                                    == PasswordManager.PASSWORD_TYPE_PIN) {
                                                patternPasswordManager.showVerifyPinPasswordDialog(
                                                        LocalizedStringProvider.getInstance()
                                                                .get(ctx, "pin_lock_verify_hint"));
                                            }
                                        }
                                    }
                                };
                        list.addItemKeepRipple("privacy_lock_reset_password", resetRunnable);
                        list.updateItemText(
                                resetRow, buildPasswordStateText(ctx, passwordSet[0]), true);

                        final Runnable clearRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!passwordSet[0]) {
                                            Hook.jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "privacy_lock_not_set_password"));
                                            return;
                                        }
                                        if (patternPasswordManager == null) {
                                            patternPasswordManager = new PasswordManager(act);
                                        }
                                        patternPasswordManager.setClearingPassword(true);
                                        patternPasswordManager.setListener(
                                                new PasswordManager.PasswordListener() {
                                                    @Override
                                                    public void onPasswordSet() {}

                                                    @Override
                                                    public void onPasswordReset() {
                                                        Hook.bvLog("[BetterVia] 清除密码验证成功");
                                                        if (patternPasswordManager == null) {
                                                            patternPasswordManager =
                                                                    new PasswordManager(act);
                                                        }
                                                        patternPasswordManager.clearPasswordData();
                                                        Hook.jiguroMessageWithContext(
                                                                ctx,
                                                                LocalizedStringProvider
                                                                        .getInstance()
                                                                        .get(
                                                                                ctx,
                                                                                "privacy_lock_password_cleared"));
                                                        passwordSet[0] = false;
                                                        list.updateItemText(
                                                                resetRow,
                                                                buildPasswordStateText(ctx, false),
                                                                true);
                                                        list.updateSwitch(enableRow, false);
                                                        currentEnable[0] = false;
                                                        putPrivacyLockBoolean(
                                                                ctx,
                                                                KEY_PRIVACY_LOCK_ENABLE,
                                                                false);
                                                        refresh(ctx, ctx.getClassLoader());
                                                    }

                                                    @Override
                                                    public void onVerifySuccess() {}

                                                    @Override
                                                    public void onCancelled() {
                                                        Hook.bvLog("[BetterVia] 清除密码验证取消");
                                                    }
                                                });
                                        int currentPasswordType =
                                                patternPasswordManager.detectCurrentPasswordType();
                                        if (currentPasswordType
                                                == PasswordManager.PASSWORD_TYPE_PATTERN) {
                                            patternPasswordManager.showVerifyPasswordDialog(
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "pattern_lock_verify_for_clear"));
                                        } else if (currentPasswordType
                                                == PasswordManager.PASSWORD_TYPE_PIN) {
                                            patternPasswordManager.showVerifyPinPasswordDialog(
                                                    LocalizedStringProvider.getInstance()
                                                            .get(ctx, "pin_lock_verify_for_clear"));
                                        }
                                    }
                                };
                        list.addItemKeepRipple("privacy_lock_clear_password", clearRunnable);

                        content.addView(list);
                    }
                });
    }

    static CharSequence buildPasswordStateText(Context ctx, boolean set) {
        return LocalizedStringProvider.getInstance()
                .get(ctx, set ? "privacy_lock_password_set" : "privacy_lock_password_not_set");
    }

    static boolean getPrivacyLockBoolean(Context ctx, String key, boolean def) {
        try {
            SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
            return storage.getSecureBoolean(key, def);
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 读取隐私锁配置失败: " + e);
            return def;
        }
    }

    static void putPrivacyLockBoolean(Context ctx, String key, boolean value) {
        try {
            SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
            storage.putSecureBoolean(key, value);
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 写入隐私锁配置失败: " + e);
        }
    }

    static int getPrivacyLockInt(Context ctx, String key, int def) {
        try {
            SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
            return storage.getSecureInt(key, def);
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 读取隐私锁配置失败: " + e);
            return def;
        }
    }

    static void putPrivacyLockInt(Context ctx, String key, int value) {
        try {
            SecurePasswordStorage storage = new SecurePasswordStorage(ctx);
            storage.putSecureInt(key, value);
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 写入隐私锁配置失败: " + e);
        }
    }

    static void install(Context ctx, ClassLoader cl) {
        setPrivacyLockStartupHook(ctx, cl);
        setPrivacyLockPageAccessHook(ctx, cl);
    }

    static void refresh(Context ctx, ClassLoader cl) {
        install(ctx, cl);
    }

    static boolean isEnabled(Context ctx) {
        return getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_ENABLE, false);
    }

    static boolean appliesToStartup(Context ctx) {
        return getPrivacyLockBoolean(ctx, KEY_PRIVACY_LOCK_APPLY_STARTUP, false);
    }

    static void onActivityDestroyed(Activity activity) {
        if (privacyLockOverlayView != null) {
            ViewParent parent = privacyLockOverlayView.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup parentGroup = (ViewGroup) parent;
                if (parentGroup == activity.getWindow().getDecorView()) {
                    Hook.bvLog("[BetterVia] 清理隐私锁覆盖层");
                    parentGroup.removeView(privacyLockOverlayView);
                    privacyLockOverlayView = null;
                }
            }
        }
        String activityName = activity.getClass().getName();
        if (activityName.equals("mark.via.Shell")
                || activityName.startsWith(Hook.currentPackageName + ".Shell")) {
            Hook.bvLog("[BetterVia] Shell Activity销毁，重置隐私锁验证状态");
            privacyLockVerified = false;
            privacyLockPageUsed = false;
        }
    }

    private PrivacyLockManager() {}
}
