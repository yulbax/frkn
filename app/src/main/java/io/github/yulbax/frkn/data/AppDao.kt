package io.github.yulbax.frkn.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM apps")
    fun getAllApps(): Flow<List<App>>

    @Query("SELECT * FROM apps WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): App?

    @Upsert
    suspend fun upsertApp(app: App)

    @Upsert
    suspend fun upsertApps(apps: List<App>)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    @Query("DELETE FROM apps WHERE packageName IN (:packageNames)")
    suspend fun deleteApps(packageNames: List<String>)
}
