import { registerPlugin } from '@capacitor/core';

import type { ArcGisMapsPlugin } from './definitions';

const ArcGisMaps = registerPlugin<ArcGisMapsPlugin>('ArcGisMaps', {
  web: () => import('./web').then(m => new m.ArcGisMapsWeb()),
  electron: () => (window as any).CapacitorCustomPlatform.plugins.ArcGisMaps
});

export * from './definitions';
export { ArcGisMaps };
