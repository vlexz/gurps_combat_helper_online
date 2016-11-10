import { NgModule }     from '@angular/core';
import { RouterModule } from '@angular/router';

import { CharEditorComponent } from './char-editor.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'char-editor',
        component: CharEditorComponent
      }
    ])
  ],
  exports: [
    RouterModule
  ]
})
export class CharEditorRoutingModule {}
