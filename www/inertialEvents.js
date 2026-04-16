var exec = require("cordova/exec");

var listener = null;
var nativeListenerRegistered = false;

function ensureNativeListener() {
    if (nativeListenerRegistered) {
        return;
    }

    exec(
        function (event) {
            if (listener) {
                listener(event);
            }
        },
        function (err) {
            console.error("InertialEvents listener error", err);
        },
        "InertialEventsPlugin",
        "setListener",
        []
    );

    nativeListenerRegistered = true;
}

function call(action, args, success, error) {
    exec(success || function () {}, error || function () {}, "InertialEventsPlugin", action, args || []);
}

module.exports = {
    onEvent: function (callback) {
        listener = callback;
        ensureNativeListener();
    },

    offEvent: function (success, error) {
        listener = null;
        nativeListenerRegistered = false;
        call("clearListener", [], success, error);
    },

    startTap: function (config, success, error) {
        call("startTap", [config || {}], success, error);
    },

    stopTap: function (success, error) {
        call("stopTap", [], success, error);
    },

    startFall: function (config, success, error) {
        call("startFall", [config || {}], success, error);
    },

    stopFall: function (success, error) {
        call("stopFall", [], success, error);
    },

    startInactivity: function (config, success, error) {
        call("startInactivity", [config || {}], success, error);
    },

    stopInactivity: function (success, error) {
        call("stopInactivity", [], success, error);
    },

    enableBackground: function (success, error) {
        call("enableBackground", [], success, error);
    },

    disableBackground: function (success, error) {
      call("disableBackground", [], success, error);
    },

    stopAll: function (success, error) {
      call("stopAll", [], success, error);
    }
};
