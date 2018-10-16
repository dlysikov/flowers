import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EsealActivationFormComponent } from './eseal-activation-form.component';

describe('EsealActivationFormComponent', () => {
  let component: EsealActivationFormComponent;
  let fixture: ComponentFixture<EsealActivationFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EsealActivationFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EsealActivationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
