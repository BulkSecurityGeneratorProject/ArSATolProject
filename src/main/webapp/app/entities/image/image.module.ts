import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArsatollserviceSharedModule } from 'app/shared';
import {
    ImageComponent,
    ImageDetailComponent,
    ImageUpdateComponent,
    ImageDeletePopupComponent,
    ImageDeleteDialogComponent,
    imageRoute,
    imagePopupRoute
} from './';

const ENTITY_STATES = [...imageRoute, ...imagePopupRoute];

@NgModule({
    imports: [ArsatollserviceSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ImageComponent, ImageDetailComponent, ImageUpdateComponent, ImageDeleteDialogComponent, ImageDeletePopupComponent],
    entryComponents: [ImageComponent, ImageUpdateComponent, ImageDeleteDialogComponent, ImageDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ArsatollserviceImageModule {}
