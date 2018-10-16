import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {UserService} from "../user-service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class UserDetailsResolve implements Resolve<any> {

  constructor(private userService: UserService, private router: Router) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.userService.getUser(route.params['userId'])
      .catch(err => {
        this.router.navigate(["/404"]);
        return Observable.empty();
      });
  }
}
