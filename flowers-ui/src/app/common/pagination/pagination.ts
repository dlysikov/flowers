import {HttpParams} from "@angular/common/http";
import {FieldFilter} from "../field-filter/field-filter";

export class PageResponse<T> {
  data: T[] = [];
  totalCount:number;
}

export const DEFAULT_PAGE_SIZE = 20;

export const PAGE_SIZES:number[] = [1,5,10,DEFAULT_PAGE_SIZE,30];


export class PageParams {
  pageNumber:number;
  pageSize:number;
  filter:FieldFilter[];

  toHttpParams(): HttpParams {
    let httpParams = new HttpParams();

    if (this.pageNumber != null && this.pageNumber > 0) {
      httpParams = httpParams.set("pageNumber", this.pageNumber + "");
    }

    if (this.pageSize != null && this.pageSize > 0) {
      httpParams = httpParams.set("pageSize", this.pageSize + "");
    }

    if (this.filter != null) {
      this.filter.forEach((f: FieldFilter, i) => {
        if (typeof f.value == "string") {
          httpParams = httpParams.set("filter[" + i + "].value[" + 0 + "]", f.value);
        } else {
          f.value.forEach((v, j) => {
            if (f.field.values.valuesFieldToUse) {
              httpParams = httpParams.set("filter[" + i + "].value[" + j + "]", v[f.field.values.valuesFieldToUse]);
            } else {
              httpParams = httpParams.set("filter[" + i + "].value[" + j + "]", v);
            }
          });
        }
        httpParams = httpParams.set("filter[" + i + "].field", f.field.name);
        httpParams = httpParams.set("filter[" + i + "].type", f.field.type);
      })
    }

    return httpParams;
  }
}

export class Pagination {
  pageNumber: number;
  pageSize:number;

  constructor(pageNumber: number, pageSize: number) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }
}
