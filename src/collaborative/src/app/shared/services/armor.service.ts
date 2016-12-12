import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { environment } from '../../../environments/environment';
import { Armor } from 'interfaces/armor';

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

}
