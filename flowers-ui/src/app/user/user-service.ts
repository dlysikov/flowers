import "rxjs/Rx";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {environment} from "../../environments/environment";
import {Role, RoleType, User} from "./user";
import {ServicePagingImpl} from "../common/service/service-paging";

@Injectable()
export class UserService extends ServicePagingImpl<User> {

  constructor(private httpClient: HttpClient) {
    super(httpClient, `${environment.apiEndpoint}/users`);
  }

  getUser(id: number): Observable<User> {
    return this.httpClient.get<User>(`${environment.apiEndpoint}/users/${id}`).map((res:any) => Object.assign(new User(), res));
  }

  updateUser(user: User): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/users/${user.id}`, user);
  }

  createUser(user: User): Observable<any> {
    return this.httpClient.put(`${environment.apiEndpoint}/users`, user);
  }

  getRolesToCreate(): Observable<Role[]> {
    return this.httpClient.get(`${environment.apiEndpoint}/users/roles_to_create`).map((res:any[]) => res.map(r => Object.assign(new Role(), r)));
  }
}
