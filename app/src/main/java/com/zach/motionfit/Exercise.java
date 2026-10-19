package com.zach.motionfit;

/** 四组训练的定义。 */
public class Exercise {

    public static final String MODE_OKTO = "okto";
    public static final String MODE_PURSUIT = "pursuit";
    public static final String MODE_SACCADE = "saccade";
    public static final String MODE_HEADTURN = "headturn";

    public final String mode;
    public final String index;
    public final String name;
    public final String summary;
    public final String howto;
    public final int seconds;

    public Exercise(String mode, String index, String name, String summary, String howto, int seconds) {
        this.mode = mode;
        this.index = index;
        this.name = name;
        this.summary = summary;
        this.howto = howto;
        this.seconds = seconds;
    }

    public static Exercise[] all() {
        return new Exercise[]{
                new Exercise(MODE_OKTO, "01", "视动适应", "条纹漂移 · 90 秒",
                        "盯住屏幕中心的十字，用余光感受条纹的流动。条纹换向时，点一下屏幕。", 60),
                new Exercise(MODE_PURSUIT, "02", "平滑追踪", "手指跟随 · 60 秒",
                        "把手指按在光点上，跟着它一起移动，全程不要离开它。", 45),
                new Exercise(MODE_SACCADE, "03", "快速扫视", "抓点反应 · 14 个目标",
                        "光点出现后尽快点中它，超时会自动消失并换下一个。", 30),
                new Exercise(MODE_HEADTURN, "04", "头眼协同", "缓慢转头 · 90 秒",
                        "举着手机，眼睛盯住中心圆点，按提示的方向缓慢左右转头。", 60)
        };
    }

    public static Exercise byMode(String mode) {
        for (Exercise e : all()) {
            if (e.mode.equals(mode)) return e;
        }
        return all()[0];
    }

    public static String nameOf(String mode) {
        return byMode(mode).name;
    }
}
