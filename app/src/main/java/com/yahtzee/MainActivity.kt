package com.yahtzee

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yahtzee.ui.theme.YahtzeeTheme
import java.io.IOException


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    var appMediaPlayer: MediaPlayer = MediaPlayer()
    var playing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")

        appMediaPlayer.setDataSource(this.resources.openRawResourceFd(R.raw.audio))
        appMediaPlayer.prepareAsync()

        setContent {
            YahtzeeTheme {
                YahtzeeApp(appMediaPlayer = appMediaPlayer)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")

        playing = appMediaPlayer.isPlaying
        if (playing) {
            try {
                appMediaPlayer.pause()
            } catch (e: IOException) {
                Log.e(TAG, "prepareAsync -> IOException")
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "prepareAsync -> IllegalArgumentException")
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "prepareAsync -> IllegalStateException")
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
        Log.d(TAG, appMediaPlayer.audioSessionId.toString())
        Log.d(TAG, playing.toString())
        if (playing) {
            try {
                appMediaPlayer.start()
            } catch (e: IOException) {
                Log.e(TAG, "prepareAsync -> IOException")
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "prepareAsync -> IllegalArgumentException")
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "prepareAsync -> IllegalStateException")
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")

        appMediaPlayer.stop()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")

        try {
            appMediaPlayer.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "prepareAsync -> IOException")
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "prepareAsync -> IllegalArgumentException")
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "prepareAsync -> IllegalStateException")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")

        appMediaPlayer.stop()
        appMediaPlayer.release()
    }
}
