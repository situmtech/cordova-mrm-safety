function log(message) {
  var el = document.getElementById("log");
  var ts = new Date().toLocaleTimeString();
  el.textContent += "[" + ts + "] " + message + "\n";
}

function situmPlugin() {
  return window.cordova && cordova.plugins && cordova.plugins.Situm ? cordova.plugins.Situm : null;
}

var appState = {
  positioningState: "stopped"
};

function setDotClass(element, className) {
  element.classList.remove("status-green", "status-orange", "status-red", "status-gray");
  element.classList.add(className);
}

function updateStatusIndicators() {
  var positioningText = document.getElementById("positioningStatusText");
  var positioningDot = document.getElementById("positioningStatusDot");

  if (appState.positioningState === "running") {
    positioningText.textContent = "running";
    setDotClass(positioningDot, "status-green");
  } else if (appState.positioningState === "starting") {
    positioningText.textContent = "starting";
    setDotClass(positioningDot, "status-orange");
  } else if (appState.positioningState === "error") {
    positioningText.textContent = "error";
    setDotClass(positioningDot, "status-red");
  } else {
    positioningText.textContent = "stopped";
    setDotClass(positioningDot, "status-gray");
  }

}

function onPositioningRunning() {
  appState.positioningState = "running";
  updateStatusIndicators();
}

function onPositioningError() {
  appState.positioningState = "error";
  updateStatusIndicators();
}

function vibrateOnInertialEvent() {
  if (navigator && typeof navigator.vibrate === "function") {
    navigator.vibrate(250);
  }
}

function updateBackgroundMode() {
  var mode = document.getElementById("backgroundMode").value;
  if (mode === "on") {
    window.InertialEvents.enableBackground(
      function () { log("Background mode enabled"); },
      function (err) { log("Error enabling background: " + JSON.stringify(err)); }
    );
  } else {
    window.InertialEvents.disableBackground(
      function () { log("Background mode disabled"); },
      function (err) { log("Error disabling background: " + JSON.stringify(err)); }
    );
  }
}

function registerSitumCallbacks() {
  var situm = situmPlugin();
  if (!situm) {
    log("Situm plugin not available");
    return;
  }

  situm.onLocationStatus(function (status) {
    log("Situm status: " + status);
    var normalized = String(status || "").toUpperCase();
    if (normalized.indexOf("STOP") >= 0) {
      appState.positioningState = "stopped";
      updateStatusIndicators();
      return;
    }
    if (normalized.indexOf("ERROR") >= 0) {
      onPositioningError();
      return;
    }
    onPositioningRunning();
  });

  situm.onLocationError(function (error) {
    log("Situm error: " + JSON.stringify(error));
    onPositioningError();
  });

  situm.onLocationUpdate(function (location) {
    if (location && location.position) {
      log("Situm location update");
      onPositioningRunning();
    }
  });

  situm.enableUserHelper(
    function () {
      log("Situm user helper enabled");
    },
    function (error) {
      log("Situm user helper error: " + JSON.stringify(error));
    }
  );
}

function loginSitum() {
  var situm = situmPlugin();
  if (!situm) {
    log("Situm plugin not available");
    return;
  }

  var email = document.getElementById("situmEmail").value.trim();
  var password = document.getElementById("situmPassword").value.trim();

  if (!email || !password) {
    log("Provide Situm email and password first");
    return;
  }

  situm.setUserPass(
    email,
    password,
    function () {
      log("Situm login success");
    },
    function (error) {
      log("Situm login error: " + JSON.stringify(error));
    }
  );
}

function startSitumPositioning() {
  var situm = situmPlugin();
  if (!situm) {
    log("Situm plugin not available");
    return;
  }

  var buildingIdentifier = document.getElementById("buildingIdentifier").value.trim();
  var request = {};
  if (buildingIdentifier) {
    request.buildingIdentifier = buildingIdentifier;
  }

  try {
    appState.positioningState = "starting";
    updateStatusIndicators();
    situm.requestLocationUpdates(request);
    log("Situm positioning requested");
  } catch (error) {
    onPositioningError();
    log("Error requesting Situm positioning: " + String(error));
  }
}

