package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.planoobra.models.Ocorrencia
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object OcorrenciaRepository {
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("ocorrencias")

    val ocorrencias = mutableStateListOf<Ocorrencia>()
    val erroCarregamento = mutableStateOf<String?>(null)

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull {
                    it.getValue(Ocorrencia::class.java)
                }
                ocorrencias.clear()
                ocorrencias.addAll(lista)
                erroCarregamento.value = null
            }

            override fun onCancelled(error: DatabaseError) {
                erroCarregamento.value =
                    "Não foi possível carregar as ocorrências: ${error.message}"
            }
        })
    }

    fun salvar(ocorrencia: Ocorrencia, onComplete: (String?) -> Unit = {}) {
        val id = ocorrencia.id.ifBlank { database.push().key.orEmpty() }
        if (id.isBlank()) {
            onComplete("Não foi possível criar um identificador para a ocorrência.")
            return
        }
        database.child(id).setValue(ocorrencia.copy(id = id)).addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível salvar a ocorrência. Tente novamente."
            )
        }
    }

    fun excluir(ocorrencia: Ocorrencia, onComplete: (String?) -> Unit = {}) {
        database.child(ocorrencia.id).removeValue().addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível excluir a ocorrência. Tente novamente."
            )
        }
    }
}
