package org.example.project.ui

enum class IncidentType(val id: Int, val title: String) {
    FATALITY(1, "Fatality"),
    HOSPITALIZATION(2, "Hospitalization"),
    ROAD_TRAFFIC_ACCIDENT(3, "Road Traffic Accident"),
    DANGEROUS_OCCURRENCE(4, "Dangerous Occurrence"),
    NON_ACCIDENTAL_DEATH(5, "Non-Accidental Death"),
    PROPERTY_DAMAGE(6, "Property Damage"),
    SIGNIFICANT_ENVIRONMENTAL_INCIDENT(7, "Significant Environmental Incident"),
    OTHER(8, "Other");

    companion object {
        fun fromId(id: Int): IncidentType? {
            return entries.find { it.id == id }
        }
        
        fun getTitlesFromIds(ids: List<Int>?): String {
            if (ids.isNullOrEmpty()) return "Unknown Incident"
            val titles = ids.mapNotNull { id -> fromId(id)?.title }
            return if (titles.isNotEmpty()) titles.joinToString(", ") else "Unknown Incident"
        }
    }
}
