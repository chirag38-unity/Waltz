package com.chirag_redij.waltz.ui.screens.detailscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.chirag_redij.waltz.ui.utils.WallpaperEvents
import com.chirag_redij.waltz.ui.utils.WallpaperProvider
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestinationNavArgs
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailScreenViewModel (
    private val wallpaperProvider: WallpaperProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: DetailScreenDestinationNavArgs = DetailScreenDestination.argsFrom(savedStateHandle)

    private val _eventChannel = Channel<WallpaperEvents>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
        onUndeliveredElement = {
            Timber.tag("Event Dropped").d(it.toString())
        }
    )

    val eventChannel = _eventChannel.receiveAsFlow()

    fun downloadImage(photoModel: PhotoModel) {

        wallpaperProvider.downloadFile(
            photoModel.src.original, photoModel.photographer,
            onDownloadSuccess = { imageUri ->
                viewModelScope.launch {
                    _eventChannel.send(WallpaperEvents.WallpaperDownloaded(imageUri))
                }
            },
            onDownloadFailed = { error ->
                viewModelScope.launch {
                    _eventChannel.send(WallpaperEvents.WallpaperDownloadFailed())
                }
            },
            onFileAlreadyExists = { imageUri ->
                viewModelScope.launch {
                    _eventChannel.send(WallpaperEvents.WallpaperDownloaded(imageUri))
                }
            }
        )

    }

    fun setWallpaper(photoModel: PhotoModel) {
        wallpaperProvider.setWallpaper(
            photoModel.src.original, photoModel.photographer,
            onWallpaperSetSuccess = {
                viewModelScope.launch {
                    _eventChannel.send(WallpaperEvents.WallpaperSetSuccess)
                }
            },
            onWallpaperSetFailed = {
                viewModelScope.launch {
                    _eventChannel.send(WallpaperEvents.WallpaperSetFailed())
                }
            }
        )

    }

}