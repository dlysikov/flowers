import {NgModule} from '@angular/core';
import {SharedModule} from "../../shared.module";
import {RouterModule} from "@angular/router";
import {ESealOrderRoutingModule} from "./eseal-order-routing.module";
import {ManageESealOrdersComponent} from "./manage-eseal-orders/manage-eseal-orders.component";
import {ESealOrderListComponent} from "./eseal-order-list/eseal-order-list.component";
import {ManageESealOrdersResolve} from "./manage-eseal-orders/manage-eseal-orders.resolve";
import { ESealOrderFormComponent } from './eseal-order-form/eseal-order-form.component';
import {ESealDetailsResolve} from "./eseal-order-form/eseal-details-resolve";
import {ESealManagersResolve} from "./eseal-managers-resolve";
import { ESealActivationFormComponent } from './eseal-activation-form/eseal-activation-form.component';
import {ESealActivationResolve} from "./eseal-activation-form/eseal-activation-resolve";

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    ESealOrderRoutingModule
  ],
  declarations: [ManageESealOrdersComponent, ESealOrderListComponent, ESealOrderFormComponent, ESealActivationFormComponent],
  providers: [ManageESealOrdersResolve, ESealDetailsResolve, ESealManagersResolve, ESealActivationResolve]
})
export class ESealOrderModule {
}
