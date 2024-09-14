package ai.app.contactassignment.database.dao

import ai.app.contactassignment.database.model.Contact
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contacts WHERE phoneNumber LIKE '%' || :query || '%'")
    suspend fun searchContacts(query: String): List<Contact>
}
