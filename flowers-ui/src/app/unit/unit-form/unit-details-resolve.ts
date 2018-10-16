import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {UnitService} from "../unit-service";

@Injectable()
export class UnitDetailsResolve implements Resolve<any> {

  constructor(private unitService: UnitService, private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.unitService.getUnit(route.params['unitId'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
