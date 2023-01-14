package mobsensing.edu.dreamy

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import mobsensing.edu.dreamy.data.sleep.db.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
//@SmallTest
class SleepDataTest {
    companion object {
        const val TAG = "sleepRoomTestClass"
    }
    private lateinit var database: SleepDatabase
    private lateinit var sleepSegmentEventDao: SleepSegmentEventDao
    private lateinit var sleepClassifyEventDao: SleepClassifyEventDao

    private val sleepSegmentEventEntity = listOf(
        SleepSegmentEventEntity(1_000L, 1_500L,0),
        SleepSegmentEventEntity(2_000L, 2_500L,0)
    )
    private val reversedEntityList = sleepSegmentEventEntity.asReversed()
    private val sleepClassifyEventEntity = listOf(
        SleepClassifyEventEntity(1_000, 1, 1, 1),
        SleepClassifyEventEntity(2_000, 50, 3, 3),
        SleepClassifyEventEntity(3_000, 100, 6, 6)
    )
    private val sleepClassifyEventEntityReversed = listOf(
        SleepClassifyEventEntity(3_000, 100, 6, 6),
        SleepClassifyEventEntity(2_000, 50, 3, 3),
        SleepClassifyEventEntity(1_000, 1, 1, 1)
    )


    @Before
    fun setupDatabase() {
//        Log.d("setup", "1: ${this@SleepDataTest::database.isInitialized}")
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            SleepDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
//        Log.d(TAG, "2: ${this@SleepDataTest::database.isInitialized}")


        sleepSegmentEventDao = database.sleepSegmentEventDao()
        sleepClassifyEventDao = database.sleepClassifyEventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        Log.d(TAG, "1: ${this@SleepDataTest::database.isInitialized}")
        database.close()
    }

    // ? segment Dao tests
    @Test
    @Throws(Exception::class)
    fun insert_SleepSegment_returnsTrue() = runBlocking {
        sleepSegmentEventDao.insert(sleepSegmentEventEntity[0])

        val sleepSegmentEvent = sleepSegmentEventDao.getAll().first()
        assertEquals(sleepSegmentEvent[0], sleepSegmentEventEntity[0])
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_SleepSegment_returnsTrue() = runBlocking {
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntity)

        val allSleepSegmentEvents = sleepSegmentEventDao.getAll().collect {
            assertEquals(it, reversedEntityList)
        }

        val all = sleepSegmentEventDao.getAll().onEach {

        }
/*
        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            sleepClassifyEventDao.getAll().collect {
                ViewMatchers.assertThat(it, Matchers.equalTo(sleepSegmentEventEntity))
            }
        }

        withContext(Dispatchers.IO) {
            latch.await()
        }
        job.cancelAndJoin()
*/
    }

    @Test
    @Throws(Exception::class)
    fun delete_SleepSegment_returnsTrue() = runBlocking {
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntity)
        sleepSegmentEventDao.delete(sleepSegmentEventEntity[0])

        val sleepSegmentEvent = sleepSegmentEventDao.getAll().first()
        assertNotEquals(sleepSegmentEvent[0], sleepSegmentEventEntity[0])
        assertEquals(sleepSegmentEvent[0], sleepSegmentEventEntity[1])
    }


    @Test
    @Throws(Exception::class)
    fun deleteAll_SleepSegment_returnsTrue() = runBlocking {
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntity)
        sleepSegmentEventDao.deleteAll()

        val sleepSegmentEvent = sleepSegmentEventDao.getAll().collect() {
            assertTrue(it.isEmpty())
        }
    }

    // ? Classify Dao tests
    @Test
    @Throws(Exception::class)
    fun insert_SleepClassify_returnsTrue() = runBlocking {
        sleepClassifyEventDao.insert(sleepClassifyEventEntity[0])

        val sleepClassifyEvent = sleepClassifyEventDao.getAll().first()
        assertEquals(sleepClassifyEvent[0], sleepClassifyEventEntity[0])
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_SleepClassify_returnsTrue() = runBlocking {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntity)

        val allSleepClassifyEvents = sleepClassifyEventDao.getAll().collect {
            println("$it")
            assertEquals(it, sleepClassifyEventEntityReversed)
        }
    }

    @Test
    @Throws(Exception::class)
    fun delete_SleepClassify_returnsTrue() = runBlocking {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntity)
        sleepClassifyEventDao.delete(sleepClassifyEventEntity[0])

        val sleepClassifyEvent = sleepClassifyEventDao.getAll().first()
        assertNotEquals(sleepClassifyEvent[0], sleepClassifyEventEntity[0])
        assertEquals(sleepClassifyEvent[1], sleepClassifyEventEntity[1])
    }


    @Test
    @Throws(Exception::class)
    fun deleteAll_SleepClassify_returnsTrue() = runBlocking {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntity)
        sleepClassifyEventDao.deleteAll()

        val sleepClassifyEvent = sleepClassifyEventDao.getAll().collect() {
            assertTrue(it.isEmpty())
        }
    }
}