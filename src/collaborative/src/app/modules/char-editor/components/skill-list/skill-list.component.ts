import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from 'shared/services/character.service';
import { Skill, SkillDescriptor } from 'interfaces/skill';
import { SkillsService } from 'shared/services/skills.service';
import { ConstantTables } from 'interfaces/tables';

@Component({
  selector: 'app-skill-list',
  templateUrl: './skill-list.component.html',
  styleUrls: ['./skill-list.component.css']
})
export class SkillListComponent implements OnInit {

  @Output() change: EventEmitter<Object> = new EventEmitter();
  @Input() skills: Skill[] = null;

  tables: ConstantTables = new ConstantTables;

  defaultSkill: Skill = null;

  search_results: SkillDescriptor[];

  constructor(
    private chars: CharacterService,
    private skillssrv: SkillsService
  ) { }

  skillChanged() {
    this.change.emit({});
  }

  addSkill() {
    this.skills.push(this.defaultSkill.clone());
    this.change.emit({});
  }

  addFromSearch(idx: number) {
    this.skillssrv.skill(this.search_results[idx].id)
    .subscribe(skill => {
      console.log(skill.skill);
      this.skills.push(skill.skill);
      this.cancelSearch();
      this.change.emit({});
    });
  }

  searchSkill(term: string) {
    this.skillssrv.search(term)
    .subscribe(results => {
      this.search_results = results;
    });
  }

  cancelSearch() {
    this.search_results = null;
  }

  removeSkill(i: number) {
    this.skills.splice(i, 1);
    this.change.emit({});
  }

  ngOnInit() {
    this.skillssrv.default.subscribe(skill => this.defaultSkill = skill);
  }

}
