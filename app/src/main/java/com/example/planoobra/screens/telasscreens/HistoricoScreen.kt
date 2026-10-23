package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planoobra.firebase.HistoricoRepository
import com.example.planoobra.models.EventoHistorico
import com.example.planoobra.models.tiposEventoHistorico
import com.example.planoobra.ui.theme.AzulObra
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Distância do topo do item até o centro do marcador (20dp de margem + metade dos 14dp)
private val centroDoMarcador = 27.dp

@Composable
fun HistoricoScreen(modifier: Modifier = Modifier) {
    HistoricoContent(
        eventos = HistoricoRepository.eventos,
        modifier = modifier
    )
}

// Separada da HistoricoScreen para o @Preview funcionar sem os repositórios
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoricoContent(
    eventos: List<EventoHistorico>,
    modifier: Modifier = Modifier
) {
    var filtroTipo by remember { mutableStateOf("Todos") }

    val eventosFiltrados = eventos
        .filter { filtroTipo == "Todos" || it.tipo == filtroTipo }
        .sortedByDescending { it.dataHora }

    Scaffold(modifier = modifier) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (listOf("Todos") + tiposEventoHistorico).forEach { tipo ->
                        FilterChip(
                            selected = filtroTipo == tipo,
                            onClick = { filtroTipo = tipo },
                            label = { Text(tipo) }
                        )
                    }
                }
            }

            if (eventosFiltrados.isEmpty()) {
                item {
                    MensagemVazia(
                        texto = if (filtroTipo == "Todos") {
                            "Nenhum evento registrado."
                        } else {
                            "Nenhum evento do tipo \"$filtroTipo\"."
                        }
                    )
                }
            } else {
                itemsIndexed(items = eventosFiltrados, key = { _, evento -> evento.id }) { indice, evento ->
                    ItemLinhaDoTempo(
                        evento = evento,
                        primeiro = indice == 0,
                        ultimo = indice == eventosFiltrados.lastIndex
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ItemLinhaDoTempo(
    evento: EventoHistorico,
    primeiro: Boolean,
    ultimo: Boolean
) {
    val cor = corDoTipoEvento(evento.tipo)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!(primeiro && ultimo)) {
                Box(
                    modifier = Modifier
                        .padding(top = if (primeiro) centroDoMarcador else 0.dp)
                        .width(2.dp)
                        .then(
                            if (ultimo) Modifier.height(centroDoMarcador)
                            else Modifier.fillMaxHeight()
                        )
                        .background(Color(0xFFE0E0E0))
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(14.dp)
                    .background(cor, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
        Spacer(Modifier.width(8.dp))
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatarDataHora(evento.dataHora),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = cor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = evento.tipo,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = cor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = evento.acao,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (evento.detalhe.isNotBlank()) {
                    Text(
                        text = evento.detalhe,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "por ${evento.autor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

private fun corDoTipoEvento(tipo: String): Color = when (tipo) {
    "Obra" -> AzulObra
    "Tarefa" -> Color(0xFF2E7D32)
    "Material" -> LaranjaObra
    "Ocorrência" -> Color(0xFFD32F2F)
    "Equipe" -> Color(0xFF8E6C00)
    else -> Color.Gray
}

private fun formatarDataHora(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(millis))

@Preview(showBackground = true)
@Composable
fun HistoricoScreenPreview() {
    PlanoObraTheme {
        HistoricoContent(
            eventos = listOf(
                EventoHistorico("a", 1792755600000, "Participante adicionado", "Eng. Carlos Mendes", "Ana Ribeiro — Eletricista", "Equipe"),
                EventoHistorico("b", 1792748400000, "Ocorrência registrada", "Eng. Carlos Mendes", "Atraso — João Batista", "Ocorrência"),
                EventoHistorico("c", 1792681800000, "Material pedido", "Eng. Carlos Mendes", "Cimento CP-II 50kg — 80 sacos", "Material"),
                EventoHistorico("d", 1792508400000, "Tarefa concluída", "Equipe Topografia", "Terraplenagem e compactação da base", "Tarefa"),
                EventoHistorico("e", 1792321200000, "Obra cadastrada", "Eng. Carlos Mendes", "Galpão Logístico Via Norte", "Obra")
            )
        )
    }
}
