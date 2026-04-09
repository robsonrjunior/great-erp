import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable, asapScheduler, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ICity, NewCity } from '../city.model';

export type PartialUpdateCity = Partial<ICity> & Pick<ICity, 'id'>;

@Injectable()
export class CitiesService {
  readonly citiesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly citiesResource = httpResource<ICity[]>(() => {
    const params = this.citiesParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of city that have been fetched. It is updated when the citiesResource emits a new value.
   * In case of error while fetching the cities, the signal is set to an empty array.
   */
  readonly cities = computed(() => (this.citiesResource.hasValue() ? this.citiesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/cities');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/cities/_search');
}

@Injectable({ providedIn: 'root' })
export class CityService extends CitiesService {
  protected readonly http = inject(HttpClient);

  create(city: NewCity): Observable<ICity> {
    return this.http.post<ICity>(this.resourceUrl, city);
  }

  update(city: ICity): Observable<ICity> {
    return this.http.put<ICity>(`${this.resourceUrl}/${encodeURIComponent(this.getCityIdentifier(city))}`, city);
  }

  partialUpdate(city: PartialUpdateCity): Observable<ICity> {
    return this.http.patch<ICity>(`${this.resourceUrl}/${encodeURIComponent(this.getCityIdentifier(city))}`, city);
  }

  find(id: number): Observable<ICity> {
    return this.http.get<ICity>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICity[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ICity[]> {
    const options = createRequestOption(req);
    return this.http.get<ICity[]>(this.resourceSearchUrl, { params: options }).pipe(catchError(() => scheduled([], asapScheduler)));
  }

  getCityIdentifier(city: Pick<ICity, 'id'>): number {
    return city.id;
  }

  compareCity(o1: Pick<ICity, 'id'> | null, o2: Pick<ICity, 'id'> | null): boolean {
    return o1 && o2 ? this.getCityIdentifier(o1) === this.getCityIdentifier(o2) : o1 === o2;
  }

  addCityToCollectionIfMissing<Type extends Pick<ICity, 'id'>>(
    cityCollection: Type[],
    ...citiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cities: Type[] = citiesToCheck.filter(isPresent);
    if (cities.length > 0) {
      const cityCollectionIdentifiers = cityCollection.map(cityItem => this.getCityIdentifier(cityItem));
      const citiesToAdd = cities.filter(cityItem => {
        const cityIdentifier = this.getCityIdentifier(cityItem);
        if (cityCollectionIdentifiers.includes(cityIdentifier)) {
          return false;
        }
        cityCollectionIdentifiers.push(cityIdentifier);
        return true;
      });
      return [...citiesToAdd, ...cityCollection];
    }
    return cityCollection;
  }
}
