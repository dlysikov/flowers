import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-back-to-list',
  templateUrl: './back-to-list.component.html',
  styleUrls: ['./back-to-list.component.css']
})
export class BackToListComponent implements OnInit {

  @Input()
  label: any;

  constructor() { }

  ngOnInit() {
  }

}
