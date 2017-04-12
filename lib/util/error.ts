/**
 * Created by ldx on 2017/4/8.
 */

import logger from "./logger";
import {strings} from "../res/string";

export class BaseError extends Error {
    constructor(msg?: string) {
        super(msg);
        Error.captureStackTrace(this, this.constructor);
        this.name = this.constructor.name;
    }

    public cause() {
        return strings.error[this.name].cause;
    }

    public solution() {
        return strings.error[this.name].solution;
    }

    public toString(): string {
        return logger.errorStr(this);
    }
}

export class SubcoreError extends BaseError {
}
