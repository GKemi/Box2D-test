package com.company

import org.jbox2d.dynamics.Body
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import javax.swing.Spring.height




class Particle(var x: Float, var y: Float, var r: Float) {
    lateinit var body: Body

    init {
        makeBody(x, y, r)
    }

    // This function removes the particle from the box2d world
    fun killBody() {
        Sketch.box2d.destroyBody(body)
    }

    // Is the particle ready for deletion?
    fun done(): Boolean {
        // Let's find the screen position of the particle
        val pos = Sketch.box2d.getBodyPixelCoord(body)
        // Is it off the bottom of the screen?
        if (pos.y > Sketch.height + r * 2) {
            killBody()
            return true
        }
        return false
    }

    fun display() {
        Sketch.run {
            // We look at each body and get its screen position
            val pos = box2d.getBodyPixelCoord(body)
            // Get its angle of rotation
            val a = body.angle
            pushMatrix()
            translate(pos.x, pos.y)
            rotate(-a)
            fill(254f, 202f, 32f)
            noStroke()
            ellipse(0f, 0f, r * 2, r * 2)
            // Let's add a line so we can see the rotation
            line(0f, 0f, r, 0f)
            popMatrix()
        }
    }

    // Here's our function that adds the particle to the Box2D world
    fun makeBody(x: Float, y: Float, r: Float) {
        // Define a body
        val bd = BodyDef()
        // Set its position
        bd.position = Sketch.box2d.coordPixelsToWorld(x, y)
        bd.type = BodyType.DYNAMIC
        body = Sketch.box2d.world.createBody(bd)

        // Make the body's shape a circle
        val cs = CircleShape()
        cs.m_radius = Sketch.box2d.scalarPixelsToWorld(r)

        val fd = FixtureDef()
        fd.shape = cs
        // Parameters that affect physics
        fd.density = 1f
        fd.friction = 0.01f
        fd.restitution = 0.3f

        // Attach fixture to body
        body.createFixture(fd)

        // Give it a random initial velocity (and angular velocity)
        body.linearVelocity = Vec2(Sketch.random(-10f, 10f), Sketch.random(5f, 10f))
        body.angularVelocity = Sketch.random(-10f, 10f)
    }
}