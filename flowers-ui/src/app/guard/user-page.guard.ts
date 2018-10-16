import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import { Observable } from 'rxjs/Observable';
import {UserSession} from "../common/session";
import {CertificateOrderService} from "../order/certificate-order/certificate-order-service";
import {Subject} from "rxjs/Subject";
import {OrderMobileCodeVerification} from "../order/mobile-code-verification/order-mobile-code-verification";
import {TabStorage} from "../common/storage";

@Injectable()
export class UserPageGuard implements CanActivate {

  constructor(private userSession: UserSession, private storage: TabStorage,
              private orderService: CertificateOrderService, private router: Router) {
    this.userSession.init();
  }

  canActivate(next: ActivatedRouteSnapshot,state: RouterStateSnapshot): Observable<boolean>  {
    let result = new Subject<boolean>();
    let code: OrderMobileCodeVerification = this.storage.getItem("orderUserMobileCode");
    if (!code || !code.mobileCode || next.params['pageHash'] != code.pageHash) {
      this.orderService.isMobileCodeVerificationRequired(next.params['pageHash']).subscribe((required) => {
        if (!required) {
          let dummy = new OrderMobileCodeVerification();
          dummy.pageHash = next.params['pageHash'];
          this.storage.setItem("orderUserMobileCode", dummy);
        }
        result.next(!required);
        result.complete();
        if (required) {
          this.router.navigate(["certificate-orders", "user", next.params['pageHash'], "mobile-code-verification"])
        }
      }, error => {
        result.next(false);
        result.complete();
        this.router.navigate(["/404"]);
      });
      return result;
    } else {
      return Observable.of(true);
    }
  }
}
