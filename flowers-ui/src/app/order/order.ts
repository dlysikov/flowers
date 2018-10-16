import {Unit} from "../unit/unit";

export abstract class AbstractOrder {
  public id:number;
  public lastStatusDate:Date;
  public lrsOrderNumber:string;
  public ssn:string;
  public unit:Unit;
  public publish:boolean;
  public status:OrderStatus = OrderStatus.DRAFT;
  public acceptedGTC:boolean;
}

export enum OrderStatus {
  DRAFT = "DRAFT",
  USER_DRAFT = "USER_DRAFT",
  CSD_REVIEW_REQUIRED = "CSD_REVIEW_REQUIRED",
  DIA_SIGNING_REQUIRED = "DIA_SIGNING_REQUIRED",
  REMOTE_IDENTIFICATION_REQUIRED = "REMOTE_IDENTIFICATION_REQUIRED",
  FACE_2_FACE_REQUIRED = "FACE_2_FACE_REQUIRED",
  SENT_TO_LRS = "SENT_TO_LRS",
  PENDING_FINAL_IDENTIFICATION = "PENDING_FINAL_IDENTIFICATION",
  PENDING_CSD_DECISION = "PENDING_CSD_DECISION",
  REJECTED = "REJECTED",
  LRS_ONGOING = "LRS_ONGOING",
  LRS_PRODUCED = "LRS_PRODUCED",
  LRS_ACTIVATED = "LRS_ACTIVATED",
  LRS_CANCELED = "LRS_CANCELED"
}

export enum OrderStatusColor {
  DRAFT = "#d1d4d3",
  USER_DRAFT = "#a5acb0",
  CSD_REVIEW_REQUIRED = "#005072",
  DIA_SIGNING_REQUIRED = "#5fceea",
  REMOTE_IDENTIFICATION_REQUIRED = "#00aae7",
  FACE_2_FACE_REQUIRED = "#0081ab",
  SENT_TO_LRS = "#36424a",
  PENDING_FINAL_IDENTIFICATION = "#a50069",
  PENDING_CSD_DECISION = "#682145",
  REJECTED = "#ef69b9",
  LRS_ONGOING = "#bed600",
  LRS_PRODUCED = "#8dc63f",
  LRS_ACTIVATED = "#719500",
  LRS_CANCELED = "#d60080"
}
