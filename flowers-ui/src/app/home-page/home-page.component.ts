import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {NgxPermissionsService} from "ngx-permissions";
import {HOME_PAGE_MENU, MenuItem} from "./home-page-menu";
import {Router} from "@angular/router";
import {UserSession} from "../common/session";


@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit{
  menu:MenuItem[] = [];

  constructor(private translateService: TranslateService,
              private session: UserSession,
              private permissionService: NgxPermissionsService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.initMenu();
    this.session.onLoggedIn.subscribe(() => {
      this.initMenu();
    })
  }

  private initMenu() {
    this.menu = [];
    HOME_PAGE_MENU.forEach((mi) => {
      if (this.session.hasAnyPermission.apply(this.session, mi.permissions)) {
        this.menu.push(mi)
      }
    });
  }

  goTo(link : any): void {
    this.router.navigateByUrl("/"+link);
  }
}
