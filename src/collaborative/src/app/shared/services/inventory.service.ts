import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SearchItem, LibraryItem } from 'interfaces/search';
import { SearchApi } from 'interfaces/searchapi';

@Injectable()
export class InventoryService implements SearchApi {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  default(): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + 'items/default')
    .map(res => res.json());
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `items/search?term=${term}`)
    .map(res => res.json());
  }

  getOne(id: string): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + `items/${id}`)
    .map(res => res.json());
  }

}
