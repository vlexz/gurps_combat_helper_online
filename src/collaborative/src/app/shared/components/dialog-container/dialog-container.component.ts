import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'dialog-container',
  templateUrl: './dialog-container.component.html',
  styleUrls: ['./dialog-container.component.css']
})
export class DialogContainerComponent implements OnInit {

  @Input() title: string = 'Dialog Title';
  @Output() done: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  _done() {
    this.done.emit();
  }

  ngOnInit() {
  }

}
