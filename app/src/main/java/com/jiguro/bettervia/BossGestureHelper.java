package com.jiguro.bettervia;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.hardware.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import java.util.List;

public class BossGestureHelper {

    private enum GestureState {
        IDLE,
        FACE_UP_MONITOR,
        FLIP_DETECTED,
        FACE_DOWN_VERIFY
    }

    private static final float FACE_UP_Z_THRESHOLD = 8.0f;
    private static final float FACE_DOWN_Z_THRESHOLD = -8.0f;
    private static final long FACE_UP_WINDOW_MS = 2000L;
    private static final long FLIP_TIMEOUT_MS = 3000L;
    private static final float STABILITY_VARIANCE = 0.8f;
    private static final long STABLE_DURATION_MS = 400L;

    private static final int SAMPLE_WINDOW_SIZE = 10;

    static final String KEY_BOSS_GESTURE = "boss_gesture";
    static final String KEY_BOSS_ACTION = "boss_action";
    static final String KEY_BOSS_ACTION_PARAM = "boss_action_param";

    static final String ACTION_KILL_PROCESS = "kill_process";
    static final String ACTION_GO_HOME = "go_home";
    static final String ACTION_OPEN_APP = "open_app";
    static final String ACTION_OPEN_URL = "open_url";
    static final String ACTION_KILL_AND_OPEN = "kill_and_open";

    private final Hook hookRef;
    private Context appContext;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private GestureSensorListener sensorListener;
    private boolean isMonitoring = false;
    private Application.ActivityLifecycleCallbacks lifecycleCallbacks;

    private GestureState state = GestureState.IDLE;
    private long lastFaceUpTime = 0L;
    private long flipStartTime = 0L;
    private long faceDownStableTime = 0L;

    private final float[] magWindow = new float[SAMPLE_WINDOW_SIZE];
    private int windowIdx = 0;
    private int windowCnt = 0;

    private volatile boolean appInForeground = true;

    public BossGestureHelper(Hook hook) {
        this.hookRef = hook;
    }

    public void startMonitoring(final Context ctx) {
        if (isMonitoring) {
            return;
        }
        try {
            if (ctx != null) {
                appContext = ctx.getApplicationContext();
                if (appContext == null) {
                    appContext = ctx;
                }
            }

            if (sensorManager == null && appContext != null) {
                sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
            }
            if (sensorManager == null) {
                return;
            }
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer == null) {
                return;
            }
            sensorListener = new GestureSensorListener();
            sensorManager.registerListener(
                    sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            isMonitoring = true;
            resetState();

            registerLifecycleCallbacks();
        } catch (Throwable t) {
        }
    }

    public void stopMonitoring() {
        if (!isMonitoring) {
            return;
        }
        try {
            if (sensorManager != null && sensorListener != null) {
                sensorManager.unregisterListener(sensorListener);
                sensorListener = null;
            }
        } catch (Throwable ignored) {
        }
        try {
            unregisterLifecycleCallbacks();
        } catch (Throwable ignored) {
        }
        isMonitoring = false;
        resetState();
    }

    public void destroy() {
        stopMonitoring();
        sensorManager = null;
        accelerometer = null;
        appContext = null;
    }

