package com.spicemarket.match.presentation.app

import androidx.lifecycle.ViewModel
import com.spicemarket.match.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = currentScreen.asStateFlow()

    private val backAvailable = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = backAvailable.asStateFlow()

    private var roundCounter = 0

    fun navigateTo(target: Screen) {
        val from = currentScreen.value
        if (from !is Screen.Splash) backStack.addLast(from)
        currentScreen.value = target
        backAvailable.value = backStack.isNotEmpty()
    }

    fun replaceWith(target: Screen) {
        currentScreen.value = target
        backAvailable.value = backStack.isNotEmpty()
    }

    fun startRound() {
        roundCounter += 1
        navigateTo(Screen.Game(roundCounter))
    }

    fun restartRound() {
        roundCounter += 1
        replaceWith(Screen.Game(roundCounter))
    }

    fun showResult(target: Screen) {
        replaceWith(target)
    }

    fun openMenu() {
        backStack.clear()
        currentScreen.value = Screen.Menu
        backAvailable.value = false
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        currentScreen.value = previous
        backAvailable.value = backStack.isNotEmpty()
    }
}
