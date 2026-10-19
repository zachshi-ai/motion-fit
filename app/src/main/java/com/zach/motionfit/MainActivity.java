package com.zach.motionfit;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zach.motionfit.Store.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** 主页：训练 / 计划 / 记录。 */
public class MainActivity extends Activity {

    private static final String[] TABS = {"训练", "计划", "记录"};
    private static final int[] TAB_GLYPHS = {Glyph.NAV_TRAIN, Glyph.NAV_PLAN, Glyph.NAV_LOG};

    private FrameLayout contentHolder;
    private LinearLayout nav;
    private LinearLayout[] cells;
    private Glyph[] icons;
    private TextView[] labels;
    private int tab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.addView(new Backdrop(this), new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        contentHolder = new FrameLayout(this);
        root.addView(contentHolder, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        nav = buildNav();
        FrameLayout.LayoutParams nlp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        nlp.gravity = Gravity.BOTTOM;
        nlp.setMargins(Ui.dp(this, 18), 0, Ui.dp(this, 18), Ui.dp(this, 20));
        root.addView(nav, nlp);

        setContentView(root);
        selectTab(0);
    }

    private LinearLayout buildNav() {
        LinearLayout bar = Ui.row(this);
        bar.setPadding(Ui.dp(this, 7), Ui.dp(this, 7), Ui.dp(this, 7), Ui.dp(this, 7));
        bar.setBackground(Ui.navBar(this));
        bar.setElevation(Ui.dp(this, 14));

        cells = new LinearLayout[TABS.length];
        icons = new Glyph[TABS.length];
        labels = new TextView[TABS.length];

        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            LinearLayout cell = Ui.column(this);
            cell.setGravity(Gravity.CENTER);
            cell.setPadding(0, Ui.dp(this, 8), 0, Ui.dp(this, 8));
            cell.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            Glyph g = new Glyph(this);
            g.type(TAB_GLYPHS[i]);
            LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(Ui.dp(this, 18), Ui.dp(this, 18));
            g.setLayoutParams(glp);
            cell.addView(g);

            TextView label = Ui.text(this, TABS[i], 10, Ui.DIM, Ui.MED);
            label.setLetterSpacing(0.1f);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.topMargin = Ui.dp(this, 5);
            label.setLayoutParams(llp);
            cell.addView(label);

            cell.setOnClickListener(v -> selectTab(index));
            bar.addView(cell);
            cells[i] = cell;
            icons[i] = g;
            labels[i] = label;
        }
        return bar;
    }

    private void selectTab(int index) {
        tab = index;
        for (int i = 0; i < cells.length; i++) {
            boolean on = i == index;
            cells[i].setBackground(on ? Ui.primaryButton(this) : null);
            icons[i].color(on ? Ui.ON_ACCENT : 0xFF87A3A1);
            labels[i].setTextColor(on ? Ui.ON_ACCENT : 0xFF87A3A1);
            labels[i].setTypeface(on ? Ui.MED : Ui.REG);
        }
        contentHolder.removeAllViews();
        View page;
        if (index == 1) page = buildPlan();
        else if (index == 2) page = buildRecords();
        else page = buildTraining();
        contentHolder.addView(page);
    }

    private ScrollView scrollPage(LinearLayout content) {
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(false);
        sv.setVerticalScrollBarEnabled(false);
        sv.addView(content);
        return sv;
    }

    private LinearLayout pageColumn() {
        LinearLayout page = Ui.column(this);
        page.setPadding(Ui.dp(this, 22), Ui.dp(this, 30), Ui.dp(this, 22), Ui.dp(this, 118));
        return page;
    }

