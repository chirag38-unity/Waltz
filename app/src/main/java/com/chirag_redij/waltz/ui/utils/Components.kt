package com.chirag_redij.waltz.ui.utils

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import coil.compose.AsyncImage
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel

@Composable
fun PhotoItem(
    photo: PhotoModel,
    windowHeightSizeClass: WindowHeightSizeClass,
    onPhotoClicked : (PhotoModel) -> Unit,
    modifier: Modifier = Modifier
) {

    val heightMultiplier = when (windowHeightSizeClass) {
        WindowHeightSizeClass.EXPANDED -> 1.5f
        else -> 1f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
//            .height(photo.height.dp)
            .height((photo.height * heightMultiplier + 24).dp)
            .clickable (
                onClick = {
                    onPhotoClicked(photo)
                }
            )
    ) {
        AsyncImage(
            model = photo.src.large,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height((photo.height * heightMultiplier).dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.basicMarquee(),
            text = photo.photographer,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}