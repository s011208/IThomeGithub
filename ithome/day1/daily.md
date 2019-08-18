# Day 1 Kotlin + buildSrc 管理 libraries

## 創建 buildSrc 資料夾
## 創建 buildSrc/build.gradle.kts 
```
plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}
``` 
## 創建 buildSrc/src/main/java/Dependencies.kt

接下來在 Dependencies.kt 內把 app/build.gradle 裡面的 dependencies 都移植過來
```
object Versions {

    val appcompat = "1.0.2"
    val core_ktx = "1.0.2"
    val constraintlayout = "1.1.3"
    val junit = "4.12"
    val test_runner = "1.2.0"
    val espresso_core = "3.2.0"
}

object Libs {
    val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    val junit = "junit:junit:${Versions.junit}"
    val test_runner = "androidx.test:runner:${Versions.test_runner}"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"
}
```

然後按下 gradle sync

## 使用 Libs

這樣即可在 app/build.gradle 使用 Dependencies.kt 內宣告的 Libs

```
implementation Libs.appcompat
implementation Libs.core_ktx
implementation Libs.constraintlayout
testImplementation Libs.junit
androidTestImplementation Libs.test_runner
androidTestImplementation Libs.espresso_core
```

相較於使用 ext 的方式管理，使用 buildSrc 的方式也可以是另一個選項唷。

## 資料
https://www.youtube.com/watch?v=sQC9-Rj2yLI&t=410s
