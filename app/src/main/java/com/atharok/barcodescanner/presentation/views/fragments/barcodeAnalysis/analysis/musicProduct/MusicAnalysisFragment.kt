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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.musicProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.convertToString
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_KEY
import com.atharok.barcodescanner.databinding.FragmentMusicAnalysisBinding
import com.atharok.barcodescanner.domain.entity.analysis.MusicBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.musicProduct.AlbumTrack
import com.atharok.barcodescanner.domain.library.DateConverter
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.about.BarcodeAboutOverviewFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.ApiAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ProductOverviewFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.musicAlbumTracks.MusicAlbumTracksAdapter
import org.koin.android.ext.android.get

/**
 * Vue affichant les informations de MusicBrainz.
 */
class MusicAnalysisFragment : ApiAnalysisFragment<MusicBarcodeAnalysis>() {

    private var _binding: FragmentMusicAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(analysis: MusicBarcodeAnalysis) {
        super.start(analysis)
        viewBinding.fragmentMusicAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()
        configureProductOverviewFragment(analysis)
        configureTracksView(analysis.albumTracks, analysis.artists?.convertToString())
        configureBarcodeAboutOverviewFragment()
    }

    private fun configureProductOverviewFragment(product: MusicBarcodeAnalysis) {
        val fragment = ProductOverviewFragment.newInstance(
            imageUrl = product.coverUrl,
            title = product.album ?: getString(R.string.bar_code_type_unknown_music_album_title),
            subtitle1 = product.artists?.convertToString(),
            subtitle2 = get<DateConverter>().convertDateToLocalizedFormat(requireContext(), product.date),
            subtitle3 = product.trackCount?.let { getString(R.string.music_product_tracks_number, "$it") }
        )
        applyFragment(viewBinding.fragmentMusicAnalysisOverviewLayout.id, fragment)
    }

    private fun configureTracksView(tracks: List<AlbumTrack>?, artists: String?) {
        if(tracks.isNullOrEmpty()) {
            viewBinding.fragmentMusicAnalysisTracksCardView.visibility = View.GONE
        } else {
            val linearLayoutManager = LinearLayoutManager(requireActivity())
            viewBinding.fragmentMusicAnalysisTracksRecyclerView.apply {
                this.adapter = MusicAlbumTracksAdapter(tracks, artists)
                this.layoutManager = linearLayoutManager
                this.addItemDecoration(DividerItemDecoration(requireActivity(), linearLayoutManager.orientation))
            }
            viewBinding.fragmentMusicAnalysisTracksCardView.visibility = View.VISIBLE
        }
    }

    private fun configureBarcodeAboutOverviewFragment() = applyFragment(
        containerViewId = viewBinding.fragmentMusicAnalysisBarcodeAboutOverviewFrameLayout.id,
        fragmentClass = BarcodeAboutOverviewFragment::class,
        args = arguments
    )

    companion object {
        fun newInstance(musicProduct: MusicBarcodeAnalysis) = MusicAnalysisFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(BARCODE_ANALYSIS_KEY, musicProduct)
            }
        }
    }
}