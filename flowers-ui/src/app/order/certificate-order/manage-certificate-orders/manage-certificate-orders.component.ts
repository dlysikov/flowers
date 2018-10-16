import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {CertificateOrder} from "../certificate-order";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateOrderService} from "../certificate-order-service";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {ImportResult} from "../../../common/import-result";
import {MatDialog} from '@angular/material';
import {AlertPopup, AlertType} from "../../../common/popup/alert/alert.component";
import {UserSession} from "../../../common/session";
import {MzModalService} from "ng2-materialize";
import {OrderStatus} from "../../order";
import {BaseListComponent} from "../../../common/component/base-list.component";
import {Field, FieldFilterType, FieldValues} from "../../../common/field-filter/field-filter";
import {Device} from "../certificate-order-form/certificate-order-form.component";

const SUCCESS_IMPORT_MSG: string = "manage-orders.import.success";
const IMPORT_MSG_PREFIX: string = "manage-orders.import.";
const DUPLICATES_IMPORT_MSG: string = "manage-orders.import.duplicates";

@Component({
  selector: 'app-manage-certificate-orders',
  templateUrl: './manage-certificate-orders.component.html',
  styleUrls: ['./manage-certificate-orders.component.css']
})
export class ManageOrdersComponent extends BaseListComponent<CertificateOrder>{

  @ViewChild("ordersXMLInput")
  ordersXMLInput: ElementRef;

  constructor(private route: ActivatedRoute,
              private orderService: CertificateOrderService,
              private dialog: MatDialog,
              private userSession: UserSession,
              private spinnerService: Ng4LoadingSpinnerService,
              private router: Router,
              private modalService: MzModalService) {
    super(route, userSession, "orders", [new Field("id", "order.id", FieldFilterType.INTEGER),
                                         new Field("lrsOrderNumber", "order.lrsOrderNumber", FieldFilterType.STRING_LIKE),
                                         new Field("ssn", "order.holder.ssn", FieldFilterType.STRING_LIKE),
                                         new Field("device", "order.device", FieldFilterType.STRING_EQ, new FieldValues([Device.MOBILE, Device.TOKEN], null, null, 'order.device-type.')),
                                         new Field("status", "manage-orders.list.status", FieldFilterType.STRING_EQ, new FieldValues(Object.values(OrderStatus), null, null, 'order.statuses.')),
                                         new Field("holder.firstName", "order.holder.firstName", FieldFilterType.STRING_LIKE),
                                         new Field("holder.surName", "order.holder.surname", FieldFilterType.STRING_LIKE),
                                         new Field("holder.notifyEmail", "order.holder.notifyEmail", FieldFilterType.STRING_LIKE),
                                         new Field("holder.phoneNumber", "order.holder.phoneNumber", FieldFilterType.STRING_LIKE)],
      orderService, spinnerService, router);
  }

  importOrdersFromXML(event: any) {
    this.spinnerService.show();
    let file: File = event.target.files[0];

    this.orderService.importOrdersFromXML(file).subscribe((ir: ImportResult<CertificateOrder>) => {
      this.resetInputXML();
      if (ir.successful > 0) {
        this.applyFilters(this.currentFilters);
      }
      if (ir.failed == 0) {
        this.showAlert(AlertType.SUCCESS, SUCCESS_IMPORT_MSG, {count: ir.successful});
      } else {
        let body:string = "";
        ir.failedDetails.forEach(o => {
          body += o.holder.surName + " " + o.holder.firstName + "<br/>";
        });
        this.showAlert(AlertType.INFO, DUPLICATES_IMPORT_MSG, {count: ir.successful, body: body});
      }
    }, (response) => {
      this.resetInputXML();
      this.showAlert(AlertType.FAILED, IMPORT_MSG_PREFIX + response.error.globalErrors[0].code, {details: response.error.globalErrors[0]});
    });
  }

  private showAlert(alertType: AlertType, msg: string, params?: any) {
    this.modalService.open(AlertPopup, {
      message: msg,
      param: params,
      alertType: alertType
    });
  }

  private resetInputXML() {
    this.spinnerService.hide();
    this.ordersXMLInput.nativeElement.value = "";
  }
}
