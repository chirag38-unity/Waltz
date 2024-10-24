package com.chirag_redij.waltz.ui.utils

import android.net.Uri

sealed interface WallpaperEvents {
    data class WallpaperDownloaded(val imageURI: Uri) : WallpaperEvents
    data class WallpaperDownloadFailed(val errorMessage : String? = null) : WallpaperEvents
    data object WallpaperSetSuccess : WallpaperEvents
    data class WallpaperSetFailed(val errorMessage : String? = null) : WallpaperEvents
}