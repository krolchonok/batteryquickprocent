package com.ushastoe.batteryquickprocent

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat

class BatteryQS: TileService() {

    override fun onStartListening() {
        super.onStartListening()
        println("onStartListening")
        qsTile.label = getProcentBat().toString()
        qsTile.state = Tile.STATE_ACTIVE
        val numberDrawable = generateNumberDrawable(applicationContext, getProcentBat(), 1000f, Color.WHITE)
        val numberIcon = drawableToIcon(numberDrawable)
        qsTile.icon = numberIcon
        qsTile.updateTile()
    }

    private fun getProcentBat(): Int {
        val bm = ContextCompat.getSystemService(applicationContext, BatteryManager::class.java)
        bm?.let {
            val batLevel: Int = it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            return batLevel
        }
        return 0
    }

    fun generateNumberDrawable(context: Context, number: Int, textSizeF: Float, textColor: Int): Drawable {
        // Преобразуем число в строку
        val numberText = number.toString()

        // Создаём Paint для рисования текста
        val paint = Paint().apply {
            color = textColor
            textSize = textSizeF
            typeface = Typeface.DEFAULT_BOLD // Можно выбрать любой шрифт
            isAntiAlias = true
            textAlign = Paint.Align.CENTER // Выравнивание текста по центру
        }

        // Измеряем размер текста
        val textBounds = Rect()
        paint.getTextBounds(numberText, 0, numberText.length, textBounds)

        // Учитываем отступы
        val drawableWidth = textBounds.width() + 100F * 2
        val drawableHeight = textBounds.height() + 100F * 2

        // Создаём новый Drawable, чтобы нарисовать цифры
        return object : Drawable() {
            override fun draw(canvas: Canvas) {
                // Рисуем фон с отступами
                val x = intrinsicWidth / 2f
                val y = (intrinsicHeight + textBounds.height()) / 2f
                canvas.drawText(numberText, x, y, paint)
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
            }

            override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
                paint.colorFilter = colorFilter
            }

            override fun getOpacity(): Int {
                return android.graphics.PixelFormat.OPAQUE
            }

            override fun getIntrinsicWidth(): Int {
                return drawableWidth.toInt()
            }

            override fun getIntrinsicHeight(): Int {
                return drawableHeight.toInt()
            }
        }
    }

    fun drawableToIcon(drawable: Drawable): Icon {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return Icon.createWithBitmap(bitmap)
    }


}
