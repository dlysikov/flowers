import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ESealOrderService} from "../eseal-order-service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ESealDetailsResolve implements Resolve<any>{


  constructor(private eSealOrderService: ESealOrderService,
              private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.eSealOrderService.getESeal(route.params['eSealId'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
