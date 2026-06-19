package io.github.yulbax.frkn.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.yulbax.frkn.data.profile.ProfileDao
import io.github.yulbax.frkn.data.profile.ProfileEntity

@Database(
    entities = [App::class, SettingsEntity::class, ProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
    abstract fun settingsDao(): SettingsDao
    abstract fun profileDao(): ProfileDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "frkn.db"
            ).build()
    }
}
