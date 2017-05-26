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
const string_1 = require("./res/string");
const logger_1 = require("./util/logger");
const user_interface_1 = require("./user-interface");
const CLI = require("./index");
const index_1 = require("./index");
const error_1 = require("./util/error");
const argv = require("yargs")
    .usage(`${string_1.strings.uiUsage}`)
    .boolean("d").alias("d", "debug").describe("d", string_1.strings.uiOptionDebug)
    .boolean("v").alias("v", "version").describe("v", string_1.strings.uiOptionVersion)
    .boolean("s").alias("s", "setting").describe("s", "Coming soon...")
    .boolean("f").alias("f", "force").describe("f", "[DEBUG]Force install the new app version.")
    .help("h").alias("h", "help").describe("h", string_1.strings.uiOptionHelp)
    .detectLocale(false)
    .argv;
if (argv.v) {
    const pkg = require("../package.json");
    console.log(`${pkg.name} ${pkg.version}`);
    process.exit(0);
}
if (argv.debug) {
    index_1.Config.DEBUG = true;
}
if (argv.force) {
    index_1.Config.FORCE_INSTALL_SUBCORE = true;
}
class TerminalUI extends user_interface_1.UserInterface {
    failure(msg) {
        console.log(msg);
        terminate().then();
    }
    message(msg) {
        console.log(msg);
    }
    tryingToSearchDevice() {
        console.log("正在搜索设备...");
    }
    tryingToPrepareSubcore() {
        console.log("正在准备android端子程序...");
    }
    tryingToInstallSubcore() {
        console.log("正在安装android端子程序...");
    }
    tryingToEstablishConnect() {
        console.log("正在与子程序建立连接...");
    }
    allPreparedSuccessfully() {
        console.log("初始化完成...");
    }
    makeUserChoose(devices) {
        return undefined;
    }
}
const ui = new TerminalUI();
function start() {
    return __awaiter(this, void 0, void 0, function* () {
        let isErr = false;
        try {
            yield CLI.start(ui, ui);
        }
        catch (err) {
            outputErr(err);
            isErr = true;
        }
        if (isErr) {
            yield terminate();
        }
    });
}
function terminate() {
    return __awaiter(this, void 0, void 0, function* () {
        console.log(string_1.strings.uiTerminate);
        logger_1.default.log("terminate...");
        try {
            yield CLI.terminate();
        }
        catch (err) {
            logger_1.default.error(err);
        }
        finally {
            process.exit(0);
        }
    });
}
start().then(() => {
});
process.stdin.on("keypress", (str, key) => {
    if (key.ctrl && key.name === 'c') {
        terminate().then();
    }
});
function outputErr(err) {
    if (err instanceof error_1.BaseError) {
        console.log(`${err.cause()} ${err.message} ${err.solution()}`);
        logger_1.default.error(err);
    }
    else {
        let unknownError = string_1.strings.error[error_1.BaseError.name];
        console.log(`${unknownError.cause} ${unknownError.solution}`);
        console.log(logger_1.default.errorStr(err));
    }
}
