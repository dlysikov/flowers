import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {AppRoutingModule} from './app-routing.module';
import {HTTP_INTERCEPTORS, HttpClient} from "@angular/common/http";
import {CertificateOrderService} from "./order/certificate-order/certificate-order-service";
import {AuthenticationHttpInterceptor} from "./authentication/authentication-interceptor";
import {AuthenticationService} from "./authentication/authentication-service";
import {SharedModule} from "./shared.module";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {BrowserModule} from "@angular/platform-browser";
import {AuthGuard} from "./guard/auth.guard";
import {NgxPermissionsModule} from "ngx-permissions";
import {HomePageComponent} from "./home-page/home-page.component";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import {environment} from "../environments/environment";
import {SessionStorage} from "./common/storage";
import {DoughnutChartComponent} from 'angular-d3-charts/src/doughnutChart.component';
import {UnitService} from "./unit/unit-service";
import {UserService} from "./user/user-service";
import {UserPageGuard} from "./guard/user-page.guard";
import {ESealOrderService} from "./order/eseal-order/eseal-order-service";
import {TabStorage} from "./common/storage";
import {UserSession} from "./common/session";

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, environment.translationPrefix, ".json");
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomePageComponent,
    DoughnutChartComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    SharedModule.forRoot(),
    NgxPermissionsModule.forRoot(),
    Ng4LoadingSpinnerModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    AuthGuard,
    UserPageGuard,
    AuthenticationService,
    UserSession,
    SessionStorage,
    TabStorage,
    CertificateOrderService,
    ESealOrderService,
    UnitService,
    UserService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthenticationHttpInterceptor, multi: true},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
