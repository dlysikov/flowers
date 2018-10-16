import {Component} from "@angular/core";
import {User} from "../user";
import {UserService} from "../user-service";
import {ActivatedRoute, Router} from "@angular/router";
import {Field, FieldFilterType, FieldValues} from "../../common/field-filter/field-filter";
import {UserSession} from "../../common/session";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {BaseListComponent} from "../../common/component/base-list.component";

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent extends BaseListComponent<User> {

  constructor(private route: ActivatedRoute,
              private session: UserSession,
              private spinnerService: Ng4LoadingSpinnerService,
              private router: Router,
              private userService: UserService) {
    super(route, session, "users", [new Field("unit.requestor.name", "user.requestor", FieldFilterType.STRING_LIKE),
                                    new Field("unit.commonName", "user.unit", FieldFilterType.STRING_LIKE),
                                    new Field("firstName", "user.firstName", FieldFilterType.STRING_LIKE),
                                    new Field("roles.id", "user.role", FieldFilterType.INTEGER, new FieldValues(route.snapshot.data["roles"], "roleType", "id", "user.roles.")),
                                    new Field("surName", "user.surName", FieldFilterType.STRING_LIKE),
                                    new Field("ssn", "user.ssn", FieldFilterType.STRING_LIKE)],
         userService, spinnerService, router);
  }
}
