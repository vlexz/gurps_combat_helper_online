import { Component, OnInit } from '@angular/core';

import { CurrentCharService } from '../../services/current-char.service';

@Component({
  selector: 'armor-visual',
  templateUrl: './armor-visual.component.html',
  // templateUrl: './armor.svg',
  styleUrls: ['./armor-visual.component.css']
})
export class ArmorVisualComponent implements OnInit {

  constructor(
    private current: CurrentCharService
  ) { }

  dr_description(location: string, front: boolean) {
    let side = front ? 'Front' : 'Rear';
    let {dr, ep, epi} = this.current.char.damageResistance[location][side.toLowerCase()];
    return `${side}: ${dr}/${ep}/${epi}`;
  }

  ngOnInit() {
  }

}
