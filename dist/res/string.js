"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const android_1 = require("../device/android");
const error_1 = require("../util/error");
exports.strings = {
    error: {
        [error_1.SubcoreError.name]: {
            cause: "子程序错误.",
            solution: ""
        },
        [android_1.AndroidConnectError.name]: {
            cause: "连接手机失败.",
            solution: "请确认:\n" +
                "    1. 是否正确连接usb连接线.\n" +
                "    2. 是否已在手机上开启开发者模式.\n" +
                "    3. 部分手机需设置连接方式."
        },
        [error_1.BaseError.name]: {
            cause: "未知错误.",
            solution: "请联系作者."
        },
    },
    uiUsage: "Usage : " +
        "  vimdroid [options] 开启程序; Ctrl-C退出\n" +
        "  使您能够用电脑键盘控制您的Android手机。\n" +
        "  请将Android设备和电脑使用usb连接，在命令行中运行程序，并根据引导在手机上授权。\n" +
        "  授权后，支持键位：\n\n" +
        "       f : 选中某区域，对其进行点击\n" +
        "    hjkl : 向左下上右滚动\n" +
        "       i : 进入输入模式",
    uiOptionDebug: "以调试模式启动程序。",
    uiOptionHelp: "显示帮助信息",
    uiOptionVersion: "显示版本号",
    uiTerminate: "程序已终止..."
};
