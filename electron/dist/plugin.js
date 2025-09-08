'use strict';

Object.defineProperty(exports, "__esModule", { value: true });
exports.ArcGisMaps = void 0;
const electron_1 = require("electron");
const electron_cgi_1 = require("electron-cgi");
const events_1 = require("events");
const path = require("path");
class ArcGisMaps extends events_1.EventEmitter {
    constructor() {
        super();
        this.initializeConnection();
    }
    async signIn(options) {
        var _a;
        try {
            return await ((_a = this._connection) === null || _a === void 0 ? void 0 : _a.send('signIn', options));
        }
        catch (error) {
            throw (error === null || error === void 0 ? void 0 : error.message) || error;
        }
    }
    async signOut() {
        var _a;
        try {
            return await ((_a = this._connection) === null || _a === void 0 ? void 0 : _a.send('signOut'));
        }
        catch (error) {
            throw (error === null || error === void 0 ? void 0 : error.message) || error;
        }
    }
    async query(options) {
        var _a;
        try {
            return await ((_a = this._connection) === null || _a === void 0 ? void 0 : _a.send('query', options));
        }
        catch (error) {
            throw (error === null || error === void 0 ? void 0 : error.message) || error;
        }
    }
    initializeConnection() {
        const appPath = electron_1.app
            .getAppPath()
            .replace('app.asar', 'app.asar.unpacked');
        const srcPath = path.join(appPath, 'node_modules/capacitor-arcgis-maps/electron/dist');
        this._connection = new electron_cgi_1.ConnectionBuilder()
            .connectTo(path.join(srcPath, 'plugin.exe'))
            .build();
        if (this._connection) {
            this._connection.onDisconnect = () => {
                this.initializeConnection();
            };
        }
    }
}
exports.ArcGisMaps = ArcGisMaps;
//# sourceMappingURL=plugin.js.map
