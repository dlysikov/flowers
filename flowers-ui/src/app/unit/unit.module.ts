import {NgModule} from '@angular/core';
import {SharedModule} from "../shared.module";
import {RouterModule} from "@angular/router";
import {UnitRoutingModule} from "./unit-routing.module";
import {UnitFormComponent} from "./unit-form/unit-form.component";
import {UnitDetailsResolve} from "./unit-form/unit-details-resolve";
import {UnitModifyStatusResolve} from "./unit-form/unit-modify-status-resolve";
import {UnitsListComponent} from "./units-list/units-list.component";
import { ManageUnitsComponent } from './manage-units/manage-units.component';
import {ManagerUnitsResolve} from "./manage-units/manage-units.resolve";

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    UnitRoutingModule,
  ],
  declarations: [UnitFormComponent, UnitsListComponent, ManageUnitsComponent],
  providers: [UnitDetailsResolve, UnitModifyStatusResolve, ManagerUnitsResolve]
})
export class UnitModule {
}
