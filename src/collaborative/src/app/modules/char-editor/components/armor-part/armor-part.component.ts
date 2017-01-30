import { Component, OnInit } from '@angular/core';

import { CurrentCharService } from '../../services/current-char.service';

@Component({
  selector: 'app-armor-part',
  templateUrl: './armor-part.component.html',
  styleUrls: ['./armor-part.component.css']
})
export class ArmorPartComponent implements OnInit {

  constructor(
    private current: CurrentCharService
  ) { }

  ngOnInit() {
  }

}
