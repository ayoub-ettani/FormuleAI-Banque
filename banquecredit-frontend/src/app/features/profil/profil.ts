import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {UserInfo} from '../../core/models/auth-model';
import {AuthService} from '../../core/services/auth-service';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profil.html',
  styleUrl: './profil.css'
})
export class ProfileComponent implements OnInit {

  user: UserInfo | null = null;
  isLoading = true;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.fetchMe().subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: () => {
        this.user = this.authService.getCurrentUser();
        this.isLoading = false;
      }
    });
  }
}
