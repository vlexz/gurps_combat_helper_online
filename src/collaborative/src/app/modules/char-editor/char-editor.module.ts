import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CharEditorRoutingModule } from './char-editor-routing.module';

import { SharedModule } from 'shared/shared.module';

import { CurrentCharService } from './services/current-char.service';

import { CharEditorComponent } from './char-editor.component';
import { CharListComponent } from './components/char-list/char-list.component';
import { SkillListComponent } from './components/skill-list/skill-list.component';
import { TraitListComponent } from './components/trait-list/trait-list.component';
import { TechniquesComponent } from './components/techniques/techniques.component';
import { WeaponListComponent } from './components/weapon-list/weapon-list.component';
import { StatBlockComponent } from './components/stat-block/stat-block.component';
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import { AdvDisPartComponent } from './components/adv-dis-part/adv-dis-part.component';
import { SkillTeqPartComponent } from './components/skill-teq-part/skill-teq-part.component';
import { TraitEditorComponent } from './components/trait-editor/trait-editor.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    CharEditorRoutingModule,
    SharedModule
  ],
  declarations: [
    CharEditorComponent,
    CharListComponent,
    SkillListComponent,
    TraitListComponent,
    TechniquesComponent,
    WeaponListComponent,
    StatBlockComponent,
    ToolbarComponent,
    AdvDisPartComponent,
    SkillTeqPartComponent,
    TraitEditorComponent
  ],
  providers: [
    CurrentCharService
  ],
  exports: [
    CharEditorComponent
  ]
})
export class CharEditorModule { }
