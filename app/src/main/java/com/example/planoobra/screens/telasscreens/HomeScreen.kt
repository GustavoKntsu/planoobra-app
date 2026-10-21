package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.planoobra.components.ObraBottomSheet
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.models.Obra

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var showSheet by remember { mutableStateOf(false) }
    var obraToEdit by remember { mutableStateOf<Obra?>(null) }

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Em andamento") }

    val obras = FirebaseRepository.obras

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    obraToEdit = null
                    titulo = ""; descricao = ""; localizacao = ""; status = "Em andamento"
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.secondary, // Laranja
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "Adicionar Obra")
            }
        }
    ) { padding ->
        if (obras.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma obra cadastrada.\nToque no + para começar.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            items(items = obras, key = { it.id }) { obra ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                                Text(text = "📍 ${obra.localizacao}", style = MaterialTheme.typography.bodyMedium)
                            }

                            SuggestionChip(
                                onClick = {},
                                label = { Text(obra.status) },
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Row {
                            IconButton(onClick = {
                                obraToEdit = obra
                                titulo = obra.titulo; descricao = obra.descricao
                                localizacao = obra.localizacao; status = obra.status
                                showSheet = true
                            }) {
                                Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { FirebaseRepository.deleteObra(obra) }) {
                                Icon(Icons.Default.Delete, "Excluir", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ObraBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                tituloTela = if (obraToEdit == null) "Nova Obra" else "Editar Obra",
                titulo = titulo, onTituloChange = { titulo = it },
                descricao = descricao, onDescricaoChange = { descricao = it },
                localizacao = localizacao, onLocalizacaoChange = { localizacao = it },
                status = status, onStatusChange = { status = it },
                onDismissRequest = { showSheet = false; obraToEdit = null },
                onConfirmClick = {
                    val obraAtual = obraToEdit
                    if (obraAtual == null) {
                        FirebaseRepository.createObra(Obra(titulo = titulo, descricao = descricao, localizacao = localizacao, status = status))
                    } else {
                        FirebaseRepository.editObra(obraAtual.copy(titulo = titulo, descricao = descricao, localizacao = localizacao, status = status))
                    }
                    showSheet = false; obraToEdit = null
                }
            )
        }
    }
}