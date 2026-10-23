package com.example.planoobra.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.example.planoobra.models.Participante
import com.example.planoobra.models.funcoesParticipante

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ParticipanteBottomSheet(
    participante: Participante?,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Participante) -> Unit,
    modifier: Modifier = Modifier
) {
    var nome by remember(participante?.id) { mutableStateOf(participante?.nome.orEmpty()) }
    var funcao by remember(participante?.id) { mutableStateOf(participante?.funcao.orEmpty()) }

    val podeSalvar = nome.isNotBlank() && funcao.isNotBlank()
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
                text = if (participante == null) "Novo participante" else "Editar participante",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nome") },
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )
            OutlinedTextField(
                value = funcao,
                onValueChange = { funcao = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Função") },
                placeholder = { Text("Ex.: Pedreiro") },
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                funcoesParticipante.forEach { opcao ->
                    FilterChip(
                        selected = funcao.equals(opcao, ignoreCase = true),
                        onClick = { funcao = opcao },
                        label = { Text(opcao) }
                    )
                }
            }

            Button(
                onClick = {
                    onConfirmClick(
                        participante?.copy(nome = nome.trim(), funcao = funcao.trim())
                            ?: Participante(nome.trim(), funcao.trim())
                    )
                },
                enabled = podeSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Salvar participante",
                    modifier = Modifier.padding(8.dp)
                )
            }
            val motivoBloqueio = when {
                nome.isBlank() -> "Informe o nome do participante."
                funcao.isBlank() -> "Informe a função."
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
