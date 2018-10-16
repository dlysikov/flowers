import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Unit} from "../unit";
import {Field, FieldFilterType} from "../../common/field-filter/field-filter";
import {UserSession} from "../../common/session";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {UnitService} from "../unit-service";
import {BaseListComponent} from "../../common/component/base-list.component";

@Component({
  selector: 'app-manage-units',
  templateUrl: './manage-units.component.html',
  styleUrls: ['./manage-units.component.css']
})
export class ManageUnitsComponent extends BaseListComponent<Unit> {

  constructor(private route: ActivatedRoute,
              private session: UserSession,
              private unitService: UnitService,
              private spinnerService: Ng4LoadingSpinnerService,
              private router: Router) {
    super(route, session, "units", [new Field("requestor.name", "unit.requestor", FieldFilterType.STRING_LIKE),
                                     new Field("commonName", "unit.common-name", FieldFilterType.STRING_LIKE),
                                     new Field("identifier.value", "unit.company-identifier-value", FieldFilterType.STRING_LIKE)],
          unitService, spinnerService, router);
  }
}
