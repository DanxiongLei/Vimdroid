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
const constants_1 = require("../constants");
const logger_1 = require("../util/logger");
class DeviceBase {
    create() {
        return __awaiter(this, void 0, void 0, function* () {
            return yield this.onCreate();
        });
    }
    onCreate() {
        return __awaiter(this, void 0, void 0, function* () {
            return true;
        });
    }
    finish() {
    }
    static __communicate__(socket, id, entity = "") {
        socket.setEncoding("utf8");
        socket.setNoDelay(true);
        socket.write(`${id}\n${entity}\n`);
        return new Promise((resolve, reject) => {
            let response = "";
            socket.on("data", function (data) {
                response += data;
                if (response.endsWith("\n")) {
                    socket.end();
                }
            });
            socket.on("error", function (err) {
                reject(err);
            });
            socket.on("end", function () {
                let json;
                try {
                    json = JSON.parse(response);
                }
                catch (err) {
                    logger_1.default.log("JSON.parse error", response);
                    throw err;
                }
                resolve(json);
            });
        });
    }
}
exports.DeviceBase = DeviceBase;
class ProtocolBase {
    constructor(device) {
        this.mDevice = device;
    }
    _send_(id, entity) {
        logger_1.default.log("__send__ id:", id);
        if (typeof entity === 'object') {
            entity = JSON.stringify(entity);
        }
        return this.mDevice.communicate(id, entity);
    }
    ping() {
        return this._send_(constants_1.default.CMD_ID.ping);
    }
    sendKeypress(key) {
        return this._send_(constants_1.default.CMD_ID.keyboard, key);
    }
    shutdown() {
        return this._send_(constants_1.default.CMD_ID.shutdown);
    }
}
exports.ProtocolBase = ProtocolBase;
