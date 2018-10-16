import {Component, Input} from '@angular/core';
import {CertificateOrder} from "../certificate-order";
import {OrderStatusColor} from "../../order";

@Component({
  selector: 'app-certificate-orders-list',
  templateUrl: './certificate-orders-list.component.html',
  styleUrls: ['./certificate-orders-list.component.css']
})
export class CertificateOrdersListComponent {

  @Input()
  orders: CertificateOrder[];

  statusColor: any = OrderStatusColor;

  ngOnInit(): void {
  }

  ordersEmpty(): boolean {
    return this.orders == null || this.orders.length == 0;
  }
}
