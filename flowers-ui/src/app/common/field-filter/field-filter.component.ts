import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Field, FieldFilter} from "./field-filter";
import {EqualsById} from "../common";
import {MzSelectContainerComponent} from "ng2-materialize";

@Component({
  selector: 'app-field-filter',
  templateUrl: './field-filter.component.html',
  styleUrls: ['./field-filter.component.css']
})
export class FieldFilterComponent implements OnInit {

  @ViewChild("filterSelect")
  public filterSelect: MzSelectContainerComponent;

  @Input()
  fields: [Field];
  @Input()
  filterName: string;

  filter: FieldFilter = new FieldFilter();

  activeFilters: Array<FieldFilter> = [];

  @Output()
  filtersChanged = new EventEmitter<FieldFilter[]>();
  @Output()
  initialFilters = new EventEmitter<FieldFilter[]>();

  constructor() {
  }

  ngOnInit() {
    if (sessionStorage.getItem(this.filterName)) {
      this.activeFilters = JSON.parse(sessionStorage.getItem(this.filterName));
      if (this.activeFilters.length > 0) {
        this.initialFilters.emit(this.activeFilters);
      }
    }
  }

  changeActiveFilters(): void {
    let existedFilter = this.activeFilters.find((af) => af.field == this.filter.field);
    if (existedFilter) {
      existedFilter.value = this.filter.value;
    } else {
      this.activeFilters.push(Object.assign(new FieldFilter(), this.filter));
    }
    sessionStorage.setItem(this.filterName, JSON.stringify(this.activeFilters));
    this.filtersChanged.emit(this.activeFilters);
  }

  clearFilter(): void {
    this.filter = new FieldFilter();
  }

  deleteActiveFilter(activeFilter: FieldFilter): void {
    this.activeFilters = this.activeFilters.filter((af) => af != activeFilter);
    sessionStorage.setItem(this.filterName, JSON.stringify(this.activeFilters));
    this.filtersChanged.emit(this.activeFilters);
  }

  fieldEquals(o1: Field, o2: Field): boolean {
    if (o1 && o2) {
      return o1.name == o2.name;
    }
    return o1 === o2;
  }

  equalsById(o1: any, o2: any): boolean {
    return EqualsById(o1, o2);
  }

  private getFilterValueToDisplay(filter: FieldFilter, value: any): any {
    if (filter.field.values && filter.field.values.valuesFieldToShow) {
      return value[filter.field.values.valuesFieldToShow];
    } else {
      return value;
    }
  }

  getValueToDisplay(value: any):any {
    return this.getFilterValueToDisplay(this.filter, value);
  }

  getActiveFilterValueToDisplay(filter: FieldFilter, value: any): any {
    return this.getFilterValueToDisplay(filter, value);
  }
}
