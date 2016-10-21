import { Component, OnInit } from '@angular/core';
import { CharacterService } from '../../services/character.service';
import { Character } from '../../interfaces/character';
// import { StatBlockComponent } from './components/stat-block/stat-block.component';

@Component({
  selector: 'app-char-editor',
  templateUrl: './char-editor.component.html',
  styleUrls: ['./char-editor.component.css'],
  providers: [CharacterService]
})
export class CharEditorComponent implements OnInit {

  private current: Character;

  constructor(
    private chars: CharacterService
  ) {}

  statChange(ev: any): void {
    this.chars.updateStat(this.current._id, ev.name, ev.val);
  }

  ngOnInit() {
    console.log('on init routine in char editor');
    this.current = this.chars.localCharacter();
    console.log(this.current);
  }

}
