import {CertificateType, Country, Nationality, Requestor} from "../../common/static-data";
import {AuthorityType} from "../../authentication/authentication";
import {FormGroup} from "@angular/forms";
import {FormControlConfig} from "../../common/common";
import {AbstractOrder, OrderStatus} from "../order";

export class CertificateOrder extends AbstractOrder{
  roles: [string];
  device: string;
  tokenSerialNumber: string;
  requestDate: Date;
  holder: Holder;
  address: Address;
  remoteId: boolean;
  userExternalId: string;
  department: string;

  constructor() {
    super();
    this.holder = new Holder();
    this.address = new Address();
  }
}

export class Holder {
  firstName: string;
  surName: string;
  nationality: Nationality;
  eMail: string;
  eMailSecond: string;
  certificateType: CertificateType;
  roleType: AuthorityType;
  birthDate: Date;
  activationCode: string;
  phoneNumber: string;
  notifyEmail: string;
  certificateLevel: string;
  documents: any[];
}

export class Address {
  name: string;
  company: string;
  streetAndHouseNo: string;
  addressLine2: string;
  postCode: string;
  city: string;
  country: Country;
}

export class HolderFormConfig {
  documents = new FormControlConfig({visible: false, max: 30 * 1024 * 1024}).initDefaultValues();
  documentsSelect = new FormControlConfig({visible: false}).initDefaultValues();
}

export class OrderFormConfig {
  device: FormControlConfig = new FormControlConfig().initDefaultValues();
  acceptedGTC: FormControlConfig = new FormControlConfig({visible: false, required: true}).initDefaultValues();
  holder: HolderFormConfig = new HolderFormConfig();
}
