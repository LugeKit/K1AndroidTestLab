package com.k1.k1testlab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.k1.k1testlab.page.FillBlankActivity
import com.k1.k1testlab.page.FloatingViewActivity
import com.k1.k1testlab.page.LineLayoutTestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        fill_blank_start_btn.setOnClickListener {
            FillBlankActivity.start(this)
        }

        line_layout_start_btn.setOnClickListener {
            LineLayoutTestActivity.start(this)
        }

        floating_view_start_btn.setOnClickListener {
            FloatingViewActivity.start(this)
        }
    }
}