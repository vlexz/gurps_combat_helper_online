import { Injectable } from '@angular/core';

import { Http, Response } from '@angular/http';
import { Character } from 'interfaces/character';
import { Skill } from 'interfaces/skill';
import { Trait } from 'interfaces/trait';
import { Technique } from 'interfaces/technique';
import { Armor } from 'interfaces/armor';
import { InventoryItem } from 'interfaces/inventory';
import { CharacterDescriptor } from '../../interfaces/char_descriptor';

import { Observable }     from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

import { environment } from '../../../environments/environment';

@Injectable()
export class CharacterService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(private http: Http) {
  }

  updateStat(id: string, name: string, val: number): Observable<Character> {
    let data = {stats: {}};
    data.stats[name] = {delta: val};
    console.log(data);
    return this.http.patch(this.apiEndPoint + `chars/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateCp(id: string, cp: number) {
    return this.http.patch(this.apiEndPoint + `chars/${id}`, {cp: {cp}})
    .map(res => Character.fromJson(res.json() || {}));
  }

  uploadPortrait(id: string, files: FileList): Observable<any> {
    console.log(files);
    let data = new FormData();
    for (let i = 0; i < files.length; ++i) {
      data.append('pic', files[i]);
    }
    return this.http.put(this.apiEndPoint + `chars/${id}/pic`, data);
  }

  updateMainInfo(id: string, data): Observable<Character> {
    console.log('Update main info of the character');
    return this.http.patch(this.apiEndPoint + `chars/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateTraits(id: string, traits: Trait[]): Observable<Character> {
    console.log('Update traits for character', id);
    let data = {traits};
    return this.http.patch(this.apiEndPoint + `chars/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateSkills(id: string, skills: Skill[]): Observable<Character> {
    let data = {skills};
    return this.http.patch(this.apiEndPoint + `chars/${id}`, data)
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateTechniques(id: string, techniques: Technique[]): Observable<Character> {
    return this.http.patch(this.apiEndPoint + `chars/${id}`, {techniques})
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateArmor(id: string, armor: Armor[]): Observable<Character> {
    return this.http.patch(this.apiEndPoint + `chars/${id}`, {equip: {armor}})
    .map(res => Character.fromJson(res.json() || {}));
  }

  updateInventory(id: string, items: InventoryItem[]): Observable<Character> {
    return this.http.patch(this.apiEndPoint + `chars/${id}`, {equip: {items}})
    .map(res => Character.fromJson(res.json() || {}));
  }

  charList(): Observable<CharacterDescriptor[]> {
    console.log('get character list');
    return this.http.get(this.apiEndPoint + 'chars')
    .map(res => res.json());
  }

  load(id: string): Observable<Character> {
    console.log('Loading character', id);
    return this.http.get(this.apiEndPoint + 'chars/' + id)
    .map(res => Character.fromJson(res.json() || {}));
  }

  add(char: Character): Observable<Character> {
    console.log('adding character');
    return this.http.post(this.apiEndPoint + 'chars', char)
    .map(res => Character.fromJson(res.json() || {}));
  }

  del(id: string): Observable<CharacterDescriptor[]> {
    return this.http.delete(this.apiEndPoint + `chars/${id}`)
    .map(res => res.json());
  }

  save(char: Character): Observable<Character> {
    console.log('Saving character');
    return this.http.put(this.apiEndPoint + 'chars/' + char._id, char)
    .map(res => Character.fromJson(res.json() || {}));
  }

  defaultCharacter(): Observable<Character> {
    console.log('Get default character from scala api');
    return this.http.get(this.apiEndPoint + 'chars/default')
    .map((res: Response) => {
      return Character.fromJson(res.json() || {});
    });
  }


  defaultSkill(): Observable<Skill> {
    console.log('get default skill from scala api');
    return this.http.get(this.apiEndPoint + 'skill')
    .map(res => Skill.fromJson(res.json()));
  }

  defaultTechnique(): Observable<Technique> {
    console.log('get default technique from scala api');
    return this.http.get(this.apiEndPoint + 'teq')
    .map(res => Technique.fromJson(res.json()));
  }

}
