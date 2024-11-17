package com.teamify

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

class Actions(val navCont: NavHostController){
    val goHome: ()->Unit = {
        navCont.navigate("home")
    }
    val showTeam: (Long)->Unit={id->navCont.navigate("showTeam/${id}")}

    val editTeam: (Long)->Unit = {id->navCont.navigate("editTeam/${id}")}

    val navigateBack: ()->Unit = {navCont.popBackStack()}

    val teamChat : (Long) -> Unit = {id->navCont.navigate("teamChat/${id}")}


}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val actions = remember(navController) {
        Actions(navController)
    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home", enterTransition = {
            when (initialState.destination.route) {
                "details" ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700, easing = EaseInOut)
                    )

                else -> null
            }
        },
            exitTransition = {
                when (targetState.destination.route) {
                    "details" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700, easing = EaseInOut)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "details" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700, easing = EaseInOut)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "details" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700, easing = EaseInOut)
                        )

                    else -> null
                }
            }) {
            HomeScreen( actions)
        }
        composable("editTeam/{teamId}",
            enterTransition = {
                when (initialState.destination.route) {
                    "details" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(700, easing = EaseInOut)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "details" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(700,easing = EaseInOut)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "details" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(700,easing = EaseInOut)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "details" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(700, easing = EaseInOut)
                        )

                    else -> null
                }
            },
            arguments = listOf(navArgument("teamId") { type = NavType.LongType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val teamId = arguments.getLong("teamId")
            // Call your EditTeamScreen passing teamId
            EditTeamScreen(teamId = teamId,navController= navController,actions= actions)
        }
        composable(
            route = "showTeam/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.LongType }),
            enterTransition = {
                return@composable fadeIn(tween(1000))
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(1000)
                )
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            popExitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val teamId = arguments.getLong("teamId")
            TeamDetailsPane(teamId, navController)
        }

        composable(
            route = "profileDetails/{personId}",
            enterTransition = {
                return@composable fadeIn(tween(1000))
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            popExitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            arguments = listOf(navArgument("personId") { type = NavType.LongType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val personId = arguments.getLong("personId")
            ProfileDetailsPane(personId, navController)
        }

        //opened when the user clicks on the invite deeplink
        composable(
            route = "invite/{teamId}/{role}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "teamify://invite/{teamId}/{role}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType },
                navArgument("role") { type = NavType.StringType }
            )
        )
        {backStackEntry ->

            val args = requireNotNull(backStackEntry.arguments)
            val teamId = args.getLong("teamId")
            val role = args.getString("role")

            InviteScreen(teamId, Role.valueOf(role!!), navController)
        }

        composable(route="teamChat/{teamId}",enterTransition = {
            return@composable fadeIn(tween(1000))
        }, exitTransition = {
            return@composable slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(700)
            )
        },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            popExitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            },
            arguments = listOf(navArgument("teamId") { type = NavType.LongType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val teamId = arguments.getLong("teamId")
            ChatScreen(teamId, navController)
        }
    }
}

