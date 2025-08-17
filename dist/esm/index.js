import { registerPlugin } from '@capacitor/core';
const ArcGisMaps = registerPlugin('ArcGisMaps', {
    web: () => import('./web').then(m => new m.ArcGisMapsWeb()),
    electron: () => window.CapacitorCustomPlatform.plugins.ArcGisMaps
});
export * from './definitions';
export { ArcGisMaps };
//# sourceMappingURL=index.js.map