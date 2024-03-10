/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { GreetingService } from './greeting.service';

describe('Service: Greeting', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GreetingService]
    });
  });

  it('should ...', inject([GreetingService], (service: GreetingService) => {
    expect(service).toBeTruthy();
  }));
});
