import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../shared/services/character.service';
import { Trait, TraitDescriptor } from '../../../../interfaces/trait';
import { TraitsService } from 'shared/services/traits.service';

@Component({
  selector: 'app-trait-list',
  templateUrl: './trait-list.component.html',
  styleUrls: ['./trait-list.component.css'],
  providers: [CharacterService]
})
export class TraitListComponent implements OnInit {

  @Input() traits: Trait[];
  @Input() category: string;

  @Output() traitChange: EventEmitter<Object> = new EventEmitter;

  defaultTrait: Trait;

  search_results: TraitDescriptor[];

  _search_term: string;

  edited_trait: Trait = null;


  constructor(
    private chars: CharacterService,
    private traitsrv: TraitsService
  ) { }

  addTrait() {
    let newTrait = this.defaultTrait.clone();
    this.traits.push(newTrait);
    this.edited_trait = newTrait;
    this.traitChange.emit({});
  }

  get filtered() {
    return this.traits.filter(t => t.category === this.category);
  }

  removeTrait(index: number) {
    let i = -1;
    let toRemove = this.traits.findIndex(t => {
      if (t.category === this.category) {
        ++i;
      }
      if (i === index) {
        return true;
      }
      return false;
    });
    this.traits.splice(toRemove, 1);
    this.traitChange.emit({});
  }

  get searchTerm() {
    return this._search_term;
  }

  set searchTerm(term: string) {
    this._search_term = term;
    if (this._search_term.length > 2) {
      this.traitsrv.search(this.category, term)
      .subscribe(results => this.search_results = results);
    } else {
      this.search_results = null;
    }
  }

  searchTrait(ev: any) {
    this.traitsrv.search(this.category, ev.srcElement.value)
    .subscribe(results => this.search_results = results);
  }

  addFromSearch(idx: number) {
    this.traitsrv.getTrait(this.search_results[idx].id)
    .subscribe(trait => {
      this.traits.push(trait);
      this.traitChange.emit();
      this.cancelSearch();
    });
  }

  cancelSearch() {
    this.search_results = null;
    this._search_term = '';
  }

  editTrait(trait: Trait) {
    this.edited_trait = trait;
  }

  editCanceled() {
    this.edited_trait = null;
  }

  editFinished() {
    console.log('Edit finished');
    this.edited_trait = null;
    this.traitChange.emit({});
  }

  traitChanged(ev: any, trait: Trait) {
    console.log('Trait changed');
    this.traitChange.emit({});
  }

  ngOnInit() {
    this.traitsrv.default.subscribe(trait => {
      trait.category = this.category;
      this.defaultTrait = trait;
    });
  }

}
