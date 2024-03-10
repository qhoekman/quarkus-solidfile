import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class GreetingService {
  constructor(private http: HttpClient) {}
  fetchGreetingMessage() {
    return this.http.get('/api/greeting', { responseType: 'text' });
  }
}
