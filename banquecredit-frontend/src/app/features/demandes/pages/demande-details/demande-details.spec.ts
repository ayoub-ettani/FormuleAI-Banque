import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeDetails } from './demande-details';

describe('DemandeDetails', () => {
  let component: DemandeDetails;
  let fixture: ComponentFixture<DemandeDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
