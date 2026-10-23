package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import com.example.planoobra.models.EventoHistorico
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

object HistoricoRepository {
    private val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"))

    private fun millis(dataHora: String): Long = formatoEntrada.parse(dataHora)?.time ?: 0L

    val eventos = mutableStateListOf(
        EventoHistorico(
            id = "ev_1",
            dataHora = millis("23/10/2026 08:40"),
            acao = "Ocorrência registrada",
            autor = "Eng. Carlos Mendes",
            detalhe = "Atraso — João Batista",
            tipo = "Ocorrência"
        ),
        EventoHistorico(
            id = "ev_2",
            dataHora = millis("22/10/2026 16:10"),
            acao = "Material pedido",
            autor = "Eng. Carlos Mendes",
            detalhe = "Cimento CP-II 50kg — 80 sacos",
            tipo = "Material"
        ),
        EventoHistorico(
            id = "ev_3",
            dataHora = millis("22/10/2026 11:25"),
            acao = "Pedido aprovado",
            autor = "Mestre Roberto",
            detalhe = "Concreto Usinado FCK 30 — 24 m³",
            tipo = "Material"
        ),
        EventoHistorico(
            id = "ev_4",
            dataHora = millis("21/10/2026 09:00"),
            acao = "Participante adicionado",
            autor = "Eng. Carlos Mendes",
            detalhe = "Marcos Lima — Serralheiro",
            tipo = "Equipe"
        ),
        EventoHistorico(
            id = "ev_5",
            dataHora = millis("20/10/2026 17:30"),
            acao = "Tarefa concluída",
            autor = "Equipe Topografia",
            detalhe = "Terraplenagem e compactação da base",
            tipo = "Tarefa"
        ),
        EventoHistorico(
            id = "ev_6",
            dataHora = millis("19/10/2026 14:15"),
            acao = "Tarefa criada",
            autor = "Eng. Carlos Mendes",
            detalhe = "Concretagem da laje do 4º pavimento",
            tipo = "Tarefa"
        ),
        EventoHistorico(
            id = "ev_7",
            dataHora = millis("18/10/2026 10:00"),
            acao = "Obra pausada",
            autor = "Eng. Carlos Mendes",
            detalhe = "Reforma Clínica Santa Clara",
            tipo = "Obra"
        ),
        EventoHistorico(
            id = "ev_8",
            dataHora = millis("15/10/2026 08:00"),
            acao = "Obra cadastrada",
            autor = "Eng. Carlos Mendes",
            detalhe = "Galpão Logístico Via Norte",
            tipo = "Obra"
        )
    )

    fun registrar(acao: String, autor: String, detalhe: String = "", tipo: String = "Obra") {
        eventos.add(
            0,
            EventoHistorico(
                id = "ev_${UUID.randomUUID().toString().take(8)}",
                dataHora = System.currentTimeMillis(),
                acao = acao,
                autor = autor,
                detalhe = detalhe,
                tipo = tipo
            )
        )
    }
}
