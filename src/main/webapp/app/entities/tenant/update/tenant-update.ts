import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PersonService } from 'app/entities/person/service/person.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IPerson } from 'app/entities/person/person.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IRawMaterial } from 'app/entities/raw-material/raw-material.model';
import { RawMaterialService } from 'app/entities/raw-material/service/raw-material.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { ISaleItem } from 'app/entities/sale-item/sale-item.model';
import { SaleItemService } from 'app/entities/sale-item/service/sale-item.service';
import { StockMovementService } from 'app/entities/stock-movement/service/stock-movement.service';
import { IStockMovement } from 'app/entities/stock-movement/stock-movement.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { WarehouseService } from 'app/entities/warehouse/service/warehouse.service';
import { IWarehouse } from 'app/entities/warehouse/warehouse.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TenantService } from '../service/tenant.service';
import { ITenant } from '../tenant.model';

import { TenantFormGroup, TenantFormService } from './tenant-form.service';

@Component({
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TenantUpdate implements OnInit {
  readonly isSaving = signal(false);
  tenant: ITenant | null = null;

  customersSharedCollection = signal<ICustomer[]>([]);
  suppliersSharedCollection = signal<ISupplier[]>([]);
  peopleSharedCollection = signal<IPerson[]>([]);
  companiesSharedCollection = signal<ICompany[]>([]);
  productsSharedCollection = signal<IProduct[]>([]);
  rawMaterialsSharedCollection = signal<IRawMaterial[]>([]);
  warehousesSharedCollection = signal<IWarehouse[]>([]);
  salesSharedCollection = signal<ISale[]>([]);
  saleItemsSharedCollection = signal<ISaleItem[]>([]);
  stockMovementsSharedCollection = signal<IStockMovement[]>([]);

  protected tenantService = inject(TenantService);
  protected tenantFormService = inject(TenantFormService);
  protected customerService = inject(CustomerService);
  protected supplierService = inject(SupplierService);
  protected personService = inject(PersonService);
  protected companyService = inject(CompanyService);
  protected productService = inject(ProductService);
  protected rawMaterialService = inject(RawMaterialService);
  protected warehouseService = inject(WarehouseService);
  protected saleService = inject(SaleService);
  protected saleItemService = inject(SaleItemService);
  protected stockMovementService = inject(StockMovementService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantFormGroup = this.tenantFormService.createTenantFormGroup();

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  compareSupplier = (o1: ISupplier | null, o2: ISupplier | null): boolean => this.supplierService.compareSupplier(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  compareRawMaterial = (o1: IRawMaterial | null, o2: IRawMaterial | null): boolean => this.rawMaterialService.compareRawMaterial(o1, o2);

  compareWarehouse = (o1: IWarehouse | null, o2: IWarehouse | null): boolean => this.warehouseService.compareWarehouse(o1, o2);

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  compareSaleItem = (o1: ISaleItem | null, o2: ISaleItem | null): boolean => this.saleItemService.compareSaleItem(o1, o2);

  compareStockMovement = (o1: IStockMovement | null, o2: IStockMovement | null): boolean =>
    this.stockMovementService.compareStockMovement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.tenant = tenant;
      if (tenant) {
        this.updateForm(tenant);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tenant = this.tenantFormService.getTenant(this.editForm);
    if (tenant.id === null) {
      this.subscribeToSaveResponse(this.tenantService.create(tenant));
    } else {
      this.subscribeToSaveResponse(this.tenantService.update(tenant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITenant | null>): void {
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

  protected updateForm(tenant: ITenant): void {
    this.tenant = tenant;
    this.tenantFormService.resetForm(this.editForm, tenant);

    this.customersSharedCollection.update(customers =>
      this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, tenant.customers),
    );
    this.suppliersSharedCollection.update(suppliers =>
      this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(suppliers, tenant.suppliers),
    );
    this.peopleSharedCollection.update(people => this.personService.addPersonToCollectionIfMissing<IPerson>(people, tenant.people));
    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, tenant.companies),
    );
    this.productsSharedCollection.update(products =>
      this.productService.addProductToCollectionIfMissing<IProduct>(products, tenant.products),
    );
    this.rawMaterialsSharedCollection.update(rawMaterials =>
      this.rawMaterialService.addRawMaterialToCollectionIfMissing<IRawMaterial>(rawMaterials, tenant.rawMaterials),
    );
    this.warehousesSharedCollection.update(warehouses =>
      this.warehouseService.addWarehouseToCollectionIfMissing<IWarehouse>(warehouses, tenant.warehouses),
    );
    this.salesSharedCollection.update(sales => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, tenant.sales));
    this.saleItemsSharedCollection.update(saleItems =>
      this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, tenant.saleItems),
    );
    this.stockMovementsSharedCollection.update(stockMovements =>
      this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, tenant.stockMovements),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.tenant?.customers),
        ),
      )
      .subscribe((customers: ICustomer[]) => this.customersSharedCollection.set(customers));

    this.supplierService
      .query()
      .pipe(map((res: HttpResponse<ISupplier[]>) => res.body ?? []))
      .pipe(
        map((suppliers: ISupplier[]) =>
          this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(suppliers, this.tenant?.suppliers),
        ),
      )
      .subscribe((suppliers: ISupplier[]) => this.suppliersSharedCollection.set(suppliers));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.tenant?.people)))
      .subscribe((people: IPerson[]) => this.peopleSharedCollection.set(people));

    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.tenant?.companies)),
      )
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.tenant?.products)))
      .subscribe((products: IProduct[]) => this.productsSharedCollection.set(products));

    this.rawMaterialService
      .query()
      .pipe(map((res: HttpResponse<IRawMaterial[]>) => res.body ?? []))
      .pipe(
        map((rawMaterials: IRawMaterial[]) =>
          this.rawMaterialService.addRawMaterialToCollectionIfMissing<IRawMaterial>(rawMaterials, this.tenant?.rawMaterials),
        ),
      )
      .subscribe((rawMaterials: IRawMaterial[]) => this.rawMaterialsSharedCollection.set(rawMaterials));

    this.warehouseService
      .query()
      .pipe(map((res: HttpResponse<IWarehouse[]>) => res.body ?? []))
      .pipe(
        map((warehouses: IWarehouse[]) =>
          this.warehouseService.addWarehouseToCollectionIfMissing<IWarehouse>(warehouses, this.tenant?.warehouses),
        ),
      )
      .subscribe((warehouses: IWarehouse[]) => this.warehousesSharedCollection.set(warehouses));

    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.tenant?.sales)))
      .subscribe((sales: ISale[]) => this.salesSharedCollection.set(sales));

    this.saleItemService
      .query()
      .pipe(map((res: HttpResponse<ISaleItem[]>) => res.body ?? []))
      .pipe(
        map((saleItems: ISaleItem[]) =>
          this.saleItemService.addSaleItemToCollectionIfMissing<ISaleItem>(saleItems, this.tenant?.saleItems),
        ),
      )
      .subscribe((saleItems: ISaleItem[]) => this.saleItemsSharedCollection.set(saleItems));

    this.stockMovementService
      .query()
      .pipe(map((res: HttpResponse<IStockMovement[]>) => res.body ?? []))
      .pipe(
        map((stockMovements: IStockMovement[]) =>
          this.stockMovementService.addStockMovementToCollectionIfMissing<IStockMovement>(stockMovements, this.tenant?.stockMovements),
        ),
      )
      .subscribe((stockMovements: IStockMovement[]) => this.stockMovementsSharedCollection.set(stockMovements));
  }
}
