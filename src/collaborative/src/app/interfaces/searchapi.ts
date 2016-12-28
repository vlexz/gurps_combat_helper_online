
import { Observable } from 'rxjs';
import {SearchItem, LibraryItem} from './search';

export declare abstract class SearchApi {
  abstract search(term: string): Observable<SearchItem[]>;
  abstract getOne(id: string): Observable<LibraryItem>;
  abstract default(): Observable<Object>;
}
