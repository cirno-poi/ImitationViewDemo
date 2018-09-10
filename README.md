# AnimateViewDemo
自定义view仿写一些coooool动画效果的demo

## 动画1————仿即刻点赞效果

- 重写了onTouchEvent()，保留onClick()供外部使用;
- 做了边界值处理；
- 内容测量尚未完成；
- 圆圈效果尚未完成，发散效果不完善。


![Jike_screenshot](https://github.com/cirno-poi/ImitationViewDemo/raw/master/picture/Jike_screenshot.gif)

## 动画2————仿Flipboard翻页动画（加强版）

- 参考了HenCoder的仿写比赛的实现，做了微调；
- 重新计算canvas的裁切范围，先进行裁切再进行旋转。


![Flipboard_screenshot](https://raw.githubusercontent.com/cirno-poi/ImitationViewDemo/master/picture/Flipboard_screenshot.gif)
