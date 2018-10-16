import {Component} from '@angular/core';
import {FormBuilder, FormControl} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseFormComponent} from "../../common/component/base-form.component";
import {OrderMobileCodeVerification} from "./order-mobile-code-verification";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {MzModalService} from "ng2-materialize";
import {UserSession} from "../../common/session";
import {FormControlConfig, ValidationUtils} from "../../common/common";
import {CertificateOrderService} from "../certificate-order/certificate-order-service";
import {TabStorage} from "../../common/storage";

@Component({
  selector: 'app-orders-mobile-code-verification',
  templateUrl: './mobile-code-verification.component.html',
  styleUrls: ['./mobile-code-verification.component.css']
})
export class MobileCodeVerificationComponent extends BaseFormComponent<OrderMobileCodeVerification> {

  formConfig = new MobileCodeVerificationFormConfig();

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private orderService: CertificateOrderService,
              private session: UserSession,
              private storage: TabStorage,
              private spinnerService: Ng4LoadingSpinnerService,
              private modalService: MzModalService) {
    super(router, session, modalService, spinnerService);
  }

  public prepareData(): void {
    this.entity = new OrderMobileCodeVerification();
    this.entity.pageHash = this.route.snapshot.params['pageHash'];
  }

  public createForm(): void {
    this.form = this.formBuilder.group({
      mobileCode: new FormControl({value: this.entity.mobileCode, disabled: this.formConfig.mobileCode.disabled})
    });
  }


  public initFormButtons(): void {}

  public initForm(): void {}

  public submit(): void {
    this.spinnerService.show();
    this.orderService.verifyMobileCode(this.entity).subscribe(
      (valid) => {
        ValidationUtils.clearErrors(this.form);
        this.spinnerService.hide();
        if (!valid) {
          this.form.get("mobileCode").setErrors({"invalid": true});
        } else {
          this.storage.setItem("orderUserMobileCode", this.entity);
          this.router.navigateByUrl("/certificate-orders/user/"+this.entity.pageHash);
        }
      },
      (err) => {
        if (err.status == 404) {
           this.router.navigate(["/404"]);
        } else {
          this.actionFailed(err);
        }
      }
    );
  }
}

export class MobileCodeVerificationFormConfig {
  mobileCode: FormControlConfig = new FormControlConfig({max:7})
}
