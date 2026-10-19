package com.zach.motionfit.ex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;

import java.util.Random;

/** 快速扫视：只在视野中跳动的目标里快速定位并点中。 */
public class SaccadeView extends BaseExerciseView {

    private static final int TOTAL_TARGETS = 14;

    private final Random random = new Random();
    private float tx, ty;
    private long bornAt;
    private int spawned = 0;
    private int hits = 0;
    private boolean targetAlive = false;
    private int radiusPx;

    public SaccadeView(Context c) {
        super(c);
        totalSec = 60;
    }

    private long lifeMs() {
        return 1900L - (level - 1) * 250L;
    }

    @Override
    protected void onBegin() {
        spawned = 0;
        hits = 0;
        radiusPx = Math.round(dp(44));
        targetAlive = false;
    }

    private void spawnTarget() {
        if (spawned >= TOTAL_TARGETS) {
            targetAlive = false;
            finishEarly();
            return;
        }
        spawned++;
        int w = getWidth() > 0 ? getWidth() : 1080;
        int h = getHeight() > 0 ? getHeight() : 1920;
        int margin = radiusPx + Math.round(dp(24));
        tx = margin + random.nextFloat() * Math.max(1, w - margin * 2);
        ty = margin + random.nextFloat() * Math.max(1, h - margin * 2 - Math.round(dp(80)));
        bornAt = SystemClock.elapsedRealtime();
        targetAlive = true;
    }

    @Override
    protected void onFrame(Canvas c, float elapsedSec) {
        if (spawned == 0 && getWidth() > 0) {
            spawnTarget();
            return;
        }
        long now = SystemClock.elapsedRealtime();
        if (targetAlive && now - bornAt > lifeMs()) {
            targetAlive = false;
            spawnTarget();
            return;
        }

        int w = getWidth();
        int h = getHeight();
        Paint p = paint;

        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFF0A1B20);
        c.drawRect(0, 0, w, h, p);

        // 进度点
        for (int i = 0; i < TOTAL_TARGETS; i++) {
            float dotR = dp(3f);
            float gap = (w - dp(40)) / (float) (TOTAL_TARGETS - 1);
            float x = dp(20) + gap * i;
            p.setColor(i < spawned - 1 ? 0xFF2EC4B6 : 0xFF22474B);
            c.drawCircle(x, dp(26), dotR, p);
        }

        if (targetAlive) {
            float life = 1f - (now - bornAt) / (float) lifeMs();
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.argb(60, 0x2E, 0xC4, 0xB6));
            c.drawCircle(tx, ty, radiusPx * 1.5f, p);
            p.setColor(0xFF2EC4B6);
            c.drawCircle(tx, ty, radiusPx * (0.55f + 0.45f * life), p);
            p.setColor(0xFF071417);
            c.drawCircle(tx, ty, radiusPx * 0.22f, p);
        }

        drawHint(c, "点中出现的圆点 · " + hits + "/" + TOTAL_TARGETS, h - dp(30));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) return true;
        if (!targetAlive) return true;
        float d = (float) Math.hypot(event.getX() - tx, event.getY() - ty);
        if (d <= radiusPx * 1.8f) {
            hits++;
            targetAlive = false;
            performClick();
            spawnTarget();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected int computeScore() {
        return Math.round(100f * hits / (float) TOTAL_TARGETS);
    }

    @Override
    protected String resultDetail() {
        return "点中 " + hits + " / " + TOTAL_TARGETS + " 个目标";
    }
}
