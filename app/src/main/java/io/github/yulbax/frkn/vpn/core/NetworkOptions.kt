package io.github.yulbax.frkn.vpn.core

data class NetworkOptions(
    val tunStack: String = "gvisor",
    val mtu: Int = 9000,
    val ipv6Mode: String = "disable",
    val dnsRemote: String = "1.1.1.1",
    val dnsDirect: String = "1.1.1.1",
    val sniff: Boolean = true,
    val bypassLan: Boolean = false
)
