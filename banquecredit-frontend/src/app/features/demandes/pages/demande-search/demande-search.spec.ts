import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { DemandeService } from '../../services/demande.service';

import { DemandeSearchComponent } from './demande-search';

describe('DemandeSearchComponent', () => {
  let component: DemandeSearchComponent;
  let fixture: ComponentFixture<DemandeSearchComponent>;
  let demandeServiceSpy: jasmine.SpyObj<any>;

  beforeEach(async () => {
    demandeServiceSpy = jasmine.createSpyObj('DemandeService', ['rechercher']);
    demandeServiceSpy.rechercher.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [DemandeSearchComponent],
      providers: [provideRouter([]), { provide: DemandeService, useValue: demandeServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(DemandeSearchComponent);
    component = fixture.componentInstance;
  });

  it('should create', fakeAsync(() => {
    fixture.detectChanges();
    tick(300);

    expect(component).toBeTruthy();
    expect(demandeServiceSpy.rechercher).toHaveBeenCalled();
  }));
});

