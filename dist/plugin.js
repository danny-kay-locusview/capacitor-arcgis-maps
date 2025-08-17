var capacitorArcGisMaps = (function (exports, core) {
    'use strict';

    const ArcGisMaps = core.registerPlugin('ArcGisMaps', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.ArcGisMapsWeb()),
        electron: () => window.CapacitorCustomPlatform.plugins.ArcGisMaps
    });

    class ArcGisMapsWeb extends core.WebPlugin {
        async echo(options) {
            console.log('ECHO', options);
            return options;
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        ArcGisMapsWeb: ArcGisMapsWeb
    });

    exports.ArcGisMaps = ArcGisMaps;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
