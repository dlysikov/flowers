import {Component, EventEmitter, Output} from "@angular/core";
import {AbstractFormComponent} from "../abstract.form.component";

@Component({
  selector: 'app-checkbox',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.css']
})
export class FCheckboxComponent extends AbstractFormComponent<boolean> {

  @Output()
  change: EventEmitter<boolean> = new EventEmitter();

  public submitEvent(event: Event) {
    event.stopPropagation();
    this.value = !this.value;
    super.submit(event);
    this.change.emit(this.value);
  }
}
