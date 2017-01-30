import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from 'shared/services/character.service';
import { TechniqueService } from 'shared/services/technique.service';
import { Technique } from 'interfaces/technique';
import { Skill } from 'interfaces/skill';
import { ConstantTables } from 'interfaces/tables';
import { LibraryItem } from 'interfaces/search';

@Component({
  selector: 'app-techniques',
  templateUrl: './techniques.component.html',
  styleUrls: ['./techniques.component.css']
})
export class TechniquesComponent implements OnInit {

  _techniques: Technique[];
  @Input() skills: Skill[];
  @Output() change: EventEmitter<Object> = new EventEmitter();

  tables: ConstantTables = new ConstantTables;

  editedTeq: Technique = null;
  editedTeqIdx: number= -1;

  constructor(
    private chars: CharacterService,
    private techsrv: TechniqueService
  ) { }

  @Input() set techniques(tecns: Technique[]) {
    this._techniques = tecns;
    if (this.editedTeqIdx !== -1) {
      this.editedTeq = this._techniques[this.editedTeqIdx];
    }
  }

  get skillNames(): string[] {
    return this.skills.map(skill => skill.name);
  }

  add(data: LibraryItem) {
    this._techniques.push(Technique.fromJson(data.data));
    if (data.ready) {
      this.editedTeqIdx = this._techniques.length - 1;
      this.editedTeq = this._techniques[this.editedTeqIdx];
    }
    this.change.emit({});
  }

  edit(i: number) {
    this.editedTeqIdx = i;
    this.editedTeq = this._techniques[i];
  }

  editDone() {
    this.editedTeq = null;
    this.editedTeqIdx = -1;
    this.change.emit();
  }

  remove(i: number) {
    this._techniques.splice(i, 1);
    this.change.emit({});
  }

  techChanged() {
    this.change.emit({});
  }

  ngOnInit() {
  }

}
