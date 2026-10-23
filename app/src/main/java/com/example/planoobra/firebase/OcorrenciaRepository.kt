package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.planoobra.models.Ocorrencia
// --- FIREBASE COMENTADO PARA MODO DEMONSTRAÇÃO ---
// import com.google.firebase.Firebase
// import com.google.firebase.database.DataSnapshot
// import com.google.firebase.database.DatabaseError
// import com.google.firebase.database.ValueEventListener
// import com.google.firebase.database.database
import java.util.UUID

object OcorrenciaRepository {
    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("ocorrencias")
    */

    val ocorrencias = mutableStateListOf(
        Ocorrencia(
            id = "oc_1",
            obraId = "obra_1",
            trabalhadorNome = "João Batista (Pedreiro)",
            data = "23/10/2026",
            tipo = "Atraso",
            observacao = "Chegou às 08h35 devido a atraso no transporte público."
        ),
        Ocorrencia(
            id = "oc_2",
            obraId = "obra_1",
            trabalhadorNome = "Ricardo Santos (Armador)",
            data = "22/10/2026",
            tipo = "Falta",
            observacao = "Não compareceu e ainda não apresentou justificativa."
        ),
        Ocorrencia(
            id = "oc_3",
            obraId = "obra_2",
            trabalhadorNome = "Marcos Lima (Serralheiro)",
            data = "21/10/2026",
            tipo = "Falta justificada",
            observacao = "Apresentou atestado médico de consulta oftalmológica."
        ),
        Ocorrencia(
            id = "oc_4",
            obraId = "obra_2",
            trabalhadorNome = "Pedro Alves (Ajudante Geral)",
            data = "20/10/2026",
            tipo = "Saída antecipada",
            observacao = "Liberado às 15h00 para resolver pendência familiar."
        )
    )
    val erroCarregamento = mutableStateOf<String?>(null)

    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
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
    */

    fun salvar(ocorrencia: Ocorrencia, onComplete: (String?) -> Unit = {}) {
        val id = ocorrencia.id.ifBlank { "oc_${UUID.randomUUID().toString().take(8)}" }
        val indice = ocorrencias.indexOfFirst { it.id == id }
        if (indice == -1) {
            ocorrencias.add(0, ocorrencia.copy(id = id))
        } else {
            ocorrencias[indice] = ocorrencia.copy(id = id)
        }
        onComplete(null)

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
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
        */
    }

    fun excluir(ocorrencia: Ocorrencia, onComplete: (String?) -> Unit = {}) {
        ocorrencias.removeAll { it.id == ocorrencia.id }
        onComplete(null)

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        database.child(ocorrencia.id).removeValue().addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível excluir a ocorrência. Tente novamente."
            )
        }
        */
    }
}
