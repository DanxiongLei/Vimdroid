/**
 * Created by ldx on 2017/4/7.
 */

import * as path from "path";
import {Config} from "../index";

export default class logger {

    public static log(...args) {
        if (!Config.DEBUG) {
            return;
        }

        args.forEach((item, index, array) => {
            if (typeof item === 'object') {
                try {
                    array[index] = JSON.stringify(item);
                } catch (e) {
                    array[index] = item.toString();
                }
            }
        });

        console.log(`[!!!DEBUG!!!][${logger.getInvokeMessage()}] - ${args}`);
    }

    public static errorStr(e: Error, message?: string) {
        return `[!!!ERROR!!!][${logger.parseRawStack(e.stack).toString()}]`
            + `- ${message}: ${e.name} with msg [${e.message}]\n${e.stack}`;
    }

    public static error(e: Error, message?: string) {
        if (!Config.DEBUG) {
            return;
        }
        console.log(logger.errorStr(e, message));
    }

    public static getInvokeMessage(): string {
        const stack = logger.getRawStack();
        let parsed;
        try {
            parsed = logger.parseRawStack(stack);
        } catch (err) {
            console.log(stack);
            throw err;
        }
        return parsed.toString();
    }

    public static parseRawStack(stack: string): Stack {
        let cantParsedStack = new Stack({fileName: `Stack can not parsed.[${stack}]`, method: null, lineNumber: 0});
        let lines = stack.split("\n");
        if (lines.length < 2) {
            return cantParsedStack;
        }
        let line = lines[1];
        const reg_with_method = /^\s*at (\S+) \((.+\.js):(\d+):\d+\)\s*$/;
        if (reg_with_method.test(line)) {
            let split = reg_with_method.exec(line);
            return new Stack({
                fileName: path.basename(split[2]),
                method: split[1],
                lineNumber: parseInt(split[3]),
            });
        }

        const reg_without_method = /^\s*at (.+\.js):(\d+):\d+\s*$/;
        if (reg_without_method.test(line)) {
            let split = reg_without_method.exec(line);
            return new Stack({
                fileName: path.basename(split[1]),
                method: null,
                lineNumber: parseInt(split[2]),
            });
        }

        return cantParsedStack;
    }

    private static getRawStack(limit = 1, opt = logger.log): string {
        const rawLimit = Error.stackTraceLimit;
        Error.stackTraceLimit = limit;
        const result: any = {};
        Error.captureStackTrace(result, opt);
        Error.stackTraceLimit = rawLimit;
        return result.stack;
    }


};

class Stack {
    fileName: string;
    method: string;
    lineNumber: number;

    constructor(source) {
        Object.assign(this, source);
    }

    public toString(): string {
        return `${this.method}():(${this.fileName}:${this.lineNumber})`
    }
}
