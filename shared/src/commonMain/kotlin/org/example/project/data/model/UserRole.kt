package org.example.project.data.model

enum class UserRole(val value: Int) {
    SUPER_ADMIN(1),
    ADMIN(2),
    PARTICIPANT(3);

    companion object {
        fun fromInt(value: Int): UserRole {
            return entries.firstOrNull { it.value == value }
                ?: PARTICIPANT
        }
    }
}

enum class CompanyType(val value: Int) {
    NORMAL_USER(1),
    SUB_CONTRACTOR(2);

    companion object {
        fun fromInt(value: Int?): CompanyType {
            return CompanyType.entries.firstOrNull { it.value == value }
                ?: NORMAL_USER
        }
    }
}