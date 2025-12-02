import {TestBed} from '@angular/core/testing';
import {ProductService} from './product.service';

describe('ProductService', () => {
  let service: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProductService]
    });

    service = TestBed.inject(ProductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return mock products when mocked mode is enabled', (done) => {
    (service as any).mocked = true;

    service.getAllProducts().subscribe(products => {
      expect(products.length).toBeGreaterThan(0);

      const firstProduct = products[0];

      expect(firstProduct.name).toBeDefined();

      expect(firstProduct).toEqual(
        jasmine.objectContaining({
          name: jasmine.any(String)
        })
      );

      done();
    });
  });

  it('should return an observable when calling getAllProducts()', () => {
    const result = service.getAllProducts();
    expect(result.subscribe).toBeDefined();
  });
});
