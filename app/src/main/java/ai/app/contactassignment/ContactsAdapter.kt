package ai.app.contactassignment

import ai.app.contactassignment.database.model.Contact
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(private var contactList: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contactList.size

    fun updateContacts(newContacts: List<Contact>) {
        contactList = newContacts
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name
            phoneTextView.text = contact.phoneNumber
        }
    }
}
