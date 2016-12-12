import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CharacterService } from './services/character.service';
import { TraitsService } from './services/traits.service';
import { ArmorService } from './services/armor.service';
import { FileDialogComponent } from './components/file-dialog/file-dialog.component';
import { SearchBlockComponent } from './components/search-block/search-block.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  providers: [
    CharacterService,
    TraitsService,
    ArmorService
  ],
  declarations: [
    FileDialogComponent,
    SearchBlockComponent
  ],
  exports: [
    FileDialogComponent,
    SearchBlockComponent
  ]
})
export class SharedModule { }
