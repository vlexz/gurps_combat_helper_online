import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../shared/services/character.service';
import { CharacterDescriptor } from '../../../../interfaces/char_descriptor';

@Component({
  selector: 'app-char-list',
  templateUrl: './char-list.component.html',
  styleUrls: ['./char-list.component.css']
})
export class CharListComponent implements OnInit {

  private characters: Array<CharacterDescriptor>;
  @Output() characterSelected = new EventEmitter();
  @Input() visible: boolean;

  constructor(
    private charService: CharacterService
  ) {
    this.characters = [];
    console.log('Instantiate char list');
  }

  selectCharacter(i: number) {
    console.log('Selected character:', this.characters[i]._id);
    this.characterSelected.emit({
      character: this.characters[i]._id
    });
  }

  deleteCharacter(i: number) {
    console.log('Delete character:', this.characters[i]._id);
    this.charService.del(this.characters[i]._id)
    .subscribe(res => {
      console.log(res);
      this.refresh();
    });
  }

  refresh() {
    console.log('refreshing charlist...');
    this.charService.charList()
    .subscribe(list => {
      console.log('character list', list);
      this.characters = list;
    });
  }

  ngOnInit() {
    this.refresh();
  }

}