    private void registerLifecycleCallbacks() {
        try {
            if (appContext == null) {
                return;
            }
            Application app = null;
            if (appContext instanceof Application) {
                app = (Application) appContext;
            }

            if (app == null) {
                return;
            }

            lifecycleCallbacks =
                    new Application.ActivityLifecycleCallbacks() {
                        @Override
                        public void onActivityCreated(
                                Activity activity, Bundle savedInstanceState) {}

                        @Override
                        public void onActivityStarted(Activity activity) {}

                        @Override
                        public void onActivityResumed(Activity activity) {
                            appInForeground = true;
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {}

                        @Override
                        public void onActivityStopped(Activity activity) {
                            new Handler(Looper.getMainLooper())
                                    .postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {}
                                            },
                                            500L);
                        }

                        @Override
                        public void onActivitySaveInstanceState(
                                Activity activity, Bundle outState) {}

                        @Override
                        public void onActivityDestroyed(Activity activity) {}
                    };

            app.registerActivityLifecycleCallbacks(lifecycleCallbacks);
        } catch (Throwable ignored) {
        }
    }

    private void unregisterLifecycleCallbacks() {
        if (lifecycleCallbacks != null) {
            lifecycleCallbacks = null;
        }
    }

    private class GestureSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!isMonitoring || !appInForeground) {
                return;
            }

            final float x = event.values[0];
            final float y = event.values[1];
            final float z = event.values[2];

            final float mag = (float) Math.sqrt(x * x + y * y + z * z);
            synchronized (magWindow) {
                magWindow[windowIdx] = mag;
                windowIdx = (windowIdx + 1) % SAMPLE_WINDOW_SIZE;
                if (windowCnt < SAMPLE_WINDOW_SIZE) {
                    windowCnt++;
                }
            }

            final long now = SystemClock.elapsedRealtime();

            processStateMachine(z, now);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        private void processStateMachine(final float z, final long now) {
            switch (state) {
                case IDLE:
                    if (z > FACE_UP_Z_THRESHOLD) {
                        state = GestureState.FACE_UP_MONITOR;
                        lastFaceUpTime = now;
                    }
                    break;

                case FACE_UP_MONITOR:
                    if (z > FACE_UP_Z_THRESHOLD) {
                        lastFaceUpTime = now;
                    } else {
                        if (now - lastFaceUpTime <= FACE_UP_WINDOW_MS) {
                            state = GestureState.FLIP_DETECTED;
                            flipStartTime = now;
                        } else {
                            state = GestureState.IDLE;
                        }
                    }
                    break;

                case FLIP_DETECTED:
                    if (z < FACE_DOWN_Z_THRESHOLD) {
                        if (now - flipStartTime <= FLIP_TIMEOUT_MS) {
                            state = GestureState.FACE_DOWN_VERIFY;
                            faceDownStableTime = 0L;
                        } else {
                            state = GestureState.IDLE;
                        }
                    } else if (z > FACE_UP_Z_THRESHOLD) {
                        state = GestureState.FACE_UP_MONITOR;
                        lastFaceUpTime = now;
                    } else if (now - flipStartTime > FLIP_TIMEOUT_MS) {
                        state = GestureState.IDLE;
                    }
                    break;

                case FACE_DOWN_VERIFY:
                    if (z >= FACE_DOWN_Z_THRESHOLD) {
                        faceDownStableTime = 0L;
                        if (z > FACE_UP_Z_THRESHOLD) {
                            state = GestureState.FACE_UP_MONITOR;
                            lastFaceUpTime = now;
                        } else if (now - flipStartTime <= FLIP_TIMEOUT_MS) {
                            state = GestureState.FLIP_DETECTED;
                        } else {
                            state = GestureState.IDLE;
                        }
                        break;
                    }

                    float variance = computeMagnitudeVariance();
                    if (variance >= 0f && variance < STABILITY_VARIANCE) {
                        if (faceDownStableTime == 0L) {
                            faceDownStableTime = now;
                        }
                        if (now - faceDownStableTime > STABLE_DURATION_MS) {
                            state = GestureState.IDLE;
                            faceDownStableTime = 0L;
                            onGestureTriggered();
                        }
                    } else {
                        faceDownStableTime = 0L;
                    }
                    break;
            }
        }
    }

    private float computeMagnitudeVariance() {
        synchronized (magWindow) {
            if (windowCnt < SAMPLE_WINDOW_SIZE) {
                return -1f;
            }

            float sum = 0f;
            for (int i = 0; i < SAMPLE_WINDOW_SIZE; i++) {
                sum += magWindow[i];
            }
            final float mean = sum / SAMPLE_WINDOW_SIZE;

            float varSum = 0f;
            for (int i = 0; i < SAMPLE_WINDOW_SIZE; i++) {
                float diff = magWindow[i] - mean;
                varSum += diff * diff;
            }
            return varSum / SAMPLE_WINDOW_SIZE;
        }
    }

    private void resetState() {
        state = GestureState.IDLE;
        lastFaceUpTime = 0L;
        flipStartTime = 0L;
        faceDownStableTime = 0L;
        synchronized (magWindow) {
            windowIdx = 0;
            windowCnt = 0;
            for (int i = 0; i < SAMPLE_WINDOW_SIZE; i++) {
                magWindow[i] = 0f;
            }
        }
    }

    private void onGestureTriggered() {
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                executeAction();
                            }
                        });
    }

    private void executeAction() {
        try {
            Context ctx = appContext;
            if (ctx == null) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return;
            }

            String action = Hook.getPrefString(ctx, KEY_BOSS_ACTION, ACTION_GO_HOME);
            String param = Hook.getPrefString(ctx, KEY_BOSS_ACTION_PARAM, "");

            if (ACTION_KILL_PROCESS.equals(action)) {
                removeFromRecents(ctx);
                android.os.Process.killProcess(android.os.Process.myPid());
            } else if (ACTION_GO_HOME.equals(action)) {
                goToHome(ctx);
            } else if (ACTION_OPEN_APP.equals(action)) {
                openApp(ctx, param);
            } else if (ACTION_OPEN_URL.equals(action)) {
                openUrl(ctx, param);
            } else if (ACTION_KILL_AND_OPEN.equals(action)) {
                removeFromRecents(ctx);
                openApp(ctx, param);
                new Handler()
                        .postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                },
                                300L);
            } else {
                goToHome(ctx);
            }
        } catch (Throwable t) {
            try {
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Throwable ignored) {
            }
        }
    }

    private void goToHome(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    private void openApp(Context ctx, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            goToHome(ctx);
            return;
        }
        try {
            PackageManager pm = ctx.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName.trim());
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            } else {
                goToHome(ctx);
            }
        } catch (Throwable t) {
            goToHome(ctx);
        }
    }

    private void openUrl(Context ctx, String url) {
        if (url == null || url.trim().isEmpty()) {
            goToHome(ctx);
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url.trim()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Throwable t) {
            goToHome(ctx);
        }
    }

    private void removeFromRecents(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                return;
            }
            if (ctx instanceof Activity) {
                Activity act = (Activity) ctx;
                if (!act.isFinishing() && !act.isDestroyed()) {
                    act.finishAndRemoveTask();
                }
            }
            List<ActivityManager.AppTask> tasks = am.getAppTasks();
            if (tasks != null) {
                for (ActivityManager.AppTask task : tasks) {
                    task.finishAndRemoveTask();
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public void showDialog(final Context ctx) {
        final Activity act = hookRef.getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final String[] currentAction =
                new String[] {Hook.getPrefString(ctx, KEY_BOSS_ACTION, ACTION_GO_HOME)};
        final String[] currentParam =
                new String[] {Hook.getPrefString(ctx, KEY_BOSS_ACTION_PARAM, "")};
        final boolean[] currentEnable =
                new boolean[] {Hook.getPrefBoolean(ctx, KEY_BOSS_GESTURE, false)};

        final String[] actionValues =
                new String[] {
                    ACTION_GO_HOME,
                    ACTION_KILL_PROCESS,
                    ACTION_OPEN_APP,
                    ACTION_OPEN_URL,
                    ACTION_KILL_AND_OPEN
                };
        final String[] actionDisplayKeys =
                new String[] {
                    "boss_gesture_action_go_home",
                    "boss_gesture_action_kill_process",
                    "boss_gesture_action_open_app",
                    "boss_gesture_action_open_url",
                    "boss_gesture_action_kill_and_open"
                };

        final Runnable saveBoss =
                new Runnable() {
                    @Override
                    public void run() {
                        boolean enable = currentEnable[0];
                        String action = currentAction[0];
                        String param = currentParam[0].trim();

                        Hook.putPrefBoolean(ctx, KEY_BOSS_GESTURE, enable);
                        hookRef.putPrefString(ctx, KEY_BOSS_ACTION, action);
                        hookRef.putPrefString(ctx, KEY_BOSS_ACTION_PARAM, param);

                        if (enable) {
                            startMonitoring(ctx);
                        } else {
                            stopMonitoring();
                        }
                    }
                };

        SettingsUI.showPage(
                act,
                "boss_gesture_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final int[] enableRowRef = new int[1];
                        final int[] actionRowRef = new int[1];
                        final int[] paramRowRef = new int[1];
                        final Runnable[] refreshAdvancedRef = new Runnable[1];
                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        currentEnable[0] = isChecked;
                                        if (isChecked
                                                && needsParam(currentAction[0])
                                                && currentParam[0].trim().isEmpty()) {
                                            hookRef.jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "boss_gesture_param_required"));
                                            list.updateSwitch(enableRowRef[0], false);
                                            currentEnable[0] = false;
                                            refreshAdvancedRef[0].run();
                                            return;
                                        }
                                        refreshAdvancedRef[0].run();
                                        saveBoss.run();
                                    }
                                };
                        enableRowRef[0] = list.getItemCount();
                        list.addSwitchItem(
                                "boss_gesture_enable",
                                "boss_gesture_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "boss_gesture_notes_title",
                                                "boss_gesture_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("boss_gesture_notes_title", notesRunnable);

                        list.addSectionHeader("boss_gesture_advanced");

                        actionRowRef[0] = list.getItemCount();

                        final SettingsUI.OnSelectListener actionSelectListener =
                                new SettingsUI.OnSelectListener() {
                                    @Override
                                    public void onSelect(int index) {
                                        String newAction = actionValues[index];
                                        if (currentEnable[0]
                                                && needsParam(newAction)
                                                && currentParam[0].trim().isEmpty()) {
                                            hookRef.jiguroMessageWithContext(
                                                    ctx,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(
                                                                    ctx,
                                                                    "boss_gesture_param_required"));
                                            return;
                                        }
                                        currentAction[0] = newAction;
                                        list.updateItem(
                                                actionRowRef[0], actionDisplayKey(newAction), true);
                                        list.updateItemText(
                                                paramRowRef[0],
                                                buildParamDisplayText(
                                                        ctx, newAction, currentParam[0]),
                                                !currentEnable[0] && needsParam(newAction));
                                        saveBoss.run();
                                    }
                                };

                        final Runnable actionRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        int sel = 0;
                                        for (int i = 0; i < actionValues.length; i++) {
                                            if (actionValues[i].equals(currentAction[0])) {
                                                sel = i;
                                                break;
                                            }
                                        }
                                        SettingsUI.showSelectDialog(
                                                act,
                                                "boss_gesture_action_label",
                                                actionDisplayKeys,
                                                sel,
                                                actionSelectListener);
                                    }
                                };
                        list.addItemKeepRipple(
                                "boss_gesture_action_label",
                                actionDisplayKey(currentAction[0]),
                                actionRunnable);

                        final SettingsUI.OnInputListener paramInputListener =
                                new SettingsUI.OnInputListener() {
                                    @Override
                                    public void onConfirm(String input) {
                                        currentParam[0] = input.trim();
                                        list.updateItemText(
                                                paramRowRef[0],
                                                buildParamDisplayText(
                                                        ctx, currentAction[0], currentParam[0]),
                                                !currentEnable[0] && needsParam(currentAction[0]));
                                        saveBoss.run();
                                    }

                                    @Override
                                    public void onCancel() {}
                                };

                        paramRowRef[0] = list.getItemCount();
                        final Runnable paramRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showInputDialog(
                                                act,
                                                "boss_gesture_param_label",
                                                (String) null,
                                                paramHintKey(currentAction[0]),
                                                currentParam[0],
                                                1,
                                                "dialog_ok",
                                                "dialog_cancel",
                                                paramInputListener);
                                    }
                                };
                        list.addItemKeepRipple(
                                "boss_gesture_param_label",
                                paramHintKey(currentAction[0]),
                                paramRunnable);
                        refreshAdvancedRef[0] =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        list.updateItem(
                                                actionRowRef[0],
                                                actionDisplayKey(currentAction[0]),
                                                !currentEnable[0]);
                                        list.updateItemText(
                                                paramRowRef[0],
                                                buildParamDisplayText(
                                                        ctx, currentAction[0], currentParam[0]),
                                                !currentEnable[0] && needsParam(currentAction[0]));
                                    }
                                };
                        refreshAdvancedRef[0].run();

                        content.addView(list);
                    }
                });
    }

    private static boolean needsParam(String action) {
        return ACTION_OPEN_APP.equals(action)
                || ACTION_KILL_AND_OPEN.equals(action)
                || ACTION_OPEN_URL.equals(action);
    }

    private static String paramHintKey(String action) {
        if (ACTION_OPEN_APP.equals(action) || ACTION_KILL_AND_OPEN.equals(action)) {
            return "boss_gesture_param_hint_package";
        }
        if (ACTION_OPEN_URL.equals(action)) {
            return "boss_gesture_param_hint_url";
        }
        return "boss_gesture_param_hint_none";
    }

    private static String actionDisplayKey(String action) {
        if (ACTION_KILL_PROCESS.equals(action)) {
            return "boss_gesture_action_kill_process";
        }
        if (ACTION_OPEN_APP.equals(action)) {
            return "boss_gesture_action_open_app";
        }
        if (ACTION_OPEN_URL.equals(action)) {
            return "boss_gesture_action_open_url";
        }
        if (ACTION_KILL_AND_OPEN.equals(action)) {
            return "boss_gesture_action_kill_and_open";
        }
        return "boss_gesture_action_go_home";
    }

    private static CharSequence buildParamDisplayText(Context ctx, String action, String param) {
        if (param != null && !param.trim().isEmpty()) {
            return param.trim();
        }
        return LocalizedStringProvider.getInstance().get(ctx, paramHintKey(action));
    }
}
