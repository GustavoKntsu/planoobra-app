package com.example.planoobra.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planoobra.ui.theme.PlanoObraTheme
import androidx.compose.ui.tooling.preview.Preview

enum class AuthMode { SignIn, SignUp }

@Composable
fun SignInScreen(
    mode: AuthMode = AuthMode.SignIn,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onPrimary: (String, String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSwitch: () -> Unit = {}
) {
    val isSignUp = mode == AuthMode.SignUp

    var email by remember(mode) { mutableStateOf(if (isSignUp) "" else "admin@planoobra.com") }
    var password by remember(mode) { mutableStateOf(if (isSignUp) "" else "123456") }
    var showPassword by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var localError by remember { mutableStateOf<String?>(null) }

    val titleText = if (isSignUp) "Criar Conta" else "Entrar"
    val primaryText = if (isSignUp) "Cadastrar" else "Entrar"
    val bottomPrompt = if (isSignUp) "Já possui uma conta? " else "Novo por aqui? "
    val bottomLink = if (isSignUp) "Entrar" else "Cadastre-se"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = titleText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isSignUp) {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Modo Demonstração: Login fixo preenchido (admin@planoobra.com / 123456)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            label = { Text("Senha") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(text = if (showPassword) "Ocultar" else "Mostrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(Modifier.height(16.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                singleLine = true,
                label = { Text("Confirmar Senha") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Text(text = if (showConfirmPassword) "Ocultar" else "Mostrar", color = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(Modifier.height(16.dp))
        }

        // MENSAGEM DE ERRO
        if (errorMessage != null || localError != null) {
            Text(
                text = localError ?: errorMessage ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                localError = null
                if (isSignUp && password != confirmPassword) {
                    localError = "As senhas não coincidem."
                } else if (password.length < 6) {
                    localError = "A senha deve ter pelo menos 6 caracteres."
                } else {
                    onPrimary(email, password)
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(primaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = bottomPrompt, color = Color.Gray)
            TextButton(onClick = onSwitch, contentPadding = PaddingValues(0.dp), enabled = !isLoading) {
                Text(text = bottomLink, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    PlanoObraTheme {
        SignInScreen(mode = AuthMode.SignIn)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    PlanoObraTheme {
        SignInScreen(mode = AuthMode.SignUp)
    }
}
