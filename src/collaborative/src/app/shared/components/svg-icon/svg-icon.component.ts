import { Component, OnInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { SvgCacheService } from '../../services/svg-cache.service';

@Component({
  selector: 'svg-icon',
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.css']
})
export class SvgIconComponent implements OnInit {

  @Input() src: string;
  @Output() click: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('placeholder') placeholder: ElementRef;

  constructor(
    private cache: SvgCacheService
  ) { }

  ngOnInit() {
    this.cache.getsvg(this.src)
    .subscribe(svg => this.placeholder.nativeElement.innerHTML =  svg);
  }

}
