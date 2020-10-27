package com.example.ludoroller

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ludoroller.domain.Dice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val NUM_OF_SIDES = 6

    private var webView : WebView? = null
    private var rollButton : Button? = null
    private var effectsCheck : CheckBox? = null
    private var clearButton : Button? = null
    private var trackButton : Button? = null
    private var rollResultText : TextView? = null
    private var trackText : TextView? = null

    private var awwPlayer : MediaPlayer? = null
    private var rollPlayer: MediaPlayer? = null
    private var cheerPlayer : MediaPlayer? = null
    private var numberedPlayers : Array<MediaPlayer>? = null

    private var lastRoll : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        webView = findViewById(R.id.rolling_view)
        webView?.loadUrl("file:///android_asset/rolling_dice.gif")

        trackText = findViewById(R.id.textView2)
        effectsCheck = findViewById(R.id.checkBox)
        rollResultText = findViewById(R.id.textView)

        createMediaPlayers()
        setupListeners()
    }

    private fun setupListeners() {
        rollButton = findViewById(R.id.button3)
        rollButton?.setOnClickListener { rollDice() }

        trackButton = findViewById(R.id.button2)
        trackButton?.setOnClickListener {
            if (lastRoll > 0) {
                trackText?.text = "${trackText?.text} $lastRoll,"
            }
        }

        clearButton = findViewById(R.id.button)
        clearButton?.setOnClickListener { trackText!!.text = "" }
    }

    private fun createMediaPlayers() {
        rollPlayer = MediaPlayer.create(baseContext, R.raw.dice)
        cheerPlayer = MediaPlayer.create(baseContext, R.raw.cheer)
        awwPlayer = MediaPlayer.create(baseContext, R.raw.aww)

        numberedPlayers = Array(NUM_OF_SIDES) {
            i -> when(i) {
                0 -> MediaPlayer.create(baseContext, R.raw.one)
                1 -> MediaPlayer.create(baseContext, R.raw.two)
                2 -> MediaPlayer.create(baseContext, R.raw.three)
                3 -> MediaPlayer.create(baseContext, R.raw.four)
                4 -> MediaPlayer.create(baseContext, R.raw.five)
                5 -> MediaPlayer.create(baseContext, R.raw.six)
                else -> MediaPlayer.create(baseContext, R.raw.dice)
            }
        }
    }

    private fun rollDice() {
        lastRoll = Dice(NUM_OF_SIDES).roll()
        trackButton?.visibility = View.INVISIBLE
        clearButton?.visibility = View.INVISIBLE
        rollButton?.visibility = View.INVISIBLE
        webView?.visibility = View.VISIBLE
        Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show()

        GlobalScope.launch {
            rollPlayer?.start()
            delay(timeMillis = 1500)

            resultAudioPlayers()

            runOnUiThread {
                trackButton?.visibility = View.VISIBLE
                clearButton?.visibility = View.VISIBLE
                rollButton?.visibility = View.VISIBLE
                webView?.visibility = View.INVISIBLE
                rollResultText?.text = "$lastRoll"
            }
        }
    }

    private fun resultAudioPlayers() {
        if (effectsCheck!!.isChecked) {
            when (lastRoll) {
                1 -> awwPlayer?.start()
                6 -> cheerPlayer?.start()
            }
        }
        numberedPlayers!![lastRoll - 1].start()
    }
}
