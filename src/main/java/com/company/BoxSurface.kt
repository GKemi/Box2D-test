package com.company

import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PVector
import java.util.ArrayList

class BoxSurface(shapeVertices: MutableList<PVector>) {
    // We'll keep track of all of the surface points
    internal var surface: ArrayList<Vec2>
    lateinit var body: Body

    init {
        surface = ArrayList()

        // This is what box2d uses to put the surface in its world
        val chain = ChainShape()

        for (vertex in shapeVertices) surface.add(Vec2(vertex.x + Sketch.width / 2f, vertex.y + Sketch.height / 2f))
        surface.add(Vec2(shapeVertices[0].x + Sketch.width / 2f, shapeVertices[0].y + Sketch.height / 2f))
        // This has to go backwards so that the objects  bounce off the top of the surface
        // This "edgechain" will only work in one direction!

        // Build an array of shapeVertices in Box2D coordinates
        // from the ArrayList we made
        val vertices = arrayOfNulls<Vec2>(surface.size)
        for (i in vertices.indices) {
            val edge = Sketch.box2d.coordPixelsToWorld(surface[i])
            vertices[i] = edge
        }

        // Create the chain!
        chain.createChain(vertices, vertices.size)

        // The edge chain is now attached to a body via a fixture
        val bd = BodyDef()
        bd.position.set(0.0f, 0.0f)
        body = Sketch.box2d.createBody(bd)
        // Shortcut, we could define a fixture if we
        // want to specify frictions, restitution, etc.
        body.createFixture(chain, 1f)
    }

    // A simple function to just display the edge chain as a series of vertex points
    internal fun display() {
        Sketch.strokeWeight(2f)
        Sketch.stroke(0)
        Sketch.noFill()
        Sketch.beginShape()
        for (v in surface) {
            Sketch.vertex(v.x, v.y)
        }
        Sketch.endShape(PConstants.CLOSE)
    }
}