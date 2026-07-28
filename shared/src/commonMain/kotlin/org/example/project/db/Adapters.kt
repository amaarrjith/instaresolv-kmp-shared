package org.example.project.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isBlank()) emptyList() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<String>): String =
        Json.encodeToString(value)
}
