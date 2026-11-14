package com.jiguro.bettervia;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class HostPatchDetector {

    private static final String BETTERVIA_PACKAGE = "com.jiguro.bettervia";

    private static final int MAX_SCAN_FILES = 4000;
    private static final int MAX_SCAN_DEPTH = 8;
    private static final int MAX_RISK_APKS = 5;

    private static final String[] SKIP_DIR_PATHS = {
        "app_webview",
        "app_textures",
        "databases",
        "shared_prefs",
        "no_backup",
        "WebView",
        "/storage/emulated/0/Android/data/{pkg}/files/content",
        "/storage/emulated/0/Android/data/{pkg}/files/favicons",
        "/storage/emulated/0/Android/data/{pkg}/files/gm",
        "/storage/emulated/0/Android/data/{pkg}/files/logs",
        "/storage/emulated/0/Android/data/{pkg}/files/tabs",
        "/storage/emulated/0/Android/data/{pkg}/files/opened",
        "/storage/emulated/0/Android/data/{pkg}/files/filters",
        "/storage/emulated/0/Android/data/{pkg}/files/font",
        "/storage/emulated/0/Android/data/{pkg}/files/offline",
        "/storage/emulated/0/Android/data/{pkg}/files/pass",
        "/storage/emulated/0/Android/data/{pkg}/files/proxy",
        "/storage/emulated/0/Android/data/{pkg}/files/settings",
        "/storage/emulated/0/Android/data/{pkg}/files/skins",
        "/storage/emulated/0/Android/data/{pkg}/files/syncing",
        "/data/user/0/{pkg}/cache/Crash Reports",
        "/data/user/0/{pkg}/files/icon",
        "files/BetterVia"
    };

    private static final String[] EMPTY_PATHS = new String[0];

    private static volatile Boolean sCachedLspatch = null;
    private static volatile String[] sCachedRiskApks = null;

    private HostPatchDetector() {}

    interface Callback {
        void onResult(boolean lspatch, String[] riskApks);
    }

    static void detectAsync(final Context ctx, final Callback callback) {
        if (callback == null) return;
        final Boolean cached = sCachedLspatch;
        if (cached != null) {
            postResult(callback, cached.booleanValue(), cachedRiskApks());
            return;
        }
        Thread worker =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                detectNow(ctx);
                                Boolean mode = sCachedLspatch;
                                postResult(
                                        callback,
                                        mode != null && mode.booleanValue(),
                                        cachedRiskApks());
                            }
                        },
                        "BetterVia-ModeDetect");
        worker.setDaemon(true);
        worker.start();
    }

    private static void postResult(
            final Callback callback, final boolean lspatch, final String[] riskApks) {
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(lspatch, riskApks);
                            }
                        });
    }

    private static String[] cachedRiskApks() {
        String[] risk = sCachedRiskApks;
        return risk != null ? risk : EMPTY_PATHS;
    }

    private static void detectNow(Context ctx) {
        synchronized (HostPatchDetector.class) {
            if (sCachedLspatch != null) return;
            boolean lspatch = detectLspatch(ctx);
            String[] risk = EMPTY_PATHS;
            if (!lspatch && ctx != null) {
                DirScanResult scan = scanHostDirsForRiskApk(ctx);
                if (scan.betterViaPackage) {
                    lspatch = true;
                } else {
                    risk = scan.riskApks.toArray(new String[scan.riskApks.size()]);
                }
            }
            sCachedRiskApks = risk;
            sCachedLspatch = Boolean.valueOf(lspatch);
        }
    }

    private static boolean detectLspatch(Context ctx) {
        if (ctx == null) return false;
        String pkg = ctx.getPackageName();

        Boolean result = checkGroup(collectRuntimeApkPaths(ctx, pkg));
        if (result != null) return result.booleanValue();

        String[] pmPaths;
        try {
            ApplicationInfo ai = ctx.getApplicationInfo();
            pmPaths = new String[] {ai.sourceDir, ai.publicSourceDir, ctx.getPackageCodePath()};
        } catch (Throwable t) {
            pmPaths = EMPTY_PATHS;
        }
        result = checkGroup(pmPaths);
        if (result != null) return result.booleanValue();

        result = checkGroup(new String[] {findApkByShell(pkg)});
        if (result != null) return result.booleanValue();
        return false;
    }

    private static String[] collectRuntimeApkPaths(Context ctx, String pkg) {
        LinkedHashSet<String> paths = new LinkedHashSet<String>();
        addPath(paths, findApkInMaps(pkg));
        String[] dexPaths = findDexApkPaths(ctx.getClassLoader(), pkg);
        for (int i = 0; i < dexPaths.length; i++) addPath(paths, dexPaths[i]);
        return paths.toArray(new String[paths.size()]);
    }

    private static void addPath(LinkedHashSet<String> paths, String path) {
        if (path != null && path.length() > 0) paths.add(path);
    }

    private static Boolean checkGroup(String[] apkPaths) {
        if (apkPaths == null) return null;
        boolean readable = false;
        for (int i = 0; i < apkPaths.length; i++) {
            String path = apkPaths[i];
            if (path == null || path.length() == 0) continue;
            File file = new File(path);
            if (!file.isFile() || !file.canRead()) continue;
            readable = true;
            if (hasEmbeddedPayload(path)) return Boolean.TRUE;
        }
        return readable ? Boolean.FALSE : null;
    }

    static boolean hasEmbeddedPayload(String apkPath) {
        if (apkPath == null || apkPath.length() == 0) return false;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(apkPath);
            java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                if (name == null) continue;
                String lower = name.toLowerCase(Locale.US);
                if (lower.endsWith(".apk")) return true;
                if (lower.contains("bettervia")) return true;
            }
        } catch (Throwable ignored) {
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private static final String[] MANIFEST_KEYWORDS = {"xposed", "lsposed", "patch"};

    static boolean isPackagePatched(Context ctx, String packageName) {
        if (ctx == null || packageName == null || packageName.length() == 0) return false;
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(packageName, 0);
            String apkPath = ai == null ? null : ai.sourceDir;
            return apkPath != null && isManifestPatched(apkPath);
        } catch (Throwable ignored) {
        }
        return false;
    }

    static boolean isManifestPatched(String apkPath) {
        if (apkPath == null || apkPath.length() == 0) return false;
        ZipFile zipFile = null;
        InputStream in = null;
        try {
            zipFile = new ZipFile(apkPath);
            ZipEntry entry = zipFile.getEntry("AndroidManifest.xml");
            if (entry == null) return false;
            in = zipFile.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            byte[] data = out.toByteArray();
            String utf8 = new String(data, "UTF-8").toLowerCase(Locale.US);
            String utf16 = new String(data, "UTF-16LE").toLowerCase(Locale.US);
            for (int i = 0; i < MANIFEST_KEYWORDS.length; i++) {
                String keyword = MANIFEST_KEYWORDS[i];
                if (utf8.contains(keyword) || utf16.contains(keyword)) return true;
            }
        } catch (Throwable ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignored) {
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private static String findApkInMaps(String pkg) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/self/maps"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("/data/app/") || !line.contains(".apk") || !line.contains(pkg))
                    continue;
                String[] parts = line.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (part.startsWith("/") && part.endsWith(".apk") && part.contains(pkg))
                        return part;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return null;
    }

    private static String[] findDexApkPaths(ClassLoader cl, String pkg) {
        if (cl == null) return EMPTY_PATHS;
        LinkedHashSet<String> paths = new LinkedHashSet<String>();
        try {
            Object pathList = readField(cl, "pathList");
            Object elementsObj = pathList == null ? null : readField(pathList, "dexElements");
            if (elementsObj instanceof Object[]) {
                Object[] elements = (Object[]) elementsObj;
                for (int i = 0; i < elements.length; i++) {
                    String path = elementPath(elements[i]);
                    if (path != null && path.endsWith(".apk") && path.contains(pkg))
                        paths.add(path);
                }
            }
        } catch (Throwable ignored) {
        }
        return paths.toArray(new String[paths.size()]);
    }

    private static String elementPath(Object element) {
        if (element == null) return null;
        Object path = readField(element, "path");
        if (path instanceof String) return (String) path;
        Object dexFile = readField(element, "dexFile");
        if (dexFile == null) return null;
        try {
            return (String) dexFile.getClass().getMethod("getPath").invoke(dexFile);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String name) {
        if (target == null) return null;
        Class<?> cls = target.getClass();
        while (cls != null) {
            try {
                java.lang.reflect.Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static String findApkByShell(String pkg) {
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(new String[] {"pm", "path", pkg});
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package:")) {
                    String path = line.substring(8).trim();
                    return path.length() > 0 ? path : null;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable ignored) {
                }
            }
            if (process != null) process.destroy();
        }
        return null;
    }

    private static final class DirScanResult {
        boolean betterViaPackage;
        final ArrayList<String> riskApks = new ArrayList<String>();
    }

    private static DirScanResult scanHostDirsForRiskApk(Context ctx) {
        DirScanResult result = new DirScanResult();
        if (ctx == null) return result;
        String pkg = ctx.getPackageName();
        int[] budget = new int[] {MAX_SCAN_FILES};

        ApplicationInfo ai = ctx.getApplicationInfo();
        String dataDir = ai == null ? null : ai.dataDir;
        if (dataDir != null) scanDirForApk(ctx, new File(dataDir), "", 0, result, budget);

        if (!result.betterViaPackage && result.riskApks.size() < MAX_RISK_APKS) {
            @SuppressWarnings("deprecation")
            File externalRoot = Environment.getExternalStorageDirectory();
            if (externalRoot != null)
                scanDirForApk(
                        ctx, new File(externalRoot, "Android/data/" + pkg), "", 0, result, budget);
        }
        return result;
    }

    private static void scanDirForApk(
            Context ctx,
            File dir,
            String relativePath,
            int depth,
            DirScanResult result,
            int[] budget) {
        if (result.betterViaPackage
                || result.riskApks.size() >= MAX_RISK_APKS
                || budget[0] <= 0
                || depth > MAX_SCAN_DEPTH) return;
        if (dir == null || !dir.isDirectory()) return;
        if (shouldSkipDir(relativePath, dir.getAbsolutePath(), dir.getName(), ctx.getPackageName()))
            return;

        File[] files = dir.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            if (result.betterViaPackage
                    || result.riskApks.size() >= MAX_RISK_APKS
                    || budget[0] <= 0) return;
            File file = files[i];
            budget[0]--;
            if (file.isDirectory()) {
                String childPath =
                        relativePath.length() == 0
                                ? file.getName()
                                : relativePath + "/" + file.getName();
                scanDirForApk(ctx, file, childPath, depth + 1, result, budget);
            } else if (file.getName().toLowerCase(Locale.US).endsWith(".apk")) {
                String path = file.getAbsolutePath();
                if (isBetterViaApk(ctx, path)) result.betterViaPackage = true;
                else result.riskApks.add(path);
            }
        }
    }

    private static boolean shouldSkipDir(
            String relativePath, String absPath, String name, String pkg) {
        for (int i = 0; i < SKIP_DIR_PATHS.length; i++) {
            String rule = SKIP_DIR_PATHS[i];
            if (rule == null || rule.length() == 0) continue;
            if (rule.indexOf('/') < 0) {
                if (rule.equals(name)) return true;
                continue;
            }
            if (rule.indexOf('{') >= 0)
                rule = rule.replace("{pkg}", pkg).replace("{Packagename}", pkg);
            while (rule.length() > 1 && rule.endsWith("/"))
                rule = rule.substring(0, rule.length() - 1);
            if (rule.startsWith("/")) {
                if (absPath.equals(rule) || absPath.startsWith(rule + "/")) return true;
            } else if (relativePath.equals(rule) || relativePath.startsWith(rule + "/")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBetterViaApk(Context ctx, String apkPath) {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageArchiveInfo(apkPath, 0);
            return info != null && BETTERVIA_PACKAGE.equals(info.packageName);
        } catch (Throwable ignored) {
        }
        return false;
    }
}
