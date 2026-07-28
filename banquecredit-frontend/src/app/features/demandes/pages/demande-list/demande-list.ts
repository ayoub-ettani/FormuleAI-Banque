import { Component, inject } from '@angular/core';
import { Demande } from '../../models/demande.model';
import { DemandeService } from '../../services/demande.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-demande-list',
  imports: [],
  templateUrl: './demande-list.html',
  styleUrl: './demande-list.css',
})
export class DemandeList {
  demandes : Demande[] = [];
  
  private demandeService =inject(DemandeService);
  private router = inject(Router);
  
  ngOnInit(): void{
    this.demandeService.getAll().subscribe({
      next: demandes => this.demandes = demandes,
      error : err => console.error(err)
    });
  }

  afficherDettails(id : number) : void{
    this.router.navigate(['/demandes', id]);
  }

}