function stopSitumPositioning() {
  var situm = situmPlugin();
  if (!situm) {
    log("Situm plugin not available");
    return;
  }

  situm.removeUpdates()
    .then(function () {
      appState.positioningState = "stopped";
      updateStatusIndicators();
      log("Situm positioning stopped");
    })
    .catch(function (error) {
      onPositioningError();
      log("Error stopping Situm positioning: " + JSON.stringify(error));
    });
}

document.addEventListener("deviceready", function () {
  log("Device ready");

  registerSitumCallbacks();
  updateStatusIndicators();

  window.InertialEvents.onEvent(function (event) {
    if (event.type === "error") {
      log("Inertial error: " + event.message);
      return;
    }
    log("Inertial event: " + event.type + " @" + event.timestamp);
    vibrateOnInertialEvent();
  });

  document.getElementById("situmLogin").addEventListener("click", loginSitum);
  document.getElementById("startPositioning").addEventListener("click", startSitumPositioning);
  document.getElementById("stopPositioning").addEventListener("click", stopSitumPositioning);
  document.getElementById("backgroundMode").addEventListener("change", updateBackgroundMode);

  document.getElementById("startTap").addEventListener("click", function () {
    if (appState.positioningState !== "running") {
      log("Tap detector started without active positioning. Events will be filtered.");
    }
    var taps = parseInt(document.getElementById("tapTaps").value, 10);
    var sensitivity = parseInt(document.getElementById("tapSensitivity").value, 10);
    window.InertialEvents.startTap({ taps: taps, sensitivity: sensitivity }, function () {
      log("Tap started");
    }, function (err) {
      log("Error startTap: " + JSON.stringify(err));
    });
  });

  document.getElementById("stopTap").addEventListener("click", function () {
    window.InertialEvents.stopTap(function () {
      log("Tap stopped");
    }, function (err) {
      log("Error stopTap: " + JSON.stringify(err));
    });
  });

  document.getElementById("startFall").addEventListener("click", function () {
    if (appState.positioningState !== "running") {
      log("Fall detector started without active positioning. Events will be filtered.");
    }
    var sensitivity = parseFloat(document.getElementById("fallSensitivity").value);
    var lieTimeSec = parseFloat(document.getElementById("fallLieTime").value);
    window.InertialEvents.startFall({ sensitivity: sensitivity, lieTimeSec: lieTimeSec }, function () {
      log("Fall started");
    }, function (err) {
      log("Error startFall: " + JSON.stringify(err));
    });
  });

  document.getElementById("stopFall").addEventListener("click", function () {
    window.InertialEvents.stopFall(function () {
      log("Fall stopped");
    }, function (err) {
      log("Error stopFall: " + JSON.stringify(err));
    });
  });

  document.getElementById("startInactivity").addEventListener("click", function () {
    if (appState.positioningState !== "running") {
      log("Inactivity detector started without active positioning. Events will be filtered.");
    }
    var sensitivity = parseFloat(document.getElementById("inactivitySensitivity").value);
    var idleTimeSec = parseFloat(document.getElementById("inactivityIdleTime").value);
    var ignoreHorizontal = document.getElementById("ignoreHorizontal").value === "true";

    window.InertialEvents.startInactivity({
      sensitivity: sensitivity,
      idleTimeSec: idleTimeSec,
      ignoreHorizontal: ignoreHorizontal
    }, function () {
      log("Inactivity started");
    }, function (err) {
      log("Error startInactivity: " + JSON.stringify(err));
    });
  });

  document.getElementById("stopInactivity").addEventListener("click", function () {
    window.InertialEvents.stopInactivity(function () {
      log("Inactivity stopped");
    }, function (err) {
      log("Error stopInactivity: " + JSON.stringify(err));
    });
  });

  document.getElementById("stopAll").addEventListener("click", function () {
    window.InertialEvents.stopAll(function () {
      log("All detectors stopped");
    }, function (err) {
      log("Error stopAll: " + JSON.stringify(err));
    });
  });

  document.getElementById("clearLog").addEventListener("click", function () {
    document.getElementById("log").textContent = "";
  });
});
