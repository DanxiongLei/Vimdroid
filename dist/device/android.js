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
const base_1 = require("./base");
const error_1 = require("../util/error");
const logger_1 = require("../util/logger");
const constants_1 = require("../constants");
const path = require("path");
const index_1 = require("../index");
const adb = require('adbkit');
class AndroidDevice extends base_1.DeviceBase {
    constructor(ui) {
        super();
        this.initialized = false;
        this.protocol = new AndroidProtocol(this);
        this.userInterface = ui;
    }
    registerCallback(callback) {
        this.uiCallback = callback;
    }
    initialize() {
        return __awaiter(this, void 0, void 0, function* () {
            logger_1.default.log(`initialize`);
            let device = yield this.chooseDevice();
            if (!device) {
                return false;
            }
            this.device = device;
            yield this.prepareSubcore();
            yield this.establishConnect();
            yield this.ensurePrepared();
            return true;
        });
    }
    ensurePrepared() {
        return __awaiter(this, void 0, void 0, function* () {
            let prepared = yield this.protocol.ensurePrepared();
            logger_1.default.log("ensurePrepared ", prepared);
            switch (prepared.status) {
                case constants_1.default.RESP_STATUS.FAILURE:
                    if (this.uiCallback) {
                        this.uiCallback.failure(prepared.data);
                    }
                    break;
                case constants_1.default.RESP_STATUS.FAILURE_NOT_FATAL:
                    if (this.uiCallback) {
                        this.uiCallback.message(prepared.data);
                    }
                    break;
                case constants_1.default.RESP_STATUS.OK:
                    this.initialized = true;
                    if (this.uiCallback) {
                        this.uiCallback.allPreparedSuccessfully();
                    }
                    break;
                default:
            }
        });
    }
    establishConnect() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.uiCallback) {
                this.uiCallback.tryingToEstablishConnect();
            }
            const client = AndroidDevice.getAdbClient();
            client.shell(this.device.id, "am instrument -w" +
                " -e class \"com.damonlei.vimdroid.Initializer#startup\"" +
                " com.damonlei.vimdroid.test/" +
                "android.support.test.runner.AndroidJUnitRunner").then(adb.util.readAll).then((output) => {
                logger_1.default.log("run instrument finished", output.toString().trim());
            }).catch(err => {
                logger_1.default.error(err);
                throw err;
            });
            let resp = yield this.tryToPingRetry(3, 1000);
            logger_1.default.log("Ping received", resp);
            if (resp.status !== constants_1.default.RESP_STATUS.OK) {
                throw new error_1.SubcoreError(resp.data);
            }
        });
    }
    tryToPingRetry(retry, timeout) {
        let _resolve, _reject;
        let self = this;
        function handler() {
            return __awaiter(this, void 0, void 0, function* () {
                let resp = yield self.tryToPing();
                if (resp) {
                    _resolve(resp);
                    return;
                }
                if (retry > 0) {
                    retry--;
                    setTimeout(handler, timeout);
                }
                else {
                    _reject(new error_1.SubcoreError("Could not connect with mobile."));
                }
            });
        }
        return new Promise((resolve, reject) => {
            _resolve = resolve;
            _reject = reject;
            setTimeout(handler, timeout);
        });
    }
    tryToPing() {
        return __awaiter(this, void 0, void 0, function* () {
            let resp;
            try {
                resp = yield this.protocol.ping();
            }
            catch (e) {
                logger_1.default.error(e, "Trying to ping failed.");
                return null;
            }
            if (resp.status !== constants_1.default.RESP_STATUS.OK) {
                logger_1.default.log("Trying to ping failed.", resp.data);
                return resp;
            }
            return resp;
        });
    }
    prepareSubcore() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.uiCallback) {
                this.uiCallback.tryingToPrepareSubcore();
            }
            const client = AndroidDevice.getAdbClient();
            if (index_1.Config.FORCE_INSTALL_SUBCORE || !(yield client.isInstalled(this.device.id, constants_1.default.PACKAGE_NAME))) {
                if (this.uiCallback) {
                    this.uiCallback.tryingToInstallSubcore();
                }
                yield this.installSubcore();
            }
        });
    }
    installSubcore() {
        return __awaiter(this, void 0, void 0, function* () {
            yield AndroidDevice.getAdbClient().install(this.device.id, path.join(__dirname, "../res/apk/app.apk"));
            yield AndroidDevice.getAdbClient().install(this.device.id, path.join(__dirname, "../res/apk/app-server.apk"));
        });
    }
    chooseDevice() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.uiCallback) {
                this.uiCallback.tryingToSearchDevice();
            }
            const client = AndroidDevice.getAdbClient();
            let devices;
            try {
                devices = yield AndroidDevice.getAdbDevices(client);
            }
            catch (e) {
                logger_1.default.error(e, AndroidConnectError.name);
                throw new AndroidConnectError();
            }
            yield AndroidDevice.insertDeviceName(client, devices);
            return yield this.chooseOneDeviceFromArray(devices);
        });
    }
    chooseOneDeviceFromArray(devices) {
        return __awaiter(this, void 0, void 0, function* () {
            let hitDevice;
            if (devices.length > 1) {
                let deviceId = yield this.userInterface.makeUserChoose(AndroidDevice.adaptUIDevice(devices));
                for (let device of devices) {
                    if (device.id === deviceId) {
                        hitDevice = device;
                        break;
                    }
                }
            }
            else if (devices.length === 1) {
                hitDevice = devices[0];
            }
            else {
                throw new Error("Devices length === 0");
            }
            return hitDevice;
        });
    }
    static insertDeviceName(client, devices) {
        return __awaiter(this, void 0, void 0, function* () {
            for (let item of devices) {
                let properties = yield client.getProperties(item.id);
                item.name = properties['ro.product.name'];
            }
            return devices;
        });
    }
    static adaptUIDevice(devices) {
        let result = [];
        for (let item of devices) {
            result.push({ id: item.id, name: `${item.name} [${item.type}]` });
        }
        return result;
    }
    static getAdbDevices(client) {
        return __awaiter(this, void 0, void 0, function* () {
            const devices = yield client.listDevices();
            if (!devices) {
                throw new Error("Devices got from adb is empty");
            }
            let hit = false;
            for (let item of devices) {
                if (item.type != "offline") {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                throw new Error("All devices adb got is offline");
            }
            return devices;
        });
    }
    static getAdbClient() {
        if (AndroidDevice.adbClient == null) {
            logger_1.default.log(`create adbClient`);
            AndroidDevice.adbClient = adb.createClient();
        }
        return AndroidDevice.adbClient;
    }
    isInitialized() {
        return this.initialized;
    }
    isConnected() {
        return this.protocol.ping();
    }
    communicate(id, entity) {
        return __awaiter(this, void 0, void 0, function* () {
            const socket = yield AndroidDevice.getAdbClient().openTcp(this.device.id, constants_1.default.DEVICE_PORT);
            return base_1.DeviceBase.__communicate__(socket, id, entity);
        });
    }
}
exports.AndroidDevice = AndroidDevice;
class AndroidConnectError extends error_1.BaseError {
}
exports.AndroidConnectError = AndroidConnectError;
class AndroidProtocol extends base_1.ProtocolBase {
    ensurePrepared() {
        return this._send_(constants_1.default.CMD_ID.ensurePrepared);
    }
}
