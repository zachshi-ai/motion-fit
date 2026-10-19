package com.zach.motionfit.ex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/** 平滑追踪：手指按住光点跟随移动。 */
public class PursuitView extends BaseExerciseView {

    private float dotX, dotY;
    private float fingerX = -1, fingerY = -1;
    private boolean tracking = false;
    private int goodFrames = 0;
    private int frames = 0;
    private float tolerance;

    public PursuitView(Context c) {
        super(c);
        totalSec = 60;
    }

    private float speedScale() {
        return 1f + (level - 1) * 0.18f;
    }

    @Override
    protected void onBegin() {
        tolerance = dp(96);
        goodFrames = 0;
        frames = 0;
        fingerX = fingerY = -1;
        tracking = false;
    }

    @Override
    protected void onFrame(Canvas c, float elapsedSec) {
        int w = getWidth();
        int h = getHeight();
        float t = elapsedSec * speedScale();

        float cx = w / 2f;
        float cy = h / 2f;
        float rx = w * 0.34f;
        float ry = h * 0.26f;

        dotX = cx + rx * (float) Math.sin(t * 0.95f) * (float) Math.cos(t * 0.21f);
        dotY = cy + ry * (float) Math.sin(t * 1.37f + 0.6f);

        if (tracking) {
            frames++;
            float d = (float) Math.hypot(fingerX - dotX, fingerY - dotY);
            if (d < tolerance) goodFrames++;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.5f));
            paint.setColor(Ui_ALPHA);
            c.drawLine(fingerX, fingerY, dotX, dotY, paint);
        }

        Paint p = paint;
        p.setStyle(Paint.Style.FILL);
        p.setColor(0x222EC4B6);
        c.drawCircle(dotX, dotY, dp(46), p);
        p.setColor(0xFF2EC4B6);
        c.drawCircle(dotX, dotY, dp(20), p);
        p.setColor(0xFF071417);
        c.drawCircle(dotX, dotY, dp(7), p);

        if (tracking) {
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(2f));
            p.setColor(0x88EAF4F2);
            c.drawCircle(fingerX, fingerY, dp(14), p);
        }

        drawHint(c, "手指按住光点，一路跟着它", h - dp(34));
    }

    private static final int Ui_ALPHA = 0x332EC4B6;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                tracking = true;
                fingerX = event.getX();
                fingerY = event.getY();
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) performClick();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                tracking = false;
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected int computeScore() {
        if (frames == 0) return 0;
        return Math.round(100f * goodFrames / (float) frames);
    }

    @Override
    protected String resultDetail() {
        if (frames == 0) return "本次没有按住光点，下次把手指放上去就行";
        return "跟随贴合度 " + Math.round(100f * goodFrames / frames) + "%";
    }
}
