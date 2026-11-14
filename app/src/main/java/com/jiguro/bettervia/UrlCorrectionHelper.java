package com.jiguro.bettervia;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import de.robv.android.xposed.*;

import java.util.*;

public class UrlCorrectionHelper {

    public static final String KEY_ENABLE = "url_correction_enable";
    public static final String KEY_FULLWIDTH = "url_correction_fullwidth";
    public static final String KEY_SPACE = "url_correction_space";
    public static final String KEY_MISSPELL = "url_correction_misspell";
    public static final String KEY_DOT = "url_correction_dot";
    public static final String KEY_PROTOCOL = "url_correction_protocol";
    public static final String KEY_TLD = "url_correction_tld";

    private static boolean isInitialized = false;
    private static Hook hookRef;
    private static XC_MethodHook.Unhook searchHook = null;

    private static String lastCorrected = "";
    private static long lastShownTime = 0;
    private static final long DEDUP_MS = 3000;
    private static final int DISPLAY_DURATION = 6000;

    private static final Map<String, String> MISSPELLINGS = new HashMap<String, String>();

    static {
        MISSPELLINGS.put("gogle", "google");
        MISSPELLINGS.put("googel", "google");
        MISSPELLINGS.put("goolge", "google");
        MISSPELLINGS.put("gooogle", "google");
        MISSPELLINGS.put("gogole", "google");
        MISSPELLINGS.put("githbu", "github");
        MISSPELLINGS.put("githib", "github");
        MISSPELLINGS.put("gitbub", "github");
        MISSPELLINGS.put("gihub", "github");
        MISSPELLINGS.put("youtbe", "youtube");
        MISSPELLINGS.put("yotube", "youtube");
        MISSPELLINGS.put("youube", "youtube");
        MISSPELLINGS.put("facebok", "facebook");
        MISSPELLINGS.put("facbook", "facebook");
        MISSPELLINGS.put("faceboo", "facebook");
        MISSPELLINGS.put("twiter", "twitter");
        MISSPELLINGS.put("twtter", "twitter");
        MISSPELLINGS.put("wikpedia", "wikipedia");
        MISSPELLINGS.put("wikipedai", "wikipedia");
        MISSPELLINGS.put("baiduu", "baidu");
        MISSPELLINGS.put("taoba", "taobao");
        MISSPELLINGS.put("taobaoo", "taobao");
        MISSPELLINGS.put("souhu", "sohu");
        MISSPELLINGS.put("sougo", "sogou");
        MISSPELLINGS.put("bilibi", "bilibili");
    }

    private static final Map<String, String> SCHEME_FIXES = new HashMap<String, String>();

    static {
        SCHEME_FIXES.put("htp", "http");
        SCHEME_FIXES.put("htt", "http");
        SCHEME_FIXES.put("hpp", "http");
        SCHEME_FIXES.put("hppt", "http");
        SCHEME_FIXES.put("htps", "https");
        SCHEME_FIXES.put("htts", "https");
        SCHEME_FIXES.put("httsp", "https");
        SCHEME_FIXES.put("hptps", "https");
        SCHEME_FIXES.put("hppts", "https");
        SCHEME_FIXES.put("httpss", "https");
        SCHEME_FIXES.put("htttps", "https");
        SCHEME_FIXES.put("htps", "https");
    }

    private static final Map<String, String> TLD_FIXES = new HashMap<String, String>();

    static {
        TLD_FIXES.put("con", "com");
        TLD_FIXES.put("cmo", "com");
        TLD_FIXES.put("ocm", "com");
        TLD_FIXES.put("comm", "com");
        TLD_FIXES.put("c0m", "com");
        TLD_FIXES.put("comn", "com");
        TLD_FIXES.put("ogr", "org");
        TLD_FIXES.put("otg", "org");
        TLD_FIXES.put("og", "org");
        TLD_FIXES.put("nte", "net");
        TLD_FIXES.put("ent", "net");
        TLD_FIXES.put("cm", "com");
        TLD_FIXES.put("om", "com");
        TLD_FIXES.put("on", "com");
        TLD_FIXES.put("cnm", "com");
        TLD_FIXES.put("cor", "com");
    }

