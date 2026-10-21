package org.example.project.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.createBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import org.example.project.shared.R
import org.example.project.utilites.getExifOrientation
import org.example.project.utilites.rotateBitmapByOrientation
import java.io.File

class EditorToolView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    private val btnClose: View
    private val btnUndo: View
    private val btnRedo: View
    private val btnShape: View
    private val btnText: View
    private val btnDraw: View
    private val btnEraser: View
    private val btnSave: AppCompatButton
    private val previewImage: PhotoEditorView
    private val shapePanel: LinearLayoutCompat
    private val shapeCircle: AppCompatImageView
    private val shapeArrow: AppCompatImageView
    private val shapeRect: AppCompatImageView
    private val shapeLine: AppCompatImageView
    private val llTextColorPickerView: LinearLayout
    private val llSeekbar: LinearLayoutCompat
    private val seekBar: SeekBar

    private lateinit var photoEditor: PhotoEditor
    var listener: EditorToolListener? = null
    private var destinationFile: String? = null
    private var selectedImageUrl: String? = null

    private val colorList = listOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
        Color.parseColor("#FF9800"),
        Color.parseColor("#9C27B0"),
        Color.BLACK, Color.WHITE
    )

    private var brushColor: Int = Color.RED
    private var brushSize: Float = 10f
    private var selectedColorView: View? = null
    private var isImageReady = false

    private val toolButtons by lazy {
        listOf(btnUndo, btnRedo, btnShape, btnText, btnDraw)
    }

    private val shapeButtons by lazy {
        listOf(shapeCircle, shapeArrow, shapeRect, shapeLine)
    }

    private var selectedTool: ToolType = ToolType.NONE
    private var selectedTextColor: Int = Color.RED

    private fun materialDialogBuilder(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_Material3_DayNight
            )
        )
    }

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.editor_view, this, true)

        btnClose = findViewById(R.id.btnClose)
        btnUndo = findViewById(R.id.btnUndo)
        btnRedo = findViewById(R.id.btnRedo)
        btnShape = findViewById(R.id.btnShape)
        btnText = findViewById(R.id.btnText)
        btnDraw = findViewById(R.id.btnDraw)
        btnEraser = findViewById(R.id.btnEraser)
        btnSave = findViewById(R.id.btnSave)
        previewImage = findViewById(R.id.previewImage)
        shapePanel = findViewById(R.id.shapePanel)
        shapeCircle = findViewById(R.id.shapeCircle)
        shapeArrow = findViewById(R.id.shapeArrow)
        shapeRect = findViewById(R.id.shapeRect)
        shapeLine = findViewById(R.id.shapeLine)
        llTextColorPickerView = findViewById(R.id.llTextColorPickerView)
        llSeekbar = findViewById(R.id.llSeekbar)
        seekBar = findViewById(R.id.seekBar)

        setupUI()
        initPhotoEditor()
    }

    fun setImageBytes(imageBytes: ByteArray, destinationFileName: String, callback: EditorToolListener) {
        listener = callback
        destinationFile = createOutputFile(destinationFileName)
        isImageReady = false

        photoEditor.clearAllViews()
        previewImage.source.setImageDrawable(null)

        val decoded = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        if (decoded != null) {
            val orientation = getExifOrientation(imageBytes)
            val bitmap = rotateBitmapByOrientation(decoded, orientation)
            previewImage.source.setImageBitmap(bitmap)
            isImageReady = true
        }
    }

    fun setImageUrl(imageUrl: String, destinationFileName: String, callback: EditorToolListener) {
        listener = callback
        selectedImageUrl = imageUrl
        destinationFile = createOutputFile(destinationFileName)
        isImageReady = false

        photoEditor.clearAllViews()
        previewImage.source.setImageDrawable(null)

        val cleanPath = imageUrl.removePrefix("file://")
        val localFile = File(cleanPath)
        if (localFile.exists()) {
            val decoded = BitmapFactory.decodeFile(localFile.absolutePath)
            if (decoded != null) {
                val orientation = getExifOrientation(localFile.absolutePath)
                val bitmap = rotateBitmapByOrientation(decoded, orientation)
                previewImage.source.setImageBitmap(bitmap)
                isImageReady = true
            }
        }
    }

    private fun initPhotoEditor() {
        photoEditor = PhotoEditor.Builder(context, previewImage)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .build()

        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
                openTextEditDialog(text, colorCode, rootView)
            }
            override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: ViewType) {}
            override fun onStopViewChangeListener(viewType: ViewType) {}
            override fun onTouchSourceImage(event: MotionEvent) {}
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                brushSize = value.toFloat()
                photoEditor.brushSize = brushSize
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        loadColorPalette()
    }

    private fun setupUI() {
        btnClose.setOnClickListener {
            photoEditor.clearAllViews()
            listener?.onClose()
        }

        btnSave.setOnClickListener { saveEditedImage() }

        btnUndo.setOnClickListener {
            photoEditor.undo()
        }

        btnRedo.setOnClickListener {
            photoEditor.redo()
        }

        btnText.setOnClickListener {
            selectTool(btnText, ToolType.TEXT)
            openTextDialog()
        }

        btnShape.setOnClickListener {
            selectTool(btnShape, ToolType.SHAPE)
        }

        btnDraw.setOnClickListener {
            activateBrushMode()
        }

        btnEraser.setOnClickListener {
            photoEditor.setBrushDrawingMode(true)
            photoEditor.brushEraser()
            selectTool(btnEraser, ToolType.ERASER)
        }

        shapeCircle.setOnClickListener { addShapeSticker(ShapeType.Oval, shapeCircle) }
        shapeRect.setOnClickListener { addShapeSticker(ShapeType.Rectangle, shapeRect) }
        shapeLine.setOnClickListener { addShapeSticker(ShapeType.Line, shapeLine) }
        shapeArrow.setOnClickListener { addShapeSticker(ShapeType.Arrow(), shapeArrow) }
    }

    private fun selectTool(button: View, tool: ToolType) {
        selectedTool = tool
        toolButtons.forEach { it.isSelected = it == button }

        when (tool) {
            ToolType.DRAW -> {
                llSeekbar.visibility = View.VISIBLE
                llTextColorPickerView.visibility = View.VISIBLE
                shapePanel.visibility = View.GONE
            }
            ToolType.SHAPE -> {
                shapePanel.visibility = View.VISIBLE
                llSeekbar.visibility = View.GONE
                llTextColorPickerView.visibility = View.VISIBLE
                photoEditor.setBrushDrawingMode(false)
            }
            ToolType.TEXT -> {
                shapePanel.visibility = View.GONE
                llSeekbar.visibility = View.GONE
                llTextColorPickerView.visibility = View.GONE
                photoEditor.setBrushDrawingMode(false)
            }
            ToolType.ERASER -> {
                llSeekbar.visibility = View.VISIBLE
                llTextColorPickerView.visibility = View.GONE
                shapePanel.visibility = View.GONE
            }
            else -> {
                llSeekbar.visibility = View.GONE
                llTextColorPickerView.visibility = View.GONE
                shapePanel.visibility = View.GONE
                photoEditor.setBrushDrawingMode(false)
            }
        }
    }

    private fun activateBrushMode() {
        selectTool(btnDraw, ToolType.DRAW)
        photoEditor.setShape(ShapeBuilder().withShapeType(ShapeType.Brush))
        photoEditor.setBrushDrawingMode(true)
        photoEditor.brushColor = brushColor
        photoEditor.brushSize = brushSize
    }

    private fun addShapeSticker(shape: ShapeType, selectedShape: AppCompatImageView) {
        shapeButtons.forEach { it.isSelected = it == selectedShape }
        val bmp = generateShapeBitmap(shape, 300, brushColor)
        photoEditor.addImage(bmp)
    }

    private fun generateShapeBitmap(shape: ShapeType, size: Int, shapeColor: Int): Bitmap {
        val bmp = createBitmap(size, size)
        val canvas = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = shapeColor
            strokeWidth = 10f
            style = Paint.Style.STROKE
        }

        when (shape) {
            is ShapeType.Oval -> canvas.drawOval(5f, 5f, size - 5f, size - 5f, p)
            is ShapeType.Rectangle -> canvas.drawRect(5f, 5f, size - 5f, size - 5f, p)
            is ShapeType.Line -> canvas.drawLine(0f, size / 2f, size.toFloat(), size / 2f, p)
            is ShapeType.Arrow -> {
                canvas.drawLine(0f, size / 2f, size.toFloat(), size / 2f, p)
                canvas.drawLine(size * 0.6f, size * 0.2f, size.toFloat(), size / 2f, p)
                canvas.drawLine(size * 0.6f, size * 0.8f, size.toFloat(), size / 2f, p)
            }
            else -> {}
        }
        return bmp
    }

    private fun openTextDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_text, null)
        val etText = view.findViewById<AppCompatEditText>(R.id.etTextInput)
        val colorContainer = view.findViewById<LinearLayoutCompat>(R.id.colorContainer)

        etText.requestFocus()
        etText.setSelection(etText.text?.length ?: 0)
        colorContainer.removeAllViews()

        colorList.forEach { color ->
            val circle = View(context).apply {
                val size = (40 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(12, 10, 12, 10)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    if (color == selectedTextColor) {
                        setStroke(6, Color.BLACK)
                    }
                }
                setOnClickListener {
                    selectedTextColor = color
                    highlightSelectedColor(this, colorContainer)
                }
            }
            colorContainer.addView(circle)
        }

        val dialog = materialDialogBuilder()
            .setTitle(context.getString(R.string.title_add_text_to_image))
            .setView(view)
            .setPositiveButton("ADD") { _, _ ->
                val text = etText.text.toString().trim()
                if (text.isNotEmpty()) {
                    val style = TextStyleBuilder().apply {
                        withTextColor(selectedTextColor)
                    }
                    photoEditor.addText(text, style)
                }
            }
            .setNegativeButton("CANCEL", null)
            .create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun openTextEditDialog(text: String?, color: Int, rootView: View?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_text, null)
        val etText = view.findViewById<EditText>(R.id.etTextInput)
        val colorContainer = view.findViewById<LinearLayoutCompat>(R.id.colorContainer)

        colorContainer.removeAllViews()
        etText.setText(text)
        etText.requestFocus()

        var editColor = color

        colorList.forEach { c ->
            val circle = View(context).apply {
                val size = (40 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(12, 10, 12, 10)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(c)
                    if (c == color) {
                        setStroke(6, Color.BLACK)
                    }
                }
                setOnClickListener {
                    editColor = c
                    highlightSelectedColor(this, colorContainer)
                }
            }
            colorContainer.addView(circle)
        }

        val dialog = materialDialogBuilder()
            .setTitle("Edit Text")
            .setView(view)
            .setPositiveButton("UPDATE") { _, _ ->
                val newText = etText.text.toString().trim()
                if (newText.isNotEmpty() && rootView != null) {
                    val style = TextStyleBuilder().apply {
                        withTextColor(editColor)
                    }
                    photoEditor.editText(rootView, newText, style)
                }
            }
            .setNegativeButton("CANCEL", null)
            .create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun highlightSelectedColor(selectedView: View, container: LinearLayoutCompat) {
        for (i in 0 until container.childCount) {
            val v = container.getChildAt(i)
            (v.background as? GradientDrawable)?.setStroke(0, Color.TRANSPARENT)
        }
        (selectedView.background as GradientDrawable).setStroke(6, Color.BLACK)
    }

    private fun loadColorPalette() {
        llTextColorPickerView.removeAllViews()
        colorList.forEach { color ->
            val size = (40 * resources.displayMetrics.density).toInt()
            val circle = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply { setMargins(12, 8, 12, 8) }
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                val tagDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                background = drawable
                tag = tagDrawable
                setOnClickListener {
                    selectedColorView?.background = (selectedColorView?.tag as? GradientDrawable)
                    val selected = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(color)
                        setStroke(6, Color.BLACK)
                    }
                    background = selected
                    selectedColorView = this
                    brushColor = color
                    photoEditor.brushColor = color
                }
            }
            llTextColorPickerView.addView(circle)
        }
    }

    private fun saveEditedImage() {
        val destPath = destinationFile ?: return
        if (!isImageReady) return

        photoEditor.saveAsFile(destPath, object : PhotoEditor.OnSaveListener {
            override fun onSuccess(imagePath: String) {
                listener?.onSaveClick(Uri.fromFile(File(imagePath)))
            }
            override fun onFailure(e: Exception) {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createOutputFile(fileName: String): String {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        return File(dir, fileName).absolutePath
    }

    enum class ToolType { NONE, UNDO, REDO, SHAPE, TEXT, DRAW, ERASER }
}
