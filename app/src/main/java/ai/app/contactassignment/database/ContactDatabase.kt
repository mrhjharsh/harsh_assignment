package ai.app.contactassignment.database

import ai.app.contactassignment.database.dao.ContactDao
import ai.app.contactassignment.database.model.Contact
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}
