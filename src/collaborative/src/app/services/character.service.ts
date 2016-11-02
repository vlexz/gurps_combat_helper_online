import { Injectable } from '@angular/core';

import { Http, Response } from '@angular/http';
import { Character } from '../interfaces/character';
import { CharacterDescriptor } from '../interfaces/char_descriptor';

import { Observable }     from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class CharacterService {

  private apiEndPoint: string = 'http://localhost:9000/api/';

  constructor(private http: Http) {
  }

  updateStat(id: string, name: string, val: number): Observable<Character> {
    let data = {stats: {}};
    data.stats[name] = {delta: val};
    console.log(data);
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => res.json());
  }

  updateMainInfo(id: string, data): Observable<Character> {
    console.log('Update main info of the character');
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => res.json() || {});
  }

  charList(): Observable<CharacterDescriptor[]> {
    console.log('get character list');
    return this.http.get(this.apiEndPoint + 'chars')
    .map(res => res.json());
  }

  load(id: string): Observable<Character> {
    console.log('Loading character', id);
    return this.http.get(this.apiEndPoint + 'char/' + id)
    .map(res => res.json() || {});
  }

  add(char: Character): Observable<Character> {
    console.log('adding character');
    return this.http.post(this.apiEndPoint + 'char', char)
    .map(res => res.json() || {});
  }

  del(id: string): Observable<CharacterDescriptor[]> {
    return this.http.delete(this.apiEndPoint + `char/${id}`)
    .map(res => res.json());
  }

  save(char: Character): Observable<Character> {
    console.log('Saving character');
    return this.http.put(this.apiEndPoint + 'char/' + char._id, char)
    .map(res => res.json() || {});
  }

  defaultCharacter(): Observable<Character> {
    console.log('Get default character from scala api');
    return this.http.get(this.apiEndPoint + 'char')
    .map((res: Response) => {
      return res.json() || {};
    });
  }
}
