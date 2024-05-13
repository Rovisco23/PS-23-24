import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkListingsComponent } from './work-listings.component';

describe('WorkListingsComponent', () => {
  let component: WorkListingsComponent;
  let fixture: ComponentFixture<WorkListingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkListingsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(WorkListingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
