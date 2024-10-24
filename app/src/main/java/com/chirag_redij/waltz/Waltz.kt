package com.chirag_redij.waltz

import android.app.Activity
import android.app.Application
import android.os.Bundle
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.chirag_redij.waltz.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class Waltz : Application(), ImageLoaderFactory {

    private val lifecycleCallbacks = MyActivityLifecycleCallbacks()

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache{
                MemoryCache.Builder(this)
                    .maxSizePercent(0.05)
                    .strongReferencesEnabled(true)
                    .build()
            }.logger(DebugLogger())
            .build()
    }

    fun isAppInForeground() : Boolean {
        return lifecycleCallbacks.isAppInForeground()
    }

    override fun onCreate() {
        Timber.plant(Timber.DebugTree())
        registerActivityLifecycleCallbacks(lifecycleCallbacks)
        super.onCreate()

        startKoin {
            androidContext(this@Waltz)
            modules(
                appModule
            )
        }

    }

    override fun onTerminate() {
        imageLoader.memoryCache?.clear()
        super.onTerminate()
    }

}

class MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    var foregroundActivities = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        foregroundActivities++
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        foregroundActivities--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    fun isAppInForeground(): Boolean {
        return foregroundActivities > 0
    }
}