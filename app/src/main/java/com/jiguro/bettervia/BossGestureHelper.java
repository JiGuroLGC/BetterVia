package com.jiguro.bettervia;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.ColorStateList;
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
import java.util.ArrayList;
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
			sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
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

			lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
				@Override
				public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				}

				@Override
				public void onActivityStarted(Activity activity) {
				}

				@Override
				public void onActivityResumed(Activity activity) {
					appInForeground = true;
				}

				@Override
				public void onActivityPaused(Activity activity) {
				
				}

				@Override
				public void onActivityStopped(Activity activity) {
				
					new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
						
						}
					}, 500L);
				}

				@Override
				public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
				}

				@Override
				public void onActivityDestroyed(Activity activity) {
				}
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
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		private void processStateMachine(final float z, final long now) {
			switch (state) {

				case IDLE :
					if (z > FACE_UP_Z_THRESHOLD) {
						state = GestureState.FACE_UP_MONITOR;
						lastFaceUpTime = now;
					}
					break;

				case FACE_UP_MONITOR :
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

				case FLIP_DETECTED :
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

				case FACE_DOWN_VERIFY :
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
	
		new Handler(Looper.getMainLooper()).post(new Runnable() {
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

			String action = Hook.getPrefStringStatic(ctx, KEY_BOSS_ACTION, ACTION_GO_HOME);
			String param = Hook.getPrefStringStatic(ctx, KEY_BOSS_ACTION_PARAM, "");

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
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}, 300L);
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

		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed()) {
					return;
				}

				final int bgColor = hookRef.getBgColor(ctx);
				final int titleColor = hookRef.getTitleColor(ctx);
				final int textColor = hookRef.getTextColor(ctx);
				final int hintColor = hookRef.getHintColor(ctx);
				final int okBtnBgColor = hookRef.getOkBtnBgColor(ctx);
				final int okBtnTextColor = hookRef.getOkBtnTextColor(ctx);
				final int itemBgColor = hookRef.getItemBgColor(ctx);
				final int dividerColor = hookRef.getDividerColor(ctx);
				final int switchOnColor = hookRef.getSwitchOnColor(ctx);
				final int switchOffColor = hookRef.getSwitchOffColor(ctx);
				final int editBgColor = hookRef.getEditBgColor(ctx);

				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);

				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBgDrawable = new GradientDrawable();
				containerBgDrawable.setColor(bgColor);
				containerBgDrawable.setCornerRadius(Hook.dp(act, 24));
				dialogContainer.setBackground(containerBgDrawable);

				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);

				final LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(Hook.dp(act, 24), Hook.dp(act, 40), Hook.dp(act, 24), Hook.dp(act, 24));

				TextView titleTv = new TextView(act);
				titleTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_dialog_title"));
				titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
				titleTv.setTextColor(titleColor);
				titleTv.setTypeface(null, Typeface.BOLD);
				titleTv.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.bottomMargin = Hook.dp(act, 6);
				root.addView(titleTv, titleLp);

				TextView subtitleTv = new TextView(act);
				subtitleTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_dialog_subtitle"));
				subtitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				subtitleTv.setTextColor(hintColor);
				subtitleTv.setGravity(Gravity.CENTER);
				subtitleTv.setPadding(0, 0, 0, Hook.dp(act, 36));
				root.addView(subtitleTv);

				TextView enableTitleTv = new TextView(act);
				enableTitleTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_enable"));
				enableTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				enableTitleTv.setTypeface(null, Typeface.BOLD);
				enableTitleTv.setTextColor(textColor);
				enableTitleTv.setGravity(Gravity.START);
				LinearLayout.LayoutParams enableTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				enableTitleLp.bottomMargin = Hook.dp(act, 8);
				root.addView(enableTitleTv, enableTitleLp);

				LinearLayout enableContainer = new LinearLayout(act);
				enableContainer.setOrientation(LinearLayout.VERTICAL);
				enableContainer.setPadding(Hook.dp(act, 12), Hook.dp(act, 12), Hook.dp(act, 12), Hook.dp(act, 12));
				GradientDrawable enableBg = new GradientDrawable();
				enableBg.setColor(itemBgColor);
				enableBg.setCornerRadius(Hook.dp(act, 8));
				enableBg.setStroke(Hook.dp(act, 1), dividerColor);
				enableContainer.setBackground(enableBg);

				final Switch bossSwitch = new Switch(act);
				bossSwitch.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_enable"));
				bossSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				bossSwitch.setTextColor(textColor);
				bossSwitch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
				bossSwitch.setChecked(Hook.getPrefBoolean(ctx, KEY_BOSS_GESTURE, false));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] colors = new int[]{switchOnColor, switchOffColor};
					ColorStateList colorStateList = new ColorStateList(states, colors);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						bossSwitch.setThumbTintList(colorStateList);
						bossSwitch.setTrackTintList(colorStateList);
					}
				}
				LinearLayout.LayoutParams switchLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				switchLp.bottomMargin = Hook.dp(act, 8);
				enableContainer.addView(bossSwitch, switchLp);

				TextView switchHintTv = new TextView(act);
				switchHintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_enable_hint"));
				switchHintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				switchHintTv.setTextColor(hintColor);
				switchHintTv.setGravity(Gravity.START);
				enableContainer.addView(switchHintTv);

				LinearLayout.LayoutParams enableContainerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				enableContainerLp.bottomMargin = Hook.dp(act, 16);
				root.addView(enableContainer, enableContainerLp);

				TextView notesTitleTv = new TextView(act);
				notesTitleTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_notes_title"));
				notesTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				notesTitleTv.setTypeface(null, Typeface.BOLD);
				notesTitleTv.setTextColor(textColor);
				notesTitleTv.setGravity(Gravity.START);
				LinearLayout.LayoutParams notesTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				notesTitleLp.bottomMargin = Hook.dp(act, 8);
				root.addView(notesTitleTv, notesTitleLp);

				LinearLayout notesContainer = new LinearLayout(act);
				notesContainer.setOrientation(LinearLayout.VERTICAL);
				notesContainer.setPadding(Hook.dp(act, 12), Hook.dp(act, 12), Hook.dp(act, 12), Hook.dp(act, 12));
				GradientDrawable notesBg = new GradientDrawable();
				notesBg.setColor(itemBgColor);
				notesBg.setCornerRadius(Hook.dp(act, 8));
				notesBg.setStroke(Hook.dp(act, 1), dividerColor);
				notesContainer.setBackground(notesBg);

				TextView notesContentTv = new TextView(act);
				notesContentTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_notes_content"));
				notesContentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				notesContentTv.setTextColor(textColor);
				notesContentTv.setLineSpacing(Hook.dp(act, 4), 1.2f);
				notesContainer.addView(notesContentTv);

				LinearLayout.LayoutParams notesContainerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				notesContainerLp.bottomMargin = Hook.dp(act, 16);
				root.addView(notesContainer, notesContainerLp);

				TextView configTitleTv = new TextView(act);
				configTitleTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_config_section"));
				configTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				configTitleTv.setTypeface(null, Typeface.BOLD);
				configTitleTv.setTextColor(textColor);
				configTitleTv.setGravity(Gravity.START);
				LinearLayout.LayoutParams configTitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				configTitleLp.bottomMargin = Hook.dp(act, 8);
				root.addView(configTitleTv, configTitleLp);

				LinearLayout configContainer = new LinearLayout(act);
				configContainer.setOrientation(LinearLayout.VERTICAL);
				configContainer.setPadding(Hook.dp(act, 16), Hook.dp(act, 12), Hook.dp(act, 16), Hook.dp(act, 12));
				GradientDrawable configBg = new GradientDrawable();
				configBg.setColor(itemBgColor);
				configBg.setCornerRadius(Hook.dp(act, 12));
				configBg.setStroke(Hook.dp(act, 1), dividerColor);
				configContainer.setBackground(configBg);

				TextView actionLabelTv = new TextView(act);
				actionLabelTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_label"));
				actionLabelTv.setTextColor(hintColor);
				actionLabelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				actionLabelTv.setTypeface(null, Typeface.BOLD);
				actionLabelTv.setPadding(0, 0, 0, Hook.dp(act, 8));
				configContainer.addView(actionLabelTv);

				final String savedAction = Hook.getPrefStringStatic(ctx, KEY_BOSS_ACTION, ACTION_GO_HOME);
				final List<CheckBox> actionCheckBoxes = new ArrayList<CheckBox>();
				final String[] currentAction = {savedAction != null ? savedAction : ACTION_GO_HOME};
			
				final EditText[] paramEditHolder = new EditText[1];

				final CheckBox cbHome = new CheckBox(act);
				cbHome.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_go_home"));
				cbHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cbHome.setTextColor(textColor);
				cbHome.setPadding(Hook.dp(act, 4), Hook.dp(act, 4), 0, Hook.dp(act, 4));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] st = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] cl = new int[]{switchOnColor, switchOffColor};
					cbHome.setButtonTintList(new ColorStateList(st, cl));
				}
				cbHome.setChecked(ACTION_GO_HOME.equals(savedAction) || savedAction == null || savedAction.isEmpty());
				configContainer.addView(cbHome);
				actionCheckBoxes.add(cbHome);

				final CheckBox cbKill = new CheckBox(act);
				cbKill.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_kill_process"));
				cbKill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cbKill.setTextColor(textColor);
				cbKill.setPadding(Hook.dp(act, 4), Hook.dp(act, 4), 0, Hook.dp(act, 4));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] st = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] cl = new int[]{switchOnColor, switchOffColor};
					cbKill.setButtonTintList(new ColorStateList(st, cl));
				}
				cbKill.setChecked(ACTION_KILL_PROCESS.equals(savedAction));
				configContainer.addView(cbKill);
				actionCheckBoxes.add(cbKill);

				final CheckBox cbApp = new CheckBox(act);
				cbApp.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_open_app"));
				cbApp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cbApp.setTextColor(textColor);
				cbApp.setPadding(Hook.dp(act, 4), Hook.dp(act, 4), 0, Hook.dp(act, 4));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] st = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] cl = new int[]{switchOnColor, switchOffColor};
					cbApp.setButtonTintList(new ColorStateList(st, cl));
				}
				cbApp.setChecked(ACTION_OPEN_APP.equals(savedAction));
				configContainer.addView(cbApp);
				actionCheckBoxes.add(cbApp);

				final CheckBox cbUrl = new CheckBox(act);
				cbUrl.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_open_url"));
				cbUrl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cbUrl.setTextColor(textColor);
				cbUrl.setPadding(Hook.dp(act, 4), Hook.dp(act, 4), 0, Hook.dp(act, 4));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] st = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] cl = new int[]{switchOnColor, switchOffColor};
					cbUrl.setButtonTintList(new ColorStateList(st, cl));
				}
				cbUrl.setChecked(ACTION_OPEN_URL.equals(savedAction));
				configContainer.addView(cbUrl);
				actionCheckBoxes.add(cbUrl);

				final CheckBox cbKillOpen = new CheckBox(act);
				cbKillOpen.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_action_kill_and_open"));
				cbKillOpen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cbKillOpen.setTextColor(textColor);
				cbKillOpen.setPadding(Hook.dp(act, 4), Hook.dp(act, 4), 0, Hook.dp(act, 4));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int[][] st = new int[][]{new int[]{android.R.attr.state_checked},
							new int[]{-android.R.attr.state_checked}};
					int[] cl = new int[]{switchOnColor, switchOffColor};
					cbKillOpen.setButtonTintList(new ColorStateList(st, cl));
				}
				cbKillOpen.setChecked(ACTION_KILL_AND_OPEN.equals(savedAction));
				configContainer.addView(cbKillOpen);
				actionCheckBoxes.add(cbKillOpen);

				CompoundButton.OnCheckedChangeListener mutualExclusiveListener = new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (!isChecked) {
							return;
						}
					
						for (CheckBox cb : actionCheckBoxes) {
							if (cb != buttonView) {
								cb.setOnCheckedChangeListener(null);
								cb.setChecked(false);
								cb.setOnCheckedChangeListener(this);
							}
						}
					
						if (buttonView == cbHome) {
							currentAction[0] = ACTION_GO_HOME;
							paramEditHolder[0].setHint(
									LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_none"));
							paramEditHolder[0].setEnabled(false);
							paramEditHolder[0].setAlpha(0.4f);
						} else if (buttonView == cbKill) {
							currentAction[0] = ACTION_KILL_PROCESS;
							paramEditHolder[0].setHint(
									LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_none"));
							paramEditHolder[0].setEnabled(false);
							paramEditHolder[0].setAlpha(0.4f);
						} else if (buttonView == cbApp) {
							currentAction[0] = ACTION_OPEN_APP;
							paramEditHolder[0].setHint(
									LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_package"));
							paramEditHolder[0].setEnabled(true);
							paramEditHolder[0].setAlpha(1.0f);
						} else if (buttonView == cbUrl) {
							currentAction[0] = ACTION_OPEN_URL;
							paramEditHolder[0].setHint(
									LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_url"));
							paramEditHolder[0].setEnabled(true);
							paramEditHolder[0].setAlpha(1.0f);
						} else if (buttonView == cbKillOpen) {
							currentAction[0] = ACTION_KILL_AND_OPEN;
							paramEditHolder[0].setHint(
									LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_package"));
							paramEditHolder[0].setEnabled(true);
							paramEditHolder[0].setAlpha(1.0f);
						}
					}
				};

				cbHome.setOnCheckedChangeListener(mutualExclusiveListener);
				cbKill.setOnCheckedChangeListener(mutualExclusiveListener);
				cbApp.setOnCheckedChangeListener(mutualExclusiveListener);
				cbUrl.setOnCheckedChangeListener(mutualExclusiveListener);
				cbKillOpen.setOnCheckedChangeListener(mutualExclusiveListener);

				final TextView paramLabelTv = new TextView(act);
				paramLabelTv.setText(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_label"));
				paramLabelTv.setTextColor(hintColor);
				paramLabelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				paramLabelTv.setPadding(0, Hook.dp(act, 8), 0, Hook.dp(act, 4));
				configContainer.addView(paramLabelTv);

				final EditText paramEdit = new EditText(act);
				paramEditHolder[0] = paramEdit;
				paramEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				paramEdit.setTextColor(textColor);
				paramEdit.setHintTextColor(hintColor);
				paramEdit.setPadding(Hook.dp(act, 8), Hook.dp(act, 10), Hook.dp(act, 8), Hook.dp(act, 10));
				GradientDrawable editBgDrawable = new GradientDrawable();
				editBgDrawable.setColor(editBgColor);
				editBgDrawable.setCornerRadius(Hook.dp(act, 6));
				editBgDrawable.setStroke(Hook.dp(act, 1), dividerColor);
				paramEdit.setBackground(editBgDrawable);
				String savedParam = Hook.getPrefStringStatic(ctx, KEY_BOSS_ACTION_PARAM, "");
				if (savedParam != null && !savedParam.isEmpty()) {
					paramEdit.setText(savedParam);
				}
				paramEdit.setSingleLine(true);
				LinearLayout.LayoutParams paramEditLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				paramEditLp.bottomMargin = Hook.dp(act, 8);
				configContainer.addView(paramEdit, paramEditLp);

				if (ACTION_GO_HOME.equals(savedAction) || ACTION_KILL_PROCESS.equals(savedAction) || savedAction == null
						|| savedAction.isEmpty()) {
					paramEditHolder[0]
							.setHint(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_none"));
					paramEditHolder[0].setEnabled(false);
					paramEditHolder[0].setAlpha(0.4f);
				} else if (ACTION_OPEN_APP.equals(savedAction) || ACTION_KILL_AND_OPEN.equals(savedAction)) {
					paramEditHolder[0]
							.setHint(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_package"));
					paramEditHolder[0].setEnabled(true);
					paramEditHolder[0].setAlpha(1.0f);
				} else if (ACTION_OPEN_URL.equals(savedAction)) {
					paramEditHolder[0]
							.setHint(LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_hint_url"));
					paramEditHolder[0].setEnabled(true);
					paramEditHolder[0].setAlpha(1.0f);
				}

				LinearLayout.LayoutParams configContainerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				configContainerLp.topMargin = Hook.dp(act, 8);
				root.addView(configContainer, configContainerLp);

				Button okBtn = new Button(act);
				hookRef.applyClickAnim(okBtn);
				okBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_ok"));
				okBtn.setTextColor(okBtnTextColor);
				okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				okBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable okBtnBg = new GradientDrawable();
				okBtnBg.setColor(okBtnBgColor);
				okBtnBg.setCornerRadius(Hook.dp(act, 12));
				okBtn.setBackground(okBtnBg);
				LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				okLp.topMargin = Hook.dp(act, 16);
				root.addView(okBtn, okLp);

				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);

				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					win.setGravity(Gravity.CENTER);
					DisplayMetrics dialogMetrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(dialogMetrics);
					int dialogWidth = (int) (dialogMetrics.widthPixels * 0.9);
					WindowManager.LayoutParams dialogLp = new WindowManager.LayoutParams();
					dialogLp.copyFrom(win.getAttributes());
					dialogLp.width = dialogWidth;
					dialogLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					dialogLp.gravity = Gravity.CENTER;
					win.setAttributes(dialogLp);
				}

				dialog.setContentView(dialogContainer);
				dialog.show();
				hookRef.animateDialogEntrance(root, act);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.9);
				dialog.getWindow().setAttributes(lp);

				okBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean enable = bossSwitch.isChecked();
						String action = currentAction[0];
						String param = paramEdit.getText().toString().trim();

						if (enable && (ACTION_OPEN_APP.equals(action) || ACTION_KILL_AND_OPEN.equals(action))) {
							if (param.isEmpty()) {
								hookRef.jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_required"));
								return;
							}
						}
						if (enable && ACTION_OPEN_URL.equals(action)) {
							if (param.isEmpty()) {
								hookRef.jiguroMessageWithContext(ctx,
										LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_param_required"));
								return;
							}
						}

						Hook.putPrefBoolean(ctx, KEY_BOSS_GESTURE, enable);
						hookRef.putPrefString(ctx, KEY_BOSS_ACTION, action);
						hookRef.putPrefString(ctx, KEY_BOSS_ACTION_PARAM, param);

						if (enable) {
							startMonitoring(ctx);
						} else {
							stopMonitoring();
						}

						dialog.dismiss();
						hookRef.jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "boss_gesture_saved"));
					}
				});
			}
		});
	}
}
