package com.zach.motionfit.ex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

/** 视动适应：匀速漂移的条纹 + 换向响应。 */
public class OptokineticView extends BaseExerciseView {

    private float offset = 0f;
    private long lastFrameAt = 0L;
    private int direction = 1;

    private long lastFlipAt = -100000L;
    private boolean answered = true;
    private int flips = 0;
    private int correct = 0;

    public OptokineticView(Context c) {
        super(c);
        totalSec = 90;
    }

    private long flipIntervalMs() {
        return 5200L - (level - 1) * 700L;
    }

    private float speedPx() {
        return dp(140 + (level - 1) * 45);
    }

    @Override
    protected void onBegin() {
        lastFrameAt = 0L;
        lastFlipAt = SystemClock.elapsedRealtime();
        answered = true;
        flips = 0;
        correct = 0;
        direction = 1;
        offset = 0f;
    }

    @Override
    protected void onFrame(Canvas c, float elapsedSec) {
        long now = SystemClock.elapsedRealtime();
        if (lastFrameAt == 0L) lastFrameAt = now;
        float dt = Math.min(0.05f, (now - lastFrameAt) / 1000f);
        lastFrameAt = now;

        offset += direction * speedPx() * dt;
        if (now - lastFlipAt >= flipIntervalMs()) {
            if (!answered) {
                // 上一轮未响应，计为漏判
            }
            direction = -direction;
            lastFlipAt = now;
            answered = false;
            flips++;
        }

        int w = getWidth();
        int h = getHeight();

        float stripe = dp(64);
        float period = stripe * 2f;
        float start = ((offset % period) + period) % period - period;

        Paint p = paint;
        p.setStyle(Paint.Style.FILL);
        for (float x = start; x < w + period; x += period) {
            p.setColor(Color.parseColor("#103237"));
            c.drawRect(x, 0, x + stripe, h, p);
            p.setColor(Color.parseColor("#0A1D21"));
            c.drawRect(x + stripe, 0, x + period, h, p);
        }

        // 上下渐隐，让视野中心更突出
        p.setShader(new LinearGradient(0, 0, 0, h * 0.35f, new int[]{0xFF071417, 0x00071417},
                null, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h * 0.35f, p);
        p.setShader(new LinearGradient(0, h, 0, h * 0.68f, new int[]{0xFF071417, 0x00071417},
                null, Shader.TileMode.CLAMP));
        c.drawRect(0, h * 0.68f, w, h, p);
        p.setShader(null);

        // 中央注视十字
        float cx = w / 2f;
        float cy = h / 2f;
        p.setColor(0xFF2EC4B6);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2f));
        c.drawCircle(cx, cy, dp(18), p);
        c.drawLine(cx - dp(30), cy, cx - dp(8), cy, p);
        c.drawLine(cx + dp(8), cy, cx + dp(30), cy, p);
        c.drawLine(cx, cy - dp(30), cx, cy - dp(8), p);
        c.drawLine(cx, cy + dp(8), cx, cy + dp(30), p);

        // 换向后的响应窗口提示
        long sinceFlip = now - lastFlipAt;
        if (!answered && sinceFlip <= 1600) {
            float a = 1f - sinceFlip / 1600f;
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(3));
            p.setColor(Color.argb((int) (200 * a), 0x2E, 0xC4, 0xB6));
            c.drawCircle(cx, cy, dp(52) + dp(16) * (1 - a), p);
        }

        drawHint(c, "盯住十字 · 条纹换向时点一下屏幕", h - dp(34));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) return true;
        long now = SystemClock.elapsedRealtime();
        if (!answered && now - lastFlipAt < 1600) {
            correct++;
            answered = true;
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected int computeScore() {
        if (flips == 0) return 60;
        return Math.round(40f + 60f * correct / (float) flips);
    }

    @Override
    protected String resultDetail() {
        return "条纹换向 " + flips + " 次，及时响应 " + correct + " 次";
    }
}
