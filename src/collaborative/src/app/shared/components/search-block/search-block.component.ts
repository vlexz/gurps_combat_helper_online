import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'search-block',
  templateUrl: './search-block.component.html',
  styleUrls: ['./search-block.component.css']
})
export class SearchBlockComponent implements OnInit {

  @Input() search_results;
  @Output() add_new: EventEmitter<void> = new EventEmitter<void>();
  @Output() add_from_search: EventEmitter<number> = new EventEmitter<number>();
  @Output() search_term: EventEmitter<string> = new EventEmitter<string>();
  @Output() cancel_search: EventEmitter<void> = new EventEmitter<void>();

  private _searchTerm: string;

  constructor() {
  }

  get searchTerm(): string {
    return this._searchTerm;
  }

  set searchTerm(term: string) {
    this._searchTerm = term;
    if (this._searchTerm.length > 2) {
      this.search_term.emit(this._searchTerm);
    } else {
      this.cancel_search.emit();
    }
  }

  addNew() {
    this.add_new.emit();
    this._searchTerm = '';
  }

  addFromSearch(idx: number) {
    this._searchTerm = '';
    this.add_from_search.emit(idx);
  }

  cancelSearch() {
    this.cancel_search.emit();
    this._searchTerm = '';
  }

  ngOnInit() {
  }

}
