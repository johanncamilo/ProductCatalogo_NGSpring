import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ProductListComponent} from './product-list.component';
import {of} from 'rxjs';
import {ProductService} from '../product.service';

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;
  let productService: any;

  beforeEach(async () => {
    const mockService = jasmine.createSpyObj('ProductService', ['getAllProducts']);

    await TestBed.configureTestingModule({
      imports: [ProductListComponent],
      providers: [{provide: ProductService, useValue: mockService}]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;

    productService = TestBed.inject(ProductService);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should contain default displayed columns', () => {
    expect(component.displayedColumns).toContain('name');
    expect(component.displayedColumns).toContain('price');
    expect(component.displayedColumns).toContain('quantity');
    expect(component.displayedColumns).toContain('actions');
  });

  it('should load products into the table', () => {
    const mockProducts = [
      {id: 1, name: 'Laptop', description: 'Gaming', price: 1200, quantity: 5}
    ];

    productService.getAllProducts.and.returnValue(of(mockProducts));

    component.loadProducts();

    expect(component.dataSource.data.length).toBe(1);
    expect(component.dataSource.data[0].name).toBe('Laptop');
  });
});
