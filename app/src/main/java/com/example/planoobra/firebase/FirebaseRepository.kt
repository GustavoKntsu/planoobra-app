package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
// --- FIREBASE COMENTADO PARA MODO DEMONSTRAÇÃO ---
// import com.google.firebase.Firebase
// import com.google.firebase.database.DataSnapshot
// import com.google.firebase.database.DatabaseError
// import com.google.firebase.database.ValueEventListener
// import com.google.firebase.database.database
import com.example.planoobra.models.Obra
import java.util.UUID


object FirebaseRepository {
    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("obras")
    */

    val obras = mutableStateListOf(
        Obra(
            id = "obra_1",
            titulo = "Residencial Parque das Águas",
            descricao = "Construção de condomínio vertical com 2 torres de 12 andares e área de lazer completa.",
            localizacao = "Av. Brasil, 1450 - Centro",
            status = "Em andamento"
        ),
        Obra(
            id = "obra_2",
            titulo = "Galpão Logístico Via Norte",
            descricao = "Estrutura pré-moldada e piso industrial de alta resistência para centro de distribuição.",
            localizacao = "Rod. Anhanguera, Km 42 - Distrito Industrial",
            status = "Em andamento"
        ),
        Obra(
            id = "obra_3",
            titulo = "Reforma Clínica Santa Clara",
            descricao = "Adequação hidráulica, elétrica e acabamento fino de 8 consultórios médicos.",
            localizacao = "Rua das Palmeiras, 320 - Jardim América",
            status = "Pausada"
        ),
        Obra(
            id = "1",
            titulo = "Reforma da Cozinha",
            descricao = "Troca de azulejos",
            localizacao = "Setor Bueno",
            status = "Em andamento"
        ),
        Obra(
            id = "2",
            titulo = "Construção do Muro",
            descricao = "Muro de arrimo",
            localizacao = "Jardim Goiás",
            status = "Pausada"
        )
    )
    val erroCarregamento = mutableStateOf<String?>(null)

    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
    init {
        getObras {
            obras.clear()
            obras.addAll(it)
        }
    }
    */

    fun createObra(obra: Obra) {
        createObra(obra) {}
    }

    fun createObra(obra: Obra, onComplete: (String?) -> Unit) {
        val id = obra.id.ifBlank { "obra_${UUID.randomUUID().toString().take(8)}" }
        obras.add(obra.copy(id = id))
        onComplete(null)

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        val id = database.push().key
        if (id == null) {
            onComplete("Não foi possível criar um identificador para a obra.")
            return
        }
        database.child(id).setValue(obra.copy(id = id)).addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível salvar a obra. Tente novamente."
            )
        }
        */
    }

    fun editObra(obra: Obra, onComplete: (String?) -> Unit = {}) {
        val index = obras.indexOfFirst { it.id == obra.id }
        if (index != -1) {
            obras[index] = obra
            onComplete(null)
        } else {
            onComplete("Obra não encontrada.")
        }

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        database.child(obra.id).setValue(obra).addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível atualizar a obra. Tente novamente."
            )
        }
        */
    }

    fun deleteObra(obra: Obra) {
        obras.removeAll { it.id == obra.id }
        // database.child(obra.id).removeValue()
    }

    fun getObras(onDataChange: (List<Obra>) -> Unit) {
        onDataChange(obras.toList())
        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Obra>()
                snapshot.children.forEach {
                    it.getValue(Obra::class.java)?.let { obra -> lista.add(obra) }
                }
                onDataChange(lista)
                erroCarregamento.value = null
            }

            override fun onCancelled(error: DatabaseError) {
                erroCarregamento.value =
                    "Não foi possível carregar as obras: ${error.message}"
            }
        })
        */
    }
}