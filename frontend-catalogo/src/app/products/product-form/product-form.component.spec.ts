import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ProductFormComponent} from './product-form.component';

describe('ProductFormComponent', () => {
  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductFormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the form component', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.productForm.valid).toBeFalse();
  });

  it('should require the name field', () => {
    const nameControl = component.productForm.get('name');
    nameControl?.setValue('');
    expect(nameControl?.valid).toBeFalse();
  });

  it('form should be valid when all fields are filled', () => {
    component.productForm.setValue({
      id: null,
      name: 'Mouse Gamer',
      description: 'RGB high precision',
      price: 150,
      quantity: 5
    });

    expect(component.productForm.valid).toBeTrue();
  });
});
