import {Selectable} from "./common";

export class Country implements Selectable {
  id:number;
  countryCode:string;

  getSortByField(): string {
    return "countryCode";
  }

  getView(): string {
    return this.countryCode;
  }
}
export class Nationality implements Selectable {
  id: number;
  nationalityCode:string;

  getSortByField(): string {
    return "nationalityCode";
  }

  getView(): string {
    return this.nationalityCode;
  }
}
export class Requestor implements Selectable {
  id: number;
  name:string;
  config: RequestorConfig = new RequestorConfig();

  getSortByField(): string {
    return "name";
  }

  getView(): string {
    return this.name;
  }
}
export class Language implements Selectable{
  id: number;
  twoCharsCode:string;
  useByDefault: boolean;

  getSortByField(): string {
    return 'twoCharsCode';
  }

  getView(): string {
    return this.twoCharsCode;
  }
}
export class RequestorConfig {
  shortFlow: boolean;
  remoteId: boolean;
}

export enum CertificateType {
  PRIVATE = "PRIVATE",
  PROFESSIONAL_PERSON="PROFESSIONAL_PERSON",
  PROFESSIONAL_CERTIFICATE_ADMINISTRATOR="PROFESSIONAL_CERTIFICATE_ADMINISTRATOR"
}

