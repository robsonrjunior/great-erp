import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'greatErpApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'tenant',
    data: { pageTitle: 'greatErpApp.tenant.home.title' },
    loadChildren: () => import('./tenant/tenant.routes'),
  },
  {
    path: 'country',
    data: { pageTitle: 'greatErpApp.country.home.title' },
    loadChildren: () => import('./country/country.routes'),
  },
  {
    path: 'state',
    data: { pageTitle: 'greatErpApp.state.home.title' },
    loadChildren: () => import('./state/state.routes'),
  },
  {
    path: 'city',
    data: { pageTitle: 'greatErpApp.city.home.title' },
    loadChildren: () => import('./city/city.routes'),
  },
  {
    path: 'supplier',
    data: { pageTitle: 'greatErpApp.supplier.home.title' },
    loadChildren: () => import('./supplier/supplier.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'greatErpApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'person',
    data: { pageTitle: 'greatErpApp.person.home.title' },
    loadChildren: () => import('./person/person.routes'),
  },
  {
    path: 'company',
    data: { pageTitle: 'greatErpApp.company.home.title' },
    loadChildren: () => import('./company/company.routes'),
  },
  {
    path: 'product',
    data: { pageTitle: 'greatErpApp.product.home.title' },
    loadChildren: () => import('./product/product.routes'),
  },
  {
    path: 'raw-material',
    data: { pageTitle: 'greatErpApp.rawMaterial.home.title' },
    loadChildren: () => import('./raw-material/raw-material.routes'),
  },
  {
    path: 'warehouse',
    data: { pageTitle: 'greatErpApp.warehouse.home.title' },
    loadChildren: () => import('./warehouse/warehouse.routes'),
  },
  {
    path: 'stock-movement',
    data: { pageTitle: 'greatErpApp.stockMovement.home.title' },
    loadChildren: () => import('./stock-movement/stock-movement.routes'),
  },
  {
    path: 'sale',
    data: { pageTitle: 'greatErpApp.sale.home.title' },
    loadChildren: () => import('./sale/sale.routes'),
  },
  {
    path: 'sale-item',
    data: { pageTitle: 'greatErpApp.saleItem.home.title' },
    loadChildren: () => import('./sale-item/sale-item.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
