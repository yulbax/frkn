<div align="center">

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="192" alt="FRKN icon" />

# FRKN

**A per-app split VPN for Android.**

</div>

FRKN tunnels each installed app independently. Instead of an all-or-nothing VPN switch,
you decide — per app — whether traffic should go through a proxy, through a built-in
DPI-bypass engine, or straight out untouched. All three can run at the same time.

## Features

- **Per-app routing.** Assign every app one of three modes:
  - **Direct** — leaves the device normally, with its real IP and no overhead.
  - **VPN** — tunnelled through your proxy server.
  - **ByeDPI** — routed through a built-in engine that defeats DPI-based censorship
    locally, without any remote server.
- **Wide protocol support.** Import servers from `vless`, `vmess`, `trojan`,
  `shadowsocks` and `hysteria2` share links or a subscription URL.
- **Modern transports.** TCP, WebSocket, gRPC and HTTPUpgrade, with TLS / Reality and
  configurable uTLS fingerprints.
- **Stays out of the way.** No system-wide proxy, no exposed control API, optional
  auto-connect on boot, and a built-in connection health check.

## How it works

FRKN combines two engines behind a single VPN interface:

- **[sing-box](https://github.com/SagerNet/sing-box)** owns the tunnel and dispatches
  traffic per app — *VPN* apps to the upstream server, *ByeDPI* apps to the local bypass
  proxy. *Direct* apps are excluded from the tunnel entirely, so they never touch the
  engine.
- **[ByeDPI](https://github.com/hufrea/byedpi)** runs as an embedded local proxy that
  desynchronizes packets to slip past Deep Packet Inspection.

## Building

FRKN is fully open-source — no Google services, no proprietary dependencies.

```bash
./gradlew assembleDebug
```

Requires the Android SDK, NDK and CMake (the ByeDPI native engine is compiled from source).
A physical device is recommended — Android's VPN APIs are unreliable on emulators.

## Built with

- [sing-box](https://github.com/SagerNet/sing-box) — the VPN core
- [ByeDPI](https://github.com/hufrea/byedpi) — the DPI-bypass engine
- Kotlin, Jetpack Compose, Room, Koin, Ktor

## License

FRKN is licensed under the **GNU General Public License v3.0** — see [`LICENSE`](LICENSE).

ByeDPI retains its own license; see `app/src/main/cpp/byedpi/LICENSE`.
