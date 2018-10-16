import {Component} from "@angular/core";
import {ESealOrder} from "../eseal-order";
import {ActivatedRoute, Router} from "@angular/router";
import {Field, FieldFilterType, FieldValues} from "../../../common/field-filter/field-filter";
import {UserSession} from "../../../common/session";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {BaseListComponent} from "../../../common/component/base-list.component";
import {ESealOrderService} from "../eseal-order-service";
import {OrderStatus} from "../../order";

@Component({
  selector: 'app-manage-eseal-orders',
  templateUrl: './manage-eseal-orders.component.html',
  styleUrls: ['./manage-eseal-orders.component.css']
})
export class ManageESealOrdersComponent extends BaseListComponent<ESealOrder>{

  constructor(private route: ActivatedRoute,
              private session: UserSession,
              private spinnerService: Ng4LoadingSpinnerService,
              private router: Router,
              private esealService: ESealOrderService) {
    super(route, session, "eseals", [new Field("id", "eseal.id", FieldFilterType.INTEGER),
                                     new Field("unit.requestor.name", "eseal.requestor", FieldFilterType.STRING_LIKE),
                                     new Field("unit.commonName", "eseal.unit", FieldFilterType.STRING_LIKE),
                                     new Field("unit.identifier.value", "unit.company-identifier-value", FieldFilterType.STRING_LIKE),
                                     new Field("organisationalUnit", "eseal.organisational-unit", FieldFilterType.STRING_LIKE),
                                     new Field("ssn", "eseal.ssn", FieldFilterType.STRING_LIKE),
                                     new Field("status", "eseal.status", FieldFilterType.STRING_EQ,
                                     new FieldValues([OrderStatus.DRAFT,
                                        OrderStatus.SENT_TO_LRS,
                                        OrderStatus.LRS_ONGOING,
                                        OrderStatus.LRS_PRODUCED,
                                        OrderStatus.LRS_ACTIVATED,
                                        OrderStatus.LRS_CANCELED],
                                       null, null, 'order.statuses.'))],
    esealService, spinnerService, router);
  }
}
