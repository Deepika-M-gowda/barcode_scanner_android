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

package com.atharok.barcodescanner.presentation.views.fragments.templates

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentProductOverviewBinding
import com.atharok.barcodescanner.common.utils.IMAGE_URI_KEY
import com.atharok.barcodescanner.presentation.views.activities.ImageFullScreenActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.atharok.barcodescanner.common.extentions.setImageFromWeb
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 * Use the [ProductOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductOverviewFragment : BaseFragment() {

    private var title: String? = null
    private var subtitle1: String? = null
    private var subtitle2: String? = null
    private var subtitle3: String? = null
    private var imageUrl: String? = null

    private var _binding: FragmentProductOverviewBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE_KEY)
            subtitle1 = it.getString(SUBTITLE_1_KEY)
            subtitle2 = it.getString(SUBTITLE_2_KEY)
            subtitle3 = it.getString(SUBTITLE_3_KEY)
            imageUrl = it.getString(IMAGE_URL_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductOverviewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureImage(imageUrl)
        configureTitle(title)
        configureSubtitle1(subtitle1)
        configureSubtitle2(subtitle2)
        configureSubtitle3(subtitle3)
    }

    private fun configureTitle(title: String?) = displayText(
        textView = viewBinding.fragmentProductOverviewTitleTextView,
        layout = viewBinding.fragmentProductOverviewTitleTextView,
        text = title
    )

    private fun configureSubtitle1(subtitle: String?) = displayText(
        textView = viewBinding.fragmentProductOverviewSubtitle1TextView,
        layout = viewBinding.fragmentProductOverviewSubtitle1TextView,
        text = subtitle
    )

    private fun configureSubtitle2(subtitle: String?) = displayText(
        textView = viewBinding.fragmentProductOverviewSubtitle2TextView,
        layout = viewBinding.fragmentProductOverviewSubtitle2TextView,
        text = subtitle
    )

    private fun configureSubtitle3(subtitle: String?) = displayText(
        textView = viewBinding.fragmentProductOverviewSubtitle3TextView,
        layout = viewBinding.fragmentProductOverviewSubtitle3TextView,
        text = subtitle
    )

    private fun configureImage(imageFrontUrl: String?){
        viewBinding.fragmentProductOverviewImageView.setImageFromWeb(
            url = imageFrontUrl,
            centerCrop = true,
            layout = viewBinding.fragmentProductOverviewImageLayout
        )

        if(imageFrontUrl != null) {
            viewBinding.fragmentProductOverviewImageView.setOnClickListener {
                startImageFullScreenActivity(imageFrontUrl)
            }
        }
    }

    private fun startImageFullScreenActivity(imageFrontUrl: String){
        val intent = getImageFullScreenActivityIntent().apply {
            putExtra(IMAGE_URI_KEY, imageFrontUrl)
        }

        val options = generateTransitionAnimation(viewBinding.fragmentProductOverviewImageView)

        startActivity(intent, options.toBundle())
    }

    private fun getImageFullScreenActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(ImageFullScreenActivity::class) }

    private fun generateTransitionAnimation(view: View?): ActivityOptions {
        return ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            view,
            getString(R.string.animation_activity_transition)
        )
    }

    companion object {
        private const val IMAGE_URL_KEY = "imageUrlKey"
        private const val TITLE_KEY = "titleKey"
        private const val SUBTITLE_1_KEY = "subtitle1Key"
        private const val SUBTITLE_2_KEY = "subtitle2Key"
        private const val SUBTITLE_3_KEY = "subtitle3Key"

        @JvmStatic
        fun newInstance(imageUrl: String?, title: String, subtitle1: String? = null, subtitle2: String? = null, subtitle3: String? = null) =
            ProductOverviewFragment().apply {
                arguments = get<Bundle>().apply {

                    putString(TITLE_KEY, title)

                    if(imageUrl != null)
                        putString(IMAGE_URL_KEY, imageUrl)

                    if(subtitle1 != null)
                        putString(SUBTITLE_1_KEY, subtitle1)

                    if(subtitle2 != null)
                        putString(SUBTITLE_2_KEY, subtitle2)

                    if(subtitle3 != null)
                        putString(SUBTITLE_3_KEY, subtitle3)
                }
            }
    }
}