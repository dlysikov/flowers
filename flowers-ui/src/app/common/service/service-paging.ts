import {PageParams, PageResponse} from "../pagination/pagination";
import {Observable} from "rxjs/Observable";
import {HttpClient, HttpParams} from "@angular/common/http";

export interface ServicePaging<T> {
  getPage(params?:PageParams): Observable<PageResponse<T>>;
}

export class ServicePagingImpl<T> implements ServicePaging<T> {

  constructor(private _httpClient: HttpClient, private pageURL: string) {

  }

  getPage(params?:PageParams): Observable<PageResponse<T>> {
    let httpParams = params ? params.toHttpParams() : new HttpParams();
    return this._httpClient.get(this.pageURL, {params: httpParams}).map((data:any) =>{
      return Object.assign(new PageResponse(), data);
    });
  }
}
