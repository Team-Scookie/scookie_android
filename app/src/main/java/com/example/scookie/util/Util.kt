package com.example.scookie.util

import android.content.res.Resources

/**
 * 코드로 view 사이즈에 변화를 주거나 여백을 설정할 때는 Pixel 단위로 변환해서 작업을 해줘야 한다!
 */
fun Int.pxToDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt() // 픽셀을 dp로

fun Int.dpToPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt() // dp를 픽셀로
