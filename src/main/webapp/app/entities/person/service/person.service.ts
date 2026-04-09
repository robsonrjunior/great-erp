import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IPerson, NewPerson } from '../person.model';

export type PartialUpdatePerson = Partial<IPerson> & Pick<IPerson, 'id'>;

type RestOf<T extends IPerson | NewPerson> = Omit<T, 'birthDate' | 'deletedAt'> & {
  birthDate?: string | null;
  deletedAt?: string | null;
};

export type RestPerson = RestOf<IPerson>;

export type NewRestPerson = RestOf<NewPerson>;

export type PartialUpdateRestPerson = RestOf<PartialUpdatePerson>;

@Injectable()
export class PeopleService {
  readonly peopleParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly peopleResource = httpResource<RestPerson[]>(() => {
    const params = this.peopleParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of person that have been fetched. It is updated when the peopleResource emits a new value.
   * In case of error while fetching the people, the signal is set to an empty array.
   */
  readonly people = computed(() =>
    (this.peopleResource.hasValue() ? this.peopleResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/people');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/people/_search');

  protected convertValueFromServer(restPerson: RestPerson): IPerson {
    return {
      ...restPerson,
      birthDate: restPerson.birthDate ? dayjs(restPerson.birthDate) : undefined,
      deletedAt: restPerson.deletedAt ? dayjs(restPerson.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PersonService extends PeopleService {
  protected readonly http = inject(HttpClient);

  create(person: NewPerson): Observable<IPerson> {
    const copy = this.convertValueFromClient(person);
    return this.http.post<RestPerson>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(person: IPerson): Observable<IPerson> {
    const copy = this.convertValueFromClient(person);
    return this.http
      .put<RestPerson>(`${this.resourceUrl}/${encodeURIComponent(this.getPersonIdentifier(person))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(person: PartialUpdatePerson): Observable<IPerson> {
    const copy = this.convertValueFromClient(person);
    return this.http
      .patch<RestPerson>(`${this.resourceUrl}/${encodeURIComponent(this.getPersonIdentifier(person))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPerson> {
    return this.http.get<RestPerson>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPerson[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPerson[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IPerson[]> {
    const options = createRequestOption(req);
    return this.http.get<RestPerson[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getPersonIdentifier(person: Pick<IPerson, 'id'>): number {
    return person.id;
  }

  comparePerson(o1: Pick<IPerson, 'id'> | null, o2: Pick<IPerson, 'id'> | null): boolean {
    return o1 && o2 ? this.getPersonIdentifier(o1) === this.getPersonIdentifier(o2) : o1 === o2;
  }

  addPersonToCollectionIfMissing<Type extends Pick<IPerson, 'id'>>(
    personCollection: Type[],
    ...peopleToCheck: (Type | null | undefined)[]
  ): Type[] {
    const people: Type[] = peopleToCheck.filter(isPresent);
    if (people.length > 0) {
      const personCollectionIdentifiers = personCollection.map(personItem => this.getPersonIdentifier(personItem));
      const peopleToAdd = people.filter(personItem => {
        const personIdentifier = this.getPersonIdentifier(personItem);
        if (personCollectionIdentifiers.includes(personIdentifier)) {
          return false;
        }
        personCollectionIdentifiers.push(personIdentifier);
        return true;
      });
      return [...peopleToAdd, ...personCollection];
    }
    return personCollection;
  }

  protected convertValueFromClient<T extends IPerson | NewPerson | PartialUpdatePerson>(person: T): RestOf<T> {
    return {
      ...person,
      birthDate: person.birthDate?.format(DATE_FORMAT) ?? null,
      deletedAt: person.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPerson): IPerson {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPerson[]): IPerson[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
