### 项目模块说明
- 启动 : `...\business\launcher`
	- 启动页 : LauncherActivity
	- 引导页 : GuideActivity
- 登录页 : `...\business\login`
	- 登录 : LoginActivity
	- 忘记密码 : ForgetPasswordActivity
- 主页面 : `...\business\dashboard`
	- 生意概况 : KpiFragment
	- 报表 : ReportFragment
	- 工具箱 : WorkBoxFragment
		- 独立的第三方工具页面 : WebApplication
	- 我的 : MineFragment
		- 公告预警 : AnnouncementWarningFragment
		- 数据学院 : DataCollegeFragment
		- 个人信息 : UserFragment
			- 文章收藏 : FavoriteArticleActivity
			- 问题反馈 : FeedbackActivity
			- 修改密码 : AlterPasswordActivity
- 模版 ： `...\business\subject`
	- 模板一 （原生信息图表模板） : TemplateOneActivity
		- 模板九（添加信息采集功能的模板一） : TemplateNineActivity
	- 模板二 (H5信息图表模板) : SubjectActivity
		- 模板四 (无群组权限限制的模板二) : WebApplicationActivity/WebApplicationActivityV6
	- 模板三 (多指标信息图表模板) : TemplateThreeActivity
	- 模板五 (超级表格) : TemplateFiveActivity
- 扫一扫 : `...\business\scanner`
	- 扫码页面 : BarCodeScannerActivity

