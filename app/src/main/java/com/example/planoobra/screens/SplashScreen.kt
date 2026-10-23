package com.example.planoobra.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planoobra.R
import com.example.planoobra.ui.theme.AzulObra
import com.example.planoobra.ui.theme.LaranjaObra
import com.example.planoobra.ui.theme.PlanoObraTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SplashScreen(
    onLogin: () -> Unit = {},
    onSignUp: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoplanoobra),
                contentDescription = "Imagem de entrada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "PlanoObra",
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold, // Fonte normal, sem cursiva
                color = AzulObra,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Gestão de Obras",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onLogin,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(2.dp, AzulObra),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulObra),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onSignUp,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = LaranjaObra),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text(text = "Cadastrar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PlanoObraTheme {
        SplashScreen()
    }
}
