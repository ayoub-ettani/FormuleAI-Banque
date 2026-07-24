import { Component, inject, OnInit } from '@angular/core';
import { Demande } from '../../models/demande.model';
import { DemandeService } from '../../services/demande.service';
import { Router } from '@angular/router';
import { subscribeOn } from 'rxjs';

@Component({
  selector: 'app-demande-form',
  imports: [],
  templateUrl: './demande-form.html',
  styleUrl: './demande-form.css',
})
export class DemandeForm implements OnInit{
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
