import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {ManageOrdersComponent} from "./manage-certificate-orders/manage-certificate-orders.component";
import {ManagerCertificateOrdersResolve} from "./manage-certificate-orders/manage-certificate-orders-resolve";
import {StaticDataResolve} from "../../common/static-data-resolve";
import {AuthGuard} from "../../guard/auth.guard";
import {NgxPermissionsGuard} from "ngx-permissions";
import {CertificateOrderFormComponent} from "./certificate-order-form/certificate-order-form.component";
import {CertificateOrderDetailsResolve} from "./certificate-order-form/certificate-order-details-resolve";
import {UserPageResolve} from "./certificate-order-form/user-page-resolve";
import {UserPageGuard} from "../../guard/user-page.guard";
import {UnitsResolve} from "../../common/units-resolve";
import {MobileCodeVerificationComponent} from "../mobile-code-verification/mobile-code-verification.component";
import {Permission} from "../../authentication/authentication";
import {MobileCodePageResolve} from "../mobile-code-verification/mobile-code-page-resolve";

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: "",
      pathMatch: 'full',
      component: ManageOrdersComponent,
      resolve: {orders: ManagerCertificateOrdersResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.READ_CERTIFICATEORDER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "details/:orderId",
      pathMatch: 'full',
      component: CertificateOrderFormComponent,
      resolve: {order: CertificateOrderDetailsResolve, staticData: StaticDataResolve, units:UnitsResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.EDIT_CERTIFICATEORDER, Permission.READ_CERTIFICATEORDER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "details",
      pathMatch: 'full',
      component: CertificateOrderFormComponent,
      resolve: {staticData: StaticDataResolve, units:UnitsResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.CREATE_CERTIFICATEORDER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "user/:pageHash/mobile-code-verification",
      pathMatch: 'full',
      component: MobileCodeVerificationComponent,
      resolve: {required: MobileCodePageResolve}
    },
    {
      path: "user/:pageHash",
      pathMatch: 'full',
      component: CertificateOrderFormComponent,
      resolve: {order: UserPageResolve, staticData: StaticDataResolve, units:UnitsResolve},
      canActivate: [UserPageGuard]
    }
  ])],
  exports: [RouterModule]
})

export class CertificateOrderRoutingModule {
}
