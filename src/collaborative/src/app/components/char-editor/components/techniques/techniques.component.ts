import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../services/character.service';
import { Technique } from '../../../../interfaces/technique';
import { Skill } from '../../../../interfaces/skill';
import { ConstantTables } from '../../../../interfaces/tables';

@Component({
  selector: 'app-techniques',
  templateUrl: './techniques.component.html',
  styleUrls: ['./techniques.component.css'],
  providers: [CharacterService]
})
export class TechniquesComponent implements OnInit {

  @Input() techniques: Technique[];
  @Input() skills: Skill[];
  @Output() change: EventEmitter<Object> = new EventEmitter();

  private defaultTech: Technique;
  private tables: ConstantTables = new ConstantTables;

  constructor(
    private chars: CharacterService
  ) { }

  get skillNames(): string[] {
    return this.skills.map(skill => skill.name);
  }

  add() {
    this.techniques.push(this.defaultTech.clone());
    this.change.emit({});
  }

  remove(i: number){
    this.techniques.splice(i, 1);
    this.change.emit({});
  }

  techChanged() {
    this.change.emit({});
  }

  ngOnInit() {
    this.chars.defaultTechnique()
    .subscribe(tech => this.defaultTech = tech);
  }

}
