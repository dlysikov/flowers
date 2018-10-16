import {Component, OnInit} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {MzBaseModal} from "ng2-materialize";

@Component({
  selector: 'app-popup-success',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertPopup extends MzBaseModal implements OnInit{

  imgLocation: any;

  modalOptions: any = {
    dismissible: false
  };

  alertType: any;
  param: any;
  message: any;

  ngOnInit(): void {
    this.imgLocation = environment.imagesLocation + this.alertType;
  }

}
export enum AlertType {
  SUCCESS="success-icon.png",
  FAILED="failed-icon.png",
  INFO="info-icon.png"
}
