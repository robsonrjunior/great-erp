import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { PartyType } from 'app/entities/enumerations/party-type.model';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IRawMaterial } from 'app/entities/raw-material/raw-material.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { SupplierService } from '../service/supplier.service';
import { ISupplier } from '../supplier.model';

import { SupplierFormGroup, SupplierFormService } from './supplier-form.service';
import { RawMaterialService } from 'app/entities/raw-material/service/raw-material.service';

@Component({
  selector: 'jhi-supplier-update',
  templateUrl: './supplier-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SupplierUpdate implements OnInit {
  readonly isSaving = signal(false);
  supplier: ISupplier | null = null;
  partyTypeValues = Object.keys(PartyType);

  peopleCollection = signal<IPerson[]>([]);
  companiesCollection = signal<ICompany[]>([]);
  rawMaterialsSharedCollection = signal<IRawMaterial[]>([]);

  protected supplierService = inject(SupplierService);
  protected supplierFormService = inject(SupplierFormService);
  protected personService = inject(PersonService);
  protected companyService = inject(CompanyService);
  protected rawMaterialService = inject(RawMaterialService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SupplierFormGroup = this.supplierFormService.createSupplierFormGroup();

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareRawMaterial = (o1: IRawMaterial | null, o2: IRawMaterial | null): boolean => this.rawMaterialService.compareRawMaterial(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ supplier }) => {
      this.supplier = supplier;
      if (supplier) {
        this.updateForm(supplier);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const supplier = this.supplierFormService.getSupplier(this.editForm);
    if (supplier.id === null) {
      this.subscribeToSaveResponse(this.supplierService.create(supplier));
    } else {
      this.subscribeToSaveResponse(this.supplierService.update(supplier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISupplier | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(supplier: ISupplier): void {
    this.supplier = supplier;
    this.supplierFormService.resetForm(this.editForm, supplier);

    this.peopleCollection.set(this.personService.addPersonToCollectionIfMissing<IPerson>(this.peopleCollection(), supplier.person));
    this.companiesCollection.set(
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(this.companiesCollection(), supplier.company),
    );
    this.rawMaterialsSharedCollection.update(rawMaterials =>
      this.rawMaterialService.addRawMaterialToCollectionIfMissing<IRawMaterial>(rawMaterials, supplier.rawMaterials),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.personService
      .query({ 'supplierId.specified': 'false' })
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.supplier?.person)))
      .subscribe((people: IPerson[]) => this.peopleCollection.set(people));

    this.companyService
      .query({ 'supplierId.specified': 'false' })
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.supplier?.company)),
      )
      .subscribe((companies: ICompany[]) => this.companiesCollection.set(companies));

    this.rawMaterialService
      .query()
      .pipe(map((res: HttpResponse<IRawMaterial[]>) => res.body ?? []))
      .pipe(
        map((rawMaterials: IRawMaterial[]) =>
          this.rawMaterialService.addRawMaterialToCollectionIfMissing<IRawMaterial>(rawMaterials, this.supplier?.rawMaterials),
        ),
      )
      .subscribe((rawMaterials: IRawMaterial[]) => this.rawMaterialsSharedCollection.set(rawMaterials));
  }
}
