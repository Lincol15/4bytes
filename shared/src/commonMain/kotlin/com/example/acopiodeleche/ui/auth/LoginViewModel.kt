package com.example.acopiodeleche.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acopiodeleche.data.remote.ApiResult
import com.example.acopiodeleche.data.repository.AuthRepository
import com.example.acopiodeleche.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val usuario: Usuario? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun iniciarSesion(correoODni: String, contrasena: String) {
        if (_estado.value.cargando) return
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(correoODni, contrasena)
            _estado.update {
                when (resultado) {
                    is ApiResult.Exito -> LoginUiState(usuario = resultado.datos)
                    is ApiResult.ErrorValidacion -> LoginUiState(error = "Correo/DNI o contraseña incorrectos.")
                    is ApiResult.Error -> LoginUiState(error = resultado.mensaje)
                }
            }
        }
    }

    fun limpiarError() = _estado.update { it.copy(error = null) }
}
