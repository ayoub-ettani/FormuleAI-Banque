import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import { createClientRequest } from '../../models/create-client.model';

@Component({
  selector: 'app-client-form',
  imports: [ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrl: './client-form.css',
})

export class ClientForm {

  private formBuilder = inject(FormBuilder);
  private clientService = inject(ClientService);

   situations = [
    'CDI',
    'CDD',
    'FONCTIONNAIRE',
    'INDEPENDANT',
    'SANS_EMPLOI'
];

  form= this.formBuilder.group({
    nom:['', Validators.required],
    email:['', [Validators.required, Validators.email]],
    revenuMensuel: [0,[Validators.required, Validators.min(0)]],
    chargeMensuelles : [0,[Validators.required, Validators.min(0)]],
    situationPro: ['', Validators.required],
  });

  onSubmit()
  {
    if (this.form.invalid)
    {
      this.form.markAllAsTouched();
      return;
    }
    const client : createClientRequest = this.form.getRawValue() as createClientRequest;

    this.clientService.createClient(client).subscribe({
      next:()=>{
        alert('Client créé avec succes');
        this.form.reset();
      },
      error: err =>{
        console.error(err);
      }      
    })
  }
}
