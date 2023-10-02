package space.pablorjd.storeapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity ORDER BY name DESC")
    fun allStore(): MutableList<StoreEntity>

    @Query("SELECT * FROM StoreEntity where id = :id")
    fun getStoreById(id: Long): StoreEntity

    @Insert
    fun addStore(store: StoreEntity): Long

    @Update
    fun updateStore(store: StoreEntity)

    @Delete
    fun deleteStore(store: StoreEntity)
}