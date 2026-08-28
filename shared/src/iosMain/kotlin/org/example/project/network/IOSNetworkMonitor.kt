@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.example.project.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_get_status
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithAddress
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import kotlinx.cinterop.*
import platform.posix.sockaddr_in
import platform.posix.AF_INET
import platform.posix.sockaddr

class IOSNetworkMonitor : NetworkMonitor {

    private val _isNetworkConnected = MutableStateFlow(currentlyConnected())
    private val monitor = nw_path_monitor_create()

    init {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            _isNetworkConnected.value = (status == nw_path_status_satisfied)
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }

    override fun currentlyConnected(): Boolean {
        return memScoped {
            val zeroAddress = alloc<sockaddr_in>()
            zeroAddress.sin_len = sizeOf<sockaddr_in>().convert()
            zeroAddress.sin_family = AF_INET.convert()

            val reachability = SCNetworkReachabilityCreateWithAddress(null, zeroAddress.ptr.reinterpret<sockaddr>())
                ?: return@memScoped false

            val flags = alloc<UIntVar>()
            if (!SCNetworkReachabilityGetFlags(reachability, flags.ptr)) {
                return@memScoped false
            }

            val flagsValue = flags.value.toInt()
            val isReachable = (flagsValue and kSCNetworkReachabilityFlagsReachable.toInt()) != 0
            val needsConnection = (flagsValue and kSCNetworkReachabilityFlagsConnectionRequired.toInt()) != 0
            isReachable && !needsConnection
        }
    }

    override val isNetworkConnected: Flow<Boolean> = _isNetworkConnected.asStateFlow()
}
