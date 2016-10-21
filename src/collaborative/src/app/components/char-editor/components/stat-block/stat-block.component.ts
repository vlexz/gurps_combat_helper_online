import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Stat } from '../../../../interfaces/character';

@Component({
  selector: 'app-stat-block',
  templateUrl: './stat-block.component.html',
  styleUrls: ['./stat-block.component.css']
})
export class StatBlockComponent implements OnInit {

  @Input() stat: Stat;
  @Input() name: string;
  @Output() change: EventEmitter<Object>;

  constructor() {
    this.change = new EventEmitter();
  }

  get total(): number {
    if (this.stat) {
      return this.stat.base + this.stat.delta;
    }
  }

  set total(total: number) {
    if (this.stat) {
      this.stat.delta = total - this.stat.base;
      this.change.emit({
        name: this.name,
        delte: this.stat.delta
      });
    }
  }

  ngOnInit() {
  }

}
