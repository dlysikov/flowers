import {Component} from '@angular/core';
import {
 DefaultFieldErrorProcessor,
} from "../../common";
import {AbstractControl} from "@angular/forms";
import {AbstractFormComponent} from "../abstract.form.component";
import {ApiFieldError} from "../../error";

@Component({
  selector: 'app-date-input',
  templateUrl: './date-input.component.html',
  styleUrls: ['./date-input.component.css']
})
export class DateInputComponent extends AbstractFormComponent<Date> {

  public dayMask = [/\d/, /\d/];
  public monthMask = [/\d/, /\d/];
  public yearMask = [/\d/, /\d/, /\d/, /\d/];

  public day;
  public month;
  public year;

  ngOnInit() {
    if (!this.param) {
      this.param = {};
    }
    this.param.errorProcessor = new DateErrorProcessor(this);
    super.ngOnInit();
    if(this.value) {
      this.value = new Date(this.value.toString());
      this.day = this.value.getDate();
      this.month = this.value.getMonth()+1;
      this.year = this.value.getFullYear();
    }
  }

  changeDate(event: Event){
    this.formControl.setErrors(null);
    if (this.day && this.month && this.year) {
      let date = new Date();
      date.setHours(0,0,0,0);
      date.setDate(this.day);
      date.setMonth(this.month-1);
      date.setUTCFullYear(this.year);
      if (date.getDate() != this.day || date.getMonth() != this.month-1 || date.getFullYear() != this.year) {
        let error = new ApiFieldError();
        error.code = "format";
        error.field = this.path.join('.');
        this.formControl.setErrors(error);
        this.formControl.errors[error.code] = true;
        this.value = null;
      } else {
        this.value = date;
      }
      super.submit(event);
    } else {
      this.value = null;
      super.submit(event);
    }
  }

}

export class DateErrorProcessor extends DefaultFieldErrorProcessor {

  constructor(private component: DateInputComponent) {
    super();
  }

  applyError(error: ApiFieldError, control: AbstractControl): void {
    if (error.code == 'required' && (this.component.day || this.component.month || this.component.year)) {
      error.code = 'format';
    }
    super.applyError(error, control);
  }
}
