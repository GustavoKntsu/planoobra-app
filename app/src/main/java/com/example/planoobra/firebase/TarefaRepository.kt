package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import com.example.planoobra.models.Tarefa

object TarefaRepository {
    val tarefas = mutableStateListOf<Tarefa>()

    fun salvar(tarefa: Tarefa) {
        val id = tarefa.id.ifBlank { java.util.UUID.randomUUID().toString() }
        val indice = tarefas.indexOfFirst { it.id == id }
        if (indice == -1) {
            tarefas.add(tarefa.copy(id = id))
        } else {
            tarefas[indice] = tarefa.copy(id = id)
        }
    }

    fun excluir(tarefa: Tarefa) {
        tarefas.removeAll { it.id == tarefa.id }
    }
}
