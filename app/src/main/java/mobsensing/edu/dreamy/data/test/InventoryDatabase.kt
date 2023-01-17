package mobsensing.edu.dreamy.data.test
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//private const val NAME = "item_database"
//
//@Database(entities = [Item::class], version = 1, exportSchema = false)
//abstract class DB : RoomDatabase() {
//    abstract fun itemDao(): ItemDao
//
//    companion object {
//        @Volatile
//        private var Instance: DB? = null
//        fun getDatabase(context: Context): DB {
//            return Instance ?: synchronized(this) {
//                Room.databaseBuilder(context, DB::class.java, NAME)
//                    .build()
//                    .also {
//                        Instance = it
//                    }
//            }
//        }
//    }
//}


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
