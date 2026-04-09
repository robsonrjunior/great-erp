import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ITenant, NewTenant } from '../tenant.model';

export type PartialUpdateTenant = Partial<ITenant> & Pick<ITenant, 'id'>;

type RestOf<T extends ITenant | NewTenant> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestTenant = RestOf<ITenant>;

export type NewRestTenant = RestOf<NewTenant>;

export type PartialUpdateRestTenant = RestOf<PartialUpdateTenant>;

@Injectable()
export class TenantsService {
  readonly tenantsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tenantsResource = httpResource<RestTenant[]>(() => {
    const params = this.tenantsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tenant that have been fetched. It is updated when the tenantsResource emits a new value.
   * In case of error while fetching the tenants, the signal is set to an empty array.
   */
  readonly tenants = computed(() =>
    (this.tenantsResource.hasValue() ? this.tenantsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/tenants');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/tenants/_search');

  protected convertValueFromServer(restTenant: RestTenant): ITenant {
    return {
      ...restTenant,
      deletedAt: restTenant.deletedAt ? dayjs(restTenant.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TenantService extends TenantsService {
  protected readonly http = inject(HttpClient);

  create(tenant: NewTenant): Observable<ITenant> {
    const copy = this.convertValueFromClient(tenant);
    return this.http.post<RestTenant>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tenant: ITenant): Observable<ITenant> {
    const copy = this.convertValueFromClient(tenant);
    return this.http
      .put<RestTenant>(`${this.resourceUrl}/${encodeURIComponent(this.getTenantIdentifier(tenant))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tenant: PartialUpdateTenant): Observable<ITenant> {
    const copy = this.convertValueFromClient(tenant);
    return this.http
      .patch<RestTenant>(`${this.resourceUrl}/${encodeURIComponent(this.getTenantIdentifier(tenant))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITenant> {
    return this.http.get<RestTenant>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITenant[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTenant[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ITenant[]> {
    const options = createRequestOption(req);
    return this.http.get<RestTenant[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getTenantIdentifier(tenant: Pick<ITenant, 'id'>): number {
    return tenant.id;
  }

  compareTenant(o1: Pick<ITenant, 'id'> | null, o2: Pick<ITenant, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantIdentifier(o1) === this.getTenantIdentifier(o2) : o1 === o2;
  }

  addTenantToCollectionIfMissing<Type extends Pick<ITenant, 'id'>>(
    tenantCollection: Type[],
    ...tenantsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenants: Type[] = tenantsToCheck.filter(isPresent);
    if (tenants.length > 0) {
      const tenantCollectionIdentifiers = tenantCollection.map(tenantItem => this.getTenantIdentifier(tenantItem));
      const tenantsToAdd = tenants.filter(tenantItem => {
        const tenantIdentifier = this.getTenantIdentifier(tenantItem);
        if (tenantCollectionIdentifiers.includes(tenantIdentifier)) {
          return false;
        }
        tenantCollectionIdentifiers.push(tenantIdentifier);
        return true;
      });
      return [...tenantsToAdd, ...tenantCollection];
    }
    return tenantCollection;
  }

  protected convertValueFromClient<T extends ITenant | NewTenant | PartialUpdateTenant>(tenant: T): RestOf<T> {
    return {
      ...tenant,
      deletedAt: tenant.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTenant): ITenant {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTenant[]): ITenant[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
