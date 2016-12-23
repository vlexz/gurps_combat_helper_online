import { Component, OnInit, Input } from '@angular/core';
import { CurrentCharService } from '../../services/current-char.service';
import { Armor } from 'interfaces/armor';
import { ArmorService } from 'shared/services/armor.service';
import { SearchItem } from 'interfaces/search_item';

@Component({
  selector: 'armor-list',
  templateUrl: './armor-list.component.html',
  styleUrls: ['./armor-list.component.css']
})
export class ArmorListComponent implements OnInit {

  private defaultArmor: Armor;

  private _armors: Armor[];

  private currentArmor: Armor;
  private currentArmorIdx: number;

  private search_results: SearchItem[];

  constructor(
    private current: CurrentCharService,
    private armorsrv: ArmorService
  ) { }

  @Input() set armors(armors: Armor[]) {
    this._armors = armors;
    if (this.currentArmorIdx) {
      this.currentArmor = this._armors[this.currentArmorIdx];
    }
  }

  add() {
    this._armors.push(this.defaultArmor.clone());
    this.current.updateArmor();
  }

  edit(i: number) {
    this.currentArmorIdx = i;
    this.currentArmor = this._armors[this.currentArmorIdx];
  }

  editDone() {
    this.currentArmor = null;
    this.currentArmorIdx = null;
    this.current.updateArmor();
  }

  search(term: string) {
    this.armorsrv.search(term)
    .subscribe(results => {
      this.search_results = results;
    });
  }

  addFromSearch(i: number) {
    this.armorsrv.get(this.search_results[i].id)
    .subscribe(armor => {
      this._armors.push(armor);
      this.search_results = null;
      this.current.updateArmor();
    });
  }

  cancelSearch() {
    this.search_results = null;
  }

  remove(i: number) {
    this._armors.splice(i, 1);
    this.current.updateArmor();
  }

  ngOnInit() {
    this.armorsrv.defaultArmor()
    .subscribe(armor => this.defaultArmor = armor);
  }

}
