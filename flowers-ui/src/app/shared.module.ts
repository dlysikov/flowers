import {ModuleWithProviders, NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslateModule} from "@ngx-translate/core";
import {HttpClientModule} from "@angular/common/http";
import {NgxPermissionsModule} from "ngx-permissions";
import {StaticDataService} from "./common/static-data-service";
import {StaticDataResolve} from "./common/static-data-resolve";
import {Ng2OrderModule} from "ng2-order-pipe";
import {ChartsModule} from 'ng2-charts';
import {Ng4LoadingSpinnerModule} from 'ng4-loading-spinner';
import {MatDialogModule} from "@angular/material";
import {CdkTableModule} from '@angular/cdk/table';
import {AlertPopup} from './common/popup/alert/alert.component';
import {TermsAndConditionsComponent} from './common/popup/terms-and-conditions/terms-and-conditions.component';
import {PageNotFoundComponent} from "./page-not-found/page-not-found.component";
import {RoutingState} from "./common/routing-state";
import {DpDatePickerModule} from "ng2-date-picker";

import {
  MzButtonModule,
  MzCheckboxModule, MzIconMdiModule, MzIconModule, MzInputModule, MzSelectModule, MzTooltipModule,
  MzValidationModule, MzDropdownModule, MzModalModule, MzPaginationModule
} from 'ng2-materialize';
import {ErrorComponent} from "./common/error/error.component";
import {TextMaskModule} from "angular2-text-mask";
import {InfoComponent} from "./common/info/info.component";
import {BackToListComponent} from "./common/back-to-list/back-to-list.component";
import {YesNoDialogComponent} from "./common/popup/yes-no/yes-no-dialog.component";
import {FieldFilterComponent} from "./common/field-filter/field-filter.component";
import {PaginationComponent} from "./common/pagination/pagination.component";
import {UnitsResolve} from "./common/units-resolve";
import {FInputComponent} from "./common/form/input/input.component";
import {FSelectComponent} from "./common/form/select/select.component";
import {UnitSelectComponent} from "./common/form/multiselect/unit.select.component";
import { DateInputComponent } from './common/form/date-input/date-input.component';
import {FCheckboxComponent} from "./common/form/checkbox/checkbox.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    HttpClientModule,
    DpDatePickerModule,
    Ng2OrderModule,
    ChartsModule,
    MzModalModule,
    MzButtonModule,
    TextMaskModule,
    MzValidationModule,
    MzTooltipModule,
    MzPaginationModule,
    MzSelectModule,
    MzDropdownModule,
    MzInputModule,
    MzCheckboxModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    DpDatePickerModule,
    ReactiveFormsModule,
    TranslateModule,
    HttpClientModule,
    NgxPermissionsModule,
    Ng2OrderModule,
    NgxPermissionsModule,
    Ng4LoadingSpinnerModule,
    MatDialogModule,
    CdkTableModule,
    TextMaskModule,
    ChartsModule,
    MzDropdownModule,
    MzSelectModule,
    MzPaginationModule,
    MzInputModule,
    MzValidationModule,
    MzIconModule,
    MzIconMdiModule,
    MzCheckboxModule,
    MzButtonModule,
    MzTooltipModule,
    YesNoDialogComponent,
    ErrorComponent,
    BackToListComponent,
    FieldFilterComponent,
    InfoComponent,
    PaginationComponent,
    FInputComponent,
    FSelectComponent,
    UnitSelectComponent,
    DateInputComponent,
    FCheckboxComponent
  ],
  providers: [StaticDataService, StaticDataResolve, RoutingState, UnitsResolve],
  declarations: [
    AlertPopup,
    TermsAndConditionsComponent,
    PageNotFoundComponent,
    ErrorComponent,
    PaginationComponent,
    InfoComponent,
    BackToListComponent,
    YesNoDialogComponent,
    FieldFilterComponent,
    FInputComponent,
    FSelectComponent,
    UnitSelectComponent,
    DateInputComponent,
    FCheckboxComponent
  ],
  entryComponents: [AlertPopup, TermsAndConditionsComponent, PageNotFoundComponent,  YesNoDialogComponent]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule,

      providers: [
        FormBuilder
      ]
    };
  }
}
