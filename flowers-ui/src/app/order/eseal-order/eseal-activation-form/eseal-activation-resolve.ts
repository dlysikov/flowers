import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ESealOrderService} from "../eseal-order-service";

@Injectable()
export class ESealActivationResolve implements Resolve<any> {

  constructor(private eSealOrderService: ESealOrderService,
              private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.eSealOrderService.getKeys()
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }

}
