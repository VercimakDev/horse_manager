import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse} from '../dto/horse';
import {Sex} from '../dto/sex';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }
  /**
   * Filter for parentsuggentions.
   *
   * @param input userinput to match with the horses name to give suggestions
   * @param sex the sex of the parent to filter by
   * @return an Observable for the horses meeting the parentcriteria
   */
  filter(input: string, sex: Sex): Observable<Horse[]>{
    return this.http.get<Horse[]>(baseUri + `/${input}/${sex}`);
  }
  /**
   * Update the Horse with the given id.
   *
   * @param id the id of the horse that is to be changed
   * @param horse the new data for the horse
   * @return an Observable for the updated horse
   */
  update(id: number, horse: Horse): Observable<Horse>{
    return this.http.put<Horse>(baseUri + `/${id}`,horse);
  }
  /**
   * Get the Horse with the given id.
   *
   * @param id the id of the horse
   * @return an Observable for the looked up horse
   */
  getById(id: number): Observable<Horse>{
    return this.http.get<Horse>(baseUri + `/${id}`);
  }

  delete(id: number): Observable<any>{
    return this.http.delete(baseUri + `/${id}`);
  }
}
