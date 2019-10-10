package com.company

import processing.core.PApplet
import processing.core.PConstants
import java.util.ArrayList
import shiffman.box2d.Box2DProcessing

object Sketch : PApplet(){
    init {
        this.runSketch()
    }

    override fun settings() {
        super.settings()
        size(800, 800, PConstants.P2D)
        pixelDensity(2)
        //fullScreen(PConstants.P2D)
    }

    // A reference to our box2d world
    lateinit var box2d: Box2DProcessing

    // An ArrayList of particles that will fall on the surface
    var particles = ArrayList<Particle>()

    // An object to store information about the uneven surface
    lateinit var arcRect: ArcRect
    lateinit var sketchProducer: SketchProducer

    override fun setup() {
        // Initialize box2d physics and create the world
        sketchProducer = SketchProducer(this.g)
        box2d = Box2DProcessing(this)
        box2d.createWorld()
        surface.setLocation((displayWidth/8*6)-width/2, (displayHeight/2)-height/2)
        // We are setting a custom gravity
        box2d.setGravity(0f, -20f)

        // Create the empty list
    //particles = ArrayList()
        arcRect = ArcRect(width/2f, height/2f, width*0.25f, height*0.75f, arcRadius = 1.2f, arcDepth = 0.7f, physicsEnabled = true)
    }

    override fun draw() {
        box2d.step()

        // If the mouse is pressed, we make new particles
        if (mousePressed) {
            val sz = random(2f)
            particles.add(Particle(mouseX*1f, mouseY*1f, sz*1f))
        }

        // We must always step through time!

        background(255)

        arcRect.display()

        // Draw all particles
        for (p in particles) {
            p.display()
        }

        // Particles that leave the screen, we delete them
        // (note they have to be deleted from both the box2d world and our list
        for (i in particles.size - 1 downTo 0) {
            val p = particles[i]
            if (p.done()) {
                particles.removeAt(i)
            }
        }
        sketchProducer.drawDoodool()
    }
}

fun main() {
    Sketch
}