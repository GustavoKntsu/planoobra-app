package com.example.planoobra.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.example.planoobra.models.Obra
import com.example.planoobra.models.Ocorrencia
import com.example.planoobra.models.tiposOcorrencia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OcorrenciaBottomSheet(
    ocorrencia: Ocorrencia?,
    obras: List<Obra>,
    isSaving: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Ocorrencia) -> Unit,
    modifier: Modifier = Modifier
) {
    var obraId by remember(ocorrencia?.id) {
        mutableStateOf(ocorrencia?.obraId ?: obras.firstOrNull()?.id.orEmpty())
    }
    var trabalhador by remember(ocorrencia?.id) {
        mutableStateOf(ocorrencia?.trabalhadorNome.orEmpty())
    }
    var data by remember(ocorrencia?.id) { mutableStateOf(ocorrencia?.data ?: dataDeHoje()) }
    var tipo by remember(ocorrencia?.id) {
        mutableStateOf(ocorrencia?.tipo ?: tiposOcorrencia.first())
    }
    var observacao by remember(ocorrencia?.id) {
        mutableStateOf(ocorrencia?.observacao.orEmpty())
    }
    var obraMenuExpanded by remember { mutableStateOf(false) }

    val obraSelecionada = obras.firstOrNull { it.id == obraId }
    val dataValida = isPrazoValido(data)
    // Para "Outro" a observação é o que explica o que aconteceu
    val observacaoObrigatoria = tipo == "Outro"
    val podeSalvar = obraSelecionada != null &&
        trabalhador.isNotBlank() &&
        dataValida &&
        (!observacaoObrigatoria || observacao.isNotBlank()) &&
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
                text = if (ocorrencia == null) "Registrar ocorrência" else "Editar ocorrência",
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
                value = trabalhador,
                onValueChange = { trabalhador = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nome do trabalhador") },
                singleLine = true
            )
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Data (DD/MM/AAAA)") },
                supportingText = {
                    if (!dataValida) Text("Informe uma data válida no formato DD/MM/AAAA.")
                },
                isError = !dataValida,
                singleLine = true
            )

            Text("Tipo", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tiposOcorrencia.forEach { opcao ->
                    FilterChip(
                        selected = tipo == opcao,
                        onClick = { tipo = opcao },
                        label = { Text(opcao) }
                    )
                }
            }

            OutlinedTextField(
                value = observacao,
                onValueChange = { observacao = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                label = { Text(if (observacaoObrigatoria) "Observação" else "Observação (opcional)") },
                placeholder = { Text("Ex.: avisou por telefone, atestado médico...") }
            )

            Button(
                onClick = {
                    onConfirmClick(
                        Ocorrencia(
                            id = ocorrencia?.id.orEmpty(),
                            obraId = obraId,
                            trabalhadorNome = trabalhador.trim(),
                            data = data.trim(),
                            tipo = tipo,
                            observacao = observacao.trim()
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
                    text = if (isSaving) "Salvando..." else "Registrar",
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (!isSaving) {
                val motivoBloqueio = when {
                    obras.isEmpty() -> "Cadastre uma obra antes de registrar ocorrências."
                    obraSelecionada == null -> "Selecione a obra."
                    trabalhador.isBlank() -> "Informe o nome do trabalhador."
                    !dataValida -> "Corrija a data."
                    observacaoObrigatoria && observacao.isBlank() -> "Descreva a ocorrência na observação."
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

private fun dataDeHoje(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date())
