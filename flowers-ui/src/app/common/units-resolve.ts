import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {UnitService} from "../unit/unit-service";
import {StaticDataService} from "./static-data-service";

@Injectable()
export class UnitsResolve implements Resolve<any> {

  constructor(private staticDataService: StaticDataService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.staticDataService.getUnits();
  }
}
