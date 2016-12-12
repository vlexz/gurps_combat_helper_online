import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Armor } from 'interfaces/armor';
import { ConstantTables } from 'interfaces/tables';

@Component({
  selector: 'armor-editor',
  templateUrl: './armor-editor.component.html',
  styleUrls: ['./armor-editor.component.css']
})
export class ArmorEditorComponent implements OnInit {

  @Input() armor: Armor;

  @Output() editDone: EventEmitter<void> = new EventEmitter<void>();
  @Output() editCancel: EventEmitter<void> = new EventEmitter<void>();

  tables: ConstantTables = new ConstantTables;

  _showLocations: boolean = false;

  constructor() { }

  showLocations() {
    this._showLocations = true;
  }

  addLocation(loc: string) {
    this.armor.locations.push(loc);
    this._showLocations = false;
  }

  removeLocation(i: number) {
    this.armor.locations.splice(i, 1);
  }

  done() {
    this.editDone.emit();
  }

  ngOnInit() {
  }

}
