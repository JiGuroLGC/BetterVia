package com.jiguro.bettervia;

import android.content.Context;
import android.os.SystemClock;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class TimeProvider {

    private static final String TIME_API_URL =
            "http://acs.m.taobao.com/gw/mtop.common.getTimestamp/";

    private static volatile long baseTime = 0L;

    private static volatile long baseElapsed = 0L;

    private static volatile boolean onlineSynced = false;

    private TimeProvider() {}

    public static void init(final Context ctx) {
        baseTime = System.currentTimeMillis();
        baseElapsed = SystemClock.elapsedRealtime();
        onlineSynced = false;

        if (syncing) {
            return;
        }
        syncing = true;
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    long onlineTime = fetchOnlineTimestamp(ctx);
                                    baseTime = onlineTime;
                                    baseElapsed = SystemClock.elapsedRealtime();
                                    onlineSynced = true;
                                } catch (Exception e) {
                                    onlineSynced = false;
                                } finally {
                                    syncing = false;
                                }
                            }
                        })
                .start();
    }

    public static boolean isOnlineSynced() {
        return onlineSynced;
    }

    public static long now() {
        return baseTime + (SystemClock.elapsedRealtime() - baseElapsed);
    }

    private static volatile boolean syncing = false;

    private static long fetchOnlineTimestamp(Context ctx) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(TIME_API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP " + code);
            }

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONObject data = json.getJSONObject("data");
            String t = data.getString("t");
            return Long.parseLong(t);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
