import {AbstractOrder} from "../order";
import {FormButton} from "../../common/common";
import {User} from "../../user/user";

export class ESealOrder extends AbstractOrder {
  organisationalUnit:string;
  eSealAdministratorEmail:string;
  eSealManagers: User[];
}

export class ESealButtons {
  saveAsDraft:FormButton =  new FormButton(false, false);
  save:FormButton =  new FormButton(false, false);
  sendToLrs:FormButton =  new FormButton(false, false);
  activate:FormButton =  new FormButton(false, false);
}

export class ESealCredentials {
  sealId: string;
  initialPassword: string;
  newPassword: string;
  keyId: string;
}

export class ResultDTO {
   resultCode: string;
   additionalInfo: string;
   message: string;
}

export class PublicKey {
  result: ResultDTO;
  modulus: string;
  exponent: string;
  keyId: string;
}
