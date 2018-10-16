import {environment} from "../../environments/environment";
import {AuthorityType, Permission} from "../authentication/authentication";

export class MenuItem {
  constructor(public iconPath: string, public labelCode: string, public   permissions: Permission[], public routerLink: string[]) {
  }
}

const CERTIFICATE_ORDER_PERMISSIONS = [
  Permission.READ_CERTIFICATEORDER
];

const UNIT_PERMISSIONS = [
  Permission.READ_UNIT
];

const USER_PERMISSIONS = [
  Permission.READ_USER
];

const ESEAL_ORDER_PERMISSIONS = [
  Permission.READ_ESEALORDER,
];

export const HOME_PAGE_MENU:MenuItem[] = [
  new MenuItem(environment.imagesLocation + "manage-orders-icon.png",  'home-page.manage-users' , CERTIFICATE_ORDER_PERMISSIONS, ['certificate-orders']),
  new MenuItem(environment.imagesLocation + "manage-units-icon.png",  'home-page.manage-units' ,UNIT_PERMISSIONS, ['units']),
  new MenuItem(environment.imagesLocation + "manage-users-icon.png",  'home-page.manage-system-users' ,USER_PERMISSIONS, ['users']),
  new MenuItem(environment.imagesLocation + "manage-eseals-icon.png",  'home-page.manage-eseals' ,ESEAL_ORDER_PERMISSIONS, ['eseal-orders'])
];