### syp 模版配置说明
1. [GitHub syp-template 分支](https://github.com/PassionateWsj/YH-Android/)
2. 项目包名修改
 	- 文件夹重命名 : `../app/src/main/java/com/intfocus/{syp-template}`
 	- 包名修改 : 
 		- 文件 : `../app/src/main/AndroidManifest.xml`
 		- 搜素关键字 : **package**
 		- 重命名 : `com.intfocus.{template}`
	- 应用在系统中的包名修改
		- 文件 : `../app/build.gradle`
		- 搜素关键字 : **applicationId**
		- 重命名 : `com.intfocus.{template}`
3. App 名称配置
	- 文件路径 : `..\app\src\main\res\values\strings.xml`
	- 搜索 **app_name** 关键字
	- 替换 `永辉生意人` 为所需 **app** 的名称
4. 功能配置
	- 文件 ： `..\app\src\main\java\com\intfocus\{spy_template}\general\constant\{ConfigConstants.kt}`
	- 文件列表
		- 生意+ (默认) : `ConfigConstants.kt`
		- 睿商 : `RSConfigConstants.kt`
		- 数据通 : `SJTConfigConstants.kt`
		- 永辉生意人 : `YHConfigConstants.kt`
		- 永辉测试 : `YHTestConfigConstants.kt`
		- 永辉开发 : `YHDevConfigConstants.kt`
	- 配置单（**末尾带 * 为默认设置**）
        1. 启动页更新资源开关

                说明 : 展示模板二报表的必备组件，如果使用模板二报表，必选 true，如果不需要使用模板二，可以 false 免去每次进入应用的资源更新，加快启动速度
                true : 启动页有资源更新 *
                false : 启动页无资源更新，启动后停留 2s 进入下一页面

        2. 启动页底部广告条

                说明 : 启动页面 底部广告显示功能，不做详细说明
                true : 显示
                false : 不显示

        3. 第一次登录是否展示 使用 Guide 说明页面

                说明 : 需要展示的话，需要提供第一次登陆时 Guide 说明页面的图片，推荐 3 张 16 : 9 的图片
                true : 展示 *
                false : 不展示


        4. 是否显示登陆页底部 忘记密码 | 申请注册

                说明 : 根据业务需求选择
                true : 显示 *
                false : 不显示


        5. 在报表页面结束应用，再次登录时是否自动跳转上次报表页面

                说明 : 在报表页面结束应用后，下次登陆时会默认跳转到结束应用的报表页面
                true : 跳转
                false : 不跳转 *


        6. 是否保存上次退出时停留的主页签

                说明 : 上次退出时停留在主页的底部的报表选项页面，下次登陆时默认会先展示报表页签
                true : 跳转
                false : 每次登录默认显示第一个页签 *


        7. 是否提供扫码功能 (Kpi 概况页签)

                说明 : 概况页签 右上角的扫码功能开关
                true : 提供 *
                false : 不提供


        8. 是否提供扫码功能 (Report 报表页签)

                说明 : 报表页签 右上角的扫码功能开关
                true : 提供 *
                false : 不提供


        9. 是否提供扫码功能 (WorkBox 工具箱页签)

                说明 : 工具箱页签 右上角的扫码功能开关
                true : 提供
                false : 不提供 *


        10. 扫一扫 条码/二维码 开关

                说明 : 条码都支持，二维码扫码功能可选择性开启或关闭，关闭二维码功能时，手动输入只可输入数字
                true : 不支持二维码 *
                false : 支持二维码


        11. 是否支持 相册扫码 功能

                说明 : 扫码界面，右上角 相册 按钮显示与否
                true : 支持 *
                false : 不支持


        12. 是否支持 扫码定位显示

                说明 : 扫码界面，定位信息显示与否
                true : 支持 *
                false : 不支持


        13. 帐号输入是否只支持数字键盘

                说明 : 登陆时账号输入文本选择
                true : 数字输入键盘 *
                false : 文本键盘


        14. 主页面显示 Kpi 概况页签

                说明 : 略
                true : 显示 *
                false : 不显示


        15. 显示 Report 报表页签

                说明 : 略
                true : 显示 *
                false : 不显示


        16. 显示 WorkBox 工具箱页签

                说明 : 略
                true : 显示 *
                false : 不显示


        17. 我的页面是否只显示 个人信息 一个页签

                说明 : 如果 false ，在主界面 我的 页签中会展示 公告预警、数据学院、个人信息 三个页签
                true : 1 个 *
                false : 3 个


        18. 我的页面是否自定义

                说明 :
                true : 用户自定义添加界面 *
                false : 显示用户基本信息（）


        19. 归属部门是否有详情页面

                说明 : 如果支持跳转，现功能需要提供三方页面
                true : 可点击跳转
                false : 不可点击 *


        20. 头像是否支持点击上传

                说明 : 我的页面，点击头像可以打开本地相册选择图片上传新的头像
                true : 支持 *
                false : 不支持


        21. 是否允许主页面4个页签 滑动切换

                说明 : 设置为 true 时，在主界面可以左右滑动来切换 概况/报表/工具箱/我的 页签
                true : 允许滑动
                false : 不允许滑动 *
5. 资源替换
	- **启动页面**
		- logo 替换
			- 推荐比例 : **1:1**
			- 推荐分辨率 : **350 * 350** 、 **550 * 550**
			- 将 logo 图片重命名为 `startup_logo.png`
			- 分别在路径下替换当前的图片
				- **350 * 350** : `..\app\src\main\res\drawable-xhdpi\startup_logo.png` 
				- **550 * 550** : `..\app\src\main\res\drawable-xxhdpi\startup_logo.png`
		- 背景图片替换
			- 推荐比例 : **16 : 9**
			- 命名 : bg_startup.png
			- 替换 : `..\app\src\main\res\drawable\bg_startup.png`
		- 底部广告替换
			- 图片推荐 : **168 * 13.7**、**392 * 32**、**588 * 48**
			- 命名 : `slogan.png`
			- 替换 :
				- **168 * 13.7** : `..\app\src\main\res\drawable-hdpi\slogan.png`
				- **392 * 32** : `..\app\src\main\res\drawable-xhdpi\slogan.png`
				- **588 * 48** :`..\app\src\main\res\drawable-xxhdpi\slogan.png`
	- **登录页**
		- logo 替换
			- 推荐分辨率 : **86 * 86** 、 **200 * 200** 、**300 * 300**
			- 命名 : `logo.png`
			- 替换
				- **86 * 86** ： `..\app\src\main\res\drawable-hdpi\logo.png`
				- **200 * 200** ： `..\app\src\main\res\drawable-xhdpi\logo.png`
				- **300 * 300** ： `..\app\src\main\res\drawable-xxhdpi\logo.png`
		- 登录帐号提示语
			- 路径 ： `..\app\src\main\res\values\strings.xml`
			- 搜索关键词替换
				- 员工号 ： `hint_login_user`
	- **主页**
		- 页签名称 替换
			- 文件路径 : `..\app\src\main\res\values\strings.xml`
			- 搜索关键字并修改
				- 生意概况 : `text_kpi`
				- 报表 : `text_report`
				- 工具箱 : `text_workbox`
				- 我的 : `text_mine`
6. 三方 sdk 配置
	- **高德定位** 功能
		- 文件 : `..\app\src\main\AndroidManifest.xml`
		- 搜索关键字 : `com.amap.api.v2.apikey`
			- 替换 **value** 的值为当前应用的 高德地图 **APIKey**
	- **蒲公英** 平台发布
		- 文件 : `..\app\src\main\AndroidManifest.xml`
		- 搜索关键字 : `PGYER_APPID`
			- 替换 **value** 的值为当前应用的 蒲公英平台提供的 **APPID**
	- **友盟推送**
		- 文件 : `..\app\src\main\AndroidManifest.xml`
		- 搜索关键字 : `UMENG_APPKEY`
			- 替换 **value** 的值为当前应用的 蒲公英平台提供的 **APPKEY**
		- 搜索关键字 : `UMENG_MESSAGE_SECRET`
			- 替换 **value** 的值为当前应用的 蒲公英平台提供的 **SECRET**
