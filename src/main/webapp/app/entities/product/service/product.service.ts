import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IProduct, NewProduct } from '../product.model';

export type PartialUpdateProduct = Partial<IProduct> & Pick<IProduct, 'id'>;

type RestOf<T extends IProduct | NewProduct> = Omit<T, 'deletedAt'> & {
  deletedAt?: string | null;
};

export type RestProduct = RestOf<IProduct>;

export type NewRestProduct = RestOf<NewProduct>;

export type PartialUpdateRestProduct = RestOf<PartialUpdateProduct>;

@Injectable()
export class ProductsService {
  readonly productsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly productsResource = httpResource<RestProduct[]>(() => {
    const params = this.productsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of product that have been fetched. It is updated when the productsResource emits a new value.
   * In case of error while fetching the products, the signal is set to an empty array.
   */
  readonly products = computed(() =>
    (this.productsResource.hasValue() ? this.productsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/products');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/products/_search');

  protected convertValueFromServer(restProduct: RestProduct): IProduct {
    return {
      ...restProduct,
      deletedAt: restProduct.deletedAt ? dayjs(restProduct.deletedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ProductService extends ProductsService {
  protected readonly http = inject(HttpClient);

  create(product: NewProduct): Observable<IProduct> {
    const copy = this.convertValueFromClient(product);
    return this.http.post<RestProduct>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(product: IProduct): Observable<IProduct> {
    const copy = this.convertValueFromClient(product);
    return this.http
      .put<RestProduct>(`${this.resourceUrl}/${encodeURIComponent(this.getProductIdentifier(product))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(product: PartialUpdateProduct): Observable<IProduct> {
    const copy = this.convertValueFromClient(product);
    return this.http
      .patch<RestProduct>(`${this.resourceUrl}/${encodeURIComponent(this.getProductIdentifier(product))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IProduct> {
    return this.http
      .get<RestProduct>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IProduct[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProduct[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IProduct[]> {
    const options = createRequestOption(req);
    return this.http.get<RestProduct[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getProductIdentifier(product: Pick<IProduct, 'id'>): number {
    return product.id;
  }

  compareProduct(o1: Pick<IProduct, 'id'> | null, o2: Pick<IProduct, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductIdentifier(o1) === this.getProductIdentifier(o2) : o1 === o2;
  }

  addProductToCollectionIfMissing<Type extends Pick<IProduct, 'id'>>(
    productCollection: Type[],
    ...productsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const products: Type[] = productsToCheck.filter(isPresent);
    if (products.length > 0) {
      const productCollectionIdentifiers = productCollection.map(productItem => this.getProductIdentifier(productItem));
      const productsToAdd = products.filter(productItem => {
        const productIdentifier = this.getProductIdentifier(productItem);
        if (productCollectionIdentifiers.includes(productIdentifier)) {
          return false;
        }
        productCollectionIdentifiers.push(productIdentifier);
        return true;
      });
      return [...productsToAdd, ...productCollection];
    }
    return productCollection;
  }

  protected convertValueFromClient<T extends IProduct | NewProduct | PartialUpdateProduct>(product: T): RestOf<T> {
    return {
      ...product,
      deletedAt: product.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestProduct): IProduct {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestProduct[]): IProduct[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
