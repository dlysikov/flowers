import {Injectable} from "@angular/core";
import {ESealOrderService} from "../eseal-order-service";
import {ESealOrder} from "../eseal-order";
import {BaseManageListResolve} from "../../../common/component/base-manage-list-resolve";

@Injectable()
export class ManageESealOrdersResolve extends BaseManageListResolve<ESealOrder> {

  constructor(private orderService: ESealOrderService) {
    super(orderService, "esealOrdersFilter")
  }
}
