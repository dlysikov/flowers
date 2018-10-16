import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../guard/auth.guard";
import {NgxPermissionsGuard} from "ngx-permissions";
import {StaticDataResolve} from "../common/static-data-resolve";
import {UnitFormComponent} from "./unit-form/unit-form.component";
import {UnitDetailsResolve} from "./unit-form/unit-details-resolve";
import {UnitModifyStatusResolve} from "./unit-form/unit-modify-status-resolve";
import {ManageUnitsComponent} from "./manage-units/manage-units.component";
import {ManagerUnitsResolve} from "./manage-units/manage-units.resolve";
import {Permission} from "../authentication/authentication";

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: "details/:unitId",
      pathMatch: 'full',
      component: UnitFormComponent,
      resolve: {staticData: StaticDataResolve, unit: UnitDetailsResolve, valuableDataModifiable: UnitModifyStatusResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.READ_UNIT, Permission.EDIT_UNIT],
          redirectTo: "/"
        }
      }
    },
    {
      path: "details",
      pathMatch: 'full',
      component: UnitFormComponent,
      resolve: {staticData: StaticDataResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.CREATE_UNIT],
          redirectTo: "/"
        }
      }
    },
    {
      path: "",
      pathMatch: 'full',
      component: ManageUnitsComponent,
      canActivate: [AuthGuard, NgxPermissionsGuard],
      resolve: {units: ManagerUnitsResolve},
      data: {
        permissions: {
          only: [Permission.READ_UNIT],
          redirectTo: "/"
        }
      }
    },
  ])],
  exports: [RouterModule]
})

export class UnitRoutingModule {
}
