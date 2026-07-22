import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ClientList } from './features/client/pages/client-list/client-list';
import { ClientForm } from './features/client/pages/client-form/client-form';

@Component({
  selector: 'app-root',
  imports: [ClientForm],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('banque-credit-front');
}
