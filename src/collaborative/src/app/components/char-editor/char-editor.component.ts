import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../services/character.service';
import { Character, Skill } from '../../interfaces/character';
import { Trait } from '../../interfaces/trait';

@Component({
  selector: 'app-char-editor',
  templateUrl: './char-editor.component.html',
  styleUrls: ['./char-editor.component.css'],
  providers: [CharacterService]
})
export class CharEditorComponent implements OnInit {

  private current: Character = null;

  private defTrait: Trait = null;
  private defSkill: Skill = null;

  @Output() characterAdded = new EventEmitter();

  constructor(
    private chars: CharacterService
  ) {}


  ensureCharacterExists(): Promise<boolean> {
    return new Promise((function(resolve, reject){
      if (this.current._id) { // character loaded or saved already
        return resolve(false);
      } else {
        return this.chars.add(this.current)
        .subscribe(char => {
          this.current = char;
          this.characterAdded.emit();
          resolve(true);
        });
      }
    }).bind(this));
  }

  statChanged(ev: any): void {
    console.log(ev);
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateStat(this.current._id, ev.name, ev.delta)
        .subscribe(char => this.current = char);
      } else {
        console.log('Character already saved');
      }
    });
  }

  mainInfoChanged(ev: any) {
    console.log(ev);
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        let data = {};
        data[ev.srcElement.name] = ev.srcElement.value;
        console.log(data);
        this.chars.updateMainInfo(this.current._id, data)
        .subscribe(() => {
          this.characterAdded.emit();
        });
      } else {
        console.log('Character already saved');
      }
    });
  }

  addAdvantage() {
    console.log('add advantage');
    this.current.traits.push(this.defTrait.cloneWith({category: 'Advantage'}));
  }

  removeAdvantage(i: number) {
    console.log('Remove adv', i);
    this.current.removeTrait('Advantage', i);
  }

  addDisadvantage() {
    console.log('add disadvantage');
    this.current.traits.push(this.defTrait.cloneWith({category: 'Disadvantage'}));
  }

  removeDisadvantage(i: number) {
    console.log('Remove disadv', i);
    this.current.removeTrait('Disadvantage', i);
  }

  traitChanged() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateTraits(this.current._id, this.current.traits)
        .subscribe(char => this.current = char);
      }
    });
  }

  addSkill() {
    this.current.skills.push(new Skill);
  }

  removeSkill(i: number) {
    this.current.skills.splice(i, 1);
  }

  setCharacter(char: Character) {
    this.current = char;
  }

  loadCharacter(id: string) {
    this.chars.load(id)
    .subscribe(char => this.current = char);
  }

  loadDefaultChracter() {
    this.chars.defaultCharacter()
    .subscribe(this.setCharacter.bind(this));
    console.log(this.current);
  }

  ngOnInit() {
    console.log('on init routine in char editor');
    this.loadDefaultChracter();
    this.chars.defaultTrait().subscribe(trait => this.defTrait = trait);
    this.chars.defaultSkill().subscribe(skill => this.defSkill = skill);
  }

}
