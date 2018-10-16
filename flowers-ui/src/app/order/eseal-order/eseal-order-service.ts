import {Injectable} from "@angular/core";
import {ServicePagingImpl} from "../../common/service/service-paging";
import {ESealCredentials, ESealOrder, PublicKey} from "./eseal-order";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {User} from "../../user/user";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ESealOrderService  extends ServicePagingImpl<ESealOrder> {

  constructor(private httpClient: HttpClient) {
    super(httpClient, `${environment.apiEndpoint}/eseal_order`);
  }

  getESeal(id: number):Observable<ESealOrder> {
    return this.httpClient.get<ESealOrder>(`${environment.apiEndpoint}/eseal_order/${id}`).map((res:any) => Object.assign(new ESealOrder(), res));
  }

   getAllManagers():Observable<User[]> {
     return this.httpClient.get<User[]>(`${environment.apiEndpoint}/eseal_order/managers`)
       .map((res:any[]) => res.map(man => Object.assign(new User(), man)));
  }

  saveESeal(eSealOrder: ESealOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/eseal_order`, eSealOrder);
  }

  saveESealAsDraft(eSealOrder: ESealOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/eseal_order/draft`, eSealOrder);
  }

  sendToLRS(eSealOrder: ESealOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/eseal_order/send_to_lrs`, eSealOrder);
  }

  getKeys(): Observable<any> {
    return this.httpClient.get(`${environment.apiEndpoint}/eseal_order/activate/keys`)
      .map(key => Object.assign(new PublicKey(), key));
  }

  activate(eSealCredentials: ESealCredentials): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/eseal_order/activate`, eSealCredentials);
  }

}
