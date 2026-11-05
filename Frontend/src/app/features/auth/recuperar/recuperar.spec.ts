import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Auth } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';

import { Recuperar } from './recuperar';

describe('Recuperar', () => {
  let component: Recuperar;
  let fixture: ComponentFixture<Recuperar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Recuperar],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Auth, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Recuperar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});