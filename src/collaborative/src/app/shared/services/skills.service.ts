import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Skill, SkillDescriptor, LibrarySkill } from 'interfaces/skill';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class SkillsService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  get default(): Observable<Skill> {
    return this.http.get(this.apiEndPoint + 'skill')
    .map(res => Skill.fromJson(res.json()));
  }

  skill(id: string): Observable<LibrarySkill> {
    return this.http.get(this.apiEndPoint + `skill/${id}`)
    .map(res => LibrarySkill.fromJson(res.json()));
  }

  search(term: string): Observable<SkillDescriptor[]> {
    return this.http.get(this.apiEndPoint + `skills/search?term=${term}`)
    .map(res => res.json());
  }

}
