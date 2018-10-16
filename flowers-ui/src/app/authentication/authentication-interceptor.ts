import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest,
  HttpResponse, HttpResponseBase
} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {forwardRef, Inject, Injectable, Injector} from "@angular/core";
import {Router} from "@angular/router";
import {Subject} from "rxjs/Subject";
import 'rxjs/add/operator/do';
import {environment} from "../../environments/environment";
import {AuthenticationService} from "./authentication-service";
import {UserSession} from "../common/session";

@Injectable()
export class AuthenticationHttpInterceptor implements HttpInterceptor {

  private userSession: UserSession;
  private authService: AuthenticationService;

  constructor(private router: Router, private inj: Injector) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.userSession || !this.authService) {
      this.userSession = this.inj.get(UserSession);
      this.authService = this.inj.get(AuthenticationService);
    }

    let result = new Subject<HttpEvent<any>>();

    next.handle(this.authService.populateRequestWithToken(req)).do(
      event => this.handleResponseEvent(result, event),
      err => {
        if (err instanceof HttpErrorResponse && err.status == 401) {
          if (this.userSession.isSignedIn()) {
            return next.handle(this.createRefreshTokenRequest()).do(
              (e) => {
                if (e instanceof HttpResponse) {
                  this.authService.refreshToken(e.headers);
                  return next.handle(this.authService.populateRequestWithToken(req)).do(
                    (event) => this.handleResponseEvent(result, event),
                    () => this.handleAuthError(result)
                  ).subscribe();
                }
              },
              () => this.handleAuthError(result)
            ).subscribe();
          } else {
            this.authService.sendToLoginPage();
            result.complete();
          }
        } else {
          result.error(err);
          result.complete();
        }
      }
    ).subscribe();
    return result;
  }

  private handleResponseEvent(result: Subject<any>, event: any) {
    if (event instanceof HttpResponseBase) {
      result.next(event);
      result.complete();
    }
  }

  private createRefreshTokenRequest(): HttpRequest<any> {
    let refreshReq = new HttpRequest("GET", `${environment.apiEndpoint}/login/refresh`, {headers: new HttpHeaders()});
    return this.authService.populateRequestWithRefreshToken(refreshReq);
  }

  private handleAuthError(result: Subject<any>) {
    this.authService.logout();
    this.authService.sendToLoginPage();
    result.complete();
  }
}