    private View header(String brand, String right) {
        LinearLayout row = Ui.row(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(Ui.spaced(this, brand, 11, Ui.MINT, Ui.MED, 0.24f));
        View fill = new View(this);
        fill.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        row.addView(fill);
        row.addView(Ui.text(this, right, 11, 0xFF8AA6A4, Ui.REG));
        return row;
    }

    // ---------------- 训练 ----------------

    private View buildTraining() {
        Store store = new Store(this);
        LinearLayout page = pageColumn();

        page.addView(header("MOTIONFIT",
                new SimpleDateFormat("M 月 d 日 · EEEE", Locale.CHINA).format(new Date())));
        page.addView(Ui.spacer(this, 22));
        page.addView(buildHero(store));
        page.addView(Ui.spacer(this, 20));
        page.addView(buildChips(store));
        page.addView(Ui.spacer(this, 26));

        LinearLayout sec = Ui.row(this);
        sec.addView(Ui.spaced(this, "四组练习", 12, Ui.DIM, Ui.MED, 0.16f));
        View fill = new View(this);
        fill.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        sec.addView(fill);
        sec.addView(Ui.text(this, "建议 01 → 04 顺序", 11, Ui.FAINT, Ui.REG));
        page.addView(sec);
        page.addView(Ui.spacer(this, 12));

        for (Exercise e : Exercise.all()) {
            page.addView(buildExerciseCard(store, e));
            page.addView(Ui.spacer(this, 11));
        }

        return scrollPage(page);
    }

    private View buildHero(Store store) {
        FrameLayout hero = new FrameLayout(this);
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Ui.dp(this, 206));
        hero.setLayoutParams(hlp);

        int done = Math.min(4, store.todayCount());
        ProgressRing ring = new ProgressRing(this).progress(done / 4f);
        FrameLayout.LayoutParams rlp = new FrameLayout.LayoutParams(
                Ui.dp(this, 196), Ui.dp(this, 196), Gravity.CENTER);
        hero.addView(ring, rlp);

        LinearLayout inner = Ui.column(this);
        inner.setGravity(Gravity.CENTER);
        inner.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        SpannableStringBuilder sb = Ui.fraction(this, String.valueOf(done), "/4");
        TextView num = Ui.text(this, "", 46, Ui.TEXT, Ui.LIGHT);
        num.setText(sb);
        num.setLetterSpacing(-0.02f);
        num.setGravity(Gravity.CENTER);
        inner.addView(num);

        TextView cap = Ui.spaced(this, "今日完成", 11, Ui.DIM, Ui.REG, 0.16f);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        clp.topMargin = Ui.dp(this, 8);
        cap.setLayoutParams(clp);
        inner.addView(cap);

        hero.addView(inner);
        return hero;
    }

    private View buildChips(Store store) {
        LinearLayout chips = Ui.row(this);
        chips.setGravity(Gravity.CENTER);

        int streak = store.streak();
        chips.addView(Ui.chipView(this, streak > 0 ? "连续 " + streak + " 天" : "今天开始", true));
        chips.addView(Ui.hspacer(this, 8));
        chips.addView(Ui.chipView(this, "Lv." + store.level() + " · 中等强度", false));
        chips.addView(Ui.hspacer(this, 8));
        chips.addView(Ui.chipView(this, "约 5 分钟", false));

        LinearLayout wrap = Ui.column(this);
        wrap.addView(chips);
        return wrap;
    }

