import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Unit} from "./unit";
import {environment} from "../../environments/environment";
import {ServicePagingImpl} from "../common/service/service-paging";

@Injectable()
export class UnitService extends ServicePagingImpl<Unit> {

  constructor(private httpClient: HttpClient) {
    super(httpClient, `${environment.apiEndpoint}/units`);
  }

  create(unit: Unit): Observable<any> {
    return this.httpClient.put(`${environment.apiEndpoint}/units`, unit);
  }

  update(unit: Unit): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/units/${unit.id}`, unit);
  }

  delete(unit: Unit): Observable<any> {
    return this.httpClient.delete(`${environment.apiEndpoint}/units/${unit.id}`);
  }

  getUnit(id: number): Observable<any> {
    return this.httpClient.get(`${environment.apiEndpoint}/units/${id}`).map((u) => UnitService.toUnit(u));
  }

  getValuableDataModifiable(id: number): Observable<any> {
    return this.httpClient.get(`${environment.apiEndpoint}/units/${id}/valuable_data_modifiable`);
  }

  public static toUnit(data: any): Unit {
    return Object.assign(new Unit(), data);
  }

  getUnits() : Observable<Unit[]> {
    return this.httpClient.get(`${environment.apiEndpoint}/units`).map((res:any[]) => res.map(u => UnitService.toUnit(u)));
  }
}
