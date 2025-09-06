package com.example.app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.example.app.model.Entry
import com.example.app.model.User

class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun addEntry(userId: String, entry: Entry, onResult: (Boolean, String?) -> Unit) {
        val key = database.child("entries").child(userId).push().key
        if (key == null) {
            onResult(false, "Key generation failed")
            return
        }
        val entryWithId = entry.copy(id = key)
        database.child("entries").child(userId).child(key).setValue(entryWithId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun updateEntry(userId: String, entry: Entry, onResult: (Boolean, String?) -> Unit) {
        if (entry.id == null) {
            onResult(false, "Entry ID is null")
            return
        }
        database.child("entries").child(userId).child(entry.id).setValue(entry)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun deleteEntry(userId: String, entryId: String, onResult: (Boolean, String?) -> Unit) {
        database.child("entries").child(userId).child(entryId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getEntries(userId: String, onDataChange: (List<Entry>) -> Unit, onError: (String) -> Unit) {
        database.child("entries").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<Entry>()
                    for (child in snapshot.children) {
                        val entry = child.getValue(Entry::class.java)
                        if (entry != null) entries.add(entry)
                    }
                    onDataChange(entries)
                }
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }
}
