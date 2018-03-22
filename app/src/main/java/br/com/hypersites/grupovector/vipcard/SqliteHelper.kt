package br.com.hypersites.grupovector.vipcard

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

/**
 * Created by alexandre.andrade on 22/03/18.
 */
class SqliteHelper(ctx: Context): ManagedSQLiteOpenHelper(ctx, "vector_alarm"){

    companion object {
        private var instance: SqliteHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): SqliteHelper{
            if (instance == null) {
                instance = SqliteHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}

// Access property for Context
val Context.database: SqliteHelper
    get() = SqliteHelper.getInstance(applicationContext)