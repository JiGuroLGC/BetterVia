package com.jiguro.bettervia;

import android.content.*;
import org.json.*;

public class AnnouncementData {

	public static final int MODE_DIALOG_ONLY = 0;
	public static final int MODE_TOAST_ONLY = 1;
	public static final int MODE_DIALOG_AND_TOAST = 2;
	public static final int MODE_CELEBRATION = 3;

	public static final int ACTION_NONE = 0;
	public static final int ACTION_OPEN_VIA = 1;
	public static final int ACTION_EXIT_VIA = 2;
	public static final int ACTION_SHARE = 3;
	public static final int ACTION_OPEN_URL = 4;
	public static final int ACTION_DISMISS = 5;

	public String id;
	public int displayMode = MODE_DIALOG_ONLY;
	public String toastText = "";

	public String dialogTitle = "";
	public String dialogSubtitle = "";
	public String dialogContent = "";

	public boolean hasPositiveButton = true;
	public String positiveButtonText = "";
	public int positiveButtonAction = ACTION_DISMISS;
	public String positiveButtonUrl = "";

	public boolean hasNegativeButton = false;
	public String negativeButtonText = "";
	public int negativeButtonAction = ACTION_DISMISS;
	public String negativeButtonUrl = "";

	public boolean hasCheckbox = false;
	public String checkboxText = "";

	public static class ButtonAction {
		public String text;
		public int action;
		public String url;

		public ButtonAction(String text, int action, String url) {
			this.text = text;
			this.action = action;
			this.url = url;
		}
	}

	public static AnnouncementData fromJSON(JSONObject json) throws JSONException {
		AnnouncementData data = new AnnouncementData();

		data.id = json.optString("id", "");

		String modeStr = json.optString("displayMode", "dialog_only");
		if ("dialog_only".equals(modeStr)) {
			data.displayMode = MODE_DIALOG_ONLY;
		} else if ("toast_only".equals(modeStr)) {
			data.displayMode = MODE_TOAST_ONLY;
		} else if ("dialog_and_toast".equals(modeStr)) {
			data.displayMode = MODE_DIALOG_AND_TOAST;
		} else if ("celebration".equals(modeStr)) {
			data.displayMode = MODE_CELEBRATION;
		}

		data.toastText = getLocalizedString(json, "toastText");

		data.dialogTitle = getLocalizedString(json, "dialogTitle");
		data.dialogSubtitle = getLocalizedString(json, "dialogSubtitle");
		data.dialogContent = getLocalizedString(json, "dialogContent");

		if (json.has("positiveButton")) {
			JSONObject positiveBtn = json.getJSONObject("positiveButton");
			data.hasPositiveButton = positiveBtn.optBoolean("enabled", true);
			data.positiveButtonText = getLocalizedString(positiveBtn, "text");
			data.positiveButtonAction = parseActionType(positiveBtn.optString("action", "dismiss"));
			data.positiveButtonUrl = positiveBtn.optString("url", "");
		}

		if (json.has("negativeButton")) {
			JSONObject negativeBtn = json.getJSONObject("negativeButton");
			data.hasNegativeButton = negativeBtn.optBoolean("enabled", false);
			data.negativeButtonText = getLocalizedString(negativeBtn, "text");
			data.negativeButtonAction = parseActionType(negativeBtn.optString("action", "dismiss"));
			data.negativeButtonUrl = negativeBtn.optString("url", "");
		}

		if (json.has("checkbox")) {
			JSONObject checkbox = json.getJSONObject("checkbox");
			data.hasCheckbox = checkbox.optBoolean("enabled", false);
			data.checkboxText = getLocalizedString(checkbox, "text");
		}

		return data;
	}

	private static int parseActionType(String actionStr) {
		if ("none".equals(actionStr)) {
			return ACTION_NONE;
		} else if ("open_via".equals(actionStr)) {
			return ACTION_OPEN_VIA;
		} else if ("exit_via".equals(actionStr)) {
			return ACTION_EXIT_VIA;
		} else if ("share".equals(actionStr)) {
			return ACTION_SHARE;
		} else if ("open_url".equals(actionStr)) {
			return ACTION_OPEN_URL;
		} else if ("dismiss".equals(actionStr)) {
			return ACTION_DISMISS;
		}
		return ACTION_DISMISS;
	}

