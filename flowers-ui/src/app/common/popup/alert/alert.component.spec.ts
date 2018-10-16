import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertPopup } from './alert.component';

describe('AlertPopup', () => {
  let component: AlertPopup;
  let fixture: ComponentFixture<AlertPopup>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlertPopup ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertPopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
