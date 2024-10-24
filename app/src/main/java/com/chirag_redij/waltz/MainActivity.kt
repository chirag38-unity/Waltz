package com.chirag_redij.waltz

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.window.core.ExperimentalWindowCoreApi
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowMetricsCalculator
import com.chirag_redij.waltz.ui.theme.WaltzTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    @OptIn(ExperimentalWindowCoreApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        checkUpdates()

        val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val width = windowMetrics.bounds.width()
        val height = windowMetrics.bounds.height()

        val windowSizeClass = WindowSizeClass.compute(width,height, displayMetrics.density)

        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.EXPANDED) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContent {
            WaltzTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    private fun checkUpdates() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val isUpdateAllowed = when(updateType) {
                    AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                    AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                    else -> false
                }

                if (isUpdateAvailable && isUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        this,
                        123
                    )
                }

            }.addOnFailureListener { exception ->
                Timber.e(exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                Timber.e("Something went wrong while updating the app")
            }
        }

    }

}