import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {UserService} from "./user-service";

@Injectable()
export class UserRolesToCreateResolve implements Resolve<any> {

  constructor(private userService: UserService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    return this.userService.getRolesToCreate();
  }
}
