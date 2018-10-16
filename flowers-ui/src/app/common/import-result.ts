export class ImportResult<T> {
  successful:number;
  failed:number;
  failedDetails: T[];
}
