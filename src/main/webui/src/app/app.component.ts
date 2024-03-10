import { Component } from '@angular/core';
import { GreetingService } from '../services/greeting.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'solidfile-app';
  greetingMessage$ = this.greetingService.fetchGreetingMessage();

  constructor(protected greetingService: GreetingService) {}
}
