package com.therapist.assistant.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * رندر کارت ویزیت به Bitmap و اشتراک به‌صورت تصویر.
 */
object BusinessCardImage {

    data class Style(
        val primary: Int,
        val secondary: Int,
        val accent: Int,
        val onPrimary: Int = 0xFFFFFFFF.toInt(),
        val textDark: Int = 0xFF0F172A.toInt(),
        val textMuted: Int = 0xFF475569.toInt()
    )

    fun render(
        name: String,
        role: String,
        clinic: String,
        phone: String,
        email: String,
        website: String,
        address: String,
        tagline: String,
        style: Style,
        qrBitmap: Bitmap?,
        width: Int = 1080,
        height: Int = 640
    ): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val density = width / 360f

        fun dp(v: Float) = v * density

        // Background card with rounded corners via clip
        val card = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val path = Path().apply { addRoundRect(card, dp(28f), dp(28f), Path.Direction.CW) }
        c.save()
        c.clipPath(path)

        // White base
        c.drawColor(0xFFF8FAFC.toInt())

        // Gradient header band
        val headerH = height * 0.42f
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), headerH,
                intArrayOf(style.primary, style.secondary),
                null,
                Shader.TileMode.CLAMP
            )
        }
        c.drawRect(0f, 0f, width.toFloat(), headerH, headerPaint)

        // Decorative circle
        val deco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x33FFFFFF
            style = android.graphics.Paint.Style.FILL // 👈 اصلاح شد
        }
        c.drawCircle(width * 0.92f, dp(20f), dp(90f), deco)
        c.drawCircle(width * 0.05f, headerH + dp(10f), dp(50f), deco)

        // Avatar circle
        val avR = dp(34f)
        val avCx = dp(40f) + avR
        val avCy = dp(36f) + avR
        val avBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0x44FFFFFF }
        c.drawCircle(avCx, avCy, avR, avBg)
        val avStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xAAFFFFFF.toInt()
            style = android.graphics.Paint.Style.STROKE // 👈 اصلاح شد
            strokeWidth = dp(2.5f)
        }
        c.drawCircle(avCx, avCy, avR, avStroke)
        val initial = name.trim().firstOrNull()?.toString() ?: "د"
        val initialPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.onPrimary
            textSize = dp(28f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val iFm = initialPaint.fontMetrics
        c.drawText(initial, avCx, avCy - (iFm.ascent + iFm.descent) / 2f, initialPaint)

        // Name & role on header
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.onPrimary
            textSize = dp(26f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        val rolePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xEEFFFFFF.toInt()
            textSize = dp(14f)
            textAlign = Paint.Align.RIGHT
        }
        val clinicPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xCCFFFFFF.toInt()
            textSize = dp(13f)
            textAlign = Paint.Align.RIGHT
        }
        val right = width - dp(28f)
        var ty = dp(48f)
        c.drawText(name.ifBlank { "درمانگر" }, right, ty, namePaint)
        ty += dp(28f)
        c.drawText(role.ifBlank { "روان‌شناس" }, right, ty, rolePaint)
        if (clinic.isNotBlank()) {
            ty += dp(22f)
            c.drawText(clinic, right, ty, clinicPaint)
        }
        if (tagline.isNotBlank()) {
            ty += dp(22f)
            val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xBBFFFFFF.toInt()
                textSize = dp(12f)
                textAlign = Paint.Align.RIGHT
            }
            c.drawText(tagline, right, ty, tagPaint)
        }

        // Body contact lines
        val bodyY0 = headerH + dp(28f)
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = style.primary }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.textDark
            textSize = dp(14f)
            textAlign = Paint.Align.RIGHT
        }
        val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.textMuted
            textSize = dp(12f)
            textAlign = Paint.Align.RIGHT
        }

        var by = bodyY0
        fun line(label: String, value: String) {
            if (value.isBlank()) return
            // small accent dot
            c.drawCircle(width - dp(28f), by - dp(5f), dp(4f), iconPaint)
            c.drawText(value, width - dp(42f), by, linePaint)
            by += dp(26f)
        }
        if (phone.isNotBlank()) line("phone", phone)
        if (email.isNotBlank()) line("email", email)
        if (website.isNotBlank()) line("web", website)
        if (address.isNotBlank()) {
            c.drawCircle(width - dp(28f), by - dp(5f), dp(4f), iconPaint)
            c.drawText(address, width - dp(42f), by, mutedPaint)
            by += dp(26f)
        }

        // Footer brand
        val foot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.textMuted
            textSize = dp(11f)
            textAlign = Paint.Align.RIGHT
        }
        c.drawText("درمانگرنو · کارت ویزیت دیجیتال", width - dp(28f), height - dp(22f), foot)

        // QR on left bottom
        qrBitmap?.let { qr ->
            val qrSize = dp(120f).toInt().coerceAtLeast(120)
            val scaled = Bitmap.createScaledBitmap(qr, qrSize, qrSize, true)
            val qx = dp(24f)
            val qy = height - qrSize - dp(28f)
            val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFFFFF.toInt() }
            val r = RectF(qx - dp(8f), qy - dp(8f), qx + qrSize + dp(8f), qy + qrSize + dp(8f))
            c.drawRoundRect(r, dp(12f), dp(12f), bg)
            c.drawBitmap(scaled, qx, qy, null)
            if (scaled !== qr) scaled.recycle()
        }

        c.restore()
        return bmp
    }

    fun saveToCache(context: Context, bitmap: Bitmap, fileName: String = "business_card.png"): File {
        val dir = File(context.cacheDir, "cards").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun shareImage(context: Context, file: File, title: String = "کارت ویزیت") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "ارسال کارت ویزیت به‌صورت تصویر"))
    }
}
