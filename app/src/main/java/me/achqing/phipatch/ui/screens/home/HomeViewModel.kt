package me.achqing.phipatch.ui.screens.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
}

data class ReleaseInfo(
    val tag: String,
    val publishedAt: String,
    val name: String,
    val body: String
)