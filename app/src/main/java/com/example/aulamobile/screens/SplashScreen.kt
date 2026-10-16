package com.example.aulatelas.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulamobile.R

@Composable
fun SplashScreen(
    onLogin: () -> Unit = {},
    onSignUp: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.undraw),
                contentDescription = "Imagem de entrada para App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Bem-Vindo!!!",
                fontSize = 55.sp,
                fontFamily = FontFamily.Cursive,
                fontStyle = FontStyle.Italic,
                color = colorResource(R.color.purple_500),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)

            )

            Spacer(Modifier.height(12.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 31.dp, vertical = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onLogin,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(2.dp, colorResource(R.color.purple_500)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(R.color.purple_500),
                    ),
                    modifier = Modifier.weight(1f).height(48.dp)

                ) {
                    Text(
                        text = "Login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(32.dp))

                Button(
                    onClick = onSignUp,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.purple_500),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(
                        text = "Cadastrar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}