    private static final String[] KNOWN_TLDS = {
        ".com",
        ".cn",
        ".net",
        ".org",
        ".io",
        ".top",
        ".xyz",
        ".cc",
        ".me",
        ".tv",
        ".info",
        ".site",
        ".vip",
        ".co",
        ".us",
        ".uk",
        ".jp",
        ".tw",
        ".hk",
        ".au",
        ".ru",
        ".de",
        ".fr",
        ".app",
        ".dev",
        ".wiki",
        ".tech",
        ".shop",
        ".online",
        ".club",
        ".work",
        ".fun",
        ".live",
        ".link",
        ".pro",
        ".biz",
        ".mobi",
        ".name",
        ".wang",
        ".cloud",
        ".store",
        ".gov",
        ".edu",
        ".ca",
        ".in",
        ".br",
        ".kr",
        ".it",
        ".es",
        ".nl",
        ".se",
        ".ch",
        ".at",
        ".be",
        ".dk",
        ".fi",
        ".no",
        ".pl",
        ".cz",
        ".ro",
        ".gr",
        ".tr",
        ".mx",
        ".ar",
        ".cl",
        ".za",
        ".sg",
        ".my",
        ".th",
        ".vn",
        ".ph",
        ".id",
        ".nz",
        ".il",
        ".ae",
        ".sa",
        ".eg",
        ".int",
        ".mil",
        ".museum",
        ".aero",
        ".asia",
        ".cat",
        ".jobs",
        ".post",
        ".tel",
        ".travel",
        ".xxx",
        ".eu",
        ".asia",
        ".ai",
        ".io",
        ".am",
        ".bz",
        ".cm",
        ".cx",
        ".fm",
        ".gd",
        ".gg",
        ".gl",
        ".gs",
        ".gy",
        ".hm",
        ".hn",
        ".ht",
        ".im",
        ".je",
        ".jm",
        ".ke",
        ".ki",
        ".kn",
        ".ky",
        ".kz",
        ".la",
        ".lb",
        ".lc",
        ".li",
        ".lk",
        ".ls",
        ".lt",
        ".lu",
        ".lv",
        ".ly",
        ".ma",
        ".md",
        ".mg",
        ".mk",
        ".ml",
        ".mm",
        ".mn",
        ".mo",
        ".mp",
        ".mq",
        ".mr",
        ".ms",
        ".mt",
        ".mu",
        ".mv",
        ".mw",
        ".mz",
        ".na",
        ".nc",
        ".ne",
        ".nf",
        ".ng",
        ".ni",
        ".np",
        ".nr",
        ".nu",
        ".om",
        ".pa",
        ".pe",
        ".pf",
        ".pg",
        ".pk",
        ".pm",
        ".pn",
        ".pr",
        ".ps",
        ".pt",
        ".pw",
        ".py",
        ".qa",
        ".re",
        ".rw",
        ".sb",
        ".sc",
        ".sd",
        ".sh",
        ".si",
        ".sk",
        ".sl",
        ".sm",
        ".sn",
        ".so",
        ".sr",
        ".st",
        ".su",
        ".sv",
        ".sx",
        ".sz",
        ".tc",
        ".td",
        ".tf",
        ".tg",
        ".tj",
        ".tk",
        ".tl",
        ".tm",
        ".tn",
        ".to",
        ".tp",
        ".tt",
        ".tz",
        ".ua",
        ".ug",
        ".uy",
        ".uz",
        ".vc",
        ".ve",
        ".vg",
        ".vi",
        ".vu",
        ".wf",
        ".ws",
        ".ye",
        ".yt",
        ".za",
        ".zm",
        ".zw",
        ".abogado",
        ".ac",
        ".accountant",
        ".actor",
        ".ad",
        ".ae",
        ".af",
        ".ag",
        ".agency",
        ".alsace",
        ".amsterdam",
        ".apartments",
        ".army",
        ".associates",
        ".attorney",
        ".auction",
        ".audio",
        ".auto",
        ".band",
        ".bank",
        ".bar",
        ".barcelona",
        ".bayern",
        ".beer",
        ".berlin",
        ".best",
        ".bet",
        ".bible",
        ".bid",
        ".bike",
        ".bio",
        ".black",
        ".blue",
        ".boat",
        ".bond",
        ".boston",
        ".broker",
        ".brother",
        ".build",
        ".builders",
        ".business",
        ".cab",
        ".cafe",
        ".camera",
        ".camp",
        ".capital",
        ".car",
        ".cards",
        ".care",
        ".careers",
        ".cash",
        ".casino",
        ".catering",
        ".center",
        ".ceo",
        ".chat",
        ".cheap",
        ".christmas",
        ".church",
        ".city",
        ".claims",
        ".clean",
        ".click",
        ".clinic",
        ".clothing",
        ".coach",
        ".codes",
        ".coffee",
        ".college",
        ".cologne",
        ".community",
        ".company",
        ".computer",
        ".condos",
        ".construction",
        ".consulting",
        ".contractors",
        ".cool",
        ".country",
        ".coupon",
        ".courses",
        ".credit",
        ".creditcard",
        ".cruise",
        ".dad",
        ".dance",
        ".date",
        ".dating",
        ".day",
        ".delivery",
        ".democrat",
        ".dental",
        ".dentist",
        ".desi",
        ".diamonds",
        ".diet",
        ".digital",
        ".direct",
        ".directory",
        ".discount",
        ".doctor",
        ".dog",
        ".domains",
        ".download",
        ".earth",
        ".eco",
        ".education",
        ".email",
        ".energy",
        ".engineer",
        ".engineering",
        ".enterprises",
        ".equipment",
        ".estate",
        ".events",
        ".exchange",
        ".expert",
        ".exposed",
        ".express",
        ".fail",
        ".faith",
        ".family",
        ".fans",
        ".farm",
        ".fashion",
        ".film",
        ".finance",
        ".financial",
        ".fish",
        ".fishing",
        ".fit",
        ".fitness",
        ".flights",
        ".florist",
        ".flowers",
        ".football",
        ".forex",
        ".fund",
        ".furniture",
        ".futbol",
        ".gallery",
        ".game",
        ".garden",
        ".gift",
        ".gifts",
        ".gives",
        ".glass",
        ".global",
        ".gold",
        ".golf",
        ".graphics",
        ".gratis",
        ".green",
        ".gripe",
        ".group",
        ".guide",
        ".guitars",
        ".guru",
        ".haus",
        ".health",
        ".healthcare",
        ".help",
        ".hiphop",
        ".hockey",
        ".holdings",
        ".holiday",
        ".homes",
        ".horse",
        ".hospital",
        ".host",
        ".hosting",
        ".house",
        ".how",
        ".immo",
        ".immobilien",
        ".industries",
        ".institute",
        ".insurance",
        ".international",
        ".investments",
        ".irish",
        ".jewelry",
        ".juegos",
        ".kaufen",
        ".kim",
        ".kitchen",
        ".land",
        ".law",
        ".lawyer",
        ".lease",
        ".legal",
        ".life",
        ".lighting",
        ".limited",
        ".limo",
        ".loan",
        ".loans",
        ".london",
        ".lotto",
        ".love",
        ".ltd",
        ".luxury",
        ".maison",
        ".management",
        ".market",
        ".marketing",
        ".mba",
        ".media",
        ".memorial",
        ".miami",
        ".moda",
        ".money",
        ".mortgage",
        ".movie",
        ".nagoya",
        ".navy",
        ".network",
        ".news",
        ".ninja",
        ".nyc",
        ".okinawa",
        ".one",
        ".onl",
        ".ooo",
        ".organic",
        ".osaka",
        ".page",
        ".paris",
        ".partners",
        ".parts",
        ".party",
        ".pet",
        ".photography",
        ".photos",
        ".pics",
        ".pictures",
        ".pink",
        ".pizza",
        ".place",
        ".plumbing",
        ".plus",
        ".poker",
        ".porn",
        ".press",
        ".promo",
        ".properties",
        ".property",
        ".pub",
        ".qpon",
        ".realtor",
        ".recipes",
        ".red",
        ".rehab",
        ".reise",
        ".reisen",
        ".ren",
        ".rent",
        ".rentals",
        ".repair",
        ".report",
        ".republican",
        ".rest",
        ".restaurant",
        ".review",
        ".reviews",
        ".rip",
        ".rocks",
        ".rodeo",
        ".rum",
        ".ryukyu",
        ".sale",
        ".sales",
        ".salon",
        ".sarl",
        ".school",
        ".schule",
        ".science",
        ".scot",
        ".services",
        ".sex",
        ".sexy",
        ".shiksha",
        ".shoes",
        ".show",
        ".singles",
        ".social",
        ".software",
        ".solar",
        ".solutions",
        ".soy",
        ".space",
        ".sport",
        ".sports",
        ".studio",
        ".style",
        ".supplies",
        ".supply",
        ".support",
        ".surgery",
        ".systems",
        ".tattoo",
        ".tax",
        ".taxi",
        ".team",
        ".technology",
        ".tennis",
        ".theater",
        ".tires",
        ".today",
        ".tools",
        ".tours",
        ".town",
        ".toys",
        ".trading",
        ".training",
        ".tube",
        ".university",
        ".uno",
        ".vacations",
        ".vegas",
        ".ventures",
        ".versicherung",
        ".vet",
        ".viajes",
        ".video",
        ".villas",
        ".vin",
        ".vision",
        ".vodka",
        ".vote",
        ".voting",
        ".voto",
        ".voyage",
        ".wales",
        ".watch",
        ".webcam",
        ".website",
        ".wedding",
        ".whoswho",
        ".wien",
        ".win",
        ".wine",
        ".work",
        ".works",
        ".world",
        ".wtf",
        ".xin",
        ".yachts",
        ".yokohama",
        ".zone"
    };

