package com.sistr.kaskelotti

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log


private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI("com.sistr.kaskelotti.provider", "search", 0)
    addURI("com.sistr.kaskelotti.provider", SearchManager.SUGGEST_URI_PATH_QUERY + "/*", 1)
    addURI("com.sistr.kaskelotti.provider", "search/#", 2)
}

class SearchProvider : ContentProvider() {
    private val TAG = "SearchProvider"
    private lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        val dbHelper = DbHelper(context)
        db = dbHelper.readableDatabase
        return true
    }

    override fun query(
        uri: Uri?,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when (sUriMatcher.match(uri)) {
            0 -> {
                return db.rawQuery("SELECT * FROM searchtable_fts WHERE searchtable_fts MATCH '$selection*';", null)
            }
            1 -> {
                val builder = SQLiteQueryBuilder()
                val projectionMap: MutableMap<String, String> = HashMap()
                projectionMap["_ID"] = "_ID AS _id, _ID AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                projectionMap["english"] = "english AS " + SearchManager.SUGGEST_COLUMN_TEXT_1
                projectionMap["suomi"] = "suomi AS " + SearchManager.SUGGEST_COLUMN_TEXT_2
                builder.setProjectionMap(projectionMap)
                //val query = builder.buildQuery(null, "SELECT * FROM searchtable_fts WHERE searchtable_fts MATCH '${uri?.lastPathSegment}*';", null, null, null, null)
                builder.tables = "searchtable_fts"
                builder.appendWhere("searchtable_fts MATCH '${uri?.lastPathSegment}*'")
                val query = builder.buildQuery(null, null, null, null, null, null)
                val cursor = db.rawQuery(query, null)
                Log.d(TAG, "query: $query")
                for(i in 0 until cursor.columnCount)
                {
                    Log.d(TAG, "column [$i]: ${cursor.getColumnName(i)}")
                }
                return cursor
                //return db.rawQuery("SELECT * FROM searchtable_fts WHERE searchtable_fts MATCH '${uri?.lastPathSegment}*';", null)
            }
            2 -> {  // If the incoming URI was for a single row
                return db.rawQuery("SELECT * FROM searchtable WHERE _ID = ${uri?.lastPathSegment};", null)
            }
            else -> { // If the URI is not recognized
                return null
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        when (sUriMatcher.match(uri)) {
            0 -> {
                return null
            }
            1 -> {
                return SearchManager.SUGGEST_MIME_TYPE;
            }
            2 -> {
                return null
            }
            else -> { // If the URI is not recognized
                return null
            }
        }
    }
}