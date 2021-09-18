package com.k1.common

import android.util.TypedValue
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

inline val Int.dpf
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), BaseApplication.instance.resources.displayMetrics)

inline val Int.dp
    get() = this.dpf.toInt()

inline val Float.dpf
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, BaseApplication.instance.resources.displayMetrics)

inline val Float.dp
    get() = this.dpf.toInt()

inline val Int.spf
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), BaseApplication.instance.resources.displayMetrics)

inline val Int.sp
    get() = this.spf.toInt()

inline val Float.spf
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, BaseApplication.instance.resources.displayMetrics)

inline val Float.sp
    get() = this.spf.toInt()

inline val Float.px: Int
    get() = (this / BaseApplication.instance.resources.displayMetrics.density + 0.5f).toInt()

inline val Int.px
    get() = this.toFloat().px

fun Int.getSize() = View.MeasureSpec.getSize(this)

fun Int.getMode() = View.MeasureSpec.getMode(this)

inline val View.marginHorizontal
    get() = marginLeft + marginRight

inline val View.marginVertical
    get() = marginTop + marginBottom