    public static void init(final Context ctx, final ClassLoader cl, final Hook hookRef) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        UrlCorrectionHelper.hookRef = hookRef;

        try {
            String className =
                    ViaClassMapping.getClassName(
                            ViaClassMapping.ClassMethodKey.SEARCH_CORRECTION_HOOK, ctx);
            String methodName =
                    ViaClassMapping.getMethodName(
                            ViaClassMapping.ClassMethodKey.SEARCH_CORRECTION_HOOK, ctx);

            searchHook =
                    XposedHelpers.findAndHookMethod(
                            className,
                            cl,
                            methodName,
                            String.class,
                            String.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                        throws Throwable {
                                    String template = (String) param.args[0];
                                    String keyword = (String) param.args[1];
                                    handleSearchKeyword(ctx, template, keyword);
                                }
                            });
            Hook.bvLog("[BetterVia] 网址纠错增强 Hook 已挂载: " + className + "." + methodName);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 网址纠错增强 Hook 挂载失败: " + t);
        }
    }

    private static void handleSearchKeyword(Context ctx, String template, String keyword) {
        try {
            if (ctx == null || keyword == null || keyword.isEmpty()) {
                return;
            }
            if (!Hook.getPrefBoolean(ctx, KEY_ENABLE, false)) {
                return;
            }

            String corrected = detectCorrection(ctx, keyword);
            if (corrected == null || corrected.equalsIgnoreCase(keyword.trim())) {
                return;
            }

            long now = System.currentTimeMillis();
            if (corrected.equals(lastCorrected) && now - lastShownTime < DEDUP_MS) {
                return;
            }
            lastCorrected = corrected;
            lastShownTime = now;

            Hook.bvLog("[BetterVia] 网址纠错: \"" + keyword + "\" -> \"" + corrected + "\"");
            showCorrectionToast(ctx, corrected);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 网址纠错处理失败: " + t);
        }
    }

    private static String detectCorrection(Context ctx, String keyword) {
        String original = keyword.trim();
        if (original.isEmpty()) {
            return null;
        }
        String result = original;
        for (int round = 0; round < 6; round++) {
            String prev = result;

            if (Hook.getPrefBoolean(ctx, KEY_FULLWIDTH, true)) {
                String converted = toHalfWidth(result);
                if (!converted.equals(result)) {
                    result = converted;
                }
            }

            if (Hook.getPrefBoolean(ctx, KEY_SPACE, true)) {
                String noSpace = result.replaceAll("[\\s\u3000]+", "");
                if (!noSpace.equals(result)) {
                    result = noSpace;
                }
            }

            if (Hook.getPrefBoolean(ctx, KEY_PROTOCOL, true)) {
                result = fixProtocol(result);
            }

            if (Hook.getPrefBoolean(ctx, KEY_MISSPELL, true)) {
                result = fixMisspell(result);
            }

            if (Hook.getPrefBoolean(ctx, KEY_DOT, true)) {
                result = fixMissingDot(result);
            }

            if (Hook.getPrefBoolean(ctx, KEY_TLD, true)) {
                result = fixTopLevelDomain(result);
            }

            if (result.equals(prev)) {
                break;
            }
        }

        if (result.equalsIgnoreCase(original)) {
            return null;
        }
        if (!looksLikeUrl(result)) {
            return null;
        }
        return result;
    }

    private static String toHalfWidth(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u3000') {
                sb.append(' ');
            } else if (c >= '\uFF01' && c <= '\uFF5E') {
                sb.append((char) (c - 0xFEE0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String fixMisspell(String original) {
        String lower = original.toLowerCase().trim();
        if (lower.isEmpty()) {
            return original;
        }

        String tld = null;
        String host = lower;
        String[] tlds = {".com", ".net", ".org", ".cn", ".io", ".co", ".tv"};
        for (String t : tlds) {
            if (lower.endsWith(t) && lower.length() > t.length()) {
                tld = t;
                host = lower.substring(0, lower.length() - t.length());
                break;
            }
        }

        boolean hasWww = host.startsWith("www.");
        String bareHost = hasWww ? host.substring(4) : host;

        String correctHost = MISSPELLINGS.get(bareHost);
        if (correctHost == null) {
            return original;
        }

        String resultHost = hasWww ? "www." + correctHost : correctHost;
        return resultHost + (tld != null ? tld : ".com");
    }

    private static String fixMissingDot(String original) {
        String lower = original.toLowerCase().trim();
        if (lower.isEmpty() || lower.indexOf(' ') >= 0) {
            return original;
        }

        if (lower.startsWith("www")
                && lower.length() > 4
                && !lower.startsWith("www.")
                && lower.indexOf('.') > 0) {
            char after = lower.charAt(3);
            if (Character.isLetterOrDigit(after) || after == '-') {
                return "www." + original.substring(3);
            }
        }

        if (lower.startsWith("ww.") && lower.length() > 4) {
            return "www" + original.substring(2);
        }

        String[] bareTlds = {"com", "net", "org", "cn", "io"};
        for (String t : bareTlds) {
            if (lower.length() > t.length() + 4 && lower.endsWith(t)) {
                String prefix = original.substring(0, original.length() - t.length());
                if (!prefix.contains(".")
                        && !prefix.contains("/")
                        && !prefix.contains("?")
                        && !prefix.contains("@")
                        && prefix.matches("[a-zA-Z0-9\\-]+")) {
                    return prefix + "." + original.substring(original.length() - t.length());
                }
            }
        }
        return original;
    }

    private static String fixProtocol(String original) {
        String s = original.trim();
        if (s.isEmpty()) {
            return original;
        }
        String normalized = toHalfWidth(s);

        int sepStart = -1;
        String rest = null;
        int idx = normalized.indexOf("://");
        if (idx > 0) {
            sepStart = idx;
            rest = normalized.substring(idx);
        } else {
            idx = normalized.indexOf("//");
            if (idx > 0) {
                sepStart = idx;
                rest = normalized.substring(idx);
            } else {
                idx = normalized.indexOf(':');
                if (idx > 0) {
                    sepStart = idx;
                    rest = normalized.substring(idx);
                }
            }
        }
        if (sepStart <= 0 || rest == null) {
            return original;
        }

        String scheme = normalized.substring(0, sepStart);
        String fixedScheme = SCHEME_FIXES.get(scheme.toLowerCase());
        if (fixedScheme == null
                && ("http".equals(scheme.toLowerCase()) || "https".equals(scheme.toLowerCase()))) {
            fixedScheme = scheme.toLowerCase();
        }
        if (fixedScheme == null) {
            return original;
        }

        String content = rest;
        if (content.startsWith("://")) {
            content = content.substring(3);
        } else if (content.startsWith("//")) {
            content = content.substring(2);
        } else if (content.startsWith(":/")) {
            content = content.substring(2);
        } else if (content.startsWith(":")) {
            content = content.substring(1);
        }
        if (content.isEmpty()) {
            return original;
        }

        String fixed = fixedScheme + "://" + content;
        if (fixed.equalsIgnoreCase(s)) {
            return original;
        }
        return fixed;
    }

    private static String fixTopLevelDomain(String original) {
        String lower = original.toLowerCase().trim();
        if (lower.isEmpty()) {
            return original;
        }
        int lastDot = lower.lastIndexOf('.');
        if (lastDot <= 0 || lastDot >= lower.length() - 1) {
            return original;
        }
        String domainPart = original.substring(0, lastDot);
        String tld = lower.substring(lastDot + 1);

        if (isKnownTld(tld)) {
            return original;
        }

        String fixedTld = TLD_FIXES.get(tld);
        if (fixedTld == null) {
            fixedTld = findClosestTld(tld);
        }
        if (fixedTld == null || fixedTld.equals(tld)) {
            return original;
        }
        String fixed = domainPart + "." + fixedTld;
        if (fixed.equalsIgnoreCase(original)) {
            return original;
        }
        return fixed;
    }

    private static String findClosestTld(String tld) {
        if (tld == null || tld.isEmpty()) {
            return null;
        }
        int maxDist = tld.length() <= 2 ? 1 : 2;
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        int count = 0;
        for (String known : KNOWN_TLDS) {
            String k = known.substring(1);
            int dist = levenshtein(tld, k);
            if (dist < bestDist) {
                bestDist = dist;
                best = k;
                count = 1;
            } else if (dist == bestDist) {
                count++;
            }
        }
        if (count == 1 && best != null && bestDist <= maxDist) {
            return best;
        }
        return null;
    }

    private static int levenshtein(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] =
                        Math.min(
                                Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                                dp[i - 1][j - 1] + cost);
            }
        }
        return dp[m][n];
    }

    private static boolean isKnownTld(String tld) {
        String dotTld = "." + tld;
        for (String known : KNOWN_TLDS) {
            if (known.equals(dotTld)) {
                return true;
            }
        }
        return false;
    }

    private static boolean looksLikeUrl(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        String lower = s.toLowerCase();
        if (lower.contains("://")) {
            return true;
        }
        if (lower.startsWith("www.") || lower.startsWith("ww.")) {
            return true;
        }
        String[] tlds = {
            ".com", ".cn", ".net", ".org", ".io", ".top", ".xyz", ".cc", ".me", ".tv", ".info",
            ".site", ".vip", ".co", ".us", ".uk", ".jp", ".tw", ".hk", ".au", ".ru", ".de", ".fr",
            ".app", ".dev", ".wiki", ".tech", ".shop", ".online", ".club", ".work", ".fun", ".live",
            ".link", ".pro", ".biz", ".mobi", ".name", ".wang", ".cloud", ".store", ".gov", ".edu"
        };
        for (String tld : tlds) {
            if (lower.endsWith(tld)) {
                return true;
            }
        }
        return false;
    }

    private static void showCorrectionToast(final Context ctx, final String correctedUrl) {
        if (ctx == null || correctedUrl == null) {
            return;
        }
        Activity activity = hookRef != null ? hookRef.getActivityFrom(ctx) : null;
        if (activity == null) {
            return;
        }
        final Activity finalActivity = activity;
        finalActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (finalActivity.isFinishing() || finalActivity.isDestroyed()) {
                            return;
                        }
                        try {
                            final LinearLayout overlay = new LinearLayout(finalActivity);
                            overlay.setOrientation(LinearLayout.VERTICAL);
                            GradientDrawable bg = new GradientDrawable();
                            bg.setColor(0xCC000000);
                            bg.setCornerRadius(Hook.dp(finalActivity, 12));
                            overlay.setBackgroundDrawable(bg);

                            TextView titleView = new TextView(finalActivity);
                            titleView.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "url_correction_toast_title"));
                            titleView.setTextColor(Color.WHITE);
                            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                            titleView.setTypeface(null, Typeface.BOLD);
                            titleView.setPadding(
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 12),
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 4));

                            final TextView urlView = new TextView(finalActivity);
                            urlView.setText(correctedUrl);
                            urlView.setTextColor(Color.WHITE);
                            urlView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            urlView.setSingleLine(true);
                            urlView.setEllipsize(TextUtils.TruncateAt.END);
                            urlView.setMaxWidth(Hook.dp(finalActivity, 320));
                            urlView.setPadding(
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 4),
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 4));

                            TextView hintView = new TextView(finalActivity);
                            hintView.setText(
                                    LocalizedStringProvider.getInstance()
                                            .get(ctx, "url_correction_toast_hint"));
                            hintView.setTextColor(Color.GREEN);
                            hintView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                            hintView.setPadding(
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 4),
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 12));

                            overlay.addView(titleView);
                            overlay.addView(urlView);
                            overlay.addView(hintView);

                            overlay.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openCorrectedUrl(ctx, correctedUrl);
                                            removeOverlay(overlay);
                                        }
                                    });

                            FrameLayout.LayoutParams params =
                                    new FrameLayout.LayoutParams(
                                            Hook.dp(finalActivity, 320),
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                            params.setMargins(
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 16),
                                    Hook.dp(finalActivity, 16),
                                    0);

                            ViewGroup decorView =
                                    (ViewGroup) finalActivity.getWindow().getDecorView();
                            decorView.addView(overlay, params);

                            TranslateAnimation slideIn =
                                    new TranslateAnimation(
                                            Animation.RELATIVE_TO_PARENT,
                                            -1.0f,
                                            Animation.RELATIVE_TO_PARENT,
                                            0.0f,
                                            Animation.RELATIVE_TO_PARENT,
                                            0.0f,
                                            Animation.RELATIVE_TO_PARENT,
                                            0.0f);
                            slideIn.setDuration(300);
                            slideIn.setInterpolator(new DecelerateInterpolator());
                            overlay.startAnimation(slideIn);

                            new Handler(Looper.getMainLooper())
                                    .postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    TranslateAnimation slideOut =
                                                            new TranslateAnimation(
                                                                    Animation.RELATIVE_TO_PARENT,
                                                                    0.0f,
                                                                    Animation.RELATIVE_TO_PARENT,
                                                                    1.0f,
                                                                    Animation.RELATIVE_TO_PARENT,
                                                                    0.0f,
                                                                    Animation.RELATIVE_TO_PARENT,
                                                                    0.0f);
                                                    slideOut.setDuration(300);
                                                    slideOut.setInterpolator(
                                                            new AccelerateInterpolator());
                                                    slideOut.setAnimationListener(
                                                            new Animation.AnimationListener() {
                                                                @Override
                                                                public void onAnimationStart(
                                                                        Animation animation) {}

                                                                @Override
                                                                public void onAnimationEnd(
                                                                        Animation animation) {
                                                                    removeOverlay(overlay);
                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(
                                                                        Animation animation) {}
                                                            });
                                                    overlay.startAnimation(slideOut);
                                                }
                                            },
                                            DISPLAY_DURATION);

                            Hook.bvLog("[BetterVia] 已显示网址纠错提示框: " + correctedUrl);
                        } catch (Throwable t) {
                            Hook.bvLog("[BetterVia] 网址纠错提示框显示失败: " + t);
                        }
                    }
                });
    }

    private static void removeOverlay(final View overlay) {
        try {
            ViewGroup parent = (ViewGroup) overlay.getParent();
            if (parent != null) {
                parent.removeView(overlay);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void openCorrectedUrl(Context ctx, String url) {
        try {
            String target = url;
            if (!target.contains("://")) {
                target = "https://" + target;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
            intent.setPackage(ctx.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            Hook.bvLog("[BetterVia] 网址纠错跳转: " + target);
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 网址纠错跳转失败: " + t);
        }
    }

    public static void showDialog(final Context ctx, final Hook hookRef) {
        UrlCorrectionHelper.hookRef = hookRef;
        final Activity act = hookRef.getActivityFrom(ctx);
        if (act == null) {
            return;
        }

        final boolean[] currentEnable = new boolean[] {Hook.getPrefBoolean(ctx, KEY_ENABLE, false)};
        final boolean[] schemeSel =
                new boolean[] {
                    Hook.getPrefBoolean(ctx, KEY_FULLWIDTH, true),
                    Hook.getPrefBoolean(ctx, KEY_SPACE, true),
                    Hook.getPrefBoolean(ctx, KEY_MISSPELL, true),
                    Hook.getPrefBoolean(ctx, KEY_DOT, true),
                    Hook.getPrefBoolean(ctx, KEY_PROTOCOL, true),
                    Hook.getPrefBoolean(ctx, KEY_TLD, true)
                };

        final String[] SCHEME_KEYS =
                new String[] {
                    "url_correction_scheme_fullwidth",
                    "url_correction_scheme_space",
                    "url_correction_scheme_misspell",
                    "url_correction_scheme_dot",
                    "url_correction_scheme_protocol",
                    "url_correction_scheme_tld"
                };

        final Runnable persist =
                new Runnable() {
                    @Override
                    public void run() {
                        Hook.putPrefBoolean(ctx, KEY_ENABLE, currentEnable[0]);
                        Hook.putPrefBoolean(ctx, KEY_FULLWIDTH, schemeSel[0]);
                        Hook.putPrefBoolean(ctx, KEY_SPACE, schemeSel[1]);
                        Hook.putPrefBoolean(ctx, KEY_MISSPELL, schemeSel[2]);
                        Hook.putPrefBoolean(ctx, KEY_DOT, schemeSel[3]);
                        Hook.putPrefBoolean(ctx, KEY_PROTOCOL, schemeSel[4]);
                        Hook.putPrefBoolean(ctx, KEY_TLD, schemeSel[5]);
                        Hook.bvLog("[BetterVia] 网址纠错增强设置已保存");
                    }
                };

        SettingsUI.showPage(
                act,
                "url_correction_dialog_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final CompoundButton.OnCheckedChangeListener switchListener =
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView, boolean isChecked) {
                                        currentEnable[0] = isChecked;
                                        persist.run();
                                    }
                                };
                        list.addSwitchItem(
                                "url_correction_enable",
                                "url_correction_enable_hint",
                                currentEnable[0],
                                switchListener);

                        final Runnable notesRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMessageDialog(
                                                act,
                                                "url_correction_notes_title",
                                                "url_correction_notes_content");
                                    }
                                };
                        list.addItemKeepRipple("url_correction_notes_title", notesRunnable);

                        list.addSectionHeader("url_correction_advanced");

                        final int schemeRow = list.getItemCount();
                        final Runnable schemeRunnable =
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingsUI.showMultiSelectDialog(
                                                act,
                                                "url_correction_schemes",
                                                SCHEME_KEYS,
                                                schemeSel.clone(),
                                                "dialog_ok",
                                                "dialog_cancel",
                                                null,
                                                new SettingsUI.OnMultiSelectListener() {
                                                    @Override
                                                    public void onResult(
                                                            int which, boolean[] checked) {
                                                        if (which
                                                                != android.content.DialogInterface
                                                                        .BUTTON_POSITIVE) {
                                                            return;
                                                        }
                                                        System.arraycopy(
                                                                checked,
                                                                0,
                                                                schemeSel,
                                                                0,
                                                                schemeSel.length);
                                                        list.updateItemText(
                                                                schemeRow,
                                                                Hook.buildUaSelectionSummary(
                                                                        ctx,
                                                                        SCHEME_KEYS,
                                                                        schemeSel),
                                                                true);
                                                        persist.run();
                                                    }
                                                });
                                    }
                                };
                        list.addItemKeepRipple("url_correction_schemes", schemeRunnable);
                        list.updateItemText(
                                schemeRow,
                                Hook.buildUaSelectionSummary(ctx, SCHEME_KEYS, schemeSel),
                                true);

                        content.addView(list);
                    }
                });
    }
}
