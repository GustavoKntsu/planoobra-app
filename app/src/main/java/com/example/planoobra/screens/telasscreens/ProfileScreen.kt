package com.example.planoobra.screens.telasscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planoobra.R
import com.example.planoobra.auth.AuthViewModel
import com.example.planoobra.auth.DemoUser
import com.example.planoobra.firebase.FirebaseRepository
import com.example.planoobra.firebase.OcorrenciaRepository
import com.example.planoobra.firebase.PedidoMaterialRepository
import com.example.planoobra.firebase.TarefaRepository
import com.example.planoobra.models.Obra
import com.example.planoobra.ui.theme.AzulObra
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usuario = AuthViewModel.currentUser ?: DemoUser()
    val obras = FirebaseRepository.obras
    val tarefasConcluidas = TarefaRepository.tarefas.count { it.status == "Concluída" }
    val totalTarefas = TarefaRepository.tarefas.size
    val totalPedidos = PedidoMaterialRepository.pedidos.size
    val totalOcorrencias = OcorrenciaRepository.ocorrencias.size

    ProfileContent(
        usuario = usuario,
        obras = obras,
        tarefasConcluidas = tarefasConcluidas,
        totalTarefas = totalTarefas,
        totalPedidos = totalPedidos,
        totalOcorrencias = totalOcorrencias,
        onSalvarPerfil = { novoUsuario ->
            AuthViewModel.updateCurrentUser(novoUsuario)
        },
        onLogout = onLogout,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    usuario: DemoUser,
    obras: List<Obra>,
    tarefasConcluidas: Int,
    totalTarefas: Int,
    totalPedidos: Int,
    totalOcorrencias: Int,
    onSalvarPerfil: (DemoUser) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarEditarPerfil by remember { mutableStateOf(false) }
    var confirmarLogout by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Cabeçalho do Perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AzulObra,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 6.dp,
                        color = Color.White,
                        modifier = Modifier.size(92.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_2),
                            contentDescription = "Foto do perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = usuario.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = usuario.cargo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    SuggestionChip(
                        onClick = { mostrarEditarPerfil = true },
                        label = { Text(usuario.crea) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = LaranjaObra,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // 2. Resumo de Atuação no Canteiro (Estatísticas)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstatisticaPerfilCard(
                    valor = obras.size.toString(),
                    legenda = "Obras",
                    cor = AzulObra,
                    modifier = Modifier.weight(1f)
                )
                EstatisticaPerfilCard(
                    valor = "$tarefasConcluidas/$totalTarefas",
                    legenda = "Tarefas OK",
                    cor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                EstatisticaPerfilCard(
                    valor = totalPedidos.toString(),
                    legenda = "Pedidos",
                    cor = LaranjaObra,
                    modifier = Modifier.weight(1f)
                )
                EstatisticaPerfilCard(
                    valor = totalOcorrencias.toString(),
                    legenda = "Registros",
                    cor = Color(0xFF8E6C00),
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Dados Profissionais e de Contato
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dados Profissionais",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = { mostrarEditarPerfil = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar perfil",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Editar")
                        }
                    }

                    InfoPerfilRow(
                        icone = Icons.Default.Email,
                        rotulo = "E-mail corporativo",
                        valor = usuario.email
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    InfoPerfilRow(
                        icone = Icons.Default.Phone,
                        rotulo = "Telefone / Plantão",
                        valor = usuario.telefone
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    InfoPerfilRow(
                        icone = Icons.Default.Badge,
                        rotulo = "Registro Profissional",
                        valor = usuario.crea
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    InfoPerfilRow(
                        icone = Icons.Default.Engineering,
                        rotulo = "Função no Canteiro",
                        valor = usuario.cargo
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    InfoPerfilRow(
                        icone = Icons.Default.Apartment,
                        rotulo = "Construtora / Empresa",
                        valor = usuario.empresa
                    )
                }
            }

            // 4. Obras Vinculadas ao Responsável
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Obras sob Responsabilidade (${obras.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (obras.isEmpty()) {
                        Text(
                            text = "Nenhuma obra vinculada no momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        obras.forEachIndexed { index, obra ->
                            if (index > 0) {
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = obra.titulo,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (obra.localizacao.isNotBlank()) {
                                        Text(
                                            text = "📍 ${obra.localizacao}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(obra.status) }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Botões de Ação da Conta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { mostrarEditarPerfil = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar Dados do Perfil", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { confirmarLogout = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sair da Conta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // BottomSheet para Editar Perfil
        if (mostrarEditarPerfil) {
            EditarPerfilBottomSheet(
                usuario = usuario,
                onDismissRequest = { mostrarEditarPerfil = false },
                onConfirmClick = { atualizado ->
                    onSalvarPerfil(atualizado)
                    mostrarEditarPerfil = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Perfil atualizado com sucesso!")
                    }
                }
            )
        }

        // Confirmação de Logout
        if (confirmarLogout) {
            AlertDialog(
                onDismissRequest = { confirmarLogout = false },
                title = { Text("Sair da conta?") },
                text = { Text("Você voltará para a tela inicial de login do PlanoObra.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            confirmarLogout = false
                            onLogout()
                        }
                    ) {
                        Text("Sair", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmarLogout = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun EstatisticaPerfilCard(
    valor: String,
    legenda: String,
    cor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = cor
            )
            Text(
                text = legenda,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoPerfilRow(
    icone: ImageVector,
    rotulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = AzulObra.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icone,
                    contentDescription = rotulo,
                    tint = AzulObra,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rotulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditarPerfilBottomSheet(
    usuario: DemoUser,
    onDismissRequest: () -> Unit,
    onConfirmClick: (DemoUser) -> Unit
) {
    var nome by remember { mutableStateOf(usuario.displayName) }
    var cargo by remember { mutableStateOf(usuario.cargo) }
    var crea by remember { mutableStateOf(usuario.crea) }
    var telefone by remember { mutableStateOf(usuario.telefone) }
    var empresa by remember { mutableStateOf(usuario.empresa) }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Editar Perfil Profissional",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cargo,
                onValueChange = { cargo = it },
                label = { Text("Cargo / Função") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = crea,
                onValueChange = { crea = it },
                label = { Text("Registro Profissional (CREA / CAU)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefone,
                onValueChange = { telefone = it },
                label = { Text("Telefone / WhatsApp") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = empresa,
                onValueChange = { empresa = it },
                label = { Text("Construtora / Empresa") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onConfirmClick(
                        usuario.copy(
                            displayName = nome.trim(),
                            cargo = cargo.trim(),
                            crea = crea.trim(),
                            telefone = telefone.trim(),
                            empresa = empresa.trim()
                        )
                    )
                },
                enabled = nome.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LaranjaObra),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Salvar alterações",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    PlanoObraTheme {
        ProfileContent(
            usuario = DemoUser(),
            obras = FirebaseRepository.obras,
            tarefasConcluidas = 1,
            totalTarefas = 5,
            totalPedidos = 5,
            totalOcorrencias = 4,
            onSalvarPerfil = {},
            onLogout = {}
        )
    }
}
