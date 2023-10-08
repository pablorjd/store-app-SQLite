package space.pablorjd.storeapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoreEntity")
data class StoreEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")var id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "phone") var phone: String,
    @ColumnInfo(name = "webSite") var webSite: String = "",
    @ColumnInfo(name = "photoUrl") var photoUrl: String,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean = false
)
