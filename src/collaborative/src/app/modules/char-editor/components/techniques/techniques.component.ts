import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from 'shared/services/character.service';
import { TechniqueService } from 'shared/services/technique.service';
import { Technique } from 'interfaces/technique';
import { Skill } from 'interfaces/skill';
import { ConstantTables } from 'interfaces/tables';
import { SearchItem } from 'interfaces/search_item';

@Component({
  selector: 'app-techniques',
  templateUrl: './techniques.component.html',
  styleUrls: ['./techniques.component.css']
})
export class TechniquesComponent implements OnInit {

  @Input() techniques: Technique[];
  @Input() skills: Skill[];
  @Output() change: EventEmitter<Object> = new EventEmitter();

  private defaultTech: Technique;
  tables: ConstantTables = new ConstantTables;

  search_results: SearchItem[];

  constructor(
    private chars: CharacterService,
    private techsrv: TechniqueService
  ) { }

  get skillNames(): string[] {
    return this.skills.map(skill => skill.name);
  }

  add() {
    this.techniques.push(this.defaultTech.clone());
    this.change.emit({});
  }

  remove(i: number) {
    this.techniques.splice(i, 1);
    this.change.emit({});
  }

  search(term) {
    this.techsrv.search(term)
    .subscribe(res => this.search_results = res);
  }

  addFromSearch(i: number) {
    this.techsrv.get(this.search_results[i].id)
    .subscribe(tech => {
      this.techniques.push(tech.technique);
      this.cancelSearch();
      this.change.emit({});
    });
  }

  cancelSearch() {
    this.search_results = null;
  }

  techChanged() {
    this.change.emit({});
  }

  ngOnInit() {
    this.techsrv.default
    .subscribe(tech => this.defaultTech = tech);
  }

}
