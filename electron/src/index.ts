import { app } from "electron";
import type { Connection} from "electron-cgi";
import { ConnectionBuilder } from "electron-cgi";
import { EventEmitter } from 'events';
import path from 'path';

export class ArcGisMaps extends EventEmitter {
  private _connection: Connection;

  constructor() {
    super();
    this.initializeConnection();
  }

  async echo(): Promise<any> {
    try {
      return await this._connection?.send('echo');
    } catch (error) {
      throw (error?.message || error);
    }
  }

  private initializeConnection(): void {
    const appPath: string = app.getAppPath().replace("app.asar", "app.asar.unpacked");
    const srcPath: string = path.join(appPath, "node_modules/capacitor-arcgis-maps/electron/dist");

    this._connection = new ConnectionBuilder()
      .connectTo(path.join(srcPath, "plugin.exe"))
      .build();

    if (this._connection) {
      this._connection.onDisconnect = () => {
        this.initializeConnection();
      }
    }
  }
}
