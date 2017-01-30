import { Component, OnInit, Output, EventEmitter, ViewChild, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CharListComponent } from '../char-list/char-list.component';


@Component({
  selector: 'char-editor-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  @Output() clone: EventEmitter<any> = new EventEmitter();

  @ViewChild(CharListComponent)
  charList: CharListComponent;

  showCharlist: boolean = false;


  constructor(
    private router: Router
  ) { }

  @HostListener('document:keydown', ['$event'])
  onKey(ev) {
    if (ev.code === 'Escape') {
      this.showCharlist = false;
    }
  }

  @HostListener('document:click', ['$event'])
  onClick(ev) {
    if (this.showCharlist) {
      if (ev.path.findIndex(obj => obj.localName === 'char-editor-toolbar') === -1) {
        this.showCharlist = false;
      }
    }
  }

  showCharacters() {
    this.showCharlist = !this.showCharlist;
  }

  updateCharList() {
    this.charList.refresh();
  }

  _selected(ev) {
    this.router.navigate(['/char-editor', {id: ev.character}]);
    this.showCharlist = false;
  }

  _create() {
    this.router.navigate(['/char-editor', {id: 'new'}]);
  }

  _clone() {
    this.clone.emit({});
  }


  ngOnInit() {
  }

}
