import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { CharacterService } from '../../services/character.service';
import { Character } from '../../interfaces/character';
import { ToolbarComponent } from './components/toolbar/toolbar.component';


@Component({
  selector: 'char-editor-root',
  templateUrl: './char-editor.component.html',
  styleUrls: ['./char-editor.component.css'],
  providers: [CharacterService]
})
export class CharEditorComponent implements OnInit {

  private current: Character = null;
  private timestamp: number = new Date().getTime();
  private setchar: any;

  @ViewChild(ToolbarComponent)
  toolbar: ToolbarComponent;

  @Output() characterAdded = new EventEmitter();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private chars: CharacterService
  ) {
    this.setchar = this.setCharacter.bind(this);
  }


  get portraitUrl() {
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
          this.toolbar.updateCharList();
          this.router.navigate(['/char-editor', {id: char._id}]);
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
        .subscribe(this.setchar);
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
      .subscribe(this.setchar);
    });
  }

  uploadPortrait() {
    document.getElementById('portrait_file').click();
  }

  processUpload() {
    console.log('Upload portrait');
    this.ensureCharacterExists()
    .then(saved => {
      this.chars.uploadPortrait(this.current._id, document.getElementById('portrait_file'))
      .subscribe(() => this.timestamp = new Date().getTime());
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
          this.toolbar.updateCharList();
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
        .subscribe(this.setchar);
      }
    });
  }

  skillChanged() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateSkills(this.current._id, this.current.skills)
        .subscribe(this.setchar);
      }
    });
  }

  techniqueChanged() {
    this.ensureCharacterExists()
    .then(saved => {
      if (!saved) {
        this.chars.updateTechniques(this.current._id, this.current.techniques)
        .subscribe(this.setchar);
      }
    });
  }

  setCharacter(char: Character) {
    this.current = char;
    this.timestamp = new Date().getTime();
  }

  loadCharacter(id: string) {
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

  ngOnInit() {
    this.route.params.forEach((params: Params) => {
      if (params['id']) {
        this.loadCharacter(params['id']);
      }
    });
    // extract character id from router if any
    // if (this.route)
    // console.log('on init routine in char editor');
    // this.loadDefaultChracter();
  }

}
