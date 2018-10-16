import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {UnitService} from "../unit-service";

@Injectable()
export class UnitModifyStatusResolve implements Resolve<any> {

  constructor(private unitService: UnitService, private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.unitService.getValuableDataModifiable(route.params['unitId'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
