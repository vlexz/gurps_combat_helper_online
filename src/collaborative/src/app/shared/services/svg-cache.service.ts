import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';

@Injectable()
export class SvgCacheService {

  private storage: any = {};

  constructor(
    private http: Http
  ) { }

  getsvg(url: string): Observable<string> {
    if (url in this.storage) {
      return Observable.of(this.storage[url]);
    } else {
      return this.http.get(url)
      .map(res => {
        this.storage[url] = res.text();
        return res.text();
      });
    }
  }

}
