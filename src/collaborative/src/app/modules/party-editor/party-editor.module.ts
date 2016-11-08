import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PartyEditorComponent } from './party-editor.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [PartyEditorComponent],
  exports: [PartyEditorComponent]
})
export class PartyEditorModule { }
