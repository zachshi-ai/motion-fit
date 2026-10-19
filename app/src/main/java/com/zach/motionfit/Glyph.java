package com.zach.motionfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/** 统一的线性图标：全部使用同一线宽与圆角端点。 */
public class Glyph extends View {

    public static final int TARGET = 0;
    public static final int PURSUIT = 1;
    public static final int SCAN = 2;
    public static final int ARROWS = 3;
    public static final int NAV_TRAIN = 4;
    public static final int NAV_PLAN = 5;
    public static final int NAV_LOG = 6;

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int type = TARGET;
    private int color = Ui.MINT;

    public Glyph(Context c) {
        super(c);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setColor(color);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public Glyph type(int t) {
        this.type = t;
        invalidate();
        return this;
    }

    public Glyph color(int c) {
        this.color = c;
        p.setColor(c);
        invalidate();
        return this;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float size = Math.min(w, h);
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(1.7f));

        switch (type) {
            case TARGET: {
                float r = size * 0.32f;
                canvas.drawCircle(cx, cy, r, p);
                canvas.drawLine(cx - size * 0.46f, cy, cx - r - dp(2.5f), cy, p);
                canvas.drawLine(cx + r + dp(2.5f), cy, cx + size * 0.46f, cy, p);
                break;
            }
            case PURSUIT: {
                p.setStyle(Paint.Style.FILL);
                p.setColor(Ui.alpha(color, 0.45f));
                canvas.drawCircle(cx - size * 0.14f, cy + size * 0.06f, size * 0.22f, p);
                p.setColor(color);
                canvas.drawCircle(cx + size * 0.16f, cy - size * 0.14f, size * 0.13f, p);
                break;
            }
            case SCAN: {
                float r = size * 0.34f;
                p.setPathEffect(new DashPathEffect(new float[]{dp(2.6f), dp(3.2f)}, 0));
                canvas.drawCircle(cx, cy, r, p);
                p.setPathEffect(null);
                p.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, size * 0.09f, p);
                break;
            }
            case ARROWS: {
                float len = size * 0.30f;
                Path left = new Path();
                left.moveTo(cx - len, cy);
                left.lineTo(cx - len * 0.45f, cy - len * 0.45f);
                left.moveTo(cx - len, cy);
                left.lineTo(cx - len * 0.45f, cy + len * 0.45f);
                canvas.drawPath(left, p);
                Path right = new Path();
                right.moveTo(cx + len, cy);
                right.lineTo(cx + len * 0.45f, cy - len * 0.45f);
                right.moveTo(cx + len, cy);
                right.lineTo(cx + len * 0.45f, cy + len * 0.45f);
                canvas.drawPath(right, p);
                break;
            }
            case NAV_TRAIN: {
                canvas.drawCircle(cx, cy, size * 0.34f, p);
                p.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, size * 0.12f, p);
                break;
            }
            case NAV_PLAN: {
                RectF r = new RectF(cx - size * 0.33f, cy - size * 0.29f,
                        cx + size * 0.33f, cy + size * 0.33f);
                canvas.drawRoundRect(r, dp(3.2f), dp(3.2f), p);
                canvas.drawLine(r.left, r.top + size * 0.20f, r.right, r.top + size * 0.20f, p);
                canvas.drawLine(cx - size * 0.15f, r.top - size * 0.09f, cx - size * 0.15f, r.top + size * 0.03f, p);
                canvas.drawLine(cx + size * 0.15f, r.top - size * 0.09f, cx + size * 0.15f, r.top + size * 0.03f, p);
                break;
            }
            case NAV_LOG: {
                p.setStrokeWidth(dp(2f));
                canvas.drawLine(cx - size * 0.28f, cy + size * 0.28f, cx - size * 0.28f, cy + size * 0.02f, p);
                canvas.drawLine(cx, cy + size * 0.28f, cx, cy - size * 0.26f, p);
                canvas.drawLine(cx + size * 0.28f, cy + size * 0.28f, cx + size * 0.28f, cy - size * 0.10f, p);
                break;
            }
            default:
                break;
        }
    }
}
