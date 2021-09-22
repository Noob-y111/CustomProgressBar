package com.example.customprogressbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var view: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById(R.id.progress)
    }

    override fun onResume() {
        super.onResume()
        var time = 0
        var second = 0
        thread {
            while (second < 100) {
                time += 1
                view.toSecondPosition(time)
                if (time >= 100) {
                    time = 0
                    second += 1
                    view.toFirstPosition(second)
                }
                Thread.sleep(100)
            }
        }
    }
}