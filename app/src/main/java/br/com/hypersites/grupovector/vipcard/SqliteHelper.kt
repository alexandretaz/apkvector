package br.com.hypersites.grupovector.vipcard

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar

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
        this.createDBIfNeeded(db)
        val connection = db
    }

     fun createDBIfNeeded(db:SQLiteDatabase?){
        db?.createTable("user_access",true,"id" to INTEGER+ PRIMARY_KEY + UNIQUE,
                "token" to TEXT,
                "device_id" to TEXT
                )
        db?.createTable("open_alarms",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
            "last_update" to TEXT
        )
        db?.createTable("open_helps",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "last_update" to TEXT
        )
        db?.createTable("lost_points_alarms",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "latitude" to TEXT,
                "longitue" to TEXT
        )
        db?.createTable("lost_points_help",true,"id" to INTEGER+ PRIMARY_KEY+ UNIQUE,
                "latitude" to TEXT,
                "longitue" to TEXT
                )

    }

    fun createUser( token:String, device_id:String):Long {
        val value = ContentValues()
        value.put("id",1)
        value.put("token",token)
        value.put("device_id", device_id)
        if(this.getAllUsers()<1) {
            return writableDatabase.insert("user_access", null, value)
        }
        else{
            value.put("token",token)
            value.put("device_id", device_id)
            writableDatabase.delete("user_access", "id=1")
            return writableDatabase.insert("user_access", null, value)
        }

    }

    fun registerAlarm( id:Int):Long {
        val value = ContentValues()
        val lastUpdateDate = Calendar.getInstance().time;
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val lastUpdate = format.format(lastUpdateDate)
        value.put("id",id)
        value.put("last_update",lastUpdate)
        return writableDatabase.insert("open_alarms",null, value)

    }

    fun getAllUsers(): Int {
        val stuList: MutableList<AppClient> = mutableListOf<AppClient>()
        val cursor: Cursor = readableDatabase.query("user_access", arrayOf("id", "token", "device_id"), null, null, null, null, null)
        val numberOfUsers:Int = cursor.count
        Log.i("NumberOfUsers",numberOfUsers.toString())
        cursor.close()

        return numberOfUsers
    }

    fun getUser():AppClient {

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
        db?.dropTable("user_access")
    }




}

// Access property for Context
val Context.database: SqliteHelper
    get() = SqliteHelper.getInstance(applicationContext)