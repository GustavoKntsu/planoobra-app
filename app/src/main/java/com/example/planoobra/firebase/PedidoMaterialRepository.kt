package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.planoobra.models.PedidoMaterial
// --- FIREBASE COMENTADO PARA MODO DEMONSTRAÇÃO ---
// import com.google.firebase.Firebase
// import com.google.firebase.database.DataSnapshot
// import com.google.firebase.database.DatabaseError
// import com.google.firebase.database.ValueEventListener
// import com.google.firebase.database.database
import java.util.UUID

object PedidoMaterialRepository {
    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("pedidos_material")
    */

    val pedidos = mutableStateListOf(
        PedidoMaterial(
            id = "ped_1",
            obraId = "obra_1",
            item = "Cimento CP-II 50kg",
            quantidade = 80.0,
            unidade = "sacos",
            urgencia = "Urgente",
            status = "Aberto"
        ),
        PedidoMaterial(
            id = "ped_2",
            obraId = "obra_1",
            item = "Vergalhão CA-50 10mm",
            quantidade = 120.0,
            unidade = "barras",
            urgencia = "Urgente",
            status = "Em análise"
        ),
        PedidoMaterial(
            id = "ped_3",
            obraId = "obra_2",
            item = "Concreto Usinado FCK 30",
            quantidade = 24.0,
            unidade = "m³",
            urgencia = "Normal",
            status = "Aprovado"
        ),
        PedidoMaterial(
            id = "ped_4",
            obraId = "obra_2",
            item = "Telha Galvalume Trapezoidal",
            quantidade = 150.0,
            unidade = "m²",
            urgencia = "Normal",
            status = "Aberto"
        ),
        PedidoMaterial(
            id = "ped_5",
            obraId = "obra_3",
            item = "Cabo Flexível 2,5mm² Preto",
            quantidade = 6.0,
            unidade = "rolos",
            urgencia = "Baixa",
            status = "Atendido"
        )
    )
    val erroCarregamento = mutableStateOf<String?>(null)

    /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
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
    */

    fun salvar(pedido: PedidoMaterial, onComplete: (String?) -> Unit = {}) {
        val id = pedido.id.ifBlank { "ped_${UUID.randomUUID().toString().take(8)}" }
        val indice = pedidos.indexOfFirst { it.id == id }
        if (indice == -1) {
            pedidos.add(0, pedido.copy(id = id))
        } else {
            pedidos[indice] = pedido.copy(id = id)
        }
        onComplete(null)

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
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
        */
    }

    fun excluir(pedido: PedidoMaterial, onComplete: (String?) -> Unit = {}) {
        pedidos.removeAll { it.id == pedido.id }
        onComplete(null)

        /* --- FIREBASE COMENTADO PARA DEMONSTRAÇÃO ---
        database.child(pedido.id).removeValue().addOnCompleteListener { result ->
            onComplete(
                if (result.isSuccessful) null
                else result.exception?.localizedMessage
                    ?: "Não foi possível excluir o pedido. Tente novamente."
            )
        }
        */
    }
}
