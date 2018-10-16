import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EsealOrderFormComponent } from './eseal-order-form.component';

describe('EsealOrderFormComponent', () => {
  let component: EsealOrderFormComponent;
  let fixture: ComponentFixture<EsealOrderFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EsealOrderFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EsealOrderFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
