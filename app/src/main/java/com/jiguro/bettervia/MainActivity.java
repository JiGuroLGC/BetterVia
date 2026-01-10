package com.jiguro.bettervia;
import android.*;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import org.json.*;
import java.lang.Process;
public class MainActivity extends Activity {
	private static final String PKG_VIA = "mark.via";
	private static final String PKG_VIAGP = "mark.via.gp";
	public static final String SP_NAME = "module_sp";
	private static final String KEY_AGREED = "user_agreed";
	private static final String KEY_LANGUAGE = "agreement_language";
	private static final String TAG = "BetterVia";
	private static final String KEY_LANGUAGE_SELECTED = "language_selected";
	private static final int LANGUAGE_AUTO = -1;
	private static final int LANGUAGE_SIMPLIFIED_CHINESE = 0;
	private static final int LANGUAGE_TRADITIONAL_CHINESE = 1;
	private static final int LANGUAGE_ENGLISH = 2;
	private TextView moduleStatusText;
	private WebView flowWebView;
	private FrameLayout rootContainer;
	private boolean useWebViewBackground = true;
	private boolean isIconHidden = false;
	private static final String ALIAS_ACTIVITY_NAME = "com.jiguro.bettervia.LauncherAlias";
	private static final String KEY_AUTO_UPDATE = "auto_update";
	private static final String KEY_UPDATE_SOURCE = "update_source";
	private static final int UPDATE_SOURCE_GITHUB = 0;
	private static final int UPDATE_SOURCE_GITEE = 1;
	private static final String GITHUB_UPDATE_URL = "https:
	private static final String GITEE_UPDATE_URL = "https:
	private static final int REQUEST_STORAGE_PERMISSION_FOR_FIX = 1002;
	private static final String EXPECTED_PACKAGE_NAME = "com.jiguro.bettervia";
	private static final int EXPECTED_VERSION_CODE = 20260111;
	private static final String EXPECTED_VERSION_NAME = "1.4.0";
	private TextView appNameText;
	private TextView byAuthorText;
	private TextView blogLinkText;
	private TextView emailLinkText;
	private TextView versionText;
	private TextView copyrightText;
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(updateBaseContextForLanguage(newBase));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isLanguageSelected()) {
			showLanguageSelectionDialog();
			return;
		}
		if (!isUserAgreed()) {
			showAgreementDialog();
			return;
		}
		initializeMainUI();
		if (isAutoUpdateEnabled()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					checkUpdate();
				}
			}).start();
		}
	}
	private boolean isLanguageSelected() {
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		return sp.getBoolean(KEY_LANGUAGE_SELECTED, false);
	}
	private void showLanguageSelectionDialog() {
		String dialogTitle;
		try {
			dialogTitle = getString(R.string.select_language);
		} catch (Resources.NotFoundException e) {
			dialogTitle = "Select Language";
		}
		final String[] languages = {getString(R.string.auto_select), getString(R.string.simplified_chinese),
				getString(R.string.traditional_chinese), getString(R.string.english)};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(dialogTitle).setItems(languages, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor editor = getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
				if (which == 0) {
					editor.putInt(KEY_LANGUAGE, LANGUAGE_AUTO);
				} else {
					editor.putInt(KEY_LANGUAGE, which - 1);
				}
				editor.putBoolean(KEY_LANGUAGE_SELECTED, true);
				editor.apply();
				restartActivityForLanguage();
			}
		}).setCancelable(false).show();
	}
	private Context updateBaseContextForLanguage(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
		int language = sp.getInt(KEY_LANGUAGE, LANGUAGE_AUTO);
		Locale selectedLocale = getSelectedLocale(context, language);
		Configuration config = context.getResources().getConfiguration();
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			config.setLocale(selectedLocale);
			return context.createConfigurationContext(config);
		} else {
			config.locale = selectedLocale;
			context.getResources().updateConfiguration(config, dm);
			return context;
		}
	}
	private Locale getSelectedLocale(Context context, int language) {
		switch (language) {
			case LANGUAGE_SIMPLIFIED_CHINESE :
				return Locale.SIMPLIFIED_CHINESE;
			case LANGUAGE_TRADITIONAL_CHINESE :
				return Locale.TRADITIONAL_CHINESE;
			case LANGUAGE_ENGLISH :
				return Locale.ENGLISH;
			case LANGUAGE_AUTO :
			default :
				return getSystemLanguageLocale(context);
		}
	}
	private Locale getSystemLanguageLocale(Context context) {
		Locale systemLocale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			systemLocale = context.getResources().getConfiguration().getLocales().get(0);
		} else {
			systemLocale = context.getResources().getConfiguration().locale;
		}
		String language = systemLocale.getLanguage();
		String country = systemLocale.getCountry();
		if (language.startsWith("zh")) {
			if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
				return Locale.TRADITIONAL_CHINESE;
			} else {
				return Locale.SIMPLIFIED_CHINESE;
			}
		} else {
			return Locale.ENGLISH;
		}
	}
	private void restartActivityForLanguage() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	private void showAgreementDialog() {
		final Dialog agreementDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		agreementDialog.setContentView(R.layout.dialog_agreement);
		agreementDialog.setCancelable(false);
		final TextView titleText = agreementDialog.findViewById(R.id.title_text);
		final TextView contentText = agreementDialog.findViewById(R.id.agreement_content);
		final ScrollView scrollView = agreementDialog.findViewById(R.id.agreement_scrollview);
		Button agreeButton = agreementDialog.findViewById(R.id.agree_button);
		Button disagreeButton = agreementDialog.findViewById(R.id.disagree_button);
		String agreementTitle = getString(R.string.agreement_title);
		String agreementContent = getString(R.string.agreement_content);
		String agreeButtonText = getString(R.string.agree_button);
		String disagreeButtonText = getString(R.string.disagree_button);
		titleText.setText(agreementTitle);
		contentText.setText(agreementContent);
		agreeButton.setText(agreeButtonText);
		disagreeButton.setText(disagreeButtonText);
		agreementDialog.show();
		titleText.post(new Runnable() {
			@Override
			public void run() {
				ensureTitleVisibility(titleText, scrollView, agreementDialog);
			}
		});
		agreeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
				editor.putBoolean(KEY_AGREED, true);
				editor.apply();
				agreementDialog.dismiss();
				restartActivity();
			}
		});
		disagreeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String toastMessage = getString(R.string.toast_agreement_required);
				Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
				agreementDialog.dismiss();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, 2000);
			}
		});
		animateDialogEntrance(agreementDialog);
	}
	private void restartActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	private void ensureTitleVisibility(TextView titleText, final ScrollView scrollView, Dialog dialog) {
		int[] titleLocation = new int[2];
		int[] scrollLocation = new int[2];
		titleText.getLocationOnScreen(titleLocation);
		scrollView.getLocationOnScreen(scrollLocation);
		int titleBottom = titleLocation[1] + titleText.getHeight();
		int scrollTop = scrollLocation[1];
		if (scrollTop < titleBottom) {
			adjustLayoutToProtectTitle(titleText, scrollView, dialog);
		}
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, 0);
			}
		});
		animateTitleProtection(titleText);
	}
	private void adjustLayoutToProtectTitle(TextView titleText, ScrollView scrollView, Dialog dialog) {
		View titleContainer = dialog.findViewById(R.id.title_container);
		ViewGroup.LayoutParams titleParams = titleContainer.getLayoutParams();
		titleParams.height = (int) (titleParams.height * 1.2f);
		titleContainer.setLayoutParams(titleParams);
		ViewGroup.MarginLayoutParams scrollParams = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
		scrollParams.topMargin = (int) (scrollParams.topMargin * 1.5f);
		scrollView.setLayoutParams(scrollParams);
		titleContainer.requestLayout();
		scrollView.requestLayout();
	}
	private void animateTitleProtection(TextView titleText) {
		titleText.setAlpha(0f);
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f);
		ObjectAnimator scaleUp = ObjectAnimator.ofFloat(titleText, "scaleX", 0.95f, 1f);
		ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(titleText, "scaleY", 0.95f, 1f);
		animatorSet.playTogether(fadeIn, scaleUp, scaleUpY);
		animatorSet.setDuration(400);
		animatorSet.setInterpolator(new OvershootInterpolator());
		animatorSet.start();
	}
	private void animateDialogEntrance(Dialog dialog) {
		View dialogView = dialog.getWindow().getDecorView();
		dialogView.setAlpha(0f);
		dialogView.setScaleX(0.9f);
		dialogView.setScaleY(0.9f);
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(dialogView, "alpha", 0f, 1f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(dialogView, "scaleX", 0.9f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(dialogView, "scaleY", 0.9f, 1f);
		animatorSet.playTogether(fadeIn, scaleX, scaleY);
		animatorSet.setDuration(300);
		animatorSet.setInterpolator(new DecelerateInterpolator());
		animatorSet.start();
	}
	private void initializeMainUI() {
		setContentView(R.layout.main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
			int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
			getWindow().getDecorView().setSystemUiVisibility(flags);
		}
		if (getActionBar() != null) {
			getActionBar().hide();
		}
		checkIconState();
		rootContainer = (FrameLayout) findViewById(R.id.root_container);
		flowWebView = (WebView) findViewById(R.id.flow_webview);
		checkAndSetupBackground();
		initViews();
		startEntranceAnimations();
	}
	private void checkAndSetupBackground() {
		if (isWebViewSupported() && useWebViewBackground) {
			setupWebViewBackground();
		} else {
			useWebViewBackground = false;
			setupFallbackBackground();
			Log.w(TAG, "WebView背景不支持，使用回退方案");
		}
	}
	private boolean isLauncherIconVisible() {
		try {
			ComponentName component = new ComponentName(this, ALIAS_ACTIVITY_NAME);
			PackageManager manager = getPackageManager();
			Intent intent = new Intent().setComponent(component);
			List<ResolveInfo> list;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				list = manager.queryIntentActivities(intent,
						PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY));
			} else {
				list = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			}
			return list != null && !list.isEmpty();
		} catch (Exception e) {
			Log.e(TAG, "检测图标状态异常: " + e.getMessage());
			return true;
		}
	}
	public void setLauncherIconVisible(boolean visible) {
		if (isLauncherIconVisible() == visible) {
			Log.d(TAG, "状态相同，无需操作");
			return;
		}
		ComponentName component = new ComponentName(this, ALIAS_ACTIVITY_NAME);
		PackageManager manager = getPackageManager();
		int newState = visible
				? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
				: PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		try {
			manager.setComponentEnabledSetting(component, newState, PackageManager.DONT_KILL_APP);
			isIconHidden = !visible;
			boolean success = (isLauncherIconVisible() == visible);
			Log.d(TAG, "操作结果: " + (success ? "成功" : "失败"));
			refreshLauncher();
			updateMenuState();
			String message = visible ? getString(R.string.toast_icon_shown) : getString(R.string.toast_icon_hidden);
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Log.e(TAG, "权限不足: " + e.getMessage());
			Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG, "设置失败: " + e.getMessage());
			Toast.makeText(this, R.string.toast_operation_failed, Toast.LENGTH_SHORT).show();
		}
	}
	private void updateMenuState() {
		ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
		if (menuButton != null) {
			Log.d(TAG, "菜单状态已更新，当前图标状态: " + (isIconHidden ? "已隐藏" : "显示"));
		}
	}
	private void checkIconState() {
		ComponentName aliasComponent = new ComponentName(this, ALIAS_ACTIVITY_NAME);
		PackageManager manager = getPackageManager();
		int currentState = manager.getComponentEnabledSetting(aliasComponent);
		isIconHidden = (currentState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
		Log.d(TAG, "当前图标状态: " + (isIconHidden ? "已隐藏" : "显示"));
	}
	private void refreshLauncher() {
		try {
			String[] actions = {"com.android.launcher3.action.REFRESH_LAUNCHER", "miui.intent.action.REFRESH_LAUNCHER",
					"com.huawei.android.launcher.action.REFRESH_LAUNCHER",
					"com.sec.android.app.launcher.action.REFRESH_LAUNCHER",
					"com.oppo.launcher.action.REFRESH_LAUNCHER"};
			for (String action : actions) {
				try {
					Intent intent = new Intent(action);
					sendBroadcast(intent);
					Log.d(TAG, "发送广播: " + action);
				} catch (Exception e) {
					Log.w(TAG, "广播发送失败: " + action);
				}
			}
			Intent stdIntent = new Intent(Intent.ACTION_MAIN);
			stdIntent.addCategory(Intent.CATEGORY_HOME);
			sendBroadcast(stdIntent);
		} catch (Exception e) {
			Log.e(TAG, "刷新桌面异常: " + e.getMessage());
		}
	}
	private boolean isWebViewSupported() {
		try {
			WebView testView = new WebView(this);
			testView.destroy();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "WebView不支持: " + e.getMessage());
			return false;
		}
	}
	private void setupWebViewBackground() {
		try {
			flowWebView.setBackgroundColor(Color.TRANSPARENT);
			flowWebView.setVerticalScrollBarEnabled(false);
			flowWebView.setHorizontalScrollBarEnabled(false);
			flowWebView.getSettings().setLoadWithOverviewMode(true);
			flowWebView.getSettings().setUseWideViewPort(true);
			flowWebView.getSettings().setJavaScriptEnabled(true);
			flowWebView.getSettings().setDomStorageEnabled(true);
			flowWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
			flowWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return true;
				}
			});
			flowWebView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			flowWebView.loadUrl("file:
			flowWebView.setVisibility(View.VISIBLE);
			Log.d(TAG, "WebView背景设置成功");
		} catch (Exception e) {
			Log.e(TAG, "WebView背景设置失败: " + e.getMessage());
			useWebViewBackground = false;
			setupFallbackBackground();
		}
	}
	private void setupFallbackBackground() {
		rootContainer.setBackgroundColor(Color.WHITE);
		flowWebView.setVisibility(View.GONE);
	}
	private void initViews() {
		moduleStatusText = (TextView) findViewById(R.id.module_status);
		appNameText = (TextView) findViewById(R.id.app_name);
		byAuthorText = (TextView) findViewById(R.id.by_author);
		blogLinkText = (TextView) findViewById(R.id.blog_link);
		emailLinkText = (TextView) findViewById(R.id.email_link);
		versionText = (TextView) findViewById(R.id.version_text);
		copyrightText = (TextView) findViewById(R.id.copyright_text);
		updateAllTexts();
		if (blogLinkText != null) {
			blogLinkText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openBlog();
				}
			});
		}
		if (emailLinkText != null) {
			emailLinkText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendEmail();
				}
			});
		}
		ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
		if (menuButton != null) {
			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showPopupMenu(v);
				}
			});
		}
		setupIconClickEvents();
		setInitialViewStates();
	}
	private void updateAllTexts() {
		updateModuleStatus();
		if (appNameText != null) {
			appNameText.setText(getString(R.string.app_name));
		}
		if (byAuthorText != null) {
			byAuthorText.setText(getString(R.string.by_author));
		}
		if (blogLinkText != null) {
			blogLinkText.setText(getString(R.string.blog_link));
		}
		if (emailLinkText != null) {
			emailLinkText.setText(getString(R.string.contact_email));
		}
		if (versionText != null) {
			versionText.setText(getString(R.string.version_info));
		}
		if (copyrightText != null) {
			copyrightText.setText(getString(R.string.copyright_info));
		}
		ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
		if (menuButton != null) {
			menuButton.setContentDescription(getString(R.string.menu));
		}
	}
	private void updateModuleStatus() {
		if (moduleStatusText != null) {
			moduleStatusText.setText(getString(R.string.module_status));
			if (isActivated()) {
				moduleStatusText.setText(getString(R.string.activated));
				moduleStatusText.setTextColor(0xFF4CAF50);
				animateActivatedStatus();
			} else {
				moduleStatusText.setText(getString(R.string.not_activated));
				moduleStatusText.setTextColor(0xFFF44336);
			}
		}
	}
	private void setupIconClickEvents() {
		ImageView githubIcon = (ImageView) findViewById(R.id.github_icon);
		if (githubIcon != null) {
			githubIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openGitHub();
				}
			});
		}
		ImageView giteeIcon = (ImageView) findViewById(R.id.gitee_icon);
		if (giteeIcon != null) {
			giteeIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openGitee();
				}
			});
		}
		ImageView xposedIcon = (ImageView) findViewById(R.id.xposed_icon);
		if (xposedIcon != null) {
			xposedIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openXposedRepo();
				}
			});
		}
	}
	private void setInitialViewStates() {
		View customActionBar = findViewById(R.id.custom_actionbar);
		if (customActionBar != null) {
			customActionBar.setAlpha(0f);
			customActionBar.setTranslationY(-30f);
		}
		if (appNameText != null) {
			appNameText.setAlpha(0f);
			appNameText.setTranslationY(20f);
		}
		if (byAuthorText != null) {
			byAuthorText.setAlpha(0f);
			byAuthorText.setTranslationY(20f);
		}
		View divider = findViewById(R.id.contact_divider);
		if (divider != null) {
			divider.setAlpha(0f);
		}
		if (blogLinkText != null) {
			blogLinkText.setAlpha(0f);
			blogLinkText.setTranslationX(-30f);
		}
		if (emailLinkText != null) {
			emailLinkText.setAlpha(0f);
			emailLinkText.setTranslationX(30f);
		}
		ImageView githubIcon = (ImageView) findViewById(R.id.github_icon);
		if (githubIcon != null) {
			githubIcon.setAlpha(0f);
			githubIcon.setTranslationY(30f);
			githubIcon.setScaleX(0.8f);
			githubIcon.setScaleY(0.8f);
		}
		ImageView giteeIcon = (ImageView) findViewById(R.id.gitee_icon);
		if (giteeIcon != null) {
			giteeIcon.setAlpha(0f);
			giteeIcon.setTranslationY(30f);
			giteeIcon.setScaleX(0.8f);
			giteeIcon.setScaleY(0.8f);
		}
		ImageView xposedIcon = (ImageView) findViewById(R.id.xposed_icon);
		if (xposedIcon != null) {
			xposedIcon.setAlpha(0f);
			xposedIcon.setTranslationY(30f);
			xposedIcon.setScaleX(0.8f);
			xposedIcon.setScaleY(0.8f);
		}
		if (versionText != null) {
			versionText.setAlpha(0f);
			versionText.setTranslationY(20f);
		}
		if (copyrightText != null) {
			copyrightText.setAlpha(0f);
			copyrightText.setTranslationY(20f);
		}
	}
	private void startEntranceAnimations() {
		AnimatorSet animatorSet = new AnimatorSet();
		View customActionBar = findViewById(R.id.custom_actionbar);
		Animator actionBarAnim = createActionBarAnimation(customActionBar);
		AnimatorSet mainContentAnimSet = new AnimatorSet();
		Animator titleAuthorAnim = createTitleAuthorAnimation();
		Animator contactAnim = createContactAnimation();
		Animator iconsAnim = createIconsAnimation();
		Animator versionAnim = createVersionAnimation();
		contactAnim.setStartDelay(300);
		iconsAnim.setStartDelay(600);
		versionAnim.setStartDelay(900);
		mainContentAnimSet.playTogether(titleAuthorAnim, contactAnim, iconsAnim, versionAnim);
		animatorSet.playSequentially(actionBarAnim, mainContentAnimSet);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setStartDelay(200);
		animatorSet.start();
	}
	private Animator createActionBarAnimation(View view) {
		if (view == null)
			return new AnimatorSet();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		ObjectAnimator slideDown = ObjectAnimator.ofFloat(view, "translationY", -30f, 0f);
		set.playTogether(fadeIn, slideDown);
		set.setDuration(400);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}
	private Animator createTitleAuthorAnimation() {
		AnimatorSet set = new AnimatorSet();
		Animator titleAnim = createFadeUpAnimation(appNameText, 20f, 600);
		Animator authorAnim = createFadeUpAnimation(byAuthorText, 20f, 600);
		authorAnim.setStartDelay(100);
		set.playTogether(titleAnim, authorAnim);
		return set;
	}
	private Animator createContactAnimation() {
		AnimatorSet set = new AnimatorSet();
		View divider = findViewById(R.id.contact_divider);
		Animator dividerAnim = createFadeInAnimation(divider, 400);
		Animator blogAnim = createSlideInAnimation(blogLinkText, -30f, 0f, 500);
		blogAnim.setStartDelay(200);
		Animator emailAnim = createSlideInAnimation(emailLinkText, 30f, 0f, 500);
		emailAnim.setStartDelay(200);
		set.playTogether(dividerAnim, blogAnim, emailAnim);
		return set;
	}
	private Animator createIconsAnimation() {
		AnimatorSet set = new AnimatorSet();
		ImageView githubIcon = (ImageView) findViewById(R.id.github_icon);
		ImageView giteeIcon = (ImageView) findViewById(R.id.gitee_icon);
		ImageView xposedIcon = (ImageView) findViewById(R.id.xposed_icon);
		Animator githubAnim = createIconAnimation(githubIcon, 0);
		Animator giteeAnim = createIconAnimation(giteeIcon, 150);
		Animator xposedAnim = createIconAnimation(xposedIcon, 300);
		set.playTogether(githubAnim, giteeAnim, xposedAnim);
		return set;
	}
	private Animator createVersionAnimation() {
		AnimatorSet set = new AnimatorSet();
		Animator versionAnim = createFadeUpAnimation(versionText, 20f, 500);
		Animator copyrightAnim = createFadeUpAnimation(copyrightText, 20f, 500);
		copyrightAnim.setStartDelay(100);
		set.playTogether(versionAnim, copyrightAnim);
		return set;
	}
	private Animator createFadeUpAnimation(View view, float startY, long duration) {
		if (view == null)
			return new AnimatorSet();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", startY, 0f);
		set.playTogether(fadeIn, slideUp);
		set.setDuration(duration);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}
	private Animator createFadeInAnimation(View view, long duration) {
		if (view == null)
			return new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		fadeIn.setDuration(duration);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		return fadeIn;
	}
	private Animator createSlideInAnimation(View view, float startX, float endX, long duration) {
		if (view == null)
			return new AnimatorSet();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
		set.playTogether(fadeIn, slideIn);
		set.setDuration(duration);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}
	private Animator createIconAnimation(View view, long delay) {
		if (view == null)
			return new AnimatorSet();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 30f, 0f);
		ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f);
		ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f);
		set.playTogether(fadeIn, slideUp, scaleUp, scaleUpY);
		set.setDuration(500);
		set.setStartDelay(delay);
		set.setInterpolator(new OvershootInterpolator(0.8f));
		return set;
	}
	private void animateActivatedStatus() {
		ObjectAnimator pulse = ObjectAnimator.ofFloat(moduleStatusText, "alpha", 1f, 0.7f, 1f);
		pulse.setDuration(1000);
		pulse.setRepeatCount(ObjectAnimator.INFINITE);
		pulse.setRepeatMode(ObjectAnimator.REVERSE);
		pulse.setStartDelay(1000);
		pulse.start();
	}
	private boolean isActivated() {
		return com.jiguro.bettervia.ModuleStatus.activated;
	}
	private void showPopupMenu(View view) {
		PopupMenu popupMenu = new PopupMenu(this, view);
		popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
		MenuItem updateSettingsItem = popupMenu.getMenu().findItem(R.id.menu_update_settings);
		if (updateSettingsItem != null) {
			updateSettingsItem.setTitle(getString(R.string.update_settings));
		}
		MenuItem toggleIconItem = popupMenu.getMenu().findItem(R.id.menu_toggle_icon);
		if (toggleIconItem != null) {
			toggleIconItem.setTitle(isIconHidden ? getString(R.string.show_icon) : getString(R.string.hide_icon));
		}
		MenuItem withdrawAgreementItem = popupMenu.getMenu().findItem(R.id.menu_withdraw_agreement);
		if (withdrawAgreementItem != null) {
			withdrawAgreementItem.setTitle(getString(R.string.withdraw_agreement));
		}
		MenuItem securityfixItem = popupMenu.getMenu().findItem(R.id.menu_security_fix);
		if (securityfixItem != null) {
			securityfixItem.setTitle(getString(R.string.security_fix));
		}
		MenuItem exitItem = popupMenu.getMenu().findItem(R.id.menu_exit);
		if (exitItem != null) {
			exitItem.setTitle(getString(R.string.exit));
		}
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.menu_update_settings) {
					showUpdateSettingsDialog();
					return true;
				} else if (item.getItemId() == R.id.menu_toggle_icon) {
					setLauncherIconVisible(isIconHidden);
					return true;
				} else if (item.getItemId() == R.id.menu_withdraw_agreement) {
					showWithdrawAgreementDialog();
					return true;
				} else if (item.getItemId() == R.id.menu_exit) {
					animateExit();
					return true;
				}
				return false;
			}
		});
		popupMenu.show();
	}
	private void showWithdrawAgreementDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.withdraw_agreement_title))
				.setMessage(getString(R.string.withdraw_agreement_message))
				.setPositiveButton(getString(R.string.withdraw_confirm), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						withdrawAgreement();
					}
				}).setNegativeButton(getString(R.string.withdraw_cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setCancelable(true).show();
	}
	private void withdrawAgreement() {
		try {
			SharedPreferences.Editor editor = getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
			editor.putBoolean(KEY_AGREED, false);
			editor.apply();
			Toast.makeText(this, getString(R.string.withdraw_success), Toast.LENGTH_LONG).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						finishAffinity();
					}
				}
			}, 1500);
		} catch (Exception e) {
			Log.e(TAG, "撤回声明同意失败: " + e.getMessage());
			Toast.makeText(this, R.string.toast_operation_failed, Toast.LENGTH_SHORT).show();
		}
	}
	private void animateExit() {
		AnimatorSet exitAnimator = new AnimatorSet();
		View rootView = findViewById(android.R.id.content);
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f);
		ObjectAnimator scaleDown = ObjectAnimator.ofFloat(rootView, "scaleX", 1f, 0.95f);
		ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(rootView, "scaleY", 1f, 0.95f);
		exitAnimator.playTogether(fadeOut, scaleDown, scaleDownY);
		exitAnimator.setDuration(250);
		exitAnimator.setInterpolator(new AccelerateInterpolator());
		exitAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				finish();
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				finish();
			}
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		exitAnimator.start();
	}
	private void openGitHub() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.toast_cannot_open_github, Toast.LENGTH_SHORT).show();
		}
	}
	private void openGitee() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.toast_cannot_open_gitee, Toast.LENGTH_SHORT).show();
		}
	}
	private void openXposedRepo() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https:
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.toast_cannot_open_xposed, Toast.LENGTH_SHORT).show();
		}
	}
	private void openBlog() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.blog_url)));
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.toast_cannot_open_link, Toast.LENGTH_SHORT).show();
		}
	}
	private void sendEmail() {
		try {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + getString(R.string.email_address)));
			intent.putExtra(Intent.EXTRA_SUBJECT, "BetterVia Module Feedback");
			startActivity(Intent.createChooser(intent, getString(R.string.contact_email)));
		} catch (Exception e) {
			Toast.makeText(this, R.string.toast_cannot_send_email, Toast.LENGTH_SHORT).show();
		}
	}
	private boolean isUserAgreed() {
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		return sp.getBoolean(KEY_AGREED, false);
	}
	private void saveUpdateSettings(boolean autoUpdate, int updateSource) {
		SharedPreferences.Editor editor = getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
		editor.putBoolean(KEY_AUTO_UPDATE, autoUpdate);
		editor.putInt(KEY_UPDATE_SOURCE, updateSource);
		editor.apply();
	}
	private boolean isAutoUpdateEnabled() {
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		return sp.getBoolean(KEY_AUTO_UPDATE, true);
	}
	private int getCurrentUpdateSource() {
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		return sp.getInt(KEY_UPDATE_SOURCE, UPDATE_SOURCE_GITEE);
	}
	private void checkUpdate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int updateSource = getCurrentUpdateSource();
					String updateUrl = (updateSource == UPDATE_SOURCE_GITHUB) ? GITHUB_UPDATE_URL : GITEE_UPDATE_URL;
					Log.d(TAG, "检查更新，URL: " + updateUrl);
					URL url = new URL(updateUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
						Log.e(TAG, "HTTP错误代码: " + conn.getResponseCode());
						showUpdateCheckFailed();
						return;
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					br.close();
					conn.disconnect();
					String jsonResponse = sb.toString();
					Log.d(TAG, "获取到的JSON: " + jsonResponse);
					final UpdateInfo info = new UpdateInfo();
					JSONObject json = new JSONObject(jsonResponse);
					info.versionName = json.getString("versionName");
					info.apkUrl = json.getString("apkUrl");
					try {
						JSONObject updateLogJson = json.getJSONObject("updateLog");
						String currentLangCode = getCurrentLanguageCode();
						if (updateLogJson.has(currentLangCode)) {
							info.updateLog = updateLogJson.getString(currentLangCode);
						} else {
							info.updateLog = updateLogJson.getString("en");
						}
					} catch (JSONException e) {
						info.updateLog = json.getString("updateLog");
					}
					String localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
					Log.d(TAG, "本地版本: " + localVersion + ", 远程版本: " + info.versionName);
					if (!info.versionName.equals(localVersion)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showUpdateDialog(info);
							}
						});
					} else {
						Log.d(TAG, "已是最新版本，无需更新");
					}
				} catch (Exception e) {
					Log.e(TAG, "更新检查异常: " + e.getMessage(), e);
					showUpdateCheckFailed();
				}
			}
		}).start();
	}
	private String getCurrentLanguageCode() {
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		int language = sp.getInt(KEY_LANGUAGE, LANGUAGE_AUTO);
		switch (language) {
			case LANGUAGE_SIMPLIFIED_CHINESE :
				return "zh-CN";
			case LANGUAGE_TRADITIONAL_CHINESE :
				return "zh-TW";
			case LANGUAGE_ENGLISH :
			case LANGUAGE_AUTO :
			default :
				if (language == LANGUAGE_AUTO) {
					Locale systemLocale;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						systemLocale = getResources().getConfiguration().getLocales().get(0);
					} else {
						systemLocale = getResources().getConfiguration().locale;
					}
					String languageCode = systemLocale.getLanguage();
					String country = systemLocale.getCountry();
					if (languageCode.startsWith("zh")) {
						if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
							return "zh-TW";
						} else {
							return "zh-CN";
						}
					}
				}
				return "en";
		}
	}
	private void showUpdateSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_settings, null);
		builder.setView(dialogView);
		final Switch autoUpdateSwitch = dialogView.findViewById(R.id.auto_update_switch);
		final RadioGroup updateSourceGroup = dialogView.findViewById(R.id.update_source_group);
		final RadioButton githubRadio = dialogView.findViewById(R.id.github_radio);
		final RadioButton giteeRadio = dialogView.findViewById(R.id.gitee_radio);
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		autoUpdateSwitch.setChecked(sp.getBoolean(KEY_AUTO_UPDATE, true));
		int updateSource = getCurrentUpdateSource();
		if (updateSource == UPDATE_SOURCE_GITHUB) {
			updateSourceGroup.check(R.id.github_radio);
		} else {
			updateSourceGroup.check(R.id.gitee_radio);
		}
		View githubOption = dialogView.findViewById(R.id.github_option_container);
		View giteeOption = dialogView.findViewById(R.id.gitee_option_container);
		if (githubOption != null) {
			githubOption.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSourceGroup.check(R.id.github_radio);
				}
			});
		}
		if (giteeOption != null) {
			giteeOption.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSourceGroup.check(R.id.gitee_radio);
				}
			});
		}
		builder.setTitle(getString(R.string.update_settings))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int selectedId = updateSourceGroup.getCheckedRadioButtonId();
						int selectedSource;
						if (selectedId == R.id.github_radio) {
							selectedSource = UPDATE_SOURCE_GITHUB;
						} else if (selectedId == R.id.gitee_radio) {
							selectedSource = UPDATE_SOURCE_GITEE;
						} else {
							selectedSource = getCurrentUpdateSource();
						}
						saveUpdateSettings(autoUpdateSwitch.isChecked(), selectedSource);
						performImmediateUpdateCheck();
					}
				}).setNegativeButton(android.R.string.cancel, null).show();
	}
	private void performImmediateUpdateCheck() {
		if (!isAutoUpdateEnabled()) {
			return;
		}
		Toast.makeText(this, getString(R.string.checking_updates), Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				checkUpdate();
			}
		}, 500);
	}
	private void showUpdateDialog(final UpdateInfo info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(String.format(getString(R.string.new_version_found), info.versionName))
				.setMessage(info.updateLog).setCancelable(false)
				.setPositiveButton(getString(R.string.download_now), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.apkUrl));
							startActivity(intent);
						} catch (Exception e) {
							Toast.makeText(MainActivity.this, getString(R.string.cannot_open_download_link),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(getString(R.string.later), null).show();
	}
	private void showUpdateCheckFailed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, getString(R.string.update_check_failed), Toast.LENGTH_SHORT).show();
			}
		});
	}
	private static class UpdateInfo {
		String versionName;
		String updateLog;
		String apkUrl;
	}
	private boolean hasRootPermission() {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("echo 'Checking root access'\n");
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			return exitValue == 0;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
			}
		}
	}
	private boolean checkStoragePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		}
		return true; 
	}
	private void requestStoragePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_STORAGE_PERMISSION_FOR_FIX);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (flowWebView != null) {
			flowWebView.destroy();
		}
	}
}