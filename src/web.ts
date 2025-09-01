import { WebPlugin } from '@capacitor/core';

import type {
  ArcGisMapsPlugin
} from './definitions';

export class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
  signIn(): Promise<any> {
    throw this.unimplemented();
  }

  signOut(): Promise<any> {
    throw this.unimplemented();
  }

  query(): Promise<any> {
    throw this.unimplemented();
  }
}
