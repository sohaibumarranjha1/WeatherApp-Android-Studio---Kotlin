package com.lifepulse.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lifepulse.domain.model.City
import com.lifepulse.domain.model.PrecipitationUnit
import com.lifepulse.domain.model.TemperatureUnit
import com.lifepulse.domain.model.ThemeMode
import com.lifepulse.domain.model.UserPreferences
import com.lifepulse.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifepulse_user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val PRECIP_UNIT = stringPreferencesKey("precip_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")

        val ACTIVE_CITY_ID = longPreferencesKey("active_city_id")
        val ACTIVE_CITY_NAME = stringPreferencesKey("active_city_name")
        val ACTIVE_CITY_LAT = doublePreferencesKey("active_city_lat")
        val ACTIVE_CITY_LON = doublePreferencesKey("active_city_lon")
        val ACTIVE_CITY_COUNTRY = stringPreferencesKey("active_city_country")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { prefs ->
            UserPreferences(
                tempUnit = parseEnum(prefs[Keys.TEMP_UNIT], TemperatureUnit.CELSIUS),
                windSpeedUnit = parseEnum(prefs[Keys.WIND_UNIT], WindSpeedUnit.KM_H),
                precipitationUnit = parseEnum(prefs[Keys.PRECIP_UNIT], PrecipitationUnit.MM),
                themeMode = parseEnum(prefs[Keys.THEME_MODE], ThemeMode.SYSTEM),
                notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
                activeCityId = prefs[Keys.ACTIVE_CITY_ID] ?: 2950159L,
                activeCityName = prefs[Keys.ACTIVE_CITY_NAME] ?: "Berlin",
                activeCityLat = prefs[Keys.ACTIVE_CITY_LAT] ?: 52.52,
                activeCityLon = prefs[Keys.ACTIVE_CITY_LON] ?: 13.41,
                activeCityCountry = prefs[Keys.ACTIVE_CITY_COUNTRY] ?: "Germany"
            )
        }

    suspend fun updateTempUnit(unit: TemperatureUnit) {
        context.dataStore.edit { prefs -> prefs[Keys.TEMP_UNIT] = unit.name }
    }

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { prefs -> prefs[Keys.WIND_UNIT] = unit.name }
    }

    suspend fun updatePrecipitationUnit(unit: PrecipitationUnit) {
        context.dataStore.edit { prefs -> prefs[Keys.PRECIP_UNIT] = unit.name }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode.name }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setActiveCity(city: City) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACTIVE_CITY_ID] = city.id
            prefs[Keys.ACTIVE_CITY_NAME] = city.name
            prefs[Keys.ACTIVE_CITY_LAT] = city.latitude
            prefs[Keys.ACTIVE_CITY_LON] = city.longitude
            prefs[Keys.ACTIVE_CITY_COUNTRY] = city.country ?: ""
        }
    }

    private inline fun <reified T : Enum<T>> parseEnum(value: String?, default: T): T {
        return if (value != null) {
            try {
                enumValueOf<T>(value)
            } catch (e: Exception) {
                default
            }
        } else {
            default
        }
    }
}
