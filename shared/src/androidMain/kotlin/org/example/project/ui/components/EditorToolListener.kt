package org.example.project.ui.components

import android.net.Uri

interface EditorToolListener {
    fun onClose()
    fun onSaveClick(uri: Uri)
}
