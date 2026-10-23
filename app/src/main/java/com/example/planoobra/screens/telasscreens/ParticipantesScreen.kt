package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planoobra.auth.AuthViewModel
import com.example.planoobra.auth.DemoUser
import com.example.planoobra.components.ParticipanteBottomSheet
import com.example.planoobra.firebase.HistoricoRepository
import com.example.planoobra.firebase.ParticipanteRepository
import com.example.planoobra.models.Participante
import com.example.planoobra.ui.theme.AzulObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import kotlinx.coroutines.launch

@Composable
fun ParticipantesScreen(modifier: Modifier = Modifier) {
    val autor = (AuthViewModel.currentUser ?: DemoUser()).displayName

    ParticipantesContent(
        participantes = ParticipanteRepository.participantes,
        onSalvar = { participante ->
            val jaExistia = ParticipanteRepository.participantes.any { it.id == participante.id }
            ParticipanteRepository.salvar(participante)
            HistoricoRepository.registrar(
                acao = if (jaExistia) "Participante atualizado" else "Participante adicionado",
                autor = autor,
                detalhe = "${participante.nome} — ${participante.funcao}",
                tipo = "Equipe"
            )
        },
        onExcluir = { participante ->
            ParticipanteRepository.excluir(participante)
            HistoricoRepository.registrar(
                acao = "Participante removido",
                autor = autor,
                detalhe = "${participante.nome} — ${participante.funcao}",
                tipo = "Equipe"
            )
        },
        modifier = modifier
    )
}

// Separada da ParticipantesScreen para o @Preview funcionar sem os repositórios
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParticipantesContent(
    participantes: List<Participante>,
    onSalvar: (Participante) -> Unit,
    onExcluir: (Participante) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtroFuncao by remember { mutableStateOf("Todos") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var participanteEmEdicao by remember { mutableStateOf<Participante?>(null) }
    var participanteParaExcluir by remember { mutableStateOf<Participante?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // A função selecionada continua como opção mesmo sem participantes, para a mensagem de vazio fazer sentido
    val funcoes = (participantes.map { it.funcao } + filtroFuncao)
        .filter { it != "Todos" }
        .distinct()
        .sorted()
    val participantesFiltrados = participantes
        .filter { filtroFuncao == "Todos" || it.funcao == filtroFuncao }
        .sortedBy { it.nome.lowercase() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    participanteEmEdicao = null
                    mostrarFormulario = true
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar participante")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (listOf("Todos") + funcoes).forEach { funcao ->
                        val quantidade = if (funcao == "Todos") participantes.size
                        else participantes.count { it.funcao == funcao }
                        FilterChip(
                            selected = filtroFuncao == funcao,
                            onClick = { filtroFuncao = funcao },
                            label = { Text("$funcao ($quantidade)") }
                        )
                    }
                }
            }

            if (participantesFiltrados.isEmpty()) {
                item {
                    MensagemVazia(
                        texto = if (filtroFuncao == "Todos") {
                            "Nenhum participante cadastrado.\nToque no + para adicionar."
                        } else {
                            "Nenhum participante com a função \"$filtroFuncao\"."
                        }
                    )
                }
            } else {
                items(items = participantesFiltrados, key = { it.id }) { participante ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AzulObra.copy(alpha = 0.1f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = participante.nome.trim().firstOrNull()?.uppercase() ?: "?",
                                        fontWeight = FontWeight.Bold,
                                        color = AzulObra
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = participante.nome,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = participante.funcao,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(onClick = {
                                participanteEmEdicao = participante
                                mostrarFormulario = true
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar participante",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { participanteParaExcluir = participante }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir participante",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        if (mostrarFormulario) {
            ParticipanteBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                participante = participanteEmEdicao,
                onDismissRequest = {
                    mostrarFormulario = false
                    participanteEmEdicao = null
                },
                onConfirmClick = { participante ->
                    onSalvar(participante)
                    mostrarFormulario = false
                    participanteEmEdicao = null
                    scope.launch { snackbarHostState.showSnackbar("Participante salvo!") }
                }
            )
        }

        participanteParaExcluir?.let { participante ->
            AlertDialog(
                onDismissRequest = { participanteParaExcluir = null },
                title = { Text("Excluir participante?") },
                text = { Text("O participante \"${participante.nome}\" será removido.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onExcluir(participante)
                            participanteParaExcluir = null
                        }
                    ) {
                        Text("Excluir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { participanteParaExcluir = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParticipantesScreenPreview() {
    PlanoObraTheme {
        ParticipantesContent(
            participantes = listOf(
                Participante("João", "Mestre de Obras", "1"),
                Participante("Eng. Carlos Mendes", "Engenheiro", "2"),
                Participante("João Batista", "Pedreiro", "3"),
                Participante("Ricardo Santos", "Armador", "4")
            ),
            onSalvar = {},
            onExcluir = {}
        )
    }
}
