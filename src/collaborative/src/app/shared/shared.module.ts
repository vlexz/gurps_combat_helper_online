import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CharacterService } from './services/character.service';
import { TraitsService } from './services/traits.service';
import { ArmorService } from './services/armor.service';
import { SkillsService } from './services/skills.service';
import { FileDialogComponent } from './components/file-dialog/file-dialog.component';
import { SearchBlockComponent } from './components/search-block/search-block.component';
import { DialogContainerComponent } from './components/dialog-container/dialog-container.component';
import { TechniqueService } from './services/technique.service';
import { SvgIconComponent } from './components/svg-icon/svg-icon.component';
import { SvgCacheService } from './services/svg-cache.service';
import { InventoryService } from './services/inventory.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  providers: [
    CharacterService,
    TraitsService,
    ArmorService,
    SkillsService,
    TechniqueService,
    SvgCacheService,
    InventoryService
  ],
  declarations: [
    FileDialogComponent,
    SearchBlockComponent,
    DialogContainerComponent,
    SvgIconComponent
  ],
  exports: [
    FileDialogComponent,
    SearchBlockComponent,
    DialogContainerComponent,
    SvgIconComponent
  ]
})
export class SharedModule { }
