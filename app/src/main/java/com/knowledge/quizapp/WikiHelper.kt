package com.knowledge.quizapp

import DbWikiHelper
import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import com.knowledge.quizapp.WikiPath.TABLE_USER
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.STARTTITLE
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.GOALTITLE
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.PATH
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.PATHLENGTH
import java.util.ArrayList

class WikiHelper(private val context: Context) {
    private var dbWikiHelper: DbWikiHelper = DbWikiHelper(context)
    private var database: SQLiteDatabase = dbWikiHelper.writableDatabase

    val user:  ArrayList<PathItem>
        get() {
            val cursor = database.query(TABLE_USER, null, null, null, null, null, null)
            val arrayList = ArrayList<PathItem>()

            while (cursor.moveToNext()) {
                val wikiPath = PathItem().apply {
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                    titleStart = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                    titleGoal = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                    path = cursor.getString(cursor.getColumnIndexOrThrow(PATH))
                    pathLength = cursor.getInt(cursor.getColumnIndexOrThrow(PATHLENGTH))
                }

                arrayList.add(wikiPath)
            }

            cursor.close()
            return arrayList
        }

    @Throws(SQLException::class)
    fun open(): WikiHelper {
        database = dbWikiHelper.writableDatabase
        return this
    }

    fun close() {
        dbWikiHelper.close()
    }

    fun insert(wikiPath: PathItem): Long {
        val values = ContentValues().apply {
            put(STARTTITLE, wikiPath.titleStart)
            put(GOALTITLE, wikiPath.titleGoal)
            put(PATH, wikiPath.path)
            put(PATHLENGTH, wikiPath.pathLength)
        }
        return database.insert(TABLE_USER, null, values)
    }

    fun beginTransaction() {
        database.beginTransaction()
    }

    fun setTransactionSuccess() {
        database.setTransactionSuccessful()
    }

    fun endTransaction() {
        database.endTransaction()
    }

    fun getRecordByStartAndGoalTitle(startTitle: String, goalTitle: String): PathItem? {
        val query = "SELECT * FROM $TABLE_USER WHERE $STARTTITLE = ? AND $GOALTITLE = ?"
        val cursor = database.rawQuery(query, arrayOf(startTitle, goalTitle))

        var record: PathItem? = null
        if (cursor.moveToFirst()) {
            record = PathItem().apply {
                titleStart = cursor.getString(cursor.getColumnIndex(STARTTITLE))
                titleGoal = cursor.getString(cursor.getColumnIndex(GOALTITLE))
                path = cursor.getString(cursor.getColumnIndex(PATH))
                pathLength = cursor.getInt(cursor.getColumnIndex(PATHLENGTH))
            }
        }

        cursor.close()
        return record
    }
}