package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

data class Obra(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val localizacao: String = "",
    val status: String = "Em andamento",
)

object FirebaseRepository {
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("obras")

    val obras = mutableStateListOf<Obra>()

    init {
        getObras {
            obras.clear()
            obras.addAll(it)
        }
    }

    fun createObra(obra: Obra) {
        val id = database.push().key!!
        database.child(id).setValue(obra.copy(id = id))
    }

    fun editObra(obra: Obra) {
        database.child(obra.id).setValue(obra)
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
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}