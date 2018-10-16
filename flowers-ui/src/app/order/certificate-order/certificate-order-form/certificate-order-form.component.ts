import {Component, ElementRef, ViewChild} from '@angular/core';
import {CertificateType, Country, Nationality, Requestor} from "../../../common/static-data";
import {Address, CertificateOrder, OrderFormConfig} from "../certificate-order";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateOrderService} from "../certificate-order-service";
import {FormBuilder, FormControl, } from "@angular/forms";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {AlertPopup, AlertType} from "../../../common/popup/alert/alert.component";
import {MatDialog} from "@angular/material";
import {TermsAndConditionsComponent} from "../../../common/popup/terms-and-conditions/terms-and-conditions.component";
import * as FileSaver from 'file-saver';
import {UserSession} from "../../../common/session";
import {Authentication, AuthorityType, Permission} from "../../../authentication/authentication";
import {StaticDataService} from "../../../common/static-data-service";
import {ApiFieldError, ApiGlobalError} from "../../../common/error";
import {CommonHelper, FormButton, FormControlConfig,ValidationUtils} from "../../../common/common";
import {MzModalService, MzSelectContainerComponent} from "ng2-materialize";
import {Unit} from "../../../unit/unit";
import {TranslateService} from "@ngx-translate/core";
import {OrderStatus} from "../../order";
import {OrderMobileCodeVerification} from "../../mobile-code-verification/order-mobile-code-verification";
import {TabStorage} from "../../../common/storage";
import {BaseFormComponent} from "../../../common/component/base-form.component";
import {environment} from "../../../../environments/environment";

const ACTION_SAVE_DRAFT_SUCCESS_MSG = "order.ui.messages.order-saving.draft-success";
const ACTION_USER_VALIDATES_SUCCESS_MSG = "order.ui.messages.order-saving.user-validation-success";
const ACTION_SERVER_ERROR = "order.ui.messages.order-saving.server-error";

export enum OrderFormActionStatus {
  NONE = "NONE",
  FAILED = "FAILED",
  SUCCESS = "SUCCESS"
}

export enum Device {
  TOKEN = "TOKEN",
  MOBILE = "MOBILE"
}

export class OrderFormButtons{
  backToList: FormButton = new FormButton(true, true);
  saveAsDraft: FormButton = new FormButton(false, false);
  saveAsUserDraft: FormButton = new FormButton(false, false);
  reject: FormButton = new FormButton(false, false);
  signAndSend: FormButton = new FormButton(false, false);
  userValidate: FormButton = new FormButton(false, false);
}

@Component({
  selector: 'app-certificate-order-form',
  templateUrl: './certificate-order-form.component.html',
  styleUrls: ['./certificate-order-form.component.css']
})
export class CertificateOrderFormComponent extends BaseFormComponent<CertificateOrder>  {

  loaderTemplate: string = `<div class="loader"></div>`;

  userPageAuthCode:OrderMobileCodeVerification;

  deviceList  = [Device.MOBILE, Device.TOKEN];
  certificateTypeList = [CertificateType.PROFESSIONAL_PERSON, CertificateType.PROFESSIONAL_CERTIFICATE_ADMINISTRATOR, CertificateType.PRIVATE];
  roleTypeList = [AuthorityType.DIA, AuthorityType.END_USER];
  certificateLevelList  = ['LCP', 'NCP'/*, 'QCP'*/];

  nationalities: Nationality[] = [];
  countries: Country[] = [];
  services: Requestor[] = [];
  units: Unit[] = [];
  orderFormConfig: OrderFormConfig = new OrderFormConfig();

  formActionStatus: OrderFormActionStatus = OrderFormActionStatus.NONE;
  formActionResultMessage: string;

  actionButtons: OrderFormButtons = new OrderFormButtons();

