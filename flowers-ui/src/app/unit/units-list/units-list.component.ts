import {Component, Input} from '@angular/core';
import {Unit} from "../unit";

@Component({
  selector: 'app-units-list',
  templateUrl: './units-list.component.html',
  styleUrls: ['./units-list.component.css']
})
export class UnitsListComponent {

  @Input()
  units: Unit[];

  unitsEmpty(): boolean {
    return this.units == null || this.units.length == 0;
  }
}
