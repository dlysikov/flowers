import "rxjs/Rx";
import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Address, Holder, CertificateOrder} from "./certificate-order";
import {environment} from "../../../environments/environment";
import {ImportResult} from "../../common/import-result";
import {OrderMobileCodeVerification} from "../mobile-code-verification/order-mobile-code-verification";
import {ServicePagingImpl} from "../../common/service/service-paging";

@Injectable()
export class CertificateOrderService extends ServicePagingImpl<CertificateOrder>{

  constructor(private httpClient: HttpClient) {
    super(httpClient, `${environment.apiEndpoint}/orders`);
  }

  importOrdersFromXML(file: File): Observable<ImportResult<CertificateOrder>> {
    let formData: FormData = new FormData();
    formData.append("file", file);
    return this.httpClient.post<ImportResult<CertificateOrder>>(`${environment.apiEndpoint}/orders/xml`, formData).map(r => Object.assign(new ImportResult(), r));
  }

  getUserPage(pageHash: string, authCode: string): Observable<CertificateOrder> {
    return this.httpClient.get<CertificateOrder>(`${environment.apiEndpoint}/orders/user/validate/${pageHash}`, {headers: this.userPageAuthHeader(authCode)}).map(r => CertificateOrderService.toOrder(r));
  }

  private userPageAuthHeader(authCode: string): HttpHeaders {
    let header = new HttpHeaders();
    return header.append("Order-User-Validates-Auth", authCode || "");
  }

  isMobileCodeVerificationRequired(pageHash: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${environment.apiEndpoint}/orders/user/validate/is_mobile_code_required/${pageHash}`);
  }

  getOrder(id: number): Observable<CertificateOrder> {
    return this.httpClient.get<CertificateOrder>(`${environment.apiEndpoint}/orders/${id}`).map(res => CertificateOrderService.toOrder(res));
  }

  public static toOrder(data: any): CertificateOrder {
    let order =  Object.assign(new CertificateOrder(), data);
    if (!order.holder) {
      order.holder = new Holder();
    }
    if (!order.address) {
      order.address = new Address();
    }
    return order;
  }

  public saveAsDraft(order: CertificateOrder): Observable<any> {
    return this.httpClient.put(`${environment.apiEndpoint}/orders`, order);
  }

  public updateDraft(order: CertificateOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/${order.id}`, order);
  }

  public getRequiredFields(order: CertificateOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/required_fields`, order);
  }

  public getRequiredFieldsForUser(order: CertificateOrder, pageHash: string, authCode: string): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/user/validate/${pageHash}/required_fields`, order, {headers: this.userPageAuthHeader(authCode)});
  }

  public submitOrder(order: CertificateOrder): Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/${order.id}/submit`, order);
  }

  public submitOrderWithoutDraft(order: CertificateOrder) :Observable<any> {
    return this.httpClient.put(`${environment.apiEndpoint}/orders/submit`, order);
  }

  uploadDocuments(pageHash: string, documents: File[], authCode: string): Observable<any> {
    let formData: FormData = new FormData();
    documents.forEach((doc, index) => {
      formData.append("file[" + index + "]", doc);
    });
    return this.httpClient.post<ImportResult<CertificateOrder>>(`${environment.apiEndpoint}/orders/user/validate/${pageHash}/document`, formData, {headers: this.userPageAuthHeader(authCode)});
  }

  deleteDocument(pageHash: string, documentName: string, authCode: string) {
    return this.httpClient.delete(`${environment.apiEndpoint}/orders/user/validate/${pageHash}/document/` + documentName, {headers: this.userPageAuthHeader(authCode)});
  }

  submitUserValidates(order: CertificateOrder, pageHash: string, authCode: string):Observable<any> {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/user/validate/${pageHash}`, order, {headers: this.userPageAuthHeader(authCode)});
  }

  verifyMobileCode(verification: OrderMobileCodeVerification): Observable<boolean> {
    return this.httpClient.post<boolean>(`${environment.apiEndpoint}/orders/user/validate/verify_mobile_code`, verification);
  }

  getDocument(orderId:number, documentId: number) {
    return this.httpClient.get(`${environment.apiEndpoint}/orders/${orderId}/document/${documentId}`,{responseType: "blob"});
  }

  getDocumentToUser(pageHash:number, documentId: number, authCode: string) {
    return this.httpClient.get(`${environment.apiEndpoint}/orders/user/validate/${pageHash}/document/${documentId}`,{responseType: "blob", headers: this.userPageAuthHeader(authCode)});
  }

  sendToLrs(order: CertificateOrder) {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/${order.id}/send_to_lrs`, order);
  }

  sendToLrsFromDraft(order: CertificateOrder) {
    return this.httpClient.put(`${environment.apiEndpoint}/orders/send_to_lrs`, order);
  }

  reject(orderId: number) {
    return this.httpClient.post(`${environment.apiEndpoint}/orders/${orderId}/reject`, orderId);
  }
}
