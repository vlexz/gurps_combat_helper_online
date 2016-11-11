import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { CharacterService } from '../../shared/services/character.service';
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import { CurrentCharService } from './services/current-char.service';




@Component({
  selector: 'char-editor-root',
  templateUrl: './char-editor.component.html',
  styleUrls: ['./char-editor.component.css'],
  providers: [CharacterService]
})
export class CharEditorComponent implements OnInit {

  // private current: Character = null;  

  @ViewChild(ToolbarComponent)
  toolbar: ToolbarComponent;

  @Output() characterAdded = new EventEmitter();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private chars: CharacterService,
    private current: CurrentCharService
  ) {
  }


  uploadPortrait() {
    document.getElementById('portrait_file').click();
  }

  // processUpload() {
  //   console.log('Upload portrait');
  //   this.ensureCharacterExists()
  //   .then(saved => {
  //     this.chars.uploadPortrait(this.current._id, document.getElementById('portrait_file'))
  //     .subscribe(() => this.timestamp = new Date().getTime());
  //   });
  // }

  mainInfoChanged(ev: any) {
    this.current.updateMainInfo(ev.srcElement.name, ev.srcElement.value)
    .then(() => {
      this.toolbar.updateCharList();
    });
  }

  // traitChanged() {
  //   this.ensureCharacterExists()
  //   .then(saved => {
  //     if (!saved) {
  //       this.chars.updateTraits(this.current._id, this.current.traits)
  //       .subscribe(this.setchar);
  //     }
  //   });
  // }

  // skillChanged() {
  //   this.ensureCharacterExists()
  //   .then(saved => {
  //     if (!saved) {
  //       this.chars.updateSkills(this.current._id, this.current.skills)
  //       .subscribe(this.setchar);
  //     }
  //   });
  // }

  // techniqueChanged() {
  //   this.ensureCharacterExists()
  //   .then(saved => {
  //     if (!saved) {
  //       this.chars.updateTechniques(this.current._id, this.current.techniques)
  //       .subscribe(this.setchar);
  //     }
  //   });
  // }

  // setCharacter(char: Character) {
  //   this.current = char;
  //   this.timestamp = new Date().getTime();
  // }

  // loadCharacter(id: string) {
  //   if (id === 'new') {
  //     this.loadDefaultChracter();
  //   } else {
  //     this.chars.load(id)
  //     .subscribe(this.setchar);
  //   }
  // }

  // loadDefaultChracter() {
  //   this.chars.defaultCharacter()
  //   .subscribe(this.setchar);
  // }

  ngOnInit() {
    this.route.params.forEach((params: Params) => {
      if (params['id']) {
        this.current.loadCharacter(params['id']);
      }
    });
    // extract character id from router if any
    // if (this.route)
    // console.log('on init routine in char editor');
    // this.loadDefaultChracter();
  }

}
