<p align="center">
  <img width="233" src="https://situm.com/wp-content/themes/situm/img/logo-situm.svg" style="margin-bottom:1rem" />
  <h1 align="center">cordova-mrm-safety example</h1>
</p>

<p align="center" style="text-align:center">
Reference Cordova app to validate `@situm/cordova` + `@situm/cordova-mrm-safety` integration,
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

## Recommended folder structure

Create the host Cordova app **outside** this plugin repository.

```text
/Users/you/repos/
  cordova-mrm-safety/        # this plugin repo
  inertial-example-app/      # host Cordova app (separate folder)
```

Do not create the host app inside `cordova-mrm-safety` (for example `cordova-mrm-safety/example-app`),
because local plugin installation can fail with copy/subdirectory recursion errors.

## Step-by-step setup (from scratch)

1. Create a Cordova app in a separate folder:

```bash
mkdir -p /Users/you/repos/inertial-example-app
cd /Users/you/repos/inertial-example-app
cordova create . com.situm.inertial.demo InertialEventsDemo
cordova platform add android
```

2. Copy this example web content from the plugin repository:

```bash
cp -R /Users/you/repos/cordova-mrm-safety/example/www/* ./www/
cp /Users/you/repos/cordova-mrm-safety/example/config.xml ./config.xml
```

3. Install plugins:

```bash
cordova plugin add @situm/cordova
cordova plugin add @situm/cordova-mrm-safety
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

1. No inertial events
   - Verify Situm positioning is running (`Start Positioning` with no errors).

2. Android runtime permission errors
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
