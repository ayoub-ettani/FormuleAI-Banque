import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DashboardStats} from '../../core/models/dashboard-model';
import {DashboardService} from '../../core/services/dashboard';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  stats: DashboardStats | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le dashboard';
        this.isLoading = false;
      }
    });
  }
}
