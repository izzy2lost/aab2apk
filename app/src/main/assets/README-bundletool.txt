This is a placeholder asset named 'bundletool'.

To enable on-device AAB -> APK conversion you must replace this file with a native bundletool executable
built for Android (ARM64 or ARMv7) using GraalVM native-image or a prebuilt binary.

Recommended approach:
1. Build bundletool native image with GraalVM (supports linux/arm64 target) or obtain a prebuilt binary.
2. Rename the binary to 'bundletool' and place it here: app/src/main/assets/bundletool
3. Rebuild the app. On first run the app will copy the asset to its internal files directory and mark it executable.
4. The UI will then be able to run bundletool to convert AAB -> APKS.

If you want, I can help produce a GraalVM build script for bundletool or attempt to include a tested ARM64 binary.
