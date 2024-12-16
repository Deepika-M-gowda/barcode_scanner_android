package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeNameModalBottomSheetBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BarcodeNameModalBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBarcodeNameModalBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeNameModalBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.serializable(BARCODE_KEY, Barcode::class.java)?.let { barcode: Barcode ->

            binding.fragmentBarcodeNameModalBottomSheetInputEditText.apply {
                this.setText(barcode.name)
                this.requestFocus()
            }

            binding.fragmentBarcodeNameModalBottomSheetApplyButton.setOnClickListener {
                // Récupère le contenu du TextInputEditText
                val newBarcodeName: String = binding.fragmentBarcodeNameModalBottomSheetInputEditText.text.toString()

                (requireActivity() as? BarcodeAnalysisActivity)?.updateBarcodeName(barcode, newBarcodeName)
                dismiss()
            }
        } ?: run { dismiss() }
    }


    companion object {
        @JvmStatic
        fun newInstance(barcode: Barcode) =
            BarcodeNameModalBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BARCODE_KEY, barcode)
                }
            }
    }
}