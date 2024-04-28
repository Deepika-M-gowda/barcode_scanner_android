package com.atharok.barcodescanner.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.CUSTOM_SEARCH_URL_BARCODE_KEY_WORD
import com.atharok.barcodescanner.common.utils.CUSTOM_URL_KEY
import com.atharok.barcodescanner.databinding.ActivityCustomSearchUrlCreatorBinding
import com.atharok.barcodescanner.domain.entity.customUrl.CustomUrl

class CustomSearchUrlCreatorActivity : BaseActivity() {

    companion object {
        private const val HTTPS = "https://"
        const val RESULT_CODE_INSERT = 0
        const val RESULT_CODE_UPDATE = 1
    }

    private val viewBinding: ActivityCustomSearchUrlCreatorBinding by lazy {
        ActivityCustomSearchUrlCreatorBinding.inflate(layoutInflater)
    }
    override val rootView: View get() = viewBinding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityCustomSearchUrlCreatorActivityLayout.toolbar)

        intent.serializable(CUSTOM_SEARCH_URL_BARCODE_KEY_WORD, CustomUrl::class.java)?.let {
            supportActionBar?.setTitle(R.string.custom_search_urls_modify_url)
            configureInputEditText(it)
        } ?: run {
            configureUrlInputEditText()
        }
        configureErrorMessageTextView()
        configureInfoMessageTextView()
        configureExampleMessageTextView()

        setContentView(rootView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_confirm, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_activity_confirm_item -> sendResult()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureInputEditText(customUrl: CustomUrl) {
        viewBinding.activityCustomSearchUrlCreatorNameTextInputEditText.setText(customUrl.name)
        viewBinding.activityCustomSearchUrlCreatorUrlTextInputEditText.setText(customUrl.url)
    }

    private fun configureUrlInputEditText() {
        val urlInputEditText = viewBinding.activityCustomSearchUrlCreatorUrlTextInputEditText
        if(urlInputEditText.text.isNullOrEmpty()) {
            urlInputEditText.setText(HTTPS)
        }
    }

    private fun configureInfoMessageTextView() {
        viewBinding.activityCustomSearchUrlCreatorInfoMessageTextView.text =
            getString(R.string.custom_search_urls_add_info, CUSTOM_SEARCH_URL_BARCODE_KEY_WORD)
    }

    private fun configureExampleMessageTextView() {
        val exampleStrBuilder = StringBuilder()
        exampleStrBuilder.append(getString(R.string.search_engine_google_url, CUSTOM_SEARCH_URL_BARCODE_KEY_WORD))
        exampleStrBuilder.append("\n")
        exampleStrBuilder.append(getString(R.string.search_engine_open_food_facts_product_url, CUSTOM_SEARCH_URL_BARCODE_KEY_WORD))
        exampleStrBuilder.append("\n")
        exampleStrBuilder.append(getString(R.string.search_engine_musicbrainz_product_url, CUSTOM_SEARCH_URL_BARCODE_KEY_WORD))

        viewBinding.activityCustomSearchUrlCreatorInfoExampleMessageTextView.text =
            exampleStrBuilder.toString()
    }

    private fun configureErrorMessageTextView(text: String = getString(R.string.empty), isVisible: Boolean = false) {
        viewBinding.activityCustomSearchUrlCreatorErrorMessage.text = text
        viewBinding.activityCustomSearchUrlCreatorErrorMessage.visibility =
            if(isVisible) View.VISIBLE else View.GONE
    }

    private fun sendResult() {
        val name = viewBinding.activityCustomSearchUrlCreatorNameTextInputEditText.text.toString()
        val url = viewBinding.activityCustomSearchUrlCreatorUrlTextInputEditText.text.toString()

        if(check(name, url)) {

            val resultIntent = Intent()

            // Si il y a un Intent, cela signifie une modification de l'URL
            intent.serializable(CUSTOM_SEARCH_URL_BARCODE_KEY_WORD, CustomUrl::class.java)?.let {
                val customUrl = CustomUrl(id = it.id, name = name, url = url)
                resultIntent.putExtra(CUSTOM_URL_KEY, customUrl)
                setResult(RESULT_CODE_UPDATE, resultIntent)
            } ?: run {
                val customUrl = CustomUrl(name = name, url = url)
                resultIntent.putExtra(CUSTOM_URL_KEY, customUrl)
                setResult(RESULT_CODE_INSERT, resultIntent)
            }

            finish()
        }
    }

    private fun check(name: String, url: String): Boolean = when {
        name.isBlank() || url.isBlank() -> {
            configureErrorMessageTextView(
                text = getString(R.string.error_empty_fields),
                isVisible = true
            )
            false
        }

        !url.contains(CUSTOM_SEARCH_URL_BARCODE_KEY_WORD, false) -> {
            configureErrorMessageTextView(
                getString(R.string.custom_search_urls_error_url, CUSTOM_SEARCH_URL_BARCODE_KEY_WORD),
                isVisible = true
            )
            false
        }

        else -> {
            configureErrorMessageTextView()
            true
        }
    }
}