	private static String getLocalizedString(JSONObject obj, String key) throws JSONException {
		if (!obj.has(key)) {
			return "";
		}

		Object value = obj.get(key);

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof JSONObject) {
			JSONObject langObj = (JSONObject) value;

			if (langObj.has("zh-CN")) {
				return langObj.getString("zh-CN");
			} else if (langObj.has("zh-TW")) {
				return langObj.getString("zh-TW");
			} else if (langObj.has("en")) {
				return langObj.getString("en");
			}

			if (langObj.length() > 0) {
				return langObj.getString(langObj.keys().next());
			}
		}

		return "";
	}

	public String getLocalizedDialogTitle(Context ctx) {
		return getLocalizedStringFromContext(ctx, dialogTitle);
	}

	public String getLocalizedDialogSubtitle(Context ctx) {
		return getLocalizedStringFromContext(ctx, dialogSubtitle);
	}

	public String getLocalizedDialogContent(Context ctx) {
		return getLocalizedStringFromContext(ctx, dialogContent);
	}

	public String getLocalizedPositiveButton(Context ctx) {
		return getLocalizedStringFromContext(ctx, positiveButtonText);
	}

	public String getLocalizedNegativeButton(Context ctx) {
		return getLocalizedStringFromContext(ctx, negativeButtonText);
	}

	public String getLocalizedCheckboxText(Context ctx) {
		return getLocalizedStringFromContext(ctx, checkboxText);
	}

	public String getLocalizedToastText(Context ctx) {
		return getLocalizedStringFromContext(ctx, toastText);
	}

	private String getLocalizedStringFromContext(Context ctx, String value) {

		if (!value.trim().startsWith("{")) {
			return value;
		}

		try {
			JSONObject langObj = new JSONObject(value);
			String currentLang = Hook.getCurrentLanguageCodeStatic(ctx);

			if (langObj.has(currentLang)) {
				return langObj.getString(currentLang);
			}

			if (langObj.has("zh-CN")) {
				return langObj.getString("zh-CN");
			}

			if (langObj.has("zh-TW")) {
				return langObj.getString("zh-TW");
			}

			if (langObj.has("en")) {
				return langObj.getString("en");
			}

			if (langObj.length() > 0) {
				return langObj.getString(langObj.keys().next());
			}
		} catch (JSONException e) {

		}

		return value;
	}

	public boolean isValid() {

		if (id == null || id.isEmpty()) {
			return false;
		}

		if (displayMode == MODE_CELEBRATION) {
			return id != null && !id.isEmpty();
		}

		if (displayMode == MODE_DIALOG_ONLY || displayMode == MODE_DIALOG_AND_TOAST) {
			if (dialogTitle == null || dialogTitle.isEmpty()) {
				return false;
			}
			if (dialogContent == null || dialogContent.isEmpty()) {
				return false;
			}
		}

		if (displayMode == MODE_TOAST_ONLY || displayMode == MODE_DIALOG_AND_TOAST) {
			if (toastText == null || toastText.isEmpty()) {
				return false;
			}
		}

		if (hasPositiveButton && (positiveButtonText == null || positiveButtonText.isEmpty())) {
			return false;
		}

		if (hasNegativeButton && (negativeButtonText == null || negativeButtonText.isEmpty())) {
			return false;
		}

		if (hasCheckbox && (checkboxText == null || checkboxText.isEmpty())) {
			return false;
		}

		if (positiveButtonAction == ACTION_OPEN_URL && (positiveButtonUrl == null || positiveButtonUrl.isEmpty())) {
			return false;
		}
		if (negativeButtonAction == ACTION_OPEN_URL && (negativeButtonUrl == null || negativeButtonUrl.isEmpty())) {
			return false;
		}

		return true;
	}
}