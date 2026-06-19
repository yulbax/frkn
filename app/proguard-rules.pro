# ───────────────────────── sing-box / libbox (Go Mobile) ─────────────────────────
# libbox.aar ships consumer rules for go.**/libbox.**, but be explicit anyway.
-keep class go.** { *; }
-keep class libbox.** { *; }

# Our libbox PlatformInterface / CommandServerHandler implementations are invoked
# from native Go via JNI; their method names/signatures must survive. The vpn
# package is small, so keep it wholesale (covers FrknVpnService, FrknPlatformInterface,
# DefaultNetworkMonitor, SocketProtector, StringArray, ConfigBuilder, …).
-keep class cz.cvut.fit.abdulafz.frkn.vpn.** { *; }

# ───────────────────────── byedpi (JNI, name-based lookup) ─────────────────────────
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keep class cz.cvut.fit.abdulafz.frkn.engine.ByeDpi { *; }

# ───────────────────────── kotlinx.serialization ─────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keep,includedescriptorclasses class cz.cvut.fit.abdulafz.frkn.**$$serializer { *; }
-keepclassmembers class cz.cvut.fit.abdulafz.frkn.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# ───────────────────────── Room ─────────────────────────
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep @androidx.room.Entity class cz.cvut.fit.abdulafz.frkn.** { *; }
-dontwarn androidx.room.paging.**

# ───────────────────────── Ktor (engine resolved via reflection/ServiceLoader) ─────────────────────────
-keep class io.ktor.client.engine.android.** { *; }
-dontwarn io.ktor.**
-dontwarn org.slf4j.**

# ───────────────────────── misc ─────────────────────────
-dontwarn kotlinx.coroutines.**
