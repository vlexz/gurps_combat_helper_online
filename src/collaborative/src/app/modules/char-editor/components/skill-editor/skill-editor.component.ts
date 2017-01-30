import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Skill } from 'interfaces/skill';
import { ConstantTables } from 'interfaces/tables';

@Component({
  selector: 'skill-editor',
  templateUrl: './skill-editor.component.html',
  styleUrls: ['./skill-editor.component.css']
})
export class SkillEditorComponent implements OnInit {

  @Input() skill: Skill;
  @Output() done: EventEmitter<void> = new EventEmitter<void>();

  tables: ConstantTables = new ConstantTables;

  private _tldep: boolean = null;

  constructor() { }

  _done() {
    this.done.emit();
  }

  get tlDependent(): boolean {
    return this._tldep === null ? this.skill.tl > 0 : this._tldep;
  }

  set tlDependent(val: boolean) {
    this._tldep = val;
  }

  ngOnInit() {
  }

}
