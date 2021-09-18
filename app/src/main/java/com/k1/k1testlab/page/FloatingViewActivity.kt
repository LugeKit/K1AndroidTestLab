package com.k1.k1testlab.page

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.k1.k1testlab.R
import kotlinx.android.synthetic.main.activity_floating_view.*

class FloatingViewActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FloatingViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_view)

        init()
    }

    private fun init() {
        drawGradientHollow(floating_view_main.background)
    }

    private fun drawGradientHollow(drawable: Drawable) {
        if (drawable !is GradientDrawable) return

        var shape: Int = GradientDrawable.RECTANGLE
        var radii: FloatArray? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            radii = drawable.cornerRadii
            shape = drawable.shape
        } else {
            try {
                val fieldGradientState = Class.forName("android.graphics.drawable.GradientDrawable").getDeclaredField("mGradientState")
                fieldGradientState.isAccessible = true
                val gradientState = fieldGradientState.get(drawable)

                val fieldShape = gradientState.javaClass.getDeclaredField("mShape")
                shape = fieldShape.get(gradientState) as? Int ?: GradientDrawable.RECTANGLE

                val fieldRadiusArray = gradientState.javaClass.getDeclaredField("mRadiusArray")
                radii = fieldRadiusArray.get(gradientState) as? FloatArray

            } catch (ignore: Throwable) {

            }
        }

        Log.d("k1", "radii: ${radii.toString()}")
        Log.d("k1", "shape: ${shape.toString()}")
    }
}