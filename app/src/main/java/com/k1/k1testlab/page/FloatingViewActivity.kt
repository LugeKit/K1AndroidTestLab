package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.k1.common.dp
import com.k1.common.widget.floatingview.FloatingView
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_floating_view.*

class FloatingViewActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FloatingViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var floatingView: FloatingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_view)

        init()
    }

    private fun init() {
        initViews()


    }

    private fun initViews() {
        floatingView = FloatingView.build(this, floating_view_main)

        floatingView.show()
        val companionView = layoutInflater.inflate(R.layout.item_companion_view, floating_view_main, false)
        floatingView.addHighlightItem(FloatingView.HighlightItem(highlightView = floating_view_highlight, companionView = companionView))
        floatingView.addHighlightItem(FloatingView.HighlightItem(highlightView = floating_view_highlight2))
        floatingView.setOnClickListener {
            floatingView.dismiss()
        }
    }

}