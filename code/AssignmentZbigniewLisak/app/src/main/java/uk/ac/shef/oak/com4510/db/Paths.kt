package uk.ac.shef.oak.com4510.db

import android.content.Context
import androidx.room.*

@Database(entities = [PathData::class,Measurement::class,Photo::class], version = 1, exportSchema = false)
abstract class Paths: RoomDatabase() {
    abstract fun pathDao() : PathDao


    companion object{

        @Volatile
        private var INSTANCE: Paths? = null

        fun getDatabase(context : Context): Paths {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, Paths::class.java, "path_tracking_db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }
}