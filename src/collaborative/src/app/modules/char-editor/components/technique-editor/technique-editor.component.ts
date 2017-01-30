import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Technique } from 'interfaces/technique';
import { Skill } from 'interfaces/skill';

@Component({
  selector: 'technique-editor',
  templateUrl: './technique-editor.component.html',
  styleUrls: ['./technique-editor.component.css']
})
export class TechniqueEditorComponent implements OnInit {

  @Input() technique: Technique;
  @Input() skills: Skill[];
  @Output() done: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  _done() {
    this.done.emit();
  }

  ngOnInit() {
  }

}
