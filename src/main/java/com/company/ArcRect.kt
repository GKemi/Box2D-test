package com.company

import com.company.Sketch.box2d
import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PVector

class ArcRect(val x: Float,
              val y: Float,
              val shapeWidth: Float,
              val shapeHeight: Float,
              var arcRadius: Float = 1f,
              var arcDepth: Float = 1f,
              var drawCircle: Boolean = false,
              var physicsEnabled: Boolean = false) {

    var arcType: ArcType = ArcType.BEZIER_ARC
    var vertices = mutableListOf<PVector>()
    var circleSize = 0f
    var topMidPoint = 0f
    var bottomMidPoint = 0f
    lateinit var mainShapeBody: Body
    lateinit var topCircleBody: Body
    lateinit var bottomCircleBody: Body

    init {
        if (arcDepth > 1f) arcDepth = 1f else if (arcDepth < 0f) arcDepth = 0f
        arcDepth = map(arcDepth, 0f, 1f, 2f, -.65f)

        arcRadius /= 2f
        arcRadius = if (shapeWidth <= shapeHeight) shapeWidth * arcRadius else shapeHeight * arcRadius
        defineMainShape()
        defineCircles()
        if (physicsEnabled) {
            defineMainShapeBoundaries()
            if (drawCircle){
                defineCircleBoundaries()
            }
        }
    }

    fun display() {
        Sketch.noFill()
        Sketch.stroke(0f)
        drawMainShape()
        if (drawCircle) drawCircles()
    }

    private fun defineMainShape() {
        //Left-hand side
        vertices.add(PVector(-shapeWidth * 0.5f, -shapeHeight * 0.5f))
        defineArc(-shapeWidth * 0.5f, -arcRadius, -shapeWidth * 0.5f, arcRadius)
        vertices.add(PVector(-shapeWidth * 0.5f, shapeHeight * 0.5f))

        //Right-hand right
        vertices.add(PVector(shapeWidth * 0.5f, shapeHeight * 0.5f))
        defineArc(shapeWidth * 0.5f, arcRadius, shapeWidth * 0.5f, -arcRadius)
        vertices.add(PVector(shapeWidth * 0.5f, -shapeHeight * 0.5f))
    }

    private fun defineArc(startX: Float, startY: Float, endX: Float, endY: Float) {
        when (arcType) {
            ArcType.CIRCULAR_ARC -> defineCircularArc(startX, endX, startY, endY)
            ArcType.BEZIER_ARC -> defineBezierArc(startX, startY, endY, endX)
        }
    }

    private fun defineCircularArc(startX: Float, endX: Float, startY: Float, endY: Float) {
        val posX = (startX + endX) / 2f
        val posY = (startY + endY) / 2f
        val startingPointOffset = 90
        val loopMax = 180 + startingPointOffset

        for (i in startingPointOffset..loopMax) {
            var theta = atan2(posY, posX)
            theta += (PI / 180) * i

            val x = cos(theta) * (arcRadius)
            val y = sin(theta) * (arcRadius)

            vertices.add(PVector(x, y))
        }
    }

    private fun defineBezierArc(startX: Float, startY: Float, endY: Float, endX: Float) {
        Sketch.run {
            val steps = 30
            for (i in 0..steps) {
                val t = i / steps.toFloat()
                val x = bezierPoint(startX, startX / 2f * arcDepth, startX / 2f * arcDepth, endX, t)
                val y = bezierPoint(startY, startY, endY, endY, t)
                vertices.add(PVector(x, y))
            }
        }
    }

    private fun defineCircles() {
        topMidPoint = ((y - shapeHeight / 2f) + (y - arcRadius)) / 2f
        bottomMidPoint = ((y + shapeHeight / 2f) + (y + arcRadius)) / 2f
        circleSize = arcRadius * 1.25f
    }

    private fun defineMainShapeBoundaries() {
        val chain = ChainShape()
        val mainShapeBorder = mutableListOf<Vec2>()
        for (vertex in vertices) mainShapeBorder.add(Vec2(vertex.x + x, vertex.y + y))
        mainShapeBorder.add(Vec2(vertices[0].x + x, vertices[0].y + y))
        val convertedBodyVertices = arrayOfNulls<Vec2>(mainShapeBorder.size)
        for (i in convertedBodyVertices.indices) {
            val edge = Sketch.box2d.coordPixelsToWorld(mainShapeBorder[i])
            convertedBodyVertices[i] = edge
        }
        chain.createChain(convertedBodyVertices, convertedBodyVertices.size)

        val mainShapeBodyDef = BodyDef()
        mainShapeBodyDef.position.set(0f, 0f)
        mainShapeBody = Sketch.box2d.createBody(mainShapeBodyDef)
        mainShapeBody.createFixture(chain, 1f)
    }

    private fun defineCircleBoundaries() {
        val topCircle = BodyDef()
        topCircle.position = box2d.coordPixelsToWorld(x, topMidPoint)
        topCircle.type = BodyType.STATIC
        topCircleBody = box2d.world.createBody(topCircle)
        val topCircleShape = CircleShape()
        topCircleShape.m_radius = box2d.scalarPixelsToWorld(circleSize / 2f)
        topCircleBody.createFixture(topCircleShape, 1f)

        val bottomCircle = BodyDef()
        bottomCircle.position = box2d.coordPixelsToWorld(x, bottomMidPoint)
        bottomCircle.type = BodyType.STATIC
        bottomCircleBody = box2d.world.createBody(bottomCircle)
        val bottomCircleShape = CircleShape()
        bottomCircleShape.m_radius = box2d.scalarPixelsToWorld(circleSize / 2f)
        bottomCircleBody.createFixture(bottomCircleShape, 1f)
    }

    private fun drawMainShape() {
        Sketch.run {
            push()
            translate(x, y)
            beginShape()
            for (vertex in vertices) {
                vertex(vertex.x, vertex.y)
            }
            endShape(PConstants.CLOSE)
            pop()
        }
    }

    private fun drawCircles() {
        Sketch.run {
            circle(x, topMidPoint, circleSize)
            circle(x, topMidPoint, circleSize * 0.05f)

            circle(x, bottomMidPoint, circleSize)
            circle(x, bottomMidPoint, circleSize * 0.05f)
        }
    }
}

public enum class ArcType {
    CIRCULAR_ARC,
    BEZIER_ARC
}

