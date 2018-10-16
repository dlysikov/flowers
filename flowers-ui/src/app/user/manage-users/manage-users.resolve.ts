import {Injectable} from "@angular/core";
import {UserService} from "../user-service";
import {BaseManageListResolve} from "../../common/component/base-manage-list-resolve";
import {User} from "../user";

@Injectable()
export class ManageUsersResolve extends BaseManageListResolve<User>{

  constructor(private userService: UserService) {
    super(userService, "usersFilter");
  }
}
