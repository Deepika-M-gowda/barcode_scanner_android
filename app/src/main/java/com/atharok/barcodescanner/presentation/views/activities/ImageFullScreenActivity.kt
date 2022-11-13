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

package com.atharok.barcodescanner.presentation.views.activities

import android.os.Bundle
import com.atharok.barcodescanner.common.extensions.setImageFromWeb
import com.atharok.barcodescanner.common.utils.IMAGE_URI_KEY
import com.atharok.barcodescanner.databinding.ActivityImageFullScreenBinding

class ImageFullScreenActivity : BaseActivity() {

    private val viewBinding: ActivityImageFullScreenBinding by lazy { ActivityImageFullScreenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityImageFullScreenToolbar)
        supportActionBar?.title = null

        val imageUri: String? = intent.getStringExtra(IMAGE_URI_KEY)

        viewBinding.activityImageFullScreenImageView.setImageFromWeb(imageUri)

        setContentView(viewBinding.root)
    }
}