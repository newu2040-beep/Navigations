package com.example.gesture

import com.example.database.GesturePoint
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.max

object GestureRecognizer {
    private const val NUM_POINTS = 32
    private const val SQUARE_SIZE = 100f
    private val HALF_DIAGONAL = hypot(SQUARE_SIZE, SQUARE_SIZE) / 2f

    // Standard templates represented as point coordinates on a 100x100 grid.
    // Screen coordinates (Y increases downwards).
    val DEFAULT_TEMPLATES = mapOf(
        "M" to listOf(
            GesturePoint(10f, 90f),
            GesturePoint(10f, 10f),
            GesturePoint(50f, 50f),
            GesturePoint(90f, 10f),
            GesturePoint(90f, 90f)
        ),
        "W" to listOf(
            GesturePoint(10f, 10f),
            GesturePoint(10f, 90f),
            GesturePoint(50f, 50f),
            GesturePoint(90f, 90f),
            GesturePoint(90f, 10f)
        ),
        "C" to listOf(
            GesturePoint(90f, 15f),
            GesturePoint(50f, 10f),
            GesturePoint(10f, 30f),
            GesturePoint(10f, 70f),
            GesturePoint(50f, 90f),
            GesturePoint(90f, 85f)
        ),
        "S" to listOf(
            GesturePoint(90f, 15f),
            GesturePoint(20f, 15f),
            GesturePoint(15f, 45f),
            GesturePoint(85f, 55f),
            GesturePoint(80f, 85f),
            GesturePoint(10f, 85f)
        ),
        "V" to listOf(
            GesturePoint(15f, 15f),
            GesturePoint(50f, 85f),
            GesturePoint(85f, 15f)
        ),
        "Circle" to listOf(
            GesturePoint(50f, 10f),
            GesturePoint(85f, 25f),
            GesturePoint(85f, 75f),
            GesturePoint(50f, 90f),
            GesturePoint(15f, 75f),
            GesturePoint(15f, 25f),
            GesturePoint(50f, 10f)
        ),
        "Lightning" to listOf(
            GesturePoint(80f, 10f),
            GesturePoint(20f, 50f),
            GesturePoint(70f, 50f),
            GesturePoint(15f, 90f)
        )
    )

    data class RecognitionResult(val name: String, val score: Float)

    /**
     * Recognizes a raw list of gesture points against a set of templates.
     * Returns a RecognitionResult with the template name and matching score in [0..1].
     */
    fun recognize(rawPoints: List<GesturePoint>, customTemplates: Map<String, List<GesturePoint>> = emptyMap()): RecognitionResult {
        if (rawPoints.size < 3) {
            return RecognitionResult("Unknown", 0.0f)
        }

        // Prepare raw input
        val processedInput = normalizePoints(rawPoints)

        var bestMatch = "Unknown"
        var bestScore = 0.0f

        // Combine predefined default templates and custom templates
        val allTemplates = DEFAULT_TEMPLATES + customTemplates

        for ((name, templateRaw) in allTemplates) {
            if (templateRaw.size < 3) continue
            val processedTemplate = normalizePoints(templateRaw)

            val distance = pathDistance(processedInput, processedTemplate)
            val score = max(0.0f, 1.0f - (distance / HALF_DIAGONAL))

            if (score > bestScore) {
                bestScore = score
                bestMatch = name
            }
        }

        return RecognitionResult(bestMatch, bestScore)
    }

    private fun normalizePoints(points: List<GesturePoint>): List<GesturePoint> {
        val resampled = resample(points, NUM_POINTS)
        val scaled = scaleTo(resampled, SQUARE_SIZE)
        return translateToOrigin(scaled)
    }

    /**
     * Resamples the path to contain exactly N equidistantly spaced points.
     */
    fun resample(points: List<GesturePoint>, n: Int): List<GesturePoint> {
        if (points.isEmpty()) return emptyList()
        if (points.size == 1) return List(n) { points[0] }

        val interval = pathLength(points) / (n - 1)
        if (interval <= 0f) return List(n) { points[0] }

        val result = mutableListOf<GesturePoint>()
        result.add(points[0])

        var d = 0f
        var i = 1
        var tempPoints = points.toMutableList()

        while (i < tempPoints.size) {
            val p1 = tempPoints[i - 1]
            val p2 = tempPoints[i]
            val dist = hypot(p2.x - p1.x, p2.y - p1.y)

            if (d + dist >= interval) {
                // Interpolate
                val qx = p1.x + ((interval - d) / dist) * (p2.x - p1.x)
                val qy = p1.y + ((interval - d) / dist) * (p2.y - p1.y)
                val q = GesturePoint(qx, qy)
                result.add(q)
                // Insert q as the new previous element
                tempPoints.add(i, q)
                d = 0f
            } else {
                d += dist
            }
            i++
        }

        // Handle rounding edge case and force size matching
        while (result.size < n) {
            result.add(points.last())
        }
        return result.take(n)
    }

    /**
     * Scales points uniformly to fit within a square box of scaleSize.
     */
    private fun scaleTo(points: List<GesturePoint>, scaleSize: Float): List<GesturePoint> {
        if (points.isEmpty()) return emptyList()

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (p in points) {
            minX = min(minX, p.x)
            maxX = max(maxX, p.x)
            minY = min(minY, p.y)
            maxY = max(maxY, p.y)
        }

        val width = maxX - minX
        val height = maxY - minY
        
        // Prevent division by zero
        val scaleX = if (width != 0f) scaleSize / width else 1f
        val scaleY = if (height != 0f) scaleSize / height else 1f
        
        // Use uniform scaling to preserve aspect ratio
        val scale = min(scaleX, scaleY)

        return points.map { p ->
            GesturePoint(
                (p.x - minX) * scale,
                (p.y - minY) * scale
            )
        }
    }

    /**
     * Moves the gesture bounding box centroid to (0,0)
     */
    private fun translateToOrigin(points: List<GesturePoint>): List<GesturePoint> {
        if (points.isEmpty()) return emptyList()
        val centroid = centroid(points)
        return points.map { p ->
            GesturePoint(p.x - centroid.x, p.y - centroid.y)
        }
    }

    private fun centroid(points: List<GesturePoint>): GesturePoint {
        var sumX = 0f
        var sumY = 0f
        for (p in points) {
            sumX += p.x
            sumY += p.y
        }
        return GesturePoint(sumX / points.size, sumY / points.size)
    }

    private fun pathLength(points: List<GesturePoint>): Float {
        var len = 0f
        for (i in 1 until points.size) {
            len += hypot(points[i].x - points[i - 1].x, points[i].y - points[i - 1].y)
        }
        return len
    }

    private fun pathDistance(pts1: List<GesturePoint>, pts2: List<GesturePoint>): Float {
        val len = min(pts1.size, pts2.size)
        if (len == 0) return 0f
        var sum = 0f
        for (i in 0 until len) {
            sum += hypot(pts1[i].x - pts2[i].x, pts1[i].y - pts2[i].y)
        }
        return sum / len
    }
}
