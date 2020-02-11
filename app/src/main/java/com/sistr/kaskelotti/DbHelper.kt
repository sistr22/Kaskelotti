package com.sistr.kaskelotti

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.net.URI


class DbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val DB_NAME = "sqlite3.db"

    init {
        if (!File(context.getDatabasePath(DB_NAME).path).exists()) {
            copyDatabase()
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun copyDatabase() {
        val inputStream = context.assets.open(DB_NAME)
        val outputFile = File(context.getDatabasePath(DB_NAME).path)
        val outputStream = FileOutputStream(outputFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(context.getDatabasePath(DB_NAME).path, null, SQLiteDatabase.OPEN_READONLY)
    }

    companion object {
        const val DATABASE_NAME = "sqlite3"
        const val DATABASE_VERSION = 1
    }

}