package com.example.planoobra.models

// Sua data class (já existia, mas agora fica organizada aqui)
data class Obra(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val localizacao: String = "",
    val status: String = "Em andamento"
)

// Para o Gustavo Augusto (Materiais)
data class PedidoMaterial(
    val id: String = "",
    val obraId: String = "",
    val item: String = "",
    val quantidade: Double = 0.0,
    val unidade: String = "",
    val urgencia: String = "Normal",
    val status: String = "Aberto" // Aberto, Em análise, Aprovado, Atendido, Cancelado
)

// Para o Bruno (Tarefas)
data class Tarefa(
    val id: String = "",
    val obraId: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val status: String = "A fazer" // A fazer, Em andamento, Concluída
)

// Para o Callebe (Equipe/Ocorrências)
data class Ocorrencia(
    val id: String = "",
    val obraId: String = "",
    val trabalhadorNome: String = "",
    val data: String = "",
    val tipo: String = "", // Falta, Atraso, etc.
    val observacao: String = ""
)