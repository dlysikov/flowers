import {Language} from "../common/static-data";
import {Unit} from "../unit/unit";

export class AuthenticationDetails {
  certificateType: string;
  ssn: string;
  givenName: string;
  surname: string;
}

export enum AuthorityType  {
  END_USER = "END_USER",
  DIA = "DIA",
  CSD_AGENT = "CSD_AGENT",
  FLOWERS_ADMIN = "FLOWERS_ADMIN",
  ESEAL_MANAGER = "ESEAL_MANAGER",
  ESEAL_OFFICER = "ESEAL_OFFICER"
}

export enum Permission {
  SIGN_AND_SEND_CERTIFICATEORDER="SIGN_AND_SEND_CERTIFICATEORDER",
  CREATE_IN_BATCH_CERTIFICATEORDER="CREATE_IN_BATCH_CERTIFICATEORDER",
  CREATE_CERTIFICATEORDER="CREATE_CERTIFICATEORDER",
  EDIT_CERTIFICATEORDER="EDIT_CERTIFICATEORDER",
  READ_CERTIFICATEORDER="READ_CERTIFICATEORDER",

  CREATE_UNIT="CREATE_UNIT",
  EDIT_UNIT="EDIT_UNIT",
  READ_UNIT="READ_UNIT",

  CREATE_USER="CREATE_USER",
  EDIT_USER="EDIT_USER",
  READ_USER="READ_USER",

  CREATE_ESEALORDER="CREATE_ESEALORDER",
  EDIT_ESEALORDER="EDIT_ESEALORDER",
  READ_ESEALORDER="READ_ESEALORDER",
  ACTIVATE_ESEALORDER="ACTIVATE_ESEALORDER",
  "SIGN_AND_SEND_ESEALORDER"="SIGN_AND_SEND_ESEALORDER"
}

export class Authority {
  authority: AuthorityType;
}

export class Authentication {
  id: number;
  ssn: string;
  defaultLanguage: Language;
  details: AuthenticationDetails;
  jwt: string;
  refreshJwt:string;
  authorities: Authority[];
  permissions:Permission[];
  unit: Unit;
}



