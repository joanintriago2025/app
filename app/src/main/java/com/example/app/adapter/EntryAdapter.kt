package com.example.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.google.android.material.card.MaterialCardView
import com.example.app.model.Entry
import com.google.android.material.button.MaterialButton

class EntryAdapter(
    private var entries: List<Entry>,
    private val onEdit: (Entry) -> Unit,
    private val onDelete: (Entry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(
            R.id.entryTitle)
        val descText: TextView = itemView.findViewById(R.id.entryDescription)
        val editBtn: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.titleText.text = entry.title
        holder.descText.text = entry.description
        holder.editBtn.setOnClickListener { onEdit(entry) }
        holder.deleteBtn.setOnClickListener { onDelete(entry) }
    }

    override fun getItemCount(): Int = entries.size

    fun updateEntries(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}
