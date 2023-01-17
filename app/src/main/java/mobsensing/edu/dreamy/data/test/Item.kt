package mobsensing.edu.dreamy.data.test

//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "item_table")
//data class Item (
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val name: String
//)

//Companion Obj

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int
)