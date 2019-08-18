package yhh.ithome.github.fragment.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_log_in.*
import yhh.ithome.github.R
import yhh.ithome.github.fragment.login.vm.LogInFragmentState
import yhh.ithome.github.fragment.login.vm.LogInViewModel
import java.util.concurrent.TimeUnit

class LogInFragment : BaseMvRxFragment() {

    private val viewModel: LogInViewModel by fragmentViewModel()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        compositeDisposable += logIn.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                viewModel.logIn(userName.text, password.text)
            }

        viewModel.asyncSubscribe(
            LogInFragmentState::logInResult,
            onFail = {
                Toast.makeText(requireContext(), R.string.fragment_log_in_fail_toast, Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(
                        R.string.fragment_log_in_welcome_message,
                        if (it.name.isBlank()) it.login else it.name
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    override fun invalidate() {
        withState(viewModel, { state ->
            logIn.isEnabled = state.isLogInEnable

            progressBar.visibility = if (state.logInResult is Loading) View.VISIBLE else View.INVISIBLE
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}