package com.k1.common

import android.util.TypedValue

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