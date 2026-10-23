package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.planoobra.components.OcorrenciaBottomSheet
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.firebase.OcorrenciaRepository
import com.example.planoobra.models.Obra
import com.example.planoobra.models.Ocorrencia
import com.example.planoobra.models.tiposOcorrencia
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import kotlinx.coroutines.launch

@Composable
fun OcorrenciasScreen(modifier: Modifier = Modifier) {
    val erroCarregamento by OcorrenciaRepository.erroCarregamento

    OcorrenciasContent(
        ocorrencias = OcorrenciaRepository.ocorrencias,
        obras = FirebaseRepository.obras,
        erroCarregamento = erroCarregamento,
        onSalvar = OcorrenciaRepository::salvar,
        onExcluir = OcorrenciaRepository::excluir,
        modifier = modifier
    )
}

// Separada da OcorrenciasScreen para o @Preview funcionar sem o Firebase
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OcorrenciasContent(
    ocorrencias: List<Ocorrencia>,
    obras: List<Obra>,
    erroCarregamento: String?,
    onSalvar: (Ocorrencia, (String?) -> Unit) -> Unit,
    onExcluir: (Ocorrencia, (String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtroTipo by remember { mutableStateOf("Todos") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var salvando by remember { mutableStateOf(false) }
    var ocorrenciaEmEdicao by remember { mutableStateOf<Ocorrencia?>(null) }
    var ocorrenciaParaExcluir by remember { mutableStateOf<Ocorrencia?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(erroCarregamento) {
        erroCarregamento?.let { snackbarHostState.showSnackbar(it) }
    }

    val obrasPorId = obras.associateBy { it.id }
    // "dd/MM/yyyy" -> "yyyyMMdd" para ordenar da mais recente para a mais antiga
    val ocorrenciasFiltradas = ocorrencias
        .filter { filtroTipo == "Todos" || it.tipo == filtroTipo }
        .sortedByDescending { it.data.split("/").reversed().joinToString("") }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    ocorrenciaEmEdicao = null
                    salvando = false
                    mostrarFormulario = true
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Registrar ocorrência")
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
                    (listOf("Todos") + tiposOcorrencia).forEach { tipo ->
                        FilterChip(
                            selected = filtroTipo == tipo,
                            onClick = { filtroTipo = tipo },
                            label = { Text(tipo) }
                        )
                    }
                }
            }

            if (ocorrenciasFiltradas.isEmpty()) {
                item {
                    MensagemVazia(
                        texto = when {
                            obras.isEmpty() -> "Cadastre uma obra na aba Início para registrar ocorrências."
                            filtroTipo == "Todos" -> "Nenhuma ocorrência registrada.\nToque no + para registrar uma falta ou ocorrência."
                            else -> "Nenhuma ocorrência do tipo \"$filtroTipo\"."
                        }
                    )
                }
            } else {
                items(items = ocorrenciasFiltradas, key = { it.id }) { ocorrencia ->
                    val obra = obrasPorId[ocorrencia.obraId]
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ocorrencia.trabalhadorNome,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = {
                                    ocorrenciaEmEdicao = ocorrencia
                                    salvando = false
                                    mostrarFormulario = true
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar ocorrência",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { ocorrenciaParaExcluir = ocorrencia }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir ocorrência",
                                        tint = Color.Red
                                    )
                                }
                            }
                            Text(
                                text = obra?.titulo ?: "Obra removida",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Data: ${ocorrencia.data}",
                                modifier = Modifier.padding(top = 4.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (ocorrencia.observacao.isNotBlank()) {
                                Text(
                                    text = ocorrencia.observacao,
                                    modifier = Modifier.padding(top = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            SuggestionChip(
                                onClick = {},
                                label = { Text(ocorrencia.tipo) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = corDoTipo(ocorrencia.tipo).copy(alpha = 0.15f),
                                    labelColor = corDoTipo(ocorrencia.tipo)
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        if (mostrarFormulario) {
            OcorrenciaBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                ocorrencia = ocorrenciaEmEdicao,
                obras = obras,
                isSaving = salvando,
                onDismissRequest = {
                    mostrarFormulario = false
                    ocorrenciaEmEdicao = null
                    salvando = false
                },
                onConfirmClick = { ocorrencia ->
                    salvando = true
                    onSalvar(ocorrencia) { erro ->
                        salvando = false
                        if (erro == null) {
                            mostrarFormulario = false
                            ocorrenciaEmEdicao = null
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(erro) }
                        }
                    }
                }
            )
        }

        ocorrenciaParaExcluir?.let { ocorrencia ->
            AlertDialog(
                onDismissRequest = { ocorrenciaParaExcluir = null },
                title = { Text("Excluir ocorrência?") },
                text = {
                    Text("A ocorrência de ${ocorrencia.trabalhadorNome} em ${ocorrencia.data} será removida.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onExcluir(ocorrencia) { erro ->
                                erro?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
                            }
                            ocorrenciaParaExcluir = null
                        }
                    ) {
                        Text("Excluir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { ocorrenciaParaExcluir = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun corDoTipo(tipo: String): Color = when (tipo) {
    "Falta", "Acidente" -> Color.Red
    "Atraso", "Saída antecipada" -> LaranjaObra
    "Falta justificada" -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(showBackground = true)
@Composable
fun OcorrenciasScreenPreview() {
    val obras = listOf(
        Obra(id = "1", titulo = "Residencial Jardins"),
        Obra(id = "2", titulo = "Galpão Industrial")
    )
    PlanoObraTheme {
        OcorrenciasContent(
            ocorrencias = listOf(
                Ocorrencia("a", "1", "João Silva", "22/10/2026", "Falta", ""),
                Ocorrencia("b", "1", "Carlos Souza", "21/10/2026", "Atraso", "Chegou às 8h40, ônibus atrasou."),
                Ocorrencia("c", "2", "Marcos Lima", "20/10/2026", "Falta justificada", "Atestado médico de 2 dias."),
                Ocorrencia("d", "2", "Pedro Alves", "18/10/2026", "Acidente", "Corte leve na mão, atendido no local.")
            ),
            obras = obras,
            erroCarregamento = null,
            onSalvar = { _, concluir -> concluir(null) },
            onExcluir = { _, concluir -> concluir(null) }
        )
    }
}
