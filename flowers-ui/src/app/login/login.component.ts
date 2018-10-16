import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {environment} from "../../environments/environment";
import {AuthenticationService} from "../authentication/authentication-service";
import {RoutingState} from "../common/routing-state";
import {Router} from "@angular/router";
import {UserSession} from "../common/session";
import {Language} from "../common/static-data";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private langSubscription;

  trustedUrl: SafeResourceUrl;

  constructor(private domSanitizer: DomSanitizer,
              private routingState: RoutingState,
              private router: Router,
              private userSession: UserSession,
              private authService: AuthenticationService) { }

  ngOnInit() {
    if (this.userSession.isSignedIn()) {
      this.router.navigate(['/']);
    }
    this.langSubscription = this.userSession.onLanguageChanged.subscribe((lang) => this.changeLoginURL(lang));
    setTimeout(() => {
      if (!this.trustedUrl) {
        if (!this.userSession.getCurrentLanguage()) {
          this.userSession.init();
        }
        if (!this.trustedUrl) {
          this.userSession.changeLanguage(this.userSession.getCurrentLanguage());
        }
      }
    }, 300);
  }

  ngOnDestroy(): void {
    this.langSubscription.unsubscribe();
  }

  private changeLoginURL(lang: Language) {
    this.trustedUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(`${environment.apiEndpoint}/login?lang=${lang.twoCharsCode}`);
  }

  @HostListener('window:message', ['$event'])
  iframeEventListener(event: MessageEvent) {
    if (event.data) {
      if (event.data["reload"]) {
        this.changeLoginURL(this.userSession.getCurrentLanguage());
      } else {
        this.authService.login(event.data, this.routingState.getPreviousUrl());
      }
    }
  }
}
