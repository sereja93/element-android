package im.vector.game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.features.MainActivity
import im.vector.game.databinding.GameFragmentBinding
import im.vector.game.databinding.ItemGameBinding
import im.vector.game.views.DividerItemDecorator
import java.util.LinkedList
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

interface ButtonClicked {
    fun clicked(number: Int)
}

class GameFragment : VectorBaseFragment<GameFragmentBinding>(), ButtonClicked {

    private val longClickDuration = 2000L
    private val handler = Handler(Looper.myLooper()!!)
    private fun View.setOnVeryLongClickListener(listener: () -> Unit) {
        setOnTouchListener { v, event ->
            v.performClick()
            if (event?.action == MotionEvent.ACTION_DOWN) {
                this@GameFragment.handler.postDelayed({ listener.invoke() }, longClickDuration)
                return@setOnTouchListener true
            } else if (event?.action == MotionEvent.ACTION_UP) {
                this@GameFragment.handler.removeCallbacksAndMessages(null)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    private val findList = LinkedList((1..25).toList())

    private var timer: Timer? = null

    private val startTimeGame = System.currentTimeMillis()
    private var findNumber: Int = -1
    private var gameEnd = false

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GameFragmentBinding {
        return GameFragmentBinding.inflate(inflater, container, false)
    }

    private fun startTimer() {
        find()
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            val time = System.currentTimeMillis()
            val totalSecs = time - startTimeGame
            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalSecs) % 60
            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalSecs) % 60
            activity?.runOnUiThread {
                if (!isAdded) {
                    return@runOnUiThread
                }
                if (minutes >= 1) {
                    views.time.text = String.format("01:%02d", seconds)
                    gameEnd(true)
                } else {
                    views.time.text = String.format("00:%02d", seconds)
                }
            }
        }, 0, 200)
    }

    private fun find() {
        val n = findList.pollFirst()
        if (n == null) {
            gameEnd(false)
        } else {
            findNumber = n
            views.findSymbol.text = findNumber.toString()
        }
    }

    private fun gameEnd(endTime: Boolean) {
        if (gameEnd) {
            return
        }
        gameEnd = true
        stopTimer()
        val endTimeGame = System.currentTimeMillis()
        if (endTime) {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.end_time)
                    .setPositiveButton(R.string.retry) { _, _ ->
                        (activity as? GameActivityHandler)?.restart()
                    }
                    .show()
        } else {
            val time = endTimeGame - startTimeGame
            val format = Utils.convertTimeToStringWithMls(time)
            val text = getString(R.string.game_end, format)
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(text)
                    .setPositiveButton(R.string.retry) { _, _ ->
                        (activity as? GameActivityHandler)?.restart()
                    }
                    .show()
        }
    }

    override fun clicked(number: Int) {
        if (number == findNumber) {
            find()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTimer()
        with(views) {

            start.setOnVeryLongClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                (activity as? GameActivityHandler)?.unlock()
            }
            rv.layoutManager = GridLayoutManager(requireContext(), 5)
            rv.addItemDecoration(
                    DividerItemDecorator(
                            ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.divider
                            )!!
                    )
            )
            rv.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
                            .apply {
                                setDrawable(
                                        ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
                                )
                            }
            )
            rv.adapter = GameAdapter(this@GameFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }

    private fun stopTimer() {
        try {
            timer?.cancel()
            timer?.purge()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            timer = null
        }
    }
}

class GameAdapter(private val clicked: ButtonClicked) : RecyclerView.Adapter<GameAdapter.VH>() {
    class VH(val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root)

    private var data = (1..25).toList().shuffled()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        with(holder.binding) {
            btn.text = item.toString()
            btn.setOnClickListener { clicked.clicked(item) }
        }
    }
}
