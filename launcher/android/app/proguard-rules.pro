# Keep launcher entry points and config models
-keep class com.grandhorizonrp.launcher.data.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
