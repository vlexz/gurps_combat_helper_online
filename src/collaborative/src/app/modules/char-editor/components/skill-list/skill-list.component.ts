import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from 'shared/services/character.service';
import { Skill, SkillDescriptor } from 'interfaces/skill';
import { LibraryItem } from 'interfaces/search';
import { SkillsService } from 'shared/services/skills.service';
import { ConstantTables } from 'interfaces/tables';

@Component({
  selector: 'app-skill-list',
  templateUrl: './skill-list.component.html',
  styleUrls: ['./skill-list.component.css']
})
export class SkillListComponent implements OnInit {

  @Output() change: EventEmitter<Object> = new EventEmitter();
   _skills: Skill[] = null;

  tables: ConstantTables = new ConstantTables;

  search_results: SkillDescriptor[];

  editedSkill: Skill = null;
  editedSkillIdx: number = -1;

  constructor(
    private chars: CharacterService,
    private skillssrv: SkillsService
  ) { }

  @Input() set skills(skills: Skill[]) {
    this._skills = skills;
    if (this.editedSkillIdx !== -1) {
      this.editedSkill = this._skills[this.editedSkillIdx];
    }
  }

  skillChanged() {
    this.change.emit({});
  }

  addSkill(skill: LibraryItem) {
    this._skills.push(Skill.fromJson(skill.data));
    if (!skill.ready) {
      this.editedSkillIdx = this._skills.length - 1;
    }
    this.change.emit({});
  }

  skillType(skill: Skill) {
    let diff = this.tables.skillDifficulties.find(d => d.val === skill.diff).name;
    return `${skill.attr}/${diff}`;
  }

  removeSkill(i: number) {
    this._skills.splice(i, 1);
    this.change.emit({});
  }

  editSkill(i: number) {
    this.editedSkillIdx = i;
    this.editedSkill = this._skills[i];
  }

  editFinished() {
    this.editedSkillIdx = -1;
    this.editedSkill = null;
    this.change.emit({});
  }

  ngOnInit() {
  }

}
