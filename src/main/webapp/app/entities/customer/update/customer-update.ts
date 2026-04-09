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
import { ISale } from 'app/entities/sale/sale.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ICustomer } from '../customer.model';
import { CustomerService } from '../service/customer.service';

import { CustomerFormGroup, CustomerFormService } from './customer-form.service';
import { SaleService } from 'app/entities/sale/service/sale.service';

@Component({
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CustomerUpdate implements OnInit {
  readonly isSaving = signal(false);
  customer: ICustomer | null = null;
  partyTypeValues = Object.keys(PartyType);

  peopleCollection = signal<IPerson[]>([]);
  companiesCollection = signal<ICompany[]>([]);
  salesSharedCollection = signal<ISale[]>([]);

  protected customerService = inject(CustomerService);
  protected customerFormService = inject(CustomerFormService);
  protected personService = inject(PersonService);
  protected companyService = inject(CompanyService);
  protected saleService = inject(SaleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerFormGroup = this.customerFormService.createCustomerFormGroup();

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
      if (customer) {
        this.updateForm(customer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const customer = this.customerFormService.getCustomer(this.editForm);
    if (customer.id === null) {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICustomer | null>): void {
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

  protected updateForm(customer: ICustomer): void {
    this.customer = customer;
    this.customerFormService.resetForm(this.editForm, customer);

    this.peopleCollection.set(this.personService.addPersonToCollectionIfMissing<IPerson>(this.peopleCollection(), customer.person));
    this.companiesCollection.set(
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(this.companiesCollection(), customer.company),
    );
    this.salesSharedCollection.update(sales => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, customer.sales));
  }

  protected loadRelationshipsOptions(): void {
    this.personService
      .query({ 'customerId.specified': 'false' })
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.customer?.person)))
      .subscribe((people: IPerson[]) => this.peopleCollection.set(people));

    this.companyService
      .query({ 'customerId.specified': 'false' })
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.customer?.company)),
      )
      .subscribe((companies: ICompany[]) => this.companiesCollection.set(companies));

    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.customer?.sales)))
      .subscribe((sales: ISale[]) => this.salesSharedCollection.set(sales));
  }
}
