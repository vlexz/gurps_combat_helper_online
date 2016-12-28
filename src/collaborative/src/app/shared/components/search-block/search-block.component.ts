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

  search_results: SearchItem[];

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

  ngOnInit() {
  }

}
