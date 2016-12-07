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

  mainInfoChanged(ev: any) {
    this.current.updateMainInfo(ev.srcElement.name, ev.srcElement.value)
    .then(() => {
      this.toolbar.updateCharList();
    });
  }

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
