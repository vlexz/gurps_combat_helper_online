import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Armor, ArmorComponent } from 'interfaces/armor';
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

  _showLocations: number = -1;

  constructor() { }

  showLocations(i: number) {
    this._showLocations = i;
  }

  hideLocations() {
    this._showLocations = -1;
  }

  addLocation(component: ArmorComponent, loc: string) {
    component.locations.push(loc);
    this._showLocations = -1;
  }

  removeLocation(component: ArmorComponent, i: number) {
    component.locations.splice(i, 1);
  }

  addComponent() {
    this.armor.components.push(new ArmorComponent);
  }

  removeComponent(i: number) {
    this.armor.components.splice(i, 1);
  }

  done() {
    this.editDone.emit();
  }

  ngOnInit() {
  }

}
