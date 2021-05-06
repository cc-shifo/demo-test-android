application to module
* change plugins id in module build.gradle from com.android.application to com.android.library
* hide Field applicationId of closure defaultConfig in module build.gradle
* remove app:name of <application> in module AndroidManifest.xml
* the main application and all modules which have used Arouter feature should include dependency
  Arouter.
* There is no need to declare <action> for target activity in the module AndroidManifest.xml
1.修改模块的build.gradle，将代表此模块是application插件改成代表library的插件。
2.修改模块的build.gradle，隐藏android闭包下的defaultConfig闭包里的applicationId。library方式的模块不需要
  application id
3.如果模块的有自定义application，并且在AndroidManifest.xml的<application>添加过app:name属性。必须移除
  app:name属性.
4.对于要跳转的目标activity，不一定非要(可要可不要)在模块AndroidManifest.xml中给该activity声明action。
5.只要是用到了Arouter，不论是宿主Application类型的模块，还是其他任意的library类型的模块，这些模块都要添加
  Arouter的依赖。
6.使用startActivity直接启动其他模块的activity所传递参数，包名使用目标activity所在app的applicationId(
  在build.gradle文件里), 类名使用目标activity文件开头定义的package的路径加上.和目标activity.用法例子见
  PlayAudioActivity和QRCPayActivity的initData()方法.


二、插拔模块
https://huangxiaoguo.blog.csdn.net/article/details/78753555?utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&dist_request_id=1619680678359_34742&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control
1.项目根目录gradle.properties配置：
# 是否需要单独编译 true表示不需要，false表示需要
isNeedHomeModule=true
#isNeedHomeModule=false
isNeedChatModule=true
#isNeedChatModule=false
isNeedRecomModule=true
#isNeedRecomModule=false
isNeedMeModule=true
#isNeedMeModule=false
2.在各个子模块中配置（例如module_me）：
if (!isNeedMeModule.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
defaultConfig {
        if (!isNeedMeModule.toBoolean()) {
            applicationId "tsou.cn.module_me"
        }
 }
3.在app主模块中：
if (isNeedHomeModule.toBoolean()) {
        compile project(':module_home')
    }
    if (isNeedChatModule.toBoolean()) {
        compile project(':module_chat')
    }
    if (isNeedRecomModule.toBoolean()) {
        compile project(':module_recom')
    }
    if (isNeedMeModule.toBoolean()) {
        compile project(':module_me')
    }
