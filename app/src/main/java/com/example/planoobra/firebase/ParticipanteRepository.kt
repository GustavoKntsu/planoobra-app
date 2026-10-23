package com.example.planoobra.firebase

import androidx.compose.runtime.mutableStateListOf
import com.example.planoobra.models.Participante
import java.util.UUID

object ParticipanteRepository {
    val participantes = mutableStateListOf(
        Participante("João", "Mestre de Obras"),
        Participante("Eng. Carlos Mendes", "Engenheiro"),
        Participante("João Batista", "Pedreiro"),
        Participante("Ricardo Santos", "Armador"),
        Participante("Marcos Lima", "Serralheiro"),
        Participante("Pedro Alves", "Servente")
    )

    fun salvar(participante: Participante) {
        val id = participante.id.ifBlank { UUID.randomUUID().toString() }
        val indice = participantes.indexOfFirst { it.id == id }
        if (indice == -1) {
            participantes.add(participante.copy(id = id))
        } else {
            participantes[indice] = participante.copy(id = id)
        }
    }

    fun excluir(participante: Participante) {
        participantes.removeAll { it.id == participante.id }
    }
}
