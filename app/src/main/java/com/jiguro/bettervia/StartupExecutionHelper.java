package com.jiguro.bettervia;
import android.*;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import org.json.*;
public class StartupExecutionHelper {
	private static final String KEY_STARTUP_EXECUTION_ENABLE = "startup_execution_enable";
	private static final String KEY_STARTUP_IMAGE_ENABLE = "startup_image_enable";
	private static final String KEY_STARTUP_IMAGE_DURATION = "startup_image_duration"; 
	private static final String KEY_STARTUP_IMAGE_PATH = "startup_image_path";
	private static final String KEY_STARTUP_IMAGE_BORDER_COLOR = "startup_image_border_color"; 
	private static final String KEY_STARTUP_IMAGE_FORCE_STRETCH = "startup_image_force_stretch"; 
	private static final String KEY_STARTUP_MUSIC_ENABLE = "startup_music_enable";
	private static final String KEY_STARTUP_MUSIC_PATH = "startup_music_path";
	private static final String KEY_STARTUP_HINT_ENABLE = "startup_hint_enable";
	private static final String KEY_STARTUP_HINT_TYPE = "startup_hint_type"; 
	private static final String KEY_STARTUP_HINT_CUSTOM_TEXT = "startup_hint_custom_text";
	private static final String KEY_STARTUP_HINT_HITOKOTO_API = "startup_hint_hitokoto_api"; 
	private static final String KEY_STARTUP_HINT_HITOKOTO_TYPE = "startup_hint_hitokoto_type"; 
	private static final String KEY_STARTUP_HINT_HITOKOTO_TYPES = "startup_hint_hitokoto_types"; 
	private static final String KEY_STARTUP_HINT_MIN_LENGTH = "startup_hint_min_length"; 
	private static final String KEY_STARTUP_HINT_MAX_LENGTH = "startup_hint_max_length"; 
	private static final int DEFAULT_IMAGE_DURATION = 3; 
	private static final int DEFAULT_IMAGE_BORDER_COLOR = 0xFF000000; 
	private static final boolean DEFAULT_IMAGE_FORCE_STRETCH = false; 
	private static final String DEFAULT_HITOKOTO_API = "https:
	private static final String DEFAULT_HITOKOTO_TYPE = "a"; 
	private static final String DEFAULT_HITOKOTO_TYPES = "a"; 
	private static final int DEFAULT_MIN_LENGTH = 0; 
	private static final int DEFAULT_MAX_LENGTH = 30; 
	private static final String FILE_STARTUP_IMAGE = "start_bg.jpg";
	private static final String FILE_STARTUP_MUSIC = "start_music.mp3";
	private static final String HITOKOTO_TYPE_ANIME = "a"; 
	private static final String HITOKOTO_TYPE_COMIC = "b"; 
	private static final String HITOKOTO_TYPE_GAME = "c"; 
	private static final String HITOKOTO_TYPE_LITERATURE = "d"; 
	private static final String HITOKOTO_TYPE_ORIGINAL = "e"; 
	private static final String HITOKOTO_TYPE_INTERNET = "f"; 
	private static final String HITOKOTO_TYPE_OTHER = "g"; 
	private static final String HITOKOTO_TYPE_MOVIE = "h"; 
	private static final String HITOKOTO_TYPE_POETRY = "i"; 
	private static final String HITOKOTO_TYPE_PHILOSOPHY = "k"; 
	private static MediaPlayer musicPlayer = null;
	private static FrameLayout startupOverlay = null;
	private static Hook hookInstance = null;
	public static void setHookInstance(Hook hook) {
		hookInstance = hook;
	}
	public static boolean getStartupExecutionEnable(Context ctx) {
		return getPrefBoolean(ctx, KEY_STARTUP_EXECUTION_ENABLE, false);
	}
	public static void setStartupExecutionEnable(Context ctx, boolean enable) {
		putPrefBoolean(ctx, KEY_STARTUP_EXECUTION_ENABLE, enable);
	}
	public static boolean getStartupImageEnable(Context ctx) {
		return getPrefBoolean(ctx, KEY_STARTUP_IMAGE_ENABLE, false);
	}
	public static void setStartupImageEnable(Context ctx, boolean enable) {
		putPrefBoolean(ctx, KEY_STARTUP_IMAGE_ENABLE, enable);
	}
	public static int getStartupImageDuration(Context ctx) {
		return getPrefInt(ctx, KEY_STARTUP_IMAGE_DURATION, DEFAULT_IMAGE_DURATION);
	}
	public static void setStartupImageDuration(Context ctx, int duration) {
		putPrefInt(ctx, KEY_STARTUP_IMAGE_DURATION, duration);
	}
	public static String getStartupImagePath(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_IMAGE_PATH, "");
	}
	public static void setStartupImagePath(Context ctx, String path) {
		putPrefString(ctx, KEY_STARTUP_IMAGE_PATH, path);
	}
	public static int getStartupImageBorderColor(Context ctx) {
		return getPrefInt(ctx, KEY_STARTUP_IMAGE_BORDER_COLOR, DEFAULT_IMAGE_BORDER_COLOR);
	}
	public static void setStartupImageBorderColor(Context ctx, int color) {
		putPrefInt(ctx, KEY_STARTUP_IMAGE_BORDER_COLOR, color);
	}
	public static boolean getStartupImageForceStretch(Context ctx) {
		return getPrefBoolean(ctx, KEY_STARTUP_IMAGE_FORCE_STRETCH, DEFAULT_IMAGE_FORCE_STRETCH);
	}
	public static void setStartupImageForceStretch(Context ctx, boolean force) {
		putPrefBoolean(ctx, KEY_STARTUP_IMAGE_FORCE_STRETCH, force);
	}
	public static boolean getStartupMusicEnable(Context ctx) {
		return getPrefBoolean(ctx, KEY_STARTUP_MUSIC_ENABLE, false);
	}
	public static void setStartupMusicEnable(Context ctx, boolean enable) {
		putPrefBoolean(ctx, KEY_STARTUP_MUSIC_ENABLE, enable);
	}
	public static String getStartupMusicPath(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_MUSIC_PATH, "");
	}
	public static void setStartupMusicPath(Context ctx, String path) {
		putPrefString(ctx, KEY_STARTUP_MUSIC_PATH, path);
	}
	public static boolean getStartupHintEnable(Context ctx) {
		return getPrefBoolean(ctx, KEY_STARTUP_HINT_ENABLE, false);
	}
	public static void setStartupHintEnable(Context ctx, boolean enable) {
		putPrefBoolean(ctx, KEY_STARTUP_HINT_ENABLE, enable);
	}
	public static int getStartupHintType(Context ctx) {
		return getPrefInt(ctx, KEY_STARTUP_HINT_TYPE, 0);
	}
	public static void setStartupHintType(Context ctx, int type) {
		putPrefInt(ctx, KEY_STARTUP_HINT_TYPE, type);
	}
	public static String getStartupHintCustomText(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_HINT_CUSTOM_TEXT, "");
	}
	public static void setStartupHintCustomText(Context ctx, String text) {
		putPrefString(ctx, KEY_STARTUP_HINT_CUSTOM_TEXT, text);
	}
	public static String getStartupHintHitokotoApi(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_API, DEFAULT_HITOKOTO_API);
	}
	public static void setStartupHintHitokotoApi(Context ctx, String api) {
		putPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_API, api);
	}
	public static String getStartupHintHitokotoType(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_TYPE, DEFAULT_HITOKOTO_TYPE);
	}
	public static void setStartupHintHitokotoType(Context ctx, String type) {
		putPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_TYPE, type);
	}
	public static String getStartupHintHitokotoTypes(Context ctx) {
		return getPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_TYPES, DEFAULT_HITOKOTO_TYPES);
	}
	public static void setStartupHintHitokotoTypes(Context ctx, String types) {
		putPrefString(ctx, KEY_STARTUP_HINT_HITOKOTO_TYPES, types);
	}
	public static int getStartupHintMinLength(Context ctx) {
		return getPrefInt(ctx, KEY_STARTUP_HINT_MIN_LENGTH, DEFAULT_MIN_LENGTH);
	}
	public static void setStartupHintMinLength(Context ctx, int length) {
		putPrefInt(ctx, KEY_STARTUP_HINT_MIN_LENGTH, length);
	}
	public static int getStartupHintMaxLength(Context ctx) {
		return getPrefInt(ctx, KEY_STARTUP_HINT_MAX_LENGTH, DEFAULT_MAX_LENGTH);
	}
	public static void setStartupHintMaxLength(Context ctx, int length) {
		putPrefInt(ctx, KEY_STARTUP_HINT_MAX_LENGTH, length);
	}
	private static SharedPreferences getSharedPreferences(Context ctx) {
		return ctx.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
	}
	private static boolean getPrefBoolean(Context ctx, String key, boolean defValue) {
		try {
			return getSharedPreferences(ctx).getBoolean(key, defValue);
		} catch (Exception e) {
			return defValue;
		}
	}
	private static void putPrefBoolean(Context ctx, String key, boolean value) {
		getSharedPreferences(ctx).edit().putBoolean(key, value).apply();
	}
	private static int getPrefInt(Context ctx, String key, int defValue) {
		try {
			return getSharedPreferences(ctx).getInt(key, defValue);
		} catch (Exception e) {
			return defValue;
		}
	}
	private static void putPrefInt(Context ctx, String key, int value) {
		getSharedPreferences(ctx).edit().putInt(key, value).apply();
	}
	private static String getPrefString(Context ctx, String key, String defValue) {
		try {
			return getSharedPreferences(ctx).getString(key, defValue);
		} catch (Exception e) {
			return defValue;
		}
	}
	private static void putPrefString(Context ctx, String key, String value) {
		getSharedPreferences(ctx).edit().putString(key, value).apply();
	}
	public static boolean saveStartupImage(Activity act, Uri uri) {
		if (uri == null)
			return false;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			File outFile = new File(act.getFilesDir(), FILE_STARTUP_IMAGE);
			in = act.getContentResolver().openInputStream(uri);
			out = new FileOutputStream(outFile);
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			setStartupImagePath(act, outFile.getAbsolutePath());
			return true;
		} catch (Exception e) {
			Log.e("StartupExecutionHelper", "保存启动图失败: " + e);
			return false;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception ignored) {
				}
			if (out != null)
				try {
					out.close();
				} catch (Exception ignored) {
				}
		}
	}
	public static boolean saveStartupMusic(Activity act, Uri uri) {
		if (uri == null)
			return false;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			File outFile = new File(act.getFilesDir(), FILE_STARTUP_MUSIC);
			in = act.getContentResolver().openInputStream(uri);
			out = new FileOutputStream(outFile);
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			setStartupMusicPath(act, outFile.getAbsolutePath());
			return true;
		} catch (Exception e) {
			Log.e("StartupExecutionHelper", "保存启动音乐失败: " + e);
			return false;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception ignored) {
				}
			if (out != null)
				try {
					out.close();
				} catch (Exception ignored) {
				}
		}
	}
	public static void showStartupImageEarly(final Activity activity, final Runnable onComplete) {
		final String imagePath = getStartupImagePath(activity);
		if (imagePath == null || imagePath.isEmpty()) {
			if (onComplete != null)
				onComplete.run();
			return;
		}
		Log.d("StartupExecutionHelper", "早期显示启动图: " + imagePath);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (activity.isFinishing() || activity.isDestroyed())
					return;
				final int borderColor = getStartupImageBorderColor(activity);
				final boolean forceStretch = getStartupImageForceStretch(activity);
				startupOverlay = new FrameLayout(activity);
				startupOverlay.setBackgroundColor(borderColor);
				startupOverlay.setClickable(true);
				startupOverlay.setFocusable(true);
				ImageView imageView = new ImageView(activity);
				if (forceStretch) {
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				} else {
					imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}
				try {
					File imageFile = new File(imagePath);
					if (!imageFile.exists()) {
						Log.e("StartupExecutionHelper", "启动图文件不存在: " + imagePath);
						if (onComplete != null)
							onComplete.run();
						return;
					}
					Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
					if (bitmap != null) {
						imageView.setImageBitmap(bitmap);
						Log.d("StartupExecutionHelper", "启动图加载成功: " + imagePath);
					} else {
						Log.e("StartupExecutionHelper", "启动图解码失败: " + imagePath);
						if (onComplete != null)
							onComplete.run();
						return;
					}
				} catch (Exception e) {
					Log.e("StartupExecutionHelper", "加载启动图失败: " + e);
					if (onComplete != null)
						onComplete.run();
					return;
				}
				FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				imageView.setLayoutParams(imgParams);
				startupOverlay.addView(imageView, imgParams);
				activity.getWindow().getDecorView().post(new Runnable() {
					@Override
					public void run() {
						if (activity.isFinishing() || activity.isDestroyed())
							return;
						try {
							ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
							FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
							overlayParams.gravity = Gravity.CENTER;
							decorView.addView(startupOverlay, overlayParams);
							AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
							fadeIn.setDuration(300);
							startupOverlay.startAnimation(fadeIn);
							Log.d("StartupExecutionHelper", "早期启动图覆盖层已添加到窗口");
							int duration = getStartupImageDuration(activity) * 1000;
							startupOverlay.postDelayed(new Runnable() {
								@Override
								public void run() {
									hideStartupImage();
									if (onComplete != null)
										onComplete.run();
								}
							}, duration);
						} catch (Exception e) {
							Log.e("StartupExecutionHelper", "添加早期启动图覆盖层失败: " + e);
							if (onComplete != null)
								onComplete.run();
						}
					}
				});
			}
		});
	}
	public static void executeStartupWithoutImage(final Activity activity, final Runnable onComplete) {
		Log.d("StartupExecutionHelper", "执行音乐和提示（无启动图）");
		if (getStartupMusicEnable(activity)) {
			Log.d("StartupExecutionHelper", "播放启动音乐");
			playStartupMusic(activity);
		} else {
			Log.d("StartupExecutionHelper", "启动音乐未开启");
		}
		if (getStartupHintEnable(activity)) {
			Log.d("StartupExecutionHelper", "播放启动提示");
			playStartupHint(activity, onComplete);
		} else {
			Log.d("StartupExecutionHelper", "启动提示未开启，执行完成回调");
			if (onComplete != null)
				onComplete.run();
		}
	}
	public static void executeStartup(final Activity activity, final Runnable onComplete) {
		if (!getStartupExecutionEnable(activity)) {
			Log.d("StartupExecutionHelper", "启动执行总开关未开启");
			if (onComplete != null)
				onComplete.run();
			return;
		}
		Log.d("StartupExecutionHelper", "开始执行启动流程");
		if (getStartupMusicEnable(activity)) {
			Log.d("StartupExecutionHelper", "播放启动音乐");
			playStartupMusic(activity);
		} else {
			Log.d("StartupExecutionHelper", "启动音乐未开启");
		}
		if (getStartupImageEnable(activity)) {
			Log.d("StartupExecutionHelper", "显示启动图");
			showStartupImage(activity, new Runnable() {
				@Override
				public void run() {
					Log.d("StartupExecutionHelper", "启动图显示结束");
					if (getStartupHintEnable(activity)) {
						Log.d("StartupExecutionHelper", "播放启动提示");
						playStartupHint(activity, onComplete);
					} else {
						Log.d("StartupExecutionHelper", "启动提示未开启，执行完成回调");
						if (onComplete != null)
							onComplete.run();
					}
				}
			});
		} else {
			Log.d("StartupExecutionHelper", "启动图未开启");
			if (getStartupHintEnable(activity)) {
				Log.d("StartupExecutionHelper", "播放启动提示");
				playStartupHint(activity, onComplete);
			} else {
				Log.d("StartupExecutionHelper", "启动提示未开启，执行完成回调");
				if (onComplete != null)
					onComplete.run();
			}
		}
	}
	private static void showStartupImage(final Activity activity, final Runnable onComplete) {
		final String imagePath = getStartupImagePath(activity);
		if (imagePath == null || imagePath.isEmpty()) {
			if (onComplete != null)
				onComplete.run();
			return;
		}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (activity.isFinishing() || activity.isDestroyed())
					return;
				final int borderColor = getStartupImageBorderColor(activity);
				final boolean forceStretch = getStartupImageForceStretch(activity);
				startupOverlay = new FrameLayout(activity);
				startupOverlay.setBackgroundColor(borderColor);
				startupOverlay.setClickable(true);
				startupOverlay.setFocusable(true);
				ImageView imageView = new ImageView(activity);
				if (forceStretch) {
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				} else {
					imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}
				try {
					File imageFile = new File(imagePath);
					if (!imageFile.exists()) {
						Log.e("StartupExecutionHelper", "启动图文件不存在: " + imagePath);
						if (onComplete != null)
							onComplete.run();
						return;
					}
					Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
					if (bitmap != null) {
						imageView.setImageBitmap(bitmap);
						Log.d("StartupExecutionHelper", "启动图加载成功: " + imagePath);
					} else {
						Log.e("StartupExecutionHelper", "启动图解码失败: " + imagePath);
						if (onComplete != null)
							onComplete.run();
						return;
					}
				} catch (Exception e) {
					Log.e("StartupExecutionHelper", "加载启动图失败: " + e);
					if (onComplete != null)
						onComplete.run();
					return;
				}
				FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				imageView.setLayoutParams(imgParams);
				startupOverlay.addView(imageView, imgParams);
				activity.getWindow().getDecorView().post(new Runnable() {
					@Override
					public void run() {
						if (activity.isFinishing() || activity.isDestroyed())
							return;
						try {
							ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
							FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
							overlayParams.gravity = Gravity.CENTER;
							decorView.addView(startupOverlay, overlayParams);
							AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
							fadeIn.setDuration(300);
							startupOverlay.startAnimation(fadeIn);
							Log.d("StartupExecutionHelper", "启动图覆盖层已添加到窗口");
							int duration = getStartupImageDuration(activity) * 1000;
							startupOverlay.postDelayed(new Runnable() {
								@Override
								public void run() {
									hideStartupImage();
									if (onComplete != null)
										onComplete.run();
								}
							}, duration);
						} catch (Exception e) {
							Log.e("StartupExecutionHelper", "添加启动图覆盖层失败: " + e);
							if (onComplete != null)
								onComplete.run();
						}
					}
				});
			}
		});
	}
	private static void hideStartupImage() {
		if (startupOverlay != null) {
			AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
			fadeOut.setDuration(300);
			startupOverlay.startAnimation(fadeOut);
			startupOverlay.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (startupOverlay != null) {
						try {
							ViewParent parent = startupOverlay.getParent();
							if (parent instanceof ViewGroup) {
								((ViewGroup) parent).removeView(startupOverlay);
								Log.d("StartupExecutionHelper", "启动图覆盖层已从窗口移除");
							}
						} catch (Exception e) {
							Log.e("StartupExecutionHelper", "移除启动图失败: " + e);
						}
						startupOverlay = null;
					}
				}
			}, 300);
		}
	}
	private static void playStartupMusic(final Activity activity) {
		String musicPath = getStartupMusicPath(activity);
		if (musicPath == null || musicPath.isEmpty()) {
			Log.e("StartupExecutionHelper", "启动音乐路径为空");
			return;
		}
		try {
			File musicFile = new File(musicPath);
			if (!musicFile.exists()) {
				Log.e("StartupExecutionHelper", "启动音乐文件不存在: " + musicPath);
				return;
			}
			musicPlayer = new MediaPlayer();
			musicPlayer.setDataSource(musicPath);
			musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			musicPlayer.prepare();
			musicPlayer.setLooping(false);
			musicPlayer.start();
			Log.d("StartupExecutionHelper", "启动音乐播放成功: " + musicPath);
			musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.d("StartupExecutionHelper", "启动音乐播放完成");
					stopMusic();
				}
			});
		} catch (Exception e) {
			Log.e("StartupExecutionHelper", "播放启动音乐失败: " + e);
		}
	}
	public static void stopMusic() {
		if (musicPlayer != null) {
			try {
				if (musicPlayer.isPlaying()) {
					musicPlayer.stop();
				}
				musicPlayer.release();
			} catch (Exception e) {
				Log.e("StartupExecutionHelper", "停止音乐失败: " + e);
			}
			musicPlayer = null;
		}
	}
	private static void playStartupHint(final Activity activity, final Runnable onComplete) {
		int hintType = getStartupHintType(activity);
		Log.d("StartupExecutionHelper", "启动提示类型: " + hintType);
		final String text;
		if (hintType == 0) {
			text = getStartupHintCustomText(activity);
			Log.d("StartupExecutionHelper", "自定义文本: " + text);
			if (text != null && !text.isEmpty()) {
				showToast(activity, text, onComplete);
			} else {
				Log.w("StartupExecutionHelper", "自定义文本为空，跳过显示");
				if (onComplete != null)
					onComplete.run();
			}
		} else {
			final String apiUrl = getStartupHintHitokotoApi(activity);
			final String hitokotoTypes = getStartupHintHitokotoTypes(activity);
			final int minLength = getStartupHintMinLength(activity);
			final int maxLength = getStartupHintMaxLength(activity);
			Log.d("StartupExecutionHelper",
					"一言API: " + apiUrl + ", 类型: " + hitokotoTypes + ", 长度: " + minLength + "-" + maxLength);
			new Thread(new Runnable() {
				@Override
				public void run() {
					String hitokotoText = fetchHitokoto(apiUrl, hitokotoTypes, minLength, maxLength);
					Log.d("StartupExecutionHelper", "获取到一言: " + hitokotoText);
					if (hitokotoText != null && !hitokotoText.isEmpty()) {
						showToast(activity, hitokotoText, onComplete);
					} else {
						Log.w("StartupExecutionHelper", "获取一言失败，跳过显示");
						if (onComplete != null)
							onComplete.run();
					}
				}
			}).start();
		}
	}
	private static void showToast(final Activity activity, final String text, final Runnable onComplete) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				boolean useCustomToast = getPrefBoolean(activity, "custom_toast", true);
				boolean isLongText = text != null && text.length() > 20;
				int duration = isLongText ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
				if (useCustomToast) {
					Toast customToast = createCustomToast(activity, text, duration);
					if (customToast != null) {
						customToast.show();
					}
				} else {
					Toast.makeText(activity, text, duration).show();
				}
				if (onComplete != null) {
					new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
						@Override
						public void run() {
							onComplete.run();
						}
					}, duration == Toast.LENGTH_LONG ? 3500 : 2000); 
				}
			}
		});
	}
	private static Toast createCustomToast(final Context context, final String msg, final int duration) {
		if (context == null || msg == null) {
			return null;
		}
		LinearLayout container = new LinearLayout(context);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setGravity(Gravity.CENTER);
		GradientDrawable bg = new GradientDrawable();
		bg.setColor(0xCC1E1E1E); 
		bg.setCornerRadius(dp(context, 22)); 
		container.setBackgroundDrawable(bg);
		int padding = dp(context, 18);
		int verticalPadding = dp(context, 14);
		container.setPadding(padding, verticalPadding, padding, verticalPadding);
		TextView textView = new TextView(context);
		textView.setText(msg);
		textView.setTextColor(Color.WHITE); 
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		textView.setGravity(Gravity.CENTER);
		textView.setMaxWidth(dp(context, 280)); 
		container.addView(textView);
		Toast toast = new Toast(context);
		toast.setView(container);
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM, 0, dp(context, 122));
		return toast;
	}
	private static int dp(Context ctx, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
	}
	private static String fetchHitokoto(String apiUrl, String types, int minLength, int maxLength) {
		try {
			StringBuilder urlBuilder = new StringBuilder(apiUrl);
			urlBuilder.append("?");
			if (types != null && !types.isEmpty()) {
				String[] typeArray = types.split(",");
				for (String type : typeArray) {
					urlBuilder.append("c=").append(type.trim()).append("&");
				}
			}
			if (minLength > 0) {
				urlBuilder.append("min_length=").append(minLength).append("&");
			}
			if (maxLength > 0) {
				urlBuilder.append("max_length=").append(maxLength).append("&");
			}
			String url = urlBuilder.toString();
			if (url.endsWith("&")) {
				url = url.substring(0, url.length() - 1);
			}
			Log.d("StartupExecutionHelper", "请求一言API: " + url);
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				JSONObject json = new JSONObject(response.toString());
				if (json.has("hitokoto")) {
					return json.getString("hitokoto");
				}
			}
		} catch (Exception e) {
			Log.e("StartupExecutionHelper", "获取一言失败: " + e);
		}
		return "";
	}
	public static void cleanup() {
		stopMusic();
	}
}