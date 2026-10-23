package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.planoobra.models.PedidoMaterial
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object PedidoMaterialRepository {
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("pedidos_material")

    val pedidos = mutableStateListOf<PedidoMaterial>()
    val erroCarregamento = mutableStateOf<String?>(null)

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull {
                    it.getValue(PedidoMaterial::class.java)
                }
                pedidos.clear()
                // As chaves do push() são cronológicas: invertendo, os mais recentes ficam no topo
                pedidos.addAll(lista.reversed())
                erroCarregamento.value = null
            }

            override fun onCancelled(error: DatabaseError) {
                erroCarregamento.value =
                    "Não foi possível carregar os pedidos: ${error.message}"
            }
        })
    }

    fun salvar(pedido: PedidoMaterial, onComplete: (String?) -> Unit = {}) {
        val id = pedido.id.ifBlank { database.push().key.orEmpty() }
        if (id.isBlank()) {
            onComplete("Não foi possível criar um identificador para o pedido.")
            return
        }
        database.child(id).setValue(pedido.copy(id = id)).addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível salvar o pedido. Tente novamente."
            )
        }
    }

    fun excluir(pedido: PedidoMaterial, onComplete: (String?) -> Unit = {}) {
        database.child(pedido.id).removeValue().addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível excluir o pedido. Tente novamente."
            )
        }
    }
}
