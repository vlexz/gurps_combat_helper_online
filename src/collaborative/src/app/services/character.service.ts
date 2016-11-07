import { Injectable } from '@angular/core';

import { Http, Response } from '@angular/http';
import { Character } from '../interfaces/character';
import { Skill } from '../interfaces/skill';
import { Trait } from '../interfaces/trait';
import { CharacterDescriptor } from '../interfaces/char_descriptor';

import { Observable }     from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

import { environment } from '../../environments/environment';

@Injectable()
export class CharacterService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(private http: Http) {
  }

  updateStat(id: string, name: string, val: number): Observable<Character> {
    let data = {stats: {}};
    data.stats[name] = {delta: val};
    console.log(data);
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateCp(id: string, cp: number) {
    return this.http.patch(this.apiEndPoint + `char/${id}`, {cp: {cp}})
    .map(res => Character.fromJson(res.json() || {}));
  }

  uploadPortrait(id: string, element: any): Observable<any> {
    console.log(element.files);
    let data = new FormData();
    for(let i = 0; i < element.files.length; ++i) {
      data.append('pic', element.files[i]);
    }
    return this.http.put(this.apiEndPoint + `char/${id}/pic`, data);
  }

  updateMainInfo(id: string, data): Observable<Character> {
    console.log('Update main info of the character');
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateTraits(id: string, traits: Trait[]): Observable<Character> {
    console.log('Update traits for character', id);
    let data = {traits};
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateSkills(id: string, skills: Skill[]): Observable<Character> {
    let data = {skills};
    return this.http.patch(this.apiEndPoint + `char/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  charList(): Observable<CharacterDescriptor[]> {
    console.log('get character list');
    return this.http.get(this.apiEndPoint + 'chars')
    .map(res => res.json());
  }

  load(id: string): Observable<Character> {
    console.log('Loading character', id);
    return this.http.get(this.apiEndPoint + 'char/' + id)
    .map(res => Character.fromJson(res.json() || {}));
  }

  add(char: Character): Observable<Character> {
    console.log('adding character');
    return this.http.post(this.apiEndPoint + 'char', char)
    .map(res => Character.fromJson(res.json() || {}));
  }

  del(id: string): Observable<CharacterDescriptor[]> {
    return this.http.delete(this.apiEndPoint + `char/${id}`)
    .map(res => res.json());
  }

  save(char: Character): Observable<Character> {
    console.log('Saving character');
    return this.http.put(this.apiEndPoint + 'char/' + char._id, char)
    .map(res => Character.fromJson(res.json() || {}));
  }

  defaultCharacter(): Observable<Character> {
    console.log('Get default character from scala api');
    return this.http.get(this.apiEndPoint + 'char')
    .map((res: Response) => {
      return Character.fromJson(res.json() || {});
    });
  }

  defaultTrait(): Observable<Trait> {
    console.log('get default trait from scala api');
    return this.http.get(this.apiEndPoint + 'chars/trait')
    .map(res => Trait.fromJson(res.json()));
  }

  defaultSkill(): Observable<Skill> {
    console.log('get default skill from scala api');
    return this.http.get(this.apiEndPoint + 'chars/skill')
    .map(res => Skill.fromJson(res.json()));
  }
}
