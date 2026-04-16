# Keep Room-generated database impls (they extend the abstract RoomDatabase subclass)
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Keep Kotlin metadata for reflection-heavy Compose internals
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Biometric
-keep class androidx.biometric.** { *; }
