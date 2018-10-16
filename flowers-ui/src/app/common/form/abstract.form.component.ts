import {CommonHelper, FormControlConfig} from "../common";
import {AbstractControl, FormControl, FormGroup} from "@angular/forms";
import {FormContext} from "../component/base-form.component";
import {Input, OnInit} from "@angular/core";

export function controlId(path:string[]): string {
  return path.join('-');
}

export class AbstractFormComponent<T> implements OnInit {

  @Input()
  public formContext: FormContext<T>;
  @Input()
  public label: string;
  @Input()
  public errorLabel: string;
  @Input()
  public param?: any = {};
  @Input()
  public tooltip: string;
  @Input()
  public path: string[];
  @Input()
  public valueChangeAction: (value:any) => void;

  value: any;
  id: string;
  entity: T;
  form: FormGroup;
  control: FormControlConfig;
  formControl: AbstractControl;

  ngOnInit(): void {
    if (this.path) {
      this.id = controlId(this.path);
    }
    this.value = this.formContext.getEntity();
    this.path.forEach(value => this.value = this.value[value]);

    this.control = this.formContext.getControlConfig(this.id);
    if (this.param) {
      Object.keys(this.param).forEach(value => {
        if (this.control[value] == null) {
          this.control[value] = this.param[value];
        }
      });
    }
    this.control.initDefaultValues();

    this.form = this.formContext.getForm();
    this.formControl = new FormControl({value: this.value, disabled: this.control.disabled});
    this.form.addControl(this.id, this.formControl);
    if (this.valueChangeAction) {
      this.formControl.valueChanges.subscribe(this.valueChangeAction);
    }
  }

  getRequired(): string {
    return CommonHelper.getRequired(this.control);
  }

  public fieldFormHasErrors(): boolean {
    let control = this.form.get(this.id);
    return !control.disabled && !control.valid;
  }

  public submit(event: Event) {
    event.stopPropagation();
    let value = this.formContext.getEntity();
    for (let i = 0; i < this.path.length - 1; i++) {
      value = value[this.path[i]];
    }
    value[this.path[this.path.length-1]] = this.value;
  }
}
