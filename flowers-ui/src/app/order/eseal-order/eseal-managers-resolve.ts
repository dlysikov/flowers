import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {ESealOrderService} from "./eseal-order-service";

@Injectable()
export class ESealManagersResolve implements Resolve<any>{

  constructor(private eSealOrderService: ESealOrderService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.eSealOrderService.getAllManagers();
  }
}
