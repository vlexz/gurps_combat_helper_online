import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CharEditorComponent } from './char-editor.component';
import { CharListComponent } from './components/char-list/char-list.component';
import { SkillListComponent } from './components/skill-list/skill-list.component';
import { TraitListComponent } from './components/trait-list/trait-list.component';
import { TechniquesComponent } from './components/techniques/techniques.component';
import { WeaponListComponent } from './components/weapon-list/weapon-list.component';
import { StatBlockComponent } from './components/stat-block/stat-block.component';
import { ToolbarComponent } from './components/toolbar/toolbar.component';

// import { CharacterManagerComponent } from './components/character-manager/character-manager.component';



@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  declarations: [
    CharEditorComponent,
    CharListComponent,
    SkillListComponent,
    TraitListComponent,
    TechniquesComponent,
    WeaponListComponent,
    StatBlockComponent,
    ToolbarComponent
  ],
  exports: [
    CharEditorComponent
  ]
})
export class CharEditorModule { }
