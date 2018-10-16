import {AfterViewInit, Component,  Input, ViewChild} from "@angular/core";
import {
  compareByField,
  compareWithTranslation,
  EqualsById,
  Selectable,
} from "../../common";
import {TranslateService} from "@ngx-translate/core";
import {MzSelectContainerComponent} from "ng2-materialize";
import {UserSession} from "../../session";
import {AbstractFormComponent} from "../abstract.form.component";


@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.css']

})
export class FSelectComponent<T extends Selectable> extends AbstractFormComponent<T> implements AfterViewInit {

  constructor(private translateService: TranslateService,
              private _session: UserSession) {
    super();
  }

  @ViewChild("select")
  select: MzSelectContainerComponent;

  @Input()
  public values: T[];
  @Input()
  public translateViewPrefix: string;
  @Input()
  public multiple: boolean;
  @Input()
  public condition: (value: T) => boolean;
  @Input()
  public objectContent: boolean = true;
  @Input()
  public selectAction: () => void;

  public equalsById(o1: any, o2: any): boolean {
    return EqualsById(o1, o2);
  }


  public submit(event: Event): void {
    super.submit(event);
    if(this.selectAction) {
      this.selectAction.call(this);
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.translateViewPrefix != null) {
      this._session.onLanguageChanged.subscribe(() => this.sortData());
    }

  }

  ngAfterViewInit(): void {
    this.sortData();
  }

  sortData(): void {
    if (this.values[0]) {
      setTimeout(() => {
        if (this.translateViewPrefix != null) {
          this.values = this.values.sort(compareWithTranslation(this.translateViewPrefix, this.objectContent ? this.values[0].getSortByField() : null, this.translateService));
          if (this.select) {
            this.select.mzSelectDirective.updateMaterialSelect();
          }

        } else {
          if (this.objectContent) {
            this.values = this.values.sort(compareByField(this.values[0].getSortByField()));
          } else {
            this.values = this.values.sort();
          }
        }
      }, 200);
    }
  }

  showOption(value: T): string {
    if (this.translateViewPrefix == null) {
      return this.objectContent ? value.getView() : value.toString();
    } else {
      return this.translateService.instant(this.translateViewPrefix + (this.objectContent ? value.getView() : value));
    }
  }

  showCondition(value: T): boolean {
    if (this.condition) {
      return this.condition.call(null, value);
    } else {
      return true;
    }
  }

}
