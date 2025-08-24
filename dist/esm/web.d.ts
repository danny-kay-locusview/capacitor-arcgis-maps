import { WebPlugin } from '@capacitor/core';
import type { ArcGisMapsPlugin, SignInResult, TestAuthResult } from './definitions';
export declare class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
    init(): Promise<void>;
    signIn(): Promise<SignInResult>;
    testAuth(): Promise<TestAuthResult>;
}
