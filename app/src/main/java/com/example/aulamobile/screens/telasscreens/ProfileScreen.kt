package com.example.aulatelas.screens.telasscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulamobile.R

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(
                colorResource(id = R.color.teal_200)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),

            )


        Box(
            modifier
                .fillMaxSize()
                .padding(top = 400.dp)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(
                    colorResource(id = R.color.white)
                )
        )
        Column(
            modifier
                .fillMaxSize()
                .padding(top = 230.dp)
        ) {
            Surface(
                shape = CircleShape,
                shadowElevation = 6.dp,
                color = colorResource(id = R.color.white),
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_2),
                    contentDescription = "profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = "John Doe",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(id = R.color.white)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "email@email.com",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorResource(
                        id = R.color.white
                    )
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .navigationBarsPadding()
            ) {
                MenuItemRow("Sair", R.drawable.baseline_logout_24, onLogout)

            }
        }
    }
}

@Composable
private fun MenuItemRow(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            tonalElevation = 6.dp,
            color = colorResource(id = R.color.white),
            modifier = Modifier.size(50.dp)
        ) {
            Image(painter = painterResource(iconRes), contentDescription = null)
        }
        Spacer(Modifier.width(14.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.purple_500)
            ),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
            contentDescription = null,
            tint = colorResource(id = R.color.purple_500)
        )

    }

}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
