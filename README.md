# 晕船适应（MotionFit）

给容易晕船的人做的前庭适应训练 App。四组短时训练，每天几分钟，目标是让「身体没动、视野在动」这件事不再引起不适。

> 这是行为训练工具，不是医疗器械，不做任何诊断或治疗承诺。真的晕得厉害请找医生。

## 四组训练

| # | 训练 | 做什么 |
|---|---|---|
| 01 | 视动适应 | 条纹持续漂移，盯住中心十字，用余光感受流动，换向时点屏幕 |
| 02 | 平滑追踪 | 手指按住光点跟随移动，全程不脱离 |
| 03 | 快速扫视 | 光点随机出现，尽快点中，超时自动换下一个 |
| 04 | 头眼协同 | 举着手机、眼睛盯住中心圆点，按提示方向缓慢左右转头 |

## 其他

- **四周计划**：W1 只做 01，W2 加 02，W3 加 03，W4 四组全做（约 5 分钟）；按「少量、每天、不难受」的节奏推进
- **记录**：连续天数、累计次数、当前等级、各项最好成绩
- **纯本地**：数据存 SharedPreferences，无网络权限、无账号
- **全自绘**：所有训练界面用 Canvas 手绘，不依赖图片资源

## 结构

```
app/src/main/java/com/zach/motionfit/MainActivity.java      首页 / 计划 / 记录
app/src/main/java/com/zach/motionfit/ExerciseActivity.java  训练执行与计时
app/src/main/java/com/zach/motionfit/ex/*.java              四组训练的绘制与判定
app/src/main/java/com/zach/motionfit/{Ui,Glyph,Store,ProgressRing,Backdrop}.java
```

## 安装

`apk/MotionFit-1.1.0.apk`（另附 1.0.0 首版）。Android 7.0+（minSdk 24，targetSdk 34）。

## 构建

Android Studio 打开根目录（Gradle，compileSdk 34，minSdk 24）。签名自备，仓库不含 keystore。

## 许可

MIT
