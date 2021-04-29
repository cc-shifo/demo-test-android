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