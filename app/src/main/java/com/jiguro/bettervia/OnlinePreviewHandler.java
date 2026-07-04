package com.jiguro.bettervia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class OnlinePreviewHandler {

	private static boolean isInitialized = false;

	private static final String KKFILEVIEW_URL = "https://file.kkview.cn/onlinePreview?url=";

	private static final String BASEMETAS_URL = "https://file.basemetas.cn/preview/view?data=";

	private static final List<View> previewOverlays = new ArrayList<View>();
	private static final List<Handler> dismissHandlerList = new ArrayList<Handler>();
	private static final int DISPLAY_DURATION = 8000;

	private static String lastDetectedUrl = "";

	private static String cooldownUrl = "";
	private static long cooldownUntil = 0;
	private static final long COOLDOWN_MS = 5000;

	private static final String[] EXTENSIONS_WORD = {".doc", ".docx"};
	private static final String[] EXTENSIONS_PPT = {".ppt", ".pptx"};
	private static final String[] EXTENSIONS_EXCEL = {".xls", ".xlsx"};
	private static final String[] EXTENSIONS_PDF = {".pdf"};

	private static final String[] LOCAL_HOSTS = {"127.0.0.1", "localhost", "0.0.0.0", "10.", "172.16.", "172.17.",
			"172.18.", "172.19.", "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.", "172.26.",
			"172.27.", "172.28.", "172.29.", "172.30.", "172.31.", "192.168."};

	private static final String PREF_NAME = "BetterVia";
	private static final String KEY_ENABLE = "online_preview_enable";
	private static final String KEY_WORD = "online_preview_word";
	private static final String KEY_PPT = "online_preview_ppt";
	private static final String KEY_EXCEL = "online_preview_excel";
	private static final String KEY_PDF = "online_preview_pdf";
	private static final String KEY_SOURCE = "online_preview_source";

	public static void init(final Context ctx, final ClassLoader cl) {
		if (isInitialized) {
			return;
		}
		isInitialized = true;

		Hook.bvLog("[BetterVia] 在线预览处理器初始化 (getUrl模式)");

		try {
		
			XposedHelpers.findAndHookMethod(WebView.class, "getUrl", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					if (!isEnabled(ctx)) {
						return;
					}

					String url = (String) param.getResult();
					if (url == null || url.isEmpty()) {
						return;
					}

					if (url.equals(cooldownUrl) && System.currentTimeMillis() < cooldownUntil) {
						return;
					}

					if (url.equals(lastDetectedUrl)) {
						return;
					}
					lastDetectedUrl = url;

					WebView webView = (WebView) param.thisObject;
					Activity act = null;
					Context webViewCtx = webView.getContext();
					if (webViewCtx instanceof Activity) {
						act = (Activity) webViewCtx;
					}

					checkUrlAndShowOverlay(ctx, act, url);
				}
			});

			Hook.bvLog("[BetterVia] 在线预览Hook安装成功 (getUrl模式)");
		} catch (Throwable t) {
			Hook.bvLog("[BetterVia] 安装在线预览Hook失败: " + t);
		}
	}

	private static boolean isEnabled(Context ctx) {
		try {
			SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
			return sp.getBoolean(KEY_ENABLE, false);
		} catch (Exception e) {
			return false;
		}
	}

	private static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	private static int getPreviewSource(Context ctx) {
		try {
			SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
			return sp.getInt(KEY_SOURCE, 0);
		} catch (Exception e) {
			return 0;
		}
	}

	private static String buildPreviewUrl(String fileUrl, int source) {
		try {
			if (source == 0) {
			
				String encoded = Base64.encodeToString(fileUrl.getBytes("UTF-8"), Base64.NO_WRAP);
				return KKFILEVIEW_URL + encoded + "&key=000";
			} else {
			
				String jsonStr = "{\"url\":\"" + fileUrl + "\",\"displayName\":\"远程文件\"}";
				String encoded = Base64.encodeToString(jsonStr.getBytes("UTF-8"), Base64.NO_WRAP);
				return BASEMETAS_URL + encoded;
			}
		} catch (Exception e) {
			Hook.bvLog("[BetterVia] 构建预览URL失败: " + e.getMessage());
		
			try {
				String encoded = Base64.encodeToString(fileUrl.getBytes("UTF-8"), Base64.NO_WRAP);
				return KKFILEVIEW_URL + encoded + "&key=000";
			} catch (Exception e2) {
				return null;
			}
		}
	}

	private static void checkUrlAndShowOverlay(final Context ctx, final Activity act, final String url) {
	
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			return;
		}

		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			if (host != null) {
				String hostLower = host.toLowerCase();
				for (String prefix : LOCAL_HOSTS) {
					if (hostLower.startsWith(prefix)) {
						return;
					}
				}
			}
		} catch (Exception e) {
			return;
		}

		String urlLower = url.toLowerCase();
		SharedPreferences prefs = getPrefs(ctx);
		String matchedType = null;

		if (prefs.getBoolean(KEY_WORD, false) && endsWithAny(urlLower, EXTENSIONS_WORD)) {
			matchedType = "word";
		} else if (prefs.getBoolean(KEY_PPT, false) && endsWithAny(urlLower, EXTENSIONS_PPT)) {
			matchedType = "ppt";
		} else if (prefs.getBoolean(KEY_EXCEL, false) && endsWithAny(urlLower, EXTENSIONS_EXCEL)) {
			matchedType = "excel";
		} else if (prefs.getBoolean(KEY_PDF, false) && endsWithAny(urlLower, EXTENSIONS_PDF)) {
			matchedType = "pdf";
		}

		if (matchedType != null) {
			showOverlay(ctx, act, url, matchedType);
		}
	}

	private static boolean endsWithAny(String str, String[] suffixes) {
		for (String suffix : suffixes) {
			if (str.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	private static String getPreviewTitle(Context ctx, String fileType) {
		if ("word".equals(fileType)) {
			return LocalizedStringProvider.getInstance().get(ctx, "online_preview_word_title");
		} else if ("ppt".equals(fileType)) {
			return LocalizedStringProvider.getInstance().get(ctx, "online_preview_ppt_title");
		} else if ("excel".equals(fileType)) {
			return LocalizedStringProvider.getInstance().get(ctx, "online_preview_excel_title");
		} else if ("pdf".equals(fileType)) {
			return LocalizedStringProvider.getInstance().get(ctx, "online_preview_pdf_title");
		}
		return LocalizedStringProvider.getInstance().get(ctx, "online_preview_title");
	}

	private static void showOverlay(final Context ctx, final Activity activity, final String url,
			final String fileType) {
		if (ctx == null || activity == null || url == null) {
			return;
		}

		final Activity act = activity;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed()) {
					return;
				}

				try {
					final FrameLayout overlay = new FrameLayout(act);
					overlay.setBackgroundColor(0xCC000000);

					TextView titleView = new TextView(act);
					titleView.setText(LocalizedStringProvider.getInstance().get(ctx, "online_preview_detected"));
					titleView.setTextColor(Color.WHITE);
					titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					titleView.setTypeface(null, android.graphics.Typeface.BOLD);
					titleView.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 8));

					final TextView textView = new TextView(act);
					textView.setText(url);
					textView.setTextColor(Color.WHITE);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					textView.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 8));
					textView.setMaxWidth(dp(act, 320));
					textView.setEllipsize(TextUtils.TruncateAt.END);
					textView.setSingleLine(true);

					final TextView loadingHint = new TextView(act);
					loadingHint.setText(LocalizedStringProvider.getInstance().get(ctx, "online_preview_loading"));
					loadingHint.setTextColor(Color.GREEN);
					loadingHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
					loadingHint.setPadding(dp(act, 16), dp(act, 8), dp(act, 16), dp(act, 12));
					loadingHint.setVisibility(View.GONE);

					GradientDrawable bg = new GradientDrawable();
					bg.setColor(0xCC000000);
					bg.setCornerRadius(dp(act, 12));
					overlay.setBackgroundDrawable(bg);

					LinearLayout layout = new LinearLayout(act);
					layout.setOrientation(LinearLayout.VERTICAL);
					layout.addView(titleView);
					layout.addView(textView);
					layout.addView(loadingHint);
					overlay.addView(layout);

					overlay.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							loadingHint.setVisibility(View.VISIBLE);
							cancelAutoDismiss(overlay);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									removeOverlay(overlay);
									startProcess(act, ctx, url, fileType);
								}
							}, 500);
						}
					});

					int currentOffset;
					synchronized (previewOverlays) {
						currentOffset = previewOverlays.size() * dp(act, 110);
						previewOverlays.add(overlay);
					}

					int marginTop = dp(act, 16) + currentOffset;
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(act, 320),
							ViewGroup.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
					params.setMargins(dp(act, 16), marginTop, dp(act, 16), 0);

					ViewGroup decorView = (ViewGroup) act.getWindow().getDecorView();
					decorView.addView(overlay, params);

					TranslateAnimation slideIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
							Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
							Animation.RELATIVE_TO_PARENT, 0.0f);
					slideIn.setDuration(300);
					slideIn.setInterpolator(new DecelerateInterpolator());
					overlay.startAnimation(slideIn);

					final Handler dismissHandler = new Handler(Looper.getMainLooper());
					synchronized (dismissHandlerList) {
						dismissHandlerList.add(dismissHandler);
					}

					dismissHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							TranslateAnimation slideOut = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
									Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
									Animation.RELATIVE_TO_PARENT, 0.0f);
							slideOut.setDuration(300);
							slideOut.setInterpolator(new AccelerateInterpolator());
							slideOut.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									removeOverlay(overlay);
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});
							overlay.startAnimation(slideOut);
						}
					}, DISPLAY_DURATION);

					Hook.bvLog("[BetterVia] 已显示在线预览浮窗: " + url);
				} catch (Exception e) {
					Hook.bvLog("[BetterVia] 显示在线预览浮窗失败: " + e);
				}
			}
		});
	}

	private static void removeOverlay(View overlay) {
		try {
			ViewGroup parent = (ViewGroup) overlay.getParent();
			if (parent != null) {
				parent.removeView(overlay);
			}
		} catch (Exception e) {
		
		}
		synchronized (previewOverlays) {
			previewOverlays.remove(overlay);
		}
	}

	private static void cancelAutoDismiss(View overlay) {
		synchronized (dismissHandlerList) {
			for (Handler h : dismissHandlerList) {
				h.removeCallbacksAndMessages(null);
			}
			dismissHandlerList.clear();
		}
	}

	private static void startProcess(final Activity act, final Context ctx, final String fileUrl,
			final String fileType) {
		if (act == null || act.isFinishing() || act.isDestroyed()) {
			return;
		}

		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Dialog dialog = new Dialog(act, android.R.style.Theme_NoTitleBar_Fullscreen);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(false);

				final int bgColor = Hook.getBgColorStatic(act);
				final int textColor = Hook.getTextColorStatic(act);
				final int hintColor = Hook.getHintColorStatic(act);
				final int itemBgColor = Hook.getItemBgColorStatic(act);
				final String dialogTitle = getPreviewTitle(act, fileType);

				LinearLayout rootLayout = new LinearLayout(act);
				rootLayout.setOrientation(LinearLayout.VERTICAL);
				rootLayout.setBackgroundColor(bgColor);

				RelativeLayout titleBar = new RelativeLayout(act);
				titleBar.setBackgroundColor(itemBgColor);
				titleBar.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));

				ImageButton backButton = new ImageButton(act);
				backButton.setImageResource(android.R.drawable.ic_menu_revert);
				backButton.setBackgroundResource(android.R.color.transparent);
				backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				backButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				backButton.setColorFilter(textColor);
				RelativeLayout.LayoutParams backLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
				backLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				backLp.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(backButton, backLp);

				TextView titleText = new TextView(act);
				titleText.setText(dialogTitle);
				titleText.setTextColor(textColor);
				titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				titleText.setTypeface(null, android.graphics.Typeface.BOLD);
				titleText.setEllipsize(TextUtils.TruncateAt.END);
				titleText.setSingleLine(true);
				RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				titleLp.addRule(RelativeLayout.CENTER_IN_PARENT);
				titleBar.addView(titleText, titleLp);

				ImageButton refreshButton = new ImageButton(act);
				refreshButton.setImageResource(android.R.drawable.ic_menu_rotate);
				refreshButton.setBackgroundResource(android.R.color.transparent);
				refreshButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				refreshButton.setPadding(dp(act, 8), dp(act, 8), dp(act, 8), dp(act, 8));
				refreshButton.setColorFilter(textColor);
				RelativeLayout.LayoutParams refreshLp = new RelativeLayout.LayoutParams(dp(act, 48), dp(act, 48));
				refreshLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				refreshLp.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(refreshButton, refreshLp);

				rootLayout.addView(titleBar);

				final FrameLayout webviewContainer = new FrameLayout(act);
				final WebView webView = new WebView(act);
				WebSettings settings = webView.getSettings();
				settings.setJavaScriptEnabled(true);
				settings.setLoadsImagesAutomatically(true);
				settings.setBlockNetworkImage(false);
				settings.setLoadWithOverviewMode(true);
				settings.setUseWideViewPort(true);
				settings.setDomStorageEnabled(true);
				settings.setBuiltInZoomControls(true);
				settings.setDisplayZoomControls(false);
				settings.setAllowFileAccess(false);
				settings.setAllowContentAccess(false);
				settings.setCacheMode(WebSettings.LOAD_DEFAULT);
				settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
				String ua = settings.getUserAgentString();
				if (ua != null && !ua.contains("Chrome")) {
					settings.setUserAgentString(ua + " Chrome/120.0.6099.144");
				}
				webviewContainer.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));

				final FrameLayout loadingOverlay = new FrameLayout(act);
				loadingOverlay.setBackgroundColor(bgColor);

				LinearLayout loadingContent = new LinearLayout(act);
				final LinearLayout loadingContentFinal = loadingContent;
				loadingContent.setOrientation(LinearLayout.VERTICAL);
				loadingContent.setGravity(Gravity.CENTER);
				loadingContent.setPadding(dp(act, 40), dp(act, 40), dp(act, 40), dp(act, 40));

				ProgressBar spinner = new ProgressBar(act);
				final ProgressBar spinnerFinal = spinner;
				LinearLayout.LayoutParams spinnerLp = new LinearLayout.LayoutParams(dp(act, 64), dp(act, 64));
				spinnerLp.gravity = Gravity.CENTER;
				spinnerLp.bottomMargin = dp(act, 24);
				loadingContent.addView(spinner, spinnerLp);

				final TextView overlayLabel = new TextView(act);
				overlayLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "online_preview_preparing"));
				overlayLabel.setTextColor(textColor);
				overlayLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				overlayLabel.setGravity(Gravity.CENTER);
				loadingContent.addView(overlayLabel);

				final TextView overlayPercent = new TextView(act);
				overlayPercent.setText("0%");
				overlayPercent.setTextColor(hintColor);
				overlayPercent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				overlayPercent.setGravity(Gravity.CENTER);
				overlayPercent.setPadding(0, dp(act, 8), 0, 0);
				overlayPercent.setTag("percent_text");
				loadingContent.addView(overlayPercent);

				loadingOverlay.addView(loadingContent, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				webviewContainer.addView(loadingOverlay, new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

				final ProgressBar progressBar = new ProgressBar(act, null, android.R.attr.progressBarStyleHorizontal);
				progressBar.setMax(100);
				progressBar.setProgress(0);
				progressBar.setVisibility(View.GONE);
				FrameLayout.LayoutParams ppLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						dp(act, 3));
				ppLp.gravity = Gravity.TOP;
				webviewContainer.addView(progressBar, ppLp);

				rootLayout.addView(webviewContainer,
						new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

				dialog.setContentView(rootLayout);
				Window win = dialog.getWindow();
				if (win != null) {
					win.setBackgroundDrawableResource(android.R.color.transparent);
					win.setGravity(Gravity.CENTER);
					win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					}
					win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				}
				dialog.show();

				final class OverlayUpdater {
					void update(int pct) {
						findPercentTextView(loadingOverlay, pct);
					}
				}
				final OverlayUpdater overlayUpdater = new OverlayUpdater();

				final boolean[] hasError = new boolean[1];
				webView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						return false;
					}

					@Override
					public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
						super.onReceivedError(view, request, error);
						if (request != null && request.isForMainFrame()) {
							hasError[0] = true;
							webView.setVisibility(View.INVISIBLE);
							showErrorOnOverlay(loadingOverlay, progressBar, loadingContentFinal, spinnerFinal,
									overlayLabel, overlayPercent, act,
									Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
											? error.getDescription().toString()
											: LocalizedStringProvider.getInstance().get(act,
													"online_preview_error_network"));
							dialog.setCancelable(true);
						}
					}

					@Override
					public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
						super.onReceivedError(view, errorCode, description, failingUrl);
						hasError[0] = true;
						webView.setVisibility(View.INVISIBLE);
						showErrorOnOverlay(loadingOverlay, progressBar, loadingContentFinal, spinnerFinal, overlayLabel,
								overlayPercent, act, description);
						dialog.setCancelable(true);
					}

					@Override
					public void onReceivedHttpError(WebView view, WebResourceRequest request,
							WebResourceResponse errorResponse) {
						super.onReceivedHttpError(view, request, errorResponse);
						if (request != null && request.isForMainFrame()) {
							hasError[0] = true;
							webView.setVisibility(View.INVISIBLE);
							int statusCode = errorResponse != null ? errorResponse.getStatusCode() : 0;
							showErrorOnOverlay(loadingOverlay, progressBar, loadingContentFinal, spinnerFinal,
									overlayLabel, overlayPercent, act, "HTTP " + statusCode + " - "
											+ (errorResponse != null ? errorResponse.getReasonPhrase() : ""));
							dialog.setCancelable(true);
						}
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						if (!hasError[0]) {
							loadingOverlay.animate().alpha(0f).setDuration(300).start();
							dialog.setCancelable(true);
						}
					}
				});
				webView.setWebChromeClient(new WebChromeClient() {
					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						if (newProgress < 100) {
							progressBar.setVisibility(View.VISIBLE);
							progressBar.setProgress(newProgress);
						} else {
							progressBar.setVisibility(View.GONE);
						}
						overlayUpdater.update(newProgress);
					}
				});

				backButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				refreshButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hasError[0] = false;
						webView.setVisibility(View.VISIBLE);
						webView.reload();
					}
				});

				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface d) {
						cooldownUrl = fileUrl;
						cooldownUntil = System.currentTimeMillis() + COOLDOWN_MS;
					}
				});

				int source = getPreviewSource(act);
				final String previewUrl = buildPreviewUrl(fileUrl, source);

				if (previewUrl != null) {
					webView.loadUrl(previewUrl);
				} else {
					hasError[0] = true;
					webView.setVisibility(View.INVISIBLE);
					showErrorOnOverlay(loadingOverlay, progressBar, loadingContentFinal, spinnerFinal, overlayLabel,
							overlayPercent, act,
							LocalizedStringProvider.getInstance().get(act, "online_preview_error_parse"));
					dialog.setCancelable(true);
				}
			}
		});
	}

	private static void findPercentTextView(View view, int percent) {
		if (view instanceof TextView) {
			TextView tv = (TextView) view;
			Object tag = tv.getTag();
			if ("percent_text".equals(tag)) {
				tv.setText(percent + "%");
				return;
			}
		}
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				findPercentTextView(group.getChildAt(i), percent);
			}
		}
	}

	private static void showErrorOnOverlay(final FrameLayout loadingOverlay, final ProgressBar progressBar,
			final LinearLayout loadingContent, final ProgressBar spinner, final TextView overlayLabel,
			final TextView overlayPercent, final Activity act, final String errorMessage) {
		progressBar.setVisibility(View.GONE);
		spinner.setVisibility(View.GONE);
		overlayPercent.setVisibility(View.GONE);
		loadingContent.removeAllViews();

		final int textColor = Hook.getTextColorStatic(act);
		final int hintColor = Hook.getHintColorStatic(act);

		ImageView errorIcon = new ImageView(act);
		int iconSize = dp(act, 48);
		LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSize, iconSize);
		iconLp.gravity = Gravity.CENTER;
		iconLp.bottomMargin = dp(act, 16);
		errorIcon.setImageResource(android.R.drawable.ic_dialog_alert);
		errorIcon.setColorFilter(0xFFFF5252, PorterDuff.Mode.SRC_IN);
		errorIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		loadingContent.addView(errorIcon, iconLp);

		TextView errorTitle = new TextView(act);
		errorTitle.setText(LocalizedStringProvider.getInstance().get(act, "online_preview_error_title"));
		errorTitle.setTextColor(textColor);
		errorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		errorTitle.setTypeface(null, android.graphics.Typeface.BOLD);
		errorTitle.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLp.bottomMargin = dp(act, 12);
		loadingContent.addView(errorTitle, titleLp);

		TextView errorDetail = new TextView(act);
		errorDetail.setText(errorMessage);
		errorDetail.setTextColor(hintColor);
		errorDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		errorDetail.setGravity(Gravity.CENTER);
		errorDetail.setLineSpacing(dp(act, 3), 1.1f);
		errorDetail.setMaxWidth(dp(act, 280));
		LinearLayout.LayoutParams detailLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		detailLp.gravity = Gravity.CENTER;
		detailLp.bottomMargin = dp(act, 24);
		loadingContent.addView(errorDetail, detailLp);

		TextView retryHint = new TextView(act);
		retryHint.setText(LocalizedStringProvider.getInstance().get(act, "online_preview_retry_hint"));
		retryHint.setTextColor(hintColor);
		retryHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		retryHint.setGravity(Gravity.CENTER);
		retryHint.setAlpha(0.6f);
		LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		loadingContent.addView(retryHint, hintLp);

		loadingOverlay.setAlpha(1.0f);
		loadingOverlay.setVisibility(View.VISIBLE);
	}

	private static int dp(Context ctx, int dp) {
		float density = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}
}
