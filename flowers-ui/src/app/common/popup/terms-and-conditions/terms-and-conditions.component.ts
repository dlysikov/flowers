import { Component} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {DomSanitizer} from "@angular/platform-browser";
import {UserSession} from "../../session";
import {MzBaseModal} from "ng2-materialize";

@Component({
  selector: 'app-terms-and-conditions',
  templateUrl: './terms-and-conditions.component.html',
  styleUrls: ['./terms-and-conditions.component.css']
})
export class TermsAndConditionsComponent extends MzBaseModal {

  private langSubscription;

  modalOptions: any = {
    dismissible: false
  };

  pageLocation: any;

  okCallback(): void {}

  noCallback(): void {}

  private loadPage(lang: string) {
    this.pageLocation = this.sanitizer.bypassSecurityTrustResourceUrl(environment.pagesLocation.gtc[lang]);
  }

  constructor(private sanitizer: DomSanitizer,
              private userSession: UserSession) {
    super();
    this.loadPage(this.userSession.getCurrentLanguage().twoCharsCode);
    this.langSubscription = this.userSession.onLanguageChanged.subscribe(lang => this.loadPage(lang.twoCharsCode));

  }
}
