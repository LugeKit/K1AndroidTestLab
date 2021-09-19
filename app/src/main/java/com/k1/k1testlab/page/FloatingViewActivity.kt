package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        floatingView.addHighlightItem(FloatingView.HighlightItem(
            paddingLeft = 10.dp,
            paddingTop = 10.dp,
            paddingRight = 10.dp,
            paddingBottom = 10.dp,
            view = floating_view_highlight
        ))
        floatingView.addHighlightItem(FloatingView.HighlightItem(view = floating_view_highlight2))
        floatingView.setOnClickListener {
            floatingView.dismiss()
        }
    }

}