package rs.kitten.buggy

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rs.kitten.buggy.ui.theme.BuggyTheme
import rs.kitten.buggy.BuildConfig;

class ApplicationInformation : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuggyTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )
                Scaffold(
                    topBar = {
                        TopBar(
                            scrollBehavior = scrollBehavior,
                            title = "Information"
                        )
                    }
                ) { paddingValues ->
                    Info(modifier = Modifier, paddingValues)
                }
            }
        }
    }

    @Composable
    fun Info(modifier: Modifier = Modifier, paddingValues: PaddingValues) {
        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp
            )) {
            item() {
                Text(
                    text = "Build Info",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Application ID: ${BuildConfig.APPLICATION_ID}\n" +
                            "Build Version: ${BuildConfig.VERSION_CODE}\n" +
                            "Build Name: ${BuildConfig.VERSION_NAME}\n" +
                            "Build Type: ${BuildConfig.BUILD_TYPE}\n" +
                            "Build Date: ${BuildConfig.BUILD_TIME}\n" +
                            "Is Debug: ${BuildConfig.DEBUG}",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item() {
                Text(
                    text = "Contact",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Github: Github.com/Portablefire22\n" +
                            "Website: Kitten.rs\n",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        BuggyTheme {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                state = rememberTopAppBarState()
            )
            Scaffold(
                topBar = {
                    TopBar(
                        scrollBehavior = scrollBehavior,
                        title = "Select a Bluetooth Device"
                    )
                }
            ) { paddingValues ->
                paddingValues
            }
        }
    }
}