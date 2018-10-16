import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {DEFAULT_PAGE_SIZE, PageParams} from "../pagination/pagination";
import {ServicePaging} from "../service/service-paging";

export abstract class BaseManageListResolve<T> implements Resolve<any> {

  constructor(private service: ServicePaging<T>, private filterName: string) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    let pageParams = new PageParams();
    pageParams.pageNumber = 1;
    pageParams.pageSize = DEFAULT_PAGE_SIZE;

    if (sessionStorage.getItem(this.filterName)) {
      pageParams.filter = JSON.parse(sessionStorage.getItem(this.filterName));
    }

    return this.service.getPage(pageParams);
  }
}
