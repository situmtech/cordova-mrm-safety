<p align="center">
  <img width="233" src="https://situm.com/wp-content/themes/situm/img/logo-situm.svg" style="margin-bottom:1rem" />
  <h1 align="center">cordova-mrm-safety example</h1>
</p>

<p align="center" style="text-align:center">
Reference Cordova app to validate `@situm/cordova` + `cordova-mrm-safety` integration,
including login, positioning, inertial detection events and background behavior.
</p>

<div align="center" style="text-align:center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![Cordova](https://img.shields.io/badge/Cordova-Android-blueviolet)
![Example](https://img.shields.io/badge/type-example-blue)

</div>

## Goal

Validate this end-to-end flow:

1. Situm login
2. Start positioning
3. Start inertial detectors
4. Receive events (`tap`, `fall`, `inactivity`, `error`)

## Requirements

- Node.js + npm
- Cordova CLI (`npm i -g cordova`)
- Android SDK + JDK
- Access to Artifactory repository hosting `es.situm:inertial-events-detection:1.4.0` in `libs-release-local`

## Step-by-step setup (from scratch)

1. Create a Cordova app:

```bash
cordova create example-app com.situm.inertial.demo InertialEventsDemo
cd example-app
cordova platform add android
```

2. Copy this example web content:

- `../example/www/*` -> `www/`
- `../example/config.xml` -> `config.xml`

3. Install plugins:

```bash
cordova plugin add @situm/cordova
cordova plugin add ../cordova-mrm-safety \
  --variable INERTIAL_EVENTS_VERSION=1.4.0 \
  --variable INERTIAL_EVENTS_MAVEN_REPO=https://repo.situm.es/artifactory/libs-release-local
```

4. Build and run:

```bash
cordova build android
cordova run android
```

If Android platform was already added before updating `config.xml`, re-add it once:

```bash
cordova platform rm android
cordova platform add android
```

## How to use the app

In the app UI:

1. Enter Situm email/password and tap `Login`.
2. (Optional) enter `buildingIdentifier`.
3. Tap `Start Positioning`.
4. Start detectors (`Start Tap`, `Start Fall`, `Start Inactivity`).
5. Check the `Event log`.

Status indicators:

- Positioning: gray/orange/green/red

To stop:

- `Stop Tap/Fall/Inactivity`
- `Stop Positioning`

## Background test

1. Set `Background mode` to `on`.
2. Start detector(s).
3. Lock screen and validate event behavior.

When an inertial event is received, the example triggers a short vibration (JS-side debug feedback).

Notes:

- Android shows a persistent notification while foreground service is active.
- If app task is removed from recents, service stops.

## Troubleshooting

1. Artifactory dependency error
   - Verify plugin variables used at install time (`INERTIAL_EVENTS_VERSION`, `INERTIAL_EVENTS_MAVEN_REPO`).

2. No inertial events
   - Verify Situm positioning is running (`Start Positioning` with no errors).

3. Android runtime permission errors
   - Grant location/BLE permissions.

---

## License

This project is licensed under the MIT license.

---

## More information

More info is available at our [Developers Page](https://situm.com/docs/01-introduction/).

---

## Support information

For any question or bug report, please send an email to [support@situm.com](mailto:support@situm.com).
