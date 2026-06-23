package org.example.project.data.model

enum class UserRole(val value: Int) {
    ADMIN(2),
    PARTICIPANT(3);

    companion object {
        fun fromInt(value: Int): UserRole {
            return entries.firstOrNull { it.value == value }
                ?: PARTICIPANT
        }
    }
}