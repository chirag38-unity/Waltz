package com.chirag_redij.waltz.ui.screens.homescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirag_redij.waltz.network.api.APIRepository
import com.chirag_redij.waltz.network.api.Webservices.FEED_IMAGE_COUNT
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.chirag_redij.waltz.network.dataclasses.util.onError
import com.chirag_redij.waltz.network.dataclasses.util.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class HomeScreenViewModel : ViewModel() {

    private val _feedPhotosList = MutableStateFlow<List<PhotoModel>>(emptyList())
    val feedPhotosList = _feedPhotosList
        .onStart { loadInitialFeed() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()     // Initial Value
        )

    private var pageIndex by mutableIntStateOf(0)

    private val _initLoading = MutableStateFlow(true)
    val initLoading = _initLoading.asStateFlow()

    private val _paging = MutableStateFlow(false)
    val paging = _paging.asStateFlow()

    private val _isEndReached = MutableStateFlow(false)
    val isEndReached = _isEndReached.asStateFlow()

    private fun loadInitialFeed() {
        viewModelScope.launch {
            APIRepository.getPhotosFeed(++pageIndex)
                .onSuccess { feedList ->

                    viewModelScope.launch {
                        _isEndReached.update {
                            feedList.page * feedList.per_page >= feedList.total_results
                        }
                    }

                    val currentList = _feedPhotosList.value

                    viewModelScope.launch {

                        val updatedPhotosList = feedList.photos
                            .filterNot { newPhoto ->
                                currentList.any { currentPhoto -> currentPhoto.id == newPhoto.id }
                            }
                            .map { photo ->
                                photo.copy(
                                    height = Random.nextInt(100, 300),
                                )
                            }

                        val combinedList = currentList + updatedPhotosList

                        viewModelScope.launch {
                            _feedPhotosList.update {
                                combinedList
                            }
                            _initLoading.update{false}
                        }

                    }

                }
                .onError { error ->
                    Timber.tag("loadInitialFeed").d(error.name)
                    viewModelScope.launch {
                        _initLoading.update{false}
                    }
                }

        }
    }

    fun pageFutureResults() {
        viewModelScope.launch {
            _paging.emit(true)
        }

        viewModelScope.launch {
            APIRepository.getPhotosFeed(++pageIndex )
                .onSuccess { feedList ->
                    viewModelScope.launch {

                        viewModelScope.launch {
                            _isEndReached.update{
                                feedList.page * feedList.per_page >= feedList.total_results
                            }
                        }

                        val currentList = _feedPhotosList.value

                        val updatedPhotosList = feedList.photos
                            .filterNot { newPhoto ->
                                currentList.any { currentPhoto -> currentPhoto.id == newPhoto.id }
                            }
                            .map { photo ->
                                photo.copy(
                                    height = Random.nextInt(100, 300),
                                )
                            }

                        val combinedList = currentList + updatedPhotosList

                        viewModelScope.launch {
                            delay(1000)
                            _feedPhotosList.update{ combinedList }
                            _paging.update{ false }
                        }

                    }

                }
                .onError { error ->
                    Timber.tag("loadInitialFeed").d(error.name)
                    viewModelScope.launch {
                        _paging.update{ false }
                    }
                }

        }
    }

}