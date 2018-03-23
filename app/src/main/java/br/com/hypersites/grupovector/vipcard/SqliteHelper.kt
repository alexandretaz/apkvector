package br.com.hypersites.grupovector.vipcard

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.*

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

     fun createDBIfNeeded(){
        writableDatabase.createTable("user_access",true,"id" to INTEGER+ PRIMARY_KEY + UNIQUE,
                "token" to TEXT,
                "device_id" to TEXT
                )
        writableDatabase.createTable("open_alarms",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
            "last_update" to TEXT
        )
        writableDatabase.createTable("open_helps",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "last_update" to TEXT
        )
        writableDatabase.createTable("lost_points_alarms",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "latitude" to TEXT,
                "longitue" to TEXT
        )
        writableDatabase.createTable("lost_points_help",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "latitude" to TEXT,
                "longitue" to TEXT
                )

    }

    fun createUser( token:String, device_id:String):Long {
        val value = ContentValues()
        value.put("id",1)
        value.put("token",token)
        value.put("device_id", device_id)
        return writableDatabase.insert("user_access",null, value)

    }

    fun getAllUsers(): Int {
        val stuList: MutableList<AppClient> = mutableListOf<AppClient>()
        val cursor: Cursor = readableDatabase.query("user_access", arrayOf("id", "token", "device_id"), null, null, null, null, null)
        val numberOfUsers:Int = cursor.count
        Log.i("NumberOfUsers",numberOfUsers.toString())
        cursor.close()

        return numberOfUsers
    }

    fun getUser(db: SQLiteDatabase?):AppClient {

        val cursor: Cursor = readableDatabase.query("user_access", arrayOf("id", "token", "device_id"), null, null, null, null, null)
        try {
            if(cursor.count>=1) {
                cursor.moveToFirst()
                val user:AppClient = AppClient(cursor.getString(cursor.getColumnIndex("token")), cursor.getString(cursor.getColumnIndex("device_id")))
                return user
            }
        } finally {
            cursor.close()
        }
        return AppClient("","")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




}

// Access property for Context
val Context.database: SqliteHelper
    get() = SqliteHelper.getInstance(applicationContext)