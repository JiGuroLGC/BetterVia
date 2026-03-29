package com.jiguro.bettervia;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import de.robv.android.xposed.XposedBridge;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ThemeApplier {
	private static final String MODLE_FOLDER = "BetterVia";
	private static final String HOMEPAGE_HTML_FILE = "homepage2.html";
	private static final String HOMEPAGE_CSS_FILE = "homepage.css";
	private static final String SETTINGS_XML = "settings.xml";
	private static final String HTML_FIRST_SEARCH_PATTERN = 
		"<a\\s+class\\s*=\\s*\"logo\"\\s+href\\s*=\\s*\"\"\\s+onclick\\s*=\\s*\"javascript:window\\.via\\.cmd\\(257\\);\"\\s+title\\s*=\\s*\"[^\"]*\">";
	private static final String HTML_SECOND_SEARCH_PATTERN = 
		"</a\\s*><form\\s+onsubmit\\s*=\\s*\"return\\s+search\\(\\)\"\\s+class\\s*=\\s*\"search_bar\"\\s+title\\s*=\\s*\"[^\"]*\">";
	private static final String CSS_SEARCH_PATTERN = "\\.opSug_wpr td \\{ color: [^;]*; \\}\\}";
	private Context context;
	private Hook.ThemeInfo theme;
	private String packageName;
	private String filesDir;
	private String tempDir;
	private String sharedPrefsDir;
	public interface ThemeApplyCallback {
		void onSuccess();
		void onError(String message);
	}
	public ThemeApplier(Context ctx, Hook.ThemeInfo themeInfo) {
		this.context = ctx;
		this.theme = themeInfo;
		this.packageName = ctx.getPackageName();
		this.filesDir = "/data/user/0/" + packageName + "/files/";
		this.tempDir = filesDir + MODLE_FOLDER + "/temp/";
		this.sharedPrefsDir = "/data/user/0/" + packageName + "/shared_prefs/";
	}
	public void applyTheme(final ThemeApplyCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String htmlUrl = getThemeUrl(theme.htmlUrls);
					String cssUrl = getThemeUrl(theme.cssUrls);
					if (htmlUrl == null || cssUrl == null) {
						notifyError(callback, getLocalizedString(context, "theme_url_not_found"));
						return;
					}
					String themeHtmlContent = downloadFileContent(htmlUrl);
					String themeCssContent = downloadFileContent(cssUrl);
					if (themeHtmlContent == null || themeCssContent == null) {
						notifyError(callback, getLocalizedString(context, "homepage_theme_apply_failed"));
						return;
					}
					if (!createTempDirectory()) {
						notifyError(callback, getLocalizedString(context, "temp_dir_create_failed"));
						return;
					}
					boolean htmlSuccess = processHtmlFile(themeHtmlContent);
					if (!htmlSuccess) {
						cleanupTempDirectory();
						notifyError(callback, getLocalizedString(context, "html_process_failed"));
						return;
					}
					boolean cssSuccess = processCssFile(themeCssContent);
					if (!cssSuccess) {
						cleanupTempDirectory();
						notifyError(callback, getLocalizedString(context, "css_process_failed"));
						return;
					}
					saveThemeSettings(themeHtmlContent, themeCssContent);
					boolean copySuccess = copyTempFilesToOriginal();
					if (!copySuccess) {
						cleanupTempDirectory();
						notifyError(callback, getLocalizedString(context, "file_copy_failed"));
						return;
					}
					cleanupTempDirectory();
					saveCurrentThemeId(theme.id);
					notifySuccess(callback);
				} catch (Exception e) {
					XposedBridge.log("[BetterVia] 应用主题失败: " + e);
					e.printStackTrace();
					cleanupTempDirectory();
					notifyError(callback, getLocalizedString(context, "homepage_theme_apply_error"));
				}
			}
		}).start();
	}
	private String getThemeUrl(Map<String, String> urlMap) {
		if (urlMap == null) {
			return null;
		}
		String url = urlMap.get(packageName);
		if (url != null) {
			return url;
		}
		url = urlMap.get("mark.via");
		if (url != null) {
			return url;
		}
		if (!urlMap.isEmpty()) {
			return urlMap.values().iterator().next();
		}
		return null;
	}
	private String downloadFileContent(String urlString) {
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				return outputStream.toString("UTF-8");
			}
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 下载文件失败: " + e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				XposedBridge.log("[BetterVia] 关闭流失败: " + e);
			}
		}
		return null;
	}
	private boolean createTempDirectory() {
		File tempFolder = new File(tempDir);
		if (!tempFolder.exists()) {
			return tempFolder.mkdirs();
		}
		return true;
	}
	private void cleanupTempDirectory() {
		File tempFolder = new File(tempDir);
		if (tempFolder.exists()) {
			File[] files = tempFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			tempFolder.delete();
		}
	}
	private boolean processHtmlFile(String themeHtmlContent) throws Exception {
		File originalHtmlFile = new File(filesDir + HOMEPAGE_HTML_FILE);
		File tempHtmlFile = new File(tempDir + HOMEPAGE_HTML_FILE);
		if (!originalHtmlFile.exists()) {
			XposedBridge.log("[BetterVia] 原始HTML文件不存在: " + originalHtmlFile.getAbsolutePath());
			return false;
		}
		if (!copyFile(originalHtmlFile, tempHtmlFile)) {
			XposedBridge.log("[BetterVia] 复制HTML文件失败");
			return false;
		}
		String taghomeBase64 = readSettingFromXml("taghome");
		String userCustomHtml = null;
		if (taghomeBase64 != null && !taghomeBase64.isEmpty()) {
			String cleanedBase64 = taghomeBase64.replace("&#10;", "");
			userCustomHtml = decodeBase64(cleanedBase64);
		}
		String htmlContent = readFileContent(tempHtmlFile);
		String preview = htmlContent.length() > 500 ? htmlContent.substring(0, 500) : htmlContent;
		XposedBridge.log("[BetterVia] HTML文件内容预览: " + preview);
		if (userCustomHtml != null && htmlContent.contains(userCustomHtml)) {
			htmlContent = htmlContent.replace(userCustomHtml, themeHtmlContent);
			XposedBridge.log("[BetterVia] 已找到用户自定义HTML内容并替换");
		} else {
			if (userCustomHtml != null) {
				XposedBridge.log("[BetterVia] 未找到用户自定义HTML内容，尝试查找默认位置");
			} else {
				XposedBridge.log("[BetterVia] settings.xml中没有taghome项，尝试查找默认位置");
			}
			Pattern firstPattern = Pattern.compile(HTML_FIRST_SEARCH_PATTERN);
			Matcher firstMatcher = firstPattern.matcher(htmlContent);
			boolean firstFound = firstMatcher.find();
			XposedBridge.log("[BetterVia] 第一条正则匹配结果: " + (firstFound ? "成功" : "失败"));
			if (firstFound) {
				XposedBridge.log("[BetterVia] 第一条匹配内容: " + firstMatcher.group());
				XposedBridge.log("[BetterVia] 第一条匹配位置: " + firstMatcher.start() + "-" + firstMatcher.end());
			}
			Pattern secondPattern = Pattern.compile(HTML_SECOND_SEARCH_PATTERN);
			Matcher secondMatcher = secondPattern.matcher(htmlContent);
			boolean secondFound = secondMatcher.find();
			XposedBridge.log("[BetterVia] 第二条正则匹配结果: " + (secondFound ? "成功" : "失败"));
			if (secondFound) {
				XposedBridge.log("[BetterVia] 第二条匹配内容: " + secondMatcher.group());
				XposedBridge.log("[BetterVia] 第二条匹配位置: " + secondMatcher.start() + "-" + secondMatcher.end());
			}
			if (firstFound && secondFound) {
				int firstEnd = firstMatcher.end();
				int secondStart = secondMatcher.start();
				String newContent = htmlContent.substring(0, firstEnd) + 
					themeHtmlContent + 
					htmlContent.substring(secondStart);
				htmlContent = newContent;
				XposedBridge.log("[BetterVia] 已找到默认HTML位置并替换");
			} else {
				XposedBridge.log("[BetterVia] 未找到可替换的HTML位置");
				if (htmlContent.contains("class=\"logo\"")) {
					XposedBridge.log("[BetterVia] 找到 class=\"logo\"");
					int logoIndex = htmlContent.indexOf("class=\"logo\"");
					String logoContext = htmlContent.substring(Math.max(0, logoIndex - 20), Math.min(htmlContent.length(), logoIndex + 100));
					XposedBridge.log("[BetterVia] logo附近内容: " + logoContext);
				}
				if (htmlContent.contains("search_bar")) {
					XposedBridge.log("[BetterVia] 找到 search_bar");
					int searchIndex = htmlContent.indexOf("search_bar");
					String searchContext = htmlContent.substring(Math.max(0, searchIndex - 50), Math.min(htmlContent.length(), searchIndex + 100));
					XposedBridge.log("[BetterVia] search_bar附近内容: " + searchContext);
				}
				return false;
			}
		}
		return writeFileContent(tempHtmlFile, htmlContent);
	}
	private boolean processCssFile(String themeCssContent) throws Exception {
		File originalCssFile = new File(filesDir + HOMEPAGE_CSS_FILE);
		File tempCssFile = new File(tempDir + HOMEPAGE_CSS_FILE);
		if (!originalCssFile.exists()) {
			XposedBridge.log("[BetterVia] 原始CSS文件不存在: " + originalCssFile.getAbsolutePath());
			return false;
		}
		if (!copyFile(originalCssFile, tempCssFile)) {
			XposedBridge.log("[BetterVia] 复制CSS文件失败");
			return false;
		}
		String cssthemeBase64 = readSettingFromXml("csstheme");
		String userCustomCss = null;
		if (cssthemeBase64 != null && !cssthemeBase64.isEmpty()) {
			String cleanedBase64 = cssthemeBase64.replace("&#10;", "");
			userCustomCss = decodeBase64(cleanedBase64);
		}
		String cssContent = readFileContent(tempCssFile);
		String cssPreview = cssContent.length() > 500 ? cssContent.substring(0, 500) : cssContent;
		XposedBridge.log("[BetterVia] CSS文件内容预览: " + cssPreview);
		if (userCustomCss != null && cssContent.contains(userCustomCss)) {
			cssContent = cssContent.replace(userCustomCss, themeCssContent);
			XposedBridge.log("[BetterVia] 已找到用户自定义CSS内容并替换");
		} else {
			if (userCustomCss != null) {
				XposedBridge.log("[BetterVia] 未找到用户自定义CSS内容，尝试查找默认位置");
			} else {
				XposedBridge.log("[BetterVia] settings.xml中没有csstheme项，尝试查找默认位置");
			}
			Pattern cssPattern = Pattern.compile(CSS_SEARCH_PATTERN);
			Matcher matcher = cssPattern.matcher(cssContent);
			boolean found = matcher.find();
			XposedBridge.log("[BetterVia] CSS正则匹配结果: " + (found ? "成功" : "失败"));
			if (found) {
				XposedBridge.log("[BetterVia] CSS匹配内容: " + matcher.group());
				XposedBridge.log("[BetterVia] CSS匹配位置: " + matcher.start() + "-" + matcher.end());
				int endPos = matcher.end();
				String newContent = cssContent.substring(0, endPos) + themeCssContent;
				cssContent = newContent;
				XposedBridge.log("[BetterVia] 已找到默认CSS位置并替换");
			} else {
				XposedBridge.log("[BetterVia] 未找到可替换的CSS位置");
				if (cssContent.contains(".opSug_wpr")) {
					XposedBridge.log("[BetterVia] 找到 .opSug_wpr");
					int opSugIndex = cssContent.indexOf(".opSug_wpr");
					String opSugContext = cssContent.substring(Math.max(0, opSugIndex), Math.min(cssContent.length(), opSugIndex + 100));
					XposedBridge.log("[BetterVia] .opSug_wpr附近内容: " + opSugContext);
				}
				return false;
			}
		}
		return writeFileContent(tempCssFile, cssContent);
	}
	private void saveThemeSettings(String themeHtmlContent, String themeCssContent) throws Exception {
		String themeHtmlBase64 = encodeBase64WithNewline(themeHtmlContent);
		String themeCssBase64 = encodeBase64WithNewline(themeCssContent);
		writeSettingToXml("taghome", themeHtmlBase64);
		writeSettingToXml("csstheme", themeCssBase64);
	}
	private boolean copyTempFilesToOriginal() {
		boolean htmlSuccess = copyFile(new File(tempDir + HOMEPAGE_HTML_FILE), new File(filesDir + HOMEPAGE_HTML_FILE));
		boolean cssSuccess = copyFile(new File(tempDir + HOMEPAGE_CSS_FILE), new File(filesDir + HOMEPAGE_CSS_FILE));
		return htmlSuccess && cssSuccess;
	}
	private String readSettingFromXml(String key) {
		File settingsFile = new File(sharedPrefsDir + SETTINGS_XML);
		if (!settingsFile.exists()) {
			return null;
		}
		try {
			String content = readFileContent(settingsFile);
			String searchPattern = "name=\"" + key + "\">";
			int keyIndex = content.indexOf(searchPattern);
			if (keyIndex == -1) {
				return null;
			}
			int valueStart = keyIndex + searchPattern.length();
			int valueEnd = content.indexOf("</string>", valueStart);
			if (valueEnd == -1) {
				return null;
			}
			return content.substring(valueStart, valueEnd);
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 读取settings.xml失败: " + e);
		}
		return null;
	}
	private void writeSettingToXml(String key, String value) {
		File settingsFile = new File(sharedPrefsDir + SETTINGS_XML);
		String content = "";
		try {
			if (settingsFile.exists()) {
				content = readFileContent(settingsFile);
			}
			String searchPattern = "name=\"" + key + "\">";
			int keyIndex = content.indexOf(searchPattern);
			if (keyIndex != -1) {
				int valueStart = keyIndex + searchPattern.length();
				int valueEnd = content.indexOf("</string>", valueStart);
				if (valueEnd != -1) {
					String beforeEndTag = content.substring(valueEnd - 4, valueEnd);
					String newContent = content.substring(0, valueStart) + value + beforeEndTag + "</string>" + content.substring(valueEnd + 9);
					writeFileContent(settingsFile, newContent);
					XposedBridge.log("[BetterVia] 已更新settings.xml中的" + key + "项");
					return;
				}
			} else {
				int mapEnd = content.indexOf("</map>");
				if (mapEnd != -1) {
					String newEntry = "    <string name=\"" + key + "\">" + value + "&#10;    </string>\n";
					String newContent = content.substring(0, mapEnd) + newEntry + content.substring(mapEnd);
					writeFileContent(settingsFile, newContent);
					XposedBridge.log("[BetterVia] 已在settings.xml中添加" + key + "项");
					return;
				}
			}
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 写入settings.xml失败: " + e);
		}
	}
	private boolean copyFile(File source, File dest) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
			fos.flush();
			return true;
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 复制文件失败: " + e);
			return false;
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				XposedBridge.log("[BetterVia] 关闭流失败: " + e);
			}
		}
	}
	private String readFileContent(File file) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fis.read(buffer);
			return new String(buffer, "UTF-8");
		} finally {
			if (fis != null)
				fis.close();
		}
	}
	private boolean writeFileContent(File file, String content) throws Exception {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.getBytes("UTF-8"));
			fos.flush();
			return true;
		} finally {
			if (fos != null)
				fos.close();
		}
	}
	private String decodeBase64(String encoded) {
		try {
			byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
			return new String(decoded, "UTF-8");
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] Base64解码失败: " + e);
			return null;
		}
	}
	private String encodeBase64WithNewline(String decoded) {
		try {
			byte[] encoded = Base64.encode(decoded.getBytes("UTF-8"), Base64.NO_WRAP);
			String encodedStr = new String(encoded, "UTF-8");
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < encodedStr.length(); i++) {
				result.append(encodedStr.charAt(i));
				if ((i + 1) % 76 == 0) {
					result.append("&#10;");
				}
			}
			if (encodedStr.length() % 76 != 0) {
				result.append("&#10;");
			}
			return result.toString();
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] Base64编码失败: " + e);
			return null;
		}
	}
	private void saveCurrentThemeId(String themeId) {
		try {
			SharedPreferences sp = context.getSharedPreferences("BetterVia", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("current_homepage_theme", themeId);
			editor.apply();
		} catch (Exception e) {
			XposedBridge.log("[BetterVia] 保存主题ID失败: " + e);
		}
	}
	private String getLocalizedString(Context ctx, String key) {
		String result = LocalizedStringProvider.getInstance().get(ctx, key);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		switch (key) {
			case "theme_url_not_found":
				return "Unable to get theme URL";
			case "homepage_theme_apply_failed":
				return "Failed to apply homepage theme";
			case "temp_dir_create_failed":
				return "Failed to create temporary directory";
			case "html_process_failed":
				return "Failed to process HTML file";
			case "css_process_failed":
				return "Failed to process CSS file";
			case "file_copy_failed":
				return "Failed to copy files";
			case "homepage_theme_apply_error":
				return "Error applying homepage theme";
			default:
				return key;
		}
	}
	private void notifySuccess(final ThemeApplyCallback callback) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onSuccess();
				}
			}
		});
	}
	private void notifyError(final ThemeApplyCallback callback, final String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onError(message);
				}
			}
		});
	}
}