package com.k1.common.widget.floatingview

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isVisible

class FloatingView private constructor(
    context: Context,
): FrameLayout(context) {

    companion object {
        fun build(context: Context, parentView: ViewGroup): FloatingView {
            return FloatingView(context).apply {
                parentView.addView(this)
            }
        }
    }

    // region init
    private val highlightItems = ArrayList<HighlightItem>()

    private val highlightPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }

    private val backgroundColor = "#88000000"
    var highlightDrawer: IHighlightDrawer = DefaultHighlightDrawer(this)

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setWillNotDraw(false) // 默认是true
        isVisible = false
    }
    // endregion init

    // region public method
    /**
     * 添加高亮的view
     */
    fun addHighlightItem(item: HighlightItem) {
        highlightItems.add(item)
        if (item.companionView != null) {
            addView(item.companionView)
        }
        postInvalidate()
    }

    fun clear() {
        highlightItems.clear()
        removeAllViews()
        postInvalidate()
    }

    fun show() {
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
    // endregion public method

    // region layout
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (item in highlightItems) {
            item.companionView?.also {
                val area = highlightDrawer.getHighlightArea(item)
                it.layout(area[2], area[1], area[2] + it.measuredWidth, area[1] + it.measuredHeight)
            }
        }
    }

    // endregion layout

    // region draw
    override fun onDraw(canvas: Canvas) {
        // 设置一层layer, 最底色为透明色, SRC覆盖后就会显示出下面一层view的颜色
        val count = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // 画出透明底色
        canvas.drawColor(Color.parseColor(backgroundColor))
        for (item in highlightItems) {
            highlightDrawer.drawHighlight(canvas, item)
        }

        canvas.restoreToCount(count)

        super.onDraw(canvas)
    }
    // endregion draw

    /**
     * @param padding in pixel
     */
    class HighlightItem(
        val highlightView: View? = null,
        val companionView: View? = null,
        val targetShape: Shape = Shape.Default()
    ) {
        sealed class Shape(val paddings: Paddings) {
            class Default(paddings: Paddings = Paddings()): Shape(paddings)
            class Rectangle(paddings: Paddings = Paddings()): Shape(paddings)
            class Oval(paddings: Paddings = Paddings(), val radX: Float = 0f, val radY: Float = 0f): Shape(paddings)

            fun setPaddings(l: Int = 0, t: Int = 0, r: Int = 0, b: Int = 0) {
                paddings.apply {
                    left = l
                    top = t
                    right = r
                    bottom = b
                }
            }

            fun updatePaddings(l: Int = 0, t: Int = 0, r: Int = 0, b: Int = 0) {
                paddings.apply {
                    left += l
                    top += t
                    right += r
                    bottom += b
                }
            }
        }

        data class Paddings(
            var left: Int = 0,
            var top: Int = 0,
            var right: Int = 0,
            var bottom: Int = 0,
        )
    }

    interface IHighlightDrawer {
        /**
         * @return An IntArray(4) to describe the bounds of highlight view. 0->left, 1->top, 2->right, 3->bottom
         */
        fun getHighlightArea(item: HighlightItem): IntArray

        fun drawHighlight(canvas: Canvas, item: HighlightItem)
    }

}