import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ISaleItem, NewSaleItem } from '../sale-item.model';

export type PartialUpdateSaleItem = Partial<ISaleItem> & Pick<ISaleItem, 'id'>;

type RestOf<T extends ISaleItem | NewSaleItem> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestSaleItem = RestOf<ISaleItem>;

export type NewRestSaleItem = RestOf<NewSaleItem>;

export type PartialUpdateRestSaleItem = RestOf<PartialUpdateSaleItem>;

@Injectable()
export class SaleItemsService {
  readonly saleItemsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly saleItemsResource = httpResource<RestSaleItem[]>(() => {
    const params = this.saleItemsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of saleItem that have been fetched. It is updated when the saleItemsResource emits a new value.
   * In case of error while fetching the saleItems, the signal is set to an empty array.
   */
  readonly saleItems = computed(() =>
    (this.saleItemsResource.hasValue() ? this.saleItemsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/sale-items');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/sale-items/_search');

  protected convertValueFromServer(restSaleItem: RestSaleItem): ISaleItem {
    return {
      ...restSaleItem,
      deletedAt: restSaleItem.deletedAt ? dayjs(restSaleItem.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SaleItemService extends SaleItemsService {
  protected readonly http = inject(HttpClient);

  create(saleItem: NewSaleItem): Observable<ISaleItem> {
    const copy = this.convertValueFromClient(saleItem);
    return this.http.post<RestSaleItem>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(saleItem: ISaleItem): Observable<ISaleItem> {
    const copy = this.convertValueFromClient(saleItem);
    return this.http
      .put<RestSaleItem>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleItemIdentifier(saleItem))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(saleItem: PartialUpdateSaleItem): Observable<ISaleItem> {
    const copy = this.convertValueFromClient(saleItem);
    return this.http
      .patch<RestSaleItem>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleItemIdentifier(saleItem))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ISaleItem> {
    return this.http
      .get<RestSaleItem>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISaleItem[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSaleItem[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ISaleItem[]> {
    const options = createRequestOption(req);
    return this.http.get<RestSaleItem[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getSaleItemIdentifier(saleItem: Pick<ISaleItem, 'id'>): number {
    return saleItem.id;
  }

  compareSaleItem(o1: Pick<ISaleItem, 'id'> | null, o2: Pick<ISaleItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getSaleItemIdentifier(o1) === this.getSaleItemIdentifier(o2) : o1 === o2;
  }

  addSaleItemToCollectionIfMissing<Type extends Pick<ISaleItem, 'id'>>(
    saleItemCollection: Type[],
    ...saleItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const saleItems: Type[] = saleItemsToCheck.filter(isPresent);
    if (saleItems.length > 0) {
      const saleItemCollectionIdentifiers = saleItemCollection.map(saleItemItem => this.getSaleItemIdentifier(saleItemItem));
      const saleItemsToAdd = saleItems.filter(saleItemItem => {
        const saleItemIdentifier = this.getSaleItemIdentifier(saleItemItem);
        if (saleItemCollectionIdentifiers.includes(saleItemIdentifier)) {
          return false;
        }
        saleItemCollectionIdentifiers.push(saleItemIdentifier);
        return true;
      });
      return [...saleItemsToAdd, ...saleItemCollection];
    }
    return saleItemCollection;
  }

  protected convertValueFromClient<T extends ISaleItem | NewSaleItem | PartialUpdateSaleItem>(saleItem: T): RestOf<T> {
    return {
      ...saleItem,
      deletedAt: saleItem.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSaleItem): ISaleItem {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSaleItem[]): ISaleItem[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
