package com.example.aulamobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aulamobile.auth.AuthViewModel
import com.example.aulatelas.navigation.Screen
import com.example.aulatelas.screens.AuthMode
import com.example.aulatelas.screens.LoginScreen
import com.example.aulatelas.screens.SignInScreen
import com.example.aulatelas.screens.SplashScreen
import com.example.aulatelas.screens.TelasScreen


@Composable
fun AppNav(vm: AuthViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by vm.state.collectAsState()

    NavHost(navController = nav, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onLogin = { nav.navigate(Screen.LoginOptions.route) },
                onSignUp = { nav.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.LoginOptions.route) {
            LoginScreen(
                onEmailClick = { nav.navigate(Screen.SignIn.route) },
                onGoogleClick = {},
                onSingUpClick = { nav.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignIn.route) {
            SignInScreen(
                mode = AuthMode.SignIn,
                onPrimary = { email, pass -> vm.signIn(email, pass) },
                onForgotPassword = {},
                onSwitch = { nav.navigate(Screen.SignUp.route) }
            )
            LaunchedEffect(state.user) {
                if (state.user != null) {
                    goToTelas(nav)
                }
            }

        }
        composable(Screen.SignUp.route) {
            SignInScreen(
                mode = AuthMode.SignUp,
                onPrimary = { email, pass -> vm.signUp(email, pass) },
                onSwitch = { nav.navigate(Screen.SignIn.route) }
            )
            LaunchedEffect(state.user) {
                if (state.user != null) {
                    goToTelas(nav)
                }
            }
        }
        composable(Screen.Telas.route) {
            TelasScreen( onLogout = {
                vm.signOut()
                nav.navigate(Screen.Splash.route) {
                    popUpTo(Screen.Telas.route) {
                        inclusive = true
                    }
                }
            })
        }

    }
}

private fun goToTelas(nav: NavHostController) {
    nav.navigate(Screen.Telas.route) {
        popUpTo(Screen.Splash.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}