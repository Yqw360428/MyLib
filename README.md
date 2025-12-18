# yqwLibrary

自己封装了一个Contract，起初是因为选择照片或者视频无法修改数量，干脆就源码拉出来自己随便改了一下。

## Installation

Add the dependency in your `build.gradle`:
---

### Step 1: 配置 repositories

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: 添加依赖

```gradle
dependencies {
    implementation 'com.github.Yqw360428:MyLib:v2.0'
}
```

## Usage

注册协定(必须在onCreate之前注册，否则会报错)

```
private val pickLaunch = RegisterUtil.registerPickMedia(this)//照片或图片选择协定

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  
        }
```
在需要选择的地方调用:
pickMediaRequest有三个参数，第一个是默认的PickMediaContract.imageOnly也可以不用传，第二个是数量，默认是1，如果单选也可以不用传。
如果不需要回调也可以直接调用，不注册接口()
```
pickLaunch.launch(RegisterUtil.pickMediaRequest())
```
```
pickLaunch.launch(RegisterUtil.pickMediaRequest(PickMediaContract.imageOnly,5))
```
```
  pickLaunch.launch(RegisterUtil.pickMediaRequest(PickMediaContract.imageOnly,object : ResultCallback{
                override fun onUriResult(uriList: List<Uri>) {
                    super.onUriResult(uriList)
                    Log.e(TAG, "registerPickUri:$uriList")
                }
                override fun onPathResult(pathList: List<String>) {
                    super.onPathResult(pathList)
                    Log.e(TAG, "registerPickPath:$pathList")
                }
            }))
```
各协定对应的回调接口：
1. 选择照片或者视频
```
override fun onUriResult(uriList : List<Uri>){}
override fun onPathResult(pathList : List<String>){}
```
2. 请求权限
```
override fun onPermissionAgree(permissions : List<String>){}
override fun onPermissionDeny(permissions : List<String>){}
override fun onPermissionResult(result : Boolean){}
```
3. 带参回传
```
override fun onActivityResult(result : ActivityResult){}
```
4. 拍照,录制视频
```
override fun onUriListener(uri : Uri?){}
``` 
5. 选择联系人
``` 
override fun onPickContact(name : String,numberList : List<String>){}
``` 
6. 获取文件
```
override fun onUriResult(uriList : List<Uri>){}
```
### 其他的用法demo当中都有
其中实现了大部分的注册协定，但有一部分我用的也很少就没有封装了。
### 混淆
```
-keep class com.libyqwcon.yqwlibrary.** { ; }
```
欢迎各位大神指教
