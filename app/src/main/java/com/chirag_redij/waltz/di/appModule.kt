package com.chirag_redij.waltz.di

import com.chirag_redij.waltz.ui.screens.detailscreen.DetailScreenViewModel
import com.chirag_redij.waltz.ui.screens.homescreen.HomeScreenViewModel
import com.chirag_redij.waltz.ui.utils.WallpaperProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    viewModel {
        HomeScreenViewModel()
    }

    viewModel {
        DetailScreenViewModel(get())
    }

    single {
        WallpaperProvider(get())
    }

}