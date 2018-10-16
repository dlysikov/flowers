import {AbstractControl, FormControl, FormGroup} from "@angular/forms";
import {ApiErrors, ApiFieldError} from "./error";
import {TranslateService} from "@ngx-translate/core";
import {FormContext} from "./component/base-form.component";

export enum CompanyIdentifierType {
  VAT = "VAT",
  RCSL = "RCSL",
  OTHER = "OTHER"
}

export class CompanyIdentifier {
  type: CompanyIdentifierType;
  value: string;
}

export class FormControlConfig {

  visible:boolean;
  max:any;
  min:any;
  format:string;
  mask:any[];
  disabled:boolean;
  required: boolean;
  errorProcessor: FieldErrorProcessor;

  constructor(param?: any) {
    if (param) {
      if (param.visible === false) {
        this.visible = param.visible;
      }
      if (param.min) {
        this.min = param.min;
      }
      if (param.max) {
        this.max = param.max;
      }
      if (param.format) {
        this.format = param.format;
      }
      if (param.mask) {
        this.mask = param.mask;
      }
      if (param.disabled) {
        this.disabled = param.disabled;
      }
      if (param.required) {
        this.required = param.required;
      }
    }
  }

  public initDefaultValues(): FormControlConfig {
    if (this.visible == undefined) {
      this.visible = true;
    }
    if (this.max == undefined) {
      this.max = 10000;
    }
    if (this.min == undefined) {
      this.min = 0;
    }
    if (this.disabled == undefined) {
      this.disabled = false;
    }
    if (this.required == undefined) {
      this.required = false;
    }
    if (this.errorProcessor == undefined) {
      this.errorProcessor = new DefaultFieldErrorProcessor();
    }
    return this;
  }

}

export interface FieldErrorProcessor {
  applyError(error: ApiFieldError, control: AbstractControl)
}

export class DefaultFieldErrorProcessor implements FieldErrorProcessor {
  applyError(error: ApiFieldError, control: AbstractControl) {
    if (!control.errors) {
      control.setErrors({});
    }
    control.errors[error.code] = true;
  }
}

export class ValidationUtils {

  static applyErrorsToForm(formContext: FormContext<any>, errObj: any): ApiErrors {
    let errors = Object.assign(new ApiErrors(), errObj.error);
    if (errors.fieldErrors != null && errors.fieldErrors.length > 0) {
    errors.fieldErrors.forEach((error: ApiFieldError) => {
      let path = error.field.replace('.', '-');
      let control = formContext.getForm().get(path);
      if (control) {
        formContext.getControlConfig(path).errorProcessor.applyError(error, control);
      }
    });
    }
    return errors;
  }

  static setRequiredFields(formContext:FormContext<any>, errObj: any, ...alwaysMandatory: string[]) {
    let errors = Object.assign(new ApiErrors(), errObj);
    if (errors) {
      errors.fieldErrors.forEach((error: ApiFieldError) => {
        if (error.code == 'required') {
          let control = formContext.getControlConfig(error.field.replace('.', '-'));
          if (control) {
            control['required'] = true;
          }
        }
      });
    }
    if (alwaysMandatory) {
      alwaysMandatory.forEach((f) => {
        let control  = formContext.getControlConfig(f.replace('.', '-'));
        if (control) {
          control['required'] = true;
        }
      })
    }
  }

  static clearErrors(form: FormGroup) {
    Object.keys(form.controls).forEach((c) => {
      if (form.get(c) instanceof FormGroup) {
        this.clearErrors(form.get(c) as FormGroup);
      } else {
        form.get(c).setErrors(null);
      }
    })
  }

  static fieldFormHasErrors(form: FormGroup, fieldPath: Array<string> | string): boolean {
    let control = form.get(fieldPath);
    if (control.disabled) {
      return false;
    }
    return !control.valid;
  }

  static getGlobalMmg(errors: ApiErrors): FormGlobalMsg {
    let msg = new FormGlobalMsg(FormGlobalMsgType.NONE);
    if (errors['status'] == 500) {
      msg = new FormGlobalMsg(FormGlobalMsgType.FAILED, 'internal-server-error');
    } else if (errors['status'] == 403) {
      msg = new FormGlobalMsg(FormGlobalMsgType.FAILED, 'access-denied');
    } else if (errors.globalErrors.length > 0) {
      msg = new FormGlobalMsg(FormGlobalMsgType.FAILED);
      errors.globalErrors.forEach((e) => msg.codes.push(e.code));
    } else if (errors.fieldErrors.length > 0) {
      msg = new FormGlobalMsg(FormGlobalMsgType.FAILED, 'validation-error');
    }
    return msg;
  }
}

export class FormButton {
  constructor(public enabled: boolean, public visible: boolean) {
  }
}

export enum FormGlobalMsgType {
  NONE="NONE", FAILED="FAILED", SUCCESS="SUCCESS"
}

export class FormGlobalMsg {
  type: FormGlobalMsgType;
  codes: string[] = [];

  constructor(type: FormGlobalMsgType, msgCode? : string) {
    this.type = type;
    if (msgCode) {
      this.codes.push(msgCode);
    }
  }
}

export class CommonHelper {
  static scrollToBottom() {
    setTimeout(() => {
      window.scrollTo(0, document.body.scrollHeight);
    }, 100);
  }

  static getRequired(config: FormControlConfig): string {
    let result = '';
    if (config) {
      result = config.required ? '*' : '';
    }
    return result;
  }
}


export function EqualsById(o1: any, o2: any):boolean {
  if (o1 && o2) {
    if (o1['id'] && o2['id']) {
      return o1['id'] === o2['id'];
    }
  }
  return o1 === o2;
}

export function compareWithTranslation(prefix: string, field: string, translateService: TranslateService): any {
  return (o1: any,o2: any) => {
    if (translateService.instant(prefix + o1[field]) > translateService.instant(prefix + o2[field])) {
      return 1;
    }
    if (translateService.instant(prefix + o1[field]) < translateService.instant(prefix + o2[field])) {
      return -1;
    }
    return 0;
  }
}

export function compareByField(field: string): any {
  return (o1: any,o2: any) => {
    if (o1[field] > o2[field]) {
      return 1;
    }
    if (o1[field] < o2[field]) {
      return -1;
    }
    return 0;
  }
}

export interface Selectable {
  getSortByField():string;
  getView():string;
}
