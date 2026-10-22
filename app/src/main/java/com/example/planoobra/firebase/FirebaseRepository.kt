package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.example.planoobra.models.Obra


object FirebaseRepository {
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("obras")

    val obras = mutableStateListOf<Obra>()
    val erroCarregamento = mutableStateOf<String?>(null)

    init {
        getObras {
            obras.clear()
            obras.addAll(it)
        }
    }

    fun createObra(obra: Obra) {
        createObra(obra) {}
    }

    fun createObra(obra: Obra, onComplete: (String?) -> Unit) {
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
    }

    fun editObra(obra: Obra, onComplete: (String?) -> Unit = {}) {
        database.child(obra.id).setValue(obra).addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível atualizar a obra. Tente novamente."
            )
        }
    }

    fun deleteObra(obra: Obra) {
        database.child(obra.id).removeValue()
    }

    fun getObras(onDataChange: (List<Obra>) -> Unit) {
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
    }
}