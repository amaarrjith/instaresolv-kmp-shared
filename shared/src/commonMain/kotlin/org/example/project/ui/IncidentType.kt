package org.example.project.ui

import androidx.compose.runtime.Composable
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class IncidentType(val id: Int, val title: String, val stringRes: StringResource) {
    FATALITY(1, "Fatality", Res.string.incident_fatality),
    HOSPITALIZATION(2, "Hospitalization", Res.string.incident_hospitalization),
    ROAD_TRAFFIC_ACCIDENT(3, "Road Traffic Accident", Res.string.incident_road_traffic_accident),
    DANGEROUS_OCCURRENCE(4, "Dangerous Occurrence", Res.string.incident_dangerous_occurrence),
    NON_ACCIDENTAL_DEATH(5, "Non-Accidental Death", Res.string.incident_non_accidental_death),
    PROPERTY_DAMAGE(6, "Property Damage", Res.string.incident_property_damage),
    SIGNIFICANT_ENVIRONMENTAL_INCIDENT(7, "Significant Environmental Incident", Res.string.incident_significant_environmental_incident),
    OTHER(8, "Other", Res.string.incident_other);

    companion object {
        fun fromId(id: Int): IncidentType? {
            return entries.find { it.id == id }
        }
        
        fun getTitlesFromIds(ids: List<Int>?): String {
            if (ids.isNullOrEmpty()) return "Unknown Incident"
            val titles = ids.mapNotNull { id -> fromId(id)?.title }
            return if (titles.isNotEmpty()) titles.joinToString(", ") else "Unknown Incident"
        }

        @Composable
        fun getLocalizedTitlesFromIds(ids: List<Int>?): String {
            if (ids.isNullOrEmpty()) return stringResource(Res.string.unknown_incident)
            val titles = ids.mapNotNull { id -> fromId(id)?.let { stringResource(it.stringRes) } }
            return if (titles.isNotEmpty()) titles.joinToString(", ") else stringResource(Res.string.unknown_incident)
        }
    }
}
