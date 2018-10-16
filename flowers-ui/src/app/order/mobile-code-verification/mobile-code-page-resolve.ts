import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {CertificateOrderService} from "../certificate-order/certificate-order-service";

@Injectable()
export class MobileCodePageResolve implements Resolve<any> {

  constructor(private orderService: CertificateOrderService, private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {

    return this.orderService.isMobileCodeVerificationRequired(route.params['pageHash'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
