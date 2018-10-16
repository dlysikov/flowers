import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {CertificateOrderService} from "../certificate-order-service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class CertificateOrderDetailsResolve implements Resolve<any> {

  constructor(private orderService: CertificateOrderService, private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.orderService.getOrder(route.params['orderId'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
