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
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import com.atharok.barcodescanner.R

class HorizontalGraphView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    // ---- Guide ----
    private class Point(val x: Float, val y: Float)

    fun setValues(guidePosition: Float, low: Float, high: Float){
        if(low > high) throw Exception("La valeur Low est supérieur à la valeur High")
        if(low<minValue) throw Exception("La valeur Low doit être supérieur à 0")
        if(guidePosition<minValue) throw Exception("La valeur Guide Position doit être supérieur à 0")

        this.lowValue = low
        this.highValue = high
        this.guideValue = guidePosition

        maxValue = high+low

        if(guideValue>maxValue)
            maxValue = guideValue+1

        invalidate()
    }

    private val minValue: Float = 0.0f
    private var maxValue: Float = 3.0f
    private var lowValue: Float = 1.0f
    private var highValue: Float = 2.0f
    private var guideValue = 0.0f


    //private val padX: Float = (paddingLeft + paddingRight).toFloat()
    private val padY: Float = (paddingTop + paddingBottom).toFloat()

    private val lowBar: Paint
    private val mediumBar: Paint
    private val highBar: Paint
    private val textPaint: Paint
    private val guidePaint: Paint


    private val barHeight: Float
    private val spaceBetweenBarAndText = 4
    private val textSize: Float

    private val offset: Float

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.HorizontalGraphView, 0, 0).apply {
            try {
                barHeight = getDimension(R.styleable.HorizontalGraphView_barHeight, 10f)
                textSize = getDimension(R.styleable.HorizontalGraphView_textSize, 10f)

                val fontFamily = getString(R.styleable.HorizontalGraphView_fontFamily)
                val textColor = getColor(R.styleable.HorizontalGraphView_textColor, Color.BLACK)
                val lowBarColor = getColor(R.styleable.HorizontalGraphView_lowBarColor, Color.GREEN)
                val mediumBarColor = getColor(R.styleable.HorizontalGraphView_mediumBarColor, Color.YELLOW)
                val highBarColor = getColor(R.styleable.HorizontalGraphView_highBarColor, Color.RED)

                textPaint = Paint(ANTI_ALIAS_FLAG).apply {
                    color = textColor
                    textSize = this@HorizontalGraphView.textSize
                    if(!fontFamily.isNullOrEmpty())
                        typeface = Typeface.create(fontFamily, Typeface.NORMAL)//ResourcesCompat.getFont(context, fontResource)
                }

                guidePaint = Paint(ANTI_ALIAS_FLAG).apply {
                    color = textColor
                    style = Paint.Style.FILL
                }

                lowBar = Paint(ANTI_ALIAS_FLAG).apply {
                    color = lowBarColor
                    style = Paint.Style.FILL
                }

                mediumBar = Paint(ANTI_ALIAS_FLAG).apply {
                    color = mediumBarColor
                    style = Paint.Style.FILL
                }

                highBar = Paint(ANTI_ALIAS_FLAG).apply {
                    color = highBarColor
                    style = Paint.Style.FILL
                }

                offset = barHeight*3


            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minHeight = padY + textSize + barHeight*5 + spaceBetweenBarAndText
        val height = resolveSizeAndState(minHeight.toInt(), heightMeasureSpec, 1)

        setMeasuredDimension(widthMeasureSpec, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val barLeftPosX = offset
        val barRightPosX = width-offset

        val mult = if(maxValue!=0f) (barRightPosX-barLeftPosX)/maxValue else 1f

        val textCenter = textSize

        val barTop = barHeight*3+barHeight/2
        val barBottom = barTop+barHeight


        val posMinValue = minValue*mult+barLeftPosX
        val posMaxValue = maxValue*mult+barLeftPosX
        val posLowValue = lowValue*mult+barLeftPosX
        val posHighValue = highValue*mult+barLeftPosX
        val posGuideValue = guideValue*mult+barLeftPosX

        val positionLowTextX = posLowValue-textCenter
        val positionHighTextX = posHighValue-textCenter
        val positionGuideTextX = posGuideValue-textCenter

        val positionTextY = barBottom+textSize-padY
        val positionGuideTextY = barTop-barHeight-spaceBetweenBarAndText*2+padY

        canvas?.apply {
            //drawColor(Color.BLUE)
            drawRect(posMinValue, barTop, posLowValue, barBottom, lowBar)
            drawRect(posLowValue, barTop, posHighValue, barBottom, mediumBar)
            drawRect(posHighValue, barTop, posMaxValue, barBottom, highBar)

            drawText("$lowValue", positionLowTextX, positionTextY, textPaint)
            drawText("$highValue", positionHighTextX, positionTextY, textPaint)

            drawPath(obtainGuidePath(posGuideValue, barTop), guidePaint)
            drawText("$guideValue", positionGuideTextX, positionGuideTextY, textPaint)
        }
    }

    /**
     * Génère un triangle permettant de voir la position choisie sur le graphique.
     */
    private fun obtainGuidePath(posGuideValue: Float, barTop: Float): Path{

        val guideA = Point(posGuideValue, barTop)
        val guideB = Point(posGuideValue-barHeight, barTop-barHeight)
        val guideC = Point(posGuideValue+barHeight, barTop-barHeight)

        return Path().apply {
            moveTo(guideA.x, guideA.y)
            lineTo(guideB.x, guideB.y)
            lineTo(guideC.x, guideC.y)
            lineTo(guideA.x, guideA.y)
            close()
        }
    }
}