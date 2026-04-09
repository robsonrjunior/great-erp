import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ISale, NewSale } from '../sale.model';

export type PartialUpdateSale = Partial<ISale> & Pick<ISale, 'id'>;

type RestOf<T extends ISale | NewSale> = Omit<T, 'saleDate' | 'deletedAt'> & {
  saleDate?: string | null;
  deletedAt?: string | null;
};

export type RestSale = RestOf<ISale>;

export type NewRestSale = RestOf<NewSale>;

export type PartialUpdateRestSale = RestOf<PartialUpdateSale>;

@Injectable()
export class SalesService {
  readonly salesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly salesResource = httpResource<RestSale[]>(() => {
    const params = this.salesParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of sale that have been fetched. It is updated when the salesResource emits a new value.
   * In case of error while fetching the sales, the signal is set to an empty array.
   */
  readonly sales = computed(() =>
    (this.salesResource.hasValue() ? this.salesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/sales');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/sales/_search');

  protected convertValueFromServer(restSale: RestSale): ISale {
    return {
      ...restSale,
      saleDate: restSale.saleDate ? dayjs(restSale.saleDate) : undefined,
      deletedAt: restSale.deletedAt ? dayjs(restSale.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SaleService extends SalesService {
  protected readonly http = inject(HttpClient);

  create(sale: NewSale): Observable<ISale> {
    const copy = this.convertValueFromClient(sale);
    return this.http.post<RestSale>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sale: ISale): Observable<ISale> {
    const copy = this.convertValueFromClient(sale);
    return this.http
      .put<RestSale>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleIdentifier(sale))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sale: PartialUpdateSale): Observable<ISale> {
    const copy = this.convertValueFromClient(sale);
    return this.http
      .patch<RestSale>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleIdentifier(sale))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ISale> {
    return this.http.get<RestSale>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISale[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSale[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ISale[]> {
    const options = createRequestOption(req);
    return this.http.get<RestSale[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getSaleIdentifier(sale: Pick<ISale, 'id'>): number {
    return sale.id;
  }

  compareSale(o1: Pick<ISale, 'id'> | null, o2: Pick<ISale, 'id'> | null): boolean {
    return o1 && o2 ? this.getSaleIdentifier(o1) === this.getSaleIdentifier(o2) : o1 === o2;
  }

  addSaleToCollectionIfMissing<Type extends Pick<ISale, 'id'>>(
    saleCollection: Type[],
    ...salesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sales: Type[] = salesToCheck.filter(isPresent);
    if (sales.length > 0) {
      const saleCollectionIdentifiers = saleCollection.map(saleItem => this.getSaleIdentifier(saleItem));
      const salesToAdd = sales.filter(saleItem => {
        const saleIdentifier = this.getSaleIdentifier(saleItem);
        if (saleCollectionIdentifiers.includes(saleIdentifier)) {
          return false;
        }
        saleCollectionIdentifiers.push(saleIdentifier);
        return true;
      });
      return [...salesToAdd, ...saleCollection];
    }
    return saleCollection;
  }

  protected convertValueFromClient<T extends ISale | NewSale | PartialUpdateSale>(sale: T): RestOf<T> {
    return {
      ...sale,
      saleDate: sale.saleDate?.toJSON() ?? null,
      deletedAt: sale.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSale): ISale {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSale[]): ISale[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
