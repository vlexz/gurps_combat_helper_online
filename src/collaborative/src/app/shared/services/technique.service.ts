import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Technique, LibraryTechnique } from 'interfaces/technique';
import { SearchItem } from 'interfaces/search_item';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';

@Injectable()
export class TechniqueService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  get default(): Observable<Technique> {
    return this.http.get(this.apiEndPoint + 'tecn')
    .map(res => Technique.fromJson(res.json()));
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `tecns/search?term=${term}`)
    .map(res => res.json());
  }

  get(id: string): Observable<LibraryTechnique> {
    return this.http.get(this.apiEndPoint + `tecn/${id}`)
    .map(res => LibraryTechnique.fromJson(res.json()));
  }

}
