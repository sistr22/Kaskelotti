package com.sistr.kaskelotti

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*
import java.util.zip.CRC32


class DbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val _tag = "DbHelper"
    private val DB_NAME = "sqlite3.db"

    @Throws(IOException::class)
    private fun getCRC(file: File): Long {
        val fis = FileInputStream(file)
        val data = ByteArray(file.length().toInt())
        fis.read(data)
        fis.close()
        val crc32 = CRC32()
        crc32.update(data)
        return crc32.value
    }

    init {
        if (!File(context.getDatabasePath(DB_NAME).path).exists()) {
            copyDatabase()
        } else {
            val crcCopiedDb = getCRC(File(context.getDatabasePath(DB_NAME).path))
            Log.d(_tag, "CRC copied DB: $crcCopiedDb")

            val inputStream = context.assets.open(DB_NAME)
            val bufferedReader = BufferedInputStream(inputStream)
            val buff = ByteArray(128)
            var sizeRead: Int
            val crc32 = CRC32()
            do {
                sizeRead = bufferedReader.read(buff)
                if(sizeRead == -1)
                    continue
                if(sizeRead == buff.size)
                    crc32.update(buff)
                else {
                    crc32.update(buff.copyOfRange(0, sizeRead))
                }

            } while (sizeRead > 0)

            Log.d(_tag, "CRC assets DB: ${crc32.value}")

            if(crc32.value != crcCopiedDb) {
                copyDatabase()
            }

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