package com.durgasoft.slot

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.Locale

object LocationUtils {

    fun getCity(context: Context): String {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val location: Location? = try {
            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: SecurityException) {
            return "Sin permisos"
        }

        if (location == null) return "Desconocida"

        return try {
            val geo = Geocoder(context, Locale.getDefault())
            val result = geo.getFromLocation(location.latitude, location.longitude, 1)
            result?.firstOrNull()?.locality ?: "Desconocida"
        } catch (e: Exception) {
            "Desconocida"
        }
    }
}
