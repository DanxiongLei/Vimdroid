/**
 * Created by ldx on 2017/4/9.
 */
import {AndroidDevice, AndroidCallback} from "./device/android";
import * as keypress from "./io/keypress";
import {UserInterface} from "./user-interface";
import logger from "./util/logger";
import {Resp} from "./device/base";

let androidDevice;

export const Config = {
    DEBUG: false,
    FORCE_INSTALL_SUBCORE: false
};

export async function start(ui: UserInterface, callback: AndroidCallback) {
    keypress.emitKeypressEvents(keyObserve);
    androidDevice = new AndroidDevice(ui);
    androidDevice.registerCallback(callback);
    let result = await androidDevice.initialize();
    logger.log(result);
    return result;
}

interface KeyEvent {
    name: string;
    ctrl: boolean;
    shift: boolean;
    sequence?: string
    meta: string
}

async function keyObserve(key: KeyEvent) {
    logger.log("receive keyEvent : ", key);
    if (key.ctrl && key.name === 'c') {
        return;
    }
    let resp: Resp<string> = await androidDevice.protocol.sendKeypress(key);
    logger.log(`receive resp(${JSON.stringify(resp)}) for keyEvent(${key.name})`);
}

export function end(outputErr) {
    process.stdin.removeAllListeners("keypress");
    logger.log("CLI.end");
    androidDevice.protocol.shutdown().then(() => {
        process.exit(0);
    }).catch(err => {
        outputErr(err);
        process.exit(0);
    });
    androidDevice = null;
}

export async function settings() {

}
