import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {CertificateOrderService} from "../certificate-order-service";
import {OrderMobileCodeVerification} from "../../mobile-code-verification/order-mobile-code-verification";
import {TabStorage} from "../../../common/storage";

@Injectable()
export class UserPageResolve implements Resolve<any> {

  constructor(private orderService: CertificateOrderService, private router: Router, private tabStorage: TabStorage) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    let mobileCode:OrderMobileCodeVerification = this.tabStorage.getItem("orderUserMobileCode");

    return this.orderService.getUserPage(route.params['pageHash'], mobileCode.mobileCode)
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
