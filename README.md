# region-selection [![](https://jitpack.io/v/dzenm/region-selection.svg)](https://jitpack.io/#dzenm/region-selection)

这是一个区域选择的工具

<img src="https://github.com/dzenm/region-selection/blob/master/pic/pic.gif?raw=true" width="50%" >

## 下载 （[查看最新版本](https://github.com/dzenm/region-selection/releases/latest)）

```groovy
implementation 'com.github.dzenm:region-selection:1.0.0'
```

## 使用

#### 1. 使用View实现
```xml
<com.dzenm.multi.RegionSelectionView
        android:id="@+id/region_view"
        android:layout_width="match_parent"
        android:layout_height="500dp"
        app:layout_constraintBottom_toBottomOf="parent" />
```
```java
final RegionSelectionView regionView = findViewById(R.id.region_view);
regionView.setOnSelectedListener(new RegionSelectionView.OnSelectedListener() {
    public void onCompleted(RegionBean[] regionBeans) {
        address.setText(getText(regionBeans));
    }
});

```
#### 1. 使用PopupWindow实现
```java
new RegionSelectionDialog.Builder(MainActivity.this)
        .setOnSelectedListener(new RegionSelectionDialog.OnSelectedListener() {
            @Override
            public void onCompleted(PopupWindow popupWindow, RegionBean[] regionBeans) {
                popupWindow.dismiss();
                reselected.setText(getText(regionBeans));
            }
        })
        .create(getWindow().getDecorView());
```

## 下载 [APK](https://github-production-release-asset-2e65be.s3.amazonaws.com/281135592/9f713f80-cad7-11ea-9313-a2a201ae8c1a?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20200720%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20200720T143007Z&X-Amz-Expires=300&X-Amz-Signature=423fa6a5ed5a7a24bd24bb10b6d0092c1457a83b29e49fe0b6a98f1a418c2c06&X-Amz-SignedHeaders=host&actor_id=28523411&repo_id=281135592&response-content-disposition=attachment%3B%20filename%3Dapp-debug.apk&response-content-type=application%2Fvnd.android.package-archive)

