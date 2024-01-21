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

package com.atharok.barcodescanner.presentation.customView

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.atharok.barcodescanner.R

class ExpandableView(context: Context, attrs: AttributeSet?): LinearLayout(context, attrs), View.OnClickListener {

    companion object {
        private const val MAX_ROTATION = 180f
        private const val MIN_ROTATION = 0f
    }

    private var isOpen = false

    private var headerIconView: ImageView? = null
    private val iconIdHeaderResource: Int

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ExpandableView, 0, 0).apply {
            try {
                isOpen = getBoolean(R.styleable.ExpandableView_open, false)
                iconIdHeaderResource = getResourceId(R.styleable.ExpandableView_iconHeaderId, -1)
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Fonctionne uniquement si l'ExpandableView possède 2 éléments: le Header et le Body dans le XML. Sinon déclenche exception
        check(childCount == 2) { "ExpandableView must have two children" }

        // Si on a une référence vers une image pour l'icone
        headerIconView = if(iconIdHeaderResource != -1) findViewById<ImageView>(iconIdHeaderResource) else null

        /*
        java.lang.IllegalStateException: Page(s) contain a ViewGroup with a LayoutTransition
        (or animateLayoutChanges="true"), which interferes with the scrolling animation.
        Make sure to call getLayoutTransition().setAnimateParentHierarchy(false) on all
        ViewGroups with a LayoutTransition before an animation is started.
        at androidx.viewpager2.widget.ScrollEventAdapter.updateScrollEventValues
         */
        //layoutTransition?.setAnimateParentHierarchy(false)

        // Permet de synchroniser le rapetissement de la vue avec les autres vues extérieurs
        layoutTransition?.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0)

        val headerView = children.elementAt(0)
        headerView.setOnClickListener(this)
        //setOnClickListener(this)


        val subView = children.elementAt(1)
        if(!isOpen) {

            subView.visibility = View.GONE
        }else{
            // La vue est ouverte, donc l'icone doit être rotationné par défaut
            headerIconView?.rotation = MAX_ROTATION
            subView.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        if(!isOpen)
            open(children.elementAt(1))
        else
            close(children.elementAt(1))
    }

    private fun open(view: View){
        isOpen = true

        // Rotation Icone Header
        headerIconView?.animate()?.rotation(MAX_ROTATION)?.start()
        view.visibility = View.VISIBLE
    }

    private fun close(view: View){
        isOpen = false

        // Rotation Icone Header
        headerIconView?.animate()?.rotation(MIN_ROTATION)?.start()
        view.visibility = View.GONE
    }

    fun open(){
        if(!isOpen)
            open(children.elementAt(1))
    }

    fun close(){
        if(isOpen)
            close(children.elementAt(1))
    }
}