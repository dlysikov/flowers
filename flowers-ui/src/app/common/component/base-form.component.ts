import {AfterViewInit, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {
  CommonHelper, EqualsById, FormControlConfig, FormGlobalMsg, FormGlobalMsgType,
  ValidationUtils
} from "../common";
import {ActivatedRoute, Router} from "@angular/router";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {UserSession} from "../session";
import {MzModalService} from "ng2-materialize";
import {YesNoDialogComponent} from "../popup/yes-no/yes-no-dialog.component";

export abstract class BaseFormComponent<T> implements OnInit, AfterViewInit {

  loaderTemplate: string = `<div class="loader"></div>`;

  public phoneMask = ['+', /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/, /\d/];

  public formChanged:boolean = false;
  public form: FormGroup;
  public formGlobalMsg = new FormGlobalMsg(FormGlobalMsgType.NONE);
  public entity:T;
  public formContext:FormContext<T>;

  constructor(private _router: Router,
              private _session: UserSession,
              private _modalService: MzModalService,
              private _spinnerService: Ng4LoadingSpinnerService) {
  }

  ngOnInit(): void {
    this.prepareData();
    this._session.onLanguageChanged.subscribe(() => this.handleDataWithTranslations());
    this.createForm();
    this.formContext = new FormContext(this.form, this.entity);
    this.initForm();
    this.initFormButtons();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.form.valueChanges.subscribe(() => {
      this.formChanged = true;
      this.formChangedAdditionalActions();
    }), 200);
    this.handleDataWithTranslations();
  }

  public abstract prepareData(): void;

  public handleDataWithTranslations(): void {}

  public initForm(): void {};

  public abstract createForm(): void;

  public initFormButtons(): void {};

  public formChangedAdditionalActions(): void {}

  public fieldFormHasErrors(fieldPath: Array<string> | string): boolean {
    return ValidationUtils.fieldFormHasErrors(this.form, fieldPath);
  }

  public actionFailed(err: any): void {
    ValidationUtils.clearErrors(this.form);
    this.formGlobalMsg = ValidationUtils.getGlobalMmg(ValidationUtils.applyErrorsToForm(this.formContext, err));
    CommonHelper.scrollToBottom();
    this._spinnerService.hide();
  }

  public actionSuccess(entity: T, msgCode?: string): void {
    ValidationUtils.clearErrors(this.form);
    let messageCode = msgCode == null ? 'save-success' : msgCode;
    this.formGlobalMsg = new FormGlobalMsg(FormGlobalMsgType.SUCCESS, messageCode);
    this.entity = entity;
    setTimeout(() => this.formChanged = false, 200);
    CommonHelper.scrollToBottom();
    this._spinnerService.hide();
  }

  public changePage(navigateToURL:string): void {
    this._spinnerService.hide();
    if (this.formChanged) {
      this._modalService.open(YesNoDialogComponent, {
        textCode: "app.unsaved-data",
        okCallback: () => this._router.navigateByUrl(navigateToURL)
      })
    } else {
      this._router.navigateByUrl(navigateToURL);
    }
  }
}

export class FormContext<T> {

  private controlConfigs: Map<string, FormControlConfig>;
  private context: Map<string, any>;
  private disabled: boolean = false;

  constructor(private form: FormGroup,
              private entity:T) {
    this.controlConfigs = new Map<string, FormControlConfig>();
    this.context = new Map<string, any>();
  }

  public getForm(): FormGroup {
    return this.form;
  }

  public getControlConfig(key:string): FormControlConfig {
    let controlConfig = this.controlConfigs.get(key);
    if (controlConfig == null) {
      controlConfig = new FormControlConfig();
      if (this.disabled) {
        controlConfig.disabled = true;
      }
      this.controlConfigs.set(key, controlConfig);
    }
    return controlConfig;
  }

  public disableAll(): void {
    this.controlConfigs.forEach((value) => value.disabled = true);
    this.disabled = true;
  }

  public getEntity(): T {
    return this.entity;
  }

  public put(key:string, value:any): void {
    this.context.set(key, value);
  }

  public get(key:string): any {
    return this.context.get(key);
  }
}
