package com.zach.motionfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/** 深海夜色背景：竖向渐变 + 两处柔光。 */
public class Backdrop extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Backdrop(Context c) {
        super(c);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        paint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{Ui.BG_TOP, 0xFF071417, Ui.BG_BOTTOM}, new float[]{0f, 0.45f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        paint.setShader(new RadialGradient(w * 0.18f, -h * 0.06f, w * 1.05f,
                new int[]{0x335FE3C8, 0x0A5FE3C8, 0x00000000}, new float[]{0f, 0.45f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        paint.setShader(new RadialGradient(w * 0.95f, h * 0.18f, w * 0.85f,
                new int[]{0x223E8DFF, 0x00000000}, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        paint.setShader(null);
    }
}
