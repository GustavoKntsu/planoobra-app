package com.example.aulatelas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aulatelas.screens.telasscreens.ContactsScreen
import com.example.aulatelas.screens.telasscreens.EventsScreen
import com.example.aulatelas.screens.telasscreens.HomeScreen
import com.example.aulatelas.screens.telasscreens.ProfileScreen
import com.example.aulatelas.ui.theme.AulaTelasTheme


class BottomAppBarItem(
    val icon: ImageVector,
    val label: String
)

class TopAppBarItem(
    var title: String,
    val icons: List<ImageVector> = emptyList()
)

sealed class ScreenItem(
    val topAppBarItem: TopAppBarItem,
    val bottomAppBarItem: BottomAppBarItem

) {
    data object Home : ScreenItem(
        topAppBarItem = TopAppBarItem(
            title = "Início",
            icons = listOf(
                Icons.Default.AccountCircle,
                Icons.Default.MoreVert,
            )
        ),
        bottomAppBarItem = BottomAppBarItem(
            icon = Icons.Default.Home,
            label = "Início"
        )
    )

    data object Events : ScreenItem(
        topAppBarItem = TopAppBarItem(
            title = "Eventos",
            icons = listOf(
                Icons.Default.Search,
                Icons.Default.MoreVert,
            )
        ),
        bottomAppBarItem = BottomAppBarItem(
            icon = Icons.Default.EventBusy,
            label = "Eventos"
        )
    )

    data object Contacts : ScreenItem(
        topAppBarItem = TopAppBarItem(
            title = "Contatos",
            icons = listOf(
                Icons.Default.Search,
                Icons.Default.MoreVert,
            )
        ),
        bottomAppBarItem = BottomAppBarItem(
            icon = Icons.Default.Contacts,
            label = "Contatos"
        )
    )

    data object Perfil : ScreenItem(
        topAppBarItem = TopAppBarItem(
            title = "Perfil",
            icons = listOf(
                Icons.Default.MoreVert,
            )
        ),
        bottomAppBarItem = BottomAppBarItem(
            icon = Icons.Default.Person,
            label = "Perfil"
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TelasScreen(
    onLogout: () -> Unit = {}
) {
    val screens = remember {
        listOf(
            ScreenItem.Home,
            ScreenItem.Events,
            ScreenItem.Contacts,
            ScreenItem.Perfil

        )
    }

    var currentScreen by remember {
        mutableStateOf(screens.first())
    }

    val pagerState = rememberPagerState {
        screens.size
    }

    LaunchedEffect(currentScreen) {
        pagerState.animateScrollToPage(screens.indexOf(currentScreen))
    }

    LaunchedEffect(pagerState.targetPage) {
        currentScreen = screens[pagerState.targetPage]
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Text(currentScreen.topAppBarItem.title)
            }, actions = {
                Row(
                    Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    currentScreen.topAppBarItem.icons.forEach { icon ->
                        Icon(icon, contentDescription = null)

                    }
                }
            })
        },
        bottomBar = {
            BottomAppBar {
                screens.forEach { screen ->
                    with(screen.bottomAppBarItem) {
                        NavigationBarItem(
                            selected = screen == currentScreen,
                            onClick = {
                                currentScreen = screen
                            },
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(label) }
                        )
                    }
                }
            }

        }

    ) { innerPadding ->

        HorizontalPager(
            pagerState,
            Modifier.padding(innerPadding)
        ) { page ->
            val item = screens[page]
            when (item) {
                ScreenItem.Contacts -> ContactsScreen()
                ScreenItem.Events -> EventsScreen()
                ScreenItem.Home -> HomeScreen()
                ScreenItem.Perfil -> ProfileScreen(onLogout = onLogout)
            }

        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    AulaTelasTheme {
        TelasScreen()
    }
}
