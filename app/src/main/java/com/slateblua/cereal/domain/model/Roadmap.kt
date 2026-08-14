package com.slateblua.cereal.domain.model

enum class NodeStatus {
    LOCKED,
    AVAILABLE,
    COMPLETED
}

data class RoadmapNode(
    val id: String,
    val lessonId: String,
    val title: String,
    val subtitle: String,
    val order: Int,
    val status: NodeStatus,
    val xpReward: Int,
    val iconName: String,
    val starsEarned: Int = 0
)

data class RoadmapUnit(
    val id: String,
    val unitNumber: Int,
    val title: String,
    val description: String,
    val bannerColorHex: String,
    val nodes: List<RoadmapNode>
)
