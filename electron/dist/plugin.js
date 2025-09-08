'use strict';

Object.defineProperty(exports, "__esModule", { value: true });
exports.ArcGisMaps = void 0;
const electron_1 = require("electron");
const electron_cgi_1 = require("electron-cgi");
const events_1 = require("events");
const path_1 = require("path");
class ArcGisMaps extends events_1.EventEmitter {
    _connection;
    constructor() {
        super();
        this.initializeConnection();
    }
    async signIn(options) {
        try {
            return await this._connection?.send('signIn', options);
        }
        catch (error) {
            throw error?.message || error;
        }
    }
    async signOut() {
        try {
            return await this._connection?.send('signOut');
        }
        catch (error) {
            throw error?.message || error;
        }
    }
    async query(options) {
        try {
            return await this._connection?.send('query', options);
        }
        catch (error) {
            throw error?.message || error;
        }
    }
    initializeConnection() {
        const appPath = electron_1.app
            .getAppPath()
            .replace('app.asar', 'app.asar.unpacked');
        const srcPath = path_1.default.join(appPath, 'node_modules/capacitor-arcgis-maps/electron/dist');
        this._connection = new electron_cgi_1.ConnectionBuilder()
            .connectTo(path_1.default.join(srcPath, 'plugin.exe'))
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
