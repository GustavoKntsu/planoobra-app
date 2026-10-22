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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.planoobra.components.EditCarBottomSheet
import com.example.planoobra.components.ObraBottomSheet
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.firebase.TarefaRepository
import com.example.planoobra.models.Obra
import com.example.planoobra.models.Tarefa
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private val statusTarefa = listOf("A fazer", "Em andamento", "Concluída")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var mostrarObras by remember { mutableStateOf(false) }
    var filtroStatus by remember { mutableStateOf("Todas") }
    var mostrarFormularioTarefa by remember { mutableStateOf(false) }
    var salvandoTarefa by remember { mutableStateOf(false) }
    var mostrarFormularioObra by remember { mutableStateOf(false) }
    var tarefaEmEdicao by remember { mutableStateOf<Tarefa?>(null) }
    var tarefaParaExcluir by remember { mutableStateOf<Tarefa?>(null) }

    var obraEmEdicao by remember { mutableStateOf<Obra?>(null) }
    var tituloObra by remember { mutableStateOf("") }
    var descricaoObra by remember { mutableStateOf("") }
    var localizacaoObra by remember { mutableStateOf("") }
    var statusObra by remember { mutableStateOf("Em andamento") }

    val obras = FirebaseRepository.obras
    val tarefas = TarefaRepository.tarefas
    val usuarioAtual = FirebaseAuth.getInstance().currentUser
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val erroCarregamentoObras by FirebaseRepository.erroCarregamento

    LaunchedEffect(erroCarregamentoObras) {
        erroCarregamentoObras?.let { snackbarHostState.showSnackbar(it) }
    }

    val obrasVisiveis = obras.associateBy { it.id }
    val tarefasFiltradas = tarefas
        .filter { it.obraId.isBlank() || it.obraId in obrasVisiveis.keys }
        .filter { filtroStatus == "Todas" || it.status == filtroStatus }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (mostrarObras) {
                        obraEmEdicao = null
                        tituloObra = ""
                        descricaoObra = ""
                        localizacaoObra = ""
                        statusObra = "Em andamento"
                        mostrarFormularioObra = true
                    } else {
                        tarefaEmEdicao = null
                        salvandoTarefa = false
                        mostrarFormularioTarefa = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = if (mostrarObras) "Adicionar obra" else "Adicionar tarefa"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !mostrarObras,
                        onClick = { mostrarObras = false },
                        label = { Text("Tarefas") }
                    )
                    FilterChip(
                        selected = mostrarObras,
                        onClick = { mostrarObras = true },
                        label = { Text("Obras") }
                    )
                }
            }

            if (mostrarObras) {
                if (obras.isEmpty()) {
                    item {
                        MensagemVazia(
                            texto = "Nenhuma obra cadastrada.\nToque no + para começar."
                        )
                    }
                } else {
                    items(items = obras, key = { it.id }) { obra ->
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
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = obra.titulo,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (obra.localizacao.isNotBlank()) {
                                        Text(
                                            text = "📍 ${obra.localizacao}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(obra.status) },
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            obraEmEdicao = obra
                                            tituloObra = obra.titulo
                                            descricaoObra = obra.descricao
                                            localizacaoObra = obra.localizacao
                                            statusObra = obra.status
                                            mostrarFormularioObra = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar obra",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { FirebaseRepository.deleteObra(obra) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Excluir obra",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (listOf("Todas") + statusTarefa).forEach { status ->
                            FilterChip(
                                selected = filtroStatus == status,
                                onClick = { filtroStatus = status },
                                label = { Text(status) }
                            )
                        }
                    }
                }

                if (tarefasFiltradas.isEmpty()) {
                    item {
                        MensagemVazia(
                            texto = if (obras.isEmpty()) {
                                "Cadastre uma obra para começar a organizar tarefas."
                            } else if (filtroStatus == "Todas") {
                                "Nenhuma tarefa cadastrada.\nToque no + para adicionar."
                            } else {
                                "Nenhuma tarefa com o status \"$filtroStatus\"."
                            },
                            acao = if (obras.isEmpty()) {
                                { mostrarObras = true }
                            } else {
                                null
                            },
                            textoAcao = "Ver obras"
                        )
                    }
                } else {
                    items(items = tarefasFiltradas, key = { it.id }) { tarefa ->
                        val obra = obrasVisiveis[tarefa.obraId]
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = tarefa.titulo,
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(onClick = {
                                        tarefaEmEdicao = tarefa
                                        mostrarFormularioTarefa = true
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar tarefa",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { tarefaParaExcluir = tarefa }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Excluir tarefa",
                                            tint = Color.Red
                                        )
                                    }
                                }
                                Text(
                                    text = obra?.titulo ?: "Sem obra vinculada",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (tarefa.descricao.isNotBlank()) {
                                    Text(
                                        text = tarefa.descricao,
                                        modifier = Modifier.padding(top = 4.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "Responsável: ${
                                        if (
                                            usuarioAtual != null &&
                                            tarefa.responsavelId == usuarioAtual.uid
                                        ) {
                                            usuarioAtual.displayName ?: usuarioAtual.email
                                                ?: tarefa.responsavelId
                                        } else {
                                            tarefa.responsavelId.ifBlank { "Não informado" }
                                        }
                                    }",
                                    modifier = Modifier.padding(top = 4.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (tarefa.prazo.isNotBlank()) {
                                    Text(
                                        text = "Prazo: ${tarefa.prazo}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                SuggestionChip(
                                    onClick = {
                                        tarefaEmEdicao = tarefa
                                        mostrarFormularioTarefa = true
                                    },
                                    label = { Text(tarefa.status) },
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }

        if (mostrarFormularioTarefa) {
            EditCarBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                tarefa = tarefaEmEdicao,
                obras = obras,
                currentUserId = usuarioAtual?.uid.orEmpty(),
                currentUserLabel = usuarioAtual?.displayName ?: usuarioAtual?.email.orEmpty(),
                isSaving = salvandoTarefa,
                onDismissRequest = {
                    mostrarFormularioTarefa = false
                    tarefaEmEdicao = null
                    salvandoTarefa = false
                },
                onConfirmClick = { tarefa ->
                    TarefaRepository.salvar(tarefa)
                    mostrarFormularioTarefa = false
                    tarefaEmEdicao = null
                }
            )
        }

        if (mostrarFormularioObra) {
            ObraBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                tituloTela = if (obraEmEdicao == null) "Nova Obra" else "Editar Obra",
                titulo = tituloObra,
                onTituloChange = { tituloObra = it },
                descricao = descricaoObra,
                onDescricaoChange = { descricaoObra = it },
                localizacao = localizacaoObra,
                onLocalizacaoChange = { localizacaoObra = it },
                status = statusObra,
                onStatusChange = { statusObra = it },
                onDismissRequest = {
                    mostrarFormularioObra = false
                    obraEmEdicao = null
                },
                onConfirmClick = {
                    val obraAtual = obraEmEdicao
                    val aoConcluir: (String?) -> Unit = { erro ->
                        if (erro == null) {
                            mostrarFormularioObra = false
                            obraEmEdicao = null
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(erro) }
                        }
                    }
                    if (obraAtual == null) {
                        FirebaseRepository.createObra(
                            Obra(
                                titulo = tituloObra,
                                descricao = descricaoObra,
                                localizacao = localizacaoObra,
                                status = statusObra
                            ),
                            onComplete = aoConcluir
                        )
                    } else {
                        FirebaseRepository.editObra(
                            obraAtual.copy(
                                titulo = tituloObra,
                                descricao = descricaoObra,
                                localizacao = localizacaoObra,
                                status = statusObra
                            ),
                            onComplete = aoConcluir
                        )
                    }
                }
            )
        }

        tarefaParaExcluir?.let { tarefa ->
            AlertDialog(
                onDismissRequest = { tarefaParaExcluir = null },
                title = { Text("Excluir tarefa?") },
                text = { Text("A tarefa \"${tarefa.titulo}\" será removida.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            TarefaRepository.excluir(tarefa)
                            tarefaParaExcluir = null
                        }
                    ) {
                        Text("Excluir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tarefaParaExcluir = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun MensagemVazia(
    texto: String,
    acao: (() -> Unit)? = null,
    textoAcao: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = texto,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (acao != null) {
                TextButton(onClick = acao) { Text(textoAcao) }
            }
        }
    }
}
