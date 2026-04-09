import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISale } from '../sale.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sale.test-samples';

import { RestSale, SaleService } from './sale.service';

const requireRestSample: RestSale = {
  ...sampleWithRequiredData,
  saleDate: sampleWithRequiredData.saleDate?.toJSON(),
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('Sale Service', () => {
  let service: SaleService;
  let httpMock: HttpTestingController;
  let expectedResult: ISale | ISale[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SaleService);
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

    it('should create a Sale', () => {
      const sale = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sale).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sale', () => {
      const sale = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sale).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sale', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sale', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Sale', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a Sale', () => {
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

    describe('addSaleToCollectionIfMissing', () => {
      it('should add a Sale to an empty array', () => {
        const sale: ISale = sampleWithRequiredData;
        expectedResult = service.addSaleToCollectionIfMissing([], sale);
        expect(expectedResult).toEqual([sale]);
      });

      it('should not add a Sale to an array that contains it', () => {
        const sale: ISale = sampleWithRequiredData;
        const saleCollection: ISale[] = [
          {
            ...sale,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSaleToCollectionIfMissing(saleCollection, sale);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sale to an array that doesn't contain it", () => {
        const sale: ISale = sampleWithRequiredData;
        const saleCollection: ISale[] = [sampleWithPartialData];
        expectedResult = service.addSaleToCollectionIfMissing(saleCollection, sale);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sale);
      });

      it('should add only unique Sale to an array', () => {
        const saleArray: ISale[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const saleCollection: ISale[] = [sampleWithRequiredData];
        expectedResult = service.addSaleToCollectionIfMissing(saleCollection, ...saleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sale: ISale = sampleWithRequiredData;
        const sale2: ISale = sampleWithPartialData;
        expectedResult = service.addSaleToCollectionIfMissing([], sale, sale2);
        expect(expectedResult).toEqual([sale, sale2]);
      });

      it('should accept null and undefined values', () => {
        const sale: ISale = sampleWithRequiredData;
        expectedResult = service.addSaleToCollectionIfMissing([], null, sale, undefined);
        expect(expectedResult).toEqual([sale]);
      });

      it('should return initial array if no Sale is added', () => {
        const saleCollection: ISale[] = [sampleWithRequiredData];
        expectedResult = service.addSaleToCollectionIfMissing(saleCollection, undefined, null);
        expect(expectedResult).toEqual(saleCollection);
      });
    });

    describe('compareSale', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSale(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2908 };
        const entity2 = null;

        const compareResult1 = service.compareSale(entity1, entity2);
        const compareResult2 = service.compareSale(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2908 };
        const entity2 = { id: 10270 };

        const compareResult1 = service.compareSale(entity1, entity2);
        const compareResult2 = service.compareSale(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2908 };
        const entity2 = { id: 2908 };

        const compareResult1 = service.compareSale(entity1, entity2);
        const compareResult2 = service.compareSale(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
