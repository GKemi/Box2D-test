package com.company

import processing.core.PApplet
import processing.core.PGraphics

class SketchProducer(val sketch: PGraphics) {

    fun drawDool() = sketch.run {
        fill(255f, 0f, 0f)
        ellipse(Mouse.x, Mouse.y, 100f, 100f)
    }
}