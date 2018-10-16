import {EventEmitter, Injectable} from "@angular/core";
import {TranslateService} from "@ngx-translate/core";
import {Authentication, AuthorityType, Permission} from "../authentication/authentication";
import {Language} from "./static-data";
import {StaticDataService} from "./static-data-service";
import {SessionStorage} from "./storage";
import {NgxPermissionsService} from "ngx-permissions";

const AUTHENTICATION: string = "authentication";
const LANG: string = "lang";

@Injectable()
export class UserSession {

  onLanguageChanged: EventEmitter<Language> = new EventEmitter();

  onLoggedIn: EventEmitter<Authentication> = new EventEmitter();

  onLogout: EventEmitter<Authentication> = new EventEmitter();

  private currentLanguage: Language;
  private languages: Language[];
  private defaultLanguage: Language;
  private authentication: Authentication;

  constructor(private translation: TranslateService,
              private staticDataService: StaticDataService,
              private sessionStorage: SessionStorage,
              private permissions: NgxPermissionsService) {
  }

  init(): void {
    if (this.sessionStorage.hasItem(AUTHENTICATION)) {
      this.authentication = this.sessionStorage.getItem(AUTHENTICATION);
      if (this.authentication.defaultLanguage != null) {
        this.currentLanguage = this.authentication.defaultLanguage;
      }
    }

    if (this.sessionStorage.hasItem(LANG)) {
      this.currentLanguage = this.sessionStorage.getItem(LANG);
    }

    this.staticDataService.getLanguages().subscribe(languages => {
      this.languages = languages;
      this.languages.forEach((lang) => {
        if (lang.useByDefault) {
          if (!this.currentLanguage) {
            this.currentLanguage = lang;
          }
          this.defaultLanguage = lang;
        }
      });
      this.translation.setDefaultLang(this.defaultLanguage.twoCharsCode);
      this.translation.use(this.currentLanguage.twoCharsCode);
      this.onLanguageChanged.emit(this.currentLanguage);
    });
  }

  isSignedIn(): boolean {
    return this.authentication != null && this.sessionStorage.hasItem(AUTHENTICATION);
  }

  getLanguages(): Language[] {
    return this.languages
  }

  changeLanguage(lang: Language) {
    this.translation.use(lang.twoCharsCode);
    this.currentLanguage = lang;
    this.sessionStorage.setItem(LANG, lang);
    this.onLanguageChanged.emit(lang);
  }

  getCurrentLanguage(): Language {
    return this.currentLanguage;
  }

  signIn(authentication: Authentication) {
    this.authentication = authentication;
    this.onLoggedIn.emit(this.authentication);
    if (this.authentication.defaultLanguage) {
      this.changeLanguage(this.authentication.defaultLanguage);
    }
    this.sessionStorage.setItem(AUTHENTICATION, this.authentication);
    this.permissions.loadPermissions(this.authentication.permissions);
  }

  hasAuthority(role: AuthorityType): boolean {
    return this.isSignedIn() && this.authentication.authorities.map((a) => a.authority).indexOf(role) != -1;
  }

  hasPermission(permission: Permission): boolean {
    return this.isSignedIn() &&this.authentication.permissions.indexOf(permission) != -1;
  }

  hasAnyPermission(...permission: Permission[]): boolean {
    let has = false;
    if (this.isSignedIn()) {
      permission.forEach((p) => has = has || this.authentication.permissions.indexOf(p) != -1);
    }
    return has;
  }

  signOut() {
    this.onLogout.emit(this.authentication);
    this.authentication = null;
    this.sessionStorage.clear();
    this.permissions.flushPermissions();
  }

  getAuthentication(): Authentication {
    if (this.sessionStorage.hasItem(AUTHENTICATION)) {
      return this.authentication;
    }
    return null;
  }

  refreshAuthentication(authentication: Authentication) {
    this.authentication = authentication;
    this.sessionStorage.setItem(AUTHENTICATION, authentication);
  }
}
