import { WebPlugin } from '@capacitor/core';

import type {
  ArcGisMapsPlugin,
  SignInResult,
  TestAuthResult,
} from './definitions';

export class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
  async init(): Promise<void> {
    throw this.unimplemented();
  }

  async signIn(): Promise<SignInResult> {
    throw this.unimplemented();
  }

  async testAuth(): Promise<TestAuthResult> {
    throw this.unimplemented();
  }
}
