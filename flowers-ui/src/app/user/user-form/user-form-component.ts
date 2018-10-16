import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {Role, RoleType, User} from "../user";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateType, Language, Nationality, Requestor} from "../../common/static-data";
import {Ng4LoadingSpinnerService} from "ng4-loading-spinner";
import {UserService} from "../user-service";
import {Unit} from "../../unit/unit";
import {UserSession} from "../../common/session";
import {MzModalService} from "ng2-materialize";
import {BaseFormComponent} from "../../common/component/base-form.component";
import {TranslateService} from "@ngx-translate/core";
import {FInputComponent} from "../../common/form/input/input.component";

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form-component.html',
  styleUrls: ['./user-form-component.css']
})
export class UserFormComponent extends BaseFormComponent<User>  implements OnInit, AfterViewInit{

  certificateTypeList : CertificateType[] = [CertificateType.PRIVATE, CertificateType.PROFESSIONAL_PERSON, CertificateType.PROFESSIONAL_CERTIFICATE_ADMINISTRATOR];
  nationalities: Nationality[] = [];
  services: Requestor[] = [];
  languages: Language[] = [];
  units: Unit[] = [];
  rolesToCreate:Role[];
  requestor: Requestor;

  @ViewChild("rolesSelect")
  rolesSelect: FInputComponent<Role>;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private session: UserSession,
              private translateService:TranslateService,
              private modalService: MzModalService,
              private spinnerService: Ng4LoadingSpinnerService,
              private userService: UserService) {
    super(router, session, modalService, spinnerService);
  }

  prepareData(): void {
    if (this.route.snapshot.data['user']) {
      this.entity = this.route.snapshot.data['user'];
      if (this.entity.unit)
      this.requestor = this.entity.unit.requestor;
    } else {
      this.entity = new User();
      this.entity.certificateType = CertificateType.PROFESSIONAL_PERSON;
    }
    this.services = this.route.snapshot.data['staticData'].services;
    this.units = this.route.snapshot.data['units'];
    this.nationalities = this.route.snapshot.data['staticData'].nationalities;
    this.languages = this.route.snapshot.data['staticData'].languages;
    this.rolesToCreate = this.route.snapshot.data['roles'];
  }


  createForm(): void {
    this.form = this.formBuilder.group({});
  }

  public handleRoles(): void {
    let birthDateAndNationalityMandatory = false;
    this.entity.roles.forEach((r: Role) => {
      if (r.roleType === RoleType.ESEAL_MANAGER) {
        birthDateAndNationalityMandatory = true;
      }
    });
    let nationalityControl = this.formContext.getControlConfig('nationality');
    let birthDateControl = this.formContext.getControlConfig('birthDate');
    if (birthDateAndNationalityMandatory) {
      birthDateControl.required = true;
      nationalityControl.required = true;
    } else {
      birthDateControl.required = false;
      nationalityControl.required = false;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formContext.put('rolesSelect', this.rolesSelect);
  }

  formChangedAdditionalActions(): void {
    this.handleRoles();
  }

  saveUser() {
    this.spinnerService.show();
    if (this.entity.id) {
      this.userService.updateUser(this.entity).subscribe((u: User) => this.actionSuccess(u), (e) => this.actionFailed(e));
    } else {
      this.userService.createUser(this.entity).subscribe((u: User) => this.actionSuccess(u), (e) => this.actionFailed(e));
    }
  }

  unitSelected(): void {
    this.entity.roles = [];
    this.formContext.get('rolesSelect').value = null;
  }

  rolesCondition():(value: Role) => boolean {
    return (value:Role) => {
      if (this.formContext.getEntity().unit) {
        return value.roleType == RoleType.DIA || value.roleType == RoleType.ESEAL_MANAGER;
      } else {
        return value.roleType != RoleType.DIA && value.roleType != RoleType.ESEAL_MANAGER;
      }
    };
  }

}
