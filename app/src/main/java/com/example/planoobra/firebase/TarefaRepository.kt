package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import com.example.planoobra.models.Tarefa

object TarefaRepository {
    val tarefas = mutableStateListOf(
        Tarefa(
            id = "tarefa_1",
            obraId = "obra_1",
            titulo = "Concretagem da laje do 4º pavimento",
            descricao = "Conferir escoramento metálico e armaduras antes do lançamento do concreto usinado.",
            responsavelId = "Eng. Carlos Mendes",
            prazo = "28/10/2026",
            status = "Em andamento",
            atualizadoEm = System.currentTimeMillis(),
            atualizadoPor = "demo_user_01"
        ),
        Tarefa(
            id = "tarefa_2",
            obraId = "obra_1",
            titulo = "Instalação da tubulação de água fria - Torre A",
            descricao = "Executar prumadas hidráulicas do 1º ao 3º andar conforme projeto executivo.",
            responsavelId = "Mestre Roberto",
            prazo = "30/10/2026",
            status = "A fazer",
            atualizadoEm = System.currentTimeMillis(),
            atualizadoPor = "demo_user_01"
        ),
        Tarefa(
            id = "tarefa_3",
            obraId = "obra_2",
            titulo = "Montagem das tesouras metálicas da cobertura",
            descricao = "Içamento com caminhão munck e fixação dos chumbadores nos pilares principais.",
            responsavelId = "Eng. Carlos Mendes",
            prazo = "25/10/2026",
            status = "A fazer",
            atualizadoEm = System.currentTimeMillis(),
            atualizadoPor = "demo_user_01"
        ),
        Tarefa(
            id = "tarefa_4",
            obraId = "obra_2",
            titulo = "Terraplenagem e compactação da base",
            descricao = "Nivelamento a laser e ensaio de compactação do solo para o piso industrial.",
            responsavelId = "Equipe Topografia",
            prazo = "15/10/2026",
            status = "Concluída",
            atualizadoEm = System.currentTimeMillis(),
            atualizadoPor = "demo_user_01"
        ),
        Tarefa(
            id = "tarefa_5",
            obraId = "obra_3",
            titulo = "Revisão do quadro de distribuição elétrica",
            descricao = "Substituição de disjuntores e identificação dos circuitos dos consultórios.",
            responsavelId = "Eletricista Marcos",
            prazo = "05/11/2026",
            status = "A fazer",
            atualizadoEm = System.currentTimeMillis(),
            atualizadoPor = "demo_user_01"
        )
    )

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
