package com.k1.common.widget.floatingview

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.core.view.isVisible
import com.k1.common.widget.floatingview.FloatingView.HighlightItem.CompanionItem.CompanionPosition.Companion.LEFT_BOTTOM
import com.k1.common.widget.floatingview.FloatingView.HighlightItem.CompanionItem.CompanionPosition.Companion.LEFT_TOP
import com.k1.common.widget.floatingview.FloatingView.HighlightItem.CompanionItem.CompanionPosition.Companion.RIGHT_BOTTOM
import com.k1.common.widget.floatingview.FloatingView.HighlightItem.CompanionItem.CompanionPosition.Companion.RIGHT_TOP
import kotlin.math.max

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

    private var backgroundColor = "#bb000000"
    var highlightDrawer: IHighlightDrawer = DefaultHighlightDrawer(this)

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setWillNotDraw(false) // 默认是true
        isVisible = false
    }
    // endregion init

    // region public method
    /**
     * 添加高亮区域
     */
    fun addHighlightItem(item: HighlightItem) {
        highlightItems.add(item)
        for (companionItem in item.companionItems) {
            addView(companionItem.view)
        }
        postInvalidate()
    }

    /**
     * 移除高亮区域和伴随view
     */
    fun removeHighlightItem(item: HighlightItem) {
        highlightItems.remove(item)
        for (companionItem in item.companionItems) {
            removeView(companionItem.view)
        }
        postInvalidate()
    }

    /**
     * 清除高亮区域
     */
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

    fun setBaseColor(colorString: String) {
        backgroundColor = colorString
    }
    // endregion public method

    // region measure
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 要保证它绘制在其他view之上
        (parent as? ViewGroup)?.also {
            var maxZ = 0f
            for (idx in 0 until it.childCount) {
                val child = it[idx]
                if (child != this) {
                    maxZ = max(maxZ, child.translationZ)
                }
            }
            translationZ = maxZ + 1
        }
    }
    // endregion measure

    // region layout
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (item in highlightItems) {
            val area = highlightDrawer.getRelativeHighlightArea(item)
            for (companionItem in item.companionItems) {
                val cView = companionItem.view
                if (cView.parent != this) continue // 没add进去 理论上需要throw

                var cl = 0
                var ct = 0
                when (companionItem.companionPosition.position) {
                    LEFT_TOP -> {
                        cl = area[0]
                        ct = area[1]
                    }
                    RIGHT_TOP -> {
                        cl = area[2]
                        ct = area[1]
                    }
                    RIGHT_BOTTOM -> {
                        cl = area[2]
                        ct = area[3]
                    }
                    LEFT_BOTTOM -> {
                        cl = area[0]
                        ct = area[3]
                    }
                }
                cl += companionItem.companionPosition.offsetX
                ct += companionItem.companionPosition.offsetY

                cView.apply {
                    layout(cl, ct, cl + measuredWidth, ct + measuredHeight)
                }
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

    // region relative class
    /**
     * @param highlightView 需要被高亮的view, 可以为null, 为null时请指定targetShape && targetShape.area
     * @param targetShape 想要绘制的高亮形状，默认为Default，即根据highlightView的background绘制，可以设置该参数的paddings值，从而更改高亮的区域(添加高亮padding)
     * @param companionItems 一个列表，包含了附着在高亮view上的其他view的信息，绘制在蒙层上(例如指引图)
     */
    class HighlightItem(
        val highlightView: View? = null,
        val targetShape: Shape = Shape.Default(),
        val companionItems: List<CompanionItem> = arrayListOf(),
    ) {
        sealed class Shape(val paddings: Paddings) {
            class Default(paddings: Paddings = Paddings()): Shape(paddings)

            /**
             * @param area view相对于window的绝对位置, 如果area不为null, 绘制的高亮区域就是area指定的区域, 注意此时paddings无效
             */
            class Rectangle(paddings: Paddings = Paddings(), val radii: FloatArray = FloatArray(8) { 0f }, val area: Rect? = null): Shape(paddings)
            class Oval(paddings: Paddings = Paddings(), val area: Rect? = null): Shape(paddings)

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

        class CompanionItem(val view: View, val companionPosition: CompanionPosition) {
            /**
             * @param offsetX in px, positive->move right
             * @param offsetY in px, positive->move down
             */
            class CompanionPosition(val position: Int, val offsetX: Int = 0, val offsetY: Int = 0) {
                companion object {
                    const val LEFT_TOP = 0
                    const val RIGHT_TOP = 1
                    const val RIGHT_BOTTOM = 2
                    const val LEFT_BOTTOM = 3
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
         * 获取高亮view的高亮范围，返回的是相对于FloatingView的位置，计算了padding后的值
         * @return An IntArray(4) to describe the bounds of highlight view. 0->left, 1->top, 2->right, 3->bottom
         */
        fun getRelativeHighlightArea(item: HighlightItem): IntArray

        /**
         * 绘制高亮区域的规则，实现参考DefaultHighlightDrawer
         */
        fun drawHighlight(canvas: Canvas, item: HighlightItem)
    }
    // endregion relative class

}