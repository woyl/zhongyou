### 项目框架搭建(2.3.4版本后)
1. 2.3.5后使用的是[MVPArms](https://github.com/JessYanCoding/MVPArms "MVPArms")搭建完成，请仔细的看[wiki](https://github.com/JessYanCoding/MVPArms/wiki "wiki"),注意项目目录下config文件夹中的类，里面是关于application,activity,fragment,http请求的配置，如果对项目不是很熟，尽量不要动。andoird 10已经适配。

2. 对于项目中的rxjava的使用，如果上游产生的数据量比较多，请不要使用rxjava，使用广播，在IM中我使用Rxjava,当接受到消息过多，Rxjava进行分发的时候，会报错。

3. 本项目的屏幕适配使用的是[AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize "AndroidAutoSize")，但是对于某些对话框Dialog会适配不正确，屏幕要自动旋转也会不正确，只能固定屏幕方向

4. 对于参会人主持人的右侧小窗口要等比放大的话，第一步：需要动态的设置recycleview的宽(高不用)，第二步，去NewAudienceVideoAdapter类的onBindViewHolder方法中去根据itemCount数量来动态设置高度，**MyGridLayoutHelper不变**

		 if (mLayoutHelper instanceof MyGridLayoutHelper) {
    			switch (getItemCount()) {
    				case 0:
    				case 1:
    				case 2:
    				case 3:
    					mLayoutParams.width = DisplayUtil.dip2px(context, 240);
    					mLayoutParams.height = getHeight(context) / 3;
    					break;
    				case 4:
    				case 5:
    				case 6:
    				case 7:
    				case 8:
    					mLayoutParams.width = DisplayUtil.dip2px(context, 80);
    					mLayoutParams.height = DisplayUtil.dip2px(context, 60);
    					break;
    			}
    		}
5.  会议里面参会人的小窗口是用recyclerview来添加的，因为他们要求的添加和展示顺序有关系，所以使用[vlayout](https://github.com/alibaba/vlayout/blob/master/README-ch.md "vlayout")来管理item，希望你能熟悉一下使用，其中修改了GridLayoutHelper的源码，主要是MyGridLayoutHelper和SpliteGridLayoutHelper，MyGridLayoutHelper主要是管理小窗口的，SpliteGridLayoutHelper主要是来管理均分模式下的窗口，具体的改动请查看代码。

6. 本项目页面的搭建使用的模版[MVPArmsTemplate](https://github.com/JessYanCoding/MVPArmsTemplate "MVPArmsTemplate")，我使用的是自己修改的，主要是添加我自己使用的网络框架，已经上传到公司的服务器上[git](http://192.168.1.83:3000/luopan/MVPArmsTemplate "git")，如果你的Andorid Studio版本不是Android Studio 3.6.*的，请下载最新的模版参照修改。

7. 此项目的键值对的存贮SharedPreferences已被替换成了MMKV。

8 . 电视上问题
		 可以使用android:nextFocusDown、nextFocusUp等去设置，但是下一个控件必要要开启android:focusable="true",对于有条件进行隐藏或者展示的控件，这个方法不适用，只能进行代码控制，具体请查看代码
  		电视上的视频播放，proguard-rules.pro这个文件尽量别去删除，可以新增，否则可能会导致播放不了视频
  		电视盒子的adb端口是：7896


~~这个项目的除了会议里面是全部重写，会议里面的改动也比较多，但是不好改动，我曾经动了无数次的想法去重构，但是真重构不动，改到一半又来新需求，而且会议里面的逻辑处理太多了，重构了还要花费很多时间进行测试，反正这个项目的坑很多，后台接口的坑也多。好自为之吧。~~













