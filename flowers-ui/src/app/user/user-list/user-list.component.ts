import {Component, Input} from "@angular/core";
import {User} from "../user";

@Component({
  selector: 'app-users-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent {

  @Input()
  users: User[];
}
