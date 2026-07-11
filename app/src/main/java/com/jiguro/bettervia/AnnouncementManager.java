package com.jiguro.bettervia;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import de.robv.android.xposed.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class AnnouncementManager {

	private static final String KEY_ANNOUNCEMENT_DISMISSED_PREFIX = "announcement_dismissed_";

	private static final String VERCEL_ANNOUNCEMENT_URL = "https://raw.196104.xyz/announcement.json";
	private static final String GITHUB_ANNOUNCEMENT_URL = "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/announcement.json";

	private Context context;
	private Activity activity;

	private AnnouncementData announcementData;

	public AnnouncementManager(Context ctx) {
		this.context = ctx;
		if (ctx instanceof Activity) {
			this.activity = (Activity) ctx;
		}
	}

	public void checkAndShowAnnouncement() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
				
					String networkSource = getNetworkSource(context);
					String announcementUrl = networkSource.equals(Hook.NETWORK_SOURCE_VERCEL)
							? VERCEL_ANNOUNCEMENT_URL
							: GITHUB_ANNOUNCEMENT_URL;

					String jsonContent = downloadAnnouncementJson(announcementUrl);
					if (jsonContent == null || jsonContent.isEmpty()) {
						return;
					}

					JSONObject json = new JSONObject(jsonContent);
					announcementData = AnnouncementData.fromJSON(json);

					if (!announcementData.isValid()) {
						return;
					}

					if (isAnnouncementDismissed(context, announcementData.id)) {
						return;
					}

					if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showAnnouncement();
							}
						});
					}
				} catch (Exception e) {
				
					Log.e("AnnouncementManager", "加载公告失败: " + e.getMessage());
				}
			}
		}).start();
	}

	private String downloadAnnouncementJson(String urlString) {
		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
				reader.close();
				return result.toString();
			}
		} catch (Exception e) {
			Log.e("AnnouncementManager", "下载公告JSON失败: " + e.getMessage());
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				Log.e("AnnouncementManager", "关闭连接失败: " + e.getMessage());
			}
		}
		return null;
	}

	private void showAnnouncement() {
		if (announcementData == null || activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}

		switch (announcementData.displayMode) {
			case AnnouncementData.MODE_DIALOG_ONLY :
				showAnnouncementDialog();
				break;
			case AnnouncementData.MODE_TOAST_ONLY :
				showAnnouncementToast();
				break;
			case AnnouncementData.MODE_DIALOG_AND_TOAST :
				showAnnouncementDialog();
				showAnnouncementToast();
				break;
			case AnnouncementData.MODE_CELEBRATION :
				CelebrationDialog.show(activity, context, announcementData);
				break;
		}
	}

	private void showAnnouncementDialog() {
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}

		ScrollView scrollRoot = new ScrollView(activity);
		scrollRoot.setPadding(dp(activity, 16), dp(activity, 16), dp(activity, 16), dp(activity, 16));

		final LinearLayout root = new LinearLayout(activity);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setPadding(dp(activity, 24), dp(activity, 24), dp(activity, 24), dp(activity, 24));
		GradientDrawable bg = new GradientDrawable();
		bg.setColor(getBgColor());
		bg.setCornerRadius(dp(activity, 24));
		root.setBackground(bg);

		TextView title = new TextView(activity);
		title.setText("BetterVia");
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		title.setTextColor(getTitleColor());
		title.setTypeface(null, Typeface.BOLD);
		title.setGravity(Gravity.START);
		LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLp.bottomMargin = dp(activity, 8);
		root.addView(title, titleLp);

		String dialogTitle = announcementData.getLocalizedDialogTitle(context);
		if (dialogTitle != null && !dialogTitle.isEmpty()) {
			TextView subtitle = new TextView(activity);
			subtitle.setText(dialogTitle);
			subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			subtitle.setTextColor(getTextColor());
			subtitle.setTypeface(null, Typeface.BOLD);
			subtitle.setGravity(Gravity.START);
			LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			subtitleLp.bottomMargin = dp(activity, 16);
			root.addView(subtitle, subtitleLp);
		}

		String dialogSubtitle = announcementData.getLocalizedDialogSubtitle(context);
		TextView contentTitle = new TextView(activity);
		contentTitle.setText(dialogSubtitle != null && !dialogSubtitle.isEmpty() ? dialogSubtitle : "公告内容");
		contentTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		contentTitle.setTextColor(getHintColor());
		contentTitle.setTypeface(null, Typeface.BOLD);
		contentTitle.setGravity(Gravity.START);
		LinearLayout.LayoutParams contentTitleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		contentTitleLp.bottomMargin = dp(activity, 8);
		root.addView(contentTitle, contentTitleLp);

		LinearLayout contentContainer = new LinearLayout(activity);
		contentContainer.setOrientation(LinearLayout.VERTICAL);
		contentContainer.setPadding(dp(activity, 16), dp(activity, 16), dp(activity, 16), dp(activity, 16));
		GradientDrawable contentBg = new GradientDrawable();
		contentBg.setColor(getItemBgColor());
		contentBg.setCornerRadius(dp(activity, 12));
		contentContainer.setBackground(contentBg);

		LinearLayout.LayoutParams containerLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		containerLp.bottomMargin = dp(activity, 20);
		root.addView(contentContainer, containerLp);

		String content = announcementData.getLocalizedDialogContent(context);
		if (content != null && !content.isEmpty()) {
			TextView contentTv = new TextView(activity);
			contentTv.setText(formatContentWithLinks(content));
			contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			contentTv.setTextColor(getHintColor());
			contentTv.setLineSpacing(dp(activity, 4), 1.2f);
			contentTv.setGravity(Gravity.START);
			contentTv.setMovementMethod(LinkMovementMethod.getInstance());
			contentContainer.addView(contentTv);
		}

		if (announcementData.hasCheckbox) {
			LinearLayout checkboxLayout = new LinearLayout(activity);
			checkboxLayout.setOrientation(LinearLayout.HORIZONTAL);
			checkboxLayout.setGravity(Gravity.CENTER_VERTICAL);
			checkboxLayout.setPadding(0, 0, 0, dp(activity, 16));

			final CheckBox checkbox = new CheckBox(activity);
			checkboxLayout.addView(checkbox);

			TextView checkboxText = new TextView(activity);
			checkboxText.setText(announcementData.getLocalizedCheckboxText(context));
			checkboxText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			checkboxText.setTextColor(getTextColor());
			checkboxText.setPadding(dp(activity, 8), 0, 0, 0);
			checkboxLayout.addView(checkboxText);

			root.addView(checkboxLayout);
		}

		LinearLayout buttonLayout = new LinearLayout(activity);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER);

		final AlertDialog[] dialogRef = new AlertDialog[1];

		if (announcementData.hasNegativeButton) {
			Button negativeButton = new Button(activity);
			applyClickAnim(negativeButton);
			negativeButton.setText(announcementData.getLocalizedNegativeButton(context));
			negativeButton.setTextColor(getTitleColor());
			negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			negativeButton.setTypeface(null, Typeface.BOLD);
			GradientDrawable negativeBg = new GradientDrawable();
			negativeBg.setColor(getBtnBgColor());
			negativeBg.setCornerRadius(dp(activity, 12));
			negativeButton.setBackground(negativeBg);
			negativeButton.setPadding(dp(activity, 24), dp(activity, 12), dp(activity, 24), dp(activity, 12));

			LinearLayout.LayoutParams negativeLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
					1.0f);
			negativeLp.rightMargin = dp(activity, 8);
			buttonLayout.addView(negativeButton, negativeLp);

			negativeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				
					handleCheckboxIfPresent(root);
				
					executeButtonAction(announcementData.negativeButtonAction, announcementData.negativeButtonUrl);
				
					if (dialogRef[0] != null) {
						dialogRef[0].dismiss();
					}
				}
			});
		}

		if (announcementData.hasPositiveButton) {
			Button positiveButton = new Button(activity);
			applyClickAnim(positiveButton);
			positiveButton.setText(announcementData.getLocalizedPositiveButton(context));
			positiveButton.setTextColor(getOkBtnTextColor());
			positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			positiveButton.setTypeface(null, Typeface.BOLD);
			GradientDrawable positiveBg = new GradientDrawable();
			positiveBg.setColor(getOkBtnBgColor());
			positiveBg.setCornerRadius(dp(activity, 12));
			positiveButton.setBackground(positiveBg);
			positiveButton.setPadding(dp(activity, 24), dp(activity, 12), dp(activity, 24), dp(activity, 12));

			LinearLayout.LayoutParams positiveLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
					1.0f);
			if (announcementData.hasNegativeButton) {
				positiveLp.leftMargin = dp(activity, 8);
			}
			buttonLayout.addView(positiveButton, positiveLp);

			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				
					handleCheckboxIfPresent(root);
				
					executeButtonAction(announcementData.positiveButtonAction, announcementData.positiveButtonUrl);
				
					if (dialogRef[0] != null) {
						dialogRef[0].dismiss();
					}
				}
			});
		}

		root.addView(buttonLayout);
		scrollRoot.addView(root);

		dialogRef[0] = new AlertDialog.Builder(activity).setView(scrollRoot).setCancelable(false).create();
	
		Window win = dialogRef[0].getWindow();
		if (win != null) {
			win.setBackgroundDrawableResource(android.R.color.transparent);
			GradientDrawable round = new GradientDrawable();
			round.setColor(getBgColor());
			round.setCornerRadius(dp(activity, 24));
			win.setBackgroundDrawable(round);
			win.setGravity(Gravity.CENTER);
			win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		dialogRef[0].show();
	
		animateDialogEntrance(root, activity);
	}

	private void showAnnouncementToast() {
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}

		String toastText = announcementData.getLocalizedToastText(context);
		if (toastText == null || toastText.isEmpty()) {
			return;
		}

		boolean useCustomToast = Hook.getPrefBooleanStatic(context, "custom_toast", true);

		boolean isLongText = toastText.length() > 20;
		int duration = isLongText ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;

		if (useCustomToast) {
			Toast customToast = createCustomToast(activity, toastText, duration);
			if (customToast != null) {
				customToast.show();
			}
		} else {
			Toast.makeText(activity, toastText, duration).show();
		}
	}

	private Toast createCustomToast(Context ctx, String msg, int duration) {
		if (ctx == null || msg == null) {
			return null;
		}

		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setGravity(Gravity.CENTER);

		GradientDrawable bg = new GradientDrawable();
		bg.setColor(0xCC1E1E1E);
		bg.setCornerRadius(dp(ctx, 22));
		container.setBackgroundDrawable(bg);

		int padding = dp(ctx, 18);
		int verticalPadding = dp(ctx, 14);
		container.setPadding(padding, verticalPadding, padding, verticalPadding);

		TextView textView = new TextView(ctx);
		textView.setText(msg);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		textView.setGravity(Gravity.CENTER);
		textView.setMaxWidth(dp(ctx, 280));

		container.addView(textView);

		Toast toast = new Toast(ctx);
		toast.setView(container);
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM, 0, dp(ctx, 122));

		return toast;
	}

	private void handleCheckboxIfPresent(ViewGroup parent) {
		if (!announcementData.hasCheckbox) {
			return;
		}

		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof LinearLayout) {
				LinearLayout layout = (LinearLayout) child;
				for (int j = 0; j < layout.getChildCount(); j++) {
					View subChild = layout.getChildAt(j);
					if (subChild instanceof CheckBox) {
						CheckBox checkbox = (CheckBox) subChild;
						if (checkbox.isChecked()) {
						
							markAnnouncementAsDismissed(context, announcementData.id);
						}
						return;
					}
				}
			}
		}
	}

	private void executeButtonAction(int action, String url) {
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return;
		}

		switch (action) {
			case AnnouncementData.ACTION_NONE :
			
				break;
			case AnnouncementData.ACTION_OPEN_VIA :
			
				try {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("via://"));
					activity.startActivity(intent);
				} catch (Exception e) {
					Log.e("AnnouncementManager", "打开Via失败: " + e.getMessage());
				}
				break;
			case AnnouncementData.ACTION_EXIT_VIA :
			
				activity.finish();
				System.exit(0);
				break;
			case AnnouncementData.ACTION_SHARE :
			
				try {
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_TEXT, announcementData.getLocalizedDialogSubtitle(context));
					activity.startActivity(Intent.createChooser(shareIntent,
							LocalizedStringProvider.getInstance().get(context, "share")));
				} catch (Exception e) {
					Log.e("AnnouncementManager", "分享失败: " + e.getMessage());
				}
				break;
			case AnnouncementData.ACTION_OPEN_URL :
			
				if (url != null && !url.isEmpty()) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						activity.startActivity(intent);
					} catch (Exception e) {
						Log.e("AnnouncementManager", "打开链接失败: " + e.getMessage());
					}
				}
				break;
			case AnnouncementData.ACTION_DISMISS :
			default :
			
				break;
		}
	}

	private SpannableString formatContentWithLinks(String content) {
		SpannableString spannable = new SpannableString(content);

		String urlPattern = "https?://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*";
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(urlPattern);
		java.util.regex.Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			final String url = matcher.group();
			int start = matcher.start();
			int end = matcher.end();

			spannable.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						activity.startActivity(intent);
					} catch (Exception e) {
						Log.e("AnnouncementManager", "打开链接失败: " + e.getMessage());
					}
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setColor(getTitleColor());
					ds.setUnderlineText(true);
				}
			}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return spannable;
	}

	public static void markAnnouncementAsDismissed(Context ctx, String announcementId) {
		if (announcementId == null || announcementId.isEmpty()) {
			return;
		}

		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			Object editor = XposedHelpers.callMethod(sp, "edit");
			XposedHelpers.callMethod(editor, "putBoolean", KEY_ANNOUNCEMENT_DISMISSED_PREFIX + announcementId, true);
			XposedHelpers.callMethod(editor, "apply");
		} catch (Exception e) {
			Log.e("AnnouncementManager", "标记公告为已关闭失败: " + e.getMessage());
		}
	}

	public static boolean isAnnouncementDismissed(Context ctx, String announcementId) {
		if (announcementId == null || announcementId.isEmpty()) {
			return false;
		}

		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (Boolean) XposedHelpers.callMethod(sp, "getBoolean",
					KEY_ANNOUNCEMENT_DISMISSED_PREFIX + announcementId, false);
		} catch (Exception e) {
			Log.e("AnnouncementManager", "检查公告是否已关闭失败: " + e.getMessage());
			return false;
		}
	}

	private String getNetworkSource(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "network_source", Hook.DEFAULT_NETWORK_SOURCE);
		} catch (Exception e) {
			return Hook.DEFAULT_NETWORK_SOURCE;
		}
	}

	private int getBgColor() {
		return Hook.getBgColorStatic(context);
	}

	private int getTitleColor() {
		return Hook.getTitleColorStatic(context);
	}

	private int getTextColor() {
		return Hook.getTextColorStatic(context);
	}

	private int getHintColor() {
		return Hook.getHintColorStatic(context);
	}

	private int getBtnBgColor() {
		return Hook.getBtnBgColorStatic(context);
	}

	private int getOkBtnBgColor() {
		return Hook.getOkBtnBgColorStatic(context);
	}

	private int getOkBtnTextColor() {
		return Hook.getOkBtnTextColorStatic(context);
	}
	private int getItemBgColor() {
		return Hook.getItemBgColorStatic(context);
	}

	private void applyClickAnim(final View v) {
		if (v == null)
			return;

		v.animate().cancel();

		v.setClickable(true);
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
						return false;
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_CANCEL :
						v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
						return false;
				}
				return false;
			}
		});
	}

	private void animateDialogEntrance(final ViewGroup root, final Activity act) {
		if (root == null || act == null)
			return;

		final int childCount = root.getChildCount();
		final int baseDelay = 60;
		int delay = 0;

		if (childCount > 0) {
			final View maybeTitle = root.getChildAt(0);
			maybeTitle.setAlpha(0f);
			maybeTitle.setTranslationY(dp(act, 6));
			maybeTitle.setScaleX(0.98f);
			maybeTitle.setScaleY(0.98f);
			maybeTitle.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setStartDelay(delay).setDuration(320)
					.setInterpolator(new OvershootInterpolator(1.0f)).start();
			delay += baseDelay;
		}

		for (int i = 1; i < childCount; i++) {
			final View v = root.getChildAt(i);
			if (v == null)
				continue;
			v.setAlpha(0f);
			v.setTranslationY(dp(act, 10));
			v.animate().alpha(1f).translationY(0f).setStartDelay(delay).setDuration(220)
					.setInterpolator(new DecelerateInterpolator()).start();
			delay += baseDelay;
		}
	}

	private int dp(Context ctx, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
	}
}
