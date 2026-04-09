import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IWarehouse, NewWarehouse } from '../warehouse.model';

export type PartialUpdateWarehouse = Partial<IWarehouse> & Pick<IWarehouse, 'id'>;

type RestOf<T extends IWarehouse | NewWarehouse> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestWarehouse = RestOf<IWarehouse>;

export type NewRestWarehouse = RestOf<NewWarehouse>;

export type PartialUpdateRestWarehouse = RestOf<PartialUpdateWarehouse>;

@Injectable()
export class WarehousesService {
  readonly warehousesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly warehousesResource = httpResource<RestWarehouse[]>(() => {
    const params = this.warehousesParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of warehouse that have been fetched. It is updated when the warehousesResource emits a new value.
   * In case of error while fetching the warehouses, the signal is set to an empty array.
   */
  readonly warehouses = computed(() =>
    (this.warehousesResource.hasValue() ? this.warehousesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/warehouses');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/warehouses/_search');

  protected convertValueFromServer(restWarehouse: RestWarehouse): IWarehouse {
    return {
      ...restWarehouse,
      deletedAt: restWarehouse.deletedAt ? dayjs(restWarehouse.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class WarehouseService extends WarehousesService {
  protected readonly http = inject(HttpClient);

  create(warehouse: NewWarehouse): Observable<IWarehouse> {
    const copy = this.convertValueFromClient(warehouse);
    return this.http.post<RestWarehouse>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(warehouse: IWarehouse): Observable<IWarehouse> {
    const copy = this.convertValueFromClient(warehouse);
    return this.http
      .put<RestWarehouse>(`${this.resourceUrl}/${encodeURIComponent(this.getWarehouseIdentifier(warehouse))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(warehouse: PartialUpdateWarehouse): Observable<IWarehouse> {
    const copy = this.convertValueFromClient(warehouse);
    return this.http
      .patch<RestWarehouse>(`${this.resourceUrl}/${encodeURIComponent(this.getWarehouseIdentifier(warehouse))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IWarehouse> {
    return this.http
      .get<RestWarehouse>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IWarehouse[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWarehouse[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IWarehouse[]> {
    const options = createRequestOption(req);
    return this.http.get<RestWarehouse[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getWarehouseIdentifier(warehouse: Pick<IWarehouse, 'id'>): number {
    return warehouse.id;
  }

  compareWarehouse(o1: Pick<IWarehouse, 'id'> | null, o2: Pick<IWarehouse, 'id'> | null): boolean {
    return o1 && o2 ? this.getWarehouseIdentifier(o1) === this.getWarehouseIdentifier(o2) : o1 === o2;
  }

  addWarehouseToCollectionIfMissing<Type extends Pick<IWarehouse, 'id'>>(
    warehouseCollection: Type[],
    ...warehousesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const warehouses: Type[] = warehousesToCheck.filter(isPresent);
    if (warehouses.length > 0) {
      const warehouseCollectionIdentifiers = warehouseCollection.map(warehouseItem => this.getWarehouseIdentifier(warehouseItem));
      const warehousesToAdd = warehouses.filter(warehouseItem => {
        const warehouseIdentifier = this.getWarehouseIdentifier(warehouseItem);
        if (warehouseCollectionIdentifiers.includes(warehouseIdentifier)) {
          return false;
        }
        warehouseCollectionIdentifiers.push(warehouseIdentifier);
        return true;
      });
      return [...warehousesToAdd, ...warehouseCollection];
    }
    return warehouseCollection;
  }

  protected convertValueFromClient<T extends IWarehouse | NewWarehouse | PartialUpdateWarehouse>(warehouse: T): RestOf<T> {
    return {
      ...warehouse,
      deletedAt: warehouse.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestWarehouse): IWarehouse {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestWarehouse[]): IWarehouse[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
