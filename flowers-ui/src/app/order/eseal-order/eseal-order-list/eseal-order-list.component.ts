import {Component, Input} from "@angular/core";
import {ESealOrder} from "../eseal-order";

@Component({
  selector: 'app-eseal-orders-list',
  templateUrl: './eseal-order-list.component.html'
})
export class ESealOrderListComponent {

  @Input()
  eseals: ESealOrder[];
}
