/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CurrentCharService } from './current-char.service';

describe('Service: CurrentChar', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CurrentCharService]
    });
  });

  it('should ...', inject([CurrentCharService], (service: CurrentCharService) => {
    expect(service).toBeTruthy();
  }));
});
