import { Component, OnInit, Input} from '@angular/core';

import { InventoryItem } from 'interfaces/inventory';
import { InventoryService } from 'shared/services/inventory.service';
import { CurrentCharService } from '../../services/current-char.service';
import { LibraryItem } from 'interfaces/search';
import { ConstantTables } from 'interfaces/tables';

@Component({
  selector: 'character-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {

  _items: InventoryItem[] = [];

  editedItem: InventoryItem = null;
  editedItemIdx: number = -1;

  tables: ConstantTables = new ConstantTables;

  constructor(
    private inventorysrv: InventoryService,
    private current: CurrentCharService
  ) { }

  @Input() set items(items: InventoryItem[]) {
    this._items = items;
    if (this.editedItemIdx !== -1) {
      this.editedItem = this._items[this.editedItemIdx];
    }
  }

  add(item: LibraryItem) {
    this._items.push(InventoryItem.fromJson(item.data));
    if (!item.ready) {
      this.editedItemIdx = this._items.length - 1;
      this.editedItem = this._items[this.editedItemIdx];
    }
    this.update();
  }

  edit(i: number) {
    this.editedItem = this._items[i];
    this.editedItemIdx = i;
  }

  editFinished() {
    this.editedItem = null;
    this.editedItemIdx = -1;
    this.update();
  }

  remove(i: number) {
    this._items.splice(i, 1);
    this.update();
  }

  update() {
    this.current.updateInventory();
  }

  ngOnInit() {
  }

}
