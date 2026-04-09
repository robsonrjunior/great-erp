import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPerson } from '../person.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../person.test-samples';

import { PersonService, RestPerson } from './person.service';

const requireRestSample: RestPerson = {
  ...sampleWithRequiredData,
  birthDate: sampleWithRequiredData.birthDate?.format(DATE_FORMAT),
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('Person Service', () => {
  let service: PersonService;
  let httpMock: HttpTestingController;
  let expectedResult: IPerson | IPerson[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PersonService);
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

    it('should create a Person', () => {
      const person = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(person).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Person', () => {
      const person = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(person).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Person', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Person', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Person', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a Person', () => {
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

    describe('addPersonToCollectionIfMissing', () => {
      it('should add a Person to an empty array', () => {
        const person: IPerson = sampleWithRequiredData;
        expectedResult = service.addPersonToCollectionIfMissing([], person);
        expect(expectedResult).toEqual([person]);
      });

      it('should not add a Person to an array that contains it', () => {
        const person: IPerson = sampleWithRequiredData;
        const personCollection: IPerson[] = [
          {
            ...person,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPersonToCollectionIfMissing(personCollection, person);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Person to an array that doesn't contain it", () => {
        const person: IPerson = sampleWithRequiredData;
        const personCollection: IPerson[] = [sampleWithPartialData];
        expectedResult = service.addPersonToCollectionIfMissing(personCollection, person);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(person);
      });

      it('should add only unique Person to an array', () => {
        const personArray: IPerson[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const personCollection: IPerson[] = [sampleWithRequiredData];
        expectedResult = service.addPersonToCollectionIfMissing(personCollection, ...personArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const person: IPerson = sampleWithRequiredData;
        const person2: IPerson = sampleWithPartialData;
        expectedResult = service.addPersonToCollectionIfMissing([], person, person2);
        expect(expectedResult).toEqual([person, person2]);
      });

      it('should accept null and undefined values', () => {
        const person: IPerson = sampleWithRequiredData;
        expectedResult = service.addPersonToCollectionIfMissing([], null, person, undefined);
        expect(expectedResult).toEqual([person]);
      });

      it('should return initial array if no Person is added', () => {
        const personCollection: IPerson[] = [sampleWithRequiredData];
        expectedResult = service.addPersonToCollectionIfMissing(personCollection, undefined, null);
        expect(expectedResult).toEqual(personCollection);
      });
    });

    describe('comparePerson', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePerson(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8101 };
        const entity2 = null;

        const compareResult1 = service.comparePerson(entity1, entity2);
        const compareResult2 = service.comparePerson(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8101 };
        const entity2 = { id: 8051 };

        const compareResult1 = service.comparePerson(entity1, entity2);
        const compareResult2 = service.comparePerson(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8101 };
        const entity2 = { id: 8101 };

        const compareResult1 = service.comparePerson(entity1, entity2);
        const compareResult2 = service.comparePerson(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
