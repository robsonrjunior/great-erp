import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IState } from '../state.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../state.test-samples';

import { StateService } from './state.service';

const requireRestSample: IState = {
  ...sampleWithRequiredData,
};

describe('State Service', () => {
  let service: StateService;
  let httpMock: HttpTestingController;
  let expectedResult: IState | IState[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StateService);
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

    it('should create a State', () => {
      const state = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(state).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a State', () => {
      const state = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(state).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a State', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of State', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a State', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a State', () => {
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

    describe('addStateToCollectionIfMissing', () => {
      it('should add a State to an empty array', () => {
        const state: IState = sampleWithRequiredData;
        expectedResult = service.addStateToCollectionIfMissing([], state);
        expect(expectedResult).toEqual([state]);
      });

      it('should not add a State to an array that contains it', () => {
        const state: IState = sampleWithRequiredData;
        const stateCollection: IState[] = [
          {
            ...state,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, state);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a State to an array that doesn't contain it", () => {
        const state: IState = sampleWithRequiredData;
        const stateCollection: IState[] = [sampleWithPartialData];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, state);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(state);
      });

      it('should add only unique State to an array', () => {
        const stateArray: IState[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stateCollection: IState[] = [sampleWithRequiredData];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, ...stateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const state: IState = sampleWithRequiredData;
        const state2: IState = sampleWithPartialData;
        expectedResult = service.addStateToCollectionIfMissing([], state, state2);
        expect(expectedResult).toEqual([state, state2]);
      });

      it('should accept null and undefined values', () => {
        const state: IState = sampleWithRequiredData;
        expectedResult = service.addStateToCollectionIfMissing([], null, state, undefined);
        expect(expectedResult).toEqual([state]);
      });

      it('should return initial array if no State is added', () => {
        const stateCollection: IState[] = [sampleWithRequiredData];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, undefined, null);
        expect(expectedResult).toEqual(stateCollection);
      });
    });

    describe('compareState', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareState(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31448 };
        const entity2 = null;

        const compareResult1 = service.compareState(entity1, entity2);
        const compareResult2 = service.compareState(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31448 };
        const entity2 = { id: 6174 };

        const compareResult1 = service.compareState(entity1, entity2);
        const compareResult2 = service.compareState(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31448 };
        const entity2 = { id: 31448 };

        const compareResult1 = service.compareState(entity1, entity2);
        const compareResult2 = service.compareState(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
