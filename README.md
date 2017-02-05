### View-Effect

<a href="https://imgflip.com/gif/1izppg"><img src="https://i.imgflip.com/1izppg.gif" title="made at imgflip.com"/></a>

### Step 1. Add the JitPack repository to your build file
```jitpack
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```
### Step 2. Add the dependency
```
dependencies {
	   compile 'com.github.zhekeen:View-Effect:1.0.0'
}
```
### Custom .xml
```
<keen.lib.ViewEffect
            android:id="@+id/img1"
            android:src="@drawable/img1"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```
