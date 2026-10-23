package com.example.planoobra.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.planoobra.models.Obra
import com.example.planoobra.models.PedidoMaterial
import com.example.planoobra.models.statusPedidoMaterial
import com.example.planoobra.models.urgenciasPedidoMaterial

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PedidoMaterialBottomSheet(
    pedido: PedidoMaterial?,
    obras: List<Obra>,
    isSaving: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: (PedidoMaterial) -> Unit,
    modifier: Modifier = Modifier
) {
    var obraId by remember(pedido?.id) {
        mutableStateOf(pedido?.obraId ?: obras.firstOrNull()?.id.orEmpty())
    }
    var item by remember(pedido?.id) { mutableStateOf(pedido?.item.orEmpty()) }
    var quantidade by remember(pedido?.id) {
        mutableStateOf(pedido?.quantidade?.let(::formatarQuantidade).orEmpty())
    }
    var unidade by remember(pedido?.id) { mutableStateOf(pedido?.unidade.orEmpty()) }
    var urgencia by remember(pedido?.id) { mutableStateOf(pedido?.urgencia ?: "Normal") }
    var status by remember(pedido?.id) {
        mutableStateOf(pedido?.status ?: statusPedidoMaterial.first())
    }
    var obraMenuExpanded by remember { mutableStateOf(false) }

    val obraSelecionada = obras.firstOrNull { it.id == obraId }
    val quantidadeNumero = quantidade.replace(',', '.').toDoubleOrNull()
    val quantidadeValida = quantidadeNumero != null && quantidadeNumero > 0
    val podeSalvar = obraSelecionada != null &&
        item.isNotBlank() &&
        quantidadeValida &&
        unidade.isNotBlank() &&
        !isSaving
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (pedido == null) "Novo pedido de material" else "Editar pedido",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedButton(
                onClick = { obraMenuExpanded = true },
                enabled = obras.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = obraSelecionada?.titulo ?: "Selecione a obra",
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Selecionar obra")
            }
            DropdownMenu(
                expanded = obraMenuExpanded,
                onDismissRequest = { obraMenuExpanded = false }
            ) {
                obras.forEach { obra ->
                    DropdownMenuItem(
                        text = { Text(obra.titulo) },
                        onClick = {
                            obraId = obra.id
                            obraMenuExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = item,
                onValueChange = { item = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Material (ex.: Cimento CP-II)") },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Quantidade") },
                    isError = quantidade.isNotBlank() && !quantidadeValida,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = unidade,
                    onValueChange = { unidade = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Unidade") },
                    placeholder = { Text("sacos, m³, kg") },
                    singleLine = true
                )
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("sacos", "m³", "m²", "kg", "barras", "un").forEach { unSugerida ->
                    FilterChip(
                        selected = unidade.equals(unSugerida, ignoreCase = true),
                        onClick = { unidade = unSugerida },
                        label = { Text(unSugerida) }
                    )
                }
            }

            Text("Urgência", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                urgenciasPedidoMaterial.forEach { opcao ->
                    FilterChip(
                        selected = urgencia == opcao,
                        onClick = { urgencia = opcao },
                        label = { Text(opcao) }
                    )
                }
            }

            // Pedido novo sempre nasce "Aberto"; o status só muda depois, na edição
            if (pedido != null) {
                Text("Status", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusPedidoMaterial.forEach { opcao ->
                        FilterChip(
                            selected = status == opcao,
                            onClick = { status = opcao },
                            label = { Text(opcao) }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onConfirmClick(
                        PedidoMaterial(
                            id = pedido?.id.orEmpty(),
                            obraId = obraId,
                            item = item.trim(),
                            quantidade = quantidadeNumero ?: 0.0,
                            unidade = unidade.trim(),
                            urgencia = urgencia,
                            status = status
                        )
                    )
                },
                enabled = podeSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = if (isSaving) "Salvando..." else "Salvar pedido",
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (!isSaving) {
                val motivoBloqueio = when {
                    obras.isEmpty() -> "Cadastre uma obra antes de pedir materiais."
                    obraSelecionada == null -> "Selecione a obra."
                    item.isBlank() -> "Informe o material."
                    !quantidadeValida -> "Informe uma quantidade maior que zero."
                    unidade.isBlank() -> "Informe a unidade."
                    else -> null
                }
                if (motivoBloqueio != null) {
                    Text(
                        text = motivoBloqueio,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

// 10.0 vira "10" e 2.5 vira "2,5"
fun formatarQuantidade(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toLong().toString()
    else valor.toString().replace('.', ',')
