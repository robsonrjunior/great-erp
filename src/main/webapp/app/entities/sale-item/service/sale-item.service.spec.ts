import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISaleItem } from '../sale-item.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sale-item.test-samples';

import { RestSaleItem, SaleItemService } from './sale-item.service';

const requireRestSample: RestSaleItem = {
  ...sampleWithRequiredData,
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('SaleItem Service', () => {
  let service: SaleItemService;
  let httpMock: HttpTestingController;
  let expectedResult: ISaleItem | ISaleItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SaleItemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SaleItem', () => {
      const saleItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(saleItem).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SaleItem', () => {
      const saleItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(saleItem).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SaleItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SaleItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SaleItem', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a SaleItem', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addSaleItemToCollectionIfMissing', () => {
      it('should add a SaleItem to an empty array', () => {
        const saleItem: ISaleItem = sampleWithRequiredData;
        expectedResult = service.addSaleItemToCollectionIfMissing([], saleItem);
        expect(expectedResult).toEqual([saleItem]);
      });

      it('should not add a SaleItem to an array that contains it', () => {
        const saleItem: ISaleItem = sampleWithRequiredData;
        const saleItemCollection: ISaleItem[] = [
          {
            ...saleItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSaleItemToCollectionIfMissing(saleItemCollection, saleItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SaleItem to an array that doesn't contain it", () => {
        const saleItem: ISaleItem = sampleWithRequiredData;
        const saleItemCollection: ISaleItem[] = [sampleWithPartialData];
        expectedResult = service.addSaleItemToCollectionIfMissing(saleItemCollection, saleItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(saleItem);
      });

      it('should add only unique SaleItem to an array', () => {
        const saleItemArray: ISaleItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const saleItemCollection: ISaleItem[] = [sampleWithRequiredData];
        expectedResult = service.addSaleItemToCollectionIfMissing(saleItemCollection, ...saleItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const saleItem: ISaleItem = sampleWithRequiredData;
        const saleItem2: ISaleItem = sampleWithPartialData;
        expectedResult = service.addSaleItemToCollectionIfMissing([], saleItem, saleItem2);
        expect(expectedResult).toEqual([saleItem, saleItem2]);
      });

      it('should accept null and undefined values', () => {
        const saleItem: ISaleItem = sampleWithRequiredData;
        expectedResult = service.addSaleItemToCollectionIfMissing([], null, saleItem, undefined);
        expect(expectedResult).toEqual([saleItem]);
      });

      it('should return initial array if no SaleItem is added', () => {
        const saleItemCollection: ISaleItem[] = [sampleWithRequiredData];
        expectedResult = service.addSaleItemToCollectionIfMissing(saleItemCollection, undefined, null);
        expect(expectedResult).toEqual(saleItemCollection);
      });
    });

    describe('compareSaleItem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSaleItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25187 };
        const entity2 = null;

        const compareResult1 = service.compareSaleItem(entity1, entity2);
        const compareResult2 = service.compareSaleItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25187 };
        const entity2 = { id: 21071 };

        const compareResult1 = service.compareSaleItem(entity1, entity2);
        const compareResult2 = service.compareSaleItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25187 };
        const entity2 = { id: 25187 };

        const compareResult1 = service.compareSaleItem(entity1, entity2);
        const compareResult2 = service.compareSaleItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
