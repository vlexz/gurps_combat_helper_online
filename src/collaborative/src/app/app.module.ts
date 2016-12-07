import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { CharEditorModule } from './modules/char-editor/char-editor.module';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
// import { CharEditorComponent } from './components/char-editor/char-editor.component';
// import { StatBlockComponent } from './components/char-editor/components/stat-block/stat-block.component';
// import { CharListComponent } from './components/char-list/char-list.component';
// import { CharacterManagerComponent } from './components/character-manager/character-manager.component';
// import { SkillListComponent } from './components/char-editor/components/skill-list/skill-list.component';
// import { TraitListComponent } from './components/char-editor/components/trait-list/trait-list.component';
// import { TechniquesComponent } from './components/char-editor/components/techniques/techniques.component';
// import { WeaponListComponent } from './components/char-editor/components/weapon-list/weapon-list.component';


@NgModule({
  declarations: [
    AppComponent,
    // CharEditorComponent,
    // StatBlockComponent,
    // CharListComponent,
    // CharacterManagerComponent,
    // SkillListComponent,
    // TraitListComponent,
    // TechniquesComponent,
    // WeaponListComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    CharEditorModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
