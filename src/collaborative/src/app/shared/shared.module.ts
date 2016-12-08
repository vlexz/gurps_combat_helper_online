import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CharacterService } from './services/character.service';
import { TraitsService } from './services/traits.service';
import { FileDialogComponent } from './components/file-dialog/file-dialog.component';

@NgModule({
  imports: [
    CommonModule
  ],
  providers: [
    CharacterService,
    TraitsService
  ],
  declarations: [FileDialogComponent]
})
export class SharedModule { }
