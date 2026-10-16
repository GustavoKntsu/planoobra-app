@file:Suppress("DEPRECATION")

package com.example.aulatelas.screens


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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulamobile.R

enum class AuthMode { SignIn, SignUp }

@Composable
fun SignInScreen(
    mode: AuthMode = AuthMode.SignIn,
    onPrimary: (String, String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSwitch: () -> Unit = {}

) {
    val isSignUp = mode == AuthMode.SignUp

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }

    var showConfirmPassword by remember { mutableStateOf(false) }

    val titleText = if (isSignUp) "Criar Conta" else "Entrar"
    val primaryText = if (isSignUp) "Criar Conta" else "Entrar"
    val bottomPrompt = if (isSignUp) "Já possuo uma conta." else "Sou um novo usuário."
    val bottomLink = if (isSignUp) "Entrar" else "Criar Conta"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.standing),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = titleText,
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.purple_500),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 36.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("E-mail") },
            leadingIcon = { Icon(painterResource(id = R.drawable.mail), null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(

                focusedTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            label = { Text("Senha") },
            leadingIcon = { Icon(painterResource(id = R.drawable.clarity_lock_solid), null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(text = if (showPassword) "Ocultar" else "Mostrar")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(Modifier.height(8.dp))

        if (isSignUp) {
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                singleLine = true,
                label = { Text("Confirmar Senha") },
                leadingIcon = { Icon(painterResource(id = R.drawable.clarity_lock_solid), null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Text(text = if (showConfirmPassword) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Transparent,
                    unfocusedTextColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        if (!isSignUp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 8.dp),
            ) {
                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )

                ) {
                    Text("Esqueci a senha")
                }

            }
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { onPrimary(email, password) },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple_500),
                contentColor = colorResource(R.color.white),
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)

        ) {
            Text(primaryText, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))

        val annotated = buildAnnotatedString {
            append(bottomPrompt)
            pushStringAnnotation("switch", "switch")
            withStyle(SpanStyle(color = colorResource(R.color.purple_500))) {
                append(bottomLink)
            }
            pop()
        }
        ClickableText(
            text = annotated,
            onClick = { offset ->
                annotated.getStringAnnotations("switch", offset, offset)
                    .firstOrNull()?.let {
                        onSwitch()
                    }
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .navigationBarsPadding()
        )
    }
}


@Preview
@Composable
fun SingInScreenPreview() {
    SignInScreen(
        mode = AuthMode.SignUp,
        onPrimary = { _, _ -> },
        onForgotPassword = {},
        onSwitch = {}
    )
}
