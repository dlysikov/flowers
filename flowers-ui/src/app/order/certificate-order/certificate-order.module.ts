import {NgModule} from '@angular/core';
import {SharedModule} from "../../shared.module";
import {CertificateOrderRoutingModule} from "./certificate-order-routing.module";
import {ManageOrdersComponent} from './manage-certificate-orders/manage-certificate-orders.component';
import {ManagerCertificateOrdersResolve} from "./manage-certificate-orders/manage-certificate-orders-resolve";
import {CertificateOrdersListComponent} from "./certificate-orders-list/certificate-orders-list.component";
import {RouterModule} from "@angular/router";
import {CertificateOrderFormComponent} from './certificate-order-form/certificate-order-form.component';
import {CertificateOrderDetailsResolve} from "./certificate-order-form/certificate-order-details-resolve";
import {UserPageResolve} from "./certificate-order-form/user-page-resolve";
import {MobileCodeVerificationComponent} from "../mobile-code-verification/mobile-code-verification.component";
import {MobileCodePageResolve} from "../mobile-code-verification/mobile-code-page-resolve";

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    CertificateOrderRoutingModule
  ],
  declarations: [ManageOrdersComponent,
    CertificateOrdersListComponent,
    CertificateOrderFormComponent,
    MobileCodeVerificationComponent
  ],
  providers: [ManagerCertificateOrdersResolve, MobileCodePageResolve, CertificateOrderDetailsResolve, UserPageResolve]
})
export class CertificateOrderModule {
}
