package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_line_layout_test.*

class LineLayoutTestActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LineLayoutTestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_layout_test)

        init()
    }

    private fun init() {
    }


}