import { Component, Input, Output, EventEmitter, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { US_STATES } from '../../core/models/states';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'admin-data-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatCardModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatPaginator
  ],
  templateUrl: './admin-data-table.component.html',
  styleUrls: ['./admin-data-table.component.scss']
})
export class AdminDataTableComponent {
  states = US_STATES;
  roles = ['ADMIN', 'USER'];

  /** Column configs */
  @Input() columns: { key: string; label: string }[] = [];

  /** Data to render */
  @Input() data: any[] = [];

  /** States */
  @Input() loading = false;
  @Input() error: string | null = null;
  @Input() emptyMessage = 'No records found.';

  /** Pagination */
  @Input() pageSizeOptions = [5, 10, 25, 50];
  @Input() pageSize = 10;
  @Input() pageIndex = 0;
  @Input() totalItems = 0;

  /** For existing action buttons */
  @Input() actionsTemplate?: TemplateRef<any>;

  /** Output events for parent to handle actions */
  @Output() editRow = new EventEmitter<any>();
  @Output() deleteRow = new EventEmitter<any>();
  @Output() pageChange = new EventEmitter<{ page: number, size: number }>();

  get displayedColumnKeys(): string[] {
    const keys = this.columns.map(c => c.key);
    if (this.actionsTemplate) {
      keys.push('actions');
    }
    return keys;
  }

  /** Trigger edit event */
  onEdit(row: any) {
    this.editRow.emit(row);
  }

  /** Trigger delete event */
  onDelete(row: any) {
    this.deleteRow.emit(row);
  }

  onPageChange(event: PageEvent) {
    this.pageChange.emit({
      page: event.pageIndex,
      size: event.pageSize
    })
  }

}

