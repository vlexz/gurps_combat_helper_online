import { Component, OnInit } from '@angular/core';
import { CurrentCharService } from '../../services/current-char.service';

@Component({
  selector: 'app-skill-teq-part',
  templateUrl: './skill-teq-part.component.html',
  styleUrls: ['./skill-teq-part.component.css']
})
export class SkillTeqPartComponent implements OnInit {

  constructor(
    private current: CurrentCharService
  ) { }

  ngOnInit() {
  }

}
