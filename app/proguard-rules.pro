# Proguard rules for Masareefi
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
