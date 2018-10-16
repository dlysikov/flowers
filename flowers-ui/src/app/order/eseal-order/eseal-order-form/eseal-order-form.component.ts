import {AfterViewInit, Component, OnInit} from '@angular/core';
import {User} from "../../../user/user";
import {BaseFormComponent} from "../../../common/component/base-form.component";
import {ESealButtons, ESealOrder} from "../eseal-order";
import {ActivatedRoute, Router} from "@angular/router";
import {UserSession} from "../../../common/session";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {TranslateService} from "@ngx-translate/core";
import {FormBuilder} from "@angular/forms";
import {MzModalService} from "ng2-materialize";
import {Unit} from "../../../unit/unit";
import {ESealOrderService} from "../eseal-order-service";
import {YesNoDialogComponent} from "../../../common/popup/yes-no/yes-no-dialog.component";
import {OrderStatus} from "../../order";
import {Requestor} from "../../../common/static-data";
import {Authentication, Permission} from "../../../authentication/authentication";

@Component({
  selector: 'app-eseal-order-form',
  templateUrl: './eseal-order-form.component.html',
  styleUrls: ['./eseal-order-form.component.css']
})
export class ESealOrderFormComponent extends BaseFormComponent<ESealOrder>  implements OnInit, AfterViewInit{

  services: Requestor[] = [];
  units: Unit[] = [];
  managers: User[]= [];
  managersChanged: boolean = false;
  adminEmailChanged: boolean = false;
  requestor: Requestor;
  private authentication: Authentication;
  formButtons: ESealButtons = new ESealButtons();

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private session: UserSession,
              private translateService:TranslateService,
              private modalService: MzModalService,
              private spinnerService: Ng4LoadingSpinnerService,
              private eSealOrderService: ESealOrderService,
              private userSession: UserSession) {
    super(router, session, modalService, spinnerService);
    this.authentication = this.userSession.getAuthentication();
  }

  prepareData(): void {
    if (this.route.snapshot.data['eSeal']) {
      this.entity = this.route.snapshot.data['eSeal'];
      if (this.entity.unit) {
        this.requestor = this.entity.unit.requestor;
      }
    } else {
      this.entity = new ESealOrder();
    }

    this.units = this.route.snapshot.data['units'];
    this.managers = this.route.snapshot.data['managers'];
    this.services = this.route.snapshot.data['staticData'].services;
  }

  createForm(): void {
    this.form = this.formBuilder.group({});
  }

  managersChangedAction(): (value:any) => void {
    return (value:any) => this.managersChanged = true;
  }

  adminEmailChangedAction(): (value:any) => void {
    return (value:any) => this.adminEmailChanged = true;
  }

  initForm(): void {
    if (!this.userSession.hasAnyPermission(Permission.CREATE_ESEALORDER, Permission.EDIT_ESEALORDER)) {
      this.formContext.disableAll();
    } else {
      if (this.entity.status != OrderStatus.DRAFT) {
        this.formContext.getControlConfig('publish').disabled = true;
        this.formContext.getControlConfig('acceptedGTC').disabled = true;
        this.formContext.getControlConfig('unit').disabled = true;
      }
    }
  }

  initFormButtons(): void {
    if (this.entity.status == OrderStatus.DRAFT) {
      this.formButtons.saveAsDraft.visible = this.userSession.hasAnyPermission(Permission.CREATE_ESEALORDER, Permission.EDIT_ESEALORDER);
      this.formButtons.sendToLrs.visible = this.userSession.hasPermission(Permission.SIGN_AND_SEND_ESEALORDER);
      this.formButtons.sendToLrs.enabled = this.entity.acceptedGTC;
    } else if (this.entity.status == OrderStatus.LRS_PRODUCED && this.userSession.hasPermission(Permission.ACTIVATE_ESEALORDER)) {
      this.formButtons.activate.enabled = true;
      this.formButtons.activate.visible = true;
    }
    else {
      this.formButtons.save.visible = this.userSession.hasAnyPermission(Permission.EDIT_ESEALORDER);
    }
  }

  selectUnitAction(): () => void {
    return () => this.entity.eSealManagers = [];
  }

  saveESeal() {
    if (this.entity.id) {
      if (this.adminEmailChanged || this.managersChanged) {
        this.saveConfirmation();
      } else {
        this.createUpdateESeal();
      }
    } else {
       this.createUpdateESeal();
    }
  }

  saveESealAsDraft() {
    this.spinnerService.show();
    this.eSealOrderService.saveESealAsDraft(this.entity).subscribe(
      (eseal: ESealOrder) => {
        this.actionSuccess(eseal);
      },
      (e) => this.actionFailed(e));
  }

  private createUpdateESeal() {
    this.spinnerService.show();
    this.eSealOrderService.saveESeal(this.entity).subscribe(
      (eseal: ESealOrder) => {
        this.actionSuccess(eseal);
        setTimeout(() => { this.adminEmailChanged = false; this.managersChanged = false;}, 300)
      },
      (e) => this.actionFailed(e));
  }

  private saveConfirmation() {
    this.modalService.open(YesNoDialogComponent, {
      textCode: "eseal.messages.update-managers",
      okCallback: () => {
        this.createUpdateESeal();
      }
    })
  }

  sendToLRS() {
    this.spinnerService.show();
    this.eSealOrderService.sendToLRS(this.entity).subscribe(
      (eseal: ESealOrder) => {
        this.spinnerService.hide();
        this.router.navigateByUrl("/eseal-orders");
       },
      (e) => this.actionFailed(e));
  }

  eSealManagersCondition():(value: User) => boolean {
    return (value:User) => {
      return this.entity.unit != null && value.unit != null && this.entity.unit.id == value.unit.id;
    };
  }

}
