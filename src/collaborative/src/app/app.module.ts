import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { CharEditorComponent } from './components/char-editor/char-editor.component';
import { StatBlockComponent } from './components/char-editor/components/stat-block/stat-block.component';

@NgModule({
  declarations: [
    AppComponent,
    CharEditorComponent,
    StatBlockComponent
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
