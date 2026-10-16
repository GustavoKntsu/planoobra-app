package com.example.aulatelas.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulamobile.R


@Composable
fun LoginScreen(
    onEmailClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onSingUpClick: () -> Unit = {},
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(top = 8.dp)

            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Entrar",
                fontSize = 55.sp,
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.purple_500),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 36.dp)

            )

            LoadingIconButton(
                text = "Continuar com Email",
                iconRes = R.drawable.baseline_email_24,
                container = colorResource(R.color.purple_500),
                onClick = onEmailClick

            )
            Spacer(Modifier.height(24.dp))

            LoadingIconButton(
                text = "Continuar com Google",
                iconRes = R.drawable.icons8_google_24,
                container = colorResource(R.color.purple_500),
                onClick = onEmailClick

            )
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.weight(1f))
        }
        val annotated = buildAnnotatedString {
            append("Não tem uma conta? ")
            pushStringAnnotation(tag = "signup", annotation = "signup")
            withStyle(
                SpanStyle(
                    color = colorResource(R.color.purple_500),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Cadastre-se")
            }
            pop()
        }
        ClickableText(
            text = annotated,
            onClick = { offset ->
                annotated.getStringAnnotations(
                    tag = "signup",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onSingUpClick()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun LoadingIconButton(
    text: String,
    iconRes: Int,
    container: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()

        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.Companion.size(30.dp)
            )
            Spacer(Modifier.Companion.width(12.dp))

            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )

        }
    }

}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}