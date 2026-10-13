package com.lifepulse.domain.model

enum class RecommendationSeverity {
    INFO,
    WARNING,
    ALERT
}

enum class RecommendationCategory {
    PRECIPITATION,
    TEMPERATURE,
    UV,
    WIND,
    AIR_QUALITY,
    GENERAL
}

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val category: RecommendationCategory,
    val severity: RecommendationSeverity,
    val actionText: String? = null
)
