package com.wwf.projectmanagement.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wwf.projectmanagement.ui.theme.WwfTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {}
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WwfTheme { HomeScreen() }
}
