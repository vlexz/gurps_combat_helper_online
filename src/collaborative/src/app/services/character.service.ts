import { Injectable } from '@angular/core';

import { Http, Response } from '@angular/http';
import { Character } from '../interfaces/character';

// import { Observable }     from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class CharacterService {

  private apiEndPoint: string = 'http://localhost:9000/api/';

  constructor(private http: Http) {
  }

  updateStat(id: string, name: string, val: number): void {
    // TODO: call backend for update
  }

  localCharacter(): Character {
    return {
      _id: '',
      // 'player' : 'vlex',
      // 'name' : 'Bjorn Masterson',
      // 'cp' : {
      //   'cp' : 265,
      //   'stats' : 190, // calculated field
      //   'adv' : 83, // calculated field
      //   'dis' : 0, // calculated field
      //   'skills' : 13, // calculated field
      //   'unspent' : -21 // calculated field
      // },
      // 'description' : {
      //   'age' : '35',
      //   'height' : '6f6i',
      //   'weight' : '250',
      //   'portrait' : 'somelink',
      //   'bio' : 'longbio'
      // },
      'stats' : {
        'st' : {
          'delta' : 0,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0 // calculated field
        },
        'dx' : {
          'delta' : 3,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 120 // calculated field
        },
        'iq' : {
          'delta' : 0,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0 // calculated field
        },
        'ht' : {
          'delta' : 2,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 40 // calculated field
        },
        'will' : {
          'delta' : 2,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 20 // calculated field
        },
        'per' : {
          'delta' : 2,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 20 // calculated field
        },
        'liftSt' : {
          'delta' : 0,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0 // calculated field
        },
        'strikeSt' : {
          'delta' : 0,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0 // calculated field
        },
        'hp' : {
          'delta' : 0,
          'base' : 10, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0, // calculated field
          'lost' : 0, // valid values >= 0
          'compromised' : false, // calculated field
          'collapsing' : false // calculated field
        },
        'fp' : {
          'delta' : 0,
          'base' : 12, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0, // calculated field
          'lost' : 0,
          'compromised' : false, // calculated field
          'collapsing' : false // calculated field
        },
        'basicSpeed' : {
          'delta' : -0.25,
          'base' : 6.25, // calculated field
          'bonus' : 0, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : -10 // calculated field
        },
        'basicMove' : {
          'delta' : 0,
          'base' : 6, // calculated field
          'bonus' : 1, // calculated field
          'cpMod' : 100, // calculated field
          'cp' : 0 // calculated field
        }
      }
    };
  }

  defaultCharacter(): Promise<Character> {
    console.log('Get default character from scala api');
    return this.http.get(this.apiEndPoint + 'char')
    .map((res: Response) => {
      return res.json() || {};
    })
    .toPromise();
  }
}
