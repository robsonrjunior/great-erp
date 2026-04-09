import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IRawMaterial, NewRawMaterial } from '../raw-material.model';

export type PartialUpdateRawMaterial = Partial<IRawMaterial> & Pick<IRawMaterial, 'id'>;

type RestOf<T extends IRawMaterial | NewRawMaterial> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestRawMaterial = RestOf<IRawMaterial>;

export type NewRestRawMaterial = RestOf<NewRawMaterial>;

export type PartialUpdateRestRawMaterial = RestOf<PartialUpdateRawMaterial>;

@Injectable()
export class RawMaterialsService {
  readonly rawMaterialsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rawMaterialsResource = httpResource<RestRawMaterial[]>(() => {
    const params = this.rawMaterialsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rawMaterial that have been fetched. It is updated when the rawMaterialsResource emits a new value.
   * In case of error while fetching the rawMaterials, the signal is set to an empty array.
   */
  readonly rawMaterials = computed(() =>
    (this.rawMaterialsResource.hasValue() ? this.rawMaterialsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/raw-materials');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/raw-materials/_search');

  protected convertValueFromServer(restRawMaterial: RestRawMaterial): IRawMaterial {
    return {
      ...restRawMaterial,
      deletedAt: restRawMaterial.deletedAt ? dayjs(restRawMaterial.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RawMaterialService extends RawMaterialsService {
  protected readonly http = inject(HttpClient);

  create(rawMaterial: NewRawMaterial): Observable<IRawMaterial> {
    const copy = this.convertValueFromClient(rawMaterial);
    return this.http.post<RestRawMaterial>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(rawMaterial: IRawMaterial): Observable<IRawMaterial> {
    const copy = this.convertValueFromClient(rawMaterial);
    return this.http
      .put<RestRawMaterial>(`${this.resourceUrl}/${encodeURIComponent(this.getRawMaterialIdentifier(rawMaterial))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(rawMaterial: PartialUpdateRawMaterial): Observable<IRawMaterial> {
    const copy = this.convertValueFromClient(rawMaterial);
    return this.http
      .patch<RestRawMaterial>(`${this.resourceUrl}/${encodeURIComponent(this.getRawMaterialIdentifier(rawMaterial))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRawMaterial> {
    return this.http
      .get<RestRawMaterial>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRawMaterial[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRawMaterial[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IRawMaterial[]> {
    const options = createRequestOption(req);
    return this.http.get<RestRawMaterial[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getRawMaterialIdentifier(rawMaterial: Pick<IRawMaterial, 'id'>): number {
    return rawMaterial.id;
  }

  compareRawMaterial(o1: Pick<IRawMaterial, 'id'> | null, o2: Pick<IRawMaterial, 'id'> | null): boolean {
    return o1 && o2 ? this.getRawMaterialIdentifier(o1) === this.getRawMaterialIdentifier(o2) : o1 === o2;
  }

  addRawMaterialToCollectionIfMissing<Type extends Pick<IRawMaterial, 'id'>>(
    rawMaterialCollection: Type[],
    ...rawMaterialsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rawMaterials: Type[] = rawMaterialsToCheck.filter(isPresent);
    if (rawMaterials.length > 0) {
      const rawMaterialCollectionIdentifiers = rawMaterialCollection.map(rawMaterialItem => this.getRawMaterialIdentifier(rawMaterialItem));
      const rawMaterialsToAdd = rawMaterials.filter(rawMaterialItem => {
        const rawMaterialIdentifier = this.getRawMaterialIdentifier(rawMaterialItem);
        if (rawMaterialCollectionIdentifiers.includes(rawMaterialIdentifier)) {
          return false;
        }
        rawMaterialCollectionIdentifiers.push(rawMaterialIdentifier);
        return true;
      });
      return [...rawMaterialsToAdd, ...rawMaterialCollection];
    }
    return rawMaterialCollection;
  }

  protected convertValueFromClient<T extends IRawMaterial | NewRawMaterial | PartialUpdateRawMaterial>(rawMaterial: T): RestOf<T> {
    return {
      ...rawMaterial,
      deletedAt: rawMaterial.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRawMaterial): IRawMaterial {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRawMaterial[]): IRawMaterial[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
