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
	private static final int EXPECTED_VERSION_CODE = 20260401;
	private static final String EXPECTED_VERSION_NAME = "1.8.0";
	private TextView appNameText;
	private TextView byAuthorText;
	private TextView blogLinkText;
	private TextView emailLinkText;
	private TextView versionText;
	private TextView copyrightText;
	private static final int ERROR_APK_PATH_NOT_FOUND = 100001; 
	private static final int ERROR_PATH_VERIFICATION_NULL = 101001; 
	private static final int ERROR_PATH_NOT_SYSTEM_DIR = 101002; 
	private static final int ERROR_PATH_MISSING_PACKAGE = 101003; 
	private static final int ERROR_PATH_MAPS_INVALID = 102001; 
	private static final int ERROR_PATH_PM_INVALID = 102002; 
	private static final int ERROR_PATH_SHELL_INVALID = 102003; 
	private static final int ERROR_PATH_MAPS_SHELL_MISMATCH = 102004; 
	private static final int ERROR_PATH_MAPS_PM_MISMATCH = 102005; 
	private static final int ERROR_APK_LOCATION_NULL = 103001; 
	private static final int ERROR_APK_NOT_IN_DATA_APP = 103002; 
	private static final int ERROR_APK_ANALYSIS_EXCEPTION = 200001; 
	private static final int ERROR_APK_CONTAINS_SO_FILES = 200002; 
	private static final int ERROR_APK_CONTAINS_APK_FILES = 200003; 
	private static final int ERROR_APK_DEX_COUNT_INVALID = 200004; 
	private static final int ERROR_APK_SIZE_FILE_NOT_EXIST = 201001; 
	private static final int ERROR_APK_SIZE_TOO_SMALL = 201002; 
	private static final int ERROR_APK_SIZE_TOO_LARGE = 201003; 
	private static final int ERROR_APK_SIZE_SHELL_MISMATCH = 201004; 
	private static final int ERROR_PACKAGE_INFO_EXCEPTION = 300001; 
	private static final int ERROR_PACKAGE_INFO_APK_NULL = 300002; 
	private static final int ERROR_PACKAGE_NAME_CURRENT_APK_MISMATCH = 300003; 
	private static final int ERROR_PACKAGE_NAME_CURRENT_EXPECTED_MISMATCH = 300004; 
	private static final int ERROR_PACKAGE_NAME_APK_EXPECTED_MISMATCH = 300005; 
	private static final int ERROR_VERSION_CODE_CURRENT_APK_MISMATCH = 300006; 
	private static final int ERROR_VERSION_CODE_CURRENT_EXPECTED_MISMATCH = 300007; 
	private static final int ERROR_VERSION_CODE_APK_EXPECTED_MISMATCH = 300008; 
	private static final int ERROR_VERSION_NAME_CURRENT_APK_MISMATCH = 300009; 
	private static final int ERROR_VERSION_NAME_CURRENT_EXPECTED_MISMATCH = 300010; 
	private static final int ERROR_VERSION_NAME_APK_EXPECTED_MISMATCH = 300011; 
	private static final int ERROR_UNAUTHORIZED_FILE_EXCEPTION = 400001; 
	private static final int ERROR_UNAUTHORIZED_FILE_IN_EXTERNAL = 400002; 
	private static final int ERROR_UNAUTHORIZED_FILE_IN_INTERNAL = 400003; 
	private static final int ERROR_UNAUTHORIZED_FILE_IN_DATA = 400004; 
	private static final int ERROR_SIGNATURE_VERIFICATION_FAILED = 500001; 
	private static final int ERROR_CREATOR_NAME_EXCEPTION = 600001; 
	private static final int ERROR_CREATOR_NAME_MISMATCH = 600002; 
	private static final int ERROR_CREATOR_CLASSLOADER_EXCEPTION = 600003; 
	private static final int ERROR_CREATOR_CLASSLOADER_NULL = 600004; 
	private static final int ERROR_CREATOR_CLASSLOADER_TAMPERED = 600005; 
	private static final int ERROR_SECURITY_CHECK_PERMISSION_GRANTED = 900001; 
	private static final int ERROR_SECURITY_CHECK_PERMISSION_DENIED = 900002; 
	private int lastErrorCode = 0;
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(updateBaseContextForLanguage(newBase));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String realApkPath = getRealApkPath();
		if (realApkPath == null) {
			lastErrorCode = ERROR_APK_PATH_NOT_FOUND;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int pathVerifyError = verifyApkPathWithCode(realApkPath);
		if (pathVerifyError != 0) {
			lastErrorCode = pathVerifyError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int packagePathError = packagePathCheckWithCode();
		if (packagePathError != 0) {
			lastErrorCode = packagePathError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int locationError = checkApkInDataAppDirectoryWithCode();
		if (locationError != 0) {
			lastErrorCode = locationError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int apkContentError = detectApkWithCode(realApkPath);
		if (apkContentError != 0) {
			lastErrorCode = apkContentError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int apkSizeError = checkApkSizeWithCode(realApkPath);
		if (apkSizeError != 0) {
			lastErrorCode = apkSizeError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int packageInfoError = checkPackageInfoWithCode(realApkPath);
		if (packageInfoError != 0) {
			lastErrorCode = packageInfoError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		int unauthorizedFileError = detectUnauthorizedFilesWithCode();
		if (unauthorizedFileError != 0) {
			lastErrorCode = unauthorizedFileError;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
		if (!SignatureVerifier.verifySignature(this)) {
			lastErrorCode = ERROR_SIGNATURE_VERIFICATION_FAILED;
			showTamperedAppDialogWithPermissionCheck();
			return;
		}
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
	private int verifyApkPathWithCode(String apkPath) {
		if (apkPath == null) {
			return ERROR_PATH_VERIFICATION_NULL;
		}
		boolean isSystemPath = apkPath.startsWith("/data/app/") || apkPath.startsWith("/system/app/")
				|| apkPath.startsWith("/system/priv-app/");
		if (!isSystemPath) {
			return ERROR_PATH_NOT_SYSTEM_DIR;
		}
		String packageName = getPackageName();
		if (!apkPath.contains(packageName)) {
			return ERROR_PATH_MISSING_PACKAGE;
		}
		return 0; 
	}
	private int packagePathCheckWithCode() {
		try {
			String mapsPath = normalizeApkPath(getApkPathFromMaps());
			String pmPath = normalizeApkPath(getApkPathFromPackageManager());
			String shellPath = normalizeApkPath(getApkPathFromShell());
			List<String> validPaths = new ArrayList<>();
			if (mapsPath != null && verifyApkPathWithCode(mapsPath) == 0) {
				validPaths.add(mapsPath);
			}
			if (pmPath != null && verifyApkPathWithCode(pmPath) == 0) {
				validPaths.add(pmPath);
			}
			if (shellPath != null && verifyApkPathWithCode(shellPath) == 0) {
				validPaths.add(shellPath);
			}
			int validCount = validPaths.size();
			if (validCount <= 1) {
				return validCount == 0 ? ERROR_PATH_MAPS_INVALID : 0;
			}
			if (arePathsSameInstance(validPaths)) {
				return 0; 
			}
			return getWeightedMismatchError(mapsPath, pmPath, shellPath, validPaths);
		} catch (Exception e) {
			return ERROR_PATH_MAPS_INVALID;
		}
	}
	private String normalizeApkPath(String path) {
		if (path == null)
			return null;
		try {
			String normalized = path.trim().replaceAll("/+$", "");
			File file = new File(normalized);
			if (file.exists()) {
				try {
					normalized = file.getCanonicalPath(); 
				} catch (IOException e) {
				}
			}
			return normalized.replace("\\", "/").replaceAll("/+", "/");
		} catch (Exception e) {
			return null;
		}
	}
	private boolean arePathsSameInstance(List<String> paths) {
		if (paths.size() < 2)
			return true;
		String firstCoreId = extractInstallInstanceId(paths.get(0));
		if (firstCoreId == null)
			return false;
		for (int i = 1; i < paths.size(); i++) {
			String currentCoreId = extractInstallInstanceId(paths.get(i));
			if (!firstCoreId.equals(currentCoreId)) {
				return false; 
			}
		}
		return true;
	}
	private String extractInstallInstanceId(String path) {
		if (path == null)
			return null;
		try {
			String packageName = getPackageName();
			int pkgIndex = path.indexOf(packageName);
			if (pkgIndex == -1)
				return null;
			String subPath = path.substring(pkgIndex);
			int slashIndex = subPath.indexOf('/');
			if (slashIndex > 0) {
				return subPath.substring(0, slashIndex); 
			}
			return subPath; 
		} catch (Exception e) {
			return null;
		}
	}
	private int getWeightedMismatchError(String mapsPath, String pmPath, String shellPath, List<String> validPaths) {
		if (mapsPath != null && validPaths.contains(mapsPath)) {
			if (pmPath != null && validPaths.contains(pmPath)
					&& !arePathsSameInstance(Arrays.asList(mapsPath, pmPath))) {
				return ERROR_PATH_MAPS_PM_MISMATCH;
			}
			if (shellPath != null && validPaths.contains(shellPath)
					&& !arePathsSameInstance(Arrays.asList(mapsPath, shellPath))) {
				return ERROR_PATH_MAPS_SHELL_MISMATCH;
			}
		}
		if (pmPath != null && shellPath != null && validPaths.containsAll(Arrays.asList(pmPath, shellPath))) {
			if (!arePathsSameInstance(Arrays.asList(pmPath, shellPath))) {
				return ERROR_PATH_MAPS_INVALID;
			}
		}
		return ERROR_PATH_MAPS_INVALID;
	}
	private int checkApkInDataAppDirectoryWithCode() {
		String apkPath = getApkPathFromPackageManager();
		if (apkPath == null) {
			return ERROR_APK_LOCATION_NULL;
		}
		if (!apkPath.startsWith("/data/app/")) {
			return ERROR_APK_NOT_IN_DATA_APP;
		}
		return 0;
	}
	private int detectApkWithCode(String apkPath) {
		try {
			int dexCount = 0;
			boolean hasIllegalFile = false;
			ZipFile zip = new ZipFile(apkPath);
			Enumeration<? extends ZipEntry> e = zip.entries();
			while (e.hasMoreElements()) {
				String name = e.nextElement().getName();
				if (name.endsWith(".so")) {
					return ERROR_APK_CONTAINS_SO_FILES;
				}
				if (name.endsWith(".apk")) {
					return ERROR_APK_CONTAINS_APK_FILES;
				}
				if (name.endsWith(".dex")) {
					dexCount++;
				}
			}
			zip.close();
			if (dexCount != 1) {
				return ERROR_APK_DEX_COUNT_INVALID;
			}
			return 0;
		} catch (Throwable ignore) {
			return ERROR_APK_ANALYSIS_EXCEPTION;
		}
	}
	private int checkApkSizeWithCode(String apkPath) {
		try {
			File apkFile = new File(apkPath);
			if (!apkFile.exists()) {
				return ERROR_APK_SIZE_FILE_NOT_EXIST;
			}
			long minSize = (long) (0.664 * 1024 * 1024);
			long maxSize = (long) (0.668 * 1024 * 1024);
			long fileSize = apkFile.length();
			if (fileSize < minSize) {
				return ERROR_APK_SIZE_TOO_SMALL;
			}
			if (fileSize > maxSize) {
				return ERROR_APK_SIZE_TOO_LARGE;
			}
			try {
				Process process = Runtime.getRuntime().exec(new String[]{"stat", "-c", "%s", apkPath});
				InputStream inputStream = process.getInputStream();
				byte[] buffer = new byte[20];
				int bytesRead = inputStream.read(buffer);
				process.waitFor();
				if (bytesRead > 0) {
					String output = new String(buffer, 0, bytesRead).trim().replace(" ", "").replace("\n", "");
					try {
						long shellSize = Long.parseLong(output);
						if (Math.abs(shellSize - fileSize) > 1024) {
							return ERROR_APK_SIZE_SHELL_MISMATCH;
						}
					} catch (NumberFormatException e) {
						return 0;
					}
				}
			} catch (Exception e) {
				return 0;
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}
	private int checkPackageInfoWithCode(String apkPath) {
		try {
			PackageInfo currentInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String currentPackageName = currentInfo.packageName;
			int currentVersionCode = currentInfo.versionCode;
			String currentVersionName = currentInfo.versionName;
			PackageInfo apkInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA);
			if (apkInfo == null) {
				return ERROR_PACKAGE_INFO_APK_NULL;
			}
			String apkPackageName = apkInfo.packageName;
			int apkVersionCode = apkInfo.versionCode;
			String apkVersionName = apkInfo.versionName;
			String expectedPackageName = EXPECTED_PACKAGE_NAME;
			int expectedVersionCode = EXPECTED_VERSION_CODE;
			String expectedVersionName = EXPECTED_VERSION_NAME;
			if (!currentPackageName.equals(apkPackageName)) {
				return ERROR_PACKAGE_NAME_CURRENT_APK_MISMATCH;
			}
			if (!currentPackageName.equals(expectedPackageName)) {
				return ERROR_PACKAGE_NAME_CURRENT_EXPECTED_MISMATCH;
			}
			if (!apkPackageName.equals(expectedPackageName)) {
				return ERROR_PACKAGE_NAME_APK_EXPECTED_MISMATCH;
			}
			if (currentVersionCode != apkVersionCode) {
				return ERROR_VERSION_CODE_CURRENT_APK_MISMATCH;
			}
			if (currentVersionCode != expectedVersionCode) {
				return ERROR_VERSION_CODE_CURRENT_EXPECTED_MISMATCH;
			}
			if (apkVersionCode != expectedVersionCode) {
				return ERROR_VERSION_CODE_APK_EXPECTED_MISMATCH;
			}
			if (!currentVersionName.equals(apkVersionName)) {
				return ERROR_VERSION_NAME_CURRENT_APK_MISMATCH;
			}
			if (!currentVersionName.equals(expectedVersionName)) {
				return ERROR_VERSION_NAME_CURRENT_EXPECTED_MISMATCH;
			}
			if (!apkVersionName.equals(expectedVersionName)) {
				return ERROR_VERSION_NAME_APK_EXPECTED_MISMATCH;
			}
			return 0;
		} catch (Exception e) {
			return ERROR_PACKAGE_INFO_EXCEPTION;
		}
	}
	private int detectUnauthorizedFilesWithCode() {
		try {
			File externalPrivateDir = getExternalFilesDir(null);
			if (externalPrivateDir != null) {
				File externalAppDir = externalPrivateDir.getParentFile();
				if (externalAppDir != null && externalAppDir.exists()) {
					if (scanTargetDirectoryWithCode(externalAppDir) != 0) {
						return ERROR_UNAUTHORIZED_FILE_IN_EXTERNAL;
					}
				}
			}
			File internalFilesDir = getFilesDir();
			if (internalFilesDir != null) {
				File internalAppDir = internalFilesDir.getParentFile();
				if (internalAppDir != null && internalAppDir.exists()) {
					if (scanTargetDirectoryWithCode(internalAppDir) != 0) {
						return ERROR_UNAUTHORIZED_FILE_IN_INTERNAL;
					}
				}
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				File dataDir = getDataDir();
				if (dataDir != null && dataDir.exists()) {
					if (scanTargetDirectoryWithCode(dataDir) != 0) {
						return ERROR_UNAUTHORIZED_FILE_IN_DATA;
					}
				}
			}
			return 0;
		} catch (Exception e) {
			Log.e(TAG, "文件检测异常: " + e.getMessage(), e);
			return ERROR_UNAUTHORIZED_FILE_EXCEPTION;
		}
	}
	private int scanTargetDirectoryWithCode(File dir) {
		if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
			return 0;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return 0;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				if (scanTargetDirectoryWithCode(file) != 0) {
					return 1;
				}
			} else {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(".apk") || fileName.endsWith(".so")) {
					return 1;
				}
			}
		}
		return 0;
	}
	private int checkCreatorByNameWithCode() {
		try {
			String expectedName = "android.content.pm.PackageInfo$1";
			Field creatorField = PackageInfo.class.getDeclaredField("CREATOR");
			creatorField.setAccessible(true);
			Object creator = creatorField.get(null);
			String actualName = creator.getClass().getName();
			if (!expectedName.equals(actualName)) {
				return ERROR_CREATOR_NAME_MISMATCH;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_CREATOR_NAME_EXCEPTION;
		}
	}
	private int checkCreatorByClassLoaderWithCode() {
		try {
			Field creatorField = PackageInfo.class.getDeclaredField("CREATOR");
			creatorField.setAccessible(true);
			Object creator = creatorField.get(null);
			if (creator == null) {
				return ERROR_CREATOR_CLASSLOADER_NULL;
			}
			ClassLoader creatorClassLoader = creator.getClass().getClassLoader();
			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
			if (creatorClassLoader == null || systemClassLoader == null) {
				return ERROR_CREATOR_CLASSLOADER_NULL;
			}
			if (!systemClassLoader.getClass().getName().equals(creatorClassLoader.getClass().getName())) {
				return ERROR_CREATOR_CLASSLOADER_TAMPERED;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_CREATOR_CLASSLOADER_EXCEPTION;
		}
	}
	private String getRealApkPath() {
		String apkPath = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"));
			String line;
			String packageName = getPackageName();
			while ((line = reader.readLine()) != null) {
				if (line.contains("/data/app/") && line.contains(".apk") && line.contains(packageName)) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("/data/app/") && part.contains(".apk")) {
							reader.close();
							apkPath = part;
							if (!apkPath.endsWith(".apk")) {
								int apkEnd = apkPath.lastIndexOf(".apk");
								if (apkEnd != -1) {
									apkPath = apkPath.substring(0, apkEnd + 4); 
								}
							}
							if (apkPath.startsWith("/data/app/") && apkPath.endsWith(".apk")) {
								return apkPath;
							}
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
		} catch (Exception e) {
		}
		try {
			apkPath = getApplicationInfo().sourceDir;
			if (apkPath != null && new File(apkPath).exists()) {
				return apkPath;
			}
		} catch (Exception e) {
		}
		return null;
	}
	private String getApkPathFromMaps() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"));
			String line;
			String packageName = getPackageName();
			while ((line = reader.readLine()) != null) {
				if (line.contains("/data/app/") && line.contains(".apk") && line.contains(packageName)) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("/data/app/") && part.contains(".apk")) {
							reader.close();
							return part;
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
		}
		return null;
	}
	private String getApkPathFromPackageManager() {
		try {
			return getApplicationInfo().sourceDir;
		} catch (Exception e) {
			return null;
		}
	}
	private String getApkPathFromShell() {
		Process process = null;
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			String packageName = getPackageName();
			String[] command = {"pm", "path", packageName};
			process = Runtime.getRuntime().exec(command);
			inputStream = process.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("package:")) {
					return line.substring(8).trim(); 
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (inputStream != null)
					inputStream.close();
				if (process != null)
					process.destroy();
			} catch (Exception e) {
			}
		}
	}
	private void showTamperedAppDialogWithPermissionCheck() {
		TamperResponseHelper.handleTamper(this, new TamperResponseHelper.TamperResponseCallback() {
			@Override
			public void onComplete(boolean success) {
				if (success) {
					showTamperedAppDialog(lastErrorCode);
				} else {
					finish();
				}
			}
		});
	}
	private void showTamperedAppDialog(final int errorCode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
				int language = sp.getInt(KEY_LANGUAGE, LANGUAGE_AUTO);
				String title, message;
				switch (language) {
					case LANGUAGE_SIMPLIFIED_CHINESE :
						title = "安全检测异常";
						message = "检测到应用修改痕迹或存在安全风险！\n为了您的系统安全，程序将会自动退出。\n请下载正版软件或清空存储重试。";
						break;
					case LANGUAGE_TRADITIONAL_CHINESE :
						title = "安全檢測異常";
						message = "檢測到應用修改痕跡或存在安全風險！\n為了您的系統安全，程式將會自動退出。\n請下載正版軟體或清空存儲重試。";
						break;
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
							if (languageCode.startsWith("zh")) {
								String country = systemLocale.getCountry();
								if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
									title = "安全檢測異常";
									message = "檢測到應用修改痕跡或存在安全風險！\n為了您的系統安全，程式將會自動退出。\n請下載正版軟體或清空存儲重試。";
								} else {
									title = "安全检测异常";
									message = "检测到应用修改痕迹或存在安全风险！\n为了您的系统安全，程序将会自动退出。\n请下载正版软件或清空存储重试。";
								}
							} else {
								title = "Security Detection Exception";
								message = "Application modification detected or security risk exists!\nFor your system security, the program will exit automatically.\nPlease download the official version or clear storage and try again.";
							}
						} else {
							title = "Security Detection Exception";
							message = "Application modification detected or security risk exists!\nFor your system security, the program will exit automatically.\nPlease download the official version or clear storage and try again.";
						}
						break;
				}
				String fullMessage = message + "\n\n" + getErrorCodeText(language) + errorCode;
				final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(title)
						.setMessage(fullMessage).setCancelable(false)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).create();
				dialog.show();
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!isFinishing() && !isDestroyed()) {
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							finish();
						}
					}
				}, 3000);
			}
		});
	}
	private String getErrorCodeText(int language) {
		switch (language) {
			case LANGUAGE_SIMPLIFIED_CHINESE :
				return "错误代码：";
			case LANGUAGE_TRADITIONAL_CHINESE :
				return "錯誤代碼：";
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
					if (languageCode.startsWith("zh")) {
						String country = systemLocale.getCountry();
						if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
							return "錯誤代碼：";
						} else {
							return "错误代码：";
						}
					}
				}
				return "Error Code: ";
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
		final ScrollView scrollView = agreementDialog.findViewById(R.id.agreement_scrollview);
		Button agreeButton = agreementDialog.findViewById(R.id.agree_button);
		Button disagreeButton = agreementDialog.findViewById(R.id.disagree_button);
		if (scrollView != null) {
			ViewGroup scrollViewChild = (ViewGroup) scrollView.getChildAt(0);
			final WebView webView = new WebView(this);
			FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			webView.setLayoutParams(webViewParams);
			webView.setBackgroundColor(Color.TRANSPARENT);
			webView.setVerticalScrollBarEnabled(true);
			webView.setHorizontalScrollBarEnabled(false);
			WebSettings webSettings = webView.getSettings();
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setUseWideViewPort(true);
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setTextZoom(100);
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					webView.setAlpha(0f);
					ObjectAnimator fadeIn = ObjectAnimator.ofFloat(webView, "alpha", 0f, 1f);
					fadeIn.setDuration(500);
					fadeIn.setInterpolator(new DecelerateInterpolator());
					fadeIn.start();
				}
			});
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			int language = sp.getInt(KEY_LANGUAGE, LANGUAGE_AUTO);
			String htmlFileName;
			switch (language) {
				case LANGUAGE_SIMPLIFIED_CHINESE :
					htmlFileName = "useragreement_zh.html";
					break;
				case LANGUAGE_TRADITIONAL_CHINESE :
					htmlFileName = "useragreement_tw.html";
					break;
				case LANGUAGE_ENGLISH :
					htmlFileName = "useragreement_en.html";
					break;
				case LANGUAGE_AUTO :
				default :
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
							htmlFileName = "useragreement_tw.html";
						} else {
							htmlFileName = "useragreement_zh.html";
						}
					} else {
						htmlFileName = "useragreement_en.html";
					}
					break;
			}
			webView.loadUrl("file:
			if (scrollViewChild != null) {
				scrollViewChild.addView(webView);
			} else {
				scrollView.addView(webView);
			}
		}
		String agreeButtonText = getString(R.string.agree_button);
		String disagreeButtonText = getString(R.string.disagree_button);
		agreeButton.setText(agreeButtonText);
		disagreeButton.setText(disagreeButtonText);
		agreementDialog.show();
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
		if (com.jiguro.bettervia.ModuleStatus.activated) {
			return true;
		}
		return isLSPatchModeActive();
	}
	private boolean isLSPatchModeActive() {
		String[] targetPackages = {PKG_VIA, PKG_VIAGP};
		for (String packageName : targetPackages) {
			try {
				PackageManager pm = getPackageManager();
				ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
				String apkPath = appInfo.sourceDir;
				if (apkPath == null || !new File(apkPath).exists()) {
					continue; 
				}
				if (checkManifestForLSPatch(apkPath)) {
					Log.d(TAG, "检测到LSPatch修改痕迹，包名：" + packageName);
					return true; 
				}
			} catch (PackageManager.NameNotFoundException e) {
				continue;
			} catch (Exception e) {
				Log.e(TAG, "LSPatch检测异常，包名：" + packageName + "，错误：" + e.getMessage());
				continue;
			}
		}
		return false; 
	}
	private boolean checkManifestForLSPatch(String apkPath) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(apkPath);
			ZipEntry manifestEntry = zipFile.getEntry("AndroidManifest.xml");
			if (manifestEntry == null) {
				return false; 
			}
			InputStream is = zipFile.getInputStream(manifestEntry);
			byte[] buffer = new byte[(int) manifestEntry.getSize()];
			int bytesRead = is.read(buffer);
			is.close();
			if (bytesRead <= 0) {
				return false; 
			}
			String manifestContent = new String(buffer, java.nio.charset.StandardCharsets.UTF_8);
			String lowerContent = manifestContent.toLowerCase();
			return lowerContent.contains("patch") || lowerContent.contains("lsposed");
		} catch (Exception e) {
			Log.e(TAG, "读取Manifest失败: " + e.getMessage());
			return false;
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
				}
			}
		}
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
				} else if (item.getItemId() == R.id.menu_security_fix) {
					showSecurityFixDialog();
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
	private void showSecurityFixDialog() {
		new AlertDialog.Builder(this).setTitle(getString(R.string.security_fix_dialog_title))
				.setMessage(getString(R.string.security_fix_dialog_message))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startSecurityFixProcess();
					}
				}).setNegativeButton(android.R.string.cancel, null).show();
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
	private void startSecurityFixProcess() {
		if (hasRootPermission()) {
			performSecurityFixWithRoot();
		} else {
			if (checkStoragePermission()) {
				performSecurityFixWithoutRoot();
			} else {
				requestStoragePermission();
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
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_STORAGE_PERMISSION_FOR_FIX) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				performSecurityFixWithoutRoot();
			} else {
				if (hasRootPermission()) {
					performSecurityFixWithRoot();
				} else {
					Toast.makeText(this, getString(R.string.security_fix_need_permission), Toast.LENGTH_LONG).show();
				}
			}
			return;
		}
		TamperResponseHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults,
				new TamperResponseHelper.TamperResponseCallback() {
					@Override
					public void onComplete(boolean success) {
						if (success) {
							showTamperedAppDialog(lastErrorCode);
						} else {
							if (lastErrorCode == 0) {
								lastErrorCode = 900002; 
							}
							showTamperedAppDialog(lastErrorCode);
						}
					}
				});
	}
	private void performSecurityFixWithRoot() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean fixedPrivate = fixConfigInPrivateDirectories();
				boolean fixedPublic = fixConfigInPublicDirectoryWithRoot();
				final boolean result = fixedPrivate || fixedPublic;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (result) {
							Toast.makeText(MainActivity.this, getString(R.string.security_fix_success),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, getString(R.string.security_fix_no_issue),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}).start();
	}
	private void performSecurityFixWithoutRoot() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean fixed = fixConfigInPublicDirectory();
				final boolean result = fixed;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (result) {
							Toast.makeText(MainActivity.this, getString(R.string.security_fix_success),
									Toast.LENGTH_LONG).show();
							Toast.makeText(MainActivity.this, getString(R.string.security_fix_suggest_root),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, getString(R.string.security_fix_no_issue),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}).start();
	}
	private boolean fixConfigInPublicDirectoryWithRoot() {
		boolean fixed = false;
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		String suCommand = sp.getString("su_command", "").trim();
		String command = suCommand.isEmpty() ? "su" : suCommand;
		String publicDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.bettervia";
		String publicFilePath = publicDirPath + "/config.json";
		try {
			Process process = Runtime.getRuntime()
					.exec(new String[]{command, "-c", "test -f " + publicFilePath + " && echo exists"});
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String result = reader.readLine();
			process.waitFor();
			if ("exists".equals(result)) {
				process = Runtime.getRuntime().exec(new String[]{command, "-c", "cat " + publicFilePath});
				is = process.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder content = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				process.waitFor();
				JSONObject config = new JSONObject(content.toString());
				if (config.has("whiteConfiguration")) {
					config.remove("whiteConfiguration");
					String configStr = config.toString();
					Process writeProcess = Runtime.getRuntime()
							.exec(new String[]{command, "-c", "echo '" + configStr + "' > " + publicFilePath});
					writeProcess.waitFor();
					fixed = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fixed;
	}
	private boolean fixConfigInPrivateDirectories() {
		boolean fixed = false;
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		String suCommand = sp.getString("su_command", "").trim();
		String command = suCommand.isEmpty() ? "su" : suCommand;
		String[] packages = {PKG_VIA, PKG_VIAGP};
		for (String pkg : packages) {
			String dirPath = "/storage/emulated/0/Android/data/" + pkg + "/files/.bettervia";
			String filePath = dirPath + "/config.json";
			try {
				Process process = Runtime.getRuntime()
						.exec(new String[]{command, "-c", "test -f " + filePath + " && echo exists"});
				InputStream is = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String result = reader.readLine();
				process.waitFor();
				if ("exists".equals(result)) {
					process = Runtime.getRuntime().exec(new String[]{command, "-c", "cat " + filePath});
					is = process.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is));
					StringBuilder content = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						content.append(line);
					}
					process.waitFor();
					JSONObject config = new JSONObject(content.toString());
					if (config.has("whiteConfiguration")) {
						config.remove("whiteConfiguration");
						String configStr = config.toString();
						Process writeProcess = Runtime.getRuntime()
								.exec(new String[]{command, "-c", "echo '" + configStr + "' > " + filePath});
						writeProcess.waitFor();
						fixed = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fixed;
	}
	private boolean fixConfigInPublicDirectory() {
		boolean fixed = false;
		File publicDir = new File(Environment.getExternalStorageDirectory(), ".bettervia");
		File publicFile = new File(publicDir, "config.json");
		try {
			if (publicFile.exists()) {
				FileInputStream fis = new FileInputStream(publicFile);
				byte[] buffer = new byte[(int) publicFile.length()];
				fis.read(buffer);
				fis.close();
				String content = new String(buffer, "UTF-8");
				JSONObject config = new JSONObject(content);
				if (config.has("whiteConfiguration")) {
					config.remove("whiteConfiguration");
					FileOutputStream fos = new FileOutputStream(publicFile);
					fos.write(config.toString().getBytes("UTF-8"));
					fos.close();
					fixed = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fixed;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (flowWebView != null) {
			flowWebView.destroy();
		}
	}
}