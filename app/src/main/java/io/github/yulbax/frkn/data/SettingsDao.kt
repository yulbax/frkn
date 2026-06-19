package io.github.yulbax.frkn.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 1")
    fun observeSettings(): Flow<SettingsEntity?>

    @Upsert
    suspend fun upsertSettings(settings: SettingsEntity)
}