    private View buildExerciseCard(Store store, Exercise e) {
        LinearLayout card = Ui.row(this);
        card.setBackground(Ui.card(this));
        card.setElevation(Ui.dp(this, 6));
        card.setPadding(Ui.dp(this, 15), Ui.dp(this, 15), Ui.dp(this, 14), Ui.dp(this, 15));
        card.setClickable(true);
        card.setOnClickListener(v -> startActivity(ExerciseActivity.intent(this, e.mode)));

        FrameLayout tile = new FrameLayout(this);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(Ui.dp(this, 52), Ui.dp(this, 52));
        tlp.rightMargin = Ui.dp(this, 15);
        tile.setLayoutParams(tlp);
        tile.setBackground(Ui.diagonal(this, 17, 0x335FE3C8, 0x143E8DFF));

        Glyph glyph = new Glyph(this);
        glyph.type(glyphOf(e.mode));
        glyph.color(0xFFCFF7EE);
        tile.addView(glyph, new FrameLayout.LayoutParams(Ui.dp(this, 26), Ui.dp(this, 26), Gravity.CENTER));
        card.addView(tile);

        LinearLayout body = Ui.column(this);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        body.setLayoutParams(blp);

        LinearLayout titleRow = Ui.row(this);
        TextView idx = Ui.text(this, e.index, 10, 0x805FE3C8, Ui.MED);
        idx.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ilp.rightMargin = Ui.dp(this, 8);
        idx.setLayoutParams(ilp);
        titleRow.addView(idx);
        titleRow.addView(Ui.text(this, e.name, 15, Ui.TEXT, Ui.MED));
        body.addView(titleRow);

        body.addView(Ui.spacer(this, 7));

        LinearLayout meta = Ui.row(this);
        int best = store.best(e.mode);
        if (best > 0) {
            meta.addView(Ui.text(this, "最好 " + best + " 分", 10.5f, Ui.MINT, Ui.MED));
            meta.addView(Ui.hspacer(this, 8));
            View dot = new View(this);
            dot.setBackground(Ui.round(this, 999, 0xFF40595A));
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(Ui.dp(this, 3), Ui.dp(this, 3));
            dlp.rightMargin = Ui.dp(this, 8);
            dot.setLayoutParams(dlp);
            meta.addView(dot);
        }
        meta.addView(Ui.text(this, e.summary, 11.5f, Ui.MUTED, Ui.REG));
        body.addView(meta);

        card.addView(body);

        TextView chev = Ui.text(this, "›", 18, 0xFF9FB8B6, Ui.REG);
        chev.setBackground(Ui.round(this, 999, 0x12FFFFFF));
        chev.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(Ui.dp(this, 26), Ui.dp(this, 26));
        chev.setLayoutParams(clp);
        card.addView(chev);

        return card;
    }

    private int glyphOf(String mode) {
        switch (mode) {
            case Exercise.MODE_PURSUIT:
                return Glyph.PURSUIT;
            case Exercise.MODE_SACCADE:
                return Glyph.SCAN;
            case Exercise.MODE_HEADTURN:
                return Glyph.ARROWS;
            default:
                return Glyph.TARGET;
        }
    }

    // ---------------- 计划 ----------------

    private View buildPlan() {
        Store store = new Store(this);
        LinearLayout page = pageColumn();

        page.addView(header("TRAINING PLAN", "Lv." + store.level()));
        page.addView(Ui.spacer(this, 18));
        page.addView(Ui.text(this, "四周循序渐进", 26, Ui.TEXT, Ui.LIGHT));
        page.addView(Ui.spacer(this, 8));
        page.addView(Ui.text(this, "前庭适应靠「少量、每天、不难受」，不是一次练到晕。", 12.5f, Ui.MUTED, Ui.REG));
        page.addView(Ui.spacer(this, 22));

        String[][] weeks = {
                {"W1", "只做 01 视动适应，每天 1 次。让眼睛习惯「条纹在动、身体没动」。"},
                {"W2", "01 + 02 平滑追踪，练「眼睛咬住目标」，每天约 2 分半。"},
                {"W3", "01 + 02 + 03 扫视，加入快速眼动，适应视野突变，每天约 3 分钟。"},
                {"W4", "四组全做，约 5 分钟。做完闭眼静坐 1 分钟，感受当天的稳定度。"}
        };
        for (String[] w : weeks) {
            page.addView(buildWeekRow(w[0], w[1]));
            page.addView(Ui.spacer(this, 10));
        }

        page.addView(Ui.spacer(this, 18));
        page.addView(Ui.spaced(this, "为什么是这四组", 12, Ui.DIM, Ui.MED, 0.16f));
        page.addView(Ui.spacer(this, 12));

        String[][] principles = {
                {"视动适应", "晕船的核心是「眼睛看到的运动」与「内耳感受到的静止」互相打架。反复看规律漂移的条纹，大脑会慢慢学会忽略这类冲突信号。"},
                {"平滑追踪", "盯住并跟随一个慢速目标，练眼球连续控制，抵消船体摇晃时的视线乱跳。"},
                {"快速扫视", "船在水上，一切都会突然移位。快速定位目标，能减少眩晕后的「找不着北」。"},
                {"头眼协同", "头在动、眼要稳。这对反射稳住了，甲板上走动会轻松很多。"}
        };
        for (String[] p : principles) {
            page.addView(buildTextBlock(p[0], p[1]));
            page.addView(Ui.spacer(this, 10));
        }

        page.addView(Ui.spacer(this, 10));

        LinearLayout warn = Ui.column(this);
        warn.setBackground(Ui.warnCard(this));
        warn.setPadding(Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16));
        warn.addView(Ui.text(this, "什么时候该停", 13.5f, 0xFFFFB27A, Ui.MED));
        warn.addView(Ui.spacer(this, 8));
        warn.addView(Ui.text(this,
                "出现冷汗、恶心、心慌或持续头晕时，立刻停下、闭眼、看向远处固定物体，休息 10 分钟。"
                        + "强度宁可不足，也不要过量——过量会强化「一动就晕」的条件反射。",
                12, 0xFFC8D3CF, Ui.REG));
        page.addView(warn);

