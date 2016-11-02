import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../services/character.service';
import { Character } from '../../interfaces/character';

@Component({
  selector: 'app-char-editor',
  templateUrl: './char-editor.component.html',
  styleUrls: ['./char-editor.component.css'],
  providers: [CharacterService]
})
export class CharEditorComponent implements OnInit {

  private current: Character = null;

  @Output() characterAdded = new EventEmitter();

  constructor(
    private chars: CharacterService
  ) {}


  ensureCharacterExists(): Promise<void> {
    return new Promise((function(resolve, reject){
      if (this.current._id) { // character loaded or saved already
        return resolve();
      } else {
        return this.chars.add(this.current)
        .subscribe(char => {
          this.current = char;
          this.characterAdded.emit();
          resolve();
        });
      }
    }).bind(this));
  }

  statChanged(ev: any): void {
    console.log(ev);
    this.ensureCharacterExists()
    .then(() => {
      this.chars.updateStat(this.current._id, ev.name, ev.delta)
      .subscribe(char => this.current = char);
    });
  }

  mainInfoChanged(ev: any) {
    console.log(ev);
    this.ensureCharacterExists()
    .then(() => {
      let data = {};
      data[ev.srcElement.name] = ev.srcElement.value;
      console.log(data);
      this.chars.updateMainInfo(this.current._id, data)
      .subscribe(() => {
        this.characterAdded.emit();
      });
    });
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
  }

}
