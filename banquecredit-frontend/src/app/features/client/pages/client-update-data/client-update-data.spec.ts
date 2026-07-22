import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientUpdateData } from './client-update-data';

describe('ClientUpdateData', () => {
  let component: ClientUpdateData;
  let fixture: ComponentFixture<ClientUpdateData>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientUpdateData]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientUpdateData);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
