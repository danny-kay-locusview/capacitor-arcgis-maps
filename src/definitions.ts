export interface ArcGisMapsPlugin {
  signIn(options: SignInOptions): Promise<void>;
  signOut(): Promise<void>;
  query(options: QueryOptions): Promise<QueryResult>;
}

export interface SignInOptions {
  portalUrl: string;
  clientId: string;
  redirectUrl: string;
  licenseKey?: string;
  apiKey?: string;
}

export interface QueryOptions {
  layerUrl: string;
  where?: string;
  limit?: number;
  bbox?: {
    xmin: number;
    ymin: number;
    xmax: number;
    ymax: number;
  }
}

export interface QueryResult {
  displayFieldName: string;
  fields: Field[];
  fieldAliases: Record<string, string>;
  geometryType: 'esriGeometryPoint' | 'esriGeometryPolyline';
  spatialReference: Record<string, string>;
  features: Feature[];
  url: string;
  layerId: number;
  layerName: string;
  symbology: any;
}

export interface Field {
  name: string;
  type: string;
  alias: string;
  length: number;
}

export interface Feature {
  geometry: { x: number, y: number } | number[][];
  attributes: Record<string, any>;
}

