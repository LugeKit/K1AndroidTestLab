package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_fill_blank.*

class FillBlankActivity: AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FillBlankActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_blank)

        init()
    }

    private fun init() {
        fill_blank_scroll_view.post {
            Log.d("k1", "fill_blank_scroll_view: ${fill_blank_scroll_view.height}")
            Log.d("k1", "fill_blank_ll_container: ${fill_blank_ll_container.height}")
        }
    }
}