/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.bookProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.convertToString
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_KEY
import com.atharok.barcodescanner.databinding.FragmentBookAnalysisBinding
import com.atharok.barcodescanner.domain.entity.analysis.BookBarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.about.BarcodeAboutOverviewFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.ApiAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ProductOverviewFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class BookAnalysisFragment : ApiAnalysisFragment<BookBarcodeAnalysis>() {

    private var _binding: FragmentBookAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(analysis: BookBarcodeAnalysis) {
        super.start(analysis)
        viewBinding.fragmentBookAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()
        configureProductOverviewFragment(analysis)
        configureSummary(analysis)
        configureCategories(analysis)
        configureNbPages(analysis)
        configureContributions(analysis)
        configurePublishDate(analysis)
        configureOriginalTitle(analysis)
        configureMoreEntitled(analysis)
        configureBarcodeAboutOverviewFragment()
    }

    private fun configureProductOverviewFragment(bookProduct: BookBarcodeAnalysis) {
        val fragment = ProductOverviewFragment.newInstance(
            imageUrl = bookProduct.coverUrl,
            title = bookProduct.title ?: getString(R.string.bar_code_type_unknown_book_title),
            subtitle1 = bookProduct.subtitle,
            subtitle2 = bookProduct.authors?.convertToString(),
            subtitle3 = bookProduct.publishers?.convertToString()
        )

        applyFragment(viewBinding.fragmentBookAnalysisOverviewLayout.id, fragment)
    }

    private fun configureSummary(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisSummaryFrameLayout,
        title = getString(R.string.book_product_summary_label),
        contents = bookProduct.description,
        iconDrawableResource = R.drawable.ic_book_24
    )

    private fun configureCategories(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisCategoriesFrameLayout,
        title = getString(R.string.categories_label),
        contents = bookProduct.categories?.convertToString()
    )

    private fun configureNbPages(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisNbPagesFrameLayout,
        title = getString(R.string.book_product_pages_number_label),
        contents = if(bookProduct.numberPages != null) getString(R.string.book_product_pages_number, bookProduct.numberPages.toString()) else null
    )

    private fun configureContributions(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisContributionsFrameLayout,
        title = getString(R.string.book_product_contributions_label),
        contents = bookProduct.contributions?.convertToString()
    )

    private fun configurePublishDate(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisPublicationDateFrameLayout,
        title = getString(R.string.book_product_publish_date_label),
        contents = bookProduct.publishDate
    )

    private fun configureOriginalTitle(bookProduct: BookBarcodeAnalysis) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentBookAnalysisOriginalTitleFrameLayout,
        title = getString(R.string.book_product_original_title_label),
        contents = bookProduct.originalTitle
    )

    private fun configureMoreEntitled(bookProduct: BookBarcodeAnalysis) {
        if (!bookProduct.description.isNullOrBlank() || !bookProduct.categories.isNullOrEmpty()
            || bookProduct.numberPages != null || !bookProduct.contributions.isNullOrEmpty()
            || !bookProduct.publishDate.isNullOrBlank() || !bookProduct.originalTitle.isNullOrBlank()) {
            viewBinding.fragmentBookAnalysisMoreEntitledLayout.visibility = View.VISIBLE
        } else {
            viewBinding.fragmentBookAnalysisMoreEntitledLayout.visibility = View.GONE
        }
    }

    private fun configureBarcodeAboutOverviewFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBookAnalysisBarcodeAboutOverviewFrameLayout.id,
        fragmentClass = BarcodeAboutOverviewFragment::class,
        args = arguments
    )

    companion object {
        fun newInstance(bookProduct: BookBarcodeAnalysis) = BookAnalysisFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(BARCODE_ANALYSIS_KEY, bookProduct)
            }
        }
    }
}