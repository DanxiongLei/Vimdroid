"use strict";
const keypress = require("keypress");
const logger = require("../util/logger");
function emit(callback) {
    keypress(process.stdin);
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
