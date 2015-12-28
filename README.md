# Android界面优化培训

工作几年，遇到需要页面优化的地方不少，在刚开始学写代码的时候大多数人只为了方便实现，而很少考虑效率问题，等到发现问题的时候回来修改，就会变得十分麻烦了。

该文总结了Google Io大会的一个效率方面的培训，和我自己的一些经验。

## 16毫秒原则
![ui screenshot](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/optimizeUi/optimize_16ms.png)
Android系统每隔**16ms**发出**VSYNC**信号，触发对UI进行渲染， 如果每次渲染都成功，这样就能够达到流畅的画面所需要的**60fps**，为了能够实现**60fps**，这意味着程序的大多数操作都必须在**16ms**内完成。如图所示，如果你的操作都是在16ms内完成的，那么你的页面就是流畅的。

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_16ms2.png?raw=true)

看上图，如果你的某个操作花费时间是**24ms**，系统在得到**VSYNC**信号的时候就无法进行正常渲染，这样就发生了丢帧现象。那么用户在**32ms**内看到的会是同一帧画面。

## 什么原因会导致丢帧？
上面说了产生卡顿的原理，但是实际工作中，我们有哪些原因会引起这种问题呢：

* 过度绘制：Layout 太复杂，UI重叠太多
* 内存问题：GC频繁 内存泄露
* 多线程处理不当，互锁产生

### 过度绘制
**Overdraw**(过度绘制) 描述的是屏幕上的某个像素在同一帧的时间内被绘制了多次。在多层次的UI结构里面，如果不可见的UI也在做绘制的操作，这就会导致某些像素区域被绘制了多次。这就浪费大量的CPU以及GPU资源。见下图：

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_overdraw.png?raw=true)

#### 调试OverDraw的办法
方法1：Android开发者选项中打开***调试GPU过度绘制***，页面上就会显示出不同的颜色。

- 淡蓝色  表示只有一层 这是最好的情况
- 浅绿色  表示有二层覆盖 这也还好
- 淡红色  表示三层覆盖 我认为这种情况在实际工作中，还是很常见的
- 深红色  表示4层以上的覆盖 这就比较严重了 需要自己研究下布局

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_overdraw2.png?raw=true)

方法二：DDMS 中有一个工具叫作：**Hierarchy View** 这个工具就可以看出具体的布局了，你可以通过该工具分析具体的布局深度，但是该工具需要root手机，如果你的手机没有root，那么就用模拟器。下图是我的一个截图，大概就是这样子。

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_hierarchy.png?raw=true)

#### 常用的一些Android布局标签
我们还可以用一些Android提供的布局标签去优化我们的布局，有三个：

1. ***Mergy***
2. ***ViewStup***
3. ***Include***

##### Mergy
Mergy标签的主要目的就是用来减少层级使用的，具体可以参考<http://android-developers.blogspot.jp/2009/03/android-layout-tricks-3-optimize-by.html>

#### ViewStup
ViewStup标签最大的优点是当你需要时才会加载，使用他并不会影响UI初始化时的性能。各种不常用的布局想进度条、显示错误消息等可以使用标签，以减少内存使用量，加快渲染速度。具体可以参考<http://android-developers.blogspot.jp/2009/03/android-layout-tricks-3-optimize-with.html>

#### Include
Include就是可以重用布局，具体对层级没啥优化效果，只是可以减少一部分重复代码。

我写了Mergy和ViewStup的一个例子放在我的GitHub上，大家可以使用Hierarchy View 去观察布局的变化。 [Samples->](https://github.com/lorcanluo/trainingOptimizingUI)

### 内存问题-GC
虽然Android有自动管理内存的机制，但是对内存的不恰当使用仍然容易引起严重的性能问题。***在同一帧里面创建过多的对象是件需要特别引起注意的事情。***

Android系统里面有一个Generational Heap Memory的模型，系统会根据内存中不同 的内存数据类型分别执行不同的GC操作。例如，最近刚分配的对象会放在Young Generation区域，这个区域的对象通常都是会快速被创建并且很快被销毁回收的，同时这个区域的GC操作速度也是比Old Generation区域的GC操作速度更快的。模型如图：

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_memory.png?raw=true)

#### 为什么GC会引起卡顿了？

除了速度差异之外，执行GC操作的时候，***任何线程的任何操作都会需要暂停，等待GC操作完成之后，其他操作才能够继续运行***。
通常来说，单个的GC并不会占用太多时间，但是大量不停的GC操作则会显著占用帧间隔时间(16ms)。***如果在帧间隔时间里面做了过多的GC操作，那么自然其他类似计算，渲染等操作的可用时间就变得少了。***

##### 导致GC频繁的原因：
**Memory Churn内存抖动**，内存抖动是因为大量的对象被创建又在短时间内马上被释放。

瞬间产生大量的对象会严重占用Young Generation的内存区域，当达到阀值，剩余空间不够的时候，也会触发GC。即使每次分配的对象占用了很少的内存，但是他们叠加在一起会增加 Heap的压力，从而触发更多其他类型的GC。这个操作有可能会影响到帧率，并使得用户感知到性能问题。

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_memory2.png?raw=true)

通过Android提供的[Memory Monitor](https://developer.android.com/tools/performance/memory-monitor/index.html)工具就可以观察到这一现象，大概如下图：

![ui screenshot](https://github.com/lorcanluo/myHugoProject/blob/master/myImage/optimizeUi/optimize_memory3.png?raw=true)

如果你看到图中的现象那么你就需要注意查看你得代码是否有问题了。

#### 内存泄露

**内存泄漏指的是那些程序不再使用的对象无法被GC识别，这样就导致这个对象一直留在内存当中，占用了宝贵的内存空间**。显然，这还使得每级Generation的内存区域可用空间变小，GC就会更容易被触发，从而引起性能问题。

Android也提供了工具去分析这些情况：

1. DDMS中的 Heap Tool  
2. Allocation tracker

你还可以使用***MAT***去分析你得代码等等，具体内存分析的问题下一次再来讨论。

