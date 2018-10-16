export class FieldFilter {
  field:Field;
  value:any | Array<any>;
}
export class Field {
  name:string;
  label:string;
  type: FieldFilterType;
  values: FieldValues;

  constructor(name: string, label: string, type: FieldFilterType, values?: FieldValues) {
    this.name = name;
    this.label = label;
    this.type = type;
    this.values = values;
  }
}

export class FieldValues {
  values:any[];
  valuesFieldToShow:string;
  valuesFieldToUse:string;
  translationPrefix:string;

  constructor(values: any[], valuesFieldToShow: string, valuesFieldToUse: string, translationPrefix?: string) {
    this.values = values;
    this.valuesFieldToShow = valuesFieldToShow;
    this.valuesFieldToUse = valuesFieldToUse;
    this.translationPrefix = translationPrefix;
    if ((valuesFieldToShow && !valuesFieldToUse) || (valuesFieldToUse && !valuesFieldToShow)) {
      throw new Error("valuesFieldToUse and valuesFieldToShow must be filled!");
    }
  }
}

export enum FieldFilterType {
  STRING_LIKE = "STRING_LIKE",
  STRING_EQ = "STRING_EQ",
  INTEGER = "INTEGER",
  FLOAT = "FLOAT"
}
