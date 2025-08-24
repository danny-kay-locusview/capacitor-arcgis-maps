'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const ArcGisMaps = core.registerPlugin('ArcGisMaps', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.ArcGisMapsWeb()),
    electron: () => window.CapacitorCustomPlatform.plugins.ArcGisMaps
});

class ArcGisMapsWeb extends core.WebPlugin {
    async init() {
        throw this.unimplemented();
    }
    async signIn() {
        throw this.unimplemented();
    }
    async testAuth() {
        throw this.unimplemented();
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    ArcGisMapsWeb: ArcGisMapsWeb
});

exports.ArcGisMaps = ArcGisMaps;
//# sourceMappingURL=plugin.cjs.js.map
