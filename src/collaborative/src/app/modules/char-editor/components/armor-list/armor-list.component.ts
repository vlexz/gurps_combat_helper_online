import { Component, OnInit, Input } from '@angular/core';
import { CurrentCharService } from '../../services/current-char.service';
import { Armor } from 'interfaces/armor';
import { ArmorService } from 'shared/services/armor.service';

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

  remove(i: number) {
    this._armors.splice(i, 1);
    this.current.updateArmor();
  }

  ngOnInit() {
    this.armorsrv.defaultArmor()
    .subscribe(armor => this.defaultArmor = armor);
  }

}
