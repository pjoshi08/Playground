package com.pj.playground.contentprovider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.pj.playground.data.LogDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.lang.UnsupportedOperationException

private const val LOGS_TABLE = "logs"

private const val AUTHORITY = "com.pj.playground.provider"

private const val CODE_LOGS_DIR = 1

private const val CODE_LOGS_ITEM = 2

class LogsContentProvider : ContentProvider() {

    private val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, LOGS_TABLE, CODE_LOGS_DIR)
        addURI(AUTHORITY, "$LOGS_TABLE/*", CODE_LOGS_ITEM)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = matcher.match(uri)
        return if (code == CODE_LOGS_DIR || code == CODE_LOGS_ITEM) {
            val appContext = context?.applicationContext ?: throw IllegalStateException()
            val logDao: LogDao = getLogDao(appContext)

            val cursor: Cursor? = if (code == CODE_LOGS_DIR) {
                logDao.selectAllLogsCursor()
            } else {
                logDao.selectLogById(ContentUris.parseId(uri))
            }
            // apply function returns the object on which it is applied
            cursor?.apply { setNotificationUri(appContext.contentResolver, uri) }
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri = readOnly

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = readOnly

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = readOnly

    override fun getType(uri: Uri): String = readOnly

    private val readOnly: Nothing = throw UnsupportedOperationException(
        "Only reading operations are allowed"
    )

    /**
     * To access an entry point, use the appropriate static method from [EntryPointAccessors].
     * The parameter should be either the component instance or the @[AndroidEntryPoint]
     * object that acts as the component holder. Make sure that the component you pass
     * as a parameter and the EntryPointAccessors static method both match the Android
     * class in the @[InstallIn] annotation on the @[EntryPoint] interface
     */
    private fun getLogDao(appContext: Context): LogDao {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            LogsContentProviderEntryPoint::class.java
        )
        return hiltEntryPoint.logDao()
    }

    /**
     * The best practice is adding the new entry point interface inside the class that uses it.
     * Therefore, include the interface in [LogsContentProvider].kt file
     */
    // Check if this can be separated to another place
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface LogsContentProviderEntryPoint {
        fun logDao(): LogDao
    }
}