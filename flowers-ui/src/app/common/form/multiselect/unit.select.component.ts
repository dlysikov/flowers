import {CommonHelper, EqualsById, FormControlConfig, ValidationUtils} from "../../common";
import {Unit} from "../../../unit/unit";
import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Requestor} from "../../static-data";
import {ActivatedRoute} from "@angular/router";
import {AbstractControl, FormControl, FormGroup} from "@angular/forms";
import {FormContext} from "../../component/base-form.component";

@Component({
  selector: 'app-unit-select',
  templateUrl: './unit.select.component.html',
  styleUrls: ['./unit.select.component.css']
})
export class UnitSelectComponent implements OnInit {

  @Input()
  public selectUnitAction: () => void;
  @Input()
  public formContext: FormContext<any>;
  @Input()
  public param?: any = {};

  @Output()
  public entityChange = new EventEmitter();

  entity: any;
  id: string = 'unit';
  form: FormGroup;
  control: FormControlConfig;
  formControl: AbstractControl;
  requestor: Requestor;
  services: Requestor[] = [];
  units: Unit[] = [];

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.entity = this.formContext.getEntity();
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
    this.formControl = new FormControl({value: this.entity.unit, disabled: this.control.disabled});
    this.form.addControl(this.id, this.formControl);

    this.services = this.route.snapshot.data['staticData'].services;
    this.units = this.route.snapshot.data['units'];
    if (this.entity.unit != null) {
      this.requestor = this.entity.unit.requestor;
    }
  }

  public selectRequestor() {
    this.entity.unit = null;
  }

  public selectUnit() {
    if (this.entity.unit != null) {
      this.requestor = this.entity.unit.requestor;
    } else {
      this.requestor = null;
    }
    if (this.selectUnitAction != null) {
      this.selectUnitAction.call(this);
    }
    this.entityChange.emit(this.entity);
  }

  public fieldFormHasErrors(): boolean {
    let control = this.form.get(this.id);
    return !control.disabled && !control.valid;
  }

  getRequired(): string {
    return CommonHelper.getRequired(this.control);
  }

  public equalsById(o1: any, o2: any): boolean {
    return EqualsById(o1, o2);
  }

}
