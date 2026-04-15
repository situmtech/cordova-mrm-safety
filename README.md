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
![Version](https://img.shields.io/badge/version-1.4.0-blue)

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
cordova plugin add ../cordova-mrm-safety \
  --variable INERTIAL_EVENTS_VERSION=1.4.0 \
  --variable INERTIAL_EVENTS_MAVEN_REPO=https://repo.situm.es/artifactory/libs-release-local
```

3. Build Android:

```bash
cordova build android
```

The plugin defaults already point to:

`https://repo.situm.es/artifactory/libs-release-local`

## Minimal usage

Recommended order: start Situm positioning first, then start inertial detectors.

```js
cordova.plugins.Situm.setUserPass(email, password)
cordova.plugins.Situm.requestLocationUpdates({ buildingIdentifier: "YOUR_BUILDING_ID" })

window.InertialEvents.onEvent((event) => {
  // event.type: "tap" | "fall" | "inactivity" | "error"
  // event.timestamp: epoch millis
  console.log("Inertial event", event)
})

window.InertialEvents.startTap({ taps: 3, sensitivity: 8 })
window.InertialEvents.startFall({ sensitivity: 0.5, lieTimeSec: 30 })
window.InertialEvents.startInactivity({
  sensitivity: 0.5,
  idleTimeSec: 30,
  ignoreHorizontal: true,
})
```

To stop:

```js
window.InertialEvents.stopTap()
window.InertialEvents.stopFall()
window.InertialEvents.stopInactivity()
window.InertialEvents.stopAll()
cordova.plugins.Situm.removeUpdates()
```

## Important behavior

Detectors may be running, but inertial callbacks are emitted only while Situm positioning is running (`LocationManager.isRunning() == true`).

## Background mode

Enable background inertial detection:

```js
window.InertialEvents.enableBackground()
// then startTap/startFall/startInactivity
```

Disable background mode:

```js
window.InertialEvents.disableBackground()
```

Notes:

- Android will show a persistent foreground notification.
- If the app task is removed from recent apps, the service stops.

## Troubleshooting

1. Dependency resolution errors from Artifactory
   - Verify `INERTIAL_EVENTS_VERSION` and `INERTIAL_EVENTS_MAVEN_REPO` values.

2. No inertial events are received
   - Verify Situm login succeeded and `requestLocationUpdates(...)` is active.

3. Android permission errors
   - Grant runtime location/BLE permissions.

---

## License

This project is licensed under the MIT license.

---

## More information

More info is available at our [Developers Page](https://situm.com/docs/01-introduction/).

---

## Support information

For any question or bug report, please send an email to [support@situm.com](mailto:support@situm.com).
