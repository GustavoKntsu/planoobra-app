package com.example.planoobra.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onEmailClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onSingUpClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PlanoObra",
            fontSize = 45.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Acesse sua conta",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = onEmailClick,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continuar com E-mail", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onGoogleClick, // <-- CORRIGIDO AQUI
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)), // Vermelho do Google
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continuar com Google", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Não tem uma conta? ", color = Color.Gray)
            TextButton(onClick = onSingUpClick, contentPadding = PaddingValues(0.dp)) {
                Text(text = "Cadastre-se", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}