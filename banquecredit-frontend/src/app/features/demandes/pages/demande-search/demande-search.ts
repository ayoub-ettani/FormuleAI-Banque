import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { debounceTime, startWith } from 'rxjs';
import { DemandeRechercheFiltres } from '../../models/demande-recherche-filtres.model';
import { DemandeCredit, StatutDemande } from '../../models/demande.model';
import { DemandeService } from '../../services/demande.service';

type DemandeSearchForm = FormGroup<{
  clientNom: FormControl<string>;
  statut: FormControl<StatutDemande | ''>;
  montantMin: FormControl<number | null>;
  montantMax: FormControl<number | null>;
}>;

function montantRangeValidator(control: AbstractControl): ValidationErrors | null {
  const montantMin = control.get('montantMin')?.value as number | null;
  const montantMax = control.get('montantMax')?.value as number | null;

  if (montantMin === null || montantMin === undefined || montantMax === null || montantMax === undefined) {
    return null;
  }

  return montantMin <= montantMax ? null : { montantRange: true };
}

@Component({
  selector: 'app-demande-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './demande-search.html',
  styleUrl: './demande-search.css'
})
export class DemandeSearchComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly demandeService = inject(DemandeService);

  readonly statuts: StatutDemande[] = [
    'BROUILLON',
    'SOUMISE',
    'EN_ANALYSE',
    'ACCEPTEE',
    'REFUSEE',
    'ANNULEE'
  ];

  readonly form: DemandeSearchForm;

  demandes: DemandeCredit[] = [];
  isLoading = false;
  errorMessage = '';

  constructor() {
    this.form = this.fb.group(
      {
        clientNom: this.fb.nonNullable.control(''),
        statut: this.fb.nonNullable.control<StatutDemande | ''>(''),
        montantMin: this.fb.control<number | null>(null),
        montantMax: this.fb.control<number | null>(null)
      },
      { validators: montantRangeValidator }
    );
  }


  ngOnInit(): void {
    this.form.valueChanges
      .pipe(startWith(this.form.getRawValue()), debounceTime(300), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.form.hasError('montantRange')) {
          this.demandes = [];
          this.isLoading = false;
          this.errorMessage =
            'Le montant minimum doit etre inferieur ou egal au montant maximum.';
          return;
        }

        this.rechercher();
      });
  }

  resetForm(): void {
    this.form.reset({
      clientNom: '',
      statut: '',
      montantMin: null,
      montantMax: null
    });
  }

  statutClass(statut: StatutDemande): string {
    switch (statut) {
      case 'ACCEPTEE':
        return 'badge-green';
      case 'REFUSEE':
        return 'badge-red';
      case 'EN_ANALYSE':
      case 'SOUMISE':
        return 'badge-orange';
      case 'ANNULEE':
        return 'badge-gray';
      default:
        return 'badge-blue';
    }
  }

  private rechercher(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.demandeService.rechercher(this.buildFiltres()).subscribe({
      next: (data) => {
        this.demandes = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.demandes = [];
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Erreur lors de la recherche des demandes';
      }
    });
  }

  private buildFiltres(): DemandeRechercheFiltres {
    const { clientNom, statut, montantMin, montantMax } = this.form.getRawValue();

    return {
      ...(clientNom.trim() ? { clientNom: clientNom.trim() } : {}),
      ...(statut ? { statut } : {}),
      ...(montantMin !== null ? { montantMin } : {}),
      ...(montantMax !== null ? { montantMax } : {})
    };
  }
}

