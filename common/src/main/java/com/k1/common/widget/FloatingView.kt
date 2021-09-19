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
    private val path = Path()
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
        // 设置一层layer, 最底色为透明色, SRC覆盖后就会显示出下面一层view的颜色
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
        if (background != null) {
            val shape = getDrawableShape(background)
            val radii = getDrawableRadii(background)

            when (shape) {
                GradientDrawable.RECTANGLE -> {
                    drawRectHighlight(canvas, item, radii)
                }
            }
        } else {
            drawRectHighlight(canvas, item)
        }
    }

    private fun drawRectHighlight(canvas: Canvas, item: HighlightItem, radii: FloatArray? = null) {
        if (item.view == null) return

        canvas.save()
        getLocationInWindow(location)
        canvas.translate(-location[0].toFloat(), -location[1].toFloat())
        calculateAndSetHighlightArea(item)

        if (radii != null && radii.any { !equals(0f) }) {
            path.reset()
            rectF.set(rect)
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, highlightPaint)
        } else {
            canvas.drawRect(rect, highlightPaint)
        }

        canvas.restore()
    }

    private fun calculateAndSetHighlightArea(item: HighlightItem) {
        if (item.view == null) return

        item.view.getDrawingRect(rect)
        item.view.getLocationInWindow(location)

        val l = location[0] - item.paddingLeft
        val t = location[1] - item.paddingTop
        val r = location[0] + rect.right + item.paddingRight
        val b = location[1] + rect.bottom + item.paddingBottom

        rect.apply {
            left = l
            top = t
            right = r
            bottom = b
        }
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
     * @param padding in pixel
     */
    class HighlightItem(
        var paddingLeft: Int = 0,
        var paddingTop: Int = 0,
        var paddingRight: Int = 0,
        var paddingBottom: Int = 0,
        val view: View? = null,
    )

}