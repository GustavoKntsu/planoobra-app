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
import com.example.planoobra.components.PedidoMaterialBottomSheet
import com.example.planoobra.components.formatarQuantidade
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.firebase.PedidoMaterialRepository
import com.example.planoobra.models.Obra
import com.example.planoobra.models.PedidoMaterial
import com.example.planoobra.models.statusPedidoMaterial
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import kotlinx.coroutines.launch

@Composable
fun MateriaisScreen(modifier: Modifier = Modifier) {
    val erroCarregamento by PedidoMaterialRepository.erroCarregamento

    MateriaisContent(
        pedidos = PedidoMaterialRepository.pedidos,
        obras = FirebaseRepository.obras,
        erroCarregamento = erroCarregamento,
        onSalvar = PedidoMaterialRepository::salvar,
        onExcluir = PedidoMaterialRepository::excluir,
        modifier = modifier
    )
}

// Separada da MateriaisScreen para o @Preview funcionar sem o Firebase
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MateriaisContent(
    pedidos: List<PedidoMaterial>,
    obras: List<Obra>,
    erroCarregamento: String?,
    onSalvar: (PedidoMaterial, (String?) -> Unit) -> Unit,
    onExcluir: (PedidoMaterial, (String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtroStatus by remember { mutableStateOf("Todos") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var salvando by remember { mutableStateOf(false) }
    var pedidoEmEdicao by remember { mutableStateOf<PedidoMaterial?>(null) }
    var pedidoParaExcluir by remember { mutableStateOf<PedidoMaterial?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(erroCarregamento) {
        erroCarregamento?.let { snackbarHostState.showSnackbar(it) }
    }

    val obrasPorId = obras.associateBy { it.id }
    val pedidosFiltrados = pedidos.filter {
        filtroStatus == "Todos" || it.status == filtroStatus
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    pedidoEmEdicao = null
                    salvando = false
                    mostrarFormulario = true
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Novo pedido de material")
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
                    (listOf("Todos") + statusPedidoMaterial).forEach { status ->
                        val quantidade = if (status == "Todos") pedidos.size
                        else pedidos.count { it.status == status }
                        FilterChip(
                            selected = filtroStatus == status,
                            onClick = { filtroStatus = status },
                            label = { Text("$status ($quantidade)") }
                        )
                    }
                }
            }

            if (pedidosFiltrados.isEmpty()) {
                item {
                    MensagemVazia(
                        texto = when {
                            obras.isEmpty() -> "Cadastre uma obra na aba Início para pedir materiais."
                            filtroStatus == "Todos" -> "Nenhum pedido de material.\nToque no + para fazer um pedido."
                            else -> "Nenhum pedido com o status \"$filtroStatus\"."
                        }
                    )
                }
            } else {
                items(items = pedidosFiltrados, key = { it.id }) { pedido ->
                    val obra = obrasPorId[pedido.obraId]
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
                                    text = pedido.item,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = {
                                    pedidoEmEdicao = pedido
                                    salvando = false
                                    mostrarFormulario = true
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar pedido",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { pedidoParaExcluir = pedido }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir pedido",
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
                                text = "Quantidade: ${formatarQuantidade(pedido.quantidade)} ${pedido.unidade}",
                                modifier = Modifier.padding(top = 4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Urgência: ${pedido.urgencia}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (pedido.urgencia == "Urgente") FontWeight.Bold else FontWeight.Normal,
                                color = if (pedido.urgencia == "Urgente") Color.Red else Color.Unspecified
                            )
                            SuggestionChip(
                                onClick = {
                                    pedidoEmEdicao = pedido
                                    salvando = false
                                    mostrarFormulario = true
                                },
                                label = { Text(pedido.status) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = corDoStatus(pedido.status).copy(alpha = 0.15f),
                                    labelColor = corDoStatus(pedido.status)
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
            PedidoMaterialBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                pedido = pedidoEmEdicao,
                obras = obras,
                isSaving = salvando,
                onDismissRequest = {
                    mostrarFormulario = false
                    pedidoEmEdicao = null
                    salvando = false
                },
                onConfirmClick = { pedido ->
                    salvando = true
                    onSalvar(pedido) { erro ->
                        salvando = false
                        if (erro == null) {
                            mostrarFormulario = false
                            pedidoEmEdicao = null
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(erro) }
                        }
                    }
                }
            )
        }

        pedidoParaExcluir?.let { pedido ->
            AlertDialog(
                onDismissRequest = { pedidoParaExcluir = null },
                title = { Text("Excluir pedido?") },
                text = { Text("O pedido de \"${pedido.item}\" será removido.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onExcluir(pedido) { erro ->
                                erro?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
                            }
                            pedidoParaExcluir = null
                        }
                    ) {
                        Text("Excluir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pedidoParaExcluir = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun corDoStatus(status: String): Color = when (status) {
    "Aberto" -> LaranjaObra
    "Em análise" -> Color(0xFF8E6C00)
    "Aprovado" -> MaterialTheme.colorScheme.primary
    "Atendido" -> Color(0xFF2E7D32)
    "Cancelado" -> Color.Gray
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(showBackground = true)
@Composable
fun MateriaisScreenPreview() {
    val obras = listOf(
        Obra(id = "1", titulo = "Residencial Jardins"),
        Obra(id = "2", titulo = "Galpão Industrial")
    )
    PlanoObraTheme {
        MateriaisContent(
            pedidos = listOf(
                PedidoMaterial("a", "1", "Cimento CP-II", 50.0, "sacos", "Urgente", "Aberto"),
                PedidoMaterial("b", "1", "Areia média", 8.0, "m³", "Normal", "Aprovado"),
                PedidoMaterial("c", "2", "Vergalhão 10mm", 120.0, "barras", "Normal", "Em análise"),
                PedidoMaterial("d", "2", "Tinta acrílica", 18.5, "L", "Baixa", "Atendido")
            ),
            obras = obras,
            erroCarregamento = null,
            onSalvar = { _, concluir -> concluir(null) },
            onExcluir = { _, concluir -> concluir(null) }
        )
    }
}
