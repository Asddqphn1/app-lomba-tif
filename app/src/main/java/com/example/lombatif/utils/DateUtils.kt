// file: utils/DateUtils.kt

package com.example.lombatif.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


fun formatTanggal(isoDate: String, pattern: String = "d MMMM yyyy, HH:mm 'WIB'"): String {
    // Pola untuk mem-parsing input string dari API. 'Z' menandakan UTC.
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    // Set timezone parser ke UTC agar sesuai dengan 'Z'
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        // 1. Parsing string input menjadi objek Date
        val date = inputFormat.parse(isoDate)

        // 2. Buat formatter untuk output yang diinginkan
        val outputFormat = SimpleDateFormat(pattern, Locale("id", "ID"))
        // Set timezone output ke WIB (GMT+7)
        outputFormat.timeZone = TimeZone.getTimeZone("GMT+7")

        // 3. Format objek Date menjadi string output dan kembalikan
        date?.let { outputFormat.format(it) } ?: "Tanggal Tidak Valid"

    } catch (e: Exception) {
        // Jika terjadi error saat parsing, kembalikan teks ini
        e.printStackTrace()
        "Tanggal Tidak Valid"
    }
}
