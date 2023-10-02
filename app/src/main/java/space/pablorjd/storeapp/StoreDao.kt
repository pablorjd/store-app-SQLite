package space.pablorjd.storeapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStore(): MutableList<StoreEntity>

    @Insert
    fun addStore(store: StoreEntity)

    @Update
    fun updateStore(store: StoreEntity)

    @Delete
    fun deleteStore(store: StoreEntity)
}