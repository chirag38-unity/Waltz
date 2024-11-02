package com.chirag_redij.waltz.ui.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PhotoItem(
    photo: PhotoModel,
    windowHeightSizeClass: WindowHeightSizeClass,
    shimmerInstance: Shimmer,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPhotoClicked : (PhotoModel) -> Unit,
    modifier: Modifier = Modifier
) {

    val rounderCornerAnimation by animatedVisibilityScope.transition
        .animateDp(label = "Rounder Corner") { enterExit ->
            when(enterExit) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> 10.dp
                EnterExitState.PostExit -> 10.dp
            }
        }

    val heightMultiplier = when (windowHeightSizeClass) {
        WindowHeightSizeClass.EXPANDED -> 1.5f
        else -> 1f
    }

    Column(
        modifier = modifier
//            .sharedBounds(
//                sharedContentState = rememberSharedContentState(
//                    key = "${photo.id}_bounds"
//                ),
//                animatedVisibilityScope = animatedVisibilityScope
//            )
            .fillMaxWidth()
            .height((photo.height * heightMultiplier + 24).dp)
            .clickable (
                onClick = {
                    onPhotoClicked(photo)
                }
            )
    ) {
        SubcomposeAsyncImage(
            model = photo.src.large,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .shimmer(shimmerInstance)
                )
            },
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("${photo.id}_image"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(
                        RoundedCornerShape(rounderCornerAnimation)
                    )
                )
                .fillMaxWidth()
                .height((photo.height * heightMultiplier).dp)
                .clip(RoundedCornerShape(rounderCornerAnimation))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("${photo.id}_name"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .basicMarquee(),
            text = photo.photographer,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}