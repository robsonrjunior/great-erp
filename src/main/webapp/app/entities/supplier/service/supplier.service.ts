import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ISupplier, NewSupplier } from '../supplier.model';

export type PartialUpdateSupplier = Partial<ISupplier> & Pick<ISupplier, 'id'>;

type RestOf<T extends ISupplier | NewSupplier> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestSupplier = RestOf<ISupplier>;

export type NewRestSupplier = RestOf<NewSupplier>;

export type PartialUpdateRestSupplier = RestOf<PartialUpdateSupplier>;

@Injectable()
export class SuppliersService {
  readonly suppliersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly suppliersResource = httpResource<RestSupplier[]>(() => {
    const params = this.suppliersParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of supplier that have been fetched. It is updated when the suppliersResource emits a new value.
   * In case of error while fetching the suppliers, the signal is set to an empty array.
   */
  readonly suppliers = computed(() =>
    (this.suppliersResource.hasValue() ? this.suppliersResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/suppliers');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/suppliers/_search');

  protected convertValueFromServer(restSupplier: RestSupplier): ISupplier {
    return {
      ...restSupplier,
      deletedAt: restSupplier.deletedAt ? dayjs(restSupplier.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SupplierService extends SuppliersService {
  protected readonly http = inject(HttpClient);

  create(supplier: NewSupplier): Observable<ISupplier> {
    const copy = this.convertValueFromClient(supplier);
    return this.http.post<RestSupplier>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(supplier: ISupplier): Observable<ISupplier> {
    const copy = this.convertValueFromClient(supplier);
    return this.http
      .put<RestSupplier>(`${this.resourceUrl}/${encodeURIComponent(this.getSupplierIdentifier(supplier))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(supplier: PartialUpdateSupplier): Observable<ISupplier> {
    const copy = this.convertValueFromClient(supplier);
    return this.http
      .patch<RestSupplier>(`${this.resourceUrl}/${encodeURIComponent(this.getSupplierIdentifier(supplier))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ISupplier> {
    return this.http
      .get<RestSupplier>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISupplier[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSupplier[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ISupplier[]> {
    const options = createRequestOption(req);
    return this.http.get<RestSupplier[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getSupplierIdentifier(supplier: Pick<ISupplier, 'id'>): number {
    return supplier.id;
  }

  compareSupplier(o1: Pick<ISupplier, 'id'> | null, o2: Pick<ISupplier, 'id'> | null): boolean {
    return o1 && o2 ? this.getSupplierIdentifier(o1) === this.getSupplierIdentifier(o2) : o1 === o2;
  }

  addSupplierToCollectionIfMissing<Type extends Pick<ISupplier, 'id'>>(
    supplierCollection: Type[],
    ...suppliersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const suppliers: Type[] = suppliersToCheck.filter(isPresent);
    if (suppliers.length > 0) {
      const supplierCollectionIdentifiers = supplierCollection.map(supplierItem => this.getSupplierIdentifier(supplierItem));
      const suppliersToAdd = suppliers.filter(supplierItem => {
        const supplierIdentifier = this.getSupplierIdentifier(supplierItem);
        if (supplierCollectionIdentifiers.includes(supplierIdentifier)) {
          return false;
        }
        supplierCollectionIdentifiers.push(supplierIdentifier);
        return true;
      });
      return [...suppliersToAdd, ...supplierCollection];
    }
    return supplierCollection;
  }

  protected convertValueFromClient<T extends ISupplier | NewSupplier | PartialUpdateSupplier>(supplier: T): RestOf<T> {
    return {
      ...supplier,
      deletedAt: supplier.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSupplier): ISupplier {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSupplier[]): ISupplier[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
