import {AfterViewInit, Component, OnInit} from '@angular/core';
import {BaseFormComponent} from "../../../common/component/base-form.component";
import {ESealCredentials, PublicKey} from "../eseal-order";
import {ActivatedRoute, Router} from "@angular/router";
import {UserSession} from "../../../common/session";
import {MzModalService} from "ng2-materialize";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {FormBuilder} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {ESealOrderService} from "../eseal-order-service";
import {ValidationUtils} from "../../../common/common";
import {ApiErrors, ApiFieldError} from "../../../common/error";

declare var encryptData: any;

@Component({
  selector: 'app-eseal-activation-form',
  templateUrl: './eseal-activation-form.component.html',
  styleUrls: ['./eseal-activation-form.component.css']
})
export class ESealActivationFormComponent extends BaseFormComponent<ESealCredentials>  implements OnInit, AfterViewInit {

  confirmedPassword: string;
  imgLocation = environment.imagesLocation + 'sms-image.png';
  confirmedIsValid: boolean = true;
  publicKey: PublicKey;
  regExp =  new RegExp(/(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,}/);


  constructor(private router: Router,
              private session: UserSession,
              private modalService: MzModalService,
              private spinnerService: Ng4LoadingSpinnerService,
              private formBuilder: FormBuilder,
              private eSealService: ESealOrderService,
              private route: ActivatedRoute) {
    super(router, session, modalService, spinnerService);

  }

  public prepareData(): void {
    this.entity = new ESealCredentials();
    this.publicKey = this.route.snapshot.data['key'];
  }

  public createForm(): void {
    this.form = this.formBuilder.group({
    });
  }


  private setFieldError(fieldName: string) {
    let errors = new ApiErrors();
    errors.fieldErrors = [];
    let fieldError = new ApiFieldError();
    fieldError.field = fieldName;
    fieldError.code = 'invalid';
    errors.fieldErrors.push(fieldError);
    ValidationUtils.applyErrorsToForm(this.formContext, {error: errors});
  }

  checkPass(): boolean {
    let newPassIsValid = true;
    this.confirmedIsValid = true;
    if (this.entity.newPassword && !this.regExp.test(this.entity.newPassword)) {
      this.setFieldError('newPassword');
      newPassIsValid = false;
    }
    if (newPassIsValid && this.confirmedPassword !== this.entity.newPassword) {
      this.confirmedIsValid = false;
    }
    return this.confirmedIsValid && newPassIsValid;
  }

  private getEncryptedCredentials(): ESealCredentials {
    let encryptedCredentials = new ESealCredentials();
    encryptedCredentials.sealId = this.entity.sealId;
    encryptedCredentials.initialPassword = this.entity.initialPassword ? encryptData(this.publicKey.modulus, this.publicKey.exponent, this.entity.initialPassword) : this.entity.initialPassword;
    encryptedCredentials.newPassword = this.entity.newPassword ? encryptData(this.publicKey.modulus, this.publicKey.exponent, this.entity.newPassword) : this.entity.newPassword;
    encryptedCredentials.keyId = this.publicKey.keyId;
    return encryptedCredentials;
  }

  activate() {
    if (this.checkPass()) {
      this.spinnerService.show();
      this.eSealService.activate(this.getEncryptedCredentials()).subscribe((eSealCredentials: ESealCredentials) => {
        this.actionSuccess(eSealCredentials);
      }, (err) => {
        this.actionFailed(err);
      })
    }
  }
}
