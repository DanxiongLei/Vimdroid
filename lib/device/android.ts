/**
 * Created by ldx on 2017/4/7.
 */
import {DeviceBase, ProtocolBase, Resp} from "./base";
import {BaseError, SubcoreError} from "../util/error";
import logger from "../util/logger";
import {UIDevice, UserInterface} from "../user-interface";
import constants from "../constants";
import * as path from "path";
import {Config} from "../index";

const adb = require('adbkit');

export class AndroidDevice extends DeviceBase {

    private static adbClient: any;
    private device: AndroidDeviceModel;

    private uiCallback: AndroidCallback;

    private userInterface: UserInterface;

    private initialized: boolean = false;

    protocol: AndroidProtocol;

    constructor(ui) {
        super();
        this.protocol = new AndroidProtocol(this);
        this.userInterface = ui;
    }

    registerCallback(callback: AndroidCallback) {
        this.uiCallback = callback;
    }

    /**
     * 1. 初始化
     * 2. 搜索设备
     * 3. 安装子程序
     * 4. 建立并测试连接
     * 5. 成功/失败
     */
    async initialize() {
        logger.log(`initialize`);
        // 搜索设备
        let device = await this.chooseDevice();
        if (!device) {
            // 用户取消
            return false;
        }
        this.device = device;
        // 安装子程序并建立连接
        await this.prepareSubcore();
        await this.establishConnect();
        await this.ensurePrepared();
        return true;
    }

    private async ensurePrepared() {
        let prepared = await this.protocol.ensurePrepared();
        logger.log("ensurePrepared ", prepared);
        switch (prepared.status) {
            case constants.RESP_STATUS.FAILURE:
                if (this.uiCallback) {
                    this.uiCallback.failure(prepared.data);
                }
                break;
            case constants.RESP_STATUS.FAILURE_NOT_FATAL:
                if (this.uiCallback) {
                    this.uiCallback.message(prepared.data);
                }
                break;
            case constants.RESP_STATUS.OK:
                this.initialized = true;
                if (this.uiCallback) {
                    this.uiCallback.allPreparedSuccessfully();
                }
                break;
            default:
        }
    }

    private async establishConnect() {
        if (this.uiCallback) {
            this.uiCallback.tryingToEstablishConnect();
        }
        const client = AndroidDevice.getAdbClient();
        // 启动Instrument
        client.shell(this.device.id, "am instrument -w" +
            " -e class \"com.damonlei.vimdroid.Initializer#startup\"" +
            " com.damonlei.vimdroid.test/" +
            "android.support.test.runner.AndroidJUnitRunner"
        ).then(adb.util.readAll
        ).then((output) => {
            logger.log("run instrument finished", output.toString().trim());
        }).catch(err => {
            logger.error(err);
            throw err;
        });
        // 尝试进行连接
        let resp = await this.tryToPingRetry(3, 1000);
        logger.log("Ping received", resp);
        if (resp.status !== constants.RESP_STATUS.OK) {
            throw new SubcoreError(resp.data);
        }
    }

    private tryToPingRetry(retry: number, timeout: number) {
        let _resolve, _reject;
        let self = this;

        async function handler() {
            let resp = await self.tryToPing();
            if (resp) {
                _resolve(resp);
                return;
            }
            if (retry > 0) {
                retry--;
                setTimeout(handler, timeout);
            } else {
                _reject(new SubcoreError("Could not connect with mobile."));
            }
        }

        return new Promise((resolve: (x: Resp<string>) => {}, reject) => {
            _resolve = resolve;
            _reject = reject;
            setTimeout(handler, timeout);
        });
    }

    private async tryToPing() {
        let resp;
        try {
            resp = await this.protocol.ping();
        } catch (e) {
            logger.error(e, "Trying to ping failed.");
            return null;
        }
        if (resp.status !== constants.RESP_STATUS.OK) {
            logger.log("Trying to ping failed.", resp.data);
            return resp;
        }
        return resp;
    }

    private async prepareSubcore() {
        if (this.uiCallback) {
            this.uiCallback.tryingToPrepareSubcore();
        }
        const client = AndroidDevice.getAdbClient();
        if (Config.FORCE_INSTALL_SUBCORE || !(await client.isInstalled(this.device.id, constants.PACKAGE_NAME))) {
            if (this.uiCallback) {
                this.uiCallback.tryingToInstallSubcore();
            }
            await this.installSubcore();
        }
    }

    private async installSubcore() {
        await AndroidDevice.getAdbClient().install(this.device.id, path.join(__dirname, "../res/apk/app.apk"));
        await AndroidDevice.getAdbClient().install(this.device.id, path.join(__dirname, "../res/apk/app-server.apk"));
    }

    private async chooseDevice() {
        if (this.uiCallback) {
            this.uiCallback.tryingToSearchDevice();
        }
        const client = AndroidDevice.getAdbClient();
        let devices: AndroidDeviceModel[];
        try {
            devices = await AndroidDevice.getAdbDevices(client);
        } catch (e) {
            logger.error(e, AndroidConnectError.name);
            throw new AndroidConnectError();
        }

        await AndroidDevice.insertDeviceName(client, devices);

        return await this.chooseOneDeviceFromArray(devices);
    }

    private async chooseOneDeviceFromArray(devices) {
        let hitDevice;
        if (devices.length > 1) {
            let deviceId = await this.userInterface.makeUserChoose(AndroidDevice.adaptUIDevice(devices));
            for (let device of devices) {
                if (device.id === deviceId) {
                    hitDevice = device;
                    break;
                }
            }
        } else if (devices.length === 1) {
            hitDevice = devices[0];
        } else {
            throw new Error("Devices length === 0");
        }
        return hitDevice;
    }

    private static async insertDeviceName(client, devices: AndroidDeviceModel[]) {
        for (let item of devices) {
            let properties = await client.getProperties(item.id);
            item.name = properties['ro.product.name'];
        }
        return devices;
    }

    private static adaptUIDevice(devices: AndroidDeviceModel[]) {
        let result: UIDevice[] = [];
        for (let item of devices) {
            result.push({id: item.id, name: `${item.name} [${item.type}]`});
        }
        return result;
    }

    private static async getAdbDevices(client) {
        const devices = await client.listDevices();
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
    }

    private static getAdbClient() {
        if (AndroidDevice.adbClient == null) {
            logger.log(`create adbClient`);
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

    protected async communicate<T>(id: number, entity?: string): Promise<Resp<T>> {
        const socket = await AndroidDevice.getAdbClient().openTcp(this.device.id, constants.DEVICE_PORT);
        return DeviceBase.__communicate__<T>(socket, id, entity);
    }
}

interface AndroidDeviceModel {
    id: string, type: string, name?: string, path?: string
}

export class AndroidConnectError extends BaseError {
}

class AndroidProtocol extends ProtocolBase {

    public ensurePrepared(): Promise<Resp<string>> {
        return this._send_(constants.CMD_ID.ensurePrepared);
    }
}

export interface AndroidCallback {
    tryingToSearchDevice();
    tryingToPrepareSubcore();
    tryingToInstallSubcore();
    tryingToEstablishConnect();
    allPreparedSuccessfully();
    failure(msg);
    message(msg);
}

