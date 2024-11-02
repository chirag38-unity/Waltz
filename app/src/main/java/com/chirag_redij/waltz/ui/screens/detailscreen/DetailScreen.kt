package com.chirag_redij.waltz.ui.screens.detailscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chirag_redij.waltz.R
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.chirag_redij.waltz.ui.utils.SlideTransition
import com.chirag_redij.waltz.ui.utils.WallpaperEvents
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination<RootGraph>()
@Composable
fun SharedTransitionScope.DetailScreen(
    photoModel: PhotoModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    detailScreenViewModel: DetailScreenViewModel = koinViewModel(),
    navigator: DestinationsNavigator
) {

    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val messageBarState = rememberMessageBarState()

    val pagingLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.heart_loader_anim)
    )
    val pagingProgress by animateLottieCompositionAsState(
        composition = pagingLottieComposition,
        iterations = LottieConstants.IterateForever
    )

    val rounderCornerAnimation by animatedVisibilityScope.transition
        .animateDp(label = "Rounder Corner") { enterExit ->
            when(enterExit) {
                EnterExitState.PreEnter -> 10.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 0.dp
            }
        }


    LaunchedEffect(key1 = lifeCycleOwner.lifecycle) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            detailScreenViewModel.eventChannel.collect{ event ->
                when (event) {
                    is WallpaperEvents.WallpaperDownloadFailed -> {
                        messageBarState.addError(Exception("Download Failed ${event.errorMessage}"))
                    }
                    is WallpaperEvents.WallpaperDownloaded -> {
                        messageBarState.addSuccess("Downloaded Successfully")
                    }
                    is WallpaperEvents.WallpaperSetFailed -> {
                        messageBarState.addError(Exception("Failed to set wallpaper"))
                    }
                    WallpaperEvents.WallpaperSetSuccess -> {
                        messageBarState.addSuccess("Wallpaper Set Successfully")
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        ContentWithMessageBar(
            messageBarState = messageBarState,
            verticalPadding = 40.dp,
            showCopyButton = false,
        ) {

            Box (
                modifier = Modifier
                    .fillMaxSize()
            ) {

                SubcomposeAsyncImage(
                    model = photoModel.src.original,
                    contentDescription = photoModel.alt,
                    contentScale = ContentScale.FillHeight,
                    loading = {
                        LottieAnimation(
                            modifier = Modifier,
                            composition = pagingLottieComposition,
                            progress = { pagingProgress }
                        )
                    },
                    modifier = Modifier
                        .then(
                            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                                Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("${photoModel.id}_image"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            RoundedCornerShape(rounderCornerAnimation)
                                        )
                                    )
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(rounderCornerAnimation))
                            } else {
                                Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("${photoModel.id}_image"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            RoundedCornerShape(rounderCornerAnimation)
                                        )
                                    )
                                    .align(Alignment.Center)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(rounderCornerAnimation))
                            }
                        )
                )

                with(animatedVisibilityScope) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface, // Starting color (top)
                                        Color.Unspecified  // Ending color (bottom)
                                    )
                                )
                            )
                            .padding(innerPadding)
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f
                            )
                            .animateEnterExit(
                                enter = fadeIn() + slideInVertically{ -it },
                                exit = fadeOut() + slideOutVertically{ -it }
                            )
                    ) {

                        Text(
                            photoModel.photographer,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState("${photoModel.id}_name"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .align(Alignment.CenterStart)
                                .padding(start = 10.dp)
                            ,
                            style = MaterialTheme.typography.headlineMedium
                        )

                    }

                    Row (
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(innerPadding)
                            .padding(bottom = 20.dp)
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f
                            )
                            .animateEnterExit(
                                enter = fadeIn() + slideInVertically{ it },
                                exit = fadeOut() + slideOutVertically{ it }
                            )
                        ,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        FloatingActionButton(
                            onClick = {
                                detailScreenViewModel.downloadImage(photoModel)
                                messageBarState.addSuccess("Downloading image...")
                            }
                        ) {
                            Icon(imageVector = Icons.Rounded.Download, contentDescription = "Download Image")
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        ExtendedFloatingActionButton (
                            onClick = {
                                detailScreenViewModel.setWallpaper(photoModel)
                            }
                        ) {
                            Icon(imageVector = Icons.Rounded.PhotoLibrary, contentDescription = "Set Image")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Set Wallpaper")
                        }

                    }
                }



            }

        }

    }

}