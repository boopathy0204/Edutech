import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface AcademicPeriod {
  academicPeriodId: number;
  academicYear: string;
  academicHalf: string;
  status: string;
  startDate: string;
  endDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class AcademicPeriodService {
  private selectedPeriodSubject = new BehaviorSubject<AcademicPeriod | null>(null);
  public selectedPeriod$ = this.selectedPeriodSubject.asObservable();

  private periodsChangedSubject = new BehaviorSubject<boolean>(true);
  public periodsChanged$ = this.periodsChangedSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAllPeriods(): Observable<AcademicPeriod[]> {
    return this.http.get<AcademicPeriod[]>('/api/academic-periods');
  }

  getActivePeriod(): Observable<AcademicPeriod> {
    return this.http.get<AcademicPeriod>('/api/academic-periods/active').pipe(
      tap(period => {
        this.selectedPeriodSubject.next(period);
      })
    );
  }

  setSelectedPeriod(period: AcademicPeriod) {
    this.selectedPeriodSubject.next(period);
  }

  triggerPeriodsRefresh() {
    this.periodsChangedSubject.next(true);
  }

  getSelectedPeriod(): AcademicPeriod | null {
    return this.selectedPeriodSubject.value;
  }
}
