package com.zach.motionfit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 配色、字体与通用控件样式（深海夜色主题）。 */
public final class Ui {

    public static final int BG_TOP = 0xFF0A1D23;
    public static final int BG_BOTTOM = 0xFF050D10;
    public static final int CARD_TOP = 0xFF1C3A3E;
    public static final int CARD_BOTTOM = 0xFF0F2226;
    public static final int MINT = 0xFF5FE3C8;
    public static final int MINT_DEEP = 0xFF37BFB2;
    public static final int BLUE = 0xFF3E8DFF;
    public static final int TEXT = 0xFFEAF6F3;
    public static final int MUTED = 0xFF95AFAD;
    public static final int DIM = 0xFF7E9A99;
    public static final int FAINT = 0xFF56706F;
    public static final int ON_ACCENT = 0xFF052420;
    public static final int LINE = 0x16FFFFFF;

    public static final Typeface LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL);
    public static final Typeface REG = Typeface.create("sans-serif", Typeface.NORMAL);
    public static final Typeface MED = Typeface.create("sans-serif-medium", Typeface.NORMAL);

    private Ui() {}

    public static int dp(Context c, float v) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                c.getResources().getDisplayMetrics()));
    }

    public static GradientDrawable round(Context c, float radiusDp, int color) {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(c, radiusDp));
        d.setColor(color);
        return d;
    }

    public static GradientDrawable vertical(Context c, float radiusDp, int top, int bottom) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{top, bottom});
        d.setCornerRadius(dp(c, radiusDp));
        return d;
    }

    public static GradientDrawable diagonal(Context c, float radiusDp, int from, int to) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{from, to});
        d.setCornerRadius(dp(c, radiusDp));
        return d;
    }

    /** 玻璃感卡片：上深下浅 + 极细高光描边。 */
    public static GradientDrawable card(Context c) {
        GradientDrawable d = vertical(c, 22, CARD_TOP, CARD_BOTTOM);
        d.setStroke(dp(c, 1), LINE);
        return d;
    }

    public static GradientDrawable softCard(Context c) {
        GradientDrawable d = vertical(c, 20, 0xFF14282C, 0xFF0C1C20);
        d.setStroke(dp(c, 1), LINE);
        return d;
    }

    public static GradientDrawable chip(Context c, boolean accent) {
        GradientDrawable d = round(c, 999, accent ? 0x1F5FE3C8 : 0x0FFFFFFF);
        d.setStroke(dp(c, 1), accent ? 0x575FE3C8 : 0x24FFFFFF);
        return d;
    }

    /** 悬浮底栏。 */
    public static GradientDrawable navBar(Context c) {
        GradientDrawable d = round(c, 26, 0xD90A181B);
        d.setStroke(dp(c, 1), 0x14FFFFFF);
        return d;
    }

    /** 警示卡片。 */
    public static GradientDrawable warnCard(Context c) {
        GradientDrawable d = vertical(c, 20, 0xFF2A2417, 0xFF1A1910);
        d.setStroke(dp(c, 1), 0x24FFB27A);
        return d;
    }

    public static GradientDrawable primaryButton(Context c) {
        GradientDrawable d = diagonal(c, 16, 0xFF63E7CC, MINT_DEEP);
        return d;
    }

    public static TextView text(Context c, String s, float sp, int color, Typeface tf) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setTypeface(tf);
        t.setIncludeFontPadding(false);
        t.setLineSpacing(dp(c, 6), 1f);
        return t;
    }

    public static TextView spaced(Context c, String s, float sp, int color, Typeface tf, float spacing) {
        TextView t = text(c, s, sp, color, tf);
        t.setLetterSpacing(spacing);
        return t;
    }

    public static TextView chipView(Context c, String s, boolean accent) {
        TextView t = text(c, s, 11, accent ? 0xFF9FE7D8 : 0xFF9DB6B4, MED);
        t.setBackground(chip(c, accent));
        t.setPadding(dp(c, 12), dp(c, 7), dp(c, 12), dp(c, 7));
        t.setGravity(Gravity.CENTER);
        return t;
    }

    public static SpannableStringBuilder fraction(Context c, String big, String small) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        int a = sb.length();
        sb.append(big);
        sb.setSpan(new RelativeSizeSpan(1f), a, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int b = sb.length();
        sb.append(small);
        sb.setSpan(new RelativeSizeSpan(0.42f), b, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(DIM), b, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static View spacer(Context c, float dp) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(c, dp)));
        return v;
    }

    public static View hspacer(Context c, float dp) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(dp(c, dp), 1));
        return v;
    }

    public static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    public static LinearLayout row(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    public static int alpha(int color, float a) {
        int al = Math.round(255 * Math.max(0f, Math.min(1f, a)));
        return Color.argb(al, Color.red(color), Color.green(color), Color.blue(color));
    }
}
