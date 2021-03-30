package com.pj.playground.framework.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class MajesticReaderDatabase : RoomDatabase() {
    companion object {

        private const val DATABASE_NAME = "reader.db"

        private var instance: MajesticReaderDatabase? = null

        private fun create(context: Context): MajesticReaderDatabase =
            Room.databaseBuilder(context, MajesticReaderDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()

        fun getInstance(context: Context): MajesticReaderDatabase =
            (instance ?: create(context)).also { instance = it }
    }

    // Abstract methods go here for any DAO class

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun documentDao(): DocumentDao
}