        page.addView(Ui.spacer(this, 14));
        page.addView(Ui.text(this, "已完成 " + store.totalSessions() + " 次训练，每满 4 次自动提升一档强度", 11, Ui.FAINT, Ui.REG));

        return scrollPage(page);
    }

    private View buildWeekRow(String tag, String desc) {
        LinearLayout row = Ui.row(this);
        row.setBackground(Ui.softCard(this));
        row.setPadding(Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 16), Ui.dp(this, 14));

        TextView t = Ui.text(this, tag, 11, Ui.ON_ACCENT, Ui.MED);
        t.setBackground(Ui.primaryButton(this));
        t.setPadding(Ui.dp(this, 10), Ui.dp(this, 5), Ui.dp(this, 10), Ui.dp(this, 5));
        t.setGravity(Gravity.CENTER);
        row.addView(t);

        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        dlp.leftMargin = Ui.dp(this, 13);
        TextView body = Ui.text(this, desc, 12, 0xFFC3D6D2, Ui.REG);
        body.setLayoutParams(dlp);
        row.addView(body);
        return row;
    }

    private View buildTextBlock(String title, String body) {
        LinearLayout block = Ui.column(this);
        block.setBackground(Ui.softCard(this));
        block.setPadding(Ui.dp(this, 16), Ui.dp(this, 15), Ui.dp(this, 16), Ui.dp(this, 15));
        block.addView(Ui.text(this, title, 13, Ui.MINT, Ui.MED));
        block.addView(Ui.spacer(this, 7));
        block.addView(Ui.text(this, body, 12, 0xFFC3D6D2, Ui.REG));
        return block;
    }

    // ---------------- 记录 ----------------

    private View buildRecords() {
        Store store = new Store(this);
        LinearLayout page = pageColumn();

        page.addView(header("PROGRESS", "共 " + store.totalSessions() + " 次"));
        page.addView(Ui.spacer(this, 18));
        page.addView(Ui.text(this, "训练记录", 26, Ui.TEXT, Ui.LIGHT));
        page.addView(Ui.spacer(this, 18));

        LinearLayout stats = Ui.row(this);
        stats.addView(statCard("连续天数", String.valueOf(store.streak()), "天", true));
        stats.addView(Ui.hspacer(this, 10));
        stats.addView(statCard("累计训练", String.valueOf(store.totalSessions()), "次", false));
        stats.addView(Ui.hspacer(this, 10));
        stats.addView(statCard("当前等级", "Lv." + store.level(), "", false));
        page.addView(stats);

        page.addView(Ui.spacer(this, 24));
        page.addView(Ui.spaced(this, "各项最好成绩", 12, Ui.DIM, Ui.MED, 0.16f));
        page.addView(Ui.spacer(this, 14));

        for (Exercise e : Exercise.all()) {
            page.addView(bestRow(store.best(e.mode), e));
            page.addView(Ui.spacer(this, 12));
        }

        page.addView(Ui.spacer(this, 14));
        page.addView(Ui.spaced(this, "最近训练", 12, Ui.DIM, Ui.MED, 0.16f));
        page.addView(Ui.spacer(this, 12));

        List<Record> recent = store.recent(20);
        if (recent.isEmpty()) {
            page.addView(Ui.text(this, "还没有记录。做完第一组，这里就会长出一条。", 12, Ui.FAINT, Ui.REG));
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
            for (Record r : recent) {
                page.addView(recordRow(fmt, r));
                page.addView(Ui.spacer(this, 8));
            }
        }

        return scrollPage(page);
    }

    private View bestRow(int best, Exercise e) {
        LinearLayout row = Ui.row(this);
        row.addView(Ui.text(this, e.name, 12.5f, 0xFFC3D6D2, Ui.REG));
        View fill = new View(this);
        fill.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        row.addView(fill);

        FrameLayout barWrap = new FrameLayout(this);
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(Ui.dp(this, 78), Ui.dp(this, 4));
        wlp.rightMargin = Ui.dp(this, 10);
        barWrap.setLayoutParams(wlp);
        barWrap.setBackground(Ui.round(this, 999, 0x1AFFFFFF));

        View bar = new View(this);
        bar.setBackground(Ui.round(this, 999, Ui.MINT));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                Math.max(1, Math.round(Ui.dp(this, 78) * Math.max(0, best) / 100f)), Ui.dp(this, 4));
        bar.setLayoutParams(blp);
        barWrap.addView(bar);
        row.addView(barWrap);

        row.addView(Ui.text(this, best > 0 ? best + "" : "—", 12.5f,
                best > 0 ? Ui.MINT : Ui.FAINT, Ui.MED));
        return row;
    }

    private View recordRow(SimpleDateFormat fmt, Record r) {
        LinearLayout row = Ui.row(this);
        row.setBackground(Ui.softCard(this));
        row.setPadding(Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14), Ui.dp(this, 12));

        LinearLayout col = Ui.column(this);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        col.setLayoutParams(clp);
        col.addView(Ui.text(this, Exercise.nameOf(r.mode), 13, Ui.TEXT, Ui.MED));
        col.addView(Ui.spacer(this, 4));
        col.addView(Ui.text(this, fmt.format(new Date(r.ts)) + " · " + r.seconds + " 秒", 10.5f, Ui.FAINT, Ui.REG));
        row.addView(col);

        TextView score = Ui.text(this, r.score + " 分", 13, Ui.MINT, Ui.MED);
        row.addView(score);
        return row;
    }

    private View statCard(String label, String value, String unit, boolean accent) {
        LinearLayout card = Ui.column(this);
        card.setBackground(Ui.softCard(this));
        card.setPadding(Ui.dp(this, 14), Ui.dp(this, 15), Ui.dp(this, 12), Ui.dp(this, 14));
        card.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout valueRow = Ui.row(this);
        TextView v = Ui.text(this, value, 24, accent ? Ui.MINT : Ui.TEXT, Ui.LIGHT);
        valueRow.addView(v);
        if (!unit.isEmpty()) {
            TextView u = Ui.text(this, unit, 10.5f, Ui.MUTED, Ui.REG);
            u.setPadding(0, 0, 0, Ui.dp(this, 3));
            valueRow.addView(u);
        }
        card.addView(valueRow);
        card.addView(Ui.spacer(this, 6));
        card.addView(Ui.text(this, label, 10.5f, Ui.DIM, Ui.REG));
        return card;
    }
}
