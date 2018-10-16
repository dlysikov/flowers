import { Pipe, PipeTransform } from '@angular/core';
import {Role, RoleType, User} from "../../user/user";
import {Unit} from "../../unit/unit";

@Pipe({
  name: 'rolesFilter'
})
export class RolesFilterPipe implements PipeTransform {

  transform(allRoles: Role[], unit: Unit): any {
    if (allRoles) {
      if (unit) {
        return allRoles.filter((role) => {
          return role.roleType == RoleType.DIA || role.roleType == RoleType.ESEAL_MANAGER;
        });
      } else {
        return allRoles.filter((role) => {
          return role.roleType != RoleType.DIA && role.roleType != RoleType.ESEAL_MANAGER;
        });
      }
    }
  }

}
