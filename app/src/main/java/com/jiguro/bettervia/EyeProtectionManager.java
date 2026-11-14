package com.jiguro.bettervia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public final class EyeProtectionManager {

    private static final String HUB_KEY = "eye_protection";

    private static final Map<Activity, View> sOverlays = new HashMap<Activity, View>();

    private static Paint sPaint;

    private static boolean sEnabled;
    private static int sTemperature = 50;
    private static int sTexture;

    private static final ActivityLifecycleHub.Listener sListener =
            new ActivityLifecycleHub.Listener() {
                @Override
                public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                    if (!sEnabled) return;
                    activity.runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    addOverlay(activity);
                                }
                            });
                }
            };

    private EyeProtectionManager() {}

    static void init(Context ctx) {
        sTemperature = Hook.prefs(ctx).getInt(Hook.KEY_EYE_TEMPERATURE, 50);
        sTexture = Hook.prefs(ctx).getInt(Hook.KEY_EYE_TEXTURE, 0);
        sEnabled = Hook.prefs(ctx).getBoolean(Hook.KEY_EYE_PROTECTION, false);
        if (sEnabled) {
            ActivityLifecycleHub.add(HUB_KEY, sListener, true);
            Hook.bvLog("[BetterVia] 护眼模式已启用");
        }
    }

    public static void setEnabled(Context ctx, boolean on) {
        sEnabled = on;
        if (on) {
            ActivityLifecycleHub.add(HUB_KEY, sListener, true);
            Hook.bvLog("[BetterVia] 护眼模式已启用");
        } else {
            ActivityLifecycleHub.setEnabled(HUB_KEY, false);
            removeAll();
            Hook.bvLog("[BetterVia] 护眼模式已停用");
        }
        Hook.putPrefBoolean(ctx, Hook.KEY_EYE_PROTECTION, on);
    }

    public static boolean isEnabled() {
        return sEnabled;
    }

    public static void applyConfig(Context ctx, int temperature, int texture) {
        sTemperature = temperature;
        sTexture = texture;
        Hook.prefs(ctx)
                .edit()
                .putInt(Hook.KEY_EYE_TEMPERATURE, temperature)
                .putInt(Hook.KEY_EYE_TEXTURE, texture)
                .apply();
        if (!sEnabled) return;
        for (Iterator<Map.Entry<Activity, View>> it = sOverlays.entrySet().iterator();
                it.hasNext(); ) {
            Map.Entry<Activity, View> entry = it.next();
            Activity activity = entry.getKey();
            if (activity.isFinishing() || activity.isDestroyed()) {
                it.remove();
                continue;
            }
            entry.getValue().invalidate();
        }
    }

    static void onActivityDestroyed(Activity activity) {
        sOverlays.remove(activity);
    }

    private static void addOverlay(Activity activity) {
        try {
            ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
            View existing = sOverlays.get(activity);
            if (existing != null) {
                rootView.removeView(existing);
            }
            View overlay =
                    new View(activity) {
                        @Override
                        protected void onDraw(Canvas canvas) {
                            super.onDraw(canvas);
                            canvas.drawColor(calculateTemperatureColor(sTemperature));
                            if (sTexture > 0) {
                                drawPaperTexture(canvas, sTexture);
                            }
                        }
                    };
            overlay.setTag("eye_protection_overlay");
            overlay.setClickable(false);
            overlay.setFocusable(false);
            overlay.setFocusableInTouchMode(false);
            rootView.addView(
                    overlay,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            sOverlays.put(activity, overlay);
            Hook.bvLogVerbose("[BetterVia] 已为 " + activity.getClass().getSimpleName() + " 添加护眼遮罩");
        } catch (Throwable t) {
            Hook.bvLog("[BetterVia] 添加护眼遮罩失败: " + t);
        }
    }

    static void removeAll() {
        for (Map.Entry<Activity, View> entry : sOverlays.entrySet()) {
            Activity activity = entry.getKey();
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                try {
                    ((ViewGroup) activity.getWindow().getDecorView()).removeView(entry.getValue());
                } catch (Throwable ignored) {
                }
            }
        }
        sOverlays.clear();
        Hook.bvLog("[BetterVia] 已移除所有护眼遮罩");
    }

    static int calculateTemperatureColor(int temperature) {
        float ratio = temperature / 100.0f;
        int alpha = (int) (0x40 * ratio);
        int r = (int) (255 * ratio);
        int g = (int) (245 * ratio);
        int b = (int) (200 * ratio);
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static Paint paint() {
        if (sPaint == null) {
            sPaint = new Paint();
        }
        return sPaint;
    }

    private static void drawPaperTexture(Canvas canvas, int textureLevel) {
        Paint paint = paint();
        paint.setColor(0x20FFFFFF);
        Random random = new Random(12345);
        int density = textureLevel / 5;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        for (int i = 0; i < density; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            float radius = random.nextFloat() * 2 + 1;
            canvas.drawCircle(x, y, radius, paint);
        }
        if (textureLevel > 50) {
            paint.setColor(0x10FFFFFF);
            for (int i = 0; i < textureLevel / 10; i++) {
                float x = random.nextFloat() * width;
                float y = random.nextFloat() * height;
                float radius = random.nextFloat() * 3 + 2;
                canvas.drawCircle(x, y, radius, paint);
            }
        }
    }

    static void drawTexturePreview(Canvas canvas, int textureLevel, int width, int height) {
        Paint paint = paint();
        paint.setColor(0x20FFFFFF);
        Random random = new Random(12345);
        int density = textureLevel / 3;
        int pointCount = (((width * height) / 1000) * density) / 10;
        for (int i = 0; i < pointCount; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            float radius = random.nextFloat() * 1.5f + 0.5f;
            canvas.drawCircle(x, y, radius, paint);
        }
        if (textureLevel > 50) {
            paint.setColor(0x10FFFFFF);
            for (int i = 0; i < pointCount / 2; i++) {
                float x = random.nextFloat() * width;
                float y = random.nextFloat() * height;
                float radius = random.nextFloat() * 2 + 1;
                canvas.drawCircle(x, y, radius, paint);
            }
        }
    }
}
