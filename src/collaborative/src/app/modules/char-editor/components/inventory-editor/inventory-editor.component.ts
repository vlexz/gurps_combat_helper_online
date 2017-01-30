import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { InventoryItem } from 'interfaces/inventory';

@Component({
  selector: 'inventory-editor',
  templateUrl: './inventory-editor.component.html',
  styleUrls: ['./inventory-editor.component.css']
})
export class InventoryEditorComponent implements OnInit {

  @Input() item: InventoryItem;

  @Output() done: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  _done() {
    this.done.emit();
  }

  ngOnInit() {
  }

}
