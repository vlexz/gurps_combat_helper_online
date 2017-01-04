import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { LibrarySkill } from 'interfaces/skill';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { SearchItem, LibraryItem } from 'interfaces/search';
import { SearchApi } from 'interfaces/searchapi';

@Injectable()
export class SkillsService implements SearchApi {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  default(): Observable<Object> {
    return this.http.get(this.apiEndPoint + 'skills/default')
    .map(res => res.json());
  }

  skill(id: string): Observable<LibrarySkill> {
    return this.http.get(this.apiEndPoint + `skills/${id}`)
    .map(res => LibrarySkill.fromJson(res.json()));
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `skills/search?term=${term}`)
    .map(res => res.json());
  }

  getOne(id: string): Observable<LibraryItem> {
    return this.http.get(this.apiEndPoint + `skills/${id}`)
    .map(res => res.json());
  }

  getAll(): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + 'skills')
    .map(res => res.json());
  }

}
