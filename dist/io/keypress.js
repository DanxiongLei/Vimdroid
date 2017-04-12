"use strict";
const readline = require("readline");
const logger = require("../util/logger");
function emit(callback) {
    readline.emitKeypressEvents(process.stdin);
    if (process.stdin.isTTY) {
        process.stdin.setRawMode(true);
        process.stdin.resume();
    }
    if (typeof callback === 'function') {
        process.stdin.on("keypress", (str, key) => {
            callback(key);
        });
    }
}
exports.emitKeypressEvents = emit;
exports.keyEmitter = process.stdin;
