package rs.kitten.buggy

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity


import rs.kitten.buggy.ui.theme.BuggyTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BuggyBluetooth.SetContext(this)

        if (BuggyBluetooth.getAdapter()?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    Log.e("Activity result", "OK")
                    // There are no request codes
                    val data = result.data
                }
            }

            activityResultLauncher.launch(enableBtIntent)
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND) // Create a BroadcastReceiver for ACTION_FOUND.
        registerReceiver(BuggyBluetooth.receiver, filter)

        RequestPermissions()

        enableEdgeToEdge()
        setContent {
            BuggyTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )
                Scaffold(
                    topBar = { TopBar(scrollBehavior = scrollBehavior, title = "Buggy Control") }
                ) { paddingValues ->
                    ScreenContent(paddingValues, this)
                }
            }
        }
    }

    fun RequestPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
            ) {}
        val permissions: Array<String> = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
        requestPermissionLauncher.launch(permissions)
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val device = result.data?.getParcelableExtra("device", BluetoothDevice::class.java)
                if (device != null) {
                    BuggyBluetooth.currentDevice = mutableStateOf(device)
                    updateBluetoothInformation()

                    BuggyBluetooth.connect(device)
                }
            }
        }

    private fun updateBluetoothInformation() {

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun BlueToothOptionsMain(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val paddingValues = PaddingValues(8.dp)

        val device = BuggyBluetooth.currentDevice?.value
        Column (modifier = Modifier.fillMaxSize()){
            Text(
                modifier = modifier.padding(paddingValues),
                text = if (device == null) {
                "No Bluetooth device connected."
            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                "${device.name}\n" +
                        "Address: ${device.address}\n" +
                        "Bond State: " +
                        when (device.bondState) {
                            BluetoothDevice.BOND_BONDING -> "Bonding"
                            BluetoothDevice.BOND_BONDED -> "Bonded"
                            BluetoothDevice.BOND_NONE -> "Disconnected"
                            else -> "null"
                        } + "\n" +
                        "Connected?: ${BluetoothDevice::class.java.getMethod("isConnected").invoke(device)}"
            } else {
                "Application was denied Bluetooth.\nThe application will " +
                        "not function."
            }
            )
            Spacer(modifier.weight(1f))
            FilledTonalButton(
                colors = ButtonDefaults.buttonColors(),
                modifier = modifier.align(Alignment.End)
                    .padding(horizontal = paddingValues.calculateRightPadding(LayoutDirection.Ltr)),
                onClick = {
                    if (device == null) {
                        val intent = Intent(context, BlueToothSelection::class.java)
                        intentLauncher.launch(intent)
                    } else {
                        BuggyBluetooth.disconnect()
                    }
                }) {
                if (device == null) {
                    Text("Select Device")
                } else {
                    Text("Disconnect")
                }
            }
        }
    }

    @Composable
    fun ScreenContent(paddingValues: PaddingValues, context: Context) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ErrorAlert("Application was denied Bluetooth. The application will " +
                    "not function.")
            return
        }

        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp
            )
        ) {
            item(10) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    BlueToothOptionsMain(modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @PreviewLightDark()
    @Composable
    fun Preview() {
        BuggyTheme {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                state = rememberTopAppBarState()
            )
            Scaffold(
                topBar = { TopBar(scrollBehavior = scrollBehavior, title = "Buggy Control") }
            ) { paddingValues ->
                ScreenContent(paddingValues, this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior, title: String) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.7f)
        ),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground.copy(1f),
                fontSize = 17.sp
            )
        },
        actions = {
            IconButton(onClick = {
                expanded = true
            }) { Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More",
            ) }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                DropdownMenuItem(
                    text = { Text("Information") },
                    leadingIcon = {
                        Icon (Icons.Filled.Info, "Information")
                    },
                    onClick = {
                        val intent = Intent(context, ApplicationInformation::class.java)
                        startActivity(context, intent, null)
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorAlert(text: String) {
    val openDialog = remember { mutableStateOf(true) }
    if (!openDialog.value) {
        return
    }
    BasicAlertDialog(
        onDismissRequest = {
            openDialog.value = false
        }
    ){             Surface (
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextButton (
                onClick = { openDialog.value = false },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Confirm")
            }
        }
    }
    }
}