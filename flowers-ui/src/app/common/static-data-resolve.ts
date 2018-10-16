import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {StaticDataService} from "./static-data-service";

@Injectable()
export class StaticDataResolve implements Resolve<any> {

  constructor(private staticDataService: StaticDataService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return Observable.forkJoin(
      this.staticDataService.getCountries(),
      this.staticDataService.getNationalities(),
      this.staticDataService.getServices(),
      this.staticDataService.getLanguages()
    ).map(r => {
      return {
        countries: r[0],
        nationalities: r[1],
        services: r[2],
        languages: r[3]
      }
    });
  }
}
