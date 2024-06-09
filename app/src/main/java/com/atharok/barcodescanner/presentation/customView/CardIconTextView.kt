package com.atharok.barcodescanner.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.atharok.barcodescanner.R
import com.google.android.material.card.MaterialCardView

class CardIconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val card: MaterialCardView
    private val iconImageView: ImageView
    private val contentTextView: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.template_card_icon_text_view, this, true)

        card = view.findViewById(R.id.template_card_icon_text_view_card)
        iconImageView = view.findViewById(R.id.template_card_icon_text_view_icon)
        contentTextView = view.findViewById(R.id.template_card_icon_text_view_text)

        context.theme.obtainStyledAttributes(attrs, R.styleable.CardIconTextView, defStyleAttr, defStyleRes).apply {
            try {
                val iconRes = getResourceId(R.styleable.CardIconTextView_iconRes, -1)
                if(iconRes!=-1)
                    iconImageView.setImageResource(iconRes)
                contentTextView.text = getString(R.styleable.CardIconTextView_text) ?: ""
            } finally {
                recycle()
            }
        }
    }

    fun getRoot(): MaterialCardView = card

    fun setIcon(@DrawableRes drawableRes: Int) {
        iconImageView.setImageResource(drawableRes)
    }

    fun setContent(content: String) {
        contentTextView.text = content
    }

    fun getContent(): String = contentTextView.text.toString()

}