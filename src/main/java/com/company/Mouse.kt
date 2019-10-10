package com.company

object Mouse {
    val x get() = Sketch.mouseX.toFloat()
    val y get() = Sketch.mouseY.toFloat()
}