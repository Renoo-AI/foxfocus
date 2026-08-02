package com.foxfocus.app.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.foxfocus.app.data.repo.FoxRepository
import kotlinx.coroutines.launch

@Composable
fun OnboardingHostScreen(
  repository: FoxRepository,
  onOnboardingComplete: () -> Unit
) {
  var currentStep by remember { mutableIntStateOf(1) }
  val scope = rememberCoroutineScope()

  when (currentStep) {
    1 -> LanguageScreen(onNext = { currentStep = 2 })
    2 -> AgeSegmentationScreen(onNext = { ageYears ->
      scope.launch { repository.updateAgeYears(ageYears) }
      currentStep = 3
    })
    3 -> PrimaryGoalScreen(onNext = { currentStep = 4 })
    4 -> AppSelectionScreen(repository = repository, onNext = { currentStep = 5 })
    5 -> BaselineScreen(onFinish = onOnboardingComplete)
  }
}
