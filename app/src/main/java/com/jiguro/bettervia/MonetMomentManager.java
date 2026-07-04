package com.jiguro.bettervia;

import android.animation.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.android.apksig.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;
import java.util.*;
import java.util.zip.*;
import org.json.*;

public class MonetMomentManager {

	private final Hook host;

	public MonetMomentManager(Hook host) {
		this.host = host;
	}

	private int dp(Context c, int d) { return host.dp(c, d); }
	private int getBgColor(Context c) { return host.getBgColor(c); }
	private int getTitleColor(Context c) { return host.getTitleColor(c); }
	private int getTextColor(Context c) { return host.getTextColor(c); }
	private int getHintColor(Context c) { return host.getHintColor(c); }
	private int getDividerColor(Context c) { return host.getDividerColor(c); }
	private int getBtnBgColor(Context c) { return host.getBtnBgColor(c); }
	private int getBtnTextColor(Context c) { return host.getBtnTextColor(c); }
	private int getOkBtnBgColor(Context c) { return host.getOkBtnBgColor(c); }
	private int getOkBtnTextColor(Context c) { return host.getOkBtnTextColor(c); }
	private int getSwitchOnColor(Context c) { return host.getSwitchOnColor(c); }
	private int getSwitchOffColor(Context c) { return host.getSwitchOffColor(c); }
	private int getItemBgColor(Context c) { return host.getItemBgColor(c); }
	private int getEditBgColor(Context c) { return host.getEditBgColor(c); }
	private Activity getActivityFrom(Context c) { return host.getActivityFrom(c); }
	private GradientDrawable getRoundBg(Context c, int color, int r) { return host.getRoundBg(c, color, r); }
	private void applyClickAnim(View v) { host.applyClickAnim(v); }
	private void jiguroMessageWithContext(Context c, String m) { host.jiguroMessageWithContext(c, m); }
	private String getPrefString(Context c, String k, String d) { return host.getPrefString(c, k, d); }
	private int getPrefInt(Context c, String k, int d) { return host.getPrefInt(c, k, d); }
	private void putPrefString(Context c, String k, String v) { host.putPrefString(c, k, v); }
	private void putPrefInt(Context c, String k, int v) { host.putPrefInt(c, k, v); }
	private void animateDialogEntrance(ViewGroup root, Activity act) { host.animateDialogEntrance(root, act); }
	private void showMonetBasePopup(Context c, View a, String[] i, Hook.SourceSelectedCallback cb) { host.showMonetBasePopup(c, a, i, cb); }

	private static void putPrefBoolean(Context c, String k, boolean v) { Hook.putPrefBoolean(c, k, v); }
	private static void bvLog(String m) { Hook.bvLog(m); }
	private static boolean getPrefBoolean(Context c, String k, boolean d) { return Hook.getPrefBoolean(c, k, d); }

	private static final String KEY_MONET_BASE = "monet_base_version";
	private static final String KEY_MONET_PKG = "monet_package_name";
	private static final String KEY_MONET_VER_NAME = "monet_version_name";
	private static final String KEY_MONET_VER_CODE = "monet_version_code";
	private static final String KEY_MONET_USE_ICON = "monet_use_icon";
	private static final String KEY_MONET_MAKE_LITE = "monet_make_lite";
	private static final String KEY_MONET_SIGN_SCHEME = "monet_sign_scheme";
	private static final String KEY_MONET_OUTPUT_LOCATION = "monet_output_location";

	private static final int MONET_BUFFER_SIZE = 8192;
	private volatile boolean monetProcessing = false;
	private volatile boolean monetCancelled = false;

	private static final String MONET_JSON_URL_VERCEL = "https://raw.196104.xyz/monet.json";
	private static final String MONET_JSON_URL_GITHUB = "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/monet.json";

	private static final String NEWEST_JSON_URL_VERCEL = "https://raw.196104.xyz/newest.json";
	private static final String NEWEST_JSON_URL_GITHUB = "https://raw.githubusercontent.com/JiGuroLGC/CDN/main/newest.json";

	private String generatedApkPath = null;

