import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {HttpHeaders, HttpRequest} from "@angular/common/http";
import {Authentication} from "./authentication";
import {UserSession} from "../common/session";

const JWT:string = "jwt";
const JWT_REFRESH:string = "refreshJwt";
const AUTHENTICATION: string = "authentication";
const AUTHORIZATION_HEADER: string = "Authorization";
const AUTHORIZATION_REFRESH_HEADER: string = "Authorization-Refresh";

@Injectable()
export class AuthenticationService {

  constructor(private router: Router, private userSession: UserSession) {
  }

  populateRequestWithToken(req: HttpRequest<any>): HttpRequest<any> {
    if (this.userSession.isSignedIn()) {
      return req.clone({headers: req.headers.set(AUTHORIZATION_HEADER, this.userSession.getAuthentication().jwt)});
    }
    return req;
  }

  populateRequestWithRefreshToken(req: HttpRequest<any>): HttpRequest<any> {
    if (this.userSession.isSignedIn()) {
      return req.clone({headers: req.headers.set(AUTHORIZATION_REFRESH_HEADER, this.userSession.getAuthentication().refreshJwt)});
    }
    return req;
  }

  logout() {
    this.userSession.signOut();
  }

  refreshToken(data: HttpHeaders) {
    if (data.get(AUTHORIZATION_HEADER) && data.get(AUTHORIZATION_REFRESH_HEADER)) {
      let authentication = this.userSession.getAuthentication();
      authentication.jwt = data.get(AUTHORIZATION_HEADER);
      authentication.refreshJwt = data.get(AUTHORIZATION_REFRESH_HEADER);
      this.userSession.refreshAuthentication(authentication);
    } else {
      this.sendToLoginPage();
    }
  }

  login(data: any, redirectURL: string): void {
    if (data[JWT] && data[JWT_REFRESH] && data[AUTHENTICATION]) {
      let authentication = Object.assign(new Authentication(), JSON.parse(data[AUTHENTICATION]));
      authentication.jwt = data[JWT];
      authentication.refreshJwt = data[JWT_REFRESH];

      this.userSession.signIn(authentication);
      this.router.navigate([redirectURL]);
    } else {
      this.sendToLoginPage();
    }
  }

  sendToLoginPage() {
    this.router.navigate(["/login"]);
  }
}
