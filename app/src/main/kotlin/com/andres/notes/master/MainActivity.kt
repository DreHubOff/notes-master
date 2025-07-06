@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.andres.notes.master

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.core.model.ThemeType
import com.andres.notes.master.data.preferences.UserPreferences
import com.andres.notes.master.ui.animation.defaultAnimationSpec
import com.andres.notes.master.ui.animation.scaleInFromBottomRight
import com.andres.notes.master.ui.animation.scaleOutToBottomRight
import com.andres.notes.master.ui.navigation.NavigationEvent
import com.andres.notes.master.ui.navigation.NavigationEventsHost
import com.andres.notes.master.ui.screens.Route
import com.andres.notes.master.ui.screens.edit.checklist.EditChecklistScreen
import com.andres.notes.master.ui.screens.edit.note.EditNoteScreen
import com.andres.notes.master.ui.screens.main.MainScreen
import com.andres.notes.master.ui.screens.trash.TrashScreen
import com.andres.notes.master.ui.shared.LocalSharedTransitionSettings
import com.andres.notes.master.ui.shared.SetupSystemNavigationBars
import com.andres.notes.master.ui.shared.SharedTransitionSettings
import com.andres.notes.master.ui.theme.ApplicationTheme
import com.andres.notes.master.util.getAndRemove
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Provider

private val TAG = MainActivity::class.java.simpleName

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationEventsHost: NavigationEventsHost

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var jsonProvider: Provider<Json>

    private var initialThemeType: ThemeType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { initialThemeType == null }
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { initialThemeType = userPreferences.getTheme() }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeType: ThemeType by userPreferences
                .observeTheme()
                .collectAsStateWithLifecycle(
                    initialThemeType ?: ThemeType.SYSTEM_DEFAULT
                )

            val isSystemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = remember(themeType, isSystemInDarkTheme) {
                when (themeType) {
                    ThemeType.LIGHT -> false
                    ThemeType.DARK -> true
                    ThemeType.SYSTEM_DEFAULT -> isSystemInDarkTheme
                }
            }

            LaunchedEffect(isDarkTheme) {
                val window = window
                val decorView = window.decorView
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, decorView).isAppearanceLightStatusBars = !isDarkTheme
            }

            LaunchedEffect(themeType) {
                when (themeType) {
                    ThemeType.LIGHT -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )

                    ThemeType.DARK -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )

                    ThemeType.SYSTEM_DEFAULT -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )
                }
            }

            CompositionLocalProvider(LocalThemeMode provides if (isDarkTheme) ThemeMode.DARK else ThemeMode.LIGHT) {
                ApplicationTheme(darkTheme = isDarkTheme) {
                    val navController = rememberNavController()
                    SharedTransitionLayout {
                        BuildNavigationGraph(navController)
                    }
                    ObserveNavigationEvents(navController)
                }
            }
        }
        intent.targetItem(jsonProvider.get())?.let(::openItemEditorScreen)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.targetItem(jsonProvider.get())?.let(::openItemEditorScreen)
    }

    private fun openItemEditorScreen(itemToOpen: ApplicationMainDataType) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                intent = null
                Log.d(TAG, "Opening item editor screen: $itemToOpen")
                val itemRoute = when (itemToOpen) {
                    is TextNote -> Route.EditNoteScreen(
                        itemToOpen.id
                    )

                    is Checklist -> Route.EditChecklistScreen(
                        itemToOpen.id
                    )
                }
                navigationEventsHost.popBackStack(toRoute = Route.MainScreen)
                navigationEventsHost.navigate(itemRoute)
            }
        }
    }

    @Composable
    private fun SharedTransitionScope.BuildNavigationGraph(navController: NavHostController) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            navController = navController,
            startDestination = Route.MainScreen,
        ) {
            composable<Route.MainScreen>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) { backStackEntry ->
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    val noteEditingResult = backStackEntry
                        .savedStateHandle
                        .getAndRemove<Route.EditNoteScreen.Result>(Route.EditNoteScreen.Result.KEY)
                    val checklistEditingResult = backStackEntry
                        .savedStateHandle
                        .getAndRemove<Route.EditChecklistScreen.Result>(Route.EditChecklistScreen.Result.KEY)
                    MainScreen(
                        noteEditingResult = noteEditingResult,
                        checklistEditingResult = checklistEditingResult,
                    )
                }
            }
            composable<Route.EditNoteScreen>(
                enterTransition = { scaleInFromBottomRight() },
                exitTransition = { scaleOutToBottomRight() },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditNoteScreen()
                }
            }
            composable<Route.EditChecklistScreen>(
                enterTransition = { scaleInFromBottomRight() },
                exitTransition = { scaleOutToBottomRight() },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditChecklistScreen()
                }
            }
            composable<Route.TrashScreen>(
                enterTransition = { fadeIn(animationSpec = defaultAnimationSpec()) },
                exitTransition = { fadeOut(animationSpec = defaultAnimationSpec()) },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    TrashScreen()
                }
            }
        }
    }

    @Composable
    private fun ObserveNavigationEvents(navController: NavHostController) {
        LaunchedEffect(navigationEventsHost, lifecycle) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                var lastNavigationEventTime = System.currentTimeMillis()
                navigationEventsHost.navigationRoute.collectLatest { event ->
                    val currentTime = System.currentTimeMillis()
                    if (event is NavigationEvent.NavigateBack && currentTime - lastNavigationEventTime < 400) {
                        Log.w(TAG, "Ignoring navigation event: $event")
                        return@collectLatest
                    }
                    lastNavigationEventTime = currentTime
                    withContext(Dispatchers.Main.immediate) {
                        handleNavigationEvent(event = event, navController = navController)
                    }
                }
            }
        }
    }

    private fun handleNavigationEvent(
        event: NavigationEvent,
        navController: NavHostController,
    ) {
        if (event.consumed) return
        Log.d(TAG, "Handling navigation event: $event")
        when (event) {
            is NavigationEvent.NavigateBack -> {
                event.result?.let { (key, result) ->
                    navController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(key, result)
                }
                if (navController.previousBackStackEntry == null) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            }

            is NavigationEvent.NavigateTo -> navController.navigate(event.route)
            is NavigationEvent.SendIntent -> {
                try {
                    startActivity(event.intent)
                } catch (error: Exception) {
                    Log.e(TAG, "Error while sending intent", error)
                }
            }

            is NavigationEvent.PopBackStack -> navController.popBackStack(
                event.toRoute,
                event.inclusive
            )
        }
        event.consumed = true
    }

    @Composable
    private fun AnimatedContentScope.NavigationRoute(
        sharedTransitionScope: SharedTransitionScope,
        content: @Composable AnimatedContentScope.() -> Unit,
    ) {
        SetupSystemNavigationBars()
        CompositionLocalProvider(
            value = LocalSharedTransitionSettings provides SharedTransitionSettings(
                transitionScope = sharedTransitionScope, animationScope = this@NavigationRoute
            )
        ) {
            content()
        }
    }

    companion object {

        private const val KEY_TARGET_ITEM = "target_item"

        private fun Intent.targetItem(json: Json): ApplicationMainDataType? {
            val itemJson = getStringExtra(KEY_TARGET_ITEM) ?: return null
            return json.decodeFromString(itemJson)
        }

        fun getOpenItemEditorIntent(
            context: Context,
            json: Json,
            item: ApplicationMainDataType,
        ): Intent =
            Intent(context, MainActivity::class.java)
                .putExtra(KEY_TARGET_ITEM, json.encodeToString(item))
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}