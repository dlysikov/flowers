import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {PAGE_SIZES, Pagination} from "./pagination";
import {MzPaginationComponent} from "ng2-materialize";

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {

  pageSizes:number[] = PAGE_SIZES;

  @ViewChild("pagination")
  pagination:MzPaginationComponent;

  @Input()
  currentPage:number;
  @Input()
  pageSize:number;
  @Input()
  totalItems:number;

  @Output()
  pageChanged = new EventEmitter<Pagination>();

  ngOnInit(): void {
  }

  changePage(pageNumber: number): void {
    this.currentPage = pageNumber;
    this.pageChanged.emit(new Pagination(this.currentPage, this.pageSize));
  }

  changePageSize(pageSize: number): void {
    this.pageSize = pageSize;
    this.currentPage = 1;
    this.pageChanged.emit(new Pagination(this.currentPage, this.pageSize));
  }
}
