import { WebPlugin } from '@capacitor/core';
import type { ArcGisMapsPlugin } from './definitions';
export declare class ArcGisMapsWeb extends WebPlugin implements ArcGisMapsPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
