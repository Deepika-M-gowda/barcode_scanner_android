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

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.*
import com.atharok.barcodescanner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FloatingActionMenu(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs) {

    companion object {
        private const val MAX_ROTATION = 45f
        private const val MIN_ROTATION = 0f
    }

    private val fab: FloatingActionButton = FloatingActionButton(context, attrs)
    private val itemsList  = mutableListOf<FloatingActionButton>()
    private var activate = false

    private var closeHeight = 0

    private val iconResource: Int
    private val iconTint: Int
    private val backgroundTint: Int

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.FloatingActionMenu, 0, 0).apply {
            try {
                iconResource = getResourceId(R.styleable.FloatingActionMenu_fab_icon_root, -1)
                iconTint = getColor(R.styleable.FloatingActionMenu_fab_icon_tint, Color.WHITE)
                backgroundTint = getColor(R.styleable.FloatingActionMenu_fab_background_tint, -1)
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        fab.id = View.NO_ID
        if(iconResource!=-1) fab.setImageResource(iconResource)
        if(backgroundTint!=-1) fab.backgroundTintList = ColorStateList.valueOf(backgroundTint)
        fab.supportImageTintList = ColorStateList.valueOf(iconTint)
        fab.setOnClickListener { onClick() }
        fab.visibility = View.VISIBLE
        fab.layoutParams=obtainLayoutParams()

        this.addView(fab, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        closeHeight = fab.measuredHeight
    }

    fun addItem(drawable: Int, listener: OnClickListener?=null) {

        val subFab = FloatingActionButton(context).apply {
            id = View.NO_ID
            if(drawable!=-1) setImageResource(drawable)
            if(backgroundTint!=-1) backgroundTintList = ColorStateList.valueOf(backgroundTint)
            supportImageTintList = ColorStateList.valueOf(iconTint)
            setOnClickListener(listener)
            this.visibility = View.GONE
            layoutParams=obtainLayoutParams()
        }

        this.addView(subFab, 0)

        itemsList.add(subFab)
    }

    fun removeAllItems(){
        if(activate) {
            fab.rotation = MIN_ROTATION
            activate = false
        }

        for(view in itemsList){
            this.removeView(view)
        }

        itemsList.clear()

    }

    private fun obtainLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(marginLeft, marginTop, marginRight, marginBottom)
            gravity = Gravity.BOTTOM
        }
    }

    private fun onClick(){

        activate=!activate

        val angle = if(activate) MAX_ROTATION else MIN_ROTATION
        fab.animate().rotation(angle).start()

        val heightWithMargin = this.fab.height+this.fab.marginTop+this.fab.marginBottom

        var heightLayout = heightWithMargin
        for(fab in itemsList){

            if(!activate) {
                fab.hide()
                fab.animate().translationY(0f)
            } else {
                fab.show()
                fab.animate().translationY(-heightLayout.toFloat())
                heightLayout+=heightWithMargin
            }
        }

        if(activate) {
            this.layoutParams.height = heightLayout
            this.requestLayout()
        }
    }
}