package com.github.pvkvetkin.android.android_chat_project.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.pvkvetkin.android.android_chat_project.viewmodel.UserAuthFlowViewModel
import com.github.pvkvetkin.android.android_chat_project.viewmodel.MainViewModel

@Composable
fun RootScreen(userAuthFlowViewModel: UserAuthFlowViewModel, mainViewModel: MainViewModel) {
    val authState by userAuthFlowViewModel.flowState.collectAsState()
    when (authState) {
        is UserAuthFlowViewModel.AuthState.Active -> HomeScreen(mainViewModel)
        else -> SignInScreen(userAuthFlowViewModel)
    }
}
