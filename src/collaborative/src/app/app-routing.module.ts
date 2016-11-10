import { NgModule }     from '@angular/core';
import { RouterModule } from '@angular/router';

import { CharEditorModule } from './modules/char-editor/char-editor.module';
import { PartyEditorModule } from './modules/party-editor/party-editor.module';
import { PartyEditorComponent } from './modules/party-editor/party-editor.component';

@NgModule({
  imports: [
    CharEditorModule,
    PartyEditorModule,
    RouterModule.forRoot([
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'char-editor'
      },
      {
        path: 'party-editor',
        component: PartyEditorComponent
      }
    ])
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
