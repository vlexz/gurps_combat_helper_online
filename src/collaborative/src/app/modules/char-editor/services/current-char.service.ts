import { Injectable } from '@angular/core';

import { CharacterService } from 'shared/services/character.service';
import { Character } from 'interfaces/character';
import { Router } from '@angular/router';

@Injectable()
export class CurrentCharService {

  public current: Character;
  private timestamp: number = new Date().getTime();
  private setchar: any;

  constructor(
    private chars: CharacterService,
    private router: Router
  ) {
    this.setchar = this.setCharacter.bind(this);
   }

  get char() {
    return this.current;
  }

  get portrait() {
    if (this.current._id) {
      return this.chars.apiEndPoint + 'char/' + this.current._id + '/pic?' + this.timestamp;
    } else {
      return '/assets/default_portrait.png';
    }
  }


  ensureCharacterExists(): Promise<boolean> {
    return new Promise((function(resolve, reject){
      if (this.current._id) { // character loaded or saved already
        return resolve(false);
      } else {
        return this.chars.add(this.current)
        .subscribe(char => {
          this.current = char;
          this.router.navigate(['/char-editor', {id: char._id}]);
          resolve(true);
        });
      }
    }).bind(this));
  }

  updateStats(ev: any): void {
    console.log(ev);
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateStat(this.current._id, ev.name, ev.delta)
        .subscribe(this.setchar);
      } else {
        console.log('Character already saved');
      }
    });
  }

  updateCp(ev: any) {
    console.log(ev);
    this.ensureCharacterExists()
    .then(saved => {
      this.chars.updateCp(this.current._id, parseInt(ev.srcElement.value, 10))
      .subscribe(this.setchar);
    });
  }

  uploadPortrait(files: FileList) {
    console.log('Upload portrait');
    this.ensureCharacterExists()
    .then(saved => {
      this.chars.uploadPortrait(this.current._id, files)
      .subscribe(() => this.timestamp = new Date().getTime());
    });
  }

  updateMainInfo(key: string, value: string): Promise<any> {
    return new Promise((resolve, reject) => {
      this.ensureCharacterExists()
      .then(saved => {
        if (!saved) {
          let data = {};
          data[key] = value;
          console.log(data);
          this.chars.updateMainInfo(this.current._id, data)
          .subscribe(resolve, reject);
        } else {
          resolve();
          console.log('Character already saved');
        }
      });
    });
  }


  updateTraits() {
    console.log('Update traits');
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateTraits(this.current._id, this.current.traits)
        .subscribe(this.setchar);
      }
    });
  }

  updateSkills() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateSkills(this.current._id, this.current.skills)
        .subscribe(this.setchar);
      }
    });
  }

  updateTechniques() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateTechniques(this.current._id, this.current.techniques)
        .subscribe(this.setchar);
      }
    });
  }

  updateArmor() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateArmor(this.current._id, this.current.equip.armor)
        .subscribe(this.setchar);
      }
    });
  }

  setCharacter(char: Character) {
    console.log(char);
    this.current = char;
  }

  loadCharacter(id: string) {
    this.timestamp = new Date().getTime();
    if (id === 'new') {
      this.loadDefaultChracter();
    } else {
      this.chars.load(id)
      .subscribe(this.setchar);
    }
  }

  loadDefaultChracter() {
    this.chars.defaultCharacter()
    .subscribe(this.setchar);
  }

}
