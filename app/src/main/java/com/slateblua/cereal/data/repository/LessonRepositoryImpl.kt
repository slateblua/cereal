package com.slateblua.cereal.data.repository

import com.slateblua.cereal.data.datasource.LessonLocalDataSource
import com.slateblua.cereal.data.datasource.PreferencesDataSource
import com.slateblua.cereal.domain.model.Lesson
import com.slateblua.cereal.domain.model.NodeStatus
import com.slateblua.cereal.domain.model.RoadmapNode
import com.slateblua.cereal.domain.model.RoadmapUnit
import com.slateblua.cereal.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LessonRepositoryImpl(
    private val lessonLocalDataSource: LessonLocalDataSource,
    private val preferencesDataSource: PreferencesDataSource
) : LessonRepository {

    override fun getRoadmap(): Flow<List<RoadmapUnit>> {
        return preferencesDataSource.userStatsFlow.map { userStats ->
            val allLessons = lessonLocalDataSource.getAllLessons()
            val completed = userStats.completedLessonIds

            // Define thematic units
            val unit1Lessons = allLessons.filter { it.unitId == "unit_1_foundations" }
            val unit2Lessons = allLessons.filter { it.unitId == "unit_2_wealth_growth" }
            val unit3Lessons = allLessons.filter { it.unitId == "unit_3_financial_mastery" }

            var hasFoundNextAvailable = false

            fun mapToNodes(lessons: List<Lesson>, startIndex: Int): List<RoadmapNode> {
                return lessons.mapIndexed { idx, lesson ->
                    val isCompleted = completed.contains(lesson.id)
                    val status = when {
                        isCompleted -> NodeStatus.COMPLETED
                        !hasFoundNextAvailable -> {
                            hasFoundNextAvailable = true
                            NodeStatus.AVAILABLE
                        }
                        else -> NodeStatus.LOCKED
                    }

                    RoadmapNode(
                        id = "node_${lesson.id}",
                        lessonId = lesson.id,
                        title = lesson.title,
                        subtitle = lesson.subtitle,
                        order = startIndex + idx,
                        status = status,
                        xpReward = lesson.xpReward,
                        iconName = lesson.iconName,
                        starsEarned = if (isCompleted) 3 else 0
                    )
                }
            }

            val unit1Nodes = mapToNodes(unit1Lessons, 0)
            val unit2Nodes = mapToNodes(unit2Lessons, unit1Nodes.size)
            val unit3Nodes = mapToNodes(unit3Lessons, unit1Nodes.size + unit2Nodes.size)

            listOf(
                RoadmapUnit(
                    id = "unit_1_foundations",
                    unitNumber = 1,
                    title = "Unit 1: Foundations of Money",
                    description = "Understand the flow of currency and unlock the 50/30/20 budget framework.",
                    bannerColorHex = "#5B67CA", // Slate Blue Primary
                    nodes = unit1Nodes
                ),
                RoadmapUnit(
                    id = "unit_2_wealth_growth",
                    unitNumber = 2,
                    title = "Unit 2: Growing Wealth & Credit",
                    description = "Discover exponential compound interest and conquer your credit score.",
                    bannerColorHex = "#00C9A7", // Mint Teal
                    nodes = unit2Nodes
                ),
                RoadmapUnit(
                    id = "unit_3_investing_and_security",
                    unitNumber = 3,
                    title = "Unit 3: Investing & Financial Security",
                    description = "Build a diversified index portfolio and build a bulletproof emergency fund.",
                    bannerColorHex = "#FFB800", // Warm Gold
                    nodes = unit3Nodes
                )
            )
        }
    }

    override suspend fun getLesson(lessonId: String): Result<Lesson> {
        return lessonLocalDataSource.getLessonById(lessonId)
    }

    override suspend fun markLessonCompleted(lessonId: String, score: Int, xpEarned: Int) {
        preferencesDataSource.markLessonCompleted(lessonId, xpEarned)
    }
}
