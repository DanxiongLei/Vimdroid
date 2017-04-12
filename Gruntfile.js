/**
 * Created by ldx on 2017/4/10.
 */
const path = require("path");
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
            apk: ["<%= apk_path%>", "./lib/res/apk"],
            dist: ["dist"],
            libjs: ["./lib/**/*.js", "!./lib/**/keypress.js", "./lib/**/*.js.map"]
        },
        copy: {
            apk: {
                files: [{
                    expand: true,
                    nonull: true,
                    cwd: "<%= apk_path %>",
                    src: "*[!est].apk",
                    dest: "./lib/res/apk/",
                    rename: (dest, src) => path.join(dest, "app.apk"),
                }, {
                    expand: true,
                    nonull: true,
                    cwd: "<%= apk_path %>",
                    src: "*est.apk",
                    dest: "./lib/res/apk/",
                    rename: (dest, src) => path.join(dest, "app-server.apk")
                }],
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: "./lib",
                    src: "**/*.js",
                    dest: "./dist/",
                    rename: (dest, src) => {
                        console.log(dest);
                        console.log(src);
                        console.log(path.join(dest, src));
                        return path.join(dest, src)
                    }
                }]
            }
        },
        ts: {
            default: {
                tsconfig: true,
            }
        }
    });

    grunt.registerTask("dist", ["clean:libjs", "clean:dist", "ts"]);
    let moveApk = ["copy:apk"];
    grunt.registerTask("release", ["clean:apk", "shell:buildRelease", "shell:buildAndroidTest"].concat(moveApk));
    grunt.registerTask("debug", ["clean:apk", "shell:buildDebug", "shell:buildAndroidTest"].concat(moveApk));
    grunt.registerTask("default", "debug");
};
