package com.jiguro.bettervia;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.util.*;

public class CelebrationDialog {

	public static void show(final Activity act, final Context ctx, final AnnouncementData data) {
		if (act == null || act.isFinishing() || act.isDestroyed()) {
			return;
		}

		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					showInternal(act, ctx, data);
				} catch (Exception e) {
					Hook.bvLog("[BetterVia] 庆祝对话框显示失败: " + e.getMessage());
				}
			}
		});
	}

	private static void showInternal(final Activity act, final Context ctx, final AnnouncementData data) {
	
		final int bgColor = Hook.getBgColorStatic(act);
		final int textColor = Hook.getTextColorStatic(act);
		final int hintColor = Hook.getHintColorStatic(act);
		final int titleColor = Hook.getTitleColorStatic(act);
		final int itemBgColor = Hook.getItemBgColorStatic(act);
		final int okBtnBgColor = Hook.getOkBtnBgColorStatic(act);
		final int okBtnTextColor = Hook.getOkBtnTextColorStatic(act);
		final int btnBgColor = Hook.getBtnBgColorStatic(act);

		final int contentWidth = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.85);

		final FrameLayout rootFrame = new FrameLayout(act);

		final LinearLayout card = new LinearLayout(act);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(act, 24), dp(act, 28), dp(act, 24), dp(act, 20));

		GradientDrawable cardBg = new GradientDrawable();
		cardBg.setColor(bgColor);
		cardBg.setCornerRadius(dp(act, 24));
		card.setBackground(cardBg);

		final TextView kTextCard = new TextView(act);
		kTextCard.setText("1K");
		kTextCard.setTextSize(TypedValue.COMPLEX_UNIT_SP, 52);
		kTextCard.setTypeface(null, Typeface.BOLD);
		kTextCard.setTextColor(okBtnBgColor);
		kTextCard.setGravity(Gravity.CENTER);
		kTextCard.setShadowLayer(dp(act, 2), 0, dp(act, 1), 0x44000000);
		final LinearLayout.LayoutParams kLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		kLp.gravity = Gravity.CENTER;
		kLp.bottomMargin = dp(act, 2);
		card.addView(kTextCard, kLp);

		final TextView starsLabelCard = new TextView(act);
		starsLabelCard.setText(LocalizedStringProvider.getInstance().get(act, "celebration_stars_label"));
		starsLabelCard.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		starsLabelCard.setTextColor(hintColor);
		starsLabelCard.setGravity(Gravity.CENTER);
		final LinearLayout.LayoutParams starsLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		starsLp.bottomMargin = dp(act, 16);
		card.addView(starsLabelCard, starsLp);

		String dialogTitle = data.getLocalizedDialogTitle(act);
		if (dialogTitle != null && !dialogTitle.isEmpty()) {
			TextView dtView = new TextView(act);
			dtView.setText(dialogTitle);
			dtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			dtView.setTextColor(textColor);
			dtView.setShadowLayer(dp(act, 2), 0, 0, bgColor);
			dtView.setTypeface(null, Typeface.BOLD);
			dtView.setGravity(Gravity.START);
			LinearLayout.LayoutParams dtLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			dtLp.bottomMargin = dp(act, 6);
			card.addView(dtView, dtLp);
		}

		String dialogSubtitle = data.getLocalizedDialogSubtitle(act);
		if (dialogSubtitle != null && !dialogSubtitle.isEmpty()) {
			TextView dsView = new TextView(act);
			dsView.setText(dialogSubtitle);
			dsView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			dsView.setTextColor(hintColor);
			dsView.setShadowLayer(dp(act, 2), 0, 0, bgColor);
			dsView.setGravity(Gravity.START);
			dsView.setLineSpacing(dp(act, 2), 1.1f);
			LinearLayout.LayoutParams dsLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			dsLp.bottomMargin = dp(act, 12);
			card.addView(dsView, dsLp);
		}

		String content = data.getLocalizedDialogContent(act);
		if (content != null && !content.isEmpty()) {
			LinearLayout contentContainer = new LinearLayout(act);
			contentContainer.setOrientation(LinearLayout.VERTICAL);
			contentContainer.setPadding(dp(act, 16), dp(act, 14), dp(act, 16), dp(act, 14));

			GradientDrawable contentBg = new GradientDrawable();
			contentBg.setColor(itemBgColor);
			contentBg.setAlpha(245);
			contentBg.setCornerRadius(dp(act, 12));
			contentContainer.setBackground(contentBg);

			TextView contentView = new TextView(act);
			contentView.setText(content);
			contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			contentView.setTextColor(textColor);
			contentView.setGravity(Gravity.START);
			contentView.setLineSpacing(dp(act, 3), 1.2f);
			contentContainer.addView(contentView);

			LinearLayout.LayoutParams ccLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			ccLp.bottomMargin = dp(act, 16);
			card.addView(contentContainer, ccLp);
		}

		LinearLayout buttonRow = new LinearLayout(act);
		buttonRow.setOrientation(LinearLayout.HORIZONTAL);
		buttonRow.setGravity(Gravity.CENTER);

		if (data.hasPositiveButton && !TextUtils.isEmpty(data.positiveButtonText)) {
			String ghText = data.getLocalizedPositiveButton(act);
			Button ghBtn = new Button(act);
			ghBtn.setText(ghText);
			ghBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			ghBtn.setTextColor(okBtnTextColor);
			ghBtn.setTypeface(null, Typeface.BOLD);
			ghBtn.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
			ghBtn.setAllCaps(false);
			GradientDrawable ghBg = new GradientDrawable();
			ghBg.setColor(okBtnBgColor);
			ghBg.setCornerRadius(dp(act, 12));
			ghBtn.setBackground(ghBg);
			ghBtn.setMinHeight(dp(act, 44));
			applyClickAnim(ghBtn);
			ghBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String url = data.positiveButtonUrl;
						if (url != null && !url.isEmpty()) {
							act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
						}
					} catch (Exception ignored) {
					}
				}
			});
			LinearLayout.LayoutParams ghLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
					1.0f);
			ghLp.rightMargin = dp(act, 8);
			buttonRow.addView(ghBtn, ghLp);
		}

		String confirmText = data.getLocalizedNegativeButton(act);
		if (confirmText == null || confirmText.isEmpty()) {
			confirmText = LocalizedStringProvider.getInstance().get(act, "dialog_ok");
		}
		Button confirmBtn = new Button(act);
		confirmBtn.setText(confirmText);
		confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		confirmBtn.setTextColor(titleColor);
		confirmBtn.setTypeface(null, Typeface.BOLD);
		confirmBtn.setPadding(dp(act, 24), dp(act, 12), dp(act, 24), dp(act, 12));
		confirmBtn.setAllCaps(false);
		GradientDrawable confBg = new GradientDrawable();
		confBg.setColor(btnBgColor);
		confBg.setCornerRadius(dp(act, 12));
		confirmBtn.setBackground(confBg);
		confirmBtn.setMinHeight(dp(act, 44));
		applyClickAnim(confirmBtn);
		LinearLayout.LayoutParams confLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
		confLp.leftMargin = dp(act, 8);
		buttonRow.addView(confirmBtn, confLp);

		card.addView(buttonRow);

		rootFrame.addView(card,
				new FrameLayout.LayoutParams(contentWidth, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		card.setAlpha(0f);

		final FireworksView fireworks = new FireworksView(act);
		fireworks.setClickable(false);
		fireworks.setFocusable(false);
		rootFrame.addView(fireworks,
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		final FrameLayout entryView = new FrameLayout(act);
		entryView.setClickable(false);

		final LinearLayout entryInner = new LinearLayout(act);
		entryInner.setOrientation(LinearLayout.VERTICAL);
		entryInner.setGravity(Gravity.CENTER);

		final TextView kTextEntry = new TextView(act);
		kTextEntry.setText("1K");
		kTextEntry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
		kTextEntry.setTypeface(null, Typeface.BOLD);
		kTextEntry.setTextColor(okBtnBgColor);
		kTextEntry.setGravity(Gravity.CENTER);
		kTextEntry.setShadowLayer(dp(act, 4), 0, dp(act, 2), 0x44000000);
		entryInner.addView(kTextEntry, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		final TextView starsLabelEntry = new TextView(act);
		starsLabelEntry.setText(LocalizedStringProvider.getInstance().get(act, "celebration_stars_label"));
		starsLabelEntry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		starsLabelEntry.setTextColor(hintColor);
		starsLabelEntry.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams starsEntryLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		starsEntryLp.topMargin = (int) (dp(act, 2) / (52f / 80f) + 0.5f);
		entryInner.addView(starsLabelEntry, starsEntryLp);

		entryView.addView(entryInner, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		rootFrame.addView(entryView,
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		final Dialog dialog = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(rootFrame);
		dialog.setCancelable(false);

		Window win = dialog.getWindow();
		if (win != null) {
			win.setBackgroundDrawableResource(android.R.color.transparent);
			GradientDrawable winBg = new GradientDrawable();
			winBg.setColor(bgColor);
			win.setBackgroundDrawable(winBg);
			win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			}
			if (Build.VERSION.SDK_INT >= 19) {
				win.getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		}

		TextView backBtn = new TextView(act);
		backBtn.setText("\u2715");
		backBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		backBtn.setTextColor(textColor);
		backBtn.setGravity(Gravity.CENTER);
		backBtn.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fireworks.stopAnimation();
				AnnouncementManager.markAnnouncementAsDismissed(act, data.id);
				dialog.dismiss();
			}
		});
		FrameLayout.LayoutParams backLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		backLp.gravity = Gravity.TOP | Gravity.START;
		backLp.topMargin = dp(act, 12);
		rootFrame.addView(backBtn, backLp);

		dialog.show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				int[] entryTextLoc = new int[2];
				int[] cardTextLoc = new int[2];
				int[] evLoc = new int[2];

				kTextEntry.getLocationOnScreen(entryTextLoc);
				kTextCard.getLocationOnScreen(cardTextLoc);
				entryView.getLocationOnScreen(evLoc);

				float pivotX = evLoc[0] + entryView.getWidth() / 2f;
				float pivotY = evLoc[1] + entryView.getHeight() / 2f;

				float entryTextCX = entryTextLoc[0] + kTextEntry.getWidth() / 2f;
				float entryTextCY = entryTextLoc[1] + kTextEntry.getHeight() / 2f;

				float cardTextCX = cardTextLoc[0] + kTextCard.getWidth() / 2f;
				float cardTextCY = cardTextLoc[1] + kTextCard.getHeight() / 2f;

				float scale = 52f / 80f;

				float targetX = cardTextCX - pivotX - scale * (entryTextCX - pivotX);
				float targetY = cardTextCY - pivotY - scale * (entryTextCY - pivotY);

				entryView.animate().translationX(targetX).translationY(targetY).scaleX(scale).scaleY(scale)
						.setDuration(500).setInterpolator(new DecelerateInterpolator(2f)).start();
			}
		}, 800);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
			
				for (int i = 2; i < card.getChildCount(); i++) {
					View v = card.getChildAt(i);
					if (v != null)
						v.setAlpha(0f);
				}

				entryView.animate().alpha(0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();

				card.animate().alpha(1f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						rootFrame.removeView(entryView);
					}
				}, 200);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						for (int i = 2; i < card.getChildCount(); i++) {
							final View v = card.getChildAt(i);
							if (v == null)
								continue;
							v.setTranslationY(dp(act, 10));
							v.animate().alpha(1f).translationY(0f).setStartDelay((i - 2) * 60).setDuration(220)
									.setInterpolator(new DecelerateInterpolator()).start();
						}
					}
				}, 250);
			}
		}, 1400);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				fireworks.startAnimation();
			}
		}, 2200);

		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fireworks.stopAnimation();
				AnnouncementManager.markAnnouncementAsDismissed(act, data.id);
				dialog.dismiss();
			}
		});
	}

	private static void applyClickAnim(final View v) {
		if (v == null)
			return;
		v.setClickable(true);
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						v.animate().cancel();
						v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
						return false;
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_CANCEL :
						v.animate().cancel();
						v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
						return false;
				}
				return false;
			}
		});
	}

	private static class FireworksView extends View {
		private final List<Confetti> confettiList = new ArrayList<Confetti>();
		private final Random random = new Random();
		private final Handler handler = new Handler();
		private boolean running = false;
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private int screenW = 400, screenH = 700;

		private static final int BURST_PER_SIDE = 100;
		private static final int[] COLORS = {0xFFFFD700, 0xFFFF6B6B, 0xFF4ECDC4, 0xFFFF69B4, 0xFFA78BFA, 0xFF34D399,
				0xFF60A5FA, 0xFFFBBF24, 0xFFFF8C42, 0xFFE040FB,};

		public FireworksView(Context ctx) {
			super(ctx);
			screenW = ctx.getResources().getDisplayMetrics().widthPixels;
			screenH = ctx.getResources().getDisplayMetrics().heightPixels;
		}

		public void startAnimation() {
			running = true;
			post(new Runnable() {
				@Override
				public void run() {
					if (!running)
						return;
					shootBurst();
					updateLoop();
				}
			});
		}

		public void stopAnimation() {
			running = false;
			handler.removeCallbacksAndMessages(null);
		}

		private void shootBurst() {
		
			for (int i = 0; i < BURST_PER_SIDE; i++) {
				Confetti c = new Confetti();
				double angle = Math.toRadians(-30 + random.nextDouble() * -60);
				double speed = 12 + random.nextDouble() * 24;
				c.x = random.nextInt(80);
				c.y = screenH - 20 + random.nextInt(50);
				c.vx = (float) (Math.cos(angle) * speed);
				c.vy = (float) (Math.sin(angle) * speed - 3);
				c.rot = random.nextFloat() * 360f;
				c.rotSpeed = (random.nextFloat() - 0.5f) * 8f;
				c.w = dp(this, 8 + random.nextInt(10));
				c.h = dp(this, 3 + random.nextInt(4));
				if (random.nextBoolean()) {
					float t = c.w;
					c.w = c.h;
					c.h = t;
				}
				c.color = COLORS[random.nextInt(COLORS.length)];
				c.alpha = 1f;
				c.life = 50 + random.nextInt(50);
				c.age = 0;
				confettiList.add(c);
			}

			for (int i = 0; i < BURST_PER_SIDE; i++) {
				Confetti c = new Confetti();
				double angle = Math.toRadians(-90 + random.nextDouble() * -60);
				double speed = 12 + random.nextDouble() * 24;
				c.x = screenW - random.nextInt(80);
				c.y = screenH - 20 + random.nextInt(50);
				c.vx = (float) (Math.cos(angle) * speed);
				c.vy = (float) (Math.sin(angle) * speed - 3);
				c.rot = random.nextFloat() * 360f;
				c.rotSpeed = (random.nextFloat() - 0.5f) * 8f;
				c.w = dp(this, 8 + random.nextInt(10));
				c.h = dp(this, 3 + random.nextInt(4));
				if (random.nextBoolean()) {
					float t = c.w;
					c.w = c.h;
					c.h = t;
				}
				c.color = COLORS[random.nextInt(COLORS.length)];
				c.alpha = 1f;
				c.life = 50 + random.nextInt(50);
				c.age = 0;
				confettiList.add(c);
			}
		}

		private void updateLoop() {
			if (!running)
				return;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!running)
						return;
					updateConfetti();
					invalidate();
					if (!confettiList.isEmpty()) {
						updateLoop();
					}
				}
			}, 25);
		}

		private void updateConfetti() {
			for (int i = confettiList.size() - 1; i >= 0; i--) {
				Confetti c = confettiList.get(i);
				c.x += c.vx;
				c.y += c.vy;
				c.vy += 0.08f;
				c.vx *= 0.998f;
				c.rot += c.rotSpeed;
				c.age++;

				double t = (double) c.age / c.life;
				if (t > 1.0)
					t = 1.0;
				c.alpha = (float) (1.0 - t * t * t);

				if (c.age >= c.life) {
					confettiList.remove(i);
				}
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (confettiList.isEmpty())
				return;

			for (Confetti c : confettiList) {
				if (c.alpha <= 0.01f)
					continue;
				paint.setColor(c.color);
				paint.setAlpha((int) (c.alpha * 255));
				canvas.save();
				canvas.rotate(c.rot, c.x, c.y);
				canvas.drawRect(c.x - c.w / 2f, c.y - c.h / 2f, c.x + c.w / 2f, c.y + c.h / 2f, paint);
				canvas.restore();
			}
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			stopAnimation();
		}

		private static int dp(View v, int dp) {
			return (int) (v.getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
		}

		private static class Confetti {
			float x, y, vx, vy, rot, rotSpeed, w, h, alpha;
			int color, life, age;
		}
	}

	private static int dp(Context ctx, int dp) {
		float density = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}
}
