import { Component, OnInit, ViewChild } from '@angular/core';
import { CharEditorComponent } from '../char-editor/char-editor.component';
import { CharListComponent } from '../char-list/char-list.component';

@Component({
  selector: 'app-character-manager',
  templateUrl: './character-manager.component.html',
  styleUrls: ['./character-manager.component.css'],
})
export class CharacterManagerComponent implements OnInit {

  @ViewChild(CharEditorComponent)
  private editor: CharEditorComponent;

  @ViewChild(CharListComponent)
  private list: CharListComponent;

  constructor(
  ) { }

  selectCharacter(ev) {
    console.log('Charlist selected character', ev.character);
    this.editor.loadCharacter(ev.character);
  }

  createCharacter(ev) {
    console.log('Create empty character');
    this.editor.loadDefaultChracter();
  }

  characterAdded() {
    this.list.refresh();
  }

  ngOnInit() {
  }

}
