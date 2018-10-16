import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {AuthenticationService} from "../authentication/authentication-service";
import {NgxPermissionsService, NgxRolesService} from "ngx-permissions";
import {UserSession} from "../common/session";
import {Authority, Permission} from "../authentication/authentication";

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private userSession: UserSession,
              private authService: AuthenticationService,
              private permissionsService: NgxPermissionsService) {
    this.userSession.init();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (!this.userSession.isSignedIn()) {
      this.authService.sendToLoginPage();
    } else {
      this.permissionsService.loadPermissions(this.userSession.getAuthentication().permissions);
    }
    return Observable.of(this.userSession.isSignedIn());
  }
}
