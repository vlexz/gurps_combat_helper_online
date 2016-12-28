import { Component, OnInit, Input } from '@angular/core';
import { CurrentCharService } from '../../services/current-char.service';
import { Armor } from 'interfaces/armor';
import { ArmorService } from 'shared/services/armor.service';
import { LibraryItem } from 'interfaces/search';

@Component({
  selector: 'armor-list',
  templateUrl: './armor-list.component.html',
  styleUrls: ['./armor-list.component.css']
})
export class ArmorListComponent implements OnInit {

  private _armors: Armor[];

  private currentArmor: Armor;
  private currentArmorIdx: number;

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

  add(data: LibraryItem) {
    this._armors.push(Armor.fromJson(data.data));
    if (!data.ready) {
      this.currentArmorIdx = this._armors.length - 1;
      this.currentArmor = this._armors[this.currentArmorIdx];
    }
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

  remove(i: number) {
    this._armors.splice(i, 1);
    this.current.updateArmor();
  }

  ngOnInit() {
  }

}
