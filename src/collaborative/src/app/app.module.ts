import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { CharEditorComponent } from './components/char-editor/char-editor.component';
import { StatBlockComponent } from './components/char-editor/components/stat-block/stat-block.component';
import { CharListComponent } from './components/char-list/char-list.component';
import { CharacterManagerComponent } from './components/character-manager/character-manager.component';
import { TraitBlockComponent } from './components/char-editor/components/trait-block/trait-block.component';
import { SkillBlockComponent } from './components/char-editor/components/skill-block/skill-block.component';

@NgModule({
  declarations: [
    AppComponent,
    CharEditorComponent,
    StatBlockComponent,
    CharListComponent,
    CharacterManagerComponent,
    TraitBlockComponent,
    SkillBlockComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
