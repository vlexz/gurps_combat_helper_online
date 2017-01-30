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
import { ArmorVisualComponent } from './components/armor-visual/armor-visual.component';
import { ArmorPartComponent } from './components/armor-part/armor-part.component';
import { ArmorListComponent } from './components/armor-list/armor-list.component';
import { ArmorEditorComponent } from './components/armor-editor/armor-editor.component';
import { SkillEditorComponent } from './components/skill-editor/skill-editor.component';
import { TechniqueEditorComponent } from './components/technique-editor/technique-editor.component';
import { InventoryComponent } from './components/inventory/inventory.component';
import { InvPartComponent } from './components/inv-part/inv-part.component';
import { InventoryEditorComponent } from './components/inventory-editor/inventory-editor.component';

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
    TraitEditorComponent,
    ArmorVisualComponent,
    ArmorPartComponent,
    ArmorListComponent,
    ArmorEditorComponent,
    SkillEditorComponent,
    TechniqueEditorComponent,
    InventoryComponent,
    InvPartComponent,
    InventoryEditorComponent
  ],
  providers: [
    CurrentCharService
  ],
  exports: [
    CharEditorComponent
  ]
})
export class CharEditorModule { }
