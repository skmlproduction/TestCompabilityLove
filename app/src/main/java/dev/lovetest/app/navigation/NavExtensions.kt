package dev.lovetest.app.navigation

import androidx.navigation.NavController

fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
