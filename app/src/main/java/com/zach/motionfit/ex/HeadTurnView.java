package com.zach.motionfit.ex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

/** 头眼协同：盯住中央注视点，按提示缓慢左右转头。 */
public class HeadTurnView extends BaseExerciseView implements SensorEventListener {

    private SensorManager manager;
    private Sensor sensor;
    private final float[] rot = new float[9];
    private float[] lastRot;
    private long lastSensorAt = 0L;
    private float lastAzimuth = 0f;
    private boolean hasAzimuth = false;

    private long cueStartAt = 0L;
    private int cueDirection = 1; // 1 右, -1 左
    private long goodMs = 0L;
    private long totalMs = 0L;
    private float liveSpeed = 0f;
    private float liveDirection = 0f;
    private boolean sensorReady = false;

    public HeadTurnView(Context c) {
        super(c);
        totalSec = 90;
    }

    @Override
    protected void onBegin() {
        manager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (manager != null) {
            sensor = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            if (sensor == null) sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        if (sensor != null) {
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        cueStartAt = SystemClock.elapsedRealtime();
        cueDirection = 1;
        goodMs = 0;
        totalMs = 0;
        hasAzimuth = false;
        lastRot = null;
    }

    private long cueMs() {
        return 3200L;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length < 3) return;
        SensorManager.getRotationMatrixFromVector(rot, event.values);

        long now = SystemClock.elapsedRealtime();
        float dt = lastSensorAt == 0 ? 0.016f : Math.min(0.08f, (now - lastSensorAt) / 1000f);
        lastSensorAt = now;
        sensorReady = true;

        if (lastRot == null) {
            lastRot = rot.clone();
        } else {
            float trace = rot[0] + rot[4] + rot[8];
            float cos = Math.max(-1f, Math.min(1f, (trace - 1f) / 2f));
            float angle = (float) Math.acos(cos);
            liveSpeed = (float) Math.toDegrees(angle) / Math.max(dt, 0.001f);
            lastRot = rot.clone();
        }

        // 用设备 x 轴（屏幕右方向）的朝向判断转头方向，竖持或平放都稳定
        float az = (float) Math.atan2(rot[0], rot[3]);
        if (hasAzimuth) {
            float delta = az - lastAzimuth;
            while (delta > Math.PI) delta -= 2 * Math.PI;
            while (delta < -Math.PI) delta += 2 * Math.PI;
            liveDirection = delta;
        }
        lastAzimuth = az;
        hasAzimuth = true;

        // 评分：方向与提示一致，且转速落在缓速区间
        totalMs += (long) (dt * 1000f);
        boolean directionOk = (cueDirection > 0 && liveDirection > 0.002f)
                || (cueDirection < 0 && liveDirection < -0.002f);
        boolean speedOk = liveSpeed > 8f && liveSpeed < 48f;
        if (directionOk && speedOk) goodMs += (long) (dt * 1000f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onFrame(Canvas c, float elapsedSec) {
        long now = SystemClock.elapsedRealtime();
        if (now - cueStartAt > cueMs()) {
            cueStartAt = now;
            cueDirection = -cueDirection;
        }

        int w = getWidth();
        int h = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF071417);
        c.drawRect(0, 0, w, h, paint);

        float cx = w / 2f;
        float cy = h / 2f;

        if (!sensorReady) {
            paint.setColor(0xFF95AFAD);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(dp(15));
            c.drawText("正在读取姿态传感器…", cx, cy + dp(90), paint);
        }

        // 中央注视点
        paint.setColor(0xFF2EC4B6);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        c.drawCircle(cx, cy, dp(26), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFEAF4F2);
        c.drawCircle(cx, cy, dp(6), paint);

        // 方向提示
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(17));
        paint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        paint.setColor(cueDirection > 0 ? 0xFF2EC4B6 : 0xFFFFA46B);
        String cue = cueDirection > 0 ? "向右缓慢转头  →" : "←  向左缓慢转头";
        c.drawText(cue, cx, cy + dp(90), paint);

        // 转速条
        float barW = w * 0.56f;
        float barH = dp(6);
        float left = cx - barW / 2f;
        float top = h - dp(86);
        paint.setColor(0x332EC4B6);
        c.drawRoundRect(left, top, left + barW, top + barH, barH, barH, paint);
        float ratio = Math.min(1f, liveSpeed / 60f);
        paint.setColor(ratio > 0.8f ? 0xFFFFA46B : 0xFF2EC4B6);
        c.drawRoundRect(left, top, left + barW * ratio, top + barH, barH, barH, paint);

        paint.setTextSize(dp(12));
        paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        paint.setColor(0xFF95AFAD);
        c.drawText("转头速度 " + Math.round(liveSpeed) + "°/秒（建议 15~35）", cx, top - dp(10), paint);
        c.drawText("保持注视中心点，幅度不用大，慢慢来", cx, h - dp(30), paint);
    }

    @Override
    protected int computeScore() {
        if (totalMs < 1000) return 0;
        return Math.round(100f * goodMs / (float) totalMs);
    }

    @Override
    protected String resultDetail() {
        if (!sensorReady) return "本机未能读取姿态传感器，这组训练可以跳过";
        return "有效转头时间 " + Math.round(goodMs / 1000f) + " 秒 / " + Math.round(totalMs / 1000f) + " 秒";
    }

    @Override
    protected void onDetachedFromWindow() {
        if (manager != null) manager.unregisterListener(this);
        super.onDetachedFromWindow();
    }
}
