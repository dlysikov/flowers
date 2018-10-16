import {CertificateType, Language, Nationality} from "../common/static-data";
import {Unit} from "../unit/unit";
import {Selectable} from "../common/common";

export class User implements Selectable {

  id: number;
  ssn: string;
  firstName: string;
  surName: string;
  email: string;
  certificateType: CertificateType;
  roles: Role[];
  defaultLanguage: Language;
  unit: Unit;
  birthDate: Date;
  nationality: Nationality;
  phoneNumber: string;

  constructor () {
    this.roles = [];
  }

  getSortByField(): string {
    return 'firstName';
  }

  getView(): string {
    return this.firstName + ' ' + this.surName;
  }
}

export enum RoleType {
  END_USER='END_USER',
  DIA='DIA',
  CSD_AGENT='CSD_AGENT',
  FLOWERS_ADMIN='FLOWERS_ADMIN',
  LUXTRUST_ADMIN='LUXTRUST_ADMIN',
  GDPR_AGENT='GDPR_AGENT',
  ESEAL_MANAGER='ESEAL_MANAGER',
  ESEAL_OFFICER='ESEAL_OFFICER'
}

export class Role implements Selectable {

  id: number;
  roleType: RoleType;

  toString(): string {
    return this.roleType;
  }

  getSortByField(): string {
    return 'roleType';
  }

  getView(): string {
    return this.roleType;
  }
}
