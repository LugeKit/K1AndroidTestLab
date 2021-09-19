package com.k1.common.widget.floatingview

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.View

class DefaultHighlightDrawer(private val baseView: FloatingView): FloatingView.IHighlightDrawer {

    private val path = Path()
    private val rect = Rect()
    private val rectF = RectF()
    private val location = IntArray(2)
    private val highlightPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }

    override fun getHighlightArea(item: FloatingView.HighlightItem): IntArray {
        val area = IntArray(4) { 0 }

        if (item.highlightView == null) return area

        item.highlightView.getLocationInWindow(location)
        item.highlightView.getDrawingRect(rect)
        area[0] = location[0] - item.targetShape.paddings.left
        area[1] = location[1] - item.targetShape.paddings.top
        area[2] = location[0] + rect.right + item.targetShape.paddings.right
        area[3] = location[1] + rect.bottom + item.targetShape.paddings.bottom

        baseView.getLocationInWindow(location)
        area[0] -= location[0]
        area[1] -= location[1]
        area[2] -= location[0]
        area[3] -= location[1]

        return area
    }

    override fun drawHighlight(canvas: Canvas, item: FloatingView.HighlightItem) {
        if (item.highlightView == null) return

        when (item.targetShape) {
            is FloatingView.HighlightItem.Shape.Default -> {
                val background = item.highlightView.background as? GradientDrawable
                if (background != null) {
                    val shape = getDrawableShape(background)
                    val radii = getDrawableRadii(background)

                    when (shape) {
                        GradientDrawable.RECTANGLE -> {
                            drawRectHighlight(canvas, item, radii)
                        }
                        else -> {
                            // todo@k1 画其他的情况
                        }
                    }
                } else {
                    drawRectHighlight(canvas, item)
                }
            }
            is FloatingView.HighlightItem.Shape.Rectangle -> {
                drawRectHighlight(canvas, item)
            }
            is FloatingView.HighlightItem.Shape.Oval -> {
                // todo@k1 椭圆的情况
            }
        }
    }

    /**
     * 画一个矩形的高亮范围，可以有圆角
     * @param radii FloatArray(8) topLeft, topRight, bottomRight, bottomLeft. Each takes two: px, py
     */
    private fun drawRectHighlight(canvas: Canvas, item: FloatingView.HighlightItem, radii: FloatArray? = null) {
        if (item.highlightView == null) return

        getHighlightArea(item).apply {
            rect.left = get(0)
            rect.top = get(1)
            rect.right = get(2)
            rect.bottom = get(3)
        }

        if (radii != null && radii.any { !equals(0f) }) {
            path.reset()
            rectF.set(rect)
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, highlightPaint)
        } else {
            canvas.drawRect(rect, highlightPaint)
        }
    }

    /**
     * todo@k1 画椭圆形
     */
    private fun drawOvalHighlight(canvas: Canvas, item: FloatingView.HighlightItem) {

    }

    /**
     * 获取需要高亮的范围
     */
    private fun calculateAndSetHighlightArea(item: FloatingView.HighlightItem) {
        if (item.highlightView == null) return

        item.highlightView.getDrawingRect(rect)
        item.highlightView.getLocationInWindow(location)

        val l = location[0] - item.targetShape.paddings.left
        val t = location[1] - item.targetShape.paddings.top
        val r = location[0] + rect.right + item.targetShape.paddings.right
        val b = location[1] + rect.bottom + item.targetShape.paddings.bottom

        rect.apply {
            left = l
            top = t
            right = r
            bottom = b
        }
    }

    /**
     * 获取drawable的shape信息
     */
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

    /**
     * 获取drawable的圆角角度
     */
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
}