package com.durgasoft.slot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat

object CalendarUtils {

    fun hasCalendarPermission(context: Context): Boolean {
        val read = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        val write = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        return read && write
    }

    fun insertVictoryEvent(context: Context, prize: Int) {
        if (!hasCalendarPermission(context)) return

        val now = System.currentTimeMillis()
        val startMillis = now
        val endMillis = now + 5 * 60 * 1000L

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.TITLE, "Victoria en DurgaSlot")
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Has ganado $prize fichas en la tragaperras."
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si no hay ninguna app de calendario instalada
            Toast.makeText(
                context,
                "No se ha encontrado ninguna aplicación de calendario.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
