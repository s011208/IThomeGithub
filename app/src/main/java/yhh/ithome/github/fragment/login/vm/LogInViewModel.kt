package yhh.ithome.github.fragment.login.vm

import com.airbnb.mvrx.MvRxState
import io.reactivex.disposables.CompositeDisposable
import yhh.ithome.github.external.mvrx.MvRxViewModel

data class LogInFragmentState(val isLogInEnable: Boolean = false) : MvRxState

class LogInViewModel(initialState: LogInFragmentState) : MvRxViewModel<LogInFragmentState>(initialState) {

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun validateUserNameAndPassword(userName: CharSequence, password: CharSequence): Boolean {
        return (userName.isNotBlank() && password.isNotBlank()).also { result -> setState { copy(isLogInEnable = result) } }
    }
}