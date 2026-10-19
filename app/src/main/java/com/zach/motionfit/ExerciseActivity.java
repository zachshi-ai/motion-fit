package com.zach.motionfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zach.motionfit.ex.BaseExerciseView;
import com.zach.motionfit.ex.HeadTurnView;
import com.zach.motionfit.ex.OptokineticView;
import com.zach.motionfit.ex.PursuitView;
import com.zach.motionfit.ex.SaccadeView;

/** 单个训练的进行页与结算页。 */
public class ExerciseActivity extends Activity implements BaseExerciseView.Listener {

    private static final String EXTRA_MODE = "mode";

    private Exercise exercise;
    private BaseExerciseView view;
    private TextView timer;
    private FrameLayout root;
    private LinearLayout page;
    private View progressFill;
    private boolean finished = false;

    public static Intent intent(Context c, String mode) {
        return new Intent(c, ExerciseActivity.class).putExtra(EXTRA_MODE, mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercise = Exercise.byMode(getIntent().getStringExtra(EXTRA_MODE));

        root = new FrameLayout(this);
        root.addView(new Backdrop(this), new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        page = Ui.column(this);
        page.setFitsSystemWindows(true);

        LinearLayout bar = Ui.row(this);
        bar.setPadding(Ui.dp(this, 20), Ui.dp(this, 16), Ui.dp(this, 20), Ui.dp(this, 10));

        TextView close = Ui.text(this, "✕", 15, Ui.MUTED, Ui.REG);
        close.setPadding(Ui.dp(this, 6), Ui.dp(this, 8), Ui.dp(this, 14), Ui.dp(this, 8));
        close.setOnClickListener(v -> finish());
        bar.addView(close);

        TextView title = Ui.text(this, exercise.name, 14, Ui.TEXT, Ui.MED);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        bar.addView(title);

        timer = Ui.text(this, "00:00", 12, Ui.MINT, Ui.MED);
        timer.setLetterSpacing(0.06f);
        timer.setPadding(Ui.dp(this, 12), Ui.dp(this, 8), Ui.dp(this, 4), Ui.dp(this, 8));
        bar.addView(timer);

        page.addView(bar);

        // 顶部细进度条
        FrameLayout track = new FrameLayout(this);
        track.setBackground(Ui.round(this, 999, 0x14FFFFFF));
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Ui.dp(this, 2));
        tlp.setMargins(Ui.dp(this, 22), 0, Ui.dp(this, 22), 0);
        track.setLayoutParams(tlp);

        progressFill = new View(this);
        progressFill.setBackground(Ui.round(this, 999, Ui.MINT));
        progressFill.setLayoutParams(new FrameLayout.LayoutParams(0, Ui.dp(this, 2)));
        track.addView(progressFill);
        page.addView(track);

        page.addView(Ui.spacer(this, 6));

        view = createView(exercise.mode);
        view.setLevel(new Store(this).level());
        view.setListener(this);
        page.addView(view, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(page);
        setContentView(root);

        view.post(view::begin);
    }

    private BaseExerciseView createView(String mode) {
        switch (mode) {
            case Exercise.MODE_PURSUIT:
                return new PursuitView(this);
            case Exercise.MODE_SACCADE:
                return new SaccadeView(this);
            case Exercise.MODE_HEADTURN:
                return new HeadTurnView(this);
            default:
                return new OptokineticView(this);
        }
    }

    @Override
    public void onProgress(int elapsedSec, int totalSec) {
        if (finished) return;
        int left = Math.max(0, totalSec - elapsedSec);
        timer.setText(String.format(java.util.Locale.CHINA, "%02d:%02d", left / 60, left % 60));

        if (progressFill != null && progressFill.getParent() != null) {
            View parent = (View) progressFill.getParent();
            int w = Math.round(parent.getWidth() * Math.min(1f, elapsedSec / (float) Math.max(1, totalSec)));
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) progressFill.getLayoutParams();
            if (lp.width != w) {
                lp.width = w;
                progressFill.setLayoutParams(lp);
            }
        }
    }

    @Override
    public void onFinish(int score, int seconds, String detail) {
        if (finished) return;
        finished = true;
        timer.setText("完成");
        new Store(this).addSession(exercise.mode, score, seconds);
        showResult(score, detail);
    }

    private void showResult(int score, String detail) {
        FrameLayout overlay = new FrameLayout(this);
        overlay.setBackgroundColor(0xCC04100F);
        overlay.setClickable(true);

        LinearLayout sheet = Ui.column(this);
        sheet.setBackground(Ui.vertical(this, 28, 0xFF14282C, 0xFF0A181B));
        sheet.setPadding(Ui.dp(this, 24), Ui.dp(this, 24), Ui.dp(this, 24), Ui.dp(this, 22));

        FrameLayout.LayoutParams slp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        slp.gravity = Gravity.BOTTOM;
        slp.setMargins(Ui.dp(this, 12), 0, Ui.dp(this, 12), Ui.dp(this, 12));
        sheet.setLayoutParams(slp);

        sheet.addView(Ui.spaced(this, exercise.index + " · " + exercise.name, 11, Ui.DIM, Ui.MED, 0.16f));
        sheet.addView(Ui.spacer(this, 16));

        LinearLayout scoreRow = Ui.row(this);
        scoreRow.setGravity(Gravity.CENTER);

        FrameLayout ringHolder = new FrameLayout(this);
        LinearLayout.LayoutParams rhp = new LinearLayout.LayoutParams(Ui.dp(this, 96), Ui.dp(this, 96));
        rhp.rightMargin = Ui.dp(this, 18);
        ringHolder.setLayoutParams(rhp);
        ProgressRing ring = new ProgressRing(this).progress(score / 100f).stroke(8);
        ringHolder.addView(ring, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        TextView scoreText = Ui.text(this, String.valueOf(score), 26, Ui.TEXT, Ui.LIGHT);
        scoreText.setGravity(Gravity.CENTER);
        ringHolder.addView(scoreText, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        scoreRow.addView(ringHolder);

        LinearLayout feedbackCol = Ui.column(this);
        feedbackCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        feedbackCol.addView(Ui.text(this, gradeOf(score), 16, Ui.MINT, Ui.MED));
        feedbackCol.addView(Ui.spacer(this, 7));
        feedbackCol.addView(Ui.text(this, detail, 11.5f, Ui.MUTED, Ui.REG));
        scoreRow.addView(feedbackCol);

        sheet.addView(scoreRow);
        sheet.addView(Ui.spacer(this, 18));
        sheet.addView(Ui.text(this, tipOf(exercise.mode), 12, Ui.DIM, Ui.REG));
        sheet.addView(Ui.spacer(this, 22));

        TextView again = Ui.text(this, "再来一次", 15, Ui.ON_ACCENT, Ui.MED);
        again.setGravity(Gravity.CENTER);
        again.setBackground(Ui.primaryButton(this));
        again.setPadding(0, Ui.dp(this, 15), 0, Ui.dp(this, 15));
        again.setOnClickListener(v -> restart(overlay));
        sheet.addView(again, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView back = Ui.text(this, "回到首页", 13, Ui.DIM, Ui.REG);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0, Ui.dp(this, 14), 0, Ui.dp(this, 10));
        back.setOnClickListener(v -> finish());
        sheet.addView(back);

        overlay.addView(sheet);
        root.addView(overlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        sheet.setTranslationY(Ui.dp(this, 60));
        sheet.setAlpha(0f);
        overlay.setAlpha(0f);
        overlay.animate().alpha(1f).setDuration(200).start();
        sheet.animate().translationY(0f).alpha(1f).setDuration(260).start();
    }

    private void restart(FrameLayout overlay) {
        finished = false;
        root.removeView(overlay);
        if (page.getChildCount() > 3) page.removeViewAt(3);
        BaseExerciseView fresh = createView(exercise.mode);
        fresh.setLevel(new Store(this).level());
        fresh.setListener(this);
        view = fresh;
        page.addView(fresh, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) progressFill.getLayoutParams();
        lp.width = 0;
        progressFill.setLayoutParams(lp);
        timer.setText("00:00");
        fresh.post(fresh::begin);
    }

    private String gradeOf(int score) {
        if (score >= 85) return "稳得很";
        if (score >= 65) return "保持住";
        if (score >= 40) return "有点飘";
        return "先减量";
    }

    private String tipOf(String mode) {
        switch (mode) {
            case Exercise.MODE_OKTO:
                return "换向后 1 秒内点屏幕，练的就是视觉冲突下的反应速度。";
            case Exercise.MODE_PURSUIT:
                return "手指贴合度越高，视觉与前庭的配合就越紧。";
            case Exercise.MODE_SACCADE:
                return "扫视要快而准：不动头，只动眼。";
            case Exercise.MODE_HEADTURN:
                return "头动、眼不动——这是抗晕最核心的一对反射。";
            default:
                return "";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!finished && !isFinishing() && view != null) view.stopNow();
    }
}
