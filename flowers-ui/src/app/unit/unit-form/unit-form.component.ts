import {Component, ViewChild} from '@angular/core';
import {Country, Requestor} from "../../common/static-data";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {Unit} from "../unit";
import {CompanyIdentifier, CompanyIdentifierType, FormButton} from "../../common/common";
import {MzModalService, MzSelectContainerComponent} from "ng2-materialize";
import {UserSession} from "../../common/session";
import {UnitService} from "../unit-service";
import {YesNoDialogComponent} from "../../common/popup/yes-no/yes-no-dialog.component";
import {BaseFormComponent} from "../../common/component/base-form.component";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {TranslateService} from "@ngx-translate/core";
import {Permission} from "../../authentication/authentication";

export class UnitButtons {
  deleteBtn:FormButton =  new FormButton(false, false);
  updateBtn:FormButton =  new FormButton(false, false);
  createBtn:FormButton =  new FormButton(false, false);
}

@Component({
  selector: 'app-unit-form',
  templateUrl: './unit-form.component.html',
  styleUrls: ['./unit-form.component.css']
})
export class UnitFormComponent extends BaseFormComponent<Unit>{

  countries: Country[] = [];
  services: Requestor[] = [];
  companyIdentifierTypes: string[] = [];

  doesntHaveRelations: boolean = true;
  formButtons = new UnitButtons();
  duplicateIdenifier: CompanyIdentifier = new CompanyIdentifier();

  @ViewChild("countrySelect")
  countrySelect: MzSelectContainerComponent;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private session: UserSession,
              private modalService: MzModalService,
              private spinnerService: Ng4LoadingSpinnerService,
              private translateService: TranslateService,
              private unitService: UnitService) {
    super(router, session, modalService, spinnerService);
  }

  prepareData(): void {
    if (this.route.snapshot.data['unit']) {
      this.entity = this.route.snapshot.data['unit'];
    } else {
      this.entity = new Unit();
    }
    if (this.route.snapshot.data['valuableDataModifiable'] != null) {
      this.doesntHaveRelations = this.route.snapshot.data['valuableDataModifiable'];
    }
    this.services = this.route.snapshot.data['staticData'].services;
    Object.keys(CompanyIdentifierType).forEach((i) => this.companyIdentifierTypes.push(i));
    this.countries = this.route.snapshot.data['staticData'].countries;
  }

  createForm(): void {
    this.form = this.formBuilder.group({});
  }

  initFormButtons(): void {
    if (!this.entity.id) {
      this.formButtons.createBtn.enabled = this.session.hasPermission(Permission.CREATE_UNIT);
      this.formButtons.createBtn.visible = true;
    } else {
      this.formButtons.updateBtn.enabled = this.session.hasPermission(Permission.EDIT_UNIT);
      this.formButtons.updateBtn.visible = true;
      this.formButtons.deleteBtn.visible = true;
      if (this.doesntHaveRelations) {
        this.formButtons.deleteBtn.enabled = this.session.hasPermission(Permission.EDIT_UNIT);
      }
    }
  }

  initForm() :void {
    if (!this.doesntHaveRelations) {
      this.formContext.getControlConfig('requestor').disabled = true;
      this.formContext.getControlConfig('country').disabled = true;
      this.formContext.getControlConfig('companyName').disabled = true;
      this.formContext.getControlConfig('commonName').disabled = true;
      this.formContext.getControlConfig('identifierType').disabled = true;
      this.formContext.getControlConfig('identifierValue').disabled = true;
    }
  }

  private createSuccess(unit: Unit): void {
    this.actionSuccess(unit);
    this.formButtons.createBtn.visible = false;
    this.formButtons.updateBtn.visible = true;
    this.formButtons.updateBtn.enabled = this.session.hasPermission(Permission.EDIT_UNIT);
    this.formButtons.deleteBtn.visible = true;
    this.formButtons.deleteBtn.enabled = this.session.hasPermission(Permission.EDIT_UNIT);
  }

  createUnit(): void {
    this.spinnerService.show();
    this.unitService.create(this.entity).subscribe((unit) => this.createSuccess(unit),(err) => {
      this.setIdentifierError();
      this.actionFailed(err)
    });
  }

  updateUnit(): void {
    this.spinnerService.show();
    this.unitService.update(this.entity).subscribe((unit) => {this.actionSuccess(unit)}, (err) => {
      this.setIdentifierError();
      this.actionFailed(err)
    });
  }

  deleteUnit(): void {
    this.modalService.open(YesNoDialogComponent, {
      textCode: "unit.messages.delete-confirmation",
      okCallback: () => {
        this.spinnerService.show();
        this.unitService.delete(this.entity).subscribe(
          () => {
            this.router.navigateByUrl("/units");
            this.spinnerService.hide();
          }
        );
      }
    });
  }

  setIdentifierError(){
    Object.assign(this.duplicateIdenifier, this.entity.identifier);
  }

}
