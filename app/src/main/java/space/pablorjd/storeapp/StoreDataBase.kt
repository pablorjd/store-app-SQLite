package space.pablorjd.storeapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoreEntity::class], version = 1)
abstract class StoreDataBase: RoomDatabase() {
    abstract fun storeDao(): StoreDao
}