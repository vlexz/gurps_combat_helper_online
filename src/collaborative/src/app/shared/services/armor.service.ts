import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { environment } from '../../../environments/environment';
import { SearchItem, LibraryItem } from 'interfaces/search';
import { SearchApi } from 'interfaces/searchapi';

@Injectable()
export class ArmorService implements SearchApi {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  default(): Observable<Object> {
    return this.http.get(this.apiEndPoint + 'armors/default')
    .map(res => res.json());
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `armors/search?term=${term}`)
    .map(res => res.json());
  }

  getOne(id: string): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + `armors/${id}`)
    .map(res => res.json());
  }

}
