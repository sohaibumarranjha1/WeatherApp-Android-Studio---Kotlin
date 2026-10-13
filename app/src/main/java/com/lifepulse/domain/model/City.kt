package com.lifepulse.domain.model

data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    val isSaved: Boolean = false,
    val timezone: String? = null
) {
    val displayName: String
        get() {
            val parts = mutableListOf<String>()
            parts.add(name)
            if (!admin1.isNullOrBlank() && !admin1.equals(name, ignoreCase = true)) {
                parts.add(admin1)
            }
            if (!country.isNullOrBlank()) {
                parts.add(country)
            }
            return parts.joinToString(", ")
        }
}
