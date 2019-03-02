/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArsatollserviceTestModule } from '../../../test.module';
import { AdministrateurDeleteDialogComponent } from 'app/entities/administrateur/administrateur-delete-dialog.component';
import { AdministrateurService } from 'app/entities/administrateur/administrateur.service';

describe('Component Tests', () => {
    describe('Administrateur Management Delete Component', () => {
        let comp: AdministrateurDeleteDialogComponent;
        let fixture: ComponentFixture<AdministrateurDeleteDialogComponent>;
        let service: AdministrateurService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ArsatollserviceTestModule],
                declarations: [AdministrateurDeleteDialogComponent]
            })
                .overrideTemplate(AdministrateurDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AdministrateurDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AdministrateurService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
