/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TechniqueService } from './technique.service';

describe('Service: Technique', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TechniqueService]
    });
  });

  it('should ...', inject([TechniqueService], (service: TechniqueService) => {
    expect(service).toBeTruthy();
  }));
});
