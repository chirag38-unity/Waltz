package com.waltz.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()


    private fun startup(mode : CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.chirag_redij.waltz",
        metrics = listOf(FrameTimingMetric(), StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()

//        val photosFeed = device.findObject(By.res("photos_feed"))
//
//        // Wait until childCount is 20 or timeout occurs
//        val maxWaitTime = 5000L // max wait time in milliseconds (5 seconds)
//        val checkInterval = 100L // interval to check childCount (100 ms)
//        var waitedTime = 0L
//
//        while (photosFeed.childCount != 20 && waitedTime < maxWaitTime) {
//            Thread.sleep(checkInterval)
//            waitedTime += checkInterval
//        }
//
//        photosFeed.setGestureMargin(device.displayWidth / 5)
//        if (photosFeed.childCount == 20) {
//            photosFeed.fling(Direction.DOWN)
//        }

    }

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.Partial())

}