package com.jiguro.bettervia;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import de.robv.android.xposed.*;
import java.io.File;
import java.util.*;
public class PasswordManager {
	private static final String MODULE_THEME_AUTO = "auto";
	private static final String MODULE_THEME_LIGHT = "light";
	private static final String MODULE_THEME_DARK = "dark";
	private static final String DEFAULT_MODULE_THEME = MODULE_THEME_AUTO;
	private static final int LIGHT_BG_COLOR = Color.WHITE;
	private static final int LIGHT_TITLE_COLOR = 0xFF6200EE;
	private static final int LIGHT_TEXT_COLOR = Color.BLACK;
	private static final int LIGHT_HINT_COLOR = 0xFF666666;
	private static final int LIGHT_DIVIDER_COLOR = 0xFFDDDDDD;
	private static final int LIGHT_BTN_BG_COLOR = 0xFFE0E0E0;
	private static final int LIGHT_BTN_TEXT_COLOR = 0xFF000000;
	private static final int LIGHT_OK_BTN_BG_COLOR = 0xFF6200EE;
	private static final int LIGHT_OK_BTN_TEXT_COLOR = Color.WHITE;
	private static final int DARK_BG_COLOR = 0xFF18181A;
	private static final int DARK_TITLE_COLOR = 0xFF87CEEB;
	private static final int DARK_TEXT_COLOR = 0xFFE0E0E0;
	private static final int DARK_HINT_COLOR = 0xFFAAAAAA;
	private static final int DARK_DIVIDER_COLOR = 0xFF333333;
	private static final int DARK_BTN_BG_COLOR = 0xFF2D2D2D;
	private static final int DARK_BTN_TEXT_COLOR = 0xFFE0E0E0;
	private static final int DARK_OK_BTN_BG_COLOR = 0xFF87CEEB;
	private static final int DARK_OK_BTN_TEXT_COLOR = Color.BLACK;
	private static final int DARK_EDIT_BG_COLOR = 0xFF2D2D2D;
	private static final int LIGHT_EDIT_BG_COLOR = 0xFFF5F5F5;
	private static final int MAX_CONTINUOUS_FAILURES = 6; 
	private static final long LOCKOUT_DURATION_MS = 5 * 60 * 1000; 
	private static final long CONTINUOUS_INTERVAL_MS = 60 * 1000; 
	private static final String KEY_CONTINUOUS_FAILURE_COUNT = "privacy_lock_continuous_failure_count"; 
	private static final String KEY_LAST_FAILURE_TIME = "privacy_lock_last_failure_time"; 
	private static final String KEY_LOCKOUT_END_TIME = "privacy_lock_lockout_end_time"; 
	private Context context;
	private PasswordListener listener;
	private SecurePasswordHelper securePasswordHelper;
	private SecurePasswordStorage secureStorage;
	private PatternLockView currentPatternLock;
	private Button currentCancelBtn;
	private Dialog currentVerifyDialog;
	private Handler countdownHandler;
	private Runnable countdownRunnable;
	private boolean isClearingPassword = false;
	private boolean isVerifyMode = false;
	public static final int PASSWORD_TYPE_PATTERN = 0;
	public static final int PASSWORD_TYPE_PIN = 1;
	private int currentPasswordType = PASSWORD_TYPE_PATTERN;
	private int targetPasswordType = PASSWORD_TYPE_PATTERN;
	public interface PasswordListener {
		void onPasswordSet();
		void onPasswordReset();
		void onVerifySuccess(); 
		void onCancelled();
	}
	public PasswordManager(Context context) {
		this.context = context;
		this.securePasswordHelper = new SecurePasswordHelper(context);
		this.secureStorage = new SecurePasswordStorage(context);
	}
	public void setListener(PasswordListener listener) {
		this.listener = listener;
	}
	public void setPasswordType(int type) {
		this.currentPasswordType = type;
	}
	public int getPasswordType() {
		return this.currentPasswordType;
	}
	public void setTargetPasswordType(int type) {
		this.targetPasswordType = type;
	}
	public int getTargetPasswordType() {
		return this.targetPasswordType;
	}
	public int detectCurrentPasswordType() {
		try {
			boolean hasPasswordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
			if (!hasPasswordSet) {
				return -1; 
			}
			String patternPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
			String pinPassword = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
			if (patternPassword != null && !patternPassword.isEmpty()) {
				return PASSWORD_TYPE_PATTERN;
			} else if (pinPassword != null && !pinPassword.isEmpty()) {
				return PASSWORD_TYPE_PIN;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	private void vibrate(long duration) {
		try {
			Activity act = getActivityFrom(context);
			if (act != null) {
				Object vibrator = act.getSystemService(Context.VIBRATOR_SERVICE);
				if (vibrator != null) {
					try {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
							VibratorManager vm = (VibratorManager) act
									.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
							if (vm != null) {
								Vibrator v = vm.getDefaultVibrator();
								v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
							}
						} else {
							Vibrator v = (Vibrator) vibrator;
							v.vibrate(duration);
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		}
	}
	public void setClearingPassword(boolean isClearing) {
		this.isClearingPassword = isClearing;
	}
	public void setVerifyMode(boolean isVerifyMode) {
		this.isVerifyMode = isVerifyMode;
	}
	public void clearPasswordData() {
		try {
			secureStorage.removeSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
			secureStorage.removeSecureValue(SecurePasswordStorage.KEY_PASSWORD_SET);
			secureStorage.removeSecureValue(SecurePasswordStorage.KEY_LAST_FAILURE_TIME);
			secureStorage.removeSecureValue(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT);
			secureStorage.removeSecureValue(SecurePasswordStorage.KEY_LOCKOUT_END_TIME);
			secureStorage.close();
			try {
				String packageName = context.getPackageName();
				String dbPath = "/data/user/0/" + packageName + "/databases/app_conf";
				File dbFile = new File(dbPath);
				if (dbFile.exists()) {
					dbFile.delete();
				}
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
	}
	public void showSetPasswordDialog() {
		final Activity act = getActivityFrom(context);
		if (act == null) {
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(context);
				final int titleColor = getTitleColor(context);
				final int textColor = getTextColor(context);
				final int hintColor = getHintColor(context);
				final int btnBgColor = getBtnBgColor(context);
				final int btnTextColor = getBtnTextColor(context);
				final int okBtnBgColor = getOkBtnBgColor(context);
				final int okBtnTextColor = getOkBtnTextColor(context);
				final int dividerColor = getDividerColor(context);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(false);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(context, "pattern_lock_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				root.addView(title);
				final TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(context, "pattern_lock_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				subtitleLp.topMargin = dp(act, 12);
				subtitleLp.bottomMargin = dp(act, 8);
				root.addView(subtitle, subtitleLp);
				final TextView hintTv = new TextView(act);
				hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pattern_lock_hint"));
				hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintTv.setTextColor(hintColor);
				hintTv.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 16);
				root.addView(hintTv, hintLp);
				FrameLayout patternContainer = new FrameLayout(act);
				LinearLayout.LayoutParams patternContainerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 300));
				patternContainerLp.bottomMargin = dp(act, 24);
				root.addView(patternContainer, patternContainerLp);
				final PatternLockView patternLock = new PatternLockView(act);
				patternLock.setPatternColor(okBtnBgColor);
				patternLock.setDotColor(dividerColor);
				patternLock.setVibratorCallback(new PatternLockView.VibratorCallback() {
					@Override
					public void onVibrate(long duration) {
						vibrate(duration);
					}
				});
				FrameLayout.LayoutParams patternLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				patternLp.gravity = Gravity.CENTER;
				patternContainer.addView(patternLock, patternLp);
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				root.addView(btnRow, btnRowLp);
				Button cancelBtn = new Button(act);
				applyClickAnim(cancelBtn);
				cancelBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_cancel"));
				cancelBtn.setTextColor(btnTextColor);
				cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				cancelBtn.setBackground(cancelBg);
				cancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRow.addView(cancelBtn, btnLp);
				final int[] step = {0}; 
				final List<Integer>[] firstPattern = new List[]{new ArrayList<Integer>()};
				patternLock.setOnPatternListener(new PatternLockView.OnPatternListener() {
					@Override
					public void onPatternStart() {
					}
					@Override
					public void onPatternDetected(List<Integer> pattern) {
						if (step[0] == 0) {
							if (pattern.size() < PatternLockView.MIN_PATTERN_LENGTH) {
								hintTv.setText(
										LocalizedStringProvider.getInstance().get(context, "pattern_lock_too_short"));
								hintTv.setTextColor(0xFFFF0000);
								patternLock.showError();
								vibrate(50);
								patternLock.postDelayed(new Runnable() {
									@Override
									public void run() {
										patternLock.clearPattern();
										hintTv.setText(LocalizedStringProvider.getInstance().get(context,
												"pattern_lock_hint"));
										hintTv.setTextColor(hintColor);
									}
								}, 1500);
							} else {
								firstPattern[0] = new ArrayList<>(pattern);
								step[0] = 1;
								subtitle.setText(LocalizedStringProvider.getInstance().get(context,
										"pattern_lock_confirm_title"));
								hintTv.setText(LocalizedStringProvider.getInstance().get(context,
										"pattern_lock_confirm_hint"));
								hintTv.setTextColor(hintColor);
								patternLock.clearPattern();
							}
						} else if (step[0] == 1) {
							if (pattern.equals(firstPattern[0])) {
								String passwordStr = patternToString(pattern);
								String hashedPassword = hashPatternPassword(passwordStr);
								secureStorage.removeSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
								secureStorage.putSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD,
										hashedPassword);
								secureStorage.putSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, true);
								hintTv.setText(
										LocalizedStringProvider.getInstance().get(context, "pattern_lock_set_success"));
								hintTv.setTextColor(0xFF4CAF50);
								patternLock.showSuccess();
								dialog.dismiss();
								if (listener != null) {
									listener.onPasswordSet();
								}
							} else {
								hintTv.setText(
										LocalizedStringProvider.getInstance().get(context, "pattern_lock_mismatch"));
								hintTv.setTextColor(0xFFFF0000);
								patternLock.showError();
								patternLock.postDelayed(new Runnable() {
									@Override
									public void run() {
										step[0] = 0;
										firstPattern[0].clear();
										subtitle.setText(LocalizedStringProvider.getInstance().get(context,
												"pattern_lock_subtitle"));
										hintTv.setText(LocalizedStringProvider.getInstance().get(context,
												"pattern_lock_hint"));
										hintTv.setTextColor(hintColor);
										patternLock.clearPattern();
									}
								}, 1500);
							}
						}
					}
					@Override
					public void onPatternCleared() {
					}
				});
				cancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if (listener != null) {
							listener.onCancelled();
						}
					}
				});
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.85);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	public void showSetPinPasswordDialog() {
		final Activity act = getActivityFrom(context);
		if (act == null) {
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(context);
				final int titleColor = getTitleColor(context);
				final int textColor = getTextColor(context);
				final int hintColor = getHintColor(context);
				final int btnBgColor = getBtnBgColor(context);
				final int btnTextColor = getBtnTextColor(context);
				final int okBtnBgColor = getOkBtnBgColor(context);
				final int okBtnTextColor = getOkBtnTextColor(context);
				final int dividerColor = getDividerColor(context);
				final int editBgColor = getEditBgColor(context);
				final Dialog dialog = new Dialog(act);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(false);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				root.addView(title);
				final TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				subtitleLp.topMargin = dp(act, 12);
				subtitleLp.bottomMargin = dp(act, 8);
				root.addView(subtitle, subtitleLp);
				final TextView hintTv = new TextView(act);
				hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_hint"));
				hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintTv.setTextColor(hintColor);
				hintTv.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 96);
				root.addView(hintTv, hintLp);
				final EditText passwordInput = new EditText(act);
				passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
				passwordInput.setHint(LocalizedStringProvider.getInstance().get(context, "pin_lock_hint"));
				passwordInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				passwordInput.setTextColor(textColor);
				passwordInput.setHintTextColor(hintColor);
				passwordInput.setGravity(Gravity.CENTER);
				passwordInput.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
				passwordInput.setSingleLine(true);
				passwordInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
				GradientDrawable editBg = new GradientDrawable();
				editBg.setColor(editBgColor);
				editBg.setCornerRadius(dp(act, 8));
				editBg.setStroke(dp(act, 1), dividerColor);
				passwordInput.setBackground(editBg);
				LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				inputLp.bottomMargin = dp(act, 64);
				root.addView(passwordInput, inputLp);
				final int[] step = {0}; 
				final String[] firstPassword = new String[1];
				passwordInput.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						String text = s.toString();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < text.length(); i++) {
							char c = text.charAt(i);
							if (c >= '0' && c <= '9') {
								sb.append(c);
							}
						}
						String filtered = sb.toString();
						if (!text.equals(filtered)) {
							passwordInput.setText(filtered);
							passwordInput.setSelection(filtered.length());
						}
					}
				});
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRowLp.topMargin = dp(act, 64);
				root.addView(btnRow, btnRowLp);
				Button cancelBtn = new Button(act);
				applyClickAnim(cancelBtn);
				cancelBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_cancel"));
				cancelBtn.setTextColor(btnTextColor);
				cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				cancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				cancelBtn.setBackground(cancelBg);
				cancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams cancelBtnLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelBtnLp.rightMargin = dp(act, 8);
				btnRow.addView(cancelBtn, cancelBtnLp);
				Button confirmBtn = new Button(act);
				applyClickAnim(confirmBtn);
				confirmBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_ok"));
				confirmBtn.setTextColor(okBtnTextColor);
				confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				confirmBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable confirmBg = new GradientDrawable();
				confirmBg.setColor(okBtnBgColor);
				confirmBg.setCornerRadius(dp(act, 12));
				confirmBtn.setBackground(confirmBg);
				confirmBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams confirmBtnLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				confirmBtnLp.leftMargin = dp(act, 8);
				btnRow.addView(confirmBtn, confirmBtnLp);
				cancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if (listener != null) {
							listener.onCancelled();
						}
					}
				});
				confirmBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String inputPassword = passwordInput.getText().toString();
						if (inputPassword.length() < 4) {
							hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_too_short"));
							hintTv.setTextColor(0xFFFF0000);
							passwordInput.setText("");
							passwordInput.requestFocus();
							vibrate(50);
							return;
						}
						if (inputPassword.length() > 16) {
							hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_too_long"));
							hintTv.setTextColor(0xFFFF0000);
							passwordInput.setText("");
							passwordInput.requestFocus();
							vibrate(50);
							return;
						}
						if (step[0] == 0) {
							firstPassword[0] = inputPassword;
							step[0] = 1;
							subtitle.setText(
									LocalizedStringProvider.getInstance().get(context, "pin_lock_confirm_title"));
							hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_confirm_hint"));
							hintTv.setTextColor(hintColor);
							passwordInput.setText("");
							passwordInput.requestFocus();
						} else if (step[0] == 1) {
							if (inputPassword.equals(firstPassword[0])) {
								try {
									String hashedPassword = securePasswordHelper.hash(inputPassword);
									secureStorage.removeSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
									secureStorage.putSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD,
											hashedPassword);
									secureStorage.putSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, true);
									hintTv.setText(
											LocalizedStringProvider.getInstance().get(context, "pin_lock_set_success"));
									hintTv.setTextColor(0xFF4CAF50);
									dialog.dismiss();
									if (listener != null) {
										listener.onPasswordSet();
									}
								} catch (Exception e) {
									hintTv.setText("Error: " + e.getMessage());
									hintTv.setTextColor(0xFFFF0000);
								}
							} else {
								hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_mismatch"));
								hintTv.setTextColor(0xFFFF0000);
								passwordInput.setText("");
								passwordInput.requestFocus();
								vibrate(50);
							}
						}
					}
				});
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				dialog.setContentView(dialogContainer);
				Window window = dialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.85);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				dialog.show();
				animateDialogEntrance(root, act);
			}
		});
	}
	private String patternToString(List<Integer> pattern) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pattern.size(); i++) {
			sb.append(pattern.get(i));
			if (i < pattern.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	private List<Integer> stringToPattern(String patternStr) {
		List<Integer> pattern = new ArrayList<>();
		if (patternStr != null && !patternStr.isEmpty()) {
			String[] parts = patternStr.split(",");
			for (String part : parts) {
				try {
					pattern.add(Integer.parseInt(part));
				} catch (NumberFormatException e) {
				}
			}
		}
		return pattern;
	}
	private String hashPatternPassword(String patternStr) {
		try {
			return securePasswordHelper.hash(patternStr);
		} catch (Exception e) {
			return "";
		}
	}
	private boolean verifyPatternPassword(String inputPatternStr, String storedHash) {
		try {
			return securePasswordHelper.verify(inputPatternStr, storedHash);
		} catch (Exception e) {
			return false;
		}
	}
	private static int dp(Context ctx, int dp) {
		return (int) (dp * ctx.getResources().getDisplayMetrics().density + 0.5f);
	}
	private Activity getActivityFrom(Context ctx) {
		try {
			if (ctx instanceof Activity) {
				return (Activity) ctx;
			}
			Context currentCtx = ctx;
			while (currentCtx instanceof ContextWrapper) {
				currentCtx = ((ContextWrapper) currentCtx).getBaseContext();
				if (currentCtx instanceof Activity) {
					return (Activity) currentCtx;
				}
			}
		} catch (Throwable ignored) {
		}
		return null;
	}
	private String getModuleTheme(Context ctx) {
		try {
			Object sp = XposedHelpers.callMethod(ctx, "getSharedPreferences", "BetterVia", Context.MODE_PRIVATE);
			return (String) XposedHelpers.callMethod(sp, "getString", "module_theme", DEFAULT_MODULE_THEME);
		} catch (Exception e) {
			return DEFAULT_MODULE_THEME;
		}
	}
	private String getActualTheme(Context ctx) {
		String theme = getModuleTheme(ctx);
		if (MODULE_THEME_AUTO.equals(theme)) {
			int nightMode = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					nightMode = ctx.getResources().getConfiguration().isNightModeActive() ? 1 : 0;
				}
			} else {
				try {
					Object uiModeManager = ctx.getSystemService(Context.UI_MODE_SERVICE);
					nightMode = (Integer) XposedHelpers.callMethod(uiModeManager, "getNightMode");
				} catch (Exception e) {
					nightMode = 0;
				}
			}
			return nightMode == 1 ? MODULE_THEME_DARK : MODULE_THEME_LIGHT;
		}
		return theme;
	}
	private int getBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BG_COLOR : LIGHT_BG_COLOR;
	}
	private int getTitleColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_TITLE_COLOR : LIGHT_TITLE_COLOR;
	}
	private int getTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_TEXT_COLOR : LIGHT_TEXT_COLOR;
	}
	private int getHintColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_HINT_COLOR : LIGHT_HINT_COLOR;
	}
	private int getDividerColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_DIVIDER_COLOR : LIGHT_DIVIDER_COLOR;
	}
	private int getBtnBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BTN_BG_COLOR : LIGHT_BTN_BG_COLOR;
	}
	private int getBtnTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_BTN_TEXT_COLOR : LIGHT_BTN_TEXT_COLOR;
	}
	private int getOkBtnBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_OK_BTN_BG_COLOR : LIGHT_OK_BTN_BG_COLOR;
	}
	private int getOkBtnTextColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_OK_BTN_TEXT_COLOR : LIGHT_OK_BTN_TEXT_COLOR;
	}
	private int getEditBgColor(Context ctx) {
		return MODULE_THEME_DARK.equals(getActualTheme(ctx)) ? DARK_EDIT_BG_COLOR : LIGHT_EDIT_BG_COLOR;
	}
	private void jiguroMessage(final String message) {
		try {
			final Activity act = getActivityFrom(context);
			if (act != null) {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boolean useCustomToast = getPrefBoolean(act, "custom_toast", true);
						boolean isLongText = message != null && message.length() > 20;
						int duration = isLongText ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
						if (useCustomToast) {
							Toast customToast = createCustomToast(act, message, duration);
							if (customToast != null) {
								customToast.show();
							}
						} else {
							Toast.makeText(act, message, duration).show();
						}
					}
				});
			}
		} catch (Exception e) {
		}
	}
	private Toast createCustomToast(final Context context, final String msg, final int duration) {
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
	private boolean getPrefBoolean(Context ctx, String key, boolean defaultValue) {
		try {
			SharedPreferences prefs = ctx.getSharedPreferences("BetterVia_prefs", Context.MODE_PRIVATE);
			return prefs.getBoolean(key, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	private boolean isLockedOut() {
		try {
			long currentTime = System.currentTimeMillis();
			long lockoutEndTime = secureStorage.getSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME, 0);
			if (lockoutEndTime > 0) {
				if (currentTime < lockoutEndTime) {
					return true;
				} else {
					secureStorage.putSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME, 0);
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	private long getRemainingLockoutTime() {
		try {
			long currentTime = System.currentTimeMillis();
			long lockoutEndTime = secureStorage.getSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME, 0);
			if (lockoutEndTime > 0) {
				long remaining = lockoutEndTime - currentTime;
				return Math.max(0, remaining / 1000); 
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}
	private void incrementFailureCount() {
		try {
			long currentTime = System.currentTimeMillis();
			int continuousFailureCount = secureStorage.getSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT,
					0);
			long lastFailureTime = secureStorage.getSecureLong(SecurePasswordStorage.KEY_LAST_FAILURE_TIME, 0);
			if (lastFailureTime > 0 && (currentTime - lastFailureTime) <= CONTINUOUS_INTERVAL_MS) {
				continuousFailureCount++;
			} else {
				continuousFailureCount = 1;
			}
			secureStorage.putSecureLong(SecurePasswordStorage.KEY_LAST_FAILURE_TIME, currentTime);
			if (continuousFailureCount >= MAX_CONTINUOUS_FAILURES) {
				secureStorage.putSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME,
						currentTime + LOCKOUT_DURATION_MS);
				secureStorage.putSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT, 0);
			} else {
				secureStorage.putSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT, continuousFailureCount);
			}
		} catch (Exception e) {
		}
	}
	private void resetFailureCount() {
		try {
			secureStorage.putSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT, 0);
			secureStorage.putSecureLong(SecurePasswordStorage.KEY_LAST_FAILURE_TIME, 0);
			secureStorage.putSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME, 0);
		} catch (Exception e) {
		}
	}
	private void startCountdown(final Activity act, final TextView hintTv, final String originalHintText) {
		if (countdownHandler != null && countdownRunnable != null) {
			countdownHandler.removeCallbacks(countdownRunnable);
		}
		countdownHandler = new Handler(Looper.getMainLooper());
		countdownRunnable = new Runnable() {
			@Override
			public void run() {
				long remainingSeconds = getRemainingLockoutTime();
				if (remainingSeconds > 0) {
					String message = String.format(
							LocalizedStringProvider.getInstance().get(context, "pattern_lock_wait_seconds"),
							remainingSeconds);
					hintTv.setText(message);
					hintTv.setTextColor(0xFFFF0000);
					countdownHandler.postDelayed(this, 1000);
				} else {
					secureStorage.putSecureLong(SecurePasswordStorage.KEY_LOCKOUT_END_TIME, 0);
					hintTv.setText(originalHintText);
					hintTv.setTextColor(getHintColor(context));
					if (currentPatternLock != null) {
						currentPatternLock.setEnabled(true);
						currentPatternLock.setClickable(true);
						currentPatternLock.setFocusable(true);
						currentPatternLock.clearPattern();
					}
					if (currentCancelBtn != null) {
						currentCancelBtn.setEnabled(true);
						currentCancelBtn.setClickable(true);
						currentCancelBtn.setFocusable(true);
					}
				}
			}
		};
		countdownHandler.post(countdownRunnable);
	}
	private void applyClickAnim(final View v) {
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
						break;
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_CANCEL :
						v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
						break;
				}
				return false;
			}
		});
	}
	private void animateDialogEntrance(final ViewGroup root, final Activity act) {
		root.setScaleX(0.8f);
		root.setScaleY(0.8f);
		root.setAlpha(0f);
		root.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).setInterpolator(new DecelerateInterpolator())
				.start();
	}
	public void showVerifyPasswordDialog() {
		showVerifyPasswordDialog(null);
	}
	public void showVerifyPasswordDialog(final String customHint) {
		final Activity act = getActivityFrom(context);
		if (act == null) {
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(context);
				final int titleColor = getTitleColor(context);
				final int textColor = getTextColor(context);
				final int hintColor = getHintColor(context);
				final int btnBgColor = getBtnBgColor(context);
				final int btnTextColor = getBtnTextColor(context);
				final int okBtnBgColor = getOkBtnBgColor(context);
				final int okBtnTextColor = getOkBtnTextColor(context);
				final int dividerColor = getDividerColor(context);
				currentVerifyDialog = new Dialog(act);
				currentVerifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				currentVerifyDialog.setCancelable(false);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(context, "pattern_lock_verify_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				root.addView(title);
				final TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(context, "pattern_lock_verify_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				subtitleLp.topMargin = dp(act, 12);
				subtitleLp.bottomMargin = dp(act, 8);
				root.addView(subtitle, subtitleLp);
				final TextView hintTv = new TextView(act);
				String hintText = (customHint != null && !customHint.isEmpty())
						? customHint
						: LocalizedStringProvider.getInstance().get(context, "pattern_lock_verify_hint");
				hintTv.setText(hintText);
				hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintTv.setTextColor(hintColor);
				hintTv.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 16);
				root.addView(hintTv, hintLp);
				final String originalHintText = hintText;
				FrameLayout patternContainer = new FrameLayout(act);
				LinearLayout.LayoutParams patternContainerLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, dp(act, 300));
				patternContainerLp.bottomMargin = dp(act, 24);
				root.addView(patternContainer, patternContainerLp);
				currentPatternLock = new PatternLockView(act);
				currentPatternLock.setPatternColor(okBtnBgColor);
				currentPatternLock.setDotColor(dividerColor);
				currentPatternLock.setVibratorCallback(new PatternLockView.VibratorCallback() {
					@Override
					public void onVibrate(long duration) {
						vibrate(duration);
					}
				});
				FrameLayout.LayoutParams patternLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				patternLp.gravity = Gravity.CENTER;
				patternContainer.addView(currentPatternLock, patternLp);
				currentCancelBtn = new Button(act);
				applyClickAnim(currentCancelBtn);
				currentCancelBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_cancel"));
				currentCancelBtn.setTextColor(btnTextColor);
				currentCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				currentCancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				currentCancelBtn.setBackground(cancelBg);
				currentCancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRowLp.bottomMargin = dp(act, 8);
				root.addView(btnRow, btnRowLp);
				LinearLayout.LayoutParams cancelBtnLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelBtnLp.rightMargin = dp(act, 8);
				btnRow.addView(currentCancelBtn, cancelBtnLp);
				final TextView securityWarningTv = new TextView(act);
				securityWarningTv.setText(LocalizedStringProvider.getInstance().get(context, "security_warning_text"));
				securityWarningTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				securityWarningTv.setTextColor(0xFFFF0000);
				securityWarningTv.setGravity(Gravity.CENTER);
				securityWarningTv.setTypeface(null, Typeface.BOLD);
				LinearLayout.LayoutParams warningLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				warningLp.topMargin = dp(act, 12);
				root.addView(securityWarningTv, warningLp);
				boolean hasPasswordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
				if (hasPasswordSet) {
					boolean patternConsistent = secureStorage
							.verifyConsistency(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
					boolean pinConsistent = secureStorage.verifyConsistency(SecurePasswordStorage.KEY_PIN_PASSWORD);
					boolean passwordSetConsistent = secureStorage
							.verifyConsistency(SecurePasswordStorage.KEY_PASSWORD_SET);
					if (patternConsistent && pinConsistent && passwordSetConsistent) {
						securityWarningTv.setVisibility(View.GONE);
					} else {
						securityWarningTv.setVisibility(View.VISIBLE);
					}
				} else {
					securityWarningTv.setVisibility(View.GONE);
				}
				currentPatternLock.setOnPatternListener(new PatternLockView.OnPatternListener() {
					@Override
					public void onPatternStart() {
					}
					@Override
					public void onPatternDetected(List<Integer> pattern) {
						if (isLockedOut()) {
							currentPatternLock.clearPattern();
							currentPatternLock.setEnabled(false);
							currentPatternLock.setClickable(false);
							currentPatternLock.setFocusable(false);
							currentCancelBtn.setEnabled(false);
							currentCancelBtn.setClickable(false);
							currentCancelBtn.setFocusable(false);
							long remainingSeconds = getRemainingLockoutTime();
							String message = String.format(
									LocalizedStringProvider.getInstance().get(context, "pattern_lock_wait_seconds"),
									remainingSeconds);
							hintTv.setText(message);
							hintTv.setTextColor(0xFFFF0000);
							startCountdown(act, hintTv, originalHintText);
							return;
						}
						if (pattern.size() >= PatternLockView.MIN_PATTERN_LENGTH) {
							boolean isCorrect = verifyPassword(pattern);
							if (isCorrect) {
								resetFailureCount();
								hintTv.setText(LocalizedStringProvider.getInstance().get(context,
										"pattern_lock_verify_success"));
								hintTv.setTextColor(0xFF4CAF50);
								currentPatternLock.showSuccess();
								if (listener != null) {
									listener.onVerifySuccess();
									listener.onPasswordReset();
								}
								currentPatternLock.postDelayed(new Runnable() {
									@Override
									public void run() {
										currentVerifyDialog.dismiss();
										if (!isClearingPassword && !isVerifyMode) {
											if (targetPasswordType == PASSWORD_TYPE_PATTERN) {
												showSetPasswordDialog();
											} else {
												showSetPinPasswordDialog();
											}
										}
										isClearingPassword = false;
										isVerifyMode = false;
									}
								}, 300); 
							} else {
								incrementFailureCount();
								if (isLockedOut()) {
									vibrate(50);
									currentPatternLock.clearPattern();
									currentPatternLock.setEnabled(false);
									currentPatternLock.setClickable(false);
									currentPatternLock.setFocusable(false);
									currentCancelBtn.setEnabled(false);
									currentCancelBtn.setClickable(false);
									currentCancelBtn.setFocusable(false);
									long remainingSeconds = getRemainingLockoutTime();
									String message = String.format(LocalizedStringProvider.getInstance().get(context,
											"pattern_lock_wait_seconds"), remainingSeconds);
									hintTv.setText(message);
									hintTv.setTextColor(0xFFFF0000);
									startCountdown(act, hintTv, originalHintText);
								} else {
									int continuousFailureCount = secureStorage
											.getSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT, 0);
									int remainingAttempts = MAX_CONTINUOUS_FAILURES - continuousFailureCount;
									String message = String.format(LocalizedStringProvider.getInstance().get(context,
											"pattern_lock_attempts_left"), remainingAttempts);
									hintTv.setText(message);
									hintTv.setTextColor(0xFFFF0000);
									currentPatternLock.showError();
									vibrate(50);
									currentPatternLock.postDelayed(new Runnable() {
										@Override
										public void run() {
											currentPatternLock.clearPattern();
											hintTv.setText(originalHintText);
											hintTv.setTextColor(hintColor);
										}
									}, 1500);
								}
							}
						} else {
							hintTv.setText(
									LocalizedStringProvider.getInstance().get(context, "pattern_lock_too_short"));
							hintTv.setTextColor(0xFFFF0000);
							currentPatternLock.showError();
							vibrate(50);
							currentPatternLock.postDelayed(new Runnable() {
								@Override
								public void run() {
									currentPatternLock.clearPattern();
									hintTv.setText(originalHintText);
									hintTv.setTextColor(hintColor);
								}
							}, 1500);
						}
					}
					@Override
					public void onPatternCleared() {
					}
				});
				currentCancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						currentVerifyDialog.dismiss();
						if (listener != null) {
							listener.onCancelled();
						}
					}
				});
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				currentVerifyDialog.setContentView(dialogContainer);
				Window window = currentVerifyDialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.85);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				currentVerifyDialog.show();
				if (isLockedOut()) {
					currentPatternLock.setEnabled(false);
					currentPatternLock.setClickable(false);
					currentPatternLock.setFocusable(false);
					currentCancelBtn.setEnabled(false);
					currentCancelBtn.setClickable(false);
					currentCancelBtn.setFocusable(false);
					long remainingSeconds = getRemainingLockoutTime();
					String message = String.format(
							LocalizedStringProvider.getInstance().get(context, "pattern_lock_wait_seconds"),
							remainingSeconds);
					hintTv.setText(message);
					hintTv.setTextColor(0xFFFF0000);
					startCountdown(act, hintTv, originalHintText);
				}
				animateDialogEntrance(root, act);
			}
		});
	}
	public void showVerifyPinPasswordDialog() {
		showVerifyPinPasswordDialog(null);
	}
	public void showVerifyPinPasswordDialog(final String customHint) {
		final Activity act = getActivityFrom(context);
		if (act == null) {
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (act.isFinishing() || act.isDestroyed())
					return;
				final int bgColor = getBgColor(context);
				final int titleColor = getTitleColor(context);
				final int textColor = getTextColor(context);
				final int hintColor = getHintColor(context);
				final int btnBgColor = getBtnBgColor(context);
				final int btnTextColor = getBtnTextColor(context);
				final int okBtnBgColor = getOkBtnBgColor(context);
				final int okBtnTextColor = getOkBtnTextColor(context);
				final int dividerColor = getDividerColor(context);
				final int editBgColor = getEditBgColor(context);
				currentVerifyDialog = new Dialog(act);
				currentVerifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				currentVerifyDialog.setCancelable(false);
				FrameLayout dialogContainer = new FrameLayout(act);
				GradientDrawable containerBg = new GradientDrawable();
				containerBg.setColor(bgColor);
				containerBg.setCornerRadius(dp(act, 24));
				dialogContainer.setBackground(containerBg);
				ScrollView scrollRoot = new ScrollView(act);
				scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);
				LinearLayout root = new LinearLayout(act);
				root.setOrientation(LinearLayout.VERTICAL);
				root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));
				TextView title = new TextView(act);
				title.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_verify_dialog_title"));
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				title.setTextColor(titleColor);
				title.setTypeface(null, Typeface.BOLD);
				title.setGravity(Gravity.CENTER);
				root.addView(title);
				final TextView subtitle = new TextView(act);
				subtitle.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_verify_subtitle"));
				subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				subtitle.setTextColor(hintColor);
				subtitle.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				subtitleLp.topMargin = dp(act, 12);
				subtitleLp.bottomMargin = dp(act, 8);
				root.addView(subtitle, subtitleLp);
				final TextView hintTv = new TextView(act);
				String hintText = (customHint != null && !customHint.isEmpty())
						? customHint
						: LocalizedStringProvider.getInstance().get(context, "pin_lock_verify_hint");
				hintTv.setText(hintText);
				hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				hintTv.setTextColor(hintColor);
				hintTv.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				hintLp.bottomMargin = dp(act, 96);
				root.addView(hintTv, hintLp);
				final String originalPinHintText = hintText;
				final EditText passwordInput = new EditText(act);
				passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
				passwordInput.setHint(LocalizedStringProvider.getInstance().get(context, "pin_lock_hint"));
				passwordInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				passwordInput.setTextColor(textColor);
				passwordInput.setHintTextColor(hintColor);
				passwordInput.setGravity(Gravity.CENTER);
				passwordInput.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));
				passwordInput.setSingleLine(true);
				passwordInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
				GradientDrawable editBg = new GradientDrawable();
				editBg.setColor(editBgColor);
				editBg.setCornerRadius(dp(act, 8));
				editBg.setStroke(dp(act, 1), dividerColor);
				passwordInput.setBackground(editBg);
				LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				inputLp.bottomMargin = dp(act, 64);
				root.addView(passwordInput, inputLp);
				passwordInput.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						String text = s.toString();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < text.length(); i++) {
							char c = text.charAt(i);
							if (c >= '0' && c <= '9') {
								sb.append(c);
							}
						}
						String filtered = sb.toString();
						if (!text.equals(filtered)) {
							passwordInput.setText(filtered);
							passwordInput.setSelection(filtered.length());
						}
					}
				});
				currentCancelBtn = new Button(act);
				applyClickAnim(currentCancelBtn);
				currentCancelBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_cancel"));
				currentCancelBtn.setTextColor(btnTextColor);
				currentCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				currentCancelBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable cancelBg = new GradientDrawable();
				cancelBg.setColor(btnBgColor);
				cancelBg.setCornerRadius(dp(act, 12));
				currentCancelBtn.setBackground(cancelBg);
				currentCancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout btnRow = new LinearLayout(act);
				btnRow.setOrientation(LinearLayout.HORIZONTAL);
				btnRow.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				btnRowLp.topMargin = dp(act, 64);
				btnRowLp.bottomMargin = dp(act, 8);
				root.addView(btnRow, btnRowLp);
				LinearLayout.LayoutParams cancelBtnLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				cancelBtnLp.rightMargin = dp(act, 8);
				btnRow.addView(currentCancelBtn, cancelBtnLp);
				final TextView securityWarningTv = new TextView(act);
				securityWarningTv.setText(LocalizedStringProvider.getInstance().get(context, "security_warning_text"));
				securityWarningTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				securityWarningTv.setTextColor(0xFFFF0000);
				securityWarningTv.setGravity(Gravity.CENTER);
				securityWarningTv.setTypeface(null, Typeface.BOLD);
				LinearLayout.LayoutParams warningLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				warningLp.topMargin = dp(act, 12);
				root.addView(securityWarningTv, warningLp);
				boolean hasPasswordSet = secureStorage.getSecureBoolean(SecurePasswordStorage.KEY_PASSWORD_SET, false);
				if (hasPasswordSet) {
					boolean patternConsistent = secureStorage
							.verifyConsistency(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
					boolean pinConsistent = secureStorage.verifyConsistency(SecurePasswordStorage.KEY_PIN_PASSWORD);
					boolean passwordSetConsistent = secureStorage
							.verifyConsistency(SecurePasswordStorage.KEY_PASSWORD_SET);
					if (patternConsistent && pinConsistent && passwordSetConsistent) {
						securityWarningTv.setVisibility(View.GONE);
					} else {
						securityWarningTv.setVisibility(View.VISIBLE);
					}
				} else {
					securityWarningTv.setVisibility(View.GONE);
				}
				currentCancelBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						currentVerifyDialog.dismiss();
						if (listener != null) {
							listener.onCancelled();
						}
					}
				});
				final Button confirmBtn = new Button(act);
				applyClickAnim(confirmBtn);
				confirmBtn.setText(LocalizedStringProvider.getInstance().get(context, "dialog_ok"));
				confirmBtn.setTextColor(okBtnTextColor);
				confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				confirmBtn.setTypeface(null, Typeface.BOLD);
				GradientDrawable confirmBg = new GradientDrawable();
				confirmBg.setColor(okBtnBgColor);
				confirmBg.setCornerRadius(dp(act, 12));
				confirmBtn.setBackground(confirmBg);
				confirmBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
				LinearLayout.LayoutParams confirmBtnLp = new LinearLayout.LayoutParams(0,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
				confirmBtnLp.leftMargin = dp(act, 8);
				btnRow.addView(confirmBtn, confirmBtnLp);
				confirmBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String inputPassword = passwordInput.getText().toString();
						if (inputPassword.length() < 4) {
							hintTv.setText(LocalizedStringProvider.getInstance().get(context, "pin_lock_too_short"));
							hintTv.setTextColor(0xFFFF0000);
							passwordInput.setText("");
							passwordInput.requestFocus();
							vibrate(50);
							return;
						}
						if (isLockedOut()) {
							passwordInput.setEnabled(false);
							passwordInput.setFocusable(false);
							confirmBtn.setEnabled(false);
							currentCancelBtn.setEnabled(false);
							long remainingSeconds = getRemainingLockoutTime();
							String message = String.format(
									LocalizedStringProvider.getInstance().get(context, "pin_lock_wait_seconds"),
									remainingSeconds);
							hintTv.setText(message);
							hintTv.setTextColor(0xFFFF0000);
							startCountdown(act, hintTv, originalPinHintText);
							return;
						}
						boolean isCorrect = verifyPinPassword(inputPassword);
						if (isCorrect) {
							resetFailureCount();
							hintTv.setText(
									LocalizedStringProvider.getInstance().get(context, "pin_lock_verify_success"));
							hintTv.setTextColor(0xFF4CAF50);
							if (listener != null) {
								listener.onVerifySuccess();
								listener.onPasswordReset();
							}
							hintTv.postDelayed(new Runnable() {
								@Override
								public void run() {
									currentVerifyDialog.dismiss();
									if (!isClearingPassword && !isVerifyMode) {
										if (targetPasswordType == PASSWORD_TYPE_PATTERN) {
											showSetPasswordDialog();
										} else {
											showSetPinPasswordDialog();
										}
									}
									isClearingPassword = false;
									isVerifyMode = false;
								}
							}, 300); 
						} else {
							incrementFailureCount();
							if (isLockedOut()) {
								vibrate(50);
								passwordInput.setText("");
								passwordInput.setEnabled(false);
								passwordInput.setFocusable(false);
								currentCancelBtn.setEnabled(false);
								currentCancelBtn.setClickable(false);
								currentCancelBtn.setFocusable(false);
								confirmBtn.setEnabled(false);
								confirmBtn.setClickable(false);
								confirmBtn.setFocusable(false);
								long remainingSeconds = getRemainingLockoutTime();
								String message = String.format(
										LocalizedStringProvider.getInstance().get(context, "pin_lock_wait_seconds"),
										remainingSeconds);
								hintTv.setText(message);
								hintTv.setTextColor(0xFFFF0000);
								startCountdown(act, hintTv, originalPinHintText);
							} else {
								int continuousFailureCount = secureStorage
										.getSecureInt(SecurePasswordStorage.KEY_CONTINUOUS_FAILURE_COUNT, 0);
								int remainingAttempts = MAX_CONTINUOUS_FAILURES - continuousFailureCount;
								String message = String.format(
										LocalizedStringProvider.getInstance().get(context, "pin_lock_attempts_left"),
										remainingAttempts);
								hintTv.setText(message);
								hintTv.setTextColor(0xFFFF0000);
								vibrate(50);
								passwordInput.setText("");
								passwordInput.requestFocus();
							}
						}
					}
				});
				scrollRoot.addView(root);
				dialogContainer.addView(scrollRoot);
				currentVerifyDialog.setContentView(dialogContainer);
				Window window = currentVerifyDialog.getWindow();
				if (window != null) {
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (int) (metrics.widthPixels * 0.85);
					WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
					layoutParams.copyFrom(window.getAttributes());
					layoutParams.width = width;
					layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
					layoutParams.gravity = Gravity.CENTER;
					window.setAttributes(layoutParams);
				}
				currentVerifyDialog.show();
				if (isLockedOut()) {
					passwordInput.setEnabled(false);
					passwordInput.setFocusable(false);
					currentCancelBtn.setEnabled(false);
					currentCancelBtn.setClickable(false);
					currentCancelBtn.setFocusable(false);
					confirmBtn.setEnabled(false);
					confirmBtn.setClickable(false);
					confirmBtn.setFocusable(false);
					long remainingSeconds = getRemainingLockoutTime();
					String message = String.format(
							LocalizedStringProvider.getInstance().get(context, "pin_lock_wait_seconds"),
							remainingSeconds);
					hintTv.setText(message);
					hintTv.setTextColor(0xFFFF0000);
					startCountdown(act, hintTv, originalPinHintText);
				}
				animateDialogEntrance(root, act);
			}
		});
	}
	private boolean verifyPassword(List<Integer> inputPattern) {
		try {
			String storedHash = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PATTERN_PASSWORD);
			if (storedHash == null || storedHash.isEmpty()) {
				return false;
			}
			String inputPasswordStr = patternToString(inputPattern);
			return verifyPatternPassword(inputPasswordStr, storedHash);
		} catch (Exception e) {
			return false;
		}
	}
	private boolean verifyPinPassword(String inputPassword) {
		try {
			String storedHash = secureStorage.getSecureValue(SecurePasswordStorage.KEY_PIN_PASSWORD);
			if (storedHash == null || storedHash.isEmpty()) {
				return false;
			}
			return securePasswordHelper.verify(inputPassword, storedHash);
		} catch (Exception e) {
			return false;
		}
	}
	public static class PatternLockView extends View {
		private static final int GRID_SIZE = 3;
		private static final int MIN_PATTERN_LENGTH = 4;
		private static final int DOT_RADIUS = 12;
		private static final int DOT_RADIUS_SELECTED = 16;
		private static final int LINE_WIDTH = 4;
		private Point[] dots = new Point[GRID_SIZE * GRID_SIZE];
		private int dotSize = 0;
		private int gapSize = 0;
		private List<Integer> pattern = new ArrayList<>();
		private boolean isDrawing = false;
		private int primaryColor = Color.BLUE;
		private int errorColor = Color.RED;
		private int dotColor = Color.GRAY;
		private int patternColor = primaryColor;
		private OnPatternListener listener;
		private VibratorCallback vibratorCallback;
		public interface OnPatternListener {
			void onPatternStart();
			void onPatternDetected(List<Integer> pattern);
			void onPatternCleared();
		}
		public interface VibratorCallback {
			void onVibrate(long duration);
		}
		public PatternLockView(Context context) {
			super(context);
			init();
		}
		public void setVibratorCallback(VibratorCallback callback) {
			this.vibratorCallback = callback;
		}
		private void init() {
			for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
				dots[i] = new Point();
			}
		}
		public void setPatternColor(int color) {
			this.primaryColor = color;
			this.patternColor = color;
			invalidate();
		}
		public void setDotColor(int color) {
			this.dotColor = color;
			invalidate();
		}
		public void setOnPatternListener(OnPatternListener listener) {
			this.listener = listener;
		}
		public void clearPattern() {
			pattern.clear();
			isDrawing = false;
			patternColor = primaryColor;
			invalidate();
			if (listener != null) {
				listener.onPatternCleared();
			}
		}
		public List<Integer> getPattern() {
			return new ArrayList<>(pattern);
		}
		public boolean isValidPattern() {
			return pattern.size() >= MIN_PATTERN_LENGTH;
		}
		public void showSuccess() {
			patternColor = primaryColor;
			invalidate();
		}
		public void showError() {
			patternColor = errorColor;
			invalidate();
		}
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			int padding = dp(getContext(), 32);
			int availableSize = Math.min(w, h) - padding * 2;
			gapSize = availableSize / GRID_SIZE;
			dotSize = dp(getContext(), 8);
			for (int i = 0; i < GRID_SIZE; i++) {
				for (int j = 0; j < GRID_SIZE; j++) {
					int index = i * GRID_SIZE + j;
					dots[index].x = padding + j * gapSize + gapSize / 2;
					dots[index].y = padding + i * gapSize + gapSize / 2;
				}
			}
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(LINE_WIDTH);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeCap(Paint.Cap.ROUND);
			for (int i = 0; i < dots.length; i++) {
				if (pattern.contains(i)) {
					paint.setColor(patternColor);
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(dots[i].x, dots[i].y, dp(getContext(), DOT_RADIUS_SELECTED), paint);
				} else {
					paint.setColor(dotColor);
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(dots[i].x, dots[i].y, dp(getContext(), DOT_RADIUS), paint);
				}
			}
			if (pattern.size() > 0) {
				paint.setColor(patternColor);
				paint.setStyle(Paint.Style.STROKE);
				for (int i = 0; i < pattern.size() - 1; i++) {
					int current = pattern.get(i);
					int next = pattern.get(i + 1);
					canvas.drawLine(dots[current].x, dots[current].y, dots[next].x, dots[next].y, paint);
				}
				if (isDrawing && lastTouchX != -1 && lastTouchY != -1) {
					int lastPoint = pattern.get(pattern.size() - 1);
					canvas.drawLine(dots[lastPoint].x, dots[lastPoint].y, lastTouchX, lastTouchY, paint);
				}
			}
		}
		private int lastTouchX = -1;
		private int lastTouchY = -1;
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (!isEnabled()) {
				return false;
			}
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN :
					lastTouchX = (int) x;
					lastTouchY = (int) y;
					handleTouch(x, y);
					break;
				case MotionEvent.ACTION_MOVE :
					lastTouchX = (int) x;
					lastTouchY = (int) y;
					int oldPatternSize = pattern.size();
					handleTouch(x, y);
					if (pattern.size() == oldPatternSize && isDrawing) {
						invalidate();
					}
					break;
				case MotionEvent.ACTION_UP :
				case MotionEvent.ACTION_CANCEL :
					lastTouchX = -1;
					lastTouchY = -1;
					if (isDrawing) {
						isDrawing = false;
						if (listener != null) {
							listener.onPatternDetected(getPattern());
						}
					}
					break;
			}
			return true;
		}
		private void handleTouch(float x, float y) {
			int touchRadius = dp(getContext(), 30);
			for (int i = 0; i < dots.length; i++) {
				float distance = (float) Math.sqrt(Math.pow(x - dots[i].x, 2) + Math.pow(y - dots[i].y, 2));
				if (distance < touchRadius && !pattern.contains(i)) {
					if (!isDrawing) {
						isDrawing = true;
						if (listener != null) {
							listener.onPatternStart();
						}
					}
					pattern.add(i);
					if (vibratorCallback != null) {
						vibratorCallback.onVibrate(15);
					}
					invalidate();
					break;
				}
			}
		}
	}
}