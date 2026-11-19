# ShadeLock

A minimal Android app intended to disable/enable the status bar (notification shade) on managed devices only. It does not change the launcher, hide apps, or request Accessibility permissions.

## Build
Use GitHub Actions workflow `Build ShadeLock APK` (manual Run workflow or push to `main`). Download the `ShadeLock-debug-apk` artifact.

## Device-owner provisioning (advanced; test carefully)
1. Install the APK on a test device.
2. Enable Developer Options and USB debugging.
3. Connect to a computer with Android platform-tools and confirm `adb devices` shows the device as authorized.
4. Provision Device Owner only on a suitable device. This may require the device to be freshly set up with no accounts; consult Android's provisioning requirements and back up first.
5. Command (package/component):

```sh
adb shell dpm set-device-owner com.example.shadelock/.ShadeLockAdminReceiver
```

6. Open ShadeLock, verify it says Device Owner, and test the **Open shade (emergency)** button before enabling the restriction.

## Important
- `setStatusBarDisabled` requires Device Owner; ordinary Device Admin is not enough.
- OEM behavior may vary. The status-bar restriction may behave differently while Lock Task is active.
- This is a debug build, unsigned for release. Test on a spare device before using on a work phone.
- To remove Device Owner, use Android's supported provisioning/removal process; depending on device state, a reset may be needed. Do not enable it unless you understand recovery.
