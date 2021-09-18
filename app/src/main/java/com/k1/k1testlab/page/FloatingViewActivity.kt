package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.k1.common.dp
import com.k1.common.widget.FloatingView
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_floating_view.*

class FloatingViewActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FloatingViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_view)

        init()
    }

    private fun init() {
        val floatingView = FloatingView(this)

        floating_view_main.addView(floatingView)

        floatingView.addHighlightItem(FloatingView.HighlightItem(offsetLeft = 10.dp, view = floating_view_highlight))
        floatingView.addHighlightItem(FloatingView.HighlightItem(view = floating_view_highlight2))
    }


}