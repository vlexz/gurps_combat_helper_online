import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Trait, TraitDescriptor, LibraryTrait } from 'interfaces/trait';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';

@Injectable()
export class TraitsService {

  apiEndPoint: string = environment.apiEndpoint;

  constructor(
    private http: Http
  ) { }

  get default(): Observable<Trait> {
    return this.http.get(this.apiEndPoint + 'trait')
    .map( resp  => Trait.fromJson(resp.json()));
  }

  getTrait(id: string): Observable<LibraryTrait> {
    return this.http.get(this.apiEndPoint + `trait/${id}`)
    .map( resp  => LibraryTrait.fromJson(resp.json()));
  }

  search(category: string, term: string): Observable<TraitDescriptor[]> {
    return this.http.get(this.apiEndPoint + `traits/search/${category}?term=${term}`)
    .map(resp => resp.json());
  }

}
