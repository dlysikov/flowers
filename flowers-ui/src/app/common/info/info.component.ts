import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  @Input()
  public id:string;
  @Input()
  tooltipText;

  @Input()
  position: string = 'left';

  constructor() { }

  ngOnInit() {
  }

}
