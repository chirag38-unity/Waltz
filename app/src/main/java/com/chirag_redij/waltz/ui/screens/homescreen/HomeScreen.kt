package com.chirag_redij.waltz.ui.screens.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chirag_redij.waltz.MainActivity
import com.chirag_redij.waltz.R
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.chirag_redij.waltz.ui.utils.PhotoItem
import com.google.android.play.core.review.ReviewManagerFactory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import kotlin.random.Random

@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeScreenViewModel: HomeScreenViewModel = koinViewModel()
) {

    val context = LocalContext.current

    val photosList by homeScreenViewModel.feedPhotosList.collectAsStateWithLifecycle()
    val initLoading by homeScreenViewModel.initLoading.collectAsStateWithLifecycle()
    val paging by homeScreenViewModel.paging.collectAsStateWithLifecycle()
    val isEndReach by homeScreenViewModel.isEndReached.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val columns = when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 2
        WindowWidthSizeClass.MEDIUM -> 3
        WindowWidthSizeClass.EXPANDED -> 4
        else -> 2
    }

    val gridState = rememberLazyStaggeredGridState()

    val randomThreshold = 0.2

    val showReviewDialog : () -> Unit = {
        try {
            val reviewManager = ReviewManagerFactory.create(context)
            reviewManager.requestReviewFlow().addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    reviewManager.launchReviewFlow(context as MainActivity, request.result)
                }
            }
        } catch (e : RuntimeException) {
            Timber.tag("Review").d(e.toString())
        }

    }

    val maybeTriggerInAppReview : () -> Unit = {
        // Generate a random number between 0.0 and 1.0
        val randomValue = Random.nextDouble(0.0, 1.0)

        // Check if random value is less than the threshold
        if (randomValue < randomThreshold) {
            showReviewDialog()
        }
    }

    LaunchedEffect (true) { maybeTriggerInAppReview() }

    LaunchedEffect(photosList) {
        Timber.tag("LoadData").d(photosList.size.toString())
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex + gridState.layoutInfo.visibleItemsInfo.size }
            .collect { visibleItemCount ->
                if (visibleItemCount >= photosList.size && !isEndReach && !initLoading &&!paging ) {
                    homeScreenViewModel.pageFutureResults()
                }
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyVerticalStaggeredGrid(
            state = gridState,
            columns = StaggeredGridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            item (
                span = StaggeredGridItemSpan.FullLine
            ) {
                AnimatedVisibility(
                    visible = initLoading,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Init Loader"
                ) {

                    val initLoadingLottieComposition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.waltz_animation)
                    )
                    val initLoadingProgress by animateLottieCompositionAsState(
                        composition = initLoadingLottieComposition,
                        iterations = 1
                    )

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LottieAnimation(
                            modifier = Modifier,
                            composition = initLoadingLottieComposition,
                            progress = { initLoadingProgress }
                        )
                    }
                }
            }

            items(
                photosList,
                key = { item: PhotoModel ->
                        item.id
                }
            ) { photo ->
                PhotoItem(
                    photo,
                    windowSizeClass.windowHeightSizeClass,
                    onPhotoClicked = {
                        navigator.navigate(DetailScreenDestination(photo))
                    },
                    modifier = Modifier.animateItem()
                )
            }


            item (
                span = StaggeredGridItemSpan.FullLine
            ) {

                AnimatedVisibility(
                    visible = paging,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Paging Loader"
                ) {
                    val pagingLottieComposition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.heart_loader_anim)
                    )
                    val pagingProgress by animateLottieCompositionAsState(
                        composition = pagingLottieComposition,
                        iterations = LottieConstants.IterateForever
                    )

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LottieAnimation(
                            modifier = Modifier,
                            composition = pagingLottieComposition,
                            progress = { pagingProgress }
                        )
                    }
                }
            }

        }

    }
}