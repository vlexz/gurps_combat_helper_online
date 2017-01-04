import { Component, OnInit } from '@angular/core';

import { CurrentCharService } from '../../services/current-char.service';

@Component({
  selector: 'app-inv-part',
  templateUrl: './inv-part.component.html',
  styleUrls: ['./inv-part.component.css']
})
export class InvPartComponent implements OnInit {

  constructor(
    private current: CurrentCharService
  ) { }

  ngOnInit() {
  }

}
