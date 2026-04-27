<p align="center">
  <img width="233" src="https://situm.com/wp-content/themes/situm/img/logo-situm.svg" style="margin-bottom:1rem" />
  <h1 align="center">@situm/cordova-mrm-safety</h1>
</p>

<p align="center" style="text-align:center">
Situm inertial safety module for Cordova. It provides Tap, Fall and Inactivity detection,
designed to work together with active Situm positioning.
</p>

<div align="center" style="text-align:center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![Cordova](https://img.shields.io/badge/Cordova-Android-blueviolet)
![Latest version:](https://img.shields.io/npm/v/@situm/react-native/latest)

</div>

## Getting Started

This plugin is intended to be integrated into a Cordova Android app together with `@situm/cordova`.
The basic setup is described below.

## Running the example

Check the [example README](../example/README.md) to run a complete demo app using this plugin.

## Requirements

- Cordova CLI (`npm i -g cordova`)
- Android platform added to the app (`cordova platform add android`)
- Android SDK + JDK compatible with your Cordova setup
- Situm Cordova plugin installed in host app: `@situm/cordova`

## Install the plugin in a host Cordova app

Run all commands from your host Cordova app root.

1. Install Situm plugin:

```bash
cordova plugin add @situm/cordova
```

2. Install this plugin (Artifactory-backed dependency resolution):

```bash
cordova plugin add @situm/cordova-mrm-safety
```

3. Build Android:

```bash
cordova build android
```

## Minimal usage

Recommended order: start Situm positioning first, then start inertial detectors.

```js
cordova.plugins.Situm.setUserPass(email, password);
cordova.plugins.Situm.requestLocationUpdates({
  buildingIdentifier: "YOUR_BUILDING_ID",
});

window.InertialEvents.onEvent((event) => {
  // event.type: "tap" | "fall" | "inactivity" | "error"
  // event.timestamp: epoch millis
  console.log("Inertial event", event);
});

window.InertialEvents.startTap({ taps: 3, sensitivity: 8 });
window.InertialEvents.startFall({ sensitivity: 0.5, lieTimeSec: 30 });
window.InertialEvents.startInactivity({
  sensitivity: 0.5,
  idleTimeSec: 30,
  ignoreHorizontal: true,
});
```

To stop:

```js
window.InertialEvents.stopTap();
window.InertialEvents.stopFall();
window.InertialEvents.stopInactivity();
window.InertialEvents.stopAll();
cordova.plugins.Situm.removeUpdates();
```

## Important behavior

Detectors may be running, but inertial callbacks are emitted only while Situm positioning is running (`LocationManager.isRunning() == true`).

## Background Mode

To continue detecting emergency events even when the app is in the background, the screen is off, or the app has been minimized, you must enable **Background Mode**.

### What does Background Mode do?

This mode starts a **Foreground Service of type `health`**.

Foreground Services are designed to perform ongoing operations that the user should be aware of. They display a persistent notification, giving the service higher priority so it can reliably detect emergency events in the background.

> [!IMPORTANT]  
> This Foreground Service is used exclusively to detect potential emergencies that users may need to report, even when the app is not actively in use.

### Permissions added automatically

When you add this plugin, the following permissions will be automatically added to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.HIGH_SAMPLING_RATE_SENSORS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_HEALTH" />
```

You don't need to require in runtime the before mentioned permissions as they are granted at install time.

Enable background inertial detection:

```js
window.InertialEvents.enableBackground();
// then startTap/startFall/startInactivity
```

Disable background mode:

```js
window.InertialEvents.disableBackground();
```

Notes:

- Android will show a persistent foreground notification.
- If the app task is removed from recent apps, the service stops.

## Troubleshooting

1. No inertial events are received
   - Verify Situm login succeeded and `requestLocationUpdates(...)` is active.

2. Android permission errors
   - Grant runtime location/BLE permissions.

## Versioning

This package is uploaded to npm as @situm/cordova-mrm-safety. To upload a new version, first make sure to update the "version" parameter at the package.json of the plugin so npm detects is a new version. You can update the plugin version by executing the command:

```bash
npm version patch # To upgrade the patch number of the version
```

Then, execute the following commands:

```bash
npm login # Login with the mobile account
npm pack --dry-run # To check out what will be pushed before actually uploading a new version
npm publish
```

---

## License

This project is licensed under the MIT license.

---

## More information

More info is available at our [Developers Page](https://situm.com/docs/01-introduction/).

---

## Support information

For any question or bug report, please send an email to [support@situm.com](mailto:support@situm.com).
