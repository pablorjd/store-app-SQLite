package space.pablorjd.storeapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoreEntity::class], version = 2)
abstract class StoreDataBase: RoomDatabase() {
    abstract fun storeDao(): StoreDao
}