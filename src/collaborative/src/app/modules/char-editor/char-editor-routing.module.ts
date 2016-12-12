import { NgModule }     from '@angular/core';
import { RouterModule } from '@angular/router';

import { CharEditorComponent } from './char-editor.component';
import { AdvDisPartComponent } from './components/adv-dis-part/adv-dis-part.component';
import { SkillTeqPartComponent } from './components/skill-teq-part/skill-teq-part.component';
import { ArmorPartComponent } from './components/armor-part/armor-part.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'char-editor',
        component: CharEditorComponent,
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'advdis'
          },
          {
            path: 'advdis',
            component: AdvDisPartComponent
          },
          {
            path: 'skills',
            component: SkillTeqPartComponent
          },
          {
            path: 'armor',
            component: ArmorPartComponent
          }
        ]
      }
    ])
  ],
  exports: [
    RouterModule
  ]
})
export class CharEditorRoutingModule {}
