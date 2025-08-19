package be.odisee.receptenboek.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "voorkeuren")

object GebruikerManager {
    private val USER_ID_KEY = intPreferencesKey("user_id")

    suspend fun saveUserId(context: Context, id: Int) {
        context.dataStore.edit { it[USER_ID_KEY] = id }
    }

    suspend fun getUserId(context: Context): Int? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID_KEY]
    }
}
