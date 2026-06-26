import re

with open('/Users/amarjithb/Desktop/InstaResolv-Shared/instaresolv-kmp-shared/shared/src/commonMain/kotlin/org/example/project/ui/components/AppFilterBottomSheet.kt', 'r') as f:
    content = f.read()

# Update tabs logic
old_tabs_logic = """    val tabs = remember(isFromObservation, isFromIncident, isFromPermit) {
        if (isFromObservation) {
            listOf("Status", "Observer", "Responsible")
        } else if (isFromIncident) {
            listOf("Project", "Reported By", "Date", "Incident Type")
        } else {
            listOf("Project", "Reported By", "Date")
        }
    }"""

new_tabs_logic = """    val tabs = remember(isFromObservation, isFromIncident, isFromPermit) {
        val baseTabs = mutableListOf("Project", "Reported By", "Date")
        if (isFromObservation) {
            baseTabs.add(0, "Status")
        }
        if (isFromIncident) {
            baseTabs.add("Incident Type")
        }
        baseTabs
    }"""

content = content.replace(old_tabs_logic, new_tabs_logic)

# Add badges
old_sidebar_row = """                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = tab }
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .padding(vertical = 14.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab,
                                style = textStyle(size = 14.sp, weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium),
                                color = if (isSelected) AppColors.Black else Color(0xFF94979D)
                            )
                        }"""

new_sidebar_row = """                        val hasActiveFilter = when(tab) {
                            "Status" -> tempStatuses.isNotEmpty()
                            "Project" -> tempProjects.isNotEmpty() || tempNoProjectSelected
                            "Reported By" -> tempReportedBy.isNotEmpty()
                            "Date" -> dateOpenMillis != null || dateCloseMillis != null
                            "Incident Type" -> tempIncidentTypes.isNotEmpty()
                            "Observer" -> tempObservers.isNotEmpty()
                            "Responsible" -> tempResponsiblePersons.isNotEmpty()
                            else -> false
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = tab }
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .padding(vertical = 14.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab,
                                style = textStyle(size = 14.sp, weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium),
                                color = if (isSelected) AppColors.Black else Color(0xFF94979D),
                                modifier = Modifier.weight(1f)
                            )
                            if (hasActiveFilter) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary)
                                )
                            }
                        }"""

content = content.replace(old_sidebar_row, new_sidebar_row)

with open('/Users/amarjithb/Desktop/InstaResolv-Shared/instaresolv-kmp-shared/shared/src/commonMain/kotlin/org/example/project/ui/components/AppFilterBottomSheet.kt', 'w') as f:
    f.write(content)