	public void addMonetMomentItem(LinearLayout parent, final Activity act, final Context ctx) {
		bvLog("[BetterVia] addMonetMomentItem 方法被调用");
		LinearLayout container = new LinearLayout(ctx);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(0, dp(ctx, 8), 0, dp(ctx, 8));

		LinearLayout hor = new LinearLayout(ctx);
		hor.setOrientation(LinearLayout.HORIZONTAL);
		hor.setGravity(Gravity.CENTER_VERTICAL);

		TextView tv = new TextView(ctx);
		tv.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_title"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(getTextColor(ctx));
	
		hor.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

		TextView configBtn = new TextView(ctx);
		applyClickAnim(configBtn);
		configBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_config"));
		configBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		configBtn.setPadding(dp(ctx, 12), dp(ctx, 6), dp(ctx, 12), dp(ctx, 6));
		configBtn.setBackground(getRoundBg(ctx, getBtnBgColor(ctx), 8));
	
		configBtn.setTextColor(getBtnTextColor(ctx));
	
		configBtn.setClickable(true);
		configBtn.setFocusable(true);
	
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					bvLog("[BetterVia] 莫奈时刻配置按钮被点击");
					showMonetMomentDialog(ctx);
				} catch (Exception e) {
					bvLog("[BetterVia] 莫奈时刻配置按钮点击时发生异常: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	
		LinearLayout.LayoutParams configBtnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		hor.addView(configBtn, configBtnLp);

		TextView hintTv = new TextView(ctx);
		hintTv.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_hint"));
		hintTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		hintTv.setTextColor(getHintColor(ctx));
	
		hintTv.setPadding(0, dp(ctx, 4), 0, 0);

		container.addView(hor);
		container.addView(hintTv);
		parent.addView(container);
	}

	private void showMonetMomentDialog(final Context ctx) {
		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			return;
		}

		final EditText[] versionInfoEdits = new EditText[2];

		showMonetMomentDialogInternal(ctx, act, null, versionInfoEdits);

		new Thread(new Runnable() {
			@Override
			public void run() {
				ViaVersionInfo latestVersionInfo = null;
				try {
					latestVersionInfo = fetchLatestViaVersion(ctx);
				} catch (Exception e) {
					bvLog("[BetterVia] 获取最新版本信息时发生异常: " + e.getMessage());
					e.printStackTrace();
				}

				final ViaVersionInfo finalVersionInfo = latestVersionInfo;
				bvLog("[BetterVia] 版本信息获取完成，版本名: "
						+ (finalVersionInfo != null ? finalVersionInfo.versionName : "null"));

				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (act.isFinishing() || act.isDestroyed()) {
							return;
						}

						if (finalVersionInfo != null && finalVersionInfo.versionName != null
								&& versionInfoEdits[0] != null) {
							versionInfoEdits[0].setText(finalVersionInfo.versionName);
						}

						if (finalVersionInfo != null && finalVersionInfo.versionCode != null
								&& versionInfoEdits[1] != null) {
							versionInfoEdits[1].setText(finalVersionInfo.versionCode);
						}
					}
				});
			}
		}).start();
	}

	private void showMonetMomentDialogInternal(final Context ctx, final Activity act,
			final ViaVersionInfo latestVersionInfo, final EditText[] versionInfoEdits) {
		if (act.isFinishing() || act.isDestroyed()) {
			return;
		}

		final int bgColor = getBgColor(ctx);
		final int titleColor = getTitleColor(ctx);
		final int textColor = getTextColor(ctx);
		final int hintColor = getHintColor(ctx);
		final int btnBgColor = getBtnBgColor(ctx);
		final int btnTextColor = getBtnTextColor(ctx);
		final int okBtnBgColor = getOkBtnBgColor(ctx);
		final int okBtnTextColor = getOkBtnTextColor(ctx);
		final int itemBgColor = getItemBgColor(ctx);
		final int editBgColor = getEditBgColor(ctx);

		final Dialog dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);

		FrameLayout dialogContainer = new FrameLayout(act);
		GradientDrawable containerBg = new GradientDrawable();
		containerBg.setColor(bgColor);
		containerBg.setCornerRadius(dp(act, 24));
		dialogContainer.setBackground(containerBg);

		ScrollView scrollRoot = new ScrollView(act);
		scrollRoot.setOverScrollMode(View.OVER_SCROLL_NEVER);

		final LinearLayout root = new LinearLayout(act);
		root.setOrientation(LinearLayout.VERTICAL);
	
		root.setPadding(dp(act, 24), dp(act, 40), dp(act, 24), dp(act, 24));

		TextView title = new TextView(act);
		title.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_dialog_title"));
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
		title.setTextColor(titleColor);
		title.setTypeface(null, Typeface.BOLD);
		title.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLp.bottomMargin = dp(act, 6);
		root.addView(title, titleLp);

		TextView subtitle = new TextView(act);
		subtitle.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_hint"));
		subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		subtitle.setTextColor(hintColor);
		subtitle.setGravity(Gravity.CENTER);
	
		subtitle.setPadding(0, 0, 0, dp(act, 36));
		root.addView(subtitle);

		if (Build.VERSION.SDK_INT < 31) {
			LinearLayout warningCard = new LinearLayout(act);
			warningCard.setOrientation(LinearLayout.HORIZONTAL);
			warningCard.setGravity(Gravity.CENTER_VERTICAL);
			warningCard.setPadding(dp(act, 16), dp(act, 12), dp(act, 16), dp(act, 12));

			GradientDrawable cardBg = new GradientDrawable();
			cardBg.setColor(0xFFFF5252);
			cardBg.setCornerRadius(dp(act, 12));
			warningCard.setBackground(cardBg);

			ImageView icon = new ImageView(act);
		
			icon.setImageResource(android.R.drawable.stat_sys_warning);
			icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
			LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(act, 20), dp(act, 20));
			iconLp.rightMargin = dp(act, 10);
			warningCard.addView(icon, iconLp);

			TextView warningText = new TextView(act);
			warningText.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_version_warning"));
			warningText.setTextColor(Color.WHITE);
			warningText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			warningText.setTypeface(null, Typeface.BOLD);
			warningCard.addView(warningText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

			LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			cardLp.setMargins(0, 0, 0, dp(act, 16));
			root.addView(warningCard, cardLp);
		}

		addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_principle_title"), true);
		addMonetInfoCard(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_principle_content"));

		addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_notes_title"), false);
		addMonetInfoCard(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_notes_content"));

		addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_config_section"), false);

		LinearLayout configContainer = new LinearLayout(act);
		configContainer.setOrientation(LinearLayout.VERTICAL);
		configContainer.setPadding(dp(act, 12), dp(act, 12), dp(act, 12), dp(act, 12));
		GradientDrawable configBg = new GradientDrawable();
		configBg.setColor(itemBgColor);
		configBg.setCornerRadius(dp(act, 8));
	
		configBg.setStroke(dp(act, 1), getDividerColor(ctx));
		configContainer.setBackground(configBg);

		TextView baseVerLabel = new TextView(act);
		baseVerLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_base_ver"));
		baseVerLabel.setTextColor(hintColor);
		baseVerLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		baseVerLabel.setTypeface(null, Typeface.BOLD);
		baseVerLabel.setPadding(0, 0, 0, dp(act, 4));
		configContainer.addView(baseVerLabel);

		final TextView baseVerSelector = new TextView(act);
		final int savedBaseIdx = getPrefInt(ctx, KEY_MONET_BASE, 0);
		final String[] baseItems = {LocalizedStringProvider.getInstance().get(ctx, "monet_base_cn"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_base_global")};
		baseVerSelector.setText(baseItems[savedBaseIdx]);
		baseVerSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		baseVerSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
	
		GradientDrawable selectorBg = new GradientDrawable();
		selectorBg.setColor(editBgColor);
		selectorBg.setCornerRadius(dp(act, 8));
		selectorBg.setStroke(dp(act, 1), getDividerColor(ctx));
		baseVerSelector.setBackground(selectorBg);
		baseVerSelector.setTextColor(textColor);
		LinearLayout.LayoutParams selectorLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		selectorLp.bottomMargin = dp(act, 12);
		configContainer.addView(baseVerSelector, selectorLp);

		final EditText pkgEdit = addMonetInput(act, configContainer,
				LocalizedStringProvider.getInstance().get(ctx, "monet_pkg_name"),
				getPrefString(ctx, KEY_MONET_PKG, savedBaseIdx == 0 ? "mark.via" : "mark.via.gp"));
		pkgEdit.setEnabled(false);
		pkgEdit.setTextColor(hintColor);

		baseVerSelector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMonetBasePopup(ctx, baseVerSelector, baseItems, new Hook.SourceSelectedCallback() {
					@Override
					public void onSelected(int pos) {
						baseVerSelector.setText(baseItems[pos]);
						putPrefInt(ctx, KEY_MONET_BASE, pos);
					
						pkgEdit.setText(pos == 0 ? "mark.via" : "mark.via.gp");
					}
				});
			}
		});

		final CheckBox useIconCb = new CheckBox(act);
		useIconCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_use_icon"));
		useIconCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		useIconCb.setTextColor(textColor);
		useIconCb.setChecked(getPrefBoolean(ctx, KEY_MONET_USE_ICON, false));
	
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			useIconCb.setButtonTintList(colorStateList);
		}
		LinearLayout.LayoutParams useIconCbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		useIconCbLp.bottomMargin = dp(act, 8);
		configContainer.addView(useIconCb, useIconCbLp);

		final CheckBox makeLiteCb = new CheckBox(act);
		makeLiteCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_make_lite"));
		makeLiteCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		makeLiteCb.setTextColor(textColor);
		makeLiteCb.setChecked(getPrefBoolean(ctx, KEY_MONET_MAKE_LITE, false));
	
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			makeLiteCb.setButtonTintList(colorStateList);
		}
		LinearLayout.LayoutParams makeLiteCbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		makeLiteCbLp.bottomMargin = dp(act, 12);
		configContainer.addView(makeLiteCb, makeLiteCbLp);

		final String monetVerName = (latestVersionInfo != null && latestVersionInfo.versionName != null)
				? latestVersionInfo.versionName
				: "7.2.0";
		final String monetVerCode = (latestVersionInfo != null && latestVersionInfo.versionCode != null)
				? latestVersionInfo.versionCode
				: "20260703";

		bvLog("[BetterVia] 莫奈时刻使用版本信息 - 版本名: " + monetVerName + ", 版本号: " + monetVerCode);

		final EditText verNameEdit = addMonetInput(act, configContainer,
				LocalizedStringProvider.getInstance().get(ctx, "monet_ver_name"), monetVerName);
		verNameEdit.setEnabled(false);
		verNameEdit.setTextColor(hintColor);
	
		if (versionInfoEdits != null && versionInfoEdits.length > 0) {
			versionInfoEdits[0] = verNameEdit;
		}

		final EditText verCodeEdit = addMonetInput(act, configContainer,
				LocalizedStringProvider.getInstance().get(ctx, "monet_ver_code"), monetVerCode);
		verCodeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
		verCodeEdit.setEnabled(false);
		verCodeEdit.setTextColor(hintColor);
	
		if (versionInfoEdits != null && versionInfoEdits.length > 1) {
			versionInfoEdits[1] = verCodeEdit;
		}

		TextView signSchemeLabel = new TextView(act);
		signSchemeLabel.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_sign_scheme"));
		signSchemeLabel.setTextColor(hintColor);
		signSchemeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		signSchemeLabel.setTypeface(null, Typeface.BOLD);
		signSchemeLabel.setPadding(0, 0, 0, dp(act, 4));
		configContainer.addView(signSchemeLabel);

		final TextView signSchemeSelector = new TextView(act);
		final int savedSignSchemeIdx = getPrefInt(ctx, KEY_MONET_SIGN_SCHEME, 0);
		final String[] signSchemeItems = {LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v2v3"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v2"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1v3"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v1"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v2v3"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v2"),
				LocalizedStringProvider.getInstance().get(ctx, "monet_sign_v3")};
		signSchemeSelector.setText(signSchemeItems[savedSignSchemeIdx]);
		signSchemeSelector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		signSchemeSelector.setPadding(dp(act, 12), dp(act, 8), dp(act, 12), dp(act, 8));
	
		GradientDrawable signSchemeBg = new GradientDrawable();
		signSchemeBg.setColor(editBgColor);
		signSchemeBg.setCornerRadius(dp(act, 8));
		signSchemeBg.setStroke(dp(act, 1), getDividerColor(ctx));
		signSchemeSelector.setBackground(signSchemeBg);
		signSchemeSelector.setTextColor(textColor);
		LinearLayout.LayoutParams signSchemeLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		signSchemeLp.bottomMargin = dp(act, 12);
		configContainer.addView(signSchemeSelector, signSchemeLp);

		signSchemeSelector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMonetBasePopup(ctx, signSchemeSelector, signSchemeItems, new Hook.SourceSelectedCallback() {
					@Override
					public void onSelected(int pos) {
						signSchemeSelector.setText(signSchemeItems[pos]);
						putPrefInt(ctx, KEY_MONET_SIGN_SCHEME, pos);
					}
				});
			}
		});

		LinearLayout.LayoutParams configLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		configLp.bottomMargin = dp(act, 16);
		root.addView(configContainer, configLp);

		addMonetSectionTitle(root, act, LocalizedStringProvider.getInstance().get(ctx, "monet_agreement_title"), false);
		TextView agreementText = new TextView(act);
		agreementText.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_agreement_content"));
		agreementText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		agreementText.setTextColor(hintColor);
		agreementText.setLineSpacing(dp(act, 3), 1.1f);
		LinearLayout.LayoutParams agreementLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		agreementLp.topMargin = dp(act, 8);
		root.addView(agreementText, agreementLp);

		final CheckBox agreeCb = new CheckBox(act);
		agreeCb.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_i_agree"));
		agreeCb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		agreeCb.setTextColor(textColor);
	
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int[][] states = new int[][]{new int[]{android.R.attr.state_checked},
					new int[]{-android.R.attr.state_checked}};
			int[] colors = new int[]{getSwitchOnColor(ctx), getSwitchOffColor(ctx)};
			ColorStateList colorStateList = new ColorStateList(states, colors);
			agreeCb.setButtonTintList(colorStateList);
		}
		LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cbLp.topMargin = dp(act, 8);
		root.addView(agreeCb, cbLp);

		LinearLayout btnRow = new LinearLayout(act);
		btnRow.setOrientation(LinearLayout.HORIZONTAL);
		btnRow.setGravity(Gravity.CENTER);
	
		LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		btnRowLp.topMargin = dp(act, 16);
		root.addView(btnRow, btnRowLp);

		Button cancelBtn = new Button(act);
		applyClickAnim(cancelBtn);
		cancelBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "dialog_cancel"));
		cancelBtn.setTextColor(btnTextColor);
		cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		cancelBtn.setTypeface(null, Typeface.BOLD);
	
		GradientDrawable cancelBg = new GradientDrawable();
		cancelBg.setColor(btnBgColor);
		cancelBg.setCornerRadius(dp(act, 12));
		cancelBtn.setBackground(cancelBg);
	
		cancelBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));

		final Button continueBtn = new Button(act);
		applyClickAnim(continueBtn);
		continueBtn.setText(LocalizedStringProvider.getInstance().get(ctx, "monet_continue"));
		continueBtn.setTextColor(okBtnTextColor);
		continueBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		continueBtn.setTypeface(null, Typeface.BOLD);
	
		GradientDrawable continueBg = new GradientDrawable();
		continueBg.setColor(0xFF9E9E9E);
		continueBg.setCornerRadius(dp(act, 12));
		continueBtn.setBackground(continueBg);
		continueBtn.setPadding(0, dp(act, 14), 0, dp(act, 14));
		continueBtn.setEnabled(false);

		LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		btnLp.rightMargin = dp(act, 8);
		btnRow.addView(cancelBtn, btnLp);
		btnLp.leftMargin = dp(act, 8);
		btnRow.addView(continueBtn, btnLp);

		scrollRoot.addView(root);
		dialogContainer.addView(scrollRoot);
		dialog.setContentView(dialogContainer);

		Window window = dialog.getWindow();
		if (window != null) {
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			DisplayMetrics metrics = new DisplayMetrics();
			act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int width = (int) (metrics.widthPixels * 0.9);
			WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
			layoutParams.copyFrom(window.getAttributes());
			layoutParams.width = width;
			layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			if (metrics.heightPixels < dp(act, 600)) {
				layoutParams.height = (int) (metrics.heightPixels * 0.85);
			}
			layoutParams.gravity = Gravity.CENTER;
			window.setAttributes(layoutParams);
		}

		agreeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				continueBtn.setEnabled(isChecked);
				GradientDrawable bg = new GradientDrawable();
				bg.setCornerRadius(dp(act, 12));
				if (isChecked) {
					bg.setColor(okBtnBgColor);
				} else {
					bg.setColor(0xFF9E9E9E);
				}
				continueBtn.setBackground(bg);
			}
		});

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		continueBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				putPrefString(ctx, KEY_MONET_PKG, pkgEdit.getText().toString());
				putPrefString(ctx, KEY_MONET_VER_NAME, verNameEdit.getText().toString());
				putPrefString(ctx, KEY_MONET_VER_CODE, verCodeEdit.getText().toString());
				putPrefBoolean(ctx, KEY_MONET_USE_ICON, useIconCb.isChecked());
				putPrefBoolean(ctx, KEY_MONET_MAKE_LITE, makeLiteCb.isChecked());
			
				int signSchemeIdx = 0;
				for (int i = 0; i < signSchemeItems.length; i++) {
					if (signSchemeSelector.getText().toString().equals(signSchemeItems[i])) {
						signSchemeIdx = i;
						break;
					}
				}
				putPrefInt(ctx, KEY_MONET_SIGN_SCHEME, signSchemeIdx);
				dialog.dismiss();
			
				startMonetMomentProcess(ctx);
			}
		});

		dialog.show();
	
		animateDialogEntrance(root, act);
	}

	private void addMonetSectionTitle(LinearLayout parent, Context ctx, String text, boolean isFirst) {
		TextView title = new TextView(ctx);
		title.setText(text);
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		title.setTextColor(getTextColor(ctx));
		title.setTypeface(null, Typeface.BOLD);
	
		title.setPadding(0, 0, 0, dp(ctx, 8));
		parent.addView(title);
	}

	private void addMonetInfoCard(LinearLayout parent, Context ctx, String text) {
		LinearLayout card = new LinearLayout(ctx);
		card.setOrientation(LinearLayout.VERTICAL);
		card.setPadding(dp(ctx, 12), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12));

		GradientDrawable bg = new GradientDrawable();
		bg.setColor(getItemBgColor(ctx));
		bg.setCornerRadius(dp(ctx, 8));
	
		if (!"dark".equals(host.getActualTheme(ctx))) {
			bg.setStroke(dp(ctx, 1), getDividerColor(ctx));
		}
		card.setBackground(bg);

		TextView content = new TextView(ctx);
		content.setText(text);
		content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		content.setTextColor(getTextColor(ctx));
		content.setLineSpacing(dp(ctx, 4), 1.2f);
		card.addView(content);

		LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		cardLp.bottomMargin = dp(ctx, 16);
		parent.addView(card, cardLp);
	}

	private EditText addMonetInput(Context ctx, LinearLayout parent, String label, String value) {
		TextView labelView = new TextView(ctx);
		labelView.setText(label);
		labelView.setTextColor(getHintColor(ctx));
		labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		labelView.setTypeface(null, Typeface.BOLD);
		labelView.setPadding(0, 0, 0, dp(ctx, 4));
		parent.addView(labelView);

		EditText editText = new EditText(ctx);
		editText.setText(value);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		editText.setTextColor(getTextColor(ctx));
		editText.setPadding(dp(ctx, 12), dp(ctx, 8), dp(ctx, 12), dp(ctx, 8));
		GradientDrawable editBg = new GradientDrawable();
		editBg.setColor(getEditBgColor(ctx));
		editBg.setCornerRadius(dp(ctx, 8));
		editBg.setStroke(dp(ctx, 1), getDividerColor(ctx));
		editText.setBackground(editBg);
		LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		editLp.bottomMargin = dp(ctx, 12);
		parent.addView(editText, editLp);
		return editText;
	}

	private static class MonetFileInfo {
		String id;
		String name;
		List<String> sources;
		String type;
		String targetPath;
		List<String> compatibleWith;

		public MonetFileInfo(String id, String name, List<String> sources, String type, String targetPath,
				List<String> compatibleWith) {
			this.id = id;
			this.name = name;
			this.sources = sources;
			this.type = type;
			this.targetPath = targetPath;
			this.compatibleWith = compatibleWith;
		}
	}

	private class MonetProgressDialog {
		private Dialog dialog;
		private TextView statusText;
		private TextView errorText;
		private ProgressBar progressBar;
		private Button cancelButton;
		private Activity activity;
		private Context context;

		public MonetProgressDialog(Activity act, Context ctx) {
			this.activity = act;
			this.context = ctx;
			createDialog();
		}

		private void createDialog() {
			dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);

			LinearLayout root = new LinearLayout(activity);
			root.setOrientation(LinearLayout.VERTICAL);
			root.setPadding(dp(activity, 24), dp(activity, 24), dp(activity, 24), dp(activity, 24));
			GradientDrawable bg = new GradientDrawable();
			bg.setColor(getBgColor(context));
		
			bg.setCornerRadius(dp(activity, 24));
			root.setBackground(bg);

			TextView title = new TextView(activity);
			title.setText(LocalizedStringProvider.getInstance().get(context, "monet_progress_title"));
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			title.setTextColor(getTitleColor(context));
		
			title.setTypeface(null, Typeface.BOLD);
			title.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			titleLp.bottomMargin = dp(activity, 16);
			root.addView(title, titleLp);

			progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
			progressBar.setMax(100);
			progressBar.setProgress(0);
		
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				progressBar.setProgressTintList(ColorStateList.valueOf(getOkBtnBgColor(context)));
			}
			LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			progressLp.bottomMargin = dp(activity, 12);
			root.addView(progressBar, progressLp);

			statusText = new TextView(activity);
			statusText.setText("");
			statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			statusText.setTextColor(getHintColor(context));
		
			statusText.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			statusLp.bottomMargin = dp(activity, 8);
			root.addView(statusText, statusLp);

			errorText = new TextView(activity);
			errorText.setText("");
			errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			errorText.setTextColor(0xFFFF5252);
		
			errorText.setGravity(Gravity.CENTER);
			errorText.setVisibility(View.GONE);
			LinearLayout.LayoutParams errorLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			errorLp.bottomMargin = dp(activity, 16);
			root.addView(errorText, errorLp);

			cancelButton = new Button(activity);
			cancelButton.setText(LocalizedStringProvider.getInstance().get(context, "monet_progress_cancel"));
			cancelButton.setTextColor(getBtnTextColor(context));
		
			cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			cancelButton.setTypeface(null, Typeface.BOLD);
			GradientDrawable btnBg = new GradientDrawable();
			btnBg.setColor(getBtnBgColor(context));
		
			btnBg.setCornerRadius(dp(activity, 12));
			cancelButton.setBackground(btnBg);
			LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			root.addView(cancelButton, btnLp);

			dialog.setContentView(root);

			Window win = dialog.getWindow();
			if (win != null) {
				win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				DisplayMetrics metrics = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int width = (int) (metrics.widthPixels * 0.85);
				WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
				layoutParams.copyFrom(win.getAttributes());
				layoutParams.width = width;
				layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
				layoutParams.gravity = Gravity.CENTER;
				win.setAttributes(layoutParams);
			}
		}

		public void show() {
			dialog.show();
		}

		public void dismiss() {
			dialog.dismiss();
		}

		public void updateProgress(final String message, final int progress) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (dialog != null && dialog.isShowing()) {
						statusText.setText(message);
						progressBar.setProgress(progress);
						errorText.setVisibility(View.GONE);
					}
				}
			});
		}

		public void showError(final String errorMessage) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (dialog != null && dialog.isShowing()) {
						errorText.setText(errorMessage);
						errorText.setVisibility(View.VISIBLE);
					}
				}
			});
		}

		public void setCancelButtonListener(final View.OnClickListener listener) {
			cancelButton.setOnClickListener(listener);
		}

		public boolean isShowing() {
			return dialog != null && dialog.isShowing();
		}
	}

	private void startMonetMomentProcess(final Context ctx) {
		if (monetProcessing) {
			jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "monet_error_processing"));
			return;
		}

		monetProcessing = true;
		monetCancelled = false;

		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			monetProcessing = false;
			return;
		}

		final MonetProgressDialog progressDialog = new MonetProgressDialog(act, ctx);
		progressDialog.setCancelButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				monetCancelled = true;
				monetProcessing = false;
				progressDialog.dismiss();
				jiguroMessageWithContext(ctx, LocalizedStringProvider.getInstance().get(ctx, "monet_error_cancelled"));
			}
		});

		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					processMonetMoment(ctx, progressDialog);
				
					monetProcessing = false;
				} catch (final Exception e) {
					bvLog("[BetterVia] 莫奈时刻处理异常: " + Log.getStackTraceString(e));
					monetProcessing = false;
					monetCancelled = false;
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.showError(
									String.format(LocalizedStringProvider.getInstance().get(ctx, "monet_error_unknown"),
											e.getMessage()));
							jiguroMessageWithContext(ctx,
									String.format(LocalizedStringProvider.getInstance().get(ctx, "monet_error_unknown"),
											e.getMessage()));
						}
					});
				}
			}
		}).start();
	}

	private void processMonetMoment(final Context ctx, final MonetProgressDialog progressDialog) throws Exception {
		final Activity act = getActivityFrom(ctx);
		if (act == null) {
			throw new Exception("Activity is null");
		}

		int outputLocationIdx = getPrefInt(ctx, KEY_MONET_OUTPUT_LOCATION, 0);
		String packageName = ctx.getPackageName();
		File tempDir;
		if (outputLocationIdx == 0) {
		
			tempDir = new File("/storage/emulated/0/Android/data/" + packageName + "/files/BetterVia/temp/");
		} else {
		
			tempDir = new File("/data/user/0/" + packageName + "/files/BetterVia/temp/");
		}

		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		int baseVersionIdx = getPrefInt(ctx, KEY_MONET_BASE, 0);
		boolean useIcon = getPrefBoolean(ctx, KEY_MONET_USE_ICON, false);
		boolean makeLite = getPrefBoolean(ctx, KEY_MONET_MAKE_LITE, false);
		int signScheme = getPrefInt(ctx, KEY_MONET_SIGN_SCHEME, 0);

		try {
		
			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_cleaning"), 0);
			cleanMonetTempDir(tempDir);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}

			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_loading_config"),
					5);
			JSONObject configJson = loadMonetConfigJson(ctx);

			if (monetCancelled)
				return;
			progressDialog
					.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_apk"), 10);
			MonetFileInfo selectedApk = selectApkFile(configJson, baseVersionIdx, useIcon, makeLite);
			if (selectedApk == null) {
				progressDialog.showError(LocalizedStringProvider.getInstance().get(ctx, "monet_error_select_apk"));
				throw new Exception(LocalizedStringProvider.getInstance().get(ctx, "monet_error_select_apk"));
			}
			String apkSource = selectSource(ctx, selectedApk.sources);
			File apkFile = new File(tempDir, "downloaded.apk");
			downloadFile(apkSource, apkFile, new DownloadProgressCallback() {
				@Override
				public void onProgress(long downloaded, long total) {
					String downloadedMB = String.format("%.2f", downloaded / 1024.0 / 1024.0);
					String totalMB = String.format("%.2f", total / 1024.0 / 1024.0);
					progressDialog
							.updateProgress(
									String.format(LocalizedStringProvider.getInstance().get(ctx,
											"monet_status_downloading_apk"), downloadedMB, totalMB),
									10 + (int) (downloaded * 25 / total));
				}
			});

			if (monetCancelled)
				return;
			progressDialog.updateProgress(
					LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_sign_zip"), 40);
			MonetFileInfo keyFile = getKeyFile(configJson);
			if (keyFile == null) {
				progressDialog.showError("Key file not found in config");
				throw new Exception("Key file not found in config");
			}
			String keySource = selectSource(ctx, keyFile.sources);
			File keyZipFile = new File(tempDir, "test.zip");
			downloadFile(keySource, keyZipFile, new DownloadProgressCallback() {
				@Override
				public void onProgress(long downloaded, long total) {
					String downloadedMB = String.format("%.2f", downloaded / 1024.0 / 1024.0);
					String totalMB = String.format("%.2f", total / 1024.0 / 1024.0);
					progressDialog.updateProgress(
							String.format(
									LocalizedStringProvider.getInstance().get(ctx, "monet_status_downloading_sign_zip")
											+ " (%s/%s MB)",
									downloadedMB, totalMB),
							40 + (int) (downloaded * 10 / (total > 0 ? total : 1)));
				}
			});

			if (monetCancelled)
				return;
			progressDialog
					.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_extracting_sign"), 50);
			File keyDir = new File(tempDir, "keys");
			if (keyDir.exists()) {
				deleteDirectory(keyDir);
			}
			keyDir.mkdirs();
			extractZip(keyZipFile, keyDir);

			File pemFile = new File(keyDir, "test.x509.pem");
			File pk8File = new File(keyDir, "test.pk8");
			if (!pemFile.exists() || !pk8File.exists()) {
				progressDialog.showError("Signing files not found in zip file");
				throw new Exception("Signing files not found in zip file");
			}

			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_renaming_apk"),
					55);
			File baseApk = new File(tempDir, "base.apk");
			if (!apkFile.renameTo(baseApk)) {
				progressDialog.showError(LocalizedStringProvider.getInstance().get(ctx, "monet_error_rename"));
				throw new Exception(LocalizedStringProvider.getInstance().get(ctx, "monet_error_rename"));
			}

			if (monetCancelled)
				return;
			final File signedApk = new File(tempDir, "Moneted.apk");
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_signing"), 60);
			signApkWithApksigner(ctx, baseApk, signedApk, pemFile, pk8File, signScheme);

			generatedApkPath = signedApk.getAbsolutePath();

			if (monetCancelled)
				return;
			progressDialog.updateProgress(LocalizedStringProvider.getInstance().get(ctx, "monet_status_cleaning_temp"),
					70);
			deleteDirectory(keyDir);
			if (keyZipFile.exists()) {
				keyZipFile.delete();
			}
			if (baseApk.exists()) {
				baseApk.delete();
			}

			if (monetCancelled)
				return;
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					jiguroMessageWithContext(ctx,
							LocalizedStringProvider.getInstance().get(ctx, "monet_status_completed"));

					Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("application/vnd.android.package-archive");
					intent.putExtra(Intent.EXTRA_TITLE, "Via_Moneted.apk");

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					}

					try {
						act.startActivityForResult(intent, 0x2001);
					} catch (Exception e) {
						bvLog("[BetterVia] 打开文件选择器失败: " + e.getMessage());
						jiguroMessageWithContext(ctx,
								LocalizedStringProvider.getInstance().get(ctx, "monet_error_open_apk"));
					}
				}
			});
		} catch (Exception e) {
			progressDialog.showError(e.getMessage());
			throw e;
		}
	}

	private void cleanMonetTempDir(File tempDir) throws Exception {
		if (tempDir.exists()) {
			deleteDirectory(tempDir);
		}
		tempDir.mkdirs();
	}

	private void deleteDirectory(File dir) throws Exception {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteDirectory(file);
				}
			}
		}
		dir.delete();
	}

	private static class ViaVersionInfo {
		String versionName;
		String versionCode;

		ViaVersionInfo(String versionName, String versionCode) {
			this.versionName = versionName;
			this.versionCode = versionCode;
		}
	}

	private ViaVersionInfo fetchLatestViaVersion(Context ctx) {
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			String url = getPrefString(ctx, Hook.KEY_NETWORK_SOURCE, Hook.DEFAULT_NETWORK_SOURCE).equals(Hook.NETWORK_SOURCE_VERCEL)
					? NEWEST_JSON_URL_VERCEL
					: NEWEST_JSON_URL_GITHUB;

			bvLog("[BetterVia] 开始获取最新Via版本信息: " + url);

			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			bvLog("[BetterVia] HTTP响应码: " + responseCode);
			if (responseCode != HttpURLConnection.HTTP_OK) {
				bvLog("[BetterVia] 获取最新版本信息失败，HTTP响应码: " + responseCode);
				return null;
			}

			is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			String jsonString = sb.toString();
			bvLog("[BetterVia] 获取到的JSON内容: " + jsonString);
			if (jsonString == null || jsonString.trim().isEmpty()) {
				bvLog("[BetterVia] 获取最新版本信息失败，文件内容为空");
				return null;
			}

			JSONObject json = new JSONObject(jsonString);
			String versionName = json.optString("versionName", "");
			String versionCode = json.optString("versionCode", "");

			bvLog("[BetterVia] 解析版本信息 - 版本名: " + versionName + ", 版本号: " + versionCode);
			if (versionName.isEmpty() || versionCode.isEmpty()) {
				bvLog("[BetterVia] 获取最新版本信息失败，版本信息不完整");
				return null;
			}

			bvLog("[BetterVia] 获取最新版本信息成功: " + versionName + " (" + versionCode + ")");
			return new ViaVersionInfo(versionName, versionCode);
		} catch (Exception e) {
			bvLog("[BetterVia] 获取最新版本信息异常: " + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ignored) {
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private JSONObject loadMonetConfigJson(Context ctx) throws Exception {
		String url = getPrefString(ctx, Hook.KEY_NETWORK_SOURCE, Hook.DEFAULT_NETWORK_SOURCE).equals(Hook.NETWORK_SOURCE_VERCEL)
				? MONET_JSON_URL_VERCEL
				: MONET_JSON_URL_GITHUB;

		bvLog("[BetterVia] 加载莫奈配置: " + url);

		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("HTTP response code: " + responseCode);
			}

			is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			String jsonString = sb.toString();
			return new JSONObject(jsonString);
		} finally {
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private String selectSource(Context ctx, List<String> sources) {
		String networkSource = getPrefString(ctx, Hook.KEY_NETWORK_SOURCE, Hook.DEFAULT_NETWORK_SOURCE);
		if (networkSource.equals(Hook.NETWORK_SOURCE_VERCEL)) {
		
			for (String source : sources) {
				if (source.contains("raw.196104.xyz")) {
					return source;
				}
			}
		
			for (String source : sources) {
				if (source.contains("cdn.jsdelivr.net")) {
					return source;
				}
			}
		} else {
		
			for (String source : sources) {
				if (source.contains("github.com")) {
					return source;
				}
			}
		}
	
		return sources.get(0);
	}

	private MonetFileInfo selectApkFile(JSONObject configJson, int baseVersionIdx, boolean useIcon, boolean makeLite)
			throws Exception {
		try {
			JSONArray apkFiles = configJson.getJSONArray("apk_files");
			String basePrefix = baseVersionIdx == 0 ? "cn" : "gp";

			for (int i = 0; i < apkFiles.length(); i++) {
				JSONObject apkObj = apkFiles.getJSONObject(i);
				String id = apkObj.getString("id");

				if (!id.startsWith(basePrefix)) {
					continue;
				}

				boolean isNoIcon = id.contains("noicon");
				if (useIcon && isNoIcon) {
					continue;
				}
				if (!useIcon && !isNoIcon) {
					continue;
				}

				boolean isOptimize = id.contains("optimize");
				if (makeLite && !isOptimize) {
					continue;
				}
				if (!makeLite && isOptimize) {
					continue;
				}

				String name = apkObj.getString("name");
				JSONArray sourcesArray = apkObj.getJSONArray("sources");
				List<String> sources = new ArrayList<String>();
				for (int j = 0; j < sourcesArray.length(); j++) {
					sources.add(sourcesArray.getString(j));
				}

				return new MonetFileInfo(id, name, sources, "apk", "", null);
			}
		} catch (JSONException e) {
			throw new Exception("Failed to parse APK files: " + e.getMessage());
		}

		return null;
	}

	private MonetFileInfo getKeyFile(JSONObject configJson) throws Exception {
		try {
			JSONArray keyFiles = configJson.getJSONArray("key_files");
			for (int i = 0; i < keyFiles.length(); i++) {
				JSONObject keyObj = keyFiles.getJSONObject(i);
				String id = keyObj.getString("id");
				String name = keyObj.getString("name");
				JSONArray sourcesArray = keyObj.getJSONArray("sources");
				List<String> sources = new ArrayList<String>();
				for (int j = 0; j < sourcesArray.length(); j++) {
					sources.add(sourcesArray.getString(j));
				}

				return new MonetFileInfo(id, name, sources, "zip", "", null);
			}
		} catch (JSONException e) {
			throw new Exception("Failed to parse key files: " + e.getMessage());
		}

		return null;
	}

	private void signApkWithApksigner(Context ctx, File inputApk, File outputApk, File pemFile, File pk8File,
			int signScheme) throws Exception {
		bvLog("[BetterVia] 使用 apksig 库进行签名");

		boolean signV1 = (signScheme == 0 || signScheme == 1 || signScheme == 2 || signScheme == 3);
		boolean signV2 = (signScheme == 0 || signScheme == 1 || signScheme == 4 || signScheme == 5);
		boolean signV3 = (signScheme == 0 || signScheme == 2 || signScheme == 4 || signScheme == 6);

		bvLog("[BetterVia] 签名方案: V1=" + signV1 + ", V2=" + signV2 + ", V3=" + signV3);

		try {
		
			PrivateKey privateKey = readPrivateKey(pk8File);
			List<X509Certificate> certs = readCertificates(pemFile);

			ApkSigner.SignerConfig signerConfig = new ApkSigner.SignerConfig.Builder("CERT", privateKey, certs).build();

			List<ApkSigner.SignerConfig> signerConfigs = new ArrayList<ApkSigner.SignerConfig>();
			signerConfigs.add(signerConfig);

			ApkSigner.Builder builder = new ApkSigner.Builder(signerConfigs).setInputApk(inputApk)
					.setOutputApk(outputApk).setV1SigningEnabled(signV1)
					.setV2SigningEnabled(signV2).setV3SigningEnabled(signV3)
					.setOtherSignersSignaturesPreserved(false);

			builder.build().sign();
			bvLog("[BetterVia] APK 签名完成");

		} catch (Exception e) {
			bvLog("[BetterVia] 签名过程中出错: " + e.getMessage());
			throw e;
		}
	}

	private byte[] readFileToBytes(File file) throws IOException {
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		FileInputStream fis = new FileInputStream(file);
		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
		} finally {
			fis.close();
		}
		return bytes;
	}

	private PrivateKey readPrivateKey(File pk8File) throws Exception {
		byte[] keyBytes = readFileToBytes(pk8File);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	private List<X509Certificate> readCertificates(File pemFile) throws Exception {
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		FileInputStream fis = new FileInputStream(pemFile);
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection<? extends java.security.cert.Certificate> collection = cf.generateCertificates(fis);
			for (java.security.cert.Certificate cert : collection) {
				if (cert instanceof X509Certificate) {
					certs.add((X509Certificate) cert);
				}
			}
		} finally {
			fis.close();
		}
		return certs;
	}

	private interface DownloadProgressCallback {
		void onProgress(long downloaded, long total);
	}

	private void downloadFile(String url, File dest, DownloadProgressCallback callback) throws Exception {
		bvLog("[BetterVia] 下载文件: " + url + " -> " + dest.getAbsolutePath());

		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("HTTP response code: " + responseCode);
			}

			long total = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				total = conn.getContentLengthLong();
			}
			is = conn.getInputStream();
			fos = new FileOutputStream(dest);

			byte[] buffer = new byte[MONET_BUFFER_SIZE];
			int len;
			long downloaded = 0;
			while ((len = is.read(buffer)) != -1) {
				if (monetCancelled) {
					throw new Exception("Download cancelled");
				}
				fos.write(buffer, 0, len);
				downloaded += len;
				if (callback != null) {
					callback.onProgress(downloaded, total);
				}
			}

			bvLog("[BetterVia] 文件下载完成: " + dest.getAbsolutePath());
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private void extractZip(File apkFile, File destDir) throws Exception {
		bvLog("[BetterVia] 解压Zip: " + apkFile.getAbsolutePath() + " -> " + destDir.getAbsolutePath());

		ZipFile zipFile = new ZipFile(apkFile);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File file = new File(destDir, entry.getName());

			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(file);

				byte[] buffer = new byte[MONET_BUFFER_SIZE];
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}

				is.close();
				fos.close();
			}
		}

		zipFile.close();
		bvLog("[BetterVia] Zip解压完成");
	}

	public void handleSaveApkResult(final Activity activity, final Intent data) {
		final Uri uri = data.getData();
		if (generatedApkPath != null && uri != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						File sourceFile = new File(generatedApkPath);
						InputStream in = null;
						OutputStream out = null;
						try {
							in = new FileInputStream(sourceFile);
							out = activity.getContentResolver().openOutputStream(uri);
							byte[] buffer = new byte[8192];
							int len;
							while ((len = in.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
							out.flush();

							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									jiguroMessageWithContext(activity, LocalizedStringProvider.getInstance()
											.get(activity, "homepage_bg_saved"));
								}
							});
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (Exception ignored) {
								}
							}
							if (out != null) {
								try {
									out.close();
								} catch (Exception ignored) {
								}
							}
						}
					} catch (final Exception e) {
						bvLog("[BetterVia] 保存APK文件失败: " + e);
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								jiguroMessageWithContext(activity, "保存失败: " + e.getMessage());
							}
						});
					}
				}
			}).start();
		}
	}
}
