/**
 * Created by ldx on 2017/4/10.
 */
const path = require("path");
let copyApkTaskFileTemplate = function (srcPattern, name) {
    return {
        expand: true,
        nonull: true,
        cwd: "<%= apk_path %>",
        src: srcPattern,
        dest: "./dist/res/apk/",
        rename: (dest, src) => path.join(dest, name),
    };
};
module.exports = function (grunt) {
    require("load-grunt-tasks")(grunt);
    grunt.initConfig({
        gradlew: "./gradlew",
        apk_path: "./vim-android/app/build/outputs/apk",
        android_path: "./vim-android",
        shell: {
            clean: {
                command: "<%= gradlew%> clean",
                cwd: "<%= android_path%>"
            },
            buildDebug: {
                command: "<%= gradlew %> assembleDebug",
                cwd: "<%= android_path%>",
            },
            buildRelease: {
                command: "<%= gradlew %> assembleRelease",
                cwd: "<%= android_path%>",
            },
            buildAndroidTest: {
                command: "<%= gradlew %> assembleAndroidTest",
                cwd: "<%= android_path%>",
            }
        },
        clean: {
            apk: ["<%= apk_path%>", "./lib/res/apk", "./dist/res/apk"],
            dist: ["dist"],
            libjs: ["./lib/**/*.js", "!./lib/**/keypress.js", "./lib/**/*.js.map"]
        },
        copy: {
            apkRelease: {
                files: [
                    copyApkTaskFileTemplate("*release.apk", "app.apk"),
                    copyApkTaskFileTemplate("*est.apk", "app-server.apk")]
            },
            apkDebug: {
                files: [
                    copyApkTaskFileTemplate("*debug.apk", "app.apk"),
                    copyApkTaskFileTemplate("*est.apk", "app-server.apk")]
            }
        },
        ts: {
            default: {
                tsconfig: true,
            }
        }
    });
    grunt.registerTask("releaseApk", ["clean:apk", "shell:buildRelease", "shell:buildAndroidTest", "copy:apkRelease"]);
    grunt.registerTask("debugApk", ["clean:apk", "shell:buildDebug", "shell:buildAndroidTest", "copy:apkDebug"]);
    grunt.registerTask("dist", ["clean:libjs", "clean:dist", "ts"]);
    grunt.registerTask("release", ["dist", "releaseApk"]);
    grunt.registerTask("debug", ["dist", "debugApk"]);
    grunt.registerTask("default", "debug");
};
