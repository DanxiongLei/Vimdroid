/**
 * Created by ldx on 2017/4/7.
 */
import constants from "../constants";
import {Socket} from "net";
import logger from "../util/logger";

/**
 * UIDevice
 *  - initialize 初始化
 *  - isInitialized  判断是否初始化
 *  - communicate(id, string) : string
 *  - isConnected 判断是否仍在连接状态
 *
 *  无连接状态 --> 无准备状态 --> 准备完成状态
 */
export abstract class DeviceBase {

    protocol: ProtocolBase;

    abstract initialize();

    abstract isInitialized();

    abstract isConnected();

    protected abstract communicate<T>(id: number, entity?: string): Promise<Resp<T>>;

    protected static __communicate__<T>(socket: Socket, id: number, entity: string = ""): Promise<Resp<T>> {
        socket.setEncoding("utf8");
        socket.setNoDelay(true);
        socket.write(`${id}\n${entity}\n`);
        return new Promise((resolve: (resp: Resp<T>) => {}, reject) => {
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
                } catch (err) {
                    logger.log("JSON.parse error", response);
                    throw err;
                }
                resolve(json);
            });
        });
    }

}

export abstract class ProtocolBase {

    protected mDevice;

    constructor(device: DeviceBase) {
        this.mDevice = device;
    }

    protected _send_<T>(id, entity?: string | Object): Promise<Resp<T>> {
        logger.log("__send__ id:", id);
        if (typeof entity === 'object') {
            entity = JSON.stringify(entity);
        }
        return this.mDevice.communicate(id, entity);
    }

    public ping(): Promise<Resp<string>> {
        return this._send_<string>(constants.CMD_ID.ping);
    }

    public sendKeypress(key: Object): Promise<Resp<string>> {
        return this._send_(constants.CMD_ID.keyboard, key);
    }

    public shutdown(): Promise<Resp<string>> {
        return this._send_(constants.CMD_ID.shutdown);
    }
}

export interface Resp<T> {
    status: number;
    data?: T;
}
