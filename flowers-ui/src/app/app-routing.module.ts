import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule} from "@angular/router";
import {LoginComponent} from "./login/login.component";
import {AuthGuard} from "./guard/auth.guard";
import {HomePageComponent} from "./home-page/home-page.component";
import {PageNotFoundComponent} from "./page-not-found/page-not-found.component";

@NgModule({
  imports: [
    RouterModule.forRoot([
      {path: "", component: HomePageComponent, canActivate: [AuthGuard]},
      {path: "login", component: LoginComponent},
      {path: "certificate-orders", loadChildren: "./order/certificate-order/certificate-order.module#CertificateOrderModule"},
      {path: "eseal-orders", loadChildren: "./order/eseal-order/eseal-order.module#ESealOrderModule"},
      {path: 'null',  component: LoginComponent},
      {path: "units", loadChildren: "./unit/unit.module#UnitModule"},
      {path: "users", loadChildren: "./user/user.module#UserModule"},
      {path: '404',  component: PageNotFoundComponent},
      {path: "**", redirectTo: "/404"}
    ], {initialNavigation: 'enabled', preloadingStrategy: PreloadAllModules})
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
