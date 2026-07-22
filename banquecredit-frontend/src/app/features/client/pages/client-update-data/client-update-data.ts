import { afterNextRender, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { updateClientRequest } from '../../models/update-client.model';
import { setThrowInvalidWriteToSignalError } from '@angular/core/primitives/signals';

@Component({
  selector: 'app-client-update-data',
  imports: [],
  templateUrl: './client-update-data.html',
  styleUrl: './client-update-data.css',
})
export class ClientUpdateData implements OnInit {

  private formBuilder = inject(FormBuilder);
  private clientService = inject(ClientService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  
  id!: number;

  situations = [
    'CDI',
    'CDD',
    'FONCTIONNAIRE',
    'INDEPENDANT',
    'SANS_EMPLOI'
    ];

    form = this.formBuilder.group({
      nom:['', Validators.required],
    email:['', [Validators.required, Validators.email]],
    revenuMensuel: [0,[Validators.required, Validators.min(0)]],
    chargeMensuelles : [0,[Validators.required, Validators.min(0)]],
    situationPro: ['', Validators.required],
    });

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.clientService.getById(this.id)
      .subscribe(client => this.form.patchValue(client)); }
 
      onSubmit()
  {  if(this.form.invalid)
  {
    this.form.markAllAsTouched();
    return;
  }
    
  const client  = this.form.getRawValue() as updateClientRequest;
  this.clientService.updateClient(this.id, client).subscribe({
    next: () =>{   alert('client modifié !');
      this.router.navigate(['/clients']);
    },
    error: err => console.error(err)
  });
}

}
