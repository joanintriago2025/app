package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.repository.FirebaseRepository
import com.example.app.model.Entry
import android.util.Patterns

class MainViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> = _entries

    private val _operationResult = MutableLiveData<Pair<Boolean, String?>>()
    val operationResult: LiveData<Pair<Boolean, String?>> = _operationResult

    // Login logic
    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>> = _loginResult

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = Pair(false, "Email and password must not be empty.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginResult.value = Pair(false, "Please enter a valid email address.")
            return
        }
        repository.login(email, password) { success, error ->
            _loginResult.value = Pair(success, error)
        }
    }

    // Register logic
    private val _registerResult = MutableLiveData<Pair<Boolean, String?>>()
    val registerResult: LiveData<Pair<Boolean, String?>> = _registerResult

    fun register(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerResult.value = Pair(false, "All fields are required.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerResult.value = Pair(false, "Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _registerResult.value = Pair(false, "Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            _registerResult.value = Pair(false, "Passwords do not match.")
            return
        }
        repository.register(email, password) { success, error ->
            if (!success && error != null && error.contains("email address is already in use", ignoreCase = true)) {
                _registerResult.value = Pair(false, "This email is already registered. Please use another email or login.")
            } else {
                _registerResult.value = Pair(success, error)
            }
        }
    }

    fun loadEntries(userId: String) {
        repository.getEntries(userId, {
            _entries.value = it
        }, {
            _operationResult.value = Pair(false, it)
        })
    }

    fun addEntry(userId: String, entry: Entry) {
        repository.addEntry(userId, entry) { success, error ->
            _operationResult.value = Pair(success, error)
        }
    }

    fun updateEntry(userId: String, entry: Entry) {
        repository.updateEntry(userId, entry) { success, error ->
            _operationResult.value = Pair(success, error)
        }
    }

    fun deleteEntry(userId: String, entryId: String) {
        repository.deleteEntry(userId, entryId) { success, error ->
            _operationResult.value = Pair(success, error)
        }
    }
}
