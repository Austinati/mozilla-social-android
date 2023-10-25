package org.mozilla.social.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.social.common.Resource
import org.mozilla.social.core.data.repository.RecommendationRepository
import org.mozilla.social.model.Recommendation
import timber.log.Timber

class DiscoverViewModel(
    private val recommendationRepository: RecommendationRepository,
) : ViewModel(), DiscoverInteractions {

    private val _recommendations = MutableStateFlow<Resource<List<Recommendation>>>(Resource.Loading())
    val recommendations = _recommendations.asStateFlow()

    init {
        getRecs()
    }

    private fun getRecs() {
        _recommendations.update { Resource.Loading() }
        viewModelScope.launch {
            try {
                _recommendations.update {
                    Resource.Loaded(recommendationRepository.getRecommendations())
                }
            } catch (e: Exception) {
                Timber.e(e)
                _recommendations.update { Resource.Error(e) }
            }
        }
    }

    override fun onRetryClicked() {
        getRecs()
    }
}