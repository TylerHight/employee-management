import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEmployeeList } from './admin-employee-list.component';

describe('AdminEmployeeList', () => {
  let component: AdminEmployeeList;
  let fixture: ComponentFixture<AdminEmployeeList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminEmployeeList]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminEmployeeList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
