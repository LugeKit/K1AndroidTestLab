package com.k1.k1testlab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.k1.k1testlab.page.FillBlankActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        fill_blank_start_btn.setOnClickListener {
            FillBlankActivity.start(this)
        }
    }
}