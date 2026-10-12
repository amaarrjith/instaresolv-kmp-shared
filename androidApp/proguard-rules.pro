# ==============================================================================
# ProGuard / R8 Rules for InstaResolv Production Release
# ==============================================================================

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class * {
    <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class org.example.project.data.model.** { *; }

# --- Ktor Client ---
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-dontwarn io.netty.**
-dontwarn org.slf4j.**

# --- SQLDelight ---
-keep class org.example.project.shared.db.** { *; }
-keep class app.cash.sqldelight.** { *; }

# --- Koin Dependency Injection ---
-keep class io.insert.koin.** { *; }
-dontwarn io.insert.koin.**

# --- Coil Image Loading ---
-keep class coil3.** { *; }
-dontwarn coil3.**

# --- Media3 / ExoPlayer ---
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# --- CanHub Image Cropper & Peekaboo ---
-keep class com.canhub.cropper.** { *; }
-keep class com.preat.peekaboo.** { *; }

# --- Firebase & Google Play Services ---
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
