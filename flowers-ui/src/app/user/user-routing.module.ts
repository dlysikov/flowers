import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../guard/auth.guard";
import {NgxPermissionsGuard} from "ngx-permissions";

import {UserFormComponent} from "./user-form/user-form-component";
import {UserDetailsResolve} from "./user-form/user-details-resolve";
import {ManageUsersComponent} from "./manage-users/manage-users.component";
import {ManageUsersResolve} from "./manage-users/manage-users.resolve";
import {StaticDataResolve} from "../common/static-data-resolve";
import {UnitsResolve} from "../common/units-resolve";
import {UserRolesToCreateResolve} from "./user-roles-to-create-resolve";
import {Permission} from "../authentication/authentication";

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: "",
      pathMatch: 'full',
      component: ManageUsersComponent,
      resolve: {users: ManageUsersResolve, roles:UserRolesToCreateResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.READ_USER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "details/:userId",
      pathMatch: 'full',
      component: UserFormComponent,
      resolve: {user: UserDetailsResolve, staticData: StaticDataResolve, units: UnitsResolve, roles:UserRolesToCreateResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.READ_USER, Permission.EDIT_USER],
          redirectTo: "/"
        }
      },
    },
    {
      path: "details",
      pathMatch: 'full',
      component: UserFormComponent,
      resolve: {staticData: StaticDataResolve, units: UnitsResolve, roles:UserRolesToCreateResolve},
      canActivate: [AuthGuard, NgxPermissionsGuard],
      data: {
        permissions: {
          only: [Permission.CREATE_USER],
          redirectTo: "/"
        }
      },
    }
  ])],
  exports: [RouterModule]
})

export class UserRoutingModule {
}
