import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../services/character.service';
import { Skill } from '../../../../interfaces/skill';
import { ConstantTables } from '../../../../interfaces/tables';

@Component({
  selector: 'app-skill-list',
  templateUrl: './skill-list.component.html',
  styleUrls: ['./skill-list.component.css'],
  providers: [CharacterService]
})
export class SkillListComponent implements OnInit {

  @Output() change: EventEmitter<Object> = new EventEmitter();
  @Input() skills: Skill[] = null;

  tables: ConstantTables = new ConstantTables;

  defaultSkill: Skill = null;

  constructor(
    private chars: CharacterService
  ) { }

  skillChanged() {
    this.change.emit({});
  }

  addSkill() {
    this.skills.push(this.defaultSkill.clone());
  }

  ngOnInit() {
    this.chars.defaultSkill().subscribe(skill => this.defaultSkill = skill);
  }

}
