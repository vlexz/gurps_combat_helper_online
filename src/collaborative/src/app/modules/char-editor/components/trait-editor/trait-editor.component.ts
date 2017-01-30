import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Trait } from 'interfaces/trait';

@Component({
  selector: 'trait-editor',
  templateUrl: './trait-editor.component.html',
  styleUrls: ['./trait-editor.component.css']
})
export class TraitEditorComponent implements OnInit {

  @Output() editDone: EventEmitter<Object> = new EventEmitter<Object>();
  @Output() editCancel: EventEmitter<Object> = new EventEmitter<Object>();

  @Input() trait: Trait;

  private costExpand: boolean = false;

  constructor() { }

  switchCostExp() {
    this.costExpand = !this.costExpand;
  }

  cancel() {
    this.editCancel.emit({});
  }

  done() {
    this.editDone.emit({});
  }

  ngOnInit() {
  }

}
