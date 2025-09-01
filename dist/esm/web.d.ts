import { WebPlugin } from '@capacitor/core';
import type { ArcGisMapsPlugin } from './definitions';
export declare class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
    signIn(): Promise<any>;
    signOut(): Promise<any>;
    query(): Promise<any>;
}
