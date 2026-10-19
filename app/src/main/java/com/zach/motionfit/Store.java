package com.zach.motionfit;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** 训练记录与进度，本地 SharedPreferences 存储。 */
public class Store {

    public static class Record {
        public final long ts;
        public final String mode;
        public final int score;
        public final int seconds;

        public Record(long ts, String mode, int score, int seconds) {
            this.ts = ts;
            this.mode = mode;
            this.score = score;
            this.seconds = seconds;
        }
    }

    private static final String PREFS = "motionfit";
    private static final String KEY_LOG = "log";
    private static final String KEY_TODAY = "today";
    private static final String KEY_TODAY_COUNT = "today_count";
    private static final String KEY_STREAK = "streak";

    private final SharedPreferences sp;

    public Store(Context c) {
        sp = c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date());
    }

    public void addSession(String mode, int score, int seconds) {
        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(sp.getString(KEY_LOG, ""));
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') sb.append('\n');
        sb.append(now).append(',').append(mode).append(',').append(score).append(',').append(seconds);
        sp.edit().putString(KEY_LOG, trim(sb.toString())).apply();

        String t = today();
        String storedDay = sp.getString(KEY_TODAY, "");
        int count = storedDay.equals(t) ? sp.getInt(KEY_TODAY_COUNT, 0) : 0;
        int streak = sp.getInt(KEY_STREAK, 0);
        if (!storedDay.equals(t)) {
            String yesterday = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                    .format(new Date(System.currentTimeMillis() - 86400000L));
            streak = storedDay.equals(yesterday) ? streak + 1 : 1;
        } else if (streak == 0) {
            streak = 1;
        }
        sp.edit()
                .putString(KEY_TODAY, t)
                .putInt(KEY_TODAY_COUNT, count + 1)
                .putInt(KEY_STREAK, streak)
                .putInt("best_" + mode, Math.max(score, sp.getInt("best_" + mode, 0)))
                .apply();
    }

    private static String trim(String log) {
        String[] lines = log.split("\n");
        if (lines.length <= 200) return log;
        StringBuilder sb = new StringBuilder();
        for (int i = lines.length - 200; i < lines.length; i++) {
            sb.append(lines[i]).append('\n');
        }
        return sb.toString();
    }

    public int best(String mode) {
        return sp.getInt("best_" + mode, 0);
    }

    public int todayCount() {
        return sp.getString(KEY_TODAY, "").equals(today()) ? sp.getInt(KEY_TODAY_COUNT, 0) : 0;
    }

    public int streak() {
        return sp.getInt(KEY_STREAK, 0);
    }

    public int totalSessions() {
        String log = sp.getString(KEY_LOG, "").trim();
        return log.isEmpty() ? 0 : log.split("\n").length;
    }

    /** 1~3 级：每完成 4 次训练升一级，影响条纹速度与目标节奏。 */
    public int level() {
        return Math.min(3, 1 + totalSessions() / 4);
    }

    public List<Record> recent(int limit) {
        List<Record> out = new ArrayList<>();
        String log = sp.getString(KEY_LOG, "").trim();
        if (log.isEmpty()) return out;
        String[] lines = log.split("\n");
        for (int i = lines.length - 1; i >= 0 && out.size() < limit; i--) {
            String[] f = lines[i].split(",");
            if (f.length != 4) continue;
            try {
                out.add(new Record(Long.parseLong(f[0]), f[1], Integer.parseInt(f[2]), Integer.parseInt(f[3])));
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }
}
