package com.wwf.projectmanagement.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.databinding.ViewInfoCardBinding
import com.wwf.projectmanagement.ui.theme.WwfTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.welcome_body), style = MaterialTheme.typography.bodyLarge)

            ComposeCounterCard()

            // XML layout embedded in Compose — the "mixture" approach.
            AndroidViewBinding(
                factory = ViewInfoCardBinding::inflate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                deviceInfo.text = deviceInfo.context.getString(
                    R.string.device_info, Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
                )
            }
        }
    }
}

@Composable
private fun ComposeCounterCard(modifier: Modifier = Modifier) {
    // rememberSaveable survives rotation and process death.
    var count by rememberSaveable { mutableIntStateOf(0) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.compose_section_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.counter_label, count))
            Button(onClick = { count++ }) { Text(stringResource(R.string.tap_me)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WwfTheme { HomeScreen() }
}
