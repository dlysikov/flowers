import {Component, OnDestroy} from '@angular/core';
import {environment} from "../environments/environment";
import {RoutingState} from "./common/routing-state";
import {Language} from "./common/static-data";
import {UserSession} from "./common/session";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy{

  private languageSubscription;

  userProfileImgURL: string = environment.imagesLocation + "user-profile-icon.png";
  logoURL: string = environment.imagesLocation + "flowers-logo.png";
  languages: Language[];
  currentLanguage: Language;

  constructor(public userSession: UserSession, private routingState: RoutingState, private router: Router) {
    routingState.loadRouting();
    userSession.init();
    this.currentLanguage = this.userSession.getCurrentLanguage();

    this.languageSubscription = this.userSession.onLanguageChanged.subscribe((lang) => {
      this.currentLanguage = lang;
      this.languages = this.userSession.getLanguages();
    });
  }

  logout(): void {
    this.userSession.signOut();
    this.router.navigate(["/login"]);
  }

  switchLanguage(lang: Language) {
    this.userSession.changeLanguage(lang);
  }

  ngOnDestroy(): void {
    this.languageSubscription.unsubscribe();
  }
}
