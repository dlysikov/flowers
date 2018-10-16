import {Injectable} from "@angular/core";
import {CertificateOrderService} from "../certificate-order-service";
import {CertificateOrder} from "../certificate-order";
import {BaseManageListResolve} from "../../../common/component/base-manage-list-resolve";

@Injectable()
export class ManagerCertificateOrdersResolve extends BaseManageListResolve<CertificateOrder> {

  constructor(private orderService: CertificateOrderService) {
    super(orderService, "order-filters");
  }
}
