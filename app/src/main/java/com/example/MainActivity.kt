package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenRoute
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDarkest
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()

            MyApplicationTheme(darkTheme = settings.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NavyDarkest
                ) {
                    CityOfWealthApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CityOfWealthApp(viewModel: GameViewModel) {
    val currentRoute by viewModel.currentRoute.collectAsState()

    // مدیریت دکمه بازگشت اندروید
    BackHandler(enabled = currentRoute != ScreenRoute.MAIN_MENU) {
        when (currentRoute) {
            ScreenRoute.GAME -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.PLAYER_SETUP -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.STATS -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.ACHIEVEMENTS -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.RULES -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.SETTINGS -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.ONLINE_AUTH -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.ONLINE_LOBBY -> viewModel.navigateTo(ScreenRoute.MAIN_MENU)
            ScreenRoute.ONLINE_ROOM -> {
                viewModel.onlineManager.leaveCurrentRoom()
                viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
            }
            ScreenRoute.MAIN_MENU -> { /* خروج یا باقی ماندن در منو */ }
        }
    }

    when (currentRoute) {
        ScreenRoute.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
        ScreenRoute.PLAYER_SETUP -> PlayerSetupScreen(viewModel = viewModel)
        ScreenRoute.GAME -> GameScreen(viewModel = viewModel)
        ScreenRoute.STATS -> StatsScreen(viewModel = viewModel)
        ScreenRoute.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
        ScreenRoute.RULES -> RulesScreen(viewModel = viewModel)
        ScreenRoute.SETTINGS -> SettingsScreen(viewModel = viewModel)
        ScreenRoute.ONLINE_AUTH -> OnlineAuthScreen(viewModel = viewModel)
        ScreenRoute.ONLINE_LOBBY -> OnlineLobbyScreen(viewModel = viewModel)
        ScreenRoute.ONLINE_ROOM -> OnlineRoomScreen(viewModel = viewModel)
    }
}

