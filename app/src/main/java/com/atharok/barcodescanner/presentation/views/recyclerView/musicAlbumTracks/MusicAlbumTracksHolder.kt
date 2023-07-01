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

package com.atharok.barcodescanner.presentation.views.recyclerView.musicAlbumTracks

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemMusicAlbumTrackBinding
import com.atharok.barcodescanner.domain.entity.product.musicProduct.AlbumTrack

class MusicAlbumTracksHolder(private val viewBinding: RecyclerViewItemMusicAlbumTrackBinding)
    : RecyclerView.ViewHolder(viewBinding.root) {

    fun updateItem(track: AlbumTrack, artist: String?) {
        configureTextView(viewBinding.recyclerViewItemMusicAlbumTrackNumberTextView, track.position?.let { "$it" })
        configureTextView(viewBinding.recyclerViewItemMusicAlbumTrackTitleTextView, track.title)
        configureTextView(viewBinding.recyclerViewItemMusicAlbumTrackLengthTextView, track.length?.let { convertMillisecondsToMinutesSeconds(it) })
        configureTextView(viewBinding.recyclerViewItemMusicAlbumTrackArtistTextView, artist)
    }

    private fun configureTextView(textView: TextView, text: String?) {
        textView.visibility = if(text.isNullOrEmpty()) View.GONE else View.VISIBLE
        textView.text = text
    }

    private fun convertMillisecondsToMinutesSeconds(time: Long): String {
        val minutes = time / 60000
        val seconds = (time % 60000) / 1000
        return String.format("%d:%02d", minutes, seconds)
    }
}