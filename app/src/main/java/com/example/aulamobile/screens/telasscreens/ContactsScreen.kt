package com.example.aulatelas.screens.telasscreens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aulamobile.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(modifier: Modifier = Modifier) {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.cupcake, "cupcake"),
            CarouselItem(1, R.drawable.donut, "donut"),
            CarouselItem(2, R.drawable.eclair, "eclair"),
            CarouselItem(3, R.drawable.froyo, "froyo"),
            CarouselItem(4, R.drawable.gingerbread, "gingerbread"),
        )
    }

    Column {

        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { items.count() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, bottom = 16.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val item = items[i]
            Image(
                modifier = Modifier
                    .height(205.dp)
                    .maskClip(MaterialTheme.shapes.extraLarge),
                painter = painterResource(id = item.imageResId),
                contentDescription = item.contentDescription,
                contentScale = ContentScale.Crop
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider(thickness = 2.dp)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            repeat(30) {
                Text("Item $it", modifier = Modifier.padding(2.dp))
            }
        }
    }
}