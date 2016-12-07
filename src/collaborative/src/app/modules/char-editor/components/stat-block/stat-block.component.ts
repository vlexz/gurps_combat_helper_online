import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Stat } from '../../../../interfaces/character';

@Component({
  selector: 'app-stat-block',
  templateUrl: './stat-block.component.html',
  styleUrls: ['./stat-block.component.css']
})
export class StatBlockComponent implements OnInit {

  @Input() stat: Stat;
  @Input() statName: string;
  @Output() statChanged: EventEmitter<Object> = new EventEmitter();

  constructor() {
  }

  get total(): number {
    if (this.stat) {
      return this.stat.base + this.stat.delta;
    }
  }

  set total(total: number) {
    if (this.stat) {
      this.stat.delta = total - this.stat.base;
      this.statChanged.emit({
        name: this.statName,
        delta: this.stat.delta
      });
    }
  }

  ngOnInit() {
  }

}
