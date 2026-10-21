package com.example.planoobra.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ObraBottomSheet(
    tituloTela: String,
    titulo: String,
    onTituloChange: (String) -> Unit,
    descricao: String,
    onDescricaoChange: (String) -> Unit,
    localizacao: String,
    onLocalizacaoChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = tituloTela,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Nome da obra") },
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Descrição") },
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            OutlinedTextField(
                value = localizacao,
                onValueChange = onLocalizacaoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Localização") },
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            Text(
                text = "Status",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Em andamento", "Pausada", "Concluída").forEach { opcao ->
                    FilterChip(
                        selected = status == opcao,
                        onClick = { onStatusChange(opcao) },
                        label = { Text(opcao) }
                    )
                }
            }
            Button(
                onClick = onConfirmClick,
                enabled = titulo.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Confirmar",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}