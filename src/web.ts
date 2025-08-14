import { WebPlugin } from '@capacitor/core';

import type { ArcGisMapsPlugin } from './definitions';

export class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
