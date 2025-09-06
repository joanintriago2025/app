package com.example.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.app.model.Entry
import com.example.app.viewmodel.MainViewModel
import com.example.app.adapter.EntryAdapter
import com.example.app.repository.FirebaseRepository

class MainActivity :
    AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var entryAdapter: EntryAdapter
    private lateinit var userId: String
    private lateinit var loadingProgressBar: View
    private lateinit var noEntryText: View

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        enableEdgeToEdge()
        setContentView(
            R.layout.activity_main
        )
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(
                R.id.main
            )
        ) { v, insets ->
            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.entryRecyclerView)
        val fab = findViewById<FloatingActionButton>(R.id.addEntryFab)
        userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        noEntryText = findViewById(R.id.noEntryText)
        loadingProgressBar.visibility = View.VISIBLE
        noEntryText.visibility = View.GONE
        recyclerView.visibility = View.GONE
        entryAdapter = EntryAdapter(listOf(), onEdit = { entry -> showEntryDialog(entry) }, onDelete = { entry ->
            showDeleteConfirmDialog(entry)
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = entryAdapter

        fab.setOnClickListener { showEntryDialog(null) }

        mainViewModel.entries.observe(this) { entries ->
            loadingProgressBar.visibility = View.GONE
            if (entries.isEmpty()) {
                noEntryText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noEntryText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                entryAdapter.updateEntries(entries)
            }
        }
        mainViewModel.operationResult.observe(this) { (success, error) ->
            if (!success && error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
        if (userId.isNotEmpty()) {
            mainViewModel.loadEntries(userId)
        }
    }

    override fun onBackPressed() {
        finishAffinity() // Close the app and remove from recent tasks
    }

    private fun showEntryDialog(entry: Entry?) {
        val dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null)
        val titleInput = EditText(this)
        titleInput.hint = "Title"
        val descInput = EditText(this)
        descInput.hint = "Description"
        if (entry != null) {
            titleInput.setText(entry.title)
            descInput.setText(entry.description)
        }
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(48, 24, 48, 0)
        layout.addView(titleInput)
        layout.addView(descInput)
        AlertDialog.Builder(this)
            .setTitle(if (entry == null) "Add Entry" else "Edit Entry")
            .setView(layout)
            .setPositiveButton(if (entry == null) "Add" else "Update") { _, _ ->
                val title = titleInput.text.toString().trim()
                val desc = descInput.text.toString().trim()
                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (entry == null) {
                    mainViewModel.addEntry(userId, Entry(title = title, description = desc))
                } else {
                    mainViewModel.updateEntry(userId, entry.copy(title = title, description = desc))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmDialog(entry: Entry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                mainViewModel.deleteEntry(userId, entry.id ?: "")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}