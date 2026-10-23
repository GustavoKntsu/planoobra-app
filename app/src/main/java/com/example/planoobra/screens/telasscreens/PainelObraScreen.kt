package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planoobra.auth.AuthViewModel
import com.example.planoobra.auth.DemoUser
import com.example.planoobra.components.EditCarBottomSheet
import com.example.planoobra.components.PedidoMaterialBottomSheet
import com.example.planoobra.components.formatarQuantidade
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.firebase.OcorrenciaRepository
import com.example.planoobra.firebase.PedidoMaterialRepository
import com.example.planoobra.firebase.TarefaRepository
import com.example.planoobra.models.Obra
import com.example.planoobra.models.Ocorrencia
import com.example.planoobra.models.PedidoMaterial
import com.example.planoobra.models.Tarefa
import com.example.planoobra.ui.theme.AzulObra
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import kotlinx.coroutines.launch

@Composable
fun PainelObraScreen(modifier: Modifier = Modifier) {
    val usuarioAtual = AuthViewModel.currentUser ?: DemoUser()

    PainelObraContent(
        obras = FirebaseRepository.obras,
        tarefas = TarefaRepository.tarefas,
        pedidos = PedidoMaterialRepository.pedidos,
        ocorrencias = OcorrenciaRepository.ocorrencias,
        currentUserId = usuarioAtual.uid,
        currentUserLabel = usuarioAtual.displayName,
        onSalvarTarefa = TarefaRepository::salvar,
        onSalvarPedido = PedidoMaterialRepository::salvar,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PainelObraContent(
    obras: List<Obra>,
    tarefas: List<Tarefa>,
    pedidos: List<PedidoMaterial>,
    ocorrencias: List<Ocorrencia>,
    currentUserId: String,
    currentUserLabel: String,
    onSalvarTarefa: (Tarefa) -> Unit,
    onSalvarPedido: (PedidoMaterial, (String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var obraSelecionadaId by remember { mutableStateOf("TODAS") }
    var filtroCategoria by remember { mutableStateOf("Todas") }

    var mostrarNovoPedido by remember { mutableStateOf(false) }
    var pedidoEmEdicao by remember { mutableStateOf<PedidoMaterial?>(null) }
    var salvandoPedido by remember { mutableStateOf(false) }

    var mostrarFormularioTarefa by remember { mutableStateOf(false) }
    var tarefaEmEdicao by remember { mutableStateOf<Tarefa?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val obrasPorId = obras.associateBy { it.id }
    val obraAtual = obrasPorId[obraSelecionadaId]

    val tarefasDaObra = tarefas.filter {
        obraSelecionadaId == "TODAS" || it.obraId == obraSelecionadaId
    }
    val pedidosDaObra = pedidos.filter {
        obraSelecionadaId == "TODAS" || it.obraId == obraSelecionadaId
    }
    val ocorrenciasDaObra = ocorrencias.filter {
        obraSelecionadaId == "TODAS" || it.obraId == obraSelecionadaId
    }

    val tarefasPendentes = tarefasDaObra.filter { it.status != "Concluída" }
    val tarefasConcluidas = tarefasDaObra.count { it.status == "Concluída" }
    val progressoTarefas = if (tarefasDaObra.isNotEmpty()) {
        tarefasConcluidas.toFloat() / tarefasDaObra.size.toFloat()
    } else {
        0f
    }

    val pedidosPendentes = pedidosDaObra.filter {
        it.status == "Aberto" || it.status == "Em análise"
    }
    val pedidosUrgentes = pedidosPendentes.filter { it.urgencia == "Urgente" }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Seletor de Obra
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Filtrar por obra",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = obraSelecionadaId == "TODAS",
                            onClick = { obraSelecionadaId = "TODAS" },
                            label = { Text("Todas (${obras.size})") }
                        )
                        obras.forEach { obra ->
                            FilterChip(
                                selected = obraSelecionadaId == obra.id,
                                onClick = { obraSelecionadaId = obra.id },
                                label = { Text(obra.titulo) }
                            )
                        }
                    }
                }
            }

            // 2. Detalhes da Obra selecionada (quando filtrada)
            if (obraAtual != null) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AzulObra),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = obraAtual.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(obraAtual.status) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = LaranjaObra,
                                        labelColor = Color.White
                                    )
                                )
                            }
                            if (obraAtual.localizacao.isNotBlank()) {
                                Text(
                                    text = "📍 ${obraAtual.localizacao}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            if (obraAtual.descricao.isNotBlank()) {
                                Text(
                                    text = obraAtual.descricao,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Card de Progresso Geral das Tarefas
            item {
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Andamento das Tarefas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${(progressoTarefas * 100).toInt()}% concluído",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = LaranjaObra
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progressoTarefas },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = LaranjaObra,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Text(
                            text = "$tarefasConcluidas de ${tarefasDaObra.size} tarefas concluídas • ${tarefasPendentes.size} pendentes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. Indicadores de Pendências (4 Cards Resumo)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResumoPendenciaCard(
                            titulo = "Tarefas Pendentes",
                            valor = tarefasPendentes.size.toString(),
                            subtitulo = "${tarefasPendentes.count { it.status == "A fazer" }} a fazer",
                            icone = Icons.Default.AssignmentLate,
                            corDestaque = AzulObra,
                            onClick = { filtroCategoria = "Tarefas" },
                            modifier = Modifier.weight(1f)
                        )
                        ResumoPendenciaCard(
                            titulo = "Pedidos Pendentes",
                            valor = pedidosPendentes.size.toString(),
                            subtitulo = "Abertos / Em análise",
                            icone = Icons.Default.Inventory2,
                            corDestaque = LaranjaObra,
                            onClick = { filtroCategoria = "Materiais" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResumoPendenciaCard(
                            titulo = "Materiais Urgentes",
                            valor = pedidosUrgentes.size.toString(),
                            subtitulo = "Prioridade máxima",
                            icone = Icons.Default.PriorityHigh,
                            corDestaque = Color(0xFFD32F2F),
                            onClick = { filtroCategoria = "Urgentes" },
                            modifier = Modifier.weight(1f)
                        )
                        ResumoPendenciaCard(
                            titulo = "Ocorrências",
                            valor = ocorrenciasDaObra.size.toString(),
                            subtitulo = "${ocorrenciasDaObra.count { it.tipo == "Falta" }} faltas registradas",
                            icone = Icons.Default.ReportProblem,
                            corDestaque = Color(0xFF8E6C00),
                            onClick = { filtroCategoria = "Ocorrências" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Botões de Ação Rápida (Novo Pedido de Material e Nova Tarefa)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            pedidoEmEdicao = if (obraAtual != null) {
                                PedidoMaterial(obraId = obraAtual.id)
                            } else {
                                null
                            }
                            salvandoPedido = false
                            mostrarNovoPedido = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaranjaObra),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Novo Pedido")
                    }

                    OutlinedButton(
                        onClick = {
                            tarefaEmEdicao = null
                            mostrarFormularioTarefa = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Nova Tarefa")
                    }
                }
            }

            // 6. Filtro de Categoria de Pendências
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Pendências da Obra",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Todas", "Urgentes", "Materiais", "Tarefas", "Ocorrências").forEach { cat ->
                            FilterChip(
                                selected = filtroCategoria == cat,
                                onClick = { filtroCategoria = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            }

            // 7. Seção: Pedidos de Material Pendentes / Urgentes
            val exibirMateriais = filtroCategoria in listOf("Todas", "Urgentes", "Materiais")
            val listaPedidosExibidos = if (filtroCategoria == "Urgentes") {
                pedidosUrgentes
            } else {
                pedidosPendentes
            }

            if (exibirMateriais && listaPedidosExibidos.isNotEmpty()) {
                item {
                    SecaoHeader(
                        titulo = if (filtroCategoria == "Urgentes") {
                            "Pedidos de Material Urgentes (${listaPedidosExibidos.size})"
                        } else {
                            "Pedidos de Material Pendentes (${listaPedidosExibidos.size})"
                        }
                    )
                }
                items(items = listaPedidosExibidos, key = { "ped_${it.id}" }) { pedido ->
                    val obra = obrasPorId[pedido.obraId]
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clickable {
                                pedidoEmEdicao = pedido
                                salvandoPedido = false
                                mostrarNovoPedido = true
                            },
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pedido.item,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (pedido.urgencia == "Urgente") {
                                    SuggestionChip(
                                        onClick = {
                                            pedidoEmEdicao = pedido
                                            mostrarNovoPedido = true
                                        },
                                        label = { Text("URGENTE") },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = Color.Red.copy(alpha = 0.15f),
                                            labelColor = Color.Red
                                        )
                                    )
                                }
                            }
                            Text(
                                text = obra?.titulo ?: "Sem obra vinculada",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Quantidade: ${formatarQuantidade(pedido.quantidade)} ${pedido.unidade} • Status: ${pedido.status}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Toque para atualizar status ou editar pedido",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 8. Seção: Tarefas Pendentes
            val exibirTarefas = filtroCategoria in listOf("Todas", "Tarefas")
            if (exibirTarefas && tarefasPendentes.isNotEmpty()) {
                item {
                    SecaoHeader(titulo = "Tarefas Pendentes (${tarefasPendentes.size})")
                }
                items(items = tarefasPendentes, key = { "tar_${it.id}" }) { tarefa ->
                    val obra = obrasPorId[tarefa.obraId]
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clickable {
                                tarefaEmEdicao = tarefa
                                mostrarFormularioTarefa = true
                            },
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tarefa.titulo,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                SuggestionChip(
                                    onClick = {
                                        tarefaEmEdicao = tarefa
                                        mostrarFormularioTarefa = true
                                    },
                                    label = { Text(tarefa.status) }
                                )
                            }
                            Text(
                                text = obra?.titulo ?: "Sem obra vinculada",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            if (tarefa.descricao.isNotBlank()) {
                                Text(
                                    text = tarefa.descricao,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Resp.: ${tarefa.responsavelId.ifBlank { currentUserLabel }}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (tarefa.prazo.isNotBlank()) {
                                    Text(
                                        text = "Prazo: ${tarefa.prazo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LaranjaObra
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 9. Seção: Ocorrências e Ausências Recentes
            val exibirOcorrencias = filtroCategoria in listOf("Todas", "Ocorrências")
            if (exibirOcorrencias && ocorrenciasDaObra.isNotEmpty()) {
                item {
                    SecaoHeader(titulo = "Ocorrências e Alertas da Equipe (${ocorrenciasDaObra.size})")
                }
                items(items = ocorrenciasDaObra, key = { "oc_${it.id}" }) { ocorrencia ->
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ocorrencia.trabalhadorNome,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(ocorrencia.tipo) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = LaranjaObra.copy(alpha = 0.15f),
                                        labelColor = LaranjaObra
                                    )
                                )
                            }
                            Text(
                                text = "${obra?.titulo ?: "Obra"} • Data: ${ocorrencia.data}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            if (ocorrencia.observacao.isNotBlank()) {
                                Text(
                                    text = ocorrencia.observacao,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Caso nenhuma pendência seja encontrada para o filtro selecionado
            val nadaParaMostrar =
                (!exibirMateriais || listaPedidosExibidos.isEmpty()) &&
                    (!exibirTarefas || tarefasPendentes.isEmpty()) &&
                    (!exibirOcorrencias || ocorrenciasDaObra.isEmpty())

            if (nadaParaMostrar) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Nenhuma pendência encontrada neste filtro!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        // Formulário de Novo / Editar Pedido de Material (Molde: EditCarBottomSheet)
        if (mostrarNovoPedido) {
            val pedidoParaSheet = pedidoEmEdicao?.takeIf { it.id.isNotBlank() }
            PedidoMaterialBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                pedido = pedidoParaSheet,
                obras = obras,
                isSaving = salvandoPedido,
                onDismissRequest = {
                    mostrarNovoPedido = false
                    pedidoEmEdicao = null
                    salvandoPedido = false
                },
                onConfirmClick = { pedido ->
                    salvandoPedido = true
                    onSalvarPedido(pedido) { erro ->
                        salvandoPedido = false
                        if (erro == null) {
                            mostrarNovoPedido = false
                            pedidoEmEdicao = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Pedido de material salvo com sucesso!")
                            }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(erro) }
                        }
                    }
                }
            )
        }

        // Formulário de Nova / Editar Tarefa
        if (mostrarFormularioTarefa) {
            EditCarBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                tarefa = tarefaEmEdicao,
                obras = obras,
                currentUserId = currentUserId,
                currentUserLabel = currentUserLabel,
                isSaving = false,
                onDismissRequest = {
                    mostrarFormularioTarefa = false
                    tarefaEmEdicao = null
                },
                onConfirmClick = { tarefa ->
                    onSalvarTarefa(tarefa)
                    mostrarFormularioTarefa = false
                    tarefaEmEdicao = null
                    scope.launch {
                        snackbarHostState.showSnackbar("Tarefa atualizada no painel!")
                    }
                }
            )
        }
    }
}

@Composable
private fun ResumoPendenciaCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    icone: ImageVector,
    corDestaque: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = corDestaque
                )
                Icon(
                    imageVector = icone,
                    contentDescription = titulo,
                    tint = corDestaque
                )
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SecaoHeader(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PainelObraScreenPreview() {
    PlanoObraTheme {
        PainelObraContent(
            obras = FirebaseRepository.obras,
            tarefas = TarefaRepository.tarefas,
            pedidos = PedidoMaterialRepository.pedidos,
            ocorrencias = OcorrenciaRepository.ocorrencias,
            currentUserId = "demo_user_01",
            currentUserLabel = "Eng. Carlos Mendes",
            onSalvarTarefa = {},
            onSalvarPedido = { _, concluir -> concluir(null) }
        )
    }
}
