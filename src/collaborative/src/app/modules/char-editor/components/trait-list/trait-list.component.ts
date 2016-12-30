import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../shared/services/character.service';
import { Trait } from '../../../../interfaces/trait';
import { TraitsService } from 'shared/services/traits.service';

import { Observable } from 'rxjs';
import { SearchApi } from 'interfaces/searchapi';
import { SearchItem, LibraryItem} from 'interfaces/search';

class TraitsSearchAdapter implements SearchApi {
  constructor(
    private service: TraitsService,
    private categories: string[]) {
  }

  search(term: string): Observable<SearchItem[]> {
    return this.service.search(this.categories, term);
  }

  getOne(id: string): Observable<LibraryItem> {
    return this.service.getTrait(id);
  }

  default(): Observable<Object> {
    return this.service.default;
  }
}

@Component({
  selector: 'app-trait-list',
  templateUrl: './trait-list.component.html',
  styleUrls: ['./trait-list.component.css'],
  providers: [CharacterService]
})
export class TraitListComponent implements OnInit {

  _traits: Trait[];

  @Output() traitChange: EventEmitter<Object> = new EventEmitter;

  defaultTrait: Trait;

  edited_trait: Trait = null;
  edited_trait_idx: number;

  searchAdapter: TraitsSearchAdapter;

  private categories: string[];


  constructor(
    private chars: CharacterService,
    private traitsrv: TraitsService
  ) {
  }

  @Input() set traits(traits: Trait[]) {
    console.log('Set traits into list');
    this._traits = traits;
    if (this.edited_trait_idx) {
      this.edited_trait = this.filtered[this.edited_trait_idx];
    }
  }

  @Input() set category(categories: string) {
    this.categories = categories.split(',');
  }

  get category(): string {
    return this.categories.map(cat => cat + 's').join(' and ');
  }

  addTrait(data: LibraryItem) {
    if (!data.ready) {
      this.edited_trait_idx = this.filtered.length;
    }
    this._traits.push(Trait.fromJson(data.data));
    this.traitChange.emit({});
  }

  get filtered() {
    return this._traits.filter(t => this.categories.indexOf(t.category) !== -1);
  }

  removeTrait(index: number) {
    let i = -1;
    let toRemove = this._traits.findIndex(t => {
      if (this.categories.indexOf(t.category) !== -1) {
        ++i;
      }
      if (i === index) {
        return true;
      }
      return false;
    });
    this._traits.splice(toRemove, 1);
    this.traitChange.emit({});
  }

  editTrait(idx: number) {
    this.edited_trait_idx = idx;
    this.edited_trait = this.filtered[idx];
  }

  editCanceled() {
    this.edited_trait_idx = null;
    this.edited_trait = null;
  }

  editFinished() {
    console.log('Edit finished');
    this.edited_trait_idx = null;
    this.edited_trait = null;
    this.traitChange.emit({});
  }

  traitChanged(ev: any, trait: Trait) {
    console.log('Trait changed');
    this.traitChange.emit({});
  }

  ngOnInit() {
    this.searchAdapter = new TraitsSearchAdapter(this.traitsrv, this.categories);
    // this.traitsrv.default.subscribe(trait => {
    //   trait.category = this.category;
    //   this.defaultTrait = trait;
    // });
  }

}
