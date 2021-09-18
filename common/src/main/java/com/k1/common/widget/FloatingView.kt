package com.k1.common.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
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
    private val rect = Rect()
    private val rectF = RectF()
    private val viewLocation = IntArray(2)
    private val location = IntArray(2)

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
        postInvalidate()
    }

    fun clear() {
        highlightItems.clear()
        postInvalidate()
    }

    fun show() {
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
    // endregion public method

    // region draw
    override fun onDraw(canvas: Canvas) {
        // 设置一层layer, 最底色为透明色, DST_OUT相交部分就会变成透明
        val count = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // 画出透明底色
        canvas.drawColor(Color.parseColor(backgroundColor))
        for (item in highlightItems) {
            drawHighlight(canvas, item)
        }

        canvas.restoreToCount(count)

        super.onDraw(canvas)
    }

    // region draw highlight
    private fun drawHighlight(canvas: Canvas, item: HighlightItem) {
        if (item.view == null) return

        val background = item.view.background as? GradientDrawable
        if (background == null) {
            // 画一个简单的矩形即可
            drawSimpleRectHighlight(canvas, item)
        } else {
            val shape = getDrawableShape(background)
            val radii = getDrawableRadii(background)

            if (radii != null && radii.any { !equals(0f) }) {
                // 有圆角 需要画圆角
            } else {
                // 退化到画矩形
                drawSimpleRectHighlight(canvas, item)
            }
        }
    }

    private fun drawSimpleRectHighlight(canvas: Canvas, item: HighlightItem) {
        if (item.view == null) return

        canvas.save()
        getLocationInWindow(location)
        canvas.translate(-location[0].toFloat(), -location[1].toFloat())

        item.view.getDrawingRect(rect) // 获取绘制的边框
        item.view.getLocationInWindow(viewLocation) // 获取在window中的位置，自动适应status bar

        val l = viewLocation[0] - item.offsetLeft
        val t = viewLocation[1] - item.offsetTop
        val r = viewLocation[0] + rect.right + item.offsetRight
        val b = viewLocation[1] + rect.bottom + item.offsetBottom

        rect.apply {
            left = l
            top = t
            right = r
            bottom = b
        }

        canvas.drawRect(rect, highlightPaint)
        canvas.restore()
    }

    private fun getDrawableShape(drawable: GradientDrawable): Int {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return drawable.shape
        } else {
            try {
                val fieldGradientState = drawable.javaClass.getDeclaredField("mGradientState")
                fieldGradientState.isAccessible = true
                val gradientState = fieldGradientState.get(drawable)

                val fieldShape = gradientState.javaClass.getDeclaredField("mShape")
                return fieldShape.get(drawable) as? Int ?: GradientDrawable.RECTANGLE
            } catch (ignore: Throwable) {
            }
            return GradientDrawable.RECTANGLE
        }
    }

    private fun getDrawableRadii(drawable: GradientDrawable): FloatArray? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return drawable.cornerRadii
        } else {
            try {
                val fieldGradientState = Class.forName("android.graphics.drawable.GradientDrawable").getDeclaredField("mGradientState")
                fieldGradientState.isAccessible = true
                val gradientState = fieldGradientState.get(drawable)

                val fieldRadiusArray = gradientState.javaClass.getDeclaredField("mRadiusArray")
                return fieldRadiusArray.get(gradientState) as? FloatArray

            } catch (ignore: Throwable) {

            }
            return null
        }
    }
    // endregion draw highlight
    // endregion draw

    /**
     * @param offset in pixel
     */
    class HighlightItem(
        var offsetLeft: Int = 0,
        var offsetTop: Int = 0,
        var offsetRight: Int = 0,
        var offsetBottom: Int = 0,
        val view: View? = null,
    )

}