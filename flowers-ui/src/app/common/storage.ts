import {Injectable} from "@angular/core";

export interface FlowersStorage {
  setItem<T>(name: string, data: T): void;
  hasItem(name: string): boolean;
  removeItem(name: string): boolean;
  getItem<T>(name: string): any;
  clear(): void;
}

@Injectable()
export class TabStorage implements FlowersStorage {

  getItem<T>(name: string): any {
    if (this.hasItem(name)) {
      return JSON.parse(sessionStorage.getItem(name));
    }
    return null;
  }

  removeItem(name: string): boolean {
    if (this.hasItem(name)) {
      sessionStorage.removeItem(name);
      return true;
    }
    return false;
  }

  hasItem(name: string): boolean {
    if (sessionStorage.getItem(name)) {
      return true;
    }
    return false;
  }

  setItem<T>(name: string, data: T): void {
    sessionStorage.setItem(name, typeof data == "string" ? data : JSON.stringify(data));
  }

  clear(): void {
    sessionStorage.clear();
  }
}


@Injectable()
export class SessionStorage implements FlowersStorage {

  setItem<T>(name: string, data: T): void {
    localStorage.setItem(name, typeof data == "string" ? data : JSON.stringify(data));
  }

  hasItem(name: string): boolean {
    if (localStorage.getItem(name)) {
      return true;
    }
    return false;
  }

  clear(): void {
    localStorage.clear();
  }

  removeItem(name: string): boolean {
    if (localStorage.getItem(name)) {
      localStorage.removeItem(name);
      return true;
    }
    return false;
  }

  getItem<T>(name: string): any {
    if (this.hasItem(name)) {
      return JSON.parse(localStorage.getItem(name));
    }
    return null;
  }
}
