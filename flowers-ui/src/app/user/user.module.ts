import {NgModule} from '@angular/core';
import {SharedModule} from "../shared.module";
import {UserRoutingModule} from "./user-routing.module";
import {RouterModule} from "@angular/router";
import {UserListComponent} from "./user-list/user-list.component";
import {UserFormComponent} from "./user-form/user-form-component";
import {UserDetailsResolve} from "./user-form/user-details-resolve";
import {ManageUsersComponent} from "./manage-users/manage-users.component";
import {ManageUsersResolve} from "./manage-users/manage-users.resolve";
import {UserRolesToCreateResolve} from "./user-roles-to-create-resolve";
import {RolesFilterPipe} from "../common/pipes/roles-filter.pipe";

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    UserRoutingModule
  ],
  declarations: [
    UserListComponent,
    UserFormComponent,
    ManageUsersComponent,
    RolesFilterPipe],
  providers: [UserDetailsResolve, ManageUsersResolve, UserRolesToCreateResolve]
})
export class UserModule {
}
