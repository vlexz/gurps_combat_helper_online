import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CharacterService } from '../../../../services/character.service';
import { Trait } from '../../../../interfaces/trait';

@Component({
  selector: 'app-trait-list',
  templateUrl: './trait-list.component.html',
  styleUrls: ['./trait-list.component.css'],
  providers: [CharacterService]
})
export class TraitListComponent implements OnInit {

  @Input() traits: Trait[];
  @Input() category: string;

  @Output() change: EventEmitter<Object> = new EventEmitter;

  defaultTrait: Trait;


  constructor(
    private chars: CharacterService
  ) { }

  addTrait() {
    this.traits.push(this.defaultTrait.clone());
    this.change.emit({});
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
    this.change.emit({});
  }

  traitChanged() {
    this.change.emit({});
  }

  ngOnInit() {
    this.chars.defaultTrait().subscribe(trait => {
      trait.category = this.category;
      this.defaultTrait = trait;
    });
  }

}
