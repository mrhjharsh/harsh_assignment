package ai.app.contactassignment
import ai.app.contactassignment.ContactsAdapter
import ai.app.contactassignment.R
import ai.app.contactassignment.database.ContactDatabase
import ai.app.contactassignment.database.model.Contact
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var contactsAdapter: ContactsAdapter
    private var contactList: MutableList<Contact> = mutableListOf()
    private lateinit var db: ContactDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)

        db = Room.databaseBuilder(applicationContext, ContactDatabase::class.java, "contacts-db").build()

        contactsAdapter = ContactsAdapter(contactList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            fetchAndSaveContacts()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchContacts(newText)
                return true
            }
        })

        loadContactsFromDB()
    }

    private fun fetchAndSaveContacts() {
        val resolver = contentResolver
        val cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        cursor?.let {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Launch a coroutine to save contacts and load them only after they are saved
            lifecycleScope.launch {
                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val phoneNumber = it.getString(numberIndex)
                    val contact = Contact(name = name, phoneNumber = phoneNumber)

                    // Check if the contact already exists in the database
                    val existingContact = db.contactDao().getContactByPhoneNumber(phoneNumber)
                    if (existingContact == null) {
                        // If the contact does not exist, insert it
                        db.contactDao().insertContact(contact)
                    }
                }
                it.close()

                // Once all contacts are saved, load them from the database and update UI
                loadContactsFromDB()
            }
        }
    }

    private fun loadContactsFromDB() {
        lifecycleScope.launch {
            val contacts = db.contactDao().getAllContacts()
            contactList.clear()
            contactList.addAll(contacts)
            contactsAdapter.notifyDataSetChanged()
        }
    }

    private fun searchContacts(query: String?) {
        lifecycleScope.launch {
            val filteredContacts = if (query.isNullOrEmpty()) {
                db.contactDao().getAllContacts()
            } else {
                db.contactDao().searchContacts(query)
            }
            contactList.clear()
            contactList.addAll(filteredContacts)
            contactsAdapter.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchAndSaveContacts()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
