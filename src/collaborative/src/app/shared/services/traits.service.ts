import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Trait } from 'interfaces/trait';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { SearchItem, LibraryItem } from 'interfaces/search';

@Injectable()
export class TraitsService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  get default(): Observable<Trait> {
    return this.http.get(this.apiEndPoint + 'traits/default')
    .map( resp  => Trait.fromJson(resp.json()));
  }

  getTrait(id: string): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + `traits/${id}`)
    .map( resp  => resp.json());
  }

  search(categories: string[], term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `traits/search?term=${term}&category=${categories.join(',')}`)
    .map(resp => resp.json());
  }

  getTraits(categories: string[]): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `traits?category=${categories.join(',')}`)
    .map(res => res.json());
  }

}
