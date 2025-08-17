export interface ArcGisMapsPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
