package com.jiguro.bettervia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VideoPlayerHook {

	public static final String KEY_LONG_PRESS_SPEED = "long_press_speed";

	public static final String KEY_FREE_ZOOM = "free_zoom";

	private static final float LONG_PRESS_SPEED = 2.0f;

	private static final long LONG_PRESS_TIMEOUT = 300L;

	private static final float LONG_PRESS_CANCEL_THRESHOLD = 40f;

	private static long touchStartTime = 0;

	private static boolean isLongPressing = false;

	private static float originalSpeed = 1.0f;

	private static TextView speedToastView = null;

	private static Handler longPressHandler = null;

	private static Runnable longPressRunnable = null;

	private static final float ORIGINAL_SCALE = 1.0f;

	private static float currentScale = 1.0f;

	private static final float REGION_TOP_RATIO = 0.15f;
	private static final float REGION_BOTTOM_RATIO = 0.15f;

	private static TextView scaleDisplayView = null;

	private static TextView resetButton = null;

	private static final int GESTURE_NONE = 0;
	private static final int GESTURE_ZOOM = 1;
	private static final int GESTURE_PAN = 2;

	private static int gestureMode = GESTURE_NONE;

	private static float zoomStartSpan = 0f;
	private static float zoomStartScale = 1.0f;

	private static float panStartCentroidX = 0f;
	private static float panStartCentroidY = 0f;
	private static float panStartTranslateX = 0f;
	private static float panStartTranslateY = 0f;

	private static float zoomStartSpeed = 1.0f;

	private static Object currentPlayerView = null;

	private static FrameLayout playerContainer = null;

	private static Handler hideScaleDisplayHandler = null;

	private static Runnable hideScaleDisplayRunnable = null;

	private static final long HIDE_SCALE_DELAY = 1500L;

	private static String fieldNameSpeed = "D";
	private static String fieldNameCallback = "A";
	private static String fieldNameSpeedText = "s";
	private static String fieldNameStartX = "R";
	private static String fieldNameStartY = "S";
	private static String methodNameSetSpeed = "c";

	public static void initVideoPlayerHook(final Context ctx, final ClassLoader cl) {
	
		final boolean longPressSpeedEnabled = Hook.getPrefBoolean(ctx, KEY_LONG_PRESS_SPEED, false);
		final boolean freeZoomEnabled = Hook.getPrefBoolean(ctx, KEY_FREE_ZOOM, false);

		if (!longPressSpeedEnabled && !freeZoomEnabled) {
			return;
		}

		try {
		
			fieldNameSpeed = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED,
					ctx);
			fieldNameCallback = ViaClassMapping
					.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_FIELD_CALLBACK, ctx);
			fieldNameSpeedText = ViaClassMapping
					.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_FIELD_SPEED_TEXT, ctx);
			fieldNameStartX = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_FIELD_START_X,
					ctx);
			fieldNameStartY = ViaClassMapping.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_FIELD_START_Y,
					ctx);
			methodNameSetSpeed = ViaClassMapping
					.getMethodName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_CALLBACK_SET_SPEED, ctx);

			final String videoPlayerClass = ViaClassMapping
					.getClassName(ViaClassMapping.ClassMethodKey.VIDEO_PLAYER_CLASS, ctx);
			Class<?> playerClass = cl.loadClass(videoPlayerClass);

			XposedHelpers.findAndHookMethod(playerClass, "dispatchTouchEvent", MotionEvent.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam hookParam) throws Throwable {
					handleTouchEvent(ctx, hookParam, freeZoomEnabled);
				}
			});

			XposedHelpers.findAndHookMethod(playerClass, "onDetachedFromWindow", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam hookParam) throws Throwable {
					cleanupAllViews();
				}
			});

			Hook.bvLog("[BetterVia] 视频播放器Hook已启用");

		} catch (ClassNotFoundException e) {
			Hook.bvLog("[BetterVia] 视频播放器类未找到: " + e.getMessage());
		} catch (Throwable t) {
			Hook.bvLog("[BetterVia] 视频播放器Hook初始化失败: " + t.getMessage());
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private static void handleTouchEvent(final Context ctx, XC_MethodHook.MethodHookParam hookParam,
			boolean freeZoomEnabled) {
		MotionEvent event = (MotionEvent) hookParam.args[0];
		int action = event.getActionMasked();
		final Object playerView = hookParam.thisObject;
		currentPlayerView = playerView;

		if (playerContainer == null && playerView instanceof FrameLayout) {
			playerContainer = (FrameLayout) playerView;
		}

		float currentSpeed = XposedHelpers.getFloatField(playerView, fieldNameSpeed);

		if (longPressHandler == null) {
			longPressHandler = new Handler(Looper.getMainLooper());
		}

		if (freeZoomEnabled && event.getPointerCount() == 2) {
			handleTwoFingerGesture(ctx, event, action);
			return;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN :
			
				touchStartTime = System.currentTimeMillis();
				isLongPressing = false;
				originalSpeed = currentSpeed;

				if (longPressRunnable != null) {
					longPressHandler.removeCallbacks(longPressRunnable);
				}

				longPressRunnable = new Runnable() {
					@Override
					public void run() {
					
						try {
						
							if (gestureMode == GESTURE_NONE && !isLongPressing) {
								isLongPressing = true;
								setPlaybackSpeed(playerView, LONG_PRESS_SPEED);
								showSpeedToast(ctx, true);
								Hook.bvLog("[BetterVia] 长按倍速已触发: " + LONG_PRESS_SPEED + "X");
							}
						} catch (Throwable t) {
							Hook.bvLog("[BetterVia] 长按倍速触发失败: " + t.getMessage());
						}
					}
				};

				longPressHandler.postDelayed(longPressRunnable, LONG_PRESS_TIMEOUT);
				break;

			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
			
				if (longPressRunnable != null) {
					longPressHandler.removeCallbacks(longPressRunnable);
				}

				if (isLongPressing) {
				
					setPlaybackSpeed(playerView, originalSpeed);
					hideSpeedToast();
					Hook.bvLog("[BetterVia] 恢复原始倍速: " + originalSpeed + "X");
				}

				isLongPressing = false;
				touchStartTime = 0;
				break;

			case MotionEvent.ACTION_MOVE :
			
				if (!isLongPressing && longPressRunnable != null) {
					float x = event.getX();
					float y = event.getY();

					try {
						int startX = XposedHelpers.getIntField(playerView, fieldNameStartX);
						int startY = XposedHelpers.getIntField(playerView, fieldNameStartY);
					
						float dx = Math.abs(x - startX);
						float dy = Math.abs(y - startY);

						if (dx > LONG_PRESS_CANCEL_THRESHOLD || dy > LONG_PRESS_CANCEL_THRESHOLD) {
							longPressHandler.removeCallbacks(longPressRunnable);
						}
					} catch (Throwable t) {
					
						longPressHandler.removeCallbacks(longPressRunnable);
					}
				}
				break;
		}
	}

	private static void handleTwoFingerGesture(Context ctx, MotionEvent event, int action) {
	
		if (isLongPressing && currentPlayerView != null) {
			setPlaybackSpeed(currentPlayerView, originalSpeed);
			hideSpeedToast();
			isLongPressing = false;
			if (longPressRunnable != null) {
				longPressHandler.removeCallbacks(longPressRunnable);
			}
			Hook.bvLog("[BetterVia] 双指按下，取消长按倍速");
		}

		switch (action) {
			case MotionEvent.ACTION_POINTER_DOWN : {
			
				float touchY = (event.getY(0) + event.getY(1)) / 2f;
				float height;
				try {
					height = (Integer) XposedHelpers.callMethod(currentPlayerView, "getHeight");
				} catch (Throwable t) {
					height = playerContainer != null ? playerContainer.getHeight() : 0f;
				}

				float topBound = height * REGION_TOP_RATIO;
				float bottomBound = height * (1.0f - REGION_BOTTOM_RATIO);

				if (touchY < topBound || touchY > bottomBound) {
				
					gestureMode = GESTURE_PAN;
					panStartCentroidX = (event.getX(0) + event.getX(1)) / 2f;
					panStartCentroidY = touchY;
					try {
						panStartTranslateX = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationX");
						panStartTranslateY = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationY");
					} catch (Throwable t) {
						panStartTranslateX = 0f;
						panStartTranslateY = 0f;
					}
					showResetButton(ctx);
					Hook.bvLog("[BetterVia] 平移模式，起始质心: (" + panStartCentroidX + ", " + panStartCentroidY + ")");

				} else {
				
					gestureMode = GESTURE_ZOOM;
					zoomStartSpan = getDistance(event);
					zoomStartScale = currentScale;

					if (currentPlayerView != null) {
						try {
							zoomStartSpeed = XposedHelpers.getFloatField(currentPlayerView, fieldNameSpeed);
						} catch (Throwable t) {
							zoomStartSpeed = 1.0f;
						}
					}

					if (hideScaleDisplayHandler != null && hideScaleDisplayRunnable != null) {
						hideScaleDisplayHandler.removeCallbacks(hideScaleDisplayRunnable);
					}

					showScaleDisplay(ctx);
					showResetButton(ctx);
					Hook.bvLog("[BetterVia] 缩放模式，起始间距: " + zoomStartSpan + "  当前缩放: " + currentScale);
				}
				break;
			}

			case MotionEvent.ACTION_MOVE : {
				if (gestureMode == GESTURE_NONE || event.getPointerCount() != 2) {
					break;
				}

				if (gestureMode == GESTURE_ZOOM) {
				
					if (zoomStartSpan <= 0f)
						break;
					float newSpan = getDistance(event);
					float scaleRatio = newSpan / zoomStartSpan;
					float newScale = zoomStartScale * scaleRatio;
					if (newScale < 0.5f)
						newScale = 0.5f;
					else if (newScale > 3.0f)
						newScale = 3.0f;
					currentScale = newScale;
					applyScaleOnly(newScale);

					if (hideScaleDisplayHandler != null && hideScaleDisplayRunnable != null) {
						hideScaleDisplayHandler.removeCallbacks(hideScaleDisplayRunnable);
					}
					updateScaleDisplay(currentScale);

				} else {
				
					float cx = (event.getX(0) + event.getX(1)) / 2f;
					float cy = (event.getY(0) + event.getY(1)) / 2f;
					float dx = cx - panStartCentroidX;
					float dy = cy - panStartCentroidY;

					if (currentPlayerView != null) {
						try {
							float newTx = panStartTranslateX + dx;
							float newTy = panStartTranslateY + dy;
							XposedHelpers.callMethod(currentPlayerView, "setTranslationX", newTx);
							XposedHelpers.callMethod(currentPlayerView, "setTranslationY", newTy);
						
							XposedHelpers.callMethod(currentPlayerView, "invalidate");
						} catch (Throwable t) {
							Hook.bvLog("[BetterVia] 平移失败: " + t.getMessage());
						}
					}
					updateResetButtonVisibility();
				}
				break;
			}

			case MotionEvent.ACTION_POINTER_UP :
			case MotionEvent.ACTION_UP : {
			
				int prevMode = gestureMode;
				gestureMode = GESTURE_NONE;

				if (longPressHandler != null && longPressRunnable != null) {
					longPressHandler.removeCallbacks(longPressRunnable);
				}

				if (prevMode == GESTURE_ZOOM) {
				
					if (currentPlayerView != null && zoomStartSpeed > 0f) {
						try {
							float curSpeed = XposedHelpers.getFloatField(currentPlayerView, fieldNameSpeed);
							if (Math.abs(curSpeed - LONG_PRESS_SPEED) < 0.01f) {
								setPlaybackSpeed(currentPlayerView, zoomStartSpeed);
								hideSpeedToast();
								isLongPressing = false;
							}
						} catch (Throwable t) {
						}
					}

					zoomStartSpan = 0f;
					Hook.bvLog("[BetterVia] 缩放结束，比例: " + currentScale);

					if (hideScaleDisplayHandler == null) {
						hideScaleDisplayHandler = new Handler(Looper.getMainLooper());
					}
					if (hideScaleDisplayRunnable != null) {
						hideScaleDisplayHandler.removeCallbacks(hideScaleDisplayRunnable);
					}
					hideScaleDisplayRunnable = new Runnable() {
						@Override
						public void run() {
							hideScaleDisplay();
						}
					};
					hideScaleDisplayHandler.postDelayed(hideScaleDisplayRunnable, HIDE_SCALE_DELAY);

				} else if (prevMode == GESTURE_PAN) {
				
					if (currentPlayerView != null) {
						try {
							panStartTranslateX = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationX");
							panStartTranslateY = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationY");
						} catch (Throwable t) {
						}
					}
					Hook.bvLog("[BetterVia] 平移结束");
				}
				break;
			}
		}
	}

	private static float getDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private static void applyScaleOnly(float scale) {
		if (currentPlayerView == null) {
			return;
		}

		try {
			int vw = (Integer) XposedHelpers.callMethod(currentPlayerView, "getWidth");
			int vh = (Integer) XposedHelpers.callMethod(currentPlayerView, "getHeight");
			XposedHelpers.callMethod(currentPlayerView, "setPivotX", vw / 2.0f);
			XposedHelpers.callMethod(currentPlayerView, "setPivotY", vh / 2.0f);
			XposedHelpers.callMethod(currentPlayerView, "setScaleX", scale);
			XposedHelpers.callMethod(currentPlayerView, "setScaleY", scale);
		} catch (Throwable t) {
			Hook.bvLog("[BetterVia] 缩放失败: " + t.getMessage());
		}
	}

	private static void showScaleDisplay(Context ctx) {
		if (playerContainer == null)
			return;

		if (scaleDisplayView == null) {
			scaleDisplayView = new TextView(ctx);
			scaleDisplayView.setTextColor(Color.WHITE);
			scaleDisplayView.setTextSize(12.0f);
			scaleDisplayView.setGravity(Gravity.CENTER);
			scaleDisplayView.setPadding(dpToPx(ctx, 16), dpToPx(ctx, 8), dpToPx(ctx, 16), dpToPx(ctx, 8));

			GradientDrawable bg = new GradientDrawable();

			bg.setColor(0xB0787878);

			bg.setCornerRadius(dpToPx(ctx, 12));

			scaleDisplayView.setBackground(bg);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			params.topMargin = dpToPx(ctx, 10);
			playerContainer.addView(scaleDisplayView, params);
		}

		updateScaleDisplay(currentScale);
	}

	private static void updateScaleDisplay(float scale) {
		if (scaleDisplayView != null) {
			int percentage = (int) (scale * 100);
			scaleDisplayView.setText(percentage + "%");
		}

		updateResetButtonVisibility();
	}

	private static void updateResetButtonVisibility() {
		if (resetButton == null) {
			return;
		}

		boolean scaleChanged = Math.abs(currentScale - ORIGINAL_SCALE) > 0.01f;

		boolean translationChanged = false;
		if (currentPlayerView != null) {
			try {
				float tx = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationX");
				float ty = (Float) XposedHelpers.callMethod(currentPlayerView, "getTranslationY");
				translationChanged = Math.abs(tx) > 1.0f || Math.abs(ty) > 1.0f;
			} catch (Throwable t) {
			
			}
		}

		if (scaleChanged || translationChanged) {
			resetButton.setVisibility(View.VISIBLE);
		} else {
			hideResetButton();
		}
	}

	private static void hideScaleDisplay() {
		if (scaleDisplayView != null && playerContainer != null) {
			playerContainer.removeView(scaleDisplayView);
			scaleDisplayView = null;
		}
	}

	private static void showResetButton(Context ctx) {
		if (playerContainer == null)
			return;

		if (resetButton == null) {
			resetButton = new TextView(ctx);
			resetButton.setText(LocalizedStringProvider.getInstance().get(ctx, "reset_zoom"));
			resetButton.setTextColor(Color.WHITE);
			resetButton.setTextSize(12.0f);
			resetButton.setGravity(Gravity.CENTER);
			resetButton.setPadding(dpToPx(ctx, 24), dpToPx(ctx, 12), dpToPx(ctx, 24), dpToPx(ctx, 12));

			GradientDrawable bg = new GradientDrawable();

			bg.setColor(0xB0787878);

			bg.setCornerRadius(dpToPx(ctx, 12));

			resetButton.setBackground(bg);
			resetButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					resetZoom();
				}
			});

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			params.bottomMargin = dpToPx(ctx, 80);
			playerContainer.addView(resetButton, params);
		}
	}

	private static void hideResetButton() {
		if (resetButton != null) {
			resetButton.setVisibility(View.GONE);
		}
	}

	private static void resetZoom() {
		if (currentPlayerView == null) {
			return;
		}

		try {
		
			currentScale = ORIGINAL_SCALE;
			gestureMode = GESTURE_NONE;
			zoomStartSpan = 0f;
			zoomStartScale = ORIGINAL_SCALE;
			panStartTranslateX = 0f;
			panStartTranslateY = 0f;

			applyScaleOnly(ORIGINAL_SCALE);
			XposedHelpers.callMethod(currentPlayerView, "setTranslationX", 0f);
			XposedHelpers.callMethod(currentPlayerView, "setTranslationY", 0f);

			if (scaleDisplayView != null) {
				updateScaleDisplay(ORIGINAL_SCALE);
			}

			hideScaleDisplay();
			hideResetButton();

			Hook.bvLog("[BetterVia] 缩放、位移已重置");

		} catch (Throwable t) {
			Hook.bvLog("[BetterVia] 重置缩放失败: " + t.getMessage());
		}
	}

	private static void setPlaybackSpeed(Object playerView, float speed) {
		try {
		
			XposedHelpers.setFloatField(playerView, fieldNameSpeed, speed);

			Object controllerCallback = XposedHelpers.getObjectField(playerView, fieldNameCallback);

			if (controllerCallback != null) {
			
				XposedHelpers.callMethod(controllerCallback, methodNameSetSpeed, speed);
			}

			TextView speedTextView = (TextView) XposedHelpers.getObjectField(playerView, fieldNameSpeedText);
			if (speedTextView != null) {
				speedTextView.setText(String.format("%sX", speed));
			}

		} catch (Throwable t) {
			Hook.bvLog("[BetterVia] 设置播放倍速失败: " + t.getMessage());
		}
	}

	private static void showSpeedToast(Context ctx, boolean show) {
		if (playerContainer == null)
			return;

		if (show) {
			if (speedToastView == null) {
				speedToastView = new TextView(ctx);
				speedToastView.setText(LocalizedStringProvider.getInstance().get(ctx, "speed_boosting"));
				speedToastView.setTextColor(Color.WHITE);
				speedToastView.setTextSize(14.0f);
				speedToastView.setGravity(Gravity.CENTER);
				speedToastView.setPadding(dpToPx(ctx, 20), dpToPx(ctx, 10), dpToPx(ctx, 20), dpToPx(ctx, 10));

				GradientDrawable bg = new GradientDrawable();

				bg.setColor(0xB0787878);

				bg.setCornerRadius(dpToPx(ctx, 12));

				speedToastView.setBackground(bg);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				params.topMargin = dpToPx(ctx, 60);
				playerContainer.addView(speedToastView, params);
			} else {
				speedToastView.setVisibility(View.VISIBLE);
			}
		} else {
			hideSpeedToast();
		}
	}

	private static void hideSpeedToast() {
		if (speedToastView != null && playerContainer != null) {
			playerContainer.removeView(speedToastView);
			speedToastView = null;
		}
	}

	private static int dpToPx(Context ctx, float dp) {
		float density = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}

	private static void cleanupAllViews() {
	
		if (speedToastView != null && playerContainer != null) {
			playerContainer.removeView(speedToastView);
			speedToastView = null;
		}
		if (scaleDisplayView != null && playerContainer != null) {
			playerContainer.removeView(scaleDisplayView);
			scaleDisplayView = null;
		}
		if (resetButton != null && playerContainer != null) {
			playerContainer.removeView(resetButton);
			resetButton = null;
		}

		currentScale = ORIGINAL_SCALE;
		gestureMode = GESTURE_NONE;
		zoomStartSpan = 0f;
		zoomStartScale = ORIGINAL_SCALE;
		panStartTranslateX = 0f;
		panStartTranslateY = 0f;
		zoomStartSpeed = 1.0f;
		playerContainer = null;
		currentPlayerView = null;

		Hook.bvLog("[BetterVia] 播放器View已清理");
	}

	public static void cleanup() {
		if (longPressHandler != null && longPressRunnable != null) {
			longPressHandler.removeCallbacks(longPressRunnable);
		}
		if (hideScaleDisplayHandler != null && hideScaleDisplayRunnable != null) {
			hideScaleDisplayHandler.removeCallbacks(hideScaleDisplayRunnable);
		}

		if (speedToastView != null && playerContainer != null) {
			playerContainer.removeView(speedToastView);
			speedToastView = null;
		}
		if (scaleDisplayView != null && playerContainer != null) {
			playerContainer.removeView(scaleDisplayView);
			scaleDisplayView = null;
		}
		if (resetButton != null && playerContainer != null) {
			playerContainer.removeView(resetButton);
			resetButton = null;
		}

		longPressHandler = null;
		longPressRunnable = null;
		hideScaleDisplayHandler = null;
		hideScaleDisplayRunnable = null;
		isLongPressing = false;
		touchStartTime = 0;

		currentScale = ORIGINAL_SCALE;
		gestureMode = GESTURE_NONE;
		zoomStartSpan = 0f;
		zoomStartScale = ORIGINAL_SCALE;
		panStartTranslateX = 0f;
		panStartTranslateY = 0f;
		zoomStartSpeed = 1.0f;
		playerContainer = null;
		currentPlayerView = null;
	}
}
