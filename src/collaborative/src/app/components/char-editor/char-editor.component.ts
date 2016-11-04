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

  cpChanged(ev: any) {
    console.log(ev);
    this.ensureCharacterExists()
    .then(saved => {
      this.chars.updateCp(this.current._id, parseInt(ev.srcElement.value, 10))
      .subscribe(char => this.current = char);
    });
  }

  uploadPortrait() {
    document.getElementById('portrait_file').click();
  }

  processUpload() {
    console.log('Upload portrait');
    this.ensureCharacterExists()
    .then(saved => {
      this.chars.uploadPortrait(this.current._id, document.getElementById('portrait_file'));
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

  traitChanged() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateTraits(this.current._id, this.current.traits)
        .subscribe(char => this.current = char);
      }
    });
  }

  skillChanged() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateSkills(this.current._id, this.current.skills)
        .subscribe(char => this.current = char);
      }
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
