package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.setTextIsSelectableCompat
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixUpiParsedBinding
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment

class BarcodeMatrixUpiParsedFragment : BaseFragment() {

    private var uri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = it.getString(URI_BUNDLE_KEY)
        }
    }

    private var _binding: FragmentBarcodeMatrixUpiParsedBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixUpiParsedBinding.inflate(inflater, container, false)
        if (uri?.startsWith("upi") != true) {
            viewBinding.root.visibility = View.GONE
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uri?.let {

            val entitledTextView = viewBinding.fragmentBarcodeMatrixUpiTemplateEntitledView.templateTextViewTitleTextView
            entitledTextView.setText(R.string.matrix_uri_upi_entitled_label)

            val uriParsed: Uri = Uri.parse(uri)
            configureText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedUpiIdTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedUpiIdLayout,
                text = uriParsed.getQueryParameter("pa")
            )
            configureText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedPayeeNameTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedPayeeNameLayout,
                text = uriParsed.getQueryParameter("pn")
            )
            configureText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedAmountTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedAmountLayout,
                text = uriParsed.getQueryParameter("am")
            )
            configureText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedCurrencyTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedCurrencyLayout,
                text = uriParsed.getQueryParameter("cu")
            )
            configureText(
                textView = viewBinding.fragmentBarcodeMatrixUpiParsedDescriptionTextView,
                layout = viewBinding.fragmentBarcodeMatrixUpiParsedDescriptionLayout,
                text = uriParsed.getQueryParameter("tn")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureText(textView: TextView, layout: View, text: String?) {
        textView.setTextIsSelectableCompat(true)
        displayText(textView, layout, text)
    }

    companion object {
        private const val URI_BUNDLE_KEY = "uriBundleKey"

        @JvmStatic
        fun newInstance(uri: String) =
            BarcodeMatrixUpiParsedFragment().apply {
                arguments = Bundle().apply {
                    putString(URI_BUNDLE_KEY, uri)
                }
            }
    }
}