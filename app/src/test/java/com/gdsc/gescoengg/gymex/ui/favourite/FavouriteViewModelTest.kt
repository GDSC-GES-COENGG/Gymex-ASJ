package com.gdsc.gescoengg.gymex.ui.favourite


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdsc.gescoengg.gymex.repository.UserPreferenceRepository
import com.gdsc.gescoengg.gymex.repository.WorkoutRepository
import com.gdsc.gescoengg.gymex.util.MainCoroutinesRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class FavouriteViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutinesRule = MainCoroutinesRule()

    private lateinit var viewModel: FavouriteViewModel
    private lateinit var repository: WorkoutRepository
    private lateinit var userPreferenceRepository: UserPreferenceRepository

    @Before
    fun setUp() {
        repository = mock(WorkoutRepository::class.java)
        userPreferenceRepository = mock(UserPreferenceRepository::class.java)
        viewModel = FavouriteViewModel(repository, userPreferenceRepository)
    }

    @Test
    fun start_getFavourieWorkouts() {
        verify(repository, atLeastOnce()).getWorkouts()
    }

    @Test
    fun start_getUserSavedSortType() {
        verify(userPreferenceRepository, atLeastOnce()).userPreferencesFlow
    }

    @Test
    fun updateSort_getNewSort() = runBlockingTest {
        viewModel.updateSortOrder("SORT_TEST")
        verify(userPreferenceRepository, atLeastOnce()).setSortOrder("SORT_TEST")
    }
}