# Retrofit 讀取 github 使用者資訊

## 新增 repository module
新增一個 repository module 專門來處理網路請求相關的事情，來減少跟主要 app module 的相依性。

## 新增 github 登入 api

我們使用 https://api.github.com/user 當例子 (可以參考 https://developer.github.com/v3/oauth_authorizations/)

### 新增 interface
首先我們要新增一個 interface 來獲取 github api
```kotlin
interface GithubApi
```
內容也不會太複雜，使用 Single 當回傳型態，`/user` 是對應到 `https://api.github.com/user` 最後面的路徑
```kotlin
        @GET("/user")
        fun user(): Single<UserEntity>
```

### Api 回傳物件 UserEntity
```kotlin
data class UserEntity(
    @field:[SerializedName("login")]
    val login: String = "",
    @field:[SerializedName("id")]
    val id: String = "",
    @field:[SerializedName("name")]
    val name: String = ""
)
```


### 建立 retrofit
```kotlin
        Retrofit
                .Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create()) // 會用 gson library 來處理 json 格式轉換
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 可以使用 rxjava 處理回傳的型態
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(BasicAuthInterceptor(userName, password))
                        .build()
                )
                .build()
                .create(GithubApi::class.java)
```

這邊比較特別的是有另外寫一個 class BasicAuthInterceptor 來處理 github Basic Auth，可以參考 `BasicAuthInterceptor.kt`，內容也沒什麼特別的，主要是在之後每個 request 都帶入用戶帳號資訊。


### 使用 GithubRepository

接著我們先把 LogInViewModel 內的 LogInFragmentState 新增一個狀態 
```kotlin
val logInResult: Async<UserEntity> = Uninitialized
```

在 mvrx 內，Async 這個型態可以幫助處理一些非同步的資料，Async 有下面幾種

1. Uninitialized
2. Loading
3. Fail
4. Success

這四種型態可以更方便我們管理 view 的狀態，比如說一開始還沒初始化前可以預設成 Uninitialized，一但我們調用了 execute， mvrx 就會拋出一個 Loading 狀態，之後根據結果會變成 Fail 或是 Success，
Fail 裡面會帶著一個 throwable 來告知錯誤訊息，Success 裡面會有回傳的物件。

所以我們在 view model 內可以這樣用
```kotlin
repository.logIn(userName = userName.toString(), password = password.toString())
            .subscribeOn(Schedulers.io())
            .execute {
                copy(logInResult = it)
            }
```

如此一來就會調用 `https://api.github.com/user` api，並且把結果傳回 execute 內的 `it` ，在這邊可以試著加上 log ，mvrx 會先產生一個 Loading 狀態，之後會有一個 Success(帳密有效) 或是 Fail (帳密無效)

### mvrx asyncSubscribe
在昨天有提到，mvrx 所有的狀態更新都會通知 view.invalidate()，在這邊我們示範另一種主動監聽的方法如下
```kotlin
viewModel.asyncSubscribe()
```

這個方法可以監聽 view model 內 Async 狀態的改變，在這邊只要登入成功或是失敗會顯示不同的 Toast，另外我們把 progress bar 的狀態放在 invalidate 內改變。