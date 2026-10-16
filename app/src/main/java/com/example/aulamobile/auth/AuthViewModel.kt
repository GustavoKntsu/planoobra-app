package com.example.aulamobile.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun signIn(email: String, password: String) {
        setLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
    }

    fun signUp(email: String, password: String) {
        setLoading()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _state.value = AuthUiState(user = null)
    }

    private fun setLoading() {
        _state.value = _state.value.copy(isLoading = true, error = null)
    }

    private fun ok() {
        _state.value = _state.value.copy(isLoading = false, user = auth.currentUser, error = null)
    }

    private fun fail(ex: Exception?) {
        _state.value = _state.value.copy(isLoading = false, user = null, error = mapError(ex))
    }

    private fun mapError(ex: Exception?): String {
        val e = ex ?: return "Falha na autenticação. Por Favor, tente novamente."
        return when (e) {
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado."
            is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "E-mail inválido."
                "ERROR_WRONG_PASSWORD" -> "Senha incorreta."
                else -> "Falha na autenticação. Por Favor, tente novamente."

            }

            is FirebaseAuthInvalidUserException -> when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "Usuário não encontrado."
                "ERROR_USER_DISABLED" -> "Usuário desativado."
                else -> "Falha na autenticação. Por Favor, tente novamente."
            }

            is FirebaseNetworkException -> "Sem conexão com a internet."
            else -> {
                val code = (e as? FirebaseAuthException)?.errorCode
                when (code) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "E-mail já cadastrado."
                    "ERROR_WEAK_PASSWORD" -> "Senha fraca."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "Operação não permitida."
                    "ERROR_TOO_MANY_REQUESTS" -> "Muitas tentativas. Tente novamente mais tarde"
                    else -> e.localizedMessage?.substringBefore('\n')
                        ?: "Falha na autenticação. Por Favor, tente novamente."

                }
            }
        }
    }
}
