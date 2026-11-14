package com.jiguro.bettervia;

import android.animation.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

public final class AboutPage {

    private AboutPage() {}

    public static void show(final Hook host, final Context ctx) {
        final Activity act = host.getActivityFrom(ctx);
        if (act == null) return;
        SettingsUI.showPage(
                act,
                "about_title",
                new SettingsUI.PageContentBuilder() {
                    @Override
                    public void build(ViewGroup content, final Activity act) {
                        final SettingsList list = new SettingsList(act);

                        final boolean isDark = Hook.isDarkTheme(ctx);
                        final int accent =
                                isDark
                                        ? ThemeColors.ABOUT_ACCENT_DARK
                                        : ThemeColors.ABOUT_ACCENT_LIGHT;
                        final int titleBase =
                                isDark
                                        ? ThemeColors.ABOUT_TITLE_BASE_DARK
                                        : ThemeColors.ABOUT_TITLE_BASE_LIGHT;
                        final int sloganColor =
                                isDark
                                        ? ThemeColors.ABOUT_SLOGAN_DARK
                                        : ThemeColors.ABOUT_SLOGAN_LIGHT;
                        final int badgeColor =
                                isDark
                                        ? ThemeColors.ABOUT_BADGE_DARK
                                        : ThemeColors.ABOUT_BADGE_LIGHT;

                        final CardThemeViews cardViews = new CardThemeViews();
                        final FrameLayout card =
                                buildAuroraCard(
                                        host,
                                        ctx,
                                        act,
                                        list,
                                        isDark,
                                        titleBase,
                                        accent,
                                        sloganColor,
                                        badgeColor,
                                        cardViews);
                        if (card != null) {
                            list.addCustom(card, 16, 18, 16, 22);
                        }
                        final int cardIndex = list.getItemCount() - 1;
                        list.setCustomCardThemeReapply(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        rebuildAuroraCard(
                                                host, ctx, act, list, cardViews, cardIndex);
                                    }
                                });

                        list.addItemKeepRipple(
                                "about_check_update",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.checkUpdate(ctx, false);
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_github_repo",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://github.com/JiGuroLGC/BetterVia");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_xposed_repo",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act,
                                                "https://modules.lsposed.org/module/com.jiguro.bettervia");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_author_blog",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(act, "https://www.196104.xyz");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_email",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(act, "mailto:JiGuroLiu@qq.com");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_telegram",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(act, "https://t.me/+GOYO4wK2NiNkNmE1");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_download",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/download");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_changelog",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/changelog");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_faq",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/faq");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_user_agreement",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/agreement");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_credits",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/credits");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_thanks_title",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.openUrlAndClose(
                                                act, "https://bettervia.196104.xyz/#/credits");
                                    }
                                });
                        list.addItemKeepRipple(
                                "about_withdraw",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        host.showWithdrawAgreementDialog(ctx);
                                    }
                                });

                        content.addView(
                                list,
                                new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    private static FrameLayout buildAuroraCard(
            final Hook host,
            final Context ctx,
            final Activity act,
            final SettingsList list,
            final boolean isDark,
            final int titleBase,
            final int accent,
            final int sloganColor,
            final int badgeColor,
            final CardThemeViews cardViews) {
        final AuroraBackground cardBg = createCardBackground(isDark, act);
        if (cardBg == null) return null;
        cardViews.cardBg = cardBg;
        cardViews.isDark = isDark;
        cardViews.titleBase = titleBase;
        try {
            cardBg.setCornerRadius(Hook.dp(act, 24));
            final FrameLayout card =
                    new FrameLayout(act) {
                        @Override
                        public void dispatchDraw(Canvas canvas) {
                            int save = canvas.save();
                            Path clip = new Path();
                            float r = Hook.dp(act, 24);
                            clip.addRoundRect(
                                    0, 0, getWidth(), getHeight(), r, r, Path.Direction.CW);
                            canvas.clipPath(clip);
                            super.dispatchDraw(canvas);
                            canvas.restoreToCount(save);
                        }
                    };
            card.setBackground(cardBg);
            card.setClipChildren(true);

            LinearLayout cardContent = new LinearLayout(act);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(
                    Hook.dp(act, 20), Hook.dp(act, 20), Hook.dp(act, 20), Hook.dp(act, 20));

            LinearLayout titleRow = new LinearLayout(act);
            titleRow.setOrientation(LinearLayout.HORIZONTAL);
            titleRow.setGravity(Gravity.CENTER_VERTICAL);

            final TextView logo = new TextView(act);
            logo.setText("BetterVia");
            logo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
            logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            logo.getPaint().setFakeBoldText(true);
            logo.setTextColor(titleBase);
            logo.setIncludeFontPadding(false);

            final Shader titleShader = createTitleShaderSafe(cardBg);
            if (titleShader == null) {
                int[] initTc = titleColorsFromAurora(cardBg.getAuroraColors(), isDark);
                final float logoW = logo.getPaint().measureText(logo.getText().toString());
                logo.getPaint()
                        .setShader(
                                new LinearGradient(
                                        0,
                                        0,
                                        logoW,
                                        0,
                                        new int[] {initTc[0], initTc[1], initTc[0]},
                                        new float[] {0f, 0.5f, 1f},
                                        Shader.TileMode.CLAMP));
            } else {
                logo.getPaint().setShader(titleShader);
            }
            final float[] titleOff = {0f, 0f};
            final int[] cardSize = {1, 1};

            final ValueAnimator titleAnim = ValueAnimator.ofFloat(0f, 1f);
            titleAnim.setDuration(1000);
            titleAnim.setRepeatCount(ValueAnimator.INFINITE);
            titleAnim.setRepeatMode(ValueAnimator.RESTART);
            titleAnim.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator a) {
                            try {
                                if (titleShader != null) {
                                    if (cardSize[0] <= 1
                                            || cardSize[0] != card.getWidth()
                                            || cardSize[1] != card.getHeight()) {
                                        int[] cl = new int[2];
                                        int[] ll = new int[2];
                                        card.getLocationOnScreen(cl);
                                        logo.getLocationOnScreen(ll);
                                        titleOff[0] = ll[0] - cl[0];
                                        titleOff[1] = ll[1] - cl[1];
                                        cardSize[0] = card.getWidth();
                                        cardSize[1] = card.getHeight();
                                    }
                                    cardBg.syncTitleShader(
                                            titleShader,
                                            titleOff[0],
                                            titleOff[1],
                                            cardSize[0],
                                            cardSize[1]);
                                    logo.invalidate();
                                } else {
                                    int[] tc =
                                            titleColorsFromAurora(cardBg.getAuroraColors(), isDark);
                                    float w = logo.getWidth();
                                    if (w <= 0) return;
                                    float span = w * 0.6f;
                                    float shift = ((Float) a.getAnimatedValue()) * span * 2f;
                                    LinearGradient lg =
                                            new LinearGradient(
                                                    -span + shift,
                                                    0,
                                                    shift,
                                                    0,
                                                    new int[] {tc[0], tc[1]},
                                                    null,
                                                    Shader.TileMode.MIRROR);
                                    logo.getPaint().setShader(lg);
                                    logo.invalidate();
                                }
                            } catch (Throwable e) {
                                titleAnim.cancel();
                            }
                        }
                    });
            titleRow.addView(logo);
            cardContent.addView(titleRow);

            LinearLayout sloganRow = new LinearLayout(act);
            sloganRow.setOrientation(LinearLayout.HORIZONTAL);
            sloganRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams sloganRowLp =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            sloganRowLp.topMargin = Hook.dp(act, 10);
            cardContent.addView(sloganRow, sloganRowLp);

            String sloganRaw = LocalizedStringProvider.getInstance().get(ctx, "about_subtitle");
            SpannableString sloganText = new SpannableString(sloganRaw);
            int viaStart = sloganText.toString().indexOf("Via");
            if (viaStart >= 0) {
                int viaEnd = viaStart + "Via".length();
                sloganText.setSpan(
                        new ForegroundColorSpan(accent),
                        viaStart,
                        viaEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            TextView slogan = new TextView(act);
            slogan.setText(sloganText);
            slogan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            slogan.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            slogan.setTextColor(sloganColor);
            sloganRow.addView(slogan);

            TextView versionText = new TextView(act);
            versionText.setText(
                    "V" + Hook.MODULE_VERSION_NAME + " (" + Hook.MODULE_VERSION_CODE + ")");
            versionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            versionText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            versionText.setTextColor(badgeColor);
            versionText.setIncludeFontPadding(false);
            LinearLayout.LayoutParams versionLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            versionLp.topMargin = Hook.dp(act, 16);
            cardContent.addView(versionText, versionLp);

            card.addView(
                    cardContent,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));

            final LinearLayout authorBlock = new LinearLayout(act);
            authorBlock.setOrientation(LinearLayout.VERTICAL);
            authorBlock.setGravity(Gravity.END);
            authorBlock.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            TextView designedBy = new TextView(act);
            designedBy.setText("Designed by");
            designedBy.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            designedBy.setLetterSpacing(0.12f);
            designedBy.setTextColor(
                    isDark
                            ? ThemeColors.ABOUT_DESIGNED_BY_DARK
                            : ThemeColors.ABOUT_DESIGNED_BY_LIGHT);
            designedBy.setIncludeFontPadding(false);
            designedBy.setGravity(Gravity.END);
            LinearLayout.LayoutParams designedLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            authorBlock.addView(designedBy, designedLp);

            final TextView authorName = new TextView(act);
            authorName.setText("JiGuro");
            authorName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            authorName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            authorName.getPaint().setFakeBoldText(true);
            authorName.setIncludeFontPadding(false);
            final int screenWpx = act.getResources().getDisplayMetrics().widthPixels;
            final int cardInnerW = screenWpx - Hook.dp(act, 72);
            final float versionW =
                    versionText.getPaint().measureText(versionText.getText().toString());
            final float sideGap = Hook.dp(act, 16);
            final float maxAuthorW = Math.max(Hook.dp(act, 80), cardInnerW - versionW - sideGap);
            final float baseW = authorName.getPaint().measureText("JiGuro");
            float fitSp = 60f;
            if (baseW > 0f && baseW > maxAuthorW * 0.95f) {
                fitSp = 60f * (maxAuthorW * 0.95f) / baseW;
            }
            fitSp = Math.max(24f, Math.min(60f, fitSp));
            authorName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fitSp);
            final int glassTop =
                    isDark ? ThemeColors.ABOUT_GLASS_TOP_DARK : ThemeColors.ABOUT_GLASS_TOP_LIGHT;
            final int glassShine =
                    isDark
                            ? ThemeColors.ABOUT_GLASS_SHINE_DARK
                            : ThemeColors.ABOUT_GLASS_SHINE_LIGHT;
            final int glassBody =
                    isDark ? ThemeColors.ABOUT_GLASS_BODY_DARK : ThemeColors.ABOUT_GLASS_BODY_LIGHT;
            final int glassBottom =
                    isDark
                            ? ThemeColors.ABOUT_GLASS_BOTTOM_DARK
                            : ThemeColors.ABOUT_GLASS_BOTTOM_LIGHT;
            final int glow = isDark ? ThemeColors.ABOUT_GLOW_DARK : ThemeColors.ABOUT_GLOW_LIGHT;
            authorName.setShadowLayer(Hook.dp(act, isDark ? 3 : 6), 0, Hook.dp(act, 3), glow);
            authorName.addOnLayoutChangeListener(
                    new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(
                                View v,
                                int l,
                                int t,
                                int r,
                                int b,
                                int ol,
                                int ot,
                                int or,
                                int ob) {
                            int h = b - t;
                            if (h <= 0) return;
                            authorName
                                    .getPaint()
                                    .setShader(
                                            new LinearGradient(
                                                    0,
                                                    0,
                                                    0,
                                                    h,
                                                    new int[] {
                                                        glassTop, glassShine, glassBody, glassBottom
                                                    },
                                                    new float[] {0f, 0.15f, 0.5f, 1f},
                                                    Shader.TileMode.CLAMP));
                            authorName.invalidate();
                        }
                    });
            LinearLayout.LayoutParams nameLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            nameLp.topMargin = -Hook.dp(act, 2);
            final android.graphics.Paint.FontMetrics fm = authorName.getPaint().getFontMetrics();
            final float CUT_FRAC = 0.15f;
            final android.graphics.Rect ink = new android.graphics.Rect();
            authorName.getPaint().getTextBounds("JiGuro", 0, "JiGuro".length(), ink);
            final float ascent = fm.ascent;
            final float descent = fm.descent;
            final float nameH = descent - ascent;
            final float inkTopL = -ascent + ink.top;
            final float inkH = ink.height();
            final float clipL = inkTopL + inkH * (1f - CUT_FRAC);
            int cutOverflow = (int) Math.ceil(nameH - clipL);
            if (cutOverflow < 0) cutOverflow = 0;
            nameLp.bottomMargin = -cutOverflow;
            authorBlock.addView(authorName, nameLp);

            FrameLayout.LayoutParams authorLp =
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
            authorLp.gravity = Gravity.BOTTOM | Gravity.END;
            authorLp.rightMargin = Hook.dp(act, 20);
            card.addView(authorBlock, authorLp);

            cardViews.card = card;

            card.addOnAttachStateChangeListener(
                    new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {
                            try {
                                cardBg.start();
                                titleAnim.start();
                            } catch (Throwable e) {
                            }
                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            cardBg.stop();
                            titleAnim.cancel();
                        }
                    });

            final int[] tapCount = {0};
            final float[] downX = {0f};
            final float[] downY = {0f};
            final long[] downTime = {0L};
            card.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case MotionEvent.ACTION_DOWN:
                                    downX[0] = event.getX();
                                    downY[0] = event.getY();
                                    downTime[0] = System.currentTimeMillis();
                                    v.animate()
                                            .scaleX(0.965f)
                                            .scaleY(0.965f)
                                            .setDuration(110)
                                            .start();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    v.animate()
                                            .scaleX(1f)
                                            .scaleY(1f)
                                            .setDuration(280)
                                            .setInterpolator(new OvershootInterpolator(1.5f))
                                            .start();
                                    float dx = event.getX() - downX[0];
                                    float dy = event.getY() - downY[0];
                                    boolean isTap =
                                            Math.abs(dx) < Hook.dp(act, 12)
                                                    && Math.abs(dy) < Hook.dp(act, 12)
                                                    && (System.currentTimeMillis() - downTime[0])
                                                            < 500;
                                    if (isTap) {
                                        tapCount[0]++;
                                        if (tapCount[0] >= 7) {
                                            tapCount[0] = 0;
                                            host.jiguroMessageWithContext(
                                                    act,
                                                    LocalizedStringProvider.getInstance()
                                                            .get(act, "about_easter_egg"));
                                        }
                                    } else {
                                        tapCount[0] = 0;
                                    }
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    v.animate()
                                            .scaleX(1f)
                                            .scaleY(1f)
                                            .setDuration(280)
                                            .setInterpolator(new OvershootInterpolator(1.5f))
                                            .start();
                                    tapCount[0] = 0;
                                    break;
                            }
                            return true;
                        }
                    });
            return card;
        } catch (Throwable e) {
            return null;
        }
    }

    private static AuroraBackground createCardBackground(boolean isDark, Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                return new AuroraShaderDrawable(isDark, act);
            } catch (Throwable e) {
            }
        }
        try {
            return new AuroraDrawable(isDark, act);
        } catch (Throwable e) {
            return null;
        }
    }

    private static Shader createTitleShaderSafe(AuroraBackground bg) {
        try {
            return bg.createTitleShader();
        } catch (Throwable e) {
            return null;
        }
    }

    private static class AuroraDrawable extends AuroraBackground {
        private static final ThemeData LIGHT = AboutPage.LIGHT;
        private static final ThemeData DARK = AboutPage.DARK;
        private static final float NANOS_PER_SEC = 1000000000f;

        private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint blobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final boolean dark;
        private final AuroraMotion motion;
        private float cornerRadius = 0f;
        private final Choreographer choreo = Choreographer.getInstance();
        private final Choreographer.FrameCallback frameCb =
                new Choreographer.FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        if (!running) return;
                        try {
                            if (lastFrame != 0) {
                                float dt = (frameTimeNanos - lastFrame) / NANOS_PER_SEC;
                                motion.update(dt);
                                invalidateSelf();
                            }
                            lastFrame = frameTimeNanos;
                            choreo.postFrameCallback(frameCb);
                        } catch (Throwable t) {
                            stop();
                        }
                    }
                };
        private long lastFrame = 0;
        private boolean running = false;

        AuroraDrawable(boolean dark, Activity act) {
            this.dark = dark;
            ThemeData data = dark ? DARK : LIGHT;
            motion =
                    new AuroraMotion(
                            data.g1,
                            data.g2,
                            data.g3,
                            data.colorInterpPeriod,
                            data.gradientSpeedChange,
                            data.gradientSpeedRest);
            motion.setVibrateActivity(act);
            basePaint.setColor(
                    dark ? ThemeColors.ABOUT_CARD_BG_DARK : ThemeColors.ABOUT_CARD_BG_LIGHT);
        }

        public void start() {
            if (running) return;
            running = true;
            lastFrame = 0;
            choreo.postFrameCallback(frameCb);
        }

        public void stop() {
            if (!running) return;
            running = false;
            choreo.removeFrameCallback(frameCb);
            motion.stop();
        }

        public void setCornerRadius(float r) {
            cornerRadius = r;
            invalidateSelf();
        }

        @Override
        public int[] getAuroraColors() {
            float[] c = motion.getColors();
            int[] out = new int[4];
            for (int i = 0; i < 4; i++) {
                out[i] =
                        0xFF000000
                                | (clampByte(c[i * 4]) << 16)
                                | (clampByte(c[i * 4 + 1]) << 8)
                                | clampByte(c[i * 4 + 2]);
            }
            return out;
        }

        private static int clampByte(float v) {
            int i = (int) (Math.max(0f, Math.min(1f, v)) * 255f + 0.5f);
            return i < 0 ? 0 : (i > 255 ? 255 : i);
        }

        @Override
        public Shader createTitleShader() {
            return null;
        }

        @Override
        public void syncTitleShader(Shader s, float offX, float offY, float resW, float resH) {}

        @Override
        public void draw(android.graphics.Canvas canvas) {
            Rect b = getBounds();
            if (b.isEmpty()) return;
            boolean rounded = cornerRadius > 0f;
            if (rounded) {
                canvas.save();
                Path path = new Path();
                path.addRoundRect(
                        b.left,
                        b.top,
                        b.right,
                        b.bottom,
                        cornerRadius,
                        cornerRadius,
                        Path.Direction.CW);
                canvas.clipPath(path);
            }
            canvas.drawRect(b, basePaint);
            try {
                ThemeData data = dark ? DARK : LIGHT;
                float[] colors = motion.getColors();
                float[] points = data.points;
                float pointOffset = data.pointOffset;
                float w = b.width();
                float h = b.height();
                float maxDim = Math.max(w, h);
                float t = motion.getAnimTime();
                for (int i = 0; i < 4; i++) {
                    float px = points[i * 3];
                    float py = points[i * 3 + 1];
                    float pr = points[i * 3 + 2];
                    float ppx = px + (float) Math.sin(t + py) * pointOffset;
                    float ppy = py + (float) Math.cos(t + ppx) * pointOffset;
                    float cx = ppx * w;
                    float cy = ppy * h;
                    float radius = pr * maxDim;
                    int cr = clampByte(colors[i * 4]);
                    int cg = clampByte(colors[i * 4 + 1]);
                    int cb = clampByte(colors[i * 4 + 2]);
                    int ca = clampByte(colors[i * 4 + 3]);
                    int center = (ca << 24) | (cr << 16) | (cg << 8) | cb;
                    int mid = (center & 0x00FFFFFF) | ((ca / 2) << 24);
                    int edge = center & 0x00FFFFFF;
                    RadialGradient rg =
                            new RadialGradient(
                                    cx,
                                    cy,
                                    radius,
                                    new int[] {center, mid, edge},
                                    new float[] {0f, 0.5f, 1f},
                                    Shader.TileMode.CLAMP);
                    blobPaint.setShader(rg);
                    canvas.drawRect(b, blobPaint);
                }
            } catch (Throwable t) {
            }
            if (rounded) {
                canvas.restore();
            }
        }

        @Override
        public void setAlpha(int alpha) {
            basePaint.setAlpha(alpha);
            blobPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter cf) {
            basePaint.setColorFilter(cf);
            blobPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private abstract static class AuroraBackground extends Drawable {
        public abstract void setCornerRadius(float r);

        public abstract void start();

        public abstract void stop();

        public abstract int[] getAuroraColors();

        public abstract Shader createTitleShader();

        public abstract void syncTitleShader(
                Shader s, float offX, float offY, float resW, float resH);
    }

    static final class ThemeData {
        final float[] points;
        final float[] g1, g2, g3;
        final float pointOffset;
        final float saturateOffset;
        final float lightOffset;
        final float colorInterpPeriod;
        final float gradientSpeedChange;
        final float gradientSpeedRest;

        ThemeData(
                float[] points,
                float[] g1,
                float[] g2,
                float[] g3,
                float pointOffset,
                float saturateOffset,
                float lightOffset,
                float colorInterpPeriod,
                float gradientSpeedChange,
                float gradientSpeedRest) {
            this.points = points;
            this.g1 = g1;
            this.g2 = g2;
            this.g3 = g3;
            this.pointOffset = pointOffset;
            this.saturateOffset = saturateOffset;
            this.lightOffset = lightOffset;
            this.colorInterpPeriod = colorInterpPeriod;
            this.gradientSpeedChange = gradientSpeedChange;
            this.gradientSpeedRest = gradientSpeedRest;
        }
    }

    static final ThemeData LIGHT =
            new ThemeData(
                    new float[] {
                        0.8f, 0.2f, 1.0f, 0.8f, 0.9f, 1.0f, 0.2f, 0.9f, 1.0f, 0.2f, 0.2f, 1.0f
                    },
                    new float[] {
                        1.00f, 0.90f, 0.94f, 1.0f, 1.00f, 0.84f, 0.89f, 1.0f, 0.97f, 0.73f, 0.82f,
                        1.0f, 0.64f, 0.65f, 0.98f, 1.0f
                    },
                    new float[] {
                        0.58f, 0.74f, 1.00f, 1.0f, 1.00f, 0.90f, 0.93f, 1.0f, 0.74f, 0.76f, 1.00f,
                        1.0f, 0.97f, 0.77f, 0.84f, 1.0f
                    },
                    new float[] {
                        0.98f, 0.86f, 0.90f, 1.0f, 0.60f, 0.73f, 0.98f, 1.0f, 0.92f, 0.93f, 1.00f,
                        1.0f, 0.56f, 0.69f, 1.00f, 1.0f
                    },
                    0.2f,
                    0.2f,
                    0.1f,
                    11.0f,
                    1.6f,
                    1.05f);

    static final ThemeData DARK =
            new ThemeData(
                    new float[] {
                        0.8f, 0.2f, 1.0f, 0.8f, 0.9f, 1.0f, 0.2f, 0.9f, 1.0f, 0.2f, 0.2f, 1.0f
                    },
                    new float[] {
                        0.20f, 0.06f, 0.88f, 0.4f, 0.30f, 0.14f, 0.55f, 0.5f, 0.00f, 0.64f, 0.96f,
                        0.5f, 0.11f, 0.16f, 0.83f, 0.4f
                    },
                    new float[] {
                        0.07f, 0.15f, 0.79f, 0.5f, 0.62f, 0.21f, 0.67f, 0.5f, 0.06f, 0.25f, 0.84f,
                        0.5f, 0.00f, 0.20f, 0.78f, 0.5f
                    },
                    new float[] {
                        0.58f, 0.30f, 0.74f, 0.4f, 0.27f, 0.18f, 0.60f, 0.5f, 0.66f, 0.26f, 0.62f,
                        0.5f, 0.12f, 0.16f, 0.70f, 0.6f
                    },
                    0.4f,
                    0.17f,
                    0.0f,
                    11.0f,
                    1.0f,
                    1.0f);

    private static final class AuroraMotion {
        private static final float SPRING1_STIFFNESS = 200f;
        private static final float SPRING1_DAMPING = 16f;
        private static final float SPRING2_STIFFNESS = 200f;
        private static final float SPRING2_DAMPING = 9f;
        private static final long SPEED_REST_DELAY_MS = 300L;
        private static final float PING_PONG_MAX = 120f;

        private final float[] g1, g2, g3;
        private final float colorInterpPeriod;
        private final float gradientSpeedChange;
        private final float gradientSpeedRest;
        private final float[] startColor = new float[16];
        private final float[] endColor = new float[16];
        private final float[] out = new float[16];

        private float animTime = 0f;
        private float gradientSpeed = 1f;
        private float speedTarget = 1f;
        private float speedVel = 0f;
        private float speedStiff = SPRING1_STIFFNESS;
        private float speedDamp = SPRING1_DAMPING;
        private float colorInterpT = 0f;
        private float colorInterpVel = 0f;
        private float prevT = 0f;
        private float cycleCount = 0f;
        private float mTime = 0f;
        private float mTimeDirection = 1f;
        private Handler restHandler;
        private final Runnable restRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        speedStiff = SPRING2_STIFFNESS;
                        speedDamp = SPRING2_DAMPING;
                        speedTarget = gradientSpeedRest;
                    }
                };

        private Activity vibrateAct;
        private long lastVibrateMs = 0L;
        private static final long VIBRATE_DEBOUNCE_MS = 600L;
        private static final long VIBRATE_DURATION_MS = 12L;

        AuroraMotion(
                float[] g1,
                float[] g2,
                float[] g3,
                float colorInterpPeriod,
                float gradientSpeedChange,
                float gradientSpeedRest) {
            this.g1 = g1;
            this.g2 = g2;
            this.g3 = g3;
            this.colorInterpPeriod = colorInterpPeriod;
            this.gradientSpeedChange = gradientSpeedChange;
            this.gradientSpeedRest = gradientSpeedRest;
            System.arraycopy(g2, 0, startColor, 0, 16);
            System.arraycopy(g2, 0, endColor, 0, 16);
        }

        void setVibrateActivity(Activity act) {
            this.vibrateAct = act;
        }

        private void vibrateOnColorSwitch() {
            if (vibrateAct == null) return;
            long now = System.currentTimeMillis();
            if (now - lastVibrateMs < VIBRATE_DEBOUNCE_MS) return;
            lastVibrateMs = now;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Object vmObj = vibrateAct.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                    if (vmObj instanceof VibratorManager) {
                        Vibrator v = ((VibratorManager) vmObj).getDefaultVibrator();
                        v.vibrate(
                                VibrationEffect.createOneShot(
                                        VIBRATE_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                } else {
                    Object vObj = vibrateAct.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vObj instanceof Vibrator) {
                        ((Vibrator) vObj).vibrate(VIBRATE_DURATION_MS);
                    }
                }
            } catch (Throwable t) {
            }
        }

        void update(float dt) {
            mTime += dt * mTimeDirection;
            if (mTimeDirection > 0f && mTime >= PING_PONG_MAX) {
                mTimeDirection = -1f;
            } else if (mTimeDirection < 0f && mTime <= 0f) {
                mTimeDirection = 1f;
            }
            float signedDt = dt * mTimeDirection;
            float absDt = dt;

            speedVel += (speedTarget - gradientSpeed) * speedStiff * absDt;
            speedVel *= (float) Math.exp(-speedDamp * absDt);
            gradientSpeed += speedVel * absDt;

            animTime += signedDt * gradientSpeed;

            float x = animTime / colorInterpPeriod;
            float frac = x - (float) Math.floor(x);
            float v0 = (float) Math.floor(frac * 2f);
            if (Math.abs(prevT - v0) > 0.5f) {
                int pair = ((int) cycleCount) % 4;
                switch (pair) {
                    case 0:
                        System.arraycopy(g2, 0, startColor, 0, 16);
                        System.arraycopy(g1, 0, endColor, 0, 16);
                        break;
                    case 1:
                        System.arraycopy(g1, 0, startColor, 0, 16);
                        System.arraycopy(g2, 0, endColor, 0, 16);
                        break;
                    case 2:
                        System.arraycopy(g2, 0, startColor, 0, 16);
                        System.arraycopy(g3, 0, endColor, 0, 16);
                        break;
                    default:
                        System.arraycopy(g3, 0, startColor, 0, 16);
                        System.arraycopy(g2, 0, endColor, 0, 16);
                        break;
                }
                colorInterpT = 0f;
                colorInterpVel = 0f;
                speedStiff = SPRING1_STIFFNESS;
                speedDamp = SPRING1_DAMPING;
                speedTarget = gradientSpeedChange;
                if (restHandler == null) {
                    restHandler = new Handler(Looper.getMainLooper());
                }
                restHandler.removeCallbacks(restRunnable);
                restHandler.postDelayed(restRunnable, SPEED_REST_DELAY_MS);
                cycleCount += 1f;
                vibrateOnColorSwitch();
            }
            prevT = v0;

            colorInterpVel += (1f - colorInterpT) * SPRING1_STIFFNESS * absDt;
            colorInterpVel *= (float) Math.exp(-SPRING1_DAMPING * absDt);
            colorInterpT += colorInterpVel * absDt;

            for (int i = 0; i < 16; i++) {
                out[i] = startColor[i] + (endColor[i] - startColor[i]) * colorInterpT;
            }
        }

        float getAnimTime() {
            return animTime;
        }

        float[] getColors() {
            return out;
        }

        void stop() {
            if (restHandler != null) {
                restHandler.removeCallbacks(restRunnable);
            }
        }
    }

    @TargetApi(33)
    private static class AuroraShaderDrawable extends AuroraBackground {

        private static final String AGSL =
                "uniform vec2 uResolution;\n"
                    + "uniform float uAnimTime;\n"
                    + "uniform vec2 uTitleOffset;\n"
                    + "uniform float uTitleBoost;\n"
                    + "uniform float uTitleSat;\n"
                    + "uniform vec4 uBound;\n"
                    + "uniform float uTranslateY;\n"
                    + "uniform vec3 uPoints[4];\n"
                    + "uniform vec4 uColors[4];\n"
                    + "uniform float uAlphaMulti;\n"
                    + "uniform float uNoiseScale;\n"
                    + "uniform float uPointOffset;\n"
                    + "uniform float uPointRadiusMulti;\n"
                    + "uniform float uSaturateOffset;\n"
                    + "uniform float uLightOffset;\n"
                    + "uniform float uAlphaOffset;\n"
                    + "uniform float uShadowColorMulti;\n"
                    + "uniform float uShadowColorOffset;\n"
                    + "uniform float uShadowNoiseScale;\n"
                    + "uniform float uShadowOffset;\n"
                    + "\n"
                    + "float hash(vec2 p) {\n"
                    + "    vec3 p3 = fract(vec3(p.xyx) * 0.13);\n"
                    + "    p3 += dot(p3, p3.yzx + 3.333);\n"
                    + "    return fract((p3.x + p3.y) * p3.z);\n"
                    + "}\n"
                    + "\n"
                    + "float perlin(vec2 x) {\n"
                    + "    vec2 i = floor(x);\n"
                    + "    vec2 f = fract(x);\n"
                    + "    float a = hash(i);\n"
                    + "    float b = hash(i + vec2(1.0, 0.0));\n"
                    + "    float c = hash(i + vec2(0.0, 1.0));\n"
                    + "    float d = hash(i + vec2(1.0, 1.0));\n"
                    + "    vec2 u = f * f * (3.0 - 2.0 * f);\n"
                    + "    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x *"
                    + " u.y;\n"
                    + "}\n"
                    + "\n"
                    + "vec3 rgb2hsv(vec3 c) {\n"
                    + "    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n"
                    + "    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n"
                    + "    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n"
                    + "    float d = q.x - min(q.w, q.y);\n"
                    + "    float e = 1.0e-10;\n"
                    + "    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e),"
                    + " q.x);\n"
                    + "}\n"
                    + "\n"
                    + "vec3 hsv2rgb(vec3 c) {\n"
                    + "    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n"
                    + "    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n"
                    + "    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n"
                    + "}\n"
                    + "\n"
                    + "float gradientNoise(vec2 uv) {\n"
                    + "    return fract(52.9829189 * fract(dot(uv, vec2(0.06711056,"
                    + " 0.00583715))));\n"
                    + "}\n"
                    + "\n"
                    + "vec4 main(vec2 fragCoord) {\n"
                    + "    vec2 coord = fragCoord + uTitleOffset;\n"
                    + "    vec2 vUv = coord / uResolution;\n"
                    + "    vUv.y = 1.0 - vUv.y;\n"
                    + "    vec2 uv = vUv;\n"
                    + "    uv -= vec2(0., uTranslateY);\n"
                    + "    uv.xy -= uBound.xy;\n"
                    + "    uv.xy /= uBound.zw;\n"
                    + "    vec4 color = vec4(0.0);\n"
                    + "    float noiseValue = perlin(vUv * uNoiseScale + vec2(-uAnimTime,"
                    + " -uAnimTime));\n"
                    + "    for (int i = 0; i < 4; i++) {\n"
                    + "        vec4 pointColor = uColors[i];\n"
                    + "        pointColor.rgb *= pointColor.a;\n"
                    + "        vec2 point = uPoints[i].xy;\n"
                    + "        float rad = uPoints[i].z * uPointRadiusMulti;\n"
                    + "        point.x += sin(uAnimTime + point.y) * uPointOffset;\n"
                    + "        point.y += cos(uAnimTime + point.x) * uPointOffset;\n"
                    + "        float d = distance(uv, point);\n"
                    + "        float pct = 1.0 - smoothstep(0.0, rad, d);\n"
                    + "        color.rgb = mix(color.rgb, pointColor.rgb, pct);\n"
                    + "        color.a = mix(color.a, pointColor.a, pct);\n"
                    + "    }\n"
                    + "    float oppositeNoise = smoothstep(0.0, 1.0, noiseValue);\n"
                    + "    color.rgb /= max(color.a, 0.0001);\n"
                    + "    vec3 hsv = rgb2hsv(color.rgb);\n"
                    + "    hsv.y = mix(hsv.y, 0.0, oppositeNoise * uSaturateOffset);\n"
                    + "    hsv.y = min(1.0, hsv.y * uTitleSat);\n"
                    + "    color.rgb = hsv2rgb(hsv);\n"
                    + "    color.rgb += oppositeNoise * uLightOffset;\n"
                    + "    color.a = clamp(color.a, 0.0, 1.0);\n"
                    + "    color.a *= uAlphaMulti;\n"
                    + "    color.rgb += uTitleBoost;\n"
                    + "    if (uTitleBoost != 0.0) { color.a = 1.0; }\n"
                    + "    color += (10.0 / 255.0) * gradientNoise(coord) - (5.0 / 255.0);\n"
                    + "    return vec4(color.rgb * color.a, color.a);\n"
                    + "}\n";

        private static final float NOISE_SCALE = 1.5f;
        private static final float POINT_RADIUS_MULTI = 1.0f;
        private static final float ALPHA_MULTI = 1.0f;
        private static final float TRANSLATE_Y = 0f;
        private static final float ALPHA_OFFSET = 0.5f;
        private static final float SHADOW_COLOR_MULTI = 0.3f;
        private static final float SHADOW_COLOR_OFFSET = 0.3f;
        private static final float SHADOW_NOISE_SCALE = 5.0f;
        private static final float SHADOW_OFFSET = 0.01f;
        private static final float NANOS_PER_SEC = 1000000000f;
        private static final float TITLE_SAT_LIGHT = 2.5f;
        private static final float TITLE_SAT_DARK = 1.5f;

        private final RuntimeShader shader;
        private final Paint basePaint;
        private final Paint shaderPaint;
        private final ThemeData data;
        private final boolean dark;
        private final AuroraMotion motion;
        private final float[] uColors = new float[16];
        private final Choreographer choreo = Choreographer.getInstance();
        private final Choreographer.FrameCallback frameCb =
                new Choreographer.FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        if (!running) return;
                        try {
                            if (lastFrame != 0) {
                                float dt = (frameTimeNanos - lastFrame) / NANOS_PER_SEC;
                                update(dt);
                            }
                            lastFrame = frameTimeNanos;
                            choreo.postFrameCallback(frameCb);
                        } catch (Throwable t) {
                            stop();
                        }
                    }
                };
        private float cornerRadius = 0f;
        private long lastFrame = 0;
        private boolean running = false;

        AuroraShaderDrawable(boolean dark, Activity act) {
            this.dark = dark;
            data = dark ? DARK : LIGHT;
            motion =
                    new AuroraMotion(
                            data.g1,
                            data.g2,
                            data.g3,
                            data.colorInterpPeriod,
                            data.gradientSpeedChange,
                            data.gradientSpeedRest);
            motion.setVibrateActivity(act);
            shader = new RuntimeShader(AGSL);
            applyStaticUniforms(shader);
            shader.setFloatUniform("uTitleOffset", 0f, 0f);
            shader.setFloatUniform("uTitleBoost", 0f);
            shader.setFloatUniform("uResolution", 1f, 1f);
            System.arraycopy(data.g2, 0, uColors, 0, 16);
            shader.setFloatUniform("uColors", uColors);
            shader.setFloatUniform("uAnimTime", 0f);

            basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            basePaint.setColor(
                    dark ? ThemeColors.ABOUT_CARD_BG_DARK : ThemeColors.ABOUT_CARD_BG_LIGHT);
            shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            shaderPaint.setShader(shader);
        }

        private void applyStaticUniforms(RuntimeShader s) {
            s.setFloatUniform("uTranslateY", TRANSLATE_Y);
            s.setFloatUniform("uAlphaMulti", ALPHA_MULTI);
            s.setFloatUniform("uNoiseScale", NOISE_SCALE);
            s.setFloatUniform("uPointRadiusMulti", POINT_RADIUS_MULTI);
            s.setFloatUniform("uAlphaOffset", ALPHA_OFFSET);
            s.setFloatUniform("uShadowColorMulti", SHADOW_COLOR_MULTI);
            s.setFloatUniform("uShadowColorOffset", SHADOW_COLOR_OFFSET);
            s.setFloatUniform("uShadowNoiseScale", SHADOW_NOISE_SCALE);
            s.setFloatUniform("uShadowOffset", SHADOW_OFFSET);
            s.setFloatUniform("uBound", 0f, 0f, 1f, 1f);
            s.setFloatUniform("uPointOffset", data.pointOffset);
            s.setFloatUniform("uSaturateOffset", data.saturateOffset);
            s.setFloatUniform("uLightOffset", data.lightOffset);
            s.setFloatUniform("uPoints", data.points);
            s.setFloatUniform("uTitleSat", 1f);
        }

        private void update(float dt) {
            motion.update(dt);
            float[] colors = motion.getColors();
            System.arraycopy(colors, 0, uColors, 0, 16);
            shader.setFloatUniform("uAnimTime", motion.getAnimTime());
            shader.setFloatUniform("uColors", uColors);
            invalidateSelf();
        }

        @Override
        public RuntimeShader createTitleShader() {
            RuntimeShader s = new RuntimeShader(AGSL);
            applyStaticUniforms(s);
            s.setFloatUniform("uTitleOffset", 0f, 0f);
            s.setFloatUniform("uTitleBoost", dark ? 0.30f : -0.35f);
            s.setFloatUniform("uTitleSat", dark ? TITLE_SAT_DARK : TITLE_SAT_LIGHT);
            s.setFloatUniform("uResolution", 1f, 1f);
            s.setFloatUniform("uColors", uColors);
            s.setFloatUniform("uAnimTime", 0f);
            return s;
        }

        @Override
        public void syncTitleShader(Shader s, float offX, float offY, float resW, float resH) {
            RuntimeShader rs = (RuntimeShader) s;
            rs.setFloatUniform("uTitleOffset", offX, offY);
            rs.setFloatUniform("uResolution", resW, resH);
            rs.setFloatUniform("uAnimTime", motion.getAnimTime());
            rs.setFloatUniform("uColors", uColors);
        }

        public void start() {
            if (running) return;
            running = true;
            lastFrame = 0;
            choreo.postFrameCallback(frameCb);
        }

        public void stop() {
            if (!running) return;
            running = false;
            choreo.removeFrameCallback(frameCb);
            motion.stop();
        }

        public void setCornerRadius(float r) {
            cornerRadius = r;
            invalidateSelf();
        }

        @Override
        public void setBounds(int l, int t, int r, int b) {
            super.setBounds(l, t, r, b);
            shader.setFloatUniform("uResolution", (float) (r - l), (float) (b - t));
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            if (b.isEmpty()) return;
            boolean rounded = cornerRadius > 0f;
            if (rounded) {
                canvas.save();
                Path path = new Path();
                path.addRoundRect(
                        b.left,
                        b.top,
                        b.right,
                        b.bottom,
                        cornerRadius,
                        cornerRadius,
                        Path.Direction.CW);
                canvas.clipPath(path);
            }
            canvas.drawRect(b, basePaint);
            try {
                canvas.drawRect(b, shaderPaint);
            } catch (Throwable t) {
            }
            if (rounded) canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            basePaint.setAlpha(alpha);
            shaderPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            basePaint.setColorFilter(cf);
            shaderPaint.setColorFilter(cf);
        }

        @Override
        public int[] getAuroraColors() {
            int[] out = new int[4];
            for (int i = 0; i < 4; i++) {
                int r = clampByte(uColors[i * 4]);
                int g = clampByte(uColors[i * 4 + 1]);
                int b = clampByte(uColors[i * 4 + 2]);
                out[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
            return out;
        }

        private static int clampByte(float v) {
            int i = (int) (Math.max(0f, Math.min(1f, v)) * 255f + 0.5f);
            return i < 0 ? 0 : (i > 255 ? 255 : i);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static int[] titleColorsFromAurora(int[] ac, boolean dark) {
        int hueA = ac[1];
        int hueB = ac[3];
        int base = dark ? lighten(hueA, 0.15f) : darken(hueA, 0.5f);
        int bright = dark ? lighten(hueB, 0.35f) : darken(hueB, 0.3f);
        return new int[] {base, bright};
    }

    private static void rebuildAuroraCard(
            final Hook host,
            final Context ctx,
            final Activity act,
            final SettingsList list,
            final CardThemeViews cardViews,
            final int cardIndex) {
        if (cardViews == null) return;
        try {
            final FrameLayout old = cardViews.card;
            if (old != null && old.getParent() instanceof ViewGroup) {
                ((ViewGroup) old.getParent()).removeView(old);
            }
            final boolean isDark = Hook.isDarkTheme(ctx);
            final int titleBase =
                    isDark ? ThemeColors.ABOUT_TITLE_BASE_DARK : ThemeColors.ABOUT_TITLE_BASE_LIGHT;
            final int accent =
                    isDark ? ThemeColors.ABOUT_ACCENT_DARK : ThemeColors.ABOUT_ACCENT_LIGHT;
            final int sloganColor =
                    isDark ? ThemeColors.ABOUT_SLOGAN_DARK : ThemeColors.ABOUT_SLOGAN_LIGHT;
            final int badgeColor =
                    isDark ? ThemeColors.ABOUT_BADGE_DARK : ThemeColors.ABOUT_BADGE_LIGHT;
            final FrameLayout newCard =
                    buildAuroraCard(
                            host,
                            ctx,
                            act,
                            list,
                            isDark,
                            titleBase,
                            accent,
                            sloganColor,
                            badgeColor,
                            cardViews);
            if (newCard == null) return;
            list.replaceCustomView(cardIndex, newCard);
        } catch (Throwable t) {
        }
    }

    private static final class CardThemeViews {
        FrameLayout card;
        AuroraBackground cardBg;
        boolean isDark;
        int titleBase;
    }

    private static int darken(int color, float f) {
        int r = (int) (((color >> 16) & 0xFF) * f);
        int g = (int) (((color >> 8) & 0xFF) * f);
        int b = (int) ((color & 0xFF) * f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int lighten(int color, float f) {
        int r = (int) (((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * f);
        int g = (int) (((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * f);
        int b = (int) ((color & 0xFF) + (255 - (color & 0xFF)) * f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
