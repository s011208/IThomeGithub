# mvrx

mvrx 是 airbnb 的一套 android 開源框架，可以參考下方連結
```
https://github.com/airbnb/MvRx
```

## 主要物件

### BaseMvRxActivity

讓 activity 繼承 BaseMvRxActivity，並且直接 replace fragment，BaseMvRxActivity 會幫忙處理之後 life cycle 的問題。

### BaseMvRxFragment

讓 fragment 繼承 BaseMvRxFragment，之後如果有任何 state 更新， view model 會呼叫 fragment 的方法 invalidate。

### MvRxState

State 是一個存在於 view model 內的狀態，view 會依照 state 的內容做更新。

### MvRxViewModel

跟 fragment bind 在一起的主要物件。


## 簡單介紹

我們將透過一個簡單的登入頁面來展現 view model & state 如何與 view (fragment) 互動。  

新增兩個 EditText (分別為 password & user name) 與一個 Sign in Button，Button 預設 `enable = false`，
當 password & user name 內都有文字的時候，才會讓 Button 變成 enable。

fragment_log_in.xml
```xml
    <LinearLayout>
        <EditText
                android:id="@+id/userName"
                android:inputType="text"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
    
        <EditText
                android:id="@+id/password"
                android:inputType="textPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
    
        <Button
                android:text="@string/fragment_log_in_button"
                android:id="@+id/logIn"
                android:enabled="false"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
    </LinearLayout>
```

LogInFragment
```kotlin

        // 讓 view model 做邏輯判斷並且改變狀態
        compositeDisposable += Observable.combineLatest(
            userName.textChanges(),
            password.textChanges(),
            BiFunction<CharSequence, CharSequence, Boolean> { userName, password ->
                viewModel.validateUserNameAndPassword(
                    userName,
                    password
                )
            }
        ).subscribe()
        
        
        
        // 透過 withState 的方法，可以把 view model 內的狀態取出，並且依照狀態修改 view 的內容，只要狀態有變，view model 就會呼叫 invalidate 方法 
        override fun invalidate() {
            withState(viewModel, { state ->
                logIn.isEnabled = state.isLogInEnable
            })
        }
```

LogInFragmentState
```kotlin
data class LogInFragmentState(val isLogInEnable: Boolean = false) : MvRxState
```

LogInViewModel

由於每次修改都需要產生出一個新的 state 物件，這邊我們使用 copy 的方法把 state 設定到 view model
```kotlin
fun validateUserNameAndPassword(userName: CharSequence, password: CharSequence): Boolean {
        return (userName.isNotBlank() && password.isNotBlank()).also { result -> setState { copy(isLogInEnable = result) } }
    }
```

最後運行 app，我們就可以看到 Log in Button 的狀態會隨著 user name & password 更改。
