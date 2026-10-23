package com.example.planoobra.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
// --- FIREBASE COMENTADO PARA MODO DEMONSTRAÇÃO ---
// import com.google.firebase.FirebaseNetworkException
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.auth.FirebaseAuthException
// import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
// import com.google.firebase.auth.FirebaseAuthInvalidUserException
// import com.google.firebase.auth.FirebaseAuthUserCollisionException
// import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DemoUser(
    val uid: String = "demo_user_01",
    val email: String = "admin@planoobra.com",
    val displayName: String = "Eng. Carlos Mendes",
    val cargo: String = "Engenheiro Civil Responsável",
    val crea: String = "CREA-SP 5069871234",
    val telefone: String = "(11) 98765-4321",
    val empresa: String = "Construtora PlanoObra Ltda."
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: DemoUser? = AuthViewModel.currentUser,
    // val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    companion object {
        const val DEMO_EMAIL = "admin@planoobra.com"
        const val DEMO_PASSWORD = "123456"

        private val usuariosCadastrados = mutableMapOf(
            DEMO_EMAIL to Pair(DEMO_PASSWORD, DemoUser())
        )

        var currentUser by mutableStateOf<DemoUser?>(null)
            private set

        fun updateCurrentUser(updated: DemoUser) {
            currentUser = updated
            val emailKey = updated.email.trim().lowercase()
            val senhaAtual = usuariosCadastrados[emailKey]?.first ?: DEMO_PASSWORD
            usuariosCadastrados[emailKey] = Pair(senhaAtual, updated)
        }
    }

    // private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(AuthUiState(user = currentUser))
    val state: StateFlow<AuthUiState> = _state

    fun signIn(email: String, password: String) {
        setLoading()
        val emailLimpo = email.trim().lowercase()
        val cadastro = usuariosCadastrados[emailLimpo]

        if (cadastro != null && cadastro.first == password) {
            currentUser = cadastro.second
            ok()
        } else if (emailLimpo.isNotBlank() && password == DEMO_PASSWORD) {
            // Aceita qualquer e-mail com a senha padrão de demonstração (123456)
            val nome = emailLimpo.substringBefore("@").replaceFirstChar { it.uppercase() }
            currentUser = DemoUser(
                uid = "user_${emailLimpo.hashCode()}",
                email = emailLimpo,
                displayName = nome
            )
            ok()
        } else {
            _state.value = _state.value.copy(
                isLoading = false,
                user = null,
                error = "Credenciais inválidas. Use $DEMO_EMAIL e senha $DEMO_PASSWORD."
            )
        }

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
        */
    }

    fun signInDemo() {
        currentUser = DemoUser()
        ok()
    }

    fun signUp(email: String, password: String) {
        setLoading()
        val emailLimpo = email.trim().lowercase()
        if (emailLimpo.isBlank() || !emailLimpo.contains("@")) {
            _state.value = _state.value.copy(
                isLoading = false,
                user = null,
                error = "E-mail inválido."
            )
            return
        }
        val nome = emailLimpo.substringBefore("@").replaceFirstChar { it.uppercase() }
        val novoUsuario = DemoUser(
            uid = "user_${System.currentTimeMillis()}",
            email = emailLimpo,
            displayName = nome
        )
        usuariosCadastrados[emailLimpo] = Pair(password, novoUsuario)
        currentUser = novoUsuario
        ok()

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
        */
    }

    fun signOut() {
        // auth.signOut()
        currentUser = null
        _state.value = AuthUiState(user = null)
    }

    private fun setLoading() {
        _state.value = _state.value.copy(isLoading = true, error = null)
    }

    private fun ok() {
        _state.value = _state.value.copy(isLoading = false, user = currentUser, error = null)
        // _state.value = _state.value.copy(isLoading = false, user = auth.currentUser, error = null)
    }

    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
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
    */
}
