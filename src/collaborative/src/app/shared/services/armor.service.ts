import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { environment } from '../../../environments/environment';
import { Armor } from 'interfaces/armor';
import { SearchItem } from 'interfaces/search_item';

@Injectable()
export class ArmorService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  defaultArmor(): Observable<Armor> {
    return this.http.get(this.apiEndPoint + 'armor')
    .map(res => Armor.fromJson(res.json()));
  }

  search(term: string): Observable<SearchItem[]> {
    return this.http.get(this.apiEndPoint + `armors/search/?term=${term}`)
    .map(res => res.json());
  }

  get(id: string): Observable<Armor> {
    return this.http.get(this.apiEndPoint + `armor/${id}`)
    .map(res => Armor.fromJson(res.json()));
  }

}
