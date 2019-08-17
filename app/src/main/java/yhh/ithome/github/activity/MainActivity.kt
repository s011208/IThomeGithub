package yhh.ithome.github.activity

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import yhh.ithome.github.R
import yhh.ithome.github.fragment.login.LogInFragment

class MainActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogInFragment())
                .commitAllowingStateLoss()
        }
    }
}
