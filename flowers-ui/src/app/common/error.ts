export class ApiErrors {
  fieldErrors: ApiFieldError[];
  globalErrors: ApiGlobalError[];
}
export class ApiFieldError {
  field: string;
  code:string;
  defaultMessage:string;
  rejectedValue: any;
}
export class ApiGlobalError {
  code:string;
  defaultMessage:string;
  details:any;
}
