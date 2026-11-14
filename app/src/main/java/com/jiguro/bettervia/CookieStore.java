package com.jiguro.bettervia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class CookieStore {

    static final String TABLE = "cookies";
    private static final long CHROME_EPOCH_OFFSET_MS = 11644473600000L;

    static class DomainInfo {
        final String domain;
        final int cookieCount;

        DomainInfo(String domain, int cookieCount) {
            this.domain = domain;
            this.cookieCount = cookieCount;
        }
    }

    static class CookieInfo {
        long creationUtc;
        String hostKey = "";
        String name = "";
        String value = "";
        String path = "";
        long expiresUtc;
        long lastAccessUtc;
        long lastUpdateUtc;
        boolean secure;
        boolean httpOnly;
        boolean persistent;
        boolean hasExpires;
        int priority;
        int samesite;
        int sourcePort;
        int sourceType;
    }

    private CookieStore() {}

    static String dbPath(Context ctx) {
        return "/data/user/0/" + ctx.getPackageName() + "/app_webview/Default/Cookies";
    }

    static List<DomainInfo> loadDomains(Context ctx) {
        List<DomainInfo> result = new ArrayList<DomainInfo>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            if (!new File(dbPath(ctx)).exists()) {
                return result;
            }
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READONLY);
            if (!tableExists(db)) {
                return result;
            }
            cursor =
                    db.rawQuery(
                            "SELECT host_key, COUNT(*) FROM " + TABLE + " GROUP BY host_key", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String domain = cursor.getString(0);
                    if (domain == null || domain.isEmpty()) {
                        domain = str(ctx, "cookie_unknown_domain");
                    }
                    result.add(new DomainInfo(domain, cursor.getInt(1)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 读取Cookie域名列表失败: " + e);
        } finally {
            closeQuietly(cursor, db);
        }
        Collections.sort(
                result,
                new Comparator<DomainInfo>() {
                    @Override
                    public int compare(DomainInfo a, DomainInfo b) {
                        return a.domain.compareToIgnoreCase(b.domain);
                    }
                });
        return result;
    }

    static int deleteDomains(Context ctx, List<String> domains) {
        SQLiteDatabase db = null;
        int deleted = 0;
        List<CookieInfo> removed = new ArrayList<CookieInfo>();
        for (String domain : domains) {
            removed.addAll(loadCookies(ctx, domain));
        }
        try {
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READWRITE);
            db.beginTransaction();
            try {
                for (String domain : domains) {
                    deleted += db.delete(TABLE, "host_key = ?", new String[] {domain});
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 批量删除域名Cookie失败: " + e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        if (deleted > 0) {
            for (CookieInfo c : removed) {
                syncCookieToWebView(c, true);
            }
            flushWebViewCookies();
        }
        return deleted;
    }

    static List<CookieInfo> loadCookies(Context ctx, String domain) {
        List<CookieInfo> result = new ArrayList<CookieInfo>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            if (!new File(dbPath(ctx)).exists()) {
                return result;
            }
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READONLY);
            if (!tableExists(db)) {
                return result;
            }
            cursor =
                    db.query(
                            TABLE, null, "host_key = ?", new String[] {domain}, null, null, "name");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    result.add(readCookie(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 读取域名Cookie失败: " + e);
        } finally {
            closeQuietly(cursor, db);
        }
        return result;
    }

    static int deleteCookies(Context ctx, List<CookieInfo> cookies) {
        SQLiteDatabase db = null;
        int deleted = 0;
        try {
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READWRITE);
            db.beginTransaction();
            try {
                for (CookieInfo c : cookies) {
                    deleted +=
                            db.delete(
                                    TABLE,
                                    "creation_utc = ? AND host_key = ? AND name = ?",
                                    new String[] {
                                        String.valueOf(c.creationUtc), c.hostKey, c.name
                                    });
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 批量删除Cookie失败: " + e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        if (deleted > 0) {
            for (CookieInfo c : cookies) {
                syncCookieToWebView(c, true);
            }
            flushWebViewCookies();
        }
        return deleted;
    }

    static boolean updateCookie(
            Context ctx, CookieInfo cookie, String origHostKey, String origName, String origPath) {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READWRITE);
            ContentValues values = new ContentValues();
            values.put("host_key", cookie.hostKey);
            values.put("name", cookie.name);
            values.put("value", cookie.value);
            values.put("path", cookie.path);
            values.put("is_secure", cookie.secure ? 1 : 0);
            values.put("is_httponly", cookie.httpOnly ? 1 : 0);
            values.put("is_persistent", cookie.persistent ? 1 : 0);
            values.put("has_expires", cookie.hasExpires ? 1 : 0);
            cookie.lastUpdateUtc = TimeProvider.now() / 1000;
            values.put("last_update_utc", cookie.lastUpdateUtc);
            String where = "creation_utc = ? AND host_key = ? AND name = ?";
            String[] args = {String.valueOf(cookie.creationUtc), origHostKey, origName};
            boolean updated = db.update(TABLE, values, where, args) > 0;
            if (updated) {
                String oldPathNorm = (origPath == null || origPath.isEmpty()) ? "/" : origPath;
                String newPathNorm =
                        (cookie.path == null || cookie.path.isEmpty()) ? "/" : cookie.path;
                if (!cookie.hostKey.equals(origHostKey)
                        || !cookie.name.equals(origName)
                        || !oldPathNorm.equals(newPathNorm)) {
                    CookieInfo old = new CookieInfo();
                    old.hostKey = origHostKey;
                    old.name = origName;
                    old.path = origPath;
                    syncCookieToWebView(old, true);
                }
                syncCookieToWebView(cookie, false);
                flushWebViewCookies();
            }
            return updated;
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 更新Cookie失败: " + e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    static boolean deleteCookie(Context ctx, CookieInfo cookie) {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(dbPath(ctx), null, SQLiteDatabase.OPEN_READWRITE);
            String where = "creation_utc = ? AND host_key = ? AND name = ?";
            String[] args = {String.valueOf(cookie.creationUtc), cookie.hostKey, cookie.name};
            boolean deleted = db.delete(TABLE, where, args) > 0;
            if (deleted) {
                syncCookieToWebView(cookie, true);
                flushWebViewCookies();
            }
            return deleted;
        } catch (Exception e) {
            Hook.bvLog("[BetterVia] 删除Cookie失败: " + e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private static void syncCookieToWebView(CookieInfo c, boolean remove) {
        if (c == null || c.name == null || c.name.isEmpty()) return;
        try {
            android.webkit.CookieManager cm = android.webkit.CookieManager.getInstance();
            String path = (c.path == null || c.path.isEmpty()) ? "/" : c.path;
            String host = c.hostKey == null ? "" : c.hostKey;
            boolean domainCookie = host.startsWith(".");
            String url = "https://" + (domainCookie ? host.substring(1) : host) + path;

            StringBuilder sb = new StringBuilder(c.name).append('=');
            if (!remove) sb.append(c.value == null ? "" : c.value);
            sb.append("; Path=").append(path);
            if (domainCookie) sb.append("; Domain=").append(host);
            if (remove) {
                sb.append("; Max-Age=0");
            } else {
                if (c.hasExpires && c.expiresUtc > 0) {
                    long maxAge = c.expiresUtc - System.currentTimeMillis() / 1000;
                    sb.append("; Max-Age=").append(Math.max(0, maxAge));
                }
                if (c.secure) sb.append("; Secure");
                if (c.httpOnly) sb.append("; HttpOnly");
                if (c.samesite == 0 && c.secure) sb.append("; SameSite=None");
                else if (c.samesite == 1) sb.append("; SameSite=Lax");
                else if (c.samesite == 2) sb.append("; SameSite=Strict");
            }
            cm.setCookie(url, sb.toString());
        } catch (Throwable t) {
            Hook.bvLogVerbose("[BetterVia] 同步Cookie到WebView失败: " + t);
        }
    }

    private static void flushWebViewCookies() {
        try {
            android.webkit.CookieManager.getInstance().flush();
        } catch (Throwable ignored) {
        }
    }

    static String formatTime(Context ctx, long raw) {
        try {
            long unixMillis = toUnixMillis(raw);
            if (unixMillis <= 0) {
                return str(ctx, "cookie_field_unknown");
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date(unixMillis));
        } catch (Throwable t) {
            return str(ctx, "cookie_time_format_error");
        }
    }

    static long toUnixMillis(long raw) {
        if (raw <= 0) {
            return 0;
        }
        if (raw < 100000000000L) {
            return raw * 1000L;
        }
        if (raw < 100000000000000L) {
            return raw;
        }
        long millis = raw / 1000L;
        long windowsEpochMillis = millis - CHROME_EPOCH_OFFSET_MS;
        if (windowsEpochMillis > 0) {
            return windowsEpochMillis;
        }
        return millis;
    }

    static String sameSiteText(Context ctx, int samesite) {
        switch (samesite) {
            case -1:
            case 0:
                return str(ctx, "cookie_samesite_none");
            case 1:
                return str(ctx, "cookie_samesite_lax");
            case 2:
                return str(ctx, "cookie_samesite_strict");
            default:
                return String.format(str(ctx, "cookie_samesite_unknown"), samesite);
        }
    }

    static String sourceTypeText(Context ctx, int sourceType) {
        switch (sourceType) {
            case 0:
                return str(ctx, "cookie_source_type_none");
            case 1:
                return str(ctx, "cookie_source_type_http");
            case 2:
                return str(ctx, "cookie_source_type_https");
            case 3:
                return str(ctx, "cookie_source_type_file");
            default:
                return String.format(str(ctx, "cookie_source_type_unknown"), sourceType);
        }
    }

    private static CookieInfo readCookie(Cursor cursor) {
        CookieInfo c = new CookieInfo();
        c.creationUtc = getLongSafe(cursor, "creation_utc");
        c.hostKey = getStringSafe(cursor, "host_key");
        c.name = getStringSafe(cursor, "name");
        c.value = getStringSafe(cursor, "value");
        c.path = getStringSafe(cursor, "path");
        c.expiresUtc = getLongSafe(cursor, "expires_utc");
        c.lastAccessUtc = getLongSafe(cursor, "last_access_utc");
        c.lastUpdateUtc = getLongSafe(cursor, "last_update_utc");
        c.secure = getIntSafe(cursor, "is_secure") == 1;
        c.httpOnly = getIntSafe(cursor, "is_httponly") == 1;
        c.persistent = getIntSafe(cursor, "is_persistent") == 1;
        c.hasExpires = getIntSafe(cursor, "has_expires") == 1;
        c.priority = getIntSafe(cursor, "priority");
        c.samesite = getIntSafe(cursor, "samesite");
        c.sourcePort = getIntSafe(cursor, "source_port");
        c.sourceType = getIntSafe(cursor, "source_type");
        return c;
    }

    private static boolean tableExists(SQLiteDatabase db) {
        Cursor c = null;
        try {
            c =
                    db.rawQuery(
                            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                            new String[] {TABLE});
            return c != null && c.moveToFirst();
        } catch (Exception e) {
            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private static String getStringSafe(Cursor cursor, String column) {
        try {
            int index = cursor.getColumnIndex(column);
            if (index == -1) {
                return "";
            }
            String v = cursor.getString(index);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }

    private static long getLongSafe(Cursor cursor, String column) {
        try {
            int index = cursor.getColumnIndex(column);
            return index == -1 ? 0 : cursor.getLong(index);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int getIntSafe(Cursor cursor, String column) {
        try {
            int index = cursor.getColumnIndex(column);
            return index == -1 ? 0 : cursor.getInt(index);
        } catch (Exception e) {
            return 0;
        }
    }

    private static void closeQuietly(Cursor cursor, SQLiteDatabase db) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable ignored) {
            }
        }
        if (db != null) {
            try {
                db.close();
            } catch (Throwable ignored) {
            }
        }
    }

    private static String str(Context ctx, String key) {
        return LocalizedStringProvider.getInstance().get(ctx, key);
    }
}