  @ViewChild("deviceSelect")
  deviceSelect: MzSelectContainerComponent;
  @ViewChild("holderDocuments")
  holderDocuments: ElementRef;
  imgLocation = environment.imagesLocation;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private dialog: MatDialog,
              private userSession: UserSession,
              private tabStorage: TabStorage,
              private router: Router,
              private spinnerService: Ng4LoadingSpinnerService,
              private orderService: CertificateOrderService,
              private translateService: TranslateService,
              private staticDataService: StaticDataService,
              private modalService: MzModalService) {
    super(router, userSession, modalService, spinnerService);
  }

  prepareData(): void {
    if (this.route.snapshot.data['order']) {
      this.entity = this.route.snapshot.data['order'];
    } else {
      this.entity = new CertificateOrder();
    }
    this.nationalities = this.route.snapshot.data['staticData'].nationalities;
    this.countries = this.route.snapshot.data['staticData'].countries;
    this.services = this.route.snapshot.data['staticData'].services;
    this.units = this.route.snapshot.data['units'];
  }

  createForm(): void {
    this.form = this.formBuilder.group({
      acceptedGTC: new FormControl({
        value: this.entity.acceptedGTC,
        disabled: this.orderFormConfig.acceptedGTC.disabled
      })
    });
  }


  public handleDataWithTranslations(): void {
    setTimeout(() => {
      if(this.deviceSelect) {
        this.deviceSelect.mzSelectDirective.updateMaterialSelect();
        this.setDeviceLabel();
      }
    }, 300);
  }

  setDeviceLabel() {
    if (this.entity.device) {
      this.translateService.get('order.device-label.' + this.entity.device).subscribe((val) => {
        this.deviceSelect.mzSelectDirective.inputElement.val(val);
      });
    }
  }

  ngOnInit() {
    super.ngOnInit();
    this.userPageAuthCode = this.tabStorage.getItem("orderUserMobileCode");

    this.initFields();

    this.initActionButtons();
    this.initRequiredFields();
  }


  ngAfterViewInit(): void {
    super.ngAfterViewInit();
    this.setDeviceLabel();
  }

  isUserValidationPage(): boolean {
    return this.entity.status === OrderStatus.USER_DRAFT && this.route.snapshot.params["pageHash"];
  }

  public initForm(): void {
    let documentsControlConfig = this.formContext.getControlConfig("holder-documents");
    documentsControlConfig.initDefaultValues();

    let deviceControlConfig = this.formContext.getControlConfig("device");
    deviceControlConfig.initDefaultValues();

    this.form.addControl("holder-documents", new FormControl({visible: documentsControlConfig.visible}));
    this.form.addControl("device", new FormControl({value: this.entity.device, disabled: deviceControlConfig.disabled}));
  }

  private initFields() {
    if (!this.isUserValidationPage() && (!this.userSession.hasAnyPermission(Permission.EDIT_CERTIFICATEORDER, Permission.CREATE_CERTIFICATEORDER) || this.entity.status === OrderStatus.USER_DRAFT)) {
      this.formContext.disableAll();
    }

    if (this.isUserValidationPage()) {
      this.staticDataService.getServiceConfig(this.entity.unit.requestor.id).subscribe(data => {
        this.formContext.getControlConfig('remoteId').visible = data.remoteId;
      });
      this.formContext.getControlConfig('unit').visible = false;
      this.formContext.getControlConfig('holder-certificateType').visible = false;
      this.formContext.getControlConfig('holder-roleType').visible = false;
      this.formContext.getControlConfig('id').visible = false;
      this.formContext.getControlConfig('lrsOrderNumber').visible = false;
      this.formContext.getControlConfig('ssn').visible = false;

      this.formContext.getControlConfig('holder-activationCode').visible = true;
      this.formContext.getControlConfig('acceptedGTC').visible = true;

      this.orderFormConfig.holder.documents.required = true;
      this.orderFormConfig.holder.documents.visible = true;
      this.orderFormConfig.holder.documentsSelect.visible = true;
    }

    if (this.isAdminValidationPage()) {
      this.orderFormConfig.holder.documents.visible = true;
      this.formContext.getControlConfig('remoteId').visible = true;
      this.formContext.getControlConfig('remoteId').disabled = true;
    }

    if (this.isDiaValidationPage()) {
      this.orderFormConfig.holder.documents.visible = true;
    }

    if (this.userSession.isSignedIn()) {

      if (this.isAuthenticationHasUnit()) {
        this.formContext.getControlConfig('unit').disabled = true;
        this.entity.unit = this.userSession.getAuthentication().unit;
      }

      if (!this.userSession.hasAuthority(AuthorityType.FLOWERS_ADMIN)) {
        this.formContext.getControlConfig('holder-roleType').visible = false;
        this.entity.holder.roleType = AuthorityType.END_USER;
      }

      if (this.isShortFlow() && this.entity.status == OrderStatus.DRAFT) {
        this.formContext.getControlConfig('holder-activationCode').visible = true;
        this.entity.acceptedGTC = true;
      }
    }

    this.formContext.getControlConfig('holder-certificateLevel').visible = false;
    this.entity.holder.certificateLevel = "LCP";
    this.formContext.getControlConfig('holder-certificateType').visible = false;
    this.entity.holder.certificateType = CertificateType.PRIVATE;


  }

  private initRequiredFields() {
    this.formActionStatus = OrderFormActionStatus.NONE;
    let order = new CertificateOrder();
    order.status = this.entity.status;
    if (this.isUserValidationPage()) {
      this.orderService.getRequiredFieldsForUser(order, this.route.snapshot.params['pageHash'], this.userPageAuthCode.mobileCode).subscribe((result: any) => {
        ValidationUtils.setRequiredFields(this.formContext, result);
      },(err) => {console.log(err)});
    } else {
      this.orderService.getRequiredFields(order).subscribe((result: any) => {
        ValidationUtils.setRequiredFields(this.formContext, result);
      }, (err) => {console.log(err)});
    }
  }

  deviceChanged(device, translatePrefix): void {
    if (Device.TOKEN != this.formContext.getEntity().device) {
      this.formContext.getEntity().tokenSerialNumber = null;
      // this.formContext.getEntity().address = null;
    } else {
      // this.formContext.getEntity().address = new Address();
    }
    this.translateService.get(translatePrefix + device).subscribe((val) => {
      this.deviceSelect.mzSelectDirective.inputElement.val(device != '0: null' ? val : '');
    });
  }

  private isAuthenticationHasUnit(): boolean {
    return this.userSession.getAuthentication().unit != null;
  }

  private initActionButtons() {
    this.actionButtons.saveAsDraft.visible = (this.entity.status == OrderStatus.DRAFT || this.entity.status == OrderStatus.USER_DRAFT || !this.entity.status) && !this.isUserValidationPage() &&
      this.userSession.hasAnyPermission(Permission.CREATE_CERTIFICATEORDER);
    this.actionButtons.saveAsUserDraft.visible = (this.entity.status == OrderStatus.DRAFT || this.entity.status == OrderStatus.USER_DRAFT || !this.entity.status) && !this.isUserValidationPage() && !this.isShortFlow() &&
      this.userSession.hasAnyPermission(Permission.EDIT_CERTIFICATEORDER, Permission.CREATE_CERTIFICATEORDER);
    this.actionButtons.reject.visible = this.entity.status == OrderStatus.REMOTE_IDENTIFICATION_REQUIRED || this.entity.status == OrderStatus.FACE_2_FACE_REQUIRED ||
      this.entity.status == OrderStatus.CSD_REVIEW_REQUIRED || this.entity.status == OrderStatus.DIA_SIGNING_REQUIRED && this.userSession.hasAnyPermission(Permission.EDIT_CERTIFICATEORDER);
    this.actionButtons.signAndSend.visible = (this.entity.status == OrderStatus.REMOTE_IDENTIFICATION_REQUIRED || this.entity.status == OrderStatus.FACE_2_FACE_REQUIRED ||
      this.entity.status == OrderStatus.CSD_REVIEW_REQUIRED || this.entity.status == OrderStatus.DIA_SIGNING_REQUIRED ||(this.isShortFlow() && this.entity.status == OrderStatus.DRAFT)) &&
      this.userSession.hasAnyPermission(Permission.SIGN_AND_SEND_CERTIFICATEORDER);
    this.actionButtons.backToList.visible = !this.isUserValidationPage();
    this.actionButtons.userValidate.visible = this.isUserValidationPage();

    if (this.isUserValidationPage()) {
      this.actionButtons.userValidate.enabled = true;
    }

    if (this.entity.status == OrderStatus.DRAFT) {
      this.actionButtons.saveAsDraft.enabled = this.userSession.hasPermission(Permission.CREATE_CERTIFICATEORDER);
      this.actionButtons.saveAsUserDraft.enabled = this.userSession.hasAnyPermission(Permission.CREATE_CERTIFICATEORDER, Permission.EDIT_CERTIFICATEORDER);
      this.actionButtons.signAndSend.enabled = this.isShortFlow() && this.userSession.hasPermission(Permission.SIGN_AND_SEND_CERTIFICATEORDER);
    }

    if (this.isAdminValidationPage() || this.isDiaValidationPage()) {
      this.actionButtons.reject.enabled = this.userSession.hasPermission(Permission.EDIT_CERTIFICATEORDER);
      this.actionButtons.signAndSend.enabled = this.userSession.hasPermission(Permission.SIGN_AND_SEND_CERTIFICATEORDER);
    }
  }

  isShortFlow(): boolean {
    return this.userSession.isSignedIn() && this.isAuthenticationHasUnit() && this.userSession.getAuthentication().unit.requestor.config.shortFlow;
  }

  isAdminValidationPage(): boolean {
    return this.userSession.hasPermission(Permission.SIGN_AND_SEND_CERTIFICATEORDER) &&
      (this.entity.status == OrderStatus.REMOTE_IDENTIFICATION_REQUIRED ||
       this.entity.status == OrderStatus.FACE_2_FACE_REQUIRED ||
       this.entity.status == OrderStatus.CSD_REVIEW_REQUIRED);
  }


  isDiaValidationPage(): boolean {
    return this.entity.status == OrderStatus.DIA_SIGNING_REQUIRED &&
           this.userSession.hasPermission(Permission.SIGN_AND_SEND_CERTIFICATEORDER) &&
           this.userSession.getAuthentication().unit &&
           this.userSession.getAuthentication().unit.id == this.entity.unit.id;
  }

  isOrderFromAPI(): boolean {
    return this.entity.userExternalId != null;
  }

  isDownloadShown(isUploadedByUser): boolean {
    return (this.isAdminValidationPage() || this.isDiaValidationPage()) || (this.isOrderFromAPI() && !isUploadedByUser);
  }

  backToOrders(): void {
    this.spinnerService.hide();
    this.router.navigateByUrl("/certificate-orders");
  }

  saveAsDraft(): void {
    this.prepareActionWithOrder();
    if (this.isNewOrder()) {
      this.orderService.saveAsDraft(this.entity).subscribe((o) => this.saveAsDraftSuccess(o), (e) => this.saveFailedWithMandatoryMsg(e));
    } else {
      this.orderService.updateDraft(this.entity).subscribe((o) => this.saveAsDraftSuccess(o), (e) => this.saveFailedWithMandatoryMsg(e));
    }
  }

  createOrder(): void {
    this.prepareActionWithOrder();
    if (this.isNewOrder()) {
      this.orderService.submitOrderWithoutDraft(this.entity).subscribe(() => this.backToOrders(), (e) => this.saveFailedWithMandatoryMsg(e));
    } else {
      this.orderService.submitOrder(this.entity).subscribe(() => this.backToOrders(), (e) => this.saveFailedWithMandatoryMsg(e));
    }
  }

  userValidates(): void {
    this.prepareActionWithOrder();
    let userUploadedDocuments = [];

    if (this.entity.holder.documents && this.entity.holder.documents.length > 0) {
      this.entity.holder.documents.forEach((document) => {
        if (document.isUploadedByUser) {
          userUploadedDocuments.push(document);
        }
      });
    }

    if (userUploadedDocuments.length > 0) {
      this.orderService.uploadDocuments(this.route.snapshot.params['pageHash'], userUploadedDocuments, this.userPageAuthCode.mobileCode).subscribe(
        (data) => {
          this.setDocumentToUploadedStatus(data);
          this.orderService.submitUserValidates(this.entity, this.route.snapshot.params['pageHash'], this.userPageAuthCode.mobileCode).subscribe(() => this.userValidatesSuccess(), (e) => this.saveFailedWithMandatoryMsg(e))
        },
        (e) => this.saveFailedWithMandatoryMsg(e)
      );
    } else {
      this.orderService.submitUserValidates(this.entity, this.route.snapshot.params['pageHash'], this.userPageAuthCode.mobileCode).subscribe(() => this.userValidatesSuccess()
        , (e) => this.saveFailedWithMandatoryMsg(e));
    }

  }

  private setDocumentToUploadedStatus(documentList) {
    this.entity.holder.documents.forEach((document) => {
      document.isUploadedByUser = false;
      documentList.forEach((doc) => {
        if(doc.name === document.name) {
          document.id = doc.id;
        }
      })
    });
  }

  private userValidatesSuccess(): void {
    this.formActionStatus = OrderFormActionStatus.SUCCESS;
    this.formActionResultMessage = ACTION_USER_VALIDATES_SUCCESS_MSG;
    this.actionButtons.userValidate.enabled = false;
    this.spinnerService.hide();
    CommonHelper.scrollToBottom();
  }

  signAndSend(): void {
    this.prepareActionWithOrder();
    if (!this.isNewOrder()) {
      this.orderService.sendToLrs(this.entity).subscribe(() => this.backToOrders(), (e) => this.saveFailedWithMandatoryMsg(e));
    } else {
      this.orderService.sendToLrsFromDraft(this.entity).subscribe(() => this.backToOrders(), (e) => this.saveFailedWithMandatoryMsg(e));
    }
  }

  reject(): void {
    this.prepareActionWithOrder();
    this.orderService.reject(this.entity.id).subscribe(() => this.backToOrders(), (e) => this.saveFailedWithMandatoryMsg(e));
  }

  private prepareActionWithOrder(): void {
    this.formActionStatus = OrderFormActionStatus.NONE;
    this.spinnerService.show();
  }

  isAgreementMandatory(): boolean {
    let isMandatory = "QCP" !== this.entity.holder.certificateLevel;
    if (!isMandatory) {
      this.entity.acceptedGTC = false;
    }
    return isMandatory;
  }

  private isDocumentExist(file): boolean {
    for (let document of this.entity.holder.documents) {
      if (document.name == file.name && document.type == file.type) {
        return true;
      }
    }
    return false;
  }

  addFilesToQueue(event): void {
    for (let file of event.target.files) {
      if (file.size > this.orderFormConfig.holder.documents.max) {

        this.modalService.open(AlertPopup, {
          message: "errors.to.big.file",
          alertType: AlertType.FAILED,
          param: {
            details: {
              details: ["30MB", "90MB"]
            }
          }

        });
      } else {
        if (!this.isDocumentExist(file)) {
          file.isUploadedByUser = true;
          this.entity.holder.documents.push(file);
          this.form.get(['holder-documents']).updateValueAndValidity();
          if (this.form.valid) {
            this.formActionStatus = OrderFormActionStatus.NONE;
          }
        }
      }
    }
    this.holderDocuments.nativeElement.value = "";
  }

  downloadDocument(document: any) {
    if (this.isOrderFromAPI() && this.isUserValidationPage()) {
      this.orderService.getDocumentToUser(this.route.snapshot.params['pageHash'], document.id, this.userPageAuthCode.mobileCode).subscribe((file) => FileSaver.saveAs(file, document.name));
    } else {
      this.orderService.getDocument(this.entity.id, document.id).subscribe((file) => FileSaver.saveAs(file, document.name));
    }
  }

  removeDocument(index: number, document: any) {
    if (document.isUploadedByUser == true) {
      this.removeDocumentFromList(index);
    } else {
      this.removeDocumentFromServer(document.name, index);
    }
  }

  removeDocumentFromList(index) {
    this.entity.holder.documents.splice(index, 1);
  }

  removeDocumentFromServer(documentName: string, index: number) {
    this.spinnerService.show();
    this.orderService.deleteDocument(this.route.snapshot.params['pageHash'], documentName, this.userPageAuthCode.mobileCode).subscribe(() => {
      this.removeDocumentFromList(index);
      this.spinnerService.hide();
    }, () => {
      this.spinnerService.hide();
    });
  }

  getRoundedValue(data): any {
    if (data) {
      return Math.round(data);
    }
  }

  showGlobalTermsAndConditions(): void {
    this.modalService.open(TermsAndConditionsComponent, {
      okCallback: () =>  {
        this.entity.acceptedGTC = true;
    },
      noCallback: () =>  {
        this.entity.acceptedGTC = false;
      }
    });

  }

  private saveFailedWithMandatoryMsg(err: any) {
    this.saveFailed(err);
    if (!this.form.valid) {
      this.formActionResultMessage = "order.ui.messages.order-saving.validation-error";
      this.formActionStatus = OrderFormActionStatus.FAILED;
    }
    CommonHelper.scrollToBottom();
  }

  private saveFailed(err: any): void {
    ValidationUtils.clearErrors(this.form);
    let errors = ValidationUtils.applyErrorsToForm(this.formContext, err);
    if (errors.globalErrors.length > 0) {
      this.formActionStatus = OrderFormActionStatus.FAILED;
      this.formActionResultMessage = ACTION_SERVER_ERROR;
      errors.globalErrors.forEach((error: ApiGlobalError) => {
        if ("LRS_REGISTER_FAILURE" == error.details) {
          this.formActionResultMessage = 'order.ui.messages.order-saving.lrs_failure';
          if (error.defaultMessage.startsWith("Error 115")) {
            this.formActionResultMessage = "order.ui.messages.order-saving.validation-error";
            let error = new ApiFieldError();
            error.code = "duplicate";
            error.field = "tokenSerialNumber";
            let control = this.form.get('tokenSerialNumber');
            control.setErrors(error);
            control.errors["duplicate"] = true;
            errors.fieldErrors.push(error);
          }
        }
      });
    }
    this.spinnerService.hide();
    CommonHelper.scrollToBottom();
  }

  private saveAsDraftSuccess(order?: CertificateOrder): void {
    if (order) {
      this.entity.id = order.id;
      this.entity.requestDate = order.requestDate;
    }
    this.formActionStatus = OrderFormActionStatus.SUCCESS;
    this.formActionResultMessage = ACTION_SAVE_DRAFT_SUCCESS_MSG;
    setTimeout(() => this.formChanged = false, 200);
    this.spinnerService.hide();
    CommonHelper.scrollToBottom();
  }

  private isNewOrder(): boolean {
    return !this.entity.id;
  }

  getRequired(config: FormControlConfig): string {
    return CommonHelper.getRequired(config);
  }
}
