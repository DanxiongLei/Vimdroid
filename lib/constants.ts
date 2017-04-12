/**
 * Created by ldx on 2017/4/7.
 */
const ANDROID_PACKAGE_NAME = "com.damonlei.vimdroid";
const constants = {
    CMD_ID: {
        ping: 236,
        keyboard: 237,
        ensurePrepared: 238,
        shutdown: 239
    },
    RESP_STATUS: {
        OK: 0,
        FAILURE: -1,
        FAILURE_NOT_FATAL: -2
    },
    DEVICE_PORT: 10086,
    PACKAGE_NAME: ANDROID_PACKAGE_NAME,
    SERVICE_CMP: `${ANDROID_PACKAGE_NAME}/.AppService`,
    ERROR_CODE: {
        ADB_ERROR: 403,
        DEVICE_NOT_FOUND: 404,
        APP_NOT_INSTALLED: 405,
        TCP_ERROR: 406,
        MOBILE_END_ERROR: 407
    },
};

export default constants;

