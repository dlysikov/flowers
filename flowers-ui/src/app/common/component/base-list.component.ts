import {OnInit, ViewChild} from "@angular/core";
import {Field, FieldFilter} from "../field-filter/field-filter";
import {DEFAULT_PAGE_SIZE, PageParams, PageResponse, Pagination} from "../pagination/pagination";
import {ActivatedRoute, Router} from "@angular/router";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {UserSession} from "../session";
import {ServicePaging} from "../service/service-paging";
import {FieldFilterComponent} from "../field-filter/field-filter.component";

export class BaseListComponent<T> implements OnInit {

  @ViewChild(FieldFilterComponent)
  fieldFilter:FieldFilterComponent;

  loaderTemplate: string = `<div class="loader"></div>`;

  items: T[];
  filterFields:[Field];

  currentFilters:FieldFilter[];
  currentPage:number = 1;
  itemsCount:number = 0;
  itemsPerPage:number = DEFAULT_PAGE_SIZE;

  constructor(private _route: ActivatedRoute,
              private _session: UserSession,
              private routeItemName: string,
              private filterFieldsTemplate:[Field],
              private servicePaging: ServicePaging<T>,
              private _spinnerService: Ng4LoadingSpinnerService,
              private _router: Router) { }

  ngOnInit(): void {
    this.items = this._route.snapshot.data[this.routeItemName].data;
    this.itemsCount = this._route.snapshot.data[this.routeItemName].totalCount;
    this._session.onLanguageChanged.subscribe(() => this.handleFilterFields());
    this.handleFilterFields();
  }

  private handleFilterFields(): void {
    setTimeout(() => {
      this.filterFields = Object.assign([], this.filterFieldsTemplate);
      this.fieldFilter.filterSelect.mzSelectDirective.updateMaterialSelect();
    }, 200);
  }

  setInitialFilters(filters: FieldFilter[]): void {
    this.currentFilters = filters;
  }

  applyFilters(filters: FieldFilter[]): void {
    this.currentPage = 1;
    this.currentFilters = filters;
    this.changeList();
  }

  changePage(pagination: Pagination): void {
    this.currentPage = pagination.pageNumber;
    this.itemsPerPage = pagination.pageSize;
    this.changeList();
  }

  private changeList(): void {
    this._spinnerService.show();
    let params = new PageParams();
    params.filter = this.currentFilters;
    params.pageSize = this.itemsPerPage;
    params.pageNumber = this.currentPage;

    this.servicePaging.getPage(params).subscribe((r: PageResponse<T>) => {
      this.items = r.data;
      this.itemsCount = r.totalCount;
      this._spinnerService.hide();
    })
  }

  backToMain(): void {
    this._router.navigateByUrl("/");
  }
}
