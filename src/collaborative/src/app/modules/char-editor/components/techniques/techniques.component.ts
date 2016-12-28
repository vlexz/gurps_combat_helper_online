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

  @Input() techniques: Technique[];
  @Input() skills: Skill[];
  @Output() change: EventEmitter<Object> = new EventEmitter();

  tables: ConstantTables = new ConstantTables;

  constructor(
    private chars: CharacterService,
    private techsrv: TechniqueService
  ) { }

  get skillNames(): string[] {
    return this.skills.map(skill => skill.name);
  }

  add(data: LibraryItem) {
    this.techniques.push(Technique.fromJson(data.data));
    this.change.emit({});
  }

  remove(i: number) {
    this.techniques.splice(i, 1);
    this.change.emit({});
  }

  techChanged() {
    this.change.emit({});
  }

  ngOnInit() {
  }

}
