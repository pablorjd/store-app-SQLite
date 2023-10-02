package space.pablorjd.storeapp

import android.app.Application
import androidx.room.Room

class StoreApplication : Application() {

    companion object {
        lateinit var dataBase: StoreDataBase
    }

    override fun onCreate() {
        super.onCreate()
        StoreApplication.dataBase = Room.databaseBuilder(
            this,
            StoreDataBase::class.java,
            "StoreDataBase"
        ).build()
    }
}