import { app } from "electron";
import type { Connection} from "electron-cgi";
import { ConnectionBuilder } from "electron-cgi";
import { EventEmitter } from 'events';
import * as path from "path";

import type {
  ArcGisMapsPlugin,
  QueryOptions,
  QueryResult,
  SignInOptions,
} from '../../src';

export class ArcGisMaps extends EventEmitter implements ArcGisMapsPlugin {
  private _connection: Connection;

  constructor() {
    super();
    this.initializeConnection();
  }

  async signIn(options: SignInOptions): Promise<void> {
    try {
      return await this._connection?.send('signIn', options);
    } catch (error) {
      throw error?.message || error;
    }
  }

  async signOut(): Promise<void> {
    try {
      return await this._connection?.send('signOut');
    } catch (error) {
      throw error?.message || error;
    }
  }

  async query(options: QueryOptions): Promise<QueryResult> {
    try {
      return await this._connection?.send('query', options);
    } catch (error) {
      throw error?.message || error;
    }
  }

  private initializeConnection(): void {
    const appPath: string = app
      .getAppPath()
      .replace('app.asar', 'app.asar.unpacked');
    console.log(`Path: ${path}`);
    console.log(`App Path: ${appPath}`);
    const srcPath: string = path.join(
      appPath,
      'node_modules/capacitor-arcgis-maps/electron/dist',
    );
    console.log(`Src Path: ${srcPath}`);
    this._connection = new ConnectionBuilder()
      .connectTo(path.join(srcPath, 'plugin.exe'))
      .build();

    if (this._connection) {
      this._connection.onDisconnect = () => {
        this.initializeConnection();
      };
    }
  }
}
