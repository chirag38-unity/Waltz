package com.chirag_redij.waltz.ui.utils

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class WallpaperProvider constructor(
    private val context: Context
) {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    // Function to check if the file already exists
    private fun fileExists(filePath: String): Boolean {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath)
        return file.exists()
    }

    fun downloadFile(
        url : String, title : String,
        onDownloadSuccess: (Uri) -> Unit,
        onDownloadFailed: (String) -> Unit,
        onFileAlreadyExists: (Uri) -> Unit
    ) {

        Timber.tag("Downloader").d("Url $url")

        val fileExtension = url.substringAfterLast('.', "")
        val mimeType = when (fileExtension.lowercase()) {
            "jpeg", "jpg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"  // Default to a binary stream if the type is unknown
        }
        val filePath = "Waltz/$title.$fileExtension"

        if (fileExists(filePath)) {
            Timber.tag("Downloader").d("File already exists: $filePath")
            val fileUri = Uri.fromFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath))
            onFileAlreadyExists(fileUri)
            return
        }

        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("$title.$fileExtension")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Waltz/$title.$fileExtension")
            .setDescription("Downloading Wallpaper from Waltz")

        val downloadId = downloadManager.enqueue(request)

    }

    fun setWallpaper(
        url : String, title : String,
        onWallpaperSetSuccess: () -> Unit,
        onWallpaperSetFailed: (String) -> Unit
    ) {

        val fileExtension = url.substringAfterLast('.', "")

        val filePath = "Waltz/$title.$fileExtension"

        if (fileExists(filePath)) {
            Timber.tag("Downloader").d("File already exists: $filePath")
            val fileUri = Uri.fromFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath))
            setWallpaper(fileUri,onWallpaperSetSuccess,onWallpaperSetFailed)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(url) // Use the provided URL
                        .allowHardware(false) // Disable hardware bitmaps.
                        .build()

                    val result = (loader.execute(request) as SuccessResult).drawable
                    val bitmap = (result as BitmapDrawable).bitmap

                    // Set the wallpaper using the downloaded bitmap
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)

                    // Switch to Main thread to update UI
                    withContext(Dispatchers.Main) {
                        onWallpaperSetSuccess()
                    }
                } catch (e: Exception) {
                    // Switch to Main thread to update UI on failure
                    withContext(Dispatchers.Main) {
                        onWallpaperSetFailed("Failed to set wallpaper: ${e.message}")
                    }
                }
            }
        }

    }

    fun setWallpaper(
        imageUri: Uri,
        onWallpaperSetSuccess: () -> Unit,
        onWallpaperSetFailed: (String) -> Unit
    ) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            wallpaperManager.setStream(inputStream)

            onWallpaperSetSuccess()
        } catch (e: Exception) {
            Timber.tag("Downloader").e(e, "Failed to set wallpaper")
            onWallpaperSetFailed("Failed to set wallpaper: ${e.message}")
        }
    }



}