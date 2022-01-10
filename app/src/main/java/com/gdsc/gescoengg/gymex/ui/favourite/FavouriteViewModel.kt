package com.gdsc.gescoengg.gymex.ui.favourite

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.gdsc.gescoengg.gymex.model.Workout
import com.gdsc.gescoengg.gymex.repository.SortOrder
import com.gdsc.gescoengg.gymex.repository.UserPreferenceRepository
import com.gdsc.gescoengg.gymex.repository.UserPreferences
import com.gdsc.gescoengg.gymex.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class FavouriteViewModel @ViewModelInject constructor(
    repository: WorkoutRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) :
    ViewModel() {
    init {
        Timber.i("FavouriteViewModel Init")
    }

    private val sortPreferenceFlow: Flow<UserPreferences> =
        userPreferenceRepository.userPreferencesFlow

    private val _sortOrder = MutableLiveData<String>()
    val sortOrder: LiveData<String> = _sortOrder

    private val favouriteListFlow: Flow<List<Workout>> = repository.getWorkouts().map {
        val favourites = it.filter { workout ->
            workout.isSaved
        }
        favourites
    }

    private val favouriteUiModelFlow: Flow<FavouriteUiModel> =
        combine(
            favouriteListFlow,
            sortPreferenceFlow
        ) { workouts: List<Workout>, userPreferences: UserPreferences ->
            _sortOrder.value = userPreferences.sortOrder.name

            return@combine FavouriteUiModel(
                workouts = sortedWorkouts(workouts, userPreferences.sortOrder),
                sortOrder = userPreferences.sortOrder
            )
        }

    val favouriteUiModel: LiveData<FavouriteUiModel> = favouriteUiModelFlow.asLiveData()

    fun updateSortOrder(order: String) {
        viewModelScope.launch {
            userPreferenceRepository.setSortOrder(order)
        }
    }

    /**
     * Helper function to sort the workouts.
     */
    private fun sortedWorkouts(workouts: List<Workout>, sortOrder: SortOrder): List<Workout> {
        return when (sortOrder) {
            SortOrder.BY_TITLE -> workouts.sortedBy { it.title }
            SortOrder.BY_DATE -> workouts.sortedByDescending { it.timeSaved }
            SortOrder.BY_TIME -> workouts.sortedBy { it.time }
            SortOrder.BY_CATEGORY -> workouts.sortedBy { it.category }
        }
    }

    data class FavouriteUiModel(
        val workouts: List<Workout>,
        val sortOrder: SortOrder
    )
}