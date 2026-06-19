package io.github.yulbax.frkn.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.yulbax.frkn.R
import io.github.yulbax.frkn.ui.components.AppSearchBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = navPages.firstOrNull { it.route == currentRoute } ?: Screen.Main

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var appsQuery by rememberSaveable { mutableStateOf("") }
    val onAppsScreen = currentRoute == Screen.Apps.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val navigate: (Screen) -> Unit = { screen ->
                scope.launch { drawerState.close() }
                navController.navigateToScreen(screen)
            }
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight()) {
                    Spacer(Modifier.height(12.dp))
                    primaryNavPages.forEach { screen ->
                        DrawerItem(
                            screen = screen,
                            selected = currentRoute == screen.route,
                            onClick = { navigate(screen) }
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
                    )
                    secondaryNavPages.forEach { screen ->
                        DrawerItem(
                            screen = screen,
                            selected = currentRoute == screen.route,
                            onClick = { navigate(screen) }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (onAppsScreen) {
                            AppSearchBar(
                                query = appsQuery,
                                onQueryChange = { appsQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(currentScreen.titleRes),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.open_menu_cd)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            FrknNavHost(
                navController = navController,
                appsQuery = appsQuery,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DrawerItem(screen: Screen, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = null
            )
        },
        label = { Text(stringResource(screen.titleRes)) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
