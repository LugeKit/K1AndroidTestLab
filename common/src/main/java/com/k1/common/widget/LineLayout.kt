package com.k1.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.use
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.k1.common.*
import kotlin.math.max

class LineLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): ViewGroup(context, attrs, defStyle) {

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    // region init
    var linePadding = 0f
    var lineItemPadding = 0f
    var orientation = HORIZONTAL

    /**
     * measure的时候直接将layoutPos存在这个list里，不需要再计算子view的坐标
     */
    private val layoutPosList = ArrayList<LayoutPos>()

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LineLayout)
        ta.use {
            linePadding = ta.getDimension(R.styleable.LineLayout_linePadding, 0f)
            lineItemPadding = ta.getDimension(R.styleable.LineLayout_lineItemPadding, 0f)
            orientation = ta.getInt(R.styleable.LineLayout_orientation, HORIZONTAL)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
    // endregion init



    // region measure
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        when (orientation) {
            HORIZONTAL -> measureHorizontal(widthMeasureSpec, heightMeasureSpec)
            VERTICAL -> measureVertical(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = widthMeasureSpec.getSize()
        val widthMode = widthMeasureSpec.getMode()
        val heightSize = heightMeasureSpec.getSize()
        val heightMode = heightMeasureSpec.getMode()

        layoutPosList.clear()

        var currLeft = paddingLeft  // 当前需要放置的view的左侧起点
        var currTop = paddingTop    // 当前需要放置的view的顶部起点
        var lineSpaceCost = 0   // 当前行占用的最高高度
        var childTotalWidth = 0 // 所有子view的宽度总和，当子view加起来没有超过最大宽度时，这个就是WRAP_CONTENT情况下设置的自身的宽度

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        for (idx in 0 until childCount) {
            val child = getChildAt(idx)

            val childWidth = child.measuredWidth + child.marginHorizontal
            val childHeight = child.measuredHeight + child.marginVertical

            if (widthMode != MeasureSpec.UNSPECIFIED
                && currLeft + childWidth + paddingRight > widthSize
                && currLeft != paddingLeft) {
                // 这行放不下了，换行，如果换行宽度也超出了自身宽度，就直接放
                // currLeft != paddingLeft 说明不是这行第一个

                currLeft = paddingLeft
                currTop += lineSpaceCost + linePadding.toInt()
                lineSpaceCost = 0
            }

            childTotalWidth += childWidth

            layoutChild(currLeft, currTop, child)
            currLeft += childWidth + lineItemPadding.toInt()
            lineSpaceCost = max(lineSpaceCost, childHeight)

            if (widthMode != MeasureSpec.UNSPECIFIED
                && currLeft >= widthSize - paddingRight) {
                // 换行
                currLeft = paddingLeft
                currTop += lineSpaceCost + linePadding.toInt()
                lineSpaceCost = 0
            }

        }

        childTotalWidth += ((childCount - 1) * lineItemPadding).toInt() + paddingLeft + paddingRight
        val childTotalHeight = currTop + lineSpaceCost + paddingBottom

        val retWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> {
                if (childTotalWidth <= widthSize) childTotalWidth
                else widthSize
            }
            else -> childTotalWidth
        }

        val retHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> {
                if (childTotalHeight <= heightSize) childTotalHeight
                else heightSize
            }
            else -> childTotalHeight
        }

        setMeasuredDimension(retWidth, retHeight)

    }

    private fun measureChildHorizontal(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        currLeft: Int,
        child: View
    ) {
        val widthSize = widthMeasureSpec.getSize()
        val childWidthMeasureSpec = when (val widthMode = widthMeasureSpec.getMode()) {
            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
                // 这里不需要关心padding，因为在measureChild中设置的child的空间会将父布局的padding减掉
                val canUseSpace = widthSize - (currLeft - paddingLeft)
                MeasureSpec.makeMeasureSpec(canUseSpace, widthMode)
            }
            else -> {
                // UNSPECIFIED
                widthMeasureSpec
            }
        }
        // 这里调用measureChild 会将子View的布局模式(WRAP_CONTENT/MATCH_PARENT/EXACTLY)与父View的布局模式进行一个对比，根据尺寸设置子View的布局模式
        // 具体见源码
        measureChild(child, childWidthMeasureSpec, heightMeasureSpec)
    }

    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = widthMeasureSpec.getSize()
        val widthMode = widthMeasureSpec.getMode()
        val heightSize = heightMeasureSpec.getSize()
        val heightMode = heightMeasureSpec.getMode()

        layoutPosList.clear()

        var currLeft = paddingLeft
        var currTop = paddingTop
        var lineSpaceCost = 0

        var childTotalHeight = 0

        for (idx in 0 until childCount) {
            val child = getChildAt(idx)
            measureChildVertical(widthMeasureSpec, heightMeasureSpec, currTop, child)

            val childWidth = child.measuredWidth + child.marginHorizontal
            val childHeight = child.measuredHeight + child.marginVertical

            if (heightMode != MeasureSpec.UNSPECIFIED
                && currTop + childHeight + paddingBottom > heightSize
                && currTop != paddingTop) {
                // 换列
                currTop = paddingTop
                currLeft += lineSpaceCost + linePadding.toInt()
                lineSpaceCost = 0
                measureChildVertical(widthMeasureSpec, heightMeasureSpec, currTop, child)
            }

            childTotalHeight += childHeight

            layoutChild(currLeft, currTop, child)
            currTop += childHeight + lineItemPadding.toInt()
            lineSpaceCost = max(lineSpaceCost, childWidth)
            if (heightMode != MeasureSpec.UNSPECIFIED
                && currTop >= heightSize - paddingBottom) {
                currTop = paddingTop
                currLeft += lineSpaceCost + linePadding.toInt()
                lineSpaceCost = 0
            }
        }
        childTotalHeight += ((childCount - 1) * lineItemPadding).toInt()
        val childTotalWidth = currLeft + lineSpaceCost + paddingRight

        val retWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> {
                if (childTotalWidth <= widthSize) childTotalWidth
                else widthSize
            }
            else -> childTotalWidth
        }

        val retHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> {
                if (childTotalHeight <= heightSize) childTotalHeight
                else heightSize
            }
            else -> childTotalHeight
        }

        setMeasuredDimension(retWidth, retHeight)
    }

    private fun measureChildVertical(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        currTop: Int,
        child: View
    ) {
        val heightSize = heightMeasureSpec.getSize()
        val childHeightMeasureSpec = when (val heightMode = heightMeasureSpec.getMode()) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> {
                val canUseSpace = heightSize - (currTop - paddingTop)
                MeasureSpec.makeMeasureSpec(canUseSpace, heightMode)
            }
            else -> {
                // UNSPECIFIED
                heightMeasureSpec
            }
        }
        measureChild(child, widthMeasureSpec, childHeightMeasureSpec)
    }

    private fun layoutChild(
        currLeft: Int,
        currTop: Int,
        child: View
    ) {
        val cl = currLeft + child.marginLeft
        val cr = cl + child.measuredWidth
        val ct = currTop + child.marginTop
        val cb = ct + child.measuredHeight

        layoutPosList.add(LayoutPos(cl, ct, cr, cb))
    }
    // endregion measure



    // region layout
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (idx in 0 until childCount) {
            val child = getChildAt(idx)
            val (cl, ct, cr, cb) = layoutPosList.getOrNull(idx) ?: continue
            child.layout(cl, ct, cr, cb)
        }
    }
    // endregion layout



    data class LayoutPos(val l: Int, val t: Int, val r: Int, val b: Int)

}