import { Component, OnInit } from '@angular/core';

import { CurrentCharService } from '../../services/current-char.service';

@Component({
  selector: 'app-adv-dis-part',
  templateUrl: './adv-dis-part.component.html',
  styleUrls: ['./adv-dis-part.component.css']
})
export class AdvDisPartComponent implements OnInit {

  constructor(
    private current: CurrentCharService
  ) { }


  traitChanged() {
    this.current.updateTraits();
  }

  ngOnInit() {
  }

}
