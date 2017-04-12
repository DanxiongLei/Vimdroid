"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const logger_1 = require("./logger");
const string_1 = require("../res/string");
class BaseError extends Error {
    constructor(msg) {
        super(msg);
        Error.captureStackTrace(this, this.constructor);
        this.name = this.constructor.name;
    }
    cause() {
        return string_1.strings.error[this.name].cause;
    }
    solution() {
        return string_1.strings.error[this.name].solution;
    }
    toString() {
        return logger_1.default.errorStr(this);
    }
}
exports.BaseError = BaseError;
class SubcoreError extends BaseError {
}
exports.SubcoreError = SubcoreError;
