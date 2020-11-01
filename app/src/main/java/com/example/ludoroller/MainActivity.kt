package com.beachvilletek.ludoroller

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beachvilletek.ludoroller.domain.Dice
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val numOfSides = 6

    private var adView : AdView? = null

    private var webView : WebView? = null
    private var rollButton : MaterialButton? = null
    private var soundCheck : CheckBox? = null
    private var noDelayCheck : CheckBox? = null
    private var clearButton : MaterialButton? = null
    private var trackButton : MaterialButton? = null
    private var rollResultText : TextView? = null
    private var trackText : TextView? = null

    private var rollPlayer: MediaPlayer? = null
    private var numberedPlayers : Array<MediaPlayer>? = null

    private var lastRoll : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        setupAds()

        grabTextElements()

        populateWebView()

        createMediaPlayers()

        setupListeners()
    }

    private fun grabTextElements() {
        trackText = findViewById(R.id.textView2)
        soundCheck = findViewById(R.id.checkBox)
        noDelayCheck = findViewById(R.id.checkBox3)
        rollResultText = findViewById(R.id.textView)
    }

    private fun populateWebView() {
        webView = findViewById(R.id.rolling_view)
        webView?.loadUrl("file:///android_asset/rolling_dice.gif")
    }

    private fun setupAds() {
        MobileAds.initialize(this) {}

        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    private fun setupListeners() {
        rollButton = findViewById(R.id.button3)
        rollButton?.setOnClickListener { rollDice() }

        trackButton = findViewById(R.id.button2)
        trackButton?.setOnClickListener {
            if (lastRoll > 0) {
                trackText?.text = getString(R.string.uselessInterpolation, trackText?.text, lastRoll)
            }
        }

        clearButton = findViewById(R.id.button)
        clearButton?.setOnClickListener { trackText!!.text = "" }

        noDelayCheck?.setOnClickListener {
            if (noDelayCheck!!.isChecked) {
                if (soundCheck!!.isChecked) {
                    soundCheck!!.toggle()
                }
                soundCheck!!.isEnabled = false
            } else {
                soundCheck!!.isEnabled = true
            }
        }
    }

    private fun createMediaPlayers() {
        rollPlayer = MediaPlayer.create(baseContext, R.raw.dice)

        numberedPlayers = Array(numOfSides) {
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
        lastRoll = Dice(numOfSides).roll()
        
        if (!noDelayCheck!!.isChecked) {
            disableAll()
        } else {
            rollResultText?.setTextColor(Color.RED)
        }

        GlobalScope.launch {
            if (!noDelayCheck!!.isChecked) {
                if (soundCheck!!.isChecked) {
                    rollPlayer?.start()
                    delay(timeMillis = 1500)
                    numberedPlayers!![lastRoll - 1].start()
                } else {
                    delay(timeMillis = 1000)
                }
            } else {
                delay(timeMillis = 100)
                rollResultText?.setTextColor(Color.GRAY)
            }

            runOnUiThread {
                if (!noDelayCheck!!.isChecked) {
                    enableAll()
                }
                rollResultText?.text = "$lastRoll"
            }
        }
    }

    private fun enableAll() {
        rollButton?.isEnabled = true
        noDelayCheck?.isEnabled = true
        trackButton?.isEnabled = true
        clearButton?.isEnabled = true
        if (!noDelayCheck!!.isChecked) {
            soundCheck?.isEnabled = true
        }

        webView?.visibility = View.INVISIBLE
    }

    private fun disableAll() {
        rollButton?.isEnabled = false
        noDelayCheck?.isEnabled = false
        trackButton?.isEnabled = false
        clearButton?.isEnabled = false
        soundCheck?.isEnabled = false

        webView?.visibility = View.VISIBLE
    }
}
