package yhh.ithome.github.fragment.login.vm

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yhh.ithome.github.external.mvrx.MvRxViewModel
import yhh.ithome.repository.github.GithubRepository
import yhh.ithome.repository.github.entity.UserEntity

data class LogInFragmentState(val isLogInEnable: Boolean = false,
                              val logInResult: Async<UserEntity> = Uninitialized) : MvRxState

class LogInViewModel(initialState: LogInFragmentState) : MvRxViewModel<LogInFragmentState>(initialState) {

    private val compositeDisposable = CompositeDisposable()

    private val repository = GithubRepository()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun validateUserNameAndPassword(userName: CharSequence, password: CharSequence): Boolean {
        return (userName.isNotBlank() && password.isNotBlank()).also { result -> setState { copy(isLogInEnable = result) } }
    }

    fun logIn(userName: CharSequence, password: CharSequence) {
        repository.logIn(userName = userName.toString(), password = password.toString())
            .subscribeOn(Schedulers.io())
            .execute {
                copy(logInResult = it)
            }
    }
}