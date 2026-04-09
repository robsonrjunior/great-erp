package com.github.robsonrjunior.service.mapper;

import com.github.robsonrjunior.domain.Company;
import com.github.robsonrjunior.domain.Customer;
import com.github.robsonrjunior.domain.Person;
import com.github.robsonrjunior.domain.Product;
import com.github.robsonrjunior.domain.RawMaterial;
import com.github.robsonrjunior.domain.Sale;
import com.github.robsonrjunior.domain.SaleItem;
import com.github.robsonrjunior.domain.StockMovement;
import com.github.robsonrjunior.domain.Supplier;
import com.github.robsonrjunior.domain.Tenant;
import com.github.robsonrjunior.domain.Warehouse;
import com.github.robsonrjunior.service.dto.CompanyDTO;
import com.github.robsonrjunior.service.dto.CustomerDTO;
import com.github.robsonrjunior.service.dto.PersonDTO;
import com.github.robsonrjunior.service.dto.ProductDTO;
import com.github.robsonrjunior.service.dto.RawMaterialDTO;
import com.github.robsonrjunior.service.dto.SaleDTO;
import com.github.robsonrjunior.service.dto.SaleItemDTO;
import com.github.robsonrjunior.service.dto.StockMovementDTO;
import com.github.robsonrjunior.service.dto.SupplierDTO;
import com.github.robsonrjunior.service.dto.TenantDTO;
import com.github.robsonrjunior.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tenant} and its DTO {@link TenantDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantMapper extends EntityMapper<TenantDTO, Tenant> {
    @Mapping(target = "customers", source = "customers", qualifiedByName = "customerId")
    @Mapping(target = "suppliers", source = "suppliers", qualifiedByName = "supplierId")
    @Mapping(target = "people", source = "people", qualifiedByName = "personId")
    @Mapping(target = "companies", source = "companies", qualifiedByName = "companyId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productId")
    @Mapping(target = "rawMaterials", source = "rawMaterials", qualifiedByName = "rawMaterialId")
    @Mapping(target = "warehouses", source = "warehouses", qualifiedByName = "warehouseId")
    @Mapping(target = "sales", source = "sales", qualifiedByName = "saleId")
    @Mapping(target = "saleItems", source = "saleItems", qualifiedByName = "saleItemId")
    @Mapping(target = "stockMovements", source = "stockMovements", qualifiedByName = "stockMovementId")
    TenantDTO toDto(Tenant s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("rawMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RawMaterialDTO toDtoRawMaterialId(RawMaterial rawMaterial);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);

    @Named("saleItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleItemDTO toDtoSaleItemId(SaleItem saleItem);

    @Named("stockMovementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StockMovementDTO toDtoStockMovementId(StockMovement stockMovement);
}
