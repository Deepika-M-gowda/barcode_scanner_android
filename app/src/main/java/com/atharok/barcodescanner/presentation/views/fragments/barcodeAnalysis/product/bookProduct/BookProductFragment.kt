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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.bookProduct

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.atharok.barcodescanner.databinding.FragmentBookProductBinding
import com.atharok.barcodescanner.domain.entity.product.BookProduct
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.extentions.convertToString
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.AboutBarcodeFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeContentsFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ProductOverviewFragment
import com.atharok.barcodescanner.common.extentions.fixAnimateLayoutChangesInNestedScroll
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class BookProductFragment : ProductBarcodeFragment<BookProduct>() {

    private var _binding: FragmentBookProductBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookProductBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BookProduct){

        viewBinding.fragmentBookProductOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureProductOverviewFragment(product)
        configureSummary(product)
        configureCategories(product)
        configureNbPages(product)
        configureContributions(product)
        configurePublishDate(product)
        configureOriginalTitle(product)

        configureMoreEntitled(product)


        configureAboutBarcode()
    }

    private fun configureProductOverviewFragment(bookProduct: BookProduct){

        val fragment = ProductOverviewFragment.newInstance(
            imageUrl = bookProduct.coverUrl,
            title = bookProduct.title ?: getString(R.string.bar_code_type_unknown_book_title),
            subtitle1 = bookProduct.subtitle,
            subtitle2 = bookProduct.authors?.convertToString(),
            subtitle3 = bookProduct.publishers?.convertToString()
        )

        applyFragment(viewBinding.fragmentBookProductOverviewLayout.id, fragment)
    }

    private fun configureSummary(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductSummaryFrameLayout,
        title = getString(R.string.book_product_summary_label),
        contents = bookProduct.description,
        iconDrawableResource = R.drawable.ic_book_24
    )

    private fun configureCategories(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductCategoriesFrameLayout,
        title = getString(R.string.categories_label),
        contents = bookProduct.categories?.convertToString()
    )

    private fun configureNbPages(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductNbPagesFrameLayout,
        title = getString(R.string.book_product_pages_number_label),
        contents = if(bookProduct.numberPages != null) getString(R.string.book_product_pages_number, bookProduct.numberPages.toString()) else null
    )

    private fun configureContributions(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductContributionsFrameLayout,
        title = getString(R.string.book_product_contributions_label),
        contents = bookProduct.contributions?.convertToString()
    )

    private fun configurePublishDate(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductPublicationDateFrameLayout,
        title = getString(R.string.book_product_publish_date_label),
        contents = bookProduct.publishDate
    )

    private fun configureOriginalTitle(bookProduct: BookProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBookProductOriginalTitleFrameLayout,
        title = getString(R.string.book_product_original_title_label),
        contents = bookProduct.originalTitle
    )

    private fun configureMoreEntitled(bookProduct: BookProduct){
        if (!bookProduct.description.isNullOrBlank() || !bookProduct.categories.isNullOrEmpty()
            || bookProduct.numberPages != null || !bookProduct.contributions.isNullOrEmpty()
            || !bookProduct.publishDate.isNullOrBlank() || !bookProduct.originalTitle.isNullOrBlank()) {

            viewBinding.fragmentBookProductMoreEntitledLayout.visibility = View.VISIBLE
            val entitled: String = getString(R.string.book_product_more_label)
            viewBinding.fragmentBookProductMoreEntitledTextViewTemplate.root.text = entitled
        }else{
            viewBinding.fragmentBookProductMoreEntitledLayout.visibility = View.GONE
        }
    }

    private fun configureAboutBarcode(){
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.fragmentBookProductAboutBarcodeEntitledTextViewTemplate.root.text = entitled

        applyFragment(viewBinding.fragmentBookProductBarcodeContentsFrameLayout.id, BarcodeContentsFragment::class, arguments)
        applyFragment(viewBinding.fragmentBookProductAboutBarcodeFrameLayout.id, AboutBarcodeFragment::class, arguments)
    }

    companion object {
        fun newInstance(bookProduct: BookProduct) = BookProductFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, bookProduct)
            }
        }
    }
}