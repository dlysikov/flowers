import "rxjs/Rx";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Country, Language, Nationality, Requestor, RequestorConfig} from "./static-data";
import {environment} from "../../environments/environment";
import {Unit} from "../unit/unit";
import {UnitService} from "../unit/unit-service";

@Injectable()
export class StaticDataService {

  constructor(private httpClient: HttpClient) {
  }

  getCountries(): Observable<Country[]> {
    return this.httpClient.get<Country[]>(`${environment.apiEndpoint}/static/countries`)
      .map((res: any[]) => res.map(c => StaticDataService.toCountry(c)));
  }

  getNationalities(): Observable<Nationality[]> {
    return this.httpClient.get<Nationality[]>(`${environment.apiEndpoint}/static/nationalities`)
      .map((res: any[]) => res.map(c => StaticDataService.toNationality(c)));
  }

  getServices(): Observable<Requestor[]> {
    return this.httpClient.get<Requestor[]>(`${environment.apiEndpoint}/static/services`)
      .map((res: any[]) => res.map(c => StaticDataService.toRequestor(c)));
  }

  getServiceConfig(id) {
    return this.httpClient.get<RequestorConfig>(`${environment.apiEndpoint}/static/services/config/` + id);
  }

  getLanguages(): Observable<Language[]> {
    return this.httpClient.get<Language[]>(`${environment.apiEndpoint}/static/languages`)
      .map((res: any[]) => res.map(l => StaticDataService.toLanguage(l)));
  }

  getUnits(): Observable<Unit[]> {
    return this.httpClient.get<Unit[]>(`${environment.apiEndpoint}/static/units`)
      .map((res: any[]) => res.map(u => UnitService.toUnit(u)));
  }

  public static toLanguage(data: any): Language {
    return Object.assign(new Language(), data);
  }

  public static toRequestor(data: any): Requestor {
    return Object.assign(new Requestor(), data);
  }

  public static toCountry(data: any): Country {
    return Object.assign(new Country(), data);
  }

  public static toNationality(data: any): Nationality {
    return Object.assign(new Nationality(), data);
  }
}
