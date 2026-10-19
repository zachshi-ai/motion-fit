package com.zach.motionfit.ex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/** 训练的公共骨架：计时、逐帧绘制、结束回调。 */
public abstract class BaseExerciseView extends View {

    public interface Listener {
        void onProgress(int elapsedSec, int totalSec);

        void onFinish(int score, int seconds, String detail);
    }

    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long startedAt;
    private boolean running;
    private boolean delivered;
    private boolean delivering;
    protected int totalSec = 60;
    protected float density;
    protected int level = 1;
    protected Listener listener;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            float elapsed = elapsedSec();
            invalidate();
            if (listener != null) listener.onProgress((int) elapsed, totalSec);
            if (elapsed >= totalSec) {
                stopAndDeliver();
            } else {
                handler.postDelayed(this, 16);
            }
        }
    };

    public BaseExerciseView(Context c) {
        super(c);
        init();
    }

    public BaseExerciseView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        setBackgroundColor(0xFF071417);
        setKeepScreenOn(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(3, level));
    }

    public void setListener(Listener l) {
        this.listener = l;
    }

    protected float dp(float v) {
        return v * density;
    }

    protected float elapsedSec() {
        return (SystemClock.elapsedRealtime() - startedAt) / 1000f;
    }

    public void begin() {
        startedAt = SystemClock.elapsedRealtime();
        running = true;
        delivered = false;
        delivering = false;
        onBegin();
        handler.post(ticker);
    }

    /** 用户主动结束。 */
    public void stopNow() {
        if (!running) return;
        stopAndDeliver();
    }

    protected void finishEarly() {
        if (!running) return;
        stopAndDeliver();
    }

    private void stopAndDeliver() {
        if (delivering || delivered) return;
        delivering = true;
        running = false;
        handler.removeCallbacks(ticker);
        invalidate();
        final float elapsed = elapsedSec();
        // 放到下一个消息里回调，避免在 onDraw 过程中修改视图树
        handler.post(() -> {
            delivered = true;
            int secs = Math.max(1, Math.round(elapsed));
            String detail = resultDetail();
            int score = Math.max(0, Math.min(100, computeScore()));
            if (listener != null) listener.onFinish(score, secs, detail);
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onFrame(canvas, elapsedSec());
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(ticker);
        running = false;
        super.onDetachedFromWindow();
    }

    protected void onBegin() {
    }

    /** 逐帧绘制，elapsedSec 为已训练秒数。 */
    protected abstract void onFrame(Canvas canvas, float elapsedSec);

    /** 0~100 的完成度评分。 */
    protected abstract int computeScore();

    /** 结果页上的一句训练反馈。 */
    protected abstract String resultDetail();

    protected void drawHint(Canvas c, String s, float y) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF95AFAD);
        paint.setTextSize(dp(13));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        c.drawText(s, getWidth() / 2f, y, paint);
    }
}
