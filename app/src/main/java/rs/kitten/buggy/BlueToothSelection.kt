package rs.kitten.buggy

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import rs.kitten.buggy.ui.theme.BuggyTheme


class BlueToothSelection : ComponentActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val devices = BluetoothDeviceList()

    private var deviceCounter by mutableIntStateOf(0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter  = bluetoothManager.adapter

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) {}

        requestPermissionLauncher.launch("android.permission.BLUETOOTH_SCAN")
        requestPermissionLauncher.launch("android.permission.BLUETOOTH_CONNECT")

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val perm = ContextCompat.checkSelfPermission(this,
            "android.permission.BLUETOOTH_SCAN")

        if (bluetoothAdapter?.isEnabled == false) {
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


        RefreshDevices(this)

        enableEdgeToEdge()
        setContent {
            BuggyTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )
                Scaffold(
                    topBar = { TopBar(scrollBehavior = scrollBehavior,
                        title = "Select a Bluetooth Device") }
                ) { paddingValues ->
                    BTSelectionContent(paddingValues)
                }

                if (bluetoothAdapter == null) {
                    ErrorAlert("Device does not support Bluetooth")
                }
                if (perm == PackageManager.PERMISSION_DENIED) {
                    ErrorAlert("Application was denied Bluetooth. The application will " +
                            "not function.")
                }
            }
        }
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

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BTSelectionContent(paddingValues: PaddingValues) {

        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp
            )
        ) {
            item () {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(intrinsicSize = IntrinsicSize.Max)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    BlueToothRefresh(modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Log.println(Log.DEBUG, "Counter", deviceCounter.toString() + devices.Devices())
            for (i in 0..<deviceCounter) {
                val device = devices.Devices()[i]
                Log.println(Log.INFO, "Q", device.toString())
                item() {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .height(intrinsicSize = IntrinsicSize.Max)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text("$device: " + device.name, modifier = Modifier.padding(16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun BlueToothRefresh(modifier: Modifier = Modifier) {
        val paddingValues = PaddingValues(8.dp)
        val context = LocalContext.current
        var searchStatus = "NULL"
        Row (modifier = Modifier.fillMaxSize()){
            Text(
                modifier = modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(paddingValues),
                text = "Status: $searchStatus"
            )
            Spacer(modifier.weight(1f))
            FilledTonalButton(
                modifier = modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(paddingValues),
                onClick = {
                    if (RefreshDevices(context) < 0) {
                        searchStatus = "Missing permissions"
                    }
                }) {
                Text("Refresh Devices")
            }
        }
    }

    fun RefreshDevices(context: Context): Int {
        val perm = ContextCompat.checkSelfPermission(
            context,
            "android.permission.BLUETOOTH_SCAN")
        if (perm == PackageManager.PERMISSION_GRANTED) {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            var i = 0
            deviceCounter = 0
            devices.Devices().clear()
            pairedDevices?.forEach { device ->
                devices.Insert(device)
                i++
                Log.println(Log.INFO, "rs.kitten.buggy.btDevices", devices.Devices().toString())
            }
            deviceCounter = i
        } else {
            return -1
        }
        return 0
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @PreviewLightDark()
    @Composable
    fun PreviewBT() {
        BuggyTheme {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                state = rememberTopAppBarState()
            )
            Scaffold(
                topBar = { TopBar(scrollBehavior = scrollBehavior,
                    title = "Select a Bluetooth Device") }
            ) { paddingValues ->
                BTSelectionContent(paddingValues)
            }

            if (false) {
                ErrorAlert("Device does not support Bluetooth")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



