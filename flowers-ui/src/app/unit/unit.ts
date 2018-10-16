import {Country, Requestor} from "../common/static-data";
import {CompanyIdentifier, FormControlConfig, Selectable} from "../common/common";

export class Unit implements Selectable{
  id: number;
  country: Country;
  requestor: Requestor;
  postCode:string;
  city:string;
  streetAndHouseNo:string;
  companyName:string;
  commonName:string;
  phoneNumber:string;
  eMail:string;
  identifier: CompanyIdentifier;

  constructor() {
    this.identifier = new CompanyIdentifier();
  }

  getSortByField(): string {
    return 'commonName';
  }

  getView(): string {
    return this.commonName;
  }
}

// export class UnitFormConfig {
//   requestorControl: FormControlConfig = new FormControlConfig({required:true});
//   countryControl: FormControlConfig = new FormControlConfig({required:true});
//   companyNameControl: FormControlConfig = new FormControlConfig({max:50, required:true});
//   commonNameControl: FormControlConfig = new FormControlConfig({max:50, required:true});
//   companyIdentifierTypeControl: FormControlConfig = new FormControlConfig({required:true});
//   companyIdentifierValueControl: FormControlConfig = new FormControlConfig({max:15, required:true});
//   phoneNumberControl: FormControlConfig = new FormControlConfig({max:20, min:5, mask:['+', /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/]});
//   emailControl: FormControlConfig = new FormControlConfig({max:100});
//   postCodeControl: FormControlConfig = new FormControlConfig({max:15, required:true});
//   cityControl: FormControlConfig = new FormControlConfig({max:25, required:true});
//   streetAndHouseNoControl: FormControlConfig = new FormControlConfig({max:35, required:true});
// }
