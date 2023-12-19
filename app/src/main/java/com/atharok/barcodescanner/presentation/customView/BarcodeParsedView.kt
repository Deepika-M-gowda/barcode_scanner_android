package com.atharok.barcodescanner.presentation.customView

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.atharok.barcodescanner.R
import com.google.android.material.button.MaterialButton

class BarcodeParsedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val clipboardManager: ClipboardManager by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val titleTextView: TextView
    private val contentsTextView: TextView
    private val copyIconButton: MaterialButton

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.template_barcode_parsed_view, this, true)

        titleTextView = view.findViewById(R.id.template_barcode_parsed_view_title)
        contentsTextView = view.findViewById(R.id.template_barcode_parsed_view_contents)
        copyIconButton = view.findViewById(R.id.template_barcode_parsed_view_copy_button)

        context.theme.obtainStyledAttributes(attrs, R.styleable.BarcodeParsedView, defStyleAttr, defStyleRes).apply {
            try {
                titleTextView.text = getString(R.styleable.BarcodeParsedView_title_text) ?: ""
                contentsTextView.text = getString(R.styleable.BarcodeParsedView_contents_text) ?: ""
                updateVisibility()
            } finally {
                recycle()
            }
        }

        copyIconButton.setOnClickListener {
            copyToClipboard(contentsTextView.text.toString())
        }
    }

    private fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("contents", text)
        clipboardManager.setPrimaryClip(clip)
    }

    fun setTitleText(title: String?) {
        titleTextView.text = title ?: ""
    }

    fun setContentsText(contents: String?) {
        contentsTextView.text = contents ?: ""
        updateVisibility()
    }

    fun setTitleText(@StringRes titleRes: Int) {
        titleTextView.setText(titleRes)
    }

    fun setContentsText(@StringRes contentsRes: Int) {
        contentsTextView.setText(contentsRes)
        updateVisibility()
    }

    /**
     * If no contents, we don't show the view
     */
    private fun updateVisibility() {
        if(contentsTextView.text.toString().isEmpty()) {
            this.visibility = GONE
        } else {
            this.visibility = VISIBLE
        }
    }
}