package com.github.pvkvetkin.android.android_chat_project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.github.pvkvetkin.android.android_chat_project.viewmodel.UserAuthFlowViewModel

@Composable
fun SignInScreen(userAuthFlowViewModel: UserAuthFlowViewModel) {
    val authState by userAuthFlowViewModel.flowState.collectAsState()
    var nickname by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Chat Messenger",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = secretKey,
                onValueChange = { secretKey = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { userAuthFlowViewModel.registerAccount(nickname) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create New Account")
                }
                Button(
                    onClick = { userAuthFlowViewModel.authorizeUser(nickname, secretKey) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            when (authState) {
                is UserAuthFlowViewModel.AuthState.Created -> {
                    Text("Your account has been created! Secret Key: ${(authState as UserAuthFlowViewModel.AuthState.Created).secret}")
                }
                is UserAuthFlowViewModel.AuthState.Active -> {
                    Text("Access granted! Session Token: ${(authState as UserAuthFlowViewModel.AuthState.Active).token}")
                }
                is UserAuthFlowViewModel.AuthState.Failure -> {
                    Text("Uh-oh! Something went wrong: ${(authState as UserAuthFlowViewModel.AuthState.Failure).reason}")
                }
                else -> {}
            }
        }
    }
}
