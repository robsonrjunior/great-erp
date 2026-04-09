import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable, asapScheduler, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IState, NewState } from '../state.model';

export type PartialUpdateState = Partial<IState> & Pick<IState, 'id'>;

@Injectable()
export class StatesService {
  readonly statesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly statesResource = httpResource<IState[]>(() => {
    const params = this.statesParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of state that have been fetched. It is updated when the statesResource emits a new value.
   * In case of error while fetching the states, the signal is set to an empty array.
   */
  readonly states = computed(() => (this.statesResource.hasValue() ? this.statesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/states');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/states/_search');
}

@Injectable({ providedIn: 'root' })
export class StateService extends StatesService {
  protected readonly http = inject(HttpClient);

  create(state: NewState): Observable<IState> {
    return this.http.post<IState>(this.resourceUrl, state);
  }

  update(state: IState): Observable<IState> {
    return this.http.put<IState>(`${this.resourceUrl}/${encodeURIComponent(this.getStateIdentifier(state))}`, state);
  }

  partialUpdate(state: PartialUpdateState): Observable<IState> {
    return this.http.patch<IState>(`${this.resourceUrl}/${encodeURIComponent(this.getStateIdentifier(state))}`, state);
  }

  find(id: number): Observable<IState> {
    return this.http.get<IState>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IState[]>> {
    const options = createRequestOption(req);
    return this.http.get<IState[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IState[]> {
    const options = createRequestOption(req);
    return this.http.get<IState[]>(this.resourceSearchUrl, { params: options }).pipe(catchError(() => scheduled([], asapScheduler)));
  }

  getStateIdentifier(state: Pick<IState, 'id'>): number {
    return state.id;
  }

  compareState(o1: Pick<IState, 'id'> | null, o2: Pick<IState, 'id'> | null): boolean {
    return o1 && o2 ? this.getStateIdentifier(o1) === this.getStateIdentifier(o2) : o1 === o2;
  }

  addStateToCollectionIfMissing<Type extends Pick<IState, 'id'>>(
    stateCollection: Type[],
    ...statesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const states: Type[] = statesToCheck.filter(isPresent);
    if (states.length > 0) {
      const stateCollectionIdentifiers = stateCollection.map(stateItem => this.getStateIdentifier(stateItem));
      const statesToAdd = states.filter(stateItem => {
        const stateIdentifier = this.getStateIdentifier(stateItem);
        if (stateCollectionIdentifiers.includes(stateIdentifier)) {
          return false;
        }
        stateCollectionIdentifiers.push(stateIdentifier);
        return true;
      });
      return [...statesToAdd, ...stateCollection];
    }
    return stateCollection;
  }
}
