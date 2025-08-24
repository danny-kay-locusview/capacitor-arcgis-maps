export interface ArcGisMapsPlugin {
  init(initOptions: InitOptions): Promise<void>;
  signIn(): Promise<SignInResult>;
  testAuth(testAuthOptions: TestAuthOptions): Promise<TestAuthResult>;
}

export interface InitOptions {
  portalUrl: string;
  clientId: string;
  redirectUri: string;
  licenseKey?: string;
  apiKey?: string;
}

export interface TestAuthOptions {
  securedLayerUrl?: string;
}

export interface SignInResult {
  username?: string;
  fullName?: string;
  email?: string;
  organizationId?: string;
  role?: string;
  privileges?: string[];
  tokenExpires?: string;
}

export interface TestAuthResult {
  portalUrl: string;
  isAuthenticated: boolean;
  username: string;
  fullName: string;
  email: string;
  organizationId: string;
  role: string;
  privileges: string[];
  orgTitle: string;
  securedLayerUrl: string;
  securedLayerFeatureCount: number;
}
