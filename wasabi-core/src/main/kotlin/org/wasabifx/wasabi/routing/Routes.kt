package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

/**
 * @author Tradunsky V.V.
 */
fun Iterable<Route>.findMostWeightyBy(path: String): Route? {
    val segments = path.split("/")
    val routeWeights = mutableListOf<Pair<Double, Route>>()
    for (route in this) {
        val routeSegments = route.path.split("/")
        if (routeSegments.size != segments.size) continue
        var routeWeight = 0.0
        segments.forEachIndexed { index, segment ->
            if (segment.startsWith(':')) {
                routeWeight += 0.5
            } else if (segment.compareTo(routeSegments[index], ignoreCase = true) == 0) {
                ++routeWeight
            }
        }
        routeWeights.add(Pair(routeWeight, route))
    }
    return routeWeights.maxBy { it.first }?.second
}

fun Set<Route>.findSimilar(method: HttpMethod, path: String): Route? {
    val segments = path.split("/")
    for (route in this) {
        val currentSegments = route.path.split("/")
        if (segments.size != currentSegments.size || route.method != method) continue

        if (areSegmentsEqual(segments, currentSegments)) return route
    }
    return null
}

private fun areSegmentsEqual(segments: List<String>, otherSegments: List<String>): Boolean {
    var index = 0
    for (segment in segments){
        val anotherSegment = otherSegments[index]
        if (segment.startsWith(":")) {
            if (!anotherSegment.startsWith(":")) return false
        }else if (anotherSegment.startsWith(":")) {
            if (!segment.startsWith(":")) return false
        } else if (segment.compareTo(anotherSegment, ignoreCase = true) != 0) return false
        ++index
    }
    return true
}
