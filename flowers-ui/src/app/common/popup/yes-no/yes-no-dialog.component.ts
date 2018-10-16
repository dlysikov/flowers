import {AfterViewInit, Component, OnInit} from '@angular/core';
import {MzBaseModal} from "ng2-materialize";

@Component({
  selector: 'app-yes-no-dialog',
  templateUrl: './yes-no-dialog.component.html',
  styleUrls: ['./yes-no-dialog.component.css']
})
export class YesNoDialogComponent extends MzBaseModal {

  modalOptions:any = {
    dismissible: false,
    width:"300px"
  };

  textCode: string;

  okCallback(): void {}

  noCallback(): void {}
}
