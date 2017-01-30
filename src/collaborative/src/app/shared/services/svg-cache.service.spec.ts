/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { SvgCacheService } from './svg-cache.service';

describe('Service: SvgCache', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SvgCacheService]
    });
  });

  it('should ...', inject([SvgCacheService], (service: SvgCacheService) => {
    expect(service).toBeTruthy();
  }));
});
