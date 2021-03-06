package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.k1.common.widget.floatingview.FloatingView
import com.k1.common.widget.floatingview.FloatingView.HighlightItem.CompanionItem.CompanionPosition.Companion.RIGHT_TOP
import com.k1.k1testlab.BaseActivity
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_floating_view.*

class FloatingViewActivity : BaseActivity() {
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

    override fun onStop() {
        super.onStop()
        floatingView.remove()
    }

    private fun initViews() {
        floatingView = FloatingView.build(this)
        val companionView = layoutInflater.inflate(R.layout.item_companion_view, floating_view_main, false)
        val location = IntArray(2)
        floating_view_main.getLocationInWindow(location)
        val highlightItem = FloatingView.HighlightItem(
            highlightView = floating_view_highlight,
            companionItems = listOf(
                FloatingView.HighlightItem.CompanionItem(companionView, FloatingView.HighlightItem.CompanionItem.CompanionPosition(RIGHT_TOP))
            )
        )
        floatingView.addHighlightItem(highlightItem)

        floating_view_show_btn.setOnClickListener {
            floatingView.show()
        }

        floatingView.setOnClickListener {
            floatingView.removeHighlightItem(highlightItem)
        }
    }

}