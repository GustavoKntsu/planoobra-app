package com.example.planoobra.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.planoobra.models.Tarefa
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

private val statusTarefa = listOf("A fazer", "Em andamento", "Concluída")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditCarBottomSheet(
    tarefa: Tarefa?,
    obras: List<Obra>,
    currentUserId: String,
    currentUserLabel: String,
    isSaving: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Tarefa) -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by remember(tarefa?.id) { mutableStateOf(tarefa?.titulo.orEmpty()) }
    var descricao by remember(tarefa?.id) { mutableStateOf(tarefa?.descricao.orEmpty()) }
    var obraId by remember(tarefa?.id) {
        mutableStateOf(tarefa?.obraId ?: obras.firstOrNull()?.id.orEmpty())
    }
    var prazo by remember(tarefa?.id) { mutableStateOf(tarefa?.prazo.orEmpty()) }
    var status by remember(tarefa?.id) { mutableStateOf(tarefa?.status ?: statusTarefa.first()) }
    var obraMenuExpanded by remember { mutableStateOf(false) }

    val obraIdSelecionada = obraId.ifBlank {
        if (tarefa == null) obras.firstOrNull()?.id.orEmpty() else ""
    }
    val obraSelecionada = obras.firstOrNull { it.id == obraIdSelecionada }
    val prazoValido = prazo.isBlank() || isPrazoValido(prazo)
    val obraIdValido = obraIdSelecionada.isBlank() || obras.any { it.id == obraIdSelecionada }
    val podeSalvar = titulo.isNotBlank() &&
        obraIdValido &&
        currentUserId.isNotBlank() &&
        prazoValido &&
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
                text = if (tarefa == null) "Nova tarefa" else "Editar tarefa",
                style = MaterialTheme.typography.titleLarge
            )

            if (obras.isEmpty()) {
                Text(
                    text = "Obra não vinculada. Você pode cadastrar uma obra depois.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedButton(
                onClick = { obraMenuExpanded = true },
                enabled = obras.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = obraSelecionada?.titulo
                        ?: tarefa?.obraId?.takeIf { it.isNotBlank() }
                        ?: "Selecione a obra",
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
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título") },
                singleLine = true,
                isError = titulo.isBlank()
            )
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descrição") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            OutlinedTextField(
                value = prazo,
                onValueChange = {
                    prazo = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Prazo (DD/MM/AAAA)") },
                supportingText = {
                        if (!prazoValido) {
                        Text("Informe uma data válida no formato DD/MM/AAAA.")
                    } else {
                        Text("Opcional")
                    }
                },
                isError = !prazoValido,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Text(
                text = "Responsável: ${
                    tarefa?.responsavelId
                        ?.takeIf { it.isNotBlank() && it != currentUserId }
                        ?: currentUserLabel.ifBlank { currentUserId }
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text("Status", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statusTarefa.forEach { opcao ->
                    FilterChip(
                        selected = status == opcao,
                        onClick = { status = opcao },
                        label = { Text(opcao) }
                    )
                }
            }

            Button(
                onClick = {
                    val responsavelId = tarefa?.responsavelId
                        ?.takeIf { it.isNotBlank() }
                        ?: currentUserId
                    onConfirmClick(
                        Tarefa(
                            id = tarefa?.id.orEmpty(),
                            obraId = obraIdSelecionada,
                            titulo = titulo.trim(),
                            descricao = descricao.trim(),
                            responsavelId = responsavelId,
                            prazo = prazo.trim(),
                            status = status,
                            atualizadoEm = System.currentTimeMillis(),
                            atualizadoPor = currentUserId
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
                    text = if (isSaving) "Salvando..." else "Salvar tarefa",
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (!isSaving) {
                val motivoBloqueio = when {
                    titulo.isBlank() -> "Informe o título da tarefa."
                    currentUserId.isBlank() -> "Entre na sua conta para salvar a tarefa."
                    !prazoValido -> "Corrija o prazo ou deixe o campo vazio."
                    !obraIdValido -> "Selecione uma obra válida."
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

internal fun isPrazoValido(value: String): Boolean {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).apply {
        isLenient = false
    }
    return try {
        val data = formato.parse(value) ?: return false
        formato.format(data) == value
    } catch (_: ParseException) {
        false
    }
}
