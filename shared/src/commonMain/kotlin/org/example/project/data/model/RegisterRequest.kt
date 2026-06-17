package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val uuid: String,
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val designation: String,
    val company: String,
    val profileImage: String
) {

}