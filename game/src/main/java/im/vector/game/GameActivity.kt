package im.vector.game

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.features.MainActivity
import im.vector.app.features.pin.UnlockedActivity
import im.vector.game.databinding.GameActivityBinding

@AndroidEntryPoint
class GameActivity : VectorBaseActivity<GameActivityBinding>(), GameActivityHandler, UnlockedActivity {
    override fun getBinding() = GameActivityBinding.inflate(layoutInflater)

    override fun restart() {
        replaceFragment(views.simpleFragmentContainer, GameFragment::class.java)
    }

    override fun unlock() {
        pinLocker.unlock()
    }

    override fun initUiAndData() {
        if (isFirstCreation()) {
            restart()
        }
    }
}

interface GameActivityHandler {
    fun restart()

    fun unlock()
}
