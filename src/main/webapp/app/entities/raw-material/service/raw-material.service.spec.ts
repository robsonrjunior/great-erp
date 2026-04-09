import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRawMaterial } from '../raw-material.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../raw-material.test-samples';

import { RawMaterialService, RestRawMaterial } from './raw-material.service';

const requireRestSample: RestRawMaterial = {
  ...sampleWithRequiredData,
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('RawMaterial Service', () => {
  let service: RawMaterialService;
  let httpMock: HttpTestingController;
  let expectedResult: IRawMaterial | IRawMaterial[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RawMaterialService);
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

    it('should create a RawMaterial', () => {
      const rawMaterial = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rawMaterial).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RawMaterial', () => {
      const rawMaterial = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rawMaterial).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RawMaterial', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RawMaterial', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RawMaterial', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a RawMaterial', () => {
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

    describe('addRawMaterialToCollectionIfMissing', () => {
      it('should add a RawMaterial to an empty array', () => {
        const rawMaterial: IRawMaterial = sampleWithRequiredData;
        expectedResult = service.addRawMaterialToCollectionIfMissing([], rawMaterial);
        expect(expectedResult).toEqual([rawMaterial]);
      });

      it('should not add a RawMaterial to an array that contains it', () => {
        const rawMaterial: IRawMaterial = sampleWithRequiredData;
        const rawMaterialCollection: IRawMaterial[] = [
          {
            ...rawMaterial,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRawMaterialToCollectionIfMissing(rawMaterialCollection, rawMaterial);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RawMaterial to an array that doesn't contain it", () => {
        const rawMaterial: IRawMaterial = sampleWithRequiredData;
        const rawMaterialCollection: IRawMaterial[] = [sampleWithPartialData];
        expectedResult = service.addRawMaterialToCollectionIfMissing(rawMaterialCollection, rawMaterial);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rawMaterial);
      });

      it('should add only unique RawMaterial to an array', () => {
        const rawMaterialArray: IRawMaterial[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rawMaterialCollection: IRawMaterial[] = [sampleWithRequiredData];
        expectedResult = service.addRawMaterialToCollectionIfMissing(rawMaterialCollection, ...rawMaterialArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rawMaterial: IRawMaterial = sampleWithRequiredData;
        const rawMaterial2: IRawMaterial = sampleWithPartialData;
        expectedResult = service.addRawMaterialToCollectionIfMissing([], rawMaterial, rawMaterial2);
        expect(expectedResult).toEqual([rawMaterial, rawMaterial2]);
      });

      it('should accept null and undefined values', () => {
        const rawMaterial: IRawMaterial = sampleWithRequiredData;
        expectedResult = service.addRawMaterialToCollectionIfMissing([], null, rawMaterial, undefined);
        expect(expectedResult).toEqual([rawMaterial]);
      });

      it('should return initial array if no RawMaterial is added', () => {
        const rawMaterialCollection: IRawMaterial[] = [sampleWithRequiredData];
        expectedResult = service.addRawMaterialToCollectionIfMissing(rawMaterialCollection, undefined, null);
        expect(expectedResult).toEqual(rawMaterialCollection);
      });
    });

    describe('compareRawMaterial', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRawMaterial(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6822 };
        const entity2 = null;

        const compareResult1 = service.compareRawMaterial(entity1, entity2);
        const compareResult2 = service.compareRawMaterial(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6822 };
        const entity2 = { id: 19276 };

        const compareResult1 = service.compareRawMaterial(entity1, entity2);
        const compareResult2 = service.compareRawMaterial(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6822 };
        const entity2 = { id: 6822 };

        const compareResult1 = service.compareRawMaterial(entity1, entity2);
        const compareResult2 = service.compareRawMaterial(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
