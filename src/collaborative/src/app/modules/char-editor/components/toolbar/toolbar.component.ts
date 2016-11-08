import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { CharListComponent } from '../char-list/char-list.component';


@Component({
  selector: 'char-editor-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  @Output() create: EventEmitter<any> = new EventEmitter();
  @Output() selected: EventEmitter<any> = new EventEmitter();
  @Output() clone: EventEmitter<any> = new EventEmitter();

  @ViewChild(CharListComponent)
  charList: CharListComponent;

  showCharlist: boolean = false;


  constructor() { }

  showCharacters() {
    this.showCharlist = !this.showCharlist;
  }

  updateCharList() {
    this.charList.refresh();
  }

  _selected(ev) {
    console.log(ev);
    this.selected.emit(ev.character);
    this.showCharlist = false;
  }

  _create() {
    this.create.emit({});
  }

  _clone() {
    this.clone.emit({});
  }


  ngOnInit() {
  }

}
