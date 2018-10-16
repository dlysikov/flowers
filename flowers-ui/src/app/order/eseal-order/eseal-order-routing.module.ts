import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../../guard/auth.guard";
import {NgxPermissionsGuard} from "ngx-permissions";
import {ManageESealOrdersComponent} from "./manage-eseal-orders/manage-eseal-orders.component";
import {ManageESealOrdersResolve} from "./manage-eseal-orders/manage-eseal-orders.resolve";
import {StaticDataResolve} from "../../common/static-data-resolve";
import {ESealOrderFormComponent} from "./eseal-order-form/eseal-order-form.component";
import {UnitsResolve} from "../../common/units-resolve";
import {ESealManagersResolve} from "./eseal-managers-resolve";
import {ESealDetailsResolve} from "./eseal-order-form/eseal-details-resolve";
import {Permission} from "../../authentication/authentication";
import {ESealActivationFormComponent} from "./eseal-activation-form/eseal-activation-form.component";
import {ESealActivationResolve} from "./eseal-activation-form/eseal-activation-resolve";

@NgModule({
  imports: [RouterModule.forChild([
    {
    path: "",
    pathMatch: 'full',
    component: ManageESealOrdersComponent,
    resolve: {eseals: ManageESealOrdersResolve},
    canActivate: [AuthGuard, NgxPermissionsGuard],
    data: {
      permissions: {
        only: [Permission.READ_ESEALORDER],
        redirectTo: "/"
      }
    }
  },
    {
      path: "details",
      pathMatch: 'full',
      component: ESealOrderFormComponent,
      resolve: {units: UnitsResolve, managers: ESealManagersResolve, staticData: StaticDataResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.CREATE_ESEALORDER],
          redirectTo: "/"
        }
      }
    },
    {
      path: "details/:eSealId",
      pathMatch: 'full',
      component: ESealOrderFormComponent,
      resolve: {eSeal: ESealDetailsResolve, units: UnitsResolve, managers: ESealManagersResolve, staticData: StaticDataResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.EDIT_ESEALORDER, Permission.READ_ESEALORDER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "details/:eSealId/activation",
      pathMatch: 'full',
      component: ESealActivationFormComponent,
      resolve: {key: ESealActivationResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.ACTIVATE_ESEALORDER],
          redirectTo: "/"
        }
      },
    }
  ])],
  exports: [RouterModule]
})

export class ESealOrderRoutingModule {
}
