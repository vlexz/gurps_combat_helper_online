/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { ArmorService } from './armor.service';

describe('Service: Armor', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ArmorService]
    });
  });

  it('should ...', inject([ArmorService], (service: ArmorService) => {
    expect(service).toBeTruthy();
  }));
});
