/**
 * Created by ldx on 2017/4/9.
 */
import {strings} from "./res/string";
import logger from "./util/logger";
import {UserInterface, UIDevice} from "./user-interface";
import {AndroidCallback} from "./device/android";
import * as CLI from "./index";
import {Config} from "./index";
import {BaseError} from "./util/error";

const argv = require("yargs")
    .usage(`${strings.uiUsage}`)
    .boolean("d").alias("d", "debug").describe("d", strings.uiOptionDebug)
    .boolean("v").alias("v", "version").describe("v", strings.uiOptionVersion)
    .boolean("s").alias("s", "setting").describe("s", "Coming soon...")
    .boolean("f").alias("f", "force").describe("f", "[DEBUG]Force install the new app version.")
    .help("h").alias("h", "help").describe("h", strings.uiOptionHelp)
    .detectLocale(false)
    .argv;

if (argv.v) {
    const pkg = require("../package.json");
    console.log(`${pkg.name} ${pkg.version}`);
    process.exit(0);
}

if (argv.debug) {
    Config.DEBUG = true;
}
if (argv.force) {
    Config.FORCE_INSTALL_SUBCORE = true;
}

class TerminalUI extends UserInterface implements AndroidCallback {
    failure(msg) {
        console.log(msg);
        terminate().then();
    }

    message(msg) {
        console.log(msg);
    }

    tryingToSearchDevice() {
        console.log("正在搜索设备...")
    }

    tryingToPrepareSubcore() {
        console.log("正在准备android端子程序...")
    }

    tryingToInstallSubcore() {
        console.log("正在安装android端子程序...")
    }

    tryingToEstablishConnect() {
        console.log("正在与子程序建立连接...")
    }

    allPreparedSuccessfully() {
        console.log("初始化完成...")
    }

    makeUserChoose(devices: UIDevice[]): Promise<string> {
        return undefined;
    }
}

const ui = new TerminalUI();

async function start() {
    let isErr = false;
    try {
        await CLI.start(ui, ui);
    } catch (err) {
        outputErr(err);
        isErr = true;
    }
    if (isErr) {
        await terminate();
    }
}

async function terminate() {
    console.log(strings.uiTerminate);
    logger.log("terminate...");
    try {
        await CLI.terminate();
    } catch (err) {
        logger.error(err);
    } finally {
        process.exit(0);
    }
}

start().then(() =>{
    // do nothing;
});

process.stdin.on("keypress", (str, key) => {
    if (key.ctrl && key.name === 'c') {
        terminate().then();
    }
});

function outputErr(err) {
    if (err instanceof BaseError) {
        console.log(`${err.cause()} ${err.message} ${err.solution()}`);
        logger.error(err);
    } else {
        let unknownError = strings.error[BaseError.name];
        console.log(`${unknownError.cause} ${unknownError.solution}`);
        console.log(logger.errorStr(err));
    }
}

