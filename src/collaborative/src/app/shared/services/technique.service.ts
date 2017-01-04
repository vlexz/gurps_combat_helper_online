import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Technique } from 'interfaces/technique';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { SearchApi } from 'interfaces/searchapi';
import { SearchItem, LibraryItem } from 'interfaces/search';

@Injectable()
export class TechniqueService implements SearchApi {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  default(): Observable<Technique> {
    return this.http.get(this.apiEndPoint + 'tecns/default')
    .map(res => Technique.fromJson(res.json()));
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `tecns/search?term=${term}`)
    .map(res => res.json());
  }

  getOne(id: string): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + `tecns/${id}`)
    .map(res => res.json());
  }

  getAll(): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + 'tecns')
    .map(res => res.json());
  }

}
