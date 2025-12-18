# yqwLibrary
自己封装了一个Contract，起初是因为选择照片或者视频无法修改数量，干脆就源码拉出来自己随便改了一下。

## Installation

### Gradle

Add the dependency in your `build.gradle`:
---
```gradle
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
	dependencies {
	        implementation 'com.github.Yqw360428:MyLib:v1.0'
	}
```	

## Usage