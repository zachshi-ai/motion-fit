package com.zach.motionfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.view.View;

/** 渐变进度环，带轻微外发光。 */
public class ProgressRing extends View {

    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private float progress = 0f;
    private float strokeDp = 9f;

    public ProgressRing(Context c) {
        super(c);
        track.setStyle(Paint.Style.STROKE);
        track.setStrokeCap(Paint.Cap.ROUND);
        track.setColor(0x14FFFFFF);
        arc.setStyle(Paint.Style.STROKE);
        arc.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public ProgressRing stroke(float dp) {
        this.strokeDp = dp;
        invalidate();
        return this;
    }

    public ProgressRing progress(float p) {
        this.progress = Math.max(0f, Math.min(1f, p));
        invalidate();
        return this;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        float s = Math.min(w, h);
        float stroke = dp(strokeDp);
        float r = s / 2f - stroke * 1.35f;
        float cx = w / 2f;
        float cy = h / 2f;

        track.setStrokeWidth(stroke);
        canvas.drawCircle(cx, cy, r, track);

        if (progress <= 0.001f) return;

        SweepGradient grad = new SweepGradient(cx, cy,
                new int[]{Ui.MINT, 0xFF59D8D0, Ui.BLUE}, new float[]{0f, 0.55f, 1f});
        Matrix m = new Matrix();
        m.setRotate(-90, cx, cy);
        grad.setLocalMatrix(m);
        arc.setShader(grad);
        arc.setStrokeWidth(stroke * 0.86f);

        // 外发光层
        arc.setAlpha(70);
        arc.setShadowLayer(dp(10), 0, 0, Ui.alpha(Ui.MINT, 0.55f));
        rect.set(cx - r, cy - r, cx + r, cy + r);
        canvas.drawArc(rect, -90, 360 * progress, false, arc);

        // 主描边
        arc.setAlpha(255);
        arc.clearShadowLayer();
        canvas.drawArc(rect, -90, 360 * progress, false, arc);
    }
}
