package com.example.cosa.presentation.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosa.data.preferences.UserPrefsKeys
import com.example.cosa.data.preferences.dataStore
import com.example.cosa.data.model.Usuario
import com.example.cosa.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SessionViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    // Nuevo: usuario actual completo
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    fun login(email: String) {
        viewModelScope.launch {
            _userEmail.value = email
            _isLoggedIn.value = true
            loadCurrentUser(email)
        }
    }

    fun saveSession(context: Context, email: String) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[UserPrefsKeys.EMAIL] = email
            }
            _userEmail.value = email
            _isLoggedIn.value = true
            loadCurrentUser(email)
        }
    }

    fun loadSession(context: Context) {
        viewModelScope.launch {
            context.dataStore.data.collectLatest { prefs ->
                val email = prefs[UserPrefsKeys.EMAIL]
                if (email != null) {
                    _userEmail.value = email
                    _isLoggedIn.value = true
                    loadCurrentUser(email)
                }
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs.remove(UserPrefsKeys.EMAIL)
            }
            _userEmail.value = null
            _isLoggedIn.value = false
            _currentUser.value = null
        }
    }

    private fun loadCurrentUser(email: String) {
        viewModelScope.launch {
            val user = usuarioRepository.obtenerPorCorreo(email)
            _currentUser.value = user
        }
    }

    /**
     * Actualiza los datos del usuario en la base de datos y emite el nuevo estado.
     * @param updatedUsername el nuevo nombre de usuario
     * @param updatedPassword la nueva contraseña (null si no se quiere cambiar)
     * @param onComplete callback con true si se guardó correctamente
     */
    fun updateUser(
        updatedUsername: String,
        updatedPassword: String?,
        onComplete: (Boolean) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            try {
                val updatedUser = user.copy(
                    nombre = updatedUsername,
                    pass = updatedPassword ?: user.pass
                )
                usuarioRepository.actualizarUsuario(updatedUser) // <--- usar update
                _currentUser.value = updatedUser
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

}
