"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const android_1 = require("./device/android");
const keypress = require("./io/keypress");
const logger_1 = require("./util/logger");
const string_1 = require("./res/string");
exports.Config = {
    DEBUG: false,
    FORCE_INSTALL_SUBCORE: false
};
function start(ui, callback) {
    return __awaiter(this, void 0, void 0, function* () {
        keypress.emitKeypressEvents(keyObserve);
        exports.androidDevice = new android_1.AndroidDevice(ui);
        exports.androidDevice.registerCallback(callback);
        let result = yield exports.androidDevice.initialize();
        logger_1.default.log(result);
        return result;
    });
}
exports.start = start;
function keyObserve(key) {
    return __awaiter(this, void 0, void 0, function* () {
        logger_1.default.log("receive keyEvent : ", key);
        if (key.ctrl && key.name === 'c') {
            return;
        }
        let resp = yield exports.androidDevice.protocol.sendKeypress(key);
        logger_1.default.log(`receive resp(${JSON.stringify(resp)}) for keyEvent(${key.name})`);
    });
}
function terminate(outputNormal, outputErr) {
    outputNormal(string_1.strings.uiTerminate);
    process.stdin.removeAllListeners("keypress");
    logger_1.default.log("CLI.terminate");
    exports.androidDevice.protocol.shutdown().then(() => {
        process.exit(0);
    }).catch(err => {
        outputErr(err);
        process.exit(0);
    });
    exports.androidDevice = null;
}
exports.terminate = terminate;
function settings() {
    return __awaiter(this, void 0, void 0, function* () {
    });
}
exports.settings = settings;
