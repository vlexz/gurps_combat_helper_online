import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { SearchItem, LibraryItem } from 'interfaces/search';
import { SearchApi } from 'interfaces/searchapi';

@Component({
  selector: 'search-block',
  templateUrl: './search-block.component.html',
  styleUrls: ['./search-block.component.css']
})
export class SearchBlockComponent implements OnInit {

  @Input() api: SearchApi;
  @Output() newitem: EventEmitter<LibraryItem> = new EventEmitter<LibraryItem>();
  @Input() dialogTitle: string;

  search_results: SearchItem[];

  allItems: SearchItem[] = null;
  filterTerm: string;
  showAll: boolean = false;

  private _searchTerm: string;

  constructor() {
  }

  get searchTerm(): string {
    return this._searchTerm;
  }

  set searchTerm(term: string) {
    this._searchTerm = term;
    if (this._searchTerm.length > 2) {
      this.api.search(this._searchTerm)
      .subscribe(results => this.search_results = results);
    } else {
      this.search_results = null;
    }
  }

  addNew() {
    this._searchTerm = '';
    this.search_results = null;
    this.api.default()
    .subscribe(object => {
      this.newitem.emit({
        ready: false,
        data: object
      });
    });
  }

  addFromSearch(idx: number) {
    this.api.getOne(this.search_results[idx].id)
    .subscribe(item => {
      this._searchTerm = '';
      this.search_results = null;
      this.newitem.emit(item);
    });
  }

  cancelSearch() {
    this.search_results = null;
    this._searchTerm = '';
  }

  viewAll() {
    if (this.allItems) {
      this.showAll = true;
    } else {
      this.api.getAll()
      .subscribe(items => {
        this.allItems = items;
        this.showAll = true;
      });
    }
  }

  closeFullList() {
    this.showAll = false;
  }

  addFromFullList(itemId: string) {
    this.api.getOne(itemId)
    .subscribe(item => {
      this.showAll = false;
      this.newitem.emit(item);
    });
  }

  get filteredItems(): SearchItem[] {
    if (this.filterTerm) {
      return this.allItems.filter(item => item.name.indexOf(this.filterTerm) !== -1);
    } else {
      return this.allItems;
    }
  }

  ngOnInit() {
  }

}
