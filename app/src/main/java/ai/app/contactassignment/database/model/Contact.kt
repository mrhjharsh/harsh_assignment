package ai.app.contactassignment.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String
)
