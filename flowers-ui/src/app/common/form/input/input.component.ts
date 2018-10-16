import {Component, Input} from "@angular/core";
import {AbstractFormComponent} from "../abstract.form.component";


@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css'],

})
export class FInputComponent<T> extends AbstractFormComponent<T> {
  @Input()
  type: string = 'text';
}
