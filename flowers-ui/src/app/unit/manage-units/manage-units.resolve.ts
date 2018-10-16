import {Injectable} from "@angular/core";
import {UnitService} from "../unit-service";
import {BaseManageListResolve} from "../../common/component/base-manage-list-resolve";
import {Unit} from "../unit";

@Injectable()
export class ManagerUnitsResolve extends BaseManageListResolve<Unit> {

  constructor(private unitService: UnitService) {
    super(unitService, "unitsFilter");
  }
}
